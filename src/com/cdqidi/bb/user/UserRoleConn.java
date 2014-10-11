package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="be_userroles",pkName="UserRoleID")
public class UserRoleConn extends Model<UserRoleConn>{

	private static final long serialVersionUID = 1L;
	
	public final static UserRoleConn dao = new UserRoleConn();

}
