package com.cdqidi.bb.school;

import java.util.List;
import java.util.UUID;


import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.annotation.RouteBind;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

@RouteBind(path = "/school")
public class SchoolController extends BaseController implements CommOpration {

	@Override
	public void save() {

		String remark = "";
		// 获取模型及其参数
		SchoolInfo school = getModel(SchoolInfo.class, "school");
		// 检测学校编码在系统中是否存在
		SchoolInfo isnull = SchoolInfo.dao.findFirst(
				"SELECT SchoolName from be_schoolinfo WHERE SchoolName=?",
				new Object[] { school.get("SchoolName") });
		if (isnull == null) {
			// 保存学校信息
			remark = school.set("SchoolID", UUID.randomUUID().toString())
					.set("SchoolCode", StringTool.generateMixString(6)).save() ? "添加学校成功！" : "添加学校失败！";
		} else
			remark = "系统中已存在同名学校，请检查！";
		CacheKit.removeAll("area");
		renderJson(remark);

	}

	@Override
	public void delete() {

	}

	@Override
	public void update() {
		boolean is_success = getModel(SchoolInfo.class, "school").update();
		if (is_success) {
			renderJson("更新成功！");

		} else
			renderJson("更新失败！");
	}

	@Override
	public void search() {
		
	}
	
	public void getSchools() {
		int id = getParaToInt("districtID");
		List<SchoolInfo> schools = SchoolInfo.dao.find(
				SqlKit.sql("school.startQuerySchools") + " " + SqlKit.sql("school.endQuerySchools") +" WHERE (s.DistrictID=? or s.ProvinceID=? or s.CityID=?)"
				, id,id,id);
		renderJson(schools);

	}

	@Override
	public void index() {
		int id =StringTool.notNull(getPara("districtID")) ? getParaToInt("districtID"): 0;
		String searchKey = getPara("key");
		String sqlext = SqlKit.sql("school.endQuerySchools");
		Object[] params=null;
		if(id >0){
			sqlext += " WHERE (s.DistrictID=? or s.ProvinceID=? or s.CityID=?)";
			params=new Object[]{id ,id ,id };
		}else {
			params=new Object[]{};
			sqlext += " WHERE 0=0";
		}
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlext += " and s.SchoolName like '%" + searchKey + "%'";
		}
		Page<Record> schools = Db.paginate(getParaToInt("pageIndex") + 1,
				getParaToInt("pageSize"),
				SqlKit.sql("school.startQuerySchools"), sqlext,params);		
		renderJson(schools);
	}

	/**
	 * @方法名:getSchoolByID
	 * @方法描述：根据学校ID查询学校信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-23 下午2:22:17
	 */
	public void getSchoolByID() {
		String schoolID = getPara("schoolID");
		Record school = Db.findFirst(SqlKit.sql("school.QuerySchoolByID"),
				new Object[] { schoolID });
		setAttr("school", school);
		render("editschool.html");
	}

	/**	 
	 * @方法名:getSchoolBaseInfo
	 * @方法描述：查询学校的基本信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-16 下午1:07:53
	 */
	public void getSchoolBaseInfo(){
		String schoolID = getPara("schoolID");
		if (StringKit.isBlank(schoolID)||schoolID==null) {
			String cookie=getCookie("bbm_usercookie");
			if (cookie!=null) {
				String[] t=cookie.split("&");
				schoolID=t[3];
			}
		}
		Record school = Db.findFirst(SqlKit.sql("school.QuerySchoolByID"),
				new Object[] { schoolID });
		setAttr("school", school);
		render("schoolinfo.html");
	
	}
}
