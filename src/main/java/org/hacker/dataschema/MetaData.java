package org.hacker.dataschema;

/**
 * Created by mr.j on 2018/1/9.
 */
public class MetaData {
  private DataType type;
  private String title;
  private String desc;
  private Integer min;
  private Integer max;
  private String pattern;
  private Boolean required;

  public MetaData(DataType type, String title) {
    this.type = type;
    this.title = title;
    this.desc = title;
  }

  public MetaData(DataType type, String title, String desc) {
    this.type = type;
    this.title = title;
    this.desc = desc;
  }

  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  @Override
  public String toString() {
    return "MetaData{" +
            "type=" + type +
            ", title='" + title + '\'' +
            ", desc='" + desc + '\'' +
            ", min=" + min +
            ", max=" + max +
            ", pattern='" + pattern + '\'' +
            ", required=" + required +
            '}';
  }
}
