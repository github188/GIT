package com.efuture.commonKit;


//这个类用于,将服务器上时间改变为本地的格式
public class TimeDate 
{
	public String fullTime =null;
	public String hh=null;
	public String min=null;
	public String ss=null;
	public String yy=null;
	public String mm=null;
	public String dd=null;
	public String cc=null;
	public static String[] ref={"fullTime"};
	
	public TimeDate(String date,String time)
	{
		String[] dates;
		if (date.indexOf('-') >= 0) dates=date.split("-");
		else dates=date.split("/");
		cc=dates[0].substring(0,2);
		yy=dates[0].substring(2,4);
		mm=dates[1].trim();
		dd=dates[2].trim();
		
		String[] times=time.split(":");
		hh=times[0].trim();
		min=times[1].trim();
		ss=times[2].trim();
	}
	
	public TimeDate()
	{}
	
	public void split()
	{
		if(fullTime == null) return;
		
		String[] lines=fullTime.split(" ");
		String date=lines[0];
		String time=lines[1];
		String[] dates;
		if (date.indexOf('-') >= 0) dates=date.split("-");
		else dates=date.split("/");
		cc=dates[0].substring(0,2);
		yy=dates[0].substring(2,4);
		mm=dates[1].trim();
		dd=dates[2].trim();
		
		String[] times=time.split(":");
		hh=times[0].trim();
		min=times[1].trim();
		ss=times[2].trim();
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getDd() {
		return dd;
	}

	public void setDd(String dd) {
		this.dd = dd;
	}

	public String getHh() {
		return hh;
	}

	public void setHh(String hh) {
		this.hh = hh;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMm() {
		return mm;
	}

	public void setMm(String mm) {
		this.mm = mm;
	}

	public String getSs() {
		return ss;
	}

	public void setSs(String ss) {
		this.ss = ss;
	}

	public String getYy() {
		return yy;
	}

	public void setYy(String yy) {
		this.yy = yy;
	}
	
}
