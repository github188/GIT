package posserver.Configure.SysConfig;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
import posserver.Configure.CmdDef.CmdDefCfgPage;
import posserver.Configure.CmdDef.DataSourceCommonStruct;
import posserver.Configure.Common.CommonMethod;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.JBoss.ServerJBoss422GACfgPage;

import com.swtdesigner.SWTResourceManager;

public class PosServerSysCfgPage extends WizardPage
{   
    private Combo cmbdatasourename;
    private Text txtfileupdateendtime;
    private Text txtfileupdatestarttime;
    private Text txtautodayendtime;
    private Text txtinspecttime;
    private Text txttasktime;
    private Text txtcreatetablecmdcode;
    private Text txtcmddefcmdcode;
    private Text txtfileupdatecmdcode;
    private Text txtdatasoureurl;
    //private Text txtdatasourename;
    
    //private Text txtfileupdatedef;
    private Combo cmbfileupdatedef;
    
    //private Text txtdayenddef;
    private Combo cmbdayenddef;
    
    //private Text txtlocaldbtype;
    private Combo cmblocaldbtype;
    
    //private Text txtdatasouremode;
    private Combo cmbdatasouremode;
    
    private Combo cmboutencoder;
    //private Text txtoutencoder;
    
    private Combo cmbinputencoder;
    //private Text txtinputencoder;
    
    private Text txtid;

    protected Button btnWrite;
    protected Button btnLoadCfg;
    protected Button btnOpenDirectory;
    protected Text txtPath;
    protected Table tabList;
    protected TabItem tbiDataSourceList;
    protected TabItem tbiDataSourceDetail;
    protected TabFolder tabFolder;
    protected Label infoLabel;
    protected Label infoLabel1;
    
    protected Button btnEditcmddefcmdcode;
    protected Button btnEditfileupdatecmdcode;
    protected Button btnEditcreatetablecmdcode;
    
    protected PosServerSysLogic logic = null;
    
    //protected boolean isneedwrite = false;
    
    public static boolean IsInit = true; //该页面是否是初始化
    
    
    
    protected PosServerSysCfgPage(String str1,String str2,ImageDescriptor imgdes)
    {
    	super(str1, str2, imgdes);
    	logic = new PosServerSysLogic();
    }
    
    public PosServerSysCfgPage()
    {
        super(ConfigWizard.Posserversyscfg, "PosServer配置", ImageDescriptor.createFromFile(ServerJBoss422GACfgPage.class, "q.gif"));
        this.setMessage("PosServer配置!");
    	logic = new PosServerSysLogic();
    }

