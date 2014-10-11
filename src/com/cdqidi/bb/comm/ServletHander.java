package com.cdqidi.bb.comm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class ServletHander extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {

		int index = target.lastIndexOf(".report");
		if (index != -1)
			target = target.substring(0, index);
		nextHandler.handle(target, request, response,new boolean[]{true});
	

	}

	

}
