package org.hacker.module.common;

import java.util.Map;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.ext.jfinal.BeetlRenderFactory;

/**
 * Beetl模板 Kit ^^ (Beetl is my best like...)
 * 
 * @author Mr.J.
 * 
 * @since  2015.3.31
 * **/
public class BeetlKit {
	
	//获取全局变量groupTemplate
	public static GroupTemplate groupTemplate = BeetlRenderFactory.groupTemplate;
	
	/**
	 * 将写好的template转化为String
	 * 
	 * @param templatePath 模板路径(包含模板全名)
	 * **/
	public static String getTemplateToString(String templatePath){
		return getTemplateToString(templatePath, null);
	}
	
	/**
	 * 将写好的template转化为String(接受参数)
	 * 
	 * @param templatePath 模板路径(包含模板全名)
	 * @param param 参数
	 * **/
	public static String getTemplateToString(String templatePath, Map<?, ?> param){
		Template t = groupTemplate.getTemplate(templatePath);
		t.binding(param);
		return t.render();
	}
	
}
