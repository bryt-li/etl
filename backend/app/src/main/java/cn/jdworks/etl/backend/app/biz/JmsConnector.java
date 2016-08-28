package cn.jdworks.etl.backend.app.biz;

import java.util.Date;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConnector extends Thread implements ExceptionListener {

	private ExecutorManager manager;
	private String connectionURI;

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	
	private Destination dest;
	
	private static final String destName = "ETL";
		
	private MessageConsumer consumer;
	
	private boolean isRunning;
	public synchronized boolean isRunning() {
		return isRunning;
	}
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public JmsConnector(ExecutorManager manager, String connectionURI) {
		this.manager = manager;
		this.connectionURI = connectionURI;
		this.isRunning = false;
	}


	public void startConnector() {
		this.setRunning(true);
		this.start();
	}

	public void stopConnector() {
		this.setRunning(false);
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private enum LSM{
		CONNECTING,
		RECEIVING
	}
	private LSM state = LSM.CONNECTING;
	public synchronized LSM getLSMState() {
		return state;
	}
	public synchronized void setLSMState(LSM state) {
		this.state = state;
	}
	
	@Override
	public void run() {
		while (this.isRunning())
		{
			switch(this.getLSMState()){
			case CONNECTING:
				if(!this.connect())
				{
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else
					this.setLSMState(LSM.RECEIVING);
				break;
			case RECEIVING:
				try {
					Message message = this.consumer.receive(300);
					if (message instanceof TextMessage) {
						String text = ((TextMessage) message).getText();
						String[] ss = text.split(":", 5);
						String type = ss[0];
						int taskId = Integer.parseInt(ss[1]);
						UUID executorId = UUID.fromString(ss[2]);
						Date ts = new Date(Long.parseLong(ss[3]));
						String log;
						if(ss.length==5)
							log = ss[4];
						else
							log = null;
						this.manager.onTaskExeLogReceived(type, taskId, executorId, ts, log);
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.disconnect();
					this.setLSMState(LSM.CONNECTING);
				}
				break;
			}
		}
		this.disconnect();
	}

	public boolean connect() {
		// create JMS connection
		try {
			// Create a ConnectionFactory
			connectionFactory = new ActiveMQConnectionFactory(connectionURI);
			connectionFactory.setTrustAllPackages(true);

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			connection.setExceptionListener(this);

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Queue)
			dest = session.createQueue(destName);

			consumer = session.createConsumer(dest);
						
			return true;
		} catch (Exception e) {
			this.disconnect();
			e.printStackTrace();
			return false;
		}
	}

	private void disconnect() {
		try {
			if (this.consumer != null)
				this.consumer.close();
		} catch (Exception e) {
		} finally {
			this.consumer = null;
		}
		try {
			if (this.session != null)
				this.session.close();
		} catch (Exception e) {
		} finally {
			this.session = null;
		}
		try {
			if (this.connection != null)
				this.connection.close();
		} catch (Exception e) {
		} finally {
			this.connection = null;
		}
	}
		
	public void onException(JMSException e) {
		e.printStackTrace();
		disconnect();
		this.setLSMState(LSM.CONNECTING);
	}
}
