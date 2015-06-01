package com.efuture.javaPos.UI;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.UI.Design.BankLogQueryForm;

public class BankLogQueryEvent 
{
	private Text txtReturnCode = null;
	private Text txtOldTrace = null;	
	private Text txtLodTime = null;
	private Text txtBank = null;
	private Text txtTrace = null;
	private Text txtReturnMsg = null;
	private Text txtMoney = null;
	private Text txtCardCode = null;
	private Text txtType = null;
	private Text txtSyyCode = null;
	private Text txtDate = null;
	private Combo cmbDjlb = null;
	
	private Text txtSyjCode = null;
	private PosTable tabBankCard = null;
	private Shell shell = null;
	
	private BankLogQueryBS bcqbs = null;
	private Text focus = null;
	private int currow = -1;
	
	public BankLogQueryEvent(BankLogQueryForm bcqf)
	{
		this.txtReturnCode = bcqf.getTxtReturnCode();
		this.txtOldTrace = bcqf.getTxtOldTrace();
		this.txtLodTime = bcqf.getTxtLodTime();
		this.txtBank = bcqf.getTxtBank();
		this.txtTrace = bcqf.getTxtTrace();
		this.txtReturnMsg = bcqf.getTxtReturnMsg();
		this.txtMoney = bcqf.getTxtMoney();
		this.txtCardCode = bcqf.getTxtCardCode();
		this.txtType = bcqf.getTxtType();
		this.txtSyyCode = bcqf.getTxtSyyCode();
		this.txtSyjCode = bcqf.getTxtSyjCode();
		this.tabBankCard = bcqf.getTabBankCard();
		this.txtDate	= bcqf.getTxtDate();
		cmbDjlb	= bcqf.getCmbDjlb();
		this.shell = bcqf.getShell();
		
		bcqbs = CustomLocalize.getDefault().createBankCardQueryBS();
		
		// 显示功能提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("'确认键'发送金卡日志，'打印键'重印签购单"));
		
		// 设定键盘事件
        NewKeyEvent event = new NewKeyEvent()
	    {
	            public void keyDown(KeyEvent e,int key)
	            {
	            	keyPressed(e,key);
	            }
	
	            public void keyUp(KeyEvent e,int key)
	            {
	            	keyReleased(e,key);
	            }
	     };
	     
	     NewKeyListener key = new NewKeyListener();
	     key.event = event;
	     
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
	        
	     txtDate.addKeyListener(key);
	     txtDate.addFocusListener(listener);
	     setFocus(txtDate);
	     txtDate.setText(new ManipulateDateTime().getDateByEmpty());
	     txtDate.selectAll();
	     cmbDjlb.addKeyListener(key);
	     cmbDjlb.select(0);
	     key.inputMode = key.IntegerInput;
	     
	     tabBankCard.addKeyListener(key);
	     tabBankCard.addFocusListener(listener);

	     txtReturnCode.addFocusListener(listener);
	     txtOldTrace.addFocusListener(listener);
	     txtLodTime.addFocusListener(listener);
	     txtBank.addFocusListener(listener);
	     txtTrace.addFocusListener(listener);
	     txtReturnMsg.addFocusListener(listener);
	     txtMoney.addFocusListener(listener);
	     txtCardCode.addFocusListener(listener);
	     txtType.addFocusListener(listener);
	     txtSyyCode.addFocusListener(listener);
	     txtSyjCode.addFocusListener(listener);
	     
	     init();
	}
	
	private void init()
	{
		if (bcqbs.init(this))
		{
			tabBankCard.setSelection(0);
		}
	}
	
