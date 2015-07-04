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

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;


public class DispWaiter extends JDialog {
	
	private String showWaiter;
	private boolean isConfirm=false;
	private StringBuffer inputLine = new StringBuffer();
	private int step=0;
	
	private DumbDocument showWaiterDocument = new DumbDocument();
	private PosKeyMap kmap;
	
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JPanel jPanel5 = new JPanel();
	JPanel jPanel6 = new JPanel();
	JPanel jPanel7 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JLabel jLabel1 = new JLabel();
	JTextField txtShowWaiter = new JTextField();
	
	public DispWaiter(){
		super(pos.posFrame);
		try{
			jbInit();
			
			this.setModal(true);
			setTitle("输入营业员");
			setSize(300,100);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
			    (int) ((screenSize.getWidth() - 300) /2 ),
			    (int) ((screenSize.getHeight() - 100) /2 ));
			
			txtShowWaiter.setDocument(showWaiterDocument);
			
			disableText();
			txtShowWaiter.setEditable(true);
			
			addKeyAndContainerListenerRecursively(this);
			
			kmap=PosKeyMap.getInstance();
			
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
		this.getContentPane().add(jPanel1,BorderLayout.CENTER);
		jPanel1.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel7,null);
		jPanel7.add(jLabel1, null);
		jPanel7.add(txtShowWaiter, null);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setPreferredSize(new Dimension(150, 23));
		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("输入营业员:");
		txtShowWaiter.setBackground(Color.white);
		txtShowWaiter.setFont(new java.awt.Font("Dialog", 0, 16));
    	txtShowWaiter.setPreferredSize(new Dimension(150, 30));
		txtShowWaiter.setEditable(true);
		txtShowWaiter.setText("");
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel1.add(jPanel4, BorderLayout.WEST);
		jPanel1.add(jPanel5, BorderLayout.NORTH);
	}
	
	public String getShowWaiter() {
		return showWaiter;
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	private PosKeyboard keyListener = new PosKeyboard();
	
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
					    showWaiterDocument.permit=true;
				     	if(inputLine.length()==4){
							showWaiter = inputLine.toString();
					        isConfirm=true;
					        dispose();
						}
						break;
					case PosFunction.ENTER:
					    showWaiter = inputLine.toString();
					    isConfirm=true;
					    dispose();
					    break;
					case PosFunction.EXIT:
					case PosFunction.CANCEL:
						isConfirm = false;
						dispose();
						break;
					case PosFunction.BACKSPACE:
						if (inputLine.length() > 0) {
							inputLine.deleteCharAt(inputLine.length() - 1);
                            txtShowWaiter.setText(inputLine.toString());
                            showWaiterDocument.permit=false;
						}
						break;
					default:
					    showWaiterDocument.permit=false;
					    break;
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		}
		
	
	private void disableText(){
		txtShowWaiter.setEditable(false);
	}
	
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
	