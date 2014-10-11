package com.cdqidi.bb.classes;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="ms_group_con",pkName="ID")
public class ClassCon extends Model<ClassCon> {
		
	private static final long serialVersionUID = 1L;
	public final static ClassCon dao = new ClassCon();

}
