package com.efuture.javaPos.UI;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.GlobalParaDesc;
import com.efuture.javaPos.UI.Design.SysParaForm;
import com.swtdesigner.SWTResourceManager;


public class SysParaEvent
{
	private Shell shell = null;
	private Text txtValueDesc;
	private Text txtValue;
	private Text txtDesc;
	private Text txtSearch;
	private Table table;
	private Tree tree;
	private Button btnClose;
	private Button btnExport;
	private Button btnSave;
		
	private GlobalParaDef.SysParaDef paradesc;
	
    public SysParaEvent(SysParaForm form)
    {
        shell   = form.shell;
        txtValueDesc = form.txtValueDesc;
        txtValue = form.txtValue;
        txtDesc = form.txtDesc;
        txtSearch = form.txtSearch;
        table = form.table;
        tree = form.tree;
        btnClose = form.btnClose;
        btnExport = form.btnExport;
        btnSave = form.btnSave;
        
        initTree();
        initTable();
        initButton();
    }
    
    public void initTree()
    {
    	tree.addMouseListener(new MouseAdapter()
    	{
			public void mouseDoubleClick(MouseEvent arg0)
			{
				TreeItem item = tree.getItem(new Point(arg0.x,arg0.y));
				if (item != null && item.getData() != null)
				{
					refushParaTable((String)item.getData());
				}
			}
    	});
    	tree.addKeyListener(new KeyListener()
    	{
			public void keyPressed(KeyEvent arg0)
			{
			}

			public void keyReleased(KeyEvent arg0)
			{
				if (arg0.keyCode == 13)
				{
					TreeItem[] ti = tree.getSelection();
					if (ti != null && ti.length > 0 && ti[0] != null)
					{
						if (ti[0].getData() != null) refushParaTable((String)ti[0].getData());
						else ti[0].setExpanded(!ti[0].getExpanded()); 
					}
				}
			}
    	});
   	
    	// 查询参数
    	txtSearch.addKeyListener(new KeyListener()
    	{
			public void keyPressed(KeyEvent arg0)
			{
			}

			public void keyReleased(KeyEvent arg0)
			{
				if (arg0.keyCode == 13)
				{
					refushParaTable("#"+txtSearch.getText());
				}
			}
    	});
    	
    	// 参数分组
    	if (GlobalInfo.sysPara == null) 
    	{
    		GlobalInfo.sysPara = new GlobalParaDef();
    		GlobalInfo.sysPara.paraInitDefault();
    	}
    	paradesc = GlobalInfo.sysPara.new SysParaDef();
    	
    	TreeItem itemGroup = new TreeItem(tree,SWT.NULL);
    	itemGroup.setText(Language.apply("参数分类"));
    	
    	for (int i=0;paradesc.sysparagroup != null && i<paradesc.sysparagroup.length;i++)
    	{
    		if (paradesc.sysparagroup[i][0] != null && paradesc.sysparagroup[i][0].trim().length() > 0)
    		{
	    		TreeItem item_grp = new TreeItem(itemGroup,SWT.NULL);
	    		item_grp.setData(paradesc.sysparagroup[i][0]);
	    		item_grp.setText(paradesc.sysparagroup[i][1]);
    		}
    	}
    	TreeItem item_grp = new TreeItem(itemGroup,SWT.NULL);
		item_grp.setText(Language.apply("未分类参数"));
		item_grp.setData("#nogroup");    	
    }
    
    private boolean haveParaGroup(String group)
    {
    	for (int i=0;paradesc.sysparagroup != null && i<paradesc.sysparagroup.length;i++)
    	{
    		if (paradesc.sysparagroup[i][0] != null && paradesc.sysparagroup[i][0].trim().length() > 0)
    		{
	    		if (group.equalsIgnoreCase(paradesc.sysparagroup[i][0])) return true;
    		}
    	}
    	
    	return false;
    }
    
