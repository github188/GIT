package com.royalstone.pos.journal;

import java.io.Serializable;

/**
 * 流水日志,它负责记录流水的状态,如新建,上传等.
 * @author liangxinbiao
 */
public class JournalLog implements Serializable{
	
	public final static int CREATE=0;
	public final static int BEGIN_UPLOAD=1;
	public final static int UPLOADING=2;
	public final static int UPLOADED=3;
	public final static int UPLOAD_FAIL=4;
	
	private String journalName;
	private String createTime;
	private String uploadTime;
	private int status;
	

	/**
	 * @return 流水的生成时间
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @return 流水的名字
	 */
	public String getJournalName() {
		return journalName;
	}

	/**
	 * @return 流水的状态
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return 流水的上传时间
	 */
	public String getUploadTime() {
		return uploadTime;
	}

	/**
	 * @param string 流水的生成时间
	 */
	public void setCreateTime(String string) {
		createTime = string;
	}

	/**
	 * @param string 流水的名字
	 */
	public void setJournalName(String string) {
		journalName = string;
	}

	/**
	 * @param i 流水的状态
	 */
	public void setStatus(int i) {
		status = i;
	}

	/**
	 * @param string 流水的上传时间
	 */
	public void setUploadTime(String string) {
		uploadTime = string;
	}

}
