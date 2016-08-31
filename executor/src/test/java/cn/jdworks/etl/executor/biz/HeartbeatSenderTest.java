package cn.jdworks.etl.executor.biz;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HeartbeatSenderTest {

	private HeartbeatSender sender;

	@BeforeClass
	public static void initClass() throws Exception {
	}

	@AfterClass
	public static void destroyClass() throws Exception {
	}

	@Before
	public void before() {

	}

	@After
	public void after() {

	}

	@Test
	public void testHeartbeatWhenServerError() throws Exception {
		int port = 3333;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		ErrorHandler handler = new ErrorHandler();
		server.createContext("/backend", handler);
		server.setExecutor(null); // creates a default executor
		server.start();

		this.sender = new HeartbeatSender();
		this.sender.startSender("localhost:" + port);

		Thread.sleep(5000);

		this.sender.shutdown();

		server.stop(0);

		handler.assertInterval();
	}

	static class ErrorHandler implements HttpHandler {

		private List<Long> intervals = new ArrayList<Long>();
		private Long last;

		public ErrorHandler() {
			last = System.currentTimeMillis();
		}

		public void handle(HttpExchange t) throws IOException {
			String response = "";
			if (t.getRequestURI().toString().equals("/backend/heartbeat")) {
				response = "ERR";
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

				Long current = System.currentTimeMillis();
				intervals.add(current - last);
				last = current;
			} else if (t.getRequestURI().toString().equals("/backend/shutdown")) {
				response = HeartbeatSender.OK;
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
			System.out.println(t.getRequestURI().toString() + " " + response);
		}

		public void assertInterval() {
			for (long interval : this.intervals) {
				System.out.println(interval);
				assertTrue(interval > HeartbeatSender.FIRST_SEND_INTERVAL);
				assertTrue(interval < HeartbeatSender.FIRST_SEND_INTERVAL*2);
			}
		}
	}

	@Test
	public void testHeartbeatWhenServerOk() throws Exception {
		int port = 3333;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		OkHandler handler = new OkHandler();
		server.createContext("/backend", handler);
		server.setExecutor(null); // creates a default executor
		server.start();

		this.sender = new HeartbeatSender();
		this.sender.startSender("localhost:" + port);

		Thread.sleep(25000);

		this.sender.shutdown();

		server.stop(0);

		handler.assertInterval();
	}

	static class OkHandler implements HttpHandler {

		private List<Long> intervals = new ArrayList<Long>();
		private Long last;

		public OkHandler() {
			last = System.currentTimeMillis();
		}

		public void handle(HttpExchange t) throws IOException {
			String response = "";
			if (t.getRequestURI().toString().equals("/backend/heartbeat")) {
				assertEquals(t.getRequestMethod(), "POST");
				BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println("client request data:" + line);
					UUID uuid = UUID.fromString(line);
					assertNotNull(uuid);
				}
				response = HeartbeatSender.OK;
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();

				Long current = System.currentTimeMillis();
				intervals.add(current - last);
				last = current;
			} else if (t.getRequestURI().toString().equals("/backend/shutdown")) {
				assertEquals(t.getRequestMethod(), "POST");
				BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println("client request data:" + line);
					UUID uuid = UUID.fromString(line);
					assertNotNull(uuid);
				}
				response = HeartbeatSender.OK;
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
			System.out.println(t.getRequestURI().toString() + " " + response);
		}

		public void assertInterval() {
			int i = 0;
			for (long interval : this.intervals) {
				System.out.println(interval);
				if (i == 0) {
					assertTrue(interval > 0);
					assertTrue(interval < HeartbeatSender.FIRST_SEND_INTERVAL*2);
				} else {
					assertTrue(interval > HeartbeatSender.SEND_INTERVAL);
					assertTrue(interval < HeartbeatSender.SEND_INTERVAL*2);
				}
				i++;
			}
		}
	}

}
