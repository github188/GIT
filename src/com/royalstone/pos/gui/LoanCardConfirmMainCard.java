package com.royalstone.pos.gui;

import javax.swing.*;
import java.awt.*;

import com.borland.jbcl.layout.*;
import java.awt.event.*;

/**
 * 挂账卡的主卡的确认对话框
 * @deprecated 
 * @author liangxinbiao
 */

public class LoanCardConfirmMainCard extends JDialog {

	private boolean isConfirm = false;

	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	JButton btnCanel = new JButton();
	JButton btnEnter = new JButton();
	
	public static void main(String[] args){
		LoanCardConfirmMainCard lccmc = new LoanCardConfirmMainCard();
		lccmc.show();
	}
	
	public LoanCardConfirmMainCard() {
		try {
			jbInit();
			setTitle("挂账卡");
			this.setModal(true);
			setSize(640, 320);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 640) / 2),
				(int) ((screenSize.getHeight() - 320) / 2));

			addKeyAndContainerListenerRecursively(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void jbInit() throws Exception {
		jPanel1.setLayout(borderLayout1);
		jPanel3.setPreferredSize(new Dimension(10, 60));
		jPanel4.setPreferredSize(new Dimension(30, 10));
		jPanel5.setPreferredSize(new Dimension(30, 10));
		jPanel6.setMinimumSize(new Dimension(10, 30));
		jPanel6.setPreferredSize(new Dimension(10, 30));
		jPanel2.setLayout(verticalFlowLayout1);
		btnCanel.setFont(new java.awt.Font("Dialog", 0, 16));
		btnCanel.setPreferredSize(new Dimension(100, 33));
		btnCanel.setText("取    消");
		btnCanel.addActionListener(
			new LoanCardConfirmEx_btnCanel_actionAdapter(this));
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 16));
		btnEnter.setPreferredSize(new Dimension(100, 33));
		btnEnter.setText("确    定");
		btnEnter.addActionListener(
			new LoanCardConfirmEx_btnEnter_actionAdapter(this));
		jLabel7.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel7.setRequestFocusEnabled(true);
		jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel7.setText("支付后余额与信贷额度的百分比:");
		panPercent.setLayout(borderLayout8);
		txtPercent.setBackground(Color.white);
		txtPercent.setFont(new java.awt.Font("Dialog", 0, 16));
		txtPercent.setPreferredSize(new Dimension(100, 27));
		txtPercent.setEditable(false);
		txtPercent.setText("");
		txtPercent.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel8.setLayout(borderLayout6);
		jLabel4.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel4.setText("余额:");
		jPanel14.setLayout(borderLayout5);
		txtBalance.setBackground(Color.white);
		txtBalance.setFont(new java.awt.Font("Dialog", 0, 16));
		txtBalance.setPreferredSize(new Dimension(100, 27));
		txtBalance.setEditable(false);
		txtBalance.setText("");
		txtBalance.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel13.setLayout(borderLayout4);
		jPanel9.setLayout(gridLayout2);
		jPanel10.setLayout(borderLayout9);
		jLabel2.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel2.setRequestFocusEnabled(true);
		jLabel2.setText("            卡类型:");
		jPanel12.setLayout(borderLayout3);
		txtCardType.setBackground(Color.white);
		txtCardType.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCardType.setPreferredSize(new Dimension(100, 27));
		txtCardType.setEditable(false);
		txtCardType.setText("主卡");
		txtCardNo.setBackground(Color.white);
		txtCardNo.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCardNo.setPreferredSize(new Dimension(100, 27));
		txtCardNo.setEditable(false);
		txtCardNo.setText("");
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("            卡号:");
		jPanel11.setLayout(borderLayout2);
		txtCustName.setBackground(Color.white);
		txtCustName.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCustName.setPreferredSize(new Dimension(300, 27));
		txtCustName.setEditable(false);
		txtCustName.setText("");
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel3.setRequestFocusEnabled(true);
		jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel3.setText("    客户名称:");
		txtTender.setBackground(Color.white);
		txtTender.setFont(new java.awt.Font("Dialog", 0, 16));
		txtTender.setPreferredSize(new Dimension(100, 27));
		txtTender.setEditable(false);
		txtTender.setText("");
		txtTender.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel5.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel5.setRequestFocusEnabled(true);
		jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel5.setText("请支付金额:");
		jPanel7.setLayout(borderLayout10);
		jPanel17.setLayout(borderLayout11);
		txtAtferTender.setBackground(Color.white);
		txtAtferTender.setFont(new java.awt.Font("Dialog", 0, 16));
		txtAtferTender.setOpaque(true);
		txtAtferTender.setPreferredSize(new Dimension(100, 27));
		txtAtferTender.setEditable(false);
		txtAtferTender.setSelectionStart(11);
		txtAtferTender.setText("");
		txtAtferTender.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel6.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel6.setText("支付后余额:");
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		panPercent.add(jLabel7, BorderLayout.CENTER);
		panPercent.add(txtPercent, BorderLayout.EAST);
		jPanel8.add(txtAtferTender, BorderLayout.EAST);
		jPanel8.add(jLabel6, BorderLayout.CENTER);
		jPanel14.add(jLabel4, BorderLayout.CENTER);
		jPanel14.add(txtBalance, BorderLayout.EAST);
		jPanel9.add(jPanel13, null);
		jPanel9.add(jPanel14, null);
		jPanel12.add(txtCardType, BorderLayout.CENTER);
		jPanel12.add(jLabel2, BorderLayout.WEST);
		jPanel10.add(jPanel11, BorderLayout.CENTER);
		jPanel11.add(jLabel1, BorderLayout.WEST);
		jPanel11.add(txtCardNo, BorderLayout.CENTER);
		jPanel10.add(jPanel12, BorderLayout.EAST);
		jPanel2.add(jPanel10, null);
		jPanel2.add(jPanel7, null);
		jPanel2.add(jPanel9, null);
		jPanel2.add(jPanel17, null);
		jPanel17.add(jLabel5, BorderLayout.CENTER);
		jPanel17.add(txtTender, BorderLayout.EAST);
		jPanel2.add(jPanel8, null);
		jPanel7.add(jLabel3, BorderLayout.WEST);
		jPanel7.add(txtCustName, BorderLayout.CENTER);
		jPanel2.add(panPercent, null);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(btnEnter, null);
		jPanel3.add(btnCanel, null);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.EAST);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
	}

	private PosKeyboard keyListener = new PosKeyboard();
	BorderLayout borderLayout8 = new BorderLayout();
	JLabel jLabel7 = new JLabel();
	JPanel panPercent = new JPanel();
	JTextField txtPercent = new JTextField();
	JPanel jPanel7 = new JPanel();
	JPanel jPanel8 = new JPanel();
	JLabel jLabel4 = new JLabel();
	BorderLayout borderLayout4 = new BorderLayout();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel14 = new JPanel();
	JTextField txtBalance = new JTextField();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel13 = new JPanel();
	JPanel jPanel9 = new JPanel();
	JPanel jPanel10 = new JPanel();
	JLabel jLabel2 = new JLabel();
	JPanel jPanel12 = new JPanel();
	JTextField txtCardType = new JTextField();
	BorderLayout borderLayout3 = new BorderLayout();
	JTextField txtCardNo = new JTextField();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel11 = new JPanel();
	JTextField txtCustName = new JTextField();
	JLabel jLabel3 = new JLabel();
	JPanel jPanel17 = new JPanel();
	JTextField txtTender = new JTextField();
	JLabel jLabel5 = new JLabel();
	BorderLayout borderLayout9 = new BorderLayout();
	BorderLayout borderLayout10 = new BorderLayout();
	BorderLayout borderLayout11 = new BorderLayout();
	BorderLayout borderLayout6 = new BorderLayout();
	JTextField txtAtferTender = new JTextField();
	JLabel jLabel6 = new JLabel();

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
					isConfirm = false;
					dispose();
					break;
				case KeyEvent.VK_ENTER :
					isConfirm = true;
					dispose();
					break;
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	public void setCardNo(String value) {
		txtCardNo.setText(value);
	}

	public void setTenderAmount(String value) {
		txtTender.setText(value);
	}

	public void setCardAmount(String value) {
		txtBalance.setText(value);
	}

	public void setAtferTenderAmount(String value) {
		txtAtferTender.setText(value);
	}

	public void setPercent(String value) {
		txtPercent.setText(value);
	}

	public void setCustName(String value) {
		txtCustName.setText(value);
	}

	public void disablePercent() {
		panPercent.setVisible(false);
	}

	void btnEnter_actionPerformed(ActionEvent e) {
		isConfirm=true;
		dispose();
	}

	void btnCanel_actionPerformed(ActionEvent e) {
		isConfirm=false;
		dispose();
	}

}

class LoanCardConfirmEx_btnEnter_actionAdapter
	implements java.awt.event.ActionListener {
	LoanCardConfirmMainCard adaptee;

	LoanCardConfirmEx_btnEnter_actionAdapter(LoanCardConfirmMainCard adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnEnter_actionPerformed(e);
	}
}

class LoanCardConfirmEx_btnCanel_actionAdapter
	implements java.awt.event.ActionListener {
	LoanCardConfirmMainCard adaptee;

	LoanCardConfirmEx_btnCanel_actionAdapter(LoanCardConfirmMainCard adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCanel_actionPerformed(e);
	}
}
