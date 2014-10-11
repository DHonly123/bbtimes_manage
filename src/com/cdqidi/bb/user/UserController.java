package com.cdqidi.bb.user;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import com.cdqidi.bb.classes.ClassCon;
import com.cdqidi.bb.classes.ClassInfo;
import com.cdqidi.bb.codetable.test.XlsReader;
import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.ImageCutterUtil;
import com.cdqidi.bb.comm.util.JsonBinder;
import com.cdqidi.bb.comm.util.MysqlDBPro;
import com.cdqidi.bb.comm.util.PropertiesFactoryHelper;
import com.cdqidi.bb.comm.util.StringTool;
import com.cdqidi.bb.comm.util.TestImg;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

@ControllerBind(controllerKey = "/user")
public class UserController extends BaseController implements CommOpration {
	private static JsonBinder binder = JsonBinder.buildNonDefaultBinder();
	private static String ROOTPATH =new File(UserController.class.getClassLoader().getResource("").getPath()).getParentFile().getParent();

	@Override
	public void index() {

	}

	@Before(Tx.class)
	@Override
	public void save() {
		String userID = UUID.randomUUID().toString();
		UserProfile userProfile = getModel(UserProfile.class, "buser")
				.set("UserID", userID)
				.set("niCheng", getPara("buser.DisplayName"))
				.set("isGenerate", 0)
				.set("identifyNmuber", getPara("buser.PhoneMobile").trim());
		UserProfile p = checkPhone(userProfile.getStr("PhoneMobile"),
				userProfile.getStr("DisplayName"), 1);
		if (p != null) {
			renderJson("msg", 0);
		} else {
			userProfile.save();
					
			// =============设置extProfile表的学校、班级关联信息
			String schoolID = getPara("schoolID");
			String classID = getPara("classID");
			if (schoolID != null && classID != null) {
				new UserExtProfile().set("UserID", userID)
						.set("SchoolID", schoolID).set("ClassID", classID)
						.save();
			}
			renderJson("msg", 1);
		}

	}

	@Override
	public void delete() {
		
		String userID = getPara("userID");
		UserProfile user = UserProfile.dao.findFirst(
				"select * from be_profiles where UserID=?", userID);
		int isGenerate = user.getInt("isGenerate");
		if (isGenerate == 0) {
			//String sql = "DELETE FROM be_extProfiles WHERE userID = '" + userID +"'";
			Db.update(SqlKit.sql("user.deleteExtProfileByUserID"),userID);
			
			//sql = "DELETE FROM be_profiles WHERE userID = '" + userID +"'";
			int r = Db.update(SqlKit.sql("user.deleteProfileByUserID"),userID);
			
			//boolean r = new UserProfile().deleteById(userID); //必须是表的主键

			if (r > -1) {
				renderJson("msg", "档案删除成功!");
			} else {
				renderJson("msg", "档案删除失败!");
			}
		}else {
			//判断生成的用户是否有业务数据，如有业务数据则不能删除，否则可关联删除用户数据和档案数据。
			renderJson("msg","此档案已生成用户，无法删除!");
		}

	}

	@Before(Tx.class)
	@Override
	public void update() {
		UserProfile user = getModel(UserProfile.class, "buser");
		user.set("PhoneMobile", user.getStr("PhoneMobile").trim());
		if (user != null) {
			boolean isChanged = true;
			user.set("niCheng", user.getStr("DisplayName"));
			if (isChangedSN(user.getStr("PhoneMobile"),
					user.getInt("ProfileID").toString())) {
				UserProfile xUserProfile = checkPhone(
						user.getStr("PhoneMobile"), user.getStr("DisplayName"),
						1);
				if (xUserProfile != null) {
					if (xUserProfile.get("PhoneMobile").equals(
							user.getStr("PhoneMobile"))) {
						isChanged = false;
					}
				}
			}
			if (isChanged) {
				int isGenerate = user.getInt("isGenerate");
				System.err.println(user);
				UserAccount userAccount = null;
				if (isGenerate == 1) {
					userAccount = UserAccount.dao
							.findFirst(
									"SELECT DISTINCT u.* from be_users u LEFT JOIN be_profiles p ON p.UserID=u.UserID  WHERE p.ProfileID=?",
									user.getInt("ProfileID"));
					userAccount.set("UserSN", user.getStr("PhoneMobile")).set(
							"UserName", user.getStr("PhoneMobile"));
				}
				if (user.update()) {
					if (userAccount != null) {
						userAccount.update();
					}
					renderJson("msg","修改成功!");
				} else
					renderJson("msg","修改失败!");
			} else
				renderJson("msg","修改失败,系统中已存在相同帐号!");

		}

	}

