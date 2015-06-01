package com.efuture.configure.wizard;

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
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class UpdateConfig extends WizardPage
{
    private Text text_Pass;
    private Text text_User;
    private Text text_Port;
    private Text text_IP;
    private Label infoLabel;
    private Button chkpasv;
    private boolean wrFlag = false; //判断读入或是写出update.ini文件
    boolean stopflg = false;
    Button b2;

    public UpdateConfig()
    {
        super(ConfigWizard.UpdateConfig, Language.apply("配置更新服务器"), ImageDescriptor.createFromFile(UpdateConfig.class, "q.gif"));
//        super(ConfigWizard.UpdateConfig, "配置更新服务器", ImageDescriptor.createFromFile(UpdateConfig.class, "q.gif"));
        this.setMessage(Language.apply("请配置FTP更新服务器,用于自动更新应用程序与相关配置"));
//        this.setMessage("请配置FTP更新服务器,用于自动更新应用程序与相关配置");
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FormLayout());
        setControl(composite);

        Label ipLabel;

        Label portLabel;
        ipLabel = new Label(composite, SWT.NONE);

        final FormData fd_ipLabel = new FormData();
        ipLabel.setLayoutData(fd_ipLabel);
        ipLabel.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        ipLabel.setText(Language.apply("IP地址"));
//        ipLabel.setText("IP地址");

        text_IP           = new Text(composite, SWT.BORDER);
        fd_ipLabel.top    = new FormAttachment(text_IP, -20, SWT.BOTTOM);
        fd_ipLabel.bottom = new FormAttachment(text_IP, 0, SWT.BOTTOM);
        fd_ipLabel.left   = new FormAttachment(text_IP, -63, SWT.LEFT);
        fd_ipLabel.right  = new FormAttachment(text_IP, -5, SWT.LEFT);

        final FormData fd_text_IP = new FormData();
        fd_text_IP.right  = new FormAttachment(0, 325);
        fd_text_IP.left   = new FormAttachment(0, 85);
        fd_text_IP.top    = new FormAttachment(0, 10);
        fd_text_IP.bottom = new FormAttachment(0, 30);
        text_IP.setLayoutData(fd_text_IP);
        text_IP.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        text_IP.setText("127.0.0.1");
        
        portLabel = new Label(composite, SWT.NONE);

        final FormData fd_portLabel = new FormData();
        fd_portLabel.right = new FormAttachment(ipLabel, 62, SWT.LEFT);
        fd_portLabel.left  = new FormAttachment(ipLabel, 0, SWT.LEFT);
        portLabel.setLayoutData(fd_portLabel);
        portLabel.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        portLabel.setText(Language.apply("端口号"));
//        portLabel.setText("端口号");

        text_Port           = new Text(composite, SWT.BORDER);
        fd_portLabel.top    = new FormAttachment(text_Port, 0, SWT.TOP);
        fd_portLabel.bottom = new FormAttachment(text_Port, 0, SWT.BOTTOM);

        final FormData fd_text_Port = new FormData();
        fd_text_Port.right  = new FormAttachment(0, 210);
        fd_text_Port.left   = new FormAttachment(portLabel, 0, SWT.RIGHT);
        fd_text_Port.top    = new FormAttachment(0, 50);
        fd_text_Port.bottom = new FormAttachment(0, 70);
        text_Port.setLayoutData(fd_text_Port);
        text_Port.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        text_Port.setText("21");

        Label label_2;
        label_2 = new Label(composite, SWT.NONE);

        final FormData fd_label_2 = new FormData();
        fd_label_2.right = new FormAttachment(portLabel, 48, SWT.LEFT);
        fd_label_2.left  = new FormAttachment(portLabel, 0, SWT.LEFT);
        label_2.setLayoutData(fd_label_2);
        label_2.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        label_2.setText(Language.apply("用户名"));
//        label_2.setText("用户名");

        text_User         = new Text(composite, SWT.BORDER);
        fd_label_2.top    = new FormAttachment(text_User, -20, SWT.BOTTOM);
        fd_label_2.bottom = new FormAttachment(text_User, 0, SWT.BOTTOM);
        text_User.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

        final FormData fd_text_User = new FormData();
        fd_text_User.right  = new FormAttachment(text_Port, 0, SWT.RIGHT);
        fd_text_User.left   = new FormAttachment(ipLabel, 5, SWT.RIGHT);
        fd_text_User.top    = new FormAttachment(0, 85);
        fd_text_User.bottom = new FormAttachment(0, 105);
        text_User.setLayoutData(fd_text_User);

        text_Pass = new Text(composite, SWT.BORDER);
        text_Pass.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

        final FormData fd_text_Pass = new FormData();
        fd_text_Pass.right = new FormAttachment(text_User, 0, SWT.RIGHT);
        fd_text_Pass.left  = new FormAttachment(text_User, 0, SWT.LEFT);
        text_Pass.setLayoutData(fd_text_Pass);

        Label label_3;
        label_3             = new Label(composite, SWT.NONE);
        fd_text_Pass.bottom = new FormAttachment(label_3, 20, SWT.TOP);
        fd_text_Pass.top    = new FormAttachment(label_3, 0, SWT.TOP);

        final FormData fd_label_3 = new FormData();
        fd_label_3.top      = new FormAttachment(0, 120);
        fd_label_3.bottom   = new FormAttachment(0, 140);
        fd_label_3.right    = new FormAttachment(label_2, 48, SWT.LEFT);
        fd_label_3.left     = new FormAttachment(label_2, 0, SWT.LEFT);
        label_3.setLayoutData(fd_label_3);
        label_3.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        label_3.setText(Language.apply("密  码"));
//        label_3.setText("密  码");

        Button button;
        button = new Button(composite, SWT.NONE);

        final FormData fd_button = new FormData();
        fd_button.right = new FormAttachment(text_Pass, 70, SWT.LEFT);
        fd_button.left = new FormAttachment(text_Pass, 0, SWT.LEFT);
        button.setLayoutData(fd_button);
        button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    String mess = ConfigWizard.ipCheck(text_IP.getText().trim(), text_Port.getText().trim());

                    if (mess != null)
                    {
                        MessageBox messBox = new MessageBox(text_IP.getShell(), SWT.ICON_INFORMATION | SWT.OK);
                        messBox.setMessage(mess);
                        messBox.open();

                        return;
                    }

                    Ftp f = new Ftp();

                    try
                    {
                        boolean islogin = f.connect(text_IP.getText(), Integer.parseInt(text_Port.getText()), text_User.getText(), text_Pass.getText());

                        if (islogin)
                        {
                            stopflg = true;

                            MessageBox me = new MessageBox(text_Pass.getShell(), SWT.ICON_INFORMATION | SWT.OK);
//                            me.setMessage("连接FTP服务器成功");
                            me.setMessage(Language.apply("连接FTP服务器成功"));
                            me.open();
                        }
                        else
                        {
                            stopflg = true;

                            MessageBox me = new MessageBox(text_Pass.getShell(), SWT.ICON_ERROR | SWT.OK);
                            me.setMessage(Language.apply("连接FTP服务器失败"));
//                            me.setMessage("连接FTP服务器失败");
                            me.open();
                        }
                    }
                    catch (Exception er)
                    {
                        //er.printStackTrace();
                        stopflg = true;

                        MessageBox me = new MessageBox(text_Pass.getShell(), SWT.ICON_ERROR | SWT.OK);
//                        me.setMessage("连接FTP服务器失败");
                        me.setMessage(Language.apply("连接FTP服务器失败"));
                        me.open();
                    }
                    finally
                    {
                        f.close();
                    }
                }
            });
        button.setText(Language.apply("测试连接"));
