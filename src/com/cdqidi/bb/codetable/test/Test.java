package com.cdqidi.bb.codetable.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import com.cdqidi.bb.user.UserController;


public class Test {
	   public static String TEAGFRIEND_SQL="select t.UserID,t.UserName,t.DisplayName  from view_userInfo t LEFT JOIN be_extprofiles e ON e.UserID=t.UserID " +
               "where e.classID=?   and t.roleType=2 " +
               "AND t.UserID NOT IN(?)";
	   public static String BASE_GSQL = "SELECT '-1','好友',count(f.fUserID) GFCount,0 groupType FROM be_friends f WHERE f.userID=? "
				+ "AND f.groupID='-1' AND f.status=1  "
				+ "UNION  SELECT '-1' groupID,'待验证好友' groupName,count(f.fUserID) GFCount,'-1' groupType FROM be_friends f WHERE f.userID=? AND f.groupID='-1' AND f.status=0 " 
				+ "UNION SELECT t.groupID,t.groupName,count(f.fUserID) GFCount,0 groupType "
				+ "FROM be_groups t LEFT JOIN  be_friends f ON f.groupID=t.groupID "
				+ "AND t.userID=f.UserID WHERE t.userID=?  GROUP BY t.groupID,t.groupName ";
	   public  static String ROOTPATH =new File(UserController.class.getClassLoader().getResource("").getPath()).getParentFile().getParent();
	public static void main(String[] args) {
		String r="458.4";
	float f = Float.parseFloat(r);
	System.err.println(BASE_GSQL);
	
	//	Long xx=new Long(r);
		
		//System.out.println(TEAGFRIEND_SQL);
		//System.out.println(UUID.randomUUID().toString());
	/*	Workbook wb;
		try {
			wb = Workbook.getWorkbook(new File("E:/xf.xls"));
			Sheet sheet = wb.getSheet(0);
			for (int i = 0; i < sheet.getRows(); i++) {
			   Cell[] cell = sheet.getRow(i);
			   for (int j = 0; j < cell.length; j++) {
				System.out.println("cell["+j+"]:" + cell[j].getContents());
			   }
			}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
	}

}
