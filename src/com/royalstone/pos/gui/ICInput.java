package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

/**
 * IC卡的刷卡对话框
 * @author liangxinbiao
 */
public class ICInput extends JDialog {
	JPanel jPanel2 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel1 = new JPanel();
	JLabel jLabel1 = new JLabel();
	JTextField cardValue = new JTextField();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel6 = new JLabel();
	FlowLayout flowLayout1 = new FlowLayout();

	StringBuffer inputcode = new StringBuffer();

	private volatile boolean isFinish = false;
	private boolean isConfirm = false;

	private PosKeyMap kmap;

	/**
	 * 返回用户是否按下确定键
	 * @return
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 * 处理键盘动作 
	 * @author liangxinbiao
	 */
	private class CardKeyListener extends KeyAdapter {
		public void keyPressed(java.awt.event.KeyEvent e) {

			switch (kmap.getFunction(e.getKeyCode()).getKey()) {
				case PosFunction.ENTER :
				case PosFunction.CANCEL :
					isFinish = true;
					dispose();
					break;
			}
		}
	}

	public ICInput() {
		super(pos.posFrame, "IC卡", false);
		try {
			jbInit();
			setSize(450, 200);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 450) / 2),
				(int) ((screenSize.getHeight() - 200) / 2));

			kmap = PosKeyMap.getInstance();

			CardKeyListener cl = new CardKeyListener();
			cardValue.addKeyListener(cl);

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					isFinish = true;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * JBuilder自动生成的初始化界面方法
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		jPanel2.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		gridLayout1.setRows(3);
		jPanel1.setLayout(flowLayout1);
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("请刷卡：");
		cardValue.setBackground(Color.white);
		cardValue.setFont(new java.awt.Font("Dialog", 0, 16));
		cardValue.setMinimumSize(new Dimension(6, 22));
		cardValue.setPreferredSize(new Dimension(200, 40));
		cardValue.setEditable(false);
		cardValue.setText("");
		jLabel6.setText("");
		jLabel5.setAlignmentY((float) 0.5);
		jLabel5.setText("");
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jLabel6, null);
		jPanel2.add(jPanel1, null);
		jPanel1.add(jLabel1, null);
		jPanel1.add(cardValue, null);
		jPanel2.add(jLabel5, null);
	}

	/**
	 * 
	 * @return 卡号
	 */
	public String getInputcode() {
		return inputcode.toString();
	}

	/**
	 * 
	 * @return 用户是否完成刷卡动作
	 */
	public boolean isFinish() {
		return isFinish;
	}

	/**
	 * 显示“*”号 
	 */
	public void showStar() {
		cardValue.setText("*************");
	}

}