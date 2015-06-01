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

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;


public class PaymentDzqEvent
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
    private PaymentDzq payObj = null;
    private int[] currentPoint = new int[] { 0, 0 };
    private NewKeyListener keyNewEditor = null;
    private Label account = null;

    public PaymentDzqEvent(PaymentDzqForm pff, PaymentDzq pay, SaleBS sale)
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

            public void msrFinish(KeyEvent e, String track1, String track2,String track3)
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
        lblPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

        // 多券种余额可以支持不同汇率,剩余付款以本位币金额为准
        double needPay = saleBS.calcPayBalance();
        txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay,payObj.paymode));
        
        account.setText(payObj.getDisplayAccountInfo());       
    }

    public void afterFormOpenDoEvent()
    {
    	//payObj.choicTrackType();
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
                        if (!setPayment(text))
                        {
                        	text.selectAll();
                        	text.setFocus();
                        	return;
                        }
                	}

                    shell.close();
                    shell.dispose();
                    
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
        // 查询电子券
        if (payObj.findMzk(track1, track2, track3))
        {
            // 设置余额显示
            if (!setYeShow())
            {
            	txtAccount.setText(Language.apply("请重新刷卡"));
                setFocus(txtAccount);
                txtAccount.selectAll();            	
                return;
            }

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
    	boolean ret = payObj.createSalePay(currentPoint[0],Convert.toDouble(text.getText()));
    	
    	// 增加了付款重刷剩余付款
        double needPay = saleBS.calcPayBalance();
        txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay,payObj.paymode));
        
        return ret;
    }

    void enterInput(KeyEvent e)
    {
        if (e.widget == txtAccount)
        {
        	msrRead(e,"",txtAccount.getText(),"");
        }
        else if (e.widget == txtMoney)
        {
        	NewKeyListener.sendKey(GlobalVar.Exit);
        }
        else if (e.widget == newEditor)
        {
            if (currentPoint[0] < table.getItemCount())
            {
                Text text = (Text) tEditor.getEditor();

                if (!text.getText().trim().equals(""))
                {
                    if (!setPayment(text)) 
                    {       
                    	text.selectAll();
                    	text.setFocus();
                    	return;
                    }
                    table.getItem(currentPoint[0]).setText(2, text.getText());
                }
                else
                {
                    tEditor.getItem().setText(currentPoint[1], "0.00");
                }

                // 付款不足,跳到下一个券种输入
                double je = saleBS.calcPayBalance();
                if (currentPoint[0] < (table.getItemCount() - 1) && je > 0)
                {
                    currentPoint[0] = currentPoint[0] + 1;

                    table.setSelection(currentPoint[0]);
                    findLocation();
                }
                else
                {
                    shell.close();
                    shell.dispose();
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

        // 按选中余额进行提示描述
        txtStatus.setText(payObj.getDisplayStatusInfo(currentPoint[0]));
        
        newEditor = new Text(table, SWT.NONE | SWT.RIGHT);
        newEditor.setTextLimit(15);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        String je = table.getItem(currentPoint[0]).getText(currentPoint[1]);
        if (Convert.toDouble(je) == 0)
        {
        	if (payObj.allowpayje > 0)
        	{
        		je = String.valueOf(Math.min(Math.abs(payObj.allowpayje), Math.abs(Convert.toDouble(txtMoney.getText())))); 
        	}
        }
        newEditor.setText(je);
        newEditor.setFocus();
        tEditor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.selectAll();

        keyNewEditor.inputMode = keyNewEditor.DoubleInput;

        newEditor.addKeyListener(keyNewEditor);
    }

    // 设置余额
    private boolean setYeShow()
    {
        return payObj.setYeShow(table);
    }
}
