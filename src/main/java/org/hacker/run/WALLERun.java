package org.hacker.run;


import com.jfinal.core.JFinal;

/**
 * XXX is your app name, choose if you want
 * */
public class WALLERun {
	
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 9090, "/walle", 1);
	}
	
}
