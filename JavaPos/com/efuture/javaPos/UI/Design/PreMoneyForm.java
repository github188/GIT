package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.PreMoneyEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class PreMoneyForm
{
    public Table table;
    public Shell shell;
 
    public void open()
    {
        final Display display = Display.getDefault();
        createContents();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        PreMoneyEvent pme = new PreMoneyEvent(this);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {
            shell.layout();
            shell.open();
            pme.findLocation();
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
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setLayout(new FormLayout());
        shell.setSize(506, 397);
        shell.setText(Language.apply("请输入备用金"));

        table = new Table(shell, SWT.NONE | SWT.BORDER);
        final FormData fd_table = new FormData();
        fd_table.bottom = new FormAttachment(0, 314);
        fd_table.top = new FormAttachment(0, 25);
        fd_table.right = new FormAttachment(0, 479);
        fd_table.left = new FormAttachment(0, 22);
        table.setLayoutData(fd_table);
        table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(180);
        newColumnTableColumn.setText(Language.apply("备用金名称"));

        final TableColumn newColumnTableColumn_1 = new TableColumn(table,
                                                                   SWT.NONE);
        newColumnTableColumn_1.setAlignment(SWT.RIGHT);
        newColumnTableColumn_1.setText(Language.apply("备用金额"));
        newColumnTableColumn_1.setWidth(151);

        final TableColumn newColumnTableColumn_2 = new TableColumn(table,
                                                                   SWT.NONE);
        newColumnTableColumn_2.setAlignment(SWT.RIGHT);
        newColumnTableColumn_2.setWidth(105);//96
        newColumnTableColumn_2.setText(Language.apply("汇 率"));

        final Label label = new Label(shell, SWT.NONE);
        final FormData fd_label = new FormData();
        fd_label.right = new FormAttachment(0, 55);
        fd_label.bottom = new FormAttachment(0, 353);
        fd_label.top = new FormAttachment(0, 333);
        fd_label.left = new FormAttachment(0, 22);
        label.setLayoutData(fd_label);
        label.setAlignment(SWT.CENTER);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("按"));

        final Label label_2 = new Label(shell, SWT.NONE);
        final FormData fd_label_2 = new FormData();
        fd_label_2.left = new FormAttachment(0, 140);
        fd_label_2.right = new FormAttachment(0, 280);
        fd_label_2.top = new FormAttachment(label, -20, SWT.BOTTOM);
        fd_label_2.bottom = new FormAttachment(label, 0, SWT.BOTTOM);
        label_2.setLayoutData(fd_label_2);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setAlignment(SWT.CENTER);
        label_2.setText(Language.apply("保存备用金，按"));

        Composite composite;
        composite = new Composite(shell, SWT.BORDER);
        final FormData fd_composite = new FormData();
        fd_composite.bottom = new FormAttachment(0, 357);
        fd_composite.top = new FormAttachment(0, 329);
        fd_composite.right = new FormAttachment(0, 133);
        fd_composite.left = new FormAttachment(0, 59);
        composite.setLayoutData(fd_composite);
        composite.setLayout(new FormLayout());

		final Color mouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color mousedown = SWTResourceManager.getColor(0, 64, 128);
		
        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Validation);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_1.setBackground(mousedown);
			}
		});
        final FormData fd_label_1 = new FormData();
        fd_label_1.bottom = new FormAttachment(100, -2);
        fd_label_1.top = new FormAttachment(0, 2);
        fd_label_1.right = new FormAttachment(0, 67);
        fd_label_1.left = new FormAttachment(0, 2);
        label_1.setLayoutData(fd_label_1);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setAlignment(SWT.CENTER);
        label_1.setText(Language.apply("确认键"));

        final Composite composite_1 = new Composite(shell, SWT.BORDER);
        final FormData fd_composite_1 = new FormData();
        fd_composite_1.left = new FormAttachment(0, 286);
        fd_composite_1.right = new FormAttachment(0, 360);
        fd_composite_1.bottom = new FormAttachment(composite, 28, SWT.TOP);
        fd_composite_1.top = new FormAttachment(composite, 0, SWT.TOP);
        composite_1.setLayoutData(fd_composite_1);
        composite_1.setLayout(new FormLayout());

        final Label label_1_1 = new Label(composite_1, SWT.NONE);
        label_1_1.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_1_1.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Exit);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_1_1.setBackground(mousedown);
			}
		});
        final FormData fd_label_1_1 = new FormData();
        fd_label_1_1.right = new FormAttachment(0, 65);
        fd_label_1_1.left = new FormAttachment(0, 5);
        fd_label_1_1.bottom = new FormAttachment(0, 22);
        fd_label_1_1.top = new FormAttachment(0, 2);
        label_1_1.setLayoutData(fd_label_1_1);
        label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1_1.setAlignment(SWT.CENTER);
        label_1_1.setText(Language.apply("退出键"));

        final Label label_2_1 = new Label(shell, SWT.NONE);
        final FormData fd_label_2_1 = new FormData();
        fd_label_2_1.right = new FormAttachment(0, 455);
        fd_label_2_1.bottom = new FormAttachment(label_2, 20, SWT.TOP);
        fd_label_2_1.top = new FormAttachment(label_2, 0, SWT.TOP);
        fd_label_2_1.left = new FormAttachment(composite_1, 5, SWT.RIGHT);
        label_2_1.setLayoutData(fd_label_2_1);
        label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2_1.setAlignment(SWT.CENTER);
        label_2_1.setText(Language.apply("放弃输入"));
    }
}
