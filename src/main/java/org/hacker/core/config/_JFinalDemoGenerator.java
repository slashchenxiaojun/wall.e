package org.hacker.core.config;

import javax.sql.DataSource;

import org.hacker.core.Dict;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalDemoGenerator {
	
	public static DataSource getDataSource() {
		WebConfig config = new WebConfig();
		config.loadPropertyFile("play.properties");
		DruidPlugin dp = new DruidPlugin(
				config.getProperty(Dict.CONFIG_JDBC_URL), 
				config.getProperty(Dict.CONFIG_JDBC_USERNAME), 
				config.getProperty(Dict.CONFIG_JDBC_PASSWORD).trim(), 
				null, "stat,wall");
		dp.start();
		return dp.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "org.hacker.mvc.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/org/hacker/mvc/model/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "org.hacker.mvc.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), 
				new CustomBaseModelGenerator(baseModelPackageName, baseModelOutputDir),
				new CustomModelGenerator(modelPackageName, baseModelPackageName, modelOutputDir));
		// 添加不需要生成的表名
		gernerator.addExcludedTable(new String[]{""});
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes(new String[]{"w_"});
		// 生成
		gernerator.generate();
	}
}




