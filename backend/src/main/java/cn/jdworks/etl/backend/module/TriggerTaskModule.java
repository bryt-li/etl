package cn.jdworks.etl.backend.module;

import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.*;

import cn.jdworks.etl.backend.bean.TimeTask;
import cn.jdworks.etl.backend.bean.TriggerTask;
import cn.jdworks.etl.backend.biz.TriggerTaskManager;

@IocBean
@At("/trig")
@Ok("json")
@Fail("http:500")
public class TriggerTaskModule extends BaseModule{

	@Inject
	protected TriggerTaskManager triggerTaskManager;

	@Ok("re:jsp:trig.list")
	@At("/")
	@GET
	public String listAll(ViewModel model) {
		if (getMe() == null)
			return redirectToLoginPage();
		
		List<TriggerTask> list = dao.query(TriggerTask.class, null);
		model.setv("list", list);
		return null;
	}
	
	@At("/t/?")
	public boolean triggerTask(String url, @Param("..") String args) {
		return this.triggerTaskManager.executeTask(url, args);
	}

}
