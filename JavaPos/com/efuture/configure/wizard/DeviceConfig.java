package com.efuture.configure.wizard;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.Ftp;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class DeviceConfig extends WizardPage
{
    private Label infoLabel;
    private List lisPara;
    private Combo cmbStyle;
    private boolean wrFlag = false;
    
    private String strconfigpath = GlobalVar.ConfigPath + "/Config.ini";
    private String strdevicenamepath = GlobalVar.ConfigPath + "/DeviceName.ini";
    private String strposmodelpath = GlobalVar.ConfigPath + "/PosModel.ini";
    
    private Vector vconfig;
    private Vector vdevicename;
    private Vector vposmodel;
    private Vector vposmodelSelect = new Vector();
    
    public DeviceConfig()
    {
//        super(ConfigWizard.DeviceConfig, "款机型号设置", ImageDescriptor.createFromFile(DeviceConfig.class, "q.gif"));
        super(ConfigWizard.DeviceConfig, Language.apply("款机型号设置"), ImageDescriptor.createFromFile(DeviceConfig.class, "q.gif"));
//        this.setMessage("您可以手工选择款机型号！");
        this.setMessage(Language.apply("您可以手工选择款机型号！"));
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FormLayout());
        setControl(composite);


        Label label;
        label = new Label(composite, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

        final FormData fd_label = new FormData();
        fd_label.left = new FormAttachment(0, 5);
        fd_label.right = new FormAttachment(0, 125);
        fd_label.bottom = new FormAttachment(0, 25);
        fd_label.top = new FormAttachment(0, 5);
        label.setLayoutData(fd_label);
        //label.setText("请选择款机型号:");
        label.setText(Language.apply("请选择款机型号:"));

        final ComboViewer cmbType = new ComboViewer(composite, SWT.READ_ONLY);
        cmbType.addSelectionChangedListener(new cmbSelectChange());
        cmbStyle = cmbType.getCombo();
        cmbStyle.setVisibleItemCount(15);
        cmbStyle.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
        final FormData fd_combo = new FormData();
        fd_combo.right = new FormAttachment(100, -72);
        fd_combo.top = new FormAttachment(0, 5);
        fd_combo.left = new FormAttachment(label, 0, SWT.RIGHT);
        cmbStyle.setLayoutData(fd_combo);

        final ListViewer lvPara = new ListViewer(composite, SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
        lisPara = lvPara.getList();
        lisPara.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
        final FormData fd_list = new FormData();
        fd_list.bottom = new FormAttachment(0, 135);
        fd_list.right = new FormAttachment(100, -5);
        fd_list.top = new FormAttachment(0, 35);
        fd_list.left = new FormAttachment(label, 0, SWT.LEFT);
        lisPara.setLayoutData(fd_list);

        infoLabel = new Label(composite, SWT.NONE);
        infoLabel.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
        final FormData fd_infoLabel = new FormData();
        fd_infoLabel.top = new FormAttachment(0, 145);
        fd_infoLabel.right = new FormAttachment(lisPara, 0, SWT.RIGHT);
        fd_infoLabel.bottom = new FormAttachment(100, -5);
        fd_infoLabel.left = new FormAttachment(lisPara, 0, SWT.LEFT);
        infoLabel.setLayoutData(fd_infoLabel);

        Button button;
        button = new Button(composite, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e)
                {
                    String line = GlobalVar.ConfigPath + "/Update.ini";

                    if (new File(line).exists())
                    {
                        Vector v = CommonMethod.readFileByVector(line);

                        if (v == null)
                        {
                            return;
                        }

                        String ip = null;
                        String port = null;
                        String user = null;
                        String pwd = null;

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
                            else
                            {
                                continue;
                            }
                        }

                        Ftp f = new Ftp();

                        try
                        {
                            if (!f.connect(ip, Integer.parseInt(port), user, pwd))
                            {
                                MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                                //mess.setMessage("连接FTP 服务器失败\n无法更新配置模版");
                                mess.setMessage(Language.apply("连接FTP 服务器失败\n无法更新配置模版"));
                                mess.open();
                                
                                return;
                            }

                            /*
                            if (!f.exist("PosModel.ini"))
                            {
                                MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                                mess.setMessage("FTP 服务器上没有找到 PosModel.ini 文件\n无法更新配置模版");
                                mess.open();
                                
                                return;
                            }
                            */
                            
                            if(!f.getFile("PosModel.ini", GlobalVar.ConfigPath + "/PosModel.ini"))
                            {
                                MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                                //mess.setMessage("从FTP 服务器上下载 PosModel.ini 文件失败\n无法更新配置模版");
                                mess.setMessage(Language.apply("从FTP 服务器上下载 PosModel.ini 文件失败\n无法更新配置模版"));
                                mess.open();
                                
                                return;
                            }
                            
                            MessageBox mess = new MessageBox(getShell(), SWT.ICON_WORKING | SWT.OK);
                            //mess.setMessage("配置模版更新成功!");
                            mess.setMessage(Language.apply("配置模版更新成功!"));
                            mess.open();
                            
                            setOldValue();
                        }
                        catch (Exception er)
                        {
                            er.printStackTrace();

                            MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                            //mess.setMessage("FTP 服务器上没有找到 PosModel.ini 文件\n无法更新配置模版");
                            mess.setMessage(Language.apply("FTP 服务器上没有找到 PosModel.ini 文件\n无法更新配置模版"));
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
        fd_button.left = new FormAttachment(100, -67);
        fd_button.right = new FormAttachment(100, -7);
        fd_button.top = new FormAttachment(cmbStyle, -22, SWT.BOTTOM);
        fd_button.bottom = new FormAttachment(cmbStyle, 0, SWT.BOTTOM);
        button.setLayoutData(fd_button);
        //button.setText("网络更新");
        button.setText(Language.apply("网络更新"));
        
        /*
         * 事件定义相关函数 button、button_1、button_2
         */
    }
    
    public IWizardPage getNextPage()
    {
        if (wrFlag == false)
        {
        	setOldValue();
        }

        if ((wrFlag == true) && canContact())
        {
            //wrFlag = false;

            return super.getNextPage();
        }

        wrFlag = true;

        return this;
    }

    public boolean canContact()
    {
    	if (vposmodelSelect.size() < 0)
    	{
    		return true;
    	}
    	
        for (int i = 0; i < this.vposmodelSelect.size(); i++)
        {
            String[] row = (String[]) vposmodelSelect.elementAt(i);

            for (int j = 0;j < vconfig.size(); j++)
            {
            	String[] row1 = (String[]) vconfig.elementAt(j);
            	
            	if (row1[0] == null) continue;
            		
            	if ((row[0].trim()+"1").equalsIgnoreCase(row1[0].trim()))
            	{
            		int inx = row[1].indexOf(",");
            		if (inx <= 0)
            		{
            			row1[1] = row[1]==null?"":row[1];
            		}
            		else
            		{
            			row1[1] = row[1].substring(0,inx);
            		}
            	}
            }
            
            for (int k = 0;k < vdevicename.size(); k++)
            {
            	String[] row2 = (String[]) vdevicename.elementAt(k);
            	
            	if (row2[0] == null) continue;
            	
            	if (row[0].trim().equalsIgnoreCase(row2[0].trim()))
            	{
              		int inx = row[1].indexOf(",");
            		if (inx < 0 || inx == row[1].length() - 1)
            		{
            			row2[1] = "";
            		}
            		else
            		{
            			row2[1] = row[1].substring(inx+1,row[1].length());
            		}
            	}
            }
        }
        
        CommonMethod.writeFileByVector(strconfigpath, vconfig);
        CommonMethod.writeFileByVector(strdevicenamepath, vdevicename);
        
        return true;
    }
    
    public IWizardPage getPreviousPage()
    {
        //wrFlag = false;

        return super.getPreviousPage();
    }
    
    public void setOldValue()
    {
    	//infoLabel.setText("点击网络更新从更新服务器的FTP上下载最新配置模版PosModel.ini");
    	infoLabel.setText(Language.apply("点击网络更新从更新服务器的FTP上下载最新配置模版PosModel.ini"));
    	
    	vposmodel = CommonMethod.readFileByVector(strposmodelpath);
        if (vposmodel == null)
        {
        	//infoLabel.setText("找不到文件:" + strposmodelpath);
        	infoLabel.setText(Language.apply("找不到文件:") + strposmodelpath);
            return;
        }

        vconfig = CommonMethod.readFileByVector(strconfigpath);
        if (vconfig == null)
        {
        	infoLabel.setText(Language.apply("找不到文件:") + strconfigpath);
            return;
        }
        
        vdevicename = CommonMethod.readFileByVector(strdevicenamepath);
        if (vdevicename == null)
        {
        	infoLabel.setText(Language.apply("找不到文件:") + strdevicenamepath);
            return;
        }
        
        cmbStyle.removeAll();
        //cmbStyle.add("当前设置");
        cmbStyle.add(Language.apply("当前设置"));
        cmbStyle.select(0);
        for (int i = 0; i < vposmodel.size(); i++)
        {
            String[] row = (String[]) vposmodel.elementAt(i);
            
            if (row[0] != null && row[0].length() > 2 && row[1] == null)
            {
            	if (row[0].charAt(0) == '[' && row[0].charAt(row[0].length() - 1) == ']')
            	{
            		cmbStyle.add(row[0].substring(1, row[0].length() - 1));
            	}
            }
        }
        
        loadval();
    }
    
    private void loadval()
    {
    	vposmodelSelect.clear();
    	for (int i = 0; i < vconfig.size();i++)
		{
			String[] row = (String[]) vconfig.elementAt(i);
			if (row[0] == null || row[0].length() <= 2) continue;
			
			if (!(row[0].charAt(0) == '[' && row[0].charAt(row[0].length()-1) == ']')) continue;
			
			String strKey = row[0].substring(1,row[0].length()-1);
			
			if (!strKey.equalsIgnoreCase("Device")) continue;

			for (int j = ++i;j<vconfig.size();j++)
			{
				String[] row1 = (String[]) vconfig.elementAt(j);
				if (row1[0] == null) break;
				if (row1[0].length() >=2 && row1[0].charAt(0) == '[' && row1[0].charAt(row1[0].length()-1) == ']') break;
				
				String strrow0 = row1[0].charAt(row1[0].length()-1) == '1'?row1[0].substring(0,row1[0].length()-1):row1[0];
				String strrow1 = row1[1] == null || row1[1].length() <= 0?"":(row1[1].charAt(row1[1].length()-1) == ','?row1[1].substring(0,row1[1].length()-1):row1[1]);
				String strrow2 = "";
				String[] row2 = new String[]{strrow0,strrow1,strrow2};
				vposmodelSelect.add(row2);
			}
		}
		
    	lisPara.removeAll();
    	
		for (int i = 0; i < vposmodelSelect.size();i ++)
		{
			String[] row = (String[]) vposmodelSelect.elementAt(i);
			for (int j = 0;j<vdevicename.size();j++)
			{
				String[] row1 = (String[]) vdevicename.elementAt(j);
				if (row[0].equalsIgnoreCase(row1[0]))
				{
					if (row1[1].length() > 0)
					{
						row[1] += "," + row1[1];
					}
				}
			}
			
			String str1 = row[0];
			String str2 = (row[1] == null || row[1].trim().length()<=0)?"":row[1].trim();
			String str3 = (row[2] == null || row[2].trim().length()<=0)?"":"\t\t&& " + row[2].trim();
			lisPara.add(Convert.appendStringSize("", str1, 0, 12, 12) + " = " + str2 + str3 );
		}
    }
  
    class cmbSelectChange implements ISelectionChangedListener
    {
    	public void selectionChanged(final SelectionChangedEvent arg0) 
    	{
    		lisPara.removeAll();
    		vposmodelSelect.clear();
    		
    		if (cmbStyle.getSelectionIndex() < 0) return;

    		if (cmbStyle.getSelectionIndex() == 0)
    		{
    			loadval();
    		}
    		else
    		{
				for (int i = 0; i < vposmodel.size(); i++)
		        {
					String[] row = (String[]) vposmodel.elementAt(i);
					
					if (row[0] == null) continue;
					if (("[" + cmbStyle.getText().trim() + "]").equalsIgnoreCase(row[0]))
					{
						for (int j = i+1;j<vposmodel.size();j++)
						{
							String[] row1 = (String[]) vposmodel.elementAt(j);
							if (row1[0] == null) continue;
							
				            if (row1[0].length() > 2 && row1[1] == null)
				            {
				            	if (row1[0].charAt(0) == '[' && row1[0].charAt(row1[0].length() - 1) == ']')
				            	{
				            		if (row1[0] != "[" + cmbStyle.getText().trim() + "]")
				            		{
				            			return;
				            		}
				            	}
				            }
				            
							String str1 = row1[0];
							String str2 = (row1[1] == null || row1[1].trim().length()<=0)?"":row1[1].trim();
							String str3 = (row1[2] == null || row1[2].trim().length()<=0)?"":"\t\t&& " + row1[2].trim();
							
							lisPara.add(Convert.appendStringSize("", str1, 0, 12, 12) + " = " + str2 + str3 );
							vposmodelSelect.add(row1);
						}
					}
		        }
    		}
    	}
    }

}


