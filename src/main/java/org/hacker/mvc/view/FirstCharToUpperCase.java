package org.hacker.mvc.view;

import com.jfinal.kit.StrKit;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * Created by Mr.J. on 2017/8/28.
 */
public class FirstCharToUpperCase implements Function {

  @Override
  public Object call(Object[] paras, Context ctx) {
    Object o = paras[0];
    if (o != null) {
      try {
        String name = (String)o;
        return StrKit.firstCharToUpperCase(name);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return "";
  }

}