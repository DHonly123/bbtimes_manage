package com.cdqidi.bb.codetable;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
/**
 * @类名字：EnumMaster
 * @类描述：码表主表
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-5-13
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
@TableBind(tableName="sys_enummaster")

public class EnumMaster extends Model<EnumMaster> {
	private static final long serialVersionUID = 1L;
	public static final EnumMaster dao=new EnumMaster();
	
	

}
