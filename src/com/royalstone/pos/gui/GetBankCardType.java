/*
 * 创建日期 2004-8-18
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
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
//import java.util.HashMap;

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

//import com.royalstone.pos.common.SheetBrief;
import com.royalstone.pos.shell.pos;
//import com.royalstone.pos.util.Value;
import com.royalstone.pos.util.getCardType;

public class GetBankCardType extends JDialog {
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JButton btnCancel = new JButton();
	JButton btnEnter = new JButton();

	private TheTableModel theTableModel;
	private TheTable theTable;
	private boolean confirm = false;

	private int holdNo;
	private EnterButtonAction enterBtnAction = new EnterButtonAction();
	private CancelButtonAction cancelBtnAction = new CancelButtonAction();

	BorderLayout borderLayout2 = new BorderLayout();

	public GetBankCardType() {
		super(pos.posFrame);
		try {
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
			

			JScrollPane scrollPane1 = new JScrollPane(theTable);
			scrollPane1.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jPanel2.add(scrollPane1, BorderLayout.CENTER);

			btnEnter.addActionListener(enterBtnAction);
			btnCancel.addActionListener(cancelBtnAction);
			getCardType cardtype = getCardType.getInstance();
			ArrayList AllCardType=(ArrayList)cardtype.getAllCardType();
			for (int i=0;i<AllCardType.size();i++)
			{
				ArrayList Card=(ArrayList)AllCardType.get(i);
				ArrayList column = new ArrayList();
				column.add(Integer.toString((theTable.getRowCount() + 1)));
				column.add(String.valueOf(Card.get(1)));
				column.add(String.valueOf(Card.get(2))+String.valueOf(Card.get(3)));
				column.add(new Integer(i));
				theTableModel.addRow(column);
			}

			if (theTable.getRowCount() > 0){
				theTable.changeSelection(0, 1, false, false);
			}
				

			addKeyAndContainerListenerRecursively(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
	public TheTableModel getTheTableModel()
	{
		return theTableModel;
	}
	public TheTable getTheTable()
	{
		return theTable;
	}
	private class TheTableModel extends AbstractTableModel {

		private ArrayList data = new ArrayList();
		private String columnNames[] = { "序号", "卡类型代码", "卡类型" };

		public Class getColumnClass(int col) {
			if (col == 3) {
				return JLabel.class;
			}
			return Object.class;
		}

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

	private class CancelButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			confirm = false;
			dispose();
		}

	}

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

	public int getHoldNo() {
		return holdNo;
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
			switch (e.getKeyCode()) {
				case KeyEvent.VK_0 :
				case KeyEvent.VK_1 :
				case KeyEvent.VK_2 :
				case KeyEvent.VK_3 :
				case KeyEvent.VK_4 :
				case KeyEvent.VK_5 :
				case KeyEvent.VK_6 :
				case KeyEvent.VK_7 :
				case KeyEvent.VK_8 :
				case KeyEvent.VK_9 :
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
				case KeyEvent.VK_ENTER :
					holdNo=((Integer)theTableModel.getValueAt(theTable.getSelectedRow(),3)).intValue();   
					confirm = true;
					dispose();
					break;
				case KeyEvent.VK_ESCAPE :
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
	
	public boolean isConfrim(){
		return confirm;
	}
	
	public static void main(String[] args){
		GetBankCardType bankcardtype = new GetBankCardType();
		bankcardtype.show();
	}

}
