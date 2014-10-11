package com.cdqidi.bb.timer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.cdqidi.bb.card.RecordDetail;

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
public class SysJob2 implements Job{

	/** @brief XXX */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//输出执行job2的时间
		//System.out.println("执行job2时间："+ new Date());
		try {
			RecordDetail.dao.exeRecordDetail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