//        button.setText("测试连接");

        infoLabel = new Label(composite, SWT.NONE);

        final FormData fd_infoLabel = new FormData();
        fd_infoLabel.top    = new FormAttachment(100, -25);
        fd_infoLabel.bottom = new FormAttachment(100, -5);
        fd_infoLabel.right  = new FormAttachment(label_3, 303, SWT.LEFT);
        fd_infoLabel.left   = new FormAttachment(label_3, 0, SWT.LEFT);
        infoLabel.setLayoutData(fd_infoLabel);

        Button btnFtpPress;
        btnFtpPress = new Button(composite, SWT.NONE);
        fd_button.bottom = new FormAttachment(btnFtpPress, 21, SWT.TOP);
        fd_button.top = new FormAttachment(btnFtpPress, 0, SWT.TOP);
        final FormData fd_btnFtpPress = new FormData();
        fd_btnFtpPress.bottom = new FormAttachment(0, 190);
        fd_btnFtpPress.top = new FormAttachment(0, 169);
        fd_btnFtpPress.right = new FormAttachment(0, 314);
        fd_btnFtpPress.left = new FormAttachment(0, 219);
        btnFtpPress.setLayoutData(fd_btnFtpPress);
        btnFtpPress.setText(Language.apply("FTP压力测试"));
