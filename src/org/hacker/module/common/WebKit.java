package org.hacker.module.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A Web helper tools
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-03-28
 * **/
public class WebKit {
	/**
	 * 获取绝对全局路径（根url，包含Java容器的content（TOMCAT,JBOSS等））
	 * */
	public static String getPath(HttpServletRequest request){
		String base = request.getScheme() + "://" + 
		request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + 
		request.getServerPort()) + request.getContextPath();		
		//jetty的getContextPath在结尾有一个'/',这里把它去掉
		return base.substring(0, base.length());
	}
	/**
	 * 获取现有URI字符串（包含servletPath），包括请求参数（多个参数用‘，’分割）
	 * */
	public static String getPathAndParamter(HttpServletRequest request){
		Map<String, String[]> params = request.getParameterMap();
		String uri = getPath(request) + request.getServletPath();
		if(params.size() == 0)
			return uri;
		else{
			uri += "?";
		}
		for(String key : params.keySet()){
			uri += key + "=";
				for(String value : params.get(key)){
						uri += value + ",";
				}
			uri = uri.substring(0,uri.length()-1) + "&";
		}
		return uri.substring(0,uri.length()-1);
	}
	/**
	 * 获取现有URI字符串根据enc进行URL编码，包括请求参数（多个参数用‘，’分割）
	 * */
	public static String getPathAndParamterURLEncoder(HttpServletRequest request,String enc){
		try {
			return URLEncoder.encode(getPathAndParamter(request),enc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * URL反编码
	 * 
	 * @param url
	 * @param enc 编码
	 * @throws UnsupportedEncodingException
	 * */
	public static String getURLDecoder(String url, String enc){
		try {
			return URLDecoder.decode(url, enc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * URL编码
	 * 
	 * @param url
	 * @param enc 编码
	 * @throws UnsupportedEncodingException
	 * */
	public static String getURLEncoder(String url, String enc){
		try {
			return URLEncoder.encode(url, enc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
