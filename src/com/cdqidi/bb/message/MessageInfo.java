package com.cdqidi.bb.message;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ms_imessage_info",pkName="iMessageID")
public class MessageInfo extends Model<MessageInfo>{
	private static final long serialVersionUID = 1L;
	public final static MessageInfo dao = new MessageInfo();

}
