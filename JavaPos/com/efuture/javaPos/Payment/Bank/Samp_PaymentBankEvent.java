package com.efuture.javaPos.Payment.Bank;

import java.util.Vector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;


public class Samp_PaymentBankEvent
{
	private Samp_PaymentBankForm form = null;
    private Shell shell = null;
    private Text txtAccount = null;
    private Text txtSeqno = null;
    private Text txtAuthno = null;
    private Text txtMoney = null;
    private Text txtDate = null;
    private Combo cmbType = null;
    private StyledText txtStatus = null;
    private Label lblPayName = null;
    private Label lblMoney = null;
    private Label lblSeqno	= null;
    private SaleBS saleBS = null;
    private PaymentBank payObj = null;
    private int banktype = -1;
    private String track1,track2,track3;
    private PaymentBankFunc pbfunc = null;
    private boolean ShellIsDisposed = false;
    private boolean FunctionIsRun = false;
    
    public Samp_PaymentBankEvent(Samp_PaymentBankForm form, PaymentBank pay, int type)
    {
    	this.form = form;
    	
        this.payObj = pay;
        this.saleBS = pay.saleBS;
        this.banktype = type;
        
    	pbfunc = CreatePayment.getDefault().getPaymentBankFunc();
        
        init(form);
    }
    
    public Samp_PaymentBankEvent(Samp_PaymentBankForm form, int type)
    {
    	this.form = form;

        this.banktype = type;
        
    	pbfunc = CreatePayment.getDefault().getPaymentBankFuncByMenu();
        
        init(form);
    }
    
    private void init(Samp_PaymentBankForm pbf)
    {
    	cmbType     = pbf.getCmbType();
    	txtAccount   = pbf.getTxtAccount();
    	txtDate     = pbf.getTxtDate();
    	txtSeqno    = pbf.getTxtSeqno();
    	txtAuthno   = pbf.getTxtAuthno();
    	txtMoney	= pbf.getTxtMoney();
        shell       = pbf.getSShell();
        lblPayName     = pbf.getLblPayName();
        lblMoney	= pbf.getLblMoney();
        lblSeqno 	= pbf.getLblSeqno();
        txtStatus  = pbf.getTxtStatus();
        
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
        key.inputMode = key.MsrInput;
        txtAccount.addKeyListener(key);
        txtAccount.setData("MSRINPUT");

        NewKeyListener key1 = new NewKeyListener();
        key1.event  = event;        
        txtSeqno.addKeyListener(key1);
        txtAuthno.addKeyListener(key1);
        txtDate.addKeyListener(key1);
        cmbType.addKeyListener(key1);
        
        NewKeyListener key2 = new NewKeyListener();
        key2.event  = event;
        key2.inputMode = key.DoubleInput;
        txtMoney.addKeyListener(key2);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2 , (GlobalVar.rec.y - shell.getSize().y) / 2);
                        
