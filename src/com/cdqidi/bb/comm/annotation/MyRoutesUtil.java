package com.cdqidi.bb.comm.annotation;

import java.util.List;


import com.jfinal.config.Routes;
import com.jfinal.core.Controller;

/**
 * @类名字：MyRoutesUtil
 * @类描述：工具类 自动绑定Controller
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-9-11
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
public class MyRoutesUtil{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void add(Routes me){
		List<Class> list= ClassSearcher.findClasses();
		if(list!=null&&list.isEmpty()==false){
			for(Class clz:list){
				RouteBind rb=(RouteBind)clz.getAnnotation(RouteBind.class);
				if(rb!=null){
					me.add(rb.path(),clz,rb.viewPath());
				}else if(clz.getSuperclass()!=null){
					if(clz.getSuperclass()==Controller.class||clz.getSuperclass().getSuperclass()==Controller.class){
						me.add("/"+clz.getSimpleName().replace("Controller", "").toLowerCase(),clz);
					}
				}
			}
		}
	}
}
