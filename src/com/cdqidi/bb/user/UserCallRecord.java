package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ms_callRecord",pkName="ID")
public class UserCallRecord extends Model<UserCallRecord>{
	private static final long serialVersionUID = 1L;
	public final static UserCallRecord dao = new UserCallRecord();

}