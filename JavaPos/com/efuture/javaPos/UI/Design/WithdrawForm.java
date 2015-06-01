package com.efuture.javaPos.UI.Design;




import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.WithdrawEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class WithdrawForm 
{
	private StyledText txtCode;
	private Combo combopostime;
	private Text txtQueryDate;
	private Label lblCountMoney;
	private Label lblCountAmount;
	private Table tabInputMoney;
	private Text txtTime = null;
	private PosTable tabBeFore = null;
	protected Shell shell = null;

	public WithdrawForm()
	{
		this.open();
	}

	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
		WithdrawEvent we = new WithdrawEvent(this);
   
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        // 居中
        shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);

        if (!shell.isDisposed())
        { 			
			shell.open();
			
			if (GlobalInfo.sysPara.isinputjkdate != 'Y')
			{
				we.findLocation();
			}
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}


	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		shell.setSize(800, 537);
		shell.setText(Language.apply("缴款窗口"));
		
		tabBeFore = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabBeFore.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabBeFore.setLinesVisible(true);
		tabBeFore.setHeaderVisible(true);
		tabBeFore.setBounds(10, 63, 416, 398);

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabBeFore, SWT.NONE);
		newColumnTableColumn_3.setWidth(69);
		newColumnTableColumn_3.setText(Language.apply("单号"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabBeFore, SWT.NONE);
		newColumnTableColumn_4.setWidth(96);
		newColumnTableColumn_4.setText(Language.apply("缴款时间"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabBeFore, SWT.NONE);
		newColumnTableColumn_5.setWidth(93);
		newColumnTableColumn_5.setText(Language.apply("收银员"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabBeFore, SWT.NONE);
        int screenwidth = Display.getDefault().getBounds().width;
        if (screenwidth >= 800)
        {
        	newColumnTableColumn_6.setAlignment(SWT.RIGHT);
        }
        else
        {
        	newColumnTableColumn_6.setAlignment(SWT.LEFT);
        }
		newColumnTableColumn_6.setWidth(141);
		newColumnTableColumn_6.setText(Language.apply("缴款金额"));

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(432, 0, 352, 57);

		final Label label_1 = new Label(group_1, SWT.NONE);
		label_1.setBounds(10, 20, 79, 20);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("交易日期"));

		txtTime = new Text(group_1, SWT.BORDER);
		txtTime.setBounds(95, 17, 111, 28);
		txtTime.setTextLimit(8);
		txtTime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label = new Label(group_1, SWT.NONE);
		label.setBounds(210, 20, 40, 20);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("班次"));

		combopostime = new Combo(group_1, SWT.READ_ONLY);
		combopostime.setBounds(255, 17, 86, 28);
		combopostime.setText(Language.apply("请选择班次"));
		combopostime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		txtCode = new StyledText(group_1, SWT.BORDER);
		txtCode.setBounds(120, 25, 60, 12);

		tabInputMoney = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
		tabInputMoney.setBounds(432, 64, 352, 345);
		tabInputMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabInputMoney.setLinesVisible(true);
		tabInputMoney.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(tabInputMoney, SWT.NONE);
		newColumnTableColumn.setWidth(115);
		newColumnTableColumn.setText(Language.apply("缴款名称"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabInputMoney, SWT.RIGHT);
		newColumnTableColumn_1.setWidth(76);
		newColumnTableColumn_1.setText(Language.apply("张数"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabInputMoney, SWT.NONE);
		newColumnTableColumn_2.setAlignment(SWT.RIGHT);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText(Language.apply("缴款金额"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(432, 408, 352, 53);

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setBounds(10, 20, 94, 20);
		label_2.setText(Language.apply("合     计"));

		lblCountAmount = new Label(group, SWT.RIGHT);
		lblCountAmount.setForeground(SWTResourceManager.getColor(0, 0, 255));
		lblCountAmount.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
		lblCountAmount.setBounds(110, 17, 75, 28);
		lblCountAmount.setText("0");

		lblCountMoney = new Label(group, SWT.RIGHT);
		lblCountMoney.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblCountMoney.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
		lblCountMoney.setBounds(191, 17, 147, 20);
		lblCountMoney.setText("0.00");

		final Group group_2 = new Group(shell, SWT.NONE);
		group_2.setBounds(10, 0, 416, 57);

		final Label label_1_1 = new Label(group_2, SWT.NONE);
		label_1_1.setBounds(10, 20, 80, 20);
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_1.setText(Language.apply("查询日期"));

		txtQueryDate = new Text(group_2, SWT.BORDER);
		txtQueryDate.setBounds(100, 15, 111, 28);
		txtQueryDate.setTextLimit(8);
		txtQueryDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_1_2 = new Label(group_2, SWT.NONE);
		label_1_2.setBounds(224, 20, 150, 20);
		label_1_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_2.setText(Language.apply("格式为:YYYYMMDD"));

		final Label lbl_Status = new Label(shell, SWT.NONE);
		lbl_Status.setText(Language.apply("[确认键]保存打印或重传   [打印键]重印   [付款键]切换焦点"));
		if (GlobalInfo.ModuleType.indexOf("ZM")==0)
		{
			lbl_Status.setText(Language.apply("[Home键]保存打印或重传   [小票重打键]重印   [付款键]切换焦点"));
		}
		
		lbl_Status.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		lbl_Status.setBounds(10, 478, 774, 27);
		
	}
	
	
	public PosTable getTabBeFore()
	{
		return tabBeFore;
	}
	
	
	public Text getTxtTime()
	{
		return txtTime;
	}
	
	public StyledText gettxtCode()
	{
		return txtCode;
	}
	
	public Text getTxtQueryDate()
	{
		return txtQueryDate;
	}
	
	public Label getLblCountAmount()
	{
		return lblCountAmount;
	}
	
	public Label getLblCountMoney()
	{
		return lblCountMoney;
	}
	
	public Table getTabInputMoney()
	{
		return tabInputMoney;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Combo getCombopostime()
	{
		return combopostime;
	}
	
}
