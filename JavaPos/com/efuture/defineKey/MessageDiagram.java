package com.efuture.defineKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class MessageDiagram
{
    protected Shell shell;
    private int done = -1;

    public MessageDiagram(Shell parent)
    {
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            MessageDiagram window = new MessageDiagram(null);
            window.open("aaa",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open(String com,boolean flag)
    {
        final Display display = Display.getDefault();
        createContents(com,flag);
        
        Rectangle area = display.getPrimaryMonitor().getBounds();
        
        shell.setBounds((area.width / 2) - (405 / 2),
                         (area.height / 2) - (172 / 2), 405, 172);
        shell.open();
        shell.layout();
        shell.setActive();
        shell.setFocus();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    public int getDone()
    {
        return done;
    }

    /**
     * Create contents of the window
     */
    protected void createContents(String com,final boolean flag)
    {
        shell = new Shell(GlobalVar.style_linux);
        
        shell.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                }
                
                public void keyReleased(KeyEvent e) {
                    if (!flag)
                    {
                        shell.close();
                        shell.dispose();
                    }
                    
                    if (e.keyCode == 121)
                    {
                        done = 1;
                        shell.close();
                        shell.dispose();
                    }
                    else if (e.keyCode == 110)
                    {
                        shell.close();
                        shell.dispose();
                    }
                }
                
                
            });
        shell.setLayout(new FormLayout());
        
        
        shell.setText(Language.apply("警告"));

        final Label label = new Label(shell, SWT.NONE);
        label.setAlignment(SWT.CENTER);

        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 170);
        formData.left = new FormAttachment(0, 45);
        formData.right  = new FormAttachment(100, -52);
        formData.top    = new FormAttachment(0, 25);
        label.setLayoutData(formData);
        
        
        if (flag)
        	com+=Language.apply("\n\n\n Y - 确 定  /  N - 取消  ");
        else
        	com+=Language.apply("\n\n\n 按任意键退出此窗口");
        
        label.setText(com);

        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
    }
}
