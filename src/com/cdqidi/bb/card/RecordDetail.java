package com.cdqidi.bb.card;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
@TableBind(tableName="ns_recordDetail",pkName="DetailID")
public class RecordDetail extends Model<RecordDetail>{
	private static final long serialVersionUID = 1L;
	public final static RecordDetail dao = new RecordDetail();
	
	@Before(Tx.class)
	public void exeRecordDetail() throws Exception{
		/*int execCount=Db.update(SqlKit.sql("card.exeRecordDetail"));
		if(execCount>0){
			Db.update(SqlKit.sql("card.updateExecTime"));
		}*/
		boolean r= false;
		
		for(Record cc : Db.find(SqlKit.sql("card.getUnexecRecord"))){
			CardRecord dd = CardRecord.dao.findFirst("select * from ns_record where UserID=? and RecordDate=?",cc.get("UserID"),cc.get("recordDate"));
			if(dd != null){
				//已有当日数据，更新
				r = dd.set("PmTime", cc.get("recordTime")).update();
			}else{
				//没有当日数据，插入
				r = new CardRecord().set("RecordID", cc.get("recordID")).set("SchoolID", cc.get("schoolID"))
						.set("UserID", cc.get("UserID")).set("RecordDate", cc.get("recordDate")).set("AmTime", cc.get("recordTime")).save();
			}
			if(r){
				Db.update(SqlKit.sql("card.updateExecTime"),cc.get("recordID"));
			}
		}
	}
}
