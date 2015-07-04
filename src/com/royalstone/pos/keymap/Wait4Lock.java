/*
 * Created on 2004-8-30
 */
package com.royalstone.pos.keymap;

import com.royalstone.pos.gui.UnLock;
import com.royalstone.pos.gui.test.KeyboardTest;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.PosConfig;

/**
 * @author HuangXuean
 *自动锁机类，在POS机启动的同时开始此线程的工作
 *当收银员在一定的时间段内不对键盘或者鼠标进行操作即启动锁机功能
 *起到保护作用
 */
public class Wait4Lock extends Thread {
	private int count = 0;
	KeyEventListener4Lock kel = null;
	boolean isStart = false;
	
	public Wait4Lock(){
		kel = new KeyEventListener4Lock();
		count = getTimeFromConfig();
	}
	
	public void setState(boolean value){
		isStart = value;
	}
	
	public void run(){
		try{
			while(true){
				if(isStart && count!=0){
					while(System.currentTimeMillis()-kel.getMillSeconds()>count){
						if(!pos.isLock()){
							UnLock unLock = new UnLock();
							unLock.show();
					 	}						
					}					
				}
				sleep(5000);				
			}			
		}catch(InterruptedException ex){
			ex.printStackTrace();	
		}
		
	}
	
	private int getTimeFromConfig(){
		PosConfig config = PosConfig.getInstance();
		int milliseconds = config.getInteger("AUTO_LOCK_TIME"); 
		return milliseconds;
	}
	
	public static void main(String[] args) {
		Wait4Lock wt = new Wait4Lock();
		wt.start();
		KeyboardTest keyboardTest = new KeyboardTest();
		keyboardTest.show();
	}
}

