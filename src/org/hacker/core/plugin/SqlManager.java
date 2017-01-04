package org.hacker.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hacker.module.common.FileKit;

import com.jfinal.kit.StrKit;

/***
 * 这是一个SQL的映射工具，使用了beetl作为模板
 * 目的是可以通过条件动态的生产sql，当然也可以
 * 把它写成是mybatis那样的架构，但是只要能满足
 * 当前项目的需要就不添加额外的内容
 * 
 * 存放sql的文件以md结尾(这个也是markdown的文
 * 件格式，方便开发者使用markdown进行阅读) SQL
 * 以```开始和结束```后面跟着的就是ID，如：
 * 
 * ```selectAll
 * SELECT * FROM XXX
 * ```
 * 使用getSQL("selectAll")就可以获取到相应的SQL
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-11-05
 */
public class SqlManager {
	//存放SQL的list
	private Map<String, String> sqls = null;
	//modelPath 模型相对路径(如果在basePath下还有子层级，则为xx/xx/xx/xx这种形式)
	private String modelPath;
	
	public SqlManager() {}

	public SqlManager(String modelPathOrSQLName) throws UnsupportedEncodingException {
		super();
		this.modelPath = modelPathOrSQLName;
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws FileNotFoundException, UnsupportedEncodingException {
		File _sql_md = new File(modelPath);
		// 读取文件中的字符串(包含换行符)
		String sql = FileKit.read(_sql_md);
		if(StrKit.notBlank(sql)) {
			sqls = new HashMap<String, String>();
			Pattern pattern = Pattern.compile("```(\\w)++\\s");
	        Matcher matcher = pattern.matcher(sql);
	        // 存放SQLID和结束的游标
	        Map<String, Integer> _id_index = new HashMap<String, Integer>();
	        while (matcher.find()) { 
	        	String group = matcher.group();
	        	//获取SQLID
	        	group = group.substring(3, group.length());
	        	int end = matcher.end();
	        	//如果存在同名的ID抛出异常
	        	if(_id_index.containsKey(group)) throw new IllegalArgumentException("SQL文件存在相同命名: " + group);
	        	_id_index.put(group, end);
	        }
	        
	        Iterator<String> it = _id_index.keySet().iterator();
	        while (it.hasNext()) {
    				String key = it.next();
    				Integer index = _id_index.get(key);
    				String _sql = sql.substring(index);
    				int end = _sql.indexOf("```");
    				String _idsql = _sql.substring(0, end);
    				//去掉\r\n的空字符
    				sqls.put(key.trim(), _idsql.trim());
	        }
		  }
	}

	/**
	 * 返回SQL
	 * 
	 * @param id SQL的唯一标示
	 * @return
	 */
	public String getSQL(String id) {
		return sqls.get(id);
	}
	
	public Map<String, String> getSqls() {
		return sqls;
	}

	public void printStack() {
		Iterator<String> it = sqls.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object obj = sqls.get(key);
			System.out.println(key + "=" + obj);
		}
	}
}
