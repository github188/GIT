package posserver.Configure.Init;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

import posserver.Configure.ConfigWizard;
import posserver.Configure.Common.CommonMethod;
import posserver.Configure.Common.GlobalVar;

public class InitCfgPage extends WizardPage 
{	
	private Label infoLabel_1;
	private Button button_2;
	private Text txtPosServerName;
	private Text txtPosServerPath;
	private Button chkIsInstall;
	public static boolean IsInit = false; //该页面是否是初始化
	
	private Label infoLabel;
	private Text txtServerPath;
    private Combo cmbServerType;
    private Button button_1;
    
	public InitCfgPage() {
		super(ConfigWizard.Initcfg,"初始设置",ImageDescriptor.createFromFile(InitCfgPage.class, "q.gif"));
		this.setMessage("请配置PosServer初始安装参数");
	}

	/**
	 * Create contents of the wizard
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		//
		setControl(container);

		final Label label_2 = new Label(container, SWT.NONE);
		label_2.setBounds(10, 43, 106, 20);
		label_2.setText("应用服务器路径:");

		final Label label_2_1 = new Label(container, SWT.NONE);
		label_2_1.setBounds(10, 16, 106, 20);
		label_2_1.setText("应用服务器类型:");

		button_1 = new Button(container, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0) 
			{
				CommonMethod.openDirectory(getShell(),txtServerPath);
			}
		});
		button_1.setBounds(514, 43, 44, 21);
		button_1.setText("..");

		cmbServerType = new Combo(container, SWT.NONE);
		cmbServerType.setBounds(123, 16, 385, 21);
		for(int i = 0;i < GlobalVar.ServerTypes.size(); i++)
		{
			cmbServerType.add(((ServerTypeStruct)GlobalVar.ServerTypes.get(i)).ServerType);
		}
		
		txtServerPath = new Text(container, SWT.BORDER);
		txtServerPath.setBounds(123, 43, 385, 21);

		infoLabel = new Label(container, SWT.NONE);
		infoLabel.setBounds(10, 146, 498, 20);

		txtPosServerPath = new Text(container, SWT.BORDER);
		txtPosServerPath.setBounds(123, 70, 385, 21);

		final Label label_2_2 = new Label(container, SWT.NONE);
		label_2_2.setBounds(10, 70, 106, 20);
		label_2_2.setText("PosServer安装文件:");

		final Label label_2_2_1 = new Label(container, SWT.NONE);
		label_2_2_1.setBounds(10, 97, 106, 20);
		label_2_2_1.setText("PosServer服务名:");

		txtPosServerName = new Text(container, SWT.BORDER);
		txtPosServerName.setBounds(123, 97, 385, 21);

		button_2 = new Button(container, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0) 
			{
				if (CommonMethod.openDirectory(getShell(),txtPosServerPath))
				{
					txtPosServerName.setText(ConfigWizard.GetFileName(txtPosServerPath.getText().trim()));
				}
			}
		});
		button_2.setBounds(514, 70, 44, 21);
		button_2.setText("..");

		chkIsInstall = new Button(container, SWT.CHECK);
		chkIsInstall.setText("安装PosServer服务到应用服务器");
		chkIsInstall.setBounds(123, 124, 247, 16);

		infoLabel_1 = new Label(container, SWT.NONE);
		infoLabel_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		infoLabel_1.setText("架设 POSSERVER 注意事项:\n\n1.POSSERVER服务器应与数据库服务器处于同一局域网内,以保证数据库连接稳定\n\n2.一台POSSERVER可配置多个数据源连接不同的数据库,分配通讯命令访问相应数据库\n\n3.POS客户端采用HTTP短连接方式,可访问远程的POSSERVER服务器\n\n4.POS客户端支持连接三个POSSERVER,一个POS业务,一个储值卡,一个会员CRM\n\n5.POS客户端不能远程访问POSSERVER时,应在本地局域网架设虚拟POSSERVER服务器,\n  在该虚拟POSSERVER服务器上用端口映射的方式访问远程POSSERVER服务器");
		infoLabel_1.setBounds(10, 172, 602, 258);
	}
    
	public IWizardPage getPreviousPage()
	{
		InitCfgPage.IsInit = true;
    	
    	return this;
	}
	
	public IWizardPage getNextPage()
    {
		// 如果是初始化则填充数据
    	if (InitCfgPage.IsInit)
    	{
    		initdata();
    	}
    	else
    	{   //不是初始化保存数据
    		if (!save())
    		{
    			return this;
    		}
    		
    		GlobalVar.LastPage = this;
    	}

    	InitCfgPage.IsInit = false;
    	
    	ServerTypeStruct servertype = GlobalVar.initCfgDef.GetServerType();
		
		if (servertype != null) 
		{
			return getWizard().getPage(servertype.ServerPage);
		}

    	return this;
    }
    
    public void initdata()
    {
    	cmbServerType.select(0);
    	
    	ServerTypeStruct servertype = GlobalVar.initCfgDef.GetServerType();
    	
    	if (servertype != null)
    	{
    		int index = GlobalVar.ServerTypes.indexOf(servertype);
    		
    		if (index >=0) cmbServerType.select(index);
    	}

        this.txtServerPath.setText(GlobalVar.initCfgDef.GetServerPath());
        
        this.txtPosServerName.setText(GlobalVar.initCfgDef.GetPosServerName());
        
        this.txtPosServerPath.setText(GlobalVar.initCfgDef.GetPosServerPath());
        
        this.chkIsInstall.setSelection(GlobalVar.initCfgDef.GetIsCopyPosInstallFile());
    }
    
    public boolean save()
    {
    	infoLabel.setText("");
    	
    	if (cmbServerType.getSelectionIndex() < 0)
    	{
    		infoLabel.setText("请选择应用服务器型号!");
    		return false;
    	}
    		
    	if (txtServerPath.getText().trim().length() <= 0)
    	{
    		infoLabel.setText("请输入应用服务器安装路径!");
    		return false;
    	}

    	// 检查安装地址是否存在
    	File file = new File(txtServerPath.getText().trim()); 
    	if (!(file.exists() && file.isDirectory()))
    	{
    		infoLabel.setText("你输入的应用服务器路径不存在或者不是目录!");
    		return false;
    	}
    	
    	// 检查PosServer安装文件是否存在
    	if (txtPosServerPath.getText().trim().length() > 0)
    	{
	    	file = new File(txtPosServerPath.getText().trim()); 
	    	if (!(file.exists() && file.isDirectory()))
	    	{
	    		infoLabel.setText("你输入的PosServer安装文件路径不存在或者不是目录!");
	    		return false;
	    	}
	    	
	    	if (txtPosServerName.getText().trim().length() <= 0)
	    	{
	    		String servername = ConfigWizard.GetFileName(txtPosServerPath.getText().trim());
	    		txtPosServerName.setText(servername);
	    	}
    	}
    	
    	int index = cmbServerType.getSelectionIndex();
    	ServerTypeStruct sts = (ServerTypeStruct)GlobalVar.ServerTypes.get(index);

    	Vector vinitcfg = ConfigWizard.read_File(GlobalVar.InitCfgPath);
        if (vinitcfg == null)
        {
            return false;
        }
        
        for (int i = 0; i < vinitcfg.size(); i++)
        {
        	String[] row = (String[]) vinitcfg.elementAt(i);
        	        	
        	if (row[0] == null) continue;
        	
        	if (row[0].equalsIgnoreCase("ServerType")) 
        		row[1] = String.valueOf(index);
        	
        	if (row[0].equalsIgnoreCase("ServerPath")) 
        		row[1] = txtServerPath.getText().trim();
        	
        	if (row[0].equalsIgnoreCase("PosServerPath")) 
        		row[1] = txtPosServerPath.getText().trim();
        	
        	if (row[0].equalsIgnoreCase("PosServerName")) 
        		row[1] = txtPosServerName.getText().trim();
        }
        
    	ConfigWizard.write_File(GlobalVar.InitCfgPath, vinitcfg);
    	
    	GlobalVar.initCfgDef.SetServerType(sts);
    	GlobalVar.initCfgDef.SetServerPath(txtServerPath.getText().trim());
    	GlobalVar.initCfgDef.SetPosServerPath(txtPosServerPath.getText().trim());
    	GlobalVar.initCfgDef.SetPosServerName(txtPosServerName.getText().trim());
    	GlobalVar.initCfgDef.SetIsCopyPosInstallFile(chkIsInstall.getSelection());

    	if (GlobalVar.initCfgDef.GetIsCopyPosInstallFile() && GlobalVar.initCfgDef.GetPosServerPath().length() > 0)
    	{
    		if (GlobalVar.initCfgDef.GetServerType().ServerJdbcJarInstallFile.length() > 0
    			&& GlobalVar.initCfgDef.GetServerType().ServerJdbcJarInstallPath.length() > 0)
    		{
				this.infoLabel.setText("正在拷备Jdbc jar文件,请等持...");
				if (!CopyJdbcJarFile()) 
				{
					this.infoLabel.setText("拷备Jdbc jar安装文件失败!");
					return false;
				}
				this.infoLabel.setText("拷备Jdbc jar安装文件完成!");
    		}
		
    		this.infoLabel.setText("正在拷备PosServer安装文件,请等持...");
    		if (!CopyPosServerInstallFile()) 
    		{
    			this.infoLabel.setText("拷备PosServer安装文件失败!");
    			return false;
    		}
    		this.infoLabel.setText("拷备PosServer安装文件完成!");
    		
    		GlobalVar.initCfgDef.SetIsCopyPosInstallFile(false);
    	}
    	
    	return true;
    }
    
    public boolean CopyJdbcJarFile()
    {
    	try
    	{
	    	String strsource = GlobalVar.initCfgDef.GetServerType().ServerJdbcJarInstallFile;
	    	String strdes = GlobalVar.initCfgDef.GetServerPath() + GlobalVar.initCfgDef.GetServerType().ServerJdbcJarInstallPath;
	    	
	    	if (!CommonMethod.CopyFolder(strsource, strdes, infoLabel,false))
	    	{
	    		return false;
	    	}
	    	
	    	return true;
    	}
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;

	    }
    }
    
    public boolean CopyPosServerInstallFile()
    {
    	try
    	{
	    	String strsource = GlobalVar.initCfgDef.GetPosServerPath();
	    	String ServerName = GlobalVar.initCfgDef.GetPosServerName();
	    	if (ServerName.length() <= 0)
	    	{
	    		ServerName = ConfigWizard.GetFileName(strsource);
	    	}
	    	String strdest = GlobalVar.initCfgDef.GetServerPath() + GlobalVar.initCfgDef.GetServerType().ServerPosServerInstallPath.replace("[SerName]",ServerName);
	    	
	    	File file = new File(strdest);
	    	if (file.exists())
	    	{
	    		 MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
	    	     msgbox.setMessage("你要安装PosServer服务的目录\n\n"+strdest+"\n\n已经存在请检查后再继续安装!");
	    	     msgbox.open();
	    	     
	    	     return false;
	    	}
	    	
	    	if (!CommonMethod.CopyFolder(strsource, strdest, infoLabel,true))
	    	{
	    		return false;
	    	}
	    	
	    	return true;
    	}
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;

	    }
    }
}
