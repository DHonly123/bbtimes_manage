package com.cdqidi.bb.timer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cdqidi.bb.message.SMS;;

/**
 * @file MyJob.java
 * @brief XXX
 * 
 * @author Administrator
 * @date 2012-5-8
 * 
 * @details <i>CopyRright 2012 LEMOTE. All Rights Reserved.</i>
 */

/**
 * @brief 继承了Job接口的任务类
 * 
 */
public class SysJob1 implements Job{

	/** @brief XXX */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//输出执行job1的时间
		//System.out.println("执行job1时间："+ new Date());
		try {
			//SMS.sendSms();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
