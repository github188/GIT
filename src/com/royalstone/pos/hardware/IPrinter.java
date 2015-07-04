package com.royalstone.pos.hardware;

/**
 * 小票打印机硬件层的接口
 * @author liangxinbiao
 */
public interface IPrinter {
	
	/**
	 * 打印一行信息
	 * @param value
	 */
	public abstract void println(String value);
	
	/**
	 * 走纸若干行
	 * @param line
	 */
	public abstract void feed(int line);
	
	/**
	 * 切纸 
	 */
	public abstract void cut();
	
}