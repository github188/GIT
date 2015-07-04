package com.royalstone.pos.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.util.FileUtil;
import com.royalstone.pos.util.Formatter;

/**
 * @author liangxinbiao
 */
public class ShopClock implements Serializable {

	private static ShopClock instance;
	public static String file4shopClock =
		"work" + File.separator + "ShopClock.dat";

	private Date workDate;
	private int shiftID;

	private ShopClock() {
		GregorianCalendar calender = new GregorianCalendar();
		calender.set(Calendar.YEAR, 1970);
		calender.set(Calendar.MONTH, 0);
		calender.set(Calendar.DAY_OF_MONTH, 1);

		workDate = calender.getTime();
		shiftID = 1;
	}

	public ShopClock(Date workDate, int shiftID) {
		this.workDate = workDate;
		this.shiftID = shiftID;
	}

	public static ShopClock getInstance() {
		if (instance == null) {
			instance = load(false);
		}
		return instance;
	}
	
	public static ShopClock getInstance(boolean isReset) {
		if (instance == null) {
			instance = load(isReset);
		}
		return instance;
	}


	public static void setInstance(ShopClock value) {
		instance = value;
	}

	/**
	 * @return 班次号
	 */
	public int getShiftID() {
		return shiftID;
	}

	/**
	 * @return 工作日期
	 */
	public Date getWorkDate() {
		return workDate;
	}

	/**
	 * @param string 班次号
	 */
	public void setShiftID(int value) {
		shiftID = value;
	}

	/**
	 * @param string 工作日期
	 */
	public void setWorkDate(Date value) {
		workDate = value;
	}

	/**
	 * 将ShopClock写到文件里 
	 */
	public void dump() {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(file4shopClock));
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static ShopClock load(boolean isReset) {
		ShopClock result = null;
		FileInputStream fin = null;
		ObjectInputStream in = null;
		try {
			fin = new FileInputStream(file4shopClock);
			in = new ObjectInputStream(fin);
			result = (ShopClock) in.readObject();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			
			result = new ShopClock();
			
		
			if(isReset){
				Date date=new Date();
				GregorianCalendar calender = new GregorianCalendar();
				calender.setTime(date);
				
				Date workDate;
				int shiftID;
		
				int hour=calender.get(Calendar.HOUR_OF_DAY);
		
				if(hour>=7 && hour<15){
					workDate=date;
					shiftID=1;		
				}else if(hour>=15 && hour<23){
					workDate=date;
					shiftID=2;
				}else if(hour>=23){
					workDate=date;
					shiftID=3;
				}else{
					calender.set(
						GregorianCalendar.DATE,
					calender.get(GregorianCalendar.DATE) -1);
					workDate=calender.getTime();
					shiftID=3;
				}

				result.setWorkDate(workDate);
				result.setShiftID(shiftID);
			}

			
			result.dump();
		} catch (Exception e) {
			e.printStackTrace();

			try {
				if (fin != null) {
					fin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			FileUtil.fileError(file4shopClock);
			
			result = new ShopClock();
			
			if(isReset){
				Date date=new Date();
				GregorianCalendar calender = new GregorianCalendar();
				calender.setTime(date);
				
				Date workDate;
				int shiftID;
		
				int hour=calender.get(Calendar.HOUR_OF_DAY);
		
				if(hour>=7 && hour<15){
					workDate=date;
					shiftID=1;		
				}else if(hour>=15 && hour<23){
					workDate=date;
					shiftID=2;
				}else if(hour>=23){
					workDate=date;
					shiftID=3;
				}else{
					calender.set(
						GregorianCalendar.DATE,
					calender.get(GregorianCalendar.DATE) -1);
					workDate=calender.getTime();
					shiftID=3;
				}

				result.setWorkDate(workDate);
				result.setShiftID(shiftID);
			}
			
			result.dump();
		}
		return result;
	}

	public void nextTurn(PosContext context) {
		if (shiftID == 3) {
			shiftID = 1;
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(workDate);
			cal.set(
				GregorianCalendar.DATE,
				cal.get(GregorianCalendar.DATE) + 1);
			workDate = cal.getTime();
			context.setSheetid(1);
			context.dump();
		} else {
			shiftID++;
		}
		dump();
	}

	public String toString() {
		return "workDate="
			+ Formatter.getDate(workDate)
			+ ",shiftID="
			+ shiftID;
	}

}
