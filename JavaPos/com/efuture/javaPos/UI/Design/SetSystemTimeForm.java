package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.SetSystemTimeEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class SetSystemTimeForm
{
    private Text txtTime;
    private Text txtDate;
    protected Shell shell;

    public SetSystemTimeForm(boolean onlysettime)
    {
        this.open(onlysettime);
    }

    public void open(boolean onlysettime)
    {
        final Display display = Display.getDefault();
        createContents();
        
		new SetSystemTimeEvent(this,onlysettime);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
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

        shell.setSize(397, 238);
        shell.setBounds((GlobalVar.rec.x / 2) - (shell.getSize().x / 2),
                        (GlobalVar.rec.y / 2) - (shell.getSize().y / 2),
                        shell.getSize().x,
                        shell.getSize().y - GlobalVar.heightPL);

        shell.setText(Language.apply("设置系统时间"));

        final Group group = new Group(shell, SWT.NONE);
        group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group.setBounds(13, 11, 366, 170);

        final Label label = new Label(group, SWT.NONE);
        label.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("请输入正确的系统日期和系统时间"));
        label.setBounds(5, 28, 341, 27);

        final Label label_1 = new Label(group, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("当前日期:"));
        label_1.setBounds(5, 66, 89, 27);

        txtDate = new Text(group, SWT.BORDER);
        txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtDate.setTextLimit(8);
        txtDate.setBounds(100, 66, 141, 27);

        final Label label_2 = new Label(group, SWT.NONE);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText(Language.apply("当前时间:"));
        label_2.setBounds(5, 115, 87, 27);

        txtTime = new Text(group, SWT.BORDER);
        txtTime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtTime.setTextLimit(6);
        txtTime.setBounds(100, 114, 141, 27);

        final Label yyyymmddLabel = new Label(group, SWT.NONE);
        yyyymmddLabel.setForeground(SWTResourceManager.getColor(255, 0, 0));
        yyyymmddLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        yyyymmddLabel.setText("YYYYMMDD");
        yyyymmddLabel.setBounds(251, 66, 105, 27);

        final Label hhmmssLabel = new Label(group, SWT.NONE);
        hhmmssLabel.setForeground(SWTResourceManager.getColor(255, 0, 0));
        hhmmssLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        hhmmssLabel.setText("HHMMSS");
        hhmmssLabel.setBounds(251, 115, 105, 27);
    }

    public Text getTxtDate()
    {
        return txtDate;
    }

    public Text getTxtTime()
    {
        return txtTime;
    }

    public Shell getShell()
    {
        return shell;
    }
}
