package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.ImportSmallTicketBackupEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class ImportSmallTicketBackupForm
{
	public Shell shell;

	public ImportSmallTicketBackupForm()
	{
		this.open();
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
		
		new ImportSmallTicketBackupEvent(this);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
		if (!shell.isDisposed())
		{
			shell.open();
		}

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}

	public TableColumn newColumnTableColumn;
	public Table table_SmallTicketList;
	public Table tabPay;
	public Table tabTicketDeatilInfo;
	public StyledText txtGiveChangeMoney;
	public StyledText txtFactInceptMoney;
	public StyledText txtAgioMoney;
	public StyledText txtShouldInceptMoney;

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.NONE | GlobalVar.style);
		shell.setSize(800, 510);
		shell.setText(Language.apply("导入小票备份"));

		table_SmallTicketList = new Table(shell, SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER);
		table_SmallTicketList.setHeaderVisible(true);
		table_SmallTicketList.setLinesVisible(true);
		table_SmallTicketList.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		table_SmallTicketList.setBounds(10, 10, 774, 114);

		newColumnTableColumn = new TableColumn(table_SmallTicketList, SWT.CENTER);
		newColumnTableColumn.setAlignment(SWT.CENTER);
		newColumnTableColumn.setWidth(59);
		newColumnTableColumn.setText(Language.apply("选择"));

		final TableColumn newColumnTableColumn_11 = new TableColumn(table_SmallTicketList, SWT.NONE);
		newColumnTableColumn_11.setWidth(140);
		newColumnTableColumn_11.setText(Language.apply("收银机号"));

		final TableColumn newColumnTableColumn_10 = new TableColumn(table_SmallTicketList, SWT.NONE);
		newColumnTableColumn_10.setWidth(140);
		newColumnTableColumn_10.setText(Language.apply("小票号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(table_SmallTicketList, SWT.NONE);
		newColumnTableColumn_1.setWidth(313);
		newColumnTableColumn_1.setText(Language.apply("交易时间"));

		final TableColumn newColumnTableColumn_12 = new TableColumn(table_SmallTicketList, SWT.NONE);
		newColumnTableColumn_12.setWidth(95);
		newColumnTableColumn_12.setText(Language.apply("收银员"));

		final Group group = new Group(shell, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		group.setText(Language.apply("小票明细"));
		group.setBounds(10, 134, 774, 252);

		tabTicketDeatilInfo = new Table(group, SWT.FULL_SELECTION | SWT.BORDER);
		tabTicketDeatilInfo.setBounds(10, 25, 754, 113);
		tabTicketDeatilInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabTicketDeatilInfo.setLinesVisible(true);
		tabTicketDeatilInfo.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_1_1 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_1_1.setWidth(143);
		newColumnTableColumn_1_1.setText(Language.apply("商品编码"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_2.setWidth(152);
		newColumnTableColumn_2.setText(Language.apply("商品名称"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_3.setWidth(113);
		newColumnTableColumn_3.setText(Language.apply("单价"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(93);
		newColumnTableColumn_4.setText(Language.apply("数量"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_5.setWidth(101);
		newColumnTableColumn_5.setText(Language.apply("折扣额"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_6.setWidth(122);
		newColumnTableColumn_6.setText(Language.apply("应收金额"));

		tabPay = new Table(group, SWT.FULL_SELECTION | SWT.BORDER);
		tabPay.setBounds(10, 151, 754, 91);
		tabPay.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabPay.setLinesVisible(true);
		tabPay.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_7 = new TableColumn(tabPay, SWT.NONE);
		newColumnTableColumn_7.setWidth(213);
		newColumnTableColumn_7.setText(Language.apply("付款名称"));

		final TableColumn newColumnTableColumn_8 = new TableColumn(tabPay, SWT.NONE);
		newColumnTableColumn_8.setWidth(320);
		newColumnTableColumn_8.setText(Language.apply("付款帐号"));

		final TableColumn newColumnTableColumn_9 = new TableColumn(tabPay, SWT.RIGHT);
		newColumnTableColumn_9.setWidth(191);
		newColumnTableColumn_9.setText(Language.apply("付款金额"));

		final Label label_1_1 = new Label(shell, SWT.NONE);
		label_1_1.setBounds(452, 404, 332, 54);
		label_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.BOLD));
		label_1_1.setText(Language.apply("1、光标上下键，浏览小票明细；") + "\r\n" + Language.apply("2、回车键，勾选或取消当前行；") + "\r\n" + Language.apply("3、确认键，执行备份小票导入当前数据库。"));

		final Group group_2 = new Group(shell, SWT.NONE);
		group_2.setBounds(10, 388, 436, 87);
		group_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_6 = new Label(group_2, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6.setBounds(5, 20, 80, 19);
		label_6.setText(Language.apply("应收金额"));

		txtShouldInceptMoney = new StyledText(group_2, SWT.READ_ONLY | SWT.BORDER);
		txtShouldInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtShouldInceptMoney.setEnabled(false);
		txtShouldInceptMoney.setEditable(false);
		txtShouldInceptMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtShouldInceptMoney.setBounds(90, 15, 109, 28);

		final Label label_7 = new Label(group_2, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_7.setBounds(230, 55, 80, 19);
		label_7.setText(Language.apply("找零金额"));

		final Label label_8 = new Label(group_2, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_8.setBounds(230, 20, 80, 19);
		label_8.setText(Language.apply("折扣金额"));

		txtAgioMoney = new StyledText(group_2, SWT.READ_ONLY | SWT.BORDER);
		txtAgioMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAgioMoney.setEnabled(false);
		txtAgioMoney.setEditable(false);
		txtAgioMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtAgioMoney.setBounds(316, 15, 109, 28);

		final Label label_9 = new Label(group_2, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_9.setBounds(5, 55, 80, 19);
		label_9.setText(Language.apply("实收金额"));

		txtFactInceptMoney = new StyledText(group_2, SWT.READ_ONLY | SWT.BORDER);
		txtFactInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtFactInceptMoney.setEnabled(false);
		txtFactInceptMoney.setEditable(false);
		txtFactInceptMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtFactInceptMoney.setBounds(90, 50, 109, 28);

		txtGiveChangeMoney = new StyledText(group_2, SWT.READ_ONLY | SWT.BORDER);
		txtGiveChangeMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGiveChangeMoney.setEnabled(false);
		txtGiveChangeMoney.setEditable(false);
		txtGiveChangeMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtGiveChangeMoney.setBounds(316, 52, 109, 28);
	}

}
