package posserver.Configure.Tomcat;

import java.sql.Connection;
import java.sql.DriverManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import posserver.Configure.ConfigWizard;
import posserver.Configure.Common.CommonMethod;
import posserver.Configure.Common.GlobalVar;

import com.swtdesigner.SWTResourceManager;


public class ServerTomCat5523CfgPage extends WizardPage
{   

    //private Button btnWrite;
    private Button btnLoadCfg;
    private Button btnOpenDirectory;
    private Text txtPath;
    private Label infoLabel1;
    private TabItem tbiDataSourceDetail;
    private Table tabList;
    private TabItem tbiDataSourceList;
    private TabFolder tabFolder;
    private Label infoLabel;
    private Combo cmbmodel_type;
    
    private Text txttype;
    private Text txtmaxWait;
    private Text txtmaxActive;
    private Text txtmaxIdle;
    private Text txtpassword;
    private Text txtusername;
    private Text txtdriverClassName;
    private Text txturl;
    private Text txtname;
    
    //protected boolean isneedwrite = false;
    
    public static boolean IsInit = true; //该页面是否是初始化
    
    ServerTomCat5523Logic logic = new ServerTomCat5523Logic();
    
    public ServerTomCat5523CfgPage()
    {
        super(ConfigWizard.Tomcatcfg, "Tomcat数据源配置", ImageDescriptor.createFromFile(ServerTomCat5523CfgPage.class, "q.gif"));
        this.setMessage("手式配置Tomcat的Jndi数据源!");
        
        logic = new ServerTomCat5523Logic();
    }
	
