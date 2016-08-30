package cn.jdworks.etl.backend.biz;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.jdworks.etl.backend.bean.TimeTask;

@IocBean(singleton=true)
public class TimeTaskScheduler {
	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	protected Dao dao;

	@Inject
	private ExecutorManager executorManager;

	private Hashtable<Integer, TaskTimer> timers;	

	public TimeTaskScheduler() {
		this.timers = new Hashtable<Integer, TaskTimer>();
	}

	public synchronized void startScheduler() {
		List<TimeTask> list = dao.query(TimeTask.class, null);
		for(TimeTask task : list){
			//todo...判断是否需要执行
			TaskTimer timer = new TaskTimer(this, task);
			try {
				timer.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.timers.put(task.getId(), timer);
			LOG.debug("timer start: " + task.getName());
		}
	}

	public synchronized void shutdown() {
		for(TaskTimer timer : this.timers.values()){
			timer.stop();
		}
	}

	public synchronized void onTimeTaskTriggered(TimeTask task) {
		LOG.debug("Time task triggered: " + task.getName());

		task.setLastExeTime(new Date());
		this.dao.update(task);

		String cmd = task.getScript() + " " + task.getArgs();
		this.executorManager.executeTask(task.getId(), "TIME", cmd);
	}

}
