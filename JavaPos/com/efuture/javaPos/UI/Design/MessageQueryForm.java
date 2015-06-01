package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.MessageQueryEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class MessageQueryForm 
{
	private StyledText txtContent = null;
	private StyledText txtTitle = null;
	private PosTable tabBaseInfo = null;
	private Shell shell = null;

	public MessageQueryForm()
	{
		this.open();
	}
		
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		new MessageQueryEvent(this); 
		
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

	
	protected void createContents() {
		shell = new Shell(GlobalVar.style);
		
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800, 510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		
		shell.setText(Language.apply("通知查询"));

		tabBaseInfo = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabBaseInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabBaseInfo.setLinesVisible(true);
		tabBaseInfo.setHeaderVisible(true);
		tabBaseInfo.setBounds(12, 15, 345, 451);

		final TableColumn newColumnTableColumn = new TableColumn(tabBaseInfo, SWT.NONE);
		newColumnTableColumn.setWidth(199);
		newColumnTableColumn.setText(Language.apply("收到时间"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabBaseInfo, SWT.NONE);
		newColumnTableColumn_1.setWidth(122);
		newColumnTableColumn_1.setText(Language.apply("收件人"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(363, 10, 419, 456);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("主 题:"));
		label.setBounds(10, 25, 60, 20);

		txtContent = new StyledText(group, SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
		txtContent.setEditable(false);
		txtContent.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtContent.setBounds(10, 59, 399, 385);
		
		txtTitle = new StyledText(group,  SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		txtTitle.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtTitle.setBounds(80, 25, 327, 24);
		txtTitle.setEnabled(false);
		txtTitle.setEditable(false);

	}
	
	public StyledText getTxtContent()
	{
		return txtContent;
	}
	
	public StyledText getTxtTitle()
	{
		return txtTitle;
		
	}
	
	public PosTable getTabBaseInfo()
	{
		return tabBaseInfo;
	}

	public Shell getShell()
	{
		return shell;
	}
}
