package cn.jdworks.etl.executor.biz;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

public class MockTaskEventHandler implements TaskEventHandler {

	private String[] messages;
	private int id;

	private List<String> logs = new ArrayList<String>();
	private List<String> errs = new ArrayList<String>();
	private String startFail = null;
	private Integer exitNo = null;
	private boolean started = false;

	public void assertTaskRunPing(){
		Assert.assertTrue(started);
		Assert.assertTrue(logs.size()>0);
		Assert.assertNotNull(exitNo);
	}
	
	public void assertTaskStartFailed() {
		Assert.assertNotNull(startFail);
	}

	public void assertTaskNotExist() {
		Assert.assertTrue(started);
		Assert.assertTrue(errs.size()>0);
		Assert.assertNotNull(exitNo);
	}
	
	public void assertTaskRunPy() {
		Assert.assertTrue(started);
		Assert.assertTrue(logs.size()>0);
		for(int i=0;i<logs.size();i++){
			Assert.assertEquals(logs.get(i),messages[i]);
		}
		Assert.assertTrue(errs.size()>0);
		Assert.assertNotNull(exitNo);
	}
	
	public void assertTaskRunPyThenAbort() {
		Assert.assertTrue(started);
		Assert.assertTrue(logs.size()>0);
		Assert.assertNotNull(exitNo);
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
		startFail = message;
	}

	public void onTaskLogged(int id, long ts, String type, String message) {
		System.out.println("LOG:"+id + ":" + ts + ":" + type + ":" + message);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		Assert.assertNotNull(type);
		logs.add(message);
	}

	public void onTaskErrorLogged(int id, long ts, String error) {
		System.out.println("ERR:"+id + ":" + ts + ":" + error);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		Assert.assertNotNull(error);
		errs.add(error);
	}

	public void onTaskStopped(int id, long ts, int exit) {
		System.out.println("STOP:"+id + ":" + ts + ":ExitCode:" + exit);
		Assert.assertEquals(this.id, id);
		Assert.assertTrue(ts > 0);
		exitNo = exit;
		exit();
	}

	
}
