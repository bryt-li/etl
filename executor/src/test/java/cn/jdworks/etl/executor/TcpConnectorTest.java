package cn.jdworks.etl.executor;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;

import cn.jdworks.etl.executor.biz.TcpConnector;
import cn.jdworks.etl.executor.mock.BackendServer;

public class TcpConnectorTest {

	private int TCP_PORT = 5050;

	private BackendServer server;
	
//	@Before
	public void start(){
		this.server = new BackendServer();
		this.server.startServer(TCP_PORT);
	}
	
//	@After
	public void stop() throws InterruptedException{
		this.server.stopServer();
	}
	
	public void send1Task() throws Exception{
		TcpConnector client = new TcpConnector(null, InetAddress.getLocalHost(),this.TCP_PORT);
		client.startConnector();

//		this.server.sendTask();
	}
}
