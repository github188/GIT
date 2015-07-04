package com.royalstone.pos.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.royalstone.pos.favor.Discount;
import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.util.Value;
import javax.swing.*;
import java.awt.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author liangxinbiao
 * @version 1.0
 */

public class PosFrame extends JFrame implements MainUI {

	private PosKeyMap kmap;

	private Color green=new Color(59, 112, 59);
	private Color red=new Color(255, 0, 0);

	java.net.URL logoURL = PosFrame.class.getResource("/images/logo.png");
	ImageIcon logoIcon = new ImageIcon(logoURL);

        java.net.URL bcURL = PosFrame.class.getResource("/images/PetroChina.JPG");
        ImageIcon bcIcon = new ImageIcon(bcURL);

        java.net.URL bpURL = PosFrame.class.getResource("/images/BP.JPG");
        ImageIcon bpIcon = new ImageIcon(bpURL);

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	JLabel waiterNo = new JLabel();
	JPanel jPanel6 = new JPanel();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel jPanel8 = new JPanel();
	BorderLayout borderLayout7 = new BorderLayout();
	JPanel jPanel9 = new JPanel();
	BorderLayout borderLayout8 = new BorderLayout();
	JPanel jPanel11 = new JPanel();
	BorderLayout borderLayout17 = new BorderLayout();

	private JMenu[] menus =
		{ new JMenu("文件(F)"),new JMenu("帮助(H)")};
	JPanel jPanel21 = new JPanel();
	BorderLayout borderLayout19 = new BorderLayout();
	BorderLayout borderLayout5 = new BorderLayout();
	JTextField inputField = new TheTextField();
	JPanel jPanel5 = new JPanel();
	JLabel jLabel5 = new JLabel();
	JTextField spCode = new JTextField();
	JPanel jPanel16 = new JPanel();
	JLabel jLabel16 = new JLabel();
	JLabel jLabel17 = new JLabel();
	JPanel jPanel17 = new JPanel();
	JPanel jPanel18 = new JPanel();
	BorderLayout borderLayout14 = new BorderLayout();
	JPanel panSpecial = new JPanel();
	BorderLayout borderLayout12 = new BorderLayout();
	BorderLayout borderLayout13 = new BorderLayout();
	JTextField spName = new JTextField();
	JPanel jPanel22 = new JPanel();
	BorderLayout borderLayout20 = new BorderLayout();
	JLabel prompt = new JLabel();
	JLabel status = new JLabel();
	JPanel jPanel23 = new JPanel();
	BorderLayout borderLayout21 = new BorderLayout();
	JPanel jPanel12 = new JPanel();
	JLabel lblTotal = new JLabel();
	JPanel jPanel13 = new JPanel();
	JTextField paid = new JTextField();
	BorderLayout borderLayout11 = new BorderLayout();
	JTextField total = new JTextField();
	BorderLayout borderLayout10 = new BorderLayout();
	BorderLayout borderLayout9 = new BorderLayout();
	JLabel lblPaid = new JLabel();
	JPanel jPanel14 = new JPanel();
	GridLayout gridLayout2 = new GridLayout();
	JPanel jPanel15 = new JPanel();
	JLabel lblUnPaid = new JLabel();
	JTextField unPaid = new JTextField();
	JLabel jLabel21 = new JLabel();
	JPanel jPanel24 = new JPanel();
	BorderLayout borderLayout22 = new BorderLayout();

	private PosKeyboard keyListener = new PosKeyboard();

	private OutputStream posOutputStream = null;

	private JTable registerTable;
	private RegisterTableModel registerTableModel;
	private TenderTableModel tenderTableModel;
	private JTable tenderTable;
	private JMenuBar menuBar;
	private boolean isMenuActive = false;

	private TheMenuListener theMenuListener = new TheMenuListener();
	private volatile int sequence=1;
	private boolean normalquit=false;

	JPanel jPanel32 = new JPanel();
	JTextField spQuantity = new JTextField();
	BorderLayout borderLayout15 = new BorderLayout();
	JPanel jPanel26 = new JPanel();
	JPanel jPanel20 = new JPanel();
	JLabel jLabel18 = new JLabel();
	JPanel jPanel33 = new JPanel();
	JLabel jLabel19 = new JLabel();
	JPanel jPanel19 = new JPanel();
	JTextField spPrice = new JTextField();
	JPanel jPanel25 = new JPanel();
	BorderLayout borderLayout16 = new BorderLayout();
	BorderLayout borderLayout29 = new BorderLayout();
	BorderLayout borderLayout30 = new BorderLayout();
	GridLayout gridLayout3 = new GridLayout();
  JPanel jPanel10 = new JPanel();
  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout28 = new BorderLayout();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JPanel jPanel34 = new JPanel();
  BorderLayout borderLayout18 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  BorderLayout borderLayout31 = new BorderLayout();
  JPanel panMemberCard = new JPanel();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel38 = new JPanel();
  JPanel jPanel40 = new JPanel();
  JPanel jPanel41 = new JPanel();
  JLabel jLabel14 = new JLabel();
  JTextField txtCarNo = new JTextField();
  BorderLayout borderLayout34 = new BorderLayout();
  TitledBorder titledBorder1;
  JPanel jPanel35 = new JPanel();
  JLabel jLabel15 = new JLabel();
  JTextField txtLoanCardMainBalance = new JTextField();
  BorderLayout borderLayout35 = new BorderLayout();
  BorderLayout borderLayout37 = new BorderLayout();
  BorderLayout borderLayout41 = new BorderLayout();
  JPanel jPanel46 = new JPanel();
  BorderLayout borderLayout23 = new BorderLayout();
  JLabel jLabel12 = new JLabel();
  JLabel posNo = new JLabel();
  JPanel jPanel27 = new JPanel();
  JLabel jLabel8 = new JLabel();
  JLabel holdNo = new JLabel();
  JPanel jPanel31 = new JPanel();
  GridLayout gridLayout4 = new GridLayout();
  JLabel transNo = new JLabel();
  BorderLayout borderLayout27 = new BorderLayout();
  JPanel jPanel30 = new JPanel();
  JLabel jLabel9 = new JLabel();
  JPanel jPanel43 = new JPanel();
  JLabel datetime = new JLabel();
  BorderLayout borderLayout26 = new BorderLayout();
  JPanel jPanel45 = new JPanel();
  BorderLayout borderLayout42 = new BorderLayout();
  JLabel connStatus = new JLabel();
  JLabel cashier = new JLabel();
  BorderLayout borderLayout25 = new BorderLayout();
  JPanel jPanel29 = new JPanel();
  JLabel jLabel10 = new JLabel();
  BorderLayout borderLayout39 = new BorderLayout();
  JPanel jPanel47 = new JPanel();
  BorderLayout borderLayout43 = new BorderLayout();
  BorderLayout borderLayout36 = new BorderLayout();
  JPanel jPanel36 = new JPanel();
  BorderLayout borderLayout24 = new BorderLayout();
  JPanel jPanel28 = new JPanel();
  BorderLayout borderLayout32 = new BorderLayout();
  JLabel jLabel7 = new JLabel();
  JLabel lblTotalQty = new JLabel();
  JPanel jPanel37 = new JPanel();
  BorderLayout borderLayout33 = new BorderLayout();
  JLabel lblPrinterStatus = new JLabel();
  JLabel jLabel13 = new JLabel();

