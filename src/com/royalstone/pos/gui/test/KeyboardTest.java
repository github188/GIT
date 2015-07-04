/*
 * 创建日期 2004-6-1
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class KeyboardTest extends JFrame{
  JPanel jPanel1 = new JPanel();
  JTextArea txtContent = new JTextArea();
  BorderLayout borderLayout1 = new BorderLayout();

  public KeyboardTest() {
    try {
      jbInit();
	  addKeyAndContainerListenerRecursively(this);
	  this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	  this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	  this.setTitle("键盘测试");

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    jPanel1.setLayout(borderLayout1);
    txtContent.setFont(new java.awt.Font("Dialog", 0, 20));
    txtContent.setEditable(false);
    txtContent.setText("");
    this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
    jPanel1.add(txtContent,  BorderLayout.CENTER);
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
	  		txtContent.append("KeyCode="+e.getKeyCode()+"\n");
	  }

	  public void keyReleased(KeyEvent e) {
	  }

	  public void keyTyped(KeyEvent e) {
	  }
  }

  public static void main(String args[]){
		KeyboardTest keyboardTest=new KeyboardTest();
		keyboardTest.show();
  }

}
