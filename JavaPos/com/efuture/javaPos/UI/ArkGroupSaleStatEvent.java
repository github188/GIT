package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
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
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.ArkGroupSaleStatBS;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.UI.Design.ArkGroupSaleStatForm;


public class ArkGroupSaleStatEvent 
{
	private PosTable tabArkStatInfo = null;
	private Text txtDate = null;
	private Combo cmbSyyh = null;
	private Combo cmbBc = null;
	private Shell shell = null;
	private Label LblSaleJe = null;
	private Label LblSaleBS = null;
	private Label LblThJe = null;
	private Label LblThBS = null;
	private Label LblZke = null;
	
	private ArkGroupSaleStatBS agssbs = null;
	
	private int currow = 0;
	
	public ArkGroupSaleStatEvent(ArkGroupSaleStatForm agssf)
	{
		tabArkStatInfo = agssf.getTabArkStatInfo();
		txtDate     = agssf.getTxtDate();
		cmbSyyh = agssf.getCmbSyyh();
		cmbBc = agssf.getCmbBc();
		shell = agssf.getShell();
		LblSaleJe = agssf.getLblSaleJe();
		LblSaleBS = agssf.getLblSaleBS();
		LblThJe = agssf.getLblThJe();
		LblThBS = agssf.getLblThBS();
		LblZke = agssf.getLblZke();
		
		agssbs = CustomLocalize.getDefault().createArkGroupSaleStatBS();
		
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("按 '打印键' 打印柜组对账单"));
		
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
	     key.inputMode = key.IntegerInput;
	     
	     tabArkStatInfo.addKeyListener(key);
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

         //
	     init ();
	}
	
	private void init ()
	{
		txtDate.setText(new ManipulateDateTime().getDateByEmpty());
		txtDate.setFocus();
		txtDate.selectAll();
		
		if (!isValidate(txtDate.getText()))
        {
            return;
        }
		
		if(agssbs.init(tabArkStatInfo,txtDate.getText(),cmbSyyh.getSelectionIndex(),cmbBc.getSelectionIndex(),LblSaleJe,LblSaleBS,LblThJe,LblThBS,LblZke))
		{
			tabArkStatInfo.setSelection(0);
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
						tabArkStatInfo.moveUp();
						currow = tabArkStatInfo.getSelectionIndex();
					}
	
	            break;
				case GlobalVar.ArrowDown:
		            if (e.getSource() == txtDate)
		            {
						tabArkStatInfo.moveDown();
						currow = tabArkStatInfo.getSelectionIndex();
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
//            	case GlobalVar.Validation://重百需要用此键做打印
				case GlobalVar.Enter:
					currow = 0;
					if (!isValidate(txtDate.getText()))
                    {
                        txtDate.selectAll();
                        tabArkStatInfo.removeAll();

                        return;
                    }
					
					if (!agssbs.getArkGroupInfo(tabArkStatInfo, txtDate.getText(),cmbSyyh.getSelectionIndex(),cmbBc.getSelectionIndex(),LblSaleJe,LblSaleBS,LblThJe,LblThBS,LblZke))
                    {
                        txtDate.selectAll();

                        return;
                    }

                    //
                    txtDate.selectAll();
		            e.data = "focus";
		            txtDate.setFocus();
		            
                    //
					tabArkStatInfo.setSelection(0);
                    break;
				case GlobalVar.PageUp:
                  	if (e.getSource() == txtDate)
                    {
						tabArkStatInfo.PageUp();
						currow = tabArkStatInfo.getSelectionIndex();
                    }

	                break;
                case GlobalVar.PageDown:
                    if (e.getSource() == txtDate)
                    {
						tabArkStatInfo.PageDown();
						currow = tabArkStatInfo.getSelectionIndex();
                    }

                    break;
                case GlobalVar.Validation://重百小键盘模式键盘键值不够用添加
				case GlobalVar.Print:
					if(currow < 0)
                	{
                		new MessageBox(Language.apply("没有查询到柜组统计数据,不能打印!"), null, false);
                		break;
                	}
					
					agssbs.printGzSale();
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
