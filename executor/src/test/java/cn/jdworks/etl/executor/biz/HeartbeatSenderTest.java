package cn.jdworks.etl.executor.biz;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

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
	private ReentrantLock lock;
	
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
	public void testFirstHeartbeat() throws Exception {
		int port = 3333;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
	    server.createContext("/backend", new ErrorHandler(this.lock));
	    server.setExecutor(null); // creates a default executor
	    server.start();
		this.lock = new ReentrantLock();
		this.lock.lock();
	    
	    this.sender = new HeartbeatSender();
		this.sender.startSender("localhost:"+port);

		long ts = System.currentTimeMillis();
		this.lock.lock();
		assertTrue(System.currentTimeMillis()-ts<100);

		this.sender.shutdown();
		server.stop(0);
	}
	
	static class ErrorHandler implements HttpHandler {
		private ReentrantLock lock;
		public ErrorHandler(ReentrantLock lock){
			this.lock = lock;
		}
        public void handle(HttpExchange t) throws IOException {
            System.out.println(t.getRequestURI());
        	String response = "ERR";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            this.lock.unlock();
        }
    }


}
