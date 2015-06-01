package com.efuture.javaPos.UI;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.FjkInfoDef;
import com.efuture.javaPos.UI.Design.FjkQueryInfoForm;

public class FjkQueryInfoEvent 
{
	private Table table = null;
	private StyledText txtCardCode = null;
	protected Shell shell = null;
	private ArrayList fjklist = null;
	private Label txt_A = null;
	private Label txt_B = null;
	private Label txt_F = null;
	private String aje  = null;
	private String bje  = null;
	private String fje  = null;
	
	public FjkQueryInfoEvent (FjkQueryInfoForm fqif)
	{
		this.table = fqif.getTable();
		this.txtCardCode = fqif.getTxtCardCode();
		this.shell = fqif.getShell();
		this.fjklist = fqif.getFjklist();
		this.txt_A  = fqif.getAje();
		this.txt_B  = fqif.getBje();
		this.txt_F  = fqif.getFje();
		this.aje    = fqif.aje;
		this.bje    = fqif.bje;
		this.fje    = fqif.fje;
		
		
		
		//设定键盘事件
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
	     
	     table.setFocus();
	     table.addKeyListener(key);
	     
	     init();
	}
	
	private void init ()
	{
		if (fjklist == null) return ;
		
		if (this.showFjkInfo())
		{
			table.select(0);
		}
	}
	
	private boolean showFjkInfo()
	{
		try
		{
			if (aje != null)
				txt_A.setText(aje);
			
			if (bje != null)
				txt_B.setText(bje);
			
			if (aje != null)
				txt_F.setText(fje);
			
			for (int i = 0;i < fjklist.size() ; i++)
			{
				FjkInfoDef fid = (FjkInfoDef)fjklist.get(i);
				txtCardCode.setText(fid.cardno);
				String[] fjkInfo = {fid.status,fid.startdate,fid.enddate,String.valueOf(fid.yeA),String.valueOf(fid.yeB),String.valueOf(fid.yeF)};
				
				TableItem item = new TableItem(table, SWT.NONE);
            	item.setText(fjkInfo);
			}
				
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (fjklist != null)
			{
				fjklist.clear();
				fjklist = null;
			}
		}
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
