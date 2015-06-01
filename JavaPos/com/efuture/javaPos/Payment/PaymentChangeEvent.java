package com.efuture.javaPos.Payment;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.PayModeDef;


public class PaymentChangeEvent
{
    private PosTable table = null;
    private PosTable table1 = null;
    private PaymentChange chgBS = null;
    private Text txt = null;
    private Shell shell = null;
    private Label payReqFee = null;
    private Label unpayfee = null;
    private Label lbl_money = null;
    private PaymentChangeForm chgForm = null;
    
    public PaymentChangeEvent(PaymentChange paychg, PaymentChangeForm chg)
    {
        this.table     = chg.table;
        this.table1    = chg.table1;
        this.txt       = chg.text;
        this.shell     = chg.shell;
        this.chgBS    = paychg;
        this.payReqFee = chg.payReqFee;
        this.unpayfee  = chg.unpayfee;
        this.lbl_money = chg.lbl_money;
        this.chgForm = chg;
        
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
        key.event     = event;
        key.inputMode = key.DoubleInput;
        key.isControl = true;

        txt.addKeyListener(key);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2+1,
                        (GlobalVar.rec.y - shell.getSize().y) / 2,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
        shell.setActive();
        txt.setFocus();

        // 初始化
        initPayment();
    }

    public void initPayment()
    {
        // 显示应付款和剩余款
        payReqFee.setText(ManipulatePrecision.doubleToString(chgBS.getChangeTotal()));
        unpayfee.setText(ManipulatePrecision.doubleToString(chgBS.getChangeBalance()));
        
    	// 显示付款方式列表,初始化只显示主付款方式,主付款方式的上级代码为0
        showChangePayMode();
        
    	// 显示已付款列表
        table1.exchangeContent(chgBS.getChangePaymentDisplay());
        table1.assignLast();
    }

    public void showChangePayMode()
    {
    	// 刷新付款列表
        table.exchangeContent(chgBS.getChangePayMode());
        table.setSelection(0);
        
        // 刷新一次金额输入框的初始值 
        keyReleased(null, GlobalVar.ArrowUp);
    }

    public void keyPressed(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.ArrowUp:
                table.moveUp();

                break;

            case GlobalVar.ArrowDown:
                table.moveDown();

                break;

            case GlobalVar.PageUp:
                table1.moveUp();

                break;

            case GlobalVar.PageDown:
                table1.moveDown();

                break;
        }
    }

    public void keyReleased(KeyEvent e, int key)
    {
        // 得到当前付款方式
        int index = table.getSelectionIndex();
        if (index == -1) return;
        
        // 得到当前付款代码
        String[] ax = table.changeItemVar(index);
        String paycode = ax[0];
        
        switch (key)
        {
            case GlobalVar.ArrowUp:
            	chgBS.setMoneyInputDefault(txt, paycode);
                lbl_money.setText(ax[1]);
                
                break;

            case GlobalVar.ArrowDown:
            	chgBS.setMoneyInputDefault(txt, paycode);
                lbl_money.setText(ax[1]);
                
                break;

            case GlobalVar.Del:
            	deleteChg();
                break;

            case GlobalVar.Enter:
            	chgEnter(paycode);
 
                break;
            	
            case GlobalVar.Exit:
            	chgExit(paycode);

                break;

            default:
                break;
        }
    }

    public void close()
    {
        shell.close();
        shell.dispose();
    }
    
    public void deleteChg()
    {
        int index = table1.getSelectionIndex();
        if (index < 0) return;

        String[] ax = table1.changeItemVar(index);
        String msg = ax[0] + Language.apply(" 找零 ") + ax[1];

        if (new MessageBox(Language.apply("你确定要删除此以下找零吗?\n\n") + msg, null, true).verify() == GlobalVar.Key1)
        {
        	// 删除付款方式
        	if (chgBS.deleteChange(index))
	        {
	            table1.deleteRow(index);
	            table1.assignLast();

	            // 刷新一次金额输入框的初始值 
	        	keyReleased(null,GlobalVar.ArrowUp);
	            
	        	// 重算付款
	        	calcChangResult();
	        }
        }
    }
    
    public boolean calcChangResult()
    {
        // 显示余额
    	unpayfee.setText(ManipulatePrecision.doubleToString(chgBS.getChangeBalance()));
        
        // 刷新界面显示
        Display.getCurrent().update();
        
        // 找零足够,完成找零
        if (chgBS.getChangeBalance() <= 0)
        {
        	chgForm.setDone(true);

        	close();
            
            return true;
        }
        
        return false;
    }
    
    public void chgEnter(String paycode)
    {
    	// 付款不足或是已存在的付款方式,进行付款记账
    	if (chgBS.getChangeBalance() > 0 ||
    		(GlobalInfo.sysPara.payover == 'Y' && chgBS.existChange(paycode,"") >= 0))
    	{
	    	PayModeDef mode = DataService.getDefault().searchPayMode(paycode);
	    	String money = txt.getText();
	    	
        	// 找零记账
        	if (chgBS.chgAccount(mode,money))
        	{
            	// 刷新已付款列表
                Vector vector = chgBS.getChangePaymentDisplay();
                table1.exchangeContent(vector);
                table1.assignLast();
                
                // 跳到下一个付款方式
                keyPressed(null,GlobalVar.ArrowDown);
                keyReleased(null,GlobalVar.ArrowDown);
        	}
        	else
        	{
        		txt.selectAll();
        	}
        }

    	// 检查找零结果
        calcChangResult();
    }
    
    public void chgExit(String paycode)
    {
    	// 退出找零界面
    	chgBS.clearChange();
    	
		close();
    }
}
