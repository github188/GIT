package com.efuture.configure.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Ftp;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class SyjConfig extends WizardPage
{
	private Text text_md;
	private Text text_sn;
	private Text text_syjh;
	private boolean wrFlag = false; //判断读入或是写出PosID.ini文件

	protected SyjConfig()
	{
		super(ConfigWizard.SyjConfig, Language.apply("收银机配置"), ImageDescriptor.createFromFile(SyjConfig.class, "q.gif"));
//		super(ConfigWizard.SyjConfig, "收银机配置", ImageDescriptor.createFromFile(SyjConfig.class, "q.gif"));
		this.setMessage(Language.apply("可以手工配置收银机号和注册码。\n如果配置了更新服务器，可以从服务器上查询本机IP对应的收银机号"));
//		this.setMessage("可以手工配置收银机号和注册码。\n如果配置了更新服务器，可以从服务器上查询本机IP对应的收银机号");
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		setControl(composite);

		final Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

		final FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 25);
		fd_label.right = new FormAttachment(0, 95);
		fd_label.bottom = new FormAttachment(0, 90);
		fd_label.top = new FormAttachment(0, 70);
		label.setLayoutData(fd_label);
		label.setText(Language.apply("注 册 码"));
//		label.setText("注 册 码");

		text_syjh = new Text(composite, SWT.BORDER);
		text_syjh.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

		final FormData fd_text_syjh = new FormData();
		fd_text_syjh.bottom = new FormAttachment(0, 60);
		fd_text_syjh.top = new FormAttachment(0, 40);
		text_syjh.setLayoutData(fd_text_syjh);

		Label label1;
		label1 = new Label(composite, SWT.NONE);
		label1.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

		final FormData fd_label1 = new FormData();
		fd_label1.bottom = new FormAttachment(0, 60);
		fd_label1.top = new FormAttachment(0, 40);
		fd_label1.left = new FormAttachment(0, 25);
		fd_label1.right = new FormAttachment(0, 95);
		label1.setLayoutData(fd_label1);
		label1.setText(Language.apply("收银机号"));
//		label1.setText("收银机号");

		text_sn = new Text(composite, SWT.BORDER);
		text_sn.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

		final FormData fd_text_sn = new FormData();
		fd_text_sn.left = new FormAttachment(label, 5, SWT.RIGHT);
		fd_text_sn.right = new FormAttachment(0, 385);
		fd_text_sn.bottom = new FormAttachment(0, 90);
		fd_text_sn.top = new FormAttachment(0, 70);
		text_sn.setLayoutData(fd_text_sn);

		Label label2;
		label2 = new Label(composite, SWT.NONE);
		final FormData fd_label2 = new FormData();
		fd_label2.bottom = new FormAttachment(0, 30);
		fd_label2.top = new FormAttachment(0, 10);
		fd_label2.right = new FormAttachment(0, 95);
		fd_label2.left = new FormAttachment(0, 25);
		label2.setLayoutData(fd_label2);
		label2.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		label2.setText(Language.apply("门店编号"));
//		label2.setText("门店编号");

		text_md = new Text(composite, SWT.BORDER);
		fd_text_syjh.left = new FormAttachment(text_md, -205, SWT.RIGHT);
		fd_text_syjh.right = new FormAttachment(text_md, 0, SWT.RIGHT);
		final FormData fd_text_md = new FormData();
		fd_text_md.bottom = new FormAttachment(0, 30);
		fd_text_md.top = new FormAttachment(0, 10);
		fd_text_md.left = new FormAttachment(label, 5, SWT.RIGHT);
		fd_text_md.right = new FormAttachment(0, 305);
		text_md.setLayoutData(fd_text_md);
		text_md.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

		Button button;
		button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				String line = GlobalVar.ConfigPath + "/Update.ini";

				if (new File(line).exists())
				{
					Vector v = CommonMethod.readFileByVector(line);

					if (v == null) { return; }

					String ip = null;
					String port = null;
					String user = null;
					String pwd = null;
					String pasv = null;

					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						if (row[0].equals("FtpUpdateIP"))
						{
							ip = row[1];
						}
						else if (row[0].equals("FtpUpdatePort"))
						{
							port = row[1];
						}
						else if (row[0].equals("FtpUpdateUser"))
						{
							user = row[1];
						}
						else if (row[0].equals("FtpUpdatePwd"))
						{
							pwd = row[1];
						}
						else if (row[0].equals("Ftppasv"))
						{
							pasv = row[1];
						}
						else
						{
							continue;
						}
					}

					Ftp f = new Ftp();

					try
					{
						f.connect(ip, Integer.parseInt(port), user, pwd, pasv);

						if (f.exist("autodeploy.ini"))
						{
							f.getFile("autodeploy.ini", "autodeploy.ini");
							
							if (new File("autodeploy.ini").exists())
							{
								// 获取当前IP地址
								String ipaddr = new Http().getIPAddress();
								String strsyjh = text_syjh.getText().trim();
								String strmd = text_md.getText().trim(); 
								
								BufferedReader read = CommonMethod.readFile("autodeploy.ini");
								String line1 = null;

								try
								{
									while ((line1 = read.readLine()) != null)
									{
										String[] rows = line1.split(",");

										// 定义IP，按IP匹配
										if (rows.length > 0 && rows[0].trim().length() > 0)
										{
											if (rows[0].equals(ipaddr)) break;
										}
										else 
										{
											// 定义了门店号，按门店和机号匹配
											if (rows.length > 1 && rows[1].trim().length() > 0)
											{
												if (rows.length > 2 && rows[1].equals(strmd) && rows[2].equals(strsyjh))
												{
													break;
												}
											}
											else
											{
												// 只按机号匹配
												if (rows.length > 2 && rows[2].equals(strsyjh))
												{
													break;
												}
											}
										}
									}
								}
								catch (IOException e1)
								{
									e1.printStackTrace();
								}

								if ((line1 != null) && (line1.split(",").length > 3))
								{
									String[] rows = line1.split(",");
									
									if (rows.length > 1 && rows[1].trim().length() > 0) text_md.setText(rows[1]);
									if (rows.length > 2 && rows[2].trim().length() > 0) text_syjh.setText(rows[2]);
									if (rows.length > 3 && rows[3].trim().length() > 0) text_sn.setText(rows[3]);

									MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_INFORMATION | SWT.OK);
									mess.setMessage(Language.apply("获取收银机信息成功"));
//									mess.setMessage("获取收银机信息成功");
									mess.open();
								}
								else
								{
									MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_ERROR | SWT.OK);
									mess.setMessage(Language.apply("autodeploy.ini 文件里没有找到本机IP对应的收银机信息\n无法自动获取收银机信息"));
//									mess.setMessage("autodeploy.ini 文件里没有找到本机IP对应的收银机信息\n无法自动获取收银机信息");
									mess.open();
								}
							}
							else
							{
								MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_ERROR | SWT.OK);
								mess.setMessage(Language.apply("从FTP服务器下载 autodeploy.ini 文件失败\n无法自动获取收银机信息"));
//								mess.setMessage("从FTP服务器下载 autodeploy.ini 文件失败\n无法自动获取收银机信息");
								mess.open();								
							}
						}
						else
						{
							MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_ERROR | SWT.OK);
							mess.setMessage(Language.apply("FTP 服务器上没有找到 autodeploy.ini 文件\n无法自动获取收银机信息"));
