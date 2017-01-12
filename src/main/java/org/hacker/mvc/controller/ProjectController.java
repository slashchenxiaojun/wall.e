package org.hacker.mvc.controller;

import static org.hacker.module.common.Assert.checkNotNull;

import java.util.List;

import org.hacker.core.BaseController;
import org.hacker.mvc.model.Folder;
import org.hacker.mvc.model.Interface;
import org.hacker.mvc.model.Project;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ProjectController extends BaseController {
	public void index() {
	  setAttr("projects", Project.dao.find("select * from w_project"));
	}
	
	public void folderForm() {
		render("_folder_form.html");
	}
	
	public void create() {
		String 
		name 	 = getPara("project.name"),
		base_url = getPara("project.base_url");
		
		checkNotNull(name, "project.name");
		checkNotNull(base_url, "project.base_url");
		
		Project project = new Project();
		project.setName(name);
		// defualt pattern is simulation
		project.setPattern("simulation");
		project.setBaseUrl(base_url);
		
		if(project.save())
			OK();
		else
			Error(500, "Oop! create project fail.");
	}
	
	/**
	 * 一次性获取到接口的树形结构json
	 * @throws Exception 
	 */
	public void getTreeFolder() {
		Integer project_id = getParaToInt("project.id");
		
		checkNotNull(project_id, "project.id");
		
		JSONArray nodes = new JSONArray(1);
		// 寻找root
		List<Folder> list = getTreeNode(project_id, 0, 1);
		for(Folder folder : list) {
			JSONObject tree_node = new JSONObject();
			JSONArray node = new JSONArray();
			List<Interface> inodes = getInterfaceNode(folder.getId());
			for(Interface iface : inodes) {
				JSONObject children = new JSONObject();
				children.put("interface_id", iface.getId());
				children.put("code", iface.getCode());
				children.put("name", iface.getName());
				// 使用自定义图标
				children.put("iconSkin", "icon01");
				node.add(children);
			}
			if(node.size() > 0) {
				tree_node.put("children", node);
			} else {
				// 使用自定义图标
				tree_node.put("iconSkin", "icon00");
			}
			tree_node.put("folder_id", folder.getId());
			tree_node.put("name", folder.getName());
			nodes.add(tree_node);
		}
		OK(nodes);
	}
	
	// 暂时只满足一层级的需求
	public void createFolder() {
		Integer project_id = getParaToInt("project.id");
		String name = getPara("folder.name");
		
		checkNotNull(project_id, "project.id");
		checkNotNull(name, "folder.name");
		
		Folder folder = new Folder();
		folder.setLevel(1);
		folder.setName(name);
		folder.setRootId(0);
		folder.setPid(0);
		folder.setWProjectId(project_id);
		if(folder.save())
			OK();
		else
			Error(500, "Oop! create folder fail.");
	}
	
	public void deteleFolder() {
		Integer folder_id = getParaToInt("folder.id");
		
		checkNotNull(folder_id, "folder.id");
		
		boolean hasInter = Interface.dao.query("w_folder_id = ?", folder_id).size() > 0;
		if(hasInter) {
			Error(400, "Oop! folder has interface can't detele"); return;
		}
		if(Folder.dao.deleteById(folder_id))
			OK();
		else 
			Error(500, "Oop! delete folder fail.");
	}
	
	/**
	 * 适配layui-tree的数据结构,并且暂时只支持一层
	 * @return
	 */
	private List<Folder> getTreeNode(int project_id, int root_id, int level) {
		List<Folder> list = Folder.dao.find("select * from w_folder a where a.w_project_id = ? and a.`level` = ? and a.root_id = ?", project_id, level, root_id);
		return list;
	}
	
	/**
	 * 适配layui-tree的数据结构,并且暂时只支持一层
	 * @return
	 */
	private List<Interface> getInterfaceNode(int folder_id) {
		List<Interface> list = Interface.dao.find("select * from w_interface a where a.w_folder_id = ?", folder_id);
		return list;
	}
}
