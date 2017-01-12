package org.hacker.aop.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hacker.module.common.WebKit;

import com.jfinal.handler.Handler;

/**
 * GlobalHandler 添加一个全局的处理，如：日志，路径等
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-03-21
 * */
public class GlobalHandler extends Handler{

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		// 设置全局属性: web根路径
		request.setAttribute("base", WebKit.getPath(request));
		next.handle(target, request, response, isHandled);
	}

}
