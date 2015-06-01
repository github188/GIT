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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.ShortcutKeyEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class ShortcutKeyForm 
{

	private Table tabShortKey = null;
	private Shell shell = null;

	public ShortcutKeyForm()
	{
		this.open();
	}
	
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		ShortcutKeyEvent ske = new ShortcutKeyEvent(this);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        { 			
			shell.open();
			ske.findLocation();
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

	/**
	 * Create contents of the window
	 */
	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800, 510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("快捷键定义"));

		tabShortKey = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabShortKey.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabShortKey.setLinesVisible(true);
		tabShortKey.setHeaderVisible(true);
		tabShortKey.setBounds(10, 10, 772, 401);

		final TableColumn newColumnTableColumn = new TableColumn(tabShortKey, SWT.NONE);
		newColumnTableColumn.setWidth(61);
		newColumnTableColumn.setText(Language.apply("序号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabShortKey, SWT.NONE);
		newColumnTableColumn_1.setWidth(78);
		newColumnTableColumn_1.setText(Language.apply("快捷键"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabShortKey, SWT.NONE);
		newColumnTableColumn_2.setWidth(608);
		newColumnTableColumn_2.setText(Language.apply("键值串"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 423, 772, 45);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("按"));
		label.setBounds(5, 17, 24, 20);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("按"));
		label_1.setBounds(265, 17, 24, 20);

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("按"));
		label_2.setBounds(525, 17, 24, 21);

		final Composite composite = new Composite(group, SWT.BORDER);
		composite.setBounds(35, 15, 68, 24);

		final Color mouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color mousedown = SWTResourceManager.getColor(0, 64, 128);
		
		final Label label_3 = new Label(composite, SWT.NONE);
		label_3.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_3.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Del);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_3.setBackground(mousedown);
			}
		});
		label_3.setBounds(0, 0, 64, 20);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("删除键"));

		final Label label_4 = new Label(group, SWT.NONE);
		label_4.setBounds(111, 17, 111, 20);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setText(Language.apply("删除快捷键"));

		final Composite composite_1 = new Composite(group, SWT.BORDER);
		composite_1.setBounds(293, 15, 68, 24);

		final Label label_3_1 = new Label(composite_1, SWT.NONE);
		label_3_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1.setBounds(0, 0, 64, 20);
		label_3_1.setText(Language.apply("确认键"));
		label_3_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_3_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Validation);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_3_1.setBackground(mousedown);
			}
		});
		final Label label_4_1 = new Label(group, SWT.NONE);
		label_4_1.setBounds(368, 17, 111, 20);
		label_4_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4_1.setText(Language.apply("保存并退出"));

		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(624, 17, 138, 21);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("定义新的快捷键"));

		final Composite composite_1_1 = new Composite(group, SWT.BORDER);
		composite_1_1.setBounds(549, 15, 68, 24);

		final Label label_3_1_1 = new Label(composite_1_1, SWT.NONE);
		label_3_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1.setBounds(0, 0, 64, 20);
		label_3_1_1.setText(Language.apply("下光标"));
		label_3_1_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_3_1_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.ArrowDown);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_3_1_1.setBackground(mousedown);
			}
		});		
	}
	
	public Table getTabShortKey()
	{
		return tabShortKey;
	}
	
	public Shell getShell()
	{
		return shell;
	}

}
