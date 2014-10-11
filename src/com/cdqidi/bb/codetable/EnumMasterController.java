package com.cdqidi.bb.codetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "enum")
public class EnumMasterController extends BaseController implements
		CommOpration {

	@Override
	public void search() {
		Page<EnumMaster> page = EnumMaster.dao.paginate(getParaToInt(0, 1), 20,
				"select * ", "from sys_enummaster");
		renderJson(page);
	}

	@Override
	public void save() {
		boolean success = new EnumMaster().set("Caption", getPara("Caption"))
				.set("Code", StringTool.generateMixString(6))
				.set("CreateDate", DateUtils.nowDateTime())
				.set("intState", getParaToInt("intState")).save();
		if (success) {
			renderText("添加成功~");
		} else
			renderText("添加失败~");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void delete() {
		String result = "";
		Integer[] ids = getParaValuesToInt("ids");
		ArrayList forbid = new ArrayList();
		ArrayList fails = new ArrayList();
		for (Integer i : ids) {
			List<Record> list = Db
					.find("SELECT DetailID,Caption  from sys_enumdetail WHERE MasterID=?",
							i);
			if (list != null) {
				forbid.add(i);
			} else {
				boolean xs = EnumMaster.dao.deleteById(i);
				if (!xs) {
					fails.add(i);
				}

			}
		}
		if (forbid.size() == 0 && fails.size() == 0) {
			result += "删除成功~";
		} else {
			result += forbid.size() > 0 ? forbid.toString() + ":节点下数据不为空禁止删除~"
					: "";
			result += fails.size() > 0 ? fails.toString() + ":删除失败原因未知~" : "";
		}
		renderText(result);
	}

	@Override
	public void update() {
	

	}

	@Override
	public void index() {
		Page<EnumMaster> page = EnumMaster.dao.paginate(getParaToInt(0, 1), 20,
				"select * ", "from sys_enummaster");
		renderJson(page);

	}

	
	
	
	
	
}
