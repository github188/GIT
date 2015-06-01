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
import com.efuture.javaPos.UI.SaleTicketListEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class SaleTicketListForm 
{
	private Combo cmbDjlb;
	private Text txtFphm;
	private Text txtDate = null;
	private PosTable tabTickList = null;
	private Group group	   = null;	
	private Label lblmessge   = null;

	protected Shell shell = null;
	
	public SaleTicketListForm()
	{
		this.open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		new SaleTicketListEvent(this); 
		
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
		shell.setSize(800, 510);
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply(" 销售小票列表"));

		tabTickList = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabTickList.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabTickList.setLinesVisible(true);
		tabTickList.setHeaderVisible(true);
		tabTickList.setBounds(10, 70, 772, 363);

		final TableColumn newColumnTableColumn = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn.setWidth(107);
		newColumnTableColumn.setText(Language.apply("小票号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_1.setWidth(57);
		newColumnTableColumn_1.setText(Language.apply("班次"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_2.setWidth(66);
		newColumnTableColumn_2.setText(Language.apply("班次"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_3.setWidth(105);
		newColumnTableColumn_3.setText(Language.apply("收银员"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(113);
		newColumnTableColumn_4.setText(Language.apply("应收金额"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(90);
		newColumnTableColumn_5.setText(Language.apply("折扣额"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_6.setAlignment(SWT.RIGHT);
		newColumnTableColumn_6.setWidth(113);
		newColumnTableColumn_6.setText(Language.apply("实际付款"));

		final TableColumn newColumnTableColumn_9 = new TableColumn(tabTickList, SWT.NONE);
		newColumnTableColumn_9.setWidth(94);
		newColumnTableColumn_9.setText(Language.apply("交易类型"));

		group = new Group(shell, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		group.setBounds(10, 0, 772, 67);

		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 30,140, 20);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("日期(YYYYMMDD)"));

		txtDate = new Text(group, SWT.BORDER);
		txtDate.setBounds(155, 25,99, 30);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setBounds(265, 30, 60, 20);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("小票号"));

		txtFphm = new Text(group, SWT.BORDER);
		txtFphm.setBounds(330, 25, 99, 30);
		txtFphm.setTextLimit(8);
		txtFphm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(440, 30, 44, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("类型"));

		cmbDjlb = new Combo(group, SWT.READ_ONLY);
		cmbDjlb.select(0);
		cmbDjlb.setItems(new String[] {Language.apply("全部交易"), Language.apply("销售小票"), Language.apply("退货小票"), Language.apply("红冲小票")});
		cmbDjlb.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbDjlb.setBounds(490, 25, 107, 28);

		final Label label_2_1_1 = new Label(group, SWT.NONE);
		label_2_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2_1_1.setBounds(600, 30, 159, 20);
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setText(Language.apply("[付款键切换输入]"));

		lblmessge = new Label(shell, SWT.NONE);
		lblmessge.setBounds(10, 450, 772, 20);
		lblmessge.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblmessge.setText(Language.apply("单据总数:0 应收合计:0.00 应缴合计:0.00" ));	
	}
	
	public PosTable getTabTickList()
	{
		return tabTickList;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	
	public Text getTxtFphm()
	{
		return txtFphm;
	}
	
	public Combo getcmbDjbl()
	{
		return cmbDjlb;
	}
	
	public Group getGroup()
	{
		return group;
	}
	
	public  Label getLblMessge()
	{
		return lblmessge;
	}
	
}
