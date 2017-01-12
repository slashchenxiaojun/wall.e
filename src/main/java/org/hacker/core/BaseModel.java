package org.hacker.core;

import java.util.Date;
import java.util.List;

import org.hacker.core.plugin.Table;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.TableMapping;

/**
 * BaseModel 只提供基础方法
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-07-02
 * */
public abstract class BaseModel<M extends BaseModel<M>> extends Model<M> {
	private static final long serialVersionUID = -4334213295707048279L;

	@Override
	public boolean save() {
//	this.set(TableMapping.me().getTable(getClass()).getPrimaryKey()[0], CodeKit.UUID().replace("-", ""));
		com.jfinal.plugin.activerecord.Table table = TableMapping.me().getTable(getClass());
		if(table.getColumnTypeMap().containsKey("create_date")){
			this.set("create_date", new Date());
		}
		if(table.getColumnTypeMap().containsKey("create_date")){
			this.set("modify_date", new Date());
		}
		return super.save();
	}
	
	public List<M> query(String sql, Object... attrs) {
		return ModelUtil.query(this, sql, attrs);
	}
	
	public List<M> queryByAnd(BaseModel<M> model, String... attrs) {
		return ModelUtil.simpleQueryByAnd(model, attrs);
	}
	
	public List<M> queryByOr(BaseModel<M> model, String... attrs) {
		return ModelUtil.simpleQueryByOr(model, attrs);
	}
	
	// 增强Model层的能力
	private static class ModelUtil<M extends Model<M>> {
		private enum SqlConditionEnum {
			AND, OR;
		}
		
//    private enum SqlMatchEnum {
//      /* `=` `!=` */
//      EQUAL, UNEQUAL,
//      /* `>` `>=` */
//      GREATER, GREATER_OR_EQUAL,
//      /* `<` `<=` */
//      LESS, LESS_OR_EQUAL,
//      BEWEEN, LIKE, IN;
//    }
		
		protected static <M extends BaseModel<M>> List<M> simpleQueryByAnd(Model<M> model, String... attrs) {
			return query(model, SqlConditionEnum.AND, attrs);
		}
		
		protected static <M extends BaseModel<M>> List<M> simpleQueryByOr(Model<M> model, String... attrs) {
			return query(model, SqlConditionEnum.OR, attrs);
		}
		
		/**
		 * 简单条件查询，为了解决每次都需要写完整的SQL语句的麻烦
		 * 如:
		 * select * from a where a.id = ?
		 * a.id = ?
		 * 
		 * @param sql
		 * @return
		 */
		private static <M extends BaseModel<M>> List<M> query(Model<M> model, String sql, Object... args) {
			String tableName = TableMapping.me().getTable(model.getClass()).getName();
			return model.find("select * from " + tableName + " where " + sql, args);
		}
		
		/**
		 * 简单条件查询，为了解决每次都需要写完整的SQL语句的麻烦
		 * 当且仅当简单条件查询(where后条件只有单个或者是单一的条件)
		 * 如:
		 * select * from a where a.id = ?
		 * select * from a where a.id = ? and a.name = ?
		 * select * from a where a.id = ? or a.name = ?
		 * 
		 * simple example:
		 * User user = new User();
		 * user.set("id", 1);
		 * user.set("name", "Mr.J.");
		 * 
		 * query(user, SqlConditionEnum.AND, "id", "name");
		 * select * from user where id = ? and name = ?
		 * 
		 * @param model
		 * @param sce and,or etc...
		 * @param attrs 需要加入where后条件的attrs
		 * @return
		 */
		private static <M extends BaseModel<M>> List<M> query(Model<M> model, SqlConditionEnum sce, String... attrs){
			if(model == null || sce == null) 
				throw new IllegalArgumentException("非法: Model或SqlConditionEnum为NULL");
			
			if(attrs == null || attrs.length < 1) 
				throw new IllegalArgumentException("非法: 查询Model的参数为NULL");
			
			// 获取model的table注解
			Table table = model.getClass().getAnnotation(Table.class);
			if(table == null) 
				throw new IllegalArgumentException("非法: 查询Model缺少@Table注解");
			if(StrKit.isBlank(table.tableName()))
				throw new IllegalArgumentException("非法: 查询Model缺少@Table注解的tableName属性");
			
			Object[] values = new Object[attrs.length];
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(table.tableName()).append(" where ");
			for(int i = 0; i < attrs.length; i++){
				sql.append(attrs[i]).append(" = ?");
				if(i != attrs.length - 1){
					if(sce.equals(SqlConditionEnum.AND)){
						sql.append(" and ");
					}else if(sce.equals(SqlConditionEnum.OR)){
						sql.append(" or ");
					}
				}
				values[i] = model.get(attrs[i]);
			}
			return model.find(sql.toString(), values);
		}
	}
}