    public void refushParaTable(String grpcode)
    {
    	table.removeAll();

    	Class classInst = GlobalInfo.sysPara.getClass();
    	Field[] flds = classInst.getDeclaredFields();
    	
    	Class descInst = paradesc.getClass();
    	Field[] descflds = descInst.getDeclaredFields();
    	
    	for (int i=0;i<flds.length;i++)
    	{
			try
			{
	    		TableItem item = null;
	    		GlobalParaDesc para = null;
	    		
				// 得到参数描述定义
	    		int j=0;
	    		for (;j<descflds.length;j++)
	    		{
	    			if (flds[i].getName().equalsIgnoreCase(descflds[j].getName())) break;
	    		}
	    		if (j < descflds.length)
	    		{
	    			para = (GlobalParaDesc)descflds[j].get(paradesc);
	    		}
	    		
	    		// 检查参数是否和查询条件匹配
				if ("#nogroup".equals(grpcode))
				{
					if (para == null || (para != null && (para.group == null || para.group.equals("") || !haveParaGroup(para.group))))
					{
						item = new TableItem(table, SWT.NONE);
					}
				}
				else if (grpcode.startsWith("#"))
				{
					String code = grpcode.substring(1);
					if (flds[i].getName().indexOf(code) >= 0 || (para != null && para.valdesc != null && para.valdesc.indexOf(code) >= 0))
					{
						item = new TableItem(table, SWT.NONE);
					}
				}
				else
				{
					if (para != null && para.group.equalsIgnoreCase(grpcode))
					{
						item = new TableItem(table, SWT.NONE);
					}
				}
				
				// 增加参数行
				if (item != null)
				{
					item.setText(0,para!=null?para.valdesc:flds[i].getName());
					item.setText(2,flds[i].getName());
					if (flds[i].getType().getName().equalsIgnoreCase("boolean"))
					{
						boolean obj = flds[i].getBoolean(GlobalInfo.sysPara);
						if (obj) item.setText(1,"Y");
						else item.setText(1,"N");
					}
					else
					{
						Object obj = flds[i].get(GlobalInfo.sysPara);
						if (obj != null) item.setText(1,String.valueOf(obj));
						else item.setText(1,"");
					}
					
					item.setData("parafld" , flds[i]);
					item.setData("paradesc", para);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
    	}
    }
    
    public void initTable()
    {
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
    	table.addMouseListener(new MouseAdapter()
    	{
			public void mouseDown(MouseEvent e)
			{
				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);
					if (rect.contains(pt))
					{
						findLocation(index, 1, editor);
						return;
					}
					else
					{
						Control oldEditor = editor.getEditor();
						if (oldEditor != null) oldEditor.dispose();
					}

					index++;
				}
			}
    	});
    	table.addSelectionListener(new SelectionAdapter()
    	{
			public void widgetSelected(final SelectionEvent arg0)
			{
				TableItem[] ti = table.getSelection();
				if (ti != null && ti.length > 0)
				{
					txtDesc.setText(ti[0].getText(0)+"(#"+ti[0].getText(2)+")");
					txtValue.setText(ti[0].getText(1));
					txtValueDesc.setText("");
					GlobalParaDesc para = (GlobalParaDesc)ti[0].getData("paradesc");
					for (int i = 0; para != null && para.valdata != null && i < para.valdata.length; i++)
					{
						// para.valdata.length < 2只有1个时表示示例数据
						if ((para.valdata.length >= 2 && para.valdata[i][0].equals(ti[0].getText(1))) ||
							 para.valdata.length <  2)
						{
							txtValueDesc.setText(para.valdata[i][1]);
						}
					}			
				}
			}
		});
    }
    
