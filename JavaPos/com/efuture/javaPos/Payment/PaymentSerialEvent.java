package com.efuture.javaPos.Payment;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.MzkResultDef;

public class PaymentSerialEvent
{
    private Shell shell = null;
    private Label lbPayName = null;
    private Text textCardNoStart = null;
    private Text textCardNoEnd = null;
    private Text textSumSl = null;
    private Text textSumYe = null;
    private Label lbPayYe = null;
    private Text textPayYe = null;
    private StyledText sttMemo = null;
    private Text focus = null;
    private boolean isallowinputmoney = true;

    private SaleBS saleBS = null;
    private PaymentSerial payObj = null; 

    public PaymentSerialEvent(PaymentSerialForm form, PaymentSerial pay, SaleBS sale, boolean isallowinputpaymoney)
    {
    	saleBS = sale;
        payObj = pay;
        this.isallowinputmoney = isallowinputpaymoney;
        
        shell       = form.sShell;
        lbPayName = form.lbPayName;
        textCardNoStart = form.textCardNoStart;
        textCardNoEnd = form.textCardNoEnd;
        textSumSl = form.textSumSl;
        textSumYe = form.textSumYe;
        lbPayYe = form.lbPayYe;
        textPayYe = form.textPayYe;
        sttMemo = form.sttMemo;
        
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
        
        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
                if (focus != e.widget)
                {
                    focus.setFocus();
                }
            }

