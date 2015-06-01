package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.defineKey.KeyCharExchange;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.ShortcutKeyBS;
import com.efuture.javaPos.UI.Design.ShortcutKeyForm;
import com.swtdesigner.SWTResourceManager;

public class ShortcutKeyEvent 
{
	private Table tabShortKey = null;
	private Shell shell = null;
	private ShortcutKeyBS skbs= null;
	private TableEditor tEditor = null;
	private int[] currentPoint = new int[] { 0, 0 };
	private NewKeyListener keyShortcut = null;
	private NewKeyListener keyNewEditor = null;
	private Text newEditor = null;
	
	private int tempShortcutKey = 0;
	private String tempKeyString = null;
	private Vector tempShortcutSet = null;
	
	public ShortcutKeyEvent(ShortcutKeyForm skf)
	{
		tempShortcutSet = new Vector();
		this.tabShortKey = skf.getTabShortKey();
		this.shell = skf.getShell();
		
		skbs = CustomLocalize.getDefault().createShortcutKeyBS();
		
		tEditor = new TableEditor(tabShortKey);
		
	    tEditor.horizontalAlignment = SWT.LEFT;
        tEditor.grabHorizontal      = true;
        tEditor.minimumWidth        = 50;
        
        // 显示提示
		GlobalInfo.statusBar.setHelpMessage(Language.apply("连续两个'功能主单'表示执行紧跟着定义的菜单ID功能"));
		
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
        
        keyShortcut = new NewKeyListener();
        keyShortcut.event = event;
       
        tabShortKey.addKeyListener(keyShortcut);
        
        keyNewEditor = new NewKeyListener();
        keyNewEditor.event = event;
        keyNewEditor.isBackSpace=false;
        keyNewEditor.isShortKey =false;
        init();
	}
	
	
	private void init()
	{
		if (skbs.init(tabShortKey,tempShortcutSet))
		{
			currentPoint[0] = tabShortKey.getItemCount()-1;
			currentPoint[1] = 1;
			tabShortKey.select(tabShortKey.getItemCount()-1);
		}
	}
	
    public void findLocation()
    {
        Control oldEditor = tEditor.getEditor();

        if (oldEditor != null)
        {
            oldEditor.dispose();
        }

        if (tabShortKey.getItemCount() <= 0)
        {
            return;
        }
        
     
        TableItem item = tabShortKey.getItem(currentPoint[0]);

        if (item == null)
        {
            return;
        }

        newEditor = new Text(tabShortKey, SWT.NONE | SWT.LEFT);
        
        if(currentPoint[1] == 1)
        {
        	newEditor.setTextLimit(10);
        }
        else
        {
        	newEditor.setTextLimit(60);
        }
        
        newEditor.setText(item.getText(currentPoint[1]));
        newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        newEditor.setFocus();
        tEditor.setEditor(newEditor, item, currentPoint[1]);
        
       
        newEditor.selectAll();
        
        if(currentPoint[1] == 2)
        {
        	newEditor.append("");
        }
        
        newEditor.addKeyListener(keyNewEditor);
    }
    
	public void keyPressed(KeyEvent e, int key)
    {	
		
		if(currentPoint[1] == 1)
		{
			tempShortcutKey = e.keyCode;
		}
		else
		{	
			Text text = (Text) tEditor.getEditor();
			
			if (text == null) return ;
			
			if (text.getText().length() > 0)
			{
				String line =text.getText().trim();
				if (key == GlobalVar.BkSp)
				{
					int num =line.lastIndexOf('+');
					if(num == line.length()-1)
					{
							int num1=line.lastIndexOf("+", num-1);
							if (num1==-1)
								line = "";
							else if ("1234567890.".indexOf(line.charAt(num1-1))!=-1)
							{
								line = line.substring(0, num1);	
							}
							else
							{
								line = line.substring(0, num1+1);
							}
							text.setText(line);
					}
					else
					{	
							text.setText(line.substring(0,line.length()-1));
					}
					
					tEditor.getItem().setText(currentPoint[1], text.getText().trim());
					
					text.selectAll();
					text.append("");
					
					
					return;
				}
				
			}
			
			
			if ((key >= GlobalVar.Key0) && (key <= GlobalVar.Decimal)) 
			{
				tempKeyString = null;
			}
			else
			{
				tempKeyString = skbs.getFuncKeyName(key);
			}
			
		}
    }
	