	@Override
	public void search() {

	}
	
	public void addAdminUser()
	{
		String schoolID = getPara("schoolID");
		Record school = Db.findFirst(SqlKit.sql("school.QuerySchoolByID"),
				new Object[] { schoolID });
		setAttr("school", school);
		render("addAdminUser.html");
	}

	/**
	 * @方法名:addAdminUser
	 * @方法描述：添加管理员帐号
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-16 上午11:04:13
	 */
	@Before(Tx.class)
	public void saveAdminUser() {
		// 保存用户帐号信息
		String UserID = UUID.randomUUID().toString();
		boolean saveU_success = new UserAccount().set("UserID", UserID)
				.set("UserName", getPara("user.UserName"))
				.set("UserSN", getPara("user.UserSN")).set("UserDB", "")
				.set("Password", getPara("user.Password"))
				.set("OrgPassword", getPara("user.Password"))
				.set("SpaceURL", "").set("CreateTime", DateUtils.nowDateTime())
				.set("LastLoginTime", "").set("EmailAddress", "")
				.set("refreshCount", 0).set("FriendsCount", 0)
				.set("ValidateFriendsCount", 0).set("TeacherFriendsCount", 0)
				.set("ClassFriendsCount", 0).set("smscount", 0)
				.set("sysinfocount", 0).set("shareinfocount", 0)
				.set("schoolID", getPara("user.schoolID")).save();
		// 保存用户角色信息
		boolean saveUc_success = new UserRoleConn().set("UserID", UserID)
				.set("RoleID", getPara("userroles.RoleID")).save();
		if (saveU_success && saveUc_success) {
			renderJson("msg","添加成功!");
		} else
			renderJson("msg","添加失败!");
	}
	
	public void getStudentClass() {
		String sql = "select g.ID, g.GroupName from be_profiles p left join be_extProfiles ep on ep.UserID=p.UserID \n"
					 +"left join ms_group g on g.ID = ep.ClassID where p.UserID =?";
		renderJson(Db.find(sql,getPara("UserID")));
		
	}

	/**
	 * @方法名:getAdminUserType
	 * @方法描述：查询管理员类别
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-16 上午11:03:47
	 */
	public void getAdminUserType() {
		String sql = "SELECT * FROM be_roles WHERE RoleType>100";
		renderJson(Db.find(sql));
	}
	
	/**
	 * 
	 * @方法名:getCommonUserType
	 * @方法描述：查询学校用户类别
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-18 下午7:29:23
	 */
	public void getCommonUserType() {
		String sql = "SELECT * FROM be_roles WHERE RoleType<100 and RoleType>=0";
		renderJson(Db.find(sql));
	}

	/**
	 * @方法名:getSchoolAdmin
	 * @方法描述：获取学校的管理员列表
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:48:18
	 */
	public void getSchoolAdmin() {
		//加入对检索的支持
		Page<Record> page = Db.paginate(getParaToInt("pageIndex", 0) + 1,
				getParaToInt("pageSize", 20),
				SqlKit.sql("user.startAdminUser"),
				SqlKit.sql("user.endAdminUser"), getPara("schoolID"));
		renderJson(page);
	}

