package posserver.tools.PressTest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class PosSrvPressureFrom
{

	public Button btnClose;
	public Table table;
	public Button btnClrTxt;
	public Text txtDelay;
	private Label label_3;
	public Button btnStopTest;
	public Button btnStartTest;
	private Label label_2;
	private Label label_1;
	private Label label;
	public Text txtThdCount;
	public Combo cmbTestCmd;
	public Text txtSrvUrl;
	public Shell shell;
	public Label lbCmdCount;
	public Label lbRunCount;
	public Label lbErrorCount;
	public Button btnClearHisData;
	
	private String srvUrl =""; 

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length > 0)GlobalVar.ConfigPath = args[0];
			PosSrvPressureFrom window = new PosSrvPressureFrom();
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

		Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setLocation((rec.width - shell.getSize().x) / 2, (rec.height - shell.getSize().y) / 2);
		
		btnClearHisData = new Button(shell, SWT.NONE);
		btnClearHisData.setText("清除历史数据");
		FormData fd_btnChar = new FormData();
		fd_btnChar.bottom = new FormAttachment(btnStartTest, 28);
		fd_btnChar.right = new FormAttachment(btnClrTxt, 108, SWT.RIGHT);
		fd_btnChar.top = new FormAttachment(btnStartTest, 0, SWT.TOP);
		fd_btnChar.left = new FormAttachment(btnClrTxt, 8);
		btnClearHisData.setLayoutData(fd_btnChar);

		new PosSrvPressureEvent(this).setSelfRef();
		
		shell.open();

		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(750, 560);
		shell.setText("PosServer压力测试");

		btnStartTest = new Button(shell, SWT.NONE);
		shell.setDefaultButton(btnStartTest);
		final FormData fd_btn_startTest = new FormData();
		fd_btn_startTest.bottom = new FormAttachment(0, 525);
		fd_btn_startTest.top = new FormAttachment(0, 497);
		fd_btn_startTest.right = new FormAttachment(0, 108);
		fd_btn_startTest.left = new FormAttachment(0, 8);
		btnStartTest.setLayoutData(fd_btn_startTest);
		btnStartTest.setText("开始测试");

		btnStopTest = new Button(shell, SWT.NONE);
		final FormData fd_btn_stopTest = new FormData();
		fd_btn_stopTest.bottom = new FormAttachment(0, 525);
		fd_btn_stopTest.top = new FormAttachment(0, 497);
		fd_btn_stopTest.left = new FormAttachment(btnStartTest, 8, SWT.DEFAULT);
		fd_btn_stopTest.right = new FormAttachment(0, 216);
		btnStopTest.setLayoutData(fd_btn_stopTest);
		btnStopTest.setText("停止测试");

		btnClrTxt = new Button(shell, SWT.NONE);
		final FormData fd_btn_clrTxt = new FormData();
		fd_btn_clrTxt.bottom = new FormAttachment(0, 525);
		fd_btn_clrTxt.top = new FormAttachment(0, 497);
		fd_btn_clrTxt.right = new FormAttachment(100, -418);
		fd_btn_clrTxt.left = new FormAttachment(btnStopTest, 10, SWT.DEFAULT);
		btnClrTxt.setLayoutData(fd_btn_clrTxt);
		btnClrTxt.setText("清除信息");

		final Group group = new Group(shell, SWT.NONE);
		group.setText("参数设置");
		final FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(0, 86);
		fd_group.top = new FormAttachment(0, 2);
		fd_group.right = new FormAttachment(0, 738);
		fd_group.left = new FormAttachment(0, 6);
		group.setLayoutData(fd_group);
		group.setLayout(new FormLayout());

		txtSrvUrl = new Text(group, SWT.BORDER);
		txtSrvUrl.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_txt_srvUrl = new FormData();
		fd_txt_srvUrl.right = new FormAttachment(0, 470);
		fd_txt_srvUrl.bottom = new FormAttachment(0, 32);
		fd_txt_srvUrl.top = new FormAttachment(0, 9);
		fd_txt_srvUrl.left = new FormAttachment(0, 105);
		txtSrvUrl.setLayoutData(fd_txt_srvUrl);
		txtSrvUrl.setText(srvUrl);
		
		label = new Label(group, SWT.CENTER);
		final FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 10);
		fd_label.right = new FormAttachment(0, 98);
		fd_label.bottom = new FormAttachment(0, 26);
		fd_label.top = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label.setText("服务器地址:");

		cmbTestCmd = new Combo(group, SWT.READ_ONLY);
		cmbTestCmd.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_cmb_testCmd = new FormData();
		fd_cmb_testCmd.right = new FormAttachment(txtSrvUrl, 0, SWT.RIGHT);
		fd_cmb_testCmd.bottom = new FormAttachment(0, 64);
		fd_cmb_testCmd.top = new FormAttachment(0, 44);
		fd_cmb_testCmd.left = new FormAttachment(0, 105);
		cmbTestCmd.setLayoutData(fd_cmb_testCmd);

		label_1 = new Label(group, SWT.NONE);
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(0, 60);
		fd_label_1.top = new FormAttachment(0, 44);
		fd_label_1.right = new FormAttachment(0, 97);
		fd_label_1.left = new FormAttachment(0, 25);
		label_1.setLayoutData(fd_label_1);
		label_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_1.setText("测试命令:");

		label_2 = new Label(group, SWT.NONE);
		final FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(0, 15);
		fd_label_2.left = new FormAttachment(0, 526);
		label_2.setLayoutData(fd_label_2);
		label_2.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_2.setText("线程数量:");
		 
		txtThdCount = new Text(group, SWT.BORDER);
		txtThdCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_txt_thdCount = new FormData();
		fd_txt_thdCount.bottom = new FormAttachment(0, 36);
		fd_txt_thdCount.top = new FormAttachment(0, 13);
		fd_txt_thdCount.right = new FormAttachment(0, 704);
		fd_txt_thdCount.left = new FormAttachment(0, 604);
		txtThdCount.setLayoutData(fd_txt_thdCount);
		txtThdCount.setText("1");

		label_3 = new Label(group, SWT.NONE);
		final FormData fd_label_3 = new FormData();
		fd_label_3.bottom = new FormAttachment(0, 58);
		fd_label_3.top = new FormAttachment(0, 42);
		fd_label_3.right = new FormAttachment(0, 598);
		fd_label_3.left = new FormAttachment(0, 510);
		label_3.setLayoutData(fd_label_3);
		label_3.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_3.setText("延时(毫秒):");

		txtDelay = new Text(group, SWT.BORDER);
		txtDelay.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_txt_delay = new FormData();
		fd_txt_delay.bottom = new FormAttachment(0, 64);
		fd_txt_delay.top = new FormAttachment(0, 41);
		fd_txt_delay.right = new FormAttachment(0, 705);
		fd_txt_delay.left = new FormAttachment(0, 605);
		txtDelay.setLayoutData(fd_txt_delay);
		txtDelay.setText("1000,200,5000");

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText("测试信息");
		final FormData fd_group_1 = new FormData();
		fd_group_1.bottom = new FormAttachment(0, 442);
		fd_group_1.top = new FormAttachment(0, 90);
		fd_group_1.right = new FormAttachment(group, 0, SWT.RIGHT);
		fd_group_1.left = new FormAttachment(group, 0, SWT.LEFT);
		group_1.setLayoutData(fd_group_1);
		group_1.setLayout(new FormLayout());
		//
		table = new Table(group_1, SWT.VIRTUAL |SWT.BORDER|SWT.FULL_SELECTION );
		table.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		final FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(0, 335);
		fd_table.top = new FormAttachment(0, 5);
		fd_table.right = new FormAttachment(100, -5);
		fd_table.left = new FormAttachment(0, 5);
		table.setLayoutData(fd_table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(70);
		newColumnTableColumn.setText("线程ID");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(150);
		newColumnTableColumn_1.setText("启动时间");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(120);
		newColumnTableColumn_2.setText("命令发送次数");

		final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);

		final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(220);
		newColumnTableColumn_4.setText("异常信息");
		newColumnTableColumn_3.setWidth(150);
		newColumnTableColumn_3.setText("停止时间");

		Group group_2;
		group_2 = new Group(shell, SWT.NONE);
		group_2.setText("状态信息");
		final FormData fd_group_2 = new FormData();
		fd_group_2.bottom = new FormAttachment(0, 490);
		fd_group_2.top = new FormAttachment(0, 444);
		fd_group_2.right = new FormAttachment(0, 740);
		fd_group_2.left = new FormAttachment(0, 8);
		group_2.setLayoutData(fd_group_2);
		group_2.setLayout(new FormLayout());

		final Label label_4 = new Label(group_2, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_label_4 = new FormData();
		fd_label_4.top = new FormAttachment(0, 8);
		fd_label_4.right = new FormAttachment(0, 117);
		fd_label_4.left = new FormAttachment(0, 7);
		label_4.setLayoutData(fd_label_4);
		label_4.setText("活动线程数量:");

		final Label label_4_1 = new Label(group_2, SWT.NONE);
		label_4_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_label_4_1 = new FormData();
		fd_label_4_1.bottom = new FormAttachment(0, 26);
		fd_label_4_1.top = new FormAttachment(0, 9);
		fd_label_4_1.right = new FormAttachment(0, 385);
		fd_label_4_1.left = new FormAttachment(0, 280);
		label_4_1.setLayoutData(fd_label_4_1);
		label_4_1.setText("命令失败次数:");

		final Label label_4_2 = new Label(group_2, SWT.NONE);
		label_4_2.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_label_4_2 = new FormData();
		fd_label_4_2.bottom = new FormAttachment(0, 26);
		fd_label_4_2.top = new FormAttachment(0, 9);
		fd_label_4_2.right = new FormAttachment(0, 625);
		fd_label_4_2.left = new FormAttachment(0, 520);
		label_4_2.setLayoutData(fd_label_4_2);
		label_4_2.setText("命令发送次数:");

		lbRunCount = new Label(group_2, SWT.NONE);
		lbRunCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_lbRunCount = new FormData();
		fd_lbRunCount.bottom = new FormAttachment(0, 26);
		fd_lbRunCount.top = new FormAttachment(0, 9);
		fd_lbRunCount.right = new FormAttachment(0, 191);
		fd_lbRunCount.left = new FormAttachment(0, 116);
		lbRunCount.setLayoutData(fd_lbRunCount);

		lbErrorCount = new Label(group_2, SWT.NONE);
		lbErrorCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_lbStopCount = new FormData();
		fd_lbStopCount.bottom = new FormAttachment(0, 27);
		fd_lbStopCount.top = new FormAttachment(0, 10);
		fd_lbStopCount.right = new FormAttachment(0, 468);
		fd_lbStopCount.left = new FormAttachment(0, 388);
		lbErrorCount.setLayoutData(fd_lbStopCount);

		lbCmdCount = new Label(group_2, SWT.NONE);
		lbCmdCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_lbcmdcountLabel = new FormData();
		fd_lbcmdcountLabel.bottom = new FormAttachment(0, 26);
		fd_lbcmdcountLabel.top = new FormAttachment(0, 9);
		fd_lbcmdcountLabel.right = new FormAttachment(0, 713);
		fd_lbcmdcountLabel.left = new FormAttachment(0, 628);
		lbCmdCount.setLayoutData(fd_lbcmdcountLabel);

		btnClose = new Button(shell, SWT.NONE);
		btnClose.setText("关闭窗口");
		final FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(0, 524);
		fd_btnClose.top = new FormAttachment(0, 496);
		fd_btnClose.left = new FormAttachment(btnClrTxt, 312, SWT.DEFAULT);
		fd_btnClose.right = new FormAttachment(100, -6);
		btnClose.setLayoutData(fd_btnClose);
	}
	
	public void setServerUrl(String url)
	{
		srvUrl = url;
	}
}
