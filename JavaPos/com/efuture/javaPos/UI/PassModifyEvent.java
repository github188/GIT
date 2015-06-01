package com.efuture.javaPos.UI;


import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.PassModifyBS;
import com.efuture.javaPos.UI.Design.PassModifyForm;


public class PassModifyEvent
{
    PassModifyForm pmf = null;
    private Shell shell = null;
    private Text txtOkNewPass = null;
    private Text txtNewPass = null;
    private Text txtOldPass = null;
    private Text txtGh = null;
    private PassModifyBS pmbs = null;
    
    public PassModifyEvent(PassModifyForm pmf)
    {
        this.pmf          = pmf;
        this.txtGh        = pmf.getTxtGh();
        this.txtOkNewPass = pmf.getTxtOkNewPass();
        this.txtNewPass   = pmf.getTxtNewPass();
        this.txtOldPass   = pmf.getTxtOldPass();
        pmbs              = CustomLocalize.getDefault().createPassModifyBS();
        
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
       
        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
            	txtOldPass.setFocus();
            }

            public void focusLost(FocusEvent e)
            {
            	
            }
        };

        NewKeyListener key = new NewKeyListener();
        key.event = event;
        
        txtGh.addFocusListener(listener);
        
        txtOldPass.addKeyListener(key);
        txtNewPass.addKeyListener(key);
        txtOkNewPass.addKeyListener(key);
        
        shell = pmf.getShell();
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:
            	if (txtOldPass.getText().length() > 0 && txtNewPass.getText().length() > 0 && txtOkNewPass.getText().length() > 0)
            	{
            		e.data = "focus";
            		if (!pmbs.checkOldPass(txtOldPass.getText()))
                    {
            			txtOldPass.setFocus();
            			txtOldPass.selectAll();
                    }
            		else if (!pmbs.checkNewPass(txtNewPass.getText()))
            		{
            			txtNewPass.setFocus();
            			txtNewPass.selectAll();
                    }
                    else if (!pmbs.setNewPassWord(txtNewPass.getText(),txtOkNewPass.getText()))
                    {
                    	txtOkNewPass.setFocus();
                    	txtOkNewPass.selectAll();
                    }
                    else
                    {
                        shell.close();
                        shell.dispose();
                        shell = null;
                    }
            	}
            	else
            	{
        			e.data = "focus";
            		if (e.widget == txtOldPass)
            		{
            			txtNewPass.setFocus();
            			txtNewPass.selectAll();
            		}
            		else if (e.widget == txtNewPass) 
            		{
            			txtOkNewPass.setFocus();
            			txtOkNewPass.selectAll();
            		}
            		else 
            		{
            			txtOldPass.setFocus();
            			txtOldPass.selectAll();
            		}
            	}

                break;

            case GlobalVar.Exit:
                shell.close();
                shell.dispose();
                shell = null;

                break;
        }
    }
}
