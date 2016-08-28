package cn.jdworks.etl.backend.app.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("ExeLog")
public class ExeLog {
	
	@One(target = TaskExe.class, field = "Id_TaskExe")
    private TaskExe Task;
	
	public TaskExe getTask() {
		return Task;
	}

	public void setTask(TaskExe task) {
		Task = task;
	}

	@Id
	private int Id;

	@Column
	private int Id_TaskExe;

	@Column
	private Date Timestamp;

	@Column
	private String Type;

	@Column
	@ColDefine(type=ColType.VARCHAR, width=2048)
	private String Log;

	public void setId(int Id) {
		this.Id = Id;
	}

	public int getId() {
		return Id;
	}

	public void setId_TaskExe(int Id_TaskExe) {
		this.Id_TaskExe = Id_TaskExe;
	}

	public int getId_TaskExe() {
		return Id_TaskExe;
	}

	public void setLog(String Log) {
		this.Log = Log;
	}

	public String getLog() {
		return Log;
	}

	public Date getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(Date timestamp) {
		Timestamp = timestamp;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

}
