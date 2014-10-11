package com.cdqidi.bb.codetable;

import java.util.List;


import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.util.DateUtils;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
@ControllerBind(controllerKey="/kind")
public class KindConroller extends BaseController {

	@Override
	public void index() {
		getCodeMaster();
	}

	// 查询学校类别
	public void schoolType() {
		List<EnumDetail> schoolType = CacheKit.get("kindtype", "schoolType");
		if (schoolType == null) {
			schoolType = getEnumInfo(1, 0);
			CacheKit.put("kindtype", "schoolType", schoolType);
		}
		renderJson(schoolType);

	}

	// 查询区域类别
	public void locationType() {
		List<EnumDetail> locationType = CacheKit
				.get("kindtype", "locationType");
		if (locationType == null) {
			locationType = getEnumInfo(2, 0);
			CacheKit.put("kindtype", "locationType", locationType);
		}
		renderJson(locationType);
	}

	// 查询办学类别
	public void capitalType() {
		List<EnumDetail> capitalType = CacheKit.get("kindtype", "capitalType");
		if (capitalType == null) {
			capitalType = getEnumInfo(3, 0);
			CacheKit.put("kindtype", "capitalType", capitalType);
		}
		renderJson(capitalType);

	}

	// 学段类别
	public void getCodeType() {
		List<EnumDetail> capitalType = getEnumInfo(getParaToInt(0), 0);
		renderJson(capitalType);
	}
/**
 * @方法名:getSexType
 * @方法描述：查询性别类型
 * @author: Carl.Wu
 * @return: void
 * @version: 2013-9-13 下午2:28:50
 */
	public void getSexType(){
		List<EnumDetail> SexType = getEnumInfo(5, 0);
		renderJson(SexType);
	}
	/**
	 * @方法名:getEnumInfo
	 * @方法描述：获取码表值
	 * @author: Carl.Wu
	 * @return: List<EnumDetail>
	 * @version: 2013-5-26 下午4:09:02
	 */
	private List<EnumDetail> getEnumInfo(int masterID, int pid) {
		String sql = "SELECT e.DetailID,e.PID,e.MasterID,e.Caption,e.`Code`,e.intState \n"
				+ "FROM sys_enumdetail AS e WHERE e.MasterID=? AND e.intState=1 AND e.PID=?";
		return EnumDetail.dao.find(sql, new Object[] { masterID, pid });
	}

	/**
	 * @方法名:getCodeMaster
	 * @方法描述：获取码表信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-27 下午12:03:32
	 */
	public void getCodeMaster() {
		Integer pagenum = getParaToInt("pageIndex") + 1;
		Integer pagesize = getParaToInt("pageSize");
		Page<Record> page = Db
				.paginate(
						pagenum,
						pagesize,
						"SELECT MasterID,Caption,`Code`,CreateDate,intState,CASE intState WHEN 1 THEN '可用' ELSE '不可用' END stateDesc",
						"FROM sys_enummaster");

		renderJson(page);
	}

	/**
	 * @方法名:getAllMaster
	 * @方法描述：获取所有码表选项
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-27 下午4:09:06
	 */
	public void getAllMaster() {
		String sql = "SELECT MasterID,Caption,`Code`,CreateDate,intState,CASE intState WHEN 1 THEN '可用' ELSE '不可用' \n"
				+ "END stateDesc FROM sys_enummaster";
		renderJson(Db.find(sql));
	}

	/**
	 * @方法名:addCodeMaster
	 * @方法描述：添加码表主信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-27 下午2:24:20
	 */
	public void addCodeMaster() {
		String code = getPara("code");
		String name = getPara("caption");
		int intstate = 0;
		if (getPara("intstate") != null) {
			intstate = getParaToInt("intstate");
		}
		EnumMaster enumMaster = new EnumMaster();
		enumMaster.set("Caption", name);
		enumMaster.set("Code", code);
		enumMaster.set("intState", intstate);
		enumMaster.set("createDate", DateUtils.nowDateTime());
		boolean b = enumMaster.save();
		if (b) {
			renderJson("添加成功！");
		} else {
			renderJson("添加失败！");
		}
	}

	/**
	 * @方法名:getCodeDetail
	 * @方法描述：获取码表详情
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-27 下午5:27:14
	 */
	public void getCodeDetail() {
		String sql = "SELECT d.DetailID,d.PID,d.MasterID,d.Caption,d.`Code`,d.intState,CASE d.intState \n"
				+ "WHEN 1 THEN '可用' ELSE '不可用' END statedesc ";
		Integer pagenum = getParaToInt("pageIndex") + 1;
		Integer pagesize = getParaToInt("pageSize");
		Integer masterID = getParaToInt("masterid");
		Page<Record> page = Db.paginate(pagenum, pagesize, sql,
				"FROM sys_enumdetail d WHERE d.MasterID=?",
				new Object[] { masterID });
		renderJson(page);
	}

	/**
	 * @方法名:addMasterDetail
	 * @方法描述：添加码表数据值
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-5-28 上午9:28:10
	 */
	public void addMasterDetail() {
		Integer masterID = getParaToInt("masterid");
		String code = getPara("code");
		String name = getPara("caption");
		int intstate = 0;
		if (getPara("intstate") != null) {
			intstate = getParaToInt("intstate");
		}
		EnumDetail detail = new EnumDetail();
		detail.set("pid", 0);
		detail.set("Caption", name);
		detail.set("Code", code);
		detail.set("intState", intstate);
		detail.set("masterID", masterID);
		boolean b = detail.save();
		if (b) {
			renderJson("添加成功！");
		} else {
			renderJson("添加失败！");
		}

	}
}
