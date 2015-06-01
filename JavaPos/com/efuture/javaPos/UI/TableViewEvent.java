package com.efuture.javaPos.UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.TableViewBS;
import com.efuture.javaPos.Logic.TableViewBS.DbType;
import com.efuture.javaPos.Logic.TableViewBS.TableVeiwStruct;
import com.efuture.javaPos.UI.Design.TableViewForm;


public class TableViewEvent
{
	TableViewForm frm = null;
	public Text txtSql = null;
    public PosTable table = null;
    public Shell shell = null;
    public TableVeiwStruct tvs = null;
    public Button btnExec = null;
    public Button btnExit = null;
    public Label lbCurrentDb = null;
    
    TableViewBS tvbs = null;

    public TableViewEvent(TableViewForm form,TableVeiwStruct tvs1)
    {
    	frm 	= form;
        table   = form.table;
        txtSql  = form.txtSql;
        shell   = form.shell;
        btnExec = form.btnExec;
        btnExit = form.btnExit;
        lbCurrentDb = form.lbCurrentDb;
        
        tvs 	= tvs1;
        
        tvbs = new TableViewBS(); 
        
        txtSql.setText(tvs.SqlText);
        
        txtSql.selectAll();
        txtSql.setFocus();

        
        KeyListener listener = new KeyListener()
		{
            public void keyPressed(KeyEvent e)
            {
            	m_keyPressed(e);
            }

            public void keyReleased(KeyEvent e)
            {
            	m_keyReleased(e);
            }
        };
        
        table.addKeyListener(listener);
        txtSql.addKeyListener(listener);
        
        btnExec.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(final SelectionEvent arg0) 
        	{
        		Exec();
        	}
        });
        
        btnExit.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(final SelectionEvent arg0) 
        	{
        		Exit();
        	}
        });
        
    }
    
    public void m_keyPressed(KeyEvent e)
    {
        try
        {
        	
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void m_keyReleased(KeyEvent e)
    {
        try
        {
        	int key = e.keyCode;
        	switch(key)
        	{
	        	case SWT.CR:     		//执行
	        		Exec();
	        		break;
	        	case SWT.ESC:       	//退出
	        		Exit();
	        		break;
        	}
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
    
    public void Exit()
    {
		shell.close();
		shell.dispose();
		shell = null;
    }
    
    public void Exec()
    {
    	ProgressBox pb = null;
    	try
    	{
	    	pb = new ProgressBox();
	    	
	        pb.setText(Language.apply("正在执行SQL语句，请等待....."));
	        
	    	table.removeAll();
			
			String sqlstr = txtSql.getText().trim();
			tvs.SqlText = sqlstr;
			String dbstr = "";
			int index = sqlstr.indexOf("|");
			if (index > 0)
			{
				dbstr = sqlstr.substring(0,index);
	    		if (dbstr.toUpperCase().startsWith(DbType.Local))
	    		{
	    			tvs.DB = DbType.Local;
	    			tvs.SqlText = sqlstr.substring(index+1);
	    		}
	    		else if(dbstr.toUpperCase().startsWith(DbType.Base))
	    		{
	    			tvs.DB = DbType.Base;
	    			tvs.SqlText = sqlstr.substring(index+1);
	    		}
	    		else if(dbstr.toUpperCase().startsWith(DbType.Day))
				{
	    			tvs.DB = dbstr.toUpperCase();
	    			tvs.SqlText = sqlstr.substring(index+1);
				}
			}
			
			frm.changeCurrentDb(tvs.DB);
			
			if (tvbs.execSql(tvs))
			{
				frm.ShowTableInfo(tvbs.cols, tvbs.widths, tvbs.contents);
			}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		new MessageBox(ex.getMessage());
    	}
    	finally
    	{
    		if (pb != null) pb.close();
    	}
    }
}
