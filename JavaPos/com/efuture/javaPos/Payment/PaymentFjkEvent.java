package com.efuture.javaPos.Payment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
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


public class PaymentFjkEvent
{
    private StyledText txtStatus = null;
    private Text txtMoney = null;
    private Text txtAccount = null;
    private Text focus = null;
    private Label lblPayName = null;
    private Table table = null;
    private TableEditor tEditor = null;
    private Shell shell = null;
    private Text newEditor = null;
    private SaleBS saleBS = null;
    private PaymentFjk payObj = null;
    private int[] currentPoint = new int[] { 0, 0 };
    private NewKeyListener keyNewEditor = null;
    private Label account = null;

    public PaymentFjkEvent(PaymentFjkForm pff, PaymentFjk pay, SaleBS sale)
    {
        this.txtStatus  = pff.getTxtStatus();
        this.txtMoney   = pff.getTxtMoney();
        this.txtAccount = pff.getTxtAccount();
        this.lblPayName = pff.getLblPayName();
        this.table      = pff.getTable();
        this.account    = pff.account;
        this.shell = pff.getShell();

        this.saleBS = sale;
        this.payObj = pay;
        
        tEditor                     = new TableEditor(table);
        tEditor.horizontalAlignment = SWT.LEFT;
        tEditor.grabHorizontal      = true;
        tEditor.minimumWidth        = 100;

        //设定键盘事件
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

            public void msrFinish(KeyEvent e, String track1, String track2,
                                  String track3)
            {
                msrRead(e, track1, track2, track3);
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
        key.event = event;

        key.inputMode = payObj.getAccountInputMode();
        txtAccount.addKeyListener(key);
        txtAccount.addFocusListener(listener);
        txtAccount.setData("MSRINPUT");

        setFocus(this.txtAccount);

        keyNewEditor       = new NewKeyListener();
        keyNewEditor.event = event;

        NewKeyListener key1 = new NewKeyListener();
        key1.event = event;
        key1.inputMode = key1.DoubleInput;
        txtMoney.addFocusListener(listener);
        txtMoney.addKeyListener(key1);

        table.addKeyListener(keyNewEditor);

        init();
    }

    private void init()
    {
        lblPayName.setText("[" + payObj.paymode.code + "]" +
                           payObj.paymode.name);

        double needPay = saleBS.calcPayBalance();
        txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
        
        account.setText(payObj.getDisplayAccountInfo());       
    }

    public void afterFormOpenDoEvent()
    {
    	payObj.choicFjkType();
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {
                case GlobalVar.ArrowUp:

                    if (txtAccount != e.widget)
                    {
                        if (currentPoint[0] > 0)
                        {
                            currentPoint[0] = currentPoint[0] - 1;
                            table.setSelection(currentPoint[0]);
                            findLocation();
                        }
/*                      不允许重新刷卡  
                        else
                        {
                            e.data = "";

                            tEditor.getEditor().dispose();

                            this.setFocus(txtAccount);
                            txtAccount.selectAll();

                            txtStatus.setText("请重新刷卡");
                            currentPoint[0] = 0;
                            currentPoint[1] = 0;

                            payObj.setFjkAMaxJe(0);
                            payObj.setFjkBMaxJe(0);
                        }
*/                        
                    }

                    break;
                	
                case GlobalVar.ArrowDown:

                    if (txtAccount != e.widget)
                    {
                        if (currentPoint[0] < (table.getItemCount() - 1))
                        {
                            currentPoint[0] = currentPoint[0] + 1;
                            table.setSelection(currentPoint[0]);
                            findLocation();
                        }
                    }

                    break;

                case GlobalVar.Enter:
                    enterInput(e);

                    break;

                case GlobalVar.Validation:
                	
                	if (e.widget == newEditor)
                	{
                		Text text = (Text) tEditor.getEditor();
                        if (!setPayment(text)) return ;
                	}
                	
                    if (e.widget != txtAccount)
                    {
                        double moneyA = 0;
                        double moneyB = 0;
                        double moneyF = 0;

                        for (int i = 0; i < table.getItemCount(); i++)
                        {
                            TableItem tableItem = table.getItem(i);

                            if (tableItem.getText(0).trim().equals(payObj.getAccountNameA()))
                            {
                                moneyA = Double.parseDouble(tableItem.getText(2).trim());
                            }
                            else if (tableItem.getText(0).trim().equals(payObj.getAccountNameB()))
                            {
                                moneyB = Double.parseDouble(tableItem.getText(2).trim());
                            }
                            else
                            {
                                moneyF = Double.parseDouble(tableItem.getText(2).trim());
                            }
                        }

                        if ((moneyA <= 0) && (moneyB <= 0) && (moneyF <= 0))
                        {
                            new MessageBox(Language.apply("请输入有效的付款金额!"));

                            Text text = (Text) tEditor.getEditor();
                            text.setFocus();
                            text.selectAll();

                            return;
                        }

                        //
                        if (payObj.createSalePay(moneyA, moneyB, moneyF))
                        {
                            shell.close();
                            shell.dispose();
                            shell = null;
                        }
                        else
                        {
                            new MessageBox(Language.apply("本次电子券付款无效!"));

                            Text text = (Text) tEditor.getEditor();
                            text.setFocus();
                            text.selectAll();

                            return;
                        }
                    }

                    break;

                case GlobalVar.Exit:
                    shell.close();
                    shell.dispose();
                    shell = null;

                    break;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setFocus(Text focus)
    {
        this.focus = focus;

        focus.setFocus();
    }

    public void msrRead(KeyEvent e, String track1, String track2, String track3)
    {
    	if (!payObj.needFindFjk(track1, track2, track3))
    	{
    		setFocus(txtMoney);
    		txtMoney.setSelection(0, txtMoney.getText().length());
    		return;
    	}
    	
        // 查询返券卡
        if (payObj.findFjk(track1, track2, track3))
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

            		// 重新刷新付款余额及已付款列表
                    double needPay = saleBS.calcPayBalance();
                    txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl,payObj.paymode));
            		saleBS.salePayEvent.refreshSalePayment();
            	}
            	if (!ret)
            	{
	                txtStatus.setText(Language.apply("此卡已经付款，请先删除原付款"));
	                txtAccount.selectAll();
	                setFocus(txtAccount);
	                return;
            	}
            }

