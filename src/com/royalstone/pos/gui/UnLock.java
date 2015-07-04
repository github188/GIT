package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.royalstone.pos.common.Operator;
import com.royalstone.pos.common.OperatorList;
import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.managment.ClerkAdm;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;

/**
 * 锁机对话框，在此状态下当前用户输入正确密码可以解锁 
 * @author liangxinbiao
 */
public class UnLock extends JDialog {

	StringBuffer inputcode = new StringBuffer();
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	JLabel jLabel1 = new JLabel();
	JTextField txtPassword = new JTextField();
	JPanel jPanel7 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	public UnLock() {
		super(pos.posFrame);
		try {
			jbInit();
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setModal(true);
			setTitle("锁机状态");
			setSize(400, 150);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 400) / 2),
				(int) ((screenSize.getHeight() - 150) / 2));

			UnLockKeyListener cl = new UnLockKeyListener();
			txtPassword.addKeyListener(cl);

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
		jPanel2.setLayout(borderLayout2);
		jPanel6.setMinimumSize(new Dimension(10, 10));
		jPanel6.setPreferredSize(new Dimension(10, 30));
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setPreferredSize(new Dimension(40, 30));
		jLabel1.setText("密码:");
		txtPassword.setBackground(Color.white);
		txtPassword.setFont(new java.awt.Font("Dialog", 0, 16));
		txtPassword.setPreferredSize(new Dimension(150, 30));
		txtPassword.setEditable(false);
		txtPassword.setText("");
		jPanel3.setPreferredSize(new Dimension(10, 30));
		jPanel4.setPreferredSize(new Dimension(30, 10));
		jPanel5.setMinimumSize(new Dimension(30, 10));
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel7, BorderLayout.CENTER);
		jPanel7.add(jLabel1, null);
		jPanel7.add(txtPassword, null);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.EAST);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
	}

	/**
	 * 处理键盘动作 
	 * @author liangxinbiao
	 */
	private class UnLockKeyListener extends KeyAdapter {
		public void keyPressed(java.awt.event.KeyEvent e) {

			switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER :
					checkPassword();
					break;
				case KeyEvent.VK_0 :
				case KeyEvent.VK_1 :
				case KeyEvent.VK_2 :
				case KeyEvent.VK_3 :
				case KeyEvent.VK_4 :
				case KeyEvent.VK_5 :
				case KeyEvent.VK_6 :
				case KeyEvent.VK_7 :
				case KeyEvent.VK_8 :
				case KeyEvent.VK_9 :
					inputcode.append(e.getKeyChar());
					StringBuffer tmp = new StringBuffer();
					for (int i = 0; i < inputcode.length(); i++) {
						tmp.append("*");
					}
					txtPassword.setText(tmp.toString());
					if (inputcode.length() == 4) {
						checkPassword();
					}
			}
		}
	}

	/**
	 * 检查所输入的密码是否正确，正确就退出，不正确就清除用户输入，继续等待用户输入
	 */
	private void checkPassword() {

		PosContext context = PosContext.getInstance();
		Operator op = null;

		if (context.isOnLine()) {
			ClerkAdm adm =
				new ClerkAdm(context.getServerip(), context.getPort());
			Response r =
				adm.getClerk(
					context.getPosid(),
					context.getCashierid(),
					inputcode.toString());
			if (r != null) {
				op = (Operator) r.getObject();
			} else {
				OperatorList lst = new OperatorList();
				lst.load("operator.lst");
				op = lst.get(context.getCashierid(), inputcode.toString());
			}
		} else {
			OperatorList lst = new OperatorList();
			lst.load("operator.lst");
			op = lst.get(context.getCashierid(), inputcode.toString());
		}

		if (op == null) {
			if (inputcode.length() > 0) {
				inputcode.delete(0, inputcode.length());
			}
			txtPassword.setText("");
		} else {
			pos.setStateBUnlock();
			dispose();
		}
	}

	/**
	 * 测试主方法
	 * @param args
	 */
	public static void main(String[] args) {
		UnLock unLock = new UnLock();
		unLock.show();
	}

}
