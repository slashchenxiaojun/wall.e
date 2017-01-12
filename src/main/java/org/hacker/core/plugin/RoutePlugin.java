package org.hacker.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hacker.core.BaseController;

import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;

/**
 * <p>自定义的默认路由基类，该类通过反射将Controller（in general the pageName is "com.zjhcsoft.mvc.controller"）
 * 中的所有继承了Controller的java类定义默认的路由规则('包名+类名(除去Controller)')，而不必每个路由都需要我们自己在JFinal中去定义，默认规则是：me.add("XXX", XXX.class);</p>
 * 
 * <p>如:com.zjhcsoft.mvc.controller.admin.UserController
 * <p>则路由规则为: '/admin/user' View路径和路由路径相同 全部为小写
 * <p>注意:com.zjhcsoft.mvc.controller.admin.Usercontroller
 * <P>XXXController是路由类的规范，则否会抛出异常
 * 
 * @version 2.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2014-12-05
 * **/
public class RoutePlugin implements IPlugin{
	
    private final Logger log = Logger.getLogger(getClass());
    
    private Routes me;
    
    private List<File> files;

    private String prefix = "org.hacker.mvc.controller";
    
	public RoutePlugin(Routes me){
		this.me = me;
	}
	
	public RoutePlugin(Routes me, String prefix){
		this.me = me;
		this.prefix = prefix;
	}
	
	@SuppressWarnings("unchecked")
	public boolean start() {
		// TODO Auto-generated method stub
		files = new ArrayList<File>();
		try {
			//得到该CLASSPATH的URL路径
			String classpath = PathKit.getRootClassPath();
			//兼容JBOSS7.1.0-Final,classpath的问题
			classpath = PathKit.getWebRootPath();
			log.info("root classpath:" + classpath);
			File root = new File(classpath);
			//遍历CLASSPATH下的所有.class文件并复制到files中
			files = PluginCommon.getInstance().getFileSetByEndName(classpath, "class");
			for(File f : files){
				Class<?> c = PluginCommon.getInstance().getClass(root.getPath(),f.getPath());
				Class<BaseController> mvc_controller = BaseController.class;
				//如果继承了BaseController类，则给加上通用的规则
				//后期如果有注册，那么就使用注解的规则
				if(mvc_controller.isAssignableFrom(c) && c.getName().contains(prefix)){
					String oldClassName = c.getName();
					//获取去除前缀(如:com.zjhcsoft.mvc.controller)后的ClassName
					oldClassName = oldClassName.substring(oldClassName.indexOf(prefix) == 0 ? prefix.length() + 1 : oldClassName.indexOf(prefix) + prefix.length() + 1);
					String controllerKey = _getPathByClassName(oldClassName);
					log.info("Route注册:" + controllerKey);
					me.add(controllerKey.toLowerCase(),(Class<Controller>)c,controllerKey.toLowerCase());
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean stop() {
		return true;
	}
	
	private String _getPathByClassName(String className){
		String[] array = className.split("\\.");
		String controllerKey = "/";
		for(String s : array){
			s = s.toLowerCase();
			if(s.contains("controller")){
				if(s.indexOf("controller") == 0){
					throw new IllegalArgumentException("ControllerController.java 是不符合命名规范的");
				}
				s = s.substring(0, s.indexOf("controller"));
			}
			controllerKey += s + "/";
		}
		return controllerKey.substring(0, controllerKey.length() - 1);
	}
}
