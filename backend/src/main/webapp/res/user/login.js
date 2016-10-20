$(function () {
	// 登陆
	$("#login_button").click(function () {
	    // 提交数据
	    $.ajax({
	        url: "login",
	        type: "POST",
	        data: $('#loginForm').serialize(),
	        error: function (request) {
	            alert("Connection error");
	        },
	        dataType: "json",
	        success: function (data) {
	            if (data) {
	                window.location = data;
	            } else {
	            	alert("登录失败，请检查用户和密码。")
	            }
	        }
	    });
	});
});