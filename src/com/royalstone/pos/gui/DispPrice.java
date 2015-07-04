package com.royalstone.pos.gui;

/*
 * Created on 2004-9-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

public class DispPrice extends JDialog implements MainUI {

	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	JPanel jPanel7 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JTextField txtprice = new TheTextField();

	private PosKeyboard keyListener = new PosKeyboard();
	private OutputStream posOutputStream = null;
	private boolean isConfirm = false;

	private PosKeyMap kmap;

	public DispPrice(OutputStream out) {
		super(pos.posFrame);
		posOutputStream = out;
		try {

			kmap = PosKeyMap.getInstance();

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					try {
						int keyCode =
							kmap.getKeyValue(new PosInput(PosFunction.EXIT));
						posOutputStream.write(keyCode);
						posOutputStream.flush();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			jbInit();

			setTitle("输入商品单价");
			setSize(300, 100);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 300) / 2),
				(int) ((screenSize.getHeight() - 100) / 2));

			addKeyAndContainerListenerRecursively(this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void jbInit() throws Exception {
		jPanel1.setLayout(borderLayout1);
		jPanel2.setMinimumSize(new Dimension(50, 10));
		jPanel2.setPreferredSize(new Dimension(50, 20));
		jPanel3.setMinimumSize(new Dimension(10, 10));
		jPanel3.setPreferredSize(new Dimension(30, 10));
		jPanel4.setMinimumSize(new Dimension(10, 10));
		jPanel4.setPreferredSize(new Dimension(10, 20));
		jPanel5.setMinimumSize(new Dimension(10, 10));
		jPanel5.setPreferredSize(new Dimension(30, 10));
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel7, null);
		jPanel7.add(jLabel1, null);
		jPanel7.add(txtprice, null);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setPreferredSize(new Dimension(150, 23));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("商品单价(元):");
		txtprice.setBackground(Color.white);
		txtprice.setFont(new java.awt.Font("Dialog", 0, 16));
		txtprice.setDoubleBuffered(false);
		txtprice.setPreferredSize(new Dimension(150, 30));
		txtprice.setEditable(true);
		txtprice.setText("");
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.NORTH);
	}

	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (posOutputStream != null) {
				try {
					int keyCode = e.getKeyCode();
					if (kmap.getFunction(keyCode).getKey()
						== PosFunction.ENTER) {
						isConfirm = true;
					}
					posOutputStream.write(keyCode);
					posOutputStream.flush();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		/* （非 Javadoc）
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent e) {
		}

		/* （非 Javadoc）
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent e) {
		}
	}

	public boolean getconfirm() {
		return isConfirm;
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

	public void setprice(String value) {
		txtprice.setText(value);
	}

	public void setInputField(final String value) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				txtprice.requestFocus();
				txtprice.setText(value);
				txtprice.setCaretPosition(value.length());
			}
		});
	}

	public void set() {
		txtprice.requestFocus();
		disableText();
		txtprice.setEditable(true);
		txtprice.setCaretPosition(txtprice.getText().length());
	}

	private void disableText() {
		txtprice.setEditable(false);
	}

	private class TheTextField extends JTextField {

		/* （非 Javadoc）
		 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
		 */
		protected boolean processKeyBinding(
			KeyStroke ks,
			KeyEvent e,
			int condition,
			boolean pressed) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setCashier(java.lang.String)
	 */
	public void setCashier(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setDatetime(java.lang.String)
	 */
	public void setDatetime(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setDutyNo(java.lang.String)
	 */
	public void setDutyNo(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setHoldNo(java.lang.String)
	 */
	public void setHoldNo(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setWaiterNo(java.lang.String)
	 */
	public void setWaiterNo(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setPaid(java.lang.String)
	 */
	public void setPaid(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setPosNo(java.lang.String)
	 */
	public void setPosNo(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setPrompt(java.lang.String)
	 */
	public void setPrompt(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setSpCode(java.lang.String)
	 */
	public void setSpCode(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setSpName(java.lang.String)
	 */
	public void setSpName(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setSpPrice(java.lang.String)
	 */
	public void setSpPrice(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setSpQuantity(java.lang.String)
	 */
	public void setSpQuantity(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setTotal(java.lang.String)
	 */
	public void setTotal(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setTransNo(java.lang.String)
	 */
	public void setTransNo(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setUnPaid(java.lang.String)
	 */
	public void setUnPaid(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#display(com.royalstone.pos.common.Sale)
	 */
	public void display(Sale s) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#display(com.royalstone.pos.common.Payment)
	 */
	public void display(Payment p) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setConnStatus(java.lang.String)
	 */
	public void setConnStatus(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setStatus(java.lang.String)
	 */
	public void setStatus(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setStep(int)
	 */
	public void setStep(int value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#setUnPaidLabel(java.lang.String)
	 */
	public void setUnPaidLabel(String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#displayDiscount(com.royalstone.pos.common.Sale)
	 */
	public void displayDiscount(Sale s) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#displayDiscount4correct(com.royalstone.pos.common.Sale, com.royalstone.pos.common.SheetValue)
	 */
	public void displayDiscount4correct(Sale s, SheetValue sheet) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#disptotal(com.royalstone.pos.common.SheetValue)
	 */
	public int disptotal(SheetValue v) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.gui.MainUI#displayprom(com.royalstone.pos.common.Sale)
	 */
	public void displayprom(Sale s) {
		// TODO Auto-generated method stub

	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#dispMemberCardHeader(com.royalstone.pos.card.LoanCardQueryVO)
	 */
	public int dispMemberCard(MemberCard memberCard) {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setWorkDay(java.lang.String)
	 */
	public void setWorkDay(String value) {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setTotalQty(java.lang.String)
	 */
	public void setTotalQty(String value) {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setPrinterStatus(boolean)
	 */
	public void setPrinterStatus(boolean value) {
		// TODO 自动生成方法存根
		
	}

}
