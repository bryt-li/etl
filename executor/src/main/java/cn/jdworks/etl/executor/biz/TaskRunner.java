package cn.jdworks.etl.executor.biz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TaskRunner extends Thread {
	public void startRunner(int id, String cmd, TaskEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		this.id = id;
		this.command = cmd;

		if (runTask()){
			this.setRunning(true);
			this.start();
		}
	}
	
	public void abortTask() {
		this.taskProc.destroyForcibly();
		this.setRunning(false);
		try {
			this.join();
		} catch (Throwable t) {
			LOG.debug(t);
		}
	}
	
	private final Log LOG = Logs.getLog(this.getClass());

	private TaskEventHandler eventHandler = null;
	private void logStarted(){
		if(this.eventHandler !=null)
			this.eventHandler.onTaskStarted(id, System.currentTimeMillis());
	}
	private void logStopped(int exit){
		if(this.eventHandler !=null)
			this.eventHandler.onTaskStopped(id, System.currentTimeMillis(), exit);
	}
	private void logStartFailed(String error){
		if(this.eventHandler !=null)
			this.eventHandler.onTaskStartFailed(id, System.currentTimeMillis(), error);
	}
	private void logged(long ts, String type, String message){
		if(this.eventHandler !=null)
			this.eventHandler.onTaskLogged(id, ts, type, message);
	}


	private Process taskProc;
	private BufferedReader stdoutReader;
	private BufferedReader stderrReader;
	private boolean isRunning = false;
	private synchronized boolean isRunning() {
		return isRunning;
	}
	private synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private int id;
	private String command;


	private boolean runTask() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(command.split(" "));
			this.taskProc = pb.start();
			this.start();

			this.stdoutReader = new BufferedReader(new InputStreamReader(taskProc.getInputStream()));
			this.stderrReader = new BufferedReader(new InputStreamReader(taskProc.getErrorStream()));

			this.logStarted();
			return true;
		} catch (Throwable t) {
			this.logStartFailed(t.getMessage());
			return false;
		}
	}

	// todo：监听标准输出
	public void run() {
		boolean isReady;
		String line = null;
		String[] ss;
		long ts;
		String type;
		String message;
		while (isRunning()) {
			try {
				isReady = false;

				if (stdoutReader.ready()) {
					isReady = true;
					line = stdoutReader.readLine();
					if(line==null)
						throw new Exception();
					//Log format: 
					//    ts:type:message
					try {
						ss = line.split(":");
						ts = Long.parseLong(ss[0]);
						type = ss[1];
						message = ss[2];
					} catch (Throwable t1) {
						ts = System.currentTimeMillis();
						type="debug";
						message = line;
					}
					this.logged(ts, type, message);
				}
				if (stderrReader.ready()) {
					isReady = true;
					line = stderrReader.readLine();
					if(line==null)
						throw new Exception();
					this.logged(System.currentTimeMillis(), "error", line);
				}
				if (!isReady)
					Thread.sleep(100);
			} catch (Throwable t) {
				this.setRunning(false);
			}
		}
		int exit = this.taskProc.exitValue();
		this.logStopped(exit);
	}
}
