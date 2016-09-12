package cn.jdworks.etl.executor.biz;

import java.util.UUID;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.jdworks.etl.utils.HttpRequest;


@IocBean(singleton = true)
public class HeartbeatSender extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	private UUID uuid;

	private String heartbeatUrl;
	private String shutdownUrl;
	private final String HEARTBEAT_URL_FMT = "http://%s/backend/heartbeat";
	private final String SHUTDOWN_URL_FMT = "http://%s/backend/shutdown";

	private boolean isRunning = false;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startSender(UUID uuid, String serverAddr) throws Exception {
		this.uuid = uuid;

		this.heartbeatUrl = String.format(HEARTBEAT_URL_FMT, serverAddr);
		this.shutdownUrl = String.format(SHUTDOWN_URL_FMT, serverAddr);

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

		if (OK.equals(response)) {
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
		if (OK.equals(response))
			LOG.debug("Heart beat sender shut down.");
		else
			LOG.warnf("Send shutdown failed. Expect: %s but Return: %s", OK, response);

	}
}