//							mess.setMessage("FTP 服务器上没有找到 autodeploy.ini 文件\n无法自动获取收银机信息");
							mess.open();
						}
					}
					catch (Exception er)
					{
						er.printStackTrace();

						MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_ERROR | SWT.OK);
						mess.setMessage(Language.apply("自动获取收银机信息时发生异常\n")+er.getMessage());
//						mess.setMessage("自动获取收银机信息时发生异常\n"+er.getMessage());
						mess.open();
					}
					finally
					{
						f.close();
					}
				}
			}
		});

		final FormData fd_button = new FormData();
		fd_button.left = new FormAttachment(0, 315);
		fd_button.bottom = new FormAttachment(0, 31);
		fd_button.top = new FormAttachment(0, 9);
		fd_button.right = new FormAttachment(0, 385);
		button.setLayoutData(fd_button);
		button.setText(Language.apply("网络查询"));
//		button.setText("网络查询");

		Label label_1;
		label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_1.setForeground(SWTResourceManager.getColor(255,0,0));
		
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(100, -5);
		fd_label_1.right = new FormAttachment(0, 450);
		fd_label_1.left = new FormAttachment(0, 25);
		fd_label_1.top = new FormAttachment(0, 100);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("    " + Language.apply("门店编号在POS集中部署时(一个数据库实例支持多\r\n门店的情况)必须填入，否则不能填入。\r\n\r\n    如果定义了更新服务器，可以根据当前款机IP地址\r\n从更新服务器自动获取收银机号与注册码\r\n    配置文件为FTP根目录的autodeploy.ini,格式为\r\nIP地址,收银机号,注册码"));
