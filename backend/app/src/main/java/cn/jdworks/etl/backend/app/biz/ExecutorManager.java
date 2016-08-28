package cn.jdworks.etl.backend.app.biz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
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

	// for unit test only
	public void setDao(Dao dao) {
		this.dao = dao;
	};

	private BrokerService broker;

	private boolean isRunning;

	@Inject("java:$config.getInt('tcpPort')")
	private int TCP_PORT;

	@Inject("java:$config.get('jmsUrl')")
	private String JMS_URL;

	private JmsConnector jmsConnector;

	private ServerSocket listenSocket;

	private List<TcpConnector> executors;

	// resource statistics of executors(according to the asynchronizedly
	// received JMS logs)
	private Hashtable<UUID, ExecutorStat> executorStats;

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

	public void startExecutorManager() {
		LOG.info("JMS broker url is: " + this.JMS_URL);
		if (startBroker()) {
			LOG.info("broker start.");
		}
		this.jmsConnector = new JmsConnector(this, this.JMS_URL);
		this.jmsConnector.startConnector();

		this.setRunning(true);
		this.start();
	}

	public void shutdown() {
		this.jmsConnector.stopConnector();
		LOG.info("JmsConnector stop.");

		this.stopBroker();
		LOG.info("broker stop.");

		// close all executor's TCP connections
		for (TcpConnector exe : this.executors) {
			exe.close();
			try {
				exe.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.setRunning(false);
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			this.listenSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			LOG.info("Tcp listen port is: " + this.TCP_PORT);
			this.listenSocket = new ServerSocket(this.TCP_PORT);
			this.listenSocket.setSoTimeout(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (this.isRunning()) {
			try {
				Socket socket = this.listenSocket.accept();
				TcpConnector exe = new TcpConnector(socket);
				if (exe.startConnector(this))
					this.addExecutor(exe);
				LOG.debug("executor connected.");
			} catch (Exception e) {
				if (e instanceof SocketTimeoutException)
					continue;
				e.printStackTrace();
			}
		}
	}

	private boolean startBroker() {
		try {
			// This system property is used if we don‘t want to
			// have persistence messages as a default
			System.setProperty("activemq.persistenceAdapter", "org.apache.activemq.store.vm.VMPersistenceAdapter");

			broker = BrokerFactory.createBroker(new URI("broker://()/localhost"));
			broker.setBrokerName("DefaultBroker");
			broker.addConnector(this.JMS_URL);
			broker.setUseShutdownHook(false);
			broker.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.broker = null;
			return false;
		}
	}

	private void stopBroker() {
		if (this.broker != null) {
			try {
				this.broker.stop();
				this.broker.waitUntilStopped();
				this.broker = null;
			} catch (Exception e) {
				e.printStackTrace();
				this.broker = null;
			}
		}
	}

	private synchronized boolean isRunning() {
		return isRunning;
	}

	private synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
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

	// event handler for JmsConnector
	public synchronized void onTaskExeLogReceived(String type, int taskExeId, UUID executorId, Date timestamp,
			String log) {
		// make sure taskExeId exists in db
		TaskExe exe = dao.fetch(TaskExe.class, taskExeId);
		if (exe == null) {
			LOG.error("Exe log received with wrong taskExeId: " + taskExeId);
			return;
		}

		if (type.equals("THREAD++")) {
			this.upThread(executorId);
		} else if (type.equals("THREAD--")) {
			this.downThread(executorId);
		} else if (type.equals("CONNECT++")) {
			this.upDbConn(executorId, log);
		} else if (type.equals("CONNECT--")) {
			this.downDbConn(executorId, log);
		} else if (type.equals("START")) {
			exe.setStatus("EXECUTING");
			exe.setExeTime(timestamp);
			dao.update(exe);
			LOG.info("Task started. id:" + exe.getId());
		}else if (type.equals("END")) {
			exe.setStatus("FINISHED");
			exe.setExitValue("OK");
			exe.setExitTime(timestamp);
			dao.update(exe);
			LOG.info("Task ended. id:" + exe.getId());
		} else {
			ExeLog el = new ExeLog();
			el.setId_TaskExe(taskExeId);
			el.setTimestamp(timestamp);
			el.setType(type);
			el.setLog(log);
			dao.insert(el);
			LOG.info("Task log received. taskExeId:" + taskExeId + " type: " + type + " log: " + el.getLog());
		}
	}

	public boolean resetTimeoutTick(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

}
