package cn.jdworks.etl.backend.module;

import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import cn.jdworks.etl.backend.bean.TimeTask;
import cn.jdworks.etl.backend.biz.ExecutorManager;
import cn.jdworks.etl.backend.biz.TimeTaskScheduler;

@IocBean
@At("/time")
@Ok("json")
@Fail("http:500")
public class TimeTaskModule extends BaseModule{

	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	protected ExecutorManager executorManager;
	
	@Inject
	protected TimeTaskScheduler timeTaskScheduler;
	
	@At
	@GET
	public List<TimeTask> list(){
		if (getMe() == null)
			return null;
		
		List<TimeTask> list = dao.query(TimeTask.class, null);
		return list;
	}
	
	@At("/new")
	@POST
	public String createTask(@Param("..")TimeTask task) {
		if (getMe() == null)
			return "用户未登录";
		
		if(this.timeTaskScheduler.createTimeTask(task))			
			return "OK";
		else
			return "ERROR";
	}

	@At("/?")
	@GET
	public TimeTask getTask(int Id) {
		TimeTask t = dao.fetch(TimeTask.class, Id);
		return t;
	}

	@At("/?")
	@POST
	public boolean updateTask(int Id, @Param("..") TimeTask task) {
		// TODO 这里是实现代码
		return true;
	}

	@At("/?")
	@DELETE
	public void deleteTask(int Id) {
		// TODO 这里是实现代码
	}

	// default to @At("/timertask/count")
	@At
	public int count() {
		LOG.debug("count");
		return 0;
	}
}
