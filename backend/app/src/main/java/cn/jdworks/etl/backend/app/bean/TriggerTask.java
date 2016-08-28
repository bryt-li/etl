package cn.jdworks.etl.backend.app.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("TriggerTask")
public class TriggerTask {

	private List<TaskExe> TaskExes;

	
	public List<TaskExe> getTaskExes() {
		return TaskExes;
	}

	public void setTaskExes(List<TaskExe> taskExes) {
		TaskExes = taskExes;
	}

	@Id
	private int Id;

	@Name
	@Column
	private String Name;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=256)
	private String Script;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=512)
	private String Args;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=512)
	private String Url;

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

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

}
