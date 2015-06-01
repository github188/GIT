package custom.localize.Smtj;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.BusinessPersonnelStatBS;
import com.efuture.javaPos.Struct.PosTimeDef;

public class Smtj_BusinessPersonnelStatEvent
{
	private PosTable tabBusinessPersonStatInfo = null;
	private Text txtDate = null;
	private Combo cmbSyyh = null;
	private Combo cmbBc = null;	
	private Shell shell = null;
	
	private BusinessPersonnelStatBS bpsbs = null;
	
	private int currow = 0;
	
	public Smtj_BusinessPersonnelStatEvent(Smtj_BusinessPersonnelStatForm bpsf)
	{
		tabBusinessPersonStatInfo = bpsf.getTabBusinessPersonStatInfo();
		txtDate = bpsf.getTxtDate();
		cmbSyyh = bpsf.getCmbSyyh();
		cmbBc = bpsf.getCmbBc();		
		shell = bpsf.getShell();
		
		bpsbs = CustomLocalize.getDefault().createBusinessPersonnelStatBS();
		
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage("按 '打印键' 打印营业员对账单");
		
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
	     key.inputMode = key.IntegerInput;
	     
	     //
	     tabBusinessPersonStatInfo.addKeyListener(key);
	     txtDate.addKeyListener(key);
         cmbSyyh.addKeyListener(key);
         cmbBc.addKeyListener(key);
         cmbSyyh.select(0);
         
         // 确定当前班次
         cmbBc.select(0);
         for (int i = 0; i < GlobalInfo.posTime.size(); i++)
         {
             if (((PosTimeDef) GlobalInfo.posTime.elementAt(i)).code == GlobalInfo.syjStatus.bc)
             {
            	 cmbBc.select(i+1);
            	 break;
             }
         }
	     
	     init();
	}
	
	private void init()
	{
		txtDate.setText(new ManipulateDateTime().getDateByEmpty());
		txtDate.setFocus();
		txtDate.selectAll();
		
		if (!isValidate(txtDate.getText()))
        {
            return;
        }
		
		if(bpsbs.init(tabBusinessPersonStatInfo,txtDate.getText(),cmbSyyh.getSelectionIndex(),cmbBc.getSelectionIndex()))
		{
			tabBusinessPersonStatInfo.setSelection(0);
		}
	}
	
	public void keyPressed(KeyEvent e,int key)
    {
		try
		{
			switch(key)
			{
				case GlobalVar.ArrowUp:
		
		            if (e.getSource() == txtDate)
		            {
	                	tabBusinessPersonStatInfo.moveUp();
	                    currow = tabBusinessPersonStatInfo.getSelectionIndex();
		            }
		
		            break;
		        case GlobalVar.ArrowDown:
		
		            if (e.getSource() == txtDate)
		            {
	                	tabBusinessPersonStatInfo.moveDown();
	                    currow = tabBusinessPersonStatInfo.getSelectionIndex();
		            }
		
		        break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	try
		{
			switch(key)
			{
            	case GlobalVar.Pay:
            		if (e.getSource() == txtDate)
            		{
            			e.data = "focus";
            			cmbSyyh.setFocus();
            		}
            		else if (e.getSource() == cmbSyyh)
            		{
            			e.data = "focus";
            			cmbBc.setFocus();
            		}
            		else 
            		{
            			e.data = "focus";
            			txtDate.setFocus();
            		}
            		break;				
				case GlobalVar.Validation:
				case GlobalVar.Enter:
					currow = 0;
					if (!isValidate(txtDate.getText()))
                    {
                        txtDate.selectAll();
                        tabBusinessPersonStatInfo.removeAll();

                        return;
                    }
					
					if (!bpsbs.getBusinessPerStat(tabBusinessPersonStatInfo, txtDate.getText(),cmbSyyh.getSelectionIndex(),cmbBc.getSelectionIndex()))
                    {
                        txtDate.selectAll();

                        return;
                    }

                    //
                    txtDate.selectAll();
        			e.data = "focus";
        			txtDate.setFocus();
        			
                    //
        			tabBusinessPersonStatInfo.setSelection(0);
        			break;
                case GlobalVar.PageUp:

                    if (e.getSource() == txtDate)
                    {
	                	tabBusinessPersonStatInfo.PageUp();
	                    currow = tabBusinessPersonStatInfo.getSelectionIndex();
                    }

                    break;

                case GlobalVar.PageDown:

                    if (e.getSource() == txtDate)
                    {
	                	tabBusinessPersonStatInfo.PageDown();
	                    currow = tabBusinessPersonStatInfo.getSelectionIndex();
                    }

                    break;
				case GlobalVar.Print:
					if(currow < 0)
                	{
                		new MessageBox("没有查询到营业员统计数据,不能打印!", null, false);
                		break;
                	}
					bpsbs.printBusinessPerSale();
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
    
    public boolean isValidate(String date)
    {
        if ((txtDate.getText() == null) || txtDate.getText().equals(""))
        {
            new MessageBox("日期不能为空,请重新输入!", null, false);

            return false;
        }

        if (txtDate.getText().length() < 8)
        {
            new MessageBox("不合法的日期输入,请检查是否有8位长\n请重新输入!", null, false);

            return false;
        }

        if(!txtDate.getText().equals(new ManipulateDateTime().getDateByEmpty())){
        	new MessageBox("收银员权限只允许查询当天数据!", null, false);

            return false;
        }

        return true;
    }


}
