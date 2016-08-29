package cn.jdworks.etl.executor.module;


import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;

public class MockClient {
	private String PREFIX;
	private ServletTester tester;

	private String cookie = null;

	public MockClient(ServletTester tester, String prefix) {
		this.tester = tester;
		this.PREFIX = prefix;
	}

	public HttpTester get(String url, String content) throws Exception {
		HttpTester request = new HttpTester();
		request.setHeader("Host", "tester");
		request.setVersion("HTTP/1.0");
		request.setMethod("GET");
		request.setURI(PREFIX + url + "?" + content);
		if (cookie != null && !cookie.isEmpty())
			request.setHeader("Cookie", cookie);

		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));
		String sc = response.getHeader("Set-Cookie");
		if (sc != null && !sc.isEmpty())
			this.cookie = sc;
		return response;
	}

	public HttpTester post(String url, String content) throws Exception {
		HttpTester request = new HttpTester();
		request.setHeader("Host", "tester");
		request.setVersion("HTTP/1.0");
		request.setMethod("POST");
		request.setURI(PREFIX + url);
		request.setContent(content);
		if (cookie != null && !cookie.isEmpty())
			request.setHeader("Cookie", cookie);

		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));
		String sc = response.getHeader("Set-Cookie");
		if (sc != null && !sc.isEmpty())
			this.cookie = sc;
		return response;
	}
}