        initEvent();
    }

    public void initEvent()
    {
    	if (saleBS != null)
    	{
	    	if (SellType.ISBACK(saleBS.saletype))
	    	{
	    		lblMoney.setText("退款金额");
	    		banktype = 1;
	    	}
	    	else
	    	{
	    		lblMoney.setText("付款金额");
	    		banktype = 0;
	    	}
	    	
	    	lblPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);
	        
	        double needPay = saleBS.calcPayBalance();
	        txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
    	}
    	else
    	{
    		lblPayName.setText("银联金卡工程");
    		
    		if (payObj != null && payObj.salepay != null)
    		{
    			txtMoney.setText(saleBS.getPayMoneyByPrecision(payObj.salepay.ybje,payObj.paymode));
    		}
    	}
    	
        cmbType.setFocus();
        cmbType.select(banktype);
        
        // 小票交易时如果是消费，不允许修改交易类型
        if (saleBS != null && banktype == 0)
        {
        	txtSeqno.setEditable(false);
        	txtAuthno.setEditable(false);
        	txtDate.setEditable(false);
        	txtAccount.setEditable(true);
        	txtAccount.selectAll();
        	txtAccount.setFocus();
        }
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
    	if (ShellIsDisposed) return;
    	
        switch (key)
        {
            case GlobalVar.Enter:
            	if (!FunctionIsRun)
            	{
            		enterInput(e);
            	}
            	break;
            case GlobalVar.MsrError:
            	new MessageBox("刷卡失败,请重新刷卡...");
            	txtAccount.selectAll();
            	break;
            case GlobalVar.Exit:
                shell.close();
                shell.dispose();
                break;
        }
    }
        
    private boolean checkType()
    {  	
		// 销售交易时只允许选择0
    	// 退货交易时只运行选择1,2
		// 非小票交易时不允许选择0
    	// 红冲时只允许选择1
    	// 后台退货时只允许选择1,2
		if ((saleBS != null && !SellType.ISBACK(saleBS.saletype) && cmbType.getSelectionIndex() != 0) ||
			(saleBS != null && SellType.ISBACK(saleBS.saletype) && cmbType.getSelectionIndex() != 1 && cmbType.getSelectionIndex() != 2) || 
			(saleBS == null && cmbType.getSelectionIndex() == 0) ||
			(payObj != null && SellType.ISHC(payObj.salehead.djlb) && cmbType.getSelectionIndex() != 1) ||
			(payObj != null && SellType.ISBACK(payObj.salehead.djlb) && cmbType.getSelectionIndex() != 1 && cmbType.getSelectionIndex() != 2))
		{
			new MessageBox("当前不允许进行该交易,请重新选择");
			return false;
		}
	
		return true;
    }
    
    void enterInput(KeyEvent e)
    {
    	if (e.widget.equals(cmbType))
    	{
    		// 检查交易类型
    		if (!checkType()) return;
    		
    		switch(cmbType.getSelectionIndex())
    		{
    			case PaymentBank.XYKXF://消费
    				txtSeqno.setEditable(false);
    				txtAuthno.setEditable(false);
    	        	txtDate.setEditable(false);
    	        	txtAccount.setEditable(true);
    	        	txtMoney.setEditable(true);
    	        	txtAccount.setText("");
    	        	txtAccount.setFocus();
    	        	break;
    			case PaymentBank.XYKCX://消费撤销
    				txtSeqno.setEditable(true);
    				txtAuthno.setEditable(false);
    	        	txtDate.setText(ManipulateDateTime.getCurrentDate());
    	        	txtDate.setEditable(false);
    				txtMoney.setEditable(true);
    				txtAccount.setEditable(true);
    				txtSeqno.setText("");
    				txtSeqno.setFocus();
    				break;
    			case PaymentBank.XYKTH://隔日退货
    				lblSeqno.setText("原参考号");
    				txtSeqno.setEditable(true);
    				txtAuthno.setEditable(true);
    	        	txtDate.setEditable(true);
    	        	txtAccount.setEditable(true);
    	        	txtMoney.setEditable(true);
    	        	txtSeqno.setText("");
    	        	txtSeqno.setFocus();
    	        	break;
    			case PaymentBank.XYKQD://交易签到
    				
    				
    				txtSeqno.setEditable(false);
    				txtAuthno.setEditable(false);
    	        	txtDate.setEditable(false);    
    	        	txtAccount.setEditable(false);
    	        	txtMoney.setEditable(true);
    	        	txtMoney.setText("按回车键开始交易签到");
    	        	txtMoney.selectAll();
    	        	txtMoney.setFocus();
    	        	break;
    			case PaymentBank.XYKJZ://交易结账
    				txtSeqno.setEditable(false);
    				txtAuthno.setEditable(false);
    	        	txtDate.setEditable(false);    
    	        	txtAccount.setEditable(false);
    	        	txtMoney.setEditable(true);
    	        	txtMoney.setText("按回车键开始交易结账");
    	        	txtMoney.selectAll();
    	        	txtMoney.setFocus();
    	        	break;    				
    			case PaymentBank.XYKYE://余额查询
    				txtSeqno.setEditable(false);
    				txtAuthno.setEditable(false);
    	        	txtDate.setEditable(false);
    	        	txtAccount.setEditable(true);
    	        	txtMoney.setEditable(true);
    	        	txtAccount.setText("");
    	        	txtAccount.setFocus();
    	        	break;
    			case PaymentBank.XYKCD://签购单重打
    				txtSeqno.setEditable(true);
    				txtAuthno.setEditable(false);
    				txtDate.setEditable(false);
    				txtAccount.setEditable(false);
    				txtMoney.setEditable(true);
    				txtSeqno.setText("");
    				txtSeqno.setFocus();
    			break;
    		}
    		
    		e.data = "focus";
    	}
    	else
    	{
        	Text txt = (Text)e.widget;
        	
        	if (txt.equals(txtSeqno))
        	{
        		switch(cmbType.getSelectionIndex())
        		{
        			case PaymentBank.XYKXF://消费
           				txtSeqno.setEditable(false);
           				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;
        			case PaymentBank.XYKCX://消费撤销
           				txtSeqno.setEditable(false);
           				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;
        			case PaymentBank.XYKTH://隔日退货
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(true);
        				txtDate.setEditable(true);
        				txtAccount.setEditable(true);
        				txtMoney.setEditable(true);
        				txtAuthno.setText("");
        				txtAuthno.setFocus();
        				break;
        			case PaymentBank.XYKQD://交易签到
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易签到");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;
        			case PaymentBank.XYKJZ://交易结账
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易结账");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;    	        	        	
        			case PaymentBank.XYKYE://余额查询        				
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;        				
        			case PaymentBank.XYKCD://签购单重打
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        				txtAccount.setEditable(false);
        				txtMoney.setEditable(true);
        				txtMoney.setText("按回车键开始签购单重打");
        				txtMoney.selectAll();
        	        	txtMoney.setFocus();        	        	
        	        	break;
        		}
        		
        		e.data = "focus";
        	}
        	else if (txt.equals(txtAuthno))
        	{
        		switch(cmbType.getSelectionIndex())
        		{
        			case PaymentBank.XYKXF://消费
           				txtSeqno.setEditable(false);
           				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;
        			case PaymentBank.XYKCX://消费撤销
           				txtSeqno.setEditable(false);
           				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;
        			case PaymentBank.XYKTH://隔日退货
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(true);
        				txtAccount.setEditable(true);
        				txtMoney.setEditable(true);
        				txtDate.setText("");
        				txtDate.setFocus();
        				break;
        			case PaymentBank.XYKQD://交易签到
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易签到");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;
        			case PaymentBank.XYKJZ://交易结账
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易结账");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;    	        	        	
        			case PaymentBank.XYKYE://余额查询        				
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;        				
        			case PaymentBank.XYKCD://签购单重打
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        				txtAccount.setEditable(false);
        				txtMoney.setEditable(true);
        				txtMoney.setText("按回车键开始签购单重打");
        				txtMoney.selectAll();
        	        	txtMoney.setFocus();        	        	
        	        	break;
        		}
        		
        		e.data = "focus";
        	}
        	else if (txt.equals(txtDate))
        	{
        		switch(cmbType.getSelectionIndex())
        		{
        			case PaymentBank.XYKXF://消费
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;        				
        			case PaymentBank.XYKCX://消费撤销
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;        				
        			case PaymentBank.XYKTH://隔日退货
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;  
        			case PaymentBank.XYKQD://交易签到
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易签到");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;
        			case PaymentBank.XYKJZ://交易结账
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        	        	txtDate.setEditable(false);    
        	        	txtAccount.setEditable(false);
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易结账");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;            	        	
        			case PaymentBank.XYKYE://余额查询        				
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        	        	txtAccount.setEditable(true);
        	        	txtMoney.setEditable(true);
        	        	txtAccount.setText("");
        	        	txtAccount.setFocus();
        	        	break;
        			case PaymentBank.XYKCD://签购单重打
        				txtSeqno.setEditable(false);
        				txtAuthno.setEditable(false);
        				txtDate.setEditable(false);
        				txtAccount.setEditable(false);
        				txtMoney.setEditable(true);
        				txtMoney.setText("按回车键开始签购单重打");
        	        	txtMoney.selectAll();
        	        	txtMoney.setFocus();
        	        	break;
        		}
        		
        		e.data = "focus";
        	}
        	else if (txt.equals(txtAccount))
        	{
				txtSeqno.setEditable(false);
				txtAuthno.setEditable(false);
				txtDate.setEditable(false);
	        	txtAccount.setEditable(false);
	        	
        		switch(cmbType.getSelectionIndex())
        		{
        			case PaymentBank.XYKXF://消费
        	        	txtMoney.setEditable(true);
        	        	break;        				
        			case PaymentBank.XYKCX://消费撤销
        	        	txtMoney.setEditable(true);
        	        	break;        				
        			case PaymentBank.XYKTH://隔日退货
        	        	txtMoney.setEditable(true);
        	        	break;  
        			case PaymentBank.XYKQD://交易签到
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易签到");
        	        	break;
        			case PaymentBank.XYKJZ://交易结账
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始交易结账");
        	        	break;            	        	
        			case PaymentBank.XYKYE://余额查询        				
        	        	txtMoney.setEditable(true);
        	        	txtMoney.setText("按回车键开始查询余额");
        	        	break;
        			case PaymentBank.XYKCD://签购单重打
        				txtMoney.setEditable(true);
        				txtMoney.setText("按回车键开始签购单重打");
        	        	break;
        		}
        		
	        	txtMoney.selectAll();
	        	txtMoney.setFocus();        		
        		e.data = "focus";
        	}        	
	        else if (txt.equals(txtMoney))
	        {
	        	// 检查交易类型
	        	if (!checkType())
	        	{
	        		cmbType.setFocus();
	        		e.data = "focus";
	        		return;
	        	}
	        	
	        	// 小票交易时，先生成付款对象,校验金额等信息是否合法
	        	if (saleBS != null)
	        	{
		        	if (!payObj.createSalePay(txt.getText()))
		            {
		        		txtMoney.selectAll();
		        		return;
		            }
	        	}

	        	//
	        	double amount = 0;
	        	try
	        	{
	        		amount = Double.parseDouble(txtMoney.getText());
	        	}
	        	catch(Exception er)
	        	{
	        		amount = 0;
	        	}
	        		        	
	        	// 调用金卡工程接口
	        	txtStatus.setText("开始调用金卡工程接口,请等待...");
	        	
	        	// 执行一次，本窗口不在相应ENTER
                ShellIsDisposed = true;

                //
                Vector vecMemo = new Vector();
                vecMemo.add(txtAuthno.getText());
                
	        	//
	        	boolean ret = pbfunc.callBankFunc(cmbType.getSelectionIndex(), amount,
	        									   track1, track2, track3,
	        									   txtSeqno.getText(), txtAuthno.getText(),txtDate.getText(),vecMemo);

	        	// 窗口每次只允许一次银联调用
	        	FunctionIsRun = true;
	        	
				//														        			
	        	txtStatus.setText("金卡工程接口调用完成!" + "\n\n" + pbfunc.getErrorMsg());

				// 小票交易时，记录交易数据到付款对象
	        	if (saleBS != null)
	        	{
	        		payObj.accountPay(ret,pbfunc);
	        	}
				
				// 检查金卡工程交易是否成功
				if (!ret)
				{
					txtMoney.selectAll();
					
					// 恢复允许按键
					ShellIsDisposed = false;
	        	}
	        	else
	        	{
		        	new MessageBox("金卡工程交易成功!");
		        	
		        	form.setDone(true);
	                shell.close();
	                shell.dispose();
	            }
	        }
    	}
    }
    
    public void msrRead(KeyEvent e,String track1,String track2,String track3)
    {
    	// 记录磁道信息
    	this.track1 = track1;
    	this.track2 = track2;
    	this.track3 = track3;
    	
    	txtSeqno.setEditable(false);
    	txtAuthno.setEditable(false);
    	txtDate.setEditable(false);    
    	txtAccount.setEditable(false);
    	
		switch(cmbType.getSelectionIndex())
		{
			case PaymentBank.XYKXF://消费
				txtMoney.setEditable(true);
				break;
			case PaymentBank.XYKCX://消费撤销
				txtMoney.setEditable(true);
				break;
			case PaymentBank.XYKTH://隔日退货   
				txtMoney.setEditable(true);
				break;
			case PaymentBank.XYKQD://交易签到
				txtMoney.setEditable(true);
				txtMoney.setText("按回车键开始交易签到");
				break;
			case PaymentBank.XYKJZ://交易结账
				txtMoney.setEditable(true);
				txtMoney.setText("按回车键开始交易结账");
				break;    				
			case PaymentBank.XYKYE://余额查询    
				txtMoney.setEditable(true);
				txtMoney.setText("按回车键开始余额查询");
				break;           				
			case PaymentBank.XYKCD://签购单重打
				txtMoney.setEditable(true);
				txtMoney.setText("按回车键开始签购单重打");
				break;
		}
		
		txtMoney.selectAll();
		txtMoney.setFocus();
		e.data = "focus";
    }    
}
