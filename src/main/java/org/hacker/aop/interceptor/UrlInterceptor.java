package org.hacker.aop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by mr.j on 2017/11/6.
 */
public class UrlInterceptor implements Interceptor {
  @Override
  public void intercept(Invocation invocation) {
    System.out.println(invocation.getActionKey());
    invocation.invoke();
  }
}
