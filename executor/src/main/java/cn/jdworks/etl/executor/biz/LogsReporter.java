package cn.jdworks.etl.executor.biz;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(singleton = true)
public class LogsReporter extends Thread {
	private boolean isRunning = false;
	public synchronized boolean isRunning() {
		return isRunning;
	}
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startReporter() {
		this.isRunning = true;
		this.start();
	}

	public void run() {
		while (isRunning()) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();
	}
}
