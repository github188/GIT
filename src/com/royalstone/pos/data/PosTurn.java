package com.royalstone.pos.data;

import java.io.Serializable;
import java.util.Date;

import com.royalstone.pos.util.Formatter;

/**
 * @author liangxinbiao
 *
 */
public class PosTurn implements Serializable {

	private String posid;
	private Date workdate;
	private int shiftID;
	private Date startTime;
	private Date endTime;
	private int stat;
	private boolean isStartOffLine;
	private boolean isEndOffLine;

	/**
	 * @return 班结时间
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return 是否在脱机状态下完成班结的（0表示联机，1表示脱机）
	 */
	public boolean isEndOffLine() {
		return isEndOffLine;
	}

	/**
	 * @return 是否在脱机状态下开班的（0表示联机，1表示脱机）
	 */
	public boolean isStartOffLine() {
		return isStartOffLine;
	}

	/**
	 * @return POS机号
	 */
	public String getPosid() {
		return posid;
	}

	/**
	 * @return 班次号
	 */
	public int getShiftID() {
		return shiftID;
	}

	/**
	 * @return 开班时间
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return 班次状态（0表示本班次还没做班结，1表示本班次已经完成）
	 */
	public int getStat() {
		return stat;
	}

	/**
	 * @return 工作日期
	 */
	public Date getWorkdate() {
		return workdate;
	}

	/**
	 * @param string 班结时间
	 */
	public void setEndTime(Date value) {
		endTime = value;
	}

	/**
	 * @param string 是否在脱机状态下完成班结的（0表示联机，1表示脱机）
	 */
	public void setEndOffLine(boolean value) {
		isEndOffLine = value;
	}

	/**
	 * @param string 是否在脱机状态下开班的（0表示联机，1表示脱机）
	 */
	public void setStartOffLine(boolean value) {
		isStartOffLine = value;
	}

	/**
	 * @param string POS机号
	 */
	public void setPosid(String string) {
		posid = string;
	}

	/**
	 * @param string 班次号
	 */
	public void setShiftID(int value) {
		shiftID = value;
	}

	/**
	 * @param string 开班时间
	 */
	public void setStartTime(Date value) {
		startTime = value;
	}

	/**
	 * @param string 班次状态（0表示本班次还没做班结，1表示本班次已经完成）
	 */
	public void setStat(int value) {
		stat = value;
	}

	/**
	 * @param string 工作日期
	 */
	public void setWorkdate(Date value) {
		workdate = value;
	}

	public String toString() {
		return "posid="
			+ posid
			+ ",workdate="
			+ Formatter.getDate(workdate)
			+ ",shiftID="
			+ shiftID
			+ ",startTime="
			+ Formatter.getDateFile(startTime)
			+ ",endTime="
			+ Formatter.getDateFile(endTime)
			+ ",stat="
			+ stat
			+ ",isStartOffLine="
			+ isStartOffLine
			+ ",isEndOffLine="
			+ isEndOffLine;
	}

}