//		label_1.setText("    门店编号在POS集中部署时(一个数据库实例支持多\r\n门店的情况)必须填入，否则不能填入。\r\n\r\n    如果定义了更新服务器，可以根据当前款机IP地址\r\n从更新服务器自动获取收银机号与注册码\r\n    配置文件为FTP根目录的autodeploy.ini,格式为\r\nIP地址,收银机号,注册码");

		final Button button_1 = new Button(composite, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
				BufferedReader br1;
				String line, LocalDBPath = "";
				String[] sp;

				br1 = CommonMethod.readFile(GlobalVar.ConfigFile);
				try
				{
					while ((line = br1.readLine()) != null)
					{
						if ((line == null) || (line.length() <= 0))
						{
							continue;
						}

						String[] lines = line.split("&&");
						sp = lines[0].split("=");
						if (sp.length < 2) continue;

						if (sp[0].trim().compareToIgnoreCase("LocalDBPath") == 0)
						{
							LocalDBPath = sp[1].trim();
						}
					}

					PathFile.delPathFile("C:\\JavaPOS\\javaPos.Logs", "");
					PathFile.delPathFile(LocalDBPath, "Base.zip,Day.zip,Local.zip");
					
					MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mess.setMessage(Language.apply("POS初始化成功！\r\n本地数据清理完毕。"));
//					mess.setMessage("POS初始化成功！\r\n本地数据清理完毕。");
					mess.open();
				}
				catch (IOException e)
				{
					MessageBox mess = new MessageBox(text_syjh.getShell(), SWT.ICON_ERROR | SWT.OK);
					mess.setMessage(Language.apply("POS初始化失败！"));
//					mess.setMessage("POS初始化失败！");
					mess.open();
					e.printStackTrace();
				}
			}
		});
		final FormData fd_button_1 = new FormData();
		fd_button_1.left = new FormAttachment(0, 315);
		fd_button_1.bottom = new FormAttachment(0, 61);
		fd_button_1.top = new FormAttachment(0, 39);
		fd_button_1.right = new FormAttachment(0, 385);
		button_1.setLayoutData(fd_button_1);
		button_1.setText(Language.apply("POS初始化"));
//		button_1.setText("POS初始化");
	}

	//填入原PosID.ini文件的值
	public void setOldValue()
	{
		String line = GlobalVar.ConfigPath + "/PosID.ini";
		Vector v = CommonMethod.readFileByVector(line);

		if (v == null) { return; }

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);

			if ("CashRegisterCode".equals(row[0]))
			{
				text_syjh.setText(row[1]);
			}
			else if ("CDKey".equals(row[0]))
			{
				text_sn.setText(row[1]);
			}
			else if ("Market".equals(row[0]))
			{
				text_md.setText(row[1]);
			}
			else
			{
				continue;
			}
		}
	}

	public boolean canContact()
	{
		String line = GlobalVar.ConfigPath + "/PosID.ini";
		Vector v = CommonMethod.readFileByVector(line);

		if (v == null) {

		return false; }

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);

			if ("CashRegisterCode".equals(row[0]))
			{
				row[1] = text_syjh.getText();
			}
			else if ("CDKey".equals(row[0]))
			{
				row[1] = text_sn.getText();
			}
			else if ("Market".equals(row[0]))
			{
				row[1] = text_md.getText();
			}
			else
			{
				continue;
			}
		}

		CommonMethod.writeFileByVector(line, v);

		return true;
	}

	public IWizardPage getNextPage()
	{
		if (wrFlag == false)
		{
			setOldValue();
		}

		if ((wrFlag == true) && canContact())
		{
			wrFlag = false;

			return super.getNextPage();
		}

		wrFlag = true;

		return this;
	}

	public IWizardPage getPreviousPage()
	{
		wrFlag = false;

		return super.getPreviousPage();
	}
}
