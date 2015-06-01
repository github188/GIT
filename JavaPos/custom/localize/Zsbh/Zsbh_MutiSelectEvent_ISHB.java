package custom.localize.Zsbh;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


public class Zsbh_MutiSelectEvent_ISHB
{
    private Zsbh_MutiSelectForm_ISHB form = null;
    private boolean textInput = false;
    private boolean manychoice= false;
    private boolean modifyvalue = false;
    private boolean specifyback = false;
    private boolean cannotchoice = false; // 在输入框上按回车后,是否可以不用选择列表中的值,直接关闭选择框
    private int rowindex;	
    public Vector content = null;
    private String[] row = null;
    private int funcid = 0;
    private Zsbh_MutiSelectBS_ISHB msbs = null;

	public Zsbh_MutiSelectEvent_ISHB(final Zsbh_MutiSelectForm_ISHB form, String help_txt, String[] title,int[] width, Vector content,boolean textInput,boolean manychoice,boolean modifyvalue,int rowindex,boolean specifyback,String[] title1,int[] width1,Vector content2,int funcID,boolean cannotchoice)
    {
        this.form = form;
        this.textInput = textInput;
        this.manychoice = manychoice;
        this.content = content;
        this.modifyvalue = modifyvalue;
        this.rowindex = rowindex;
        this.specifyback = specifyback;
        this.cannotchoice = cannotchoice;
        this.funcid = funcID;
        form.label.setText(help_txt);
        
        form.table.setTitle(title);
        form.table.setWidth(width);
        form.table.initialize();
        form.table.exchangeContent(content);
        
        if (title1 != null && width1 != null && content2 != null)
        {
            form.table1.setTitle(title1);
            form.table1.setWidth(width1);
            form.table1.initialize();
            form.table1.exchangeContent(content2);
        }

        if (this.textInput == false)
        {
        	Rectangle rect1 = form.text.getBounds();
        	Rectangle rect2 = form.table.getBounds();
        	
        	form.text.setVisible(false);
        	form.table.setBounds(rect2.x,rect1.y,rect2.width,rect2.height+rect1.height+10);
        	form.table.setSelection(0);
        	form.table.setFocus();
        }
        else
        {
        	form.text.setFocus();
        } 
                
        // 鼠标事件
        form.table.addMouseListener(new MouseAdapter()
        {
            public void mouseDoubleClick(MouseEvent mouseevent)
            {
            	if (Zsbh_MutiSelectEvent_ISHB.this.textInput && form.table.getSelectionIndex() >= 0)
            	{
	            	row = form.table.changeItemVar(form.table.getSelectionIndex());
	            	form.text.setText(row[0]);
	            	form.text.selectAll();
            	}
            	              	
            	keyReleased(null,GlobalVar.Enter);
            }
                
            public void mouseDown(MouseEvent mouseevent) 
            {          	
            	form.text.setFocus();
            }
        });
        
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
        form.table.addKeyListener(key);
        form.text.addKeyListener(key);
        if (title1 != null && width1 != null && content2 != null)
        {
        	form.table1.addKeyListener(key);
        }
        
        msbs = (Zsbh_MutiSelectBS_ISHB)CustomLocalize.getDefault().createMutiSelectBS();
        
        if (specifyback) key.inputMode = key.DoubleInput;
        
        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        form.shell.setBounds((GlobalVar.rec.x - form.shell.getSize().x) / 2,
                             (GlobalVar.rec.y - form.shell.getSize().y) / 2,
                             form.shell.getSize().x,
                             form.shell.getSize().y - GlobalVar.heightPL);  
        
        if (form.table.getColumnCount() > 0 && this.textInput == true)
        {
            if (specifyback)
            {
            	try
            	{
            		form.table.moveDown();
            		
	            	if (textInput && form.table.getSelectionIndex() >= 0)
	                {
	            		row = (String[])content.get(form.table.getSelectionIndex());
	                	form.text.setText(row[7]);
	                	form.text.selectAll();
	                }
            	}catch(Exception er)
            	{
            		er.printStackTrace();
            	}
            }
            else
            {
            	NewKeyListener.sendKey(GlobalVar.ArrowDown);
            }
        }
        
        msbs.initBS(this, form, funcID, content, cannotchoice, cannotchoice, cannotchoice, 0, cannotchoice, cannotchoice);
        
        for (int i = 0;i < form.table.getTableCount();i++)
		{
			row = (String[])content.get(i);
				row[6] = "Y";
				if (row[rowindex].trim().equals("")) row[rowindex] = row[3];
			form.table.modifyRow(row,i);
		}
    }

