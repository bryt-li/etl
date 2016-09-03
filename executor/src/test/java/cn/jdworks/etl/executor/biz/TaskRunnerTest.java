package cn.jdworks.etl.executor.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TaskRunnerTest {

	private final static Log LOG = Logs.getLog(TaskRunnerTest.class);

	private final static String TEST_SCRIPT = "/tmp/foo.py";

	@BeforeClass
	public static void initClass() throws Exception {
		//write test script
		try {
			File file = new File(TEST_SCRIPT);
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			w.write("print (\"hello,world!\")\n");
			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		//delete test script
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	public void testStartSampleTask() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler();
		int id = 0;
		String cmd = "python " + TEST_SCRIPT;
		TaskRunner runner = new TaskRunner();
		runner.startRunner(id, cmd, handler);

		handler.waitForExit();
		LOG.info("Task destroyed: " + cmd);
	}

	@Test
	public void testForceTaskExit() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler();
		int id = 0;
		String cmd = "python " + TEST_SCRIPT;
		TaskRunner runner = new TaskRunner();
		runner.startRunner(id, cmd, handler);

		Thread.sleep(1000);
		runner.abortTask();

		handler.waitForExit();
		LOG.info("Task destroyed: " + cmd);
	}

}
