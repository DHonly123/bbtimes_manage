package com.cdqidi.bb.message;

import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.xfire.client.Client;

public class SMS {
	
	public static void sendSms() throws Exception{
		
		String smid = "19876356";
		String schoolid = "9911";
		String tuserid = "1";// 暂定为1
		String busscode = "1027";
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar sendTime = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(c);
		String smlevel = "2";// 网关优先级
		int channelid = 10;// 短信发送区域（分为市内，省内:10，全网:11等）
		int isLong = 1;
		int result = 0; // 调用短信网关的返回结果
		int sendCount = 0;// 短信处理条数
		// webservice地址
		URL wsUrl = new URL(
				"http://221.182.1.11/apm/NewShortMessageWebServiceSOAP?wsdl");
		Client client = new Client(wsUrl);
		// 调用的方法名
		String methodName = "New_SendMessage";
		// 参数集合
		Object[] params = new Object[] { schoolid, tuserid, "15108315415",
				"test", sendTime, smid, busscode, smlevel, channelid, isLong,
				"" };
		// 结果
		Object[] results = client.invoke(methodName, params);

		for (Object objectValue : results) {
			result = (int) objectValue;
		}
		//System.out.println(result);
	}
}
