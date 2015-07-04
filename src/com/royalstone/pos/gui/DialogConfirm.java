package com.royalstone.pos.gui;

import javax.swing.*;

import com.royalstone.pos.shell.pos;

import java.awt.*;
import java.awt.event.*;

/**
 * 确认对话框
 * @author liangxinbiao
 */
public class DialogConfirm extends JDialog {
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JButton btnCancel = new JButton();
	JButton btnEnter = new JButton();
	JLabel lblMessage = new JLabel();
	private boolean isConfirm = false;

	public DialogConfirm() {
		this(460,160);
	}
	
	public DialogConfirm(int width,int height) {
		super(pos.posFrame);
		try {
			jbInit();
			setSize(width, height);
			this.setModal(true);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - width) / 2),
				(int) ((screenSize.getHeight() - height) / 2));

			addKeyAndContainerListenerRecursively(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * JBuilder自动生成的初始化界面方法
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		jPanel1.setLayout(borderLayout1);
		jPanel6.setPreferredSize(new Dimension(10, 30));
		jPanel5.setPreferredSize(new Dimension(30, 10));
		jPanel3.setPreferredSize(new Dimension(30, 10));
		jPanel2.setPreferredSize(new Dimension(10, 50));
		btnCancel.setFont(new java.awt.Font("Dialog", 0, 13));
		btnCancel.setText("取    消");
		btnCancel.addActionListener(
			new DutyEndConfirm_btnCancel_actionAdapter(this));
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 13));
		btnEnter.setText("确    定");
		btnEnter.addActionListener(
			new DutyEndConfirm_btnEnter_actionAdapter(this));
		lblMessage.setFont(new java.awt.Font("Dialog", 0, 16));
		lblMessage.setRequestFocusEnabled(true);
    lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setText("是否要进行班结？");
		jPanel4.setDebugGraphicsOptions(0);
		jPanel4.setLayout(borderLayout2);
		lblWarning.setFont(new java.awt.Font("Dialog", 1, 14));
		lblWarning.setForeground(Color.red);
		lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
    lblWarning.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
		jPanel1.add(jPanel5, BorderLayout.WEST);
		jPanel1.add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(lblMessage, BorderLayout.CENTER);
		jPanel4.add(lblWarning, BorderLayout.SOUTH);
		jPanel1.add(jPanel3, BorderLayout.EAST);
		jPanel1.add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(btnEnter, null);
		jPanel2.add(btnCancel, null);
	}

	private PosKeyboard keyListener = new PosKeyboard();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel lblWarning = new JLabel();

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
	 * 处理键盘动作
	 * @author liangxinbiao
	 */
	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
				case KeyEvent.VK_ESCAPE :
					isConfirm = false;
					dispose();
					break;
				case KeyEvent.VK_ENTER :
					if (btnEnter.hasFocus()) {
						isConfirm = true;
					}else{
						isConfirm = false;
					}
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
	 *
	 * @return 返回用户是否按下确定键
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 *
	 * @param e
	 */
	void btnEnter_actionPerformed(ActionEvent e) {
		isConfirm = true;
		dispose();
	}

	/**
	 *
	 * @param e
	 */
	void btnCancel_actionPerformed(ActionEvent e) {
		isConfirm = false;
		dispose();
	}

	/**
	 *
	 * @param value 提示信息
	 */
	public void setMessage(String value) {
		lblMessage.setText(value);
	}

	public void setWarning(String value) {
		lblWarning.setText("警告: " + value);
	}

}

class DutyEndConfirm_btnEnter_actionAdapter
	implements java.awt.event.ActionListener {
	DialogConfirm adaptee;

	DutyEndConfirm_btnEnter_actionAdapter(DialogConfirm adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnEnter_actionPerformed(e);
	}
}

class DutyEndConfirm_btnCancel_actionAdapter
	implements java.awt.event.ActionListener {
	DialogConfirm adaptee;

	DutyEndConfirm_btnCancel_actionAdapter(DialogConfirm adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCancel_actionPerformed(e);
	}
}
