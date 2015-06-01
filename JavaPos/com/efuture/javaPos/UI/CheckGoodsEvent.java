package com.efuture.javaPos.UI;


import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.Design.CheckGoodsForm;

public class CheckGoodsEvent 
{
    private Text txtGz = null;
    private Text txtRq = null;
    private Combo comboCw = null;
    private Shell sShell = null;
    CheckGoodsForm cf = null;
    private int lastcomboCw = -1;
    private SaleBS saleBS = null;
    
    public CheckGoodsEvent(CheckGoodsForm cf)
    {
        this.cf    = cf;
        this.txtGz = cf.txtGz;
        this.txtRq  = cf.txtRq;
        this.comboCw  = cf.comboCw;
        this.sShell     = cf.sShell;
        this.saleBS = cf.saleBS;
        
        // 初始化盘点列表
        InitCombo();

        // 设定键盘事件
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
        key.event = event;
        key.inputMode = key.IntegerInput;
        
        txtGz.addKeyListener(key);
        txtRq.addKeyListener(key);
        comboCw.addKeyListener(key);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        sShell.setBounds((GlobalVar.rec.x - sShell.getSize().x) / 2,
                         (GlobalVar.rec.y - sShell.getSize().y) / 2,
                         sShell.getSize().x,
                         sShell.getSize().y - GlobalVar.heightPL);
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Exit:
            {
            	this.saleBS.checkgz = "";
            	this.saleBS.checkrq = "";
            	this.saleBS.checkcw = "";
            	cf.isExit = 'Y';
	            sShell.close();
	            sShell.dispose();
	            break;
            }
            
            case GlobalVar.ArrowUp:
            {
            	if (e.widget.equals(txtRq))
            	{
                    e.data = "focus";
                    txtGz.setFocus();
                    txtGz.selectAll();
                    break;
            	}

            	if (e.widget.equals(comboCw))
            	{
                	if (!(comboCw.getSelectionIndex() == 0 && lastcomboCw == 0))
                	{
                		lastcomboCw = comboCw.getSelectionIndex();

                		break;
                	}
                    e.data = "focus";
                    txtRq.setFocus();
                	break;
            	}
                e.data = "focus";
                txtGz.setFocus();
                txtGz.selectAll();
                break;
            }
            
            case GlobalVar.Enter:
            {
                if (e.widget.equals(txtGz))
                {
                    if ((txtGz.getText().length() > 0) || GlobalInfo.sysPara.ischeckgz != 'Y')
                    {
                        e.data = "focus";
                        txtRq.setFocus();
                    }
                    else
                    {
                    	txtGz.selectAll();
                    }
                }
                else if (e.widget.equals(txtRq))
                {
                	if (validitCheckDate(txtRq.getText()))
                	{
                        e.data = "focus";
                        comboCw.setFocus();
                	}
                }
                else if (e.widget.equals(comboCw))
                {
                	this.saleBS.checkgz = txtGz.getText().trim();
                	this.saleBS.checkrq = txtRq.getText().trim();
                	this.saleBS.checkcw = getCwCode(comboCw.getText());
                    sShell.close();
                    sShell.dispose();
                }

                break;
            }
        }
    }
    
    public void InitCombo()
    {
    	int assignCw = 0;
    	String[] cw = null;
    	if (GlobalInfo.sysPara.checkGoodsCw.indexOf(",") > -1)
    		cw = GlobalInfo.sysPara.checkGoodsCw.split(",");
    	else
    		cw = new String[]{GlobalInfo.sysPara.checkGoodsCw};
		comboCw.setItems(cw);
		comboCw.select(assignCw);
    }
    
    public String getCwCode(String cwText)
    {
    	String cwCode = "";
    	cwCode = cwText.trim().substring(cwText.indexOf('-') + 1, cwText.length());
    	return cwCode;
    }
    
	public boolean validitCheckDate (String date)
	{
		String[] checkDates = GlobalInfo.sysPara.checkGoodsDate.split(",");
		for (int i = 0; i < checkDates.length; i++)
		{
			if (date.trim().equals(checkDates[i].trim()))
			{
				return true;
			}
		}
		return false;
	}
}
