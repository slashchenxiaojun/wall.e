package org.hacker.mvc.view;

import com.jfinal.kit.StrKit;
import org.beetl.core.Context;
import org.beetl.core.Function;

import java.math.BigDecimal;

/**
 * 转化为java的类型
 * @author Mr.J
 *
 */
public class ToJavaType implements Function {

  @Override
  public Object call(Object[] paras, Context ctx) {
    Object o = paras[0];
    // 对于枚举是类的名字,对于array是泛型类型
    Object v = paras[1];
    if ( o != null && v != null ) {
      try {
        String type = (String)o;
        switch ( type ) {
          case "string": return "String";
          case "integer": return "Integer";
          case "boolean": return "Boolean";
          case "number": return "BigDecimal";
          case "date": return "Date";
          case "object": return "JSONObject";
          // 特殊的是枚举和数组
          // 数组目前只能支持java的基础类型,enum不支持
          case "array": return "List<" + StrKit.firstCharToUpperCase(v.toString()) + ">";
          case "enum": return StrKit.firstCharToUpperCase(v.toString());
          default:
            throw new Error("不支持: "  + type);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return "";
  }

}

