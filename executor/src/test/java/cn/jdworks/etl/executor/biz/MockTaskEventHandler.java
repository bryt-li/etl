package cn.jdworks.etl.executor.biz;

public class MockTaskEventHandler implements TaskEventHandler {

	protected synchronized void waitForExit() throws Exception {
		this.wait();
	}

	protected synchronized void exit() {
		this.notify();
	}

	public void onTaskStarted(int id, long ts) {
		// TODO Auto-generated method stub

	}

	public void onTaskStartFailed(int id, long ts, String message) {
		// TODO Auto-generated method stub

	}

	public void onTaskStopped(int id, long ts, int exit) {
		// TODO Auto-generated method stub
		exit();
	}

	public void onTaskLogged(int id, long ts, String type, String message) {
		// TODO Auto-generated method stub

	}

}
