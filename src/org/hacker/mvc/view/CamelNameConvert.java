package org.hacker.mvc.view;

import java.io.IOException;

import org.beetl.core.Context;
import org.beetl.core.Function;

import com.jfinal.kit.StrKit;

public class CamelNameConvert implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		Object o = paras[0];
		if (o != null) {
			try {
				String name = (String)o;
				String[] names = name.split("_");
				if(names.length == 1) {
					return StrKit.firstCharToUpperCase(names[0]);
				}
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < names.length; i++) {
					sb.append(StrKit.firstCharToUpperCase(names[i]));
				}
				ctx.byteWriter.writeString(sb.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return "";
	}

}
