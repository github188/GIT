package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.RemoveDayEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class RemoveDayForm 
{
	private Text txtDirName = null;
	private Shell shell = null;

	public RemoveDayForm()
	{
		open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		new RemoveDayEvent(this);
		
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
				display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}

	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(348, 194);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("删除销售数据"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 5, 319, 99);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("数据库名"));
		label.setBounds(10, 20, 80, 20);

		txtDirName = new Text(group, SWT.BORDER);
		txtDirName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtDirName.setBounds(95, 15, 218, 28);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("说明：输入 20070806 表示删除\n2007-08-06日的本地销售数据。"));
		label_1.setBounds(10, 50, 299, 45);

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(10, 105, 319, 45);

		final Label label_3 = new Label(group_1, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("关闭窗口"));
		label_3.setBounds(233, 15, 80, 20);

		final Composite composite = new Composite(group_1, SWT.BORDER);
		composite.setBounds(6, 14, 64, 24);

		final Label label_2_1 = new Label(composite, SWT.NONE);
		label_2_1.setBounds(0, 0, 60, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("回车键"));

		final Color mouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color mousedown = SWTResourceManager.getColor(0, 64, 128);
		label_2_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_2_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Enter);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_2_1.setBackground(mousedown);
			}
		});
        
		final Label label_2_2 = new Label(group_1, SWT.NONE);
		label_2_2.setBounds(76, 15, 80, 20);
		label_2_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_2.setText(Language.apply("删除数据"));

		final Composite composite_1 = new Composite(group_1, SWT.BORDER);
		composite_1.setBounds(166, 14, 64, 24);

		final Label label_2_1_1 = new Label(composite_1, SWT.NONE);
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setBounds(0, 0, 60, 20);
		label_2_1_1.setText(Language.apply("退出键"));		
		label_2_1_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_2_1_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Exit);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_2_1_1.setBackground(mousedown);
			}
		});
	}
	
	public Text getTxtDirName()
	{
		return txtDirName;
	}
	
	public Shell getShell()
	{
		return shell;
	}
}
