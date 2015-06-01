package update.release;

import java.io.File;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class UpdateEvent
{
	private String appName = null;
	private Text txtSourceFile = null;
	private Text txtModeName = null;
	private Text txtTarget = null;
	private Text txtModule = null;
	private Text txtSYJ = null;
	private Text txtFtpPath = null;
	private Label lblStatus = null;

	private Text txtExecuFileName = null;

	private Button btnSelectFile = null;
	private Button btnSelectDir = null;
	private Button btnDecompress = null;
	private Button btnDel = null;
	private Button btnSend = null;
	private Button btnClose = null;

	private Button btnExecute = null;
	private Button btndeladv = null;
	
	private Button btnReadServer = null;
	private Button btnNewServer = null;
	private Button btnDelServer = null;
	private Button btnAllSelServer = null;

	private Button btnSaveCfg = null;
	private Button btnpasvCfg = null;
	private Button btnUserCfg = null;
	private Text txtIp = null;
	private Text txtPort = null;
	private Text txtUser = null;
	private Text txtPassWord = null;

	private Button btnQueryPublish = null;
	private Button btnDeletePublish = null;

	private TabFolder tabFloader = null;

	private Table tablePublish = null;
	private Table tableServer = null;

	private Shell shell = null;

	private UpdateBS ubs = null;

	public UpdateEvent(UpdateForm uf)
	{
		this.appName = uf.getAppName();

		txtModeName = uf.getTxtModeName();
		txtSourceFile = uf.getTxtSourceFile();
		txtTarget = uf.getTxtTarget();
		txtModule = uf.getTxtModule();
		txtSYJ = uf.getTxtSYJ();
		txtFtpPath = uf.getTxtPath();

		txtIp = uf.getTxtIp();
		txtPort = uf.getTxtPort();
		txtUser = uf.getTxtUser();
		txtPassWord = uf.getTxtPassWord();
		txtExecuFileName = uf.getTxtExecuFileName();

		btnSelectFile = uf.getBtnSelectFile();
		btnSelectDir = uf.getBtnSelectDir();
		btnDecompress = uf.getBtnDecompress();
		btnDel = uf.getBtnDel();
		btnSend = uf.getBtnSend();
		btnClose = uf.getBtnClose();

		btnExecute = uf.getBtnExecute();
		btndeladv = uf.getBtnDelAdv();
		
		btnDelServer = uf.getbtnDelServer();
		btnReadServer = uf.getBtnReadServer();
		btnNewServer = uf.getbtnNewServer();
		btnAllSelServer = uf.getbtnAllSel();

		btnSaveCfg = uf.getbtnSaveCfg();
		btnUserCfg = uf.getBtnUserCfg();
		btnpasvCfg = uf.getbtnpasvCfg();

		btnQueryPublish = uf.getBtnQueryPublish();
		btnDeletePublish = uf.getBtnDeletePublish();

		tabFloader = uf.gettabFolder();

		tablePublish = uf.getTablePublish();
		tableServer = uf.getTableServer();

		lblStatus = uf.getLblStatus();

		shell = uf.getShell();

		ubs = new UpdateBS();

		ButtonEvent be = new ButtonEvent();

		btnAllSelServer.addSelectionListener(be);
		btnSelectFile.addSelectionListener(be);
		btnSelectDir.addSelectionListener(be);
		btnDecompress.addSelectionListener(be);
		btnDel.addSelectionListener(be);
		btnSend.addSelectionListener(be);
		btnClose.addSelectionListener(be);

		btnExecute.addSelectionListener(be);
		btndeladv.addSelectionListener(be);
		
		btnDelServer.addSelectionListener(be);
		btnNewServer.addSelectionListener(be);
		btnReadServer.addSelectionListener(be);

		btnUserCfg.addSelectionListener(be);
		btnSaveCfg.addSelectionListener(be);
		btnpasvCfg.addSelectionListener(be);

		btnQueryPublish.addSelectionListener(be);
		btnDeletePublish.addSelectionListener(be);

		tableServer.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				tabFloader.setSelection(1);
			}
		});

		tableServer.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int index = tableServer.getSelectionIndex();

				if (index < 0 || index >= tableServer.getItemCount())
					return;

				SetDetail(index);
			}
		});

		this.init();
	}

	public void SetDetail(int index)
	{
		FtpCfgDef fcd = (FtpCfgDef) ubs.vecFtpCfg.get(index);

		txtIp.setText(fcd.FtpIP);
		txtPort.setText(String.valueOf(fcd.FtpPort));
		btnUserCfg.setSelection(fcd.isanonymous);
		btnpasvCfg.setSelection(fcd.Ftppasv.equals("Y"));

		changeanos();

		txtUser.setText(fcd.FtpUser);
		txtPassWord.setText(fcd.FtpPwd);

		tablePublish.removeAll();
	}

	public void init()
	{
		ubs.init();

		this.RefreshTable();
	}

	public Text getTxtModeName()
	{
		return txtModeName;
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

	public Text getTxtFtpPath()
	{
		return txtFtpPath;
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

	public Text getTxtPassWord()
	{
		return txtPassWord;
	}

	public Button getBtnUserCfg()
	{
		return btnUserCfg;
	}

	public Text getTxtExecuFileName()
	{
		return txtExecuFileName;
	}

	public Button getBtnSelectFile()
	{
		return btnSelectFile;
	}

	public Button getBtnDecompress()
	{
		return btnDecompress;
	}

	public Button getBtnDel()
	{
		return btnDel;
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

	public Label getLblStatus()
	{
		return lblStatus;
	}

	public Shell getShell()
	{
		return shell;
	}

	// 得到文件名
	public String getFilename(String filename)
	{
		try
		{
			String sourcename = null;

			int index = filename.lastIndexOf('\\');

			if (index < 0)
			{
				index = filename.lastIndexOf('/');
			}

			if (index > -1)
			{
				sourcename = filename.substring((index + 1));
			}
			else
			{
				sourcename = filename.substring(0);
			}

			return sourcename;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return null;
		}
	}

	// 打开目录对话框
	public void openDirDialog()
	{
		// 创建一个打开对话框,样式设置为SWT.OPEN
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);

		// 打开窗口,返回用户所选的文件目录
		String file = dialog.open();

		if (file != null)
		{
			txtSourceFile.setText(file);
			txtModeName.setText("*.*");
			txtTarget.setText("");
		}
	}

	public void openFileDialogForJStore()
	{
		// 创建一个打开对话框,样式设置为SWT.OPEN
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		// 设置打开默认的路径
		// dialog.setFilterPath(System.getProperty("java.home"));

		// 设置所打开文件的扩展名
		dialog.setFilterExtensions(new String[] { "*.jar", "*.ini", "*.zip", "*.*" });

		// 设置显示到下拉框中的扩展名的名称
		dialog.setFilterNames(new String[] { "JAR Files(*.jar)", "INI Files(*.ini)", "ZIP Files(*.zip)", "ALL Files(*.*)" });

		// 打开窗口,返回用户所选的文件目录
		String file = dialog.open();

		if (file != null)
		{
			txtSourceFile.setText(file);
			txtModeName.setText(getFilename(txtSourceFile.getText()));
			
			String str = getFilename(txtSourceFile.getText()).toLowerCase();

			if (str.lastIndexOf(".jar") >= 0)
			{
				if (str.equalsIgnoreCase("JStore.jar") || str.equalsIgnoreCase("update.jar"))
				{
					txtTarget.setText("");
				}
				else
				{
					txtTarget.setText("./JStore.ExtendJar");
				}
			}
			else if (str.lastIndexOf(".ini") >= 0||str.lastIndexOf(".xml")>0)
			{
				txtTarget.setText("./JStore.ConfigFile");
			}
			else if (str.lastIndexOf(".xls")>=0)
			{
				txtTarget.setText("./JStore.PrintTemplate");
			}
			else
			{
				txtTarget.setText("");
			}
		}

	}

	// 打开文件对话框
	public void openFileDialogForPos()
	{
		// 创建一个打开对话框,样式设置为SWT.OPEN
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		// 设置打开默认的路径
		// dialog.setFilterPath(System.getProperty("java.home"));

		// 设置所打开文件的扩展名
		dialog.setFilterExtensions(new String[] { "*.jar", "*.ini", "*.zip", "*.*" });

		// 设置显示到下拉框中的扩展名的名称
		dialog.setFilterNames(new String[] { "JAR Files(*.jar)", "INI Files(*.ini)", "ZIP Files(*.zip)", "ALL Files(*.*)" });

		// 打开窗口,返回用户所选的文件目录
		String file = dialog.open();

		if (file != null)
		{
			txtSourceFile.setText(file);
			txtModeName.setText(getFilename(txtSourceFile.getText()));

			//
			String str = getFilename(txtSourceFile.getText()).toLowerCase();

			if (str.lastIndexOf(".jar") >= 0)
			{
				if (str.equalsIgnoreCase("javapos.jar") || str.equalsIgnoreCase("update.jar"))
				{
					txtTarget.setText("");
				}
				else
				{
					txtTarget.setText("./javaPos.ExtendJar");
				}
			}
			else if (str.lastIndexOf(".ini") >= 0)
			{
				txtTarget.setText("./javaPos.ConfigFile");
			}
			else if (str.equals("base.zip") || str.equals("local.zip") || str.equals("day.zip"))
			{
				txtTarget.setText("./javaPos.Database");
			}
			else
			{
				txtTarget.setText("");
			}
		}
	}

	// 匿名切换
	public void changeanos()
	{
		if (btnUserCfg.getSelection())
		{
			txtUser.setText("anonymous");
			txtPassWord.setText("");
			txtUser.setEnabled(false);
			txtPassWord.setEnabled(false);
		}
		else
		{
			txtUser.setText("");
			txtPassWord.setText("");
			txtUser.setEnabled(true);
			txtPassWord.setEnabled(true);
		}
	}

	// 关闭
	public void closepro()
	{
		shell.close();
		shell.dispose();
		shell = null;
	}

	public void queryPublish()
	{
		tablePublish.removeAll();

		if (!ChecInput())
			return;

		Vector vcpd = new Vector();

		FtpCfgDef fcd = new FtpCfgDef();
		fcd.FtpIP = txtIp.getText().trim();
		fcd.FtpPort = (ubs.isNumeric(txtPort.getText().trim()) ? Integer.parseInt(txtPort.getText().trim()) : 21);
		fcd.FtpUser = txtUser.getText().trim();
		fcd.FtpPwd = txtPassWord.getText().trim();
		fcd.Ftppasv = btnpasvCfg.getSelection() ? "Y" : "N";
		fcd.isanonymous = btnUserCfg.getSelection();

		// if
		// (ubs.queryPublish(txtIp.getText().trim(),(ubs.isNumeric(txtPort.getText().trim())?Integer.parseInt(txtPort.getText().trim()):21),txtUser.getText(),txtPassWord.getText().trim(),vcpd))
		if (ubs.queryPublish(fcd, vcpd))
		{
			for (int i = 0; i < vcpd.size(); i++)
			{
				PublishDef pd = (PublishDef) vcpd.get(i);
				TableItem item = new TableItem(tablePublish, SWT.NONE);

				item.setText(0, pd.name);
				item.setText(1, pd.datetime);
				item.setText(2, pd.ftppath);
				item.setText(3, pd.installpath);
				item.setText(4, pd.filename);
				item.setText(5, pd.code);
				item.setText(6, pd.del);
				item.setText(7, pd.decompress);
				item.setText(8, pd.execute);
				item.setText(9, pd.executefilename);
			}
		}
		else
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage(ubs.getMessage());
			mb.open();
		}
	}

	public void deletePublish()
	{
		Table tb = tablePublish;
		int rmidx = tb.getSelectionIndex();
		if (rmidx < 0)
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("请先选择要删除程序的发布行");
			mb.open();
			return;
		}

		String modulename = tb.getItem(rmidx).getText(0);
		String ftppath = tb.getItem(rmidx).getText(2);
		MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setMessage("你确定要删除已发布的 " + modulename + " 程序模块吗?");
		if (mb.open() != SWT.YES)
			return;
		else
			while (Display.getCurrent().readAndDispatch());

		FtpCfgDef fcd = new FtpCfgDef();
		fcd.FtpIP = txtIp.getText().trim();
		fcd.FtpPort = (ubs.isNumeric(txtPort.getText().trim()) ? Integer.parseInt(txtPort.getText().trim()) : 21);
		fcd.FtpUser = txtUser.getText().trim();
		fcd.FtpPwd = txtPassWord.getText().trim();
		fcd.Ftppasv = btnpasvCfg.getSelection() ? "Y" : "N";
		fcd.isanonymous = btnUserCfg.getSelection();

		// if
		// (!ubs.deletePublish(txtIp.getText().trim(),Integer.parseInt(txtPort.getText().trim()),txtUser.getText().trim(),txtPassWord.getText().trim(),modulename,
		// ftppath))
		if (!ubs.deletePublish(fcd, modulename, ftppath))
		{
			mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage(ubs.getMessage());
			mb.open();
		}

		tablePublish.remove(rmidx);

		mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setMessage(modulename + " 程序模板删除成功...");
		mb.open();
	}

	private boolean ChecInput()
	{
		if ((txtIp.getText() == null) || txtIp.getText().trim().equals(""))
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("IP地址不能为空...");
			mb.open();

			return false;
		}

		// 有可能配置的是域名,所以不检查IP地址的合法性
		/*
		 * if (!ubs.isValidIPAddress(txtIp.getText().trim())) { MessageBox mb =
		 * new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		 * mb.setMessage("IP地址不合法..."); mb.open();
		 * 
		 * return false; }
		 */

		if ((txtUser.getText() == null) || txtUser.getText().trim().equals(""))
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("Ftp用户名不能为空...");
			mb.open();

			return false;
		}

		if ((txtPort.getText() == null) || txtPort.getText().trim().equals(""))
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("Ftp端口不能为空...");
			mb.open();

			return false;
		}

		if (!ubs.isNumeric(txtPort.getText().trim()))
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("Ftp端口只能是数字...");
			mb.open();

			return false;
		}

		return true;
	}

	private void AddNewTableServer(FtpCfgDef fcd)
	{
		TableItem ti = new TableItem(tableServer, SWT.NONE);

		ti.setChecked(true);

		RefreshTableServer(ubs.vecFtpCfg.indexOf(fcd));
	}

	private void RefreshTableServer(int index)
	{
		// 名称，服务器IP，状态，信息
		// (0-成功,1-未上传,2-正在上传,3-失败)
		FtpCfgDef fcd = (FtpCfgDef) ubs.vecFtpCfg.get(index);
		TableItem item = (TableItem) tableServer.getItem(index);
		// TableItem item = new TableItem(tableServer, SWT.NONE);
		item.setText(0, fcd.FtpIP);
		item.setText(1, fcd.FtpUser);
		switch (fcd.Status)
		{
			case 0:
				item.setText(2, "成功");
				break;
			case 1:
				item.setText(2, "未上传");
				break;
			case 2:
				item.setText(2, "正在上传");
				break;
			case 3:
				item.setText(2, "失败");
				break;
		}

		item.setText(3, fcd.Message);
	}

	// 刷新更新服务器配置的显示
	private void RefreshTable()
	{
		tableServer.removeAll();

		for (int i = 0; i < ubs.vecFtpCfg.size(); i++)
		{
			AddNewTableServer((FtpCfgDef) ubs.vecFtpCfg.get(i));
		}

		if (tableServer.getItemCount() > 0)
		{
			tableServer.deselectAll();
			tableServer.setSelection(0);
			tableServer.select(0);

			SetDetail(0);
		}
	}

	// 保存
	public void saveServer()
	{
		if (ubs.saveFtpPara())
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setMessage("发布配置保存成功");
			mb.open();
		}
		else
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setMessage(ubs.getMessage());
			mb.open();
		}
	}

	// 读取配置
	public void readServer()
	{
		String path = "";
		// 创建一个打开对话框,样式设置为SWT.OPEN
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		// 设置所打开文件的扩展名
		dialog.setFilterExtensions(new String[] { "*.ini", "*.*" });
		dialog.setFilterNames(new String[] { "INI Files(*.ini)", "ALL Files(*.*)" });
		dialog.setFilterPath(path);

		// 打开窗口,返回用户所选的文件目录
		String file = dialog.open();
		if (file == null || file.length() <= 0)
			return;

		ubs.setPath(path);
		ubs.setPublishfile(file);

		if (ubs.readConfigFile())
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setMessage("读取配置成功");
			mb.open();
		}
		else
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setMessage(ubs.getMessage());
			mb.open();
		}
		RefreshTable();
	}

	// 新增更新服务器配置
	private void newServer()
	{
		tabFloader.setSelection(1);
		tableServer.deselectAll();
		try
		{
			txtIp.setText("");
			txtPort.setText("21");
			txtUser.setText("update");
			txtUser.setEnabled(true);
			txtPassWord.setText("update");
			txtPassWord.setEnabled(true);
			btnUserCfg.setSelection(false);
			btnpasvCfg.setSelection(false);

			txtIp.setFocus();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 删除更新服务器配置
	private void delServer()
	{
		int index = tableServer.getSelectionIndex();

		try
		{
			FtpCfgDef fcd = (FtpCfgDef) ubs.vecFtpCfg.get(index);
			MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mb.setMessage("是否删除 IP地址[" + fcd.FtpIP + "] 用户[" + fcd.FtpUser + "] 的更新服务器配置?");
			if (mb.open() != SWT.YES)
				return;

			ubs.vecFtpCfg.remove(index);
			tableServer.remove(index);

			Display.getCurrent().readAndDispatch();

			saveServer();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void allSelServer()
	{
		for (int i = 0; i < this.tableServer.getItemCount(); i++)
		{
			TableItem ti = tableServer.getItem(i);

			ti.setChecked(true);
		}
	}

	public void saveCfg()
	{
		if (!ChecInput())
			return;

		FtpCfgDef fcd = null;
		if (tableServer.getSelectionIndex() < 0)
		{
			for (int i = 0; i < ubs.vecFtpCfg.size(); i++)
			{
				FtpCfgDef mfcd = (FtpCfgDef) ubs.vecFtpCfg.get(i);
				if (mfcd.FtpIP.equals(txtIp.getText().trim()) && mfcd.FtpUser.equals(txtUser.getText()))
				{
					MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					mb.setMessage("服务器IP[" + txtIp.getText().trim() + "] 用户[" + txtUser.getText() + "] 的FTP配置已存在!");
					mb.open();

					this.tableServer.setSelection(i);

					return;
				}
			}

			fcd = new FtpCfgDef();
			ubs.vecFtpCfg.add(fcd);
			AddNewTableServer(fcd);
		}
		else
		{
			fcd = (FtpCfgDef) ubs.vecFtpCfg.get(tableServer.getSelectionIndex());
		}

		fcd.FtpIP = txtIp.getText().trim();
		fcd.FtpPort = Integer.parseInt(txtPort.getText().trim());
		fcd.FtpUser = txtUser.getText().trim();
		fcd.FtpPwd = txtPassWord.getText().trim();
		fcd.isanonymous = btnUserCfg.getSelection();
		fcd.Ftppasv = btnpasvCfg.getSelection() ? "Y" : "N";

		RefreshTableServer(ubs.vecFtpCfg.indexOf(fcd));

		tableServer.setSelection(ubs.vecFtpCfg.indexOf(fcd));

		saveServer();
	}

	private void send()
	{
		boolean isdirector = false;
		File f = new File(txtSourceFile.getText().trim());

		if (f.isDirectory())
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mb.setMessage("你确定要发布 " + txtSourceFile.getText().trim() + " 目录下的 " + txtModeName.getText().trim() + " 文件吗?");
			if (mb.open() != SWT.YES)
				return;
			else
				while (Display.getCurrent().readAndDispatch());

			isdirector = true;
		}

		boolean issuccess = true;
		boolean issel = false;
		ProgressBox pb = null;
		try
		{
			for (int i = 0; i < tableServer.getItemCount(); i++)
			{
				TableItem ti = tableServer.getItem(i);
				if (ti.getChecked())
				{
					issel = true;

					FtpCfgDef fcd = (FtpCfgDef) ubs.vecFtpCfg.get(i);
					fcd.Status = 2;
					RefreshTableServer(i);

					if (pb == null)
						pb = new ProgressBox();
					pb.setText("正在上传到服务器[" + fcd.FtpIP + "],请等待...");

					ubs.sendProgramFile(fcd, isdirector, this);
					if (issuccess)
					{
						issuccess = (fcd.Status == 0);
					}
					RefreshTableServer(i);

					if (fcd.Status == 0)
					{
						ti.setChecked(false);
					}
				}
			}
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}

		if (!issel)
		{
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setMessage("请选择要发布的更新服务器!");
			mb.open();
		}
		else
		{
			if (!issuccess)
			{
				MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				mb.setMessage("有更新服务器未发送成功,请检查错误信息!");
				mb.open();
			}
			else
			{
				MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				mb.setMessage("更新发送成功!");
				mb.open();
			}
		}
	}

	class ButtonEvent extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			if (e.getSource() == btnSelectFile)
			{
				if (appName!=null && appName.equalsIgnoreCase("JSTORE"))
					openFileDialogForJStore();
				else
					openFileDialogForPos();
			}
			else if (e.getSource() == btnSelectDir)
			{
				openDirDialog();
			}
			else if (e.getSource() == btnSend)
			{
				send();
			}
			else if (e.getSource() == btnClose)
			{
				closepro();
			}
			else if (e.getSource() == btnSaveCfg)
			{
				saveCfg();
			}
			else if (e.getSource() == btnUserCfg)
			{
				changeanos();
			}
			else if (e.getSource() == btnQueryPublish)
			{
				queryPublish();
			}
			else if (e.getSource() == btnDeletePublish)
			{
				deletePublish();
			}
			else if (e.getSource() == btnReadServer)
			{
				readServer();
			}
			else if (e.getSource() == btnNewServer)
			{
				newServer();
			}
			else if (e.getSource() == btnDelServer)
			{
				delServer();
			}
			else if (e.getSource() == btnAllSelServer)
			{
				allSelServer();
			}
		}
	}
}
