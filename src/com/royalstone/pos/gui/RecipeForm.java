package com.royalstone.pos.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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

public class RecipeForm extends JDialog {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	private JLabel cflyLabel = null;
	private JLabel cfshrLabel = null;
	private JLabel cfdprLabel = null;
	private JLabel cffhrLabel = null;
	
	private JTextField cflyText = null;
	private JTextField cfshrText = null;
	private JTextField cfdprText = null;
	private JTextField cffhrText = null;
	
	private JPanel topPanel = null;
	//private JPanel bottomPanel = null;

	private JPanel cflyPanel = null;
	private JPanel cfshrPanel = null;
	private JPanel cfdprPanel = null;
	private JPanel cffhrPanel = null;

	private int frame_hight = 220;
	private int frame_width = 500;
	
	private int label_hight = 20;
	private int label_width = 100;
	private int text_width = 300;
	private int font_size = 20;
	private int comp_strut = 20;
	
	private static String cflyName = "cfly";
	private static String cfshrName = "cfshr";
	private static String cfdprName = "cfdpr";
	private static String cffhrName = "cffhr";

	private boolean isConfirm;

	private PayFormKeyboard keyListener = null;

	private String[] results = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RecipeForm form = new RecipeForm();
		form.setVisible(true);
		if(form.isConfirm())
		{
			for(String item : form.getResult())
			System.out.println(item);
		}
	}

	public RecipeForm(){
		// TODO Auto-generated constructor stub		
		super(pos.posFrame);
		
		System.setProperty( "java.awt.im.style", "no-spot" );
		
		this.keyListener = new PayFormKeyboard();
		
		drawUI();
		
		addKeyAndContainerListenerRecursively(this);
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
		if(isConfirm)
		{
			results = new String[4];
			results[0] = this.cflyText.getText().trim();
			results[1] = this.cfshrText.getText().trim();
			results[2] = this.cfdprText.getText().trim();
			results[3] = this.cffhrText.getText().trim();
		}
		
		dispose();
	}

	private void drawUI() 
	{
		setTitle("处方");
		setModal(true);
		setSize(this.frame_width, this.frame_hight);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) ((screenSize.getWidth() - this.frame_width) / 2.0D),
				(int) ((screenSize.getHeight() - this.frame_hight) / 2.0D));
		
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new BoxLayout(this.topPanel, BoxLayout.Y_AXIS));
		
		this.cflyPanel = new JPanel();
		this.cflyPanel.setLayout(new BoxLayout(this.cflyPanel, BoxLayout.X_AXIS));
		this.cflyLabel = new JLabel("来源");
		this.cflyLabel.setFont(new Font("Dialog", 0, font_size));
		this.cflyLabel.setPreferredSize(new Dimension(label_width,label_hight));
		//this.cflyLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.cflyText = new JTextField();
		this.cflyText.setName(cflyName);
		this.cflyText.setFont(new Font("Dialog", 0, font_size));
		this.cflyText.setPreferredSize(new Dimension(text_width, label_hight));
		this.cflyPanel.add(Box.createHorizontalStrut(comp_strut));
		this.cflyPanel.add(this.cflyLabel);
		this.cflyPanel.add(this.cflyText);
		this.cflyPanel.add(Box.createHorizontalStrut(comp_strut));

		this.cfshrPanel = new JPanel();
		this.cfshrPanel.setLayout(new BoxLayout(this.cfshrPanel, BoxLayout.X_AXIS));
		this.cfshrLabel = new JLabel("审方人");
		this.cfshrLabel.setFont(new Font("Dialog", 0, font_size));
		this.cfshrLabel.setPreferredSize(new Dimension(label_width,label_hight));
		//this.cfshrLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.cfshrText = new JTextField();
		this.cfshrText.setName(cfshrName);
		this.cfshrText.setFont(new Font("Dialog", 0, font_size));
		this.cfshrText.setPreferredSize(new Dimension(text_width, label_hight));
		this.cfshrPanel.add(Box.createHorizontalStrut(comp_strut));
		this.cfshrPanel.add(this.cfshrLabel);
		this.cfshrPanel.add(this.cfshrText);
		this.cfshrPanel.add(Box.createHorizontalStrut(comp_strut));

		this.cfdprPanel = new JPanel();
		this.cfdprPanel.setLayout(new BoxLayout(this.cfdprPanel, BoxLayout.X_AXIS));
		this.cfdprLabel = new JLabel("调配人");
		this.cfdprLabel.setFont(new Font("Dialog", 0, font_size));
		this.cfdprLabel.setPreferredSize(new Dimension(label_width,label_hight));
		//this.cfdprLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.cfdprText = new JTextField();
		this.cfdprText.setName(cfdprName);
		this.cfdprText.setFont(new Font("Dialog", 0, font_size));
		this.cfdprText.setPreferredSize(new Dimension(text_width, label_hight));
		this.cfdprPanel.add(Box.createHorizontalStrut(comp_strut));
		this.cfdprPanel.add(this.cfdprLabel);
		this.cfdprPanel.add(this.cfdprText);
		this.cfdprPanel.add(Box.createHorizontalStrut(comp_strut));

		this.cffhrPanel = new JPanel();
		this.cffhrPanel.setLayout(new BoxLayout(this.cffhrPanel, BoxLayout.X_AXIS));
		this.cffhrLabel = new JLabel("复核发药人");
		this.cffhrLabel.setFont(new Font("Dialog", 0, font_size));
		this.cffhrLabel.setPreferredSize(new Dimension(label_width,label_hight));
		//this.cffhrLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.cffhrText = new JTextField();
		this.cffhrText.setName(cffhrName);
		this.cffhrText.setFont(new Font("Dialog", 0, font_size));
		this.cffhrText.setPreferredSize(new Dimension(text_width, label_hight));
		this.cffhrPanel.add(Box.createHorizontalStrut(comp_strut));
		this.cffhrPanel.add(this.cffhrLabel);
		this.cffhrPanel.add(this.cffhrText);
		this.cffhrPanel.add(Box.createHorizontalStrut(comp_strut));

		this.topPanel.add(Box.createVerticalStrut(20));
		this.topPanel.add(this.cflyPanel);
		this.topPanel.add(this.cfshrPanel);
		this.topPanel.add(this.cfdprPanel);
		this.topPanel.add(this.cffhrPanel);
		this.topPanel.add(Box.createVerticalStrut(35));

		getContentPane().add(this.topPanel, "Center");
		
	}

	public String[] getResult()
	{
		return results;
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
	
	private class PayFormKeyboard implements KeyListener {
		int c;
		int key;
		String buffer;

		private PosKeyMap kmap = PosKeyMap.getInstance();

		private PayFormKeyboard() {
		}

		public void keyPressed(KeyEvent e) {
			if (!matchModifiers(e.getModifiersEx())) {
				try {
					c = e.getKeyCode();
					
					//System.out.print(e.getComponent().getName());
					
					if(e.getComponent() instanceof JTextField)
					{
						buffer = ((JTextField)e.getComponent()).getText().trim();
						//System.out.print(buffer);
					}
					
					key = this.kmap.getFunction(c).getKey();

					switch (key) {

					case PosFunction.UP:
					case PosFunction.DOWN:
						doEvent(e.getComponent().getName(),key);
						break;
					case PosFunction.ENTER:
						doEvent(e.getComponent().getName(),PosFunction.DOWN);
						break;
					case PosFunction.CASH:
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
		
		public void doEvent(String name,int key)
		{
			if(key == PosFunction.DOWN)
			{
				if(name.equals(cflyName))
					cfshrText.requestFocus();
				else if(name.equals(cfshrName))
					cfdprText.requestFocus();
				else if(name.equals(cfdprName))
					cffhrText.requestFocus();
				else if(name.equals(cffhrName))
					cflyText.requestFocus();
			}
			else if(key == PosFunction.UP)
			{
				if(name.equals(cfdprName))
					cfshrText.requestFocus();
				else if(name.equals(cffhrName))
					cfdprText.requestFocus();
				else if(name.equals(cflyName))
					cffhrText.requestFocus();
				else if(name.equals(cfshrName))
					cflyText.requestFocus();
			}	
		}
	}

}
