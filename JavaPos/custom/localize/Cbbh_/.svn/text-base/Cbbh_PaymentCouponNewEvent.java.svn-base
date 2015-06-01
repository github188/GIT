package custom.localize.Cbbh;

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
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;


public class Cbbh_PaymentCouponNewEvent
{
	public StyledText txtStatus = null;
	public Text txtMoney = null;
	public Text txtAccount = null;
	public Text focus = null;
	public Label lblPayName = null;
	public Table table = null;
	public TableEditor tEditor = null;
    public Shell shell = null;
    public Text newEditor = null;
    public SaleBS saleBS = null;
    public Cbbh_PaymentCouponNew payObj = null;
    public int[] currentPoint = new int[] { 0, 0 };
    public NewKeyListener keyNewEditor = null;
    public Label account = null;

    public Cbbh_PaymentCouponNewEvent(Cbbh_PaymentCouponNewForm pff, Cbbh_PaymentCouponNew pay, SaleBS sale)
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

        // 原本在afterFormOpenDoEvent，现在改在这里，主要是payObj.getAccountInputMode()返回配置文件里的类型
        payObj.choicFjkType();
        
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

    private void init()
    {

		
    	setYeShow();
    	
    	  
        // 进入付款金额输入
        currentPoint[0] = 0;
        currentPoint[1] = 2;
        table.setFocus();
        table.select(currentPoint[0]);
        findLocation();
        

        txtStatus.setText(payObj.getValidJe(currentPoint[0]));
        
    	
        lblPayName.setText("[" + payObj.paymode.code + "]" +
                           payObj.paymode.name);

        double needPay = saleBS.calcPayBalance();
        
        // 多券种的付款方式里本生就含各个券种的汇率
        txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay,payObj.paymode));
        
        account.setText(payObj.getDisplayAccountInfo());       
        
        payObj.specialDeal(this);
    }

    public void afterFormOpenDoEvent()
    {
    	
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
                        if (!setPayment(text)) return ;
                	}

                    shell.close();
                    shell.dispose();
                    
                    break;

               /* case GlobalVar.Exit:
                    shell.close();
                    shell.dispose();
                    shell = null;

                    break;*/
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
        if (payObj.findFjk(track1, track2, track3) && payObj.initList())
        {
            // 设置余额显示
            if (!setYeShow())
            {
                return;
            }

        	// 显示卡号
            txtAccount.setText(payObj.getDisplayCardno());
            
            // 进入付款金额输入
            if (e!=null)	e.data  = "";
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
    	StringBuffer buff = new StringBuffer();
    	if (!payObj.alreadyAddSalePay)
    		return payObj.CreateNewjPayment(currentPoint[0],Convert.toDouble(text.getText()),buff);
    	else
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
                    if (!setPayment(text)) 
                    {                   	
                    	return;
                    }
                    else
                    {
                    	if (payObj.iscloseShell())
                    	{
                    		shell.close();
                            shell.dispose();
                            return;
                    	}
                    }
                    table.getItem(currentPoint[0]).setText(2, text.getText());
                    table.select(currentPoint[0]);
                }
                else
                {
                    tEditor.getItem().setText(currentPoint[1], "0.00");
                }
                
   			
                txtStatus.setText(payObj.getValidJe(0));
                
                // 计算已付款,检查付款是否已足够
                // 付款还不足,跳到下一个券种输入
                double je =  saleBS.calcPayBalance();
                if (currentPoint[0] < (table.getItemCount() - 1) && je > 0)
                {
                    currentPoint[0] = currentPoint[0] + 1;

                    table.setSelection(currentPoint[0]);
                    findLocation();
                }
                else
                {                	
//                    shell.close();
//                    shell.dispose();
                	newEditor.setFocus();
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

        newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        newEditor.setText(table.getItem(currentPoint[0]).getText(currentPoint[1]));
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
