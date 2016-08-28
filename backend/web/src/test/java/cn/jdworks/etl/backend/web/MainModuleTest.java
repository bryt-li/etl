package cn.jdworks.etl.backend.web;

import java.util.EnumSet;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainModuleTest {

	private static ServletTester server;
	private static MockClient client;

	@BeforeClass
	public static void initClass() throws Exception {
		server = new ServletTester();
		server.setContextPath("/");

		// enabled nutz
		FilterHolder filter = server.addFilter(org.nutz.mvc.NutFilter.class, "/*", 2);
		filter.setInitParameter("modules", "cn.jdworks.etl.backend.web.MainModule");
		server.addServlet(org.nutz.mvc.NutServlet.class, "/");

		// start
		server.start();
		System.out.println("Servelet tester started.");

		// init client
		client = new MockClient(server, "/etl");
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		server.stop();
		System.out.println("Servelet tester stopped.");
	}

	@Before
	public void before() {

	}

	@After
	public void after() {

	}

	@Test
	public void testFoo() throws Exception {
		// signup
		HttpTester response = client.get("/foo", "foo");
		assertEquals(200, response.getStatus());
	}
}
