package cn.jdworks.etl.executor.biz;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class HeartbeatSender extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	private UUID uuid;

	private String serverIP;
	private String tasksDir;
	
	private String heartbeatUrl;
	private String shutdownUrl;

	private boolean isRunning = false;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startSender(UUID uuid, String serverAddr, String tasksDir) throws Exception {
		this.uuid = uuid;
		
		this.tasksDir = tasksDir;
		
		if (!Files.isDirectory(Paths.get(tasksDir))) {
			throw new Exception("Tasks Directory does not exist.");
		}

		this.serverIP = serverAddr.split(":")[0];
		
		syncTasks();
		
		this.heartbeatUrl = "http://" + serverAddr + "/backend/heartbeat";
		this.shutdownUrl = "http://" + serverAddr + "/backend/shutdown";

		this.isRunning = true;
		this.start();
	}

	public static final int SYNC_INTERVAL = 60000;
	
	public static final int SEND_INTERVAL = 5000;
	public static final int FIRST_SEND_INTERVAL = 500;
	public static final String OK = "OK";

	int tickHeartbeat = 0;
	boolean isFirst = true;

	int tickSync = 0;

	public void run() {
		int s = 100;
		while (isRunning()) {
			try {
				sleep(s);
				this.tickHeartbeat += s;
				this.tickSync += s;
				trySend();
				trySync();
			} catch (Exception e) {
			}
		}
	}

	private void trySync() {
		if (this.tickSync < SYNC_INTERVAL)
			return;
		this.tickSync = 0;
		syncTasks();
	}

	private void trySend() {
		int interval = isFirst ? FIRST_SEND_INTERVAL : SEND_INTERVAL;

		if (this.tickHeartbeat < interval)
			return;

		this.tickHeartbeat = 0;

		String response = HttpRequest.sendPost(this.heartbeatUrl, this.uuid.toString(), FIRST_SEND_INTERVAL / 2,
				FIRST_SEND_INTERVAL / 2);

		if (response.equals(OK)) {
			this.isFirst = false;
		} else {
			this.isFirst = true;
		}
	}
	
	private void syncTasks() {
		String[] cmd = new String[] { "rsync", "--recursive", "--times", "--perms", "--links", "--delete", "--compress",
				this.serverIP+"::tasks", this.tasksDir };

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


	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();

		// send shutdown
		String response = HttpRequest.sendPost(this.shutdownUrl, this.uuid.toString(), 1000, 1000);
		if (response == null || !response.equals(OK))
			LOG.warn("Send shutdown failed. Expect: " + OK + " But return: " + response);
		else
			LOG.debug("Heart beat sender shut down.");

	}
}