	public void keyReleased(KeyEvent e, int key)
    {
		try
        {
			switch (key)
			{
				case GlobalVar.ArrowUp:
					if(currentPoint[1] == 2)
					{
						 keyConfirm();
					}
					else
					{
						 if(tabShortKey.getSelectionIndex() > 0)
						 {
							 tabShortKey.select(tabShortKey.getSelectionIndex() - 1);
							 currentPoint[0] = currentPoint[0] - 1;
							 findLocation();
						 }
					}
				break;
				case GlobalVar.ArrowDown:
					 if(currentPoint[1] == 2)
					 {
						 Text text = (Text) tEditor.getEditor();
						 if (text.getText().equals("")) return ;
						 
						 if(tabShortKey.getSelectionIndex() >= tabShortKey.getItemCount() - 1)
						 {
							 String strkey[] = {String.valueOf(tabShortKey.getItemCount() + 1) ,"",""}; 
							
							 TableItem item = new TableItem(tabShortKey, SWT.NONE);
							 item.setText(strkey);
						 }
		                 
						 currentPoint[0] = currentPoint[0] + 1;
						 tabShortKey.select(currentPoint[0]);
						 
						 currentPoint[1] = 1;
						 findLocation();
					 }
					 else
					 {
						 Text text = (Text) tEditor.getEditor();
						 if (text.getText().equals("")) return ;
						 
						 if(tabShortKey.getSelectionIndex() < tabShortKey.getItemCount() - 1)
						 {
							 tabShortKey.select(tabShortKey.getSelectionIndex() + 1);
							 currentPoint[0] = currentPoint[0] + 1;
							 findLocation();
						 } 
					 }
					 
				break;
				case GlobalVar.Del:
					
					if(tabShortKey.getItemCount() < 1 ) return ;
					
					TableItem tItem = tabShortKey.getItem(tabShortKey.getSelectionIndex());
					if (tItem.getText(1).equals("") && tItem.getText(2).equals("")) return ;
					
					tempShortcutSet.remove(tabShortKey.getSelectionIndex());
					tabShortKey.remove(tabShortKey.getSelectionIndex());
					
					currentPoint[0] = tabShortKey.getItemCount() - 1;
					
					tabShortKey.select(currentPoint[0]);
					
					
					
					for (int i = 0;i<tabShortKey.getItemCount();i++)
					{
						TableItem tableItem = tabShortKey.getItem(i);
						tableItem.setText(0,String.valueOf((i + 1)));
					}
					
					findLocation();
					
				break;
				case GlobalVar.Validation:
					//确认键
					skbs.saveShortcutKeyValue(tabShortKey,tempShortcutSet);
					
					shell.close();
		            shell.dispose();
		            shell = null;
				break;	
				case GlobalVar.BkSp:
					//不用写
				break;	
				case GlobalVar.Exit:
					MessageBox me = new MessageBox(Language.apply("你想要在退出前保存新的设置吗?"), null, true);
					
					if (me.verify() == GlobalVar.Key1)
			        {
						skbs.saveShortcutKeyValue(tabShortKey,tempShortcutSet);
			        } 
					
					shell.close();
		            shell.dispose();
		            shell = null;
				break;	
				default:
					keyConfirm();
				break;	
			}
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    }
	
	private void keyConfirm()
	{
		   Text text = (Text) tEditor.getEditor();
		   
		   if(currentPoint[1] == 1)
		   {
			   if(tempShortcutKey == 0)
			   {
				   new MessageBox(Language.apply("对不起不能为空!"), null, false); 
				   return ;
			   }
			   
			   if(skbs.isOkKey(tempShortcutKey))
			   { 
				   text.selectAll();
				   return ;
			   }
			   
			   TableItem tableItem = null;
			   
			   if(tabShortKey.getSelectionIndex() <= tabShortKey.getItemCount() - 1)
			   {
			   
				   tableItem = tabShortKey.getItem(tabShortKey.getSelectionIndex());
				   
				   int currtxtvalue = -1;
				   
				   if(tableItem.getText(1) != null && !tableItem.getText(1).equals(""))
				   {
					   currtxtvalue = (int)tableItem.getText(1).charAt(0);
				   }
				   
				   if (tempShortcutKey != currtxtvalue)
				   {
					   for (int i = 0;i < tabShortKey.getItemCount() - 1;i++)
					   {
						   tableItem = tabShortKey.getItem(i);
				   
						   if(tempShortcutKey == (int)tableItem.getText(1).charAt(0))
						   {	 
							   new MessageBox(Language.apply("对不起已经定义过这个键!"), null, false);
							   text.selectAll();
							   return ;
						   }
					   }
				   }
			   }
			   tempShortcutSet.add(String.valueOf(tempShortcutKey));
			   tEditor.getItem().setText(currentPoint[1],KeyCharExchange.keyexchange(tempShortcutKey));
             
			   currentPoint[1] = 2;
			   
			   findLocation(); 
		   }
		   else
		   {
			   if(tempKeyString == null)
			   {
				   tEditor.getItem().setText(currentPoint[1], text.getText().trim());
				   return ;
			   }
			
			   if(text.getText().length() > 0)
			   {
				   if(text.getText().trim().charAt(text.getText().trim().length()-1)!='+')
				   {
					   tEditor.getItem().setText(currentPoint[1], text.getText().trim() + "+" + tempKeyString + "+");
				   }
				   else
				   {
					   tEditor.getItem().setText(currentPoint[1], text.getText().trim() + tempKeyString + "+");
				   }
			   }
			   else
			   {
				   tEditor.getItem().setText(currentPoint[1], text.getText().trim() + tempKeyString + "+");
			   }
			  
			   findLocation(); 
		   }
              
	}
	
	
}
