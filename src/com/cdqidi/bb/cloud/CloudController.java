package com.cdqidi.bb.cloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.util.DateUtil;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ControllerBind(controllerKey = "/cloud")
public class CloudController extends BaseController {

	public void getSchoolList() {
		Map<Object, Object> data = new HashMap<Object, Object>();
		String sql = SqlKit.sql("cloud.getSchoolList");
		if(StringTool.notNull(getPara("key"))){
			sql += " and s.SchoolName like '%"+getPara("key")+"%'";
		}
		List<Record> qr = Db.find(sql);
		int queryState = qr.size() > 0 ? 1 : 0;
		data.put("Data", qr);
		data.put("QueryState", queryState);
		renderJson(data);
	}

	public void getStudentList() {
		String schoolID = getPara("schoolID");
		Map<Object, Object> data = new HashMap<Object, Object>();
		List<Record> qr = Db.find(SqlKit.sql("cloud.getStudentList"), schoolID);
		int queryState = qr.size() > 0 ? 1 : 0;
		data.put("Data", qr);
		data.put("QueryState", queryState);
		renderJson(data);
	}

	public void getTeacherList() {
		String schoolID = getPara("schoolID");
		Map<Object, Object> data = new HashMap<Object, Object>();
		List<Record> qr = Db.find(SqlKit.sql("cloud.getTeacherList"), schoolID);
		int queryState = qr.size() > 0 ? 1 : 0;
		data.put("Data", qr);
		data.put("QueryState", queryState);
		renderJson(data);
	}
	
	@Before(Tx.class)
	public void submitRecordData() {
		
		String records= getPara("records");
		String machineCode = getPara("machineCode");
		Map<Object, Object> data = new HashMap<Object, Object>();
		boolean r = false;
		String tableName = "ns_recordDetail_"+DateUtils.getYear();
		/*
		String mSql = "CREATE TABLE IF NOT EXISTS "+tableName+" ( \n"
		  +"`DetailID` int(11) NOT NULL AUTO_INCREMENT, \n"
		  +"`DataID` varchar(64) DEFAULT NULL, \n"
		  +"`MachineCode` varchar(64) DEFAULT NULL, \n"
		  +"`PersonID` int(11) DEFAULT NULL, \n"
		  +"`UserName` varchar(20) DEFAULT NULL, \n"
		  +"`CardNumber` varchar(16) DEFAULT NULL, \n"
		  +"`RecordTime` varchar(20) DEFAULT NULL, \n"
		  +"`ExecTime` varchar(20) DEFAULT NULL, \n"
		  +"PRIMARY KEY (`DetailID`) \n"
		  +")";
		if(Db.update(mSql)>0)
			r = true;
		*/
		if(Db.query("select MachineCode from ns_machineInfo where MachineCode=?",machineCode).size()>0){
			JSONArray jsonArray =JSONArray.fromObject(records);
			for(int i=0;i<jsonArray.size();i++){ 
				Map o=(Map)jsonArray.get(i);
				Record record = new Record().set("MachineCode", machineCode).set("DataID", o.get("DataID")).set("PersonID", o.get("PersonID")).set("UserName", o.get("UserName"))
						.set("CardNumber", o.get("CardNumber")).set("RecordTime", o.get("RecordTime"));
				r=Db.save(tableName, record);
			}
	        if (r) {
	        	data.put("ExecuteState", 1);
	        	data.put("Msg", "数据提交成功！");
	        }else {
	        	data.put("ExecuteState", 0);
	        	data.put("Msg", "数据提交失败！");
	        }
		}else{
			data.put("ExecuteState", 0);
			data.put("Msg", "考勤机校验失败！");
		}
        renderJson(data);	
	}

	@Override
	public void index() {
		// TODO Auto-generated method stub
	}

}
