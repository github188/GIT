/*
 * Created on 2004-7-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;

/**
 * <p>Title: posv41</p>
 * <p>Description: study java for posv41</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: royalstone company </p>
 * @author huang xuean
 * @version 1.0
 */

public class AboutDialog extends JDialog {
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JButton Enter = new JButton();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel3 = new JLabel();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JTextArea jTextArea1 = new JTextArea();
	JPanel jPanel6 = new JPanel();

	private PosKeyMap kmap;

	public AboutDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			kmap = PosKeyMap.getInstance();

			jbInit();
			this.setSize(350, 210);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 350) / 2),
				(int) ((screenSize.getHeight() - 210) / 2));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public AboutDialog() {
		this(null, "", false);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(borderLayout1);
		jPanel2.setLayout(gridLayout1);
		Enter.setFont(new java.awt.Font("Dialog", 0, 14));
		Enter.setAlignmentX((float) 0.0);
		Enter.setActionCommand("jButton1");
		Enter.setContentAreaFilled(true);
		Enter.setText("确 认");
		Enter.addActionListener(new AboutDialog_Enter_actionAdapter(this));
		PosKeyboard keylistener = new PosKeyboard();
		Enter.addKeyListener(keylistener);
		gridLayout1.setColumns(4);
		gridLayout1.setHgap(2);
		gridLayout1.setVgap(2);
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setDebugGraphicsOptions(0);
		jPanel1.setLayout(gridLayout2);
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 14));
		jLabel1.setText("Version: 4.1.T");
		jLabel1.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 14));
		jLabel3.setVerifyInputWhenFocusTarget(true);
		jLabel3.setText("POS Platform");
		jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
		gridLayout2.setColumns(1);
		gridLayout2.setHgap(0);
		gridLayout2.setRows(4);
		panel1.setMinimumSize(new Dimension(342, 100));
		panel1.setPreferredSize(new Dimension(342, 100));
		panel1.setToolTipText("");
		this.setTitle("Royalstone POS");
		jTextArea1.setBackground(new Color(212, 208, 200));
		jTextArea1.setFont(new java.awt.Font("Dialog", 0, 14));
		jTextArea1.setEditable(false);
		jTextArea1.setOpaque(false);
		jTextArea1.setMargin(new Insets(0, 0, 0, 0));
		jTextArea1.setSelectedTextColor(Color.black);
		jTextArea1.setText(
			"(c) Copyright 1992-2004广州融通系统集成有限公司. "
				+ " Visit http://www.royaltone.com.cn");
		jTextArea1.setLineWrap(true);
		jTextArea1.setRows(3);
		jTextArea1.addKeyListener(keylistener);
		getContentPane().add(panel1);
		panel1.add(jPanel1, BorderLayout.CENTER);
		panel1.add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jPanel5, null);
		jPanel2.add(jPanel4, null);
		jPanel2.add(jPanel3, null);
		jPanel2.add(Enter, null);
		jPanel1.add(jLabel3, null);
		jPanel1.add(jLabel1, null);
		jPanel1.add(jTextArea1, null);
		jPanel1.add(jPanel6, null);
	}

	void Enter_actionPerformed(ActionEvent e) {
		dispose();
	}

	class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			int keyCode = kmap.getFunction(e.getKeyCode()).getKey();
			switch (keyCode) {

				case PosFunction.ENTER :
				case PosFunction.EXIT :
				case PosFunction.CANCEL :
					dispose();
					break;

			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}
}

class AboutDialog_Enter_actionAdapter
	implements java.awt.event.ActionListener {
	AboutDialog adaptee;

	AboutDialog_Enter_actionAdapter(AboutDialog adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.Enter_actionPerformed(e);
	}
}
