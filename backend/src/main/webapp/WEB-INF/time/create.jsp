<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/_inc/navheader.jsp"></jsp:include>

<ol class="breadcrumb">
	<li><a href="${base}/dashboard">控制面板</a></li>
	<li><a href="${base}/time">定时器任务</a></li>
	<li class="active">新建任务</li>
</ol>

<form id="createTimeTaskForm" action="#" onsubmit="createTimeTask();return false;">
	<div class="form-group">
		<label for="Name">任务名称</label>
		<input type="text" class="form-control" id="Name" name="Name" placeholder="输入任务名称" required autofocus />
	</div>
	
	<div class="form-group">
		<label for="Description">任务描述（可选）</label>
		<textarea id="Description" name= "Description" class="form-control" rows="3" placeholder="输入任务描述"></textarea>
	</div>

	<div class="form-group">
		<label for="datetimepickerExecTime">执行时间</label>
		<div class="input-group date" id="datetimepickerExecTime">
			<input type="text" class="form-control" id="FirstExeTime" name="FirstExeTime" placeholder="选择任务首次执行时间" required />
			<span class="input-group-addon"> <span class="glyphicon glyphicon-calendar"></span></span>
		</div>
	</div>

	<div class="form-group">
		<label for="Script">执行命令</label>
		<input type="text" class="form-control" id="Script" name="Script" placeholder="输入要执行的命令" required />
	</div>

	<div class="form-group">
		<label for="Args">命令参数（可选）</label>
		<input type="text" class="form-control" id="Args" name="Args" placeholder="输入要执行命令的参数" />
	</div>

	<div class="checkbox">
		<label><input type="checkbox" id="IsRepeated" name="IsRepeated" />定时重复执行</label>
	</div>
	
	<div class="form-group">
		<label for="ExeInterval">重复执行间隔（单位：秒）</label>
		<input type="text" class="form-control" id="ExeInterval" name="ExeInterval" placeholder="输入重复执行间隔秒数" disabled="disabled" />
	</div>

	<div class="form-group">
		<button type="submit" id="create_button" class="btn btn-primary col-sm-3">创建</button>
		<div class="col-sm-6"></div>
		<a class="btn btn-default btn-md col-sm-3" href="${base}/time/" role="button">取消</a>
	</div>
</form>

<jsp:include page="/WEB-INF/_inc/navfooter.jsp"></jsp:include>