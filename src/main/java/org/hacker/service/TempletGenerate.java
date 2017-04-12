package org.hacker.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.WebAppResourceLoader;
import org.hacker.exception.GenerateException;
import org.hacker.module.common.FileKit;
import org.hacker.mvc.model.DbModel;
import org.hacker.mvc.model.DbModelItem;
import org.hacker.mvc.model.DbModelMapping;
import org.hacker.mvc.model.Generate;
import org.hacker.mvc.model.Project;
import org.hacker.mvc.view.CamelNameConvert;
import org.hacker.mvc.view.FirstCharToLowerCase;
import org.hacker.mvc.view.ToLowerCase;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 使用beetl生成模板
 * 
 * 默认在WebRoot/gen目录下
 * 开发者可以自定义文件模板
 * 
 * @author Mr.J
 *
 */
public class TempletGenerate {
  
	private GroupTemplate gt;
	// 生成code文件目录的root-path
	private String generateCodeRootPath;
	// 生成config文件目录的root-path
//	private String generateConfigRootPath;
	// 默认的生成数据库名为`walle`
	private String generateDbName;
	// 兼容maven beetl的基础路径,否则模板不能被正确识别
	private final String MAVEN_BASE = "src/main/webapp/";
	
	public TempletGenerate(String generateRootPath, String generateConfigRootPath, String generateDbName) {
		WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
		Configuration cfg = null;
    try {
      cfg = Configuration.defaultConfiguration();
    } catch (IOException e) {
      e.printStackTrace();
    }
		gt = new GroupTemplate(resourceLoader, cfg);
		
		this.generateCodeRootPath = generateRootPath;
//		this.generateConfigRootPath = generateConfigRootPath;
		this.generateDbName = generateDbName;
		init();
	}
	
	public void init() {
	  if(StrKit.isBlank(generateDbName)) generateDbName = "walle";
	  
	  gt.registerFunction("camelNameConvert", new CamelNameConvert());
	  gt.registerFunction("firstCharToLowerCase", new FirstCharToLowerCase());
	  gt.registerFunction("toLowerCase", new ToLowerCase());
	}
	
	/**
	 * 快速生产代码，根据给定的db_model生成db
	 * 执行数据库，代码
	 */
	public void quickGenerate(Object projectId, String dbName) {
	  List<DbModel> modelList = Project.dao.findById(projectId).getModelList();
	  for(DbModel dbModel : modelList) {
	    Map<String, Object> paras = getGenerateParamter(dbModel, true);
	    generateDB(dbModel, null, dbName);
	    generateModel(dbModel, null, paras);
	    generateController(dbModel, null, paras);
	    generateService(dbModel, null, paras);
	    generateSql(dbModel, null, paras);
	  }
	  generateMappingModel(projectId, null);
	  generateMappingRoute(projectId, null);
	}
	
	/**
	 * 生成db数据库文件
	 * 
	 * 默认模板路径: gen/db/4mysqldb.btl
	 * 
	 * @param model
	 * @param templatePath 代码生成的模板路径
	 * @param dbName 需要创建表的数据库名
	 */
	public void generateDB(DbModel model, String templatePath, String dbName) {
	  if(model == null || model.getId() == null) 
	    throw new GenerateException("Oop~ model is null.");
	  if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/db/4mysqldb.btl";
	  Object id = model.getId();
	  
	  List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    List<DbModelMapping> slaves = DbModelMapping.dao.find("select * from w_db_model_mapping where master_id = ?", id);
//    List<Record> master = Db.find("select t1.*, b.length, b.type from (select * from w_db_model_mapping where slaves_id = ?) t1, w_db_model_item b where b.w_model_id = t1.master_id and b.`name` = t1.mapping_foreign_key", id);
    
    // 修改数据结构，现在所有的关系，均使用中间表来表示
    // 当且仅当相关从表中含有ManyToMany关系时生成中间表
//    List<DbModelMapping> mapping = new ArrayList<>();
//    for(DbModelMapping mm : slaves) {
//      if(mm.getMappingSchema().equals("ManyToMany")) {
//        mapping.add(mm);
//      }
//    }
    
    // 当且仅当相关主表中含有oneToMany关系时需要生成外键
//    List<Record> foreign = new ArrayList<>();
//    for(Record mm : master) {
//      if(mm.getStr("mapping_schema").equals("oneToMany")) {
//        foreign.add(mm);
//      }
//    }
    
    Map<String, Object> paras = new HashMap<>();
    paras.put("db", dbName);
    paras.put("model", model);
    paras.put("columns", columns);
    
    paras.put("mapping", slaves);
//    paras.put("foreign", foreign);
    
    Template template = gt.getTemplate(templatePath);
   
    template.binding(paras);
    
    String[] sqls = template.render().split(";");
    try {
      for(String sql : sqls) {
        if(StrKit.isBlank(sql)) continue;
        Db.update(sql);
      }
    } finally {
      // 返回walle的数据库
      Db.update("USE `" + generateDbName + "`");
    }
    System.out.println("############generateDB success############");
	}
	
