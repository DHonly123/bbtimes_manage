package com.cdqidi.bb.classes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.ImageCutterUtil;
import com.cdqidi.bb.comm.util.PropertiesFactoryHelper;
import com.cdqidi.bb.comm.util.StringTool;
import com.cdqidi.bb.dept.DeptInfo;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "/class")
public class ClassController extends BaseController implements CommOpration {
	private static String ROOTPATH =new File(ClassController.class.getClassLoader().getResource("").getPath()).getParentFile().getParent();
	// 根据学校ID获取班级信息
	@Override
	public void index() {
		String searchKey = getPara("key");
		String schoolID = getPara("schoolID");
		
		String sqlSelect = "SELECT c.ID ,c.GROUPNAME,c.SCHOOLID,s.SchoolName,c.FILE_URL,IFNULL(tc.tc,0) tCount,IFNULL(sc.sc,0) sCount";
		String sqlFrom =" FROM ms_group c \n"
				+"left join (select count(gc.ID) tc,gc.GROUP_ID from ms_group_con gc where gc.AUTH_TYPE=0 group by gc.GROUP_ID) tc on tc.GROUP_ID=c.ID \n"
				+"left join (select count(gc.ID) sc,gc.GROUP_ID from ms_group_con gc where gc.AUTH_TYPE=2 group by gc.GROUP_ID) sc on sc.GROUP_ID=c.ID \n"
				+"LEFT JOIN be_schoolinfo s ON c.SCHOOLID=s.SchoolID WHERE c.SchoolID=? ";
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlFrom += " and c.groupName like '%" + searchKey + "%'";
		}
		Page<Record> page = Db.paginate(
						getParaToInt("pageIndex", 0) + 1,
						getParaToInt("pageSize", 20),
						sqlSelect,
						sqlFrom,
						schoolID);
		renderJson(page);
	}

	@Override
	public void save() {
		boolean success = new ClassInfo()
				.set("ID", UUID.randomUUID().toString()).set("UID", "")
				.set("GROUPNAME", getPara("class.GROUPNAME"))
				.set("SCHOOLID", getPara("class.SCHOOLID"))
				.set("GROUPNUM", "").set("GROUPIMG", "").set("PUBSTATE", 1)
				.set("CURCOUNT", 0).set("HISCOUNT", 0)
				.set("CREATETIME", DateUtils.nowDateTime()).save();
		if (success) {
			renderJson("添加成功!");

		} else
			renderJson("添加失败!");

	}

	@Override
	public void delete() {

		String classID = getPara("classID");
		Record r = Db.findFirst(SqlKit.sql("class.countClassUser"), classID);

		if (r.getNumber("cuCount").intValue() > 0) {
			renderJson("msg", "此班级有关联学生或教师信息，无法删除！");
		} else {
			
			boolean x = new ClassInfo().deleteById(classID); //必须是表的主键

			if (x) {
				renderJson("msg", "班级删除成功！");
			} else {
				renderJson("msg", "班级删除失败！");
			}

		}

	}

	@Override
	public void update() {
		boolean success = getModel(ClassInfo.class, "class").update();
		if (success) {
			renderJson("更新成功!");
		} else
			renderJson("更新失败！");
	}

	@Override
	public void search() {
		ClassInfo classinfo = ClassInfo.dao.findFirst(
				"select * from ms_group WHERE ID=?", getPara("id"));
		renderJson(classinfo);
	}

	public void getClassList() {
		String sql = "SELECT  c.ID ID,c.GROUPNAME Caption,c.SCHOOLID PID,c.FILE_URL from ms_group c  WHERE c.SCHOOLID=? \n" +
				"UNION SELECT SchoolID ID,SchoolName Caption,0 PID,'' FILE_URL  from be_schoolinfo  WHERE SchoolID=? ";
		renderJson(ClassInfo.dao.find(sql, getPara("schoolID"),getPara("schoolID")));
	}
	
	public void getClassByID(){
		ClassInfo classinfo = ClassInfo.dao.findFirst(
				"select ID,GROUPNAME,SCHOOLID from ms_group WHERE ID=?", getPara("id"));
		setAttr("classinfo", classinfo);
		render("editClass.html");
		
	}
/*
	public void cutImg() {
		boolean issuccess = false;
		// 选择框相关参数
		Long selectorX = getParaToLong("selectorX");
		Long selectorY = getParaToLong("selectorY");
		Long selectorW = getParaToLong("selectorW");
		Long selectorH = getParaToLong("selectorH");
		// 预览图片大小
	//	Long viewPortW = getParaToLong("viewPortW");
	//	Long viewPortH = getParaToLong("viewPortH");

		 String simageH = getPara("imageH");
		 String simageW = getPara("imageW");
		 String simageY = getPara("imageY");
		 String simageX = getPara("imageX");
	    

		// 旋转角度
		//Long imageRotate = getParaToLong("imageRotate");
		// 资源路径
		String imageSource = getPara("imageSource");

		String userid = getPara("userid");
		String root = ROOTPATH;
		String srcroot = root + imageSource.substring(2);
		System.err.println(root);
		PropertiesFactoryHelper propFactory = PropertiesFactoryHelper
				.getInstance();

		try {
			String distsrc = propFactory.getConfig("HEADER_SAVEPATH") + "/circle/"
					+ userid + "/";
			File fx = new File(distsrc);
			if (!fx.exists()) {
				fx.mkdirs();
			}
			String newpath = ImageCutterUtil.zoom(srcroot, null, formartString(simageW).intValue(), formartString(simageH).intValue());
			ImageCutterUtil.cutImage(newpath, distsrc + "header128x128.jpg",
					(selectorX.intValue()-formartString(simageX).intValue()), (selectorY.intValue()-formartString(simageY).intValue()),
					selectorW.intValue(), selectorH.intValue());
		    new ClassInfo().set("ID", userid).set("FILE_URL", "circle/"+ userid + "/header128x128.jpg").update();
			issuccess = true;
			File fsrc = new File(srcroot);
			if (fsrc.exists()) {
				fsrc.delete();
			}
			File nsrc = new File(newpath);
			if (nsrc.exists()) {
				nsrc.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson("msg", issuccess);
	}
	
	private BigDecimal formartString(String s) throws Exception{
		if (StringKit.isBlank(s)) {
			throw new Exception("未发现有需要转换的字符参数");
		}
		BigDecimal bigDecimal=new BigDecimal(s);
		return bigDecimal;
		
	}
*/	
}
