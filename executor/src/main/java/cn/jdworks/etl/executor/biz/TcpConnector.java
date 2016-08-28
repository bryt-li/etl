package cn.jdworks.etl.executor.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class TcpConnector extends Thread {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TcpConnector.class);

	private App app;
	private InetSocketAddress socketAddr;
	private Socket socket;

	private boolean isRunning;

	private synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	// id of this executor
	private UUID uuid;

	public UUID getUuid() {
		return this.uuid;
	}

	public TcpConnector(App app, InetAddress addr, int port) {
		this.app = app;
		this.socketAddr = new InetSocketAddress(addr, port);
		this.uuid = UUID.randomUUID();
		this.setRunning(false);
	}

	// receive variables
	private static int TASKHEADER_BUFFER_SIZE;
	private static int UUIDECHO_BUFFER_SIZE;

	public void startConnector() throws Exception {
		UUIDECHO_BUFFER_SIZE = this.getUUIDEchoBufferSize();
		TASKHEADER_BUFFER_SIZE = this.getTaskHeaderBufferSize();
		LOG.debug("uuid echo buffer size: " + UUIDECHO_BUFFER_SIZE + "  task header buffer size: "
				+ TASKHEADER_BUFFER_SIZE);
		this.setRunning(true);
		this.start();
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

	public synchronized boolean isConnected() {
		return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
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

	@Override
	public void run() {
		int send_uuid_timer = 15000;
		while (this.isRunning()) {
			if (!this.isConnected()) {
				try {
					this.socket = new Socket();
					this.socket.setKeepAlive(true);
					this.socket.connect(socketAddr, 1000);
					LOG.debug("tcp server connected.");
				} catch (Exception e) {
					LOG.debug("tcp server connect failed, sleep and retry in 3s.");
					// e.printStackTrace();
					// retry in 3 seconds
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				}
			}

			try {
				int timeout = 100;
				if (this.receiveTask(timeout) == false) {
					send_uuid_timer += timeout;
					if (send_uuid_timer >= 15000) {
						send_uuid_timer = 0;
						this.sendUUID();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				this.close();
				// when error, start over and over again...
				send_uuid_timer = 15000;
				continue;
			}
		}
	}

	// receive TASK and send TASK-ECHO
	private boolean receiveTask(int timeout) throws Exception {
		byte[] buffer = this.recv(TASKHEADER_BUFFER_SIZE, timeout);
		if (buffer == null)
			return false;

		// TASKHEADER received
		int taskExeId = parseTaskHeaderExeId(buffer);
		int bodyLen = parseTaskHeaderBodyLength(buffer);
		buffer = null;
		buffer = this.recv(bodyLen, 5000);
		if (buffer == null)
			throw new Exception("task not received in 5s.");

		String cmd = parseTaskBody(buffer);
		LOG.debug("task body received, command: " + cmd);

		app.executeTask(taskExeId, cmd);

		buffer = this.getTaskEchoBuffer(taskExeId);
		LOG.debug("send uuid-echo buffer size: " + buffer.length);
		OutputStream out = this.socket.getOutputStream();
		out.write(buffer);
		LOG.debug("task echo sent: " + taskExeId);
		return true;
	}

	// send uuid and receive uuid-echo
	private void sendUUID() throws Exception {
		byte[] buffer;
		OutputStream out = this.socket.getOutputStream();
		buffer = this.getUUIDBuffer();
		out.write(buffer);
		LOG.debug("uuid sent. id: " + this.uuid);

		buffer = null;
		buffer = recv(UUIDECHO_BUFFER_SIZE, 5000);
		if (buffer == null) {
			throw new Exception("no uuid echo received in 5s.");
		}
		// uuid-echo received
		UUID echoId = this.parseUUIDEcho(buffer);
		if (!echoId.equals(uuid)) {
			throw new Exception("received wrong uuid in echo: " + echoId);
		}
		LOG.debug("uuid echo received");
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
			throw new Exception("recv() error. received: " + received + " expected length:" + len);

		return buffer;
	}

	private byte[] getTaskEchoBuffer(int taskExeId) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			oos.writeInt(taskExeId);
			byte[] buffer = baos.toByteArray();
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private byte[] getUUIDBuffer() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this.getUuid());
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private int getUUIDEchoBufferSize() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this.getUuid());
			byte[] bytes = baos.toByteArray();
			return bytes.length;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// task header with 2 integer: int(taskId)-int(cmdLength)
	private int getTaskHeaderBufferSize() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			oos.writeInt(999);
			byte[] bytes = baos.toByteArray();
			return bytes.length * 2;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private UUID parseUUIDEcho(byte[] buffer) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(bais);
		UUID uuid = (UUID) ois.readObject();
		bais.close();
		ois.close();
		return uuid;
	}

	private int parseTaskHeaderExeId(byte[] buffer) throws Exception {
		byte[] b = new byte[TASKHEADER_BUFFER_SIZE / 2];
		System.arraycopy(buffer, 0, b, 0, b.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		DataInputStream ois = new DataInputStream(bais);
		int id = ois.readInt();
		bais.close();
		ois.close();
		return id;
	}

	private int parseTaskHeaderBodyLength(byte[] buffer) throws Exception {
		byte[] b = new byte[TASKHEADER_BUFFER_SIZE / 2];
		System.arraycopy(buffer, TASKHEADER_BUFFER_SIZE / 2, b, 0, b.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		DataInputStream ois = new DataInputStream(bais);
		int length = ois.readInt();
		bais.close();
		ois.close();
		if (length <= 0) {
			throw new Exception("error command length: " + length);
		}
		return length;
	}

	private String parseTaskBody(byte[] buffer) throws Exception {
		String cmd = new String(buffer,"UTF-8");
		if (cmd.length() <= 0) {
			throw new Exception("received error command.");
		}
		return cmd;
	}
}
