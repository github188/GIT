package com.royalstone.pos.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.royalstone.pos.common.CashBox;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.util.Value;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * 收款检查对话框
 * @author liangxinbiao
 */
public class CashBoxInfo extends JDialog {
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel panList = new JPanel();
	JPanel jPanel3 = new JPanel();
	JButton jButton1 = new JButton();
	BorderLayout borderLayout2 = new BorderLayout();

	private TenderTableModel tenderTableModel;
	private JTable tenderTable;

	public CashBoxInfo() {
		try {
			jbInit();

			setTitle("收款检查");
			setSize(500, 450);
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 500) / 2),
				(int) ((screenSize.getHeight() - 450) / 2));

			tenderTableModel = new TenderTableModel();
			tenderTable = new TheTenderTable(tenderTableModel);

			tenderTable.getTableHeader().setFont(
				new java.awt.Font("Dialog", 0, 16));
			tenderTable.setFont(new java.awt.Font("Dialog", 0, 16));
			tenderTable.setRowHeight(30);
			tenderTable.setDefaultRenderer(
				JLabel.class,
				new JLabelTableCellRenderer());
				
			TableColumn col = tenderTable.getColumnModel().getColumn(0);
			col.setPreferredWidth(100);

			col = tenderTable.getColumnModel().getColumn(1);
			col.setPreferredWidth(100);

			col = tenderTable.getColumnModel().getColumn(2);
			col.setPreferredWidth(220);


			JScrollPane scrollPane = new JScrollPane(tenderTable);
			scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);

			panList.add(scrollPane, BorderLayout.CENTER);
			
			addKeyAndContainerListenerRecursively(this);


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
		jPanel3.setPreferredSize(new Dimension(10, 40));
		jButton1.setFont(new java.awt.Font("Dialog", 0, 14));
		jButton1.setPreferredSize(new Dimension(100, 30));
		jButton1.setText("确 定");
		jButton1.addActionListener(
			new CashBoxInfo_jButton1_actionAdapter(this));
		panList.setLayout(borderLayout2);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(panList, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(jButton1, null);
	}

	void jButton1_actionPerformed(ActionEvent e) {
		dispose();
	}

	/**
	 * 收银JTable的数据模型
	 * @author liangxinbiao
	 */
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
			Runnable doAddRow = new Runnable() {
				public void run() {
					data.add(columns);
					fireTableDataChanged();
				}

			};
			SwingUtilities.invokeLater(doAddRow);
		}

		public void clear() {
			data = new ArrayList();
			fireTableDataChanged();
		}

	}

	/**
	 *  
	 * @author liangxinbiao
	 */
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
	
	/**
	 * 
	 * @author liangxinbiao
	 */
	private class JLabelTableCellRenderer implements TableCellRenderer {
		private JLabel label = new JLabel();

		/**
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
	
	
	private PosKeyboard keyListener = new PosKeyboard();

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
	 * @author liangxinbiao
	 */
	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER){
				dispose();				
			}
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void keyTyped(KeyEvent e) {
			
		}
	}
	
	
	/**
	 * 
	 * @param box 一项现在钱箱里的收款信息
	 */
	public void addCashBox(CashBox box){
		ArrayList columns=new ArrayList();
		columns.add(Payment.getTypeName(box.getType()));
		columns.add(box.getCurrenCode());
		columns.add((new Value(box.getValue())).toString());
		
		tenderTableModel.addRow(columns);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		CashBoxInfo cashBoxInfo=new CashBoxInfo();
		cashBoxInfo.show();
	}


}

class CashBoxInfo_jButton1_actionAdapter
	implements java.awt.event.ActionListener {
	CashBoxInfo adaptee;

	CashBoxInfo_jButton1_actionAdapter(CashBoxInfo adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.jButton1_actionPerformed(e);
	}
}
