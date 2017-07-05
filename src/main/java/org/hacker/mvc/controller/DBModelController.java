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
import org.hacker.mvc.model.Project;
import org.hacker.service.TempletGenerate;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

public class DBModelController extends BaseController {
	
  public void index() {
		Integer 
		pageSize 	  = getParaToInt("pageSize", 10),
		pageNumber 	= getParaToInt("pageNumber", 1);
		Object projectId = getPara("projectId");
		
		Assert.checkNotNull(projectId, "projectId");
		setAttr("projectId", projectId);
		// 工程id错误会抛出null异常
		Project project = Project.dao.findById(projectId);
		setAttr("dbName", project.getDbName());
		setAttr("rootPath", project.getRootPath());
		setAttr("dbmodels", DbModel.dao.paginate(pageNumber, pageSize, "select * ", "from w_db_model where project_id = ?", projectId));
	}
  
  public void generateConfig() {
    Object projectId = getPara("projectId");
    String dbName    = getPara("dbName");
    String rootPath  = getPara("rootPath");
    
    Assert.checkNotNull(projectId, "projectId");
    Assert.checkNotNull(dbName, "dbName");
    Assert.checkNotNull(rootPath, "rootPath");
    
    Project project = Project.dao.findById(projectId);
    if (project == null) throw new ApiException(String.format("Oop! project[%s] not exits.", projectId));
    project.setDbName(dbName);
    project.setRootPath(rootPath);
    if (!project.update())
      throw new ApiException(String.format("Oop! project[%s] generate config fail.", projectId));
    OK();
  }
	
	public void create_model() {
	  Object projectId = getPara("projectId");
    Assert.checkNotNull(projectId, "projectId");
    
    setAttr("projectId", projectId);
		render("_create_modele.html");
	}
	
	public void update_model() {
	  Object projectId = getPara("projectId");
	  Object modelId = getPara("modelId");
	  
	  Assert.checkNotNull(projectId, "projectId");
	  Assert.checkNotNull(modelId, "modelId");

	  setAttr("projectId", projectId);
	  setAttr("model", DbModel.dao.findFirst("select * from w_db_model where id = ? and project_id = ?", modelId, projectId));
	  setAttr("generate", DbModel.dao.findFirst("select * from w_generate where w_model_id = ?", modelId));
	  setAttr("modelItem", DbModelItem.dao.find("select b.* from w_db_model a left join w_db_model_item b on a.id = b.w_model_id where a.id = ? order by b.serial asc", modelId));
	  render("_update_modele.html");
	}
	
	public void mapping_model() {
	  Object projectId = getPara("projectId");
    Assert.checkNotNull(projectId, "projectId");
    
	  setAttr("dbmodels", DbModel.dao.find("select * from w_db_model where project_id = ?", projectId));
	  render("_mapping_model.html");
	}
	
