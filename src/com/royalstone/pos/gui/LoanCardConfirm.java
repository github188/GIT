/*
 * 创建日期 2004-6-13
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.royalstone.pos.shell.pos;

/**
 * 挂账卡的确认对话框
 * @deprecated
 * @author liangxinbiao
 */
public class LoanCardConfirm extends JDialog implements CardConfirmUI {

	private JTable cardTable;
	private CardTableModel cardTableModel;
	private JScrollPane scrollPane;
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

	public LoanCardConfirm() {
		super(pos.posFrame);
		setModal(true);
		setSize(580, 200);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
			(int) ((screenSize.getWidth() - 580) / 2),
			(int) ((screenSize.getHeight() - 200) / 2));

		//		this.setLocation(280, 150);
		//addKeyListenerRecursively(this.getContentPane());

		enterButton.addActionListener(enterButtonAction);
		clearButton.addActionListener(clearButtonAction);

		enterButton.addKeyListener(keyListener);
		clearButton.addKeyListener(keyListener);

		cardTableModel = new CardTableModel();
		cardTable = new JTable(cardTableModel);
		cardTable.setEnabled(false);
		cardTable.setDefaultRenderer(
			JLabel.class,
			new JLabelTableCellRenderer());

		cardTable.getTableHeader().setFont(new java.awt.Font("Dialog", 0, 16));
		cardTable.setFont(new java.awt.Font("Dialog", 0, 16));
		cardTable.setRowHeight(30);
		scrollPane = new JScrollPane(cardTable);
		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		TableColumn col = null;

		col = cardTable.getColumnModel().getColumn(CardTableModel.CARD_NO);
		col.setPreferredWidth(150);
		//		col.setMaxWidth(120);
		//		col.setMinWidth(120);

		col = cardTable.getColumnModel().getColumn(CardTableModel.CARD_NAME);
		col.setPreferredWidth(60);
		//		col.setMaxWidth(60);
		//		col.setMinWidth(60);

		col = cardTable.getColumnModel().getColumn(CardTableModel.CARD_TYPE);
		col.setPreferredWidth(40);
		//		col.setMaxWidth(40);
		//		col.setMinWidth(40);

		/*col = cardTable.getColumnModel().getColumn(CardTableModel.TENDER_AMOUNT);
		col.setPreferredWidth(0);
		col.setMaxWidth(0);
		col.setMinWidth(0);
		
		col = cardTable.getColumnModel().getColumn(CardTableModel.AUTUAL_TENDER_AMOUNT);
		col.setPreferredWidth(0);
		col.setMaxWidth(0);
		col.setMinWidth(0);
		
		col = cardTable.getColumnModel().getColumn(CardTableModel.AMOUNT);
		col.setPreferredWidth(0);
		col.setMaxWidth(0);
		col.setMinWidth(0);*/

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCardNo(String value) {
		cardTableModel.setCardNo(value);
	}

	public void setTenderAmount(String value) {
		cardTableModel.setTenderAmount(value);
	}

	public void setCardAmount(String value) {
		cardTableModel.setCardAmount(value);
	}

	public void setBalance(String value) {
		cardTableModel.setBalance(value);
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
					"挂账卡",
					"挂账",
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
		clearButton.setPreferredSize(new Dimension(100, 33));
		clearButton.setText("取消");
		enterButton.setFont(new java.awt.Font("Dialog", 0, 16));
		enterButton.setPreferredSize(new Dimension(100, 33));
		enterButton.setText("确认");
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(scrollPane, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(enterButton, null);
		jPanel2.add(clearButton, null);
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
			isFinish = true;
			dispose();
		}

	}

	private class ClearButtonAction implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			confirm = false;
			isFinish = true;
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
