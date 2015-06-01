package custom.localize.Bjys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;


public class Bjys_PaymentZpForm
{
	public Label Acount_1;
    public Text Datetxt;
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    public Label Acount = null;
    public Label ID = null;
    public Label Money = null;
    public StyledText status = null;
    public Text AccountTxt = null;
    public Text IDtxt = null;
    public Text Moneytxt = null;
    public Label payName;
    
    public void open(Bjys_PaymentZp pay,SaleBS sale,boolean isneedaccnt,boolean isneedidno)
    {
    	Display display = Display.getDefault();

        createSShell();

        new Bjys_PaymentZpEvent(this,pay,sale,isneedaccnt,isneedidno);
                
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
    }
    
    public void open(Bjys_PaymentZp pay,SaleBS sale)
    {
    	open(pay,sale,false,false);
    }
    

    /**
     * This method initializes sShell
     */
    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style_linux);
        sShell.setText("Shell");
        sShell.setSize(new org.eclipse.swt.graphics.Point(438, 294));

        Acount = new Label(sShell, SWT.NONE);
        Acount.setBounds(20, 40, 96, 20);
        Acount.setText("付款帐号");
        Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        ID = new Label(sShell, SWT.NONE);
        ID.setBounds(20, 100, 96, 20);
        ID.setText("证件号码");
        ID.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Money = new Label(sShell, SWT.NONE);
        Money.setBounds(20, 130, 96, 20);
        Money.setText("付款金额");
        Money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        status = new StyledText(sShell, SWT.NONE);
        status.setBounds(23, 165, 388, 117);
        status.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));

        AccountTxt = new Text(sShell, SWT.BORDER);
        AccountTxt.setBounds(119, 37, 289, 24);
        AccountTxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        AccountTxt.setTextLimit(20);
        IDtxt = new Text(sShell, SWT.BORDER);
        IDtxt.setBounds(119, 96, 289, 24);
        IDtxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        IDtxt.setTextLimit(20);
        Moneytxt = new Text(sShell, SWT.BORDER);
        Moneytxt.setBounds(119, 126, 289, 24);
        Moneytxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Moneytxt.setTextLimit(11);

        Label label;
        label = new Label(sShell, SWT.NONE);
        label.setBounds(20, 10, 96, 20);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText("付款名称");

        payName = new Label(sShell, SWT.NONE);
        payName.setBounds(119, 10, 289, 24);
        payName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        Datetxt = new Text(sShell, SWT.BORDER);
        Datetxt.setBounds(119, 66, 289, 24);
        Datetxt.setTextLimit(10);
        Datetxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        Acount_1 = new Label(sShell, SWT.NONE);
        Acount_1.setBounds(20, 70, 96, 20);
        Acount_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        Acount_1.setText("付货日期");

    }
}
