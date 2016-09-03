package cn.jdworks.etl.executor.biz;

import java.util.UUID;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class HeartbeatSender extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	private UUID uuid;
	
	private String heartbeatUrl;
	private String shutdownUrl;

	private boolean isRunning = false;
	public synchronized boolean isRunning() {
		return isRunning;
	}
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startSender(UUID uuid, String serverAddr) throws Exception {
		this.uuid = uuid;
						
		this.heartbeatUrl = "http://" + serverAddr + "/backend/heartbeat";
		this.shutdownUrl = "http://" + serverAddr + "/backend/shutdown";

		this.isRunning = true;
		this.start();
	}
	
	public static final int SEND_INTERVAL = 5000;
	public static final int FIRST_SEND_INTERVAL = 500;
	public static final String OK = "OK";

	int tick = 0;
	boolean isFirst = true;

	public void run() {
		int s = 100;
		while (isRunning()) {
			try {
				sleep(s);
				this.tick += s;
				trySend();
			} catch (Exception e) {
			}
		}
	}

	private void trySend() {
		int interval = isFirst ? FIRST_SEND_INTERVAL : SEND_INTERVAL;

		if (this.tick < interval)
			return;
		this.tick = 0;

		String response = HttpRequest.sendPost(this.heartbeatUrl, this.uuid.toString(), FIRST_SEND_INTERVAL / 2,
				FIRST_SEND_INTERVAL / 2);

		if (response.equals(OK)) {
			this.isFirst = false;
		} else {
			this.isFirst = true;
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