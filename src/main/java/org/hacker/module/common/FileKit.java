package org.hacker.module.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A File helper tools
 * 
 * @author Mr.J.
 * 
 * @since 2014-12-05
 **/
public class FileKit {

	private static List<File> files = new ArrayList<File>();

	/**
	 * 利用递归的方法深度遍历文件夹下的所有文件，请使用
	 * <code>FileHelper.CopyListAndClear(List<File> ff)</code>获得最终返回结果
	 * 
	 * @param f
	 *            nothing to say...
	 * @param fileEndName
	 *            文件的后缀名称，如java,class...
	 * 
	 * @return void or 实际上是将数据保持在了List<File> files中，必须使用
	 *         <code>FileHelper.CopyListAndClear(List<File> ff)</code>获得List
	 * @throws FileNotFoundException
	 *             : your know this Exception...
	 **/
	public static void findAllByName(File f, String fileEndName) throws FileNotFoundException {
		validateFile(f);
		File[] tmpfiles = f.listFiles();
		if (tmpfiles == null)
			return;
		for (int i = 0; i < tmpfiles.length; i++) {
			if (tmpfiles[i].isDirectory()) {
				findAllByName(tmpfiles[i], fileEndName);
			} else {
				if (tmpfiles[i].getName().endsWith("." + fileEndName))
					files.add(tmpfiles[i]);
			}
		}
	}

	/**
	 * 将内置的list对象copy到ff中去，是FileHelper.findAllByName的辅助方法
	 * 
	 * @param ff
	 *            你需要复制的List
	 * 
	 * @return true 则复制成功被清除内置的list对象，否则是false
	 **/
	public static boolean CopyListAndClear(List<File> ff) {
		boolean isSuccess = ff.addAll(files);
		// 如果Copy成功clear所以记录，方便其他类使用（这个Helper是线程不安全的，如果有多个线程建议使用synchronized）
		if (isSuccess) {
			files.clear();
		}
		return isSuccess;
	}

	/**
	 * 根据uri创建，文件/文件夹
	 * 
	 * @param uri
	 *            文件/文件夹 路径
	 * 
	 * @return file 引用，如果为null则表示创建失败
	 */
	public static File createFile(String uri) {
		File file = new File(uri);
		if(file.exists()){
			return file;
		}
		if(uri.endsWith(File.separator)){
			return null;
		}
		//判断目标文件所在的目录是否存在
		if(!file.getParentFile().exists()){
			//如果目标文件所在的目录不存在，则创建父目录
			if(!file.getParentFile().mkdirs()){
				return null;
			}
		}
		//创建目标文件
		try {
			if(file.createNewFile()){
				return file;
			}else{
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将字符串写入指定文件
	 * 
	 * @param str
	 * @param f
	 * @throws FileNotFoundException
	 */
	public static void write(String str[], File f) throws FileNotFoundException {
		validateFile(f);
		if (f.canWrite()) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			try {
				for (String s : str) {
					bw.write(s);
				}
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串写入指定文件
	 * 
	 * @param str
	 * @param f
	 * @throws FileNotFoundException
	 */
	public static void write(String str, File f) throws FileNotFoundException {
		write(new String[] { str }, f);
	}
	
	public static String read(File f) throws FileNotFoundException{
		validateFile(f);
		if(f.canRead()){
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			StringBuffer sb = new StringBuffer();
			try {
				while(true){
					String str = br.readLine();
					if(str == null) break;
					sb.append(str).append(System.getProperty("line.separator"));
				}
			} catch (Exception e) {
			  e.printStackTrace();
			}finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 验证File参数的正确性
	 * 
	 * @param f
	 * @throws FileNotFoundException
	 */
	private static boolean validateFile(File f) throws FileNotFoundException {
		if (!f.exists() && f.isDirectory()) {
			throw new FileNotFoundException("Oop~ Please check if your File exists.");
		} else {
		  return true;
		}
	}
}
