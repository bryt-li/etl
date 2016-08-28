package cn.jdworks.etl.executor.biz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class App {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(App.class);

	private static String jmsLoggerAddr;
	private static InetAddress tcpServerAddr;
	private static int tcpServerPort;

	private JmsConnector jmsConnector;
	private TcpConnector tcpConnector;
	
	private List<TaskRuntime> runningTasks;
	private synchronized void addRunningTask(TaskRuntime task){
		this.runningTasks.add(task);
	}

	public static void main(String[] args) throws Exception {
		readConfiguration();

		App app = new App();
		if (!app.start())
			return;

		LOG.info("Executor is running...press any keys to EXIT.");
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			stdin.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

		app.stop();
		LOG.info("Executor stopped.");
	}

	private static void readConfiguration() {
		try {
			InputStream inputStream = App.class.getResourceAsStream("/server.properties");
			Properties p = new Properties();
			p.load(inputStream);
			jmsLoggerAddr = p.getProperty("jmsLoggerAddr");
			String[] addr = p.getProperty("tcpServerAddr").split(":");
			tcpServerAddr = InetAddress.getByName(addr[0]);
			tcpServerPort = Integer.parseInt(addr[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		
	}

	private boolean start() {
		try {
			jmsConnector = new JmsConnector(jmsLoggerAddr);
			jmsConnector.startConnector();

			tcpConnector = new TcpConnector(this, tcpServerAddr, tcpServerPort);
			tcpConnector.startConnector();
			
			this.runningTasks = new ArrayList<TaskRuntime>();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private synchronized void stop() {
		for(TaskRuntime task : this.runningTasks){
			task.abortTask();
		}
		tcpConnector.stopConnector();
		jmsConnector.stopConnector();
	}

	public void executeTask(int id, String cmd) {
		TaskRuntime task = new TaskRuntime(this, id, cmd);
		String error = task.runTask();
		if (error == null)
		{
			this.addLogsToJmsSendingQueue("START", id, (new Date()).getTime(), error);
			this.addRunningTask(task);
		}else{
			this.addLogsToJmsSendingQueue("END", id, (new Date()).getTime(), error);
		}
	}

	public void addLogsToJmsSendingQueue(String type, int taskId, long timestamp, String log) {
		String exLog = type+":"+taskId+":"+this.tcpConnector.getUuid()+":"+timestamp+":"+log;
		this.jmsConnector.addLogsQueue(exLog);
	}
}
