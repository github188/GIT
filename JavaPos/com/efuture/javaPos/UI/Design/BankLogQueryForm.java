package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.BankLogQueryEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class BankLogQueryForm 
{
	private Combo cmbDjlb;
	private Text txtDate = null;
	private Text txtReturnMsg = null;
	private Text txtReturnCode = null;
	private Text txtOldTrace = null;
	private Text txtLodTime = null;
	private Text txtBank = null;
	private Text txtTrace = null;
	private Text txtMoney = null;
	private Text txtCardCode = null;
	private Text txtType = null;
	private Text txtSyyCode = null;
	private Text txtSyjCode = null;
	private PosTable tabBankCard = null;
	private Shell shell = null;

	public BankLogQueryForm()
	{
		open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();

		// 独立第三方银联应用，通过执行银联查询窗口,POS系统释放打印机,然后再操作银联应用
    	boolean doClosePrint = false;
    	try
    	{
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && 
				Printer.getDefault() != null && Printer.getDefault().getStatus())
			{
				Printer.getDefault().close();
				doClosePrint = true;
			}		
			
			// 
			new BankLogQueryEvent(this);
						
	        // 创建触屏操作按钮栏 
	        ControlBarForm.createMouseControlBar(this,shell);
	                
	        // 加载背景图片
	        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
	        
	        if (!shell.isDisposed())
	        {			
				shell.open();
				shell.layout();
	        }
	        
			while (!shell.isDisposed()) 
			{
				if (!display.readAndDispatch()) display.sleep();
			}
			
	        // 释放背景图片
	        ConfigClass.disposeBackgroundImage(bkimg);
    	}
    	finally
    	{
			// 银联接口执行完重新连接打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && 
				Printer.getDefault() != null && !Printer.getDefault().getStatus() && doClosePrint)
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
    	}
	}

	protected void createContents() {
		shell = new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		shell.setSize(800, 510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("金卡工程交易日志"));

		tabBankCard = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabBankCard.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabBankCard.setLinesVisible(true);
		tabBankCard.setHeaderVisible(true);
		tabBankCard.setBounds(10, 57, 774, 157);

		final TableColumn newColumnTableColumn = new TableColumn(tabBankCard, SWT.NONE);
		newColumnTableColumn.setWidth(91);
		newColumnTableColumn.setText(Language.apply("序号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabBankCard, SWT.CENTER);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText(Language.apply("是否成功"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabBankCard, SWT.NONE);
		newColumnTableColumn_2.setWidth(265);
		newColumnTableColumn_2.setText(Language.apply("交易时间"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabBankCard, SWT.NONE);
		newColumnTableColumn_3.setWidth(128);
		newColumnTableColumn_3.setText(Language.apply("交易类型"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabBankCard, SWT.RIGHT);
		newColumnTableColumn_4.setWidth(152);
		newColumnTableColumn_4.setText(Language.apply("交易金额"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 215, 774, 252);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("收银小票"));
		label.setBounds(10, 20, 80, 20);

		txtSyjCode = new Text(group, SWT.BORDER);
		txtSyjCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtSyjCode.setBounds(100, 15, 299, 27);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("收银员号"));
		label_1.setBounds(410, 20, 80, 20);

		txtSyyCode = new Text(group, SWT.BORDER);
		txtSyyCode.setBounds(495, 15, 266, 27);
		txtSyyCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("交易类型"));
		label_2.setBounds(10, 60, 80, 20);

		txtType = new Text(group, SWT.BORDER);
		txtType.setBounds(100, 55, 299, 27);
		txtType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setBounds(410, 140, 80, 20);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("交易卡号"));

		txtCardCode = new Text(group, SWT.BORDER);
		txtCardCode.setBounds(495, 135, 266, 27);
		txtCardCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_1 = new Label(group, SWT.NONE);
		label_3_1.setBounds(410, 60, 80, 20);
		label_3_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1.setText(Language.apply("交易金额"));

		txtMoney = new Text(group, SWT.BORDER);
		txtMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMoney.setBounds(495, 55, 267, 27);

		final Label label_3_2_1 = new Label(group, SWT.NONE);
		label_3_2_1.setBounds(10, 140, 80, 20);
		label_3_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1.setText(Language.apply("流 水 号"));

		txtTrace = new Text(group, SWT.BORDER);
		txtTrace.setBounds(100, 135, 299, 27);
		txtTrace.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_1_1 = new Label(group, SWT.NONE);
		label_3_1_1.setBounds(410, 180, 80, 20);
		label_3_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1.setText(Language.apply("发卡银行"));

		txtBank = new Text(group, SWT.BORDER);
		txtBank.setBounds(495, 175, 266, 27);
		txtBank.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_2_1_1_1 = new Label(group, SWT.NONE);
		label_3_2_1_1_1.setBounds(410, 100, 80, 20);
		label_3_2_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1_1_1.setText(Language.apply("原交易日"));

		txtLodTime = new Text(group, SWT.BORDER);
		txtLodTime.setBounds(495, 95, 266, 27);
		txtLodTime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_2_1_2 = new Label(group, SWT.NONE);
		label_3_2_1_2.setBounds(10, 100, 80, 20);
		label_3_2_1_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1_2.setText(Language.apply("原流水号"));
		
		txtOldTrace = new Text(group, SWT.BORDER);
		txtOldTrace.setBounds(100, 95, 299, 27);
		txtOldTrace.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_2_1_1_3 = new Label(group, SWT.NONE);
		label_3_2_1_1_3.setBounds(10, 180, 80, 20);
		label_3_2_1_1_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1_1_3.setText(Language.apply("返 回 码"));

		txtReturnCode = new Text(group, SWT.BORDER);
		txtReturnCode.setBounds(100, 175, 299, 27);
		txtReturnCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_3_2 = new Label(group, SWT.NONE);
		label_3_2.setBounds(10, 220, 80, 20);
		label_3_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2.setText(Language.apply("返回信息"));

		txtReturnMsg = new Text(group, SWT.BORDER);
		txtReturnMsg.setBounds(100, 215, 661, 27);
		txtReturnMsg.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(10, -2, 774, 53);

		final Label label_4 = new Label(group_1, SWT.NONE);
		label_4.setBounds(10, 20, 146, 20);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setText(Language.apply("请输入查询日期"));

		txtDate = new Text(group_1, SWT.BORDER);
		txtDate.setTextLimit(8);
		txtDate.setBounds(160, 17, 107, 27);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_1_1 = new Label(group_1, SWT.NONE);
		label_1_1.setBounds(272, 20, 159, 20);
		label_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_1.setText(Language.apply("格式为:YYYYMMDD"));

		final Label label_2_1 = new Label(group_1, SWT.NONE);
		label_2_1.setBounds(442, 20, 44, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("类型"));

		cmbDjlb = new Combo(group_1, SWT.READ_ONLY);
		cmbDjlb.setItems(new String[] {Language.apply("全部日志"), Language.apply("成功日志"), Language.apply("失败日志")});
		cmbDjlb.setBounds(490, 17, 107, 28);
		cmbDjlb.select(0);
		cmbDjlb.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2_1_1 = new Label(group_1, SWT.NONE);
		label_2_1_1.setBounds(605, 20, 159, 20);
		label_2_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setText(Language.apply("[付款键切换输入]"));
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	
	public Combo getCmbDjlb() 
	{
		return cmbDjlb;
	}
	
	public Text getTxtReturnCode()
	{
		return txtReturnCode;
	}
	
	public Text getTxtOldTrace()
	{
		return txtOldTrace;
	}
	
	
	public Text getTxtLodTime()
	{
		return txtLodTime;
	}
	
	
	public Text getTxtBank()
	{
		return txtBank;
	}
	
	public Text getTxtTrace()
	{
		return txtTrace;
	}
	
	
	public Text getTxtMoney()
	{
		return txtMoney;
	}
	
	public Text getTxtCardCode()
	{
		return txtCardCode;
	}
	
	public Text getTxtType()
	{
		return txtType;
	}
	
	public Text getTxtSyyCode()
	{
		return txtSyyCode;
	}
	
	public Text getTxtSyjCode()
	{
		return txtSyjCode;
	}
	
	public PosTable getTabBankCard()
	{
		return tabBankCard;
	}
	
	public Text getTxtReturnMsg()
	{
		return txtReturnMsg;
	}
	
	public Shell getShell()
	{
		return shell;
	}

}