//        btnFtpPress.setText("FTP压力测试");
        
        btnFtpPress.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(final SelectionEvent e)
            {
            	FtpSrvPressureForm ftpForm = new FtpSrvPressureForm();
            	ftpForm.open();
            }
         });

        chkpasv = new Button(composite, SWT.CHECK);
        final FormData fd_chkpasv = new FormData();
        fd_chkpasv.top = new FormAttachment(text_Port, 0, SWT.TOP);
        fd_chkpasv.left = new FormAttachment(text_Port, 5, SWT.RIGHT);
        chkpasv.setLayoutData(fd_chkpasv);
        chkpasv.setText(Language.apply("被动模式"));
//        chkpasv.setText("被动模式");
    }

    //填入原文件的IP,PORT
    public void setOldValue()
    {
        String line = GlobalVar.ConfigPath + "/Update.ini";
        Vector v = CommonMethod.readFileByVector(line);

        if (v == null)
        {
            return;
        }

        for (int i = 0; i < v.size(); i++)
        {
            String[] row = (String[]) v.elementAt(i);

            if ("FtpUpdateIP".equals(row[0]))
            {
                text_IP.setText(row[1]);
            }
            else if ("FtpUpdatePort".equals(row[0]))
            {
                text_Port.setText(row[1]);
            }
            else if ("FtpUpdateUser".equals(row[0]))
            {
                text_User.setText(row[1]);
            }
            else if ("FtpUpdatePwd".equals(row[0]))
            {
                text_Pass.setText(row[1]);
            }
            else if ("Ftppasv".equals(row[0]))
            {
            	this.chkpasv.setSelection(row[1].equals("Y"));
            }
            else
            {
                continue;
            }
        }
    }

    //	判断是否能设定IP地址
    public boolean canContact()
    {
        String line = GlobalVar.ConfigPath + "/Update.ini";
        Vector v = CommonMethod.readFileByVector(line);

        infoLabel.setText("");

        if (v == null)
        {
            infoLabel.setText(Language.apply("找不到文件:") + line);
//            infoLabel.setText("找不到文件:" + line);

            return false;
        }

        for (int i = 0; i < v.size(); i++)
        {
            String[] row = (String[]) v.elementAt(i);

            if ("FtpUpdateIP".equals(row[0]))
            {
                row[1] = text_IP.getText();
            }
            else if ("FtpUpdatePort".equals(row[0]))
            {
                row[1] = text_Port.getText();
            }
            else if ("FtpUpdateUser".equals(row[0]))
            {
                row[1] = text_User.getText();
            }
            else if ("FtpUpdatePwd".equals(row[0]))
            {
                row[1] = text_Pass.getText();
            }
            else if ("Ftppasv".equals(row[0]))
            {
            	row[1] = this.chkpasv.getSelection()?"Y":"N";
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
}
