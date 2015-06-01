package custom.localize.Zmjc;

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
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class Zmjc_PaymentDetailForm
{
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    private Label Acount = null;
    private Label ID = null;
    public Label Money = null;
    public StyledText status = null;
    public Text AccountTxt = null;
    public Text IDtxt = null;
    public Text Moneytxt = null;
    public Label payName;
    
    public void open(Zmjc_PaymentDetail pay,SaleBS sale,boolean isneedaccnt,boolean isneedidno)
    {
    	Display display = Display.getDefault();

        createSShell();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
        
        new Zmjc_PaymentDetailEvent(this,pay,sale,isneedaccnt,isneedidno);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())
        {          
	        sShell.open();
	        AccountTxt.setFocus();
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
    }
    
    public void open(Zmjc_PaymentDetail pay,SaleBS sale)
    {
    	open(pay,sale,false,false);
    }
    

    /**
     * This method initializes sShell
     */
    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style_linux);
        sShell.setLayout(new FormLayout());
        sShell.setText("Shell");
        sShell.setSize(new org.eclipse.swt.graphics.Point(438, 294));

        Acount = new Label(sShell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 60);
        formData.top = new FormAttachment(0, 40);
        formData.right = new FormAttachment(0, 119);
        formData.left = new FormAttachment(0, 23);
        Acount.setLayoutData(formData);
        Acount.setText(Language.apply("付款帐号"));
        Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        ID = new Label(sShell, SWT.NONE);
        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 90);
        formData_1.top = new FormAttachment(0, 70);
        formData_1.right = new FormAttachment(0, 119);
        formData_1.left = new FormAttachment(0, 23);
        ID.setLayoutData(formData_1);
        ID.setText(Language.apply("证件号码"));
        ID.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Money = new Label(sShell, SWT.NONE);
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 120);
        formData_2.top = new FormAttachment(0, 100);
        formData_2.right = new FormAttachment(0, 119);
        formData_2.left = new FormAttachment(0, 23);
        Money.setLayoutData(formData_2);
        Money.setText(Language.apply("付款金额"));
        Money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        status = new StyledText(sShell, SWT.BORDER);
        final FormData formData_3 = new FormData();
        formData_3.bottom = new FormAttachment(0, 282);
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
        IDtxt = new Text(sShell, SWT.BORDER);
        final FormData formData_5 = new FormData();
        formData_5.bottom = new FormAttachment(0, 92);
        formData_5.top = new FormAttachment(0, 68);
        formData_5.right = new FormAttachment(0, 411);
        formData_5.left = new FormAttachment(0, 122);
        IDtxt.setLayoutData(formData_5);
        IDtxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        IDtxt.setTextLimit(20);
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
        label.setText(Language.apply("付款名称"));

        payName = new Label(sShell, SWT.NONE);
        final FormData formData_8 = new FormData();
        formData_8.bottom = new FormAttachment(0, 32);
        formData_8.top = new FormAttachment(0, 8);
        formData_8.right = new FormAttachment(0, 411);
        formData_8.left = new FormAttachment(0, 122);
        payName.setLayoutData(formData_8);
        payName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
    }
}
