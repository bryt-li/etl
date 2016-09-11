package cn.jdworks.etl.executor.biz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(singleton = true)
public class LogsReporter extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	private List<TaskLog> logs = new ArrayList<TaskLog>();

	private String reportUrl;
	private static final String REPORT_URL_FMT = "http://%s/backend/log";
	private static final int MAX = 20;
	private static final int TIMEOUT = 2000;
	public static final String OK = "OK";

	private boolean isRunning = false;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startReporter(String serverAddr) {
		this.reportUrl = String.format(REPORT_URL_FMT, serverAddr);

		this.isRunning = true;
		this.start();
	}

	List<TaskLog> sendLogs;

	// every loop push 1-MAX logs
	public void run() {
		while (isRunning()) {			
			sendLogs = this.getSendLogs();

			if (sendLogs == null) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					LOG.error(e);
				}
				continue;
			}
			
			String response = HttpRequest.sendTaskLogs(this.reportUrl, cloneSendLogs(sendLogs), TIMEOUT, TIMEOUT);

			if (OK.equals(response)) {
				this.removeSentLogs(sendLogs);
			} else {
				LOG.warnf("Send logs failed. Return: [%s]", response);
			}
		}
	}

	public void shutdown() throws InterruptedException {
		this.setRunning(false);
		this.join();
	}

	public synchronized void addLog(int id, long ts, String type, String message) {
		TaskLog log = new TaskLog(id, ts, type, message);
		this.logs.add(log);
	}

	private synchronized List<TaskLog> getSendLogs() {
		int size = this.logs.size();
		int send = 0;
		if (size == 0)
			return null;
		if (size <= MAX)
			send = size;
		else
			send = MAX;
		
		return this.logs.subList(0, send);
	}
	
	private synchronized List<TaskLog> cloneSendLogs(List<TaskLog> src){
		List<TaskLog> dst = new ArrayList<TaskLog>(src);
		return dst;
	}

	private synchronized void removeSentLogs(List<TaskLog> sentLogs) {
		sentLogs.clear();
	}
}
