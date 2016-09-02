package cn.jdworks.etl.executor.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.net.httpserver.HttpExchange;

/**
 * @author lixin This is base class of Heartbeat Mock HTTP handler
 */
public class HeartbeatMockHttpHandlerHelper {

	private List<Long> intervals;

	public List<Long> getIntervals() {
		return this.intervals;
	}

	private ReentrantLock lock;
	private long last;
	private int exitReqNum = 0;

	public HeartbeatMockHttpHandlerHelper(int exitReqNum) {
		this.intervals = new ArrayList<Long>();
		this.exitReqNum = exitReqNum;
	}
	protected synchronized void waitForExit() throws Exception{
		this.wait();
	}
	protected void exit(){
		this.notify();
	}

	public void setStartupTimestamp() {
		last = System.currentTimeMillis();
	}

	protected int count = 0;

	protected void sendResponse(HttpExchange t, String response) throws Exception {
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected UUID getPostUUID(HttpExchange t) throws Exception {
		if (!t.getRequestMethod().toString().equals("POST"))
			throw new Exception("Not POST request.");

		BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println("client request data:" + line);
			UUID uuid = UUID.fromString(line);
			return uuid;
		}
		throw new Exception("No UUID in request POST data.");
	}

	protected void recordInterval() throws IOException {
		Long current = System.currentTimeMillis();
		long interval = current - last;
		this.intervals.add(interval);
		last = current;
		if (++count >= exitReqNum)
			this.lock.unlock();
		System.out.println("count:" + count + " interval:" + interval + " exit request num:" + exitReqNum);
	}

}
