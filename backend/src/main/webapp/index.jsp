<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/_inc/header.jsp"></jsp:include>
    <!-- Site banner -->
    <div class="banner">
        <div class="container">
            <h1>ETL: Extract, Transform, Load</h1>
            <p>数据抽取、转换、加载管理工具</p>
        </div>
    </div>

    <!-- Middle content section -->
    <div class="middle">
        <div class="container">
            <div class="col-md-9 content">
                <h2>使用ETL Admin控制面板可以管理定时任务的触发任务。</h2>
                <p>可以查看定时任务和触发任务的每次执行信息，以及每次执行所产生的详细日志信息。</p>
                <div class="to-tutorial">
                    <p><strong>登录进入控制面板:</strong></p>
                    <a href="${base}/dashboard" class="btn btn-success">控制面板</a>
                </div>
            </div>
            <div class="col-md-3">
                <h2>快捷菜单</h2>
                <ul class="nav nav-pills nav-stacked">
                    <li><a href="/" target="_blank">定时器任务</a></li>
                    <li><a href="/" target="_blank">触发器任务</a></li>
                    <li><a href="#" target="_blank">查看执行</a></li>
                    <li><a href="#" target="_blank">查看执行日志</a></li>
                </ul>
            </div>
        </div>
    </div>
<jsp:include page="/WEB-INF/_inc/footer.jsp"></jsp:include>