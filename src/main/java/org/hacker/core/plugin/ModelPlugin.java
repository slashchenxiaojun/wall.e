package org.hacker.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hacker.core.BaseModel;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;

public class ModelPlugin implements IPlugin{

	private List<File> files;
	
	private Map<String, ActiveRecordPlugin> arpMap;

	private String prefix = "com.zjhcsoft.mvc.model";
	
	Logger log = Logger.getLogger(ModelPlugin.class);
	
	public ModelPlugin(Map<String, ActiveRecordPlugin> arpMap){
		this.arpMap = arpMap;
	}
	
	public ModelPlugin(Map<String, ActiveRecordPlugin> arpMap, String prefix){
		this.arpMap = arpMap;
		this.prefix = prefix;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		files = new ArrayList<File>();
		try{
			//得到该CLASSPATH的URL路径
			String classpath = PathKit.getRootClassPath();
			//兼容JBOSS7.1.0-Final,classpath的问题
			classpath = PathKit.getWebRootPath();
			File root = new File(classpath);
			//遍历CLASSPATH下的所有.class文件并复制到files中
			files = PluginCommon.getInstance().getFileSetByEndName(classpath, "class");
			for(File f : files){
				Class<? extends BaseModel<?>> c = (Class<? extends BaseModel<?>>) PluginCommon.getInstance().getClass(root.getPath(),f.getPath());
//				Class<BaseModel> mvc_model = BaseModel.class;
				if(c.getAnnotation(Table.class) != null && c.getName().contains(prefix)){
					mappingModel(c);
				}
			}
		}catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void mappingModel(Class<? extends BaseModel<?>> model){
		Table tableBind = (Table) model.getAnnotation(Table.class);
		if (tableBind == null) {
			throw new IllegalArgumentException("继承BaseModel的类需要绑定@Table注解");
		}

		// 获取映射属性
		String dataSourceName = tableBind.dataSourceName().trim();
		String tableName = tableBind.tableName().trim();
		String pkName = tableBind.pkName().trim();
		if(dataSourceName.equals("") || tableName.equals("") || pkName.equals("")){
			throw new IllegalArgumentException("@Table注解错误，数据源、表名、主键名为空");
		}
		
		// 映射注册
		ActiveRecordPlugin arp = arpMap.get(dataSourceName);
		if(arp == null){
			throw new IllegalArgumentException("ActiveRecordPlugin不能为null,在WebConfig中添加");
		}
		log.info("Model映射:" + tableName + " Class:" + model + " pkName:" + pkName);
		arp.addMapping(tableName, pkName, (Class<? extends Model<?>>) model);
	}

}
