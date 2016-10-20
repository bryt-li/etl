<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/_inc/header.jsp"></jsp:include>

<div class="middle">
	<div class="container">
		<div class="col-md-12 content">
			<form class="form-signin" id="loginForm"
				action="#" onsubmit="return false;">
				<h2 class="form-signin-heading">登录系统</h2>
				<label for="inputUserName" class="sr-only">用户名</label> <input
					type="text" id="inputUserName" name="username" class="form-control"
					placeholder="输入用户名" required autofocus /> <label
					for="inputPassword" class="sr-only">密码</label> <input
					type="password" id="inputPassword" name="password"
					class="form-control" placeholder="输入密码" required />
				<button id="login_button" class="btn btn-lg btn-primary btn-block"
					type="submit">登录</button>
			</form>
		</div>
	</div>
</div>
<jsp:include page="/WEB-INF/_inc/footer.jsp"></jsp:include>