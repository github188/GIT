package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.Design.ApportPaymentForm;
import com.swtdesigner.SWTResourceManager;


public class ApportPaymentEvent
{
    public Table table = null;
    public Label label = null;
    TableEditor editor;
    private Text newEditor = null;
    private int[] currentPoint = new int[] { 0, 4 };
    public double allftje = 0;
    public double leftje = 0;
    public Label leftje_lbl;
    public Shell shell;
    Vector tableInfo;
    
    boolean readonly = false;

    public ApportPaymentEvent(ApportPaymentForm form, Vector v, String info, double je, boolean mreadonly)
    {
    	readonly = mreadonly;
    	
        table           = form.table;
        
        //      增加监听器
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
        
        table.addKeyListener(key);
        
        label           = form.label;
        this.allftje    = je;
        this.leftje     = je;
        this.leftje_lbl = form.label_2;
        this.shell      = form.shell;

        editor                     = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal      = true;
        editor.minimumWidth        = 50;

        tableInfo = v;

        TableItem item = null;
        String[] row = null;

        for (int i = 0; i < tableInfo.size(); i++)
        {
            item = new TableItem(table, SWT.NULL);
            row  = (String[]) tableInfo.elementAt(i);

            // tableInfo = 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
            item.setText(0,String.valueOf(Convert.toInt(row[5])+1));
            item.setText(1,"["+row[0]+"]"+row[1]);
            item.setText(2,row[2]);
            item.setText(3,row[3]);
            item.setText(4,row[4]);
        }

        label.setText(info);
        showFtje();
    }

    public void showFtje()
    {
    	leftje = 0;
        for (int i = 0; i < table.getItemCount(); i++)
        {
           leftje += Convert.toDouble(table.getItem(i).getText(4));
        }
        leftje = ManipulatePrecision.doubleConvert(allftje - leftje);
    	leftje_lbl.setText(ManipulatePrecision.doubleToString(this.leftje));
    }
    
    public void findLocation()
    {
    	if (readonly) return;
    	
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

        if (item.getText(currentPoint[1]).length() > 0)
        {
            newEditor.setText(item.getText(currentPoint[1]));
        }
        else
        {
            newEditor.setText(String.valueOf(leftje));
        }

        editor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
        newEditor.selectAll();

        /**
        newEditor.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                Text text = (Text) editor.getEditor();
                editor.getItem().setText(currentPoint[1], text.getText());
            }
        });*/

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
        key.inputMode = key.DoubleInput;

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

                    TableItem item = table.getItem(currentPoint[0]);
                    double ftye = leftje + Convert.toDouble(item.getText(currentPoint[1])); 

                    if (((Text)e.widget).getText().length() <= 0)
                    {
                        new MessageBox(Language.apply("请输入分摊金额"));

                        return;
                    }
                    double ftje = Convert.toDouble(((Text)e.widget).getText());
                    if (ftje < 0)
                    {
                        new MessageBox(Language.apply("输入分摊金额必须大于等于0"));

                        return;
                    }

                    if (ftje > ftye)
                    {
                        new MessageBox(Language.apply("输入分摊金额必须小于分摊余额\n请确认后重新输入"));

                        return;
                    }

                    double limitje = Convert.toDouble(item.getText(3));
                    if (ftje > limitje)
                    {
                        new MessageBox(Language.apply("输入分摊金额必须小于商品可分摊金额\n请确认后重新输入"));

                        return;
                    }

                    item.setText(currentPoint[1], ManipulatePrecision.doubleToString(ftje));

                    //leftje = ManipulatePrecision.sub(ftye, ftje);
                    //this.leftje_lbl.setText(ManipulatePrecision.doubleToString(this.leftje));
                    showFtje();
                    if ((leftje <= 0) && (new MessageBox(Language.apply("分摊金额已经分配完毕，确认是否进行下笔付款"), null, true).verify() == GlobalVar.Key1))
                    {
                        saveInfo();
                        shell.close();
                        shell.dispose();
                    }
                    else
                    {
                        NewKeyListener.sendKey(GlobalVar.ArrowDown);
                    }

                    break;

                case GlobalVar.Exit:
                    if (leftje > 0)
                    {
                    	new MessageBox(Language.apply("还有付款金额未分摊完毕,请继续分摊!"));
                    }
                    else
                    {
                        saveInfo();
                        shell.close();
                        shell.dispose();
                    }

                    break;
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void saveInfo()
    {
        for (int i = 0; i < table.getItemCount(); i++)
        {
            ((String[]) tableInfo.elementAt(i))[4] = table.getItem(i).getText(4);
        }
    }
}