	public void generateModel(DbModel model, String templatePath, Map<String, Object> paras) {
    if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/pojo/4ActiveRecordEnhance.btl";
    Template t_pojo = gt.getTemplate(templatePath);
    t_pojo.binding(paras);
    Generate generate = (Generate)paras.get("generate");
    
    File file = getCodeGenerateFile(generate, "model", model.getClassName() + ".java");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############generateModel success############");
	}
	
	public void generateController(DbModel model, String templatePath, Map<String, Object> paras) {
    if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/web/4Jfinalcontroller.btl";
    Template t_pojo = gt.getTemplate(templatePath);
    t_pojo.binding(paras);
    Generate generate = (Generate)paras.get("generate");
    
    File file = getCodeGenerateFile(generate, "controller", model.getClassName() + "Controller.java");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############generateController success############");
  }
	
	public void generateService(DbModel model, String templatePath, Map<String, Object> paras) {
    if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/web/4webservice.btl";
    Template t_pojo = gt.getTemplate(templatePath);
    t_pojo.binding(paras);
    Generate generate = (Generate)paras.get("generate");
    
    File file = getCodeGenerateFile(generate, "service", model.getClassName() + "Service.java");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############generateService success############");
  }
	
	public void generateSql(DbModel model, String templatePath, Map<String, Object> paras) {
    if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/web/4mysqlmd.btl";
    Template t_pojo = gt.getTemplate(templatePath);
    t_pojo.binding(paras);
    Generate generate = (Generate)paras.get("generate");
    
    File file = getCodeGenerateFile(generate, "model", model.getClassName().toLowerCase() + ".sql.md");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############generateSql success############");
  }
	
	public void generateMappingModel(Object projectId, String templatePath) {
	  if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/config/4jfinalmappingmodel.btl";
	  Template t_pojo = gt.getTemplate(templatePath);
    List<Record> list = Db.find("select a.*, b.name primary_name, b.java_type, c.package, c.module_name from w_db_model a, w_db_model_item b, w_generate c where a.id = b.w_model_id and c.w_model_id = a.id and b.is_primary = 1 and a.project_id = ?", projectId);
	  t_pojo.binding("models", list);
    File file = getConfigGenerateFile(
        File.separator + "org" + 
        File.separator + "hacker" + 
        File.separator + "core" + 
        File.separator + "config",
        "MappingModel.java");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############getGenerateConfig success############");
	}
	
	public void generateMappingRoute(Object projectId, String templatePath) {
    if(StrKit.isBlank(templatePath)) templatePath = MAVEN_BASE + "gen/config/4jfinalmappingroute.btl";
    Template t_pojo = gt.getTemplate(templatePath);
    List<Record> list = Db.find("select a.*, b.name primary_name, b.java_type, c.package, c.module_name from w_db_model a, w_db_model_item b, w_generate c where a.id = b.w_model_id and c.w_model_id = a.id and b.is_primary = 1 and a.project_id = ?", projectId);
    t_pojo.binding("models", list);
    File file = getConfigGenerateFile(
        File.separator + "org" + 
        File.separator + "hacker" + 
        File.separator + "core" + 
        File.separator + "config",
        "MappingRoute.java");
    System.out.println(file.getAbsolutePath());
    try {
      FileKit.write(t_pojo.render(), file);
    } catch (FileNotFoundException | BeetlException e) {
      e.printStackTrace();
    }
    System.out.println("############getGenerateConfig success############");
  }
	
