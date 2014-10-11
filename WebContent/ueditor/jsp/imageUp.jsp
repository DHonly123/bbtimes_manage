    <%@ page language="java" contentType="text/html; charset=utf-8"
        pageEncoding="utf-8"%>
    <%@page import="com.cdqidi.bb.comm.util.Uploader"%>

    <%
    
    request.setCharacterEncoding("utf-8");
	response.setCharacterEncoding("utf-8");
    Uploader up = new Uploader(request);
    up.setSavePath("notice");
    String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
    up.setAllowFiles(fileType);
    up.setMaxSize(10000); //单位KB
    up.upload();
    System.out.print(up.getUrl());
    response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
    %>
