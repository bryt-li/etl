package cn.jdworks.etl.backend.bean;

import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Table;

@Table("TaskExe")
public class TaskExe {
	
	@Many(target = ExeLog.class, field = "Id_TaskExe")
	private List<ExeLog> ExeLogs;
	
	public List<ExeLog> getExeLogs() {
		return ExeLogs;
	}

	public void setExeLogs(List<ExeLog> exeLogs) {
		ExeLogs = exeLogs;
	}

	private Object Task;
	
	public Object getTask() {
		return Task;
	}

	public void setTask(Object task) {
		Task = task;
	}

	@Id
	private int Id;

	/**
	 * Set the value of Id.
	 **/
	public void setId(int Id) {
		this.Id = Id;
	}

	/**
	 * Get the value of Id.
	 **/
	public int getId() {
		return Id;
	}

	@Column
	private int Id_Task;

	/**
	 * Set the value of Id_Task.
	 **/
	public void setId_Task(int Id_Task) {
		this.Id_Task = Id_Task;
	}

	/**
	 * Get the value of Id_Task.
	 **/
	public int getId_Task() {
		return Id_Task;
	}
	
	@Column
	private String TaskType;

	public String getTaskType() {
		return TaskType;
	}

	public void setTaskType(String taskType) {
		TaskType = taskType;
	}

	@Column
	@ColDefine(type=ColType.VARCHAR, width=1024)
	private String Command;
	
	public String getCommand() {
		return Command;
	}

	public void setCommand(String command) {
		Command = command;
	}

	@Column
	private Date ExeTime;

	/**
	 * Set the value of ExeTime.
	 **/
	public void setExeTime(Date ExeTime) {
		this.ExeTime = ExeTime;
	}

	/**
	 * Get the value of ExeTime.
	 **/
	public Date getExeTime() {
		return ExeTime;
	}

	@Column
	private String Status;

	/**
	 * Set the value of Status.
	 **/
	public void setStatus(String Status) {
		this.Status = Status;
	}

	/**
	 * Get the value of Status.
	 **/
	public String getStatus() {
		return Status;
	}
	
	@Column
	private String ExitValue;

	public String getExitValue() {
		return ExitValue;
	}

	public void setExitValue(String ExitValue) {
		this.ExitValue = ExitValue;
	}
	
	@Column
	private Date ExitTime;

	public Date getExitTime() {
		return ExitTime;
	}

	public void setExitTime(Date ExitTime) {
		this.ExitTime = ExitTime;
	}


}
