package cn.jdworks.etl.backend.app.biz;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.jdworks.etl.backend.app.bean.TriggerTask;

@IocBean(singleton=true)
public class TriggerTaskManager {
	private final Log LOG = Logs.getLog(this.getClass());
	
	@Inject
	protected Dao dao;

	@Inject
	protected ExecutorManager executorManager;
	
	public boolean executeTask(String url, String args) {
		TriggerTask task = dao.fetch(TriggerTask.class, Cnd.where("url", "=", url));
		if(task==null){
			LOG.debug("trigger with wrong url: "+ url + " args: "+ args);
			return false;
		}
		if(args==null || args.isEmpty()){
			args = task.getArgs();
		}
		this.executorManager.executeTask(task.getId(), "TRIGGER", task.getScript() + " " + args);
		return true;
	}

}
