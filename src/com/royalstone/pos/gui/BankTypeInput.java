package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;
import java.awt.event.*;

/**
 * @author panxingke
 */

public class BankTypeInput extends JDialog {
        JPanel jPanel2 = new JPanel();
        GridLayout gridLayout1 = new GridLayout();
        JPanel jPanel1 = new JPanel();
        JLabel jLabel1 = new JLabel();
        JTextField BankTypeText= new JTextField();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        FlowLayout flowLayout1 = new FlowLayout();

       // StringBuffer inputcode = new StringBuffer();

        private volatile boolean isFinish=false;
        private boolean isConfirm=false;


        private PosKeyMap kmap;


        public boolean isConfirm(){
                return isConfirm;
        }




        private class BankTypeListener extends KeyAdapter {
                public void keyPressed(java.awt.event.KeyEvent e) {

                        switch (e.getKeyCode()) {
                                case KeyEvent.VK_ESCAPE :
                                        isFinish=true;
                                        dispose();
                                        break;
                                default :
                                        switch(kmap.getFunction(e.getKeyCode()).getKey()){
                                                case PosFunction.CANCEL:
                                                case PosFunction.CLEAR:
                                                                isFinish=true;
                                                                dispose();
                                                        break;
                                        }

                        }
                }
        }

        public BankTypeInput() {
                super(pos.posFrame, "银行类型", false);
                try {
                        jbInit();
						BankTypeText.setEditable(true);
                        setSize(450, 200);
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        this.setLocation(
                                (int) ((screenSize.getWidth() - 450) / 2),
                                (int) ((screenSize.getHeight() - 200) / 2));

						kmap = PosKeyMap.getInstance();


                        BankTypeListener cl = new BankTypeListener();
                        BankTypeText.addKeyListener(cl);


                        this.addWindowListener(new WindowAdapter() {
                                public void windowClosing(WindowEvent e) {
                                        isFinish=true;
                                }
                        });

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private void jbInit() throws Exception {
                jPanel2.setLayout(gridLayout1);
                gridLayout1.setColumns(2);
                gridLayout1.setRows(3);
                jPanel1.setLayout(flowLayout1);
                jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
                jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
                jLabel1.setText("请输入银行类型：");
                BankTypeText.setBackground(Color.white);
                BankTypeText.setFont(new java.awt.Font("Dialog", 0, 16));
                BankTypeText.setMinimumSize(new Dimension(6, 22));
                BankTypeText.setPreferredSize(new Dimension(200, 40));
                BankTypeText.setEditable(false);
                BankTypeText.setText("");
    			BankTypeText.addActionListener(new BankTypeInput_BankTypeText_actionAdapter(this));
                jLabel6.setText("");
                jLabel5.setAlignmentY((float) 0.5);
                jLabel5.setText("");
                this.getContentPane().add(jPanel2, BorderLayout.CENTER);
                jPanel2.add(jLabel6, null);
                jPanel2.add(jPanel1, null);
                jPanel1.add(jLabel1, null);
                jPanel1.add(BankTypeText, null);
                jPanel2.add(jLabel5, null);
        }

        /**
         * @return
         */
        public String getInputcode() {
                //return inputcode.toString();
                return BankTypeText.getText();
        }

        public boolean isFinish(){
                return isFinish;
        }

        public void showStar(){
                BankTypeText.setText("*************");
        }

  void BankTypeText_actionPerformed(ActionEvent e) {
  }

}

class BankTypeInput_BankTypeText_actionAdapter implements java.awt.event.ActionListener {
  BankTypeInput adaptee;

  BankTypeInput_BankTypeText_actionAdapter(BankTypeInput adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.BankTypeText_actionPerformed(e);
  }
}
