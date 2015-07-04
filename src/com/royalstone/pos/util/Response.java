/*
 * Created on 2004-6-4
 */
package com.royalstone.pos.util;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Response 用于把后台执行结果传递回前台. 其要素有三:
 * 执行的日期和时间. 可调用方法 getDate(), getTime() 得到.
 * 执行返回代码.可调用方法 retCode() 得到.
 * 对于执行结果的简要说明. 可调用方法getNote() 得到.
 * 执行后所返回的对象.可调用方法getObject() 得到.
 * @author Mengluoyi
 */
public class Response  implements Serializable
{
	/**
	 * @param ret
	 */
	public Response( int ret ) 
	{
		time = new GregorianCalendar();
		this.ret = ret;
	}
	
	/**
	 * @param ret
	 * @param note
	 */
	public Response( int ret, String note ) 
	{
		time = new GregorianCalendar();
		this.ret = ret;
		this.note = note;
	}
	
	/**
	 * @param ret
	 * @param note
	 * @param obj
	 */
	public Response( int ret, String note, Object obj ) 
	{
		time = new GregorianCalendar();
		this.ret = ret;
		this.note = note;
		this.obj = obj;
	}

   public Response( int ret, String note, Object obj,int listNO)
	{
		time = new GregorianCalendar();
		this.ret = ret;
		this.note = note;
		this.obj = obj;
        this.listNO=listNO;
	}

	/**
	 * @return
	 */
	public boolean succeed()
	{
		return ( ret == 0 );
	}
	
	/**
	 * @return
	 */
	public int retCode()
	{
		return ret;
	}
	
	/**
	 * @return
	 */
	public String getNote()
	{
		return note;
	}
	
	/**
	 * @return
	 */
	public Day getDate()
	{
		return new Day(time);
	}
	
	/**
	 * @return
	 */
	public PosTime getTime()
	{
		return new PosTime(time);
	}
	
	/**
	 * @return
	 */
	public Object getObject()
	{
		return obj;
	}

    public int getListNO(){
        return this.listNO;
    }

	/**
	 * @return
	 */
	public String toString()
	{
		return getDate().toString() + " " + getTime().toString() + " [" + ret + "] " + note;
	}

	private int ret = 0;
	private String note = "";
	private GregorianCalendar time; 
	private Object obj = null;
    private int listNO=0;
}
