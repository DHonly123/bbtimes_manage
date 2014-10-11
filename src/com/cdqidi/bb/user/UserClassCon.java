package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ms_group_con",pkName="ID")
public class UserClassCon extends Model<UserClassCon>{
	private static final long serialVersionUID = 1L;
	public final static UserClassCon dao = new UserClassCon();

}
