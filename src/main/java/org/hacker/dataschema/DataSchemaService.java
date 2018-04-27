package org.hacker.dataschema;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 提供JSON meta data的服务
 * Created by mr.j on 2018/1/9.
 */
public class DataSchemaService {
  public static void load() {

  }

  public static Tree<MetaData> toTree(String json) {
    Tree<MetaData> root = new Tree<>(null, null, null);
    return _toTree(root, json);
  }

  public static void printlnTree(List<Tree<MetaData>> nodes, int level) {
    if ( level < 0 ) level = 0;
    if ( nodes == null ) return;

    String empty = "";
    for ( int i = 0; i < level; i++ ) {
      empty += "\t";
    }
    level++;
    for ( Tree<MetaData> node : nodes ) {
      System.out.println(String.format(empty + "meta: %s", node.getData().toString()));
      printlnTree(node.getChildren(), level);
    }
  }

  private static Tree<MetaData> _toTree(Tree<MetaData> root, String json) {
    Map<String, JSONObject> map = (Map<String, JSONObject>) JSON.parse(json);
    Iterator<String> it = map.keySet().iterator();
    while ( it.hasNext() ) {
      String key = it.next();
      JSONObject value = map.get(key);
      MetaData metaData = getMetaData(key, value);
      Tree<MetaData> node = new Tree<>(metaData, root, null);
      root.addChildren(node);

      switch ( metaData.getType() ) {
        case ENUM:
          handlerEnum(node, value);
          break;
        case OBJECT:
          String properties = value.getString("properties");
          checkObjectElement(properties, key, value);

          _toTree(node, properties);
          break;
        case ARRAY:
          handlerArray(node, value);
          break;
      }
    }
    return root;
  }

  private static void checkObjectElement(String properties, String title, JSONObject value) {
    if ( StrKit.isBlank(properties) )
      throw new IllegalArgumentException(String.format("title: %s - value: %s --- OBJECT类型必须包含properties元素", title, value.toJSONString()));

    Map<String, JSONObject> propertiesMap = (Map<String, JSONObject>) JSON.parse(properties);
    if ( propertiesMap == null || propertiesMap.size() == 0 )
      throw new IllegalArgumentException(String.format("title: %s - value: %s --- OBJECT类型的properties元素不能为null", title, value.toJSONString()));
  }

  private static Map<String, Object> checkArrayElementAndGet(String properties, JSONObject value) {
    if ( StrKit.isBlank(properties) )
      throw new IllegalArgumentException(String.format("value: %s --- ENUM,ARRAY类型必须包含items元素", value.toJSONString()));

    Map<String, Object> propertiesMap = (Map<String, Object>) JSON.parse(properties);
    if ( propertiesMap == null || propertiesMap.size() == 0 )
      throw new IllegalArgumentException(String.format("value: %s --- ENUM,ARRAY类型的items元素不能为null", value.toJSONString()));

    return propertiesMap;
  }

  private static void handlerEnum(Tree<MetaData> enumNode, JSONObject value) {
    String items = value.getString("items");
    Map<String, Object> itemsMap = checkArrayElementAndGet(items, value);

    Iterator<String> it = itemsMap.keySet().iterator();
    while ( it.hasNext() ) {
      // enum的field的名称
      String key = it.next();
      // enum的属性(只支持decs)
      JSONObject itemValue = (JSONObject) itemsMap.get(key);
      String desc = itemValue.getString("desc");
      // enum的子field类型会忽略，这里使用DataType.STRING填充
      MetaData metaData = new MetaData(DataType.STRING, key, desc);
      enumNode.addChildren(metaData);
    }
  }

  private static void handlerArray(Tree<MetaData> arrayNode, JSONObject value) {
    String items = value.getString("items");
    Map<String, Object> itemsMap = checkArrayElementAndGet(items, value);

    String type = (String) itemsMap.get("type");
    if ( StrKit.notBlank(type) && !type.equals("object") ) {
      DataType dataType = getDataType(type);
      if ( dataType == DataType.ENUM || dataType == DataType.ARRAY ) {
        throw new IllegalArgumentException(String.format("ARRAY类型的子类型不能是ENUM, ARRAY"));
      }
      MetaData metaData = new MetaData(dataType, null);
      arrayNode.addChildren(metaData);
    } else {
      // object类型
      Iterator<String> it = itemsMap.keySet().iterator();
      while ( it.hasNext() ) {
        String key = it.next();
        Object itemValue = itemsMap.get(key);
        if ( itemValue instanceof String && key.equals("type") && !itemValue.equals("object") ) {
          throw new IllegalArgumentException(String.format("ARRAY类型的子类型type=null那么必须是object"));
        }
        if ( itemValue instanceof JSONObject && key.equals("properties") ) {
          _toTree(arrayNode, JSON.toJSONString(itemValue));
        }
      }
    }
  }

  /**
   * 获取MetaData对象, value是完整是数据描述JSON
   * {
   * "type": "[int, string, bool, number, date, enum, object, array]",
   * "desc": "描述",
   * "properties": {当且仅当type=object},
   * "items": {当且仅当type=array,并且其中只能包含type和properties},
   * "min": "当且仅当type=int,string时, 表示[int,string长度]的最小值 默认=0",
   * "max": "当且仅当type=int,string时, 表示[int,string长度]的最大值 默认=0x7fffffff",
   * "pattern": "当且仅当type=string, 表示正则表达式",
   * "required": "[bool -> 是否为非空字段] 默认=false"
   * }
   */
  private static MetaData getMetaData(String title, JSONObject value) {
    String desc = value.getString("desc");
    DataType dataType = getDataType(value);
    MetaData metaData = new MetaData(dataType, title, desc);

    Integer max = value.getInteger("max");
    Integer min = value.getInteger("max");
    String pattern = value.getString("pattern");
    Boolean required = value.getBoolean("required");
    metaData.setMax(max);
    metaData.setMin(min);
    metaData.setPattern(pattern);
    metaData.setRequired(required == null ? false : required);
    return metaData;
  }

  private static DataType getDataType(JSONObject value) {
    String type = value.getString("type");
    return getDataType(type);
  }

  private static DataType getDataType(String type) {
    if ( StrKit.isBlank(type) ) {
      throw new IllegalArgumentException(String.format("type不能为null", type));
    }
    type = type.toLowerCase();
    DataType dataType = null;
    switch ( type ) {
      case "integer":
      case "int":
        dataType = DataType.INTEGER;
        break;
      case "string":
        dataType = DataType.STRING;
        break;
      case "boolean":
      case "bool":
        dataType = DataType.BOOLEAN;
        break;
      case "number":
        dataType = DataType.NUMBER;
        break;
      case "date":
        dataType = DataType.DATE;
        break;
      case "enum":
        dataType = DataType.ENUM;
        break;
      case "object":
        dataType = DataType.OBJECT;
        break;
      case "array":
        dataType = DataType.ARRAY;
        break;
    }
    if ( dataType == null )
      throw new IllegalArgumentException(String.format("非法的type: %s, 正确的type=[int, string, bool, number, date, enum, object, array]", type));

    return dataType;
  }

}
