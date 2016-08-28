package cn.jdworks.etl.executor.biz;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConnector extends Thread implements ExceptionListener {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JmsConnector.class);

	private String connectionURI;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	
	Lock lock;
	Queue<String> queueLogs;
	private boolean hasLogToBeSent(){
		this.lock.lock();
		boolean ret = !queueLogs.isEmpty();
		this.lock.unlock();
		return ret;
	}
	private String peekLogsQueue(){
		this.lock.lock();
		String ret = this.queueLogs.peek();
		this.lock.unlock();
		return ret;
	}
	private void removeLogsQueue(){
		this.lock.lock();
		this.queueLogs.remove();
		this.lock.unlock();
	}
	public void addLogsQueue(String log){
		this.lock.lock();
		this.queueLogs.offer(log);
		this.lock.unlock();
	}
	
	private boolean isRunning;
	public synchronized boolean isRunning() {
		return isRunning;
	}
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private static final String destName = "ETL";

	public JmsConnector(String connectionURI) {
		this.connectionURI = connectionURI;
		this.isRunning = false;
		this.lock = new ReentrantLock();  
	}

	public void startConnector(){
		this.queueLogs = new LinkedList<String>();
		this.state = LSM.CONNECTING;
		this.setRunning(true);
		this.start();
	}
	public void stopConnector(){
		this.setRunning(false);
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private enum LSM{
		CONNECTING, SENDING, PENDING
	}
	private LSM state;
	
	@Override
	public void run() {
		while(this.isRunning())
		{
			switch(this.state){
			case CONNECTING:
				if(this.connect()){
					LOG.debug("jms server connected.");
					this.state = LSM.PENDING;
				}else{
					LOG.debug("jms connect failed, sleep and retry.");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case PENDING:
				if(this.hasLogToBeSent()){
					this.state = LSM.SENDING;
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case SENDING:
				String log = this.peekLogsQueue();
				if(this.sendLog(log)){
					this.removeLogsQueue();;
					if(!this.hasLogToBeSent()){
						this.state = LSM.PENDING;
					}
				}else{
					this.disconnect();
					this.state = LSM.CONNECTING;
				}
				break;
			}
		}
	}

	public void onException(JMSException e) {
		e.printStackTrace();
		disconnect();
		this.state = LSM.CONNECTING;
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

			destination = session.createQueue(destName);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			return true;
		} catch (Exception e) {
			this.disconnect();
			//e.printStackTrace();
			return false;
		}
	}

	private void disconnect() {
		try {
			if (this.producer != null)
				this.producer.close();
		} catch (Exception e) {
		} finally {
			this.producer = null;
		}
		this.destination = null;
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

	public boolean sendLog(String log) {
		try {
			TextMessage msg = this.session.createTextMessage(log);
			this.producer.send(msg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
