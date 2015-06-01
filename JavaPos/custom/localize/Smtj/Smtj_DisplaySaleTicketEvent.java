package custom.localize.Smtj;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;

public class Smtj_DisplaySaleTicketEvent
{
	private StyledText txtSpoilageMoney = null;
	private StyledText txtFactInceptMoney = null;
	private StyledText txtAgioMoney = null;
	private StyledText txtGiveChangeMoney = null;
	private StyledText txtShouldInceptMoney = null;
	private StyledText txtGrantCardCode = null;
	private StyledText txtMemberCardCode = null;
	private Table tabTicketDeatilInfo = null;
	private Table tabPay = null;
	private StyledText txtSaleType = null;
	private StyledText txtSyy = null;
	private StyledText txtSaleTime = null;
	private Text txtTicketCode = null;
	private Label lblNet = null;
	private Group group = null;
	private Shell shell = null;
	private DisplaySaleTicketBS dstbs = null;
	private StyledText txt_khje = null;

	private final int BothSwitch = 0;
	private final int DetailSwitch = 1;
	private final int PaySwitch = 2;
	
	// 0-both,1-detail,2-pay
	private int isTableSwitch = BothSwitch;
	
	private boolean isValidation = false;  //是否已经点击确认 
	
	private String xsdate = null;
	private String code = null;
	
	private int type ;
	
	//flaglist代表,是否是销售列表访问;isred代表,是否是红冲
	public Smtj_DisplaySaleTicketEvent(Smtj_DisplaySaleTicketForm dstf,String date,String code,boolean flaglist,int type)
	{
		txtSpoilageMoney = dstf.getTxtSpoilageMoney();
		txtFactInceptMoney = dstf.getTxtFactInceptMoney();
		txtAgioMoney = dstf.getTxtAgioMoney();
		txtGiveChangeMoney = dstf.getTxtGiveChangeMoney();
		txtShouldInceptMoney = dstf.getTxtShouldInceptMoney();
		txtGrantCardCode = dstf.getTxtGrantCardCode();
		txtMemberCardCode = dstf.getTxtMemberCardCode();
		tabTicketDeatilInfo = dstf.getTabTicketDeatilInfo();
		tabPay = dstf.getTabPay();
		txtSaleType = dstf.getTxtSaleType();
		txtSyy = dstf.getTxtSyy();
		txtSaleTime = dstf.getTxtSaleTime();
		txtTicketCode = dstf.getTxtTicketCode();
		lblNet = dstf.getLblNet();
		group= dstf.getGroup();
		shell = dstf.getShell();
		txt_khje = dstf.getKhje();
		this.type = type;
		
		String help_message = "'付款键'切换商品/付款";
		dstbs = CustomLocalize.getDefault().createDisplaySaleTicketBS();
		
		switch (this.type)
		{
			case StatusType.MN_XSCX:
				help_message += ",'打印键'重打小票";
				break;
			case StatusType.MN_MODIFYINVNO:
				help_message += ",'打印键'重打小票";
				help_message += ",'确认键'修改小票号";
				break;	
			case StatusType.MN_SALEHC:
				help_message += ",'确认键'进行红冲";
				break;
			case StatusType.MN_BACKSALE:
				help_message += ",'确认键'进行退货";
				break;
		}
		
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage(help_message);
		
		//
		//tabPay.setLayoutData(tabTicketDeatilInfo.getLayoutData());
		
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
	     };
	     
	     NewKeyListener key = new NewKeyListener();
	     key.event = event;
	     
	     txtTicketCode.setFocus();
	     txtTicketCode.addKeyListener(key);
	     tabTicketDeatilInfo.addKeyListener(key);
	     tabPay.addKeyListener(key);
	     
