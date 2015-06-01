package com.efuture.javaPos.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class TableTestEvent {
    TableEditor editor;
    private int[] currentPoint = new int[] { 0, 1 };
    private Table table = null;
    private Text newEditor = null;
    
    public TableTestEvent(TableTest preForm)
    {
    	table =  preForm.table;
        editor                     = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal      = true;
        editor.minimumWidth        = 50;
        
        TableItem item = null;
        String[] row = null;

        Combo combo = new Combo(table, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		combo.setText("0");
		combo.setItems(new String[]{"1","2"});
		
        for (int i = 0; i < 3; i++)
        {
            item = new TableItem(table, SWT.NULL);
            row  = new String[]{"10","10"};

            for (int j = 0; j < row.length; j++)
            {
                item.setText(j, row[j]);
                
            }
        }
        
        

        
    	table.addMouseListener(new Mouse());
    	table.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
    }
    
    public void findLocation()
    {
        Control oldEditor = editor.getEditor();

        if (oldEditor != null)
        {
            oldEditor.dispose();
        }

        if (table.getItemCount() <= 0)
        {
            return;
        }

        TableItem item = table.getItem(currentPoint[0]);

        if (item == null)
        {
            return;
        }

        Combo combo = new Combo(table, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		combo.setText("0");
		combo.setItems(new String[]{"1","2"});
        newEditor = new Text(table, SWT.LEFT | SWT.BORDER);
        newEditor.setTextLimit(100);

        newEditor.setText(item.getText(currentPoint[1]));
        editor.setEditor(combo, item, currentPoint[1]);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
        newEditor.selectAll();
        combo.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
            	Combo text = (Combo) editor.getEditor();
                editor.getItem().setText(currentPoint[1], text.getText());
            }
        });

        newEditor.setFocus();
    }
    
    class Mouse implements MouseListener
    {

		public void mouseDoubleClick(MouseEvent e) {
			// TODO 自动生成方法存根

		}

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
			    	 if (!item.getText(1).equals(" "))
			    	 {
				    	 currentPoint[0] = index;
				    	 currentPoint[1] = 1;
				    	 findLocation();
			    	 }
			    	 return ; 
			     }
			     index ++;
			 }
		}

		public void mouseUp(MouseEvent e) {
			// TODO 自动生成方法存根
			
		}
    	
    }
}
