package com.royalstone.pos.gui;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ShopClockInput extends JDialog {
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JComboBox comboWorkDate = new JComboBox();
	JLabel jLabel2 = new JLabel();
	BorderLayout borderLayout3 = new BorderLayout();
	JComboBox comboDutyNo = new JComboBox();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	JButton btnCancel = new JButton();
	JButton btnEnter = new JButton();
	JLabel jLabel3 = new JLabel();

	public ShopClockInput(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			this.setTitle("班次信息");
			setSize(450, 200);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 450) / 2),
				(int) ((screenSize.getHeight() - 200) / 2));
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ShopClockInput() {
		this(null, "", true);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(borderLayout1);
		jPanel1.setLayout(verticalFlowLayout1);
		jPanel4.setLayout(borderLayout2);
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("工作日期:");
		jLabel2.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel2.setText("班次:");
		jPanel3.setLayout(borderLayout3);
		comboWorkDate.setFont(new java.awt.Font("Dialog", 0, 16));
		comboWorkDate.setPreferredSize(new Dimension(120, 22));
		comboDutyNo.setFont(new java.awt.Font("Dialog", 0, 16));
		comboDutyNo.setPreferredSize(new Dimension(120, 22));
		this.setFont(new java.awt.Font("Dialog", 0, 16));
		btnCancel.setFont(new java.awt.Font("Dialog", 0, 16));
		btnCancel.setPreferredSize(new Dimension(67, 30));
		btnCancel.setSelectedIcon(null);
		btnCancel.setText("取消");
		btnCancel.addActionListener(
			new ShopClockInput_btnCancel_actionAdapter(this));
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 16));
		btnEnter.setPreferredSize(new Dimension(67, 30));
		btnEnter.setText("确定");
		btnEnter.addActionListener(
			new ShopClockInput_btnEnter_actionAdapter(this));
		jPanel5.setPreferredSize(new Dimension(90, 10));
		jPanel5.setRequestFocusEnabled(true);
		jPanel2.setPreferredSize(new Dimension(10, 30));
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 13));
		jLabel3.setText("由于本地保存班次信息的文件损坏，请重新选择现在的正确班次信息");
		getContentPane().add(panel1);
		panel1.add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel4, null);
		jPanel4.add(jLabel1, BorderLayout.CENTER);
		jPanel4.add(comboWorkDate, BorderLayout.EAST);
		jPanel1.add(jPanel3, null);
		jPanel3.add(jLabel2, BorderLayout.CENTER);
		jPanel3.add(comboDutyNo, BorderLayout.EAST);
		panel1.add(jPanel2, BorderLayout.NORTH);
		jPanel2.add(jLabel3, null);
		panel1.add(jPanel5, BorderLayout.EAST);
		panel1.add(jPanel6, BorderLayout.SOUTH);
		jPanel6.add(btnEnter, null);
		jPanel6.add(btnCancel, null);
	}

	void btnEnter_actionPerformed(ActionEvent e) {

	}

	void btnCancel_actionPerformed(ActionEvent e) {

	}
}

class ShopClockInput_btnEnter_actionAdapter
	implements java.awt.event.ActionListener {
	ShopClockInput adaptee;

	ShopClockInput_btnEnter_actionAdapter(ShopClockInput adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnEnter_actionPerformed(e);
	}
}

class ShopClockInput_btnCancel_actionAdapter
	implements java.awt.event.ActionListener {
	ShopClockInput adaptee;

	ShopClockInput_btnCancel_actionAdapter(ShopClockInput adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCancel_actionPerformed(e);
	}
}
