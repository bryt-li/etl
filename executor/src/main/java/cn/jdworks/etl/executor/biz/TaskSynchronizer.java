package cn.jdworks.etl.executor.biz;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class TaskSynchronizer extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	public static final int SYNC_INTERVAL = 5000;

	private String rsyncAddr;
	private String tasksDir;
	private int tick = 0;
	
	private boolean isRunning = false;
	public synchronized boolean isRunning() {
		return isRunning;
	}
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startSynchronizer(String rsyncAddr, String tasksDir) throws Exception {

		this.tasksDir = tasksDir;
		
		if (!Files.isDirectory(Paths.get(tasksDir))) {
			throw new Exception("Tasks Directory does not exist.");
		}

		this.rsyncAddr = rsyncAddr;
		syncTasks();

		this.isRunning = true;
		this.start();
	}

	public void run() {
		int s = 100;
		while (isRunning()) {
			try {
				sleep(s);
				this.tick += s;
				trySync();
			} catch (Exception e) {
			}
		}
	}

	private void trySync() {
		if (this.tick < SYNC_INTERVAL)
			return;
		this.tick = 0;
		syncTasks();
	}

	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();
	}

	private void syncTasks() {
		String[] cmd = new String[] { "rsync", "--recursive", "--times", "--perms", "--links", "--delete", "--compress",
				this.rsyncAddr+"::tasks", this.tasksDir };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p;
		try {
			p = pb.start();
			int val = p.waitFor();
			if (val != 0)
				LOG.warn("Tasks synchronize failed.");
			else
				LOG.debug("Tasks synchronized.");
		} catch (Exception e) {
			LOG.warn("Tasks synchronize failed.");
		}
	}
}
