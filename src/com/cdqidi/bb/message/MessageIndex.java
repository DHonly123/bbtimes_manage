package com.cdqidi.bb.message;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ms_imessage_index",pkName="IndexID")
public class MessageIndex extends Model<MessageIndex>{
	private static final long serialVersionUID = 1L;
	public final static MessageIndex dao = new MessageIndex();

}
