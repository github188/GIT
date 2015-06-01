package com.efuture.javaPos.UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleMemoBS;
import com.efuture.javaPos.Struct.KeyValueDef;
import com.efuture.javaPos.Struct.SaleMemoInfo;
import com.efuture.javaPos.UI.Design.SaleMemoFilterForm;

public class SaleMemoFilterEvent
{
	public Button btnsearch = null;
	public Table table = null;
	public SaleMemoBS salememobs = null;
	public SaleMemoInfo memoInfo = null;
	public Text txtwhere = null;
	public Button btnok = null;
	public Shell shell = null;
	
	public SaleMemoFilterForm memoFilterForm = null;

	public SaleMemoFilterEvent(SaleMemoFilterForm memoFilterForm,SaleMemoBS smb,SaleMemoInfo smi)
	{
		init(memoFilterForm,smb,smi);
		
		refreshTable();
	}

	private void close(boolean isok)
	{
		if (isok)
		{
			memoInfo.curselindex = -1;
			if (table.getSelectionIndex() >=0)
			{
				memoInfo.curselindex = table.getSelectionIndex();
			}
		}
		
		memoFilterForm.shell.close();
		memoFilterForm.shell.dispose();
	}
	
	// 初始化
	public void init(SaleMemoFilterForm memoFilterForm,SaleMemoBS smb,SaleMemoInfo smi)
	{
		this.memoFilterForm = memoFilterForm;
		this.btnsearch = memoFilterForm.btnsearch;
		this.btnok = memoFilterForm.btnok;
		this.txtwhere = memoFilterForm.txtwhere;
		this.table = memoFilterForm.table;
		this.salememobs = smb;
		this.memoInfo = smi;
		
		KeyListener kl = new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
            	try
                {
                    if (e.keyCode == SWT.CR)
                    {
	            		if (e.widget.equals(table))
	            		{
	            			if (table.getSelectionIndex() >= 0)
	            			{
	            				close(true);
	            			}
	            			else
	            			{
	            				search();
	            			}
	            		}
	            		else if (e.widget.equals(txtwhere))
	            		{
		                	search();
	            		}
                    }
                    else if (e.keyCode == SWT.ARROW_UP)
                    {
	            		if (e.widget.equals(txtwhere))
	            		{
	                   		if (table.getItemCount() <= 0)
                    		{
                    			return;
                    		}
                    		else if (table.getItemCount() == 1)
	            			{
	            				table.select(0);
	            			}
	            			else if (table.getSelectionIndex() <= 0)
		                	{
		                		table.select(table.getItemCount()-1);
		                	}
		                	else
		                	{
		                		table.select(table.getSelectionIndex() - 1);
		                	}
	            		}
                    }
                    else if (e.keyCode == SWT.ARROW_DOWN)
                    {
                    	if (e.widget.equals(txtwhere))
	            		{
                    		if (table.getItemCount() <= 0)
                    		{
                    			return;
                    		}
                    		else if (table.getItemCount() == 1)
	            			{
	            				table.select(0);
	            			}
	            			else if (table.getSelectionIndex() >= table.getItemCount()-1)
		                	{
		                		table.select(0);
		                	}
		                	else
		                	{
		                		table.select(table.getSelectionIndex() + 1);
		                	}
	            		}
                    }
                    	
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            public void keyReleased(KeyEvent e)
            {
            	try
                {
                    if (e.keyCode == SWT.ESC)
                    {
                    	close(false);
                    }
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
                
        };
        
		table.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				close(true);
			}

			public void mouseDown(MouseEvent event)
			{

			}

			public void mouseUp(MouseEvent e)
			{
			}
		});
		
		selectEvent se = new selectEvent();

		btnsearch.addSelectionListener(se);
		btnok.addSelectionListener(se);
		
		table.addKeyListener(kl);
		btnsearch.addKeyListener(kl);
		btnok.addKeyListener(kl);
		txtwhere.addKeyListener(kl);
		
		// 生成刚加入tableItem
		table.redraw();
		//Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
		this.memoFilterForm.shell.setLocation((GlobalVar.rec.x / 2) - (this.memoFilterForm.shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (this.memoFilterForm.shell.getSize().y / 2));
	}

	public void refreshTable()
	{
		table.removeAll();
		
		for (int i = 0;i < memoInfo.content.vccontent.size();i++)
		{
			KeyValueDef kvd =  (KeyValueDef)memoInfo.content.vccontent.get(i);
			
			TableItem ti = new TableItem(table,SWT.NULL);
			ti.setText(0,kvd.key.toString());
			ti.setText(1,kvd.value.toString());
		}
		
		if (memoInfo.curselindex < 0)
		{
			table.select(0);
		}
		else
		{
			table.select(memoInfo.curselindex);
		}
	}
	
	private void search()
	{
		ProgressBox pb = null;
		
		try
		{
			pb = new ProgressBox();
	        pb.setText(Language.apply("正在获得信息,请等待..."));
	     
	        salememobs.RefreshComboData(memoInfo,txtwhere.getText().trim());
	        
	        refreshTable();
	        
	        txtwhere.selectAll();
		}
		catch(Exception ex)
		{
			MessageBox msg = new MessageBox(memoFilterForm.shell);
			msg.setMessage(ex.getMessage());
			msg.open();
		}
		finally
		{
            if (pb != null)
            {
                pb.close();
                pb = null;
            }
		}
	}
	
	class selectEvent implements SelectionListener
	{
		public selectEvent()
		{
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			if (e.widget.equals(btnok))
			{
				if (table.getSelectionIndex() < 0)
				{
					MessageBox msg = new MessageBox(memoFilterForm.shell);
					msg.setMessage(Language.apply("请选择项目"));
					msg.open();
					
					return;
				}
				
				close(true);
			}
			
			if (e.widget.equals(btnsearch))
			{
				search();
			}
		}
	}
}

