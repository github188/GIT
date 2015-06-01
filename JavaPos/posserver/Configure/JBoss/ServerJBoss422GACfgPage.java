package posserver.Configure.JBoss;

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

public class ServerJBoss422GACfgPage extends WizardPage
{   
    private Text txttype_mapping;
    private Text txtexception_sorter_class_name;
    private Text txtSetBigStringTryClob;
    private Text txtidle_timeout_minutes;
    private Text txtblocking_timeout_millis;
    private Text txtmax_pool_size;
    private Text txtmin_pool_size;
    private Text txtpassword;
    private Text txtuser_name;
    private Text txtdriver_class;
    private Text txtconnection_url;
    private Text txtjndi_name;

    /*
    protected Button btnWrite;
    */
    
    protected Button btnLoadCfg;
    protected Button btnOpenDirectory;
    protected Text txtPath;
    protected Table tabList;
    protected TabItem tbiDataSourceList;
    protected TabItem tbiDataSourceDetail;
    protected TabFolder tabFolder;
    protected Label infoLabel;
    protected Label infoLabel1;
    protected Combo cmbmodel_type;
    
    protected ServerJBoss422GALogic logic = null;
    
    //protected boolean isneedwrite = false;
    
    public static boolean IsInit = true; //该页面是否是初始化
    
    protected ServerJBoss422GACfgPage(String str1,String str2,ImageDescriptor imgdes)
    {
    	super(str1, str2, imgdes);
    	logic = new ServerJBoss422GALogic();
    }
    
