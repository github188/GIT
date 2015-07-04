package com.royalstone.pos.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.royalstone.pos.shell.pos;
import javax.swing.*;
import java.awt.*;

/**
 * 储值卡的确认对话框
 * @author liangxinbiao

 */
public class ShoppingCardConfirm
	extends JDialog
	implements CardConfirmUI {

	private EnterButtonAction enterButtonAction = new EnterButtonAction();
	private ClearButtonAction clearButtonAction = new ClearButtonAction();
	private CardKeyListener keyListener = new CardKeyListener();
	private boolean confirm = false;

	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	FlowLayout flowLayout1 = new FlowLayout();
	JButton clearButton = new JButton();
	JButton enterButton = new JButton();

	private volatile boolean isFinish = false;
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel9 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanel10 = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel11 = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  BorderLayout borderLayout7 = new BorderLayout();
  JPanel jPanel12 = new JPanel();
  JPanel jPanel13 = new JPanel();
  BorderLayout borderLayout8 = new BorderLayout();
  JPanel jPanel14 = new JPanel();
  JPanel jPanel15 = new JPanel();
  JPanel jPanel16 = new JPanel();
  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout9 = new BorderLayout();
  JPanel jPanel17 = new JPanel();
  BorderLayout borderLayout10 = new BorderLayout();
  JPanel jPanel18 = new JPanel();
  JPanel jPanel19 = new JPanel();
  JLabel cardNO = new JLabel();
  BorderLayout borderLayout11 = new BorderLayout();
  JPanel jPanel110 = new JPanel();
  JPanel jPanel111 = new JPanel();
  BorderLayout borderLayout12 = new BorderLayout();
  BorderLayout borderLayout13 = new BorderLayout();
  JPanel jPanel112 = new JPanel();
  JLabel jLabel5 = new JLabel();
  JPanel jPanel113 = new JPanel();
  JLabel cardName = new JLabel();
  BorderLayout borderLayout14 = new BorderLayout();
  JPanel jPanel114 = new JPanel();
  BorderLayout borderLayout15 = new BorderLayout();
  JPanel jPanel115 = new JPanel();
  JPanel jPanel116 = new JPanel();
  JPanel jPanel117 = new JPanel();
  JPanel jPanel20 = new JPanel();
  BorderLayout borderLayout16 = new BorderLayout();
  JLabel cardType = new JLabel();
  JPanel jPanel118 = new JPanel();
  BorderLayout borderLayout17 = new BorderLayout();
  JPanel jPanel119 = new JPanel();
  BorderLayout borderLayout18 = new BorderLayout();
  JPanel jPanel21 = new JPanel();
  BorderLayout borderLayout19 = new BorderLayout();
  JPanel jPanel120 = new JPanel();
  JPanel jPanel121 = new JPanel();
  JLabel jLabel6 = new JLabel();
  JPanel jPanel122 = new JPanel();
  BorderLayout borderLayout110 = new BorderLayout();
  JPanel jPanel123 = new JPanel();
  JPanel jPanel124 = new JPanel();
  JPanel jPanel125 = new JPanel();
  BorderLayout borderLayout20 = new BorderLayout();
  BorderLayout borderLayout21 = new BorderLayout();
  JLabel tenderAmount = new JLabel();
  JPanel jPanel1110 = new JPanel();
  BorderLayout borderLayout111 = new BorderLayout();
  JPanel jPanel1111 = new JPanel();
  BorderLayout borderLayout112 = new BorderLayout();
  JPanel jPanel22 = new JPanel();
  BorderLayout borderLayout113 = new BorderLayout();
  JPanel jPanel126 = new JPanel();
  JPanel jPanel127 = new JPanel();
  JLabel jLabel8 = new JLabel();
  JPanel jPanel128 = new JPanel();
  BorderLayout borderLayout114 = new BorderLayout();
  JPanel jPanel129 = new JPanel();
  JPanel jPanel1210 = new JPanel();
  JPanel jPanel1211 = new JPanel();
  BorderLayout borderLayout22 = new BorderLayout();
  BorderLayout borderLayout23 = new BorderLayout();
  BorderLayout borderLayout1110 = new BorderLayout();
  JPanel jPanel11110 = new JPanel();
  JLabel realAmount = new JLabel();
  JPanel jPanel11111 = new JPanel();
  BorderLayout borderLayout123 = new BorderLayout();
  JPanel jPanel11112 = new JPanel();
  JPanel jPanel11113 = new JPanel();
  BorderLayout borderLayout124 = new BorderLayout();
  JPanel jPanel11114 = new JPanel();
  JPanel jPanel11115 = new JPanel();
  JPanel jPanel26 = new JPanel();
  JPanel jPanel1122 = new JPanel();
  BorderLayout borderLayout27 = new BorderLayout();
  JPanel jPanel131 = new JPanel();
  BorderLayout borderLayout125 = new BorderLayout();
  JLabel jLabel14 = new JLabel();
  JPanel jPanel1123 = new JPanel();
  BorderLayout borderLayout1111 = new BorderLayout();
  JPanel jPanel11116 = new JPanel();
  JLabel cardBalance = new JLabel();
  JPanel jPanel11117 = new JPanel();
  BorderLayout borderLayout126 = new BorderLayout();
  JPanel jPanel11118 = new JPanel();
  JPanel jPanel11119 = new JPanel();
  BorderLayout borderLayout127 = new BorderLayout();
  JPanel jPanel111110 = new JPanel();
  JPanel jPanel111111 = new JPanel();
  JPanel jPanel27 = new JPanel();
  JPanel jPanel1124 = new JPanel();
  BorderLayout borderLayout28 = new BorderLayout();
  JPanel jPanel132 = new JPanel();
  BorderLayout borderLayout128 = new BorderLayout();
  JLabel jLabel16 = new JLabel();
  JPanel jPanel1125 = new JPanel();
  JPanel jPanel29 = new JPanel();
  JPanel jPanel30 = new JPanel();
  JPanel jPanel1116 = new JPanel();
  BorderLayout borderLayout117 = new BorderLayout();
  JLabel jLabel12 = new JLabel();
  BorderLayout borderLayout116 = new BorderLayout();
  BorderLayout borderLayout121 = new BorderLayout();
  JLabel payAmount = new JLabel();
  JPanel jPanel1120 = new JPanel();
  JPanel jPanel1113 = new JPanel();
  JPanel jPanel25 = new JPanel();
  JPanel jPanel1214 = new JPanel();
  BorderLayout borderLayout120 = new BorderLayout();
  JLabel balance = new JLabel();
  BorderLayout borderLayout119 = new BorderLayout();
  JPanel jPanel24 = new JPanel();
  JPanel jPanel130 = new JPanel();
  BorderLayout borderLayout122 = new BorderLayout();
  JPanel jPanel1217 = new JPanel();
  JPanel jPanel1212 = new JPanel();
  JPanel jPanel1213 = new JPanel();
  BorderLayout borderLayout115 = new BorderLayout();
  JPanel jPanel1112 = new JPanel();
  BorderLayout borderLayout118 = new BorderLayout();
  JPanel jPanel1117 = new JPanel();
  BorderLayout borderLayout25 = new BorderLayout();
  JPanel jPanel1121 = new JPanel();
  JPanel jPanel1119 = new JPanel();
  JPanel jPanel28 = new JPanel();
  JPanel jPanel1114 = new JPanel();
  JPanel jPanel1215 = new JPanel();
  BorderLayout borderLayout26 = new BorderLayout();
  BorderLayout borderLayout24 = new BorderLayout();
  JPanel jPanel1216 = new JPanel();
  JPanel jPanel1115 = new JPanel();
  JPanel jPanel23 = new JPanel();
  JLabel jLabel10 = new JLabel();
  BorderLayout borderLayout29 = new BorderLayout();

	public ShoppingCardConfirm() {
		super(pos.posFrame);
		setModal(true);
		setSize(450, 240);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
			(int) ((screenSize.getWidth() - 450) / 2),
			(int) ((screenSize.getHeight() - 240) / 2));

//		this.setLocation(280, 150);
		//addKeyListenerRecursively(this.getContentPane());

		enterButton.addActionListener(enterButtonAction);
		clearButton.addActionListener(clearButtonAction);

		enterButton.addKeyListener(keyListener);
		clearButton.addKeyListener(keyListener);




		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCardNo(String value) {
	        this.cardNO.setText(value);
	}

	public void setTenderAmount(String value) {
		this.tenderAmount.setText(value);
        this.realAmount.setText(value);
        this.payAmount.setText(value);
	}

	public void setCardAmount(String value) {
		this.cardBalance.setText(value);
	}

	public void setBalance(String value) {
		this.balance.setText(value);
	}

    public void setEnterButton(boolean isTrue){
        this.enterButton.setVisible(isTrue);
        this.clearButton.setText("确定");
        if(!isTrue){
          this.jPanel9.setVisible(false);
          this.jPanel30.setVisible(false);
          setSize(450, 160);
        }
    }

	private class CardTableModel extends AbstractTableModel {

		public final static int CARD_NO = 0;
		public final static int CARD_NAME = 1;
		public final static int CARD_TYPE = 2;
		public final static int TENDER_AMOUNT = 3;
		public final static int AUTUAL_TENDER_AMOUNT = 4;
		public final static int AMOUNT = 5;

		public Class getColumnClass(int col) {
			if (col == TENDER_AMOUNT
				|| col == AUTUAL_TENDER_AMOUNT
				|| col == AMOUNT) {
				return JLabel.class;
			}
			return Object.class;
		}

		private String columnNames[] =
			{ "编号", "卡名", "卡类", "应付金额", "实付金额", "余额" };
		private String values[][] =
			{
				{
					"cardNo",
					"提货卡",
					"储值",
					"tenderAmount",
					"tenderAmount",
					"cardAmount" },
				{
				"", "", "", "", "请支付金额:", "tenderAmount" }, {
				"", "", "", "", "支付后余额:", "balance" }
		};

		public int getColumnCount() {
			return 6;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getRowCount() {
			return 3;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return values[rowIndex][columnIndex];
		}

		public void setCardNo(String value) {
			values[0][0] = value;
		}

		public void setTenderAmount(String value) {
			values[0][3] = value;
			values[0][4] = value;
			values[1][5] = value;
		}

		public void setCardAmount(String value) {
			values[0][5] = value;
		}

		public void setBalance(String value) {
			values[2][5] = value;
		}
	}

	private void jbInit() throws Exception {
		jPanel1.setLayout(borderLayout1);
		jPanel2.setLayout(flowLayout1);
		clearButton.setFont(new java.awt.Font("Dialog", 0, 16));
		clearButton.setPreferredSize(new Dimension(100, 30));
		clearButton.setText("取消");
		enterButton.setFont(new java.awt.Font("Dialog", 0, 16));
		enterButton.setPreferredSize(new Dimension(100, 30));
    enterButton.setSelected(false);
		enterButton.setText("确认");
		jPanel3.setLayout(borderLayout2);
    jPanel1.setPreferredSize(new Dimension(180, 83));
    this.setResizable(false);
    this.setTitle("储值卡信息");
    jPanel2.setMinimumSize(new Dimension(147, 40));
    jPanel2.setRequestFocusEnabled(true);
    jPanel4.setPreferredSize(new Dimension(10, 40));
    jPanel4.setLayout(borderLayout5);
    jPanel5.setLayout(borderLayout3);
    jPanel7.setPreferredSize(new Dimension(10, 40));
    jPanel7.setLayout(borderLayout16);
    jPanel8.setLayout(borderLayout4);
    jPanel9.setPreferredSize(new Dimension(10, 40));
    jPanel9.setLayout(borderLayout21);
    jPanel6.setPreferredSize(new Dimension(220, 40));
    jPanel6.setLayout(borderLayout6);
    jPanel11.setLayout(borderLayout7);
    jPanel12.setPreferredSize(new Dimension(100, 10));
    jPanel12.setLayout(borderLayout8);
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setText("卡号：");
    jPanel16.setLayout(borderLayout9);
    jPanel13.setLayout(borderLayout10);
    cardNO.setBackground(Color.white);
    cardNO.setFont(new java.awt.Font("Dialog", 0, 16));
    cardNO.setOpaque(true);
    cardNO.setHorizontalAlignment(SwingConstants.RIGHT);
    cardNO.setText("cardNO");
    jPanel19.setLayout(borderLayout11);
    jPanel110.setLayout(borderLayout12);
    jPanel110.setPreferredSize(new Dimension(100, 10));
    jPanel111.setLayout(borderLayout13);
    jLabel5.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setText("卡名：");
    cardName.setText("储值卡");
    cardName.setHorizontalAlignment(SwingConstants.RIGHT);
    cardName.setOpaque(true);
    cardName.setBackground(Color.white);
    cardName.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel114.setLayout(borderLayout15);
    jPanel115.setLayout(borderLayout14);
    cardType.setText("储值");
    cardType.setHorizontalAlignment(SwingConstants.RIGHT);
    cardType.setOpaque(true);
    cardType.setBackground(Color.white);
    cardType.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel118.setLayout(borderLayout18);
    jPanel118.setPreferredSize(new Dimension(100, 10));
    jPanel119.setLayout(borderLayout19);
    jPanel21.setPreferredSize(new Dimension(220, 10));
    jPanel21.setLayout(borderLayout20);
    jPanel120.setLayout(borderLayout110);
    jLabel6.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setText("卡类：");
    jPanel123.setLayout(borderLayout17);
    tenderAmount.setText("tenderAmount");
    tenderAmount.setHorizontalAlignment(SwingConstants.RIGHT);
    tenderAmount.setOpaque(true);
    tenderAmount.setBackground(Color.white);
    tenderAmount.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel1110.setLayout(borderLayout112);
    jPanel1110.setPreferredSize(new Dimension(100, 10));
    jPanel1111.setLayout(borderLayout113);
    jPanel22.setPreferredSize(new Dimension(220, 10));
    jPanel22.setLayout(borderLayout22);
    jPanel126.setLayout(borderLayout114);
    jPanel127.setPreferredSize(new Dimension(10, 8));
    jLabel8.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel8.setText("应付金额：");
    jPanel129.setLayout(borderLayout111);
    jPanel1211.setPreferredSize(new Dimension(10, 8));
    jPanel10.setLayout(borderLayout23);
    jPanel11110.setLayout(borderLayout123);
    realAmount.setText("realAmount");
    realAmount.setHorizontalAlignment(SwingConstants.RIGHT);
    realAmount.setOpaque(true);
    realAmount.setBackground(Color.white);
    realAmount.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel11111.setLayout(borderLayout124);
    jPanel11111.setPreferredSize(new Dimension(100, 10));
    jPanel11112.setLayout(borderLayout1110);
    jPanel11113.setLayout(borderLayout125);
    jPanel11115.setPreferredSize(new Dimension(10, 8));
    jPanel1122.setPreferredSize(new Dimension(10, 15));
    jPanel131.setLayout(borderLayout27);
    jLabel14.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel14.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel14.setText("实付金额：");
    jPanel1123.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel1123.setPreferredSize(new Dimension(10, 8));
    jPanel11116.setLayout(borderLayout126);
    cardBalance.setText("cardBalance");
    cardBalance.setHorizontalAlignment(SwingConstants.RIGHT);
    cardBalance.setOpaque(true);
    cardBalance.setBackground(Color.white);
    cardBalance.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel11117.setLayout(borderLayout127);
    jPanel11117.setPreferredSize(new Dimension(100, 10));
    jPanel11118.setLayout(borderLayout1111);
    jPanel11119.setLayout(borderLayout128);
    jPanel132.setLayout(borderLayout28);
    jLabel16.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel16.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel16.setText("余额：");
    jPanel11.setPreferredSize(new Dimension(144, 40));
    jPanel20.setPreferredSize(new Dimension(10, 10));
    jPanel27.setPreferredSize(new Dimension(10, 10));
    jPanel26.setPreferredSize(new Dimension(10, 10));
    jPanel10.setPreferredSize(new Dimension(385, 40));
    jPanel132.setPreferredSize(new Dimension(198, 50));
    jPanel11116.setPreferredSize(new Dimension(98, 50));
    jPanel123.setPreferredSize(new Dimension(32, 50));
    jPanel14.setPreferredSize(new Dimension(10, 8));
    jPanel17.setPreferredSize(new Dimension(10, 8));
    jPanel112.setPreferredSize(new Dimension(10, 8));
    jPanel117.setPreferredSize(new Dimension(10, 8));
    jPanel15.setPreferredSize(new Dimension(10, 8));
    jPanel18.setPreferredSize(new Dimension(10, 8));
    jPanel113.setPreferredSize(new Dimension(10, 8));
    jPanel116.setPreferredSize(new Dimension(10, 8));
    jPanel121.setPreferredSize(new Dimension(10, 8));
    jPanel125.setPreferredSize(new Dimension(10, 8));
    jPanel1124.setPreferredSize(new Dimension(10, 8));
    jPanel111111.setPreferredSize(new Dimension(10, 8));
    jPanel122.setPreferredSize(new Dimension(10, 8));
    jPanel124.setPreferredSize(new Dimension(10, 8));
    jPanel111110.setPreferredSize(new Dimension(10, 8));
    jPanel1125.setPreferredSize(new Dimension(10, 8));
    jPanel128.setPreferredSize(new Dimension(10, 8));
    jPanel1210.setPreferredSize(new Dimension(10, 8));
    jPanel11114.setPreferredSize(new Dimension(10, 8));
    jPanel30.setPreferredSize(new Dimension(10, 40));
    jPanel30.setLayout(borderLayout29);
    jPanel1116.setLayout(borderLayout119);
    jLabel12.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel12.setPreferredSize(new Dimension(80, 23));
    jLabel12.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel12.setText("支付后余额：");
    payAmount.setBackground(Color.white);
    payAmount.setFont(new java.awt.Font("Dialog", 0, 16));
    payAmount.setOpaque(true);
    payAmount.setHorizontalAlignment(SwingConstants.RIGHT);
    payAmount.setText("payAmount");
    jPanel1120.setPreferredSize(new Dimension(10, 8));
    jPanel1113.setLayout(borderLayout115);
    jPanel1113.setPreferredSize(new Dimension(100, 10));
    jPanel25.setPreferredSize(new Dimension(10, 10));
    jPanel1214.setPreferredSize(new Dimension(10, 8));
    balance.setText("balance");
    balance.setHorizontalAlignment(SwingConstants.RIGHT);
    balance.setOpaque(true);
    balance.setPreferredSize(new Dimension(97, 23));
    balance.setBackground(Color.white);
    balance.setFont(new java.awt.Font("Dialog", 0, 16));
    jPanel24.setPreferredSize(new Dimension(220, 10));
    jPanel24.setLayout(borderLayout25);
    jPanel130.setLayout(borderLayout26);
    jPanel130.setPreferredSize(new Dimension(220, 40));
    jPanel1217.setPreferredSize(new Dimension(10, 8));
    jPanel1212.setPreferredSize(new Dimension(10, 8));
    jPanel1213.setLayout(borderLayout116);
    jPanel1112.setLayout(borderLayout118);
    jPanel1117.setLayout(borderLayout122);
    jPanel1121.setPreferredSize(new Dimension(10, 8));
    jPanel1119.setPreferredSize(new Dimension(10, 8));
    jPanel28.setPreferredSize(new Dimension(10, 8));
    jPanel1114.setLayout(borderLayout120);
    jPanel1215.setLayout(borderLayout117);
    jPanel1216.setPreferredSize(new Dimension(10, 8));
    jPanel1115.setLayout(borderLayout121);
    jPanel1115.setPreferredSize(new Dimension(100, 10));
    jPanel23.setPreferredSize(new Dimension(220, 10));
    jPanel23.setLayout(borderLayout24);
    jLabel10.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel10.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel10.setText("请支付：");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);

		jPanel2.add(enterButton, null);
		jPanel2.add(clearButton, null);
    jPanel1.add(jPanel3,  BorderLayout.CENTER);
    jPanel3.add(jPanel5,  BorderLayout.CENTER);
    jPanel5.add(jPanel7,  BorderLayout.NORTH);
    jPanel7.add(jPanel21,  BorderLayout.WEST);
    jPanel118.add(jPanel121, BorderLayout.NORTH);
    jPanel118.add(jPanel122, BorderLayout.SOUTH);
    jPanel118.add(jPanel119, BorderLayout.CENTER);
    jPanel119.add(jLabel6, BorderLayout.CENTER);
    jPanel21.add(jPanel123, BorderLayout.CENTER);
    jPanel21.add(jPanel118, BorderLayout.WEST);
    jPanel123.add(jPanel125, BorderLayout.NORTH);
    jPanel123.add(jPanel124, BorderLayout.SOUTH);
    jPanel123.add(jPanel120, BorderLayout.CENTER);
    jPanel120.add(cardType, BorderLayout.CENTER);
    jPanel7.add(jPanel132,  BorderLayout.CENTER);
    jPanel11117.add(jPanel1124, BorderLayout.NORTH);
    jPanel11117.add(jPanel111110, BorderLayout.SOUTH);
    jPanel11117.add(jPanel11119, BorderLayout.CENTER);
    jPanel11119.add(jLabel16, BorderLayout.CENTER);
    jPanel132.add(jPanel11116, BorderLayout.CENTER);
    jPanel132.add(jPanel11117, BorderLayout.WEST);
    jPanel11116.add(jPanel111111, BorderLayout.NORTH);
    jPanel11116.add(jPanel1125, BorderLayout.SOUTH);
    jPanel11116.add(jPanel11118, BorderLayout.CENTER);
    jPanel11118.add(cardBalance, BorderLayout.CENTER);
    jPanel11118.add(jPanel27, BorderLayout.EAST);
    jPanel5.add(jPanel8, BorderLayout.CENTER);
    jPanel8.add(jPanel9, BorderLayout.NORTH);
    jPanel9.add(jPanel22,  BorderLayout.WEST);
    jPanel1110.add(jPanel127, BorderLayout.NORTH);
    jPanel1110.add(jPanel128, BorderLayout.SOUTH);
    jPanel1110.add(jPanel1111, BorderLayout.CENTER);
    jPanel1111.add(jLabel8, BorderLayout.CENTER);
    jPanel22.add(jPanel129, BorderLayout.CENTER);
    jPanel22.add(jPanel1110, BorderLayout.WEST);
    jPanel129.add(jPanel1211, BorderLayout.NORTH);
    jPanel129.add(jPanel1210, BorderLayout.SOUTH);
    jPanel129.add(jPanel126, BorderLayout.CENTER);
    jPanel126.add(tenderAmount, BorderLayout.CENTER);
    jPanel9.add(jPanel131,  BorderLayout.CENTER);
    jPanel11111.add(jPanel1122, BorderLayout.NORTH);
    jPanel11111.add(jPanel11114, BorderLayout.SOUTH);
    jPanel11111.add(jPanel11113, BorderLayout.CENTER);
    jPanel11113.add(jLabel14, BorderLayout.CENTER);
    jPanel131.add(jPanel11110, BorderLayout.CENTER);
    jPanel131.add(jPanel11111, BorderLayout.WEST);
    jPanel11110.add(jPanel11115, BorderLayout.NORTH);
    jPanel11110.add(jPanel1123, BorderLayout.SOUTH);
    jPanel11110.add(jPanel11112, BorderLayout.CENTER);
    jPanel11112.add(realAmount, BorderLayout.CENTER);
    jPanel11112.add(jPanel26, BorderLayout.EAST);
    jPanel8.add(jPanel10,  BorderLayout.CENTER);
    jPanel10.add(jPanel29, BorderLayout.CENTER);
    jPanel10.add(jPanel30, BorderLayout.NORTH);
    jPanel1114.add(jPanel1119, BorderLayout.NORTH);
    jPanel1114.add(jPanel1121, BorderLayout.SOUTH);
    jPanel1114.add(jPanel1116, BorderLayout.CENTER);
    jPanel1116.add(balance, BorderLayout.CENTER);
    jPanel1116.add(jPanel25, BorderLayout.EAST);
    jPanel130.add(jPanel1115, BorderLayout.WEST);
    jPanel130.add(jPanel1114, BorderLayout.CENTER);
    jPanel1115.add(jPanel1120, BorderLayout.NORTH);
    jPanel1115.add(jPanel1117, BorderLayout.CENTER);
    jPanel1117.add(jLabel12, BorderLayout.CENTER);
    jPanel1117.add(jPanel28, BorderLayout.SOUTH);
    jPanel30.add(jPanel23,  BorderLayout.WEST);
    jPanel23.add(jPanel24, BorderLayout.WEST);
    jPanel1113.add(jPanel1216, BorderLayout.NORTH);
    jPanel1113.add(jPanel1212, BorderLayout.SOUTH);
    jPanel1113.add(jPanel1112, BorderLayout.CENTER);
    jPanel1112.add(jLabel10,  BorderLayout.CENTER);
    jPanel24.add(jPanel1215, BorderLayout.CENTER);
    jPanel24.add(jPanel1113, BorderLayout.WEST);
    jPanel1215.add(jPanel1217, BorderLayout.NORTH);
    jPanel1215.add(jPanel1214, BorderLayout.SOUTH);
    jPanel1215.add(jPanel1213, BorderLayout.CENTER);
    jPanel1213.add(payAmount, BorderLayout.CENTER);
    jPanel30.add(jPanel130, BorderLayout.CENTER);
    jPanel3.add(jPanel4, BorderLayout.NORTH);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel4.add(jPanel6, BorderLayout.WEST);
    jPanel6.add(jPanel12, BorderLayout.WEST);
    jPanel12.add(jPanel14,  BorderLayout.NORTH);
    jPanel12.add(jPanel15,  BorderLayout.SOUTH);
    jPanel12.add(jPanel16, BorderLayout.CENTER);
    jPanel16.add(jLabel1, BorderLayout.CENTER);
    jPanel6.add(jPanel13, BorderLayout.CENTER);
    jPanel13.add(jPanel17,  BorderLayout.NORTH);
    jPanel13.add(jPanel18, BorderLayout.SOUTH);
    jPanel13.add(jPanel19, BorderLayout.CENTER);
    jPanel19.add(cardNO, BorderLayout.CENTER);
    jPanel4.add(jPanel11, BorderLayout.CENTER);
    jPanel11.add(jPanel110,  BorderLayout.WEST);
    jPanel110.add(jPanel112, BorderLayout.NORTH);
    jPanel110.add(jPanel113, BorderLayout.SOUTH);
    jPanel110.add(jPanel111, BorderLayout.CENTER);
    jPanel111.add(jLabel5, BorderLayout.CENTER);
    jPanel11.add(jPanel115,  BorderLayout.CENTER);
    jPanel115.add(jPanel117, BorderLayout.NORTH);
    jPanel115.add(jPanel116, BorderLayout.SOUTH);
    jPanel115.add(jPanel114, BorderLayout.CENTER);
    jPanel114.add(cardName,  BorderLayout.CENTER);
    jPanel114.add(jPanel20,  BorderLayout.EAST);
	}

	private class JLabelTableCellRenderer implements TableCellRenderer {

		private JLabel label = new JLabel();

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int col) {
			TableColumn tableColumn = table.getColumnModel().getColumn(col);
			label.setOpaque(true);
			label.setFont(table.getFont());
			label.setSize(tableColumn.getWidth(), table.getRowHeight());
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setText(value.toString());
			if (isSelected) {
				label.setBackground(table.getSelectionBackground());
			} else {
				label.setBackground(table.getBackground());
			}

			return label;
		}
	}

	private class CardKeyListener extends KeyAdapter {
		public void keyPressed(java.awt.event.KeyEvent e) {

			if (e.getSource() != clearButton
				&& e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
				enterButtonAction.actionPerformed(null);
			}

			if ((e.getSource() == clearButton
				&& e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
				|| e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
				clearButtonAction.actionPerformed(null);
			}
		}
	}

	private class EnterKeyListener extends KeyAdapter {
		public void keyPressed(java.awt.event.KeyEvent e) {
			enterButtonAction.actionPerformed(null);
		}
	}

	private class ClearKeyListener extends KeyAdapter {
		public void keyPressed(java.awt.event.KeyEvent e) {
			clearButtonAction.actionPerformed(null);
		}
	}

	private class EnterButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			confirm = true;
			isFinish=true;
			dispose();
		}

	}

	private class ClearButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			confirm = false;
			isFinish=true;
			dispose();
		}

	}

	private void addKeyListenerRecursively(Component c) {
		c.removeKeyListener(keyListener);
		c.addKeyListener(keyListener);
		if (c instanceof Container) {
			Container cont = (Container) c;
			Component[] children = cont.getComponents();
			for (int i = 0; i < children.length; i++) {
				addKeyListenerRecursively(children[i]);
			}
		}
	}

	/**
	 * @return
	 */
	public boolean confirm() {
		return confirm;
	}

	public boolean isFinish() {
		return isFinish;
	}
}
