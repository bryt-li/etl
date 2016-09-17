package cn.jdworks.etl.backend.biz;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class ExecutorManager extends Thread {

	private final Log LOG = Logs.getLog(this.getClass());

	@Inject
	protected Dao dao;

	private boolean isRunning;

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

	private static final int INTERVAL = 1000;

	@Override
	public void run() {
		while (isRunning()) {
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tickExecutors(INTERVAL);
		}
	}

	private synchronized boolean isRunning() {
		return isRunning;
	}

	private synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private synchronized void tickExecutors(int tick) {
		Iterator<Entry<UUID, ExecutorStat>> iter = this.executorStats.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<UUID, ExecutorStat> entry = iter.next();
			ExecutorStat stat = entry.getValue();
			stat.tickTTL(tick);
			if (!stat.isAlive()) {
				this.executorStats.remove(stat.getId());
				LOG.debugf("[%s] is dead and removed.", stat.getId());
			}
		}
	}

	public synchronized void executeTask(int taskId, String taskType, String cmd) {
		
	}

	public synchronized void resetExecutorTimeoutTick(UUID uuid) {
		if (this.executorStats.containsKey(uuid)) {
			this.executorStats.get(uuid).resetTTL();
		} else {
			this.executorStats.put(uuid, new ExecutorStat(uuid));
		}
	}

	public synchronized void shutdownExecutor(UUID uuid) {
		if (this.executorStats.containsKey(uuid))
			this.executorStats.remove(uuid);
	}

}
