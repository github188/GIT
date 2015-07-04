package com.royalstone.pos.gui;

import java.awt.*;

import javax.swing.*;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.util.Value;
import java.awt.event.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TenderDialog extends JDialog {
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JTextField txtPay = new TheTextField();
	JLabel jLabel1 = new JLabel();
	JTextField txtReturn = new JTextField();
	JLabel lblReturn = new JLabel();
	JPanel jPanel5 = new JPanel();
	JTextField txtToPay = new JTextField();
	JLabel jLabel3 = new JLabel();
	JButton btnCancle = new JButton();
	JButton btnEnter = new JButton();
	FlowLayout flowLayout1 = new FlowLayout();
	JPanel jPanel6 = new JPanel();
	SheetValue sheetValue;
	boolean isConfirm = false;

	PosKeyMap kmap = PosKeyMap.getInstance();
	private PosKeyboard keyListener = new PosKeyboard();

	public TenderDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			this.setSize(300, 265);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 300) / 2),
				(int) ((screenSize.getHeight() - 265) / 2));

			addKeyAndContainerListenerRecursively(this);

			this.addWindowListener(new WindowAdapter() {
				public void windowActivated(WindowEvent e) {
					btnEnter.requestFocus(true);
				}
			});

			this.txtPay.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent arg0) {
					txtPay.setCaretPosition(txtPay.getText().length());
				}

				public void focusLost(FocusEvent arg0) {
					//btnEnter.requestFocus(true);
				}

			});

			this.btnCancle.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent arg0) {
				}

				public void focusLost(FocusEvent arg0) {
					txtPay.requestFocus();
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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

	public TenderDialog() {
		this(null, "", false);
	}

	// zhouzhou 引用港币计算
	public void setSheetValue(SheetValue value) {

		this.sheetValue = value;

		refreshValue();
	}

	public boolean isConfirm() {
		return isConfirm;
	}
	
	public int getPay(){
		
		if(sheetValue!=null){
			return sheetValue.getValuePaid();
		}
		
		return 0;
	}

	private void refreshValue() {
		
		if (sheetValue != null) {
			
			this.txtToPay.setText(
				(new Value(sheetValue.getValueTotal())).toString());

			this.txtPay.setText(
				(new Value(sheetValue.getValuePaid())).toString());

			if (sheetValue.getValueUnPaid() <= 0) {

				this.lblReturn.setText("找  赎:");

				this.txtReturn.setText(
					new Value(-sheetValue.getValueUnPaid()).toString());

			} else {

				this.lblReturn.setText("待  收:");

				this.txtReturn.setText(
					new Value(sheetValue.getValueUnPaid()).toString());

			}

		}

	}

	private void jbInit() throws Exception {
		panel1.setLayout(borderLayout1);
		jPanel1.setLayout(flowLayout1);
		jPanel2.setLayout(borderLayout3);
		txtPay.setFont(new java.awt.Font("Dialog", 0, 20));
		txtPay.setPreferredSize(new Dimension(180, 50));
		txtPay.setText("0.00");
		txtPay.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setFont(new java.awt.Font("Dialog", 0, 20));
		jLabel1.setRequestFocusEnabled(false);
		jLabel1.setText("实  收:");
		txtReturn.setBackground(Color.white);
		txtReturn.setEnabled(true);
		txtReturn.setFont(new java.awt.Font("Dialog", 0, 20));
		txtReturn.setPreferredSize(new Dimension(180, 50));
		txtReturn.setRequestFocusEnabled(false);
		txtReturn.setEditable(false);
		txtReturn.setText("0.00");
		txtReturn.setHorizontalAlignment(SwingConstants.RIGHT);
		lblReturn.setFont(new java.awt.Font("Dialog", 0, 20));
		lblReturn.setRequestFocusEnabled(false);
		lblReturn.setText("找  赎:");
		txtToPay.setBackground(Color.white);
		txtToPay.setEnabled(true);
		txtToPay.setFont(new java.awt.Font("Dialog", 0, 20));
		txtToPay.setPreferredSize(new Dimension(180, 50));
		txtToPay.setRequestFocusEnabled(false);
		txtToPay.setEditable(false);
		txtToPay.setSelectionStart(0);
		txtToPay.setText("0.00");
		txtToPay.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 20));
		jLabel3.setRequestFocusEnabled(false);
		jLabel3.setText("应  收:");
		btnCancle.setFont(new java.awt.Font("Dialog", 0, 20));
		btnCancle.setPreferredSize(new Dimension(120, 35));
		btnCancle.setText("取消");
		btnCancle.addActionListener(
			new TenderDialog_btnCancle_actionAdapter(this));
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 20));
		btnEnter.setPreferredSize(new Dimension(120, 35));
		btnEnter.setText("确定");
		btnEnter.addActionListener(
			new TenderDialog_btnEnter_actionAdapter(this));
		panel1.setPreferredSize(new Dimension(148, 120));
		panel1.setRequestFocusEnabled(false);
		jPanel1.setPreferredSize(new Dimension(255, 50));
		jPanel1.setRequestFocusEnabled(false);
		jPanel2.setRequestFocusEnabled(false);
		jPanel3.setRequestFocusEnabled(false);
		jPanel4.setRequestFocusEnabled(false);
		jPanel5.setRequestFocusEnabled(false);
		jPanel6.setRequestFocusEnabled(false);
		getContentPane().add(panel1);
		panel1.add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(btnEnter, null);
		jPanel1.add(btnCancle, null);
		panel1.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(jPanel3, BorderLayout.CENTER);
		jPanel3.add(jLabel1, null);
		jPanel3.add(txtPay, null);
		jPanel2.add(jPanel4, BorderLayout.SOUTH);
		jPanel4.add(lblReturn, null);
		jPanel4.add(txtReturn, null);
		jPanel2.add(jPanel5, BorderLayout.NORTH);
		jPanel5.add(jLabel3, null);
		jPanel5.add(txtToPay, null);
		panel1.add(jPanel6, BorderLayout.NORTH);
	}

	void btnEnter_actionPerformed(ActionEvent e) {

		isConfirm = true;
		this.dispose();

	}

	void btnCancle_actionPerformed(ActionEvent e) {

		isConfirm = false;
		this.dispose();

	}

	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {

			int c = e.getKeyCode();

			if (c != KeyEvent.VK_TAB) {

				if (txtPay.hasFocus()) {

					String buffer = txtPay.getText();

					if (keyFunc(c) >= '0' && keyFunc(c) <= '9') {
						buffer += (char) keyFunc(c);
						buffer = normalizeDecimalBuffer(buffer, 6, 2);
						txtPay.setText(buffer);
						txtPay.setCaretPosition(buffer.length());
					}

					if (keyFunc(c) == PosFunction.BACKSPACE) {
						if (buffer.length() > 0)
							buffer = buffer.substring(0, buffer.length() - 1);
						txtPay.setText(buffer);
						txtPay.setCaretPosition(buffer.length());
					}

					if (keyFunc(c) == PosFunction.POINT
						&& buffer.indexOf(".") < 0) {
						buffer += '.';
						txtPay.setText(buffer);
						txtPay.setCaretPosition(buffer.length());
					}

					if (keyFunc(c) == PosFunction.BIZERO) {
						buffer += "00";
						txtPay.setText(buffer);
						txtPay.setCaretPosition(buffer.length());
					}

					if (keyFunc(c) == PosFunction.ENTER) {

						sheetValue.setValue(
							sheetValue.getValueTotal(),
							(int) Math.rint(atof(buffer) * 100),
							0,
							0,
							0);

						refreshValue();
						btnEnter.requestFocus(true);

					}

					if (keyFunc(c) == PosFunction.CANCEL)
						btnCancle_actionPerformed(null);
					if (keyFunc(c) == PosFunction.EXIT)
						btnCancle_actionPerformed(null);

				} else {

					switch (keyFunc(c)) {
						case PosFunction.ENTER :
							if (btnEnter.hasFocus()) {
								btnEnter_actionPerformed(null);
							} else if (btnCancle.hasFocus()) {
								btnCancle_actionPerformed(null);
							}
							break;
						case PosFunction.CANCEL :
							btnCancle_actionPerformed(null);
							break;
						case PosFunction.EXIT :
							btnCancle_actionPerformed(null);
							break;
					}

				}

			}

		}

		public void keyReleased(KeyEvent arg0) {

		}

		public void keyTyped(KeyEvent arg0) {

		}

	}

	private int keyFunc(int keyCode) {

		return kmap.getFunction(keyCode).getKey();

	}

	private class TheTextField extends JTextField {

		protected boolean processKeyBinding(
			KeyStroke ks,
			KeyEvent e,
			int condition,
			boolean pressed) {
			return false;
		}
	}

	private String normalizeDecimalBuffer(String buffer, int n, int m) {
		int offset = buffer.indexOf('.');
		if (offset < 0) {
			if (buffer.length() > n)
				buffer = buffer.substring(0, n);
			int i = atoi(buffer);
			buffer = "" + i;
			return buffer;
		}

		if (offset >= 0 && !buffer.endsWith(".")) {
			String a = buffer.substring(0, offset);
			String b = buffer.substring(offset + 1, buffer.length());
			if (b.length() > m)
				b = b.substring(0, m);
			buffer = "" + atoi(a) + "." + b;
		}
		return buffer;
	}

	private int atoi(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double atof(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}

class TenderDialog_btnEnter_actionAdapter
	implements java.awt.event.ActionListener {
	TenderDialog adaptee;

	TenderDialog_btnEnter_actionAdapter(TenderDialog adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnEnter_actionPerformed(e);
	}
}

class TenderDialog_btnCancle_actionAdapter
	implements java.awt.event.ActionListener {
	TenderDialog adaptee;

	TenderDialog_btnCancle_actionAdapter(TenderDialog adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCancle_actionPerformed(e);
	}
}
