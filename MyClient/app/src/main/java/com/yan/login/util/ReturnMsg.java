package com.yan.login.util;


import java.io.Serializable;
import java.util.List;

/**
 * 自定义输出格式
 * yjx
 */

public class ReturnMsg implements Serializable {
	private String status;
	private String statusMsg;
	private List<Department> msg;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public List<Department> getMsg() {
		return msg;
	}

	public void setMsg(List<Department> msg) {
		this.msg = msg;
	}
}
