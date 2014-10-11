package com.cdqidi.bb.notice;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ns_notice",pkName="noticeID")
public class Notice extends Model<Notice>{
	
	private static final long serialVersionUID = 1L;
	public final static Notice dao = new Notice();

}
