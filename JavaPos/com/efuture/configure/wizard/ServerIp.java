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
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class ServerIp extends WizardPage
{
    private Text Text_posServer;
    private Text text_port;
    private Text Ipadd;
    private Label infoLabel;
    private boolean wrFlag = false; //判断读入或是写出Config.ini文件

    public ServerIp()
    {
        //super(ConfigWizard.ServerIp, "POSSERVER配置", ImageDescriptor.createFromFile(ServerIp.class, "q.gif"));
        super(ConfigWizard.ServerIp, Language.apply("POSSERVER配置"), ImageDescriptor.createFromFile(ServerIp.class, "q.gif"));
        //this.setMessage("请配置POSSERVER服务器");
        this.setMessage(Language.apply("请配置POSSERVER服务器"));
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FormLayout());
        setControl(composite);

        Label label_3;

        final Label label_2 = new Label(composite, SWT.NONE);
        final FormData fd_label_2 = new FormData();
        fd_label_2.right  = new FormAttachment(0, 160);
        fd_label_2.top    = new FormAttachment(0, 10);
        fd_label_2.bottom = new FormAttachment(0, 30);
        fd_label_2.left   = new FormAttachment(0, 25);
        label_2.setLayoutData(fd_label_2);
        label_2.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        //label_2.setText("POSSERVER服务地址");
        label_2.setText(Language.apply("POSSERVER服务地址"));

        Ipadd = new Text(composite, SWT.BORDER);
        Ipadd.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

        final FormData fd_ipadd = new FormData();
        fd_ipadd.left   = new FormAttachment(0, 170);
        fd_ipadd.top    = new FormAttachment(label_2, -20, SWT.BOTTOM);
        fd_ipadd.bottom = new FormAttachment(label_2, 0, SWT.BOTTOM);
        fd_ipadd.right  = new FormAttachment(label_2, 256, SWT.RIGHT);
        Ipadd.setLayoutData(fd_ipadd);
        Ipadd.setText("127.0.0.1");

        final Label portLabel = new Label(composite, SWT.NONE);
        final FormData fd_portLabel = new FormData();
        fd_portLabel.top    = new FormAttachment(0, 50);
        fd_portLabel.bottom = new FormAttachment(0, 75);
        fd_portLabel.left   = new FormAttachment(label_2, 0, SWT.LEFT);
        fd_portLabel.right  = new FormAttachment(label_2, 0, SWT.RIGHT);
        portLabel.setLayoutData(fd_portLabel);
        portLabel.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        portLabel.setText(Language.apply("POSSERVER服务端口"));
//        portLabel.setText("POSSERVER服务端口");

        text_port = new Text(composite, SWT.BORDER);
        text_port.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

        final FormData fd_text_port = new FormData();
        fd_text_port.left   = new FormAttachment(Ipadd, 0, SWT.LEFT);
        fd_text_port.bottom = new FormAttachment(portLabel, 22, SWT.TOP);
        fd_text_port.top    = new FormAttachment(portLabel, 0, SWT.TOP);
        fd_text_port.right  = new FormAttachment(portLabel, 126, SWT.RIGHT);
        text_port.setLayoutData(fd_text_port);
        text_port.setText("8080");

        final Button button_2 = new Button(composite, SWT.NONE);
        final FormData fd_button_2 = new FormData();
        fd_button_2.top    = new FormAttachment(0, 138);
        fd_button_2.bottom = new FormAttachment(0, 160);
        button_2.setLayoutData(fd_button_2);
        button_2.setText(Language.apply("测试连接"));
//        button_2.setText("测试连接");

        button_2.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    String mess = ConfigWizard.ipCheck(Ipadd.getText().trim(), text_port.getText().trim());

                    if (mess != null)
                    {
                        MessageBox messBox = new MessageBox(Ipadd.getShell(), SWT.ICON_INFORMATION | SWT.OK);
                        messBox.setMessage(mess);
                        messBox.open();

                        return;
                    }

                    Http h = new Http();

                    try
                    {
                        h = new Http(Ipadd.getText(), Integer.parseInt(text_port.getText()), Text_posServer.getText());
                        h.init();
                        h.setConncetTimeout(10000); //连接超时
                        h.setReadTimeout(30000); //处理超时

                        TimeDate time = new TimeDate();
                        boolean chkPosServer = new NetService().getServerTime(h, time);

                        if (chkPosServer == true)
                        {
                            MessageBox me = new MessageBox(Text_posServer.getShell(), SWT.ICON_INFORMATION | SWT.OK);
                            me.setMessage(Language.apply("连接POSSERVER服务器成功"));
//                            me.setMessage("连接POSSERVER服务器成功");
                            me.open();
                        }
                        else
                        {
                            MessageBox me = new MessageBox(Text_posServer.getShell(), SWT.ICON_ERROR | SWT.OK);
                            me.setMessage(Language.apply("连接POSSERVER服务器失败"));
//                            me.setMessage("连接POSSERVER服务器失败");
                            me.open();
                        }
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();

                        MessageBox me = new MessageBox(Text_posServer.getShell(), SWT.ICON_ERROR | SWT.OK);
                        me.setMessage(Language.apply("连接POSSERVER服务器失败"));
//                        me.setMessage("连接POSSERVER服务器失败");
                        me.open();
                    }
                    finally
                    {
                        h.disconncet();
                    }
                }
            });
       
        
        final Button button_3 = new Button(composite, SWT.NONE);
        final FormData fd_button_3 = new FormData();
        fd_button_3.right = new FormAttachment(0, 421);
        fd_button_3.bottom = new FormAttachment(button_2, 22, SWT.TOP);
        fd_button_3.top = new FormAttachment(button_2, 0, SWT.TOP);
        fd_button_3.left = new FormAttachment(text_port, 0, SWT.RIGHT);
        button_3.setLayoutData(fd_button_3);
        button_3.setText(Language.apply("PosServer压力测试"));
