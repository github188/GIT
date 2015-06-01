package posserver.Configure.CmdDef;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import posserver.Configure.ConfigWizard;
import posserver.Configure.Common.CommonMethod;
import posserver.Configure.Common.Convert;
import posserver.Configure.Common.GlobalVar;
import posserver.Configure.Common.KeyValueStruct;

import com.swtdesigner.SWTResourceManager;

public class CmdDefCfgPage extends WizardPage
{   
    private Text txtOther;
    private Combo cmdFileType;
    private Combo cmbSql_XX_Type;
    private Combo cmbCmd_XX_Mode;
    private Text txtTran_XX_Sql;
    private Combo cmbStartTrans;
    //private Text txtCmd_XX_Mode;
    //private Text txtSql_XX_Type;
    //private Text txtTran_XX_Sql1;
    private Table tabListTran_XX_Col;
    private Table tabListTran_XX_Para;
    private Table tabListCmdText;
    private Combo cmbCmdType;
    private Text txtCmdMemo;
    private Text txtCmdCode;

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
    
    protected Label lbTran_XX_SqlNum;
    protected Label lbParaNum;
    protected Label lbColNum;
    protected Label lbCmdTextNum;
    protected Label lbOther;
    
    protected Button btnAddCmdText;
    protected Button btnDelCmdText;
    protected Button btnAddTran_XX_Para;
    protected Button btnDelTran_XX_Para;
    protected Button btnAddTran_XX_Col;
    protected Button btnDelTran_XX_Col;
    
    //protected Combo cmbmodel_type;
    
    protected int[] currentPoint = new int[] { 6, 0 };
    

    protected TableEditor editor;
    protected TableEditor editorpara;
    protected TableEditor editorcol;
    protected Combo cmbNewEditor;
    protected Text txtNewEditor;
	
    protected Group group;
    
    public CmdDefLogic logic = new CmdDefLogic();
    
    //protected boolean isneedwrite = false;
    
    public static boolean IsInit = true; //该页面是否是初始化
    
