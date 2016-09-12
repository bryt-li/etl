package cn.jdworks.etl.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(obj);
			return b.toByteArray();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public static Object deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			ObjectInputStream o = new ObjectInputStream(b);
			return o.readObject();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

}