	public void findLocation(final int row, final int col, final TableEditor editor)
	{
		Control oldEditor = editor.getEditor();
		if (oldEditor != null) oldEditor.dispose();

		if (table.getItemCount() <= 0) { return; }
		TableItem item = table.getItem(row);
		if (item == null) { return; }

		// 得到参数项的说明定义
		GlobalParaDesc para = (GlobalParaDesc)item.getData("paradesc");
		Field parafld = (Field)item.getData("parafld");
		
		//
    	if (para != null && para.valdata != null && para.valdata.length >= 2)
    	{
    		String val = item.getText(col);
			Combo combo = new Combo(table, SWT.READ_ONLY);
			combo.setVisibleItemCount(10);
			for (int i = 0; i < para.valdata.length; i++)
			{
				combo.add(para.valdata[i][0] + " - " + para.valdata[i][1]);
				if (para.valdata[i][0].equals(val)) combo.select(i);
			}
			editor.setEditor(combo, item, col);
			combo.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Combo cb = (Combo) editor.getEditor();
					editor.getItem().setText(col, Convert.codeInString(cb.getText(),'-').trim());
				}
			});
		}
		else
		{
			Text newEditor = new Text(table, SWT.LEFT | SWT.BORDER);
			if (parafld.getType().getName().equalsIgnoreCase("char")) newEditor.setTextLimit(1);
			newEditor.setText(item.getText(col));
			editor.setEditor(newEditor, item, col);
			newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
			newEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Field parafld = (Field)table.getItem(row).getData("parafld");
					
					Text text = (Text) editor.getEditor();
					if (parafld.getType().getName().equalsIgnoreCase("int")) editor.getItem().setText(col, String.valueOf(Convert.toInt(text.getText())));
					else if (parafld.getType().getName().equalsIgnoreCase("long")) editor.getItem().setText(col, String.valueOf(Convert.toLong(text.getText())));
					else if (parafld.getType().getName().equalsIgnoreCase("double") || parafld.getType().getName().equalsIgnoreCase("float")) editor.getItem().setText(col, String.valueOf(Convert.toDouble(text.getText())));
					else editor.getItem().setText(col, text.getText());
				}
			});
			newEditor.selectAll();
			newEditor.setFocus();
		}
	}
	
	void initButton()
	{
		btnClose.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent selectionevent) 
		    {
		    	shell.close();
		    	shell.dispose();
		    }
		});
		
		btnExport.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent selectionevent) 
		    {
		    	Class classInst = GlobalInfo.sysPara.getClass();
		    	Field[] flds = classInst.getDeclaredFields();
		    	
		    	Class descInst = paradesc.getClass();
		    	Field[] descflds = descInst.getDeclaredFields();
		    	
		    	try
		    	{
			    	java.io.FileWriter f = new java.io.FileWriter("syspara.txt",false);
			    	for (int i=0;i<flds.length;i++)
			    	{
						try
						{
				    		GlobalParaDesc para = null;
				    		
							// 得到参数描述定义
				    		int j=0;
				    		for (;j<descflds.length;j++)
				    		{
				    			if (flds[i].getName().equalsIgnoreCase(descflds[j].getName())) break;
				    		}
				    		if (j < descflds.length)
				    		{
				    			para = (GlobalParaDesc)descflds[j].get(paradesc);
				    		}
				    		
				    		// 写入文件
				    		StringBuffer sb = new StringBuffer();
				    		sb.append("insert into syspara(code,name,value,memo) values(");
				    		sb.append("'#"+flds[i].getName()+"',");
				    		sb.append("'"+(para!=null?para.valdesc:"")+"',");
				    		if (flds[i].getType().getName().equalsIgnoreCase("boolean"))
							{
								boolean obj = flds[i].getBoolean(GlobalInfo.sysPara);
								if (obj) sb.append("'Y',");
								else sb.append("'N',");
							}
							else
							{
								Object obj = flds[i].get(GlobalInfo.sysPara);
								if (obj != null) sb.append("'"+String.valueOf(obj)+"',");
								else sb.append("'',");
							}
				    		sb.append("'");
				    		if (para != null && para.valdata != null)
				    		{
				    			for (j=0;j<para.valdata.length;j++)
				    			{
				    				if (para.valdata[j][0] != null && !para.valdata[j][0].equals(""))
				    				{
				    					sb.append(para.valdata[j][0]+"-"+para.valdata[j][1]);
				    				}
				    				else
				    				{
				    					sb.append(para.valdata[j][1]);
				    				}
				    				if (j < para.valdata.length - 1) sb.append("/");
				    			}
				    		}
				    		sb.append("');\r\n");
							f.write(sb.toString());
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
			    	}
			    	f.close();
			    	
			    	Runtime.getRuntime().exec("notepad syspara.txt");
		    	}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
		    }
		});
		
		if (ConfigClass.isDeveloperMode()) btnSave.setEnabled(true);
		else btnSave.setEnabled(false);
		btnSave.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent selectionevent) 
		    {
		    	for (int i=0;i<table.getItemCount();i++)
		    	{
		    		TableItem it = table.getItem(i);
		    		if (it == null) continue;
		    		GlobalInfo.sysPara.paraConvertByCode("#"+it.getText(2),it.getText(1),it.getText(0));
		    	}
		    	
		    	new MessageBox(Language.apply("参数配置已保存,请在收银系统中试用参数设置变化"));
		    }
		});
	}
}
