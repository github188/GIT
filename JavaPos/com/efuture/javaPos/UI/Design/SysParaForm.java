package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.SysParaEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * @author yinl
 * @create 2010-12-3 下午01:41:12
 * @descri 文件说明
 */

public class SysParaForm
{
	public Text txtSearch;
	public Text txtValueDesc;
	public Text txtValue;
	public Text txtDesc;
	public Table table;
	public Tree tree;
	public Shell shell;
	public Button btnClose;
	public Button btnExport;
	public Button btnSave;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			SysParaForm window = new SysParaForm();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
		
		new SysParaEvent(this);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {
            //Rectangle rec = display.getPrimaryMonitor().getClientArea();
            shell.setBounds((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2), shell.getSize().x,
                            shell.getSize().y - GlobalVar.heightPL);
            shell.layout();
            shell.open();
            shell.setActive();
        }
        
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

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.CLOSE|GlobalVar.style);
		shell.setSize(760, 560);
		shell.setText(Language.apply("系统参数"));

		tree = new Tree(shell, SWT.BORDER|SWT.SINGLE);
		tree.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		tree.setBounds(10, 49, 218, 477);

		table = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(234, 124, 510, 365);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(322);
		newColumnTableColumn.setText(Language.apply("参数说明"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(300);
		newColumnTableColumn_1.setText(Language.apply("参数值"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(200);
		newColumnTableColumn_2.setText(Language.apply("参数项"));

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(234, 13, 107, 20);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("参数项描述"));

		txtDesc = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		txtDesc.setBounds(347, 10, 397, 26);
		txtDesc.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(234, 49, 107, 20);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("当前参数值"));

		final Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(234, 88, 107, 20);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("参数值说明"));

		txtValue = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		txtValue.setBounds(347, 47, 397, 26);
		txtValue.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		txtValueDesc = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		txtValueDesc.setBounds(347, 85, 397, 26);
		txtValueDesc.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		btnClose = new Button(shell, SWT.NONE);
		btnClose.setText(Language.apply("关闭窗口"));
		btnClose.setBounds(677, 499, 67, 27);

		btnExport = new Button(shell, SWT.NONE);
		btnExport.setBounds(234, 499, 100, 27);
		btnExport.setText(Language.apply("导出参数SQL"));

		btnSave = new Button(shell, SWT.NONE);
		btnSave.setBounds(347, 499, 100, 27);
		btnSave.setText(Language.apply("保存当前设置"));

		txtSearch = new Text(shell, SWT.BORDER);
		txtSearch.setBounds(10, 10, 218, 26);
		txtSearch.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		//
	}

}
