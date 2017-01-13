package org.hacker.mvc.controller;

import org.hacker.core.BaseController;
import org.hacker.mvc.model.Project;

import com.jfinal.kit.StrKit;

public class HomeController extends BaseController {
  public void index() {}
  
  public void create() {
    Project project = getModel(Project.class, "project");
    if(StrKit.notBlank(project.getName())) {
      project.save();
      redirect("/project?projectId=" + project.getId());
      return;
    }
    render("create.html");
  }
}
