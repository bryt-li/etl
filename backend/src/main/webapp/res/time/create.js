$(function() {
	$('#datetimepickerExecTime').datetimepicker({format: 'YYYY-MM-DD hh:mm:ss'});
	
	$("#IsRepeated").bind("click", function () {
		if($("#ExeInterval").prop("disabled")){
			$("#ExeInterval").prop("disabled",false);
			$("#ExeInterval").prop("required",true);
		}
		else{
			$("#ExeInterval").prop("disabled",true);
			$("#ExeInterval").prop("required",false);
		}
    });
});

function createTimeTask() {
    $.ajax({
        url: "new",
        type: "POST",
        data: $('#createTimeTaskForm').serialize(),
        error: function (request) {
            alert("Connection error");
        },
        dataType: "json",
        success: function (data) {
            if (data == "OK") {
            	root = $("#root").attr("href");
            	BootstrapDialog.show({
                    message: '创建任务成功，按任意键返回任务列表页面。',
                    onhidden: function(dialogRef){
                    	window.location = root+"time";
                    }
                });
            } else {
            	alert("创建任务失败，请检查。")
            }
        }
    });
}