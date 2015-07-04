package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.common.SheetBrief;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Value;

/**
 * 
 * 显示挂单列表的对话框
 * @author liangxinbiao
 * @version 1.0
 */

public class HoldList extends JDialog {
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JButton btnCancel = new JButton();
	JButton btnEnter = new JButton();

	private TheTableModel theTableModel;
	private JTable theTable;
	private boolean confirm = false;

	private int holdNo;
	private EnterButtonAction enterBtnAction = new EnterButtonAction();
	private CancelButtonAction cancelBtnAction = new CancelButtonAction();

	BorderLayout borderLayout2 = new BorderLayout();
	private PosKeyMap kmap;
	

	/**
	 * 
	 * @param briefs 所有挂单的摘要
	 */
	public HoldList(SheetBrief briefs[]) {
		super(pos.posFrame);
		try {

			kmap = PosKeyMap.getInstance();
			
			setModal(true);
			setSize(580, 300);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(
				(int) ((screenSize.getWidth() - 580) / 2),
				(int) ((screenSize.getHeight() - 300) / 2));

			jbInit();

			theTableModel = new TheTableModel();
			theTable = new TheTable(theTableModel);

			theTable.getTableHeader().setFont(
				new java.awt.Font("Dialog", 0, 20));
			theTable.setFont(new java.awt.Font("Dialog", 0, 20));
			theTable.setRowHeight(30);
			theTable.setDefaultRenderer(
				JLabel.class,
				new JLabelTableCellRenderer());
				
			TableColumn col = theTable.getColumnModel().getColumn(0);
			col.setPreferredWidth(60);

			col = theTable.getColumnModel().getColumn(1);
			col.setPreferredWidth(80);

			col = theTable.getColumnModel().getColumn(2);
			col.setPreferredWidth(300);
//TODO



			JScrollPane scrollPane1 = new JScrollPane(theTable);
			scrollPane1.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jPanel2.add(scrollPane1, BorderLayout.CENTER);

			btnEnter.addActionListener(enterBtnAction);
			btnCancel.addActionListener(cancelBtnAction);

			for (int i = 0; i < briefs.length; i++) {
				SheetBrief sheetBrief = briefs[i];
				if (sheetBrief != null && !sheetBrief.isEmpty()) {
					ArrayList column = new ArrayList();
					column.add(Integer.toString((theTable.getRowCount() + 1)));
					column.add(sheetBrief.getTime().toString());
					//TODO 

					column.add(
						(new Value(sheetBrief.getValueTotal())).toString());
					column.add(new Integer(i));
					theTableModel.addRow(column);
				}
			}

			if (theTable.getRowCount() > 0){
				theTable.changeSelection(0, 1, false, false);
			}
				

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
		btnCancel.setFont(new java.awt.Font("Dialog", 0, 16));
		btnCancel.setPreferredSize(new Dimension(100, 33));
		btnCancel.setText("取消");
		btnEnter.setFont(new java.awt.Font("Dialog", 0, 16));
		btnEnter.setPreferredSize(new Dimension(100, 33));
		btnEnter.setText("确定");
		jPanel2.setLayout(borderLayout2);
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.CENTER);
		jPanel1.add(jPanel3, BorderLayout.SOUTH);
		jPanel3.add(btnEnter, null);
		jPanel3.add(btnCancel, null);
	}

	/**
	 * 
	 * @param table
	 * @param visibleRow
	 */
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
		}
	}

	/**
	 * JTable的数据模板
	 * @author liangxinbiao
	 */
	private class TheTableModel extends AbstractTableModel {
        //TODO 
		private ArrayList data = new ArrayList();
		private String columnNames[] = { "序号", "时间", "金额" };

		public Class getColumnClass(int col) {
			//TODO 
			if (col == 2) {
				return JLabel.class;
			}
			return Object.class;
		}
		//TODO
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

		public void clear() {
			data = new ArrayList();
			fireTableDataChanged();
		}

		public void addRow(ArrayList columns) {
			data.add(columns);
			fireTableDataChanged();
			//makeRowVisible(theTable, theTable.getRowCount() - 1);
		}

	}

	/**
	 * 
	 * @author liangxinbiao
	 */
	private class JLabelTableCellRenderer implements TableCellRenderer {
		private JLabel label = new JLabel();

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
	
	/**
	 * 
	 * 确定按钮按下时的响应方法
	 * @author liangxinbiao
	 *  
	 */
	private class EnterButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			holdNo=((Integer)theTableModel.getValueAt(theTable.getSelectedRow(),3)).intValue();
			confirm = true;
			dispose();
		}

	}
	
	/**
	 * 取消按钮按下时的响应方法
	 * @author liangxinbiao
	 *
	 */
	private class CancelButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			confirm = false;
			dispose();
		}

	}
	
	/**
	 * JTable的派生类，主要是屏蔽JTable对回车键的默认的动作
	 * @author liangxinbiao
	 *
	 */
	private class TheTable extends JTable {
		public TheTable(TableModel tm) {
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
	 * @return 返回用户所选的挂单号码
	 */
	public int getHoldNo() {
		return holdNo;
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
	 * 处理键盘动作
	 * @author liangxinbiao
	 */
	private class PosKeyboard implements KeyListener {

		public void keyPressed(KeyEvent e) {
			switch (kmap.getFunction(e.getKeyCode()).getKey()) {
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
					int selRow = 0;
					if (theTable.getRowCount() < 10) {
						selRow = Integer.parseInt("" + e.getKeyChar()) - 1;
					} else {
						selRow =
							Integer.parseInt("" + e.getKeyChar() + "0") - 1;
						if(selRow==-1)selRow=0;
							
					}
					if (selRow >= 0 && selRow < theTable.getRowCount()) {
						theTable.changeSelection(selRow, 1, false, false);
						makeRowVisible(theTable, selRow);
					}
					break;
				case PosFunction.ENTER:
					//TODO
					holdNo=((Integer)theTableModel.getValueAt(theTable.getSelectedRow(),3)).intValue();   
					confirm = true;
					dispose();
					break;
				case PosFunction.EXIT :
				case PosFunction.CANCEL :
					confirm = false;
					dispose();
					break;
			}


		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}
	
	/**
	 * 
	 * @return 返回用户是否按下确定按钮
	 */
	public boolean isConfrim(){
		return confirm;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		HoldList holdlst = new HoldList(null);
		holdlst.show();
	}

}
