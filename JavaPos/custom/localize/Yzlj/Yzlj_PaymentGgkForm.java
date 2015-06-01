package custom.localize.Yzlj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class Yzlj_PaymentGgkForm
{
	public Text PwdTxt;
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    public Label Acount = null;
    public Label YeTips = null;
    public Label Money = null;
    public StyledText status = null;
    public Text AccountTxt = null;
    public Text Yetxt = null;
    public Text Moneytxt = null;
    public Label payName;
    public Yzlj_PaymentGgk pay = null;
    
    public void open(Yzlj_PaymentGgk pay,SaleBS sale)
    {
        Display display = Display.getDefault();
        
        this.pay = pay;
        
        createSShell();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
        
        Yzlj_PaymentGgkEvent ev = new Yzlj_PaymentGgkEvent(this,pay,sale);
                            
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())
        {       
        	sShell.open();
        	sShell.setActive();
        }
        
        ev.afterFormOpenDoEvent();
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
    }

    /**
     * This method initializes sShell
     */
    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style_linux);
        sShell.setLayout(new FormLayout());
        sShell.setText("Shell");
        sShell.setSize(new org.eclipse.swt.graphics.Point(436, 314));

        Acount = new Label(sShell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 60);
        formData.top = new FormAttachment(0, 40);
        formData.right = new FormAttachment(0, 119);
        formData.left = new FormAttachment(0, 23);
        Acount.setLayoutData(formData);
        Acount.setText("请 刷 卡");
        Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        YeTips = new Label(sShell, SWT.NONE);
        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 90);
        formData_1.top = new FormAttachment(0, 70);
        formData_1.right = new FormAttachment(0, 119);
        formData_1.left = new FormAttachment(0, 23);
        YeTips.setLayoutData(formData_1);
        YeTips.setText("账户余额");
        YeTips.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Money = new Label(sShell, SWT.NONE);
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 120);
        formData_2.top = new FormAttachment(0, 100);
        formData_2.right = new FormAttachment(0, 119);
        formData_2.left = new FormAttachment(0, 23);
        Money.setLayoutData(formData_2);
        Money.setText("付款金额");
        Money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        status = new StyledText(sShell, SWT.BORDER);
        final FormData formData_3 = new FormData();
        formData_3.bottom = new FormAttachment(0, 300);
        formData_3.top = new FormAttachment(0, 136);
        formData_3.right = new FormAttachment(0, 411);
        formData_3.left = new FormAttachment(0, 23);
        status.setLayoutData(formData_3);
        status.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        status.setEditable(false);
        status.setEnabled(false);
        
        AccountTxt = new Text(sShell, SWT.BORDER);
        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(0, 60);
        formData_4.top = new FormAttachment(0, 36);
        formData_4.right = new FormAttachment(0, 411);
        formData_4.left = new FormAttachment(0, 122);
        AccountTxt.setLayoutData(formData_4);
        AccountTxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Yetxt = new Text(sShell, SWT.BORDER);
        final FormData formData_5 = new FormData();
        formData_5.bottom = new FormAttachment(0, 92);
        formData_5.top = new FormAttachment(0, 68);
        formData_5.right = new FormAttachment(0, 411);
        formData_5.left = new FormAttachment(0, 122);
        Yetxt.setLayoutData(formData_5);
        Yetxt.setEditable(false);
        Yetxt.setBackground(SWTResourceManager.getColor(255, 255, 255));
        Yetxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Yetxt.setTextLimit(20);
        Moneytxt = new Text(sShell, SWT.BORDER);
        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(0, 122);
        formData_6.top = new FormAttachment(0, 98);
        formData_6.right = new FormAttachment(0, 411);
        formData_6.left = new FormAttachment(0, 122);
        Moneytxt.setLayoutData(formData_6);
        Moneytxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Moneytxt.setTextLimit(11);

        final Label label = new Label(sShell, SWT.NONE);
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 30);
        formData_7.top = new FormAttachment(0, 10);
        formData_7.right = new FormAttachment(0, 119);
        formData_7.left = new FormAttachment(0, 23);
        label.setLayoutData(formData_7);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText("付款名称");

        payName = new Label(sShell, SWT.NONE);
        final FormData formData_8 = new FormData();
        formData_8.bottom = new FormAttachment(0, 32);
        formData_8.top = new FormAttachment(0, 8);
        formData_8.right = new FormAttachment(0, 411);
        formData_8.left = new FormAttachment(0, 122);
        payName.setLayoutData(formData_8);
        payName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        if (pay.passwdMode())
        {
        	PwdTxt = new Text(sShell,SWT.BORDER);
        }
        else
        {
        	PwdTxt = new Text(sShell, SWT.PASSWORD | SWT.BORDER);
        }
        
        final FormData formData_9 = new FormData();
        formData_9.bottom = new FormAttachment(0, 104);
        formData_9.top = new FormAttachment(0, 80);
        formData_9.right = new FormAttachment(0, 429);
        formData_9.left = new FormAttachment(0, 140);
        PwdTxt.setLayoutData(formData_9);
        PwdTxt.setTextLimit(20);
        PwdTxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        PwdTxt.setBackground(SWTResourceManager.getColor(255, 255, 255));
    
        if (ConfigClass.MouseMode)
        {
        	ControlBarForm ctrlform = new ControlBarForm(sShell,SWT.NONE,GlobalVar.ConfigPath + "\\PaymentMzkForm.ini");
        	ctrlform.setControlBarForm();
        }
    }
}