            public void focusLost(FocusEvent e)
            {
            	
            }
        };

        NewKeyListener key = new NewKeyListener();
        key.event   = event;
        textCardNoStart.addKeyListener(key);
        textCardNoEnd.addKeyListener(key);
        
        NewKeyListener key1 = new NewKeyListener();
        key1.event  = event;
        key1.inputMode = key.DoubleInput;
        textPayYe.addKeyListener(key1);
        key1.isControl = true;
        
        textCardNoStart.addFocusListener(listener);
        textCardNoEnd.addFocusListener(listener);
        textPayYe.addFocusListener(listener);
        textSumSl.addFocusListener(listener);
        textSumYe.addFocusListener(listener);
        
        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2,
                          (GlobalVar.rec.y - shell.getSize().y) / 2);
        
        initEvent();
    }

    public void initEvent()
    {  		
    	if (SellType.ISBACK(saleBS.saletype))
    		lbPayYe.setText(Language.apply("退款金额"));
    	else
    		lbPayYe.setText(Language.apply("付款金额"));   
    	
    	lbPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);
        
        double needPay = saleBS.calcPayBalance();
        textPayYe.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
        
        setFocus(textCardNoStart);
    }

    public void keyPressed(KeyEvent e, int key)
    {

    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        e.data = "focus";
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
    
    private boolean checkVaild()
    {
    	// 检查面值卡
		String strCardNoStart = textCardNoStart.getText().trim();
		String strCardNoEnd = textCardNoEnd.getText().trim();
		
		StringBuffer serialCardPartStart1 = new StringBuffer();
		StringBuffer serialCardPartStart2 = new StringBuffer();
		StringBuffer serialCardPartStart3 = new StringBuffer();
		
		StringBuffer serialCardPartEnd1 = new StringBuffer();
		StringBuffer serialCardPartEnd2 = new StringBuffer();
		StringBuffer serialCardPartEnd3 = new StringBuffer();
		
		long serialCardPartStart = 0;
		long serialCardPartEnd = 0;
		
		try
		{
			if (strCardNoStart.length() != strCardNoEnd.length())
			{
				new MessageBox(Language.apply("开始卡号与结束卡号长度不一致!"));
				setFocus(textCardNoStart);
				return false;
			}
			
			payObj.formatCardNo(strCardNoStart,serialCardPartStart1,serialCardPartStart2,serialCardPartStart3);
			
			payObj.formatCardNo(strCardNoEnd,serialCardPartEnd1,serialCardPartEnd2,serialCardPartEnd3);

			if (!serialCardPartStart1.toString().equals(serialCardPartEnd1.toString()) || !serialCardPartStart3.toString().equals(serialCardPartEnd3.toString()))
			{
				new MessageBox(Language.apply("请输入连续的卡号!"));
				setFocus(textCardNoStart);
				return false;
			}
			
			try
			{
				serialCardPartStart = Long.parseLong(serialCardPartStart2.toString());
			}
			catch(Exception ex)
			{
				new MessageBox(Language.apply("请输入正确的开始卡号!"));
				setFocus(textCardNoStart);
				return false;
			}
			
			try
			{
				serialCardPartEnd = Long.parseLong(serialCardPartEnd2.toString());
			}
			catch(Exception ex)
			{
				new MessageBox(Language.apply("请输入正确的结束卡号!"));
				setFocus(textCardNoEnd);
				return false;
			}
			
			if (serialCardPartStart > serialCardPartEnd )
			{
				new MessageBox(Language.apply("开始卡号必须小于结束卡号!"));
				setFocus(textCardNoStart);
				return false;
			}
			
			if (serialCardPartEnd - serialCardPartStart > 100)
			{
				new MessageBox(Language.apply("最多只允许输入100张连续卡号!"));
				setFocus(textCardNoEnd);
				return false;
			}
		}
		catch(Exception ex)
		{
			new MessageBox(Language.apply("请输入正确的卡号!"));
			setFocus(textCardNoStart);
			
			return false;
		}
		
    	return true;
    }
    
    void enterInput(KeyEvent e)
    {
    	Text txt = (Text)e.widget;

    	if (txt.equals(textCardNoStart))
    	{
    		if (textCardNoStart.getText().trim().length() > 0)
    		{
    			setFocus(textCardNoEnd);
    		}
    	}
    	else if (txt.equals(textCardNoEnd))
    	{
    		if (!checkVaild())
    		{
    			return;
    		}
    		
    		//查找面值卡
            if (payObj.findMzk(textCardNoStart.getText(),textCardNoEnd.getText()))
            {
            	for(int i = 0;i<payObj.arrMzkRets.size();i++)
            	{
            		//查询是否已录入此卡
                	if (saleBS.existPayment(payObj.paymode.code, ((MzkResultDef)(payObj.arrMzkRets.get(i))).cardno) >= 0 )
                	{
                		//sttMemo.setText("[" + payObj.mzkret.cardno + "]" + "此卡已付，请先删除原付款");
                		return;
                	}
            	}

            	if (SellType.ISSALE(payObj.salehead.djlb))
            	{	
            		if (isallowinputmoney)
            		{
			        	double min = Math.min(Double.parseDouble(textPayYe.getText()),Double.parseDouble(payObj.getSumYe()));
			        	textPayYe.setText(ManipulatePrecision.doubleToString(min));
            		}
            		else
            		{
            			textPayYe.setText(ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getSumYe())));
            		}
            	}
            	
            	textCardNoEnd.setText(payObj.getCardNoEnd());
            	
            	textSumYe.setText(payObj.getSumYe());
            	textSumSl.setText(payObj.getSumSl());
            	
            	sttMemo.setText(payObj.getDisplayStatusInfo());
            	setFocus(textPayYe);
            	
            	if (!isallowinputmoney)	textPayYe.setEditable(false);
            }
            else
            {
            	textSumYe.setText("");
            	textSumSl.setText("");
            	
            	if (Convert.toInt(payObj.getSumSl()) > 0)
            	{
            		textCardNoEnd.setText(payObj.getCardNoEnd());
            		setFocus(textCardNoEnd);
            	}
            	else
            	{
            		setFocus(textCardNoStart);
            	}
            }
    	}
    	else if (txt.equals(textPayYe))
    	{
        	if (payObj.createSalePay(txt.getText()))
            {
                shell.close();
                shell.dispose();
            }
        	else
        	{
        		textPayYe.selectAll();
        	}
    	}
    }
    
    private void setFocus(Text focus)
    {
        this.focus = focus;
        focus.setFocus();
        focus.selectAll();
    }
 
}
