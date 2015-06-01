package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SetSystemTimeBS;
import com.efuture.javaPos.UI.Design.SetSystemTimeForm;


public class SetSystemTimeEvent
{
    private Text txtTime = null;
    private Text txtDate = null;
    protected Shell shell = null;
    private SetSystemTimeBS sstb = null;

    public SetSystemTimeEvent(SetSystemTimeForm sstf,boolean onlysettime)
    {
        txtTime = sstf.getTxtTime();
        txtDate = sstf.getTxtDate();
        shell   = sstf.getShell();

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

        txtTime.addKeyListener(key);
        txtDate.addKeyListener(key);
        key.inputMode = key.IntegerInput;
        
        init();

        sstb = CustomLocalize.getDefault().createSetSystemTimeBS();
        sstb.onlySetTime(onlysettime);
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:
                input(e.getSource(),e);

                break;
            case GlobalVar.Validation:
            	if (!input(txtDate,e))
            	{
            		e.data = "focus";
            		txtDate.setFocus();
            	}
            	else if (!input(txtTime,e))
            	{
            		e.data = "focus";
            		txtTime.setFocus();
            	}
            	break;
            case GlobalVar.ArrowDown:
            	if (e.widget == txtTime )
            	{
            		e.data = "focus";
            		txtDate.setFocus();
            	}
            	else if (e.widget == txtDate)
            	{
            		e.data = "focus";
            		txtTime.setFocus();
            	}

                break;
            case GlobalVar.ArrowUp:
            	if (e.widget == txtTime )
            	{
            		e.data = "focus";
            		txtDate.setFocus();
            	}
            	else if (e.widget == txtDate)
            	{
            		e.data = "focus";
            		txtTime.setFocus();
            	}

                break;
            case GlobalVar.Exit:
                shell.close();
                shell.dispose();
                shell = null;

                break;
        }
    }
    
    private void init()
    {
        try
        {
            ManipulateDateTime mdt = new ManipulateDateTime();
            txtDate.setText(mdt.getDateByEmpty());
            txtTime.setText(mdt.getTimeByEmpty());
            txtDate.setSelection(txtDate.getText().length());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private boolean input(Object object,KeyEvent e)
    {
        try
        {
            if (object == txtDate)
            {
            	if (!isValidate(txtDate))
            	{
            		return false;
            	}
            	
                if (ManipulateDateTime.checkDate(ManipulateDateTime.getConversionDate(txtDate.getText().trim())))
                {
                	e.data = "";
                    txtTime.setFocus();
                    txtTime.setSelection(txtTime.getText().length());
                    return true;
                }
                else
                {
                    new MessageBox(Language.apply("请输入合法的日期"), null, false);
                    txtDate.selectAll();
                }
            }
            else if (object == txtTime)
            {
            	if (!isValidate(txtTime))
            	{
            		return false;
            	}
            	
                if (ManipulateDateTime.checkTime(ManipulateDateTime.getConversionTime(txtTime.getText().trim())))
                {
                    if (sstb.modifyDateTime(ManipulateDateTime.getConversionDate(txtDate.getText().trim()),ManipulateDateTime.getConversionTime(txtTime.getText().trim())))
                    {
                        shell.close();
                        shell.dispose();
                        shell = null;
                        return true;
                    }
                }
                else
                {
                    new MessageBox(Language.apply("请输入合法的时间"), null, false);
                    txtTime.selectAll();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        
		return false;
    }
    
    private boolean isValidate(Text datetime)
    {
        if ((datetime == null) || datetime.getText().trim().equals(""))
        {
            new MessageBox(Language.apply("日期或时间不能为空,请重新输入!"), null, false);

            return false;
        }
        
        if (datetime == txtDate)
        {
	        if (datetime.getText().trim().length() < 8)
	        {
	            new MessageBox(Language.apply("不合法的日期输入,请检查是否有8位长\n请重新输入!"), null, false);
	
	            return false;
	        }
        }
        else
        {
        	if (datetime.getText().trim().length() < 6)
	        {
	            new MessageBox(Language.apply("不合法的时间输入,请检查是否有6位长\n请重新输入!"), null, false);
	
	            return false;
	        }
        }

        return true;
    }
    
}
