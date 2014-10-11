package com.cdqidi.bb.school;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="be_schoolinfo" ,pkName="SchoolID")
public class SchoolInfo extends Model<SchoolInfo>{
	private static final long serialVersionUID = 1L;
	
	public static final SchoolInfo dao=new SchoolInfo();

}
