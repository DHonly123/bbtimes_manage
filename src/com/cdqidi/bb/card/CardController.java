package com.cdqidi.bb.card;

import java.util.List;
import java.util.UUID;

import com.cdqidi.bb.classes.ClassInfo;
import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "/card")
public class CardController extends BaseController implements CommOpration{
	
	@Override
	public void index(){
		String searchKey = getPara("key");
		String schoolID = getPara("schoolID");
		
		String sqlSelect = "select m.MachineID,s.schoolName,m.MachineCode,m.Memo,rdc.rCount";
		String sqlFrom =" from ns_machineInfo m \n"
				+"left join be_schoolInfo s on s.schoolID=m.schoolID \n"
				+"left join (select MachineCode,count(DetailID) rCount from ns_recordDetail_2014 group by MachineCode) rdc \n"
				+"on rdc.MachineCode=m.MachineCode \n"
				+"where 0=0";
		if (StringTool.notNull(schoolID) && !StringTool.isBlank(schoolID))
				sqlFrom+=" and  m.schoolID='"+schoolID+"'";
		if (StringTool.notNull(searchKey) && !StringTool.isBlank(searchKey)) {
			sqlFrom += " and m.MachineCode like '%" + searchKey + "%'";
		}

		Page<Record> page = Db.paginate(
						getParaToInt("pageIndex", 0) + 1,
						getParaToInt("pageSize", 20),
						sqlSelect,
						sqlFrom);
		renderJson(page);
	}
	
	@Override
	public void  save(){
		boolean success = getModel(MachineInfo.class, "machine").save();
		if (success) {
			renderJson("添加成功!");
		} else
			renderJson("添加失败!");
	}
	
	@Override
	public void  delete(){
		String machineID = getPara("machineID");
		Record r = Db.findFirst(SqlKit.sql("machine.countMachineRecord"), machineID);

		if (r.getNumber("rCount").intValue() > 0) {
			renderJson("msg", "此接送机包含打卡记录，无法删除！");
		} else {
			
			boolean x = new MachineInfo().deleteById(machineID); //必须是表的主键

			if (x) {
				renderJson("msg", "接送机删除成功！");
			} else {
				renderJson("msg", "接送机删除失败！");
			}

		}
	}
	
	@Override
	public void update(){
		boolean success = getModel(MachineInfo.class, "machine").update();
		if (success) {
			renderJson("更新成功!");
		} else
			renderJson("更新失败！");
	}
	
	@Override
	public void search(){
		
	}
	
	public void getMachineByID(){
		MachineInfo machineInfo = MachineInfo.dao.findById(getPara("id"));
		setAttr("machineInfo", machineInfo);
		render("editMachine.html");
		
	}
	
	public void getRecordDetailByUserID(){
		List<RecordDetail> list = new RecordDetail().find("select * from ns_recordDetail_2014 where personID=? order by RecordTime",getPara("profileID"));
		renderJson(list);
	}
	
}
