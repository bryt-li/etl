package cn.jdworks.etl.executor.biz;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TaskManagerTest {

	private static TaskManager taskManager;

	@BeforeClass
	public static void initClass() throws Exception {
		taskManager = new TaskManager();
		taskManager.startManager();
	}

	@AfterClass
	public static void destroyClass() throws Exception {
		taskManager.shutdown();
	}

	@Before
	public void before() {

	}

	@After
	public void after() {

	}

	@Test
	public void testRunTasks() throws Exception {
		for (int i = 1; i <= 100; i++) {
			taskManager.runTask(i, String.format("ping -c %d bing.com", i));
		}
		Thread.sleep(5000);
	}
}
