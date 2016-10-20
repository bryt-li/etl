<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/_inc/navheader.jsp"></jsp:include>

<ol class="breadcrumb">
	<li><a href="${base}/dashboard">控制面板</a></li>
	<li><a href="${base}/time">定时器任务</a></li>
	<li class="active">全部</li>
</ol>

<p>
	<a class="btn btn-primary btn-sm" href="${base}/time/new" role="button">新建定时任务</a>
</p>

<table class="table table-hover">
	<tr>
		<th>任务名</th>
		<th>下次执行时间</th>
		<th>执行间隔</th>
		<th>重复执行</th>
		<th>状态</th>
		<th>操作</th>
	</tr>
	<tr>
		<td><a href="#">合并数据库</a></td>
		<td>2016-12-12 23:00:00</td>
		<td>24h00m00s</td>
		<td>重复</td>
		<td>激活</td>
		<td>
			<a class="btn btn-warning btn-xs" href="#" role="button">禁用</a>
			<a class="btn btn-danger btn-xs" href="#" role="button">删除</a>	
		</td>
	</tr>
	<tr></tr>
</table>

<jsp:include page="/WEB-INF/_inc/navfooter.jsp"></jsp:include>