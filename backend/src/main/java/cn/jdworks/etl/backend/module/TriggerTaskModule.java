package cn.jdworks.etl.backend.module;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import cn.jdworks.etl.backend.biz.TriggerTaskManager;

@IocBean
@At("/triggertask")
@Ok("json")
@Fail("http:500")
public class TriggerTaskModule extends BaseModule{

	@Inject
	protected TriggerTaskManager triggerTaskManager;

	@At("/t/?")
	public boolean triggerTask(String url, @Param("..") String args) {
		return this.triggerTaskManager.executeTask(url, args);
	}

}
