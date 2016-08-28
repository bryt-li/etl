package cn.jdworks.etl.backend.app.biz;

import java.util.Date;
import java.util.Timer;

import cn.jdworks.etl.backend.app.bean.TimeTask;

public class TaskTimer extends java.util.TimerTask {

	private Timer timer;
	private TimeTask task;
	private TimeTaskScheduler timeTaskScheduler;

	public TaskTimer(TimeTaskScheduler timeTaskScheduler, TimeTask task) {
		this.timeTaskScheduler = timeTaskScheduler;
		this.task = task;
	}

	public void start() throws Exception{
		Date nextExeTime = task.getNextExeTime();
		if(nextExeTime == null)
			throw new Exception("error.");
		if (task.getExeInterval() == 0) {
			if(nextExeTime!=null)
			{
				this.timer = new Timer();
				timer.schedule(this, nextExeTime);
			}
		} else {
			this.timer = new Timer();
			timer.schedule(this, nextExeTime, task.getExeInterval() * 1000);
		}
	}

	public void stop() {
		if (this.timer != null)
			this.timer.cancel();
	}

	@Override
	public void run() {
		this.timeTaskScheduler.onTimeTaskTriggered(task);
	}
}
