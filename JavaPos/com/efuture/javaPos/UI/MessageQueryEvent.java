package com.efuture.javaPos.UI;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MessageQueryBS;
import com.efuture.javaPos.UI.Design.MessageQueryForm;

public class MessageQueryEvent 
{
	private StyledText txtContent = null;
	private StyledText txtTitle = null;
	private PosTable tabBaseInfo = null;
	private Shell shell = null;
	private MessageQueryBS mqbs = null;
	
	public MessageQueryEvent(MessageQueryForm mqf)
	{
		txtContent = mqf.getTxtContent();
		txtTitle = mqf.getTxtTitle();
		tabBaseInfo = mqf.getTabBaseInfo();
		shell = mqf.getShell();
		
		tabBaseInfo.setFocus();
		
		mqbs = CustomLocalize.getDefault().createMessageQueryBS();
		
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
        
        tabBaseInfo.addKeyListener(key);
        txtContent.addKeyListener(key);
        
        tabBaseInfo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent selectionevent) 
            {
            	int currow = ((Table)selectionevent.getSource()).getSelectionIndex();
            	TableItem tableItem = tabBaseInfo.getItem(currow);
            	mqbs.getMessage(tableItem.getText(0),tableItem.getText(1), txtTitle, txtContent);
            }
        });
        
        init();
	}
	
	private void init()
    {
		if(mqbs.init(tabBaseInfo, txtTitle, txtContent))
		{
			tabBaseInfo.setSelection(0);
		}
    }
	
	public void keyPressed(KeyEvent e, int key)
    {
		 e.doit = false;
		 switch (key)
	     {
		 	case GlobalVar.ArrowUp:
		 	   if (tabBaseInfo.getSelectionIndex() >= 0)
               {
		 		  tabBaseInfo.moveUp();
		 		  TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
		 		  mqbs.getMessage(tableItem.getText(0),tableItem.getText(1), txtTitle, txtContent);
               }
              
		 	break;
		 	case GlobalVar.ArrowDown:
		 	   if ((tabBaseInfo.getSelectionIndex() <= (tabBaseInfo.getItemCount() - 1)) && tabBaseInfo.getItemCount() > 0)
		 	   {
		 		  tabBaseInfo.moveDown();
		 	      TableItem tableItem = tabBaseInfo.getItem(tabBaseInfo.getSelectionIndex());
		 	      mqbs.getMessage(tableItem.getText(0),tableItem.getText(1), txtTitle, txtContent);
		 	   }  
		 	break;
		
	     }
		 
		 tabBaseInfo.setFocus();
    }
	
	public void keyReleased(KeyEvent e, int key)
    {
	     switch (key)
	     {
	  		case GlobalVar.PageUp:
	  			if (tabBaseInfo.getSelectionIndex() >= 0 && tabBaseInfo.getSelectionIndex() < tabBaseInfo.getItemCount())
	  			{
	  				Point p = txtContent.getSelection();
	  				p.x = p.x - 100;
	  				p.y = p.y - 100;
	  				if (p.x < 0) p.x = 0;
	  				if (p.y < 0) p.y = 0;
	  				txtContent.setSelection(p);
	  			}
	 		break;	
	  		case GlobalVar.PageDown:
	  			if (tabBaseInfo.getSelectionIndex() >= 0 && tabBaseInfo.getSelectionIndex() < tabBaseInfo.getItemCount())
	  			{
	  				Point p = txtContent.getSelection();
	  				p.x = p.x + 100;
	  				p.y = p.y + 100;
	  				if (p.x > txtContent.getCharCount()) p.x = txtContent.getCharCount();
	  				txtContent.setSelection(p);
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
	
}
