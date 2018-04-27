package org.hacker.dataschema;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mr.j on 2018/1/9.
 */
public class Tree<T> {
  private T data;
  private Tree<T> parent;
  private List<Tree<T>> childrenNodes;

  public Tree(T data, Tree<T> parent, List<Tree<T>> childrenNodes) {
    this.data = data;
    this.childrenNodes = childrenNodes;
    this.parent = parent;
  }

  public Tree<T> getRoot() {
    Tree<T> node = parent;
    while ( node != null ) {
      node = node.parent;
    }
    return node;
  }

  public T getData() {
    return data;
  }

  public Tree<T> getParent() {
    return parent;
  }

  public List<Tree<T>> getChildren() {
    return childrenNodes;
  }

  public void addChildren(T child) {
    if ( child == null )
      throw new IllegalArgumentException();

    Tree<T> parent = this;
    Tree<T> node = new Tree<>(child, parent, null);

    if ( parent.childrenNodes == null )
      parent.childrenNodes = new LinkedList<>();

    parent.childrenNodes.add(node);
  }

  public void addChildren(Tree<T> child) {
    Tree<T> parent = this;

    if ( parent.childrenNodes == null )
      parent.childrenNodes = new LinkedList<>();

    parent.childrenNodes.add(child);
  }

  public void removeChildren(T parent, T child) {

  }

  public T find(String regex) {
    return null;
  }

  private Tree<T> findNode(T node) {
    for ( int i = 0; i < this.childrenNodes.size(); i++ ) {
      Tree<T> tempTree = this.childrenNodes.get(i);
      if ( tempTree.data.equals(node) ) {
        return tempTree;
      } else if ( tempTree.childrenNodes != null && tempTree.childrenNodes.size() > 0 ) {
        return findNode(tempTree.data);
      }
    }
    return null;
  }
}
