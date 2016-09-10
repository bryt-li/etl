package cn.jdworks.etl.executor.biz;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class TaskManager implements TaskEventHandler {

	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	private LogsReporter logsReporter;

	public void startManager() {
		this.tasks = new Hashtable<Integer, TaskRunner>();
		LOG.debug("Task Manager Started.");
	}

	public void shutdown() {
		LOG.debug("Stop Task Manager.");
		Iterator<Entry<Integer, TaskRunner>> iter = this.tasks.entrySet().iterator();
		while (iter.hasNext()) {
			TaskRunner task = iter.next().getValue();
			task.abortTask();
			LOG.debugf("[%d:%s] stopped.", task.getTaskId(),task.getTaskCommand());
			iter = this.tasks.entrySet().iterator();
		}
		this.tasks.clear();
		LOG.debug("Task Manager Stopped.");
	}

	public boolean runTask(int id, String cmd) {
		if (this.tasks.containsKey(id)) {
			this.onTaskStartFailed(id, System.currentTimeMillis(),
					"Duplicated task id: A task with same ID is running.");
			return false;
		}

		TaskRunner task = new TaskRunner();
		if (task.startRunner(id, cmd, this)) {
			this.addTask(id, task);
			return true;
		} else
			return false;
	}

	public void onTaskStarted(int id, long ts) {
		if (logsReporter == null)
			return;
		logsReporter.addLog(id, ts, "start", null);
	}

	public void onTaskErrorLogged(int id, long ts, String error) {
		if (logsReporter == null)
			return;
		logsReporter.addLog(id, ts, "stderr", error);
	}

	public void onTaskLogged(int id, long ts, String type, String message) {
		if (logsReporter == null)
			return;
		logsReporter.addLog(id, ts, type, message);
	}

	public void onTaskStopped(int id, long ts, int exit) {
		this.removeTask(id);

		if (logsReporter == null)
			return;

		logsReporter.addLog(id, ts, "exit", String.format("exit code: %d", exit));
	}

	public void onTaskStartFailed(int id, long ts, String message) {
		if (logsReporter == null)
			return;
		logsReporter.addLog(id, ts, "fail", message);
	}

	private Hashtable<Integer, TaskRunner> tasks;

	private synchronized void addTask(int id, TaskRunner task) {
		this.tasks.put(id, task);
		//LOG.debugf("task added. id: [%d]",id);
	}

	private synchronized void removeTask(int id) {
		this.tasks.remove(id);
		//LOG.debugf("task removed. id: [%d]",id);
	}

}
