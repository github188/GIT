package com.royalstone.pos.gui;

import com.royalstone.pos.common.PayMode;
import com.royalstone.pos.common.PayModeList;
import com.royalstone.pos.common.PosFunction;
import com.royalstone.pos.core.PosCore;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosKeyMap;
import com.royalstone.pos.shell.pos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sun.swing.table.DefaultTableCellHeaderRenderer;

public class SalePayForm extends JDialog {
	private static final long serialVersionUID = 1L;
	private PayModeTableModel payModeTableModel = null;
	private SalePayDetailTableModel salePayDetailTableModel = null;
	private JTable posPayModeTable = null;
	private JScrollPane scrollPane1 = null;
	private JPanel centerPanel = null;
	private JPanel bottomPanel = null;
	private JLabel tipLabel = null;
	private TheTextField inputText = null;
	private PayFormKeyboard keyListener = null;
	private int frame_hight = 290;
	private int frame_width = 500;

	private boolean isConfirm;
	private double money;
	private double input_money;
	private String paycode;
	
	public SalePayForm(double value) 
	{
		super(pos.posFrame);
		
		money = value;

		this.keyListener = new PayFormKeyboard();

		drawUI();

		addKeyAndContainerListenerRecursively(this);

		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				SalePayForm.this.inputText.requestFocus();
			}

			public void windowLostFocus(WindowEvent e) {
			}
		});
		/*
		ArrayList<String> data = new ArrayList<String>();
		for (PayMode pm : pos.core.payModeList.getPaymode_lst()) {
			data.add(pm.getPaycode());
			data.add(pm.getPayname());
		}*/
		
		setTableData(pos.core.payModeList.getPaymode_lst());
	}

	private void drawUI() {
		setTitle("付款");
		setModal(true);
		setSize(this.frame_width, this.frame_hight);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) ((screenSize.getWidth() - this.frame_width) / 2.0D),
				(int) ((screenSize.getHeight() - this.frame_hight) / 2.0D));
		this.payModeTableModel = new PayModeTableModel();

		this.posPayModeTable = new JTable(this.payModeTableModel);
		//this.posPayModeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
		thr.setHorizontalAlignment(JLabel.RIGHT);
		this.posPayModeTable.getTableHeader().setDefaultRenderer(thr);
		this.posPayModeTable.getTableHeader().setFont(new Font("Dialog", 0, 30));
		this.posPayModeTable.getTableHeader().setForeground(Color.RED);
		this.posPayModeTable.getTableHeader().setPreferredSize(new Dimension(this.posPayModeTable.getTableHeader().getWidth(),50));
		this.posPayModeTable.setFont(new Font("Dialog", 0, 20));
		this.posPayModeTable.setRowHeight(30);
		this.posPayModeTable.setDefaultRenderer(JLabelTableCellRenderer.class,new JLabelTableCellRenderer());
		this.posPayModeTable.setRowSelectionAllowed(true);

		this.scrollPane1 = new JScrollPane(this.posPayModeTable);

