package update.release;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class UpdateForm
{
	private String appName = null;

	private Button btnAllSelServer;
	private Button btnSaveCfg;
	private Button btnDelServer = null;
	private Button btnNewServer = null;
	private Button btnReadServer = null;

	private Button btnpasvCfg;
	private Table tableServer;
	private Text txtExecuFileName = null;
	private Text txtModeName = null;
	private Text txtPath = null;
	private Text txtSourceFile = null;
	private Text txtTarget = null;
	private Text txtModule = null;
	private Text txtSYJ = null;
	private Label lblStatus = null;

	private Text txtIp = null;
	private Text txtPort = null;
	private Text txtUser = null;
	private Text txtPassWord = null;

	private Button btnSelectFile = null;
	private Button btnSelectDir = null;
	private Button btnDecompress = null;
	private Button btnDel = null;
	private Button btnSend = null;
	private Button btnClose = null;
	private Button btnUserCfg = null;
	private Button btnExecute = null;
	private Button btnQueryPublish;
	private Button btnDeletePublish;
	private Table tablePublish;
	private Button btndeladv = null;

	private TabFolder tabFolder;

	private Shell shell = null;

	public UpdateForm()
	{
		try
		{
			this.open();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public UpdateForm(String name)
	{
		try
		{
			this.appName = name;
			this.open();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void open()
	{
		final Display display = Display.getDefault();
		createContents();

		new UpdateEvent(this);
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	protected void createContents()
	{
		shell = new Shell(SWT.MIN);
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(745, 594);
		shell.setBounds(area.width / 2 - shell.getSize().x / 2, area.height / 2 - shell.getSize().y / 2, shell.getSize().x, shell.getSize().y);
		shell.setText(Language.apply("更新发布"));
		shell.setLayout(null);

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(5, 140, 730, 423);

		final TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText(Language.apply("发布文件配置"));

		final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_1);

		final Group group_2 = new Group(composite_1, SWT.NONE);
		group_2.setBounds(5, 0, 706, 226);

		final Label label_4 = new Label(group_2, SWT.NONE);
		label_4.setBounds(5, 15, 80, 21);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setText(Language.apply("上传文件"));

		txtSourceFile = new Text(group_2, SWT.BORDER);
		txtSourceFile.setBounds(91, 15, 529, 23);

		btnSelectFile = new Button(group_2, SWT.NONE);
		btnSelectFile.setBounds(625, 15, 33, 23);
		btnSelectFile.setFont(SWTResourceManager.getFont("宋体", 13, SWT.BOLD));
		btnSelectFile.setText("...");

		final Label label_6 = new Label(group_2, SWT.NONE);
		label_6.setBounds(5, 120, 80, 21);
		label_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6.setText(Language.apply("目标目录"));

		txtTarget = new Text(group_2, SWT.BORDER);
		txtTarget.setBounds(90, 120, 299, 23);

		final Label label_5 = new Label(group_2, SWT.NONE);
		label_5.setBounds(5, 155, 80, 21);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_5.setText(Language.apply("重 命 名"));

		txtModule = new Text(group_2, SWT.BORDER);
		txtModule.setBounds(90, 155, 299, 23);

		final Label label_7 = new Label(group_2, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_7.setText(Language.apply("收银机号"));
		label_7.setBounds(5, 190, 80, 22);

		txtSYJ = new Text(group_2, SWT.BORDER);
		txtSYJ.setBounds(90, 190, 299, 23);

		final Label label_8 = new Label(group_2, SWT.NONE);
		label_8.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_8.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_8.setText(Language.apply("(指定要安装的目录,不填则为当前程序根目录)"));
		label_8.setBounds(395, 125, 308, 16);

		final Label label_9 = new Label(group_2, SWT.NONE);
		label_9.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_9.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_9.setText(Language.apply("(指定更新后的文件名,不填则模块名)"));
		label_9.setBounds(395, 160, 308, 16);

		final Label label_10 = new Label(group_2, SWT.NONE);
		label_10.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_10.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_10.setText(Language.apply("(指定要更新收银机以逗号分隔,不填则所有)"));
		label_10.setBounds(395, 195, 301, 16);

		final Label label_11 = new Label(group_2, SWT.NONE);
		label_11.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_11.setText(Language.apply("FTP路径"));
		label_11.setBounds(10, 85, 74, 21);

		txtPath = new Text(group_2, SWT.BORDER);
		txtPath.setBounds(90, 85, 299, 23);

		final Label label_12 = new Label(group_2, SWT.NONE);
		label_12.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_12.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_12.setText(Language.apply("(指定FTP路径目录,不填则为FTP根目录)"));
		label_12.setBounds(395, 90, 308, 17);

		final Label label_13 = new Label(group_2, SWT.NONE);
		label_13.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_13.setText(Language.apply("模块命名"));
		label_13.setBounds(5, 50, 78, 21);

		txtModeName = new Text(group_2, SWT.BORDER);
		txtModeName.setBounds(90, 50, 298, 23);

		final Label label_14 = new Label(group_2, SWT.NONE);
		label_14.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_14.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_14.setText(Language.apply("(指定上传FTP文件名)"));
		label_14.setBounds(395, 55, 308, 16);

		btnSelectDir = new Button(group_2, SWT.NONE);
		btnSelectDir.setBounds(664, 15, 35, 23);
		btnSelectDir.setText(Language.apply("目录"));

		final Group group_3 = new Group(composite_1, SWT.NONE);
		group_3.setBounds(5, 230, 619, 167);

		btnDecompress = new Button(group_3, SWT.CHECK);
		btnDecompress.setBounds(5, 55, 393, 21);
		btnDecompress.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btnDecompress.setText(Language.apply("更新时解压缩需要更新的文件,解压完执行"));

		btnDel = new Button(group_3, SWT.CHECK);
		btnDel.setBounds(5, 17, 284, 21);
		btnDel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btnDel.setText(Language.apply("更新前先删除源文件后再更新"));

		final Label label_10_1 = new Label(group_3, SWT.NONE);
		label_10_1.setBounds(295, 14, 314, 30);
		label_10_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_10_1.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
		label_10_1.setText(Language.apply("更新Derby本地数据库时,以压缩文件上传数据\n库，同时请勾选删除、解压缩两个选项"));

		btnExecute = new Button(group_3, SWT.CHECK);
		btnExecute.setText(Language.apply("更新后立即执行当前更新文件"));
		btnExecute.setBounds(5, 93, 284, 21);
		btnExecute.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		txtExecuFileName = new Text(group_3, SWT.BORDER);
		txtExecuFileName.setBounds(401, 55, 158, 23);

		final Label label_15 = new Label(group_3, SWT.NONE);
		label_15.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_15.setText(Language.apply("文件"));
		label_15.setBounds(565, 55, 45, 21);

		lblStatus = new Label(group_3, SWT.NONE);
		lblStatus.setBounds(295, 93, 314, 21);
		lblStatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		btndeladv = new Button(group_3, SWT.CHECK);
		btndeladv.setText(Language.apply("发布广告时删除客户端所有文件"));
		btndeladv.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		btndeladv.setBounds(5, 127, 353, 21);

		btnSend = new Button(composite_1, SWT.NONE);
		btnSend.setText(Language.apply("发送"));
		btnSend.setBounds(630, 314, 79, 34);

		btnClose = new Button(composite_1, SWT.NONE);
		btnClose.setBounds(630, 359, 79, 34);
		btnClose.setText(Language.apply("关闭"));

		final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Language.apply("Ftp配置参数"));

		final Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);

		final Group group = new Group(composite, SWT.NONE);
		group.setBounds(10, 10, 699, 136);

		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 15, 60, 21);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("服务器"));

		txtIp = new Text(group, SWT.BORDER);
		txtIp.setBounds(85, 15, 524, 21);
		txtIp.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setBounds(10, 45, 60, 21);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("端口号"));

		txtPort = new Text(group, SWT.BORDER);
		txtPort.setBounds(85, 45, 524, 21);
		txtPort.setText("21");
		txtPort.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setBounds(10, 75, 60, 21);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("用户名"));

		txtUser = new Text(group, SWT.BORDER);
		txtUser.setBounds(85, 75, 524, 21);
		txtUser.setText("update");
		txtUser.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setBounds(10, 105, 60, 21);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("密  码"));

		txtPassWord = new Text(group, SWT.BORDER);
		txtPassWord.setText("update");
		txtPassWord.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));
		txtPassWord.setBounds(85, 105, 524, 21);

		btnUserCfg = new Button(group, SWT.CHECK);
		btnUserCfg.setForeground(SWTResourceManager.getColor(255, 0, 0));
		btnUserCfg.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		btnUserCfg.setText(Language.apply("匿名用户"));
		btnUserCfg.setBounds(615, 105, 74, 21);

		btnpasvCfg = new Button(group, SWT.CHECK);
		btnpasvCfg.setBounds(615, 75, 74, 21);
		btnpasvCfg.setForeground(SWTResourceManager.getColor(255, 0, 0));
		btnpasvCfg.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		btnpasvCfg.setText(Language.apply("被动模式"));

		btnSaveCfg = new Button(group, SWT.NONE);
		btnSaveCfg.setBounds(615, 15, 82, 22);
		btnSaveCfg.setText(Language.apply("保存"));

		tablePublish = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tablePublish.setLinesVisible(true);
		tablePublish.setHeaderVisible(true);
		tablePublish.setBounds(10, 152, 609, 240);

		final TableColumn newColumnTableColumn = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn.setWidth(130);
		newColumnTableColumn.setText(Language.apply("程序文件名"));

		final TableColumn newColumnTableColumn_8 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_8.setWidth(120);
		newColumnTableColumn_8.setText(Language.apply("发布时间"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_2.setWidth(120);
		newColumnTableColumn_2.setText(Language.apply("FTP路径"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_1.setWidth(130);
		newColumnTableColumn_1.setText(Language.apply("目标安装目录"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_3.setWidth(100);
		newColumnTableColumn_3.setText(Language.apply("目标重命名"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_4.setWidth(100);
		newColumnTableColumn_4.setText(Language.apply("目标收银机"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_5.setWidth(50);
		newColumnTableColumn_5.setText(Language.apply("删除？"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_6.setWidth(50);
		newColumnTableColumn_6.setText(Language.apply("解压？"));

		final TableColumn newColumnTableColumn_7 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_7.setWidth(50);
		newColumnTableColumn_7.setText(Language.apply("执行？"));

		final TableColumn newColumnTableColumn_9 = new TableColumn(tablePublish, SWT.NONE);
		newColumnTableColumn_9.setWidth(120);
		newColumnTableColumn_9.setText(Language.apply("解压后执行"));

		btnQueryPublish = new Button(composite, SWT.NONE);
		btnQueryPublish.setBounds(625, 152, 82, 25);
		btnQueryPublish.setText(Language.apply("查看已发布"));

		btnDeletePublish = new Button(composite, SWT.NONE);
		btnDeletePublish.setBounds(625, 188, 82, 25);
		btnDeletePublish.setText(Language.apply("删除已发布"));

		tableServer = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		tableServer.setBounds(5, 5, 620, 125);
		tableServer.setLinesVisible(true);
		tableServer.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_10 = new TableColumn(tableServer, SWT.NONE);
		newColumnTableColumn_10.setWidth(122);
		newColumnTableColumn_10.setText(Language.apply("服务器IP"));

		final TableColumn newColumnTableColumn_11 = new TableColumn(tableServer, SWT.NONE);
		newColumnTableColumn_11.setWidth(118);
		newColumnTableColumn_11.setText(Language.apply("用户"));

		final TableColumn newColumnTableColumn_13 = new TableColumn(tableServer, SWT.NONE);

		final TableColumn newColumnTableColumn_12 = new TableColumn(tableServer, SWT.NONE);
		newColumnTableColumn_12.setWidth(490);
		newColumnTableColumn_12.setText(Language.apply("信息"));
		newColumnTableColumn_13.setWidth(100);
		newColumnTableColumn_13.setText(Language.apply("状态"));

		btnReadServer = new Button(shell, SWT.NONE);
		btnReadServer.setBounds(631, 10, 84, 22);
		btnReadServer.setText(Language.apply("读取配置"));

		btnNewServer = new Button(shell, SWT.NONE);
		btnNewServer.setBounds(631, 49, 84, 22);
		btnNewServer.setText(Language.apply("增加"));

		btnDelServer = new Button(shell, SWT.NONE);
		btnDelServer.setBounds(631, 80, 84, 22);
		btnDelServer.setText(Language.apply("删除"));

		btnAllSelServer = new Button(shell, SWT.NONE);
		btnAllSelServer.setBounds(631, 108, 84, 22);
		btnAllSelServer.setText(Language.apply("全选"));
	}

	public TabFolder gettabFolder()
	{
		return tabFolder;
	}

	public Text getTxtModeName()
	{
		return txtModeName;
	}

	public Text getTxtPath()
	{
		return txtPath;
	}

	public Text getTxtSourceFile()
	{
		return txtSourceFile;
	}

	public Text getTxtTarget()
	{
		return txtTarget;
	}

	public Text getTxtModule()
	{
		return txtModule;
	}

	public Text getTxtSYJ()
	{
		return txtSYJ;
	}

	public Text getTxtIp()
	{
		return txtIp;
	}

	public Text getTxtPort()
	{
		return txtPort;
	}

	public Text getTxtUser()
	{
		return txtUser;
	}

	public Text getTxtExecuFileName()
	{
		return txtExecuFileName;
	}

	public Text getTxtPassWord()
	{
		return txtPassWord;
	}

	public Button getBtnSelectFile()
	{
		return btnSelectFile;
	}

	public Button getBtnSelectDir()
	{
		return btnSelectDir;
	}

	public Button getBtnDecompress()
	{
		return btnDecompress;
	}

	public Button getBtnDel()
	{
		return btnDel;
	}

	public Button getBtnSend()
	{
		return btnSend;
	}

	public Button getBtnClose()
	{
		return btnClose;
	}

	public Button getBtnUserCfg()
	{
		return btnUserCfg;
	}

	public Button getBtnExecute()
	{
		return btnExecute;
	}

	public Button getBtnDelAdv()
	{
		return btndeladv;
	}

	public Button getBtnQueryPublish()
	{
		return btnQueryPublish;
	}

	public Button getBtnDeletePublish()
	{
		return btnDeletePublish;
	}

	public Table getTablePublish()
	{
		return tablePublish;
	}

	public Table getTableServer()
	{
		return tableServer;
	}

	public Label getLblStatus()
	{
		return lblStatus;
	}

	public Shell getShell()
	{
		return shell;
	}

	public Button getbtnDelServer()
	{
		return btnDelServer;
	}

	public Button getBtnReadServer()
	{
		return btnReadServer;
	}

	public Button getbtnNewServer()
	{
		return btnNewServer;
	}

	public Button getbtnSaveCfg()
	{
		return btnSaveCfg;
	}

	public Button getbtnpasvCfg()
	{
		return btnpasvCfg;
	}

	public Button getbtnAllSel()
	{
		return btnAllSelServer;
	}

	public String getAppName()
	{
		return this.appName;
	}
}
