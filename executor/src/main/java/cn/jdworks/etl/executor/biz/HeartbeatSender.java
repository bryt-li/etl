package cn.jdworks.etl.executor.biz;

import java.util.UUID;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(singleton = true)
public class HeartbeatSender extends Thread {
	private UUID uuid;
	private boolean isRunning = false;

	@Inject("java:$config.get('serverAddr')")
	private String SERVER_ADDR;
	private String heartbeatUrl;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void init(UUID uuid) {
		this.heartbeatUrl = "http://" + SERVER_ADDR + "/backend/heartbeat";
		this.uuid = uuid;
		this.isRunning = true;
		this.start();
	}

	private final int SEND_INTERVAL = 5000;
	private final int FIRST_SEND_INTERVAL = 500;
	private final String OK = "OK";
	
	int tick = 0;
	boolean isFirst = true;

	public void run() {
		int s = 100;
		while (isRunning()) {
			try {
				sleep(s);
				this.tick += s;
				trySend();
			} catch (InterruptedException e) {
			}
		}
	}

	private void trySend() {
		int interval = isFirst ? FIRST_SEND_INTERVAL : SEND_INTERVAL;

		if (this.tick < interval)
			return;

		this.tick = 0;

		String response = HttpRequest.sendPost(this.heartbeatUrl, this.uuid.toString());

		if (response == OK ) {
			this.isFirst = false;
		}
	}

	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();
	}
}
