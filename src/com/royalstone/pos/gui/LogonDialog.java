/*
 * 创建日期 2004-5-30
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.OutputStream;

import javax.swing.JFrame;

/**
 * 登陆窗口
 * @author liangxinbiao
 */
public class LogonDialog extends JFrame {

	private LogonPanel logonPanel;

	/**
	 * 
	 * @param output
	 */
	public LogonDialog(OutputStream output) {
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		logonPanel = new LogonPanel(output);
		this.getContentPane().add(logonPanel, BorderLayout.CENTER);
			
		this.setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		this.addWindowFocusListener(new WindowFocusListener(){

			public void windowGainedFocus(WindowEvent e) {
				logonPanel.focusComponent();
			}

			public void windowLostFocus(WindowEvent e) {
								
			}});

	}	
	
	/**
	 * 
	 * @return 登陆面板
	 */
	public LogonPanel getLogonPanel(){
		return logonPanel;
	}
	
	
	
}
