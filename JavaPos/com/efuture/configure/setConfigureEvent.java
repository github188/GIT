package com.efuture.configure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.defineKey.MessageDiagram;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class setConfigureEvent
{
    TableEditor editor;
    private int[] currentPoint = new int[] { 0, 1 };
    private Table table = null;
    private Shell shell = null;
    private Text newEditor = null;
    private Combo combo = null;
    private Text text = null;
    private Button choBtn = null;
    private Button valBtn = null;
    private Button saveBtn = null;
    private String curOpen = null;
    String[] items = { "PosID.ini", "Config.ini", "DeviceName.ini", "Update.ini" };
    String[] names = { Language.apply("收银机号"), Language.apply("收银机参数"), Language.apply("设备参数"), Language.apply("更新配置") };

    public setConfigureEvent(setConfigureForm preForm)
    {
        this.table   = preForm.table;
        this.shell   = preForm.shell;
        this.combo   = preForm.combo;
        this.text    = preForm.text;
        this.choBtn  = preForm.button;
        this.saveBtn = preForm.button_1;
        this.valBtn  = preForm.button_2;

        this.combo.setText(Language.apply("文件名"));
        this.combo.setItems(names);

        selectEvent se = new selectEvent();
        this.combo.addSelectionListener(se);
        this.choBtn.addSelectionListener(se);
        this.valBtn.addSelectionListener(se);
        this.saveBtn.addSelectionListener(se);

        Key key = new Key();

        this.combo.addKeyListener(key);
        this.choBtn.addKeyListener(key);
        this.valBtn.addKeyListener(key);
        this.saveBtn.addKeyListener(key);
        this.text.addKeyListener(key);
        this.table.addKeyListener(key);

        table.addMouseListener(new Mouse());

        editor                     = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal      = true;
        editor.minimumWidth        = 50;

        Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
        shell.setLocation((rec.width / 2) - (shell.getSize().x / 2), (rec.height / 2) - (shell.getSize().y / 2));
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

        newEditor                  = new Text(table, SWT.LEFT | SWT.BORDER);
        newEditor.setTextLimit(2000);

        newEditor.setText(item.getText(currentPoint[1]));
        editor.setEditor(newEditor, item, currentPoint[1]);
        newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
        newEditor.selectAll();
        newEditor.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    Text text = (Text) editor.getEditor();
                    editor.getItem().setText(currentPoint[1], text.getText());
                }
            });

        newEditor.addKeyListener(new tableKey());

        newEditor.setFocus();
    }

    public Vector read_File(String fileName)
    {
        BufferedReader br = null;

        br = CommonMethod.readFile(fileName);

        if (br == null)
        {
            return null;
        }

        Vector v = new Vector();
        String line;
        String[] content = null;

        try
        {
            while ((line = br.readLine()) != null)
            {
                content = new String[3];

                line = line.trim();
                if (line.indexOf('[') >= 0) line = ManipulateStr.delSpecialChar(line);
                if ((line == null) || (line.trim().length() <= 0))
                {
                    continue;
                }

                //System.out.println(line);
                String[] lines = new String[2];

                if (line.indexOf("&&") < 0)
                {
                    lines[0] = line;
                    lines[1] = null;
                }
                else
                {
                    lines[0] = line.substring(0, line.indexOf("&&"));
                    lines[1] = line.substring(line.indexOf("&&") + 2);
                }

                if (lines[1] == null)
                {
                    content[2] = null;
                }
                else
                {
                    content[2] = lines[1].trim();
                }

                if (lines[0].indexOf("=") < 0)
                {
                    content[0] = lines[0].trim();
                    content[1] = null;
                }
                else
                {
                    content[0] = lines[0].substring(0, lines[0].indexOf("=")).trim();
                    content[1] = lines[0].substring(lines[0].indexOf("=") + 1).trim();
                }

                v.add(content);
            }
        }
        catch (IOException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }

        return v;
    }

    public boolean saveData()
    {
        PrintWriter pw = null;

        try
        {
            if (table.getItemCount() > 0)
            {
                pw = CommonMethod.writeFileUTF(curOpen);

                for (int i = 0; i < table.getItemCount(); i++)
                {
                    TableItem item = table.getItem(i);
                    String line = Convert.appendStringSize("", item.getText(0).trim(), 0, 20, 20);

                    if (line.charAt(0) != '[')
                    {
                        line += " = ";
                    }

                    if (item.getText(1) == null || item.getText(1).trim().equals(""))
                    {
                        line += Convert.appendStringSize("", "", 0, 60, 60);
                    }
                    else
                    {
                    	int ilenth = Convert.countLength(item.getText(1).trim());
                    	if (ilenth <= 60)
                    	{
                    		line += ("" + Convert.appendStringSize("", item.getText(1).trim(), 0, 60, 60));
                    	}
                    	else
                    	{
                    		line += ("" + Convert.appendStringSize("", item.getText(1).trim(), 0, ilenth + 10, ilenth + 10));
                    	}
                    }

                    if ((item.getText(2) != null) && (item.getText(2).trim().length() > 0))
                    {
                        line += (" && " + item.getText(2).trim());
                    }

                    pw.println(line);
                }

                return true;
            }

            return false;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
        finally
        {
            if (pw != null)
            {
                pw.close();
            }
        }
    }

    class Key implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
        }

        public void keyReleased(KeyEvent e)
        {
            try
            {
                if (e.keyCode == SWT.ESC)
                {
                    MessageDiagram me = new MessageDiagram(shell);
                    me.open(Language.apply("你确定现在是否直接退出程序"), true);

                    if (me.getDone() == 1)
                    {
                        shell.close();
                        shell.dispose();

                        return;
                    }
                }

                if (e.widget.equals(table))
                {
                    TableItem item = table.getItem(table.getSelectionIndex());

                    if ((e.keyCode == SWT.ARROW_RIGHT) && !item.getText(1).equals(" "))
                    {
                        currentPoint[0] = table.getSelectionIndex();
                        findLocation();
                    }
                }
                else if (e.widget.equals(combo))
                {
                    if ((e.keyCode == SWT.ARROW_UP) || (e.keyCode == SWT.ARROW_DOWN))
                    {
                        int i = combo.getSelectionIndex();
                        String info = items[i];
                        text.setText(GlobalVar.ConfigPath + "/" + info);
                    }

                    if (e.keyCode == 13)
                    {
                        text.setFocus();
                        text.selectAll();
                    }
                }
                else if (e.widget.equals(text))
                {
                    if (e.keyCode == 13)
                    {
                        if (text.getText().trim().length() <= 0)
                        {
                            new MessageDiagram(shell).open(Language.apply("请先输入打开的文件\n 或\n确认输入的文件是否合法 "), false);

                            return;
                        }

                        Vector v = read_File(text.getText());

                        if (v == null)
                        {
                            new MessageDiagram(shell).open(Language.apply("未找到此文件，请确定后重新输入"), false);

                            return;
                        }

                        if (v.size() > 0)
                        {
                            curOpen = text.getText();
                            table.removeAll();

                            for (int i = 0; i < v.size(); i++)
                            {
                                String[] row = (String[]) v.elementAt(i);

                                System.out.println(i + "         " + row[0]);

                                TableItem item = new TableItem(table, SWT.LEFT);
                                item.setText(0, row[0]);

                                //如果 数值为 " " 代表 此格不能输入值
                                if (row[0].trim().charAt(0) == '[')
                                {
                                    item.setText(1, " ");
                                }
                                else
                                {
                                    if (row[1] != null)
                                    {
                                        item.setText(1, row[1]);
                                    }

                                    if (row[2] != null)
                                    {
                                        item.setText(2, row[2]);
                                    }
                                }
                            }

                            table.setFocus();
                            table.setSelection(0);
                        }
                    }
                }

                /**
                    else if (e.widget.equals(choBtn))
                    {
                            CommonMethod.openFileDialog(shell,text);
                    }
                    else if (e.widget.equals(saveBtn))
                    {
                            if (saveData())
                            {
                                    new MessageDiagram(shell).open("保存成功", false);
                            }
                    }*/
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }

    class tableKey implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
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

                            if (table.getItem(currentPoint[0]).getText(1).equals(" "))
                            {
                                keyPressed(e);

                                return;
                            }
                        }

                        table.setSelection(currentPoint[0]);
                        findLocation();

                        break;

                    case SWT.ARROW_DOWN:

                        if (currentPoint[0] == (table.getItemCount() - 1))
                        {
                            return;
                        }
                        else
                        {
                            currentPoint[0]++;

                            if (table.getItem(currentPoint[0]).getText(1).equals(" "))
                            {
                                keyPressed(e);

                                return;
                            }
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

        public void keyReleased(KeyEvent e)
        {
            try
            {
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }

    class Mouse implements MouseListener
    {
        public void mouseDoubleClick(MouseEvent e)
        {
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

                    return;
                }

                index++;
            }
        }

        public void mouseUp(MouseEvent e)
        {
            // TODO 自动生成方法存根
        }
    }

    class selectEvent implements SelectionListener
    {
        public void widgetDefaultSelected(SelectionEvent e)
        {
            // TODO 自动生成方法存根
        }

        public void widgetSelected(SelectionEvent e)
        {
            // TODO 自动生成方法存根
            if (e.widget.equals(combo))
            {
                int i = combo.getSelectionIndex();
                if (i >= 0)
                {
	                String info = items[i];
	                text.setText(GlobalVar.ConfigPath + "/" + info);
                }
            }
            else if (e.widget.equals(choBtn))
            {
                CommonMethod.openFileDialog(shell, text);

                if (text.getText().length() > 0)
                {
                    e.widget = valBtn;
                    widgetSelected(e);
                }
            }
            else if (e.widget.equals(valBtn))
            {
                if (text.getText().trim().length() <= 0)
                {
                    new MessageDiagram(shell).open(Language.apply("请先输入打开的文件\n 或\n确认输入的文件是否合法 "), false);

                    return;
                }

                Vector v = read_File(text.getText());

                if (v == null)
                {
                    new MessageDiagram(shell).open(Language.apply("未找到此文件，请确定后重新输入"), false);

                    return;
                }

                if (v.size() > 0)
                {
                    curOpen = text.getText();
                    table.removeAll();

                    for (int i = 0; i < v.size(); i++)
                    {
                        String[] row = (String[]) v.elementAt(i);

                        System.out.println(i + "         " + row[0]);

                        TableItem item = new TableItem(table, SWT.LEFT);
                        item.setText(0, row[0]);

                        //如果 数值为 " " 代表 此格不能输入值
                        if (row[0].trim().charAt(0) == '[')
                        {
                            item.setText(1, " ");
                        }
                        else
                        {
                            if (row[1] != null)
                            {
                                item.setText(1, row[1]);
                            }

                            if (row[2] != null)
                            {
                                item.setText(2, row[2]);
                            }
                        }
                    }
                }
            }
            else if (e.widget.equals(saveBtn))
            {
                if (saveData())
                {
                    new MessageDiagram(shell).open(Language.apply("保存成功"), false);
                }
                else
                {
                    new MessageDiagram(shell).open(Language.apply("确定是否已经打开文件"), false);
                }
            }
        }
    }
}
