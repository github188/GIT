package custom.localize.Bjys;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.SalePayDef;


public class Bjys_PaymentZpEvent
{
    private Shell shell = null;
    private Text accountID = null;
    private Text userID = null;
    private Text payment = null;
    private Label payName = null;
    private Label MoneyInfo = null;
    private SaleBS saleBS = null;
    private Bjys_PaymentZp payObj = null;
    private Text date = null;
    private boolean isNeedAccnt = false; //是否输入帐号
    private boolean isNeedIdno = false;  //是否输入证件号
    
    private String datestr = null;
    
    public Bjys_PaymentZpEvent(Bjys_PaymentZpForm form, Bjys_PaymentZp pay, SaleBS sale,boolean isneedaccnt,boolean isneedidno)
    {
        this.saleBS = sale;
        this.payObj = pay;
        accountID   = form.AccountTxt;
        userID      = form.IDtxt;
        payment     = form.Moneytxt;
        shell       = form.sShell;
        payName     = form.payName;
        MoneyInfo	= form.Money;
        date		= form.Datetxt;
        isNeedAccnt = isneedaccnt;
        isNeedIdno = isneedidno;
        
        NewKeyEvent event = new NewKeyEvent()
        {
            public void keyDown(KeyEvent e, int key)
            {
                keyPressed(e, key);
            }

            public void keyUp(KeyEvent e, int key)
            {
                keyReleased(e, key);
            }
        };

        NewKeyListener key = new NewKeyListener();
        key.event   = event;

        NewKeyListener key1 = new NewKeyListener();
        key1.event  = event;
        key1.inputMode = key.DoubleInput;
        accountID.addKeyListener(key);
        date.addKeyListener(key);
        userID.addKeyListener(key);
        payment.addKeyListener(key1);

        Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setLocation((rec.width - shell.getSize().x) / 2,
                          (rec.height - shell.getSize().y) / 2);
        

        initEvent();
    }

    public void initEvent()
    {
    	if (SellType.ISBACK(saleBS.saletype))
    		MoneyInfo.setText("退款金额");
    	else
    		MoneyInfo.setText("付款金额");
    	
    	
        payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);
        
        
        date.setText(new ManipulateDateTime().skipDate(ManipulateDateTime.getCurrentDate(), 3));
        
        double needPay = saleBS.calcPayBalance();
        payment.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
        
        accountID.setFocus();
        accountID.selectAll();
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:
            	enterInput(e);
                break;

            case GlobalVar.Exit:
                shell.close();
                shell.dispose();
                break;
        }
    }
    
    void enterInput(KeyEvent e)
    {
    	Text txt = (Text)e.widget;
        if (txt.equals(accountID))
        {
        	if (isNeedAccnt && accountID.getText().trim().equals(""))
        	{
        		new MessageBox("请输入付款帐号!");
        		return;
        	}
        	
        	// 检查重复输入
            if (GlobalInfo.sysPara.payover == 'Y')
            {
            	int i = saleBS.existPayment(payObj.paymode.code,accountID.getText());
            	if (i >= 0)
            	{
            		SalePayDef sp = (SalePayDef)saleBS.salePayment.elementAt(i);
            		accountID.setText(sp.payno);
            		userID.setText(sp.idno);
            		payment.setText(ManipulatePrecision.doubleToString(sp.ybje));
            	}
            }
            
            //
            e.data = "";
            date.setFocus();
            date.selectAll();
        }
        else if (txt.equals(date))
        {
        	datestr = date.getText().trim();
        	
        	if (datestr.trim().equals(""))
        	{
        		new MessageBox("请输入付货日期!");
        		return;
        	}
        	
        	if (datestr.length() == 8)
        	{
        		
        		if ((datestr = ManipulateDateTime.getConversionDate(datestr)) == null)
        		{
        			new MessageBox("请输入正确的长度!");
        			return ;
        		}
        		
        	}
        	
        	
        	if (!ManipulateDateTime.checkDate(datestr))
        	{
        		new MessageBox("请输入正确的日期:例如20080117或者2008/01/17");
        		return;
        	}
        	
        	e.data = "";
        	userID.setFocus();
        	userID.selectAll();
        }
        else if (txt.equals(userID))
        {
        	if (isNeedIdno && userID.getText().trim().equals(""))
        	{
        		new MessageBox("请输入证件号码!");
        		return;
        	}
        	
        	e.data = "";
            payment.setFocus();
            payment.selectAll();
        }
        else if (txt.equals(payment))
        {
        	if (payObj.createSalePay(txt.getText(),accountID.getText(),userID.getText(),datestr))
            {
                shell.close();
                shell.dispose();
            }
        	else
        	{
                payment.selectAll();
        	}
        }    	
    }
}
