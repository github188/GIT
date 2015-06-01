package com.efuture.javaPos.Test;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

public class DebugEvent
{
	private Shell shell = null;
    private Text txtShow = null;
//    private Label lbmsg = null;
    private Combo combo = null;
    private PosTable table = null;
    private Vector vcTree = new Vector();
    private DebugBS pmbs = null;
    private Object rootobj = null;
    private String rootobjname = null;

	public DebugEvent(final DebugForm form,Object obj,String objname)
    {
        this.table = form.gettable();
        shell = form.sShell;
        txtShow = form.getTextshow();
        combo = form.getcombo();
        
        rootobj = obj;
        rootobjname = objname;
        
        pmbs = new DebugBS();       
        
        // 鼠标事件
        table.addMouseListener(new MouseAdapter()
        {
            public void mouseDoubleClick(MouseEvent mouseevent)
            {
            	keyReleased(null,GlobalVar.Enter);
            }
        });
        
        table.addSelectionListener(new SelectionAdapter()
        {
        	public void widgetSelected(SelectionEvent e)
        	{
        		setShowText();
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
        shell.addKeyListener(key);
        txtShow.addKeyListener(key);
        table.addKeyListener(key);
        
        combo.add(objname);
        combo.add("GlobalVar");
        combo.add("ConfigClass");
        combo.add("GlobalInfo");
        
        combo.addSelectionListener(new SelectionAdapter()
        {
        	public void widgetSelected(SelectionEvent e)
        	{
        		switch(combo.getSelectionIndex())
        		{
        			case 0:
        				RefreshTableInit(rootobj,rootobjname);
        				break;
        			case 1:
        				RefreshTableInit(new GlobalVar(),"GlobalVar");
        				break;
        			case 2:
        				RefreshTableInit(new ConfigClass(),"ConfigClass");
        				break;
        			case 3:
        				RefreshTableInit(new GlobalInfo(),"GlobalInfo");
        				break;
        		}
        	}
        });
        
        combo.select(0);
        
        RefreshTable(obj,objname);
    }
	
	private void RefreshTableInit(Object obj,String objname)
	{
		this.vcTree.clear();
		
		RefreshTable(obj,objname);
	}
	
	private void RefreshTable(Object obj,String objname)
	{
		for (int i = 0;i < vcTree.size();i++)
		{
			Object[] objs = (Object[])vcTree.get(i);
			if (objs[2] == obj)
			{
				for (;i < vcTree.size();i++)
				{
					vcTree.remove(i);
					i--;
				}
				
				break;
			}
		}
		
		Vector vccontend = new Vector();
		Vector vcobject = new Vector();
		pmbs.Debug(obj,vccontend,vcobject);
		
		if (vccontend.size() > 0)
		{
			vcTree.add(new Object[]{vccontend,vcobject,obj,objname});

			table.initialize();
			table.exchangeContent((Vector)(((Object[])vcTree.get(vcTree.size()-1))[0]));
		}
		
		String strmsg = "";
		
		for (int i = 0;i < vcTree.size();i++)
		{
			String msg = (String)((Object[])vcTree.get(i))[3];
			
			strmsg = strmsg + "->" +  msg;
		}
		
		//lbmsg.setText(strmsg);
	}
	
	private void setShowText()
	{	
		int num = table.getSelectionIndex();
		
		if (num >= 0 && vcTree.size() > 0)
		{
			Vector vccontend = (Vector)(((Object[])vcTree.get(vcTree.size()-1))[0]);
			
			String[] str = (String[])vccontend.get(num);
			txtShow.setText(str[1]);
		}
	}
	
    public void keyPressed(KeyEvent e, int key)
    {
    	switch (key)
		{
			case GlobalVar.ArrowUp:
				table.moveUp();
				setShowText();

				break;

			case GlobalVar.ArrowDown:
				table.moveDown();
				setShowText();

				break;

			case GlobalVar.PageDown:
				table.PageDown();
				setShowText();

				break;

			case GlobalVar.PageUp:
				table.PageUp();
				setShowText();

				break;
		}
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
           case GlobalVar.Enter:
           {
        	    Object[] objs = (Object[])vcTree.get(vcTree.size() - 1);
        	    Vector objs1 = (Vector)objs[1];
        	    Vector contend1 = (Vector)objs[0];
        	    int i = table.getSelectionIndex();
        	    Object obj = objs1.get(i);
        	    String[] contend = (String[])contend1.get(i);
        	    String objecname = contend[0];
        	    this.RefreshTable(obj,objecname);
            	break;
           }
           case GlobalVar.Exit:
           {
            	if (vcTree.size() <= 1)
            	{
	            	shell.close();
	            	shell.dispose();
            	}
            	else
            	{
            		Object[] objs = (Object[])vcTree.get(vcTree.size() - 2);
            		String objecname = (String)objs[3];
            		this.RefreshTable(objs[2],objecname);
            	}
            	
            	break;
           }
        }
    }
    
    
}
