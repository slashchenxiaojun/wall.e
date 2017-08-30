package org.hacker.test.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.WebAppResourceLoader;
import org.hacker.core.config.PluginFactory;
import org.hacker.mvc.model.DbModel;
import org.hacker.mvc.model.DbModelItem;
import org.hacker.mvc.model.DbModelMapping;
import org.hacker.mvc.model.Generate;
import org.hacker.mvc.view.CamelNameConvert;
import org.hacker.mvc.view.FirstCharToLowerCase;
import org.hacker.mvc.view.ToLowerCase;
import org.hacker.service.TempletGenerate;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class TempletGenerateTest {
	protected static GroupTemplate gt;

	@BeforeClass
	public static void init() throws IOException {
		WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
		String root = "E:\\workspace\\WALL-E\\src\\main\\webapp";
		resourceLoader.setRoot(root);
		Configuration cfg = Configuration.defaultConfiguration();

		gt = new GroupTemplate(resourceLoader, cfg);

		PluginFactory.startActiveRecordPlugin();
	}

//	@Test
	public void test_quest() {
	  TempletGenerate tg = new TempletGenerate(
	      "E:\\workspace\\gen_web\\src\\main\\java",
	      "E:\\workspace\\gen_web\\src\\main\\java",
	      "walle");
	  tg.quickGenerate(1, "test");
	}

//	@Test
	/**
	 * 数据表的关系
	 * master 和 slaves
	 * master - slaves - onoToOne
	 * 在slaves中添加master的外键
	 * master - slaves - onoToMany
	 * 在slaves中添加master的外键
	 * master - slaves - ManyToMany
	 * 生成中间表mp_master_slaves，并添加master和slaves的外键
	 *
	 * 坚持单表是否需要中间表
	 * 需要查看是否包含ManyToMany的slaves
	 * select * from w_db_model_mapping where master_id = 3
	 * 如果包含有ManyToMany的slaves就生成中间表
	 *
	 *
	 */
	public void test_db() {
		int id = 3;
		DbModel model = DbModel.dao.findById(id);
		List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);

		// 查寻出跟自己相关的从表
		List<DbModelMapping> slaves = DbModelMapping.dao.find("select * from w_db_model_mapping where master_id = ?", id);
		// 查询出跟自己相关的主表
		List<Record> master = Db.find("select t1.*, b.length, b.type from (select * from w_db_model_mapping where slaves_id = ?) t1, w_db_model_item b where b.w_model_id = t1.master_id and b.`name` = t1.mapping_foreign_key", id);

		// 当且仅当相关从表中含有ManyToMany关系时生成中间表
		List<DbModelMapping> mapping = new ArrayList<>();
		for(DbModelMapping mm : slaves) {
			if(mm.getMappingSchema().equals("ManyToMany")) {
				mapping.add(mm);
			}
		}
		// 当且仅当相关主表中含有oneToMany关系时需要生成外键
		List<Record> foreign = new ArrayList<>();
		for(Record mm : master) {
			if(mm.getStr("mapping_schema").equals("oneToMany")) {
				foreign.add(mm);
			}
		}

		Template t2 = gt.getTemplate("gen/db/4mysqldb.btl");

		t2.binding("db", "test");
		t2.binding("model", model);
		t2.binding("columns", columns);
//		t2.binding("module", "movie");

		t2.binding("mapping", mapping);
		t2.binding("foreign", foreign);

		System.out.println(t2.render());
//		String[] sqls = t2.render().split(";");
//		for(String sql : sqls) {
//			if(StrKit.isBlank(sql)) continue;
//			Db.update(sql);
//		}
	}

//	@Test
  public void test_pojo() {
    // 基础目录
//    String basePath = "";
    int id = 5;
    gt.registerFunction("camelNameConvert", new CamelNameConvert());
    gt.registerFunction("firstCharToLowerCase", new FirstCharToLowerCase());
    gt.registerFunction("toLowerCase", new ToLowerCase());

    DbModel model = DbModel.dao.findById(id);
    List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", id);

    // 查找出跟model有关的list
    List<Record> master = Db.find("select b.class_name, a.*, c.package, c.module_name from w_db_model_mapping a, w_db_model b, w_generate c where a.master_id = b.id and a.slaves_id = ? and a.master_id = c.w_model_id", id);
    // 查找出跟model相关的从表
    List<Record> slaves = Db.find("SELECT a.class_name, b.*, c.package, c.module_name FROM w_db_model a, w_db_model_mapping b, w_generate c WHERE a.id = b.slaves_id AND b.master_id = ? and c.w_model_id = b.slaves_id", id);

    Template t = gt.getTemplate("gen/pojo/4ActiveRecordEnhance.btl");
    t.binding("model", model);
    t.binding("columns", columns);

    t.binding("importNotNull", true);
    t.binding("importLength", true);
    t.binding("importNotBlank", true);

    t.binding("generate", generate);
    t.binding("master", master);
    t.binding("slaves", slaves);
    // 是否使用驼峰命名
    t.binding("camelName", true);
    System.out.println(t.render());
  }

