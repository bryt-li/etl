package cn.jdworks.etl.executor.biz;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import cn.jdworks.etl.utils.TaskLog;

/**
 * @author lixin This is base class of LogReport Mock HTTP handler
 */
public class LogReporterMockHttpHandlerHelper {

	private List<TaskLog> logs;


	public LogReporterMockHttpHandlerHelper() {
		this.logs = new ArrayList<TaskLog>();
	}

	public synchronized List<TaskLog> getTaskLogs() {
		return this.logs;
	}
	
	public synchronized void appendTaskLogs(List<TaskLog> newLogs){
		this.logs.addAll(newLogs);
	}

	protected synchronized void waitForExit() throws Exception {
		this.wait();
	}

	protected synchronized void exit() {
		this.notify();
	}

	protected void sendResponse(HttpExchange t, String response) throws Exception {
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	@SuppressWarnings("unchecked")
	protected List<TaskLog> getPostTaskLogs(HttpExchange t) throws Exception {
		if (!t.getRequestMethod().toString().equals("POST"))
			throw new Exception("Not POST method.");

		ObjectInputStream inputStream = new ObjectInputStream(t.getRequestBody());
		List<TaskLog> readLogs = (List<TaskLog>) inputStream.readObject();
		if(readLogs==null)
			throw new Exception("No task log received.");
		
		this.appendTaskLogs(readLogs);
		return readLogs;
	}
}
