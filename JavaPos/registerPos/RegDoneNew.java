package registerPos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xvolks.jnative.misc.SHELLEXECUTEINFO;
import org.xvolks.jnative.util.Shell32;
import org.xvolks.jnative.util.constants.winuser.WindowsConstants;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.swtdesigner.SWTResourceManager;

public class RegDoneNew
{
	private Text textGrantInfo;
	private Table tbGrant;
	private Table tbProj;
	private Text textProjName;
	private Text textProjCode;
	private Button chkbtn;
	public Text textRegDate;
	public Text textMaxDate;
	public Text textRegCode;
	public Text textModul;
	public Text textMarket;
	public Text textCustomer;
	public Spinner spinnerStart;
	public Spinner spinnerEnd;
	public Button chkbtn_1;
	public Button chkbtn_2;
	public Button chkbtn_4;
	public Button chkbtn_8;
	public Button chkbtn_16;
	protected Shell shell;
	public String dbpath = ".\\registerPos\\RegDone.db3";
	public String lastprojcode = "";
	public String lastprojname = "";
	private Sqldb sql;
	private boolean tempReg = false;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{			
			// 设置时区
			System.setProperty("user.timezone","Asia/Shanghai");
			
			RegDoneNew window = new RegDoneNew();
			
			if (args != null && args.length > 0) window.dbpath = args[0].trim();
			
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
				
		// 连接数据库
		ConfigClass.MouseMode = true;
		shell.setText(dbpath);
		sql = new Sqldb("org.sqlite.JDBC","jdbc:sqlite:" + dbpath);
		if (!sql.isOpen())
		{
			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
			mb.setMessage("授权数据库 " + dbpath + " 打开失败!");
			mb.open();
		}
		// 刷新项目列表
		refushTable(null,"code");
		
		ManipulateDateTime dt = new ManipulateDateTime();
		textMaxDate.setText(dt.skipDate(dt.getDateBySign(),365).replace('/', '-')+",15");
		
		chkbtn_1.setText(ManipulatePrecision.registerFunction[0][1]);
		if (chkbtn_1.getText().equals("")) chkbtn_1.setSelection(true);
		chkbtn_2.setText(ManipulatePrecision.registerFunction[1][1]);
		if (chkbtn_2.getText().equals("")) chkbtn_2.setSelection(true);
		chkbtn_4.setText(ManipulatePrecision.registerFunction[2][1]);
		if (chkbtn_4.getText().equals("")) chkbtn_4.setSelection(true);
		chkbtn_8.setText(ManipulatePrecision.registerFunction[3][1]);
		if (chkbtn_8.getText().equals("")) chkbtn_8.setSelection(true);
		chkbtn_16.setText(ManipulatePrecision.registerFunction[4][1]);
		if (chkbtn_16.getText().equals("")) chkbtn_16.setSelection(true);

		calcRegisterFunction();
		
        Rectangle rec = Display.getCurrent().getClientArea();
        shell.setLocation((rec.width - shell.getSize().x) / 2,
                           (rec.height - shell.getSize().y)/ 2);
        
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	protected void calcRegisterFunction()
	{
		int seq = 32;
		
		if (chkbtn_1.getSelection()) seq = seq | 1;
		if (chkbtn_2.getSelection()) seq = seq | 2;
		if (chkbtn_4.getSelection()) seq = seq | 4;
		if (chkbtn_8.getSelection()) seq = seq | 8;
		if (chkbtn_16.getSelection()) seq = seq | 16;
		
		seq *= 1000;seq += 1;
		spinnerStart.setSelection(seq);
		spinnerEnd.setSelection(seq);
	}
	
	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.BORDER);
		shell.setSize(900, 600);
		shell.setText("生成注册码和有效期");

