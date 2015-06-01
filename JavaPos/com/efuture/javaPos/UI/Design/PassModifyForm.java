package com.efuture.javaPos.UI.Design;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.PassModifyEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PassModifyForm
{
    private Text txtOkNewPass;
    private Text txtNewPass;
    private Text txtOldPass;
    private Text txtGh;
    protected Shell shell;
    
    public  PassModifyForm()
    {
    	this.open();
    }
    
    public void open()
    {
        final Display display = Display.getDefault();
        createContents();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        new PassModifyEvent(this);

        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())     
        {        
	        shell.open();
	        shell.layout();
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
    } 

    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        //Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
        shell.setSize(407, 277);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
        shell.setText(Language.apply("密码修改"));

        final Group group = new Group(shell, SWT.NONE);
        group.setForeground(SWTResourceManager.getColor(255, 0, 0));
        group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group.setText(Language.apply("用户修改密码"));
        group.setBounds(10, 10, 379, 214);

        final Label label = new Label(group, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("收银员工号"));
        label.setBounds(10, 30, 116, 30);

        txtGh = new Text(group, SWT.BORDER);
        txtGh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtGh.setBackground(SWTResourceManager.getColor(255, 255, 255));
        if(GlobalInfo.posLogin == null || GlobalInfo.posLogin.gh == null || GlobalInfo.posLogin.gh.equals(""))
        {
        	txtGh.setText(Language.apply("暂无"));
        }
        else
        {
        	txtGh.setText("["+GlobalInfo.posLogin.gh+"]" +GlobalInfo.posLogin.name);
        }
        txtGh.setBounds(129, 26, 240, 31);

        final Label label_1 = new Label(group, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("输入旧密码"));
        label_1.setBounds(10, 75, 116, 30);

        txtOldPass = new Text(group, SWT.PASSWORD | SWT.BORDER);
        txtOldPass.setTextLimit(GlobalVar.MaxlengthOfPasswd);
        txtOldPass.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtOldPass.setBounds(130, 72, 240, 31);
        txtOldPass.setFocus();
        
        final Label label_2 = new Label(group, SWT.NONE);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText(Language.apply("输入新密码"));
        label_2.setBounds(10, 120, 100, 30);

        txtNewPass = new Text(group, SWT.PASSWORD | SWT.BORDER);
        txtNewPass.setTextLimit(GlobalVar.MaxlengthOfPasswd);
        txtNewPass.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtNewPass.setBackground(SWTResourceManager.getColor(255, 255, 255));
        txtNewPass.setBounds(129, 117, 240, 31);

        final Label label_3 = new Label(group, SWT.NONE);
        label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_3.setText(Language.apply("确认新密码"));
        label_3.setBounds(10, 170, 116, 30);

        txtOkNewPass = new Text(group, SWT.PASSWORD | SWT.BORDER);
        txtOkNewPass.setTextLimit(GlobalVar.MaxlengthOfPasswd);
        txtOkNewPass.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtOkNewPass.setBackground(SWTResourceManager.getColor(255, 255, 255));
        txtOkNewPass.setBounds(129, 164, 240, 29);
    }
    
   
    public Text getTxtOkNewPass()
    {
    	return txtOkNewPass;
    }
    
    public Text getTxtNewPass()
    {
    	return txtNewPass;
    }
    
    public Text getTxtOldPass()
    {
    	return txtOldPass;
    }
    
    public Text getTxtGh()
    {
    	return txtGh;
    }
    
    public Shell getShell()
    {
    	return shell;
    }
}
