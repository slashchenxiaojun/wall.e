package org.hacker.mvc.model;

import java.util.List;

import org.hacker.core.plugin.Table;

import org.hacker.mvc.model.base.BaseProject;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
@Table(tableName="w_project")
public class Project extends BaseProject<Project> {
	public static final Project dao = new Project();

  public List<DbModel> getModelList() {
    return DbModel.dao.find("select * from w_db_model where project_id = ?", getId());
  }
}