    public void createControl(Composite parent)
    {
    	Composite composite = new Composite(parent,SWT.NONE);
    	composite.setLayout(new FormLayout());
        setControl(composite); 
    	
    	tabFolder = new TabFolder(composite, SWT.NONE);
    	final FormData fd_tabFolder = new FormData();
    	fd_tabFolder.bottom = new FormAttachment(0, 445);
    	fd_tabFolder.top = new FormAttachment(0, 168);
    	fd_tabFolder.right = new FormAttachment(0, 622);
    	fd_tabFolder.left = new FormAttachment(0, 0);
    	tabFolder.setLayoutData(fd_tabFolder);
    	//nullsetBounds(0, 0, 0, 0);
    	//nullsetBounds(0, 0, 0, 0);
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
    	tabList.setBounds(0, 0, 551, 188);
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
    	newColumnTableColumn.setWidth(495);
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
    	infoLabel.setBounds(0, 194, 614, 37);
    	infoLabel.setForeground(SWTResourceManager.getColor(255, 0, 0));

    	tbiDataSourceDetail = new TabItem(tabFolder, SWT.NONE);
    	tbiDataSourceDetail.setText("数据源配置");

    	final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
    	tbiDataSourceDetail.setControl(composite_2);

    	final Label idLabel = new Label(composite_2, SWT.NONE);
    	idLabel.setBounds(10, 6, 65, 20);
    	idLabel.setText("数据源别名:");

    	txtid = new Text(composite_2, SWT.BORDER);
    	txtid.setBounds(81, 6, 469, 21);

    	cmbdatasouremode = new Combo(composite_2, SWT.NONE);
    	cmbdatasouremode.setBounds(81, 60, 470, 21);
		cmbdatasouremode.select(0);
		
		for(int i = 0;i < GlobalVar.Datasouremode.size(); i++)
		{
			cmbdatasouremode.add((String)GlobalVar.Datasouremode.get(i));
		}
		
		if (cmbdatasouremode.getItems().length > 0)
		{
			cmbdatasouremode.select(0);
		}
		
		
		/*
    	txtdatasouremode = new Text(composite_2, SWT.BORDER);
    	txtdatasouremode.setBounds(81, 45, 486, 27);
		 */
		
    	final Label label_2_1 = new Label(composite_2, SWT.NONE);
    	label_2_1.setBounds(10, 60, 62, 20);
    	label_2_1.setText("数据源模式:");

    	final Label label_2_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1.setBounds(10, 32, 65, 20);
    	label_2_1_1.setText("数据源名称:");

    	//txtdatasourename = new Text(composite_2, SWT.BORDER);
    	//txtdatasourename.setBounds(134, 57, 470, 21);

    	final Label urlLabel = new Label(composite_2, SWT.NONE);
    	urlLabel.setBounds(10, 87, 62, 20);
    	urlLabel.setText("数据源URL:");

    	txtdatasoureurl = new Text(composite_2, SWT.BORDER);
    	txtdatasoureurl.setBounds(81, 87, 470, 21);

    	final Label label_2_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1.setBounds(10, 169, 62, 20);
    	label_2_1_1_1_1.setText("更新命令:");

    	txtfileupdatecmdcode = new Text(composite_2, SWT.BORDER);
    	txtfileupdatecmdcode.setBounds(81, 169, 470, 21);

    	final Label label_2_1_1_1_1_1 = new Label(composite_2, SWT.NONE);
    	label_2_1_1_1_1_1.setBounds(10, 142, 62, 20);
    	label_2_1_1_1_1_1.setText("通讯命令:");

    	txtcmddefcmdcode = new Text(composite_2, SWT.BORDER);
    	txtcmddefcmdcode.setBounds(81, 142, 470, 21);

    	final Button btnSave = new Button(composite_2, SWT.NONE);
    	btnSave.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnSaveClick();
    		}
    	});
    	
    	btnSave.setBounds(556, 6, 56, 21);
    	btnSave.setText("保存");

    	final Label blocking_timeout_millisLabel = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel.setBounds(10, 196, 62, 20);
    	blocking_timeout_millisLabel.setText("日终命令:");

    	txtcreatetablecmdcode = new Text(composite_2, SWT.BORDER);
    	txtcreatetablecmdcode.setBounds(81, 196, 470, 21);

    	//txtinputencoder = new Text(composite_2, SWT.BORDER);
    	//txtinputencoder.setBounds(81, 33, 193, 21);

    	final Label blocking_timeout_millisLabel_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_1.setBounds(10, 115, 65, 20);
    	blocking_timeout_millisLabel_1.setText("输入字符集:");

    	//txtoutencoder = new Text(composite_2, SWT.BORDER);
    	//txtoutencoder.setBounds(358, 33, 193, 21);

    	final Label blocking_timeout_millisLabel_1_1 = new Label(composite_2, SWT.NONE);
    	blocking_timeout_millisLabel_1_1.setBounds(290, 115, 65, 21);
    	blocking_timeout_millisLabel_1_1.setText("输出字符集:");

    	infoLabel1 = new Label(composite_2, SWT.NONE);
    	infoLabel1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	infoLabel1.setBounds(10, 223, 598, 28);

    	cmbdatasourename = new Combo(composite_2, SWT.NONE);
    	cmbdatasourename.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			if (cmbdatasourename.getSelectionIndex() >= 0)
    			{
    				cmbdatasouremode.select(0);
    			}
    		}
    	});
    	cmbdatasourename.setBounds(81, 32, 470, 21);
    	cmbdatasourename.select(0);

    	cmbinputencoder = new Combo(composite_2, SWT.NONE);
    	cmbinputencoder.setBounds(81, 115, 193, 21);
    	cmbinputencoder.select(0);

		for(int i = 0;i < GlobalVar.Inputencoder.size(); i++)
		{
			cmbinputencoder.add((String)GlobalVar.Inputencoder.get(i));
		}
		
		if (cmbinputencoder.getItems().length > 0)
		{
			cmbinputencoder.select(0);
		}
		
    	cmboutencoder = new Combo(composite_2, SWT.NONE);
    	cmboutencoder.setBounds(358, 115, 193, 21);
    	cmboutencoder.select(0);

		for(int i = 0;i < GlobalVar.Outencoder.size(); i++)
		{
			cmboutencoder.add((String)GlobalVar.Outencoder.get(i));
		}
		
		if (cmboutencoder.getItems().length > 0)
		{
			cmboutencoder.select(0);
		}

    	btnEditcmddefcmdcode = new Button(composite_2, SWT.NONE);
    	btnEditcmddefcmdcode.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnEditClick(txtcmddefcmdcode);
    		}
    	});
    	btnEditcmddefcmdcode.setBounds(556, 142, 56, 21);
    	btnEditcmddefcmdcode.setText("修改");

    	btnEditfileupdatecmdcode = new Button(composite_2, SWT.NONE);
    	btnEditfileupdatecmdcode.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnEditClick(txtfileupdatecmdcode);
    		}
    	});
    	btnEditfileupdatecmdcode.setBounds(557, 169, 56, 21);
    	btnEditfileupdatecmdcode.setText("修改");

    	btnEditcreatetablecmdcode = new Button(composite_2, SWT.NONE);
    	btnEditcreatetablecmdcode.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnEditClick(txtcreatetablecmdcode);
    		}
    	});
    	btnEditcreatetablecmdcode.setBounds(556, 196, 56, 21);
    	btnEditcreatetablecmdcode.setText("修改");
		
    	txtPath = new Text(composite, SWT.BORDER);
    	final FormData fd_txtPath = new FormData();
    	fd_txtPath.bottom = new FormAttachment(0, 33);
    	fd_txtPath.top = new FormAttachment(0, 12);
    	fd_txtPath.right = new FormAttachment(0, 499);
    	fd_txtPath.left = new FormAttachment(0, 80);
    	txtPath.setLayoutData(fd_txtPath);

    	final Label label_2_3 = new Label(composite, SWT.NONE);
    	final FormData fd_label_2_3 = new FormData();
    	fd_label_2_3.bottom = new FormAttachment(0, 32);
    	fd_label_2_3.top = new FormAttachment(0, 12);
    	fd_label_2_3.right = new FormAttachment(0, 79);
    	fd_label_2_3.left = new FormAttachment(0, 10);
    	label_2_3.setLayoutData(fd_label_2_3);
    	label_2_3.setText("配置文件:");
    	
    	btnOpenDirectory = new Button(composite, SWT.NONE);
    	final FormData fd_btnOpenDirectory = new FormData();
    	fd_btnOpenDirectory.bottom = new FormAttachment(0, 33);
    	fd_btnOpenDirectory.top = new FormAttachment(0, 12);
    	fd_btnOpenDirectory.right = new FormAttachment(0, 555);
    	fd_btnOpenDirectory.left = new FormAttachment(0, 505);
    	btnOpenDirectory.setLayoutData(fd_btnOpenDirectory);
    	//nullsetBounds(0, 0, 0, 0);
    	//nullsetBounds(0, 0, 0, 0);
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
    	final FormData fd_btnLoadCfg = new FormData();
    	fd_btnLoadCfg.bottom = new FormAttachment(0, 33);
    	fd_btnLoadCfg.top = new FormAttachment(0, 12);
    	fd_btnLoadCfg.right = new FormAttachment(0, 617);
    	fd_btnLoadCfg.left = new FormAttachment(0, 557);
    	btnLoadCfg.setLayoutData(fd_btnLoadCfg);
    	//nullsetBounds(0, 0, 0, 0);
    	//nullsetBounds(0, 0, 0, 0);
    	btnLoadCfg.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnLoadCfgClick();
    		}
    	});
    	btnLoadCfg.setText("装载");

    	btnWrite = new Button(composite, SWT.NONE);
    	final FormData fd_btnWrite = new FormData();
    	fd_btnWrite.bottom = new FormAttachment(0, 81);
    	fd_btnWrite.top = new FormAttachment(0, 60);
    	fd_btnWrite.right = new FormAttachment(0, 617);
    	fd_btnWrite.left = new FormAttachment(0, 561);
    	btnWrite.setLayoutData(fd_btnWrite);
    	//nullsetBounds(0, 0, 0, 0);
    	//nullsetBounds(0, 0, 0, 0);
    	btnWrite.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnWriteClick();
    		}
    	});
    	btnWrite.setText("保存");

    	txttasktime = new Text(composite, SWT.BORDER);
    	final FormData fd_txttasktime = new FormData();
    	fd_txttasktime.bottom = new FormAttachment(0, 81);
    	fd_txttasktime.top = new FormAttachment(0, 60);
    	fd_txttasktime.right = new FormAttachment(0, 276);
    	fd_txttasktime.left = new FormAttachment(0, 129);
    	txttasktime.setLayoutData(fd_txttasktime);

    	final Label idLabel_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1 = new FormData();
    	fd_idLabel_1.bottom = new FormAttachment(0, 80);
    	fd_idLabel_1.top = new FormAttachment(0, 60);
    	fd_idLabel_1.right = new FormAttachment(0, 126);
    	fd_idLabel_1.left = new FormAttachment(0, 10);
    	idLabel_1.setLayoutData(fd_idLabel_1);
    	idLabel_1.setText("定时器时间间隔(m):");

    	final Label idLabel_1_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_1 = new FormData();
    	fd_idLabel_1_1.bottom = new FormAttachment(0, 80);
    	fd_idLabel_1_1.top = new FormAttachment(0, 60);
    	fd_idLabel_1_1.right = new FormAttachment(0, 402);
    	fd_idLabel_1_1.left = new FormAttachment(0, 286);
    	idLabel_1_1.setLayoutData(fd_idLabel_1_1);
    	idLabel_1_1.setText("联网状态刷新时间(m):");

    	txtinspecttime = new Text(composite, SWT.BORDER);
    	final FormData fd_txtinspecttime = new FormData();
    	fd_txtinspecttime.bottom = new FormAttachment(0, 81);
    	fd_txtinspecttime.top = new FormAttachment(0, 60);
    	fd_txtinspecttime.right = new FormAttachment(0, 555);
    	fd_txtinspecttime.left = new FormAttachment(0, 408);
    	txtinspecttime.setLayoutData(fd_txtinspecttime);

    	final Label idLabel_1_2 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_2 = new FormData();
    	fd_idLabel_1_2.bottom = new FormAttachment(0, 106);
    	fd_idLabel_1_2.top = new FormAttachment(0, 86);
    	fd_idLabel_1_2.right = new FormAttachment(0, 126);
    	fd_idLabel_1_2.left = new FormAttachment(0, 10);
    	idLabel_1_2.setLayoutData(fd_idLabel_1_2);
    	idLabel_1_2.setText("自动生成日终库:");

    	cmbdayenddef = new Combo(composite, SWT.NONE);
    	final FormData fd_cmbdayenddef = new FormData();
    	fd_cmbdayenddef.bottom = new FormAttachment(0, 107);
    	fd_cmbdayenddef.top = new FormAttachment(0, 86);
    	fd_cmbdayenddef.right = new FormAttachment(0, 276);
    	fd_cmbdayenddef.left = new FormAttachment(0, 129);
    	cmbdayenddef.setLayoutData(fd_cmbdayenddef);
		for(int i = 0;i < GlobalVar.Dayenddef.size(); i++)
		{
			cmbdayenddef.add((String)GlobalVar.Dayenddef.get(i));
		}
		
		if (cmbdayenddef.getItems().length > 0)
		{
			cmbdayenddef.select(0);
		}
		
		/*
    	txtdayenddef = new Text(composite, SWT.BORDER);
    	txtdayenddef.setBounds(484, 65, 155, 27);
		 */
		
    	final Label idLabel_1_1_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_1_1 = new FormData();
    	fd_idLabel_1_1_1.bottom = new FormAttachment(0, 106);
    	fd_idLabel_1_1_1.top = new FormAttachment(0, 86);
    	fd_idLabel_1_1_1.right = new FormAttachment(0, 402);
    	fd_idLabel_1_1_1.left = new FormAttachment(0, 286);
    	idLabel_1_1_1.setLayoutData(fd_idLabel_1_1_1);
    	idLabel_1_1_1.setText("日终时间(hh:mm:ss):");

    	txtautodayendtime = new Text(composite, SWT.BORDER);
    	final FormData fd_txtautodayendtime = new FormData();
    	fd_txtautodayendtime.bottom = new FormAttachment(0, 107);
    	fd_txtautodayendtime.top = new FormAttachment(0, 86);
    	fd_txtautodayendtime.right = new FormAttachment(0, 555);
    	fd_txtautodayendtime.left = new FormAttachment(0, 408);
    	txtautodayendtime.setLayoutData(fd_txtautodayendtime);

    	/*
    	txtfileupdatedef = new Text(composite, SWT.BORDER);
    	txtfileupdatedef.setBounds(135, 98, 155, 27);
    	 */
    	
    	cmbfileupdatedef = new Combo(composite, SWT.NONE);
    	final FormData fd_cmbfileupdatedef = new FormData();
    	fd_cmbfileupdatedef.bottom = new FormAttachment(0, 134);
    	fd_cmbfileupdatedef.top = new FormAttachment(0, 113);
    	fd_cmbfileupdatedef.right = new FormAttachment(0, 276);
    	fd_cmbfileupdatedef.left = new FormAttachment(0, 129);
    	cmbfileupdatedef.setLayoutData(fd_cmbfileupdatedef);
		for(int i = 0;i < GlobalVar.Fileupdatedef.size(); i++)
		{
			cmbfileupdatedef.add((String)GlobalVar.Fileupdatedef.get(i));
		}
		
		if (cmbfileupdatedef.getItems().length > 0)
		{
			cmbfileupdatedef.select(0);
		}
		
    	Label idLabel_1_2_1;
    	idLabel_1_2_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_2_1 = new FormData();
    	fd_idLabel_1_2_1.bottom = new FormAttachment(0, 133);
    	fd_idLabel_1_2_1.top = new FormAttachment(0, 113);
    	fd_idLabel_1_2_1.right = new FormAttachment(0, 110);
    	fd_idLabel_1_2_1.left = new FormAttachment(0, 10);
    	idLabel_1_2_1.setLayoutData(fd_idLabel_1_2_1);
    	idLabel_1_2_1.setText("定时生成更新库:");

    	txtfileupdatestarttime = new Text(composite, SWT.BORDER);
    	final FormData fd_txtfileupdatestarttime = new FormData();
    	fd_txtfileupdatestarttime.bottom = new FormAttachment(0, 162);
    	fd_txtfileupdatestarttime.top = new FormAttachment(0, 141);
    	fd_txtfileupdatestarttime.right = new FormAttachment(0, 325);
    	fd_txtfileupdatestarttime.left = new FormAttachment(0, 129);
    	txtfileupdatestarttime.setLayoutData(fd_txtfileupdatestarttime);

    	Label idLabel_1_2_2;
    	idLabel_1_2_2 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_2_2 = new FormData();
    	fd_idLabel_1_2_2.bottom = new FormAttachment(0, 161);
    	fd_idLabel_1_2_2.top = new FormAttachment(0, 141);
    	fd_idLabel_1_2_2.right = new FormAttachment(0, 126);
    	fd_idLabel_1_2_2.left = new FormAttachment(0, 10);
    	idLabel_1_2_2.setLayoutData(fd_idLabel_1_2_2);
    	idLabel_1_2_2.setText("停止定时器(hh:mm:ss): ");

    	txtfileupdateendtime = new Text(composite, SWT.BORDER);
    	final FormData fd_txtfileupdateendtime = new FormData();
    	fd_txtfileupdateendtime.bottom = new FormAttachment(0, 162);
    	fd_txtfileupdateendtime.top = new FormAttachment(0, 141);
    	fd_txtfileupdateendtime.right = new FormAttachment(0, 555);
    	fd_txtfileupdateendtime.left = new FormAttachment(0, 359);
    	txtfileupdateendtime.setLayoutData(fd_txtfileupdateendtime);

    	final Label idLabel_1_2_1_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_2_1_1 = new FormData();
    	fd_idLabel_1_2_1_1.bottom = new FormAttachment(0, 161);
    	fd_idLabel_1_2_1_1.top = new FormAttachment(0, 141);
    	fd_idLabel_1_2_1_1.right = new FormAttachment(0, 350);
    	fd_idLabel_1_2_1_1.left = new FormAttachment(0, 335);
    	idLabel_1_2_1_1.setLayoutData(fd_idLabel_1_2_1_1);
    	idLabel_1_2_1_1.setText("到");

    	/*
    	txtlocaldbtype = new Text(composite, SWT.BORDER);
    	txtlocaldbtype.setBounds(135, 131, 155, 27);
    	 */
    	
    	cmblocaldbtype = new Combo(composite, SWT.NONE);
    	final FormData fd_cmblocaldbtype = new FormData();
    	fd_cmblocaldbtype.bottom = new FormAttachment(0, 134);
    	fd_cmblocaldbtype.top = new FormAttachment(0, 113);
    	fd_cmblocaldbtype.right = new FormAttachment(0, 555);
    	fd_cmblocaldbtype.left = new FormAttachment(0, 408);
    	cmblocaldbtype.setLayoutData(fd_cmblocaldbtype);
		for(int i = 0;i < GlobalVar.Localdbtype.size(); i++)
		{
			cmblocaldbtype.add((String)GlobalVar.Localdbtype.get(i));
		}
		
		if (cmblocaldbtype.getItems().length > 0)
		{
			cmblocaldbtype.select(0);
		}
		
    	final Label idLabel_1_2_2_1 = new Label(composite, SWT.NONE);
    	final FormData fd_idLabel_1_2_2_1 = new FormData();
    	fd_idLabel_1_2_2_1.bottom = new FormAttachment(0, 133);
    	fd_idLabel_1_2_2_1.top = new FormAttachment(0, 113);
    	fd_idLabel_1_2_2_1.right = new FormAttachment(0, 391);
    	fd_idLabel_1_2_2_1.left = new FormAttachment(0, 286);
    	idLabel_1_2_2_1.setLayoutData(fd_idLabel_1_2_2_1);
    	idLabel_1_2_2_1.setText("本地库类型:");
    }
    
    public IWizardPage getNextPage()
    {
    	if (PosServerSysCfgPage.IsInit)
    	{
    		cmbdatasourename.removeAll();
    		for(int i = 0;i < GlobalVar.Datasourcecommon.size(); i++)
    		{
    			cmbdatasourename.add(((DataSourceCommonStruct)GlobalVar.Datasourcecommon.get(i)).DataSourceName);
    		}
    		
    		if (cmbdatasourename.getItems().length > 0)
    		{
    			cmbdatasourename.select(0);
    		}
    		
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
    	
    	PosServerSysCfgPage.IsInit = false;
    	
    	return  super.getNextPage();
    }

    public IWizardPage getPreviousPage()
    {	
    	if (!PosServerSysCfgPage.IsInit)
    	{
    		if (!canContact()) return this;
    	}
    	
    	PosServerSysCfgPage.IsInit = true;
    	
    	return super.getPreviousPage();
    }
    
    /**
     * 初始化数据
     *
     */
    public void initdata()
    {
    	infoLabel.setText("");
    	
		tabFolder.setSelection(0);
		
		ClearAll();
		
		if (!logic.InitData())
		{		
			RefreshAll();
			infoLabel.setText(logic.getMsg());
			return;
		}
		
		RefreshAll();
		
		infoLabel.setText("配置文件装载成功!");
    }
    
    /**
	 * TabFload选择改变
	 * @param item 选择页
	 */
	public void TabFolder_SelectIndexChange(TabItem item)
	{
		if(item.equals(tbiDataSourceDetail))
		{	
			PosServerSysDataSourceStruct sjs = null;
			if (tabList.getSelectionIndex() >= 0)
			{
				sjs = (PosServerSysDataSourceStruct)logic.GetConfig().get(tabList.getSelectionIndex());
			}
 
			RefreshTabDetail(sjs);
		}
	}
	
	public void btnEditClick(Text txt)
	{
		FrmTabList frm = new FrmTabList();
		CmdDefCfgPage cdcp = (CmdDefCfgPage)getWizard().getPage(ConfigWizard.Cmddefcfg);
		
		Vector vinitdata = new Vector();
		Vector vselectval = new Vector();
		
		String filename = cdcp.logic.GetCfgPath().toLowerCase();
		if (txt.equals(txtcmddefcmdcode))
		{
			if (filename.indexOf("cmddef.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("cmddef.xml", "cmddef.xml"));
			if (filename.indexOf("createtablecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("createtablecmd.xml", "cmddef.xml"));
			if (filename.indexOf("fileupdatecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("fileupdatecmd.xml", "cmddef.xml"));
		}
		else if (txt.equals(this.txtcreatetablecmdcode))
		{
			if (filename.indexOf("cmddef.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("cmddef.xml", "createtablecmd.xml"));
			if (filename.indexOf("createtablecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("createtablecmd.xml", "createtablecmd.xml"));
			if (filename.indexOf("fileupdatecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("fileupdatecmd.xml", "createtablecmd.xml"));
		}
		else if (txt.equals(this.txtfileupdatecmdcode))
		{
			if (filename.indexOf("cmddef.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("cmddef.xml", "fileupdatecmd.xml"));
			if (filename.indexOf("createtablecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("createtablecmd.xml", "fileupdatecmd.xml"));
			if (filename.indexOf("fileupdatecmd.xml") >= 0) cdcp.logic.LoadData(vinitdata,filename.replace("fileupdatecmd.xml", "fileupdatecmd.xml"));
		}
		
		if (txt.getText().trim().length() > 0) 
		{
			String[] str = txt.getText().trim().split(",");
			for (int i = 0;i < str.length ; i++)
			{
				vselectval.add(str[i]);
			}
		}
		
		frm.open(vinitdata, vselectval, getShell());
		
		txt.setText("");
		String str = "";
		for (int i = 0;i < vselectval.size();i++)
		{
			str = str + (String)vselectval.get(i) + ",";
		}
		
		if (str.length() > 0)
		{
			str = str.substring(0,str.length() - 1);
		}
		
		if (str.trim().length() > 0)
		{
			txt.setText(str);
		}
		else
		{
			txt.setText("*");
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
		
		if (txtid.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请输录入数据源别名!");
			return ;
		}
		
		if (cmbdatasourename.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请录入数据源名称!");
			return;
		}
		
		if (!logic.CheckSave(txtid.getText().trim(), tabList.getSelectionIndex()))
		{
			infoLabel1.setText("保存失败!" + logic.getMsg());
			return;
		}
		
		PosServerSysDataSourceStruct sjs = new PosServerSysDataSourceStruct();
		if (tabList.getSelectionIndex() < 0)
		{
			// 保存新增
			sjs = new PosServerSysDataSourceStruct();
			logic.GetConfig().add(sjs);
		}
		else
		{
			// 保存修改
			sjs = (PosServerSysDataSourceStruct)logic.GetConfig().get(tabList.getSelectionIndex());
		}
		
		sjs.id = txtid.getText().trim();
		//sjs.datasouremode = txtdatasouremode.getText().trim();
		sjs.datasouremode = cmbdatasouremode.getText().trim();
		sjs.datasourename = cmbdatasourename.getText().trim();
		sjs.datasoureurl = txtdatasoureurl.getText().trim();
		
		sjs.cmddefcmdcode.clear();
		sjs.createtablecmdcode.clear();
		sjs.fileupdatecmdcode.clear();
		
		String[] str = txtcmddefcmdcode.getText().trim().split(",");
		for (int i = 0;i < str.length ; i++)
		{
			sjs.cmddefcmdcode.add(str[i]);
		}
		
		str = txtcreatetablecmdcode.getText().trim().split(",");
		for (int i = 0;i < str.length ; i++)
		{
			sjs.createtablecmdcode.add(str[i]);
		}
		
		str = txtfileupdatecmdcode.getText().trim().split(",");
		for (int i = 0;i < str.length ; i++)
		{
			sjs.fileupdatecmdcode.add(str[i]);
		}
		
		sjs.inputencoder = cmbinputencoder.getText().trim();
		sjs.outencoder = cmboutencoder.getText().trim();
		
		RefreshTabList();
		
		tabList.select(logic.GetConfig().indexOf(sjs));
		
		Write();
		
		// 需要保存
		//isneedwrite = true;
		
		//this.infoLabel1.setText("保存成功,请点击写入将修改的信息写入配置文件!");
	}
	
	
	/**
	 * 
	 */
	public void btnDelClick()
	{
		if (tabList.getSelectionIndex() < 0) return;
		
		PosServerSysDataSourceStruct sjs = (PosServerSysDataSourceStruct)logic.GetConfig().get(tabList.getSelectionIndex());
		 MessageBox msgbox=new MessageBox(new Shell(),SWT.OK|SWT.CANCEL);
	     msgbox.setMessage("是否要删除ID为[" + sjs.id + "]的数据源配置信息!");
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
	public void btnWriteClick()
	{
		Write();
	}
	
	/**
	 * 刷新整个窗口数据
	 */
	public void RefreshAll()
	{
		RefreshForm();
		
		tabFolder.setSelection(0);
		
		RefreshTabList();
	}
	
	/**
	 * 刷新文本录入Text
	 */
	public void RefreshForm()
	{
		Vector v = ((PosServerSysLogic)logic).GetAllConfig();
		if (v == null ||v.size() <= 0) 
		{
			ClearForm();
			return;
		}
		
		txttasktime.setText(((PosServerSysStruct)v.get(0)).tasktime);
		//txtdayenddef.setText(((PosServerSysStruct)v.get(0)).dayenddef);
		cmbdayenddef.setText(((PosServerSysStruct)v.get(0)).dayenddef);
		//txtfileupdatedef.setText(((PosServerSysStruct)v.get(0)).fileupdatedef);
		cmbfileupdatedef.setText(((PosServerSysStruct)v.get(0)).fileupdatedef);
		txtautodayendtime.setText(((PosServerSysStruct)v.get(0)).autodayendtime);
		//txtlocaldbtype.setText(((PosServerSysStruct)v.get(0)).localdbtype);
		cmblocaldbtype.setText(((PosServerSysStruct)v.get(0)).localdbtype);
		txtinspecttime.setText(((PosServerSysStruct)v.get(0)).inspecttime);
		txtfileupdatestarttime.setText(((PosServerSysStruct)v.get(0)).fileupdatestarttime);
		txtfileupdateendtime.setText(((PosServerSysStruct)v.get(0)).fileupdateendtime);
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
			ti.setText(((PosServerSysDataSourceStruct)logic.GetConfig().get(i)).id);
		}
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
			PosServerSysDataSourceStruct psss = (PosServerSysDataSourceStruct)obj;
			infoLabel1.setText("");
			
			txtid.setText(psss.id);
			//txtdatasouremode.setText(psss.datasouremode);
			cmbdatasouremode.setText(psss.datasouremode);
			cmbdatasourename.setText(psss.datasourename);
			txtdatasoureurl.setText(psss.datasoureurl);
			
			String cmddefcmdcode = "";
	    	String fileupdatecmdcode = "";
	    	String createtablecmdcode = "";
	    	
	    	for (int i = 0;i < psss.cmddefcmdcode.size();i++)
	    	{
	    		String str = (String)psss.cmddefcmdcode.get(i);
	    		cmddefcmdcode = cmddefcmdcode + str +",";
	    	}
	    	
	    	if (cmddefcmdcode.length() > 0)
	    	{
	    		cmddefcmdcode = cmddefcmdcode.substring(0,cmddefcmdcode.length() -1);
	    	}
	    	
	    	for (int i = 0;i < psss.fileupdatecmdcode.size();i++)
	    	{
	    		String str = (String)psss.fileupdatecmdcode.get(i);
	    		fileupdatecmdcode = fileupdatecmdcode + str +",";
	    	}
	    	
	    	if (fileupdatecmdcode.length() > 0)
	    	{
	    		fileupdatecmdcode = fileupdatecmdcode.substring(0,fileupdatecmdcode.length() -1);
	    	}
	    	
	    	for (int i = 0;i < psss.createtablecmdcode.size();i++)
	    	{
	    		String str = (String)psss.createtablecmdcode.get(i);
	    		createtablecmdcode = createtablecmdcode + str +",";
	    	}
	    	
	    	if (createtablecmdcode.length() > 0)
	    	{
	    		createtablecmdcode = createtablecmdcode.substring(0,createtablecmdcode.length() -1);
	    	}
	    	
			txtcmddefcmdcode.setText(cmddefcmdcode);
			txtfileupdatecmdcode.setText(fileupdatecmdcode);
			txtcreatetablecmdcode.setText(createtablecmdcode);
			cmbinputencoder.setText(psss.inputencoder);
			cmboutencoder.setText(psss.outencoder);
		}
	}
	
	/**
	 * 清空所有数据
	 *
	 */
	public void ClearAll()
	{
		ClearForm();
		
		tabFolder.setSelection(0);
		
		tabList.removeAll();
		
		ClearDetail();
	}
	
	/*
	 * 清空文本框录入数据
	 */
	public void ClearForm()
	{
		txttasktime.setText("");
		//txtdayenddef.setText("");
		cmbdayenddef.setText("");
		//txtfileupdatedef.setText("");
		cmbfileupdatedef.setText("");
		txtautodayendtime.setText("");
		//txtlocaldbtype.setText("");
		cmblocaldbtype.setText("");
		txtinspecttime.setText("");
		txtfileupdatestarttime.setText("");
		txtfileupdateendtime.setText("");
	}
	
	/**
	 * 清空明细显示
	 *
	 */
	public void ClearDetail()
	{		
		infoLabel1.setText("");
		cmboutencoder.select(0);
		cmbinputencoder.select(0);
		txtcreatetablecmdcode.setText("");
		txtcmddefcmdcode.setText("");
		txtfileupdatecmdcode.setText("");
		txtdatasoureurl.setText("*");
		cmbdatasourename.setText("");
		//txtdatasouremode.setText("");
		cmbdatasouremode.setText("");
		txtid.setText("");
	}
	
	/**
	 * 
	 * 是否可以下一页
	 */
	protected boolean canContact()
	{
		/*
		Vector v = ((PosServerSysLogic)logic).GetAllConfig();
		if (v == null || v.size() <= 0) return true;
	
		PosServerSysStruct pss = (PosServerSysStruct)v.get(0);
		
		if (!pss.tasktime.equals(txttasktime.getText().trim()))
		{
			isneedwrite = true;
		}
		
		if (!pss.dayenddef.equals(txtdayenddef.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.fileupdatedef.equals(txtfileupdatedef.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.localdbtype.equals(txtlocaldbtype.getText().trim()))
		{
			isneedwrite = true;
		}
		
		if (!pss.dayenddef.equals(cmbdayenddef.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.fileupdatedef.equals(cmbfileupdatedef.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.localdbtype.equals(cmblocaldbtype.getText().trim()))
		{
			isneedwrite = true;
		}
		
		if (!pss.autodayendtime.equals(txtautodayendtime.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.inspecttime.equals(txtinspecttime.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.fileupdatestarttime.equals(txtfileupdatestarttime.getText().trim()))
		{
			isneedwrite = true;
		}
		if (!pss.fileupdateendtime.equals(txtfileupdateendtime.getText().trim()))
		{
			isneedwrite = true;
		}
		
		if (!isneedwrite) return true;
		
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
	     */
	     return true;
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
     * 装载配置文件
     * 
     */
    protected void btnLoadCfgClick()
    {    	
    	logic.SetCfgPath(txtPath.getText().trim());
    	
    	initdata();
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
     * 保存数据到内存
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
     * 写入数据到配置文件
     * @return
     */
    public boolean Write()
	{		
		Vector v = ((PosServerSysLogic)logic).GetAllConfig();
		PosServerSysStruct pss = (PosServerSysStruct)v.get(0);
		
		pss.tasktime = txttasktime.getText().trim();
		//pss.dayenddef = txtdayenddef.getText().trim();
		pss.dayenddef = cmbdayenddef.getText().trim();
		//pss.fileupdatedef = txtfileupdatedef.getText().trim();
		pss.fileupdatedef = cmbfileupdatedef.getText().trim();
		pss.autodayendtime = txtautodayendtime.getText().trim();
		//pss.localdbtype = txtlocaldbtype.getText().trim();
		pss.localdbtype = cmblocaldbtype.getText().trim();
		pss.inspecttime = txtinspecttime.getText().trim();
		pss.fileupdatestarttime = txtfileupdatestarttime.getText().trim();
		pss.fileupdateendtime = txtfileupdateendtime.getText().trim();
		
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
			infoLabel.setText("写入到配置文件成功!");
			//isneedwrite = false;
			return true;
		}
		
		infoLabel.setText("写入到配置文件失败!" + logic.getMsg());
		
		return false;
	}
}