	public Map<String, Object> getGenerateParamter(DbModel model, boolean isCamelName) {
	  if(model == null || model.getId() == null) 
      throw new GenerateException("Oop~ model is null.");
	  Object id = model.getId();
    
    List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", id);
    
    List<Record> master = Db.find("select b.class_name, a.*, c.package, c.module_name from w_db_model_mapping a, w_db_model b, w_generate c where a.master_id = b.id and a.slaves_id = ? and a.master_id = c.w_model_id", id);
    List<Record> slaves = Db.find("select a.class_name, b.*, c.package, c.module_name from w_db_model a, w_db_model_mapping b, w_generate c where a.id = b.slaves_id and b.master_id = ? and c.w_model_id = b.slaves_id", id);
    
    boolean 
    // for String
    importNotBlank  = false,
    // for String
    importLength    = false,
    // for Object
    importNotNull   = false;
    
    for(DbModelItem column : columns) {
      if(column.getJavaType().equals("java.lang.String")) {
        importLength = true;
      }
      if(column.getIsRequired() == true && !column.getName().equals("id")) {
        if(column.getJavaType().equals("java.lang.String")) {
          importNotBlank = true;
        } else {
          importNotNull = true;
        }
      }
    }
    
    Map<String, Object> paras = new HashMap<>();
    paras.put("generate", generate);
    
    paras.put("model", model);
    paras.put("columns", columns);
    
    paras.put("master", master);
    paras.put("slaves", slaves);
    paras.put("camelName", isCamelName);
    
    paras.put("importNotBlank", importNotBlank);
    paras.put("importLength", importLength);
    paras.put("importNotNull", importNotNull);
    
    return paras;
	}

	/**
	 * 获取代码生成路径
	 * example:
	 * E:\\workspace\\test_project\\src\\main\\java\\org\\hacker\\module\\movie\\model
	 * 
	 * generateRootPath: E:\\workspace\\test_project\\src\\main\\java
	 * generate: org\\hacker\\module\\movie
	 * moduleName: model|controller|service| `what you like...`
	 * 
	 * @param generate 生成代码的信息
	 * @param moduleName 模块名称
	 * @return
	 */
	protected File getCodeGenerateFile(Generate generate, String moduleName, String fileName) {
	  File dir = new File(generateCodeRootPath + 
	  File.separator + generate.getPackage().replace(".", File.separator) + 
	  File.separator + generate.getModuleName() + 
	  File.separator + moduleName);
	  if(!dir.exists()) {
	    if(!dir.mkdirs())
	      throw new GenerateException("Oop~ generateFile mkdirs: " + dir.getAbsolutePath() + " fail.");
	  }
	  File folder = new File(dir, fileName);
	  if(!folder.exists()) {
	    try {
        if(!folder.createNewFile())
          throw new GenerateException("Oop~ generateFile createNewFile: " + fileName + " fail.");
      } catch (IOException e) {
        e.printStackTrace();
      }
	  }
	  return folder;
	}
	
	protected File getConfigGenerateFile(String configPath, String fileName) {
    File dir = new File(generateCodeRootPath + File.separator + configPath);
    if(!dir.exists()) {
      if(!dir.mkdirs())
        throw new GenerateException("Oop~ generateFile mkdirs: " + dir.getAbsolutePath() + " fail.");
    }
    File folder = new File(dir, fileName);
    if(!folder.exists()) {
      try {
        if(!folder.createNewFile())
          throw new GenerateException("Oop~ generateFile createNewFile: " + fileName + " fail.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return folder;
  }

}
