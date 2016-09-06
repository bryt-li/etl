package cn.jdworks.etl.executor.biz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import junit.framework.Assert;

public class TaskRunnerTest {

	private final static Log LOG = Logs.getLog(TaskRunnerTest.class);

	private final static String TEST_SCRIPT = "/tmp/foo.py";

	private final static int TASK_ID = 123;
	private final static String[] MESSAGES = { UUID.randomUUID().toString(), UUID.randomUUID().toString(),
			UUID.randomUUID().toString() };

	@BeforeClass
	public static void initClass() throws Exception {
		// write test script
		String type;
		try {
			File file = new File(TEST_SCRIPT);
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			w.write("from datetime import datetime\n");
			w.write("import time\n");
			w.write("import sys\n");
			type = "info";
			for (String msg : MESSAGES) {
				w.write("ts = time.mktime(datetime.now().timetuple())\n");
				w.write("print (\"%d:" + type + ":" + msg + "\" % ts)\n");
				w.write("sys.stdout.flush()\n");//this line is important for python
				w.write("time.sleep(1)\n");
			}

			w.write("raise ValueError, 'invalid argument'\n");

			w.flush();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		// delete test script
		File file = new File(TEST_SCRIPT);
		if (file.exists())
			file.delete();
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	public void testStartTaskFailed() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler(TASK_ID, MESSAGES);
		String cmd = "do_not_exist";
		TaskRunner runner = new TaskRunner();
		Assert.assertFalse(runner.startRunner(TASK_ID, cmd, handler));

		handler.assertTaskStartFailed();
	}

	@Test
	public void testStartTaskNotExist() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler(TASK_ID, MESSAGES);
		String cmd = "python " + "do_not_exist.py";
		TaskRunner runner = new TaskRunner();
		Assert.assertTrue(runner.startRunner(TASK_ID, cmd, handler));
		handler.waitForExit();
		LOG.info("Task destroyed: " + cmd);

		handler.assertTaskNotExist();
	}

	@Test
	public void testStartTaskRun() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler(TASK_ID, MESSAGES);
		String cmd = "python " + TEST_SCRIPT;
		TaskRunner runner = new TaskRunner();
		Assert.assertTrue(runner.startRunner(TASK_ID, cmd, handler));

		handler.waitForExit();
		LOG.info("Task destroyed: " + cmd);
		handler.assertTaskRunThenError();
	}

	@Test
	public void testStartTaskRunThenAbort() throws Exception {
		MockTaskEventHandler handler = new MockTaskEventHandler(TASK_ID, MESSAGES);
		String cmd = "python " + TEST_SCRIPT;
		final TaskRunner runner = new TaskRunner();
		Assert.assertTrue(runner.startRunner(TASK_ID, cmd, handler));

		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			public void run() {
				LOG.debug("Abort task.");
				runner.abortTask();
			}
		};
		timer.schedule(task, 1000);
		handler.waitForExit();
		LOG.info("Task destroyed: " + cmd);

		handler.assertTaskRunThenAbort();
	}
}
