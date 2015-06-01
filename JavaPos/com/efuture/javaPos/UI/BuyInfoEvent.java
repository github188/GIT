package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.PosTable.NewSelectionAdapter;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.BuyInfoBS;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.UI.Design.BuyInfoForm;


public class BuyInfoEvent
{
    private Shell shell = null;
    
    private BuyInfoBS buyInfoBs = null;
    
    private Label lbMessage = null;
    private PosTable table = null;
    private Text txtInput = null;
    
    private Vector selCode = new Vector();
    
    private Vector SelectRoute = new Vector();
    
    public Vector tableInfo = null;
    
    private boolean ismustsel = false;
    private boolean isshownodata = true;

    public void mouseModeInit()
    {
        table.setFocusedControl(txtInput);

        table.addNewSelectionListener(new NewSelectionAdapter()
        {
        	public void widgetSelected(int oldindex,int index)
        	{
        		RowSelected(index);
        	}
        });
        
        table.addMouseListener(new MouseAdapter()
        {
			public void mouseDoubleClick(MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Enter);
			}

			public void mouseDown(MouseEvent arg0)
			{
				txtInput.setFocus();
			}
        });     
    }
    
    private String[] showGroup = null;
    public BuyInfoEvent(BuyInfoForm form,String[] showgroup)
    {
    	showGroup = showgroup;
    	
    	initEvent(form);
    }
    
    public BuyInfoEvent(BuyInfoForm form)
    {
    	initEvent(form);
    }
    
    public void initEvent(BuyInfoForm form)
    {
        this.table    = form.table;
        this.txtInput = form.txtInput;
        this.lbMessage = form.lbMessage;
        this.shell = form.shell;
        this.selCode = form.selCode;
        this.ismustsel = form.ismustsel;
        this.isshownodata = form.isshownodata;
        
        buyInfoBs = CustomLocalize.getDefault().createBuyInfoBS();
        buyInfoBs.SetShowGroup(showGroup);
        
        mouseModeInit();
        
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
        key.event     = event;
        key.inputMode = key.DoubleInput;
        key.isControl = true;

        txtInput.addKeyListener(key);
        table.addKeyListener(key);
        
        lbMessage.setText("请选择");

        // 读一级菜单
        tableInfo = buyInfoBs.getBuyInfoByType("1");
        
        RefreshTable();
        
        shell.setActive();
        txtInput.setFocus(); 
        
        if (!isshownodata)
        {	
        	if (tableInfo == null || tableInfo.size() <= 0)
        	{
            	if (!shell.isDisposed())
            	{
            		shell.dispose();
            	}
            	
	        	selCode.clear();
        	}
        }
    }
    
    public void RowSelected(int index)
    {
    	// 得到当前顾客信息的子信息
    	refreshtxtInpuf();
    }
    
    public void RefreshTable()
    {
    	table.removeAll();
    	lbMessage.setText(Language.apply("请选择"));
    	
    	if (tableInfo == null) return;
    		
        String[] row = null;
        
        char type = '1';
        for (int i = 0;i < tableInfo.size();i++)
        {
        	BuyerInfoDef bid = (BuyerInfoDef)tableInfo.get(i);
        	type = bid.type;
        	row = new String[2];
        	
        	row[0] = bid.code;
        	row[1] = bid.name;
        	
        	table.addRow(row);
        }
        
        if (table.getItemCount() > 0)
        {
        	tableSelection(0);
        }
        
        refreshMessage();
        
        if (type == '1' && table.getItemCount() == 1)
        {
        	enter();
        }
    }

    private void tableSelection(int i)
    {
    	table.setSelection(i);
    	refreshtxtInpuf();
    }
    
    private void refreshMessage()
    {
    	String str = "";
    	for (int i = 0;i < SelectRoute.size();i++)
    	{
    		BuyerInfoDef bif = (BuyerInfoDef)SelectRoute.get(i);
    		str += bif.name + "->";
    	}
    	
    	if (str.length() > 2) str = str.substring(0,str.length() - 2);
    	
    	lbMessage.setText(Language.apply("请选择") + (str.length()>0?"(" + str + ")":""));
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.ArrowUp:
                table.moveUp();
                refreshtxtInpuf();
                break;
            case GlobalVar.ArrowDown:
                table.moveDown();
                refreshtxtInpuf();
                break;
            case GlobalVar.PageUp:
            	table.moveUp();
            	refreshtxtInpuf();
                break;
            case GlobalVar.PageDown:
            	table.moveDown();
            	refreshtxtInpuf();
                break;
        }
    }

    public void refreshtxtInpuf()
    {
        if (table.getSelectionIndex() >= 0)
        {
        	BuyerInfoDef bid = (BuyerInfoDef)tableInfo.get(table.getSelectionIndex());
        	txtInput.setText(bid.code);
        	txtInput.selectAll();
        }
    }
    
    private void close()
    {
    	if (selCode.size() <= 0 && this.tableInfo.size() > 0 && ismustsel)
    	{
    		return;
    	}
    	
        shell.close();
        shell.dispose();
    }
    
    private void enter()
    {
    	if (this.txtInput.getText().trim().equals("")) return;
    	
    	int i;
    	for (i = 0;i < tableInfo.size();i++)
    	{
    		BuyerInfoDef bid = (BuyerInfoDef)tableInfo.get(i);
    		if (bid.code.equals(this.txtInput.getText().trim()))
    		{
    			this.tableSelection(i);
    			break;
    		}	
    	}
    	
    	if (i >= tableInfo.size()) return;
    	
    	int sel = table.getSelectionIndex();
    	
    	if (sel >= 0)
    	{
            BuyerInfoDef bif = (BuyerInfoDef)tableInfo.get(sel);
            SelectRoute.add(bif);
            
            Vector vc = buyInfoBs.getBuyInfoBySjCode(bif.code);
            if (vc != null && vc.size() > 0)
            {
            	// 如果存在子结点
                tableInfo = vc;
                RefreshTable();
            }
            else
            {
            	// 未级选择
            	for (int k = 0;k < selCode.size();k++)
            	{
            		String[] select = (String[])selCode.get(k);
            		
            		if (select[0].equals(((BuyerInfoDef)SelectRoute.get(0)).code))
            		{
            			selCode.remove(k++);
            		}
            	}
            	
            	String[] select = new String[2];
            	select[0] = ((BuyerInfoDef)SelectRoute.get(0)).code;
            	select[1] = ((BuyerInfoDef)SelectRoute.get(SelectRoute.size() - 1)).code;
            	selCode.add(select);
            	
            	if (!GlobalInfo.sysPara.custinfocyclesel.equals("Y"))
            	{
            		close();
                    return;
            	}
            	
                // 读一级菜单
            	SelectRoute.clear();
                tableInfo = buyInfoBs.getBuyInfoByType("1");
                
                RefreshTable();
            	
            }
    	}
    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:
            case GlobalVar.Validation:
            	enter();
            	break;
            case GlobalVar.Exit:
            	// 没有选择任何结点
            	if (SelectRoute.size() <= 0) 
            	{
            		close();
                    return;
            	}
            	
            	BuyerInfoDef bif = (BuyerInfoDef)SelectRoute.get(SelectRoute.size() - 1);
            	
            	if (SelectRoute.size() == 1)
            	{
            		// 读顶级菜单
        			tableInfo = buyInfoBs.getBuyInfoByType("1");
        			
        			RefreshTable();
        			
        			if (tableInfo.size() == 1)
        			{
        				close();
                        return;
        			}
            	}
            	else
            	{            		
             		// 读上一级菜单
        			tableInfo = buyInfoBs.getBuyInfoBySjCode(bif.sjcode);
            	}

    			SelectRoute.remove(bif);
    			
    			RefreshTable();
    			
    			for(int j = 0;j < tableInfo.size();j++)
    			{
    				BuyerInfoDef bif1 = (BuyerInfoDef)tableInfo.get(j);
    				
    				if (bif1.code.equals(bif.code))
    				{
    					tableSelection(j);
    				}
    			}
    			
            	break;
        }
    }

}
