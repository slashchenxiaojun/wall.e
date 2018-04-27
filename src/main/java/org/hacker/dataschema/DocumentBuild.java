package org.hacker.dataschema;

/**
 * 用于将JSON Schema MetaData转化为可读的文档
 * 目前支持markdown在Mac上配合iA Write可以生成非常漂亮的pdf文档
 * 缺点是pdf不支持目录
 *
 * Created by mr.j on 2018/1/10.
 */
public class DocumentBuild {
  Tree<MetaData> rootNode;

  public DocumentBuild(Tree<MetaData> rootNode) {
    this.rootNode = rootNode;
  }

  public void buildDocument() {

  }

}
