package org.hacker.mvc.view;

import org.beetl.core.Context;
import org.beetl.core.Function;

import com.jfinal.kit.StrKit;

/**
 * 首字符小写function
 * @author Mr.J
 *
 */
public class FirstCharToLowerCase implements Function {

  @Override
  public Object call(Object[] paras, Context ctx) {
    Object o = paras[0];
    if (o != null) {
      try {
        String name = (String)o;
        return StrKit.firstCharToLowerCase(name);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return "";
  }

}

