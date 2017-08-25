package org.hacker.mvc.controller;

import static org.hacker.module.common.Assert.checkNotNull;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.hacker.core.BaseController;
import org.hacker.exception.ApiException;
import org.hacker.mvc.model.Folder;
import org.hacker.mvc.model.Interface;
import org.hacker.mvc.model.Parameter;
import org.hacker.mvc.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;

public class ProjectController extends BaseController {
  Logger LOG = LoggerFactory.getLogger(getClass());
  
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
		
		// 检查同一个项目，同一个目录是否有相同名字的folder
		checkSameFolderName(project_id, 1, name);
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
	
	public void renameFolder() {
	  Integer project_id = getParaToInt("project.id");
	  Integer folder_id = getParaToInt("folder.id");
    String name = getPara("folder.name");
    
    checkNotNull(project_id, "project.id");
    checkNotNull(folder_id, "folder.id");
    checkNotNull(name, "folder.name");
    
    // 检查同一个项目，同一个目录是否有相同名字的folder
    checkSameFolderName(project_id, 1, name);
    Folder folder = Folder.dao.findById(folder_id);
    if (folder == null) {
      Error(500, "Oop! folder not exit."); return;
    }
    folder.setName(name);
    if(folder.update())
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
	
	// 获取folder下的所有接口
	public void getInterfaceByFolder() {
	  Integer projectId = getParaToInt("project.id");
	  Integer folderId = getParaToInt("folder.id");
	  
	  checkNotNull(projectId, "project.id");
	  checkNotNull(folderId, "folder.id");
	  
	  OK(Interface.dao.find("select * from w_interface where w_project_id = ? and  w_folder_id = ?", projectId, folderId));
	}
	
	public void getInterface() {
	  Integer interfaceId = getParaToInt("interface.id");
	   
	  checkNotNull(interfaceId, "interface.id");
    
	  OK(Interface.dao.findById(interfaceId));
	}
	 
	// 快速创建接口
	public void createInterfaceQuick() {
	  Integer projectId = getParaToInt("project.id");
	  String folderId = getPara("folder.id");
	  String name = getPara("interface.name");
//	  String code = getPara("interface.code");
	  
	  checkNotNull(projectId, "project.id");
	  checkNotNull(folderId, "folder.id");
	  checkNotNull(name, "interface.name");
//	  checkNotNull(code, "interface.code");
	  
	  Folder folder = Folder.dao.findById(folderId);
	  if (folder == null) {
	    Error(500, "Oop! not exist folder name."); return;
	  }
//	  checkSameInterfaceCode(projectId, folder.getId(), code);
	  
	  Interface interfaces = new Interface();
	  interfaces.setName(name);
	  interfaces.setWProjectId(projectId);
	  interfaces.setWFolderId(folder.getId());
//	  interfaces.setCode(code);
	  if (interfaces.save())
	    OK();
	  else
	    Error(500, "Oop! create interface fail.");
	}
	
	public void interfaceForm() {
	  Integer interfaceId = getParaToInt("interfaceId");
	  setAttr("interface", Interface.dao.findById(interfaceId));
		setAttr("paramList", Parameter.dao.find("SELECT * FROM w_parameter WHERE w_interface_id = ?", interfaceId));
	  render("_interface_form.html");
	}
	
  public void interfaceData() {
    Integer interfaceId = getParaToInt("interfaceId");
    setAttr("interface", Interface.dao.findById(interfaceId));
    render("_interface_data.html");
  }	
	
  public void interfaceSimulationData() {
    Integer interfaceId = getParaToInt("interfaceId");
    setAttr("interface", Interface.dao.findById(interfaceId));
    render("_interface_simulation_data.html");
  }

  @Before(Tx.class)
	public void saveInterface() {
	  Integer projectId = getParaToInt("project.id");
	  Interface interfaces = getModel(Interface.class, "interface");
	  List<Parameter> parameterList = getModels(Parameter.class, "param");

	  checkNotNull(projectId, "project.id");
	  checkNotNull(interfaces.getCode(), "interface.code");
	  
	  boolean create = interfaces.getId() == null;
	  if (create) {
			interfaces.save();
			for (Parameter parameter : parameterList) {
				parameter.setWInterfaceId(interfaces.getId());
				parameter.save();
			}
//	    throw new ApiException("Oop! interfaces id is null");
	  } else {
	    checkSameInterfaceCode(projectId, interfaces.getCode(), interfaces.getId());
	    if (interfaces.update()) {
				Db.update("DELETE FROM w_parameter WHERE w_interface_id = ?", interfaces.getId());
				for (Parameter parameter : parameterList) {
					parameter.setWInterfaceId(interfaces.getId());
					parameter.save();
				}
	    	OK();
			}
	    else Error(500, "Oop! save interface fail.");
	  }
	}
	
	public void renameInterface() {
	  Integer projectId = getParaToInt("project.id");
    String interfaceId = getPara("interface.id");
    String name = getPara("interface.name");
    
    checkNotNull(projectId, "project.id");
    checkNotNull(interfaceId, "interface.id");
    checkNotNull(name, "interface.name");
    
    Interface interfaces = Interface.dao.findById(interfaceId);
    if (interfaces == null) {
      Error(500, "Oop! not exist folder name."); return;
    }
    
    interfaces.setName(name);
    if (interfaces.update())
      OK();
    else
      Error(500, "Oop! create interface fail.");
	}
	
	public void deteleInterface() {
	  Integer interface_id = getParaToInt("interface.id");
    
    checkNotNull(interface_id, "interface.id");
    
    if(Interface.dao.deleteById(interface_id))
      OK();
    else 
      Error(500, "Oop! delete folder fail.");
	}
	
	public void invoke() {
	  Integer interfaceId = getParaToInt("interface.id"); 
	  
	  checkNotNull(interfaceId, "interface.id");
	  
	  Interface interfaces = Interface.dao.findById(interfaceId);
    if (interfaces == null) {
      Error(500, "Oop! interface not exist."); return;
    }
    // http-client 因为接口调度会比较频繁，所以使用conntion-pool来操作
    /**
     * 使用连接池可以根据以往接口的调用频率来决策，因为连接池采取的都是长连接
     */
    String url = interfaces.getRelativeUrl();
    if (StrKit.isBlank(url)) {
      Error(500, "Oop! interface url not exist."); return;
    }
    if (interfaces.get("data") == null) {
      Error(500, "Oop! interface data not exist."); return;
    }
    // 使用baseUrl瓶装真实的接口url
		if ( !url.startsWith("http")) {
    	String baseUrl = Project.dao.findById(interfaces.getWProjectId()).getBaseUrl();
    	if ( StrKit.notBlank(baseUrl) ) {
    		if ( url.startsWith("/"))
					url = baseUrl + url;
    		else
					url = baseUrl + "/" + url;
			}
		}
    String result = HttpKit.post(url, interfaces.get("data").toString());
    LOG.info(String.format("ID[%n]-interface:[%s (%s)] invoke result:[%s]", interfaces.getId(), interfaces.getName(), interfaces.getCode(), result));
    OK(JSON.parse(result));
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
	
	// 检查同一项目，同一层级是否存在相同名字的foler
	private void checkSameFolderName(Object projectId, int level, String folderName) {
	  Long count = Folder.dao.findFirst("select count(*) from w_folder where w_project_id = ? and `level` = ? and `name` = ?", projectId, level, folderName).getLong("count(*)");
	  if (count > 0) 
	    throw new ApiException(String.format("Oop! projectId[%s] folder(level:[%n]) has same name{%s}.", projectId, level, folderName));
	}
	
	private void checkSameInterfaceCode(Object projectId, String code, Object interfaceId) {
	  Long count = Folder.dao.findFirst("select count(1) from w_interface where w_project_id = ? and `code` = ? and id <> ?", projectId, code, interfaceId).getLong("count(1)");
	  if (count > 0) 
	    throw new ApiException(String.format("Oop! projectId[%s] interface has same code{%s}.", projectId, code));
	}
}
