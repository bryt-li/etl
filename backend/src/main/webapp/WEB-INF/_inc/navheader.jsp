<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/_inc/header.jsp"></jsp:include>

<div class="middle">
	<div class="container">
		<div class="col-md-2">
			<h3>功能菜单</h3>
			<ul class="nav nav-pills nav-stacked">
				<li><a href="${base}/time/">定时器任务</a></li>
				<li><a href="${base}/trig/">触发器任务</a></li>
				<li><a href="${base}/exec/">任务执行情况</a></li>
			</ul>
		</div>
		<div class="col-md-10">