	/**
	 * @方法名:importBaseInfo
	 * @方法描述：上传文件导入档案信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:47:55
	 */
	@Before(Tx.class)
	public void importBaseInfo() {
		String path=ROOTPATH+"/excelDir";
		File f=new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		UploadFile uploadFile = getFile("uploadFile", path);
		Map<String, Object> mesage = new HashMap<String, Object>();
		if (uploadFile != null) {
			try {
				List<Map<Object, Object>> list = XlsReader.readXls(uploadFile
						.getFile());
				if (list == null) {
					mesage.put("desc", "错误的档案模板，请在平台下载正确模板后重试！");
				} else {
					if (list.size() <= 2) {
						mesage.put("desc", "错误的档案模板！（原因：1、模板错误；2、数据为空）");
					} else {
						Map<Object, Object> m = list.get(0);
						if (m.get("key0").equals("电话号码") && m.get("key1").equals("姓名") && m.get("key2").equals("性别")
								&& m.get("key3").equals("卡号1") && m.get("key4").equals("卡号2") && m.get("key5").equals("卡号3")) {
							list.remove(0);
							StringBuilder rmsg = new StringBuilder();
							List<Map<Object, Object>> errorlist = new ArrayList<Map<Object, Object>>();
							List<Map<Object, Object>> warnlist = new ArrayList<Map<Object, Object>>();
							List<Map<Object, Object>> msglist = new ArrayList<Map<Object, Object>>();
							for (Map<Object, Object> map : list) {
								if (StringTool.isBlank(map.get("key0").toString())
										&& StringTool.isBlank(map.get("key1").toString())
										&& StringTool.isBlank(map.get("key2").toString())) {

								} else {
									System.err.println("===" + map.get("key0"));
									UserProfile result = checkPhone(map.get("key0").toString().trim(), map.get("key1").toString(), 1);

									if (result != null) {
										Map<Object, Object> warn = new HashMap<Object, Object>();
										warn.put("msgtype", "警告：");
										warn.put("name", map.get("key1"));
										warn.put("phone", map.get("key0"));
										warn.put("msg", "系统中已经存在！");
										warnlist.add(warn);
										System.err.println("消息提示："
												+ result.toJson());
									} else {
										boolean x = addUserInDataBase(map);
										if (!x) {
											Map<Object, Object> error = new HashMap<Object, Object>();
											error.put("msgtype", "错误：");
											error.put("name", map.get("key1"));
											error.put("phone", map.get("key0"));
											error.put("msg", "导入数据库失败！");
											errorlist.add(error);
										} else {
											Map<Object, Object> msg = new HashMap<Object, Object>();
											msg.put("msgtype", "消息：");
											msg.put("name", map.get("key1"));
											msg.put("phone", map.get("key0"));
											msg.put("msg", "档案导入成功！");
											msglist.add(msg);
										}
									}
								}

							}
							mesage.put("success", true);
							mesage.put("des", "上传成功！");
							errorlist.addAll(warnlist);
							errorlist.addAll(msglist);
							mesage.put("res", errorlist);
						} else {
							mesage.put("desc", "档案模板错误！");
						}
					}

				}
               uploadFile.getFile().delete();
			} catch (IOException e) {
				e.printStackTrace();
				mesage.put("success", false);
				mesage.put("des", "上传失败！");
			}
		} else {
			mesage.put("success", false);
			mesage.put("des", "上传失败！");
		}
		renderJson(mesage);

	}

	/**
	 * @方法名:addUserInDataBase
	 * @方法描述：保存生成业务帐号的基本信息
	 * @author: Carl.Wu
	 * @return: boolean
	 * @version: 2013-9-22 上午9:47:17
	 */
	private boolean addUserInDataBase(Map map) {
		String userID = UUID.randomUUID().toString();

		String id = getPara("id");
		String userType = getPara("userType");
		int sexID = map.get("key2").toString().trim().equals("男") ? 14 : map
				.get("key2").toString().trim().equals("女") ? 15 : 0;
		UserProfile userProfile = new UserProfile().set("UserID", userID)
				.set("niCheng", map.get("key1").toString())
				.set("isGenerate", 0)
				.set("identifyNmuber", map.get("key0").toString().trim())
				.set("UserType", userType).set("SexID", sexID)
				.set("DisplayName", map.get("key1").toString())
				.set("PhoneMobile", map.get("key0").toString().trim())
				.set("CardNO", map.get("key3"))
				.set("CardNO2", map.get("key4"))
				.set("CardNO3", map.get("key5"));
		//教师用户，需写入部门信息
		if (userType.equals("0")) {
			userProfile.set("DeptID", id);
		}
		userProfile.save();
		//学生用户，需写入extProfile表
		if (userType.equals("2")) {
			String schoolID = getPara("schoolID");
			if (schoolID != null && id != null) {
				new UserExtProfile().set("UserID", userID)
						.set("SchoolID", schoolID).set("ClassID", id).save();
			}
		}

		return true;

	}

