package com.cdqidi.bb.user;

import java.io.Serializable;

public class CallBackMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String phone;
	private String msgtype;
	private String name;
	private String msg;
	private int _id;
	private int _uid;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int get_uid() {
		return _uid;
	}
	public void set_uid(int _uid) {
		this._uid = _uid;
	}
	

}
