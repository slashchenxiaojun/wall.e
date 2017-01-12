package org.hacker.core;

import java.io.File;

import com.jfinal.kit.StrKit;
import com.jfinal.render.FileRender;

/**
 * 用于临时文件的下载(下载后马上删除)
 * 
 * @author Mr.J
 *
 */
public class TempFileRender extends FileRender {
  private String fileName;
  private File file;

  public TempFileRender(String fileName) {
    super(fileName);
  }

  public TempFileRender(File file) {
    super(file);
    this.file = file;
  }

  @Override
  public void render() {
    try {
      super.render();
    } finally {
      if(StrKit.isBlank(fileName) && file == null) {
        file = new File(fileName);
      }
      if(file != null && file.exists() && !file.isDirectory()) {
        file.delete();
      }
    }
  }
}
