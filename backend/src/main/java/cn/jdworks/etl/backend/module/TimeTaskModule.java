package cn.jdworks.etl.backend.module;

import org.nutz.dao.Dao;


import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import java.util.List;

import cn.jdworks.etl.backend.bean.TimeTask;
import cn.jdworks.etl.backend.biz.ExecutorManager;

@IocBean
@At("/timetask")
@Ok("json")
@Fail("http:500")
public class TimeTaskModule extends BaseModule{

	@Inject
	protected ExecutorManager executorManager;

	@At("/")
	@GET
	public List<TimeTask> getAllTasks() {
		List<TimeTask> list = dao.query(TimeTask.class, null);
		return list;
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
		return 0;
	}
}
