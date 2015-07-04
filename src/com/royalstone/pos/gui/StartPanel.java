/*
 * 创建日期 2004-6-14
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui;

import java.net.URL;

import java.awt.*;
import javax.swing.*;

/**
 * 启动窗口的具体布局的面板
 * @author liangxinbiao
 */
public class StartPanel extends JPanel{

	private Image img;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel1 = new JLabel();

	public StartPanel(){
		URL url = StartPanel.class.getResource("/images/background.jpg");
		img = this.getToolkit().createImage(url);
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(img, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
  /**
   * JBuilder自动生成的初始化界面方法
   * @throws Exception
   */
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jPanel2.setOpaque(false);
    jPanel2.setPreferredSize(new Dimension(10, 110));
    jPanel3.setOpaque(false);
    jPanel3.setPreferredSize(new Dimension(70, 10));
    jPanel3.setRequestFocusEnabled(true);
    jPanel5.setOpaque(false);
    jPanel5.setPreferredSize(new Dimension(10, 150));
    jPanel4.setMinimumSize(new Dimension(10, 10));
    jPanel4.setOpaque(false);
    jPanel4.setPreferredSize(new Dimension(50, 10));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 22));
    jLabel1.setForeground(Color.white);
    jLabel1.setText("系统正在启动，请稍候……");
    jPanel1.setOpaque(false);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1, null);
    this.add(jPanel2, BorderLayout.SOUTH);
    this.add(jPanel3, BorderLayout.WEST);
    this.add(jPanel4, BorderLayout.EAST);
    this.add(jPanel5, BorderLayout.NORTH);
  }
  
  /**
   * 
   * @param args
   */
  public static void main(String[] args){
  	StartPanel start = new StartPanel();
  	//start.show();
  }

}