    public void createControl(Composite parent)
    {
    	Composite composite = new Composite(parent,SWT.NONE);
        setControl(composite); 

    	tabFolder = new TabFolder(composite, SWT.NONE);
    	tabFolder.setBounds(0, 50, 622, 395);
    	tabFolder.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0) 
			{
				if (arg0.item != null)
				{
					TabFolder_SelectIndexChange((TabItem)arg0.item);
				}
			}
		});
    	
    	tbiDataSourceList = new TabItem(tabFolder, SWT.NONE);
    	tbiDataSourceList.setText("数据源");

    	final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
    	tbiDataSourceList.setControl(composite_1);

    	tabList = new Table(composite_1, SWT.FULL_SELECTION | SWT.BORDER);
    	tabList.setBounds(0, 0, 550, 309);
    	tabList.addMouseListener(new MouseAdapter() 
    	{
    		public void mouseDoubleClick(final MouseEvent arg0) 
    		{
    			tabFolder.setSelection(1);
    			TabFolder_SelectIndexChange(tbiDataSourceDetail);
    		}
    	});

    	tabList.setLinesVisible(true);
    	tabList.setHeaderVisible(true);

    	final TableColumn newColumnTableColumn = new TableColumn(tabList, SWT.NONE);
    	newColumnTableColumn.setWidth(513);
    	newColumnTableColumn.setText("名称");

    	final Button btnNew = new Button(composite_1, SWT.NONE);
    	btnNew.setBounds(556, 8, 56, 21);
    	btnNew.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnNewClick();
    		}
    	});
    	btnNew.setText("新增");

    	Button btnDel;
    	btnDel = new Button(composite_1, SWT.NONE);
    	btnDel.setBounds(556, 40, 56, 21);
    	btnDel.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnDelClick();
    		}
    	});
    	btnDel.setText("删除");

    	infoLabel = new Label(composite_1, SWT.NONE);
    	infoLabel.setBounds(0, 315, 595, 30);
    	infoLabel.setForeground(SWTResourceManager.getColor(255, 0, 0));

    	tbiDataSourceDetail = new TabItem(tabFolder, SWT.NONE);
    	tbiDataSourceDetail.setText("数据源配置");

    	final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
    	tbiDataSourceDetail.setControl(composite_2);

    	final Label label_2 = new Label(composite_2, SWT.NONE);
    	label_2.setBounds(10, 43, 65, 20);
    	label_2.setText("名称:");

    	txtname = new Text(composite_2, SWT.BORDER);
    	txtname.setBounds(110, 43, 440, 21);

    	txturl = new Text(composite_2, SWT.BORDER);
    	txturl.setBounds(110, 75, 440, 58);

    	final Label label_2_1 = new Label(composite_2, SWT.NONE);
    	label_2_1.setBounds(10, 75, 62, 20);
    	label_2_1.setText("连接串:");

    	final Label label_2_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1.setBounds(10, 144, 65, 20);
    	label_2_1_1.setText("驱动类名:");

    	txtdriverClassName = new Text(composite_2, SWT.BORDER);
    	txtdriverClassName.setBounds(110, 144, 440, 21);

    	final Label label_2_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1.setBounds(10, 208, 62, 20);
    	label_2_1_1_1.setText("用户名:");

    	txtusername = new Text(composite_2, SWT.BORDER);
    	txtusername.setBounds(110, 208, 164, 21);

    	final Label label_2_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1.setBounds(289, 208, 62, 20);
    	label_2_1_1_1_1.setText("密码:");

    	txtpassword = new Text(composite_2, SWT.BORDER);
    	txtpassword.setBounds(357, 208, 193, 21);

    	final Label maxidleLabel = new Label(composite_2, SWT.NONE);
    	maxidleLabel.setBounds(10, 240, 94, 20);
    	maxidleLabel.setText("最大空闲连接数:");

    	txtmaxIdle = new Text(composite_2, SWT.BORDER);
    	txtmaxIdle.setBounds(110, 240, 164, 21);

    	final Label maxactiveLabel = new Label(composite_2, SWT.NONE);
    	maxactiveLabel.setBounds(10, 272, 88, 20);
    	maxactiveLabel.setText("最大激活连接数:");

    	txtmaxActive = new Text(composite_2, SWT.BORDER);
    	txtmaxActive.setBounds(110, 272, 164, 21);

    	final Button btnSave = new Button(composite_2, SWT.NONE);
    	btnSave.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnSaveClick();
    		}
    	});
    	
    	btnSave.setBounds(556, 10, 56, 21);
    	btnSave.setText("保存");

    	cmbmodel_type = new Combo(composite_2, SWT.NONE);
    	cmbmodel_type.setVisibleItemCount(10);
    	cmbmodel_type.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			cmbmodel_type_SelectIndexChange();
    		}
    	});
    	cmbmodel_type.setBounds(110, 11, 440, 21);

    	final Label label_2_2 = new Label(composite_2, SWT.NONE);
    	label_2_2.setBounds(10, 11, 65, 20);
    	label_2_2.setText("数据库:");

    	final Label blocking_timeout_millisLabel = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel.setBounds(10, 302, 94, 20);
    	blocking_timeout_millisLabel.setText("最大等待时间(ms):");

    	txtmaxWait = new Text(composite_2, SWT.BORDER);
    	txtmaxWait.setBounds(110, 302, 164, 21);

    	infoLabel1 = new Label(composite_2, SWT.NONE);
    	infoLabel1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	infoLabel1.setBounds(10, 329, 593, 40);
    	infoLabel1.setText("Label");

    	final Label label_2_1_1_2 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_2.setBounds(10, 176, 65, 20);
    	label_2_1_1_2.setText("类型:");

    	txttype = new Text(composite_2, SWT.BORDER);
    	txttype.setBounds(110, 176, 440, 21);

    	final Label maxidleLabel_1 = new Label(composite_2, SWT.NONE);
    	maxidleLabel_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	maxidleLabel_1.setBounds(289, 240, 124, 20);
    	maxidleLabel_1.setText("(建议为款机数的1/2)");

    	final Label maxidleLabel_1_1 = new Label(composite_2, SWT.NONE);
    	maxidleLabel_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	maxidleLabel_1_1.setBounds(289, 272, 124, 20);
    	maxidleLabel_1_1.setText("(建议为款机数的3/4)");

    	final Label maxidleLabel_1_2 = new Label(composite_2, SWT.NONE);
    	maxidleLabel_1_2.setBounds(289, 302, 124, 20);

    	final Label maxidleLabel_1_1_1 = new Label(composite_2, SWT.NONE);
    	maxidleLabel_1_1_1.setBounds(289, 302, 124, 20);
    	maxidleLabel_1_1_1.setText("(推荐:10000ms)");

    	final Button btnTest = new Button(composite_2, SWT.NONE);
    	btnTest.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnTestClick();
    		}
    	});
    	btnTest.setBounds(556, 43, 56, 21);
    	btnTest.setText("测试");

    	txtPath = new Text(composite, SWT.BORDER);
    	txtPath.setBounds(80, 12, 418, 21);

    	final Label label_2_3 = new Label(composite, SWT.NONE);
    	label_2_3.setBounds(10, 12, 69, 21);
    	label_2_3.setText("配置文件:");

    	btnOpenDirectory = new Button(composite, SWT.NONE);
    	btnOpenDirectory.setBounds(504, 11, 50, 21);
    	btnOpenDirectory.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			if(CommonMethod.openFileDialog(getShell(),txtPath,new String[]{"*.xml","*.*"},new String[]{"XML Files(*.xml)"}))
    			{
    				btnLoadCfgClick();
    			}
    		}
    	});
    	btnOpenDirectory.setText("..");

    	btnLoadCfg = new Button(composite, SWT.NONE);
    	btnLoadCfg.setBounds(556, 11, 60, 21);
    	btnLoadCfg.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnLoadCfgClick();
    		}
    	});
    	btnLoadCfg.setText("装载");

    	/*
    	btnWrite = new Button(composite, SWT.NONE);
    	btnWrite.setBounds(560, 12, 56, 21);
    	btnWrite.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnWriteClick();
    		}
    	});
    	btnWrite.setText("写入");
    	*/
    }

    public IWizardPage getNextPage()
    {
    	if (ServerTomCat5523CfgPage.IsInit)
    	{
        	refreshpath();
    		initdata();
    	}
    	else	
    	{
    		if (!canContact())
    		{
    			return this;
    		}
    		
    		GlobalVar.LastPage = this;
    	}
    	
    	ServerTomCat5523CfgPage.IsInit = false;

    	return getWizard().getPage(ConfigWizard.Cmddefcfg);
    }

    public IWizardPage getPreviousPage()
    {	
    	/*
    	if (!ServerTomCat5523CfgPage.IsInit)
    	{
    		if (!canContact()) return this;
    	}
    	*/
    	
    	ServerTomCat5523CfgPage.IsInit = true;
    	
    	return super.getPreviousPage();
    }
    
	/**
	 * 清空明细显示
	 *
	 */
	public void ClearDetail()
	{
		infoLabel1.setText("");
		txtmaxWait.setText("");
		txtmaxActive.setText("");
		txtmaxIdle.setText("");
		txtpassword.setText("");
		txtusername.setText("");
		txtdriverClassName.setText("");
		txturl.setText("");
		txtname.setText("");
	}
	
    /**
     * cmbmodel_type选择改变
     */
    public void cmbmodel_type_SelectIndexChange()
    {
    	if (cmbmodel_type.getSelectionIndex() <= 0) return;
    	ServerTomCat5523Struct sjs = ((ServerTomCat5523JndiModelStruct)logic.GetCfgModel().get(cmbmodel_type.getSelectionIndex() - 1)).TomCat5523CfgModelData;
    	RefreshTabDetail(sjs);
    }
    
    /**
	 * TabFload选择改变
	 * @param item 选择页
	 */
	public void TabFolder_SelectIndexChange(TabItem item)
	{
		if(item.equals(tbiDataSourceDetail))
		{
			cmbmodel_type.select(0);
			
			ServerTomCat5523Struct sjs = null;
			if (tabList.getSelectionIndex() >= 0)
			{
				sjs = ((ServerTomCat5523Struct)logic.GetConfig().get(tabList.getSelectionIndex()));
			}
 
			RefreshTabDetail(sjs);
		}
	}
	
	/**
	 * 保存
	 * 
	 */
	public void btnSaveClick()
	{
		infoLabel.setText("");
		infoLabel1.setText("");
		
		if (txtname.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请输录入名称!");
			return ;
		}
		
		if (txturl.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请录入连接串!");
			return;
		}
		
		if (txturl.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请录入驱动类名!");
			return;
		}
		
		if (!logic.CheckSave(txtname.getText().trim(), tabList.getSelectionIndex()))
		{
			infoLabel1.setText("保存失败!" + logic.getMsg());
			return;
		}
		
		ServerTomCat5523Struct sts = new ServerTomCat5523Struct();
		if (tabList.getSelectionIndex() < 0)
		{
			// 保存新增
			sts = new ServerTomCat5523Struct();
			logic.GetConfig().add(sts);
		}
		else
		{
			// 保存修改
			sts = (ServerTomCat5523Struct)logic.GetConfig().get(tabList.getSelectionIndex());
		}
		
		sts.name = txtname.getText().trim();
		if (!(sts.name.length() >= 5 && sts.name.substring(0,5).equalsIgnoreCase("jdbc/")))
		{
			sts.name = "jdbc/" + sts.name;
			txtname.setText(sts.name);
		}
		
		sts.url = txturl.getText().trim();
		sts.driverClassName = txtdriverClassName.getText().trim();
		sts.username = txtusername.getText().trim();
		sts.password = txtpassword.getText().trim();
		sts.type = txttype.getText().trim();
		sts.maxIdle = txtmaxIdle.getText().trim();
		sts.maxActive = txtmaxActive.getText().trim();
		sts.maxWait = txtmaxWait.getText().trim();
				
		RefreshTabList();
		
		tabList.select(logic.GetConfig().indexOf(sts));
		
		Write();
		
		// 需要保存
		//isneedwrite = true;
		
		//this.infoLabel1.setText("保存成功,请点击写入将修改的信息写入配置文件!");
	}
	
	public void btnTestClick()
	{
        try
        {
        	infoLabel1.setText("正在连接数据库,请等待...");
            Class.forName(txtdriverClassName.getText().trim());
            Connection conn = DriverManager.getConnection(txturl.getText().trim(),txtusername.getText().trim(),txtpassword.getText().trim());
            if (conn != null)
            {
            	conn.close();
            	infoLabel1.setText("数据库连接成功!");
            }
            else
            {
            	infoLabel1.setText("数据库连接失败!");
            }	            
        }
        catch (Exception ex)
        {
        	infoLabel1.setText("数据库连接失败!\n"+ex.getMessage());
        }
    }
	
	/**
	 * 
	 */
	public void btnDelClick()
	{
		if (tabList.getSelectionIndex() < 0) return;
		ServerTomCat5523Struct sts = (ServerTomCat5523Struct)logic.GetConfig().get(tabList.getSelectionIndex());
		 MessageBox msgbox=new MessageBox(new Shell(),SWT.OK|SWT.CANCEL);
	     msgbox.setMessage("是否要删除名称为[" + sts.name + "]的Jndi配置信息!");
	     if(msgbox.open()!=SWT.OK)
	     {
	    	 return;
	     } 
		logic.GetConfig().remove(tabList.getSelectionIndex());
		
		RefreshTabList();
		
		Write();
	}
	
	/**
	 * 写入配置信息
	 */
	/*
	public void btnWriteClick()
	{
		Write();
	}
    */
	
	/**
	 * 写入配置文件
	 * @return
	 */
	public boolean Write()
	{	
		Label infolabel = null;
		
		if (this.tabFolder.getSelectionIndex() != 0)
		{
			infolabel = this.infoLabel1;
		}
		else
		{
			infolabel = this.infoLabel;
		}
		
		if (save(infolabel))
		{
			//isneedwrite = false;
			return true;
		}
    	
		return false;
	}
	
	/**
	 * 刷新TabList
	 *
	 */
	public void RefreshTabList()
	{
		tabList.removeAll();
		for (int i = 0;i < logic.GetConfig().size();i++)
		{
			TableItem ti = new TableItem(tabList,SWT.NONE);
			ti.setText(((ServerTomCat5523Struct)logic.GetConfig().get(i)).name);
		}
	}
	
	/**
	 * 刷新ModelType
	 * 
	 */
	public void RefreshCmbModelType()
	{
		cmbmodel_type.removeAll();
		cmbmodel_type.add("请选择配置模板");
		
		if (logic.GetCfgModel() != null)
		{
			for(int i = 0;i < logic.GetCfgModel().size(); i++)
			{
				ServerTomCat5523JndiModelStruct sjjm = ((ServerTomCat5523JndiModelStruct)logic.GetCfgModel().get(i));
				cmbmodel_type.add(sjjm.DataBaseType);
			}
		}
		
		cmbmodel_type.select(0);
	}
	
	/**
	 * 刷新数据源明细
	 *
	 */
	public void RefreshTabDetail(Object obj)
	{
	    infoLabel1.setText("");
		
	    if (obj == null)
		{
			ClearDetail();
		}
		else
		{
			ServerTomCat5523Struct sts = (ServerTomCat5523Struct)obj;
			infoLabel1.setText("");
			txtmaxActive.setText(sts.maxActive);
			txtmaxIdle.setText(sts.maxIdle);
			txtmaxWait.setText(sts.maxWait);
			txtpassword.setText(sts.password);
			txtusername.setText(sts.username);
			txtdriverClassName.setText(sts.driverClassName);
			txturl.setText(sts.url);
			txtname.setText(sts.name);
			txttype.setText(sts.type);
		}
	}
	
	/**
	 * 新增
	 *
	 */
	public void btnNewClick()
	{
		tabList.deselectAll();
		
		tabFolder.setSelection(1);
		
		TabFolder_SelectIndexChange(tabFolder.getItems()[tabFolder.getSelectionIndex()]);
	}
	
	/**
     * 保存数据
     * @return
     */
    protected boolean save(Label infolabel)
    {
    	infolabel.setText("");
    	
    	logic.SetCfgPath(txtPath.getText().trim());
    	
        if(!logic.Save())
        {
        	infolabel.setText("写入到配置文件失败!" + logic.getMsg());
        	return false;
        }
        
        //tabFolder.setSelection(0);
        
        infolabel.setText("写入到配置文件成功!");
    	
    	return true;
    }
    
    /**
     * 装载配置文件
     * 
     */
    protected void btnLoadCfgClick()
    {    	
    	logic.SetCfgPath(txtPath.getText().trim());
    	
    	initdata();
    }
    
    /**
     * 初始化数据
     *
     */
    public void initdata()
    {
    	infoLabel.setText("");
    	
		tabFolder.setSelection(0);
		
		tabList.removeAll();
		ClearDetail();
		
		RefreshCmbModelType();
		
		if (!logic.InitData())
		{
			RefreshTabList();
			infoLabel.setText(logic.getMsg());
			return;
		}
		
		RefreshTabList();
		
		infoLabel.setText("配置文件装载成功!");
    }
    
    /**
     * 刷新路径
     */
    protected void refreshpath()
    {
    	if (GlobalVar.LastPage.equals(getPreviousPage()))
    	{
    		logic.RefreshPath();
    	}
    	
    	txtPath.setText(logic.GetCfgPath());
    }
    
    /**
	 * 
	 * 是否可以下一页
	 */
	protected boolean canContact()
	{
		if (!logic.CanContact()) return true;
		
		/*
		if (isneedwrite)
		{
			 MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO);
		     msgbox.setMessage("修改的配置信息未写入文件,是否写入文件?");
		     if(msgbox.open()==SWT.YES)
		     {
		    	 return Write();
		     }
		     else
		     {
		    	 this.isneedwrite = false;
		     }
		}
		*/
	     return true;
	}
}
