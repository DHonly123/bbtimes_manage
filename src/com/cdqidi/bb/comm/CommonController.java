package com.cdqidi.bb.comm;

import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.net.*;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.Restful;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * @类名字：CommonController
 * @类描述：
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-11-14
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
@ControllerBind(controllerKey = "/")
// @Before({ Restful.class, SessionInViewInterceptor.class })
public class CommonController extends BaseController {

	public void index() {
		render("/login.html");
	}


	/**
	 * @方法名:login
	 * @方法描述：用户登录
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-11-14 下午2:47:38
	 * @throws UnsupportedEncodingException 
	 */
	public void login() throws UnsupportedEncodingException {
		String userName = getPara("username");
		String userPass = getPara("pwd");
		if (userName != null && userPass != null && !userName.isEmpty()
				&& !userPass.isEmpty()) {
			Record user = Db.findFirst(SqlKit.sql("user.login"), new Object[] {
					userName, userPass });
			if (user != null) {
				setAttr("user_info", user);
				String cookiev = new StringBuilder(user.getStr("UserID")).append("&")
						.append(userName).append("&")
						.append(userPass).append("&")
						.append(user.getStr("SchoolID")).append("&")
						.append(user.getInt("RoleType")).append("&")
						.append(URLEncoder.encode(user.getStr("DisplayName"),"UTF-8")).toString();
				this.setCookie("bbm_usercookie", cookiev, 86400000);
				redirect("/index.html");
			} else {
				setAttr("msg", "用户名或密码错误!");
				render("/login.html");
			}

		}
	}
	
	public void logout() {
		this.removeCookie("bbm_usercookie");
		render("/login.html");
	}

	private Map<String, Object> toMap(Record record) {
		return record.getColumns();
	}

}
