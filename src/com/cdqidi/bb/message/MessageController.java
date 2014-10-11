package com.cdqidi.bb.message;

import java.util.List;
import java.util.UUID;
import java.io.UnsupportedEncodingException;
import java.net.*;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.StringTool;
import com.cdqidi.bb.user.UserProfile;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.aop.Before;

@ControllerBind(controllerKey = "/message")
public class MessageController extends BaseController implements CommOpration {
	
	@Override
	public void save() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void search() {

	}
	
	@Override
	public void index() {
		String searchKey = getPara("key");
		String sqlSelect = "select iMessageID,iContent,iDateCreated";
		String sqlFrom = "from ms_iMessage_info c where c.iDataType=3 and c.iAuthorID =? \n"
				+ "order by iDateCreated DESC";
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlFrom += " and c.iContent like '%" + searchKey + "%'";
		}
		Page<Record> messages = Db.paginate(getParaToInt("pageIndex") + 1,
				getParaToInt("pageSize"),
				sqlSelect, sqlFrom, getPara("userID"));		
		
		renderJson(messages);
	}
	
	@Override
	public void delete() {
		
	}
	
	public void getMessageInfo() {
		String messageID = getPara("messageID");
		MessageInfo msg = new MessageInfo().findById(messageID);
		if (msg != null)
		{
			setAttr("message", msg);
			render("../message/userDetail.html");
		}

	}
	
	public void getUserDetail() {
		String searchKey = getPara("key");
		String sqlSelect = "select indexID,reciveUserID,reciveName,reciveTime,sendState,sendDate,reciveState";
		String sqlFrom = "from ms_iMessage_index i where i.iMessageID =?";
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlFrom += " and i.reciveName like '%" + searchKey + "%'";
		}
		Page<Record> userDetail = Db.paginate(getParaToInt("pageIndex") + 1,
				getParaToInt("pageSize"),
				sqlSelect, sqlFrom, getPara("messageID"));				
		renderJson(userDetail);
	}
	
	@Before(Tx.class)
	public void sendMessage() throws UnsupportedEncodingException {
		
        String content = getPara("content");
        String userID = getPara("userID");
        String userName = URLDecoder.decode(getPara("userName"),"UTF-8");
        int blnIsSMS = getParaToInt("blnIsSMS");
        int blnIsPrefix = getParaToInt("blnIsPrefix");
		String receivers="";
        int result = 0;
        
        MessageInfo info = new MessageInfo();
        if(info.set("iDataId", 0).set("iDataType",3).set("iContent",content).set("iAuthorID",userID).set("iDateCreated",DateUtils.nowDateTime()).set("iSource","WEB").save())
        {	
        	String[] receiverArray=getPara("receivers").split(",");
			for (int i = 0; i < receiverArray.length; i++) {
				if (i==receiverArray.length-1) {
					receivers+="'"+receiverArray[i]+"'";
				}else
					receivers+="'"+receiverArray[i]+"',";
			}
        	String sql ="insert into ms_imessage_index(iMessageID,AuthorID,AuthorName,ReciveUserID,ReciveName,ReciveState,SendState) \n"
        				+"select ?,?,?,p.userID ReciveUserID,p.displayName ReciveName,0 ReciveState,0 SendState \n"
        				+"from be_profiles p \n"
        				+"where p.userID in ("+ receivers +")";
        	result=Db.update(sql,info.getInt("iMessageID"), userID, userName);
        }
        renderText(Integer.toString(result));        
	}
	
}
