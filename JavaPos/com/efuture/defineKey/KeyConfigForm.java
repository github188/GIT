package com.efuture.defineKey;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.KeyBoard;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.KeyPadSet;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.swtdesigner.SWTResourceManager;


public class KeyConfigForm
{
    private Label label_1;
    private Table table;
    TableEditor editor;
    private int[] currentPoint = new int[] { 0, 1 };
    KeyPadSet keypad = null;
    Shell sShell = null;
    private Text newEditor = null;
    
    public KeyConfigForm(Display display, int style) {
    	CreateKeyConfigForm(display, style, true);
	}
    
    public KeyConfigForm(Display display, int style, boolean flag) {
    	CreateKeyConfigForm(display, style, flag);
	}

    /**
     * Create the shell
     *
     * @param display
     * @param style
     * @param flag(区别是否单独运行键盘定义程序的标识)
     */
    public void CreateKeyConfigForm(Display display, int style, boolean flag)
    {
    	KeyBoard key = null;
    	//单独运行键盘配置程序的场合，需要执行以下程序
    	if(flag) 
    	{
	        // 载入配置文件
	        if (!ConfigClass.LoadConfigSet())
	        {
	            PublicMethod.forceQuit();
	        }
	        
	        //读取设配逻辑名
	    	new DeviceName();

    	
	        key = new KeyBoard(ConfigClass.KeyBoard1);
	         
	        if (key.isValid())
	        {
	            if (!key.open())
	            {
	                new MessageBox(Language.apply("专业键盘设备初始化失败!"));
	            }
	            else
	            {
	                // 开机即启用键盘设备
	                key.setEnable(true);
	            }
	        }
    	}
        keypad = new KeyPadSet(GlobalVar.KeyFile);

        keypad.loadFile();

        sShell = new Shell(style);
        sShell.setLayout(new FormLayout());
        createContents();

        Rectangle area = display.getPrimaryMonitor().getBounds();

        sShell.setBounds((area.width / 2) - (650 / 2),
                         (area.height / 2) - (500 / 2), 650, 500);
        sShell.open();
        updateTable();

        findLocation();
        sShell.setActive();

        if (newEditor != null)
        {
            newEditor.setFocus();
        }

        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        //单独运行键盘配置程序的场合，需要执行以下程序
        if(flag)
        {
	        key.close();
	        System.exit(0);
        }
    }