            if (((SellType.ISBACK(payObj.salehead.djlb) && saleBS.isRefundStatus())||SellType.ISSALE(payObj.salehead.djlb)) && 
        		(Double.parseDouble(payObj.getAccountYeA()) <= 0) &&
                (Double.parseDouble(payObj.getAccountYeB()) <= 0) &&
                (Double.parseDouble(payObj.getAccountYeF()) <= 0))
            {
                new MessageBox(Language.apply("此卡无可用券余额\n或此卡已过期!"));

                txtAccount.setText(Language.apply("请重新刷卡"));
                setFocus(txtAccount);
                txtAccount.selectAll();

                return;
            }

            // 设置余额显示
            if (!setYeShow())
            {
                return;
            }

            // 提示描述
            txtStatus.setText(payObj.getDisplayStatusInfo());
            
        	// 显示卡号
            txtAccount.setText(payObj.getDisplayCardno());
            
            // 进入付款金额输入
            e.data          = "";
            currentPoint[0] = 0;
            currentPoint[1] = 2;
            table.setFocus();
            table.select(currentPoint[0]);
            findLocation();
        }
        else
        {
        	txtAccount.setText(Language.apply("请重新刷卡"));
            setFocus(txtAccount);
            txtAccount.selectAll();
        }
    }
    
    public boolean setPayment(Text text)
    {
        TableItem tableItem = table.getItem(table.getSelectionIndex());

        if (tableItem.getText(0).trim().equals(payObj.getAccountNameA()))
        {
            if (SellType.ISSALE(payObj.salehead.djlb) && (payObj.paymode.isyy != 'Y' || payObj.getFjkAMaxJe() <= 0) && payObj.getFjkAMaxJe() < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameA() + "允许的付款金额最大为:" +
//                               ManipulatePrecision.doubleToString(payObj.getFjkAMaxJe()) +
//                               "\n\n请重新输入付款金额");
                new MessageBox(Language.apply("{0}允许的付款金额最大为:{1}\n\n请重新输入付款金额" ,new Object[]{payObj.getAccountNameA() ,ManipulatePrecision.doubleToString(payObj.getFjkAMaxJe())}));
                text.selectAll();

                return false;
            }
			
            if (SellType.ISSALE(payObj.salehead.djlb) && Double.parseDouble(payObj.getAccountYeA()) < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameA() + "目前的余额只有:" +
//                               ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeA())) +
//                               "\n\n余额不足,请重新输入付款金额");
                new MessageBox(Language.apply("{0}目前的余额只有:{1}\n\n余额不足,请重新输入付款金额" ,new Object[]{payObj.getAccountNameA() ,ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeA()))}));
                text.selectAll();

                return false;
            }

            tEditor.getItem()
                   .setText(currentPoint[1],
                            ManipulatePrecision.doubleToString(Double.parseDouble(text.getText())));
        }
        else if (tableItem.getText(0).trim().equals(payObj.getAccountNameB()))
        {
            if (SellType.ISSALE(payObj.salehead.djlb) && (payObj.paymode.isyy != 'Y' || payObj.getFjkBMaxJe() <= 0) && payObj.getFjkBMaxJe() < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameB() + "允许的付款金额最大为:" +
//                               ManipulatePrecision.doubleToString(payObj.getFjkBMaxJe()) +
//                               "\n\n请重新输入付款金额");
                new MessageBox(Language.apply("{0}允许的付款金额最大为:{1}\n\n请重新输入付款金额" ,new Object[]{payObj.getAccountNameB() ,ManipulatePrecision.doubleToString(payObj.getFjkBMaxJe())}));
                text.selectAll();

                return false;
            }

            if (SellType.ISSALE(payObj.salehead.djlb) && Double.parseDouble(payObj.getAccountYeB()) < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameB() + "目前的余额只有:" +
//                               ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeB())) +
//                               "\n\n余额不足,请重新输入付款金额");
                new MessageBox(Language.apply("{0}目前的余额只有:{1}\n\n余额不足,请重新输入付款金额" ,new Object[]{payObj.getAccountNameB() ,ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeB()))}));
                text.selectAll();

                return false;
            }

            tEditor.getItem()
                   .setText(currentPoint[1],
                            ManipulatePrecision.doubleToString(Double.parseDouble(text.getText())));
        }
        else
        {
            if (SellType.ISSALE(payObj.salehead.djlb) && (payObj.paymode.isyy != 'Y' || payObj.getFjkFMaxJe() <= 0) && payObj.getFjkFMaxJe() < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameF() + "允许的付款金额最大为:" +
//                               ManipulatePrecision.doubleToString(payObj.getFjkFMaxJe()) +
//                               "\n\n请重新输入付款金额");
                new MessageBox(Language.apply("{0}允许的付款金额最大为:{1}\n\n请重新输入付款金额" ,new Object[]{payObj.getAccountNameF() ,ManipulatePrecision.doubleToString(payObj.getFjkFMaxJe())}));
                text.selectAll();

                return false;
            }
            
            if (SellType.ISSALE(payObj.salehead.djlb) && Double.parseDouble(payObj.getAccountYeF()) < Double.parseDouble(text.getText()))
            {
//                new MessageBox(payObj.getAccountNameF() + "目前的余额只有:" +
//                               ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeF())) +
//                               "\n\n余额不足,请重新输入付款金额");
                new MessageBox(Language.apply("{0}目前的余额只有:{1}\n\n余额不足,请重新输入付款金额" ,new Object[]{payObj.getAccountNameF() ,ManipulatePrecision.doubleToString(Double.parseDouble(payObj.getAccountYeF()))}));
                text.selectAll();

                return false;
            }

            tEditor.getItem()
                   .setText(currentPoint[1],
                            ManipulatePrecision.doubleToString(Double.parseDouble(text.getText())));
            
            //
            table.setSelection(currentPoint[0]);
            findLocation();
        }
        
        return true;
    }

    void enterInput(KeyEvent e)
    {
        if (e.widget == txtAccount)
        {
        	msrRead(e,"",txtAccount.getText(),"");
        }
        else if (e.widget == txtMoney)
        {
        	if (payObj.unNeedFindFjkDone(txtMoney.getText()))
        	{
        		NewKeyListener.sendKey(GlobalVar.Exit);
        	}
        }
        else if (e.widget == newEditor)
        {
            if (currentPoint[0] < table.getItemCount())
            {
                Text text = (Text) tEditor.getEditor();

                if (!text.getText().trim().equals(""))
                {
                    if (!setPayment(text)) return;
                }
                else
                {
                    tEditor.getItem().setText(currentPoint[1], "0.00");
                }

                // 计算已付款,检查付款是否已足够
                double je = 0;
                for (int i = 0;i<table.getItemCount();i++)
                {
                    // 确定返券类型
                    String fjktype = "";
                    if (table.getItem(i).getText(0).trim().equals(payObj.getAccountNameA()))
                    {
                    	fjktype = payObj.FJK_A;
                    }
                    else if (table.getItem(i).getText(0).trim().equals(payObj.getAccountNameB()))
                    {
                    	fjktype = payObj.FJK_B;
                    }
                    else
                    {
                    	fjktype = payObj.FJK_F;
                    }
                    
                	je += ManipulatePrecision.doubleConvert(Double.parseDouble(table.getItem(i).getText(currentPoint[1])) * payObj.getRefundHl(fjktype));
                }
                je = ManipulatePrecision.doubleConvert(Double.parseDouble(txtMoney.getText().trim()) - je,2,1);
                
                // 付款还不足,跳到下一个券种输入
                if (currentPoint[0] < (table.getItemCount() - 1) && je > 0)
                {
                    currentPoint[0] = currentPoint[0] + 1;

                    table.setSelection(currentPoint[0]);
                    findLocation();
                }
                else
                {
                	// 最后一个券种输入完成,确认付款
                	keyReleased(e,GlobalVar.Validation);
                }
            }
        }
    }

    public void findLocation()
    {
        Control oldEditor = tEditor.getEditor();

        if (oldEditor != null)
        {
            oldEditor.dispose();
        }

        if (table.getItemCount() <= 0)
        {
            return;
        }

        TableItem item = table.getItem(currentPoint[0]);

        if (item == null)
        {
            return;
        }

        newEditor = new Text(table, SWT.NONE | SWT.RIGHT);
        newEditor.setTextLimit(15);		
		
        // 先计算已付款,算出该券种剩余付款
        double je = 0;
        for (int i = 0;i<table.getItemCount();i++)
        {
            // 确定返券类型
            String fjktype = "";
            if (table.getItem(i).getText(0).trim().equals(payObj.getAccountNameA()))
            {
            	fjktype = payObj.FJK_A;
            }
            else if (table.getItem(i).getText(0).trim().equals(payObj.getAccountNameB()))
            {
            	fjktype = payObj.FJK_B;
            }
            else
            {
            	fjktype = payObj.FJK_F;
            }
            	
        	if (i != currentPoint[0])
        	{
        		je += ManipulatePrecision.doubleConvert(Double.parseDouble(table.getItem(i).getText(currentPoint[1])) * payObj.getRefundHl(fjktype));
        	}
        }
        je = ManipulatePrecision.doubleConvert(Double.parseDouble(txtMoney.getText().trim()) - je,2,1);
        if (je < 0) je = 0;
        
        // 确定返券类型
        String fjktype = "";
        if (item.getText(0).trim().equals(payObj.getAccountNameA()))
        {
        	fjktype = payObj.FJK_A;
        }
        else if (item.getText(0).trim().equals(payObj.getAccountNameB()))
        {
        	fjktype = payObj.FJK_B;
        }
        else
        {
        	fjktype = payObj.FJK_F;
        }
        
        // 设置输入框缺省值
        if (item.getText(0).trim().equals(payObj.getAccountNameA()))
        {
	        double monA = Math.min(Double.parseDouble(payObj.getAccountYeA()),payObj.getFjkAMaxJe());
	        monA = Math.min(je / payObj.getRefundHl(fjktype), monA);
	        newEditor.setText(ManipulatePrecision.doubleToString(monA));
        }
        else if (item.getText(0).trim().equals(payObj.getAccountNameB()))
        {
    		double monB = Math.min(Double.parseDouble(payObj.getAccountYeB()),payObj.getFjkBMaxJe());
    		monB = Math.min(je / payObj.getRefundHl(fjktype), monB);
    		newEditor.setText(ManipulatePrecision.doubleToString(monB));
        }
        else
        {
    		double monF = Math.min(Double.parseDouble(payObj.getAccountYeF()),payObj.getFjkFMaxJe());
    		monF = Math.min(je / payObj.getRefundHl(fjktype), monF);
    		newEditor.setText(ManipulatePrecision.doubleToString(monF));
        }
        
        //
        newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        newEditor.setFocus();
        tEditor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.selectAll();

        keyNewEditor.inputMode = keyNewEditor.DoubleInput;

        newEditor.addKeyListener(keyNewEditor);
    }

    //设置金额
    private boolean setYeShow()
    {
        return payObj.setYeShow(table); 
    }
}