	/**
	 * @方法名:excel
	 * @方法描述：档案模板
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:46:39
	 */
	public void excel() {
		List<UserProfile> data = new ArrayList<UserProfile>();
		Map<String, Object> beans = Maps.newHashMap();
		beans.put("users", data);
		String filename = "档案模板.xls";
		try {
			filename = new String(filename.getBytes(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] headers = new String[] { "电话号码", "姓名", "性别" };
		render(PoiRender.me(data).fileName(filename).headers(headers)
				.cellWidth(5000).headerRow(2));
	}

	/**
	 * @方法名:getClassStudent
	 * @方法描述：根据班级ID获取班级的学生信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:46:12
	 */
	public void getClassStudent() {
		String searchKey = getPara("key");
		Page<Record> page = null;
		String sqlSelect = "SELECT g.GroupName,p.ProfileID,p.UserID,p.UserType,p.SexID,ed.Caption SexCaption,p.identifyNmuber,p.DisplayName,"
				+ "p.CardNO,p.CardNO2,p.CardNO3,p.PhoneMobile,p.niCheng,p.isGenerate,case uc.Device_Token when '' then 0 else 1 end isApp,p.intState";
		String sqlFrom = "FROM be_profiles p LEFT JOIN be_extprofiles ep on ep.UserID=p.UserID LEFT JOIN ms_group g on g.ID=ep.ClassID \n"
						 + "left join ms_user_con uc on uc.UID=p.userID \n"
						 + "LEFT JOIN sys_enumdetail ed on ed.DetailID =p.SexID LEFT JOIN be_extprofiles e ON e.UserID=p.UserID \n"
						 + "WHERE (e.ClassID=? or e.SchoolID=?) AND p.UserType=?";
		if (StringTool.notNull(searchKey) || !StringTool.isBlank(searchKey)) {
			if (searchKey.contains(",")) {
				searchKey=searchKey.trim();
				String[] keyArray=searchKey.split(",");
				String keys="";
				for (int i = 0; i < keyArray.length; i++) {
					if (i==keyArray.length-1) {
						keys+="'"+keyArray[i]+"'";
					}else
					keys+="'"+keyArray[i]+"',";
				}
				sqlFrom += " and (p.PhoneMobile in("+keys+") OR p.DisplayName in("+keys+") OR p.niCheng in("+keys+"))";
			}else{
				sqlFrom +=" and (p.PhoneMobile LIKE '%"+searchKey+"%' OR p.DisplayName LIKE '%"+searchKey+"%' OR p.niCheng LIKE '%" + searchKey + "%')"; 
			}
		}
		page = Db.paginate(
					getParaToInt("pageIndex", 0) + 1,
					getParaToInt("pageSize", 20),
					sqlSelect,
					sqlFrom,
					getPara("classID"), getPara("classID"),
					getPara("userType"));		
		renderJson(page);
	}
	
	public void exportClassStudent(){
		String searchKey = getPara("key");
		String sqlString = "SELECT g.GroupName,ed.Caption SexCaption,p.identifyNmuber,p.DisplayName,p.PhoneMobile,p.CardNO,p.CardNO2,p.CardNO3,"
						 + "case p.intState when 1 then '启用' else '停用' end State \n"
						 + "FROM be_profiles p LEFT JOIN be_extprofiles ep on ep.UserID=p.UserID \n"
						 + "LEFT JOIN ms_group g on g.ID=ep.ClassID \n"
						 + "LEFT JOIN sys_enumdetail ed on ed.DetailID =p.SexID \n"
						 + "LEFT JOIN be_extprofiles e ON e.UserID=p.UserID WHERE (e.ClassID=? or e.SchoolID=?) AND p.UserType=2";
		if (StringTool.notNull(searchKey) || !StringTool.isBlank(searchKey)) {
			if (searchKey.contains(",")) {
				searchKey=searchKey.trim();
				String[] keyArray=searchKey.split(",");
				String keys="";
				for (int i = 0; i < keyArray.length; i++) {
					if (i==keyArray.length-1) {
						keys+="'"+keyArray[i]+"'";
					}else
						keys+="'"+keyArray[i]+"',";
				}
				sqlString += " and (p.PhoneMobile in("+keys+") OR p.DisplayName in("+keys+") OR p.niCheng in("+keys+"))";
			}else{
				sqlString +=" and (p.PhoneMobile LIKE '%"+searchKey+"%' OR p.DisplayName LIKE '%"+searchKey+"%' OR p.niCheng LIKE '%" + searchKey + "%')"; 
			}
		}
		List<Record> data = Db.find(sqlString,new Object[] {getPara("classID"), getPara("classID")});
		String[] headers = new String[] { "班级", "性别", "身份号" ,"姓名", "电话号码", "账号状态" };
        render(PoiRender.me(data)
        		.fileName("data.xls").headers(headers).cellWidth(5000).headerRow(2));
	}

	/**
	 * @方法名:getDeptTeacher
	 * @方法描述：根据部门编号查询部门中的老师信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:45:18
	 */
	public void getDeptTeacher() {
		String searchKey = getPara("key");
		Page<Record> page = null;
		String sqlSelect = "SELECT d.deptName,p.ProfileID,p.UserID,p.UserType,p.SexID,ed.Caption SexCaption,p.identifyNmuber,p.DisplayName,"
				+ "p.CardNO,p.CardNo2,p.CardNo3,p.PhoneMobile,p.niCheng,p.isGenerate,case uc.Device_Token when '' then 0 else 1 end isApp,p.intState";
		String sqlFrom = "FROM be_profiles p LEFT JOIN be_deptInfo d on d.deptID=p.deptID \n"
				+ "left join ms_user_con uc on uc.UID=p.userID \n"
				+ "LEFT JOIN sys_enumdetail ed on ed.DetailID =p.SexID \n"
				+ "WHERE (d.DeptID=? or d.SchoolID=?) AND p.UserType=?";
		if (StringTool.notNull(searchKey)  || !StringTool.isBlank(searchKey)) {
			sqlFrom += " and (p.PhoneMobile LIKE '%"+ searchKey +"%' OR p.DisplayName LIKE '%" + searchKey + "%' OR p.niCheng LIKE '%" + searchKey + "%')";
		}
		page = Db.paginate(
							getParaToInt("pageIndex", 0) + 1,
							getParaToInt("pageSize", 20),
							sqlSelect,
							sqlFrom,
							getPara("deptID"), getPara("deptID"),
							getPara("userType"));
		renderJson(page);
	}
	
	public void exportDeptTeacher(){
		String searchKey = getPara("key");
		String sqlString = "SELECT d.deptName,ed.Caption SexCaption,p.identifyNmuber,p.DisplayName,p.PhoneMobile,p.CardNO,"
						 + "case p.intState when 1 then '启用' else '停用' end State \n"
						 + "FROM be_profiles p LEFT JOIN be_deptInfo d on d.deptID=p.deptID \n"
						 + "LEFT JOIN sys_enumdetail ed on ed.DetailID =p.SexID \n"
						 + "LEFT JOIN be_deptinfo e ON e.DeptID=p.DeptID  WHERE (e.DeptID=? or e.SchoolID=?) AND p.UserType=0";
		if (StringTool.notNull(searchKey)  || !StringTool.isBlank(searchKey)) {
			sqlString += " and (p.PhoneMobile LIKE '%"+ searchKey +"%' OR p.DisplayName LIKE '%" + searchKey + "%' OR p.niCheng LIKE '%" + searchKey + "%')";
		}
		List<Record> data = Db.find(sqlString,new Object[] {getPara("deptID"), getPara("deptID")});
		String[] headers = new String[] { "部门", "性别", "身份号" ,"姓名", "电话号码", "账号状态" };
        render(PoiRender.me(data)
        		.fileName("data.xls").headers(headers).cellWidth(5000).headerRow(2));
	}
	
	public void getUserCallRecord(){
		
		List<UserCallRecord> ucr = new UserCallRecord().find("select * from ms_callrecord where OP_UID=? order by OP_TIME DESC Limit 0,50",getPara("userID"));
		renderJson(ucr);
	}

	/**
	 * @方法名:checkPhone
	 * @方法描述：检测在系统中是否已经存在帐号或用户名
	 * @author: Carl.Wu
	 * @return: UserProfile
	 * @version: 2013-9-22 上午9:44:37
	 */
	private UserProfile checkPhone(String phoneNumber, String Name,
			int checkIndex) {
		String sql = "SELECT DisplayName,PhoneMobile from be_profiles WHERE PhoneMobile=? ";
		UserProfile data = null;
		switch (checkIndex) {
		case 2:
			sql += "OR DisplayName=? ";
			data = UserProfile.dao.findFirst(sql, phoneNumber, Name);
			break;
		default:
			data = UserProfile.dao.findFirst(sql, phoneNumber);
			break;
		}

		if (data != null) {
			return data;
		} else
			return null;
	}

	/**
	 * @方法名:isChangedSN
	 * @方法描述：检测是否更新了电话号码
	 * @author: Carl.Wu
	 * @return: boolean
	 * @version: 2013-9-22 上午9:44:10
	 */
	private boolean isChangedSN(String sn, String id) {
		String sql = "select * from be_profiles where profileid=?";
		UserProfile xProfile = UserProfile.dao.findFirst(sql, id);
		if (xProfile.getStr("PhoneMobile").equals(sn)) {
			return false;
		}
		return true;

	}

	public void getUserList() {
		Page<UserAccount> page = null;
		String searchKey = getPara("key");
		String schoolID = getPara("schoolID");
		String classID = getPara("classID");
		String deptID = getPara("deptID");
		
		String sqlSelect = "SELECT distinct u.userID,p.displayName,u.userSN";
		String sqlFrom =" FROM be_users u left join be_profiles p on p.userID=u.userID left join be_extProfiles ep on ep.userID=p.userID \n"
						+"WHERE ep.schoolID=? and p.userType=?";		
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey))
			sqlFrom += " and p.displayName like '%" + searchKey + "%'";
		
		if (StringTool.notNull(classID) && !StringTool.isBlank(classID)) {
			sqlFrom += " and ep.classID=?";
			page = UserAccount.dao.paginate(
					getParaToInt("pageIndex", 0) + 1, getParaToInt("pageSize", 50),
					sqlSelect, sqlFrom, schoolID, getPara("userType"), classID);
		}else if (StringTool.notNull(deptID) && !StringTool.isBlank(deptID)) {
			sqlFrom += " and p.deptID=?";
			page = UserAccount.dao.paginate(
					getParaToInt("pageIndex", 0) + 1, getParaToInt("pageSize", 50),
					sqlSelect, sqlFrom, schoolID, getPara("userType"), deptID);
		}else {
			page = UserAccount.dao.paginate(
					getParaToInt("pageIndex", 0) + 1, getParaToInt("pageSize", 50),
					sqlSelect, sqlFrom, schoolID, getPara("userType"));
		}
		
		renderJson(page);
		
	}
	
	
	/**
	 * @方法名:getUserBaseInfoByID
	 * @方法描述：获取个人基本信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:43:42
	 */
	public void getUserBaseInfoByUserID() {
		String sql = "select * from be_profiles p where p.UserID=?";
		UserProfile xProfile = UserProfile.dao
				.findFirst(sql, getPara("userID"));
		//教师和学生类型，显示信息不同。
		if (Integer.parseInt(getPara("userType"))==0) {
			setAttr("teacher", xProfile);
			render("../teacher/editteacher.html");
		}else if(Integer.parseInt(getPara("userType"))==2){
			setAttr("student", xProfile);
			render("../students/editstudent.html");
		}

	}