//		this.scrollPane1.addComponentListener(new ComponentAdapter() {
//			public void componentResized(ComponentEvent e) {
//				SalePayForm.this.resizeTable(true);
//			}
//		});
		
		this.tipLabel = new JLabel("付款金额");
		this.tipLabel.setFont(new Font("Dialog", 0, 20));
		this.tipLabel.setPreferredSize(new Dimension(100, 30));
		this.tipLabel.setAlignmentY(0.5F);
		this.tipLabel.setBorder(BorderFactory.createEmptyBorder(4, 5, 0, 5));

		this.inputText = new TheTextField();
		this.inputText.setFont(new Font("Dialog", 0, 20));
		this.inputText.setPreferredSize(new Dimension(200, 30));

		this.bottomPanel = new JPanel();
		this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, 0));
		this.bottomPanel.add(this.tipLabel);
		this.bottomPanel.add(Box.createHorizontalStrut(5));
		this.bottomPanel.add(this.inputText);

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, 1));
		this.centerPanel.add(this.scrollPane1);
		this.centerPanel.add(Box.createVerticalStrut(10));
		this.centerPanel.add(this.bottomPanel);

		getContentPane().add(this.centerPanel, "Center");
	}

	protected void resizeTable(boolean bool) {
		Dimension containerwidth = null;
		if (!bool) {
			containerwidth = this.scrollPane1.getPreferredSize();
		} else {
			containerwidth = this.scrollPane1.getSize();
		}
		int allwidth = this.posPayModeTable.getIntercellSpacing().width;
		for (int j = 0; j < this.posPayModeTable.getColumnCount(); j++) {
			int max = 0;
			for (int i = 0; i < this.posPayModeTable.getRowCount(); i++) {
				int width =

				this.posPayModeTable
						.getCellRenderer(i, j)
						.getTableCellRendererComponent(this.posPayModeTable,
								this.posPayModeTable.getValueAt(i, j), false,
								false, i, j).getPreferredSize().width;
				if (width > max) {
					max = width;
				}
			}
			int headerwidth =

			this.posPayModeTable
					.getTableHeader()
					.getDefaultRenderer()
					.getTableCellRendererComponent(
							this.posPayModeTable,
							this.posPayModeTable.getColumnModel().getColumn(j)
									.getIdentifier(), false, false, -1, j)
					.getPreferredSize().width;

			max += headerwidth;

			this.posPayModeTable.getColumnModel().getColumn(j)
					.setPreferredWidth(max);

			allwidth += max + this.posPayModeTable.getIntercellSpacing().width;
		}
		allwidth += this.posPayModeTable.getIntercellSpacing().width;
		if (allwidth > containerwidth.width) {
			this.posPayModeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		} else {
			this.posPayModeTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		}
	}

	public void setTableData(Vector data) {
		this.payModeTableModel.SetData(data);
		if (this.posPayModeTable.getRowCount() > 0) {
			this.posPayModeTable.changeSelection(0, 1, false, false);
			setDefaultInput();
		}
	}

	private void setDefaultInput() {
		this.inputText.setText("" + money);
		this.inputText.selectAll();
	}

	private void displaySelectedRow(int row) {
		makeRowVisible(this.posPayModeTable, row);
		this.posPayModeTable.changeSelection(row, 1, false, false);
		setDefaultInput();
	}

	private class TheTextField extends JTextField {
		private static final long serialVersionUID = 1L;
		private boolean isSelect = false;

		public TheTextField() {
		}

		public void selectAll() {
			setSelect(true);
			super.selectAll();
		}

		public void setText(String t) {
			setSelect(false);
			super.setText(t);
		}

		protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
				int condition, boolean pressed) {
			return false;
		}

		public boolean isSelect() {
			return this.isSelect;
		}

		private void setSelect(boolean isSelect) {
			this.isSelect = isSelect;
		}
	}

	private class PayModeTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList data = new ArrayList();
		private String[] columnNames = {"应付 " + money + "  "};

		private PayModeTableModel() {
		}

		public int getRowCount() {
			return this.data.size();
		}

		public int getColumnCount() {
			return this.columnNames.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex >= this.data.size()) {
				return null;
			}
			ArrayList columns = (ArrayList) this.data.get(rowIndex);
			if (columnIndex >= columns.size()) {
				return null;
			}
			return columns.get(columnIndex);
		}

		public void clear() {
			this.data.clear();
			fireTableDataChanged();
		}

		public void addRow(ArrayList columns) {
			this.data.add(columns);
			fireTableDataChanged();
		}

		public String getColumnName(int col) {
			return this.columnNames[col];
		}

		public void SetData(Vector<PayMode> rows) {
			this.data.clear();
			ArrayList columns = null;
			for (PayMode o : rows) {
				columns = new ArrayList();
				columns.add("  [" + o.getPayname()+" "+ o.getPaycode() + "]");
				addRow(columns);
			}
		}
	}

	private class SalePayDetailTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private SalePayDetailTableModel() {
		}

		public int getRowCount() {
			return 0;
		}

		public int getColumnCount() {
			return 0;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return null;
		}
	}

	private class JLabelTableCellRenderer implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel label = new JLabel();

		private JLabelTableCellRenderer() 
		{
		}
		
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			TableColumn tableColumn = table.getColumnModel().getColumn(col);
			this.label.setOpaque(true);
			this.label.setFont(table.getFont());
			this.label.setSize(tableColumn.getWidth(),table.getRowHeight());
			this.label.setHorizontalAlignment(JLabel.LEFT);
			this.label.setText(value.toString());
			if (isSelected) {
				this.label.setBackground(table.getSelectionBackground());
			} else {
				this.label.setBackground(table.getBackground());
			}
			return this.label;
		}
	}

	private class PayFormKeyboard implements KeyListener {
		int c;
		int selRow;
		int key;
		String buffer;
		private PosKeyMap kmap = PosKeyMap.getInstance();

		private PayFormKeyboard() {
		}

		public void keyPressed(KeyEvent e) {
			if (!matchModifiers(e.getModifiersEx())) {
				try {
					c = e.getKeyCode();
					key = this.kmap.getFunction(this.c).getKey();
					buffer = SalePayForm.this.inputText.getText();
					selRow = SalePayForm.this.posPayModeTable
							.getSelectedRow();

					switch (this.key) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						if (SalePayForm.this.inputText.isSelect()) buffer = "";
						buffer += (char) this.key;
						buffer = SalePayForm.this.normalizeDecimalBuffer(
								buffer, 6, 2);
						SalePayForm.this.inputText.setText(buffer);
						SalePayForm.this.inputText.setCaretPosition(buffer
								.length());
						break;
					case PosFunction.ENTER:
						setConfirm(true,buffer,selRow);
						break;
					case PosFunction.UP:
						if (selRow > 0) {
							SalePayForm.this
									.displaySelectedRow(this.selRow - 1);
						}
						break;
					case PosFunction.DOWN:
						if (selRow < SalePayForm.this.posPayModeTable
								.getRowCount() - 1) {
							SalePayForm.this
									.displaySelectedRow(selRow + 1);
						}
						break;
					case PosFunction.BACKSPACE:
						if (!SalePayForm.this.inputText.isSelect()) {
							if (buffer.length() > 0) {
								buffer = buffer.substring(0,
										buffer.length() - 1);
							}
						} else {
							buffer = "";
						}
						SalePayForm.this.inputText.setText(buffer);
						SalePayForm.this.inputText.setCaretPosition(buffer
								.length());
						break;
					case PosFunction.POINT:
						if (buffer.indexOf(".") < 0) {
							buffer += '.';
							SalePayForm.this.inputText.setText(buffer);
							SalePayForm.this.inputText
									.setCaretPosition(buffer.length());
						}
						break;
					case PosFunction.BIZERO:
						buffer += "00";
						SalePayForm.this.inputText.setText(buffer);
						SalePayForm.this.inputText.setCaretPosition(buffer
								.length());
						break;
					case PosFunction.CANCEL:
					case PosFunction.EXIT:
						setConfirm(false,buffer,selRow);
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

	private String normalizeDecimalBuffer(String buffer, int n, int m) {
		int offset = buffer.indexOf('.');
		if (offset < 0) {
			if (buffer.length() > n) {
				buffer = buffer.substring(0, n);
			}
			int i = atoi(buffer);
			buffer = i + "";
			return buffer;
		}
		if ((offset >= 0) && (!buffer.endsWith("."))) {
			String a = buffer.substring(0, offset);
			String b = buffer.substring(offset + 1, buffer.length());
			if (b.length() > m) {
				b = b.substring(0, m);
			}
			buffer = atoi(a) + "." + b;
		}
		return buffer;
	}

	private int atoi(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	private double atof(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
		}
		return 0.0D;
	}

	public void makeRowVisible(JTable table, int visibleRow) {
		if (table.getColumnCount() == 0) {
			return;
		}
		if ((visibleRow < 0) || (visibleRow >= table.getRowCount())) {
			return;
		}
		Rectangle visible = table.getVisibleRect();
		Rectangle cell = table.getCellRect(visibleRow, 0, true);
		if (cell.y < visible.y) {
			visible.y = cell.y;
			table.scrollRectToVisible(visible);
		} else if (cell.y + cell.height > visible.y + visible.height) {
			visible.y = (cell.y + cell.height - visible.height);
			table.scrollRectToVisible(visible);
			table.paintImmediately(table.getBounds());
		}
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

	public static void main(String[] args) {
		Vector<PayMode> v = new Vector<PayMode>();
		PayMode pm = new PayMode("m", "微信", 1, 0);
		v.add(pm);
		
		pm = new PayMode("z", "支付宝", 1, 0);
		v.add(pm);
		
		pm = new PayMode("a", "翼支付", 1, 0);
		v.add(pm);
		
		pm = new PayMode("b", "储值卡", 1, 0);
		v.add(pm);
		pm = new PayMode("c", "会员积分", 1, 0);
		v.add(pm);

		SalePayForm form = new SalePayForm(100);
		form.setTableData(v);
		form.setVisible(true);
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	private void setConfirm(boolean isConfirm,String buffer,int row) {
		this.isConfirm = isConfirm;
		if(this.isConfirm)
		{
			//输入金额大于默认金额
			if(Double.compare(Double.valueOf(buffer).doubleValue(),money) > 0)
			{
				//找零
				if(pos.core.payModeList.payModeISZL(row))
				{
					setInputMoney(Double.valueOf(buffer).doubleValue());
					setPaycode(pos.core.payModeList.getPayCode(row));
					dispose();
				}
				else
				{
					
				}
			}
			else
			{
				setInputMoney(Double.valueOf(buffer).doubleValue());
				setPaycode(pos.core.payModeList.getPayCode(row));
				dispose();
			}
		}
		else
		{
			setInputMoney(0);
			setPaycode(0 +"");
			dispose();
		}
		
	}

	public double getInputMoney() {
		return input_money;
	}

	public void setInputMoney(double value) {
		this.input_money = value;
	}

	public String getPaycode() {
		return paycode;
	}

	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
}
