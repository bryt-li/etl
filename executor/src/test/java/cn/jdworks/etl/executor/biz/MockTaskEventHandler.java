package cn.jdworks.etl.executor.biz;

import junit.framework.Assert;

public class MockTaskEventHandler implements TaskEventHandler {

	private String[] messages;
	private int id;

	private boolean started = false;
	private boolean stopped = false;
	private boolean logged = false;
	private boolean errorLogged = false;
	private boolean startFailed = false;

	public void assertTaskStartFailed() {
		Assert.assertTrue(startFailed);
	}

	public void assertTaskNotExist() {
		Assert.assertTrue(started);
		Assert.assertTrue(stopped);
	}
	
	public void assertTaskRunThenError() {
		Assert.assertTrue(started);
		Assert.assertTrue(logged);
		Assert.assertTrue(errorLogged);
		Assert.assertTrue(stopped);
	}
	
	public void assertTaskRunThenAbort() {
		Assert.assertTrue(started);
		Assert.assertTrue(logged);
		Assert.assertTrue(stopped);
	}


	public MockTaskEventHandler(int id, String[] messages) {
		this.id = id;
		this.messages = messages;
	}

	protected synchronized void waitForExit() throws Exception {
		this.wait();
	}

	protected synchronized void exit() {
		this.notify();
	}

	public void onTaskStarted(int id, long ts) {
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		this.started = true;
	}

	public void onTaskStartFailed(int id, long ts, String message) {
		System.out.println("FAIL:"+id + ":" + ts + ":" + message);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		Assert.assertNotNull(message);
		this.startFailed = true;
	}

	public void onTaskStopped(int id, long ts, int exit) {
		System.out.println("STOP:"+id + ":" + ts + ":ExitCode:" + exit);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		this.stopped = true;
		exit();
	}

	private int index = 0;

	public void onTaskLogged(int id, long ts, String type, String message) {
		System.out.println("LOG:"+id + ":" + ts + ":" + type + ":" + message);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		Assert.assertNotNull(type);
		Assert.assertEquals(message, messages[index++]);
		this.logged = true;
	}

	public void onTaskErrorLogged(int id, long ts, String error) {
		System.out.println("ERR:"+id + ":" + ts + ":" + error);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		Assert.assertNotNull(error);
		this.errorLogged = true;
	}

}
