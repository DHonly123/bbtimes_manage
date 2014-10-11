package com.cdqidi.bb.report;

import java.util.List;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "/report")
public class ReportController extends BaseController {

	@Override
	public void index() {

	}
	
	public void mBusinessSummary() {
		String Date=getPara("date");
		List<Record> rs = null;
		
		String sql = "select s.schoolID,s.schoolName label,count(m.iMessageID) value from ms_iMessage_info m \n"
				+ "left join be_users u on u.userID=m.iAuthorID \n"
				+ "left join be_schoolinfo s on s.SchoolID=u.SchoolID \n";
		if(StringTool.notNull(Date) && StringTool.notBlank(Date)){
			sql += "where m.iDataType=3 and SUBSTR(m.iDateCreated,1,10)=? \n"
					+ "group by s.schoolID,s.schoolName";
			rs = Db.find(sql,Date);
		}else{
			sql += "group by s.schoolID,s.schoolName";
			rs = Db.find(sql);
		}
		
		renderJson(rs);
	}
	
	public void businessSummary() {
		String Date=getPara("date");
		List<Record> rs = null;
		
		String sql = "select s.schoolID,s.schoolName label,count(c.infoStreamID) value from ns_infostream_content c \n"
				+ "left join be_schoolinfo s on s.SchoolID=c.SchoolID \n";
		if(StringTool.notNull(Date) && StringTool.notBlank(Date)){
			sql += "where  SUBSTR(c.DateCreated,1,10)=? \n"
					+ "group by s.schoolID,s.schoolName";
			rs = Db.find(sql,Date);
		}else{
			sql += "group by s.schoolID,s.schoolName";
			rs = Db.find(sql);
		}
		
		renderJson(rs);
	}

	public void schoolTrend() {
		String sql = "select s.schoolID ,count(c.infoStreamID) value ,extract(YEAR_MONTH from c.DateCreated) label from ns_infostream_content c \n"
				+ "inner join be_schoolinfo s on c.SchoolID=s.SchoolID  WHERE s.SchoolID=? \n"
				+ "group by s.schoolID ,extract(YEAR_MONTH from c.DateCreated)";
		List<Record> res = Db.find(sql, getPara("schoolID"));
		renderJson(res);
	}

	public void classSummary() {
		String sql = "select g.ID classID ,g.groupName label ,count(c.infoStreamID) value from ns_infostream_content c \n"
				+ "inner join ms_group g on c.groupID=g.ID WHERE g.schoolID=? \n"
				+ "group by g.ID,g.groupName";
		List<Record> rs = Db.find(sql, getPara("schoolID"));
		renderJson(rs);
	}

	public void classTrend() {
		String sql = "select g.ID classID ,count(c.infoStreamID) value ,extract(YEAR_MONTH from c.DateCreated) label from ns_infostream_content c \n"
				+ "inner join ms_group g on c.groupID=g.ID WHERE g.ID=? \n"
				+ "group by g.ID ,extract(YEAR_MONTH from c.DateCreated)";
		List<Record> res = Db.find(sql, getPara("classID"));
		renderJson(res);
	}
	
	public void userTrend() {
		String sql = "select c.userID ,count(c.infoStreamID) value ,extract(YEAR_MONTH from c.DateCreated) label from ns_infostream_content c \n"
				+ "WHERE c.userID=? \n"
				+ "group by c.userID ,extract(YEAR_MONTH from c.DateCreated)";
		List<Record> res = Db.find(sql, getPara("userID"));
		renderJson(res);
	}
	
	public void past30() {
		String schoolID = getPara("schoolID");
		String sql ="";
		List<Record> res;
		
		if (schoolID != null && schoolID !=""){
			sql ="select count(c.infoStreamID) value ,SUBSTR(c.DateCreated,1,10) label \n"
					+"from ns_infostream_content c \n"
					+"WHERE c.schoolID=? and c.DateCreated > (SELECT DATE_SUB(Now(), INTERVAL 31 DAY)) \n"
					+"group by SUBSTR(c.DateCreated,1,10)";
			res = Db.find(sql,schoolID);
		}
		else {
			sql ="select count(c.infoStreamID) value ,SUBSTR(c.DateCreated,1,10) label \n"
					+"from ns_infostream_content c \n"
					+"WHERE c.DateCreated > (SELECT DATE_SUB(Now(), INTERVAL 31 DAY)) \n"
					+"group by SUBSTR(c.DateCreated,1,10)";
			res = Db.find(sql);
		}
		renderJson(res);
		
	}
	
	public void mPast30() {
		String schoolID = getPara("schoolID");
		String sql ="";
		List<Record> res;
		
		if (schoolID != null && schoolID !=""){
			sql ="select count(m.iMessageID) value ,SUBSTR(m.iDateCreated,1,10) label \n"
					+"from ms_iMessage_info m \n"
					+"left join be_users u on u.userID=m.iAuthorID \n"
					+"WHERE m.iDataType=3 and u.schoolID=? and m.iDateCreated > (SELECT DATE_SUB(Now(), INTERVAL 31 DAY)) \n"
					+"group by SUBSTR(m.iDateCreated,1,10)";
			res = Db.find(sql,schoolID);
		}
		else {
			sql ="select count(m.iMessageID) value ,SUBSTR(m.iDateCreated,1,10) label \n"
					+"from ms_iMessage_info m \n"
					+"WHERE m.iDataType=3 and m.iDateCreated > (SELECT DATE_SUB(Now(), INTERVAL 31 DAY)) \n"
					+"group by SUBSTR(m.iDateCreated,1,10)";
			res = Db.find(sql);
		}
		renderJson(res);
		
	}
	
	public void userOPTrend() {
		String schoolID = getPara("schoolID");
		String sql ="select count(t.ID) value,t.OP_DATE label from \n"
				+"(select r.ID,r.OP_UID,r.C_Type,r.C_Version,r.OP_DATE,r.OP_TYPE \n"
				+"from ms_callrecord r \n"
				+"left join ms_group_con gc on r.OP_UID=gc.UID \n"
				+"left join ms_group g on gc.GROUP_ID=g.ID \n"
				+"where g.SCHOOLID=?) t \n"
				+"where t.OP_DATE > (SELECT DATE_SUB(Now(), INTERVAL 31 DAY)) \n"
				+"group by t.OP_DATE";
		List<Record> res = Db.find(sql,schoolID);
		renderJson(res);
	}

	public void class_line() {
		String sql = "select ep.ClassID,s.GROUPNAME unit,count(c.infoStreamID) value ,extract(YEAR_MONTH from c.DateCreated) dataTime from ns_infostream_content c \n"
				+ "inner join be_extprofiles ep on ep.UserID=c.userID inner join ms_group s on ep.ClassID=s.ID  WHERE s.ID=? \n"
				+ "group by ep.ClassID,s.GROUPNAME,extract(YEAR_MONTH from c.DateCreated)";
		List<Record> res = Db.find(sql, getPara("classID"));
		renderJson(res);
	}

	public void user_bar() {
		String sql = "select ep.ClassID,s.GROUPNAME unit,count(c.infoStreamID) value ,extract(YEAR_MONTH from c.DateCreated) dataTime from ns_infostream_content c \n"
				+ "inner join be_extprofiles ep on ep.UserID=c.userID inner join ms_group s on ep.ClassID=s.ID  WHERE c.userID=? \n"
				+ "group by ep.ClassID,s.GROUPNAME,extract(YEAR_MONTH from c.DateCreated)";
		List<Record> res = Db.find(sql, getPara("userID"));
		renderJson(res);
	}

}
