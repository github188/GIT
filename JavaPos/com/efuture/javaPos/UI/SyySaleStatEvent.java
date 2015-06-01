package com.efuture.javaPos.UI;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SyySaleStatBS;
import com.efuture.javaPos.UI.Design.SyySaleStatForm;

public class SyySaleStatEvent 
{
	private Text txtDate = null;
	private Table tabPayInfo = null;
	private Table tabBaseInfo = null;
	private Shell shell = null;
	private Label lblSaleAmount = null;
	private Label lblSaleMoney = null;
	private Label lblReturnGoodsAmount = null;
	private Label lblReturnGoodsMoney = null;
	private Label lblRedCancelAmount = null;
	private Label lblRedCancelMoney = null;
	private Label lblCancelAmount = null;
	private Label lblCancelMoney = null;
	private Label lblSpoilageMoney = null;
	private Label lblGiveChangeMoney = null;
	private Label lblShouldInceptMoney = null;
	private Label lblFactInceptMoney = null;
	private SyySaleStatBS  sssbs = null;
	
	private Control focus = null; 
	
	public SyySaleStatEvent(SyySaleStatForm sssf)
	{
		txtDate = sssf.getTxtDate();
		tabPayInfo = sssf.getTabPayInfo();
		tabBaseInfo = sssf.getTabBaseInfo();
		shell = sssf.getShell();
		lblSaleAmount = sssf.getLblSaleAmount();
		lblSaleMoney = sssf.getLblSaleMoney();
		lblReturnGoodsAmount = sssf.getLblReturnGoodsAmount();
		lblReturnGoodsMoney = sssf.getLblReturnGoodsMoney();
		lblRedCancelAmount = sssf.getLblRedCancelAmount();
		lblRedCancelMoney = sssf.getLblRedCancelMoney();
		lblCancelAmount = sssf.getLblCancelAmount();
		lblCancelMoney = sssf.getLblCancelMoney();
		lblSpoilageMoney = sssf.getLblSpoilageMoney();
		lblGiveChangeMoney = sssf.getLblGiveChangeMoney();
		lblShouldInceptMoney = sssf.getLblShouldInceptMoney();
		lblFactInceptMoney = sssf.getLblFactInceptMoney();
		sssbs = CustomLocalize.getDefault().createSyySaleStatBS();
		
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("按 '打印键' 打印该收银员汇总表"));
		
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
	     key.isPage = false;
	     
	     tabBaseInfo.addKeyListener(key);
	     tabBaseInfo.addFocusListener(listener);
	     tabBaseInfo.addSelectionListener(new SelectionAdapter()
	     {
	    	 public void widgetSelected(SelectionEvent selectionevent) 
	    	 {
	    		 setFocus(tabBaseInfo);
	    		 TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
	    		 sssbs.getSaleSummary(tableItem.getText(0),tableItem.getText(1),lblSaleAmount, lblSaleMoney, lblReturnGoodsAmount, lblReturnGoodsMoney, lblRedCancelAmount, lblRedCancelMoney, lblCancelAmount, lblCancelMoney, lblSpoilageMoney, lblGiveChangeMoney, tabPayInfo, lblShouldInceptMoney, lblFactInceptMoney,txtDate.getText());
	    	 }
	     });
	     
	     tabPayInfo.addFocusListener(listener);
	     
	     txtDate.addKeyListener(key);
	     txtDate.addFocusListener(listener);
	     key.inputMode = key.IntegerInput;
	     
