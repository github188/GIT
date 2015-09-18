package com.royalstone.pos.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

public class InputForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel messageLabel = null;
	private JLabel iDCardLabel = null;
	private JTextField iDCardText = null;
	private JPanel topPanel = null;
	private JPanel iDCardPanel = null;

	private int frame_hight = 150;
	private int frame_width = 500;
	private int label_hight = 20;
	private int label_width = 100;
	private int text_width = 300;
	private int font_size = 20;
	private int comp_strut = 20;
	
	private boolean isConfirm;
	private KeyBoardListener keyListener = null;

	String result = null;
	
	public String getResult() {
		return result;
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	public InputForm(String title,String labelname,String msg) throws HeadlessException {
		
		super(pos.posFrame);
		
		System.setProperty( "java.awt.im.style", "no-spot" );
		
		this.keyListener = new KeyBoardListener();
		
		drawUI(title,labelname,msg);
		
		addKeyAndContainerListenerRecursively(this);
	}
	
	private void addKeyAndContainerListenerRecursively(Component c) {
		c.removeKeyListener(this.keyListener);
		c.addKeyListener(this.keyListener);
		if ((c instanceof Container)) {
			Container cont = (Container) c;
			Component[] children = cont.getComponents();
			for (int i = 0; i < children.length; i++) {
				addKeyAndContainerListenerRecursively(children[i]);
			}
		}
	}

	private void removeKeyAndContainerListenerRecursively(Component c) {
		c.removeKeyListener(this.keyListener);
		if ((c instanceof Container)) {
			Container cont = (Container) c;
			Component[] children = cont.getComponents();
			for (int i = 0; i < children.length; i++) {
				removeKeyAndContainerListenerRecursively(children[i]);
			}
		}
	}
	
	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
		result = this.iDCardText.getText().trim();
		dispose();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InputForm form = new InputForm("身份证","身份证","此商品为含麻黄碱类复方制剂,予于登记身份证号码!");
		form.setVisible(true);

	}

	private void drawUI(String title,String labelname,String msg) 
	{
		setTitle(title);
		
		setModal(true);
		
		setSize(this.frame_width, this.frame_hight);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) ((screenSize.getWidth() - this.frame_width) / 2.0D),
				(int) ((screenSize.getHeight() - this.frame_hight) / 2.0D));
		
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new BoxLayout(this.topPanel, BoxLayout.Y_AXIS));
		
		this.iDCardPanel = new JPanel();
		this.iDCardPanel.setLayout(new BoxLayout(this.iDCardPanel, BoxLayout.X_AXIS));
		
		this.iDCardLabel = new JLabel(labelname);		
		this.iDCardLabel.setFont(new Font("Dialog", 0, font_size));
		this.iDCardLabel.setPreferredSize(new Dimension(label_width,label_hight));
		//this.cflyLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.iDCardText = new JTextField();
		this.iDCardText.setFont(new Font("Dialog", 0, font_size));
		this.iDCardText.setPreferredSize(new Dimension(text_width, label_hight));
		this.iDCardPanel.add(Box.createHorizontalStrut(comp_strut));
		this.iDCardPanel.add(this.iDCardLabel);
		this.iDCardPanel.add(this.iDCardText);
		this.iDCardPanel.add(Box.createHorizontalStrut(comp_strut));
		
		this.messageLabel = new JLabel(msg );
		this.messageLabel.setForeground(Color.RED);
		this.messageLabel.setFont(new Font("Dialog", 0, 18));
		this.messageLabel.setPreferredSize(new Dimension(label_width,label_hight));
		
		this.topPanel.add(Box.createVerticalStrut(30));
		this.topPanel.add(this.iDCardPanel);
		this.topPanel.add(Box.createVerticalStrut(5));
		this.topPanel.add(this.messageLabel);
		this.topPanel.add(Box.createVerticalStrut(50));
		
		getContentPane().add(this.topPanel, "Center");
	}

	private class KeyBoardListener implements KeyListener {
		int c;
		int key;
		
		private PosKeyMap kmap = PosKeyMap.getInstance();

		private KeyBoardListener() {
		}


		public void keyPressed(KeyEvent e) {
			if (!matchModifiers(e.getModifiersEx())) {
				try {
					c = e.getKeyCode();
					
					//System.out.print(e.getComponent().getName());
					
					if(e.getComponent() instanceof JTextField)
					{
						((JTextField)e.getComponent()).getText().trim();
					}
					
					key = this.kmap.getFunction(c).getKey();

					switch (key) {

					case PosFunction.ENTER:
						if(iDCardText.getText().trim().length() > 0)
							setConfirm(true);
						break;
					case PosFunction.CANCEL:
					case PosFunction.EXIT:
						setConfirm(false);
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		public boolean matchModifiers(int i) {
			if ((i & 0x200) > 0) {
				return true;
			}
			if ((i & 0x80) > 0) {
				return true;
			}
			if ((i & 0x40) > 0) {
				return true;
			}
			return false;
		}
	}

}