//	@Test
	public void test_controller() {
	  int id = 3;
	  gt.registerFunction("firstCharToLowerCase", new FirstCharToLowerCase());
    DbModel model = DbModel.dao.findById(id);
    List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", id);

    // 查找出跟model有关的list
    List<Record> master = Db.find("select b.class_name, a.*, c.package, c.module_name from w_db_model_mapping a, w_db_model b, w_generate c where a.master_id = b.id and a.slaves_id = ? and a.master_id = c.w_model_id", id);
    // 查找出跟model相关的从表
    List<Record> slaves = Db.find("SELECT a.class_name, b.*, c.package, c.module_name FROM w_db_model a, w_db_model_mapping b, w_generate c WHERE a.id = b.slaves_id AND b.master_id = ? and c.w_model_id = b.slaves_id", id);

    Template t = gt.getTemplate("gen/web/4Jfinalcontroller.btl");
    t.binding("model", model);
    t.binding("columns", columns);

    t.binding("generate", generate);
    t.binding("master", master);
    t.binding("slaves", slaves);
    // 是否使用驼峰命名
    t.binding("camelName", true);
    System.out.println(t.render());
	}

//	@Test
	public void test_service() {
    int id = 3;
    gt.registerFunction("firstCharToLowerCase", new FirstCharToLowerCase());
    DbModel model = DbModel.dao.findById(id);
    List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", id);

    // 查找出跟model有关的list
    List<Record> master = Db.find("select b.class_name, a.*, c.package, c.module_name from w_db_model_mapping a, w_db_model b, w_generate c where a.master_id = b.id and a.slaves_id = ? and a.master_id = c.w_model_id", id);
    // 查找出跟model相关的从表
    List<Record> slaves = Db.find("SELECT a.class_name, b.*, c.package, c.module_name FROM w_db_model a, w_db_model_mapping b, w_generate c WHERE a.id = b.slaves_id AND b.master_id = ? and c.w_model_id = b.slaves_id", id);

    Template t = gt.getTemplate("gen/web/4curd&webservice.btl");
    t.binding("model", model);
    t.binding("columns", columns);

    t.binding("generate", generate);
    t.binding("master", master);
    t.binding("slaves", slaves);
    // 是否使用驼峰命名
    t.binding("camelName", true);
    System.out.println(t.render());
  }

//	@Test
  public void test_sqlmd() {
    int id = 3;
    gt.registerFunction("firstCharToLowerCase", new FirstCharToLowerCase());
    DbModel model = DbModel.dao.findById(id);
    List<DbModelItem> columns = DbModelItem.dao.find("select * from w_db_model_item where w_model_id = ? order by serial", id);
    Generate generate = Generate.dao.findFirst("select * from w_generate where w_model_id = ?", id);

    // 查找出跟model有关的list
    List<Record> master = Db.find("select b.class_name, a.*, c.package, c.module_name from w_db_model_mapping a, w_db_model b, w_generate c where a.master_id = b.id and a.slaves_id = ? and a.master_id = c.w_model_id", id);
    // 查找出跟model相关的从表
    List<Record> slaves = Db.find("SELECT a.class_name, b.*, c.package, c.module_name FROM w_db_model a, w_db_model_mapping b, w_generate c WHERE a.id = b.slaves_id AND b.master_id = ? and c.w_model_id = b.slaves_id", id);

    Template t = gt.getTemplate("gen/web/4mysqlmd.btl");
    t.binding("model", model);
    t.binding("columns", columns);

    t.binding("generate", generate);
    t.binding("master", master);
    t.binding("slaves", slaves);
    // 是否使用驼峰命名
    t.binding("camelName", true);
    System.out.println(t.render());
  }

  @Test
  public void test_interface_md() {
    String root = "E:\\P_Gateway_DMServer\\Branch\\StudentApartment\\src\\main";
    TempletGenerate tg = new TempletGenerate(root, root, null);
    tg.generateInterfaceMarkdownDoc(10, null);
  }

  @Test
  public void test_code_gen() {
	  String root = "E:\\P_Gateway_DMServer\\Branch\\StudentApartment\\src\\main\\java";
    TempletGenerate tg = new TempletGenerate(root, root, null);
    String classPath =
    "walle" + File.separator +
    "pstone";
    String beanClassPath =
    "walle" + File.separator +
    "pstone";

    tg.generateInterfaceControllerCode(10, classPath, beanClassPath, null);
    tg.generateInterfaceRequestBeanCode(10, beanClassPath, null);

    // PS: 只有第一次生成的时候才能使用
//    tg.generateInterfaceServiceCode(10, classPath, beanClassPath, null);
  }

}
