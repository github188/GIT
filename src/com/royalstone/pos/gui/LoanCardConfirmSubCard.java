package com.royalstone.pos.gui;

import java.awt.*;

import com.borland.jbcl.layout.*;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

import java.awt.event.*;
import javax.swing.*;

/**
 * 挂账卡的子卡的确认对话框
 * @author liangxinbiao
 */
public class LoanCardConfirmSubCard extends JDialog {

	private boolean isConfirm = false;
	private PosKeyMap kmap;

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
	public LoanCardConfirmSubCard() {
		super(pos.posFrame);
		try {
			jbInit();
			setTitle("挂账卡");
			this.setModal(true);
			setSize(700, 350);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 700) / 2),
				(int) ((screenSize.getHeight() - 350) / 2));

			addKeyAndContainerListenerRecursively(this);

			kmap = PosKeyMap.getInstance();

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
			new LoanCardConfirmSubCard_btnCanel_actionAdapter(this));
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 16));
		btnEnter.setPreferredSize(new Dimension(100, 33));
		btnEnter.setText("确    定");
		btnEnter.addActionListener(
			new LoanCardConfirmSubCard_btnEnter_actionAdapter(this));
		jPanel17.setLayout(borderLayout11);
		jPanel9.setLayout(gridLayout2);
		txtCarID.setBackground(Color.white);
		txtCarID.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCarID.setEditable(false);
		txtCarID.setText("");
		jLabel7.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel7.setRequestFocusEnabled(true);
		jLabel7.setToolTipText("");
		jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel7.setText("                               车 牌:");
		jPanel15.setLayout(borderLayout7);
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel3.setRequestFocusEnabled(true);
		jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel3.setText("                        客户名称:");
		jPanel7.setLayout(borderLayout10);
		txtCustName.setBackground(Color.white);
		txtCustName.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCustName.setPreferredSize(new Dimension(300, 27));
		txtCustName.setEditable(false);
		txtCustName.setText("");
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("                                卡号:");
		jPanel10.setLayout(borderLayout9);
		jLabel2.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel2.setRequestFocusEnabled(true);
		jLabel2.setText("            卡类型:");
		txtCardNo.setBackground(Color.white);
		txtCardNo.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCardNo.setPreferredSize(new Dimension(100, 27));
		txtCardNo.setEditable(false);
		txtCardNo.setText("");
		jPanel11.setLayout(borderLayout2);
		jPanel12.setLayout(borderLayout3);
		txtCardType.setBackground(Color.white);
		txtCardType.setFont(new java.awt.Font("Dialog", 0, 16));
		txtCardType.setPreferredSize(new Dimension(100, 27));
		txtCardType.setEditable(false);
		txtCardType.setText("子卡");
		jPanel13.setLayout(borderLayout4);
		jLabel4.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel4.setText("卡可消费金额:");
		txtBalance.setBackground(Color.white);
		txtBalance.setFont(new java.awt.Font("Dialog", 0, 16));
		txtBalance.setPreferredSize(new Dimension(150, 27));
		txtBalance.setEditable(false);
		txtBalance.setText("");
		txtBalance.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel14.setLayout(borderLayout5);
		jPanel16.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		txtAtferTender.setBackground(Color.white);
		txtAtferTender.setFont(new java.awt.Font("Dialog", 0, 16));
		txtAtferTender.setOpaque(true);
		txtAtferTender.setPreferredSize(new Dimension(150, 27));
		txtAtferTender.setEditable(false);
		txtAtferTender.setSelectionStart(11);
		txtAtferTender.setText("");
		txtAtferTender.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel8.setLayout(borderLayout6);
		jLabel6.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel6.setText("支付后卡可消费金额:");
		jLabel9.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel9.setText("支付后客户可消费金额:");
		txtAfterMainBalance.setBackground(Color.white);
		txtAfterMainBalance.setFont(new java.awt.Font("Dialog", 0, 16));
		txtAfterMainBalance.setPreferredSize(new Dimension(150, 27));
		txtAfterMainBalance.setEditable(false);
		txtAfterMainBalance.setText("");
		txtAfterMainBalance.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel18.setLayout(borderLayout8);
		txtTender.setBackground(Color.white);
		txtTender.setFont(new java.awt.Font("Dialog", 0, 16));
		txtTender.setPreferredSize(new Dimension(100, 27));
		txtTender.setEditable(false);
		txtTender.setText("");
		txtTender.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel20.setLayout(borderLayout13);
		jLabel5.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel5.setRequestFocusEnabled(true);
		jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel5.setText("                    请支付金额:");
		jLabel8.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel8.setText("            客户可消费金额:");
		txtMainBalance.setBackground(Color.white);
		txtMainBalance.setFont(new java.awt.Font("Dialog", 0, 16));
		txtMainBalance.setPreferredSize(new Dimension(150, 27));
		txtMainBalance.setEditable(false);
		txtMainBalance.setText("");
		txtMainBalance.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel17.add(jPanel20, BorderLayout.CENTER);
		jPanel20.add(txtTender, BorderLayout.CENTER);
		jPanel20.add(jLabel5, BorderLayout.WEST);
		jPanel9.add(jPanel13, null);
		jPanel13.add(jLabel8, BorderLayout.WEST);
		jPanel13.add(txtMainBalance, BorderLayout.CENTER);
		jPanel9.add(jPanel14, null);
		jPanel14.add(jLabel4, BorderLayout.CENTER);
		jPanel14.add(txtBalance, BorderLayout.EAST);
		jPanel15.add(jLabel7, BorderLayout.WEST);
		jPanel15.add(txtCarID, BorderLayout.CENTER);
		jPanel7.add(jLabel3, BorderLayout.WEST);
		jPanel7.add(txtCustName, BorderLayout.CENTER);
		jPanel12.add(txtCardType, BorderLayout.CENTER);
		jPanel12.add(jLabel2, BorderLayout.WEST);
		jPanel10.add(jPanel11, BorderLayout.CENTER);
		jPanel10.add(jPanel12, BorderLayout.EAST);
		jPanel11.add(jLabel1, BorderLayout.WEST);
		jPanel11.add(txtCardNo, BorderLayout.CENTER);
		jPanel2.add(jPanel10, null);
		jPanel2.add(jPanel7, null);
		jPanel2.add(jPanel15, null);
		jPanel2.add(jPanel9, null);
		jPanel2.add(jPanel17, null);
		jPanel2.add(jPanel16, null);
		jPanel16.add(jPanel18, null);
		jPanel18.add(txtAfterMainBalance, BorderLayout.CENTER);
		jPanel18.add(jLabel9, BorderLayout.WEST);
		jPanel16.add(jPanel8, null);
		jPanel8.add(txtAtferTender, BorderLayout.EAST);
		jPanel8.add(jLabel6, BorderLayout.CENTER);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(btnEnter, null);
		jPanel3.add(btnCanel, null);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.EAST);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
	}

	private PosKeyboard keyListener = new PosKeyboard();
	JPanel jPanel16 = new JPanel();
	JPanel jPanel17 = new JPanel();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel9 = new JPanel();
	JTextField txtCarID = new JTextField();
	BorderLayout borderLayout7 = new BorderLayout();
	JLabel jLabel7 = new JLabel();
	JPanel jPanel15 = new JPanel();
	JLabel jLabel3 = new JLabel();
	BorderLayout borderLayout10 = new BorderLayout();
	JPanel jPanel7 = new JPanel();
	JTextField txtCustName = new JTextField();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel10 = new JPanel();
	JLabel jLabel2 = new JLabel();
	JTextField txtCardNo = new JTextField();
	JPanel jPanel11 = new JPanel();
	JPanel jPanel12 = new JPanel();
	JTextField txtCardType = new JTextField();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	BorderLayout borderLayout9 = new BorderLayout();
	JPanel jPanel13 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	JLabel jLabel4 = new JLabel();
	JTextField txtBalance = new JTextField();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel jPanel14 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel18 = new JPanel();
	JTextField txtAtferTender = new JTextField();
	JPanel jPanel8 = new JPanel();
	JLabel jLabel6 = new JLabel();
	BorderLayout borderLayout6 = new BorderLayout();
	JLabel jLabel9 = new JLabel();
	JTextField txtAfterMainBalance = new JTextField();
	BorderLayout borderLayout8 = new BorderLayout();
	JTextField txtTender = new JTextField();
	BorderLayout borderLayout13 = new BorderLayout();
	JPanel jPanel20 = new JPanel();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel8 = new JLabel();
	JTextField txtMainBalance = new JTextField();
	BorderLayout borderLayout11 = new BorderLayout();

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
				case PosFunction.EXIT :
				case PosFunction.CANCEL :
					isConfirm = false;
					dispose();
					break;
				case PosFunction.ENTER :
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

	/**
	 *
	 * @return 返回用户是否按下确定键
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 *
	 * @param value 卡号
	 */
	public void setCardNo(String value) {
		txtCardNo.setText(value);
	}

	/**
	 *
	 * @param value 支付金额
	 */
	public void setTenderAmount(String value) {
		txtTender.setText(value);
	}

	/**
	 *
	 * @param value 卡余额
	 */
	public void setCardAmount(String value) {
		txtBalance.setText(value);
	}

	/**
	 * @param value 支付后卡余额
	 */
	public void setAtferTenderAmount(String value) {
		txtAtferTender.setText(value);
	}

	/**
	 *
	 * @param value 客户名称
	 */
	public void setCustName(String value) {
		txtCustName.setText(value);
	}

	/**
	 * 确认键的处理方法
	 * @param e
	 */
	void btnEnter_actionPerformed(ActionEvent e) {
		isConfirm = true;
		dispose();
	}

	/**
	 * 取消键的处理方法
	 * @param e
	 */
	void btnCanel_actionPerformed(ActionEvent e) {
		isConfirm = false;
		dispose();
	}

	/**
	 *
	 * @param value 车牌号
	 */
	public void setCarID(String value) {
		txtCarID.setText(value);
	}

	/**
	 *
	 * @param value 主账户余额
	 */
	public void setMainCardBalance(String value) {
		txtMainBalance.setText(value);
	}

	/**
	 *
	 * @param value  支付后的主账户余额
	 */
	public void setAfterMaincCardBalance(String value) {
		txtAfterMainBalance.setText(value);
	}

}

/**
 *
 * @author liangxinbiao
 */
class LoanCardConfirmSubCard_btnEnter_actionAdapter
	implements java.awt.event.ActionListener {
	LoanCardConfirmSubCard adaptee;

	LoanCardConfirmSubCard_btnEnter_actionAdapter(LoanCardConfirmSubCard adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnEnter_actionPerformed(e);
	}
}

/**
 *
 * @author liangxinbiao
 */
class LoanCardConfirmSubCard_btnCanel_actionAdapter
	implements java.awt.event.ActionListener {
	LoanCardConfirmSubCard adaptee;

	LoanCardConfirmSubCard_btnCanel_actionAdapter(LoanCardConfirmSubCard adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCanel_actionPerformed(e);
	}
}
