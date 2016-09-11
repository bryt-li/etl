package cn.jdworks.etl.executor.biz;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import junit.framework.Assert;

public class LogsReporterTest {
	private final static Log LOG = Logs.getLog(LogsReporterTest.class);

	private final static int PORT = 3333;
	private final static int DUMB_PORT = 4444;

	private static HttpServer dumbServer;
	private static HttpServer server;
	private static LogReporterHandler handler;
	private static DumbLogReporterHandler dumbHandler;

	private static LogsReporter logsReporter;

	private static HttpServer newMockHttpServer(int port, HttpHandler handler) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/backend", handler);
		server.setExecutor(null); // creates a default executor
		return server;
	}

	static class DumbLogReporterHandler extends LogReporterMockHttpHandlerHelper implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			LOG.debugf("Dumb: %s", t.getRequestURI());

			try {
				List<TaskLog> list = this.getPostTaskLogs(t);
				LOG.debugf("Recived logs: %d", list.size());
			} catch (Exception e) {
				LOG.debug(e);
				exit();
				return;
			}

			if (this.getTaskLogs().size() >= 3)
				exit();
		}
	}
	

	static class LogReporterHandler extends LogReporterMockHttpHandlerHelper implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			
			LOG.debugf("Response OK: %s", t.getRequestURI());
			try {
				List<TaskLog> list = this.getPostTaskLogs(t);
				LOG.debugf("Recived logs: %d", list.size());
				this.sendResponse(t, LogsReporter.OK);
			} catch (Exception e) {
				LOG.debug(e);
				exit();
				return;
			}

			if (this.getTaskLogs().size() >= 150)
				exit();
		}
	}


	@BeforeClass
	public static void initClass() throws Exception {
		dumbHandler = new DumbLogReporterHandler();
		dumbServer = newMockHttpServer(DUMB_PORT, dumbHandler);
		dumbServer.start();

		handler = new LogReporterHandler();
		server = newMockHttpServer(PORT, handler);
		server.start();
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		dumbServer.stop(0);
		server.stop(0);
	}

	@Before
	public void before() {

	}

	@After
	public void after() {

	}

	@Test
	public void testLogsReportToDumbServer() throws Exception {
		logsReporter = new LogsReporter();
		logsReporter.startReporter("localhost:" + DUMB_PORT);

		for (int i = 0; i < 3; i++)
			logsReporter.addLog(i, System.currentTimeMillis(), "debug", "this is test log: " + i);

		dumbHandler.waitForExit();

		Assert.assertEquals(dumbHandler.getTaskLogs().size(), 3);
	}

	@Test
	public void testLogsReportToServer() throws Exception {
		logsReporter = new LogsReporter();
		
		for (int i = 0; i < 50; i++)
			logsReporter.addLog(i, System.currentTimeMillis(), "debug", "this is test log: " + i);

		logsReporter.startReporter("localhost:" + PORT);

		for (int i = 51; i < 100; i++)
			logsReporter.addLog(i, System.currentTimeMillis(), "info", "this is test log: " + i);

		Thread thread = new Thread(new AddLogsRunnable());
		thread.start();

		handler.waitForExit();
		thread.join();
		
		// total 150
		Assert.assertEquals(handler.getTaskLogs().size(), 150);
	}

	
	class AddLogsRunnable implements Runnable {
		public void run() {
			for (int i = 100; i < 150; i++){
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
				}
				logsReporter.addLog(i, System.currentTimeMillis(), "fatal", "this is test log: " + i);
			}
		}
	}

	
}