//        button_3.setText("PosServer压力测试");
        
        button_3.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(final SelectionEvent e)
            {
            	PosSrvPressureFrom srvPressure = new PosSrvPressureFrom();
            	srvPressure.open();
            }
        });
        
        label_3 = new Label(composite, SWT.NONE);

        final FormData fd_label_3 = new FormData();
        fd_label_3.top    = new FormAttachment(0, 89);
        fd_label_3.bottom = new FormAttachment(0, 115);
        fd_label_3.left   = new FormAttachment(portLabel, 0, SWT.LEFT);
        label_3.setLayoutData(fd_label_3);
        label_3.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
        label_3.setText(Language.apply("POSSERVER服务路径"));
//        label_3.setText("POSSERVER服务路径");

        Text_posServer    = new Text(composite, SWT.BORDER);
        fd_label_3.right  = new FormAttachment(Text_posServer, -5, SWT.LEFT);
        fd_button_2.right = new FormAttachment(Text_posServer, 70, SWT.LEFT);
        fd_button_2.left  = new FormAttachment(Text_posServer, 0, SWT.LEFT);
        Text_posServer.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));

        final FormData fd_text_posServer = new FormData();
        fd_text_posServer.right  = new FormAttachment(Ipadd, 0, SWT.RIGHT);
        fd_text_posServer.left   = new FormAttachment(0, 169);
        fd_text_posServer.bottom = new FormAttachment(label_3, 20, SWT.TOP);
        fd_text_posServer.top    = new FormAttachment(label_3, 0, SWT.TOP);
        Text_posServer.setLayoutData(fd_text_posServer);
        Text_posServer.setText("/PosServerPos/PosServer");

        infoLabel = new Label(composite, SWT.NONE);

        final FormData fd_infoLabel = new FormData();
        fd_infoLabel.bottom = new FormAttachment(100, -5);
        fd_infoLabel.right  = new FormAttachment(0, 330);
        fd_infoLabel.top    = new FormAttachment(0, 170);
        fd_infoLabel.left   = new FormAttachment(label_3, 0, SWT.LEFT);
        infoLabel.setLayoutData(fd_infoLabel);
    }

    //	填入原Config.ini文件的值
    public void setOldValue()
    {
        String line = GlobalVar.ConfigPath + "/Config.ini";
        Vector v = CommonMethod.readFileByVector(line);

        if (v == null)
        {
            return;
        }

        for (int i = 0; i < v.size(); i++)
        {
            String[] row = (String[]) v.elementAt(i);

            if ("ServerIP".equals(row[0]))
            {
                Ipadd.setText(row[1]);
            }
            else if ("Serverport".equals(row[0]))
            {
                text_port.setText(row[1]);
            }
            else if ("ServerPath".equals(row[0]))
            {
                Text_posServer.setText(row[1]);
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
        String line = GlobalVar.ConfigPath + "/Config.ini";
        Vector v = CommonMethod.readFileByVector(line);

        infoLabel.setText("");

        if (v == null)
        {
            infoLabel.setText(Language.apply("找不到文件:" )+ line);
//            infoLabel.setText("找不到文件:" + line);

            return false;
        }

        for (int i = 0; i < v.size(); i++)
        {
            String[] row = (String[]) v.elementAt(i);

            if ("ServerIP".equals(row[0]))
            {
                row[1] = Ipadd.getText();
            }
            else if ("Serverport".equals(row[0]))
            {
                row[1] = text_port.getText();
            }
            else if ("ServerPath".equals(row[0]))
            {
                row[1] = Text_posServer.getText();
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
