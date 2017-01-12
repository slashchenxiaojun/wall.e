package org.hacker.core.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.beetl.core.BeetlKit;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * SQL是一个快速检索出md文件中sql的工具
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-11-06
 **/
public class SQL {
	private static SQL instance = new SQL();
	
	private SQL(){}
	
	private static Map<String, String> _id_sql = new HashMap<>();
	
	public static SQL getInstance(){ return instance; }
	
	public void put(String id, String sql){
		_id_sql.put(id, sql);
	}
	
	public static String get(String id){
		return get(id, null);
	}
	
	public static String get(String id, Map<String, Object> paras){
		String _sql = _id_sql.get(id);
		if(StrKit.isBlank(_sql)) throw new Error("Oop~ start SQLPlugin frist.");
		return BeetlKit.render(_sql, paras);
	}
	
	public static List<Record> getResult(String id, Map<String, Object> paras){
		String _sql = get(id, paras);
		Object[] objs = new Object[paras.size()];
		Iterator<String> it = paras.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			String key = it.next();
			Object obj = paras.get(key);
			objs[index++] = obj;
		}
		return Db.find(_sql, objs);
	}
	
}
