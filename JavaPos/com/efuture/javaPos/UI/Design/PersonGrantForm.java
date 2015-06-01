package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.PersonGrantEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class PersonGrantForm
{
    public Text text_1;
    public Text text;
    public Shell shell;
    boolean done = false;
    OperUserDef staff = null;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            PersonGrantForm window = new PersonGrantForm();
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean open(String title)
    {
        final Display display = Display.getDefault();
        createContents(title);

        new PersonGrantEvent(this);

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())     
        {        
	        shell.open();
	        text.setFocus();
	        shell.setActive();
        }
        
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }

        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        return done;
    }
    
    /**
     * Open the window
     */
    public boolean open()
    {
    	return open(Language.apply("员工卡授权"));
    }

    /**
     * Create contents of the window
     */
    protected void createContents(String title)
    {
        shell = new Shell(GlobalVar.style);
        shell.setSize(338, 138);
        shell.setText(title);

        final Label label = new Label(shell, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("员工卡号"));
        label.setBounds(20, 21, 88, 23);

        final Label label_1 = new Label(shell, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("员工密码"));
        label_1.setBounds(20, 62, 88, 23);
        
    	if (GlobalInfo.sysPara.grtpwdshow == 'Y')
    		//txt1.setEchoChar('*');
    		text = new Text(shell, SWT.PASSWORD|SWT.BORDER);
    	else
    		text = new Text(shell, SWT.BORDER);
    	
        text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text.setBounds(114, 20, 196, 25);
        
        if (GlobalInfo.sysPara.grantpasswordmsr == 'Y')
        {
        	text_1 = new Text(shell,SWT.BORDER);
        }
        else
        {
        	text_1 = new Text(shell, SWT.PASSWORD | SWT.BORDER);
        }
        
        text_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text_1.setBounds(115, 60, 195, 25);
    }

    public void close()
    {
        shell.close();
        shell.dispose();
    }

    public void setDone(boolean done, OperUserDef staff)
    {
        this.done  = done;
        this.staff = staff;
    }

    public OperUserDef getStaff()
    {
        return staff;
    }
}
