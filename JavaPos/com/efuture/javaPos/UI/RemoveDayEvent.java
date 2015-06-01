package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.RemoveDayBS;
import com.efuture.javaPos.UI.Design.RemoveDayForm;

public class RemoveDayEvent 
{
	private Text txtDirName = null;
	private Shell shell = null;
	private RemoveDayBS rdbs = null;
	
	public RemoveDayEvent(RemoveDayForm rdf)
	{
		txtDirName = rdf.getTxtDirName();
		shell = rdf.getShell();
		
		rdbs = CustomLocalize.getDefault().createRemoveDayBS();
		
//		设定键盘事件
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
	     
	     txtDirName.setFocus();
	     txtDirName.addKeyListener(key);
	     
	     init();
	}
	
	private void init ()
	{
		rdbs.init(txtDirName);
		txtDirName.selectAll();
	}
	
	public void keyPressed(KeyEvent e,int key)
    {
		
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	try
		{
    		switch(key)
			{
    			case GlobalVar.Enter:
    				rdbs.execRemove(txtDirName);
    			break;	
				case  GlobalVar.Exit:
					shell.close();
					shell.dispose();
					shell = null;
				break;
			}
		}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
}
