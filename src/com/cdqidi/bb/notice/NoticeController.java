package com.cdqidi.bb.notice;

import java.util.List;

import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.CommOpration;
import com.cdqidi.bb.comm.util.DateUtils;
import com.cdqidi.bb.comm.util.StringTool;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@ControllerBind(controllerKey = "/notice")
public class NoticeController extends BaseController implements CommOpration {

	@Override
	public void save() {
		boolean su = getModel(Notice.class, "notice").set("PublicDate",
				DateUtils.nowDateTime()).save();
		renderJson("msg", su);
	}

	@Override
	public void delete() {
		String noticeids = getPara("noticeids");
		String sql = "DELETE FROM ns_notice WHERE noticeID IN (" + noticeids
				+ ") ";
		int x = Db.update(sql);
		if (x >= 0) {
			renderJson("msg", "删除成功！");
		} else {
			renderJson("msg", "删除失败！");
		}

	}

	@Override
	public void update() {
		boolean success = getModel(Notice.class, "notice").update();
		renderJson("result", success);

	}

	@Override
	public void search() {
		// TODO Auto-generated method stub

	}

	@Override
	public void index() {
		// TODO Auto-generated method stub
		getNotices();
	}

	public void getNotices() {
		Page<Notice> page = Notice.dao
				.paginate(
						getParaToInt("pageIndex", 0) + 1,
						getParaToInt("pageSize", 20),
						"SELECT noticeID,SchoolID,noticeType,Title,PublicDate,Content,Summary,intState",
						" FROM ns_notice WHERE schoolID=? order by publicdate desc",
						getPara("schoolID"));
		renderJson(page);
	}

	public void edit() {
		setAttr("notice", Notice.dao.findById(getParaToInt("id")));
		render("editNotice.html");
	}
	
	// 此接口返回平台新闻的简要信息（封面图片URL | Title | 文字摘要 | 创建时间 | 作者），支持分页获取
	public void getNotice_SummaryList(){
		String SqlSelect = "select noticeID,Title,Author,PublicDate,Summary";
		String SqlFrom = " from ns_notice where noticeType=8 and intState=1";
		if (StringTool.notNull(getPara("pageIndex"))&&StringTool.notBlank(getPara("pageIndex"))){
			renderJson(Db.paginate(getParaToInt("pageIndex"), getParaToInt("pageSize",10), SqlSelect, SqlFrom));
		}else{
			renderJson("list",Db.find(SqlSelect+SqlFrom));
		}
	}
	
	// 此接口返回学校新闻的简要信息（封面图片URL | Title | 文字摘要 | 创建时间 | 作者），支持分页获取
	public void getSchoolNotice_SummaryList(){
		String SqlSelect="select noticeID,Title,Author,PublicDate,Summary";
		String SqlFrom=" from ns_notice where schoolID=? and noticeType=1 and intState=1";
		if (StringTool.notNull(getPara("pageIndex"))&&StringTool.notBlank(getPara("pageIndex"))){
			renderJson(Db.paginate(getParaToInt("pageIndex"), getParaToInt("pageSize",10), SqlSelect, SqlFrom,getPara("schoolID")));
		}else{
			renderJson("list",Db.find(SqlSelect+SqlFrom,getPara("schoolID")));
		}
	}

	/**
	 * @方法名:getNotice_Content
	 * @方法描述：根据ID查找通知详情
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-29 下午4:00:29
	 */
	public void getNotice_Content() {
		setAttr("notice", Notice.dao.findById(getPara("noticeID")));
		render("mobile_notice.html");
	}

}
