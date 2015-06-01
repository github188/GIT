package custom.localize.Zspj;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;


public class Zspj_PaymentHbEvent
{
    public Shell shell = null;
    public Text accountTxt = null;
    public Text yeTxt = null;
    public Label yeTips = null;
    public Text pwdTxt = null;
    public Text moneyTxt = null;
    public Label MoneyInfo = null;
    public Label payName = null;
    public Label AccountInfo = null;
    public StyledText StatusInfo = null;
    public SaleBS saleBS = null;
    public Zspj_PaymentHb payObj = null;
    public boolean ShellIsDisposed = false;
    
    public Zspj_PaymentHbEvent(Zspj_PaymentHbForm form, Zspj_PaymentHb pay, SaleBS sale)
    {
        this.saleBS = sale;
        this.payObj = pay;
        accountTxt   = form.AccountTxt;
        yeTxt      = form.Yetxt;
        yeTips 		= form.YeTips;
        pwdTxt		= form.PwdTxt;
        moneyTxt     = form.Moneytxt;
        MoneyInfo	= form.Money;
        AccountInfo = form.Acount;
        StatusInfo = form.status;
        shell       = form.sShell;
        payName     = form.payName;
        
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
            	msrRead(e,track1,track2,track3);
            }
        };

        NewKeyListener key = new NewKeyListener();
        
        key.event   = event;
        key.inputMode = payObj.getAccountInputMode();
        key.funcRun_ext = payObj.getExtRun();
        key.payCode = payObj.paymode.code; // 传入当前激活的付款代码，存在多IC卡设备时使用
        accountTxt.addKeyListener(key);
        accountTxt.setData("MSRINPUT");
        
        NewKeyListener key1 = new NewKeyListener();
        key1.setEditableResponseEvent(true);
        key1.event  = event;
        key1.inputMode = key.DoubleInput;
        moneyTxt.addKeyListener(key1);

        NewKeyListener key2 = new NewKeyListener();
        key2.event  = event;
        pwdTxt.addKeyListener(key2);
        pwdTxt.setLayoutData(yeTxt.getLayoutData());
        pwdTxt.setVisible(false);
        
        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2,
                          (GlobalVar.rec.y - shell.getSize().y) / 2);
        
        initEvent();
        
        autoFindCard();
    }
    
    public void autoFindCard()
    {
    	try
    	{
    		if(payObj.isAutoFindCard())
    		{
    	    	//加载界面时，去找卡
    	    	msrRead(null,"","","");
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }

    public void initEvent()
    {
    	if (SellType.ISBACK(saleBS.saletype)) MoneyInfo.setText(Language.apply("退款金额"));
    	else MoneyInfo.setText(Language.apply("付款金额"));    	
    	
        payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);
        
        // 设置付款缺省金额,inputMoney外部传入金额模式
        if (!isInputMoney())
        {
        	double needPay = saleBS.calcPayBalance();
            moneyTxt.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));            
        }
        else
        {
        	moneyTxt.setText(saleBS.getPayMoneyByPrecision(Double.parseDouble(this.payObj.inputMoney) ,payObj.paymode));
        }
        
        // 显示刷卡提示信息
        AccountInfo.setText(payObj.getDisplayAccountInfo());
        
        
        // 是否需要读卡
        if (payObj.needFindAccount())
        {

        	if(payObj.getPhoneAccountIn() != null)
        	{
	        accountTxt.setText(payObj.getPhoneAccountIn());
        	}
	        accountTxt.setFocus();
//	        accountTxt.selectAll();
	        
	        StatusInfo.setText(payObj.getInitMessage());
        }
        else
        {
        	// 自动生成找到卡信息
        	if (!payObj.autoCreateAccount())
        	{
        		shell.close();
        		shell.dispose();
        		return;
        	}
        	
        	// 显示卡号
        	accountTxt.setEditable(false);
        	accountTxt.setBackground(SWTResourceManager.getColor(255, 255, 255));
        	accountTxt.setText(payObj.getDisplayCardno());
        	
        	// 显示余额
        	yeTxt.setText(ManipulatePrecision.doubleToString(payObj.getAccountYe()));
        	
        	// 输入金额
        	double min = Math.min(Double.parseDouble(moneyTxt.getText()),payObj.getAccountAllowPay());
        	if (!SellType.ISBACK(saleBS.saleHead.djlb)) min = Math.min(min,payObj.getAccountYe());
        	moneyTxt.setText(ManipulatePrecision.doubleToString(min));   	
            moneyTxt.setFocus();
            moneyTxt.selectAll();
            
//            // 显示卡信息
//            StatusInfo.setText(payObj.getDisplayStatusInfo());
        }
        
        payObj.specialDeal(this);
    }

    public void afterFormOpenDoEvent()
    {
    	if (payObj.needFindAccount()) payObj.choicTrackType();
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
    	if (ShellIsDisposed) return ;
        switch (key)
        {
            case GlobalVar.Enter:
            	enterInput(e);
                break;
            case GlobalVar.MzkRecycle:
            	recycle();
            	break;
            case GlobalVar.Exit:
                shell.close();
                shell.dispose();

                break;
        }
    }
    
    public void recycle()
    {
    	Zspj_PaymentHb.recycleStatus = !Zspj_PaymentHb.recycleStatus;
    	
//    	StatusInfo.setText(payObj.getDisplayStatusInfo());
    }
    
    public void msrRead(KeyEvent e,String track1,String track2,String track3)
    {
    	if (!payObj.needFindAccount()) return;

        // 查询面值卡
        if (payObj.findMzk(track1,track2,track3))
        {
        	// 查询是否已刷此卡
        	if (saleBS.existPayment(payObj.paymode.code, payObj.mzkret.cardno) >= 0)
        	{
            	boolean ret = false;
            	if (new MessageBox(Language.apply("此卡已进行付款,你要取消原付款重新输入吗？"),null,true).verify() == GlobalVar.Key1)
            	{
            		ret = true;
            		int n = -1;
            		do {
            			n = saleBS.existPayment(payObj.paymode.code, payObj.mzkret.cardno);
            			if (n >= 0)
            			{
            				if (!saleBS.deleteSalePay(n))
            				{
            					ret = false;
            					break;
            				}
            			}
            		} while(n >= 0);

            		// 删除了付款需要重新刷新付款余额及已付款列表
            		if (!isInputMoney())
            		{
            			double needPay = saleBS.calcPayBalance();
                        moneyTxt.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));                		
            		}
            		else
            		{            			
            			moneyTxt.setText(saleBS.getPayMoneyByPrecision(Double.parseDouble(this.payObj.inputMoney) ,payObj.paymode));
            		}
                    saleBS.salePayEvent.refreshSalePayment();
            	}
            	if (!ret)
            	{
            		StatusInfo.setText(Language.apply("此卡已经付款，请先删除原付款"));
            		accountTxt.selectAll();
            		return;
            	}
        	}
        	
        /*	// 检查余额
        	if (SellType.ISSALE(payObj.salehead.djlb))
        	{
        		if (payObj.getAccountYe() <= 0)
        		{
        			new MessageBox(Language.apply("卡内余额为0,余额不足!"));
        			
                	accountTxt.setText(Language.apply("请重新刷卡"));
                	accountTxt.selectAll();
                	
                	return;
        		}
        	}*/
        	
        	// 显示卡号和状态提示
