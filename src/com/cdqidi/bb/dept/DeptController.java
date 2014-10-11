package com.cdqidi.bb.dept;

import java.util.List;
import java.util.UUID;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "/dept")
public class DeptController extends BaseController implements CommOpration {

	@Override
	public void save() {
		boolean success = getModel(DeptInfo.class, "dept")
				.set("DeptID", UUID.randomUUID().toString())
				.set("DeptCode", StringTool.generateMixString(6)).save();
		if (success) {
			renderJson("添加成功!");
		} else
			renderJson("添加失败!");
	}

	@Override
	public void update() {
		boolean succes = getModel(DeptInfo.class, "dept").update();
		if (succes) {
			renderJson("更新成功!");
		} else
			renderJson("更新失败!");
	}

	@Override
	public void search() {

	}

	@Override
	public void index() {
		String searchKey = getPara("key");
		String schoolID = getPara("schoolID");
		
		String sqlFrom =" FROM be_deptInfo d WHERE d.SchoolID=?";
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlFrom += " and d.deptName like '%" + searchKey + "%'";
		}
		Page<DeptInfo> page = DeptInfo.dao.paginate(
				getParaToInt("pageIndex", 0) + 1, getParaToInt("pageSize", 20),
				"Select DeptID,SchoolID,DeptCode,DeptName,Address",sqlFrom, schoolID);
		renderJson(page);
	}

	@Override
	public void delete() {

		String deptID = getPara("deptID");
		Record r = Db.findFirst(SqlKit.sql("dept.countDeptUser"), deptID);
		
		//直接用r.getInt("duCount") 会报类型错误;
		if (r.getNumber("duCount").intValue() > 0) {
			renderJson("msg", "此部门下有教师信息，无法删除！");
		} else {
			
			boolean x = new DeptInfo().deleteById(deptID); //必须是表的主键

			if (x) {
				renderJson("msg", "部门删除成功！");
			} else {
				renderJson("msg", "部门删除失败！");
			}

		}

	}

	public void getDeptList() {
		String schoolID = getPara("schoolID");
		String sql = "select DeptID ID,SchoolID PID,DeptCode Code,DeptName Caption from be_deptinfo  WHERE SchoolID=? UNION \n"
				+ "SELECT SchoolID ID,0 PID,SchoolCode Code,SchoolName Caption \n"
				+ "from be_schoolinfo  WHERE SchoolID=? ";
		List<DeptInfo> data = DeptInfo.dao.find(sql, schoolID, schoolID);
		renderJson(data);
	}

	public void getDeptByID() {
		//String sql = "select DeptID,SchoolID,DeptCode,DeptName,Address from be_deptinfo WHERE DeptID=? ";
		DeptInfo data = new DeptInfo().findById(getPara("deptID"));
		setAttr("deptInfo", data);
		render("editdept.html");
	}

}
