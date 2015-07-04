/*
 * Created on 2004-10-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.gui.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.royalstone.pos.gui.AboutDialog;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PopupMenu extends JFrame {
	JPopupMenu popup  = new JPopupMenu("Start");
	JPopupMenu popup1 = new JPopupMenu("文件");
	JPopupMenu popup2 = new JPopupMenu("帮助");
	
	public  PopupMenu(){	
			setTitle("MenuTest");
			setSize(200,200);

			//kmap = new PosKeyMap();
			//kmap.fromXML("pos.xml");
		   
		    popup.add(popup1);
		    popup.add(popup2);
		    
			JMenuItem mi = new JMenuItem("最小化");
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setState(JFrame.ICONIFIED);
				}
			});
			popup1.add(mi);

			mi = new JMenuItem("退出");
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(1); 
				}
			});
			popup1.add(mi);

			JMenuItem help = new JMenuItem("帮助正文");
			help.setFont(new java.awt.Font("Dialog", 0, 16));
			popup2.add(help);

			mi = new JMenuItem("关于 POS4.1");
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AboutDialog about = new AboutDialog();
					about.show();
				}
			});
			popup2.add(mi);

			HelpSet helpset = null;
			ClassLoader loader = null;
			URL url = HelpSet.findHelpSet(loader, "help/hello.hs");
			try {
				helpset = new HelpSet(loader, url);
			} catch (HelpSetException e) {
				System.err.println("Error loading...");
				System.err.println("HelpSetException: "+e.toString());
				return;
			}

			HelpBroker helpbroker = helpset.createHelpBroker();

			ActionListener listener =
					new CSH.DisplayHelpFromSource(helpbroker);
			help.addActionListener(listener);
/*			
			getContentPane().addMouseListener(
				new MouseAdapter(){
					public void mousePressed(MouseEvent event){
						if(event.isPopupTrigger()){
							popup.show(event.getComponent(),event.getX(),event.getY());
						}
					}
					public void mouseReleaseed(MouseEvent event){
						if(event.isPopupTrigger()){
							popup.show(event.getComponent(),event.getX(),event.getY());
						}
					} 
				}
			);
*/		

		
	}
		
	private class MouseAdapter{
		public void mousePressed(MouseEvent event){
			if(event.isPopupTrigger()){
				popup.show(event.getComponent(),event.getX(),event.getY());
			}
		}
		public void mouseReleaseed(MouseEvent event){
			if(event.isPopupTrigger()){
				popup.show(event.getComponent(),event.getX(),event.getY());
			}
		}
	}	
		
		
		
	public static void main(String[] args) {
		PopupMenu pop = new PopupMenu();
		pop.show();
	}
}
