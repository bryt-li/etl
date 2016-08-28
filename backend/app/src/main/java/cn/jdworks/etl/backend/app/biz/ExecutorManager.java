package cn.jdworks.etl.backend.app.biz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.jdworks.etl.backend.app.bean.ExeLog;
import cn.jdworks.etl.backend.app.bean.TaskExe;

@IocBean(singleton = true)
public class ExecutorManager extends Thread {

	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	protected Dao dao;

	private boolean isRunning;

	@Inject("java:$config.getInt('tcpPort')")
	private int TCP_PORT;

	@Inject("java:$config.get('jmsUrl')")
	private String JMS_URL;

	// resource statistics of executors(according to the asynchronizedly
	// received JMS logs)
	private Hashtable<UUID, ExecutorStat> executorStats;

	
	public void startManager() {
		this.setRunning(true);
		this.start();
	}

	public void shutdown() {
		this.setRunning(false);
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(isRunning()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized boolean isRunning() {
		return isRunning;
	}

	private synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	
	
	/*
	private synchronized void upThread(UUID executorId) {
		if (this.executorStats.get(executorId) == null)
			this.executorStats.put(executorId, new ExecutorStat());
		this.executorStats.get(executorId).upThreadNum();
	}

	private synchronized void downThread(UUID executorId) {
		if (this.executorStats.get(executorId) == null)
			this.executorStats.put(executorId, new ExecutorStat());
		this.executorStats.get(executorId).downThreadNum();
	}

	private synchronized void upDbConn(UUID executorId, String db) {
		if (this.executorStats.get(executorId) == null)
			this.executorStats.put(executorId, new ExecutorStat());
		this.executorStats.get(executorId).upDbConnNum(db);
	}

	private synchronized void downDbConn(UUID executorId, String db) {
		if (this.executorStats.get(executorId) == null)
			this.executorStats.put(executorId, new ExecutorStat());
		this.executorStats.get(executorId).downDbConnNum(db);
	}

	private synchronized void addExecutor(TcpConnector exe) {
		this.executors.add(exe);
	}

	private int index = 0;

	public synchronized void removeExecutor(TcpConnector exe) {
		this.executors.remove(exe);
		if (index >= this.executors.size()) {
			index = 0;
		}
	}

	private TcpConnector getAvailableExecutors() {
		TcpConnector executor = null;
		for (int i = index; i < this.executors.size(); i++) {
			executor = this.executors.get(i);
			if (executor.getUuid() != null) {
				index++;
				if (index >= this.executors.size()) {
					index = 0;
				}
				return executor;
			}
		}
		for (int i = 0; i < index; i++) {
			executor = this.executors.get(i);
			if (executor.getUuid() != null) {
				index++;
				if (index >= this.executors.size()) {
					index = 0;
				}
				return executor;
			}
		}
		// 没找到可用的executor
		return null;
	}

	public ExecutorManager() {
		this.executors = new ArrayList<TcpConnector>();
		this.executorStats = new Hashtable<UUID, ExecutorStat>();
	}

	
	public synchronized void executeTask(int taskId, String taskType, String cmd) {
		TaskExe exe = new TaskExe();
		exe.setId_Task(taskId);
		exe.setTaskType(taskType);
		exe.setCommand(cmd);
		exe.setStatus("PENDING");
		exe = dao.insert(exe);

		// 任务分配算法（目前先设置为简单轮转）
		TcpConnector executor = null;
		// 发送任务，直到成功或没有可用的Executor为止
		for (executor = this.getAvailableExecutors(); executor != null; executor = this.getAvailableExecutors()) {
			//LOG.debug("try send to executor: " + executor.getUuid());
			if (executor.sendTask(exe.getId(), cmd)) {
				// 向Executor发送任务
				LOG.info("send task to executor succeed: " + executor.getUuid());
				break;
			} else {
				this.removeExecutor(executor);
				LOG.error("send task to executor error: " + executor.getUuid());
			}
		}
		LOG.debug("execute task done.");
		if (executor == null) {
			// 没有可用的执行器，执行失败
			LOG.info("task execution failed because no available executor connected");
			exe.setStatus("FINISHED");
			exe.setExitValue("NO_EXECUTOR");
			dao.update(exe);
			return;
		}
	}
*/
	public synchronized void executeTask(int taskId, String taskType, String cmd) {
	}
	
	public boolean resetTimeoutTick(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

}
