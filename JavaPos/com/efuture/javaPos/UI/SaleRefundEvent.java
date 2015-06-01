package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.PosTable.NewSelectionAdapter;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MenuFuncForm;
import com.efuture.javaPos.UI.Design.SalePayForm;


public class SaleRefundEvent
{
    protected PosTable table = null;
    protected PosTable table1 = null;
    protected SaleBS saleBS = null;
    protected Text txt = null;
    protected Shell shell = null;
    protected Label lbl_ysje = null;
    protected Label payReqFee = null;
    protected Label unpayfee = null;
    protected Label lbl_money = null;
    protected boolean ShellIsDisposed = false;

    public void mouseModeInit()
    {
        table.setFocusedControl(txt);
        table1.setFocusedControl(txt);
        
        table.addNewSelectionListener(new NewSelectionAdapter()
        {
        	public void widgetSelected(int oldindex,int index)
        	{
        		RowSelected(index);
        	}
        });
        
        table.addMouseListener(new MouseAdapter()
        {
			public void mouseDoubleClick(MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Enter);
			}

			public void mouseDown(MouseEvent arg0)
			{
				txt.setFocus();
			}
        });
        
        table1.addMouseListener(new MouseAdapter()
        {
			public void mouseDoubleClick(MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Del);
			}

			public void mouseDown(MouseEvent arg0)
			{
				txt.setFocus();
			}
        });
    }
    
    public SaleRefundEvent(SaleBS saleBS, SalePayForm pay)
    {
        this.table    = pay.table;
        this.table1   = pay.table1;
        this.txt      = pay.text;
        this.shell    = pay.shell;
        this.lbl_ysje = pay.lbl_ysje;
        this.saleBS   = saleBS;
        this.payReqFee = pay.payReqFee;
        this.unpayfee  = pay.unpayfee;
        this.lbl_money = pay.lbl_money;

        mouseModeInit();
        
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
        shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1,
                         (GlobalVar.rec.y - shell.getSize().y) / 2,
                         shell.getSize().x,
                         shell.getSize().y - GlobalVar.heightPL);
        shell.setActive();
        txt.setFocus();

        // 初始化
        initPayment();
    }

    public void initPayment()
    {
        // 根据付款类型显示应付金额提示
        lbl_ysje.setText(Language.apply("扣回金额:"));

        // 显示应付款和剩余款
        payReqFee.setText(saleBS.getRefundPayMoneyLabel());
        unpayfee.setText(saleBS.getRefundBalanceLabel());

        // 显示付款方式列表,初始化只显示主付款方式,主付款方式的上级代码为0
        showPayModeBySuper("0");

        // 显示已付款列表
        table1.exchangeContent(saleBS.getSaleRefundDisplay());
        table1.assignLast();

        calcPayResult();
    }

    public void showPayModeBySuper(String code)
    {
        // 刷新付款列表
        table.exchangeContent(saleBS.getPayModeByRefund(code));
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

    public void RowSelected(int index)
    {
        // 得到当前付款代码
        String[] ax = table.changeItemVar(index);
        String paycode = ax[0];

        // 得到付款方式
        PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);
        
        saleBS.setRefundMoneyInputDefault(txt, paymode);

        // 主付款显示付款名
        if (paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code) || paymode.isbank == 'N')
        {
            lbl_money.setText(ax[1]);
        }
        else
        {
            lbl_money.setText(Language.apply("付款码"));
        }
        
        txt.setFocus();
    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        if (ShellIsDisposed)
        {
            return;
        }

        // 得到当前付款方式
        int index = table.getSelectionIndex();
        if (index == -1)
        {
        	table.setSelection(0);
        	index = 0;
        }

        // 得到当前付款代码
        String[] ax = table.changeItemVar(index);
        String paycode = ax[0];

        // 得到付款方式
        PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);

        switch (key)
        {
            case GlobalVar.ArrowUp:
            case GlobalVar.ArrowDown:
            {
            	RowSelected(index);
                break;
            }

            case GlobalVar.Del:
                deletePay();

                break;
            case GlobalVar.Validation:
            	calcPayResult(true);
            	
            	break;

            case GlobalVar.Enter:
                payEnter(paymode);

                break;

            case GlobalVar.Pay:
            	paySelect();
            	break;
            	
            case GlobalVar.PayBank: //银联卡付款键
            case GlobalVar.PayCash: //现金付款键
            case GlobalVar.PayCheque: //支票付款键
            case GlobalVar.PayCredit: //信用卡付款键
            case GlobalVar.PayMzk: //面值卡付款键
            case GlobalVar.PayGift: //礼券付款键
            case GlobalVar.PayTally: //赊账付款键
            {
                int last = saleBS.payButtonToPayModePosition(key);

                if (last >= 0)
                {
                    gotoPayModeLocation(last);
                }

                break;
            }

            case GlobalVar.Exit:
                payExit(paymode);

                break;

            case GlobalVar.CustomKey0:
            case GlobalVar.CustomKey1:
            case GlobalVar.CustomKey2:
            case GlobalVar.CustomKey3:
            case GlobalVar.CustomKey4:
            case GlobalVar.CustomKey5:
            case GlobalVar.CustomKey6:
            case GlobalVar.CustomKey7:
            case GlobalVar.CustomKey8:
            case GlobalVar.CustomKey9:
                customKeyInput(key);

                break;
                
            case GlobalVar.MainList:
                showFuncMenu();
                break;
        }
    }

    public void showFuncMenu()
    {
    	String func = saleBS.getFuncMenuByPaying();
    	
        try
        {
            // 显示功能菜单窗口
            new MenuFuncForm(shell,func,false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开功能菜单时发生异常\n\n") + ex.getMessage());
        }
    }

    public void customKeyInput(int key)
    {
        try
        {
            switch (key)
            {
                case GlobalVar.CustomKey0:
                    saleBS.execCustomKey0(false);

                    break;

                case GlobalVar.CustomKey1:
                    saleBS.execCustomKey1(false);

                    break;

                case GlobalVar.CustomKey2:
                    saleBS.execCustomKey2(false);

                    break;

                case GlobalVar.CustomKey3:
                    saleBS.execCustomKey3(false);

                    break;

                case GlobalVar.CustomKey4:
                    saleBS.execCustomKey4(false);

                    break;

                case GlobalVar.CustomKey5:
                    saleBS.execCustomKey5(false);

                    break;

                case GlobalVar.CustomKey6:
                    saleBS.execCustomKey6(false);

                    break;

                case GlobalVar.CustomKey7:
                    saleBS.execCustomKey7(false);

                    break;

                case GlobalVar.CustomKey8:
                    saleBS.execCustomKey8(false);

                    break;

                case GlobalVar.CustomKey9:
                    saleBS.execCustomKey9(false);

                    break;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("自定义功能时发生异常\n\n") + ex.getMessage());
        }
    }

    public void paySelect()
    {
    	// 输入付款代码
    	StringBuffer buffer = new StringBuffer();
        if (!new TextBox().open(Language.apply("请输入付款代码或顺序号"), Language.apply("付款代码"), Language.apply("请输入付款代码以便快速使用该付款方式进行付款"), buffer, 0, 0, false))
        {
            return;
        }
        
        // 查找付款方式并定位
    	PayModeDef paymode = null;
    	String s = buffer.toString().trim();
    	int k;
		for (k = 0;k < GlobalInfo.payMode.size();k++)
		{
			paymode = (PayModeDef)(GlobalInfo.payMode.elementAt(k));

			if (s.equals(paymode.code)) break;
		}
		if (k < GlobalInfo.payMode.size())
		{
			gotoPayModeLocation(k);
		}
		else
		{
			// 顺序号定位
			int pos = Convert.toInt(s) - 1;
			if (pos >= 0 && pos < table.getItemCount())
			{
	            // 选中付款方式,如果不是直接付款的付款方式，立即回车进入详细付款界面
				String paycode = table.changeItemVar(pos)[0];
		        paymode = DataService.getDefault().searchPayMode(paycode);
				table.setSelection(pos);
	            keyReleased(null, GlobalVar.ArrowUp);
	            if (!CreatePayment.getDefault().allowQuickInputMoney(paymode))
	            {
	                keyReleased(null, GlobalVar.Enter);
	            }
			}
		}
    }
    
    public void gotoPayModeLocation(int pos)
    {
        PayModeDef modeDef = (PayModeDef) GlobalInfo.payMode.elementAt(pos);
        StringBuffer buffer = new StringBuffer();

    	// 设置付款列表
    	Vector v = saleBS.getPayModeByRefund(modeDef.sjcode, buffer,modeDef.code);

    	// 定位付款方式
        if (buffer.length() > 0)
        {
        	table.exchangeContent(v);
            table.setSelection(Integer.parseInt(buffer.toString()));
            
            // 选中付款方式
            keyReleased(null, GlobalVar.ArrowUp);

            // 如果不是直接付款的付款方式，立即回车进入详细付款界面
            if (!CreatePayment.getDefault().allowQuickInputMoney(modeDef))
            {
                keyReleased(null, GlobalVar.Enter);
            }
        }        
    }

    public void close()
    {
        ShellIsDisposed = true;
        
        //
        shell.close();
        shell.dispose();
    }

    public boolean deletePay()
    {
        int index = table1.getSelectionIndex();

        if (index < 0)
        {
            return false;
        }

        String[] ax = table1.changeItemVar(index);
        String msg = ax[0] + Language.apply(" 付款 ") + ax[2];

        if (new MessageBox(Language.apply("你确定要删除此以下付款吗?\n\n") + msg, null, true).verify() == GlobalVar.Key1)
        {
            // 删除付款方式
            if (saleBS.deleteRefundPay(index))
            {
                table1.deleteRow(index);
                table1.assignLast();

                // 刷新一次金额输入框的初始值 
                keyReleased(null, GlobalVar.ArrowUp);

                // 重算付款
                calcPayResult();

                return true;
            }
        }

        return false;
    }
    
    public boolean calcPayResult()
    {
    	return calcPayResult(false);
    }

    public boolean calcPayResult(boolean permission)
    {
        // 显示余额
        unpayfee.setText(saleBS.getRefundBalanceLabel());

        // 刷新界面显示
        Display.getCurrent().update();

        // 付款足够,完成付款
        if (saleBS.calcRefundBalance() <= 0 || (permission && saleBS.checkKh()))
        {
        	ShellIsDisposed = true;
        	
            if (saleBS.refundComplete())
            {
                close();

                return true;
            }
            else
            {
            	ShellIsDisposed = false;
            }
        }

        return false;
    }

    public boolean checkPayCodeNumberEquals(String txt, String code)
    {
        try
        {
            if (Integer.parseInt(txt) == Integer.parseInt(code))
            {
                return true;
            }
        }
        catch (Exception ex)
        {
        }

        return false;
    }

    public void payEnter(PayModeDef paymode)
    {
        // 非主付款方式,TXT输入的是付款代码    	
        if (!(paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code)) && (txt.getText().trim().length() > 0))
        {
            String paycode = txt.getText().trim();

            // 
            txt.setText("");

            // 查找当前付款代码
            for (int i = 0; i < table.getItemCount(); i++)
            {
                String[] ax = table.changeItemVar(i);

                if (paycode.equals(ax[0]) ||
                    checkPayCodeNumberEquals(paycode, ax[0]))
                {
                    table.setSelection(i);
                    keyReleased(null, GlobalVar.Enter);
                    return;
                }
            }
			
            // 顺序号定位
			int pos = Convert.toInt(paycode) - 1;
			if (pos >= 0 && pos < table.getItemCount())
			{
                table.setSelection(pos);
                keyReleased(null, GlobalVar.Enter);
                return;
			}
			
            return;
        }

        // 付款不足或是已存在的付款方式,进行付款记账
        if ((saleBS.calcRefundBalance() > 0) ||
             (GlobalInfo.sysPara.payover == 'Y' && saleBS.existRefund(paymode.code, "",true) >= 0))
        {
            String money = txt.getText().trim();

            // 检查下级付款方式个数
            int submode = 0;
            if (paymode.ismj != 'Y')
            {
            	submode = saleBS.getPayModeByRefund(paymode.code).size();
            }
            
            // 末级付款或没有下级付款方式,则记账;非末级付款进入下级付款
            if (paymode.ismj == 'Y' || submode == 0)
            {
                // 付款记账
                if (saleBS.refundAccount(paymode, money))
                {
                    // 刷新已付款列表
                    Vector vector = saleBS.getSaleRefundDisplay();
                    table1.exchangeContent(vector);
                    table1.assignLast();

                    // 如果是覆盖模式下的直接付款的付款方式，跳到下一个付款方式，否则停留在当前付款位置并刷新
                    if (CreatePayment.getDefault().allowQuickInputMoney(paymode) &&
                    	GlobalInfo.sysPara.payover == 'Y')
                    {
                    	keyPressed(null, GlobalVar.ArrowDown);
                    	keyReleased(null, GlobalVar.ArrowDown);
                    }
                    else
                    {
                        // 刷新一次金额输入框的初始值 
                        keyReleased(null, GlobalVar.ArrowUp);
                    }
                }
                else
                {
                    txt.selectAll();
                }
            }
            else
            {
                showPayModeBySuper(paymode.code);
                
                // 判断此时付款方式是否为直接付款，如果为直接付款，不直接显示明细窗口
                String[] ax = table.changeItemVar(0);
                String paycode = ax[0];
                PayModeDef paymode1 = DataService.getDefault().searchPayMode(paycode);
                if (paymode1.isbank != 'N' && submode == 1)
                {
                	keyReleased(null, GlobalVar.Enter);
                	return;
                }
            }
        }

        // 检查付款
        calcPayResult();
    }

    public void payExit(PayModeDef paymode)
    {
        // 非主付款方式,返回上一级付款列表
        if (!(paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code)))
        {
            // 找到上级付款方式
            PayModeDef pay = DataService.getDefault()
                                        .searchPayMode(paymode.sjcode);

            if (pay != null)
            {
                // 显示再上级付款的下级,也就是本级的上级
                showPayModeBySuper(pay.sjcode);
            }
        }
        else
        {
            // 退出付款界面
            if (saleBS.exitRefundSell())
            {
                close();
            }
            else
            {
                // 刷新已付款列表
                Vector vector = saleBS.getSaleRefundDisplay();
                table1.exchangeContent(vector);
                table1.assignLast();
            }
        }
    }
}
