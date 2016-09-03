package cn.jdworks.etl.executor.biz;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

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

public class HeartbeatSenderTest {

	private final static Log LOG = Logs.getLog(HeartbeatSenderTest.class);

	private static int PORT = 3333;
	private static UUID uuid = UUID.randomUUID();
	
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

	private boolean isShortInterval(long interval) {
		return interval >= HeartbeatSender.FIRST_SEND_INTERVAL && interval < HeartbeatSender.FIRST_SEND_INTERVAL * 2;
	}

	private boolean isLongInterval(long interval) {
		return interval >= HeartbeatSender.SEND_INTERVAL && interval < HeartbeatSender.SEND_INTERVAL * 2;
	}

	private HttpServer newMockHttpServer(int port, HttpHandler handler) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/backend", handler);
		server.setExecutor(null); // creates a default executor
		return server;
	}

	@Test
	public void testHeartbeatWithDumbServer() throws Exception {
		int port = PORT + 1;
		DumbHandler handler = new DumbHandler(3);
		HttpServer server = newMockHttpServer(port, handler);
		server.start();
		handler.setStartupTimestamp();

		this.sender = new HeartbeatSender();
		this.sender.startSender(uuid, "localhost:" + port);

		handler.waitForExit();

		this.sender.shutdown();
		server.stop(0);

		for (long interval : handler.getIntervals()) {
			assertTrue(isShortInterval(interval));
		}
	}

	static class DumbHandler extends HeartbeatMockHttpHandlerHelper implements HttpHandler {

		public DumbHandler(int exitReqNum) {
			super(exitReqNum);
		}

		public void handle(HttpExchange t) throws IOException {
			LOG.debug(t.getRequestURI().toString());
			try {
				if (t.getRequestURI().toString().equals("/backend/heartbeat")) {
					this.recordInterval();
				} else if (t.getRequestURI().toString().equals("/backend/shutdown")) {
					//not record shutdown
				}
			} catch (Exception e) {
				exit();
			}
		}
	}

	@Test
	public void testHeartbeatWithBadServer() throws Exception {
		int port = PORT + 2;
		BadHandler handler = new BadHandler(3);
		HttpServer server = newMockHttpServer(port, handler);
		server.start();

		this.sender = new HeartbeatSender();
		this.sender.startSender(uuid, "localhost:" + port);
		handler.setStartupTimestamp();

		handler.waitForExit();

		this.sender.shutdown();
		server.stop(0);

		for (long interval : handler.getIntervals()) {
			assertTrue(isShortInterval(interval));
		}
	}

	static class BadHandler extends HeartbeatMockHttpHandlerHelper implements HttpHandler {

		public BadHandler(int exitReqNum) {
			super(exitReqNum);
		}

		public void handle(HttpExchange t) throws IOException {
			LOG.debug(t.getRequestURI().toString());
			try {
				if (t.getRequestURI().toString().equals("/backend/heartbeat")) {
					this.sendResponse(t, "ERR");
					this.recordInterval();
				} else if (t.getRequestURI().toString().equals("/backend/shutdown")) {
					this.sendResponse(t, HeartbeatSender.OK);
				}
			} catch (Exception e) {
				exit();
			}
		}
	}

	@Test
	public void testHeartbeatWithGoodServer() throws Exception {
		int port = PORT + 3;
		GoodHandler handler = new GoodHandler(6);
		HttpServer server = newMockHttpServer(port, handler);
		server.start();

		this.sender = new HeartbeatSender();
		this.sender.startSender(uuid, "localhost:" + port);
		handler.setStartupTimestamp();

		handler.waitForExit();

		this.sender.shutdown();
		server.stop(0);

		int i = 0;
		for (long interval : handler.getIntervals()) {

			if (i == 0 || i == 1 || i == 2) {
				assertTrue(isShortInterval(interval));
			} else if (i == 3 || i == 4) {
				assertTrue(isLongInterval(interval));
			} else if (i == 5) {
				assertTrue(isShortInterval(interval));
			}
			i++;
		}

	}

	static class GoodHandler extends HeartbeatMockHttpHandlerHelper implements HttpHandler {

		public GoodHandler(int exitReqNum) {
			super(exitReqNum);
		}

		public void handle(HttpExchange t) throws IOException {
			LOG.debug(t.getRequestURI().toString());
			try {
				if (t.getRequestURI().toString().equals("/backend/heartbeat")) {
					UUID uuid = this.getPostUUID(t);
					assertNotNull(uuid);

					String response = null;
					if (count == 0 || count == 1) {
						response = "ERR";
					} else if (count == 2 || count == 3) {
						response = HeartbeatSender.OK;
					} else if (count == 4) {
						response = "ERR";
					} else if (count == 5) {
						response = HeartbeatSender.OK;
					}
					this.sendResponse(t, response);
					this.recordInterval();
				} else if (t.getRequestURI().toString().equals("/backend/shutdown")) {
					UUID uuid = this.getPostUUID(t);
					assertNotNull(uuid);

					this.sendResponse(t, HeartbeatSender.OK);
				}
			} catch (Exception e) {
				exit();
			}
		}
	}

}
