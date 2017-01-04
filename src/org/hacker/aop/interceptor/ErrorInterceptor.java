package org.hacker.aop.interceptor;

import org.hacker.core.BaseController;
import org.hacker.core.Play;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

/**
 * handler normal error
 * 
 * @author Mr.J
 *
 */
public class ErrorInterceptor implements Interceptor {
  private static boolean isDebug = false;
  
  static {
    isDebug = Play.isJFinalDebug();
  }
  
	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch(Exception e) {
			BaseController controller = (BaseController)inv.getController();
			String exceptionMessage = "Oop~ Server Exception";
			Throwable t = e.getCause();
			if(t != null) exceptionMessage = t.getMessage();
			else if(StrKit.notBlank(e.getMessage())) exceptionMessage = e.getMessage();
			else e.printStackTrace();
			controller.Error(500, isDebug ? exceptionMessage : null);
		}
	}

}
