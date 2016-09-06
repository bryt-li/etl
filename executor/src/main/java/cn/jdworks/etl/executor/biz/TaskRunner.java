package cn.jdworks.etl.executor.biz;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TaskRunner extends Thread {

	public synchronized boolean startRunner(int id, String cmd, TaskEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		this.id = id;
		this.command = cmd;

		if (runTask()) {
			this.setRunning(true);
			this.start();
			return true;
		} else {
			return false;
		}
	}

	public synchronized void abortTask() {
		if (isRunning()) {
			this.taskProc.destroyForcibly();
			this.setRunning(false);
			try {
				this.join();
			} catch (Throwable t) {
				LOG.debug(t);
			}
		}
	}

	private final Log LOG = Logs.getLog(this.getClass());

	private TaskEventHandler eventHandler = null;

	private void logStarted() {
		if (this.eventHandler != null)
			this.eventHandler.onTaskStarted(id, System.currentTimeMillis());
	}

	private void logStopped(int exit) {
		if (this.eventHandler != null)
			this.eventHandler.onTaskStopped(id, System.currentTimeMillis(), exit);
	}

	private void logStartFailed(String error) {
		if (this.eventHandler != null)
			this.eventHandler.onTaskStartFailed(id, System.currentTimeMillis(), error);
	}

	private void logged(String out) {
		if (this.eventHandler == null)
			return;

		long ts;
		String type, message;
		// Log format:
		// ts:type:message
		try {
			String[] ss = out.split(":");
			ts = Long.parseLong(ss[0]);
			type = ss[1];
			message = ss[2];
		} catch (Throwable t1) {
			ts = System.currentTimeMillis();
			type = "debug";
			message = out;
		}

		this.eventHandler.onTaskLogged(id, ts, type, message);
	}

	private void errorLogged(long ts, String error) {
		if (this.eventHandler != null)
			this.eventHandler.onTaskErrorLogged(id, ts, error);
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
		String line = null;
		while (isRunning()) {
			try {
				if (!stdoutReader.ready() && !stderrReader.ready()) {
					if (this.taskProc.isAlive()) {
						Thread.sleep(100);
						continue;
					} else {
						throw new Exception("task is destroyed with no more output, let's quit.");
					}
				}

				if (stderrReader.ready()) {
					line = stderrReader.readLine();
					if (line != null) {
						this.errorLogged(System.currentTimeMillis(), line);
					}
				}

				if (stdoutReader.ready()) {
					line = stdoutReader.readLine();
					if (line != null) {
						this.logged(line);
					}
				}
			} catch (Throwable t) {
				LOG.debug(t);
				this.setRunning(false);
			}
		}

		int exit;
		try {
			// We may not wait for task exit to quit, so it is possible that
			// here task is still alive
			// We give this task -1 exit value.
			// This does not mean task can not be terminated itself, only means
			// we can not wait for its termination.
			exit = this.taskProc.exitValue();
		} catch (Exception e) {
			exit = -1;
		}
		this.logStopped(exit);
	}
}