	/**
	 * @方法名:generateAccount
	 * @方法描述：根据基础档案生成业务帐号
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:41:51
	 */
	@Before(Tx.class)
	public void generateAccount() {
		List<String> errormsg = new ArrayList<String>();
		String msg = "";
		try {
			String userids = getPara("userids");
			String[] params = userids.split(",");

			for (String userid : params) {
				System.err.println(userid);
				UserProfile profile = UserProfile.dao.findFirst(
						"select * from be_profiles where UserID=?", userid);
				List<UserExtProfile> extProfiles = UserExtProfile.dao.find(
						"select * from be_extprofiles where userid=?", userid);

				if (extProfiles.size() > 0
						&& profile.getInt("isGenerate").equals(0)) {
					new UserAccount()
							.set("UserID", userid)
							.set("UserName", profile.getStr("PhoneMobile"))
							.set("UserSN", profile.getStr("PhoneMobile"))
							.set("UserDB", "")
							.set("Password", "123456")
							.set("OrgPassword", "123456")
							.set("SpaceURL", "")
							.set("CreateTime", DateUtils.nowDateTime())
							.set("LastLoginTime", "")
							.set("EmailAddress", "")
							.set("refreshCount", 0)
							.set("FriendsCount", 0)
							.set("ValidateFriendsCount", 0)
							.set("TeacherFriendsCount", 0)
							.set("ClassFriendsCount", 0)
							.set("smscount", 0)
							.set("sysinfocount", 0)
							.set("shareinfocount", 0)
							.set("schoolID",
									extProfiles.get(0).getStr("SchoolID"))
							.save();
					String sql = "SELECT * FROM be_roles WHERE RoleType=?";
					Record r = Db.findFirst(sql, profile.getInt("UserType"));
					if (r != null) {
						new UserRoleConn().set("UserID", userid)
								.set("RoleID", r.getStr("RoleID")).save();
					}
					for (UserExtProfile userExtProfile : extProfiles) {
						new UserClassCon()
								.set("GROUP_ID", userExtProfile.get("ClassID"))
								.set("UID", userid)
								.set("AUTH_TYPE",profile.getInt("UserType"))
								.set("STATE", 0)
								.set("CREATETIME", DateUtils.nowDateTime())
								.save();
						ClassInfo c = ClassInfo.dao.findById(userExtProfile
								.get("ClassID"));
						c.set("CURCOUNT", c.getInt("CURCOUNT").intValue() + 1);
						c.set("HISCOUNT", c.getInt("HISCOUNT").intValue() + 1);
						c.update();
					}
					profile.set("isGenerate", 1).update();
					/*老版需初始化ms_tags表
					MysqlDBPro dbPro = new MysqlDBPro();
					dbPro.userid = userid;
					Db.execute(dbPro);
					*/

				} else {
					errormsg.add(profile.getStr("DisplayName"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "帐号生成失败!";
		}

		if (errormsg.size() > 0) {
			msg = errormsg.toString() + "|帐号生成失败，请优先设置用户的班级关联!";
		} else {
			msg = "帐号生成成功!";
		}
		renderJson("msg",msg);
	}

	/**
	 * @方法名:getTeacherClass
	 * @方法描述：获取教师的人教班级
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-22 上午9:41:17
	 */
	public void getTeacherClass() {
		String sql = "select e.UserID,s.SchoolID,s.SchoolName,e.ClassID,c.GROUPNAME ClassName,c.CURCOUNT studentCount \n"
				+ "from be_extprofiles e LEFT JOIN ms_group c ON c.ID=e.ClassID \n"
				+ "LEFT JOIN be_schoolinfo s ON s.SchoolID=e.SchoolID  WHERE e.UserID=?";
		List<Record> records = Db.find(sql, getPara("userID"));
		renderJson(records);
	}

	@Before(Tx.class)
	public void setTeacherClass() {
		String classids = getPara("classids").trim();
		String schoolid = getPara("schoolID");
		String userid = getPara("userID");
		String[] classParams = StringTool.isBlank(classids) ? null : classids
				.split(",");
		boolean x = opraRealationship(userid, classParams, schoolid);
		if (x) {
			renderJson("msg","更改成功!");
		} else {
			renderJson("msg","更改失败!");
		}
	}
	
	/**
	 * @方法名:opraRealationship
	 * @方法描述：处理学生或教师更换班级关系
	 * @author: Mula.liu
	 * @return: boolean
	 * @version: 2013-10-4
	 */
	private boolean opraRealationship(String userID, String[] ClassIDs,
			String schoolID) {
		try {
			UserProfile user = UserProfile.dao.findFirst(
					"select * from be_profiles where UserID=?", userID);
			int isGenerate = user.getInt("isGenerate");
			Db.update("DELETE FROM be_extprofiles  WHERE UserID=?", userID);
			if (isGenerate == 1) {
				Db.update("DELETE FROM ms_group_con  WHERE UID=?", userID);
			}

			for (String classID : ClassIDs) {
				new UserExtProfile().set("UserID", userID)
						.set("SchoolID", schoolID).set("ClassID", classID)
						.save();
				if (isGenerate == 1) {
					new ClassCon()
							.set("GROUP_ID", classID)
							.set("UID", userID)
							.set("AUTH_TYPE",user.getInt("UserType"))
							.set("STATE", 0)
							.set("CREATETIME", DateUtils.nowDateTime()).save();

				}

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @方法名:getClassUsers
	 * @方法描述：根据班级ID用户类型获取人员信息
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-23 上午11:02:35
	 */
	public void getClassUsers() {
		Page<UserProfile> page = UserProfile.dao
				.paginate(
						getParaToInt("pageIndex", 0) + 1,
						getParaToInt("pageSize", 20),
						"SELECT DISTINCT p.UserID,p.UserType,p.SexID,p.identifyNmuber,p.DisplayName,p.PhoneMobile,p.niCheng,p.DeptID,p.isGenerate",
						"FROM be_profiles p LEFT JOIN be_extprofiles ep ON ep.UserID=p.UserID WHERE ep.ClassID=? AND UserType=?",
						getPara("classID"), getPara("userType"));
		renderJson(page);

	}

	@Before(Tx.class)
	public void changeStudentsClass() {
		String msg = "";
		try {
			String useridparams = getPara("userids");
			String classidparams = getPara("classID");
			String schoolID = getPara("schoolID");
			String[] userids = useridparams.split(",");
			String[] classids = classidparams.split(",");
			for (int i = 0; i < userids.length; i++) {
				opraRealationship(userids[i], classids, schoolID);
			}
			msg = "调班成功!";
		} catch (Exception e) {
			e.printStackTrace();
			msg = "调班失败!";
		}

		renderJson("msg",msg);
	}

	/**
	 * @方法名:changeUseStatus
	 * @方法描述：改变帐号的状态
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-24 下午2:11:48
	 */
	@Before(Tx.class)
	public void changeUserStatus() {
		String msg = "操作失败!";
		try {
			int status = getParaToInt("status");
			String StrUserID = getPara("userID");
			String[] userids = StrUserID.split(",");
			for (String userid : userids) {
				UserProfile user = UserProfile.dao.findFirst(
						"select * from be_profiles where UserID=?", userid);
				if (user != null) {
					// 更新profile
					int userType = user.getInt("UserType");
					int isGenerate = user.getInt("isGenerate");
					user.set("intState", status).update();
					if (isGenerate == 1) {
						new UserAccount().set("UserID", userid)
								.set("intState", status).update();
					}
					
					//如果为教师用户，需删除教师与班级的关联设置；启用帐号时，需重新设置。
					if (userType == 0 && status == 0) {
						Db.update(
								"DELETE FROM be_extprofiles WHERE be_extprofiles.UserID=?",
								userid);
						Db.update(
								"DELETE FROM ms_group_con WHERE ms_group_con.UID=?",
								userid);
					}
				}

			}
			msg = "操作成功!";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson("msg", msg);

	}

	/**
	 * 
	 * @方法名:getUserAccountPass
	 * @方法描述：密码查询
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-26 下午4:30:21
	 */
	public void getUserAccountPass() {
		String userIDs = getPara("userIDs");
		String sql = "SELECT p.UserID,p.UserType,p.DisplayName,p.isGenerate,u.UserName,u.`Password`,p.intState,(CASE \n"
				+ "WHEN (p.isGenerate=1 AND p.intState=1) THEN '正常' \n"
				+ "WHEN p.isGenerate=0 THEN '未生成' \n"
				+ "WHEN (p.isGenerate=1 AND p.intState=0) THEN '停用' \n"
				+ "ELSE '未知' END)  Msg ";
		Page<Record> page = Db
				.paginate(
						getParaToInt("pageIndex", 0) + 1,
						getParaToInt("pageSize", 20),
						sql,
						"FROM be_profiles p LEFT JOIN be_users u ON u.UserID=p.UserID  WHERE p.UserID IN('"
								+ userIDs + "')");
		renderJson(page);
	}

	/**
	 * @方法名:resetAccountPass
	 * @方法描述：密码重置
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-26 下午4:30:35
	 */
	@Before(Tx.class)
	public void resetAccountPass() {
		String msg = "密码重置失败!";
		try {
			String userIDs = getPara("userIDs");
			String sql = "UPDATE be_users SET `Password`='123456',OrgPassword='123456' WHERE UserID IN('"
					+ userIDs + "')";
			Db.update(sql);
			msg = "密码重置成功!当前密码为：123456";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson("msg", msg);
	}

	public void exportLog() {
		String data = getPara("columns");
		List<CallBackMsg> beanList = Lists.newLinkedList();
		try {
			beanList = binder.getMapper().readValue(data,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String filename = DateUtils.nowDateTime() + "-importLog.xls";
		/*try {
			filename = new String(filename.getBytes(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		String[] headers = new String[] {"消息类型","消息说明","姓名","电话号码"};
		render(PoiRender.me(beanList).fileName(filename).headers(headers)
				.cellWidth(5000).headerRow(2));
	}

}