		final Button buttonClose = new Button(shell, SWT.NONE);
		buttonClose.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				shell.close();
				shell.dispose();
			}
		});
		buttonClose.setBounds(10, 530, 91, 28);
		buttonClose.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonClose.setText("关闭本窗口");

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(359, 225, 525, 299);

		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 25,70, 20);
		label.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label.setText("门店名称:");
		
		textCustomer = new Text(group, SWT.BORDER);
		textCustomer.setBounds(81, 22,336, 25);
		textCustomer.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		textCustomer.setText("生成试用注册码,请在本栏填空");

		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setBounds(10, 59,70, 20);
		label_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1.setText("门店编号:");

		textMarket = new Text(group, SWT.BORDER);
		textMarket.setBounds(81, 55,336, 25);
		textMarket.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		textMarket.setText("生成试用注册码,请在本栏填写试用期(YYYYMMDD)");
		
		final Label label_1_1 = new Label(group, SWT.NONE);
		label_1_1.setBounds(435, 25,70, 20);
		label_1_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_1.setText("客户模块");

		textModul = new Text(group, SWT.CENTER | SWT.BORDER);
		textModul.setBounds(423, 55,91, 25);
		textModul.setTextLimit(4);
		textModul.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Label label_1_2 = new Label(group, SWT.NONE);
		label_1_2.setBounds(10, 194,70, 20);
		label_1_2.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_2.setText("注册号码:");

		textRegCode = new Text(group, SWT.READ_ONLY | SWT.BORDER);
		textRegCode.setBounds(81, 192,336, 25);
		textRegCode.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Label label_1_1_1 = new Label(group, SWT.NONE);
		label_1_1_1.setBounds(10, 159,70, 20);
		label_1_1_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_1_1.setText("起止序号:");

		spinnerStart = new Spinner(group, SWT.BORDER);
		spinnerStart.setBounds(81, 156,164, 25);
		spinnerStart.setMaximum(99999);
		spinnerStart.setSelection(1);
		spinnerStart.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		spinnerEnd = new Spinner(group, SWT.BORDER);
		spinnerEnd.setBounds(251, 156,166, 25);
		spinnerEnd.setMaximum(99999);
		spinnerEnd.setSelection(1);
		spinnerEnd.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Label label_1_3 = new Label(group, SWT.NONE);
		label_1_3.setBounds(10, 230,70, 20);
		label_1_3.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_3.setText("有效期值:");

		textMaxDate = new Text(group, SWT.BORDER);
		textMaxDate.setBounds(81, 228,247, 25);
		textMaxDate.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Label label_1_2_1 = new Label(group, SWT.NONE);
		label_1_2_1.setBounds(10, 267,70, 20);
		label_1_2_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_2_1.setText("有效期串:");

		textRegDate = new Text(group, SWT.BORDER);
		textRegDate.setText("有效期年月日,提前N天提醒(YYYY-MM-DD,N)");
		textRegDate.setBounds(81, 263,336, 25);
		textRegDate.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Label label_1_1_2 = new Label(group, SWT.NONE);
		label_1_1_2.setBounds(10, 105,70, 20);
		label_1_1_2.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_1_2.setText("模块授权:");

		chkbtn_1 = new Button(group, SWT.CHECK);
		chkbtn_1.setBounds(81, 88,109, 20);
		chkbtn_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		chkbtn_1.setText("功能模块");

		chkbtn_2 = new Button(group, SWT.CHECK);
		chkbtn_2.setBounds(195, 88,109, 20);
		chkbtn_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});		
		chkbtn_2.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		chkbtn_2.setText("功能模块");

		chkbtn_4 = new Button(group, SWT.CHECK);
		chkbtn_4.setBounds(81, 124,109, 20);
		chkbtn_4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_4.setText("功能模块");
		chkbtn_4.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		chkbtn_8 = new Button(group, SWT.CHECK);
		chkbtn_8.setBounds(195, 124,109, 20);
		chkbtn_8.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_8.setText("功能模块");
		chkbtn_8.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		chkbtn_16 = new Button(group, SWT.CHECK);
		chkbtn_16.setBounds(308, 124,109, 20);
		chkbtn_16.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_16.setText("功能模块");
		chkbtn_16.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		chkbtn = new Button(group, SWT.CHECK);
		chkbtn.setBounds(334, 231,81, 20);
		chkbtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (((Button)(arg0.widget)).getSelection()) textMaxDate.setText("2100-01-01,15");
				else 
				{
					ManipulateDateTime dt = new ManipulateDateTime();
					textMaxDate.setText(dt.skipDate(dt.getDateBySign(),365).replace('/', '-')+",15");
				}
				textRegDate.setText("");
			}
		});
		chkbtn.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		chkbtn.setText("永久有效");
		
		final Button buttonRegDate = new Button(group, SWT.NONE);
		buttonRegDate.setBounds(423, 261,91, 28);
		buttonRegDate.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonRegDate.setText("生成有效期");
		buttonRegDate.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				String regcode,strMaxDate,strKey;
				
				strMaxDate = textMaxDate.getText().trim();
				strKey = textModul.getText().trim();
				//strCustomer = textCustomer.getText().trim();
				//strMarket = textMarket.getText().trim();
				//strKey = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,"99999",strKey);
					
				if (strKey == null || strKey.length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("模块编号不能为空");
					mb.open();
					return;
				}
				
				regcode = ManipulatePrecision.EncodeString(strMaxDate, strKey);
				textRegDate.setText(regcode);
			}
		});

		final Button buttonUnDate = new Button(group, SWT.NONE);
		buttonUnDate.setBounds(423, 224,91, 28);
		buttonUnDate.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonUnDate.setText("解密有效期");
		buttonUnDate.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				String regcode,strMaxDate,strKey;
				
				regcode = textRegDate.getText().trim();
				strKey = textModul.getText().trim();
				//String strCustomer = textCustomer.getText().trim();
				//String strMarket = textMarket.getText().trim();
				//strKey = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,"99999",strKey);

				if (strKey == null || strKey.length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("模块编号不能为空");
					mb.open();
					return;
				}
				
				strMaxDate = ManipulatePrecision.DecodeString(regcode, strKey);
				textMaxDate.setText(strMaxDate);
			}
		});
		
		final Button buttonRegCode = new Button(group, SWT.NONE);
		buttonRegCode.setBounds(423, 189,91, 28);
		buttonRegCode.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				String regcode,strCustomer,strMarket,strKey;
				
				strCustomer = textCustomer.getText().trim();
				strMarket = textMarket.getText().trim();
				strKey = textModul.getText().trim();
				
				if (strKey.length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
					mb.setMessage("客户模块为空,你确定是要以空模块号生成注册码吗!");
					if (mb.open() != SWT.YES) return;
				}
				
				if (spinnerEnd.getSelection() == spinnerStart.getSelection())
				{
					regcode = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,String.valueOf(spinnerStart.getSelection()),strKey);
					
					// 临时注册码,strMarket = 有效期(YYYYMMDD)
					if (strCustomer == null || strCustomer.length() <= 0)
					{
						regcode += "-" + strMarket;
					}
					
					textRegCode.setText(regcode);
				}
				else
				{
					textRegCode.setText("正在生成批量注册码,请等待");
					Display.getCurrent().update();
					
					try
					{
						java.io.FileWriter f = new java.io.FileWriter("regcode.txt");
						
						for(int i=spinnerStart.getSelection();i<=spinnerEnd.getSelection();i++)
						{
							regcode = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,String.valueOf(i),strKey);
							
							// 临时注册码,strMarket = 有效期(YYYYMMDD)
							if (strCustomer == null || strCustomer.length() <= 0)
							{
								regcode += "-" + strMarket;
							}
							
							f.write(regcode);
							f.write("\r\n");
						}
						
						f.close();
						
						//
						textRegCode.setText("生成批量注册码成功,请查看 regcode.txt");
						Display.getCurrent().update();
						
						//
						Runtime.getRuntime().exec("notepad regcode.txt");
					}
					catch(Exception ex)
					{
						textRegCode.setText("生成批量注册码失败!" + ex.getMessage());
					}
				}
			}
		});
		buttonRegCode.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonRegCode.setText("生成注册码");

		final Button buttonSaveInfo = new Button(group, SWT.NONE);
		buttonSaveInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				int index = tbGrant.getSelectionIndex();
				if (index < 0) return;
				TableItem item = tbGrant.getItem(index);
				item.setText(0,textCustomer.getText().trim());
				item.setText(1,textMarket.getText().trim());
				item.setText(2,String.valueOf(spinnerEnd.getSelection() - spinnerStart.getSelection() + 1));
				item.setText(3,textModul.getText().trim());
				item.setText(4,String.valueOf(spinnerStart.getSelection()));
				item.setText(5,String.valueOf(spinnerEnd.getSelection()));
				
				for (int i=0;i<tbGrant.getItemCount();i++)
				{
					item = tbGrant.getItem(i);
					if (item.getText(3).equals("")) item.setText(3,textModul.getText().trim());
				}
			}
		});
		buttonSaveInfo.setBounds(423, 99, 91, 45);
		buttonSaveInfo.setFont(SWTResourceManager.getFont("Tahoma", 9, SWT.NONE));
		buttonSaveInfo.setText("保存注册资料");

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(10, 0, 343, 524);

		final Label label_3 = new Label(group_1, SWT.NONE);
		label_3.setBounds(10, 25,70, 20);
		label_3.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_3.setText("项目编号:");

		final Label label_1_4 = new Label(group_1, SWT.NONE);
		label_1_4.setBounds(10, 60,70, 20);
		label_1_4.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_1_4.setText("项目名称:");

		textProjName = new Text(group_1, SWT.BORDER);
		textProjName.setBounds(83, 56,208, 25);
		textProjName.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		textProjCode = new Text(group_1, SWT.BORDER);
		textProjCode.setBounds(83, 21,208, 25);
		textProjCode.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Button btnProjCode = new Button(group_1, SWT.NONE);
		btnProjCode.setBounds(297, 20,36, 27);
		btnProjCode.setText("查询");
		btnProjCode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushTable(textProjCode.getText().trim(),"CODE");
				textProjCode.selectAll();
				textProjCode.setFocus();
			}
		});
		
		final Button btnProjName = new Button(group_1, SWT.NONE);
		btnProjName.setBounds(297, 55,36, 27);
		btnProjName.setText("查询");
		btnProjName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushTable(textProjName.getText().trim(),"NAME");
				textProjName.selectAll();	
				textProjName.setFocus();
			}
		});
		
		tbProj = new Table(group_1, SWT.FULL_SELECTION | SWT.BORDER);
		tbProj.setBounds(10, 94,323, 420);
		tbProj.setLinesVisible(true);
		tbProj.setHeaderVisible(true);
		tbProj.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				TableItem item = tbProj.getItem(tbProj.getSelectionIndex());
				textProjCode.setText(item.getText(2));
				textProjName.setText(item.getText(0));
			}
		});
		tbProj.addMouseListener(new MouseAdapter() 
        {
            public void mouseDoubleClick(MouseEvent mouseevent) 
            {
            	if (tbProj.getSelectionIndex() >= 0)
            	{
	            	TableItem item = tbProj.getItem(tbProj.getSelectionIndex());
					new GrantDetailDlg(shell).open(sql, item.getText(2), item.getText(0));
            	}
			}
		});
		
		final TableColumn newColumnTableColumn_1 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_1.setWidth(204);
		newColumnTableColumn_1.setText("项目名称");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(93);
		newColumnTableColumn_3.setText("授权注册数");

		final TableColumn newColumnTableColumn = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn.setWidth(160);
		newColumnTableColumn.setText("项目编号");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText("上次授权时间");

		final TableItem item = new TableItem(tbProj, SWT.BORDER);
		item.setText("New item");

		final Group group_2 = new Group(shell, SWT.NONE);
		group_2.setBounds(359, 0, 525, 219);

		tbGrant = new Table(group_2, SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER);
		tbGrant.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushGrantDetail();				
			}
		});
		tbGrant.setBounds(10, 59,505, 150);
		tbGrant.setLinesVisible(true);
		tbGrant.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_4 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_4.setWidth(152);
		newColumnTableColumn_4.setText("门店名称");

		final TableColumn newColumnTableColumn_5 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.CENTER);
		newColumnTableColumn_5.setWidth(100);
		newColumnTableColumn_5.setText("门店编号");

		final TableColumn newColumnTableColumn_6 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_6.setAlignment(SWT.RIGHT);
		newColumnTableColumn_6.setWidth(70);
		newColumnTableColumn_6.setText("注册数");

		final TableColumn newColumnTableColumn_7 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_7.setAlignment(SWT.CENTER);
		newColumnTableColumn_7.setWidth(75);
		newColumnTableColumn_7.setText("客户模块");

		final TableColumn newColumnTableColumn_8 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_8.setAlignment(SWT.CENTER);
		newColumnTableColumn_8.setWidth(100);
		newColumnTableColumn_8.setText("起始号码");

		final TableColumn newColumnTableColumn_9 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_9.setWidth(100);
		newColumnTableColumn_9.setText("结束号码");

		new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_9.setAlignment(SWT.CENTER);

		final Label label_3_1 = new Label(group_2, SWT.NONE);
		label_3_1.setBounds(10, 25, 70, 20);
		label_3_1.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		label_3_1.setText("授权资料:");

		textGrantInfo = new Text(group_2, SWT.BORDER);
		textGrantInfo.setBounds(83, 21, 390, 25);
		textGrantInfo.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));

		final Button btnOpenFile = new Button(group_2, SWT.NONE);
		btnOpenFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				CommonMethod.openFileDialog(shell,textGrantInfo,new String[]{"*.xls","*.xlsx"},new String[]{"XLS Files(*.xls)","XLSX Files(*.xlsx)"});
				if (textGrantInfo.getText().length() > 0)
				{
					readGrantInfo(textGrantInfo.getText());
				}
			}
		});
		btnOpenFile.setBounds(479, 20, 36, 27);
		btnOpenFile.setText("...");

		final Button btnDelReport = new Button(shell, SWT.NONE);
		btnDelReport.setBounds(359, 529, 91, 30);
		btnDelReport.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnDelReport.setText("创建新报告");
        btnDelReport.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent arg0) {
        		PathFile.deletePath("regreport.txt");
        		
        		MessageBox mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);		
				mb.setMessage("已删除上次授权报告文件!");
				mb.open();
        	}
        });
        
		final Button btnMaxDate = new Button(shell, SWT.NONE);
		btnMaxDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (textProjCode.getText().trim().length() <= 0 || textProjName.getText().trim().length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("项目编号和项目名称不能为空!");
					mb.open();
					return;					
				}
				
				// 有效期只生成一个
				for (int j=0;j<1;j++)
				{
					TableItem item = null;
					if (tbGrant.getItemCount() > j) item = tbGrant.getItem(j);
					
					String regcode;
					String strMaxDate = textMaxDate.getText().trim();
					String strKey = (item!=null?item.getText(3).trim():textModul.getText().trim());
					String strCustomer = (item!=null?item.getText(0).trim():textCustomer.getText().trim());
					String strMarket = (item!=null?item.getText(1).trim():textMarket.getText().trim());
					//strKey = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,"99999",strKey);
					if (strKey == null || strKey.length() <= 0)
					{
						MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
						mb.setMessage("模块编号不能为空");
						mb.open();
						return;
					}
					
					MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
					String str = "注册资料\n\n门店名称: "+strCustomer+"\n门店编号: "+strMarket +
								 "\n客户模块: "+strKey+"\n有效期至: "+strMaxDate+
								 "\n\n你确定生成有效期并登记到授权数据库吗？";
					mb.setMessage(str);
					if (mb.open() != SWT.YES) return;
					
					try
					{
						regcode = ManipulatePrecision.EncodeString(strMaxDate, strKey);
						
						java.io.FileWriter f = new java.io.FileWriter("regreport.txt",true);
						f.write("######################################\r\n");
						f.write("请把有效期串设置到POS系统参数中的OH参数值\r\n");
						//f.write("门店名称: "+doneDlg.textCustomer.getText().trim()+"\r\n");
						//f.write("门店编号: "+doneDlg.textMarket.getText().trim()+"\r\n");
						f.write("有效期至: "+strMaxDate+"\r\n");
						f.write("有效期串: "+regcode+"\r\n");
						f.write("======================================\r\n\r\n");
						f.close();
						
						// 生成注册码到数据库
						int n = 0;
						sql.beginTrans();
						for(int i=0;i<1;i++)
						{
							str = "insert into regcode(projectcode,projectname,createdate,mktname,mktcode,mktmodul,regcode,regflag,regmemo) values(?,?,?,?,?,?,?,?,?)";
							if (!sql.setSql(str)) break;
							sql.paramSetString(1, textProjCode.getText().trim());
							sql.paramSetString(2, textProjName.getText().trim());
							sql.paramSetString(3, new ManipulateDateTime().getDateTimeString());
							sql.paramSetString(4, strCustomer);
							sql.paramSetString(5, strMarket);
							sql.paramSetString(6, strKey);
							sql.paramSetString(7, regcode);
							sql.paramSetString(8, "REGDATE");
							sql.paramSetString(9, strMaxDate);
							if (!sql.executeSql()) break;
							n++;
						}
						sql.commitTrans();
						
						// 提示
						mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);		
						if (n == 1) mb.setMessage("成功登记  " + strMaxDate + " 有效期授权!");
						else mb.setMessage("登记第 " + (n+1) + " 个有效期授权失败!\n\n"+ regcode);
						mb.open();
					}
					catch(Exception ex)
					{
						mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);		
						mb.setMessage("登记有效期授权异常!\n\n"+ ex.getMessage());
						mb.open();
					}
				}			
			}
		});
		btnMaxDate.setBounds(588, 530, 91, 30);
		btnMaxDate.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnMaxDate.setText("授权有效期");

		final Button btnRegCode = new Button(shell, SWT.NONE);
		btnRegCode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (textProjCode.getText().trim().length() <= 0 || textProjName.getText().trim().length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("项目编号和项目名称不能为空!");
					mb.open();
					return;					
				}

				int mktcount = 0,regnum = 0;
				for (int j=0;j<tbGrant.getItemCount();j++)
				{
					if (!tbGrant.getItem(j).getChecked()) continue;
					mktcount++;
					regnum += Convert.toInt(tbGrant.getItem(j).getText(2));
				}
				
				if (mktcount > 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
					mb.setMessage("你确定要生成选中的 "+mktcount + " 个门店的全部 " + regnum +" 个注册码\n\n并登记到授权数据库吗？");
					if (mb.open() != SWT.YES) return;
				}
				
				// 循环生成所有门店的注册码
				regnum = 0; 
				for (int j=0;j<tbGrant.getItemCount() || j<1;j++)
				{
					TableItem item = null;
					if (tbGrant.getItemCount() > j) item = tbGrant.getItem(j);
					if (item != null)
					{
						if (!item.getChecked()) continue;
						item.setChecked(false);
						
						tbGrant.setFocus();
						tbGrant.select(j);
						tbGrant.setSelection(j);
						tbGrant.showSelection();
						refushGrantDetail();
						while (Display.getCurrent().readAndDispatch());
					}
					else
					{
						MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
						String str = "注册资料\n" +
									 "\n门店名称: "+textCustomer.getText().trim() +
									 "\n门店编号: "+textMarket.getText().trim() +
									 "\n客户模块: "+textModul.getText().trim() +
									 "\n注册码共: "+(spinnerEnd.getSelection() - spinnerStart.getSelection() + 1)+" 个" +
									 "\n\n你确定生成注册码并登记到授权数据库吗？";
						mb.setMessage(str);
						if (mb.open() != SWT.YES) continue;
					}
					
					String regcode;
					String strKey = textModul.getText().trim();
					String strCustomer = textCustomer.getText().trim();
					String strMarket = textMarket.getText().trim();
					
					if (strKey.length() <= 0)
					{
						MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
						mb.setMessage("客户模块为空,你确定是要以空模块号生成注册码吗!");
						if (mb.open() != SWT.YES) break;
					}
					
					// 检查门店是否已经授权,门店号是否发生改变
					Object mktname = sql.selectOneData("select mktname from regcode where projectcode='"+textProjCode.getText().trim()+"' and mktcode='"+strMarket+"' and regflag='REGCODE'");
					if (mktname != null && !strCustomer.equals(mktname))
					{
						Object regcount = sql.selectOneData("select count(*) from regcode where projectcode='"+textProjCode.getText().trim()+"' and mktcode='"+strMarket+"' and regflag='REGCODE'");
						
						MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
						mb.setMessage("["+strMarket+"]号门店已经授权了 "+regcount.toString()+" 个注册码,\n门店名称为:"+mktname+"\n本次名称为:"+strCustomer+"\n两次授权门店号相同但门店名称不同\n\n你要以新信息重新生成该门店的注册码吗？");
						if (mb.open() != SWT.YES) continue;

						// 删除上次门店授权,以本次门店号为准
						if (!sql.executeSql("delete from regcode where projectcode='"+textProjCode.getText().trim()+"' and mktcode='"+strMarket+"' and regflag='REGCODE'")) continue;
					}
					
					boolean error = false;
					try
					{
						java.io.FileWriter f = new java.io.FileWriter("regreport.txt",true);
						f.write("######################################\r\n");
						f.write("注册资料\r\n");
						f.write("门店名称: "+strCustomer+"\r\n");
						f.write("门店编号: "+strMarket+"\r\n");
						StringBuffer sb= new StringBuffer();
						if (chkbtn_1.getSelection() && chkbtn_1.getText().trim().length() > 0) sb.append(chkbtn_1.getText() +"、");
						if (chkbtn_2.getSelection() && chkbtn_2.getText().trim().length() > 0) sb.append(chkbtn_2.getText() +"、");
						if (chkbtn_4.getSelection() && chkbtn_4.getText().trim().length() > 0) sb.append(chkbtn_4.getText() +"、");
						if (chkbtn_8.getSelection() && chkbtn_8.getText().trim().length() > 0) sb.append(chkbtn_8.getText() +"、");
						if (chkbtn_16.getSelection()&& chkbtn_16.getText().trim().length()> 0) sb.append(chkbtn_16.getText()+"、");
						if (sb.length() > 0) f.write("授权模块: "+sb.substring(0,sb.length()-1)+"\r\n");
						f.write("注册码共: "+(spinnerEnd.getSelection() - spinnerStart.getSelection() + 1)+" 个\r\n");
						
						// 生成注册码到数据库
						int n = 0;
						sql.beginTrans();
						for(int i=spinnerStart.getSelection();i<=spinnerEnd.getSelection();i++)
						{
							regcode = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,String.valueOf(i),strKey);
							
							// 临时注册码,strMarket = 有效期(YYYYMMDD)
							if (strCustomer == null || strCustomer.length() <= 0)
							{
								regcode += "-" + strMarket;
							}
							
							// 检查数据库是否已存在注册码
							boolean ok = true;
							Object regcount = sql.selectOneData("select count(*) from regcode where projectcode='"+textProjCode.getText().trim()+"' and mktcode='"+strMarket+"' and regcode ='"+regcode+"' and regflag='REGCODE'");
							if (regcount == null || Convert.toInt(regcount) <= 0)
							{
								String str = "insert into regcode(projectcode,projectname,createdate,mktname,mktcode,mktmodul,regcode,regflag,regmemo) values(?,?,?,?,?,?,?,?,?)";
								if (sql.setSql(str))
								{
									sql.paramSetString(1, textProjCode.getText().trim());
									sql.paramSetString(2, textProjName.getText().trim());
									sql.paramSetString(3, new ManipulateDateTime().getDateTimeString());
									sql.paramSetString(4, strCustomer);
									sql.paramSetString(5, strMarket);
									sql.paramSetString(6, strKey);
									sql.paramSetString(7, regcode);
									sql.paramSetString(8, "REGCODE");
									sql.paramSetString(9, "");
									if (!sql.executeSql()) ok = false;
								}
							}
							n++;
							if (!ok)
							{
								MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
								mb.setMessage("登记第 " + n + " 个注册码授权失败!\n\n"+ regcode+"\n\n你要继续登记剩下的注册码吗？");
								if (mb.open() != SWT.YES) 
								{
									error = true;
									item.setChecked(error);
									
									break;
								}
							}
							
							f.write(regcode+"\r\n");
						}
						sql.commitTrans();
						f.write("======================================\r\n\r\n");
						f.close();
	
						// 注册计数
						regnum += n;
						
						// 刷新TABLE
						refushTable(textProjCode.getText().trim(),"CODE");
					}
					catch(Exception ex)
					{
						error = true;
						item.setChecked(error);
						
						MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);		
						mb.setMessage("登记注册码授权异常!\n\n"+ ex.getMessage());
						mb.open();
					}
					
					// 发生错误，不再继续生成下一门店
					if (error) break;
				}
				
				// 提示
				MessageBox mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);
				mb.setMessage("成功登记 " + mktcount + " 个门店,共 " + regnum + " 个注册码授权!");
				mb.open();
			}
		});
		btnRegCode.setBounds(685, 530, 91, 30);
		btnRegCode.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnRegCode.setText("授权注册码");

		final Button btnReport = new Button(shell, SWT.NONE);
		btnReport.setBounds(782, 529, 91, 30);
		btnReport.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnReport.setText("查本次报告");
		btnReport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				try 
				{
					Runtime.getRuntime().exec("notepad regreport.txt");
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
		
		final Button buttonDB = new Button(shell, SWT.NONE);
		buttonDB.setBounds(107, 529, 91, 30);
		buttonDB.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonDB.setText("打开数据库");
		buttonDB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{		
	            try
				{
					SHELLEXECUTEINFO lpExecInfo = new SHELLEXECUTEINFO();   
					lpExecInfo.lpVerb = "open";   
		            lpExecInfo.lpFile = dbpath;
		            lpExecInfo.nShow = WindowsConstants.SW_SHOWNORMAL;  
					Shell32.ShellExecuteEx(lpExecInfo);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}			
			}
		});		
	}
	
	private void refushTable(String cond,String mode)
	{
		try
		{
			tbProj.removeAll();
			String str = "select max(projectname),count(regcode),projectcode,max(createdate) from regcode where ";
			if (cond != null && cond.length() > 0)
			{
				if (mode.equalsIgnoreCase("CODE")) str += " projectcode = '" + cond + "' and ";
				else str += " projectname like '%" + cond + "%' and ";
			}
			str += " regflag='REGCODE' group by projectcode";
			ResultSet rs = sql.selectData(str);
			if (rs != null)
			{
				while(rs.next())
				{
					TableItem item = new TableItem(tbProj, SWT.NONE);
					item.setText(0, rs.getString(1)!=null?rs.getString(1):"");
					item.setText(1, rs.getString(2)!=null?rs.getString(2):"");
					item.setText(2, rs.getString(3)!=null?rs.getString(3):"");
					item.setText(3, rs.getString(4)!=null?rs.getString(4):"");
				}
				rs.close();
			}
		}
		catch(Exception ex)
		{
			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
			mb.setMessage(ex.getMessage());
			mb.open();
		}
	}
	
	private void refushGrantDetail()
	{
		int index = tbGrant.getSelectionIndex();
		if (index < 0) return;
		
		TableItem item = tbGrant.getItem(index);
		if (!tempReg) textCustomer.setText(item.getText(0));
		if (!tempReg) textMarket.setText(item.getText(1));
		textModul.setText(item.getText(3));
		spinnerStart.setSelection(Integer.parseInt(item.getText(4)));
		spinnerEnd.setSelection(Integer.parseInt(item.getText(5)));
		
		int seqno = Integer.parseInt(item.getText(4).substring(0,2));
		seqno &= ~32;
    	if ((seqno& 1) > 0) chkbtn_1.setSelection(true);
    	else chkbtn_1.setSelection(false);
    	if ((seqno& 2) > 0) chkbtn_2.setSelection(true);
    	else chkbtn_2.setSelection(false);
    	if ((seqno& 4) > 0) chkbtn_4.setSelection(true);
    	else chkbtn_4.setSelection(false);	
    	if ((seqno& 8) > 0) chkbtn_8.setSelection(true);
    	else chkbtn_8.setSelection(false);		
    	if ((seqno& 16) > 0) chkbtn_16.setSelection(true);
    	else chkbtn_16.setSelection(false);
	}
	
	private void readGrantInfo(String path)
	{
		// 清空列表
		tbGrant.removeAll();
		
		// 临时注册码
		tempReg = false;
		
		// 读取XLS
        try 
        {    
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(path));
            
            //表单   
            int numSheets = 1;

            //for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++)
            if (true)
            {    
            	if (workbook.getSheetAt(numSheets) != null)
                {    
                    HSSFSheet aSheet = workbook.getSheetAt(numSheets);// 获得一个sheet
                    
                    //行    
                    for (int rowNumOfSheet = 0; aSheet != null && rowNumOfSheet <= aSheet.getLastRowNum(); rowNumOfSheet++) 
                    {
                    	// 项目名
                    	if (rowNumOfSheet == 2)
                    	{
                    		HSSFRow aRow = aSheet.getRow(rowNumOfSheet);
                            if (aRow == null) continue;
                            if (aRow.getLastCellNum() > 2)
                            {
                            	HSSFCell aCell = aRow.getCell(2);
                            	String strCell = (aCell!=null?aCell.getStringCellValue().trim():"");
                            	HSSFCell aCell1 = aRow.getCell(4);
                            	String strCell1 = (aCell1!=null?aCell1.getStringCellValue().trim():"");

                            	if (strCell.length() > 0 || strCell1.length() > 0)
                            	{
                            		if (strCell.indexOf("[") >= 0 || strCell.indexOf("-") >= 0)
                            		{
                            			textProjCode.setText(strCell.trim());
                            			textProjName.setText(strCell1.trim());               			
                            			refushTable(textProjCode.getText(), "CODE");
                            			if (tbProj.getItemCount() <= 0) refushTable(textProjName.getText(), "NAME");
                            		}
                            		else
                            		{
                            			textProjCode.setText(strCell);
                            			textProjName.setText(strCell1);
                            			refushTable(textProjName.getText(), "NAME");
                            		}
                            		
                            		// 选中项目
                        			if (tbProj.getItemCount() >  0)
                        			{
                        				tbProj.setSelection(0);                            				
                        				TableItem item = tbProj.getItem(tbProj.getSelectionIndex());
                        				textProjCode.setText(item.getText(2));
                        				textProjName.setText(item.getText(0));
                        			}                            		
                            	}
                            }
                    	}
                    	
                    	// 有效期
                    	if (rowNumOfSheet == 5)
                    	{
                    		HSSFRow aRow = aSheet.getRow(rowNumOfSheet);
                            if (aRow == null) continue;
                            if (aRow.getLastCellNum() > 2)
                            {
                            	HSSFCell aCell = aRow.getCell(2);
                            	if (aCell != null && "永久授权".indexOf(aCell.getStringCellValue()) >= 0)
                    			{
                            		chkbtn.setSelection(true);
                            		textMaxDate.setText("2100-01-01,15");
                    			}
                            	else
                            	if (aCell != null && "时段授权".indexOf(aCell.getStringCellValue()) >= 0)
                            	{
                            		chkbtn.setSelection(false);
                            		if (aRow.getLastCellNum() > 4)
                            		{
                            			aCell = aRow.getCell(4);
                            			String strCell = "";
                            			if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                            			{
                            				if (HSSFDateUtil.isCellDateFormatted(aCell))
                            				{
                                				strCell = new SimpleDateFormat("yyyy-MM-dd").format(aCell.getDateCellValue());    
                            				}
                            				else
                            				{
                            					strCell = String.valueOf((int)aCell.getNumericCellValue());
                            				}
                            			}
                            			else if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)
                            			{
                            				strCell = (aCell!=null?new SimpleDateFormat("yyyy-MM-dd").format(aCell.getDateCellValue()):"");
                            			}
                            			else
                            			{
                            				strCell = (aCell!=null?aCell.getStringCellValue():"");
                            			}
                            			textMaxDate.setText(strCell.equals("")?"":strCell + ",15");
                            		}
                            	}
                            	else
                            	if (aCell != null && "临时注册码".indexOf(aCell.getStringCellValue()) >= 0)
                            	{
                            		textCustomer.setText("");
                            		if (aRow.getLastCellNum() > 4)
                            		{
                            			aCell = aRow.getCell(4);
                            			String strCell = "";
                            			if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                            			{
                            				if (HSSFDateUtil.isCellDateFormatted(aCell))
                            				{
                                				strCell = new SimpleDateFormat("yyyyMMdd").format(aCell.getDateCellValue());    
                            				}
                            				else
                            				{
                            					strCell = String.valueOf((int)aCell.getNumericCellValue());
                            				}
                            			}
                            			else if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)
                            			{
                            				if (HSSFDateUtil.isCellDateFormatted(aCell))
                            				{
                                				strCell = new SimpleDateFormat("yyyyMMdd").format(aCell.getDateCellValue());    
                            				}
                            			}
                            			else
                            			{
                            				strCell = (aCell!=null?aCell.getStringCellValue():"");
                            			}
                            			textMarket.setText(strCell.equals("")?"":strCell);
                            			tempReg = true;
                            		}
                            	}
                            }
                    	}
                    	
                    	// 第10行开始为门店信息
                        if (rowNumOfSheet >= 9) 
                        {    
                            HSSFRow aRow = aSheet.getRow(rowNumOfSheet);
                            if (aRow == null) continue;

                            // 增加一个门店授权
                            TableItem item = null;
                            
                            //列    
                            for (int cellNumOfRow = 0; cellNumOfRow < aRow.getLastCellNum(); cellNumOfRow++) 
                            {    
                            	HSSFCell aCell = aRow.getCell(cellNumOfRow);
                            	
                            	if (cellNumOfRow == 1)
                            	{
                            		String strCell = "";
                        			if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) strCell = String.valueOf((int)aCell.getNumericCellValue());
                        			else strCell = (aCell!=null?aCell.getStringCellValue():"");
                            		if (strCell.length() <= 0) break;
                            		
                            		if (item == null) item = new TableItem(tbGrant, SWT.NONE);
                            		item.setText(1,strCell);
                            	}
                            	else
                            	{
                            		if (cellNumOfRow == 2) item.setText(0,aCell!=null?aCell.getStringCellValue():"");
                            		if (cellNumOfRow == 5)
                            		{
                            			String strCell = "";
                            			if (aCell != null && aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) strCell = String.valueOf((int)aCell.getNumericCellValue());
                            			else strCell = (aCell!=null?aCell.getStringCellValue():"");
                            			item.setText(2,strCell);

                        				int seq = 32;
                            			if (aRow.getLastCellNum() > 6 && aRow.getCell(6) != null && "是".equals(aRow.getCell(6).getStringCellValue())) seq = seq | 1;
                            			if (aRow.getLastCellNum() > 7 && aRow.getCell(7) != null && "是".equals(aRow.getCell(7).getStringCellValue())) seq = seq | 2;
                            			seq = seq | 4;seq = seq | 8;seq = seq | 16;
                            			seq *= 1000;seq += 1;
                            			
                            			item.setText(4,String.valueOf(seq));
                            			item.setText(5,String.valueOf(seq+Integer.parseInt(strCell)-1));
                            		}
                            	}
                            }
                            
                            if (item != null) item.setChecked(true);
                        }    
                    }


                }     
            }
            
            if (tbGrant.getItemCount() > 0)
            {
            	tbGrant.select(0);
            	tbGrant.setFocus();
            	refushGrantDetail();
            }
        }
        catch (Exception e) 
        {    
            e.printStackTrace();    
        }    
	}
}