    public void keyPressed(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.ArrowUp:
                form.table.moveUp();
                
                if (specifyback)
                {
                	if (textInput && form.table.getSelectionIndex() >= 0)
                    {
                		row = (String[])content.get(form.table.getSelectionIndex());
                    	form.text.setText(row[7]);
                    	form.text.selectAll();
                    }
                	
                	return ;
                }
                
                if (modifyvalue) return ;
                
                if (textInput && form.table.getSelectionIndex() >= 0)
                {
                	String[] ax = form.table.changeItemVar(form.table.getSelectionIndex());
                	form.text.setText(ax[0]);
                	form.text.selectAll();
                }
                break;

            case GlobalVar.ArrowDown:
                form.table.moveDown();
                
                if (specifyback)
                {
                	if (textInput && form.table.getSelectionIndex() >= 0)
                    {
                		row = (String[])content.get(form.table.getSelectionIndex());
                    	form.text.setText(row[7]);
                    	form.text.selectAll();
                    }
                	
                	return ;
                }
                
                if (modifyvalue) return ;
                
                if (textInput && form.table.getSelectionIndex() >= 0)
                {
                	String[] ax = form.table.changeItemVar(form.table.getSelectionIndex());
                	form.text.setText(ax[0]);
                	form.text.selectAll();
                }
                break;
            case GlobalVar.PageDown:
            	if (form.table1 != null) form.table1.moveDown();
            	break;
            case GlobalVar.PageUp:
            	if (form.table1 != null) form.table1.moveUp();
            	break;
        }
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
           /* case GlobalVar.Enter:
            	if (msbs.enterBS(this,this.form,this.funcid,content,modifyvalue,manychoice,textInput,rowindex,specifyback,cannotchoice)) return;
            	
            	if (textInput)
            	{
            		int i = 0;
            		
            		if (!modifyvalue)
            		{
            			//查找相应代码
	            		
	            		for (i=0;i<form.table.getItemCount();i++)
	            		{
	            			String[] ax = form.table.changeItemVar(i);
	            			if (ax[0].equals(form.text.getText()))
	            			{
	            				form.table.setSelection(i);
	            				break;
	            			}
	            		}
	            		
	            		if (i >= form.table.getItemCount())
	            		{
	            			if (cannotchoice)
	            			{
	            				form.InputText = form.text.getText().trim();
	                            form.choice = -2;
	    	                    form.shell.close();
	    	                    form.shell.dispose();
	    	                    
	    	                    return;
	            			}
	            			
	            			form.text.selectAll();
	            			return;
	            		}
            		}
            	}
            	
            	//
                if (form.table.getSelectionIndex() < 0)
                {
                	if (textInput && modifyvalue)
                	{
                		form.text.setText("");
                	}
                	
                    return;
                }
                else
                {
                	if (textInput && modifyvalue)
                	{
                		row = (String[])content.get(form.table.getSelectionIndex());
                		
                		if (rowindex <= 0)
                		{
                			return ;
                		}
                		
                		if (!form.text.getText().trim().equals(""))
                		{
                			if (specifyback)
                            {
                				if (row[3] != null && !row[3].trim().equals(""))
                				{
	                				if (Double.parseDouble(form.text.getText()) > Double.parseDouble(row[3]))
	                				{
	                					new MessageBox(Language.apply("当前输入的退货数量大于原数量,请重新输入!"));
	                					form.text.selectAll();
	                					return;
	                				}
	                				
	                				if (Double.parseDouble(form.text.getText()) <= 0)
	                				{
	                					new MessageBox(Language.apply("当前输入的退货数量不能小于1,请重新输入!"));
	                					form.text.selectAll();
	                					return;
	                				}
                				}
                            }
                		
                			row[rowindex] = ManipulatePrecision.doubleToString(Double.parseDouble(form.text.getText()));
                			form.text.selectAll();
                		}
                	}
                	
                	if(manychoice)
                	{
                		row = (String[])content.get(form.table.getSelectionIndex());
                		
                		if (specifyback)
                        {
                			if (row[6].trim().equals(""))
	                		{
	                			row[6] = "Y";
	                			
	                			if (row[rowindex].trim().equals("")) row[rowindex] = row[3];
	                		}
	                		else
	                		{
	                			row[6] = "";
	                			row[rowindex] = "";
	                		}
                        }
                		else
                		{
	                		if (row[(row.length -1)].trim().equals(""))
	                		{
	                			row[(row.length -1)] = "Y";
	                		}
	                		else
	                		{
	                			row[(row.length -1)] = "";
	                		}
                		}
                		
                		form.table.modifyRow(row,form.table.getSelectionIndex());
                		
                		if (form.table.getSelectionIndex() < form.table.getItemCount() - 1)
                		{
                			form.table.setSelection(form.table.getSelectionIndex() + 1);
                		}
                		
                	}
                	else
                	{
	                    form.choice = form.table.getSelectionIndex();
	                    form.shell.close();
	                    form.shell.dispose();
                	}
                }

                break;*/
            case GlobalVar.Validation:
            	if (msbs.validationBS(this,this.form,this.funcid,content,modifyvalue,manychoice,textInput,rowindex,specifyback,cannotchoice)) return;
            	if (manychoice)
            	{
            		for (int i = 0;i < content.size();i ++)
            		{
            			row = (String[])content.get(i);
            			
            			if (specifyback)
                        {
            				if (row[6].trim().equals("Y"))
	            			{
	            				form.choice = i;
	            				break;
	            			}
                        }
            			else
            			{
	            			if (row[(row.length -1)].trim().equals("Y"))
	            			{
	            				form.choice = i;
	            				break;
	            			}
            			}
            		}
            		
            		form.shell.close();
                    form.shell.dispose();
            	}
            break;	
          /*  case GlobalVar.Pay:
            	if (msbs.payBS(this,this.form,this.funcid,content,modifyvalue,manychoice,textInput,rowindex,specifyback,cannotchoice)) return;
            	if (manychoice && specifyback)
            	{
            		boolean isselect = true;
            		
            		for (int i = 0;i < form.table.getTableCount();i++)
            		{
            			row = (String[])content.get(i);
            			
            			if (row[6].trim().equals(""))
                		{
            				isselect = false;
            				break;
                		}
            		}
            		
            		for (int i = 0;i < form.table.getTableCount();i++)
            		{
            			row = (String[])content.get(i);
            			if (!isselect)
                		{
            				row[6] = "Y";
            				if (row[rowindex].trim().equals("")) row[rowindex] = row[3];
                		}
            			else
            			{
            				row[6] = "";
                			row[rowindex] = "";
            			}
            			
            			form.table.modifyRow(row,i);
            		}
            	}
            break;*/
            case GlobalVar.Back:
            case GlobalVar.Minu: //代表-
            	form.text.setText(form.text.getText() + "-");
            	form.text.setSelection(form.text.getText().length());
            	break;
            case GlobalVar.Exit:
            	if (msbs.exitBS(this,this.form,this.funcid,content,modifyvalue,manychoice,textInput,rowindex,specifyback,cannotchoice)) return;

                form.shell.close();
                form.shell.dispose();

                break;
        }
    }
    
    
}