	// 获取model的column
	public void getModelItem() {
		String id = getPara();
		OK(DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id));
	}
	
	// 生成sql并同步到数据库
	public void syndb() {
	  Object projectId = getPara("projectId");
	  Object modelId = getPara("modelId");
	  String dbName = getPara("dbName");
	  
	  Assert.checkNotNull(projectId, "projectId");
	  Assert.checkNotNull(modelId, "modelId");
	  Assert.checkNotNull(dbName, "dbName");
	  
	  Project project = Project.dao.findById(projectId);
	  project.setDbName(dbName);
	  project.update();
	  
	  DbModel model = DbModel.dao.findById(modelId);
	  TempletGenerate tg = getTempletGenerate(projectId);
	  tg.generateDB(model, null, dbName);
	  OK();
	}
	
	public void quickGenerate() {
	  Object projectId = getPara();
	  String dbName = getPara("dbName");
	   
	  Assert.checkNotNull(projectId, "projectId");
	  Assert.checkNotNull(dbName, "dbName");
	   
    Project project = Project.dao.findById(projectId);
    project.setDbName(dbName);
    project.update();
	    
	  TempletGenerate tg = getTempletGenerate(projectId);
	  tg.quickGenerate(projectId, dbName);
	  OK();
	}
	
	// 根据自定义的模板生成代码
	public void generate() {
	  Object projectId = getPara("projectId");
	  Object modelId = getPara();
	  String genModule = getPara("genModule");
	  
	  Assert.checkNotNull(projectId, "projectId");
	  Assert.checkNotNull(modelId, "modelId");
	  Assert.checkNotNull(genModule, "genModule");
	  
	  String[] genModules = genModule.split(",");
	  DbModel model = DbModel.dao.findById(modelId);
	  TempletGenerate tg = getTempletGenerate(projectId);
	  Map<String, Object> paras = tg.getGenerateParamter(model, true);
	  for (String module : genModules) {
	    switch (module) {
      case "model":
        tg.generateModel(model, null, paras);
        break;
      case "sql":
        tg.generateSql(model, null, paras);
        break;
      case "controller":
        tg.generateController(model, null, paras);
        break;
      case "service":
        tg.generateService(model, null, paras);
        break;
      default:
        break;
      }
	  }
	  OK();
	}

	// 复制model
	@Before(Tx.class)
	public void cloneModel() {
		Object projectId = getPara("projectId");
		Object modelId = getPara();

		Assert.checkNotNull(projectId, "projectId");
		Assert.checkNotNull(modelId, "modelId");

		// 一共需要复制 model, model_item, generate
		DbModel model = DbModel.dao.findById(modelId);
		if ( model == null ) throw new ApiException("Oop! model not exist.");

		List<DbModelItem> dbModelItemList = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ?", modelId);
		if ( !model.remove("id").save() ) throw new ApiException("Oop! clone model fail.");
		for (DbModelItem dbModelItem : dbModelItemList) {
			dbModelItem.remove("id").setWModelId(model.getId());
			dbModelItem.save();
		}
		Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", modelId);
		if ( generate != null) {
			generate.remove("id").setWModelId(model.getId());
			generate.save();
		}
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
	public void update() {
	  DbModel dbmodel = getModel(DbModel.class, "dbmodel");
	  Generate generate = getModel(Generate.class, "generate");
	  List<DbModelItem> dbmodelItems = getModels(DbModelItem.class, "model");
	  if(StrKit.isBlank(dbmodel.getClassName())) {
	    dbmodel.setClassName(camelNameConvert(dbmodel.getName()));
	  }
	  if(StrKit.isBlank(generate.getModuleName())) {
	    generate.setModuleName(dbmodel.getClassName().toLowerCase());
	  }
	  if(dbmodel.update()) {
	    generate.update();
	    //  delete first
	    Db.update("delete from w_db_model_item where w_model_id = ?", dbmodel.getId());
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
	    // update mapping...
	    List<DbModelMapping> master = DbModelMapping.dao.find("select * from w_db_model_mapping where master_id = ?", dbmodel.getId());
	    List<DbModelMapping> slaves = DbModelMapping.dao.find("select * from w_db_model_mapping where slaves_id = ?", dbmodel.getId());
	    for (DbModelMapping mp : master) {
	      mp.setMasterName(dbmodel.getName());
	      mp.update();
	    }
	    for (DbModelMapping mp : slaves) {
	      mp.setSlavesName(dbmodel.getName());
	      mp.update();
	    }
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
	
	// 获取project生成代码的root路径-对应的模板对象
	private TempletGenerate getTempletGenerate(Object projectId) {
	  Project project = Project.dao.findById(projectId);
	  if (project == null) throw new ApiException(String.format("Oop! project[%s] not exits.", projectId));
	  String rootPath = project.getRootPath();
	  if (StrKit.isBlank(rootPath)) throw new ApiException(String.format("Oop! project[%s] root path is null.", projectId));
	  
	  TempletGenerate tg = new TempletGenerate(rootPath, rootPath, null);
	  return tg;
	}
}
