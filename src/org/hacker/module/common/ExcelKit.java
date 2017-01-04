package org.hacker.module.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class ExcelKit {
	// 定制日期格式
	private static String DATE_FORMAT = "yyyy-mm-dd h:mm"; // "m/d/yy h:mm"

	// 定制浮点数格式
	private static String NUMBER_FORMAT = "#,##0.00";
	
	@Deprecated
	public static void read(File file, String[] argumentNames, String[] argumentTypes, int headerNum, int sheetIndex) {
	  Assert.checkNotNull(file, "file");
	  String filename = file.getName();
	  try {
      FileInputStream fileIn = new FileInputStream(file);
      Workbook wb = null;
      Sheet sheet = null;
      if(filename.toLowerCase().endsWith("xls")) {
        wb = new HSSFWorkbook(fileIn);
      } else if(filename.toLowerCase().endsWith("xls")) {
//        wb = new XSSFWorkbook(fileIn);
      }
      if(wb.getNumberOfSheets() < sheetIndex) {
        throw new RuntimeException("Oop~ " + filename + " is no sheet.");
      }
      sheet = wb.getSheetAt(sheetIndex);
      
      for(int i = headerNum + 1; i < sheet.getLastRowNum() + headerNum; i++) {
        Row row = sheet.getRow(i);
        int colums = 0;
        for(String argumentName : argumentNames) {
          Cell cell = row.getCell(colums);
          if(cell != null) {
            System.out.println(argumentName);
          }
          colums++;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	 
	public static void read(String filename) {
	  Assert.checkNotNull(filename, "file name");

	}
	
	@SuppressWarnings("resource")
	/**
	 * 导出Excel
	 * 
	 * @param filename
	 * @param list 实际数据
	 * @param argumentNames 参数名称
	 * @param argumentTypes 参数类型
	 * @param title 标题
	 * @param customDateFormat 自定义的日期格式 key: argumentName, value: format
	 */
	public static void writer(String filename, List<Record> list, String[] argumentNames, String[] argumentTypes, String[] title, Map<String, String> customDateFormat) {
		FileOutputStream fileOut = null;
		try {
			if(
					list == null || StrKit.isBlank(filename) || 
					title == null || title.length == 0 ||
					argumentNames == null || argumentNames.length == 0 ||
					argumentTypes == null || argumentTypes.length == 0 ||
					argumentTypes.length != argumentNames.length ||
					argumentNames.length != title.length
			) throw new IllegalArgumentException();
			// create a new workbook
			Workbook wb = new HSSFWorkbook();
			wb.createCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));    
			// create a new sheet
			Sheet s = wb.createSheet();
			// declare a row object reference
			Row r = null;
			// declare a cell object reference
			Cell c = null;
			// create title
			r = s.createRow(0);
			for(int i = 0; i < title.length; i++) {
				c = r.createCell(i);
				c.setCellValue(title[i]);
			}
			for(int i = 0; i < list.size(); i++) {
				Record o = list.get(i);
				r = s.createRow(i + 1);
				for(int j = 0; j < title.length; j++) {
					c = r.createCell(j);
					if(argumentTypes[j].contains("String"))
						c.setCellValue(o.getStr(argumentNames[j]));
					else if(argumentTypes[j].contains("Date") || argumentTypes[j].contains("Timestamp")) {
						 CellStyle cellStyle = wb.createCellStyle();
						 String format = "";
						 if(customDateFormat != null) {
							 format = customDateFormat.get(argumentNames[j]);
							 if(StrKit.isBlank(format)) {
								 format = DATE_FORMAT;
							 }
						 }
						 cellStyle.setDataFormat(wb.createDataFormat().getFormat(format));
						 c.setCellStyle(cellStyle);
						 c.setCellValue(o.getDate(argumentNames[j]));
					} else if(argumentTypes[j].contains("BigDecimal")) {
						c.setCellType(Cell.CELL_TYPE_NUMERIC);
						CellStyle cellStyle = wb.createCellStyle(); // 建立新的cell样式  
						DataFormat format = wb.createDataFormat();
						cellStyle.setDataFormat(format.getFormat(NUMBER_FORMAT)); // 设置cell样式为定制的浮点数格式  
						c.setCellStyle(cellStyle); // 设置该cell浮点数的显示格式  
						Object obj = o.get(argumentNames[j], 0);
            c.setCellValue(Double.parseDouble(obj.toString()));
					} else if(argumentTypes[j].contains("Integer")) {
					  c.setCellType(Cell.CELL_TYPE_NUMERIC);
            CellStyle cellStyle = wb.createCellStyle(); // 建立新的cell样式  
            DataFormat format = wb.createDataFormat();
            cellStyle.setDataFormat(format.getFormat("0")); // 设置cell样式为定制Integer
            c.setCellStyle(cellStyle); // 设置该cell浮点数的显示格式 
            Object obj = o.get(argumentNames[j], 0);
            c.setCellValue(Double.parseDouble(obj.toString()));
					}
					else if(argumentTypes[j].contains("Boolean"))
						c.setCellValue(o.getBoolean(argumentNames[j]));
					else {
						// 如果没有符合类型，默认使用String
					  Object obj = o.get(argumentNames[j]);
						c.setCellValue(obj != null ? obj.toString() : "");
					}
				}
			}
		    // Write the output to a file
		    fileOut = new FileOutputStream(filename);
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(fileOut != null)
					fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
