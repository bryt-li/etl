package cn.jdworks.etl.backend.app.bean;

import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("TimeTask")
public class TimeTask {

	private List<TaskExe> TaskExes;

	public List<TaskExe> getTaskExes() {
		return TaskExes;
	}

	public void setTaskExes(List<TaskExe> taskExes) {
		TaskExes = taskExes;
	}

	@Id
	private int Id;

	@Column
	private String Name;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=256)
	private String Script;
	
	@Column
	@ColDefine(type=ColType.VARCHAR, width=512)
	private String Args;
	
	@Column
	private Date FirstExeTime;

	@Column
	private int ExeInterval;
	
	@Column
	private Date LastExeTime;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=1024)
	private String Description;

	@Column
	private String Status;

	public int getId() {
		return Id;
	}

	public void setId(int Id) {
		this.Id = Id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	/**
	 * Set the value of ExeInterval.
	 **/
	public void setExeInterval(int ExeInterval) {
		this.ExeInterval = ExeInterval;
	}

	/**
	 * Get the value of ExeInterval.
	 **/
	public int getExeInterval() {
		return ExeInterval;
	}

	/**
	 * Set the value of Script.
	 **/
	public void setScript(String Script) {
		this.Script = Script;
	}

	/**
	 * Get the value of Script.
	 **/
	public String getScript() {
		return Script;
	}

	public String getArgs() {
		return Args;
	}

	public void setArgs(String args) {
		Args = args;
	}


	public void setFirstExeTime(Date FirstExeTime) {
		this.FirstExeTime = FirstExeTime;
	}
	public Date getFirstExeTime() {
		return FirstExeTime;
	}
	
	public void setLastExeTime(Date LastExeTime) {
		this.LastExeTime = LastExeTime;
	}

	public Date getLastExeTime() {
		return LastExeTime;
	}

	/**
	 * Set the value of Description.
	 **/
	public void setDescription(String Description) {
		this.Description = Description;
	}

	/**
	 * Get the value of Description.
	 **/
	public String getDescription() {
		return Description;
	}

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

	public Date getNextExeTime() {
		if(this.LastExeTime == null){
			return this.FirstExeTime;
		}else if(this.ExeInterval == 0){
			return null;
		}else{
			return new Date(this.LastExeTime.getTime()+this.ExeInterval*1000);
		}
	}
}
