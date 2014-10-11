package com.cdqidi.bb.classes;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="ms_group",pkName="ID")
public class ClassInfo extends Model<ClassInfo>{
	private static final long serialVersionUID = 1L;
	public static final ClassInfo dao=new ClassInfo();

}
