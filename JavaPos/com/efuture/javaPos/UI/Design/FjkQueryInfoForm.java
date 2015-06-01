package com.efuture.javaPos.UI.Design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.FjkQueryInfoEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class FjkQueryInfoForm 
{

	private Table table = null;
	private StyledText txtName = null;
	private StyledText txtCardCode = null;
	protected Shell shell = null;
	private ArrayList fjklist = null;
	Label lbl_A   = null;
	Label txt_A = null;
	Label lbl_B = null;
	Label txt_B = null;
	Label lbl_F = null;
	Label txt_F = null;
	public String aje  = null;
	public String bje  = null;
	public String fje  = null;
	public String name = null;
	
	public FjkQueryInfoForm(ArrayList fjklist)
	{
		this.fjklist = fjklist;
		
		this.open();
	}
	
	public FjkQueryInfoForm(ArrayList fjklist,String name,String aje,String bje,String fje)
	{
		this.fjklist = fjklist;
		this.aje = aje;
		this.bje = bje;
		this.fje = fje;
		this.name = name;
		this.open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		new FjkQueryInfoEvent(this); 
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        { 		
			shell.open();
			shell.layout();
        }
        
		while (!shell.isDisposed()) {
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
		
		shell.setSize(705, 427);
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		
		shell.setText(Language.apply("返券卡信息"));
		
		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 5, 677, 87);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("卡  号"));
		label.setBounds(10, 20, 63, 20);

		txtCardCode = new StyledText(group, SWT.BORDER);
		txtCardCode.setEnabled(false);
		txtCardCode.setEditable(false);
		txtCardCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtCardCode.setBounds(79, 15, 588, 24);

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("持卡人"));
		label_1.setBounds(10, 55, 63, 20);

		txtName = new StyledText(group, SWT.BORDER);
		txtName.setBounds(80, 50, 587, 24);
		txtName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtName.setEnabled(false);
		txtName.setEditable(false);
		txtName.setText(name);

		table = new Table(shell,  SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 98, 677, 211);

		final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(80);
		newColumnTableColumn_4.setText(Language.apply("卡状态"));

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(117);
		newColumnTableColumn.setText(Language.apply("开始日期"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(116);
		newColumnTableColumn_1.setText(Language.apply("结束日期"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setAlignment(SWT.RIGHT);
		newColumnTableColumn_2.setWidth(112);
		newColumnTableColumn_2.setText(Language.apply("A券余额"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(112);
		newColumnTableColumn_3.setText(Language.apply("B券余额"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(118);
		newColumnTableColumn_5.setText(Language.apply("F券余额"));

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		group_1.setText(Language.apply("当前可用余额"));
		group_1.setBounds(10, 315, 677, 55);

		lbl_A = new Label(group_1, SWT.NONE);
		lbl_A.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_A.setText(Language.apply("A券:"));
		lbl_A.setBounds(10, 22, 53, 23);

		txt_A = new Label(group_1, SWT.NONE);
		txt_A.setAlignment(SWT.CENTER);
		txt_A.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txt_A.setBounds(69, 22, 151, 23);
		txt_A.setText("0.00");

		lbl_B = new Label(group_1, SWT.NONE);
		lbl_B.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_B.setBounds(238, 22, 53, 23);
		lbl_B.setText(Language.apply("B券:"));

		txt_B = new Label(group_1, SWT.NONE);
		txt_B.setAlignment(SWT.CENTER);
		txt_B.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txt_B.setBounds(295, 22, 139, 23);
		txt_B.setText("0.00");

		lbl_F = new Label(group_1, SWT.NONE);
		lbl_F.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_F.setBounds(452, 22, 53, 23);
		lbl_F.setText(Language.apply("F券:"));

		txt_F = new Label(group_1, SWT.NONE);
		txt_F.setAlignment(SWT.CENTER);
		txt_F.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txt_F.setBounds(511, 22, 151, 23);
		txt_F.setText("0.00");
		
	}
	
	public Label getAje()
	{
		return txt_A;
	}
	
	public Label getBje()
	{
		return txt_B;
	}
	
	public Label getFje()
	{
		return txt_F;
	}
	
	public Table getTable()
	{
		return table;
	}
	
	public StyledText getTxtName()
	{
		return txtName;
	}
	
	public StyledText getTxtCardCode()
	{
		return txtCardCode;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public ArrayList getFjklist()
	{
		return fjklist;
	}
}