	public void keyPressed(KeyEvent e,int key)
    {
		switch(key)
		{
			case GlobalVar.ArrowDown:

				if (e.getSource() == txtDate)
	            {
					int old = currow;
					tabBankCard.moveDown();
					currow = tabBankCard.getSelectionIndex();
					if (currow != old && currow != -1)
					{
						TableItem tableItem = tabBankCard.getItem(currow);
	    				bcqbs.getBankCardInfo(tableItem.getText(0),this);
					}
	            }
				break;
    		case GlobalVar.ArrowUp:
    			if (e.getSource() == txtDate)
	            {
					int old = currow;
					tabBankCard.moveUp();
					currow = tabBankCard.getSelectionIndex();
					if (currow != old && currow != -1)
	                {
	                    TableItem tableItem = tabBankCard.getItem(currow);
				 		bcqbs.getBankCardInfo(tableItem.getText(0),this);
	                }
	            }
    			break;
		}
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	switch(key)
		{
			case GlobalVar.PageUp:
				if (e.getSource() == txtDate)
                {
					int old = currow;
					tabBankCard.PageUp();
					currow = tabBankCard.getSelectionIndex();
					if (currow != old && currow != -1)
					{
						TableItem tableItem = tabBankCard.getItem(currow);
    	  				bcqbs.getBankCardInfo(tableItem.getText(0),this);
					}
                }
				break;
			case GlobalVar.PageDown:
				if (e.getSource() == txtDate)
                {
					int old = currow;
					tabBankCard.PageDown();
					currow = tabBankCard.getSelectionIndex();
					if (currow != old && currow != -1)
					{
						TableItem tableItem = tabBankCard.getItem(currow);
    	  				bcqbs.getBankCardInfo(tableItem.getText(0),this);
					}
                }
				
				break;	
        	case GlobalVar.Pay:
        		if (e.getSource() == txtDate)
        		{
        			e.data = "focus";
        			cmbDjlb.setFocus();
        		}
        		else if (e.getSource() == cmbDjlb)
        		{
        			e.data = "focus";
        			txtDate.setFocus();
        		}
        		break;				
			case GlobalVar.Enter:
				currow = 0;
				if (!isValidate(txtDate.getText()))
	            {
	                txtDate.selectAll();
	                tabBankCard.removeAll();
	
	                return;
	            }
				
				if (!bcqbs.init(this))
				{
					txtDate.selectAll();
					return ;
				}
				
				txtDate.selectAll();
	            e.data = "focus";
	            txtDate.setFocus();
	            
	            //
	            tabBankCard.setSelection(0);
	            break;	
			case GlobalVar.Print:
				if (tabBankCard.getItemCount() > 0 && currow >= 0 && (currow <= (tabBankCard.getItemCount() - 1)))
            	{
					TableItem tableItem = tabBankCard.getItem(currow);
					bcqbs.printAgainBankCardInfo(tableItem.getText(0),this);
            	}
				
				break;
			case GlobalVar.Validation:
				if (tabBankCard.getItemCount() > 0 && currow >= 0 && (currow <= (tabBankCard.getItemCount() - 1)))
            	{
					TableItem tableItem = tabBankCard.getItem(currow);
				
					String keytext = txtDate.getText() + "," + txtDate.getText()  + "," + tableItem.getText(0).substring(1).trim(); 
					
					if (tableItem.getText(0).charAt(0) == ' ')
                	{
						if (TaskExecute.getDefault().sendBankLog(keytext))
						{
							if (bcqbs.init(this))
							{
								
							}
						}
                	}
					else
					{
	            		int selkey = new MessageBox(Language.apply("该金卡日志已经上传过了!\n\n1 - 重传当前金卡日志\n2 - 重传所有金卡日志"),null,false).verify();
	            		if (selkey == GlobalVar.Key1 || selkey == GlobalVar.Key2)
        				{
	            			if (selkey == GlobalVar.Key2) keytext = keytext.substring(0,keytext.lastIndexOf(','));
							if (TaskExecute.getDefault().sendAllAgainData(StatusType.TASK_SENDBANKLOG, keytext))
							{
								if (bcqbs.init(this))
								{
									
								}
							}
        				}
					}
					
					tabBankCard.setSelection(currow);
            	}
				break;	
    		case GlobalVar.Exit:
    			shell.close();
				shell.dispose();
				shell = null;
				break;	
		}
    	
    }
    
    private void setFocus(Text focus)
    {
        this.focus = focus;
        focus.setFocus();
    }
    
	public Text getTxtReturnCode()
	{
		return txtReturnCode;
	}
	
	public Text getTxtOldTrace()
	{
		return txtOldTrace;
	}
	
	public Text getTxtLodTime()
	{
		return txtLodTime;
	}
		
	public Text getTxtBank()
	{
		return txtBank;
	}
	
	public Text getTxtTrace()
	{
		return txtTrace;
	}
	
	public Text getTxtReturnMsg()
	{
		return txtReturnMsg;
	}
	
	public Text getTxtMoney()
	{
		return txtMoney;
	}
	
	public Text getTxtCardCode()
	{
		return txtCardCode;
	}
	
	public Text getTxtType()
	{
		return txtType;
	}
	
	public Text getTxtSyyCode()
	{
		return txtSyyCode;
	}
	
	public Text getTxtSyjCode()
	{
		return txtSyjCode;
	}
	
	public Table getTabBankCard()
	{
		return tabBankCard ;
	}
	
	public Combo getCmbDjlb()
	{
		return cmbDjlb;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	public boolean isValidate(String date)
    {
        if ((txtDate.getText() == null) || txtDate.getText().equals(""))
        {
            new MessageBox(Language.apply("日期不能为空,请重新输入!"), null, false);

            return false;
        }

        if (txtDate.getText().length() < 8)
        {
            new MessageBox(Language.apply("不合法的日期输入,请检查是否有8位长\n请重新输入!"), null, false);

            return false;
        }

        return true;
    }
}
