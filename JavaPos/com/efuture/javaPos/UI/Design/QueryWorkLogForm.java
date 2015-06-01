package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.QueryWorkLogEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class QueryWorkLogForm 
{
	private Combo cmbDjlb;
	private Text txtDate = null;
	private PosTable tabWorkLog = null;
	protected Shell shell = null;
	
	public QueryWorkLogForm()
	{
		this.open();
	}
	
	public void open() 
	{
        Display display = Display.getDefault();
		createContents();
		
		new QueryWorkLogEvent(this);
		
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
		shell.setSize(800, 510);
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("查询工作日志"));

		tabWorkLog = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabWorkLog.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabWorkLog.setLinesVisible(true);
		tabWorkLog.setHeaderVisible(true);
		tabWorkLog.setBounds(10, 61, 774, 407);

		final TableColumn newColumnTableColumn = new TableColumn(tabWorkLog, SWT.NONE);
		newColumnTableColumn.setWidth(65);
		newColumnTableColumn.setText(Language.apply("序号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabWorkLog, SWT.NONE);
		newColumnTableColumn_1.setWidth(109);
		newColumnTableColumn_1.setText(Language.apply("工作时间"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabWorkLog, SWT.NONE);
		newColumnTableColumn_2.setWidth(96);
		newColumnTableColumn_2.setText(Language.apply("收银员号"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabWorkLog, SWT.NONE);
		newColumnTableColumn_3.setWidth(482);
		newColumnTableColumn_3.setText(Language.apply("工作备注"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 0, 774, 55);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setBounds(10, 20, 140, 20);
		label.setText(Language.apply("请输入查询日期"));

		txtDate = new Text(group, SWT.BORDER);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtDate.setBounds(155, 15, 100, 30);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setBounds(260, 20, 150, 20);
		label_1.setText(Language.apply("格式为:YYYYMMDD"));
		
		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(440, 20, 44, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("类型"));

		cmbDjlb = new Combo(group, SWT.READ_ONLY);
		cmbDjlb.select(0);
		cmbDjlb.setItems(new String[] {Language.apply("全部日志"), Language.apply("登入登出"),Language.apply("异常日志")});
		cmbDjlb.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbDjlb.setBounds(490, 15, 107, 28);
		
		final Label label_2_1_1 = new Label(group, SWT.NONE);
		label_2_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2_1_1.setBounds(603, 20, 159, 20);
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setText(Language.apply("[付款键切换输入]"));
		
	}
	
	public PosTable getTabWorkLog()
	{
		return tabWorkLog;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	
	public Shell getShell()
	{
		return shell;
	}

	public Combo getCmbDjlb() {
		return cmbDjlb;
	}
	
}
