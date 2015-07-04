/*
 *POS Version 4 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.gui.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.borland.jbcl.layout.VerticalFlowLayout;

public class JDialogTest extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField2 = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField jTextField3 = new JTextField();
  JButton cancel = new JButton();
  JButton Enter = new JButton();

  public JDialogTest(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      setTitle("测试");
	  setSize(350, 250);
	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	  this.setLocation((int)(screenSize.getWidth()-350)/2,(int)(screenSize.getHeight()-250)/2);
	  addKeyAndContainerListenerRecursively(this);
     // pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public JDialogTest() {
    this(null, "", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jPanel1.setLayout(verticalFlowLayout1);
    jTextField1.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField1.setPreferredSize(new Dimension(100, 26));
    jTextField1.setText("");
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel1.setText("单 价:");
    jLabel2.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel2.setText("重 量:");
    jTextField2.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField2.setPreferredSize(new Dimension(100, 26));
    jTextField2.setText("");
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel3.setText("金 额:");
    jTextField3.setFont(new java.awt.Font("Dialog", 0, 14));
    jTextField3.setPreferredSize(new Dimension(100, 26));
    jTextField3.setText("");
    cancel.setFont(new java.awt.Font("Dialog", 0, 14));
    cancel.setText("取 消");
	cancel.addActionListener(new JDialogTest_cancel_actionAdapter(this));
    Enter.setFont(new java.awt.Font("Dialog", 0, 14));
    Enter.setText("确认");
    Enter.addActionListener(new JDialogTest_Enter_actionAdapter(this));
    jPanel1.setMinimumSize(new Dimension(67, 100));
    jPanel1.setPreferredSize(new Dimension(380, 100));
    jPanel2.setAlignmentX((float) 0.5);
    jPanel2.setPreferredSize(new Dimension(10, 50));
    jPanel3.setPreferredSize(new Dimension(30, 10));
    jPanel4.setPreferredSize(new Dimension(30, 10));
    jPanel5.setPreferredSize(new Dimension(10, 30));
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel8, null);
    jPanel8.add(jLabel1, null);
    jPanel8.add(jTextField1, null);
    jPanel1.add(jPanel7, null);
    jPanel7.add(jLabel2, null);
    jPanel7.add(jTextField2, null);
    jPanel1.add(jPanel6, null);
    jPanel6.add(jLabel3, null);
    jPanel6.add(jTextField3, null);
    panel1.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(Enter, null);
    jPanel2.add(cancel, null);
    panel1.add(jPanel3, BorderLayout.WEST);
    panel1.add(jPanel4, BorderLayout.EAST);
    panel1.add(jPanel5, BorderLayout.NORTH);
  }
  
  private PosKeyboard keyListener = new PosKeyboard();
	
	  
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

	  
	  private class PosKeyboard implements KeyListener {

		  public void keyPressed(KeyEvent e) {
			  int keyCode = e.getKeyCode();
			  switch (keyCode) {
				  case KeyEvent.VK_ESCAPE :
					  //isConfirm = false;
					  dispose();
					  break;
				  case KeyEvent.VK_ENTER :
					  //if(btnEnter.hasFocus()){
						//  isConfirm = true;	
					 // }
					  dispose();
					  break;
			  }
		  }

		  public void keyReleased(KeyEvent e) {
		  }

		  public void keyTyped(KeyEvent e) {
		  }
	  }
  
  void Enter_actionPerformed(ActionEvent e){
	dispose();
  }
  
  void cancel_actionPerformed(ActionEvent e){
  	dispose();
  }
  
  public static void main(String[] args){
  	JDialogTest test = new JDialogTest();
  	test.show();
  }
  
}


class JDialogTest_Enter_actionAdapter implements java.awt.event.ActionListener {
  JDialogTest adaptee;

  JDialogTest_Enter_actionAdapter(JDialogTest adaptee) {
	this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
	adaptee.Enter_actionPerformed(e);
  }
}

class JDialogTest_cancel_actionAdapter implements java.awt.event.ActionListener {
  JDialogTest adaptee;

  JDialogTest_cancel_actionAdapter(JDialogTest adaptee) {
	this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
	adaptee.cancel_actionPerformed(e);
  }
}

