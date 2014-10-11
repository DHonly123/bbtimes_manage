package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="be_users",pkName="UserID")
public class UserAccount extends Model<UserAccount> {
	private static final long serialVersionUID = 1L;
	public final static UserAccount dao = new UserAccount();

}
