package org.hacker.core.config;

import com.jfinal.plugin.ehcache.EhCachePlugin;
import org.apache.log4j.Logger;
import org.beetl.ext.jfinal.BeetlRenderFactory;
import org.hacker.aop.handler.GlobalHandler;
import org.hacker.aop.interceptor.ErrorInterceptor;
import org.hacker.core.Dict;
import org.hacker.core.plugin.RoutePlugin;
import org.hacker.mvc.controller.HomeController;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;

public class WebConfig extends JFinalConfig {
	Logger log = Logger.getLogger(WebConfig.class);
	
	@Override
	public void configConstant(Constants me) {
		loadPropertyFile("play.properties");
		me.setDevMode(getPropertyToBoolean(Dict.CONFIG_JFINAL_MODE, false));
		me.setBaseViewPath("static/app");
		me.setMainRenderFactory(new BeetlRenderFactory(PathKit.getWebRootPath()));
	}

	@Override
	public void configRoute(Routes me) {
		new RoutePlugin(me).start();
		me.add("/", HomeController.class, "home");
	}

	@Override
	public void configPlugin(Plugins me) {
		PluginFactory.startActiveRecordPlugin();
		me.add( new EhCachePlugin() );
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new ErrorInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new GlobalHandler());
	}
	
}