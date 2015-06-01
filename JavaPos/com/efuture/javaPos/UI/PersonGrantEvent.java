package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.PersonGrantBS;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.Design.PersonGrantForm;


public class PersonGrantEvent
{
    private Text txt1 = null;
    private Text txt2 = null;
    private Shell shell = null;
    private PersonGrantForm grantForm = null;
    private OperUserDef staff = null;
    private PersonGrantBS pbs = null;
    
    public PersonGrantEvent(PersonGrantForm grantForm)
    {
        this.txt1      = grantForm.text;
        this.txt2      = grantForm.text_1;
        this.grantForm = grantForm;
        this.shell     = grantForm.shell;
        
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
            
            public void msrFinish(KeyEvent e,String track1,String track2,String track3)
            {
            	msrRead(e,track1,track2,track3);
            }
        };

        NewKeyListener key = new NewKeyListener();
        key.event     = event;
        if (GlobalInfo.sysPara.grantpwd == 'Y')
        {
        	key.inputMode = key.MsrKeyInput;
        }
        else
        {
        	key.inputMode = key.MsrInput;
        }
        txt1.addKeyListener(key);
        txt1.setData("MSRINPUT");
        
        NewKeyListener key1 = new NewKeyListener();
        key1.event     = event;
        if (GlobalInfo.sysPara.grantpasswordmsr == 'Y')
        {
        	key1.inputMode  = key1.MsrInput;
        }
        else
        {
        	key1.inputMode  = key1.MsrKeyInput;
        }
        txt2.addKeyListener(key1);
        txt2.setData("MSRINPUT");
        
        //Rectangle area = Display.getCurrent().getPrimaryMonitor().getBounds();
        shell.setBounds((GlobalVar.rec.x / 2) - (shell.getSize().x / 2),
                        (GlobalVar.rec.y / 2) - (shell.getSize().y / 2),shell.getSize().x,(shell.getSize().y-GlobalVar.heightPL));
        
        //
        pbs = CustomLocalize.getDefault().createPersonGrantBS();
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Exit:
                grantForm.close();
                break;
                
    		case GlobalVar.Enter:
    		{
    	        if (e.widget.equals(txt2))
    	        {
    	            /*if (pbs.checkPasswd(txt2.getText().trim()))
    	            {
    	                grantForm.setDone(true, staff);
    	                grantForm.close();                    	
    	            }
    	            else
    	            {
    	                txt2.selectAll();
    	            }*/
    	        	
    	        	msrRead(e,"",txt2.getText(),"");
    	        }
    	        else if (e.widget.equals(txt1))
    	        {
    	        	msrRead(e,"",txt1.getText(),"");
    	        }
                break;
    		}
        }
    }
    
    public void msrRead(KeyEvent e,String track1,String track2,String track3)
    {
        if (e.widget.equals(txt1) && txt1.getText().length() > 0)
        {	
        	staff = pbs.getGrantStaff(track1.trim(),track2.trim(),track3.trim());
        	if (staff != null)
            {
        		if (GlobalInfo.sysPara.grtpwdshow != 'Y') txt1.setText(staff.gh);
        		if (GlobalInfo.sysPara.grantpwd == 'Y'||GlobalInfo.sysPara.grantpwd == 'A')
        		{
        			e.data = "focus";
        			txt2.setFocus();
        			txt2.selectAll();
        		}
        		else
        		{
                    grantForm.setDone(true, staff);
                    grantForm.close();
        		}
            }
            else
            {
                txt1.setText(Language.apply("请重新刷卡"));
                txt1.selectAll();
            }
        }
        else if (e.widget.equals(txt2) && (txt2.getText().length() > 0 || (GlobalInfo.sysPara.grantpwd == 'Y'||GlobalInfo.sysPara.grantpwd == 'A')))
        {
        	if (staff == null || staff.gh == null ) //|| (!staff.gh.equals(txt1.getText().trim()))
        	{
        		staff = pbs.getGrantStaff(track1.trim(),txt1.getText(),track3.trim());
        		
        		if (staff != null)
                { 
        			if (GlobalInfo.sysPara.grtpwdshow != 'Y')
        				txt1.setText(staff.gh);
            		if (GlobalInfo.sysPara.grantpwd == 'Y'||GlobalInfo.sysPara.grantpwd == 'A')
            		{
                    	if (pbs.checkPasswd(track2))
                        {
                            grantForm.setDone(true, staff);
                            grantForm.close();                    	
                        }
                        else
                        {
                            txt2.selectAll();
                        }
            		}
            		else
            		{
                        grantForm.setDone(true, staff);
                        grantForm.close();
            		}
                }
                else
                {
                    txt1.setText(Language.apply("请重新刷卡"));
                    txt1.selectAll();
                }
        	}
        	else
        	{
	        	if (pbs.checkPasswd(track2))
	            {
	                grantForm.setDone(true, staff);
	                grantForm.close();                    	
	            }
	            else
	            {
	                txt2.selectAll();
	            }
        	}
        }
        else
        {
        	grantForm.close();
        }
    }
}