    public ServerJBoss422GACfgPage()
    {
        super(ConfigWizard.JBossgacfg, "JBoss数据源配置", ImageDescriptor.createFromFile(ServerJBoss422GACfgPage.class, "q.gif"));
        this.setMessage("手式配置JBoss的Jndi数据源!");
    	logic = new ServerJBoss422GALogic();
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
    	tabList.addMouseListener(new MouseAdapter() {
    		public void mouseDoubleClick(final MouseEvent arg0) 
    		{
    			tabFolder.setSelection(1);
    			TabFolder_SelectIndexChange(tbiDataSourceDetail);
    		}
    	});
    	tabList.setLinesVisible(true);
    	tabList.setHeaderVisible(true);

    	final TableColumn newColumnTableColumn = new TableColumn(tabList, SWT.NONE);
    	newColumnTableColumn.setWidth(511);
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
    	label_2.setBounds(10, 33, 65, 20);
    	label_2.setText("数据源名称:");

    	txtjndi_name = new Text(composite_2, SWT.BORDER);
    	txtjndi_name.setBounds(106, 33, 444, 21);

    	txtconnection_url = new Text(composite_2, SWT.BORDER);
    	txtconnection_url.setBounds(106, 60, 444, 21);

    	final Label label_2_1 = new Label(composite_2, SWT.NONE);
    	label_2_1.setBounds(10, 60, 62, 20);
    	label_2_1.setText("连接串:");

    	final Label label_2_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1.setBounds(10, 87, 65, 20);
    	label_2_1_1.setText("驱动类名:");

    	txtdriver_class = new Text(composite_2, SWT.BORDER);
    	txtdriver_class.setBounds(106, 87, 444, 21);

    	final Label label_2_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1.setBounds(10, 114, 62, 20);
    	label_2_1_1_1.setText("用户名:");

    	txtuser_name = new Text(composite_2, SWT.BORDER);
    	txtuser_name.setBounds(106, 114, 173, 21);

    	final Label label_2_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1.setBounds(306, 114, 40, 20);
    	label_2_1_1_1_1.setText("密码:");

    	txtpassword = new Text(composite_2, SWT.BORDER);
    	txtpassword.setBounds(352, 114, 198, 21);

    	final Label label_2_1_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1_1.setBounds(10, 140, 90, 20);
    	label_2_1_1_1_1_1.setText("最小连接数量:");

    	txtmin_pool_size = new Text(composite_2, SWT.BORDER);
    	txtmin_pool_size.setBounds(106, 141, 173, 21);

    	final Label label_2_1_1_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1_1_1.setBounds(10, 168, 90, 20);
    	label_2_1_1_1_1_1_1.setText("最大连接数量:");

    	txtmax_pool_size = new Text(composite_2, SWT.BORDER);
    	txtmax_pool_size.setBounds(106, 168, 173, 21);

    	final Button btnSave = new Button(composite_2, SWT.NONE);
    	btnSave.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnSaveClick();
    		}
    	});
    	
    	btnSave.setBounds(556, 6, 56, 21);
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
    	cmbmodel_type.setBounds(106, 6, 444, 21);

    	final Label label_2_2 = new Label(composite_2, SWT.NONE);
    	label_2_2.setBounds(10, 6, 65, 20);
    	label_2_2.setText("数据库类型:");

    	final Label blocking_timeout_millisLabel = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel.setBounds(303, 194, 198, 20);
    	blocking_timeout_millisLabel.setText("(blocking_timeout_millis)");

    	txtblocking_timeout_millis = new Text(composite_2, SWT.BORDER);
    	txtblocking_timeout_millis.setBounds(106, 194, 173, 21);

    	txtidle_timeout_minutes = new Text(composite_2, SWT.BORDER);
    	txtidle_timeout_minutes.setBounds(106, 221, 173, 21);

    	final Label blocking_timeout_millisLabel_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_1.setBounds(303, 221, 198, 20);
    	blocking_timeout_millisLabel_1.setText("(idle_timeout_minutes)");

    	txtSetBigStringTryClob = new Text(composite_2, SWT.BORDER);
    	txtSetBigStringTryClob.setBounds(106, 248, 173, 21);

    	final Label blocking_timeout_millisLabel_1_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_1_1.setBounds(303, 248, 198, 20);
    	blocking_timeout_millisLabel_1_1.setText("(SetBigStringTryClob)");

    	txtexception_sorter_class_name = new Text(composite_2, SWT.BORDER);
    	txtexception_sorter_class_name.setBounds(106, 301, 173, 20);

    	final Label blocking_timeout_millisLabel_1_1_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_1_1_1.setBounds(303, 300, 231, 23);
    	blocking_timeout_millisLabel_1_1_1.setText("(exception_sorter_class_name)");

    	txttype_mapping = new Text(composite_2, SWT.BORDER);
    	txttype_mapping.setBounds(106, 275, 173, 20);

    	final Label lbtype_mapping = new Label(composite_2, SWT.NONE);
    	lbtype_mapping.setBounds(303, 275, 231, 20);
    	lbtype_mapping.setText("(type_mapping)");

    	infoLabel1 = new Label(composite_2, SWT.NONE);
    	infoLabel1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	infoLabel1.setBounds(10, 329, 598, 40);

    	final Label blocking_timeout_millisLabel_2 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_2.setBounds(10, 194, 62, 20);
    	blocking_timeout_millisLabel_2.setText("杂项1:");

    	final Label blocking_timeout_millisLabel_2_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_2_1.setBounds(10, 221, 62, 20);
    	blocking_timeout_millisLabel_2_1.setText("杂项2:");

    	final Label blocking_timeout_millisLabel_2_2 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_2_2.setBounds(10, 248, 62, 20);
    	blocking_timeout_millisLabel_2_2.setText("杂项3:");

    	final Label blocking_timeout_millisLabel_2_2_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_2_2_1.setBounds(10, 275, 62, 20);
    	blocking_timeout_millisLabel_2_2_1.setText("杂项4:");

    	final Label blocking_timeout_millisLabel_2_2_2 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_2_2_2.setBounds(10, 301, 62, 20);
    	blocking_timeout_millisLabel_2_2_2.setText("杂项5:");

    	final Label blocking_timeout_millisLabel_3 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_3.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	blocking_timeout_millisLabel_3.setBounds(303, 141, 198, 20);
    	blocking_timeout_millisLabel_3.setText("(建议为款机数的1/2)");

    	final Label blocking_timeout_millisLabel_4 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_4.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	blocking_timeout_millisLabel_4.setBounds(303, 168, 198, 20);
    	blocking_timeout_millisLabel_4.setText("(建议为款机数的3/4)");

    	final Button btnTest = new Button(composite_2, SWT.NONE);
    	btnTest.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnTestClick();
    		}
    	});
    	btnTest.setBounds(556, 33, 56, 21);
    	btnTest.setText("测试");

    	txtPath = new Text(composite, SWT.BORDER);
    	txtPath.setBounds(74, 12, 424, 21);

    	final Label label_2_3 = new Label(composite, SWT.NONE);
    	label_2_3.setBounds(10, 12, 69, 20);
    	label_2_3.setText("配置文件:");

    	btnOpenDirectory = new Button(composite, SWT.NONE);
    	btnOpenDirectory.setBounds(504, 12, 50, 21);
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
    	btnLoadCfg.setBounds(556, 12, 60, 21);
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
    	if (ServerJBoss422GACfgPage.IsInit)
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
    	
    	ServerJBoss422GACfgPage.IsInit = false;

    	return getWizard().getPage(ConfigWizard.Cmddefcfg);
    }

    public IWizardPage getPreviousPage()
    {	   
    	/*
    	if (!ServerJBoss422GACfgPage.IsInit)
    	{
    		if (!canContact()) return this;
    	}
    	*/
    	
    	ServerJBoss422GACfgPage.IsInit = true;
    	
    	return super.getPreviousPage();
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
        
        /*
    	tabFolder.setSelection(0);
    	*/
    	
        infolabel.setText("写入到配置文件成功!");
    	
    	return true;
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
			infoLabel.setText("装载配置文件错误!\r\n" + logic.getMsg());
			return;
		}
		
		RefreshTabList();
		
		infoLabel.setText("配置文件装载成功!");
    }
    
	/**
	 * 清空明细显示
	 *
	 */
	public void ClearDetail()
	{
		infoLabel1.setText("");
		txtexception_sorter_class_name.setText("");
		txtSetBigStringTryClob.setText("");
		txtidle_timeout_minutes.setText("");
		txtblocking_timeout_millis.setText("");
		txtmax_pool_size.setText("");
		txtmin_pool_size.setText("");
		txtpassword.setText("");
		txtuser_name.setText("");
		txtdriver_class.setText("");
		txtconnection_url.setText("");
		txtjndi_name.setText("");
		txttype_mapping.setText("");
	}
	
    /**
     * cmbmodel_type选择改变
     */
    public void cmbmodel_type_SelectIndexChange()
    {
    	if (cmbmodel_type.getSelectionIndex() <= 0) return;
    	ServerJBoss422GAStruct sjs = ((ServerJBoss422GAJndiModelStruct)logic.GetCfgModel().get(cmbmodel_type.getSelectionIndex() - 1)).JBoss422CfgModelData;
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
			
			ServerJBoss422GAStruct sjs = null;
			if (tabList.getSelectionIndex() >= 0)
			{
				sjs = ((ServerJBoss422GAStruct)logic.GetConfig().get(tabList.getSelectionIndex()));
			}
 
			RefreshTabDetail(sjs);
		}
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
	 * 保存
	 * 
	 */
	public void btnSaveClick()
	{
		infoLabel.setText("");
		infoLabel1.setText("");
				
		if (txtjndi_name.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请输录入名称!");
			return ;
		}
		
		if (txtconnection_url.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请录入连接串!");
			return;
		}
		
		if (txtconnection_url.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请录入驱动类名!");
			return;
		}
		
		if (!logic.CheckSave(txtjndi_name.getText().trim(), tabList.getSelectionIndex()))
		{
			infoLabel1.setText("保存失败!" + logic.getMsg());
			return;
		}
		
		ServerJBoss422GAStruct sjs = new ServerJBoss422GAStruct();
		if (tabList.getSelectionIndex() < 0)
		{
			// 保存新增
			sjs = new ServerJBoss422GAStruct();
			logic.GetConfig().add(sjs);
		}
		else
		{
			// 保存修改
			sjs = (ServerJBoss422GAStruct)logic.GetConfig().get(tabList.getSelectionIndex());
		}
		
		sjs.jndi_name = txtjndi_name.getText().trim();
		sjs.connection_url = txtconnection_url.getText().trim();
		sjs.driver_class = txtdriver_class.getText().trim();
		sjs.user_name = txtuser_name.getText().trim();
		sjs.password = txtpassword.getText().trim();
		sjs.min_pool_size = txtmin_pool_size.getText().trim();
		sjs.max_pool_size = txtmax_pool_size.getText().trim();
		sjs.idle_timeout_minutes = txtidle_timeout_minutes.getText().trim();
		sjs.blocking_timeout_millis = txtblocking_timeout_millis.getText().trim();
		sjs.SetBigStringTryClob = txtSetBigStringTryClob.getText().trim();
		sjs.exception_sorter_class_name = txtexception_sorter_class_name.getText().trim();
		sjs.type_mapping = txttype_mapping.getText().trim();
		
		RefreshTabList();
		
		tabList.select(logic.GetConfig().indexOf(sjs));
		
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
            Class.forName(txtdriver_class.getText().trim());
            Connection conn = DriverManager.getConnection(txtconnection_url.getText().trim(),txtuser_name.getText().trim(),txtpassword.getText().trim());
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
		ServerJBoss422GAStruct sjs = (ServerJBoss422GAStruct)logic.GetConfig().get(tabList.getSelectionIndex());
		 MessageBox msgbox=new MessageBox(new Shell(),SWT.OK|SWT.CANCEL);
	     msgbox.setMessage("是否要删除名称为[" + sjs.jndi_name + "]的Jndi配置信息!");
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
			ti.setText(((ServerJBoss422GAStruct)logic.GetConfig().get(i)).jndi_name);
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
				ServerJBoss422GAJndiModelStruct sjjm = ((ServerJBoss422GAJndiModelStruct)logic.GetCfgModel().get(i));
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
			ServerJBoss422GAStruct sjs = (ServerJBoss422GAStruct)obj;
			infoLabel1.setText("");
			txtexception_sorter_class_name.setText(sjs.exception_sorter_class_name);
			txtSetBigStringTryClob.setText(sjs.SetBigStringTryClob);
			txtidle_timeout_minutes.setText(sjs.idle_timeout_minutes);
			txtblocking_timeout_millis.setText(sjs.blocking_timeout_millis);
			txtmax_pool_size.setText(sjs.max_pool_size);
			txtmin_pool_size.setText(sjs.min_pool_size);
			txtpassword.setText(sjs.password);
			txtuser_name.setText(sjs.user_name);
			txtdriver_class.setText(sjs.driver_class);
			txtconnection_url.setText(sjs.connection_url);
			txtjndi_name.setText(sjs.jndi_name);
			txttype_mapping.setText(sjs.type_mapping);
		}
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
