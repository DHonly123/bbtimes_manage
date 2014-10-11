package com.cdqidi.bb.codetable.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XlsReader {
private static Map<Object, Object> map=null;
private static List<Map<Object, Object>> list=null;
public static String errorMsg=null;

  public static  List<Map<Object, Object>> readXls(File file) throws IOException{
	list=new ArrayList<Map<Object,Object>>();
    InputStream is = new FileInputStream(file);
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook( is); 
    // 循环工作表Sheet
    for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){
      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt( numSheet);
      if(hssfSheet == null){
        continue;
      }
      // 循环行Row 
      for(int rowNum =0; rowNum <= hssfSheet.getLastRowNum(); rowNum++){
    	  map=  new HashMap<Object, Object>();
        HSSFRow hssfRow = hssfSheet.getRow( rowNum);
        if(hssfRow == null){
          continue;
        }
        // 循环列Cell  
        for(int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++){
          HSSFCell hssfCell = hssfRow.getCell( cellNum);
          if(hssfCell == null){
            continue;
          }
        System.err.println(rowNum+"|"+cellNum+"|"+getValue( hssfCell));
       /* if (rowNum==0&&cellNum==0) {
			
		}*/
          map.put("key"+cellNum, getValue( hssfCell));
        }
        list.add(map);
      }
    }
    return list;
  }
  
  private static String getValue(HSSFCell hssfCell){
	  String value="";
	  switch (hssfCell.getCellType()) {
	case HSSFCell.CELL_TYPE_NUMERIC://数字
		DecimalFormat df = new DecimalFormat("#");
		value= String.valueOf(df.format(hssfCell.getNumericCellValue()));
		break;
	case HSSFCell.CELL_TYPE_BLANK:// 空值 
		 value= String.valueOf("");
		break;
	case HSSFCell.CELL_TYPE_BOOLEAN://数字
		 value= String.valueOf( hssfCell.getBooleanCellValue());
		break;
	case HSSFCell.CELL_TYPE_ERROR:// 故障
		 value= String.valueOf( hssfCell.getErrorCellValue());
		break;
	case HSSFCell.CELL_TYPE_FORMULA:// 公式 
		 value= String.valueOf( hssfCell.getCellFormula());
		break;
	case HSSFCell.CELL_TYPE_STRING://字符串
		 value= String.valueOf(hssfCell.getStringCellValue());
		break;

	default:
		 value= String.valueOf( hssfCell.getStringCellValue());
		break;
	}
	return value;  
  }
  
}

