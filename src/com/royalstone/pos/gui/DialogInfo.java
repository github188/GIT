package com.royalstone.pos.gui;

import javax.swing.*;

import com.royalstone.pos.shell.pos;

import java.awt.*;
import java.awt.event.*;

/**
 * 提示对话框
 * @author liangxinbiao
 */
public class DialogInfo extends JDialog{
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JPanel jPanel6 = new JPanel();
  JButton jButton1 = new JButton();
  JLabel lblMessage = new JLabel();
  public DialogInfo() {
  	super(pos.posFrame);
    try {
      jbInit();
	  setSize(400, 150);
	  this.setModal(true);
	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	  this.setLocation(
		  (int) ((screenSize.getWidth() - 400) / 2),
		  (int) ((screenSize.getHeight() - 150) / 2));
		  
	  addKeyAndContainerListenerRecursively(this);

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * JBuilder自动生成的初始化界面方法
   * @throws Exception
   */
  private void jbInit() throws Exception {
    jPanel1.setLayout(borderLayout1);
    jPanel3.setPreferredSize(new Dimension(50, 50));
    jPanel2.setPreferredSize(new Dimension(10, 50));
    jPanel4.setPreferredSize(new Dimension(30, 10));
    jButton1.setFont(new java.awt.Font("Dialog", 0, 13));
    jButton1.setPreferredSize(new Dimension(76, 29));
    jButton1.setText("确  定");
    jButton1.addActionListener(new WorkTurnComplete_jButton1_actionAdapter(this));
    jPanel6.setPreferredSize(new Dimension(10, 20));
    jPanel5.setPreferredSize(new Dimension(50, 10));
    lblMessage.setFont(new java.awt.Font("Dialog", 0, 16));
    lblMessage.setText("班结完成!");
    this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(lblMessage, null);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel3.add(jButton1, null);
    jPanel1.add(jPanel4, BorderLayout.WEST);
    jPanel1.add(jPanel5, BorderLayout.EAST);
    jPanel1.add(jPanel6, BorderLayout.NORTH);
  }

  void jButton1_actionPerformed(ActionEvent e) {
      dispose();
  }
  
  private PosKeyboard keyListener = new PosKeyboard();
  
  /**
   * 
   * @param c
   */
  private void addKeyAndContainerListenerRecursively(Component c) {
	  c.removeKeyListener(keyListener);
	  c.addKeyListener(keyListener);
	  if (c instanceof Container) {
		  Container cont = (Container) c;
		  Component[] children = cont.getComponents();
		  for (int i = 0; i < children.length; i++) {
			  addKeyAndContainerListenerRecursively(children[i]);
		  }
	  }
  }

  /**
   * 
   * @param c
   */	
  private void removeKeyAndContainerListenerRecursively(Component c) {
	  c.removeKeyListener(keyListener);
	  if (c instanceof Container) {
		  Container cont = (Container) c;
		  Component[] children = cont.getComponents();
		  for (int i = 0; i < children.length; i++) {
			  removeKeyAndContainerListenerRecursively(children[i]);
		  }
	  }
  }

	  /**
	   * 
	   * @author liangxinbiao
	   */
	  private class PosKeyboard implements KeyListener {
	
		  public void keyPressed(KeyEvent e) {
			  int keyCode = e.getKeyCode();
			  switch (keyCode) {
				  case KeyEvent.VK_ENTER :
					  dispose();
					  break;
			  }
		  }
	
		  public void keyReleased(KeyEvent e) {
		  }
	
		  public void keyTyped(KeyEvent e) {
		  }
	  }

	/**
	 * 提示信息
	 * @param value
	 */
	public void setMessage(String value) {
		lblMessage.setText(value);
	}

}

class WorkTurnComplete_jButton1_actionAdapter implements java.awt.event.ActionListener {
  DialogInfo adaptee;

  WorkTurnComplete_jButton1_actionAdapter(DialogInfo adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}
