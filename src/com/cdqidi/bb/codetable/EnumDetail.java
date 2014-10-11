package com.cdqidi.bb.codetable;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
/**
 * @类名字：EnumDetail
 * @类描述：码表详情
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-5-13
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
@TableBind(tableName="sys_enumdetail")
public class EnumDetail extends Model<EnumDetail> {
	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	public static final EnumDetail dao=new EnumDetail();

}
