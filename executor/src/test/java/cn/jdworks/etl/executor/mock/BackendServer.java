package cn.jdworks.etl.executor.mock;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class BackendServer extends Thread {
	private final Logger LOG = Logger.getLogger(this.getClass());

	private int port;
	private ServerSocket listenSocket;
	private boolean isRunning = false;
	private List<TcpConnector> allClients;

	private synchronized void addConnection(TcpConnector client) {
		this.allClients.add(client);
	}
	private synchronized void removeConnection(TcpConnector client) {
		this.allClients.remove(client);
	}
	
	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void startServer(int port){
		this.allClients = new ArrayList<TcpConnector>();
		this.port = port;
		this.setRunning(true);
		this.start();
	}
	
	public void stopServer() throws InterruptedException{
		this.setRunning(false);
		this.join();
	}
	
	@Override
	public void run() {
		try {
			this.listenSocket = new ServerSocket(this.port);
			this.listenSocket.setSoTimeout(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (this.isRunning()) {
			try {
				Socket socket = this.listenSocket.accept();
				TcpConnector client = new TcpConnector(socket);
				if (client.startConnector()){
					this.addConnection(client);
					LOG.debug("executor connected.");
				}
			} catch (Exception e) {
				if (e instanceof SocketTimeoutException)
					continue;
				e.printStackTrace();
			}
		}
	}
}
