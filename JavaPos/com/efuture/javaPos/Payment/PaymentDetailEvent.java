package com.efuture.javaPos.Payment;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.SalePayDef;


public class PaymentDetailEvent
{
    private Shell shell = null;
    private Text accountID = null;
    private Text userID = null;
    private Text payment = null;
    private Label payName = null;
    private Label MoneyInfo = null;
    private SaleBS saleBS = null;
    private PaymentDetail payObj = null;
    private boolean isNeedAccnt = false;
    private boolean isNeedIdno = false;

    public PaymentDetailEvent(PaymentDetailForm form, PaymentDetail pay, SaleBS sale,boolean isneedaccnt,boolean isneedidno)
    {
        this.saleBS = sale;
        this.payObj = pay;
        accountID   = form.AccountTxt;
        userID      = form.IDtxt;
        payment     = form.Moneytxt;
        shell       = form.sShell;
        payName     = form.payName;
        MoneyInfo	= form.Money;
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
            
            public void msrFinish(KeyEvent e,String track1,String track2,String track3)
            {   
            	// 将卡号解析出来作为文本输入
            	if (track2.indexOf('=') >= 0)
            	{
            		accountID.setText(track2.substring(0, track2.indexOf('=')));
            	}
            	else
            	{
            		accountID.setText(track2);
            	}
            	
            	//
            	enterInput(e);
            }            
        };

        NewKeyListener key = new NewKeyListener();
        key.event   = event;
        key.inputMode = payObj.getAccountInputMode();
        accountID.addKeyListener(key);
        accountID.setData("MSRINPUT");
        
        NewKeyListener key1 = new NewKeyListener();
        key1.event  = event;
        userID.addKeyListener(key1);

        NewKeyListener key2 = new NewKeyListener();
        key2.event  = event;
        key2.inputMode = key.DoubleInput;
        payment.addKeyListener(key2);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2,
                          (GlobalVar.rec.y - shell.getSize().y) / 2);
        
        initEvent();
    }

    public void initEvent()
    {
    	if (SellType.ISBACK(saleBS.saletype))
    		MoneyInfo.setText(Language.apply("退款金额"));
    	else
    		MoneyInfo.setText(Language.apply("付款金额"));
    	
        payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);
        
        double needPay = saleBS.calcPayBalance();
        
        if (GlobalInfo.sysPara.isMoneyInputDefault == 'Y')
		{
        	payment.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
		}
        else
        {
        	if (GlobalInfo.sysPara.MoneyInputDefaultPay == null || GlobalInfo.sysPara.MoneyInputDefaultPay.equals(""))
			{
        		payment.setText("0");
			}
        	else
        	{
        		boolean isexist = false;
				
				String[] paycodes = GlobalInfo.sysPara.MoneyInputDefaultPay.split(",");
				
				for (int i = 0;i < paycodes.length;i++)
				{
					if (paycodes[i].trim().equals(payObj.paymode.code)) 
					{
						payment.setText("0");
						isexist = true;
						break;
					}
				}
				
				if (!isexist) payment.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
        	}
        }
        
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
            case GlobalVar.ArrowUp:
            	if (e.widget.equals(userID))
            	{
            		e.data = "focus";
            		accountID.setFocus();
            		accountID.selectAll();
            	}
            	else if (e.widget.equals(payment))
            	{
            		e.data = "focus";
            		userID.setFocus();
            		userID.selectAll();
            	}
            	break;
            case GlobalVar.ArrowDown:
            	if (e.widget.equals(accountID))
            	{
            		if (isNeedAccnt && accountID.getText().trim().equals(""))
                	{
                		new MessageBox(Language.apply("请输入付款帐号!"));
                		return;
                	}
            		e.data = "focus";
            		userID.setFocus();
            		userID.selectAll();
            	}
            	else if (e.widget.equals(userID))
            	{
                	if (isNeedIdno && userID.getText().trim().equals(""))
                	{
                		new MessageBox(Language.apply("请输入证件号码!"));
                		return;
                	}
            		e.data = "focus";
            		payment.setFocus();
            		payment.selectAll();
            	}
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
        	/**
        	if (accountID.getText().trim().length() > 20 )
        	{
        		new MessageBox("帐号长度超过20位，请重新输入");
        		return;
        	}
        	*/
        	if (isNeedAccnt && accountID.getText().trim().equals(""))
        	{
        		new MessageBox(Language.apply("请输入付款帐号!"));
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
            userID.setFocus();
            userID.selectAll();
        }
        else if (txt.equals(userID))
        {
        	if (isNeedIdno && userID.getText().trim().equals(""))
        	{
        		new MessageBox(Language.apply("请输入证件号码!"));
        		return;
        	}
        	
        	e.data = "";
            payment.setFocus();
            payment.selectAll();
        }
        else if (txt.equals(payment))
        {
        	if (payObj.createSalePay(txt.getText(),accountID.getText(),userID.getText()))
            {
        		// 覆盖模式删除账号相同的付款再增加
                if (GlobalInfo.sysPara.payover == 'Y')
                {
                	int i = saleBS.existPayment(payObj.paymode.code,accountID.getText());
                	if (i >= 0) 
                	{
                		saleBS.deleteSalePay(i);
                	}
                }
                
                // 关闭
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
