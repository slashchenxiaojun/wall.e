package org.hacker.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hacker.core.BaseController;
import org.hacker.exception.ApiException;
import org.hacker.module.common.Assert;
import org.hacker.mvc.model.DbModel;
import org.hacker.mvc.model.DbModelItem;
import org.hacker.mvc.model.DbModelMapping;
import org.hacker.mvc.model.Generate;
import org.hacker.service.TempletGenerate;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

public class DBModelController extends BaseController {
	
  private static String
	generateRootPath,
  generateConfigRootPath,
  generateDbName,
  dbName;
	
	private static TempletGenerate tg;
	
	static {
	  PropKit.use("generate.properties");
	  generateRootPath = PropKit.getProp("generate.properties").get("generateRootPath");
	  generateConfigRootPath = PropKit.getProp("generate.properties").get("generateConfigRootPath");
	  generateDbName = PropKit.getProp("generate.properties").get("generateDbName");
	  
	  tg = new TempletGenerate(
	  generateRootPath, 
	  generateConfigRootPath, 
	  generateDbName);
	}
	
  public void index() {
		Integer 
		pageSize 	  = getParaToInt("pageSize", 10),
		pageNumber 	= getParaToInt("pageNumber", 1);
		Object projectId = getPara("projectId");
		
		Assert.checkNotNull(projectId, "projectId");
		setAttr("projectId", projectId);
		setAttr("dbName", dbName);
		setAttr("dbmodels", DbModel.dao.paginate(pageNumber, pageSize, "select * ", "from w_db_model where project_id = ?", projectId));
	}
	
	public void create_model() {
	  Object projectId = getPara("projectId");
	  
    Assert.checkNotNull(projectId, "projectId");
    setAttr("projectId", projectId);
		setAttr("dbmodels", DbModel.dao.find("select * from w_db_model"));
		render("_create_modele.html");
	}
	
	public void mapping_model() {
	  setAttr("dbmodels", DbModel.dao.find("select * from w_db_model"));
	  render("_mapping_model.html");
	}
	
	// 获取model的column
	public void getModelItem() {
		String id = getPara();
		OK(DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id));
	}
	
	// 生成sql并同步到数据库
	public void syndb() {
	  Object modelId = getPara("modelId");
	  String dbName = getPara("dbName");
	  Assert.checkNotNull(modelId, "modelId");
	  Assert.checkNotNull(dbName, "dbName");
	  
	  DBModelController.dbName = dbName;
	  DbModel model = DbModel.dao.findById(modelId);
	  tg.generateDB(model, null, dbName);
	  OK();
	}
	
	public void quickGenerate() {
	   Object projectId = getPara();
	   String dbName = getPara("dbName");
	   
	   Assert.checkNotNull(projectId, "projectId");
	   Assert.checkNotNull(dbName, "dbName");
	   
	   DBModelController.dbName = dbName;
	   tg.quickGenerate(projectId, dbName);
	   OK();
	}
	
	// 根据自定义的模板生成代码
	public void generate() {
	  Object modelId = getPara();
	  Assert.checkNotNull(modelId, "modelId");
	  
	  DbModel model = DbModel.dao.findById(modelId);
	  Map<String, Object> paras = tg.getGenerateParamter(model, true);
	  tg.generateModel(model, null, paras);
	  tg.generateService(model, null, paras);
	  tg.generateController(model, null, paras);
	  OK();
	}
	
	@Before(Tx.class)
	public void save() {
		DbModel dbmodel = getModel(DbModel.class, "dbmodel");
		Generate generate = getModel(Generate.class, "generate");
		List<DbModelItem> dbmodelItems = getModels(DbModelItem.class, "model");
		if(StrKit.isBlank(dbmodel.getClassName())) {
		  dbmodel.setClassName(camelNameConvert(dbmodel.getName()));
		}
		if(StrKit.isBlank(generate.getModuleName())) {
		  generate.setModuleName(dbmodel.getClassName().toLowerCase());
		}
		if(dbmodel.save()) {
		  generate.setWModelId(dbmodel.getId());
		  generate.save();
		  int serial = 0;
			for(DbModelItem item : dbmodelItems) {
				// 根据type设置java_type
				item.setJavaType(mysqlAndJavaType.get(item.getType()));
				item.setWModelId(dbmodel.getId());
				item.save();
				serial = item.getSerial();
			}
			createDateTime("create_date", "创建时间", dbmodel.getId(), serial++).save();
			createDateTime("modify_date", "更新时间", dbmodel.getId(), serial++).save();
		}
		OK();
	}
	
	@Before(Tx.class)
	public void delete() {
	  Object id = getPara();
	  Assert.checkNotNull(id, "model id");
	  boolean hasRelation = DbModelMapping.dao.find("select * from w_db_model_mapping where master_id = ? or slaves_id = ?", id, id).size() > 0;
	  if(hasRelation) {
	    throw new ApiException("Oop~ model has relation, can't delete.");
	  }
	  if(DbModel.dao.deleteById(id)) OK();
	  else Error(500, "Oop~ delete model fail.");
	}
	
	public void mapping() {
	  DbModelMapping mapping = getModel(DbModelMapping.class, "mapping");
	  DbModelMapping modelMapping = DbModelMapping.dao.findFirst("select * from w_db_model_mapping where master_id = ? and slaves_id = ?", mapping.getMasterId(), mapping.getSlavesId());
	  if(modelMapping != null) {
	    modelMapping.setMappingSchema(mapping.getMappingSchema());
	    if(modelMapping.update()) OK(); 
	    else Error(500, "Oop~ save model mapping fail.");
	    return;
	  }
	  if(mapping.save()) OK();
	  else Error(500, "Oop~ save model mapping fail.");
	}
	
	private DbModelItem createDateTime(String name, String note, Integer modelId, Integer serial) {
	  DbModelItem item = new DbModelItem();
	  item.setName(name);
	  item.setDecimal(0);
	  item.setLength(0);
	  item.setJavaType("java.sql.Timestamp");
	  item.setType("DATETIME");
	  item.setNote(note);
	  item.setSerial(serial);
	  item.setWModelId(modelId);
	  return item;
	}
	
	private String camelNameConvert(String name) {
	  String[] names = name.split("_");
    if(names.length == 1) {
      return StrKit.firstCharToUpperCase(names[0]);
    }
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < names.length; i++) {
      sb.append(StrKit.firstCharToUpperCase(names[i]));
    }
    return sb.toString();
	}
	
	private Map<String, String> mysqlAndJavaType = new HashMap<String, String>() {{
		// int, integer, tinyint, smallint, mediumint
		put("INTEGER", "java.lang.Integer");
		// bigint
		put("BIGINT", "java.lang.Long");
		// decimal, numeric
		put("DECIMAL", "java.math.BigDecimal");
		
		// varchar, char, enum, set, text, tinytext, mediumtext, longtext
		put("VARCHAR", "java.lang.String");
		
		// bit
		put("BIT", "java.lang.Boolean");
		
		// timestamp, datetime
		put("DATETIME", "java.sql.Timestamp");
		
		// binary, varbinary, tinyblob, blob, mediumblob, longblob
		// qjd project: print_info.content varbinary(61800);
		put("BLOB", "byte[]");
	}};
}
