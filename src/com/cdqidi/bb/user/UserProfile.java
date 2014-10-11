package com.cdqidi.bb.user;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName = "be_profiles",pkName="ProfileID")
public class UserProfile extends Model<UserProfile> {
	private static final long serialVersionUID = 1L;
	public final static UserProfile dao = new UserProfile();
}