//        	accountTxt.setText(payObj.getDisplayCardno());
//            StatusInfo.setText(payObj.getDisplayStatusInfo());
            
            // 设置缺省付款金额，取账户余额与付款余额的较小值
        	double min = 0;;
        	if (SellType.ISBACK(saleBS.saleHead.djlb))
        	{
        		min = Math.min(Double.parseDouble(moneyTxt.getText()), Math.abs(payObj.mzkret.money - payObj.getAccountYe()));
        	}
        	else
        	{
        		min = payObj.getPayJe(Double.parseDouble(moneyTxt.getText()));
//        		min = Math.min(Double.parseDouble(moneyTxt.getText()),payObj.getAccountAllowPay());
//        		min = Math.min(min,payObj.getAccountYe());
        	}
//        	moneyTxt.setText(ManipulatePrecision.doubleToString(min));
//        	payObj.setMoneyVisible(this);
    		// 是否输入密码
        	payObj.setPwdAndYe(this, e);
        	/*
        	if (payObj.isPasswdInput())
        	{
        		// 显示密码
        		yeTips.setText(payObj.getPasswdLabel());        		
        		yeTxt.setVisible(false);
        		pwdTxt.setVisible(true);
        		yeTxt.setText(ManipulatePrecision.doubleToString(payObj.getAccountYe()));
        		
	        	if (e != null) e.data = "focus";
	        	pwdTxt.setFocus();
	        	pwdTxt.selectAll();
        	}
        	else
        	{
	            // 显示余额
        		yeTips.setText("账户余额");
        		yeTxt.setVisible(true);
        		pwdTxt.setVisible(false);
	            yeTxt.setText(ManipulatePrecision.doubleToString(payObj.getAccountYe()));

	            // 输入金额
	            if (e != null) e.data = "focus";
	            moneyTxt.setFocus();
	            moneyTxt.selectAll();
        	}
        	*/
        	
        	// 刷卡后自动计算付款金额,并生成付款方式
        	if (payObj.AutoCalcMoney())
        	{
        		if (!payObj.createSalePay(this.moneyTxt.getText()))
        		{
        			return;
        		}
        		
                this.shell.close();
                this.shell.dispose();
        	}
        }
        else
        {
        	payObj.doAfterFail(this);
        	/*
        	accountTxt.setText("请重新刷卡");
        	accountTxt.selectAll();
        	*/
        }
    }
    
    void enterInput(KeyEvent e)
    {
    	Text txt = (Text)e.widget;
    	if (txt.equals(accountTxt))
    	{
    		msrRead(e,"",accountTxt.getText(),"");
    	}
    	else if (txt.equals(pwdTxt))
    	{/*
    		// 设置交易密码
    		if (payObj.sendMzkSale(req, ret))
    		{
	            // 显示余额
	    		yeTips.setText(Language.apply("账户余额"));
	    		yeTxt.setVisible(true);
	    		pwdTxt.setVisible(false);
	            yeTxt.setText(ManipulatePrecision.doubleToString(payObj.getAccountYe()));
	            	            
	            // 输入金额
	        	e.data = "focus";
	            moneyTxt.setFocus();
	            moneyTxt.selectAll();
    		}
    		else
    		{
    			pwdTxt.selectAll();
    		}*/
    		
    		payObj.mzkreq.passwd =  pwdTxt.getText();
    		
    		 // 输入金额
        	e.data = "focus";
            moneyTxt.setFocus();
            moneyTxt.selectAll();
            moneyTxt.setEditable(false);
    	}
    	else if (txt.equals(moneyTxt))
        {
    		ShellIsDisposed = true;
    		try{
    		// 创建付款对象
        	if (payObj.createSalePay(txt.getText()))
            {
                shell.close();
                shell.dispose();
            }
        	else
        	{
        		ShellIsDisposed = false;
        		
        		// 外部传入金额模式如果付款失败直接关闭窗口
        		if (isInputMoney())
        		{
    				shell.close();
                    shell.dispose();
        		}
        		else 
        		{
        			moneyTxt.selectAll();
        		}
        	}
    		}catch(Exception er)
    		{
    			er.printStackTrace();
    		}
    		finally
    		{
    			ShellIsDisposed = false;
    		}
        }
    }
    
    private boolean isInputMoney()
    {
    	try
    	{
        	if (this.payObj.inputMoney == null || this.payObj.inputMoney.trim().length() <= 0) return false;
        	return true;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
}
