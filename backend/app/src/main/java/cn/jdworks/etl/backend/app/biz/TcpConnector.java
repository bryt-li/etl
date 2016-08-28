package cn.jdworks.etl.backend.app.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TcpConnector extends Thread {
	private final Log LOG = Logs.getLog(this.getClass());

	private ExecutorManager manager;
	private Socket socket;
	Lock lock;

	private boolean isRunning;

	private synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	// id of this executor connection
	private UUID uuid;

	public synchronized UUID getUuid() {
		return uuid;
	}

	public synchronized void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	// receive variables
	private static int UUID_BUFFER_SIZE;
	private static int TASKECHO_BUFFER_SIZE;

	public TcpConnector(Socket socket) {
		this.lock = new ReentrantLock();  
		this.socket = socket;
		this.uuid = null;
		this.setRunning(false);
	}

	public boolean startConnector(ExecutorManager manager) {
		try {
			UUID_BUFFER_SIZE = this.getUUIDBufferSize();
			TASKECHO_BUFFER_SIZE = this.getTaskEchoBufferSize();
			LOG.debug("uuid recv buffer size: " + UUID_BUFFER_SIZE + " task echo recv buffer size: "
					+ TASKECHO_BUFFER_SIZE);

			this.socket.setKeepAlive(true);
			this.setRunning(true);
			this.start();
			this.manager = manager;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void stopConnector() {
		this.setRunning(false);
		this.close();
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (this.isRunning()) {
			try {
				receiveUUID();
			} catch (Exception e) {
				e.printStackTrace();
				this.setRunning(false);
				this.close();
				this.manager.removeExecutor(this);
				break;
			}
		}
	}

	// close socket
	public void close() {
		try {
			this.socket.close();
			this.socket = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// receive UUID and send UUID-ECHO
	private void receiveUUID() throws Exception {
		this.lock.lock();
		byte[] buffer = this.recv(UUID_BUFFER_SIZE, 100);
		if (buffer == null){
			this.lock.unlock();
			return;
		}

		// UUID received
		UUID id = parseUUID(buffer);
		if (id == null) {
			this.lock.unlock();
			throw new Exception("received wrong uuid.");
		}
		this.setUuid(id);
		LOG.debug("UUID received: " + id);

		buffer = this.getUUIDEchoBuffer();
		LOG.debug("send uuid-echo buffer size: " + buffer.length);
		OutputStream out = this.socket.getOutputStream();
		out.write(buffer);
		LOG.debug("UUID echo sent: " + this.uuid);
		this.lock.unlock();
	}

	// send task and receive task-echo
	public synchronized boolean sendTask(int id, String command) {
		try {
			this.lock.lock();
			byte[] buffer;
			OutputStream out = this.socket.getOutputStream();
			buffer = this.getTaskToBeSentBuffer(id, command);
			out.write(buffer);
			LOG.debug("task sent. id: " + id + " command: " + command);

			buffer = recv(TASKECHO_BUFFER_SIZE, 5000);
			if (buffer == null) {
				this.lock.unlock();
				throw new Exception("no task echo received in 5s.");
			}
			// task-echo received
			int echoId = parseTaskEchoId(buffer);
			if (echoId != id) {
				this.lock.unlock();
				throw new Exception("received wrong task id in echo. received id: " + echoId + "expected: "+id);
			}
			LOG.debug("task echo received");
			this.lock.unlock();
			return true;
		} catch (Exception e) {
			// client or server connection broken or something wrong, anyway
			// shutdown the connection
			e.printStackTrace();
			this.setRunning(false);
			this.close();
			this.lock.unlock();
			return false;
		}
	}

	private byte[] recv(int buffer_size, int timeout) throws Exception {
		byte[] buffer = new byte[buffer_size];
		int off = 0;
		int len = buffer_size;
		int received = 0;
		int recv = 0;
		InputStream in = this.socket.getInputStream();
		this.socket.setSoTimeout(timeout);

		try {
			recv = in.read(buffer, off, len);
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				return null;
			} else {
				throw e;
			}
		}
		if (recv == 0)
			throw new Exception("recv() error.");

		received += recv;
		if (recv > 0 && recv < len) {
			// 如果收到了数据，那么余下数据必须在6秒内收到，否则认为出错。
			try {
				this.socket.setSoTimeout(6000);
				in.read(buffer, recv, len - recv);
			} catch (Exception e) {
				if (e instanceof SocketTimeoutException) {
					throw new Exception("network error or something wrong.");
				} else {
					throw e;
				}
			}
			received += recv;
		}
		if (received != len)
			throw new Exception("recv() error.");

		return buffer;
	}

	private byte[] getTaskToBeSentBuffer(int id, String command) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			oos.writeInt(id);
			byte[] b1 = baos.toByteArray();

			byte[] b3 = command.getBytes();

			baos = new ByteArrayOutputStream();
			oos = new DataOutputStream(baos);
			oos.writeInt(b3.length);
			byte[] b2 = baos.toByteArray();

			// merge b1-b2-b3
			byte[] buffer = new byte[b1.length + b2.length + b3.length];
			System.arraycopy(b1, 0, buffer, 0, b1.length);
			System.arraycopy(b2, 0, buffer, b1.length, b2.length);
			System.arraycopy(b3, 0, buffer, b1.length + b2.length, b3.length);
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private byte[] getUUIDEchoBuffer() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this.uuid);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private int getUUIDBufferSize() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(UUID.randomUUID());
			byte[] bytes = baos.toByteArray();
			return bytes.length;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private int getTaskEchoBufferSize() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			oos.writeInt(999);
			byte[] bytes = baos.toByteArray();
			return bytes.length;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private UUID parseUUID(byte[] buffer) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = new ObjectInputStream(bais);
			UUID uuid = (UUID) ois.readObject();
			bais.close();
			ois.close();
			return uuid;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private int parseTaskEchoId(byte[] buffer) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			DataInputStream ois = new DataInputStream(bais);
			int id = ois.readInt();
			bais.close();
			ois.close();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
	}

}
