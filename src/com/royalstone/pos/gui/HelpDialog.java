/*
 * Created on 2004-7-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * <p>Title: posv41</p>
 * <p>Description: study java for posv41</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: royalstone company </p>
 * @author HuangXuean
 * @version 1.0
 */

public class HelpDialog extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JButton jButton1 = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  Border border1;
  JTree jTree1 = new JTree();

  public HelpDialog(Frame frame, String title, boolean modal) {
	super(frame, title, modal);
	try {
	  jbInit();
	  this.setSize(400,300);
	}
	catch(Exception ex) {
	  ex.printStackTrace();
	}
  }

  public HelpDialog() {
	this(null, "", false);
  }

  private void jbInit() throws Exception {
	border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(148, 145, 140));
	panel1.setLayout(borderLayout1);
	panel1.setMinimumSize(new Dimension(342, 100));
	panel1.setPreferredSize(new Dimension(342, 100));
	panel1.setToolTipText("");
	this.setTitle("Help-Royalstone POS Platform");
	jPanel2.setLayout(flowLayout1);
	jLabel1.setText("jLabel1");
	jTextField1.setText("jTextField1");
	jButton1.setText("jButton1");
	jPanel1.setLayout(borderLayout2);
	jPanel2.setBorder(border1);
	jPanel2.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
	getContentPane().add(panel1);
	panel1.add(jPanel1, BorderLayout.CENTER);
	jPanel1.add(jPanel3,  BorderLayout.WEST);
	jPanel3.add(jTree1, null);
	jPanel1.add(jPanel4,  BorderLayout.CENTER);
	panel1.add(jPanel2, BorderLayout.NORTH);
	jPanel2.add(jLabel1, null);
	jPanel2.add(jTextField1, null);
	jPanel2.add(jButton1, null);
  }

  void Enter_actionPerformed(ActionEvent e) {
	dispose();
  }
}
