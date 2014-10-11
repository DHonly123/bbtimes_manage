package com.cdqidi.bb.comm.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.ICallback;

public class MysqlDBPro implements ICallback {
	public String userid = null;

	@Override
	public Object run(Connection conn) throws SQLException {
		CallableStatement proc = null;
		try {
			proc = conn.prepareCall("{ call myProc_init_tags(?) }");
			proc.setString(1, userid);
			proc.execute();
		} finally {
			DbKit.close(proc, conn);
		}
		return null;
	}

	/*@Override
	public void run(Connection conn) throws SQLException {
		CallableStatement proc = null;
		try {
			proc = conn.prepareCall("{ call myProc_init_tags(?) }");
			proc.setString(1, userid);
			proc.execute();
		} finally {
			DbKit.close(proc, conn);
		}
	}*/

}