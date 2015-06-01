package registerPos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.swtdesigner.SWTResourceManager;

public class RegDone
{
	private Button chkbtn;
	public Text textRegDate;
	public Text textMaxDate;
	public Text textInfo;
	public Text textModul_1;
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
			
			RegDone window = new RegDone();
			
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
		shell.setSize(545, 509);
		shell.setText("生成注册码和有效期");

		final Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 12, 91, 30);
		label.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label.setText("门店名称:");
		
		textCustomer = new Text(shell, SWT.BORDER);
		textCustomer.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		textCustomer.setBounds(106, 15, 423, 25);
		textCustomer.setText("生成试用注册码,请在本栏填空");
		
		final Button buttonRegCode = new Button(shell, SWT.NONE);
		buttonRegCode.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				String regcode,strCustomer,strMarket,strKey;
				
				strCustomer = textCustomer.getText().trim();
				strMarket = textMarket.getText().trim();
				strKey = textModul.getText().trim();
				
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
		buttonRegCode.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		buttonRegCode.setBounds(300, 437, 91, 30);
		buttonRegCode.setText("生成注册码");

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(10, 52, 91, 30);
		label_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1.setText("门店编号:");

		textMarket = new Text(shell, SWT.BORDER);
		textMarket.setBounds(106, 55, 423, 25);
		textMarket.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		textMarket.setText("生成试用注册码,请在本栏填写试用期(YYYYMMDD)");
		
		final Label label_1_1 = new Label(shell, SWT.NONE);
		label_1_1.setBounds(10, 95, 91, 30);
		label_1_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1.setText("客户模块:");

		textModul = new Text(shell, SWT.BORDER);
		textModul.setTextLimit(4);
		textModul.setBounds(106, 98, 423, 25);
		textModul.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label label_1_2 = new Label(shell, SWT.NONE);
		label_1_2.setBounds(10, 280, 91, 30);
		label_1_2.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_2.setText("注册号码:");

		textRegCode = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		textRegCode.setBounds(106, 283, 423, 25);
		textRegCode.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Button buttonClose = new Button(shell, SWT.NONE);
		buttonClose.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				shell.close();
				shell.dispose();
			}
		});
		buttonClose.setBounds(465, 437, 65, 30);
		buttonClose.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		buttonClose.setText("退出");

		final Label label_1_1_1 = new Label(shell, SWT.NONE);
		label_1_1_1.setBounds(10, 206, 91, 30);
		label_1_1_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1_1.setText("开始序号:");

		spinnerStart = new Spinner(shell, SWT.BORDER);
		spinnerStart.setMaximum(99999);
		spinnerStart.setSelection(1);
		spinnerStart.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		spinnerStart.setBounds(106, 208, 154, 25);

		final Label label_1_1_1_1 = new Label(shell, SWT.NONE);
		label_1_1_1_1.setBounds(277, 206, 91, 30);
		label_1_1_1_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1_1_1.setText("结束序号:");

		spinnerEnd = new Spinner(shell, SWT.BORDER);
		spinnerEnd.setMaximum(99999);
		spinnerEnd.setSelection(1);
		spinnerEnd.setBounds(375, 208, 154, 25);
		spinnerEnd.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label label_1_1_1_2 = new Label(shell, SWT.NONE);
		label_1_1_1_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1_1_1_2.setBounds(10, 242, 91, 30);
		label_1_1_1_2.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1_1_2.setText("重要说明:");

		textModul_1 = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		textModul_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		textModul_1.setText("可使用金卡工程的注册码从1000开始，普通注册码从1开始");
		textModul_1.setBounds(105, 245, 424, 25);
		textModul_1.setTextLimit(4);
		textModul_1.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(10, 316, 91, 30);
		label_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_2.setText("格式说明:");

		final Label label_1_3 = new Label(shell, SWT.NONE);
		label_1_3.setBounds(10, 356, 91, 30);
		label_1_3.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_3.setText("有效期值:");

		textInfo = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		textInfo.setBounds(106, 319, 423, 25);
		textInfo.setText("使用有效期,有效期前N天开始提示(YYYY-MM-DD,N)");
		textInfo.setForeground(SWTResourceManager.getColor(255, 0, 0));
		textInfo.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		textMaxDate = new Text(shell, SWT.BORDER);
		textMaxDate.setBounds(106, 359, 319, 25);
		textMaxDate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label label_1_2_1 = new Label(shell, SWT.NONE);
		label_1_2_1.setBounds(10, 392, 91, 30);
		label_1_2_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_2_1.setText("有效期串:");

		textRegDate = new Text(shell, SWT.BORDER);
		textRegDate.setBounds(106, 395, 423, 27);
		textRegDate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Button buttonUnDate = new Button(shell, SWT.NONE);
		buttonUnDate.setBounds(203, 437, 91, 30);
		buttonUnDate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
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
		
		final Button buttonRegDate = new Button(shell, SWT.NONE);
		buttonRegDate.setBounds(106, 437, 91, 30);
		buttonRegDate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
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

		final Label label_1_1_2 = new Label(shell, SWT.NONE);
		label_1_1_2.setBounds(10, 131, 91, 30);
		label_1_1_2.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1_2.setText("模块授权:");

		chkbtn_1 = new Button(shell, SWT.CHECK);
		chkbtn_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		chkbtn_1.setText("功能模块");
		chkbtn_1.setBounds(106, 131, 109, 30);

		chkbtn_2 = new Button(shell, SWT.CHECK);
		chkbtn_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});		
		chkbtn_2.setBounds(259, 131, 109, 30);
		chkbtn_2.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		chkbtn_2.setText("功能模块");

		chkbtn_4 = new Button(shell, SWT.CHECK);
		chkbtn_4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_4.setText("功能模块");
		chkbtn_4.setBounds(106, 167, 109, 30);
		chkbtn_4.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));

		chkbtn_8 = new Button(shell, SWT.CHECK);
		chkbtn_8.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_8.setText("功能模块");
		chkbtn_8.setBounds(259, 167, 109, 30);
		chkbtn_8.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));

		chkbtn_16 = new Button(shell, SWT.CHECK);
		chkbtn_16.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				calcRegisterFunction();
			}
		});
		chkbtn_16.setText("功能模块");
		chkbtn_16.setBounds(420, 167, 109, 30);
		chkbtn_16.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));

		chkbtn = new Button(shell, SWT.CHECK);
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
		chkbtn.setBounds(431, 356, 98, 30);
		chkbtn.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		chkbtn.setText("永久有效");

		final Button buttonGrant = new Button(shell, SWT.NONE);
		buttonGrant.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				GrantDlg dlg = new GrantDlg(shell);
				dlg.setDoneDialog(RegDone.this);
				dlg.open();
			}
		});
		buttonGrant.setBounds(396, 437, 65, 30);
		buttonGrant.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		buttonGrant.setText("授权");
	}

}

