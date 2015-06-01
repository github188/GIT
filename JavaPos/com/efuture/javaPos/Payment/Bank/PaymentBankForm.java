package com.efuture.javaPos.Payment.Bank;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PaymentBankForm implements Interface_PaymentBankForm
{
	private Text txtAuthno;
	private Label lblAuthno;
	private Label lblPayName 		= null;					//付款名称
	private Label Acount 		= null;
	private Combo cmbType 		= null;					//交易类型
	private Label lblSeqno 		= null;
	private Text txtSeqno 		= null;					//原流水号
	private Label lblDate 		= null;
	private Text txtDate 		= null;					//原交易日
	private Label lblAccount 			= null;
	private Text txtAccount 		= null;					//刷卡文本
	private Label lblMoney 			= null;
	private Text txtMoney 		= null;					//交易金额
	private StyledText txtStatus	= null;					//提示信息
    private Shell sShell 		= null;
    private boolean Done = false;    
        
    /**
     * @wbp.parser.entryPoint
     */
    public boolean open(PaymentBank pay,int type)
    {
    	// 检查注册码是否为允许使用银联模块
    	if (!CustomLocalize.getDefault().checkAllowUseBank()) return false;
    	
        Display display = Display.getDefault();

        createSShell();

        PaymentBankEvent e = new PaymentBankEvent(this,pay,type);
        e.initObject();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())
        {
	        sShell.open();
	        sShell.setActive();
        }
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        e = null;
        return Done;
    }
        
    public boolean open(int type)
    {
    	// 检查注册码是否为允许使用银联模块
    	if (!CustomLocalize.getDefault().checkAllowUseBank()) return false;
    	
        Display display = Display.getDefault();

        createSShell();

        PaymentBankEvent e = new PaymentBankEvent(this,type);
        e.initObject();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())     
        {
	        sShell.open();
	        sShell.setActive();
        }
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        e = null;
        return Done;
    }
    
    public boolean open(String paycode, int type)
    {
    	// 检查注册码是否为允许使用银联模块
    	if (!CustomLocalize.getDefault().checkAllowUseBank()) return false;
    	
        Display display = Display.getDefault();

        createSShell();

        PaymentBankEvent e = new PaymentBankEvent(this,paycode,type);
        e.initObject();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        sShell.open();
        sShell.setActive();
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        e = null;
        return Done;
    }
    
    public void setDone(boolean b)
    {
    	Done = b;
    }
    
    /**
     * This method initializes sShell
     */
    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style_linux);
        sShell.setLayout(new FormLayout());
        sShell.setText("Shell");
        sShell.setSize(new org.eclipse.swt.graphics.Point(438, 335));

        Acount = new Label(sShell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 60);
        formData.top = new FormAttachment(0, 40);
        formData.right = new FormAttachment(0, 119);
        formData.left = new FormAttachment(0, 23);
        Acount.setLayoutData(formData);
        Acount.setText("交易类型");
        Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lblAccount = new Label(sShell, SWT.NONE);
        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 192);
        formData_1.top = new FormAttachment(0, 172);
        lblAccount.setLayoutData(formData_1);
        lblAccount.setText("请 刷 卡");
        lblAccount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lblMoney = new Label(sShell, SWT.NONE);
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 228);
        formData_2.top = new FormAttachment(0, 208);
        lblMoney.setLayoutData(formData_2);
        lblMoney.setText("交易金额");
        lblMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtStatus = new StyledText(sShell, SWT.BORDER);
        final FormData formData_3 = new FormData();
        formData_3.top = new FormAttachment(0, 240);
        formData_3.bottom = new FormAttachment(0, 320);
        formData_3.left = new FormAttachment(lblMoney, 0, SWT.LEFT);
        txtStatus.setLayoutData(formData_3);
        txtStatus.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        txtStatus.setEditable(false);
        txtStatus.setEnabled(false);
        
        final Label label = new Label(sShell, SWT.NONE);
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 30);
        formData_7.top = new FormAttachment(0, 10);
        formData_7.right = new FormAttachment(0, 119);
        formData_7.left = new FormAttachment(0, 23);
        label.setLayoutData(formData_7);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText("付款名称");

        lblPayName = new Label(sShell, SWT.NONE);
        final FormData formData_8 = new FormData();
        formData_8.bottom = new FormAttachment(0, 32);
        formData_8.top = new FormAttachment(0, 8);
        formData_8.right = new FormAttachment(0, 411);
        formData_8.left = new FormAttachment(0, 122);
        lblPayName.setLayoutData(formData_8);
        lblPayName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        cmbType = new Combo(sShell, SWT.READ_ONLY);
        cmbType.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        cmbType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        cmbType.select(0);
        //cmbType.setItems(new String[] {"消费", "消费撤销", "隔日退货", "交易签到", "交易结账", "余额查询", "签购单重打"});
        final FormData formData_5 = new FormData();
        formData_5.right = new FormAttachment(0, 411);
        formData_5.left = new FormAttachment(0, 122);
        formData_5.bottom = new FormAttachment(lblPayName, 33, SWT.BOTTOM);
        formData_5.top = new FormAttachment(lblPayName, 5, SWT.BOTTOM);
        cmbType.setLayoutData(formData_5);

        lblSeqno = new Label(sShell, SWT.NONE);
        final FormData formData_1_1 = new FormData();
        formData_1_1.right = new FormAttachment(0, 119);
        formData_1_1.left = new FormAttachment(0, 23);
        formData_1_1.bottom = new FormAttachment(cmbType, 27, SWT.BOTTOM);
        formData_1_1.top = new FormAttachment(cmbType, 7, SWT.BOTTOM);
        lblSeqno.setLayoutData(formData_1_1);
        lblSeqno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lblSeqno.setText("原流水号");

        txtSeqno = new Text(sShell, SWT.BORDER);
        txtSeqno.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        final FormData formData_4_1 = new FormData();
        formData_4_1.top = new FormAttachment(0, 70);
        formData_4_1.right = new FormAttachment(0, 411);
        formData_4_1.left = new FormAttachment(0, 122);
        formData_4_1.bottom = new FormAttachment(cmbType, 32, SWT.BOTTOM);
        txtSeqno.setLayoutData(formData_4_1);
        txtSeqno.setTextLimit(20);
        txtSeqno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lblDate = new Label(sShell, SWT.NONE);
        final FormData formData_1_1_1 = new FormData();
        formData_1_1_1.top = new FormAttachment(0, 138);
        formData_1_1_1.bottom = new FormAttachment(0, 158);
        lblDate.setLayoutData(formData_1_1_1);
        lblDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lblDate.setText("原交易日");

        txtDate = new Text(sShell, SWT.BORDER);
        txtDate.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        final FormData formData_4_1_1 = new FormData();
        formData_4_1_1.bottom = new FormAttachment(0, 162);
        formData_4_1_1.right = new FormAttachment(0, 411);
        formData_4_1_1.left = new FormAttachment(0, 122);
        txtDate.setLayoutData(formData_4_1_1);
        txtDate.setTextLimit(20);
        txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        txtAccount = new Text(sShell, SWT.BORDER);
        txtAccount.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        final FormData formData_4_1_1_1 = new FormData();
        formData_4_1_1_1.right = new FormAttachment(0, 411);
        formData_4_1_1_1.left = new FormAttachment(0, 122);
        formData_4_1_1_1.bottom = new FormAttachment(0, 197);
        formData_4_1_1_1.top = new FormAttachment(0, 170);
        txtAccount.setLayoutData(formData_4_1_1_1);
        txtAccount.setTextLimit(80);
        txtAccount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        txtMoney = new Text(sShell, SWT.BORDER);
        formData_3.right = new FormAttachment(txtMoney, 0, SWT.RIGHT);
        final FormData formData_4_1_1_1_1 = new FormData();
        formData_4_1_1_1_1.right = new FormAttachment(0, 411);
        formData_4_1_1_1_1.left = new FormAttachment(0, 122);
        formData_4_1_1_1_1.bottom = new FormAttachment(0, 232);
        formData_4_1_1_1_1.top = new FormAttachment(0, 205);
        txtMoney.setLayoutData(formData_4_1_1_1_1);
        txtMoney.setTextLimit(20);
        txtMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        lblAuthno = new Label(sShell, SWT.NONE);
        formData_1_1_1.right = new FormAttachment(lblAuthno, 96, SWT.LEFT);
        formData_1_1_1.left = new FormAttachment(lblAuthno, 0, SWT.LEFT);
        formData_2.right = new FormAttachment(lblAuthno, 96, SWT.LEFT);
        formData_2.left = new FormAttachment(lblAuthno, 0, SWT.LEFT);
        formData_1.right = new FormAttachment(lblAuthno, 96, SWT.LEFT);
        formData_1.left = new FormAttachment(lblAuthno, 0, SWT.LEFT);
        final FormData formData_1_1_2 = new FormData();
        formData_1_1_2.top = new FormAttachment(0, 105);
        formData_1_1_2.bottom = new FormAttachment(0, 125);
        formData_1_1_2.right = new FormAttachment(lblSeqno, 96, SWT.LEFT);
        formData_1_1_2.left = new FormAttachment(lblSeqno, 0, SWT.LEFT);
        lblAuthno.setLayoutData(formData_1_1_2);
        lblAuthno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lblAuthno.setText("原授权号");

        txtAuthno = new Text(sShell, SWT.BORDER);
        formData_4_1_1.top = new FormAttachment(txtAuthno, 5, SWT.BOTTOM);
        final FormData formData_4_1_2 = new FormData();
        formData_4_1_2.right = new FormAttachment(0, 411);
        formData_4_1_2.left = new FormAttachment(0, 122);
        formData_4_1_2.top = new FormAttachment(0, 103);
        formData_4_1_2.bottom = new FormAttachment(0, 130);
        txtAuthno.setLayoutData(formData_4_1_2);
        txtAuthno.setTextLimit(20);
        txtAuthno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtAuthno.setBackground(SWTResourceManager.getColor(255, 255, 255));
    }
    
    public Label getLblSeqno()
    {
    	return lblSeqno;
    }
    
    public Label getLblAuthno()
    {
    	return lblAuthno;
    }
    
    public Label getLblDate()
    {
    	return lblDate;
    }
    
    public Label getLblAccount()
    {
    	return lblAccount;
    }
    
    public Label getLblMoney()
    {
    	return lblMoney;
    }
    
    public Label getLblPayName()
    {
    	return lblPayName;
    }
	
	public Combo getCmbType()
	{
		return cmbType;
	}
	
	public Text getTxtSeqno()
	{
		return txtSeqno;
	}
	
	public Text getTxtAuthno()
	{
		return txtAuthno;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	
	public Text getTxtAccount()
	{
		return txtAccount;
	}
	
	public Text getTxtMoney()
	{
		return txtMoney;
	}
	
	public StyledText getTxtStatus()
	{
		return txtStatus;
	}
	
    public Shell getSShell()
    {
    	return sShell;
    }
  
}