	     init();
	}
	
	private void init()
	{
		txtDate.setText(new ManipulateDateTime().getDateByEmpty());
		setFocus(txtDate);
		txtDate.selectAll();
		
		if (!isValidate(txtDate.getText()))
        {
            return;
        }
		
		if(sssbs.init(tabBaseInfo,lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,tabPayInfo,lblShouldInceptMoney,lblFactInceptMoney,txtDate.getText()))
		{
			setFocus(tabBaseInfo);
			tabBaseInfo.select(0);
		}
	
	}
	
	public void keyPressed(KeyEvent e,int key)
    {
		e.doit = false;
		switch(key)
		{
			case GlobalVar.ArrowUp:
				if (e.getSource() != tabBaseInfo) return ;
				
				if (tabBaseInfo.getSelectionIndex() >= 0)
	            {
			 		TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
			 		sssbs.getSaleSummary(tableItem.getText(0),tableItem.getText(1),lblSaleAmount, lblSaleMoney, lblReturnGoodsAmount, lblReturnGoodsMoney, lblRedCancelAmount, lblRedCancelMoney, lblCancelAmount, lblCancelMoney, lblSpoilageMoney, lblGiveChangeMoney, tabPayInfo, lblShouldInceptMoney, lblFactInceptMoney,txtDate.getText());
	            }
				
			break;
			case GlobalVar.ArrowDown:
				if (e.getSource() != tabBaseInfo) return ;
				if ((tabBaseInfo.getSelectionIndex() <= (tabBaseInfo.getItemCount() - 1)) && tabBaseInfo.getItemCount() > 0)
		 	    {
		 	    	TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
		 	    	sssbs.getSaleSummary(tableItem.getText(0),tableItem.getText(1), lblSaleAmount, lblSaleMoney, lblReturnGoodsAmount, lblReturnGoodsMoney, lblRedCancelAmount, lblRedCancelMoney, lblCancelAmount, lblCancelMoney, lblSpoilageMoney, lblGiveChangeMoney, tabPayInfo, lblShouldInceptMoney, lblFactInceptMoney,txtDate.getText());
		 	    }
				
			break;	
		}
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	try
		{
			switch(key)
			{
				case  GlobalVar.Enter:
					
					if (e.getSource() != txtDate) return ;
					
					if (!isValidate(txtDate.getText()))
                    {
                        txtDate.selectAll();
                    
                        return;
                    }
					
					if(sssbs.init(tabBaseInfo,lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,tabPayInfo,lblShouldInceptMoney,lblFactInceptMoney,txtDate.getText()))
					{
						setFocus(tabBaseInfo);
						tabBaseInfo.select(0);
					}
				break;	
				case GlobalVar.Pay:
					tabBaseInfo.removeAll();
					tabPayInfo.removeAll();
					sssbs.clear(lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,lblShouldInceptMoney,lblFactInceptMoney);
					sssbs.Close();
					setFocus(txtDate);
					txtDate.selectAll();
				break;	
				case  GlobalVar.PageUp:
					if (e.getSource() != tabBaseInfo) return ;
					
					if (tabPayInfo.getSelectionIndex() > 0)
		            {
						tabPayInfo.setSelection(tabPayInfo.getSelectionIndex() - 1);
		            }
				break;	
				case  GlobalVar.PageDown:
					if (e.getSource() != tabBaseInfo) return ;
					
					if (tabPayInfo.getSelectionIndex() < (tabPayInfo.getItemCount() - 1) && tabPayInfo.getItemCount() > 0)
			 	    {
						tabPayInfo.setSelection(tabPayInfo.getSelectionIndex() + 1);
			 	    }
				break;	
				case GlobalVar.Validation://重百小键盘模式键盘键值不够用添加
				case GlobalVar.Print:
					
					if (e.getSource() != tabBaseInfo) return ;
					
					if(tabBaseInfo.getSelectionIndex() < 0)
                	{
                		new MessageBox(Language.apply("至少要选择一项收银员报表"), null, false);
                		break;
                	}
					
					TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
					sssbs.printSyySale(tableItem.getText(0),tableItem.getText(1),txtDate.getText());
				break;	
				case  GlobalVar.Exit:
					sssbs.Close();
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
    
    private void setFocus(Control focus)
    {
        this.focus = focus;
        focus.setFocus();
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
