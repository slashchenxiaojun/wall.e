package org.hacker.core;

import java.util.Map;

import org.hacker.core.plugin.SQL;

public class BaseService {
  /**
   * 使用sqlPlugin获取SQL
   * @param id
   * @return
   */
  protected String getSql(String id) {
    return getSql(id, null);
  }
  
  protected String getSql(String id, Map<String, Object> paras) {
    return SQL.get(id, paras);
  }
}