    public CmdDefCfgPage()
    {
        super(ConfigWizard.Cmddefcfg, "通讯命令配置", ImageDescriptor.createFromFile(CmdDefCfgPage.class, "q.gif"));
        this.setMessage("手式配置通讯命令!");
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
    	tbiDataSourceList.setText("列表");

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
    	newColumnTableColumn.setWidth(134);
    	newColumnTableColumn.setText("代码");

    	final TableColumn newColumnTableColumn_1 = new TableColumn(tabList, SWT.NONE);
    	newColumnTableColumn_1.setWidth(405);
    	newColumnTableColumn_1.setText("描述");

    	new TableColumn(tabList, SWT.NONE);

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
    	tbiDataSourceDetail.setText("明细");

    	final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
    	tbiDataSourceDetail.setControl(composite_2);

    	final Label label_2 = new Label(composite_2, SWT.NONE);
    	label_2.setBounds(10, 6, 65, 20);
    	label_2.setText("命令代码:");

    	txtCmdCode = new Text(composite_2, SWT.BORDER);
    	txtCmdCode.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	txtCmdCode.setBounds(81, 3, 185, 21);

    	txtCmdMemo = new Text(composite_2, SWT.BORDER);
    	txtCmdMemo.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	txtCmdMemo.setBounds(359, 3, 185, 21);

    	final Label label_2_1 = new Label(composite_2, SWT.NONE);
    	label_2_1.setBounds(284, 6, 62, 20);
    	label_2_1.setText("命令描述:");

    	final Button btnSave = new Button(composite_2, SWT.NONE);
    	btnSave.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnSaveClick();
    		}
    	});
    	
    	btnSave.setBounds(552, 3, 56, 21);
    	btnSave.setText("保存");

    	/*
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
		*/

    	cmbCmdType = new Combo(composite_2, SWT.NONE);
    	cmbCmdType.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	cmbCmdType.setBounds(359, 28, 185, 21);
    	cmbCmdType.setVisibleItemCount(10);
    	cmbCmdType.select(0);
    	
    	for (int i = 0;i <GlobalVar.Cmdtype.size();i++)
    	{
    		cmbCmdType.add((String)GlobalVar.Cmdtype.get(i));
    	}

    	final Label label_2_2 = new Label(composite_2, SWT.NONE);
    	label_2_2.setBounds(284, 30, 65, 20);
    	label_2_2.setText("命令类型:");

    	group = new Group(composite_2, SWT.NONE);
    	group.setText("命令配置");
    	group.setBounds(0, 55, 614, 287);

    	final Label label_2_1_1_1_1_1 = new Label(group, SWT.NONE);
    	label_2_1_1_1_1_1.setBounds(237, 15,67, 20);
    	label_2_1_1_1_1_1.setText("命令模式:");

    	final Label label_2_1_1_1 = new Label(group, SWT.NONE);
    	label_2_1_1_1.setBounds(440, 15,41, 20);
    	label_2_1_1_1.setText("结果集:");

    	final Label label_2_1_1_1_1 = new Label(group, SWT.NONE);
    	label_2_1_1_1_1.setBounds(237, 44,56, 20);
    	label_2_1_1_1_1.setText("Sql语句:");

    	tabListTran_XX_Para = new Table(group, SWT.BORDER);
    	tabListTran_XX_Para.setBounds(237, 87,173, 167);
    	tabListTran_XX_Para.setLinesVisible(true);
    	tabListTran_XX_Para.setHeaderVisible(true);

    	final TableColumn newColumnTableColumn_2_1 = new TableColumn(tabListTran_XX_Para, SWT.NONE);
    	newColumnTableColumn_2_1.setWidth(79);
    	newColumnTableColumn_2_1.setText("输入参数名");

    	final TableColumn newColumnTableColumn_1_1_1 = new TableColumn(tabListTran_XX_Para, SWT.NONE);
    	newColumnTableColumn_1_1_1.setWidth(82);
    	newColumnTableColumn_1_1_1.setText("类型");
    	
    	tabListTran_XX_Para.addMouseListener(new MouseListener()
		{

			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent event)
			{
				CmdDefStruct cds = GetCurCmdDefObject();
				
				if (cds == null) return;
				
				CmdTextStruct cts = GetCurCmdTextObject();
				
				if (cts == null) return;
						
				int indextabListTran_XX_Para = -1;
				
				Point pt = new Point(event.x, event.y);
				int index = tabListTran_XX_Para.getTopIndex();
				boolean done = false;

				while (index < tabListTran_XX_Para.getItemCount())
				{
					final TableItem item = tabListTran_XX_Para.getItem(index);

					for (int i = 0; i < tabListTran_XX_Para.getColumnCount(); i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							indextabListTran_XX_Para = index;
							currentPoint[1] = index;
							currentPoint[0] = i;
							editor = editorpara;
							findLocation(cts.Tran_XX_Para,indextabListTran_XX_Para,tabListTran_XX_Para);
							done = true;
							break;
						}
					}

					if (done) break;

					index++;
				}
			}

			public void mouseUp(MouseEvent e)
			{

			}
		});
		// 生成刚加入tableItem
    	tabListTran_XX_Para.redraw();
		
		editorpara = new TableEditor(tabListTran_XX_Para);
		editorpara.horizontalAlignment = SWT.LEFT;
		editorpara.grabHorizontal = true;
		editorpara.minimumWidth = 50;
		
    	tabListTran_XX_Col = new Table(group, SWT.BORDER);
    	tabListTran_XX_Col.setBounds(431, 87,173, 167);
    	tabListTran_XX_Col.setLinesVisible(true);
    	tabListTran_XX_Col.setHeaderVisible(true);

    	final TableColumn newColumnTableColumn_2_1_1 = new TableColumn(tabListTran_XX_Col, SWT.NONE);
    	newColumnTableColumn_2_1_1.setWidth(78);
    	newColumnTableColumn_2_1_1.setText("输出参数名");

    	final TableColumn newColumnTableColumn_1_1_1_1 = new TableColumn(tabListTran_XX_Col, SWT.NONE);
    	newColumnTableColumn_1_1_1_1.setWidth(85);
    	newColumnTableColumn_1_1_1_1.setText("类型");

    	tabListTran_XX_Col.addMouseListener(new MouseListener()
		{

			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent event)
			{
				CmdDefStruct cds = GetCurCmdDefObject();
				
				if (cds == null) return;
				
				CmdTextStruct cts = GetCurCmdTextObject();
				
				if (cts == null) return;
				
				int indextabListTran_XX_Col = -1;
				
				Point pt = new Point(event.x, event.y);
				int index = tabListTran_XX_Col.getTopIndex();
				boolean done = false;
	
				while (index < tabListTran_XX_Col.getItemCount())
				{
					final TableItem item = tabListTran_XX_Col.getItem(index);
	
					for (int i = 0; i < tabListTran_XX_Col.getColumnCount(); i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							indextabListTran_XX_Col = index;
							currentPoint[1] = index;
							currentPoint[0] = i;
							editor = editorcol;
							findLocation(cts.Tran_XX_Col,indextabListTran_XX_Col,tabListTran_XX_Col);
							done = true;
							break;
						}
					}
	
					if (done) break;
	
					index++;
				}
			}

			public void mouseUp(MouseEvent e)
			{

			}
		});
		// 生成刚加入tableItem
    	tabListTran_XX_Col.redraw();
		
		editorcol = new TableEditor(tabListTran_XX_Col);
		editorcol.horizontalAlignment = SWT.LEFT;
		editorcol.grabHorizontal = true;
		editorcol.minimumWidth = 50;
		
    	btnDelTran_XX_Col = new Button(group, SWT.NONE);
    	btnDelTran_XX_Col.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnDelTran_XX_ColClick();
    		}
    	});
    	btnDelTran_XX_Col.setBounds(460, 258,23, 21);
    	btnDelTran_XX_Col.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnDelTran_XX_Col.setText("-");

    	btnAddTran_XX_Col = new Button(group, SWT.NONE);
    	btnAddTran_XX_Col.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnAddTran_XX_ColClick();
    		}
    	});
    	btnAddTran_XX_Col.setBounds(431, 258,23, 21);
    	btnAddTran_XX_Col.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnAddTran_XX_Col.setText("+");

    	btnDelTran_XX_Para = new Button(group, SWT.NONE);
    	btnDelTran_XX_Para.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnDelTran_XX_ParaClick();
    		}
    	});
    	btnDelTran_XX_Para.setBounds(266, 258,23, 21);
    	btnDelTran_XX_Para.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnDelTran_XX_Para.setText("-");

    	btnAddTran_XX_Para = new Button(group, SWT.NONE);
    	btnAddTran_XX_Para.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnAddTran_XX_ParaClick();
    		}
    	});
    	btnAddTran_XX_Para.setBounds(237, 258,23, 21);
    	btnAddTran_XX_Para.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnAddTran_XX_Para.setText("+");

    	//txtCmd_XX_Mode = new Text(group, SWT.btnAddTran_XX_ParaBORDER);
    	//txtCmd_XX_Mode.setBounds(310, 12,294, 20);

    	//txtSql_XX_Type = new Text(group, SWT.BORDER);
    	//txtSql_XX_Type.setBounds(310, 38,294, 20);

    	tabListCmdText = new Table(group, SWT.FULL_SELECTION | SWT.BORDER);
    	tabListCmdText.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
				if (arg0.item != null)
				{
					TabCmdText_SelectIndexChange((TableItem)arg0.item);
				}
    		}
    	});
    	tabListCmdText.setBounds(10, 15,221, 239);
    	tabListCmdText.setLinesVisible(true);
    	tabListCmdText.setHeaderVisible(true);
    	
    	final TableColumn newColumnTableColumn_2_2 = new TableColumn(tabListCmdText, SWT.NONE);
    	newColumnTableColumn_2_2.setWidth(45);
    	newColumnTableColumn_2_2.setText("序号");

    	final TableColumn newColumnTableColumn_2 = new TableColumn(tabListCmdText, SWT.NONE);
    	newColumnTableColumn_2.setWidth(168);
    	newColumnTableColumn_2.setText(" Sql语句");
		
    	btnDelCmdText = new Button(group, SWT.NONE);
    	btnDelCmdText.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnDelCmdTextClick();
    		}
    	});
    	btnDelCmdText.setBounds(40, 258,23, 21);
    	btnDelCmdText.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnDelCmdText.setText("-");

    	btnAddCmdText = new Button(group, SWT.NONE);
    	btnAddCmdText.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnAddCmdTextClick();
    		}
    	});
    	btnAddCmdText.setBounds(10, 258,23, 21);
    	btnAddCmdText.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	btnAddCmdText.setText("+");

    	txtTran_XX_Sql = new Text(group, SWT.BORDER|SWT.WRAP);
    	txtTran_XX_Sql.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	txtTran_XX_Sql.setBounds(310, 39, 294, 42);

    	lbTran_XX_SqlNum = new Label(group, SWT.NONE);
    	lbTran_XX_SqlNum.setAlignment(SWT.RIGHT);
    	lbTran_XX_SqlNum.setBounds(235, 67, 23, 20);
    	lbTran_XX_SqlNum.setText("0");

    	lbParaNum = new Label(group, SWT.NONE);
    	lbParaNum.setAlignment(SWT.RIGHT);
    	lbParaNum.setText("0");
    	lbParaNum.setBounds(354, 261, 35, 20);

    	lbColNum = new Label(group, SWT.NONE);
    	lbColNum.setAlignment(SWT.RIGHT);
    	lbColNum.setBounds(547, 261, 35, 20);
    	lbColNum.setText("0");

    	lbCmdTextNum = new Label(group, SWT.NONE);
    	lbCmdTextNum.setAlignment(SWT.RIGHT);
    	lbCmdTextNum.setText("0");
    	lbCmdTextNum.setBounds(164, 261, 35, 20);

    	cmbCmd_XX_Mode = new Combo(group, SWT.NONE);
    	cmbCmd_XX_Mode.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	cmbCmd_XX_Mode.setBounds(310, 12, 120, 21);
    	cmbCmd_XX_Mode.setVisibleItemCount(10);
    	cmbCmd_XX_Mode.select(0);

    	for (int i = 0;i <GlobalVar.Cmd_XX_Mode.size();i++)
    	{
    		cmbCmd_XX_Mode.add((String)GlobalVar.Cmd_XX_Mode.get(i));
    	}
    	
    	cmbSql_XX_Type = new Combo(group, SWT.NONE);
    	cmbSql_XX_Type.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	cmbSql_XX_Type.setBounds(482, 12, 122, 21);
    	cmbSql_XX_Type.setVisibleItemCount(10);
    	cmbSql_XX_Type.select(0);

    	final Label label_2_2_1_1 = new Label(group, SWT.NONE);
    	label_2_2_1_1.setBounds(105, 261, 56, 20);
    	label_2_2_1_1.setText("命令数量:");

    	final Label label_2_2_1_2 = new Label(group, SWT.NONE);
    	label_2_2_1_2.setBounds(205, 261, 23, 20);
    	label_2_2_1_2.setText("个");

    	final Label label_2_2_1_1_1 = new Label(group, SWT.NONE);
    	label_2_2_1_1_1.setBounds(295, 261, 56, 20);
    	label_2_2_1_1_1.setText("输入参数:");

    	final Label label_2_2_1_2_1 = new Label(group, SWT.NONE);
    	label_2_2_1_2_1.setBounds(395, 261, 23, 20);
    	label_2_2_1_2_1.setText("个");

    	final Label label_2_2_1_2_1_1 = new Label(group, SWT.NONE);
    	label_2_2_1_2_1_1.setBounds(263, 67, 39, 20);
    	label_2_2_1_2_1_1.setText("个参数");

    	final Label label_2_2_1_1_1_1 = new Label(group, SWT.NONE);
    	label_2_2_1_1_1_1.setBounds(485, 261, 56, 20);
    	label_2_2_1_1_1_1.setText("输入参数:");

    	final Label label_2_2_1_2_1_2 = new Label(group, SWT.NONE);
    	label_2_2_1_2_1_2.setBounds(588, 261, 23, 20);
    	label_2_2_1_2_1_2.setText("个");

    	for (int i = 0;i <GlobalVar.Sql_XX_Type.size();i++)
    	{
    		cmbSql_XX_Type.add((String)GlobalVar.Sql_XX_Type.get(i));
    	}
    	
    	cmbStartTrans = new Combo(composite_2, SWT.NONE);
    	cmbStartTrans.addFocusListener(new FocusAdapter() {
    		public void focusLost(final FocusEvent arg0) 
    		{
    			LostFocus(arg0.widget);
    		}
    	});
    	cmbStartTrans.setBounds(81, 28, 185, 21);
    	cmbStartTrans.setVisibleItemCount(10);
    	cmbStartTrans.select(0);
    	
    	for (int i = 0;i <GlobalVar.Starttrans.size();i++)
    	{
    		cmbStartTrans.add((String)GlobalVar.Starttrans.get(i));
    	}

    	infoLabel1 = new Label(composite_2, SWT.NONE);
    	infoLabel1.setBounds(0, 343,608, 26);
    	infoLabel1.setForeground(SWTResourceManager.getColor(255, 0, 0));

    	txtOther = new Text(composite_2, SWT.BORDER);
    	txtOther.addFocusListener(new FocusAdapter() 
    	{
    		public void focusLost(final FocusEvent arg0)
    		{
    			if (logic.cmdfiletype == 1)
    			{
    				
    			}
    			else if (logic.cmdfiletype == 2)
    			{
    				
    			}
    		}
    	});
    	txtOther.setBounds(81, 28, 185, 21);

    	lbOther = new Label(composite_2, SWT.NONE);
    	lbOther.setBounds(10, 32, 65, 20);
    	lbOther.setText("其他:");

    	final Button btnCheck = new Button(composite_2, SWT.NONE);
    	btnCheck.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			btnCheckClick();
    		}
    	});
    	btnCheck.setBounds(552, 28, 56, 21);
    	btnCheck.setText("检查");

    	//txtTran_XX_Sql1 = new Text(composite_2, SWT.BORDER|SWT.WRAP);
    	//txtTran_XX_Sql1.setBounds(315, 59,136, 30);

    	txtPath = new Text(composite, SWT.BORDER);
    	txtPath.setBounds(155, 10, 338, 21);

    	final Label label_2_3 = new Label(composite, SWT.NONE);
    	label_2_3.setBounds(10, 12, 54, 20);
    	label_2_3.setText("配置文件:");

    	btnOpenDirectory = new Button(composite, SWT.NONE);
    	btnOpenDirectory.setBounds(504, 10, 50, 21);
    	btnOpenDirectory.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			if(CommonMethod.openFileDialog(getShell(),txtPath,new String[]{"*.xml","*.*"},new String[]{"XML Files(*.xml)"}))
    			{	
    				String filename = ConfigWizard.GetFileName(txtPath.getText().trim()) + ".xml";
    				if (filename.equalsIgnoreCase("cmdDef.xml"))
    				{
    					cmdFileType.select(0);
    				}
    				else if(filename.equalsIgnoreCase("fileupdatecmd.xml"))
    				{
    					cmdFileType.select(1);
    				}
    				else if(filename.equalsIgnoreCase("createtablecmd.xml"))
    				{
    					cmdFileType.select(2);
    				}
    				
    				btnLoadCfgClick();
    				
    				cmdFileType_SelectIndexChange();
    			}
    		}
    	});
    	btnOpenDirectory.setText("..");

    	btnLoadCfg = new Button(composite, SWT.NONE);
    	btnLoadCfg.setBounds(556, 10, 60, 21);
    	btnLoadCfg.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0) 
    		{
    			btnLoadCfgClick();
    		}
    	});
    	btnLoadCfg.setText("装载");

    	cmdFileType = new Combo(composite, SWT.READ_ONLY);
    	cmdFileType.addSelectionListener(new SelectionAdapter() 
    	{
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			if (cmdFileType.getSelectionIndex() >= 0)
    			{
    				cmdFileType_SelectIndexChange();
    			}
    		}
    	});
    	cmdFileType.setItems(new String[] {"通讯命令", "更新命令", "日终命令"});
    	cmdFileType.setBounds(65, 10, 80, 21);
    	cmdFileType.select(0);
    	cmdFileType_SelectIndexChange();
    	
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
    
    public void LostFocus(Widget wid)
    {
    	CmdDefStruct cds = this.GetCurCmdDefObject();
    	CmdTextStruct cts = this.GetCurCmdTextObject();
    	
    	if (cds != null)
    	{
    		if (wid.equals(this.txtCmdCode))
        	{
    			if (!logic.CheckKey(txtCmdCode.getText().trim(),tabList.getSelectionIndex()))
    			{
   				 	MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
   				 	msgbox.setMessage(logic.getMsg());
    				msgbox.open();
    				
    				this.txtCmdCode.setText(cds.CmdCode);
    				return;
    			}
    			
        		cds.CmdCode = this.txtCmdCode.getText().trim();
        		
        		return;
        	}
        	if (wid.equals(this.txtCmdMemo))
        	{
        		cds.CmdMemo = this.txtCmdMemo.getText();
        		return;
        	}
        	if (wid.equals(this.cmbStartTrans))
        	{
        		cds.StartTrans = this.cmbStartTrans.getText();
        		return;
        	}
        	if (wid.equals(this.cmbCmdType))
        	{
        		cds.CmdType = this.cmbCmdType.getText();
        		return;
        	}
        	if (wid.equals(this.txtOther))
        	{
        		if (logic.cmdfiletype == 1)
        		{
        			cds.CmdFileName = this.txtOther.getText().trim();
        		}
        		else if (logic.cmdfiletype == 2)
        		{
        			cds.LocalTableName = this.txtOther.getText().trim();
        		}
        	}
    	}

    	if (cts != null)
    	{

        	if (wid.equals(this.cmbCmd_XX_Mode))
        	{
        		cts.Cmd_XX_Mode = this.cmbCmd_XX_Mode.getText();
        		return;
        	}
        	if (wid.equals(this.cmbSql_XX_Type))
        	{
        		cts.Sql_XX_Type = this.cmbSql_XX_Type.getText();
        		return;
        	}
        	if (wid.equals(this.txtTran_XX_Sql))
        	{
        		cts.Tran_XX_Sql = this.txtTran_XX_Sql.getText();
        		RefreshNum();
        		return;
        	}
    	}
    }
    
    public void findLocation(final Vector vdps,final int selectedindex, Table tablist1)
	{		
		if (tablist1.getItemCount() <= 0) { return; }
		
		TableItem items = tablist1.getItem(currentPoint[1]);
		
		if (currentPoint[0] == 0)
		{	 
			txtNewEditor = new Text(tablist1, SWT.LEFT | SWT.BORDER);
			txtNewEditor.addFocusListener(new FocusAdapter() {
	    		public void focusLost(final FocusEvent arg0) 
	    		{
	    			txtNewEditor.setVisible(false);
	    			
	    			CmdParaStruct cps = (CmdParaStruct)(vdps.get(selectedindex));
					
					Text text = (Text) editor.getEditor();
					
					for (int i = 0;i < vdps.size();i++)
					{
						CmdParaStruct cps1 = (CmdParaStruct)(vdps.get(i));
						
						if (cps1.Name.equalsIgnoreCase(text.getText().trim()) && i != selectedindex)
						{
		    				 MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
		    			     msgbox.setMessage("你输入的参数名[" + cps1.Name + "]已存在!");
		    			     msgbox.open();
		    			     return;
						}
					}
					
					cps.Name = text.getText().trim();
					editor.getItem().setText(currentPoint[0], text.getText().trim());
	    		}
	    		
	    		public void focusGained(final FocusEvent arg0) 
	    		{
	    			txtNewEditor.setVisible(true);
	    		}
	    	});
			txtNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(txtNewEditor, items, currentPoint[0]);
			txtNewEditor.selectAll();
			txtNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					
				}
			});

			txtNewEditor.setFocus();
		}
		// table的第一列(参数类型)嵌入combo
		else if (currentPoint[0] == 1)
		{
			cmbNewEditor = new Combo(tablist1, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN);
			cmbNewEditor.setVisibleItemCount(10);
			cmbNewEditor.addFocusListener(new FocusAdapter() {
	    		public void focusLost(final FocusEvent arg0) 
	    		{
	    			cmbNewEditor.setVisible(false);
	    			
	    			CmdParaStruct cps = (CmdParaStruct)(vdps.get(selectedindex));
					
					Combo comb = (Combo) editor.getEditor();
					
					int index = comb.getSelectionIndex();
					String strparatype = "";
					
					if (index >= 0)
					{
						strparatype = ((KeyValueStruct)(GlobalVar.Paradatatype.get(index))).value;
						cps.Type = ((KeyValueStruct)(GlobalVar.Paradatatype.get(index))).key;
						cps.TypeDesc = strparatype;
					}
					else
					{
						strparatype = comb.getText();
						cps.Type = strparatype;
						cps.TypeDesc = strparatype;
					}
					
					editor.getItem().setText(currentPoint[0], strparatype);
	    		}
	    		
	    		public void focusGained(final FocusEvent arg0) 
	    		{
					Combo comb = (Combo) editor.getEditor();
					
					String str = comb.getText();
					
					//解决获得选择项的问题，他不会自动选择相同的项目
					for(int i = 0;i < comb.getItemCount();i++)
					{
						if (comb.getItem(i).equalsIgnoreCase(str))
						{
							comb.select(i);
							break;
						}
					}
					
	    			cmbNewEditor.setVisible(true);
	    		}
	    	});
			for (int i = 0; i < GlobalVar.Paradatatype.size(); i++)
			{
				KeyValueStruct kvs = (KeyValueStruct)(GlobalVar.Paradatatype.get(i));
				cmbNewEditor.add(kvs.value);
			}
			cmbNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(cmbNewEditor, items, currentPoint[0]);

			cmbNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					
				}
			});

			cmbNewEditor.setFocus();
		}
	}
    
    public IWizardPage getNextPage()
    {
    	if (CmdDefCfgPage.IsInit)
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
    	
    	CmdDefCfgPage.IsInit = false;

    	return  super.getNextPage();
    }

    public IWizardPage getPreviousPage()
    {	   
    	/*
    	if (!ServerJBoss422GACfgPage.IsInit)
    	{
    		if (!canContact()) return this;
    	}
    	*/
    	
    	CmdDefCfgPage.IsInit = true;
    	
    	return super.getPreviousPage();
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
		
		//RefreshCmbModelType();
		
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
     * cmdFileType选择改变
     */
	public void cmdFileType_SelectIndexChange()
    {
		logic.cmdfiletype = cmdFileType.getSelectionIndex();
		
		if (logic.cmdfiletype == 0)
    	{
			lbOther.setText("启用事务:");
    		txtOther.setVisible(false);
    		cmbStartTrans.setVisible(true);
    		
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("cmddef.xml", "cmddef.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("createtablecmd.xml", "cmddef.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("fileupdatecmd.xml", "cmddef.xml"));
    	}
    	else if(logic.cmdfiletype == 1)
    	{
    		lbOther.setText("命令文件:");
    		txtOther.setVisible(true);
    		cmbStartTrans.setVisible(false);
    		txtOther.setText("");
    		
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("cmddef.xml", "fileupdatecmd.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("createtablecmd.xml", "fileupdatecmd.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("fileupdatecmd.xml", "fileupdatecmd.xml"));
    	}
    	else if(logic.cmdfiletype == 2)
    	{
    		lbOther.setText("本地库名:");
    		txtOther.setVisible(true);
    		cmbStartTrans.setVisible(false);
    		txtOther.setText("");
    		
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("cmddef.xml", "createtablecmd.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("createtablecmd.xml", "createtablecmd.xml"));
    		logic.SetCfgPath(logic.GetCfgPath().toLowerCase().replace("fileupdatecmd.xml", "createtablecmd.xml"));
    	}
		
		txtPath.setText(logic.GetCfgPath());
		
		CmdDefStruct cds = GetCurCmdDefObject();
		 
		RefreshTabDetail(cds);
    }
    
    /**
	 * TabFload选择改变
	 * @param item 选择页
	 */
	public void TabFolder_SelectIndexChange(TabItem item)
	{
		if(item.equals(tbiDataSourceDetail))
		{
			//cmbmodel_type.select(0);
			
			CmdDefStruct cds = GetCurCmdDefObject();
 
			RefreshTabDetail(cds);
		}
	}
	
	public void TabCmdText_SelectIndexChange(TableItem item)
	{
		CmdTextStruct cts = null;
		
		cts = GetCurCmdTextObject();

		RefreshDetailCmdText(cts);
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
				
		if (txtCmdCode.getText().trim().equals(""))
		{
			infoLabel1.setText("保存失败!" + "请输入命令代码!");
			return ;
		}
		
		if (!logic.CheckSave(txtCmdCode.getText().trim(), tabList.getSelectionIndex()))
		{
			infoLabel1.setText(logic.getMsg());
			return;
		}
		
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null)
		{
			// 保存新增
			cds = new CmdDefStruct();
			logic.GetConfig().add(cds);
		}
		
		cds.CmdCode = txtCmdCode.getText().trim();
		cds.CmdMemo = txtCmdMemo.getText().trim();
		cds.CmdType = cmbCmdType.getText().trim();
		//sjs.CmdTran = txtCmdTran.getText().trim();
		
		if (logic.cmdfiletype == 0)
		{
			cds.StartTrans = cmbStartTrans.getText().trim();
		}
		if (logic.cmdfiletype == 1)
		{
			cds.CmdFileName = this.txtOther.getText().trim();
		}
		else if (logic.cmdfiletype == 2)
		{
			cds.LocalTableName = this.txtOther.getText().trim();
		}
		
		RefreshTabList();
		
		tabList.select(logic.GetConfig().indexOf(cds));
		tabList.showSelection();
		
		Write();
		
		setEnable();
		
		// 需要保存
		//isneedwrite = true;
		
		//this.infoLabel1.setText("保存成功,请点击写入将修改的信息写入配置文件!");
	}
	
	/**
	 * 检查
	 *
	 */
	public void btnCheckClick()
	{
		CmdTextStruct cts = GetCurCmdTextObject();
		
		if (cts == null) return;
		
		boolean updatecmd = false;
		if (cmdFileType.getSelectionIndex() == 1) updatecmd = true;
		FrmSqlParaCheck frm = new FrmSqlParaCheck();
		frm.open(cts,getShell(),updatecmd);
		
		this.RefreshDetailCmdText(cts);
		
		/*
		//jdbc:oracle:thin:@172.17.6.113:1521:pos
		//oracle.jdbc.driver.OracleDriver
		//dbusrpos
		//futurepos
		Connection conn = null;
		try
		{
			 Class.forName("oracle.jdbc.driver.OracleDriver");
			 conn = DriverManager.getConnection("jdbc:oracle:thin:@172.17.6.113:1521:pos","dbusrpos","futurepos");
			 
			 //Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			 //Connection conn = DriverManager.getConnection("jdbc:microsoft:sqlserver://172.17.6.62:1433;DatabaseName=JavaPos_MyShop;SelectMethod=Cursor","sa","sa");

			 if (conn != null)
	         {
	        	 DatabaseMetaData dbmd = conn.getMetaData(); 
	        	 //ResultSet rs = dbmd.getProcedures(null,null,"JAVA_DELNEWS");
	        	 ResultSet rs = dbmd.getProcedureColumns(null, null, "JAVA_FINDGOODS", "%");
	        	 
	        	 while(rs.next())
	        	 {
	                 String dbColumnName0 = rs.getString("COLUMN_NAME");
	                 String dbColumnName1 = rs.getString("TYPE_NAME");
	                 int dbColumnName2 = rs.getInt("DATA_TYPE");
	                 String dbColumnName3 = rs.getString("COLUMN_TYPE");
	                 
	                 switch(dbColumnName2)
	                 {
	                	 case java.sql.Types.INTEGER:
	                		 break;
	                 }
	                 
	                 txtTran_XX_Sql.setText(txtTran_XX_Sql.getText() + "\r\n" + dbColumnName0 + "," + dbColumnName1 + "," + dbColumnName2 + "," + dbColumnName3);
	                 
	        		 infoLabel1.setText("存储过程查找成功!");
	        	 }
	        	 
	        	 
	         	//conn.close();
	         	//infoLabel1.setText("数据库连接成功!");
	         }
	         else
	         {
	         	infoLabel1.setText("数据库连接失败!");
	         }	   
		}
		catch(Exception ex)
		{
			infoLabel1.setText(ex.getMessage());
		}
		finally
		{
			try
			{
				if (conn != null) conn.close();
			}
			catch(Exception ex)
			{
				infoLabel1.setText(ex.getMessage());
			}
		}
		//logic.
		*/
	}
	
	public void btnAddTran_XX_ColClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;
		
		CmdTextStruct cts = GetCurCmdTextObject();
		
		if (cts == null) return;
		
		int indextabListTran_XX_Col = tabListTran_XX_Col.getSelectionIndex();
		
		if (indextabListTran_XX_Col < 0)
		{
			indextabListTran_XX_Col = cts.Tran_XX_Col.size();
		}
		else
		{
			indextabListTran_XX_Col++;
		}
		
		CmdParaStruct cps = new CmdParaStruct();
		
		int i = 0;
		while(true)
		{
			int j = 0;
			for (j = 0;j < cts.Tran_XX_Col.size();j++)
			{
				if (((CmdParaStruct)(cts.Tran_XX_Col.get(j))).Name.equalsIgnoreCase("NewCol" + String.valueOf(i)))
				{
					break;
				}
			}
			
			if (j >= cts.Tran_XX_Col.size())
			{
				cps.Name = "NewCol" + String.valueOf(i);
				break;
			}
			
			i++;
		}
		cps.Type = "";
		
		cps.TypeDesc = "";
		
		cts.Tran_XX_Col.add(indextabListTran_XX_Col,cps);
		
		this.tabListTran_XX_Col.removeAll();
		for (int k = 0;k < cts.Tran_XX_Col.size();k++)
		{
			CmdParaStruct cps1 = (CmdParaStruct)(cts.Tran_XX_Col.get(k));
			TableItem ti = new TableItem(tabListTran_XX_Col,SWT.NONE);
			ti.setText(new String[]{cps1.Name,cps1.TypeDesc});
		}
		
		RefreshNum();
		
		tabListTran_XX_Col.select(indextabListTran_XX_Col);
		tabListTran_XX_Col.showSelection();
	}
	
	public void btnDelTran_XX_ColClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;
		
		CmdTextStruct cts = GetCurCmdTextObject();
		
		if (cts == null) return;

		int indextabListTran_XX_Col = tabListTran_XX_Col.getSelectionIndex();
		
		if (indextabListTran_XX_Col < 0) return;
		
		cts.Tran_XX_Col.remove(indextabListTran_XX_Col);
		
		this.tabListTran_XX_Col.removeAll();
		for (int k = 0;k < cts.Tran_XX_Col.size();k++)
		{
			CmdParaStruct cps1 = (CmdParaStruct)(cts.Tran_XX_Col.get(k));
			TableItem ti = new TableItem(tabListTran_XX_Col,SWT.NONE);
			ti.setText(new String[]{cps1.Name,cps1.TypeDesc});
		}
		
		if (cts.Tran_XX_Col.size() > 0)
		{
			if (cts.Tran_XX_Col.size() > indextabListTran_XX_Col)
			{
				tabListTran_XX_Col.select(indextabListTran_XX_Col);
			}
			else if (cts.Tran_XX_Col.size() == indextabListTran_XX_Col)
			{
				tabListTran_XX_Col.select(indextabListTran_XX_Col -1);
			}
		}
		
		RefreshNum();
		
		tabListTran_XX_Col.showSelection();
	}
	
	public void btnAddTran_XX_ParaClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;
		
		CmdTextStruct cts = GetCurCmdTextObject();
		
		if (cts == null) return;
		
		int indextabListTran_XX_Para = tabListTran_XX_Para.getSelectionIndex();
		
		if (indextabListTran_XX_Para < 0)
		{
			indextabListTran_XX_Para = cts.Tran_XX_Para.size();
		}
		else
		{
			indextabListTran_XX_Para++;
		}
		
		CmdParaStruct cps = new CmdParaStruct();
		
		int i = 0;
		while(true)
		{
			int j = 0;
			for (j = 0;j < cts.Tran_XX_Para.size();j++)
			{
				if (((CmdParaStruct)(cts.Tran_XX_Para.get(j))).Name.equalsIgnoreCase("NewPara" + String.valueOf(i)))
				{
					break;
				}
			}
			
			if (j >= cts.Tran_XX_Para.size())
			{
				cps.Name = "NewPara" + String.valueOf(i);
				break;
			}
			
			i++;
		}
		cps.Type = "";
		
		cps.TypeDesc = "";
		
		cts.Tran_XX_Para.add(indextabListTran_XX_Para,cps);
		
		this.tabListTran_XX_Para.removeAll();
		for (int k = 0;k < cts.Tran_XX_Para.size();k++)
		{
			CmdParaStruct cps1 = (CmdParaStruct)(cts.Tran_XX_Para.get(k));
			TableItem ti = new TableItem(tabListTran_XX_Para,SWT.NONE);
			ti.setText(new String[]{cps1.Name,cps1.TypeDesc});
		}
		
		RefreshNum();
		
		tabListTran_XX_Para.select(indextabListTran_XX_Para);
		tabListTran_XX_Para.showSelection();
	}
	
	public void btnDelTran_XX_ParaClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;
		
		CmdTextStruct cts = GetCurCmdTextObject();
		
		if (cts == null) return;

		int indextabListTran_XX_Para = tabListTran_XX_Para.getSelectionIndex();
		
		if (indextabListTran_XX_Para < 0) return;
		
		cts.Tran_XX_Para.remove(indextabListTran_XX_Para);
		
		this.tabListTran_XX_Para.removeAll();
		for (int k = 0;k < cts.Tran_XX_Para.size();k++)
		{
			CmdParaStruct cps1 = (CmdParaStruct)(cts.Tran_XX_Para.get(k));
			TableItem ti = new TableItem(tabListTran_XX_Para,SWT.NONE);
			ti.setText(new String[]{cps1.Name,cps1.TypeDesc});
		}
		
		if (cts.Tran_XX_Para.size() > 0)
		{
			if (cts.Tran_XX_Para.size() > indextabListTran_XX_Para)
			{
				tabListTran_XX_Para.select(indextabListTran_XX_Para);
			}
			else if (cts.Tran_XX_Para.size() == indextabListTran_XX_Para)
			{
				tabListTran_XX_Para.select(indextabListTran_XX_Para -1);
			}
		}
		
		RefreshNum();
		
		tabListTran_XX_Para.showSelection();
	}
	
	public void btnAddCmdTextClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;

		int indextabListCmdText = tabListCmdText.getSelectionIndex();
		
		if (indextabListCmdText < 0)
		{
			indextabListCmdText = cds.CmdText.size();
		}
		else
		{
			indextabListCmdText++;
		}
		
		CmdTextStruct cts = new CmdTextStruct();
		cts.Tran_XX_Sql = "{call proc(?,?,?,?,?,?)}";
		cds.CmdText.add(indextabListCmdText,cts);
		
		this.RefreshTabListCmdText(cds);
		
		tabListCmdText.select(indextabListCmdText);
		
		tabListCmdText.showSelection();
		
		this.RefreshDetailCmdText(cts);
		
		RefreshNum();
	}
	
	public void btnDelCmdTextClick()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		
		if (cds == null) return;
		
		int indextabListCmdText = tabListCmdText.getSelectionIndex();
		
		if (indextabListCmdText < 0)
		{
			return;
		}
		
		cds.CmdText.remove(indextabListCmdText);
		CmdTextStruct cts = null;
		
		this.RefreshTabListCmdText(cds);
		
		if (cds.CmdText.size() > 0)
		{
			if (cds.CmdText.size() > indextabListCmdText)
			{
				tabListCmdText.select(indextabListCmdText);
				cts = (CmdTextStruct)(cds.CmdText.get(indextabListCmdText));
			}
			else if(cds.CmdText.size() == indextabListCmdText)
			{
				tabListCmdText.select(indextabListCmdText - 1);
				cts = (CmdTextStruct)(cds.CmdText.get(indextabListCmdText - 1));
			}
				
		}
		
		this.RefreshDetailCmdText(cts);
		
		RefreshNum();
		
		tabListCmdText.showSelection();
	}
	
	/**
	 * 
	 */
	public void btnDelClick()
	{
		if (tabList.getSelectionIndex() < 0) return;
		CmdDefStruct cmd = (CmdDefStruct)logic.GetConfig().get(tabList.getSelectionIndex());
		MessageBox msgbox=new MessageBox(new Shell(),SWT.OK|SWT.CANCEL);
	    msgbox.setMessage("是否要删除代码为[" + cmd.CmdCode + "]的命令配置信息!");
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
	 * 刷新ModelType
	 * 
	 */
	/*
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
	*/
	
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
			ti.setText(new String[]{((CmdDefStruct)logic.GetConfig().get(i)).CmdCode,((CmdDefStruct)logic.GetConfig().get(i)).CmdMemo});
		}
	}
	
	public void RefreshTabListCmdText(Object obj)
	{
		tabListCmdText.removeAll();
		CmdDefStruct cds = (CmdDefStruct)obj;
		for (int i = 0;i < cds.CmdText.size() ; i++)
		{
			CmdTextStruct cts = (CmdTextStruct)(cds.CmdText.get(i));
			
			String strnum = Convert.increaseCharForward(String.valueOf(i+1), '0', 2);
			
			TableItem ti = new TableItem(tabListCmdText,SWT.NONE);
			
			ti.setText(new String[] {strnum,cts.Tran_XX_Sql});
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
			CmdDefStruct cds = (CmdDefStruct)obj;
			
			infoLabel1.setText("");
			
			this.txtCmdCode.setText(cds.CmdCode);
			this.txtCmdMemo.setText(cds.CmdMemo);
			//this.txtCmdTran.setText(cds.CmdTran);
			this.cmbCmdType.setText(cds.CmdType);
			this.cmbStartTrans.setText(cds.StartTrans);
			
			if (logic.cmdfiletype == 1)
    		{
				this.txtOther.setText(cds.CmdFileName);
    		}
    		else if (logic.cmdfiletype == 2)
    		{
    			this.txtOther.setText(cds.LocalTableName);
    		}
			
			RefreshTabListCmdText(cds);
			
			tabListCmdText.select(0);
			
			CmdTextStruct cts = null;
			int cmdtextindex = tabListCmdText.getSelectionIndex();

			if (cmdtextindex >= 0)
			{
				cts = (CmdTextStruct)(cds.CmdText.get(cmdtextindex));
			}
			
			this.RefreshDetailCmdText(cts);
		}
	    
		RefreshNum();
		
		setEnable();
	}
	
	protected void setEnable()
	{
		boolean bool = true;
		if (tabList.getSelectionIndex()>= 0)
		{
			bool = true;
		}
		else
		{
			bool = false;
		}
		
		this.btnAddCmdText.setEnabled(bool);
		this.btnDelCmdText.setEnabled(bool);
		this.btnAddTran_XX_Para.setEnabled(bool);
		this.btnDelTran_XX_Para.setEnabled(bool);
		this.btnAddTran_XX_Col.setEnabled(bool);
		this.btnDelTran_XX_Col.setEnabled(bool);
		
		this.tabListCmdText.setEnabled(bool);
		this.tabListTran_XX_Para.setEnabled(bool);
		this.tabListTran_XX_Col.setEnabled(bool);
		
		this.cmbCmd_XX_Mode.setEnabled(bool);
		this.cmbSql_XX_Type.setEnabled(bool);
		this.txtTran_XX_Sql.setEnabled(bool);
	}
	
	/**
	 * 刷新CmdText数据明细
	 */
	protected void RefreshDetailCmdText(Object obj)
	{
		infoLabel1.setText("");
		
	    if (obj == null)
		{
			ClearDetailCmdText();
		}
		else
		{
			CmdTextStruct cts = (CmdTextStruct)obj;
			
			infoLabel1.setText("");
			
			this.cmbCmd_XX_Mode.setText(cts.Cmd_XX_Mode);
			this.cmbSql_XX_Type.setText(cts.Sql_XX_Type);
			this.txtTran_XX_Sql.setText(cts.Tran_XX_Sql);
			
			this.tabListTran_XX_Para.removeAll();
			for (int i = 0;i < cts.Tran_XX_Para.size();i++)
			{
				CmdParaStruct cps = (CmdParaStruct)(cts.Tran_XX_Para.get(i));
				TableItem ti = new TableItem(tabListTran_XX_Para,SWT.NONE);
				ti.setText(new String[]{cps.Name,cps.TypeDesc});
			}
			
			this.tabListTran_XX_Col.removeAll();
			for (int i = 0;i < cts.Tran_XX_Col.size();i++)
			{
				CmdParaStruct cps = (CmdParaStruct)(cts.Tran_XX_Col.get(i));
				TableItem ti = new TableItem(tabListTran_XX_Col,SWT.NONE);
				ti.setText(new String[]{cps.Name,cps.TypeDesc});
			
			}
		}
	    
	    RefreshNum();
	}
	
	/**
	 * 清空明细显示
	 *
	 */
	public void ClearDetail()
	{
		infoLabel1.setText("");
		
		txtCmdMemo.setText("");
		txtCmdCode.setText("");
		cmbStartTrans.setText("");
		cmbCmdType.setText("");
		
		tabListCmdText.removeAll();
		
		ClearDetailCmdText();
	}
	
	/**
	 * 清空CmdText明细显示
	 *
	 */
	public void ClearDetailCmdText()
	{
		infoLabel1.setText("");
		
		cmbCmd_XX_Mode.setText("");
		cmbSql_XX_Type.setText("");
		txtTran_XX_Sql.setText("");
		
		tabListTran_XX_Para.removeAll();
		tabListTran_XX_Col.removeAll();
	}
	
	/*
	 * 刷新数量
	 */
	public void RefreshNum()
	{
		int indextablist = tabList.getSelectionIndex();
		if (indextablist < 0)
		{
			lbTran_XX_SqlNum.setText("0");
			lbParaNum.setText("0");
			lbColNum.setText("0");
			lbCmdTextNum.setText("0");
			return;
		}
		
		CmdDefStruct cds = (CmdDefStruct)logic.GetConfig().get(indextablist);
		lbCmdTextNum.setText(String.valueOf(cds.CmdText.size()));
		
		int indextabcmdlist = tabListCmdText.getSelectionIndex();
		if (indextabcmdlist < 0) 
		{
			lbTran_XX_SqlNum.setText("0");
			lbParaNum.setText("0");
			lbColNum.setText("0");
			return;
		}

		CmdTextStruct cts = (CmdTextStruct)(cds.CmdText.get(indextabcmdlist));
		
		int count = 0;
		StringBuffer sb = new StringBuffer(cts.Tran_XX_Sql);
		for(int i = 0;i < sb.length();i++)
		{
			if (sb.charAt(i) == '?')
			{
				count ++;
			}
		}
		
		lbTran_XX_SqlNum.setText(String.valueOf(count));
		lbParaNum.setText(String.valueOf(cts.Tran_XX_Para.size()));
		lbColNum.setText(String.valueOf(cts.Tran_XX_Col.size()));
	}
	
	public CmdDefStruct GetCurCmdDefObject()
	{
		int indextablist = tabList.getSelectionIndex();
		
		if (indextablist < 0)
		{
			return null;
		}
			
		return (CmdDefStruct)logic.GetConfig().get(indextablist);
	}
	
	public CmdTextStruct GetCurCmdTextObject()
	{
		CmdDefStruct cds = GetCurCmdDefObject();
		if (cds == null) return null;
		
		
		int indextabListCmdText = this.tabListCmdText.getSelectionIndex();
		
		if (indextabListCmdText < 0)
		{
			return null;
		}
			
		return (CmdTextStruct)cds.CmdText.get(indextabListCmdText);
	}
}
