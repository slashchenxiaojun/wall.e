package org.hacker.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PluginCommon是为了解决众多插件都需要使用到的公共部分 比如:全局遍历文件，每次启动插件都会遍历一次，如果未来 插件增多，对初始化影响很大
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-11-06
 **/
public class PluginCommon {

	private static PluginCommon instance = new PluginCommon();

	private PluginCommon() {}

	public static PluginCommon getInstance() {
		return instance;
	}

	// 存放了所有的文件key:文件名后缀 value:文件集合
	private Map<String, List<File>> _fileEndName_file = new HashMap<>();

	/**
	 * 利用递归的方法深度遍历文件夹下的所有文件，请使用
	 * <code>FileHelper.CopyListAndClear(List<File> ff)</code>获得最终返回结果
	 * 
	 * @param f
	 *            nothing to say...
	 * @param fileEndName
	 *            文件的后缀名称，如java,class...
	 * 
	 * @return void or 实际上是将数据保持在了List<File> f中
	 * @throws FileNotFoundException
	 *             : your know this Exception...
	 **/
	private void findAllByName(File f, String fileEndName, List<File> files) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException("RoutePlugin>>error:ClassPath未找到");
		}
		File[] tmpfiles = f.listFiles();
		if (tmpfiles == null)
			return;
		for (int i = 0; i < tmpfiles.length; i++) {
			if (tmpfiles[i].isDirectory()) {
				findAllByName(tmpfiles[i], fileEndName, files);
			} else {
				if (tmpfiles[i].getName().endsWith("." + fileEndName))
					files.add(tmpfiles[i]);
			}
		}
	}

	/**
	 * 通过文件后缀名获取相应的集合
	 * 
	 * @param rootPath
	 *            遍历的根文件目录
	 * @param fileEndName
	 *            文件后缀名称如:class, java
	 * 
	 * @return 返回想对应的文件集合
	 * 
	 * @throws FileNotFoundException
	 */
	public List<File> getFileSetByEndName(String rootPath, String fileEndName) throws FileNotFoundException {
		if (_fileEndName_file.containsKey(fileEndName))
			return _fileEndName_file.get(fileEndName);
		// 新建一个fileList
		List<File> files = new ArrayList<>();
		File root = new File(rootPath);
		findAllByName(root, fileEndName, files);
		_fileEndName_file.put(fileEndName, files);
		return files;
	}

	/**
	 * 将文件的路径和CLASSPATH路径做'去交集'操作，得到className（含包名）
	 * 如com.zjhcsoft.mvc.appliaction,然后得到Class对象
	 * 
	 * @param root 根路径
	 * @param Path 文件路径
	 * @return
	 */
	public Class<?> getClass(String root, String Path) {
		String className = Path.substring(root.length() + 1);
		// for 'JBoss AS 7' and other classloader is what i can't understand
		if (className.contains("WEB-INF")) {
			className = className.substring("WEB-INF".length() + 1);
		}
		if (className.contains("classes")) {
			className = className.substring("classes".length() + 1);
		}
		// for windows
		className = className.replace(File.separatorChar, '.');
		className = className.substring(0, className.indexOf(".class"));
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public void clear() {
		_fileEndName_file.clear();
		instance = null;
	}

}
