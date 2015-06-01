package custom.localize.Cjmx;

import java.util.Vector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.CustomerDef;

public class Cjmx_HykSaleInfoEvent {
	public PosTable table_1;
	public StyledText styledText;
	public Cjmx_HykSaleInfoBS chsib = null;
	public Shell shell = null;
	NewKeyListener key = null;
	
	public Cjmx_HykSaleInfoEvent(Cjmx_HykSaleInfoForm form,CustomerDef cust,Vector saleinfo)
	{
		table_1 = form.table_1;
		styledText = form.styledText;
		chsib = new Cjmx_HykSaleInfoBS();
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
	            
//	            public void msrFinish(KeyEvent e, String track1, String track2,
//                        String track3)
//	            {
//	            	msrRead(e, track1, track2, track3);
//	            }
	     };
	     
	     key = new NewKeyListener();
	     key.event = event;
	    // key.inputMode = CreatePayment.getDefault().getPaymentCoupon().getAccountInputMode();
	     chsib.displayBaseInfo(styledText,cust);
	     chsib.displayCouponDetail(table_1,saleinfo);
	   //  txt_cardno.setData("MSRINPUT");
	  //   txt_cardno.addKeyListener(key);
	     //styledText.addKeyListener(key);
	     table_1.addKeyListener(key);
	     table_1.setSelection(0);
	}
	
	public void keyPressed(KeyEvent e,int key)
	{
		switch(key)
		{
			case GlobalVar.ArrowDown:
				table_1.moveDown();
				break;
			case GlobalVar.ArrowUp:
				table_1.moveUp();
				break;
//			case GlobalVar.ArrowLeft:
//				
//				break;
//			case GlobalVar.ArrowRight:
//				
//				break;
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
    	else if ( key == GlobalVar.Print)
    	{
    		//chsib.print(true);
    	}
    }
    /*
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
    */
    public void afterFormOpenDoEvent()
    {
    	//chsib.choicFjkType();
    	//coupon.specialDeal(this);
    }
}
