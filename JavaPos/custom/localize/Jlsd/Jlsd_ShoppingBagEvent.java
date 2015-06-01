package custom.localize.Jlsd;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Jlsd.Jlsd_ShoppingBagForm;


public class Jlsd_ShoppingBagEvent extends NewKeyListener
{
    TableEditor editor;
    private int[] currentPoint = new int[] { 0, 1 };
    private Table table = null;
    private Shell shell = null;
    private Text newEditor = null;
    private Jlsd_ShoppingBagBS jlsdBS = null;
    
    public Jlsd_ShoppingBagEvent(Jlsd_ShoppingBagForm shoppingForm)
    {
        this.table = shoppingForm.table;
        this.shell = shoppingForm.shell;
        
        shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
                (GlobalVar.rec.y - shell.getSize().y) / 2,
                shell.getSize().x,
                shell.getSize().y - GlobalVar.heightPL);
        
        shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2,
                (GlobalVar.rec.y - shell.getSize().y) / 2,
                shell.getSize().x,
                shell.getSize().y - GlobalVar.heightPL);
        
        jlsdBS = CustomLocalize.getDefault().createJlsd_ShoppingBagBS();

        editor                     = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal      = true;
        editor.minimumWidth        = 50;

        table.addMouseListener(new MouseAdapter()
        {
            public void mouseDown(MouseEvent mouseevent) 
            {
            	Point selectedPoint = new Point (mouseevent.x, mouseevent.y);
            	Table table = (Table)mouseevent.getSource();
				int index = table.getTopIndex ();
				if (index < 0 ) return;
				while (index < table.getItemCount()) 
				{
					TableItem item = table.getItem (index);
					for (int i=0; i < table.getColumnCount(); i++) 
					{
						Rectangle rect = item.getBounds(i);
						if (i == 1 && rect.contains (selectedPoint)) 
						{
							currentPoint[0] = index;
							currentPoint[1] = i;
							table.setSelection(currentPoint[0]);
                            findLocation();
                            return;
						}
					}
					index++;
				}
            }
        });
        
        // 读取上次输入的购物袋数量
        Vector tableInfo = jlsdBS.getTableInfo(shoppingForm.saleGoodsDef);
        TableItem item= null;
        String[] row = null;
        
        if (tableInfo.size() <= 0)
        {
        	shoppingForm.shell.dispose();
        	
        	return ;
        }
        
        for (int i = 0; i < tableInfo.size(); i++)
        {
            item = new TableItem(table, SWT.NULL);
            row  = (String[]) tableInfo.elementAt(i);

            for (int j = 0; j < row.length; j++)
            {
            	if(!row[j].equals("") && row[j].equals("D")){
            		row[j] = "大袋";
				}else if(!row[j].equals("") && row[j].equals("Z")){
					row[j] = "中袋";
				}else if(!row[j].equals("") && row[j].equals("X")){
					row[j] = "小袋";
				}
                item.setText(j, row[j]);
            }
        }
        

        //生成刚加入tableItem
        table.redraw();
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

        newEditor = new Text(table, SWT.RIGHT | SWT.BORDER);
        newEditor.setTextLimit(12);

        newEditor.setText(item.getText(currentPoint[1]));
        editor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
        newEditor.selectAll();
        newEditor.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                Text text = (Text) editor.getEditor();
                // 数量输入限制为5位
                text.setTextLimit(5);
                editor.getItem().setText(currentPoint[1], text.getText());
            }
        });

        // 增加监听器
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
        key.inputMode = key.IntegerInput;

        newEditor.addKeyListener(key);

        newEditor.setFocus();
        
    }

    public void keyPressed(KeyEvent e, int key)
    {
    	 
        try
        {              	
            switch (key)
            {
                case GlobalVar.ArrowUp:

                    if (currentPoint[0] == 0)
                    {
                        return;
                    }
                    else
                    {
                        currentPoint[0]--;
                    }

                    table.setSelection(currentPoint[0]);
                    findLocation();

                    break;

                case GlobalVar.ArrowDown:

                    if (currentPoint[0] == (table.getItemCount() - 1))
                    {
                        return;
                    }
                    else
                    {
                        currentPoint[0]++;
                    }

                    table.setSelection(currentPoint[0]);
                    findLocation();

                    break;
            }
            
            

        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {
            	case GlobalVar.Enter:
            		if (currentPoint[0] >= table.getItemCount() - 1)
            		{
            			if (saveData())
            			{
    	                    shell.close();
    	                    shell.dispose();
            			}
            		}
            		else
            		{
            			keyPressed(e,GlobalVar.ArrowDown);
            		}
            		break;
            	case GlobalVar.Validation:
            		if (currentPoint[1] > 0 && saveData())
            		{
	                    shell.close();
	                    shell.dispose();
            		}
            		break;
            	case GlobalVar.Exit:
                    shell.close();
                    shell.dispose();            		
            		break;
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
    
    public boolean saveData()
    {
 		try
 		{
            TableItem[] items = table.getItems();
            Vector tableInfo = new Vector();
            String[] jlsd_Pay = null;

            for (int i = 0; i < items.length; i++)
            {
                jlsd_Pay    = new String[2];
                jlsd_Pay[0] = items[i].getText(0);
                jlsd_Pay[1] = items[i].getText(1);

                if (jlsd_Pay[1] == null)
                {
                    jlsd_Pay[1] = "";
                }

                tableInfo.add(jlsd_Pay);
            }

            //保存购物袋信息
            return jlsdBS.saveJlsd_Money(tableInfo);
            
 		}
        catch (Exception er)
        {
            er.printStackTrace();
            new MessageBox("购物袋数量保存出现异常!\n请确定输入的数量是否合法!", null, false);
            return false;
        }           	
    }
}