	public void quit() {
		try{
			int keyCode=kmap.getKeyValue(new PosInput(PosFunction.EXIT));
			posOutputStream.write(keyCode);
			posOutputStream.flush();
		}catch(IOException ex){
			ex.printStackTrace();
		}

	}

	public void exit(){
		normalquit=true;
		this.dispose();
	}

	public PosFrame(OutputStream outputStream) {

		this.posOutputStream = outputStream;

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(!normalquit){
					System.out.println("Abnormal Quit……");
					System.exit(1);
				}
			}
		});

		try {
			//initMenu();

			jbInit();
			tenderTableModel = new TenderTableModel();
			tenderTable = new TheTenderTable(tenderTableModel);

			tenderTable.getTableHeader().setFont(
				new java.awt.Font("Dialog", 0, 20));
			tenderTable.setFont(new java.awt.Font("Dialog", 0, 20));
			tenderTable.setRowHeight(30);
			tenderTable.setDefaultRenderer(
				JLabel.class,
				new JLabelTableCellRenderer());

			JScrollPane scrollPane = new JScrollPane(tenderTable);
			scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);

			//scrollPane.getViewport().putClientProperty("EnableWindowBlit",new Boolean(false));

			jPanel11.add(scrollPane, BorderLayout.CENTER);
			jPanel11.setPreferredSize(new Dimension(110, 80));

			registerTableModel = new RegisterTableModel();
			registerTable = new TheRegisterTable(registerTableModel);

			registerTable.getTableHeader().setFont(
				new java.awt.Font("Dialog", 0, 20));
			registerTable.setFont(new java.awt.Font("Dialog", 0, 20));
			registerTable.setRowHeight(30);

			registerTable.setDefaultRenderer(
				JLabel.class,
				new JLabelTableCellRenderer());

			TableColumn col = registerTable.getColumnModel().getColumn(0);
			col.setPreferredWidth(50);

			col = registerTable.getColumnModel().getColumn(1);
			col.setPreferredWidth(140);

			col = registerTable.getColumnModel().getColumn(2);
			col.setPreferredWidth(240);

			JScrollPane scrollPane1 = new JScrollPane(registerTable);
			scrollPane1.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);

			//scrollPane1.getViewport().putClientProperty("EnableWindowBlit",new Boolean(false));

			jPanel7.add(scrollPane1, BorderLayout.CENTER);

			addKeyAndContainerListenerRecursively(this);

			if(PosConfig.getInstance().getString("TX_FLAG")!=null && PosConfig.getInstance().getString("TX_FLAG").equals("ON")){
				panSpecial.setVisible(true);
			}else{
				panSpecial.setVisible(false);
			}
			panMemberCard.setVisible(false);


			this.addWindowFocusListener(new WindowFocusListener(){

					public void windowGainedFocus(WindowEvent e) {
						inputField.requestFocus();
					}

					public void windowLostFocus(WindowEvent e) {

					}
				});

			ClockThread timer = new ClockThread();
			timer.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initMenu() {

		kmap = PosKeyMap.getInstance();

		JMenuItem mi = new JMenuItem("最小化");
		mi.setFont(new java.awt.Font("Dialog", 0, 16));
		menus[0].add(mi);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState(JFrame.ICONIFIED);
			}
		});


		mi = new JMenuItem("退出");
		mi.setFont(new java.awt.Font("Dialog", 0, 16));
		menus[0].add(mi);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		JMenuItem help = new JMenuItem("帮助正文");
		help.setFont(new java.awt.Font("Dialog", 0, 16));

		menus[1].add(help);

		mi = new JMenuItem("关于 POS4.1+");
		mi.setFont(new java.awt.Font("Dialog", 0, 16));
		menus[1].add(mi);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutDialog about = new AboutDialog();
				about.show();
			}
		});

		menuBar = new TheMenuBar();
		menuBar.setFont(new java.awt.Font("Dialog", 0, 16));
		menus[0].setMnemonic(KeyEvent.VK_F);
		menus[1].setMnemonic(KeyEvent.VK_H);
		for (int i = 0; i < menus.length; i++) {
			menus[i].setFont(new java.awt.Font("Dialog", 0, 16));
			menus[i].addMenuListener(theMenuListener);
			menuBar.add(menus[i]);
		}

		setJMenuBar(menuBar);

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

	}

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder("");
    this.getContentPane().setLayout(borderLayout1);
		jPanel2.setLayout(borderLayout2);
		jLabel2.setRequestFocusEnabled(true);
		jLabel2.setIcon(logoIcon);
		jLabel2.setText("");
		jLabel3.setBackground(Color.white);
		jLabel3.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel3.setOpaque(true);
		jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
		jLabel3.setText("Copyright 1992-2004 广州融通系统集成有限公司");
		jPanel1.setLayout(borderLayout3);
		jPanel4.setLayout(borderLayout4);
		jPanel3.setLayout(borderLayout41);
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel6.setBorder(BorderFactory.createEtchedBorder());
		jPanel6.setLayout(borderLayout6);
		jPanel8.setLayout(borderLayout7);
		jPanel9.setLayout(borderLayout8);
		jPanel11.setLayout(borderLayout17);
		jPanel21.setLayout(borderLayout19);
		inputField.setBackground(new Color(212, 208, 200));
		inputField.setFont(new java.awt.Font("Dialog", 0, 25));
		inputField.setPreferredSize(new Dimension(285, 22));
		inputField.setText("");
		jPanel5.setBorder(BorderFactory.createEtchedBorder());
    jPanel5.setPreferredSize(new Dimension(275, 39));
		jPanel5.setLayout(borderLayout5);
		jLabel5.setFont(new java.awt.Font("Dialog", 0, 25));
		jLabel5.setPreferredSize(new Dimension(70, 35));
		jLabel5.setRequestFocusEnabled(true);
		jLabel5.setToolTipText("");
		jLabel5.setText("输  入");
		spCode.setBackground(Color.white);
		spCode.setFont(new java.awt.Font("Dialog", 0, 25));
		spCode.setPreferredSize(new Dimension(285, 39));
		spCode.setEditable(false);
		spCode.setText("");
		jPanel16.setLayout(borderLayout13);
		jPanel16.setBorder(BorderFactory.createEtchedBorder());
		jLabel16.setFont(new java.awt.Font("Dialog", 0, 26));
		jLabel16.setPreferredSize(new Dimension(70, 16));
		jLabel16.setRequestFocusEnabled(true);
		jLabel16.setText("名  称");
		jLabel17.setFont(new java.awt.Font("Dialog", 0, 26));
		jLabel17.setPreferredSize(new Dimension(70, 37));
		jLabel17.setToolTipText("");
		jLabel17.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel17.setText("编  码");
		jPanel17.setLayout(borderLayout22);
		jPanel18.setLayout(borderLayout14);
		jPanel18.setBorder(BorderFactory.createEtchedBorder());
    jPanel18.setPreferredSize(new Dimension(275, 43));
		panSpecial.setLayout(borderLayout12);
		spName.setBackground(Color.white);
		spName.setFont(new java.awt.Font("Dialog", 0, 25));
		spName.setToolTipText("");
		spName.setEditable(false);
		spName.setText("");
		jPanel22.setLayout(borderLayout20);
		prompt.setFont(new java.awt.Font("Dialog", 0, 18));
		prompt.setBorder(BorderFactory.createEtchedBorder());
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setText("");
		status.setFont(new java.awt.Font("Dialog", 1, 20));
		status.setBorder(BorderFactory.createEtchedBorder());
		status.setToolTipText("");
		status.setText("销售");
		jPanel23.setLayout(borderLayout21);
		jPanel12.setLayout(gridLayout2);
		lblTotal.setFont(new java.awt.Font("Dialog", 1, 26));
		lblTotal.setBorder(null);
		lblTotal.setText("  应  收  ");
		jPanel13.setLayout(borderLayout9);
		jPanel13.setBorder(BorderFactory.createEtchedBorder());
		paid.setBackground(Color.white);
		paid.setFont(new java.awt.Font("Dialog", 0, 26));
		paid.setToolTipText("");
		paid.setEditable(false);
		paid.setSelectionStart(5);
		paid.setText("0.00");
		paid.setHorizontalAlignment(SwingConstants.RIGHT);
		total.setBackground(Color.white);
		total.setFont(new java.awt.Font("Dialog", 0, 26));
		total.setPreferredSize(new Dimension(100, 27));
		total.setEditable(false);
		total.setText("0.00");
		total.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPaid.setFont(new java.awt.Font("Dialog", 1, 26));
		lblPaid.setRequestFocusEnabled(true);
		lblPaid.setText("  实  收  ");
		jPanel14.setLayout(borderLayout11);
		jPanel14.setBorder(BorderFactory.createEtchedBorder());
		gridLayout2.setColumns(1);
		gridLayout2.setRows(3);
		jPanel15.setLayout(borderLayout10);
		jPanel15.setBorder(BorderFactory.createEtchedBorder());
		lblUnPaid.setFont(new java.awt.Font("Dialog", 1, 26));
		lblUnPaid.setRequestFocusEnabled(true);
		lblUnPaid.setText("  待  收  ");
		unPaid.setBackground(Color.white);
		unPaid.setFont(new java.awt.Font("Dialog", 0, 26));
		unPaid.setPreferredSize(new Dimension(250, 27));
		unPaid.setEditable(false);
		unPaid.setText("0.00");
		unPaid.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel21.setFont(new java.awt.Font("Dialog", 0, 16));
		jLabel21.setBorder(BorderFactory.createEtchedBorder());
		jLabel21.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel21.setText("以人民币计算");
		jLabel21.setVisible(false);
		jPanel24.setLayout(gridLayout3);
		jPanel24.setMaximumSize(new Dimension(32767, 32767));
		spQuantity.setBackground(Color.white);
		spQuantity.setFont(new java.awt.Font("Dialog", 0, 25));
		spQuantity.setPreferredSize(new Dimension(150, 39));
		spQuantity.setEditable(false);
		spQuantity.setText("");
		spQuantity.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel26.setBorder(BorderFactory.createEtchedBorder());
		jPanel20.setLayout(borderLayout15);
		jPanel20.setBorder(BorderFactory.createEtchedBorder());
		jLabel18.setFont(new java.awt.Font("Dialog", 0, 25));
		jLabel18.setText("数  量");
		jLabel19.setFont(new java.awt.Font("Dialog", 0, 25));
		jLabel19.setToolTipText("");
		jLabel19.setText("单  价");
		jPanel19.setLayout(borderLayout16);
		jPanel19.setBorder(BorderFactory.createEtchedBorder());
		spPrice.setBackground(Color.white);
		spPrice.setFont(new java.awt.Font("Dialog", 0, 25));
		spPrice.setPreferredSize(new Dimension(150, 39));
		spPrice.setEditable(false);
		spPrice.setSelectionStart(4);
		spPrice.setText("");
		spPrice.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanel25.setBorder(BorderFactory.createEtchedBorder());
		jPanel32.setLayout(borderLayout29);
		jPanel33.setLayout(borderLayout30);
		gridLayout3.setColumns(2);
		gridLayout3.setRows(1);
		jPanel12.setPreferredSize(new Dimension(361, 110));
		jLabel1.setBackground(Color.white);
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 22));
    jLabel1.setForeground(Color.red);
    jLabel1.setOpaque(true);
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("销售收银系统");
    jPanel10.setLayout(borderLayout28);
    jLabel6.setIcon(null);
    jLabel6.setText("");
    jPanel7.setLayout(borderLayout18);
    jPanel34.setLayout(borderLayout31);
    panMemberCard.setLayout(verticalFlowLayout1);
    panMemberCard.setBorder(BorderFactory.createEtchedBorder());
    jPanel38.setBorder(null);
    jPanel38.setLayout(borderLayout37);
    jLabel14.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel14.setPreferredSize(new Dimension(100, 30));
    jLabel14.setRequestFocusEnabled(true);
    jLabel14.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel14.setText("会员卡号：");
    txtCarNo.setBackground(Color.white);
    txtCarNo.setFont(new java.awt.Font("Dialog", 0, 20));
    txtCarNo.setPreferredSize(new Dimension(220, 33));
    txtCarNo.setEditable(false);
    txtCarNo.setSelectionStart(0);
    txtCarNo.setText("");
    jPanel40.setAlignmentY((float) 0.5);
    jPanel40.setLayout(borderLayout34);
    jPanel41.setLayout(borderLayout24);
    jLabel15.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel15.setPreferredSize(new Dimension(160, 29));
    jLabel15.setRequestFocusEnabled(true);
    jLabel15.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel15.setText("会员卡级别：");
    txtLoanCardMainBalance.setBackground(Color.white);
    txtLoanCardMainBalance.setEnabled(true);
    txtLoanCardMainBalance.setFont(new java.awt.Font("Dialog", 0, 16));
    txtLoanCardMainBalance.setPreferredSize(new Dimension(50, 30));
    txtLoanCardMainBalance.setRequestFocusEnabled(true);
    txtLoanCardMainBalance.setEditable(false);
    txtLoanCardMainBalance.setText("");
    txtLoanCardMainBalance.setHorizontalAlignment(SwingConstants.RIGHT);
    txtLoanCardMainBalance.setScrollOffset(0);
    jPanel35.setLayout(borderLayout35);
    jLabel12.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel12.setBorder(null);
    jLabel12.setRequestFocusEnabled(true);
    jLabel12.setText("POS机号:");
    posNo.setFont(new java.awt.Font("Dialog", 0, 16));
    posNo.setPreferredSize(new Dimension(45, 23));
    posNo.setText("P001");
    jPanel27.setLayout(borderLayout23);
    jPanel27.setBorder(BorderFactory.createEtchedBorder());
    jPanel27.setPreferredSize(new Dimension(115, 27));
    jLabel8.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel8.setBorder(null);
    jLabel8.setPreferredSize(new Dimension(55, 23));
    jLabel8.setText("挂单数:");
    holdNo.setFont(new java.awt.Font("Dialog", 0, 16));
    holdNo.setHorizontalAlignment(SwingConstants.LEFT);
    holdNo.setText("0");
    jPanel31.setLayout(borderLayout27);
    jPanel31.setBorder(BorderFactory.createEtchedBorder());
    jPanel31.setPreferredSize(new Dimension(30, 27));
    transNo.setFont(new java.awt.Font("Dialog", 0, 16));
    transNo.setText("1");
    jPanel30.setLayout(borderLayout26);
    jPanel30.setBorder(BorderFactory.createEtchedBorder());
    jPanel30.setPreferredSize(new Dimension(50, 27));
    jLabel9.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel9.setBorder(null);
    jLabel9.setToolTipText("");
    jLabel9.setText("交易号:");
    jPanel43.setLayout(borderLayout39);
    jPanel43.setBorder(null);
    jPanel43.setPreferredSize(new Dimension(100, 27));
    datetime.setFont(new java.awt.Font("Dialog", 0, 12));
    datetime.setBorder(BorderFactory.createEtchedBorder());
    datetime.setOpaque(false);
    datetime.setPreferredSize(new Dimension(160, 22));
    datetime.setRequestFocusEnabled(true);
    datetime.setHorizontalAlignment(SwingConstants.CENTER);
    datetime.setText("2004/05/09  16:18");
    jPanel45.setLayout(gridLayout4);
    jPanel46.setLayout(borderLayout42);
    connStatus.setFont(new java.awt.Font("Dialog", 1, 15));
    connStatus.setForeground(new Color(59, 112, 59));
    connStatus.setBorder(BorderFactory.createEtchedBorder());
    connStatus.setOpaque(false);
    connStatus.setPreferredSize(new Dimension(60, 27));
    connStatus.setHorizontalAlignment(SwingConstants.CENTER);
    connStatus.setText("联机 ");
    cashier.setFont(new java.awt.Font("Dialog", 0, 16));
    cashier.setHorizontalAlignment(SwingConstants.LEFT);
    cashier.setText("0001");
    cashier.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
    cashier.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
    jPanel29.setLayout(borderLayout25);
    jPanel29.setBorder(BorderFactory.createEtchedBorder());
    jLabel10.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel10.setBorder(null);
    jLabel10.setText("收银员:");
    jPanel45.setMinimumSize(new Dimension(412, 27));
    jPanel45.setPreferredSize(new Dimension(400, 27));
    jPanel47.setLayout(borderLayout43);
    jPanel36.setLayout(borderLayout36);
    jPanel28.setLayout(borderLayout32);
    jLabel7.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel7.setBorder(null);
    jLabel7.setText("数量合计:");
    lblTotalQty.setFont(new java.awt.Font("Dialog", 0, 16));
    lblTotalQty.setText("0");
    jPanel28.setBorder(BorderFactory.createEtchedBorder());
    jPanel37.setBorder(BorderFactory.createEtchedBorder());
    jPanel37.setLayout(borderLayout33);
    lblPrinterStatus.setFont(new java.awt.Font("Dialog", 0, 16));
    lblPrinterStatus.setText("允许");
    jLabel13.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel13.setText("打印机:");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.NORTH);
    jPanel27.add(jLabel12,  BorderLayout.WEST);
    jPanel27.add(posNo,  BorderLayout.CENTER);
    jPanel3.add(connStatus, BorderLayout.WEST);
    jPanel3.add(jPanel47,  BorderLayout.CENTER);
    jPanel47.add(jPanel46, BorderLayout.CENTER);
    jPanel47.add(jPanel27,  BorderLayout.WEST);
    jPanel46.add(jPanel43, BorderLayout.CENTER);
    jPanel29.add(jLabel10, BorderLayout.WEST);
    jPanel29.add(cashier, BorderLayout.CENTER);
    jPanel45.add(jPanel31, null);
    jPanel30.add(jLabel9, BorderLayout.WEST);
    jPanel30.add(transNo, BorderLayout.CENTER);
    jPanel45.add(jPanel29, null);
    jPanel45.add(jPanel30, null);
    jPanel31.add(jLabel8, BorderLayout.WEST);
    jPanel31.add(holdNo, BorderLayout.CENTER);
    jPanel45.add(jPanel28, null);
	jPanel45.add(jPanel37, null);
    jPanel37.add(lblPrinterStatus, BorderLayout.CENTER);
    jPanel37.add(jLabel13,  BorderLayout.WEST);
    jPanel28.add(jLabel7,  BorderLayout.WEST);
    jPanel28.add(lblTotalQty,  BorderLayout.CENTER);
    jPanel45.add(datetime, null);
    jPanel43.add(jPanel45, BorderLayout.CENTER);
		jPanel1.add(jPanel4, BorderLayout.SOUTH);
		jPanel4.add(jPanel21, BorderLayout.SOUTH);
		jPanel21.add(jPanel5, BorderLayout.WEST);
		jPanel5.add(jLabel5, BorderLayout.WEST);
		jPanel5.add(inputField, BorderLayout.CENTER);
		jPanel21.add(jPanel22, BorderLayout.CENTER);
		jPanel22.add(prompt, BorderLayout.CENTER);
		jPanel22.add(status, BorderLayout.EAST);
		jPanel4.add(panSpecial, BorderLayout.NORTH);
		jPanel16.add(jLabel16, BorderLayout.WEST);
		jPanel16.add(spName, BorderLayout.CENTER);
		panSpecial.add(jPanel17, BorderLayout.SOUTH);
		panSpecial.add(jPanel16, BorderLayout.CENTER);
		jPanel18.add(jLabel17, BorderLayout.WEST);
		jPanel18.add(spCode, BorderLayout.CENTER);
		jPanel17.add(jPanel24, BorderLayout.CENTER);
		jPanel24.add(jPanel32, null);
		jPanel24.add(jPanel33, null);
		jPanel20.add(jLabel18, BorderLayout.WEST);
		jPanel20.add(spQuantity, BorderLayout.CENTER);
		jPanel19.add(jLabel19, BorderLayout.WEST);
		jPanel19.add(spPrice, BorderLayout.CENTER);
		jPanel33.add(jPanel25, BorderLayout.CENTER);
		jPanel33.add(jPanel19, BorderLayout.EAST);
		jPanel32.add(jPanel26, BorderLayout.CENTER);
		jPanel32.add(jPanel20, BorderLayout.EAST);
		jPanel17.add(jPanel18, BorderLayout.WEST);
		jPanel1.add(jPanel6, BorderLayout.CENTER);
		jPanel6.add(jPanel8, BorderLayout.SOUTH);
		jPanel8.add(jPanel9, BorderLayout.NORTH);
		jPanel9.add(jPanel11, BorderLayout.CENTER);
		jPanel9.add(jPanel23, BorderLayout.EAST);
		jPanel23.add(jPanel12, BorderLayout.CENTER);
		jPanel13.add(lblTotal, BorderLayout.WEST);
		jPanel13.add(total, BorderLayout.CENTER);
		jPanel12.add(jPanel13, null);
		jPanel15.add(paid, BorderLayout.CENTER);
		jPanel15.add(lblPaid, BorderLayout.WEST);
		jPanel12.add(jPanel15, null);
		jPanel12.add(jPanel14, null);
		jPanel14.add(unPaid, BorderLayout.CENTER);
		jPanel14.add(lblUnPaid, BorderLayout.WEST);
		jPanel23.add(jLabel21, BorderLayout.NORTH);
    jPanel6.add(jPanel34, BorderLayout.CENTER);
    jPanel34.add(jPanel7,  BorderLayout.CENTER);
    jPanel34.add(panMemberCard, BorderLayout.SOUTH);
    panMemberCard.add(jPanel38, null);
    jPanel38.add(jPanel40,  BorderLayout.WEST);
    jPanel40.add(txtCarNo, BorderLayout.CENTER);
    jPanel40.add(jLabel14,  BorderLayout.WEST);
    jPanel38.add(jPanel41, BorderLayout.CENTER);
    jPanel35.add(txtLoanCardMainBalance,  BorderLayout.CENTER);
    jPanel35.add(jLabel15,  BorderLayout.WEST);

    jPanel41.add(jPanel35,  BorderLayout.WEST);
		this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jLabel2, BorderLayout.EAST);
		jPanel2.add(jLabel3, BorderLayout.CENTER);
		jPanel2.add(new MyMenu().createMenuBar(), BorderLayout.WEST);
     jPanel41.add(jPanel36,  BorderLayout.CENTER);
    this.getContentPane().add(jPanel10, BorderLayout.NORTH);
    jPanel10.add(jLabel1, BorderLayout.CENTER);
    jPanel10.add(jLabel4,  BorderLayout.EAST);
    jPanel10.add(jLabel6,  BorderLayout.WEST);

	}

	private class TenderTableModel extends AbstractTableModel {

		public Class getColumnClass(int col) {
			if (col == 2) {
				return JLabel.class;
			}
			return Object.class;
		}

		private ArrayList data = new ArrayList();
		private String columnNames[] = { "支付方式", "币种", "金额" };

		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(int row, int column) {
			if (row >= data.size())
				return null;

			ArrayList columns = (ArrayList) data.get(row);
			if (column >= columns.size())
				return null;

			return columns.get(column);
		}

		public void addRow(final ArrayList columns) {
			Runnable doAddRow=new Runnable(){
				public void run() {
					data.add(columns);
					fireTableDataChanged();
					makeRowVisible(tenderTable, tenderTable.getRowCount() - 1);
					tenderTable.changeSelection(
						tenderTable.getRowCount() - 1,
						1,
						false,
						false);
				}

			};
			SwingUtilities.invokeLater(doAddRow);
		}

		public void clear() {

			data = new ArrayList();
			fireTableDataChanged();
			lblTotalQty.setText("0");
		}

	}

	private class RegisterTableModel extends AbstractTableModel {

		private int seq = 0;

		public int getSeq() {
			return seq;
		}

		public void setSeq(int seq) {
			this.seq = seq;
		}

		public Class getColumnClass(int col) {
			if (col == 5 || col == 6 || col == 7) {
				return JLabel.class;
			}
			return Object.class;
		}

		private ArrayList data = new ArrayList();
		private String columnNames[] =
			{ "序号", "编码", "商品名称", "规格", "单位", "数量", "单价", "金额" };

		public int getColumnCount() {
			return 8;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(int row, int column) {
			if (row < 0 || row >= data.size())
				return null;

			ArrayList columns = (ArrayList) data.get(row);
			if (column >= columns.size())
				return null;

			return columns.get(column);

		}

		public void setValueAt(int row, int column) {
			ArrayList columns = (ArrayList) data.get(row);
			String name= (String) columns.get(column);
			columns.remove(column);
			String changename=name+"*";
		    columns.add(column,changename);
			fireTableDataChanged();

		}

		public void addRow(final ArrayList columns) {
			Runnable doAddRow=new Runnable(){
				public void run() {
					data.add(columns);
					fireTableDataChanged();
					makeRowVisible(registerTable, registerTable.getRowCount() - 1);

					if (columns.get(0) != null
						&& !((String) columns.get(0)).equals("")) {
						registerTable.changeSelection(
							registerTable.getRowCount() - 1,
							1,
							false,
							false);
					} else {
						if (registerTable.getRowCount() - 2 >= 0) {
							//add by lichao 08/27/2004
						    int	selRow=registerTable.getRowCount();
						    int num = 1;
						    String strSeq =
							    (String) registerTableModel.getValueAt(
								selRow - 1,
								0);
						    while(selRow > 0 && ( strSeq == null || strSeq.equals("") )){

									selRow--;
						            num++;
								     strSeq =
										(String) registerTableModel.getValueAt(
											selRow - 1,
											0);

						    }

							registerTable.changeSelection(
								registerTable.getRowCount() - num,
								1,
								false,
								false);
						}
					}

				}
			};
			SwingUtilities.invokeLater(doAddRow);
		}

		public void clear() {
			data = new ArrayList();
			fireTableDataChanged();
		}
	}

	public void makeRowVisible(JTable table, int visibleRow) {
		if (table.getColumnCount() == 0)
			return;

		if (visibleRow < 0 || visibleRow >= table.getRowCount()) {
			return;
		}

		Rectangle visible = table.getVisibleRect();
		Rectangle cell = table.getCellRect(visibleRow, 0, true);

		if (cell.y < visible.y) {
			visible.y = cell.y;
			table.scrollRectToVisible(visible);
		} else if (cell.y + cell.height > visible.y + visible.height) {
			visible.y = cell.y + cell.height - visible.height;
			table.scrollRectToVisible(visible);
			table.paintImmediately(table.getBounds());
		}
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
			if(value!=null){
				label.setText(value.toString());
			}else{
				label.setText("");
			}
			if (isSelected) {
				label.setBackground(table.getSelectionBackground());
			} else {
				label.setBackground(table.getBackground());
			}
			return label;
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

	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {

			if (posOutputStream != null
				&& !matchModifiers(e.getModifiersEx())
				&& !isMenuActive) {

				try {
					int keyCode = e.getKeyCode();
					switch (kmap.getFunction(keyCode).getKey()) {
						case PosFunction.ENTER :
							posOutputStream.write(keyCode);
							posOutputStream.flush();
							break;
						case PosFunction.UP :
							int selRow1 = registerTable.getSelectedRow();
						    /* modify by lichao 08/27/2004 */
						    String strSeq =
							    (String) registerTableModel.getValueAt(
								selRow1 - 1,
								0);
						    while(selRow1 > 0 && ( strSeq == null || strSeq.equals("") )){

									selRow1--;

								     strSeq =
										(String) registerTableModel.getValueAt(
											selRow1 - 1,
											0);

						    }
						    /*
							String strSeq =
								(String) registerTableModel.getValueAt(
									selRow1 - 1,
									0);
							if (strSeq == null || strSeq.equals("")) {
								selRow1--;
							}
							*/

							if (!tenderTable.hasFocus() && selRow1 > 0) {
								makeRowVisible(registerTable, selRow1 - 1);
								registerTable.changeSelection(
									selRow1 - 1,
									1,
									false,
									false);
							}
							break;
						case PosFunction.DOWN :
							int selRow2 = registerTable.getSelectedRow();

						    String strSeq2 =
							    (String) registerTableModel.getValueAt(
								    selRow2 + 1,
								    0);
					        while( selRow2 < registerTableModel.getRowCount() && (strSeq2 == null || strSeq2.equals(""))){

								    selRow2++;

								strSeq2 =
									(String) registerTableModel.getValueAt(
										selRow2 + 1,
										0);

					    }
					        /*
							String strSeq2 =
								(String) registerTableModel.getValueAt(
									selRow2 + 1,
									0);
							if (strSeq2 == null || strSeq2.equals("")) {
								selRow2++;
							}
							*/

							if (!tenderTable.hasFocus()
								&& selRow2 < registerTable.getRowCount() - 1) {
								makeRowVisible(registerTable, selRow2 + 1);
								registerTable.changeSelection(
									selRow2 + 1,
									1,
									false,
									false);
							}
							break;
						default :
							posOutputStream.write(keyCode);
							posOutputStream.flush();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		public boolean matchModifiers(int i) {
			if ((i & KeyEvent.ALT_DOWN_MASK) > 0)
				return true;
			if ((i & KeyEvent.CTRL_DOWN_MASK) > 0)
				return true;
			if ((i & KeyEvent.SHIFT_DOWN_MASK) > 0)
				return true;
			return false;
		}
	}

	/**
	 * @param label
	 */
	public void setCashier(String value) {
		cashier.setText(value);
	}

	/**
	 * @param label
	 */
	public void setDatetime(String value) {
		datetime.setText(value);
	}

	/**
	 * @param label
	 */
	public void setDutyNo(String value) {
		//TODO 沧州富达 by fire  2005_5_11
	    //	dutyNo.setText(value);
	}

	/**
	 * @param label
	 */
	public void setHoldNo(String value) {
		holdNo.setText(value);
	}

	/**
	 * @param label
	 */
	public void setWaiterNo(String value) {
		if(value.length()==0)
		{
			jPanel45.remove(waiterNo);
		}
		else
		{
		    waiterNo.setFont(new java.awt.Font("Dialog", 0, 16));
		    waiterNo.setBorder(BorderFactory.createEtchedBorder());
		    waiterNo.setOpaque(false);
		    waiterNo.setRequestFocusEnabled(true);
		    waiterNo.setHorizontalAlignment(SwingConstants.LEFT);
			jPanel45.remove(datetime);
			jPanel45.add(waiterNo, null);
			jPanel45.add(datetime,null);
		    waiterNo.setText(value);

		}
	}

	/**
	 * @param field
	 */
	public void setInputField(final String value) {
		Runnable doInput=new Runnable(){
			public void run() {
				inputField.setText(value);
				inputField.setCaretPosition(value.length());
			}
		};
		SwingUtilities.invokeLater(doInput);
	}

	/**
	 * @param field
	 */
	public void setPaid(String value) {
		paid.setText(value);
	}

	/**
	 * @param label
	 */
	public void setPosNo(String value) {
		posNo.setText(value);
	}

	/**
	 * @param label
	 */
	public void setPrompt(String value) {
		prompt.setText(value);
	}

	/**
	 * @param field
	 */
	public void setSpCode(String value) {
		spCode.setText(value);
	}

	/**
	 * @param field
	 */
	public void setSpName(String value) {
		spName.setText(value);
	}

	/**
	 * @param field
	 */
	public void setSpPrice(String value) {
		spPrice.setText(value);
	}

	/**
	 * @param field
	 */
	public void setSpQuantity(String value) {
		spQuantity.setText(value);
	}

	/**
	 * @param field
	 */
	public void setTotal(String value) {
		total.setText(value);
	}

	/**
	 * @param label
	 */
	public void setTransNo(String value) {
		transNo.setText(value);
	}

	/**
	 * @param field
	 */
	public void setUnPaid(String value) {
		unPaid.setText(value);
	}

    public int disptotal(SheetValue v) {
	    // TODO 自动生成方法存根

	    int row =registerTableModel.getRowCount();
	    String getvalue = (String) registerTableModel.getValueAt(row-1 , 1);

		if( (getvalue.equals("合计")) == true )
		{
			return 1;
		}

		ArrayList columns = new ArrayList();
		registerTableModel.setSeq(sequence);
		columns.add("");
		columns.add("");
		columns.add("合计");
		columns.add("");
		columns.add("");
		columns.add("");
		columns.add("");
		columns.add(new Value(v.getValueTotal()).toString());
		columns.add("");
		registerTableModel.addRow(columns);

	    return 0;

    }

    public int dispMemberCard(MemberCard query) {

		//txtLoanCardNo.setText(query.getSubcardNo());
		//txtCustName.setText(query.getCustName());
		txtCarNo.setText(query.getCardNo());
		//txtLoanCardBalance.setText(query.getMemberName());
		txtLoanCardMainBalance.setText(Integer.toString(query.getMemberLevel()));
		panMemberCard.setVisible(true);

	    return 0;

    }

	/* （非 Javadoc）
	 * @see MainUI#display(Sale)
	 */
	public void display(Sale s) {

	    if(s.getType() == Sale.TOTAL || s.getType() == Sale.AlTPRICE ||s.getType() == Sale.SINGLEDISC
	    || s.getType() == Sale.TOTALDISC || s.getType() == Sale.MONEYDISC || s.getType() == Sale.AUTODISC
		|| s.getType() == Sale.LOANCARD || s.getType() ==Sale.LOANDISC ){

			ArrayList columns = new ArrayList();
			registerTableModel.setSeq(sequence);
			columns.add("");
			columns.add("");
			if(s.getType() == Sale.LOANCARD){
				columns.add("挂帐卡:"+s.getName());
			}else{
		        columns.add(s.getName());
			}
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			if( s.getType() == Sale.TOTAL ){
			    columns.add( new Value( s.getStdValue()).toString() ) ;
			}else if( s.getType() == Sale.LOANCARD ){
				columns.add("");
			}else{
			    columns.add( new Value( s.getStdValue()*(-1)).toString() ) ;
			}

			columns.add("");
			registerTableModel.addRow(columns);
	    }
        else{

		ArrayList columns = new ArrayList();
		registerTableModel.setSeq(sequence);
		sequence++;
		if(s.getquickcorrect() == Sale.YES && s.getType() != Sale.QUICKCORRECT ){
		    columns.add(Integer.toString(registerTableModel.getSeq())+"*");
		}else{
		    columns.add(Integer.toString(registerTableModel.getSeq()));
		}
		String prefix="";
		if(s.getType() == Sale.WITHDRAW){
			prefix="退货 ";
		}else if(s.getType() == Sale.CORRECT){
			prefix="更正 ";
		}else if(s.getType() == Sale.QUICKCORRECT){
			//prefix="即更 ";
                        prefix="更正 ";
		}
		columns.add(s.getVgno());
		columns.add(prefix+s.getName());
		columns.add(s.getSpec());
		columns.add(s.getUnit());
		columns.add( s.getQtyStr() );
		columns.add((new Value(s.getStdPrice())).toString());
		columns.add((new Value(s.getStdValue())).toString());
		columns.add(s.getVgno());
		registerTableModel.addRow(columns);

		setSpName(s.getName());
		setSpCode(s.getVgno());
		setSpQuantity( s.getQtyStr() );
		setSpPrice((new Value(s.getStdPrice())).toString());

		/*
		int discDelta=pos.core.getPosSheet().getValue().getDiscDelta();

		if (discDelta != 0 && s.getlastfavor()==Discount.COMPLEX) {
        	System.out.println("进入＝＝＝＝＝＝＝组合促销.....");
			columns = new ArrayList();
			columns.add("");
			columns.add("");
			columns.add(s.getFavorName());
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add((new Value(discDelta*(-1))).toString());
			columns.add("");
			registerTableModel.addRow(columns);
		}
		*/

    }

	}

public void display4correct(SaleList salelst) {

	ArrayList columns = null;
	registerTableModel.setSeq(sequence);
	sequence++;
	Sale s = null;
	int temp = 0;
	for(int i = 0; i<salelst.size();i++){

		s = salelst.get(i);

		if(s.getDiscType()==Discount.COMPLEX){}
		temp += s.getDiscValue();
		columns.add(Integer.toString(registerTableModel.getSeq()));
		String prefix="";
		columns = new ArrayList();

		columns.add(s.getVgno());
		columns.add(prefix+s.getName());
		columns.add(s.getSpec());
		columns.add(s.getUnit());
		columns.add( s.getQtyStr() );
		columns.add((new Value(s.getStdPrice())).toString());
		columns.add((new Value(s.getStdValue())).toString());
		columns.add(s.getVgno());
		registerTableModel.addRow(columns);

		setSpName(s.getName());
		setSpCode(s.getVgno());
		setSpQuantity( s.getQtyStr() );
		setSpPrice((new Value(s.getStdPrice())).toString());

	}

	if (temp != 0) {
			columns = new ArrayList();
			columns.add("");
			columns.add("");
			if(s.getDiscType()==Discount.COMPLEX){
				columns.add(s.getFavorName());
			}else{
				columns.add((new Discount(s.getDiscType())).getTypeName());
			} 
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add((new Value(temp*(-1))).toString());
			columns.add("");
			registerTableModel.addRow(columns);
		}
	}

	/* （非 Javadoc）
	 * @see MainUI#display(Payment)
	 */
	public void display(Payment p) {

		ArrayList columns = new ArrayList();
		columns.add(Payment.getTypeName(p.getType()));
		columns.add(p.getCurrenCode());
		columns.add((new Value(p.getValue())).toString());
		tenderTableModel.addRow(columns);

	}

	private class TheTenderTable extends JTable {

		public TheTenderTable(TableModel tm) {
			super(tm);
			this.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

		}

		protected boolean processKeyBinding(
			KeyStroke ks,
			KeyEvent e,
			int condition,
			boolean pressed) {

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				return false;
			}

			return super.processKeyBinding(ks, e, condition, pressed);
		}

	}

	private class TheRegisterTable extends JTable {

		public TheRegisterTable(TableModel tm) {
			super(tm);
			this.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		}

		public void valueChanged(ListSelectionEvent e) {
			super.valueChanged(e);

			int selRow = this.getSelectedRow();

			if (selRow >= 0 && selRow < this.getRowCount()) {
				setSpName((String) registerTableModel.getValueAt(selRow, 1));
				setSpCode((String) registerTableModel.getValueAt(selRow, 7));
				setSpQuantity(
					(String) registerTableModel.getValueAt(selRow, 4));
				setSpPrice((String) registerTableModel.getValueAt(selRow, 5));
			}

		}

		/* （非 Javadoc）
		 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
		 */
		protected boolean processKeyBinding(
			KeyStroke ks,
			KeyEvent e,
			int condition,
			boolean pressed) {

			//			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			return false;
			//			}
			//
			//			return super.processKeyBinding(ks, e, condition, pressed);
		}

		/* （非 Javadoc）
		 * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
		 */
		public void changeSelection(
			int rowIndex,
			int columnIndex,
			boolean toggle,
			boolean extend) {
			String strSeq = (String) registerTableModel.getValueAt(rowIndex, 0);
			if (strSeq == null || strSeq.equals(""))
				return;
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
		}

	}

	public void clear() {
		Runnable runner=new Runnable(){
			public void run() {
				registerTableModel.clear();
				sequence=1;
				tenderTableModel.clear();
				clearSP();
				panMemberCard.setVisible(false);
			}
		};
		SwingUtilities.invokeLater(runner);
	}

	public void clearSP() {
		setSpName("");
		setSpCode("");
		setSpQuantity("0");
		setSpPrice((new Value(0)).toString());

	}

	private class ClockThread extends Thread {

		public ClockThread() {
		}

		public void run() {

			while (true) {
				updateTimer();
				try {
					Thread.sleep(10000);
				} catch (java.lang.InterruptedException e) {
				}
			}
		}

		public void updateTimer() {
			setDatetime(Formatter.getLongDate(new Date()));
		}
	}

	/**
	 * @param label
	 */
	public void setConnStatus(String value) {

		if(value.equals("联机")){
			connStatus.setForeground(green);
		}else{
			connStatus.setForeground(red);
		}
		connStatus.setText(value);

	}

	/**
	 * @param label
	 */
	public void setStatus(String value) {
		status.setText(value);
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setStep(int)
	 */
	public void setStep(int value) {
	}

	private class TheMenuListener implements MenuListener {

		/* （非 Javadoc）
		 * @see javax.swing.event.MenuListener#menuCanceled(javax.swing.event.MenuEvent)
		 */
		public void menuCanceled(MenuEvent e) {

		}

		/* （非 Javadoc）
		 * @see javax.swing.event.MenuListener#menuDeselected(javax.swing.event.MenuEvent)
		 */
		public void menuDeselected(MenuEvent e) {
			isMenuActive = false;
		}

		/* （非 Javadoc）
		 * @see javax.swing.event.MenuListener#menuSelected(javax.swing.event.MenuEvent)
		 */
		public void menuSelected(MenuEvent e) {
			isMenuActive = true;
		}

	}


	private class TheTextField extends JTextField{

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

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setUnPaidLabel(java.lang.String)
	 */
	public void setUnPaidLabel(String value) {
		lblUnPaid.setText(value);
	}

    public int quickcorrectrow(){
    	System.out.println("in quickcorrectrow....");
    	int selectrow = registerTable.getSelectedRow();
    	String Qnum = (String) registerTableModel.getValueAt(selectrow,0);
    	Qnum = Qnum.replaceAll("\\*","");
    	int quickrow = Integer.parseInt(Qnum) - 1;
    	return quickrow;
    }
    //TODO 返回显示单据最后一条记录的行数
    public int getLastrow(){
    	int lastrow=registerTableModel.getRowCount()-1;
    	String Qnum="";
    	for(int i=lastrow;i>=0&&Qnum.equals("");i--){
    	   Qnum = (String) registerTableModel.getValueAt(i,0);

    	}
    	Qnum = Qnum.replaceAll("\\*","");
    	int quickrow = Integer.parseInt(Qnum)-1;
    	return quickrow;
    }
    public void quickcorrectchangerow(){
    	int selectrow = registerTable.getSelectedRow();
    	registerTableModel.setValueAt(selectrow,0);
    }
	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#displayDiscount(com.royalstone.pos.common.Sale)
	 */
	public void displayDiscount(Sale s) {

		int discDelta=pos.core.getPosSheet().getValue().getDiscDelta();
		if (discDelta != 0) {
			ArrayList columns = new ArrayList();

			columns = new ArrayList();
			columns.add("");
			columns.add("");
			columns.add((new Discount(s.getDiscType())).getTypeName());
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add((new Value(discDelta*(-1))).toString());
			columns.add("");
			registerTableModel.addRow(columns);
		}
	}

	public void displayprom(Sale s) {

		int discDelta=pos.core.getPosSheet().getValue().getDiscDelta();
		if (discDelta != 0) {
			ArrayList columns = new ArrayList();

			columns = new ArrayList();
			columns.add("");
			columns.add("");
			if( s.getDiscType() == Discount.COMPLEX ){
		        columns.add(s.getFavorName());
			}else{
				columns.add(new Discount(s.getDiscType()).getTypeName());
			}

			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add((new Value(discDelta*(-1))).toString());
			columns.add("");
			registerTableModel.addRow(columns);
		}
	}

	public void displayDiscount4correct(Sale s, SheetValue sheet) {

		int discDelta = sheet.getDiscDelta();
		if (discDelta != 0) {
			ArrayList columns = new ArrayList();

			columns = new ArrayList();
			columns.add("");
			columns.add("");
			columns.add((new Discount(s.getDiscType())).getTypeName());
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add("");
			columns.add((new Value(discDelta*(-1))).toString());
			columns.add("");
			registerTableModel.addRow(columns);
		}
	}


	private class TheMenuBar extends JMenuBar{

			/* （非 Javadoc）
		 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
		 */
		protected boolean processKeyBinding(
			KeyStroke ks,
			KeyEvent e,
			int condition,
			boolean pressed) {
			if(e.getKeyCode()==KeyEvent.VK_F10)return false;
			return super.processKeyBinding(ks, e, condition, pressed);
		}

	}

	private class MyMenu{

		public MyMenu(){
		}

		public JMenuBar createMenuBar(){

			kmap = PosKeyMap.getInstance();

			JMenuBar menuBar = new TheMenuBar();
			JMenu start = new JMenu(" 开 始(S) ");
			start.setBackground(Color.WHITE);
			menuBar.add(start);
			start.setFont(new java.awt.Font("Dialog", 0, 16));
			start.setForeground(Color.RED);
			start.addMenuListener(theMenuListener);
			start.setMnemonic(KeyEvent.VK_S);
			start.setToolTipText("单击这里开始");

			JMenuItem mi ;

			JMenuItem helpitem = new JMenuItem("帮助正文(H)",'H');
			helpitem.setFont(new java.awt.Font("Dialog", 0, 16));
			helpitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,KeyEvent.CTRL_MASK));
			start.add(helpitem);

			mi = new JMenuItem("关于 POS4.1(A)");
			mi.setMnemonic(KeyEvent.VK_A);
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AboutDialog about = new AboutDialog();
					about.show();
				}
			});
			start.add(mi);

			mi = new JMenuItem("最小化(M)",'M');
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,KeyEvent.CTRL_MASK));
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setState(JFrame.ICONIFIED);
				}
			});
			start.add(mi);

			mi = new JMenuItem("退出(E)",'E');
			mi.setFont(new java.awt.Font("Dialog", 0, 16));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
			start.addSeparator();
			start.add(mi);

			helpitem.addActionListener(helpListener());

			return menuBar;
		}


		private ActionListener helpListener(){
			ActionListener listener ;

			HelpSet helpset = null;
			ClassLoader loader = null;
			URL url = HelpSet.findHelpSet(loader, "help/hello.hs");
			try {
				helpset = new HelpSet(loader, url);
			} catch (HelpSetException e) {
				System.err.println("Error loading...");
				System.err.println("HelpSetException: "+e.toString());
				return null;
			}

			HelpBroker helpbroker = helpset.createHelpBroker();
			listener = new CSH.DisplayHelpFromSource(helpbroker);

			return listener;
		}

	}

	/**
	 * @see com.royalstone.pos.gui.MainUI#setWorkDay(java.lang.String)
	 */
	public void setWorkDay(String value) {
		//TODO 沧州富达 by fire  2005_5_11
		//lblWorkDay.setText(value);
	}

	/*
	 * @see com.royalstone.pos.gui.MainUI#setTotalQty(java.lang.String)
	 */
	public void setTotalQty(String value) {
		lblTotalQty.setText(value);
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.gui.MainUI#setPrinterStatus(boolean)
	 */
	public void setPrinterStatus(boolean value) {
		
		if(value){
			lblPrinterStatus.setText("允许");
		}else{
			lblPrinterStatus.setText("禁用");
		}
		
	}

}


