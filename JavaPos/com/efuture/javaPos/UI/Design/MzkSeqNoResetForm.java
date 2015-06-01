package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
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
import com.efuture.javaPos.UI.MzkSeqNoResetEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class MzkSeqNoResetForm
{
    public PosTable table;
    public Label lbOldMzkSeqNo = null;
    public Text txtNewMzkSeqNo = null;
    public Shell shell;
    public Button buttonOk = null;
    public Button buttonDel = null;
    public Button buttonExit = null;
   
    public MzkSeqNoResetForm()
    {
	this.open();
    }

    public void open()
    {
	final Display display = Display.getDefault();
	
	createContents();
	
	new MzkSeqNoResetEvent(this);
	
	// 创建触屏操作按钮栏 
	ControlBarForm.createMouseControlBar(this, shell);
	
	// 加载背景图片
	Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);
	
	if (!shell.isDisposed())
	{
	    shell.open();
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
	shell = new Shell(SWT.APPLICATION_MODAL | SWT.TITLE);
	//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
	shell.setSize(735, 530);
	shell.setBounds(GlobalVar.rec.x / 2 - shell.getSize().x / 2, GlobalVar.rec.y / 2 - shell.getSize().y / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);
	shell.setText(Language.apply("设置面值卡交易流水"));
	
	final Group setMzkSeqNoGroup = new Group(shell, SWT.NONE);
	setMzkSeqNoGroup.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
	setMzkSeqNoGroup.setText(Language.apply("设置面值卡交易流水"));
	setMzkSeqNoGroup.setBounds(10, 2, 710, 113);
	
	final Label label_1_1 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_1_1.setBounds(15, 36, 104, 30);
	label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_1_1.setText(Language.apply("当前流水号"));
	
	lbOldMzkSeqNo = new Label(setMzkSeqNoGroup, SWT.BORDER);
	lbOldMzkSeqNo.setBounds(130, 33, 210, 30);
	lbOldMzkSeqNo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	final Label label_1 = new Label(setMzkSeqNoGroup, SWT.NONE);
	
	label_1.setBounds(15, 76, 103, 26);
	label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_1.setText(Language.apply("新的流水号"));
	
	txtNewMzkSeqNo = new Text(setMzkSeqNoGroup, SWT.BORDER);
	txtNewMzkSeqNo.setBounds(130, 74, 210, 31);
	txtNewMzkSeqNo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	
	final Label label = new Label(setMzkSeqNoGroup, SWT.NONE);
	label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label.setText(Language.apply("按"));
	label.setBounds(398, 19, 25, 22);

	final Label label_2 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_2.setBounds(495, 18, 197, 22);
	label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_2.setText(Language.apply("保存修改后的流水号"));

	final Label label_3 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_3.setBounds(398, 51, 25, 22);
	label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_3.setText(Language.apply("按"));

	final Label label_4 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_4.setBounds(398, 82, 25, 22);
	label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_4.setText(Language.apply("按"));

	final Label label_2_1 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_2_1.setBounds(495, 52, 197, 22);
	label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_2_1.setText(Language.apply("删除选中的冲正信息"));

	final Label label_2_2 = new Label(setMzkSeqNoGroup, SWT.NONE);
	label_2_2.setBounds(495, 83, 197, 22);
	label_2_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	label_2_2.setText(Language.apply("退出程序"));

	buttonOk = new Button(setMzkSeqNoGroup, SWT.NONE);
	buttonOk.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	buttonOk.setText(Language.apply("确 定"));
	buttonOk.setBounds(425, 16, 65, 25);

	buttonDel = new Button(setMzkSeqNoGroup, SWT.NONE);
	buttonDel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	buttonDel.setText(Language.apply("删 除"));
	buttonDel.setBounds(425, 50, 65, 25);

	buttonExit = new Button(setMzkSeqNoGroup, SWT.NONE);
	buttonExit.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	buttonExit.setBounds(425, 82, 65, 25);
	buttonExit.setText(Language.apply("退 出"));

	
	final Group mzkInfoGroup = new Group(shell, SWT.NONE);
	mzkInfoGroup.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
	mzkInfoGroup.setText(Language.apply("面值卡冲正信息"));
	mzkInfoGroup.setBounds(10, 120, 710, 380);
	
	table = new PosTable(mzkInfoGroup, SWT.FULL_SELECTION | SWT.BORDER);
	table.setBounds(5, 33,700, 340);
	table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	table.setLinesVisible(true);
	table.setHeaderVisible(true);
	
	
	final TableColumn idColumn = new TableColumn(table, SWT.CENTER);
	idColumn.setWidth(40);
	idColumn.setText(Language.apply("序"));
	final TableColumn typeColumn = new TableColumn(table, SWT.CENTER);
	typeColumn.setWidth(100);
	typeColumn.setText(Language.apply("类型"));
	final TableColumn cardColumn = new TableColumn(table, SWT.CENTER);
	cardColumn.setWidth(180);
	cardColumn.setAlignment(SWT.LEFT);
	cardColumn.setText(Language.apply("卡号"));
	final TableColumn amountColumn = new TableColumn(table, SWT.CENTER);
	amountColumn.setWidth(95);
	amountColumn.setAlignment(SWT.RIGHT);
	amountColumn.setText(Language.apply("金额"));
	final TableColumn filenameColumn = new TableColumn(table, SWT.NONE);
	filenameColumn.setWidth(130);
	filenameColumn.setText(Language.apply("文件名"));
	final TableColumn modDateColumn = new TableColumn(table, SWT.CENTER);
	modDateColumn.setWidth(150);
	modDateColumn.setText(Language.apply("修改日期"));
    }

    public Shell getShell()
    {
	return shell;
    }

}
