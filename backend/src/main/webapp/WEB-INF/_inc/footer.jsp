<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

</div>
<!-- Site footer -->
<div class="__bottom">
	<div class="container">
		<div class="col-md-4">
			<h3>
				<span class="glyphicon glyphicon-user"></span>开发者信息
			</h3>
			<p>Lixin: 15388031573</p>
		</div>
		<div class="col-md-4">
			<h3>
				<span class="glyphicon glyphicon-info-sign"></span>帮助文档
			</h3>
			<p>任务查看和任务管理</p>
		</div>
		<div class="col-md-4">
			<h3>
				<span class="glyphicon glyphicon-send"></span>联系方式
			</h3>
			<p>电子邮箱: lixin@ngblab.org</p>
		</div>
	</div>
</div>

<!-- Include jQuery and bootstrap JS plugins -->
  	<script type="text/javascript" src="${base}/bower_components/jquery/dist/jquery.min.js"></script>
  	<script type="text/javascript" src="${base}/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

  	<script type="text/javascript" src="${base}/bower_components/moment/min/moment.min.js"></script>
	<script type="text/javascript" src="${base}/bower_components/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>

	<script type="text/javascript" src="${base}/bower_components/bootstrap3-dialog/dist/js/bootstrap-dialog.min.js"></script>

<c:if test="${obj.js != null}">
	<script src="${base}/res/${obj.js}"></script>
</c:if>
</body>
</html>