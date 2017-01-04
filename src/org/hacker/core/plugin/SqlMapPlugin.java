package org.hacker.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;

/**
 * SqlMapPlugin是一个SQL映射的插件，它使用到了Beetl作为模板引擎
 * 可以实现SQL的动态拼装
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-11-06
 **/
public class SqlMapPlugin implements IPlugin{

	Logger log = Logger.getLogger(this.getClass());
	
	private List<File> files;
	
	@Override
	public boolean start() {
		try {
			// 得到该CLASSPATH的URL路径
			String classpath = PathKit.getRootClassPath();
			// 兼容JBOSS7.1.0-Final,classpath的问题
			classpath = PathKit.getWebRootPath();
			// 遍历CLASSPATH下的所有.class文件并复制到files中
			files = PluginCommon.getInstance().getFileSetByEndName(classpath, "sql.md");
			for(File f : files){
				String modelPathOrSQLName = "";
				modelPathOrSQLName = f.getName();
				String path = f.getPath();
				int index = path.indexOf("model");
				modelPathOrSQLName = path.substring(index + "model".length() + 1);
				SqlManager sqlManager = new SqlManager(path);
				Map<String, String> sqls = sqlManager.getSqls();
				modelPathOrSQLName = modelPathOrSQLName.substring(0, modelPathOrSQLName.indexOf(".sql.md"));
				modelPathOrSQLName = modelPathOrSQLName.replace(File.separatorChar + "", ".");
				Iterator<String> it = sqls.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String obj = sqls.get(key);
					log.info("SQL: " + modelPathOrSQLName + "." + key + "已经绑定");
					SQL.getInstance().put(modelPathOrSQLName + "." + key, obj);
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
