package com.efuture.javaPos.UI;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.UI.Design.CouponQueryInfoForm;

public class CouponQueryInfoEvent {
	public PosTable table_1;
	public PosTable table;
	public StyledText styledText;
	public Text txt_cardno;
	public CouponQueryInfoBS coupon = null;
	public Shell shell = null;
	NewKeyListener key = null;
	
	public CouponQueryInfoEvent(CouponQueryInfoForm form)
	{
		table_1 = form.table_1;
		table   = form.table;
		styledText = form.styledText;
		txt_cardno = form.txt_cardno;
		coupon = CustomLocalize.getDefault().createCouponQueryInfoBS();
		shell = form.shell;
		//设定键盘事件
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
	            
	            public void msrFinish(KeyEvent e, String track1, String track2,
                        String track3)
	            {
	            	msrRead(e, track1, track2, track3);
	            }
	     };
	     
	     key = new NewKeyListener();
	     key.event = event;
	     key.inputMode = CreatePayment.getDefault().getPaymentCoupon().getAccountInputMode();
	     txt_cardno.setData("MSRINPUT");
	     txt_cardno.addKeyListener(key);
	}
	
	public void keyPressed(KeyEvent e,int key)
	{
		switch(key)
		{
			case GlobalVar.ArrowDown:
				table.moveDown();
				break;
			case GlobalVar.ArrowUp:
				table.moveUp();
				break;
			case GlobalVar.PageDown:
				table_1.moveDown();
				break;
			case GlobalVar.PageUp:
				table_1.moveUp();
				break;
		}
	}
	
    public void keyReleased(KeyEvent e, int key)
    {
    	if (key == GlobalVar.Exit)
    	{
    		shell.close();
    		shell.dispose();
    	}
    	else if (key == GlobalVar.MemberGrant)
    	{
        	//刷面值卡
        	TextBox txt = new TextBox();
    		StringBuffer cardno = new StringBuffer();
            if (!txt.open(Language.apply("请刷返券卡"), Language.apply("返券卡"), Language.apply("请将返券卡从刷卡槽刷入"), cardno, 0, 0,false, CreatePayment.getDefault().getPaymentCoupon().getAccountInputMode()))
            {
                return;
            }
            
	        // 得到磁道信息
	        String track1 = txt.Track1;
	        String track2 = txt.Track2;
	        String track3 = txt.Track3;
	        msrRead(null,track1,track2,track3);
    	}
    	else if ( key == GlobalVar.Pay)
    	{
    		coupon.print(false);
    	}
    	else if ( key == GlobalVar.Print)
    	{
    		coupon.print(true);
    	}
    }
    
    public void msrRead(KeyEvent e, String track1, String track2, String track3)
    {
    	styledText.setText("");
    	table.removeAll();
    	table_1.removeAll();
    	if (coupon.findHYK(track1, track2, track3))
    	{
    		// 显示会员卡
    		txt_cardno.setText(coupon.getCardno());
    		
    		key.inputMode = TextBox.IntegerInput;
    		// 显示卡信息
    		coupon.displayBaseInfo(styledText);
    		
    		// 显示券余额
    		coupon.displayCouponValue(table);
    		
    		// 显示券明细
    		coupon.displayCouponDetail(table_1);
    	}
    }
    
    public void afterFormOpenDoEvent()
    {
    	coupon.choicFjkType();
    	coupon.specialDeal(this);
    }
}