    /**
     * Launch the application
     *
     * @param
    args
     */
    public static void main(String[] args)
    {
        try
        {
            Display display = Display.getDefault();
            
            if (args.length > 0)
            {
                GlobalVar.RefushConfPath(args[0].trim());
            }            
            
            new KeyConfigForm(display, GlobalVar.style_linux);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
		SWTResourceManager.dispose();
		System.exit(0);
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        sShell.setText("SWT Application");

        table = new Table(sShell, SWT.BORDER);

        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(100, -79);
        formData.right  = new FormAttachment(100, -40);
        formData.top    = new FormAttachment(0, 27);
        formData.left   = new FormAttachment(0, 46);
        table.setLayoutData(formData);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        String[] titles = { Language.apply("按键名称"), Language.apply("按键1"), Language.apply("按键2") };
        int[] width = new int[] { 220, 158, 158 };

        for (int loopIndex = 0; loopIndex < titles.length; loopIndex++)
        {
            TableColumn column = new TableColumn(table, SWT.NULL);
            column.setText(titles[loopIndex]);
            column.setWidth(width[loopIndex]);
        }

        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        //ConfigClass.DebugMode = true;

        PublicMethod.DEBUG_MSG("getKeypad           " + keypad.getValidKey());
        
        label_1 = new Label(sShell, SWT.NONE);

        final FormData formData_1 = new FormData();
        formData_1.right  = new FormAttachment(100, -29);
        formData_1.bottom = new FormAttachment(100, -11);
        formData_1.top    = new FormAttachment(100, -53);
        formData_1.left   = new FormAttachment(0, 46);
        label_1.setLayoutData(formData_1);
        label_1.setText(Language.apply("Enter:修改键 Delete:删除键  F10:保存退出 ESC：直接退出"));
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
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
						if ((i == 1 || i == 2) && rect.contains (selectedPoint)) 
						{
							currentPoint[0] = index;
							currentPoint[1] = i;
							table.setSelection(currentPoint[0]);
                            findLocation();
                            return;
						}
						
						if (i == 0 && rect.contains (selectedPoint))
						{
							currentPoint[0] = index;
							currentPoint[1] = 1;
							table.setSelection(currentPoint[0]);
                            findLocation();
                            return;
						}
					}
					index++;
				}
            }
        });
    }

    public void updateTable()
    {
        table.removeAll();

        String[][] key = keypad.getKey();

        for (int i = 0; i < keypad.getValidKey(); i++)
        {
            TableItem item = new TableItem(table, SWT.NULL);
            String s = key[i][1].trim();
            if (s.length() < 3) s = "000".substring(0,3-s.length()) + s; 
            item.setText(0, "["+s+"] "+key[i][0].trim());
            item.setText(1,
                         KeyCharExchange.keyexchange_new(key[i][2]));
            item.setText(2,
                         KeyCharExchange.keyexchange_new(key[i][3]));
        }

        table.setSelection(currentPoint[0]);
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

        newEditor = new Text(table, SWT.NONE);

        newEditor.setText(item.getText(currentPoint[1]));
        editor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));

        newEditor.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    Text text = (Text) editor.getEditor();
                    editor.getItem().setText(currentPoint[1], text.getText());
                }
            });

        newEditor.addKeyListener(new KeyListener()
            {
                public void keyPressed(KeyEvent e)
                {
                    e.doit = false;
                    label_1.setText(Language.apply("Enter:修改键 Delete:删除键 F10:保存退出 ESC：直接退出"));

                    try
                    {
                        switch (e.keyCode)
                        {
                            case SWT.ARROW_UP:

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

                            case SWT.ARROW_DOWN:

                                if (currentPoint[0] == (table.getItemCount() -
                                                           1))
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

                            case SWT.ARROW_LEFT:
                                if (currentPoint[1] == 0)
                                {
                                    return;
                                }
                                else
                                {
                                    currentPoint[1]--;
                                }

                                findLocation();

                                break;

                            case SWT.ARROW_RIGHT:

                                if (currentPoint[1] == 2)
                                {
                                    return;
                                }
                                else
                                {
                                    currentPoint[1]++;
                                }

                                findLocation();

                                break;
                        }
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

                public void keyReleased(KeyEvent e)
                {
                    String name = null;

                    try
                    {
                        if ((e.keyCode == 13 || e.keyCode == 16777296) && (currentPoint[1] > 0))
                        {
                            inputText window = null;
                            name   = table.getItem(currentPoint[0]).getText(0).substring(6);
                            window = new inputText(" '" + name + "' ", null,
                                                   keypad,currentPoint[0]);
                            window.open();

                            if (window.getReturn().trim().length() <= 0)
                            {
                                return;
                            }

                            keypad.setKeypad(currentPoint[0],
                                             currentPoint[1] + 1,
                                             window.getReturn());
                            updateTable();
                            findLocation();
                        }
                        else if ((e.keyCode == SWT.DEL) &&
                                     (newEditor.getText().length() > 0) &&
                                     (currentPoint[1] > 0))
                        {
                            MessageDiagram window1 = null;

                            try
                            {
                                window1 = new MessageDiagram(sShell);
                                window1.open(Language.apply("是否消除当前键值"), true);

                                if (window1.getDone() == 1)
                                {
                                    keypad.setKeypad(currentPoint[0],
                                                     currentPoint[1] + 1, "0");
                                    newEditor.setText("");
                                }

                                newEditor.setFocus();
                            }
                            catch (Exception er)
                            {
                                er.printStackTrace();
                            }
                            finally
                            {
                                window1 = null;
                            }
                        }
                        else if (e.keyCode == SWT.ESC)
                        {
                            MessageDiagram window1 = null;

                            try
                            {
                                window1 = new MessageDiagram(sShell);
                                window1.open(Language.apply("是否放弃当前设置不保存"), true);

                                if (window1.getDone() == 1)
                                {
                                    sShell.close();
                                    sShell.dispose();
                                }
                                else
                                {
                                    newEditor.setFocus();
                                }
                            }
                            catch (Exception er)
                            {
                                er.printStackTrace();
                            }
                            finally
                            {
                                window1 = null;
                            }
                        }
                        else if (e.keyCode == SWT.F10)
                        {
                            MessageDiagram window1 = null;

                            try
                            {
                                window1 = new MessageDiagram(sShell);
                                window1.open(Language.apply("是否保存键值并退出"), true);

                                if (window1.getDone() == 1)
                                {
                                    keypad.writeFile();
                                    
                                    sShell.close();
                                    sShell.dispose();
                                }
                                else
                                {
                                    newEditor.setFocus();
                                }
                            }
                            catch (Exception er)
                            {
                                er.printStackTrace();
                            }
                            finally
                            {
                                window1 = null;
                            }
                        }
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();
                    }
                    finally
                    {
                        name = null;
                    }
                }
            });
        newEditor.selectAll();
        newEditor.setFocus();
    }
}
