package cn.jdworks.etl.executor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.*;

public class ConfigTest {

	@Test
	public void StringTest(){
		String foo = "";
		foo = foo + "foo";
		Assert.assertEquals(foo, "foo");
	}
	
	@Test
	public void ReadConfig() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("server.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ip:" + p.getProperty("ip") + ",port:" + p.getProperty("port"));
	}
}
