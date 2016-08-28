package cn.jdworks.etl.backend.app.module;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import cn.jdworks.etl.backend.app.biz.TriggerTaskManager;

@IocBean
@At("/triggertask")
@Ok("json")
@Fail("http:500")
public class TriggerTaskModule {

	@Inject
	protected TriggerTaskManager triggerTaskManager;

	@At("/t/?")
	public boolean triggerTask(String url, @Param("..") String args) {
		return this.triggerTaskManager.executeTask(url, args);
	}

}
