package cn.jdworks.etl.executor.biz;

import java.util.UUID;

public class LogsReporter extends Thread {
	private UUID uuid;

	private boolean isRunning = false;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void init(UUID uuid) {
		this.uuid = uuid;
		this.isRunning = true;
		this.start();
	}

	public void run() {
		while (isRunning()) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();
	}
}
