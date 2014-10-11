package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="be_extprofiles",pkName="extProfileID")
public class UserExtProfile extends Model<UserExtProfile> {

	private static final long serialVersionUID = 1L;
	public final static UserExtProfile dao = new UserExtProfile();
}
