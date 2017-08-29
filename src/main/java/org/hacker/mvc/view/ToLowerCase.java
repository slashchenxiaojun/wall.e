package org.hacker.mvc.view;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 小写function
 * @author Mr.J
 *
 */
public class ToLowerCase implements Function {

  @Override
  public Object call(Object[] paras, Context ctx) {
    Object o = paras[0];
    if (o != null) {
      try {
        String name = (String)o;
        return name.toLowerCase();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return "";
  }

}

