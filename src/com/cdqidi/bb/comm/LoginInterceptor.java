package com.cdqidi.bb.comm;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

/**
 * @类名字：UserInterceptor
 * @类描述：用户登录拦截器
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-5-7
 * @Copyright 足下 Corporation 2013
 * @版权所有
 * 
 */
public class LoginInterceptor implements Interceptor {
	/**
	 * 绕过拦截器的方法名(注意:包括命名空间)
	 */
	private String[] throughMetods = {"/login","/notice/*","/cloud/*"};

	/**
	 * @方法名:intercept
	 * @方法描述： 检测session中是否有用户信息如果没有则检测cookie中的用信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-7 上午11:00:57
	 */
	public void intercept(ActionInvocation arg0) {
		// TODO Auto-generated method stub
		Controller controller = arg0.getController();
			if (checkCookie(controller)||canVisit(arg0.getActionKey())) {
				arg0.invoke();

			} else
				controller.redirect("/login.html");
	}

	/**
	 * @方法名:checkCookie
	 * @方法描述：查看cookie中是否有登录信息如果有则重新自动 登录把用户信息放回session中反之返回false
	 * @author: Carl.Wu
	 * @return: boolean
	 * @version: 2013-5-7 上午11:25:02
	 */
	public boolean checkCookie(Controller controller) {
		String bbm = controller.getCookie("bbm_usercookie");
		if (bbm == null) {
			return false;
		} 
		return true;
	}

	/**
	 * @方法名:canVisit
	 * @方法描述：验证绕过拦截器方法列表
	 * @author: Carl.Wu
	 * @return: boolean
	 * @version: 2013-5-7 下午12:26:21
	 */
	public boolean canVisit(String actionKey) {
		if (throughMetods != null) {
			for (String name : throughMetods) {
				System.out.println(name);
				if(name.contains("*")){
					if (actionKey.contains(name.replace("*", ""))) {
						return true;
					}
				}else{
					if (actionKey.equals(name)) {
						return true;
					}
				}
			}
			return false;
		} else
			return true;

	}
}
