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
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.UI.BusinessPersonnelStatEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class BusinessPersonnelStatForm 
{
	private Text txtDate = null;
	private Combo cmbSyyh = null;
	private Combo cmbBc = null;	
	private PosTable tabBusinessPersonStatInfo = null;
	private Shell shell = null;

	public BusinessPersonnelStatForm()
	{
		this.open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();

		new BusinessPersonnelStatEvent(this);
		
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
			if (!display.readAndDispatch()) display.sleep();
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
		shell.setText(Language.apply("营业员销售统计"));

		tabBusinessPersonStatInfo = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabBusinessPersonStatInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabBusinessPersonStatInfo.setLinesVisible(true);
		tabBusinessPersonStatInfo.setHeaderVisible(true);
		tabBusinessPersonStatInfo.setBounds(10, 65, 772, 399);

		final TableColumn newColumnTableColumn = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn.setWidth(160);
		newColumnTableColumn.setText(Language.apply("营业员号"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_1.setAlignment(SWT.RIGHT);
		newColumnTableColumn_1.setWidth(139);
		newColumnTableColumn_1.setText(Language.apply("销售金额"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_2.setAlignment(SWT.RIGHT);
		newColumnTableColumn_2.setWidth(56);
		newColumnTableColumn_2.setText(Language.apply("笔数"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_6.setAlignment(SWT.RIGHT);
		newColumnTableColumn_6.setWidth(96);
		newColumnTableColumn_6.setText(Language.apply("折扣金额"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(140);
		newColumnTableColumn_3.setText(Language.apply("退货金额"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(55);
		newColumnTableColumn_4.setText(Language.apply("笔数"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabBusinessPersonStatInfo, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(95);
		newColumnTableColumn_5.setText(Language.apply("折扣金额"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 5, 772, 55);
/*
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setBounds(10, 20, 140, 20);
		label.setText("请输入查询日期");

		txtDate = new Text(group, SWT.BORDER);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtDate.setBounds(155, 15, 172, 30);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setBounds(333, 20, 150, 20);
		label_1.setText("格式为:YYYYMMDD");
*/
		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 20,140, 20);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("日期(YYYYMMDD)"));

		txtDate = new Text(group, SWT.BORDER);
		txtDate.setBounds(155, 15,99, 30);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setBounds(263, 20, 60, 20);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("收银员"));

		cmbSyyh = new Combo(group, SWT.READ_ONLY);
		cmbSyyh.select(0);
		cmbSyyh.setItems(new String[] {Language.apply("当前款员"), Language.apply("所有款员")});
		cmbSyyh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbSyyh.setBounds(328, 15, 107, 28);

		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(440, 20, 44, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("班次"));

		cmbBc = new Combo(group, SWT.READ_ONLY);
		cmbBc.select(0);
        String[] content = new String[GlobalInfo.posTime.size()+1];
        content[0] = Language.apply("全部班次");
        for (int i = 0; i < GlobalInfo.posTime.size(); i++)
        {
        	content[i+1] = ((PosTimeDef) GlobalInfo.posTime.elementAt(i)).name;
        }		
		cmbBc.setItems(content);
		cmbBc.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbBc.setBounds(490, 15, 107, 28);

		final Label label_2_1_1 = new Label(group, SWT.NONE);
		label_2_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2_1_1.setBounds(603, 20, 159, 20);
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setText(Language.apply("[付款键切换输入]"));			
	}
	
	
	public PosTable getTabBusinessPersonStatInfo()
	{
		return tabBusinessPersonStatInfo;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}
	
	public Combo getCmbSyyh()
	{
		return cmbSyyh;
	}
	
	public Combo getCmbBc()
	{
		return cmbBc;
	}	
}
