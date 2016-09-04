package cn.jdworks.etl.executor.biz;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(singleton = true)
public class TaskManager implements TaskEventHandler{
	
	public void startManager(){
	
	}
	
	public void shutdown(){
		
	}

	public void onTaskStarted(int id, long ts) {
		// TODO Auto-generated method stub
		
	}

	public void onTaskStartFailed(int id, long ts, String message) {
		// TODO Auto-generated method stub
		
	}

	public void onTaskStopped(int id, long ts, int exit) {
		// TODO Auto-generated method stub
		
	}

	public void onTaskErrorLogged(int id, long ts, String error) {
		// TODO Auto-generated method stub
		
	}

	public void onTaskLogged(int id, long ts, String type, String message) {
		// TODO Auto-generated method stub
		
	}

}
