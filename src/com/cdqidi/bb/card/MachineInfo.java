package com.cdqidi.bb.card;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName="ns_machineInfo" ,pkName="MachineID")
public class MachineInfo extends Model<MachineInfo>{
	private static final long serialVersionUID = 1L;
	
	public static final MachineInfo dao=new MachineInfo();

}
