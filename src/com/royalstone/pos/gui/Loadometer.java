/*
 *POS Version 4 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;

import com.borland.jbcl.layout.*;
import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

/**
 * 地磅录入对话框
 */
public class Loadometer extends JDialog implements MainUI {
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
	JPanel jPanel10 = new JPanel();
	
	JLabel lblPrice = new JLabel();
	JLabel jLabel4 = new JLabel();
	JTextField txtQty = new TheTextField();
	JLabel jLabel5 = new JLabel();
	JTextField txtAmount = new TheTextField();	
	JLabel jLabel6 = new JLabel();
	
	JLabel jLabel7 = new JLabel();

	private PosKeyboard keyListener = new PosKeyboard();
	private TxtAmount txt = new TxtAmount();
	private OutputStream posOutputStream = null;
	private int step;

	private PosKeyMap kmap;

	/**
	 * @param out
	 */
	public Loadometer(OutputStream out) {
		super(pos.posFrame);
		posOutputStream = out;
		try {
			kmap = new PosKeyMap();
			kmap.fromXML("pos.xml");

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
			setTitle("地磅录入");
			setSize(450, 250);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 450) / 2),
				(int) ((screenSize.getHeight() - 250) / 2));

			addKeyAndContainerListenerRecursively(this);
			
			addComponentListener(new ComponentAdapter()
			{
			  public void componentShown(ComponentEvent evt)
			  {
				txtQty.requestFocus();  
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
		jPanel1.setLayout(borderLayout1);
		jPanel3.setPreferredSize(new Dimension(10, 30));
		jPanel4.setPreferredSize(new Dimension(40, 10));
		jPanel5.setPreferredSize(new Dimension(40, 10));
		jPanel6.setPreferredSize(new Dimension(10, 30));
		jPanel2.setLayout(verticalFlowLayout1);
		
		jLabel4.setFont(new java.awt.Font("Dialog", 0, 20));
		jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel4.setText("单   价:");
		lblPrice.setText("");
		lblPrice.setFont(new java.awt.Font("Dialog", 0, 20));
		lblPrice.setBorder(BorderFactory.createLineBorder(Color.black));
		lblPrice.setPreferredSize(new Dimension(200, 30));
		lblPrice.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrice.setText("");		
		
		jLabel6.setFont(new java.awt.Font("Dialog", 0, 20));
		jLabel6.setToolTipText("");
		jLabel6.setText("重   量:");
		txtQty.setBackground(Color.white);
		txtQty.setFont(new java.awt.Font("Dialog", 0, 20));
		txtQty.setPreferredSize(new Dimension(200, 30));
		txtQty.setEditable(true);
		txtQty.setText("");
		txtQty.setHorizontalAlignment(SwingConstants.RIGHT);
		
		jLabel5.setFont(new java.awt.Font("Dialog", 0, 20));
		jLabel5.setText("金   额:");
		txtAmount.setBackground(Color.white);
		txtAmount.setFont(new java.awt.Font("Dialog", 0, 20));
		txtAmount.setDoubleBuffered(false);
		txtAmount.setPreferredSize(new Dimension(200, 30));
		txtAmount.setEditable(false);
		txtAmount.setText("");
		txtAmount.setHorizontalAlignment(SwingConstants.RIGHT);		
		
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel2,BorderLayout.CENTER);				
		
		jPanel10.add(jLabel4,null);
		jPanel10.add(lblPrice,null);
		jPanel2.add(jPanel10,null);				
		jPanel8.add(jLabel6,null);
		jPanel8.add(txtQty,null);
		jPanel2.add(jPanel8,null);
		jPanel9.add(jLabel5,null);
		jPanel9.add(txtAmount,null);
		jPanel2.add(jPanel9,null);		
		jPanel7.add(jLabel7,null);
		jPanel2.add(jPanel7,null);	

		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.EAST);
		jPanel1.add(jPanel6, BorderLayout.NORTH);
	}

	/**
	 * 处理键盘动作
	 * @author HuangXuean
	 */
	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (posOutputStream != null) {
				try {
					int keyCode = e.getKeyCode();
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
	
	private class TxtAmount implements KeyListener{
		
		public void keyPressed(KeyEvent e){
			if(posOutputStream != null){
				try{
					int keyCode = e.getKeyCode();
					if(keyCode==KeyEvent.VK_ENTER || keyCode==KeyEvent.VK_CANCEL){
						posOutputStream.write(keyCode);
						posOutputStream.flush();
					}
					//else{
					//	if
					//}
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		
		public void keyReleased(KeyEvent e){
		}
		
		public void keyTyped(KeyEvent e){
		}
	}

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
	 * 
	 * @param value 交易单价
	 */
	public void setLoadometerPrice(String value) {
		lblPrice.setText(value);
	}

	/**
	 * 
	 * @param value 交易重量
	 */
	public void setQuantity(String value) {
		txtQty.setText(value);
	}
	
	/**
	 * @param 
	 **/
	public String getQuantity(){
		return txtQty.getText();
	}
	
	/**
	 * 
	 * @param value 交易总金额
	 */
	public void setAmount(String value) {
		txtAmount.setText(value);
	}
	
	public String getAmount(){
		return txtAmount.getText();
	}

	/**
	 * @param value 
	 */
	public void setInputField(String value) {
		switch (step) {
			case 0 :
				txtQty.setText(value);
				txtQty.setCaretPosition(value.length());
				break;
			case 1 :
				txtAmount.setText(value);
				txtAmount.setCaretPosition(value.length());
				break;
			
		}

	}
	/**
	 * @param value
	 */
	public void setStep(int value) {
		step = value;
		switch (step) {
			case 0 :
				disableText();
				txtQty.setEditable(true);
				txtQty.requestFocus();
				txtQty.setCaretPosition(txtQty.getText().length());
				break;
			case 1:
				disableText();
				txtAmount.setEditable(true);
				txtAmount.requestFocus();
				txtAmount.setCaretPosition(txtAmount.getText().length());					
				break;
			
		}
	}

	/**
	 * 
	 */
	private void disableText() {
		txtQty.setEditable(false);
		txtAmount.setEditable(false);
	}

	private class TheTextField extends JTextField {

		/**
		 * 屏蔽所有键盘输入的默认动作
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
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setCashier(java.lang.String)
	 */
	public void setCashier(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setDatetime(java.lang.String)
	 */
	public void setDatetime(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setDutyNo(java.lang.String)
	 */
	public void setDutyNo(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setHoldNo(java.lang.String)
	 */
	public void setHoldNo(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setPaid(java.lang.String)
	 */
	public void setPaid(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setPosNo(java.lang.String)
	 */
	public void setPosNo(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setPrompt(java.lang.String)
	 */
	public void setPrompt(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setSpCode(java.lang.String)
	 */
	public void setSpCode(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setSpName(java.lang.String)
	 */
	public void setSpName(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setSpPrice(java.lang.String)
	 */
	public void setSpPrice(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setSpQuantity(java.lang.String)
	 */
	public void setSpQuantity(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setTotal(java.lang.String)
	 */
	public void setTotal(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setTransNo(java.lang.String)
	 */
	public void setTransNo(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setUnPaid(java.lang.String)
	 */
	public void setWaiterNo(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#setUnPaid(java.lang.String)
	 */
	public void setUnPaid(String value) {
	}
	
	/**
	 * @see com.royalstone.pos.gui.MainUI#display(com.royalstone.pos.common.Sale)
	 */
	public void display(Sale s) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#display(com.royalstone.pos.common.Payment)
	 */
	public void display(Payment p) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#clear()
	 */
	public void clear() {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setConnStatus(java.lang.String)
	 */
	public void setConnStatus(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setStatus(java.lang.String)
	 */
	public void setStatus(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setUnPaidLabel(java.lang.String)
	 */
	public void setUnPaidLabel(String value) {
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#displayDiscount(com.royalstone.pos.common.Sale)
	 */
	public void displayDiscount(Sale s) {
	}
	
	/**
	 * @deprecated
	 */
	public void displayDiscount4correct(Sale s, SheetValue sheet){
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#disptotal()
	 */
	public int disptotal(SheetValue v) {
		return 1;
	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#displayprom(com.royalstone.pos.common.Sale)
	 */
	public void displayprom(Sale s) {
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
