package custom.localize.Smtj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Smtj_DisplaySaleTicketForm
{	
	private StyledText txtKhMoney;
	private StyledText txtSpoilageMoney = null;
	private StyledText txtFactInceptMoney = null;
	private StyledText txtAgioMoney = null;
	private StyledText txtGiveChangeMoney = null;
	private StyledText txtShouldInceptMoney = null;
	private StyledText txtGrantCardCode = null;
	private StyledText txtMemberCardCode = null;
	private Table tabTicketDeatilInfo = null;
	private Table tabPay = null;
	private StyledText txtSaleType = null;
	private StyledText txtSyy = null;
	private StyledText txtSaleTime = null;
	private Text txtTicketCode = null;
	private Label lblNet = null;
	private Group group = null;
	private Shell shell = null;
	public int type = 0;
	
	public Smtj_DisplaySaleTicketForm(int type)
	{
		this.type = type;
		this.open(type);
	}
	
	public Smtj_DisplaySaleTicketForm(String date,String code)
	{
		this.open(date,code);
	}
	
	private void open(int type) 
	{
		final Display display = Display.getDefault();
		createContents();

    	new Smtj_DisplaySaleTicketEvent(this,null,null,false,type);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {	    	
			shell.open();
			
			txtTicketCode.setFocus();
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}
	
	private void open(String date,String code)
	{
		final Display display = Display.getDefault();
		createContents();
		
    	new Smtj_DisplaySaleTicketEvent(this,date,code,true,StatusType.MN_XSCX);
    	 
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {	    	
			shell.open();
			
			txtTicketCode.setFocus();
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}

	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		shell.setLayout(new FormLayout());
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800, 510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText("显示小票信息");

		group = new Group(shell, SWT.NONE);
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 59);
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(0, 782);
		formData.left = new FormAttachment(0, 10);
		group.setLayoutData(formData);
		group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		group.setText("收银机:");

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		label.setBounds(10, 25, 60, 23);
		if (type ==  StatusType.MN_BACKSALE) 		label.setText("退货号");
		else 		label.setText("小票号");

		txtTicketCode = new Text(group, SWT.BORDER);
		txtTicketCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtTicketCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtTicketCode.setBounds(75, 25, 87, 23);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText("交易时间");
		label_1.setBounds(170, 25, 80, 23);

		txtSaleTime = new StyledText(group, SWT.READ_ONLY | SWT.BORDER);
		txtSaleTime.setEnabled(false);
		txtSaleTime.setEditable(false);
		txtSaleTime.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtSaleTime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtSaleTime.setBounds(255, 25, 167, 23);

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText("收银员");
		label_2.setBounds(430, 25, 60, 23);

		txtSyy = new StyledText(group, SWT.READ_ONLY | SWT.BORDER);
		txtSyy.setEnabled(false);
		txtSyy.setEditable(false);
		txtSyy.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtSyy.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtSyy.setBounds(495, 25, 87, 23);

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText("交易类型");
		label_3.setBounds(590, 25, 80, 23);

		txtSaleType = new StyledText(group, SWT.READ_ONLY | SWT.BORDER);
		txtSaleType.setEnabled(false);
		txtSaleType.setEditable(false);
		txtSaleType.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtSaleType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtSaleType.setBounds(675, 25, 87, 23);
		
		tabPay = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		final FormData formData_1 = new FormData();
		tabPay.setLayoutData(formData_1);
		tabPay.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabPay.setLinesVisible(true);
		tabPay.setHeaderVisible(true);
		//tabPay.setVisible(false);

		final TableColumn newColumnTableColumn_7 = new TableColumn(tabPay, SWT.NONE);
		newColumnTableColumn_7.setWidth(224);
		newColumnTableColumn_7.setText("付款名称");

		final TableColumn newColumnTableColumn_8 = new TableColumn(tabPay, SWT.NONE);
		newColumnTableColumn_8.setWidth(299);
		newColumnTableColumn_8.setText("付款帐号");

		final TableColumn newColumnTableColumn_9 = new TableColumn(tabPay, SWT.RIGHT);
		newColumnTableColumn_9.setWidth(229);
		newColumnTableColumn_9.setText("付款金额");

		tabTicketDeatilInfo = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		formData_1.top = new FormAttachment(tabTicketDeatilInfo, 5, SWT.BOTTOM);
		formData_1.right = new FormAttachment(tabTicketDeatilInfo, 772, SWT.LEFT);
		formData_1.left = new FormAttachment(tabTicketDeatilInfo, 0, SWT.LEFT);
		final FormData formData_2 = new FormData();
		formData_2.bottom = new FormAttachment(0, 200);
		formData_2.top = new FormAttachment(0, 65);
		formData_2.right = new FormAttachment(0, 782);
		formData_2.left = new FormAttachment(0, 10);
		tabTicketDeatilInfo.setLayoutData(formData_2);
		tabTicketDeatilInfo.setVisible(true);
		tabTicketDeatilInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabTicketDeatilInfo.setLinesVisible(true);
		tabTicketDeatilInfo.setHeaderVisible(true);
		
		final TableColumn newColumnTableColumn = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn.setWidth(91);
		newColumnTableColumn.setText("营业员");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_1.setWidth(133);
		newColumnTableColumn_1.setText("商品编码");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_2.setWidth(152);
		newColumnTableColumn_2.setText("商品名称");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_3.setWidth(113);
		newColumnTableColumn_3.setText("单价");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabTicketDeatilInfo, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(58);
		newColumnTableColumn_4.setText("数量");

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_5.setWidth(89);
		newColumnTableColumn_5.setText("折扣额");

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabTicketDeatilInfo, SWT.RIGHT);
		newColumnTableColumn_6.setWidth(115);
		newColumnTableColumn_6.setText("应收金额");

		Group group_1;
		group_1 = new Group(shell, SWT.NONE);
		formData_1.bottom = new FormAttachment(group_1, -5, SWT.TOP);
		final FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(0, 383);
		formData_3.top = new FormAttachment(0, 340);
		formData_3.right = new FormAttachment(0, 782);
		formData_3.left = new FormAttachment(0, 10);
		group_1.setLayoutData(formData_3);

		final Label label_4 = new Label(group_1, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setText("会员卡号");
		label_4.setBounds(5, 15, 80, 18);

		txtMemberCardCode = new StyledText(group_1, SWT.READ_ONLY | SWT.BORDER);
		txtMemberCardCode.setEnabled(false);
		txtMemberCardCode.setEditable(false);
		txtMemberCardCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtMemberCardCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMemberCardCode.setBounds(90, 10, 188, 28);

		final Label label_5 = new Label(group_1, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_5.setText("授权卡号");
		label_5.setBounds(285, 15, 80, 18);

		txtGrantCardCode = new StyledText(group_1, SWT.READ_ONLY | SWT.BORDER);
		txtGrantCardCode.setEnabled(false);
		txtGrantCardCode.setEditable(false);
		txtGrantCardCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtGrantCardCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGrantCardCode.setBounds(370, 10, 188, 28);

		lblNet = new Label(group_1, SWT.RIGHT);
		lblNet.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblNet.setBounds(552, 14, 210, 19);

		final Group group_2 = new Group(shell, SWT.NONE);
		final FormData formData_4 = new FormData();
		formData_4.bottom = new FormAttachment(0, 466);
		formData_4.top = new FormAttachment(0, 379);
		formData_4.right = new FormAttachment(0, 782);
		formData_4.left = new FormAttachment(0, 10);
		group_2.setLayoutData(formData_4);
		group_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_6 = new Label(group_2, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6.setText("应收金额");
		label_6.setBounds(5, 20, 80, 19);

		txtShouldInceptMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtShouldInceptMoney.setEnabled(false);
		txtShouldInceptMoney.setEditable(false);
		txtShouldInceptMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtShouldInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtShouldInceptMoney.setBounds(90, 15, 189, 28);

		final Label label_7 = new Label(group_2, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_7.setText("找零金额");
		label_7.setBounds(284, 55, 80, 19);

		final Label label_8 = new Label(group_2, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_8.setText("折扣金额");
		label_8.setBounds(284, 20, 80, 19);

		txtAgioMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtAgioMoney.setEnabled(false);
		txtAgioMoney.setEditable(false);
		txtAgioMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtAgioMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAgioMoney.setBounds(370, 15, 189, 28);

		final Label label_9 = new Label(group_2, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_9.setText("实收金额");
		label_9.setBounds(5, 55, 80, 19);

		txtFactInceptMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtFactInceptMoney.setEnabled(false);
		txtFactInceptMoney.setEditable(false);
		txtFactInceptMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtFactInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtFactInceptMoney.setBounds(90, 50, 189, 28);

		final Label label_10 = new Label(group_2, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_10.setText("损溢金额");
		label_10.setBounds(565, 55, 78, 19);

		txtSpoilageMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtSpoilageMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtSpoilageMoney.setEnabled(false);
		txtSpoilageMoney.setEditable(false);
		txtSpoilageMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtSpoilageMoney.setBounds(649, 52, 111, 28);

		txtGiveChangeMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtGiveChangeMoney.setBounds(370, 52,189, 28);
		txtGiveChangeMoney.setEnabled(false);
		txtGiveChangeMoney.setEditable(false);
		txtGiveChangeMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtGiveChangeMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_11 = new Label(group_2, SWT.NONE);
		label_11.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		label_11.setText("扣回金额");
		label_11.setBounds(565, 20, 78, 19);

		txtKhMoney = new StyledText(group_2, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtKhMoney.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtKhMoney.setForeground(SWTResourceManager.getColor(255, 0, 0));
		txtKhMoney.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		txtKhMoney.setEnabled(false);
		txtKhMoney.setEditable(false);
		txtKhMoney.setBounds(649, 17, 111, 25);
		
	}
	
	public FormData getforData ()
	{
		final FormData formData_2 = new FormData();
		formData_2.bottom = new FormAttachment(0, 339);
		formData_2.top = new FormAttachment(0, 65);
		formData_2.right = new FormAttachment(0, 782);
		formData_2.left = new FormAttachment(0, 10);
		return formData_2;
	}
	public StyledText getTxtSpoilageMoney()
	{
		return txtSpoilageMoney;
	}
	
	public StyledText getTxtFactInceptMoney()
	{
		return txtFactInceptMoney;
	}
	
	public StyledText getTxtAgioMoney()
	{
		return txtAgioMoney;
	}
	
	public StyledText getTxtGiveChangeMoney()
	{
		return txtGiveChangeMoney;
	}
	
	public StyledText getTxtShouldInceptMoney()
	{
		return txtShouldInceptMoney;
	}
	
	public StyledText getTxtGrantCardCode()
	{
		return txtGrantCardCode;
	}
	
	public StyledText getTxtMemberCardCode()
	{
		return txtMemberCardCode;
	}
	
	public Table getTabTicketDeatilInfo()
	{
		return tabTicketDeatilInfo;
	}
	
	public Table getTabPay()
	{
		return tabPay;
	}
	
	public StyledText getTxtSaleType()
	{
		return txtSaleType;
	}
	
	public StyledText getTxtSyy()
	{
		return txtSyy;
	}
	
	public StyledText getTxtSaleTime()
	{
		return txtSaleTime;
	}
	
	public Text getTxtTicketCode()
	{
		return txtTicketCode;
	}
	
	public Label getLblNet()
	{
		return lblNet;
	}
	
	public Group getGroup()
	{
		return  group;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public StyledText getKhje()
	{
		return txtKhMoney;
	}

}
