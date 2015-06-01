package com.efuture.configure.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.efuture.configure.SetPrintForm;
import com.efuture.configure.deviceTest;
import com.efuture.configure.setConfigureForm;
import com.efuture.defineKey.KeyConfigForm;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class OtherConfig extends WizardPage
{
    public OtherConfig()
    {
        super(ConfigWizard.OtherConfig, Language.apply("其它设置"), ImageDescriptor.createFromFile(OtherConfig.class, "q.gif"));
//        super(ConfigWizard.OtherConfig, "其它设置", ImageDescriptor.createFromFile(OtherConfig.class, "q.gif"));
        this.setMessage(Language.apply("感谢您使用本配置程序!"));
//        this.setMessage("感谢您使用本配置程序！");
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FormLayout());
        setControl(composite);

        Button btnKey;

        Label label;
        label = new Label(composite, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

        final FormData fd_label = new FormData();
        fd_label.right  = new FormAttachment(0, 395);
        fd_label.top    = new FormAttachment(0, 10);
        label.setLayoutData(fd_label);
        label.setText(Language.apply("其它设置") + ":");
//        label.setText("其它设置:");

        Button btnDevice;
        btnDevice        = new Button(composite, SWT.NONE);
        fd_label.bottom = new FormAttachment(btnDevice, -5, SWT.TOP);
        fd_label.left = new FormAttachment(btnDevice, 0, SWT.LEFT);

        final FormData fd_btnDevice = new FormData();
        fd_btnDevice.left = new FormAttachment(0, 25);
        fd_btnDevice.right = new FormAttachment(0, 120);
        fd_btnDevice.bottom = new FormAttachment(0, 58);
        fd_btnDevice.top = new FormAttachment(0, 35);
        btnDevice.setLayoutData(fd_btnDevice);

        btnKey = new Button(composite, SWT.NONE);

        final FormData fd_btnKey = new FormData();
        fd_btnKey.top = new FormAttachment(0, 73);
        fd_btnKey.bottom = new FormAttachment(0, 95);
        fd_btnKey.left = new FormAttachment(0, 25);
        fd_btnKey.right = new FormAttachment(0, 120);
        btnKey.setLayoutData(fd_btnKey);
        btnKey.setText(Language.apply("定义键盘布局"));
//        btnKey.setText("定义键盘布局");

        btnDevice.setText(Language.apply("配置硬件设备"));
//        btnDevice.setText("配置硬件设备");

        Button btnConfig;
        btnConfig = new Button(composite, SWT.NONE);

        final FormData fd_btnConfig = new FormData();
        fd_btnConfig.top = new FormAttachment(0, 143);
        fd_btnConfig.bottom = new FormAttachment(0, 165);
        fd_btnConfig.left = new FormAttachment(btnKey, -95, SWT.RIGHT);
        fd_btnConfig.right = new FormAttachment(btnKey, 0, SWT.RIGHT);
        btnConfig.setLayoutData(fd_btnConfig);
        btnConfig.setText(Language.apply("其他高级设置"));
//        btnConfig.setText("其他高级设置");

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

        final FormData fd_label_1 = new FormData();
        fd_label_1.top = new FormAttachment(0, 38);
        fd_label_1.bottom = new FormAttachment(0, 60);
        fd_label_1.right = new FormAttachment(0, 470);
        fd_label_1.left = new FormAttachment(0, 140);
        label_1.setLayoutData(fd_label_1);
        label_1.setText(Language.apply("设置键盘、打印机、刷卡槽、钱箱、显示牌等设备"));
//        label_1.setText("设置键盘、打印机、刷卡槽、钱箱、显示牌等设备");

        Label label_2;
        label_2 = new Label(composite, SWT.NONE);
        label_2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

        final FormData fd_label_2 = new FormData();
        fd_label_2.right = new FormAttachment(label_1, 0, SWT.RIGHT);
        fd_label_2.top = new FormAttachment(0, 73);
        fd_label_2.bottom = new FormAttachment(0, 95);
        fd_label_2.left = new FormAttachment(0, 140);
        label_2.setLayoutData(fd_label_2);
        label_2.setText(Language.apply("键盘键位自定义布局设置"));
//        label_2.setText("键盘键位自定义布局设置");

        final Label label_3 = new Label(composite, SWT.NONE);
        label_3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

        final FormData fd_label_3 = new FormData();
        fd_label_3.left = new FormAttachment(0, 140);
        fd_label_3.bottom = new FormAttachment(btnConfig, 22, SWT.TOP);
        fd_label_3.top = new FormAttachment(btnConfig, 0, SWT.TOP);
        label_3.setLayoutData(fd_label_3);
        label_3.setText(Language.apply("配置其他运行参数"));
//        label_3.setText("配置其他运行参数");


        Label label_2_1;
        label_2_1 = new Label(composite, SWT.NONE);
        fd_label_3.right = new FormAttachment(label_2_1, 0, SWT.RIGHT);
        final FormData fd_label_2_1 = new FormData();
        fd_label_2_1.right = new FormAttachment(label_2, 0, SWT.RIGHT);
        fd_label_2_1.bottom = new FormAttachment(0, 132);
        fd_label_2_1.top = new FormAttachment(0, 110);
        fd_label_2_1.left = new FormAttachment(label_2, 0, SWT.LEFT);
        label_2_1.setLayoutData(fd_label_2_1);
        label_2_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
        label_2_1.setText(Language.apply("配置并预览各小票打印等模板"));
//        label_2_1.setText("配置并预览各小票打印等模板");

        final Button btnPrint = new Button(composite, SWT.NONE);
        final FormData fd_btnPrint = new FormData();
        fd_btnPrint.bottom = new FormAttachment(0, 132);
        fd_btnPrint.top = new FormAttachment(0, 110);
        fd_btnPrint.right = new FormAttachment(btnKey, 95, SWT.LEFT);
        fd_btnPrint.left = new FormAttachment(btnKey, 0, SWT.LEFT);
        btnPrint.setLayoutData(fd_btnPrint);
        btnPrint.setText(Language.apply("配置打印模板"));
//        btnPrint.setText("配置打印模板");
        
        /*
         * 事件定义相关函数 button、button_1、button_2
         */
        btnDevice.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    try
                    {
                    	String JarName = ".//javaPos.ExtendJar//device.jar";
                        String path = ".//javaPos.ConfigFile";
                        
                        if (WizardStart.mainargs != null && WizardStart.mainargs.length > 0)
                        {
                        	path = WizardStart.mainargs[0];
                        	JarName = path + "..//javaPos.ExtendJar//device.jar";
                        	if (WizardStart.mainargs.length > 1)
                        	{
                            	JarName = WizardStart.mainargs[1];
                        	}
                        }
                        
                        deviceTest window = new deviceTest();
                        window.open(JarName, path);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

        btnKey.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    try
                    {
                        new KeyConfigForm(Display.getDefault(), SWT.NONE | SWT.APPLICATION_MODAL, false);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

        btnPrint.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(final SelectionEvent e)
            {
                try
                {
                	SetPrintForm setPrintForm = new SetPrintForm(); 
                	setPrintForm.open();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        
        btnConfig.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(final SelectionEvent e)
            {
                try
                {
                    setConfigureForm window = new setConfigureForm();
                    window.open();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        });
    }
}
