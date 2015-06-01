package com.efuture.commonKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class ProgressBox
{
    Label label = null;
    Shell shell = null;
    Control focus = null;
    Image bkimg = null;
    
    public ProgressBox()
    {
    	focus = Display.getDefault().getFocusControl();
        shell = new Shell(GlobalVar.style_linux);
        shell.setLayout(new FormLayout());
        
        shell.setSize(500, 105);
        
        //Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2),
                          (GlobalVar.rec.y / 2) - (shell.getSize().y / 2));
        shell.setText("SWT Application");
        //
        label = new Label(shell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 65);
        formData.top = new FormAttachment(0, 35);
        formData.right = new FormAttachment(0, 475);
        formData.left = new FormAttachment(0, 20);
        label.setLayoutData(formData);

        label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        label.setText(Language.apply("正在执行操作，请等待....."));
        
        // 加载背景图片
        bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        shell.open();
        shell.setFocus();
        
        // 刷新
        while (Display.getDefault().readAndDispatch());
    }
    
    public ProgressBox(int x,int y)
    {
    	
    	focus = Display.getDefault().getFocusControl();
        shell = new Shell(GlobalVar.style_linux);
        shell.setLayout(new FormLayout());
        
        shell.setSize(500, 105);
        
        //Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
        shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2 - x),
                          (GlobalVar.rec.y / 2) - (shell.getSize().y / 2) - y);
        shell.setText("SWT Application");
        //
        label = new Label(shell, SWT.NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 65);
        formData.top = new FormAttachment(0, 35);
        formData.right = new FormAttachment(0, 475);
        formData.left = new FormAttachment(0, 20);
        label.setLayoutData(formData);

        label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        label.setText(Language.apply("正在执行操作，请等待....."));
        
        // 加载背景图片
        bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        shell.open();
        shell.setFocus();
        
        // 刷新
        while (Display.getDefault().readAndDispatch());
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
    }

    public void setText(String arg)
    {
        label.setText(arg);
        label.getDisplay().update();
        //while (Display.getDefault().readAndDispatch());
    }

    public Label getLabel()
    {
    	return label;
    }
    
    public void close()
    {
        shell.close();
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        if (focus != null)
        {
        	focus.setFocus();
        }
        
        while (Display.getDefault().readAndDispatch());
    }

    public void waitClose()
    {
        final Display display = Display.getDefault();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        if (bkimg != null)
        {
        	bkimg.dispose();
        }
    }
}
