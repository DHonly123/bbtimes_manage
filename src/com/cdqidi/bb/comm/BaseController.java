package com.cdqidi.bb.comm;

import java.util.HashMap;
import java.util.Map;



import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

/**
 * @类名字：BaseController
 * @类描述：
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-9-11
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
public abstract class BaseController extends Controller {
	protected int pageSize=20;
	public abstract void index();
	@Override
	public void render(String view) {
		super.render(view);
	}
	/**
	 * @方法名:getCurrentUser
	 * @方法描述：获取当前系统操作人
	 * @author: Carl.Wu
	 * @return: Record
	 * @version: 2013-9-11 上午11:33:49
	 */
	public Record getCurrentUser(){
		String user_token=getCookie("bbm_usercookie");
		System.out.println("当前操作人："+user_token);
		//return (Record)MemcacheTool.mcc.get(user_token);
		return null;
	}
	
	public Object[] getCookieContext(){
		String userCookie=getCookie("bbm_usercookie");
		if (StringTool.notBlank(userCookie)&&StringTool.notNull(userCookie)) {
			return userCookie.split("&");
		}
		return null;
	}
	
	/**
	 * @方法名:toDwzJson
	 * @方法描述：转换dwz json格式输出
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-11 上午11:34:05
	 */
	public void toDwzJson(Integer statusCode,String message,String... navTabId){
		Map<String,Object> jsonMap=new HashMap<String,Object>();
		jsonMap.put("statusCode", statusCode);
		if(message!=null)
		jsonMap.put("message",message);
		if(navTabId!=null)
		jsonMap.put("navTabId", navTabId);
		this.renderJson(jsonMap);
	}
	public void toDwzJson(Integer statusCode,String message,Long id){
		Map<String,Object> jsonMap=new HashMap<String,Object>();
		jsonMap.put("statusCode", statusCode);
		if(message!=null)
		jsonMap.put("message",message);
		if(id!=null)
		jsonMap.put("idVal",id);
		this.renderJson(jsonMap);
	}
}