package cn.jdworks.etl.backend.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.json.Json;
import static org.junit.Assert.*;

public class MainModuleTest {


	private static ServletTester server;
	private static MockClient client;
	@BeforeClass
	public static void initClass() throws Exception {
		server = new ServletTester();
		server.setContextPath("/");

		// enabled nutz
		FilterHolder filter = server.addFilter(org.nutz.mvc.NutFilter.class, "/*",
				EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST));
		filter.setInitParameter("modules", "cn.caratel.cloud.voipBackend.EntryModule");
		server.addServlet(org.nutz.mvc.NutServlet.class, "/");

		// start
		server.start();
		System.out.println("Servelet tester started.");

		// init client
		client = new MockClient(server,"/account");
		// signup
		HttpTester response = client.post("/signup", "name=abc&password=123&number=12345678");
		assertEquals(200, response.getStatus());

		// signin
		response = client.post("/signin", "name=abc&password=123");
		assertEquals(200, response.getStatus());
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		// signout
		HttpTester response = client.get("/signout", "");
		assertEquals("true", response.getContent());

		server.stop();
		System.out.println("Servelet tester stopped.");
	}

	@Before
	public void before() {

	}

	@After
	public void after(){
		
	}

	@Test
	public void testSetup() {
	}
}
