package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.PosTable.NewSelectionAdapter;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.QueryWorkLogBS;
import com.efuture.javaPos.UI.Design.QueryWorkLogForm;


public class QueryWorkLogEvent
{
    private PosTable tabWorkLog = null;
    protected Shell shell = null;
    private QueryWorkLogBS qwlbs = null;
    private Text txtDate = null;
    private Combo cmbDjlb = null;
    
    private int currow = 0;
	
    public QueryWorkLogEvent(QueryWorkLogForm qwlf)
    {
        tabWorkLog = qwlf.getTabWorkLog();
        txtDate = qwlf.getTxtDate();
        cmbDjlb	= qwlf.getCmbDjlb();
        shell      = qwlf.getShell();
        qwlbs      = CustomLocalize.getDefault().createQueryWorkLogBS();
 
		//显示功能提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("'确认键'发送选中的工作日志"));
		
        //设定键盘事件
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

        tabWorkLog.addMouseListener(new MouseAdapter()
        {
            public void mouseDown(MouseEvent mouseevent) 
            {
            	txtDate.setFocus();
            }
        });
        tabWorkLog.addNewSelectionListener(new NewSelectionAdapter()
        {
        	public void widgetSelected(int oldindex,int index)
        	{
        		currow = tabWorkLog.getSelectionIndex();
        	}
        });
        
        txtDate.addKeyListener(key);
        cmbDjlb.addKeyListener(key);
        cmbDjlb.select(0);
	    key.inputMode = key.IntegerInput;
	    
	    txtDate.setText(new ManipulateDateTime().getDateByEmpty());
		txtDate.setFocus();
		txtDate.selectAll();
		
	    if (qwlbs.initWorkLog(tabWorkLog,txtDate.getText(),cmbDjlb.getSelectionIndex()))
	    {
	    	tabWorkLog.setSelection(0);
	    }
    }

    public void keyReleased(KeyEvent e, int key)
    {
    	try
    	{
	        switch (key)
	        {
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
		                tabWorkLog.removeAll();
		
		                return;
		            }
					
					if (!qwlbs.initWorkLog(tabWorkLog, txtDate.getText(),cmbDjlb.getSelectionIndex()))
		            {
		                txtDate.selectAll();
		
		                return;
		            }
		
		            //
		            txtDate.selectAll();
		            e.data = "focus";
		            txtDate.setFocus();
		            
		            //
		            tabWorkLog.setSelection(0);
		            break;
	            case GlobalVar.PageUp:
	                if (e.getSource() == txtDate)
	                {
	                	tabWorkLog.PageUp();
	                	currow = tabWorkLog.getSelectionIndex();
	        		}
	        
	                break;
	
	            case GlobalVar.PageDown:
	                if (e.getSource() == txtDate)
	                {
	                	tabWorkLog.PageDown();
	                	currow = tabWorkLog.getSelectionIndex();
	                }
	
	                break;
	            case GlobalVar.Validation:
	            	
	            	if (tabWorkLog.getItemCount() > 0 && currow >= 0 && (currow <= (tabWorkLog.getItemCount() - 1)))
	            	{
	            		tabWorkLog.setSelection(currow);
		            	TableItem tableItem = tabWorkLog.getItem(tabWorkLog.getSelectionIndex());
		            	String keytext = txtDate.getText() + "," + txtDate.getText() + "," + tableItem.getText(0).substring(1).trim(); 
		            	
		            	if (tableItem.getText(0).charAt(0) == ' ')
	                	{
		            		if (TaskExecute.getDefault().sendAllWorkLog(keytext))
		            		{
		            			if (qwlbs.initWorkLog(tabWorkLog,txtDate.getText(),cmbDjlb.getSelectionIndex()))
		            		    {
		            		    	//setSelection(0, true);
		            		    }
		            		}
	                	}
		            	else
		            	{
		            		int selkey = new MessageBox(Language.apply("该工作日志已经上传过了!\n\n1 - 重传当前工作日志\n2 - 重传所有工作日志"),null,false).verify();
		            		if (selkey == GlobalVar.Key1 || selkey == GlobalVar.Key2)
            				{
		            			if (selkey == GlobalVar.Key2) keytext = keytext.substring(0,keytext.lastIndexOf(','));
		            			if (TaskExecute.getDefault().sendAllAgainData(StatusType.TASK_SENDWORKLOG, keytext))
		            			{
		            				if (qwlbs.initWorkLog(tabWorkLog,txtDate.getText(),cmbDjlb.getSelectionIndex()))
			            		    {
		            					//setSelection(0, true);
			            		    }
		            			}
            				}
		            	}
		            	
		            	tabWorkLog.setSelection(currow);
	            	}
	            	
	            	break;
			    case GlobalVar.Exit:
			        try
			        {
			            shell.close();
			            shell.dispose();
			            shell = null;
			        }
			        catch (Exception ex)
			        {
			            ex.printStackTrace();
			        }
			
			    break;
	        }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }

    public void keyPressed(KeyEvent e, int key)
    {
    	try
    	{
	    	switch (key)
	        {
		        case GlobalVar.ArrowUp:
		
		            if (e.getSource() == txtDate)
		            {
	                	tabWorkLog.moveUp();
	                	currow = tabWorkLog.getSelectionIndex();
		            }
		
		        break;
		        case GlobalVar.ArrowDown:
	
		            if (e.getSource() == txtDate)
		            {
	                	tabWorkLog.moveDown();
	                	currow = tabWorkLog.getSelectionIndex();
		            }
		
		            break;
	        }
    	}
    	catch (Exception ex)
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
