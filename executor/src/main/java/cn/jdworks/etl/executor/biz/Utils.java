package cn.jdworks.etl.executor.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class Utils {
	public static byte[] getTaskToBeSentBuffer(int id, String command) {
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

	public static  byte[] getUUIDEchoBuffer(UUID uuid) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(uuid);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static  int getUUIDBufferSize() {
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

	public static  int getTaskEchoBufferSize() {
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

	public static  UUID parseUUID(byte[] buffer) {
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

	public static  int parseTaskEchoId(byte[] buffer) {
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