		 xsdate = date;
	     if(flaglist)
	     {
	    	 dstbs.getSaleAllInfo(xsdate,code,StatusType.MN_XSCX,txtTicketCode, txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet,group,txt_khje);
	    	 txtTicketCode.setText(code);
			 txtTicketCode.selectAll();
			 txtTicketCode.setEnabled(false);
	     }
	     else
	     {
	    	 if (type != StatusType.MN_BACKSALE)
	    	 {
	    		 txtTicketCode.setText(String.valueOf(GlobalInfo.syjStatus.fphm - 1));
	    		 txtTicketCode.selectAll();
	    		 txtTicketCode.setEnabled(false);
	    	 }
	    	 else
	    	 {
	    		 txtTicketCode.setText("");
	    	 }
	    	 
	     }
	}
	
	public void keyPressed(KeyEvent e,int key)
    {
		switch (key)
		{
			case GlobalVar.ArrowUp:
				if(isTableSwitch == BothSwitch || isTableSwitch == DetailSwitch)
				{
					  if (tabTicketDeatilInfo.getSelectionIndex() > 0)
		              {
						  tabTicketDeatilInfo.setSelection(tabTicketDeatilInfo.getSelectionIndex() - 1);
		              }
				}
				else
				{
					  if (tabPay.getSelectionIndex() > 0)
		              {
						  tabPay.setSelection(tabPay.getSelectionIndex() - 1);
		              }
				}
				break;
			case GlobalVar.ArrowDown:
				if(isTableSwitch == BothSwitch || isTableSwitch == DetailSwitch)
				{
					if ((tabTicketDeatilInfo.getSelectionIndex() < (tabTicketDeatilInfo.getItemCount() - 1)) && (tabTicketDeatilInfo.getItemCount() >= 0))
	                {
						tabTicketDeatilInfo.setSelection(tabTicketDeatilInfo.getSelectionIndex() + 1);
	                }
				}
				else
				{
					if ((tabPay.getSelectionIndex() < (tabPay.getItemCount() - 1)) && (tabPay.getItemCount() >= 0))
	                {
						tabPay.setSelection(tabPay.getSelectionIndex() + 1);
	                }
				}
				break;
			case GlobalVar.PageUp:
				if(isTableSwitch == BothSwitch || isTableSwitch == PaySwitch)
				{
					  if (tabPay.getSelectionIndex() > 0)
		              {
						  tabPay.setSelection(tabPay.getSelectionIndex() - 1);
		              }
				}
				else
				{
					  if (tabTicketDeatilInfo.getSelectionIndex() > 0)
		              {
						  tabTicketDeatilInfo.setSelection(tabTicketDeatilInfo.getSelectionIndex() - 1);
		              }
				}
				break;
			case GlobalVar.PageDown:
				if(isTableSwitch == BothSwitch || isTableSwitch == PaySwitch)
				{
					if ((tabPay.getSelectionIndex() < (tabPay.getItemCount() - 1)) && (tabPay.getItemCount() >= 0))
	                {
						tabPay.setSelection(tabPay.getSelectionIndex() + 1);
	                }
				}
				else
				{
					if ((tabTicketDeatilInfo.getSelectionIndex() < (tabTicketDeatilInfo.getItemCount() - 1)) && (tabTicketDeatilInfo.getItemCount() >= 0))
	                {
						tabTicketDeatilInfo.setSelection(tabTicketDeatilInfo.getSelectionIndex() + 1);
	                }
				}
				break;
		}
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	try
    	{
    		switch (key)
    		{
    			case GlobalVar.Enter:
    				if (isTableSwitch != BothSwitch)
    				{
    					isTableSwitch = BothSwitch;
    					tabTicketDeatilInfo.setBounds(10,65,772,135);
	    				tabTicketDeatilInfo.setVisible(true);
	    				tabPay.setBounds(10,205,772,130);
	    				tabPay.setVisible(true);
    				}
    				
    				// 刷新界面交互
    				while (Display.getCurrent().readAndDispatch());
    				
					this.code = txtTicketCode.getText();
					
					if (type == StatusType.MN_BACKSALE)
					{
						dstbs.getBackSaleInfo(this.code, txtTicketCode, txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet, group,txt_khje);
					}
					else if (type == StatusType.MN_SALEHC)
					{
						dstbs.getSaleAllInfo(null,this.code,StatusType.MN_SALEHC,txtTicketCode,txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet,group,txt_khje);
					}
					else
					{
						dstbs.getSaleAllInfo(null,this.code,StatusType.MN_XSCX,txtTicketCode,txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet,group,txt_khje);
					}
					
    				txtTicketCode.selectAll();
    				break;
    			case GlobalVar.Pay:
    				if(isTableSwitch == BothSwitch)
    				{
    					isTableSwitch = DetailSwitch;
    					tabTicketDeatilInfo.setBounds(10,65,772,270);
    					tabTicketDeatilInfo.setVisible(true);
    					tabPay.setVisible(false);
    				}
    				else if(isTableSwitch == DetailSwitch)
    				{
    					isTableSwitch = PaySwitch;
    					tabPay.setBounds(10,65,772,270);
    					tabPay.setVisible(true);
    					tabTicketDeatilInfo.setVisible(false);
    				}
    				else
    				{
    					isTableSwitch = BothSwitch;
    					tabTicketDeatilInfo.setBounds(10,65,772,135);
	    				tabTicketDeatilInfo.setVisible(true);
	    				tabPay.setBounds(10,205,772,130);
	    				tabPay.setVisible(true);
    				}
    				break;	
    			case GlobalVar.Validation:
    				
    				if (isValidation) break;
    				
    				isValidation = true;
    				if (type == StatusType.MN_MODIFYINVNO)
    				{
    					// 如果是修改小票号菜单进入该功能的化，则执行该功能键
    					StringBuffer newinvno = new StringBuffer();
    					if (dstbs.modifyInvno(newinvno))
    					{
    						this.code = newinvno.toString();
    						txtTicketCode.setText(code);
    						dstbs.getSaleAllInfo(null,this.code,StatusType.MN_XSCX,txtTicketCode,txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet,group,txt_khje);
    						txtTicketCode.selectAll();
    					}
    					
    					isValidation = false;
    				}
    				else
    				{
	    				if(type == StatusType.MN_SALEHC)
	    				{
	    					if(dstbs == null) break;
	    					
	    					dstbs.saleRedQuash();
	    				}
	    				else if (type == StatusType.MN_BACKSALE)
	    				{
	    					if(dstbs == null) break;
	    					
	    					if (!dstbs.saleBackSale())
	    					{
	    						txtTicketCode.setFocus();
	    						txtTicketCode.selectAll();
	    					}
	    				}
	    				
	    				shell.close();
	    				shell.dispose();
	    				shell = null;
    				}
    				break;	
    			case GlobalVar.Print:
    				if (type == StatusType.MN_XSCX || type == StatusType.MN_MODIFYINVNO) dstbs.printSaleTicket();
    				break;
    			case GlobalVar.SaleAppendInfo:
    				dstbs.showAppendInfo();
    				break;
    			case  GlobalVar.Exit:
    				shell.close();
    				shell.dispose();
    				shell = null;
    				break; 
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
}
