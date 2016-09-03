package cn.jdworks.etl.executor.biz;

public interface TaskEventHandler {
	void onTaskStarted(int id, long ts);
	void onTaskStartFailed(int id, long ts, String message);
	void onTaskStopped(int id, long ts, int exit);
	void onTaskLogged(int id, long ts, String type, String message);
}
