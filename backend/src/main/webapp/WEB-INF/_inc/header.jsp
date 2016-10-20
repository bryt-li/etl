<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Set the viewport so this responsive site displays correctly on mobile devices -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ETL Admin</title>
	<jsp:include page="/WEB-INF/_inc/_css_js.jsp"></jsp:include>
	<c:if test="${obj.css != null}">
	<link href="${base}/res/${obj.css}" rel="stylesheet">
	</c:if>
</head>
<body>
    <div>
    <!-- Site header and navigation -->
    <header class="__top" role="header">
        <div class="container">
            <a href="#" class="navbar-brand pull-left">ETL Admin</a>
            <button class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="glyphicon glyphicon-align-justify"></span>
            </button>
            <nav class="navbar-collapse collapse" role="navigation">
                <ul class="navbar-nav nav">
                    <li><a id="root" href="${base}/">首页</a></li>
                    <% if(session.getAttribute("me")==null){ %>
                    <li><a href="${base}/login">登录</a></li>
                    <% }else{ %>
                    <li><a href="${base}/logout">注销</a></li>                    
                    <% } %>
                    <li><a href="${base}/dashboard">控制面板</a></li>
                </ul>
            </nav>
        </div>
    </header>