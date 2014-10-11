package com.cdqidi.bb.card;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName="ns_record",pkName="RecordID")
public class CardRecord extends Model<CardRecord>{
	private static final long serialVersionUID = 1L;
	public final static CardRecord dao = new CardRecord();
	
}
