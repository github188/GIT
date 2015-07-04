package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AbstractDocument;

import com.borland.jbcl.layout.VerticalFlowLayout;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

/**
 * 修改密码对话框
 * @author liangxinbiao
 */
public class ModifyPassword extends JDialog {

	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
	private boolean isConfirm = false;
	private StringBuffer inputLine = new StringBuffer();
	private int step = 0;

	private DumbDocument oldPasswordDocument = new DumbDocument();
	private DumbDocument newPasswordDocument = new DumbDocument();
	private DumbDocument confirmPasswordDocument = new DumbDocument();
	
	private PosKeyMap kmap;

	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	JPanel jPanel7 = new JPanel();
	JPanel jPanel8 = new JPanel();
	JPanel jPanel9 = new JPanel();
	JTextField txtOldPass = new JTextField();
	JLabel jLabel1 = new JLabel();
	JTextField txtNewPass = new JTextField();
	JLabel jLabel2 = new JLabel();
	JTextField txtConfirmPass = new JTextField();
	JLabel jLabel3 = new JLabel();
	public ModifyPassword() {
		super(pos.posFrame);
		try {
			jbInit();
			
			this.setModal(true);
			setTitle("密码修改");
			setSize(400, 220);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 400) / 2),
				(int) ((screenSize.getHeight() - 220) / 2));

			txtOldPass.setDocument(oldPasswordDocument);
			txtNewPass.setDocument(newPasswordDocument);
			txtConfirmPass.setDocument(confirmPasswordDocument);
			
			disableText();
			txtOldPass.setEditable(true);
			
			addKeyAndContainerListenerRecursively(this);
			
			kmap=PosKeyMap.getInstance();

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
		jPanel2.setLayout(verticalFlowLayout1);
		jPanel6.setMinimumSize(new Dimension(10, 10));
		jPanel6.setPreferredSize(new Dimension(10, 20));
		jPanel5.setMinimumSize(new Dimension(10, 10));
		jPanel5.setPreferredSize(new Dimension(30, 10));
		jPanel4.setMinimumSize(new Dimension(10, 10));
		jPanel4.setPreferredSize(new Dimension(30, 10));
		jPanel3.setMinimumSize(new Dimension(10, 10));
		jPanel3.setPreferredSize(new Dimension(10, 20));
		txtOldPass.setBackground(Color.white);
		txtOldPass.setFont(new java.awt.Font("Dialog", 0, 16));
		txtOldPass.setPreferredSize(new Dimension(100, 30));
		txtOldPass.setEditable(false);
		txtOldPass.setText("");
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setPreferredSize(new Dimension(80, 23));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("旧密码:");
		txtNewPass.setBackground(Color.white);
		txtNewPass.setFont(new java.awt.Font("Dialog", 0, 16));
		txtNewPass.setPreferredSize(new Dimension(100, 30));
		txtNewPass.setEditable(false);
		txtNewPass.setText("");
		jLabel2.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel2.setPreferredSize(new Dimension(80, 23));
		jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel2.setText("新密码:");
		txtConfirmPass.setBackground(Color.white);
		txtConfirmPass.setFont(new java.awt.Font("Dialog", 0, 16));
		txtConfirmPass.setPreferredSize(new Dimension(100, 30));
		txtConfirmPass.setEditable(false);
		txtConfirmPass.setText("");
		jLabel3.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel3.setPreferredSize(new Dimension(80, 23));
		jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel3.setText("确认密码:");
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel9, null);
		jPanel9.add(jLabel1, null);
		jPanel9.add(txtOldPass, null);
		jPanel2.add(jPanel8, null);
		jPanel8.add(jLabel2, null);
		jPanel8.add(txtNewPass, null);
		jPanel2.add(jPanel7, null);
		jPanel7.add(jLabel3, null);
		jPanel7.add(txtConfirmPass, null);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.EAST);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
	}
	
	/**
	 * @return 确认密码
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @return 新密码
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @return 旧密码
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @return 返回用户是否按下确定键
	 */
	public boolean isConfirm() {
		return isConfirm;
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
	 * 处理键盘动作
	 * @author liangxinbiao
	 */
	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			int keyCode = kmap.getFunction(e.getKeyCode()).getKey();
			switch (keyCode) {
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					inputLine.append((char)keyCode);
					updateValue();
					if(inputLine.length()==4)nextStep();
					break;
				case PosFunction.ENTER:
					if(inputLine.length()> 0)nextStep();
					break;
				case PosFunction.EXIT:
				case PosFunction.CANCEL:
					isConfirm = false;
					dispose();
					break;
				case PosFunction.BACKSPACE:
					if (inputLine.length() > 0) {
						inputLine.deleteCharAt(inputLine.length() - 1);
					}
					break;
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}

	/**
	 * 更新显示
	 *
	 */
	private void updateValue() {
		switch (step) {
			case 0 :
				oldPasswordDocument.permit = true;
				txtOldPass.setText(getStart(inputLine.length()));
				oldPasswordDocument.permit = false;
				break;
			case 1 :
				newPasswordDocument.permit = true;
				txtNewPass.setText(getStart(inputLine.length()));
				newPasswordDocument.permit = false;
				break;
			case 2 :
				confirmPasswordDocument.permit = true;
				txtConfirmPass.setText(getStart(inputLine.length()));
				confirmPasswordDocument.permit = false;
				break;
		}
	}

	/**
	 * 确定下一步要输入的东西
	 *
	 */
	private void nextStep() {
		switch (step) {
			case 0 :
				oldPassword = inputLine.toString();
				disableText();
				txtNewPass.setEditable(true);
				txtNewPass.requestFocus();
				if(inputLine.length()>0)inputLine.delete(0,inputLine.length());
				break;
			case 1 :
				newPassword = inputLine.toString();
				disableText();
				txtConfirmPass.setEditable(true);
				txtConfirmPass.requestFocus();
				if(inputLine.length()>0)inputLine.delete(0,inputLine.length());
				break;
			case 2 :
				confirmPassword = inputLine.toString();
				isConfirm=true;
				dispose();
				break;
		}
		step++;
	}
	
	/**
	 * 
	 * @param length
	 * @return 长度为length的"*"号
	 */
	private String getStart(int length){
		StringBuffer result=new StringBuffer();
		for(int i=0;i<length;i++){
				result.append("*");	
		}
		return result.toString();
	}
	
	/**
	 * 
	 */
	private void disableText(){
		txtOldPass.setEditable(false);
		txtNewPass.setEditable(false);
		txtConfirmPass.setEditable(false);
	}
	
	/**
	 * 
	 * @author liangxinbiao
	 */
	private class DumbDocument extends PlainDocument {
		private boolean permit = false;

		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
			if (permit)
				super.insertString(offs, str, a);
		}

		protected void insertUpdate(
			AbstractDocument.DefaultDocumentEvent chng,
			AttributeSet a) {
			if (permit)
				super.insertUpdate(chng, a);
		}

		protected void removeUpdate(
			AbstractDocument.DefaultDocumentEvent chng) {
			if (permit)
				super.removeUpdate(chng);
		}

		public void setPermit(boolean value) {
			permit = value;
		}
	}
}
