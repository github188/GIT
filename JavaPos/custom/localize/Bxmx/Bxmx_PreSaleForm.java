package custom.localize.Bxmx;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Bxmx_PreSaleForm
{
	private Bxmx_FetchInfoDef fetchinfo;
	private int prekeystate = NewKeyListener.curInputMode;
	public Shell shell;
	public Text txtfetchmemo;
	public Text txtfetchtel;
	public Text txtfetcher;
	public Text txtfetchdate;
	public Text txtfetchmkt;
	public Text txtfetchmktname;
	public boolean retvalue;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @wbp.parser.entryPoint
	 */

	protected void addListener()
	{
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyReleased(e, key);
			}
		};
		
		txtfetchmkt.addFocusListener(new FocusListener(){

			public void focusLost(FocusEvent arg0)
			{
				// TODO Auto-generated method stub
				if (txtfetchmktname.getText().trim().length() <= 0)
				{
					new MessageBox("取货门店不能为空");

					txtfetchmkt.setFocus();
					txtfetchmkt.selectAll();
				}
			}

			public void focusGained(FocusEvent arg0)
			{

				
			}});

		NewKeyListener key1 = new NewKeyListener();
		key1.event = event;
		key1.inputMode = key1.IntegerInput;
		txtfetchmkt.addKeyListener(key1);
		
		NewKeyListener key = new NewKeyListener();
		key.event = event;
		txtfetchdate.addKeyListener(key);
		txtfetcher.addKeyListener(key);
		txtfetchtel.addKeyListener(key);
		txtfetchmemo.addKeyListener(key);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void keyPressed(KeyEvent e, int key)
	{

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void keyReleased(KeyEvent e, int key)
	{
		// 处理小键盘上的enter键
		if (e.keyCode == 16777296)
			key = GlobalVar.Enter;

		switch (key)
		{
			case GlobalVar.Enter:
				e.data = "focus";

				if (e.getSource() == txtfetchmkt)
				{
					if (txtfetchmkt.getText().trim().equals(""))
					{
						new MessageBox("取货门店不能为空");

						txtfetchmkt.setFocus();
						txtfetchmkt.selectAll();
					}
					else
					{
						Vector retinfo = new Vector();

						if (((Bxmx_NetService) NetService.getDefault()).checkMktcode(txtfetchmkt.getText().trim(), retinfo) && retinfo.size() != 0)
						{
							String[] tmpmkt = (String[]) retinfo.get(0);
							if (tmpmkt != null && tmpmkt.length > 0)
							{
								String mktname = tmpmkt[0];
								txtfetchmktname.setText(mktname);
							}

							txtfetchdate.setFocus();
							txtfetchdate.selectAll();
						}
						else
						{
							txtfetchmkt.setFocus();
							txtfetchmkt.selectAll();
							txtfetchmktname.setText("");
						}
					}
				}
				else if (e.getSource() == txtfetchdate)
				{
					String date = ManipulateDateTime.getConversionDate(txtfetchdate.getText());

					if (date != null && date.trim().length() > 4 && ManipulateDateTime.checkDate(date))
					{
						String cmpdata = ManipulateDateTime.getConversionDate(txtfetchdate.getText());
						long interval = new ManipulateDateTime().compareDate(cmpdata, ManipulateDateTime.getCurrentDate());
						if (interval < 0)
						{
							new MessageBox("输入的日期小于今天，请重新输入");
							txtfetchdate.setFocus();
							txtfetchdate.selectAll();
						}
						else
						{
							txtfetcher.setFocus();
							txtfetcher.selectAll();
						}
					}
					else
					{
						new MessageBox("日期的输入格式必须为\n20130101(YYYYMMDD)");

						txtfetchdate.setFocus();
						txtfetchdate.selectAll();
					}
				}
				else if (e.getSource() == txtfetcher)
				{

					if (txtfetcher.getText().trim().equals(""))
					{
						new MessageBox("联系人不能为空");

						txtfetcher.setFocus();
						txtfetcher.selectAll();
					}
					else
					{
						txtfetchtel.setFocus();
						txtfetchtel.selectAll();
					}
				}
				else if (e.getSource() == txtfetchtel)
				{
					if (txtfetchtel.getText().trim().equals("") || txtfetchtel.getText().trim().length() < 7)
					{
						new MessageBox("联系电话长度应大于6位");

						txtfetchtel.setFocus();
						txtfetchtel.selectAll();
					}
					else
					{
						txtfetchmemo.setFocus();
						txtfetchmemo.selectAll();
					}
				}
				else if (e.getSource() == txtfetchmemo)
				{
					txtfetchmkt.setFocus();
					txtfetchmkt.selectAll();
				}

				break;

			case GlobalVar.Validation:
				if (txtfetchmkt.getText().trim().length() <= 0 || txtfetchmktname.getText().trim().length() <=0)
				{
					new MessageBox("请确定门店输入的准确性");

					e.data = "focus";
					txtfetchmkt.setFocus();
					txtfetchmkt.selectAll();
					return;
				}
				
				Vector retinfo = new Vector();
				if (((Bxmx_NetService) NetService.getDefault()).checkMktcode(txtfetchmkt.getText().trim(), retinfo) && retinfo.size() > 0)
				{
					
				}
				else
				{
					new MessageBox("请确定门店输入的准确性");

					e.data = "focus";
					txtfetchmkt.setFocus();
					txtfetchmkt.selectAll();
					return;
				}
				
				String date = ManipulateDateTime.getConversionDate(txtfetchdate.getText());

				if (date != null && date.trim().length() > 4 && ManipulateDateTime.checkDate(date))
				{
				}
				else
				{
					new MessageBox("日期的输入格式必须为\n20130101(YYYYMMDD)");

					txtfetchdate.setFocus();
					txtfetchdate.selectAll();
					return;
				}

				if (txtfetchtel.getText().trim().equals("") || txtfetchtel.getText().trim().length() < 7)
				{
					new MessageBox("联系电话不合法");
					e.data = "focus";
					txtfetchtel.setFocus();
					txtfetchtel.selectAll();
					return;
				}

				NewKeyListener.curInputMode = prekeystate;
				if (new MessageBox("   是否提交保存?    ", null, true).verify() == GlobalVar.Key1)
				{
					fetchinfo.fetchmkt = txtfetchmkt.getText().trim();
					fetchinfo.fetchdate = txtfetchdate.getText().trim();
					fetchinfo.fetcher = txtfetcher.getText().trim();
					fetchinfo.fetchtel = txtfetchtel.getText().trim();
					fetchinfo.fetchmemo = txtfetchmemo.getText().trim();
					fetchinfo.fetchmktname = txtfetchmktname.getText().trim();

					retvalue = true;
					shell.close();
					shell.dispose();
				}
				else
				{
					NewKeyListener.curInputMode = 4;
					txtfetchmemo.setFocus();
					txtfetchmemo.selectAll();
				}
				break;
			case GlobalVar.Exit:
				fetchinfo = null;
				retvalue = false;
				shell.close();
				shell.dispose();
		}
	}

	public boolean open(Bxmx_FetchInfoDef fetchinfo)
	{
		this.fetchinfo = fetchinfo;

		// 让输入框接收任何形式的输入
		NewKeyListener.curInputMode = 4;

		Display display = Display.getDefault();

		createContents();
		addListener();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			shell.setActive();
		}

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);
		NewKeyListener.curInputMode = prekeystate;
		return retvalue;
	}

	protected void createContents()
	{
		shell = new Shell(GlobalVar.style_linux);
		shell.setText("Shell");
		shell.setSize(new org.eclipse.swt.graphics.Point(436, 314));
		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		CLabel lblNewLabel = new CLabel(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblNewLabel.setBounds(15, 10, 85, 24);
		lblNewLabel.setText("取货门店");

		CLabel label = new CLabel(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("提货日期");
		label.setBounds(15, 42, 85, 24);

		CLabel label_1 = new CLabel(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText("联 系 人");
		label_1.setBounds(14, 74, 85, 24);

		CLabel label_2 = new CLabel(shell, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText("联系电话");
		label_2.setBounds(14, 106, 85, 24);

		CLabel label_3 = new CLabel(shell, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText("备    注");
		label_3.setBounds(14, 139, 85, 24);

		CLabel label_4 = new CLabel(shell, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_4.setText("[确认键]");
		label_4.setBounds(14, 180, 85, 24);

		CLabel label_5 = new CLabel(shell, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_5.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_5.setText("  保存");
		label_5.setBounds(14, 200, 85, 24);

		txtfetchmemo = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtfetchmemo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetchmemo.setBounds(124, 139, 290, 163);

		txtfetchtel = new Text(shell, SWT.BORDER);
		txtfetchtel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetchtel.setBounds(124, 106, 290, 25);

		txtfetcher = new Text(shell, SWT.BORDER);
		txtfetcher.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetcher.setBounds(124, 74, 290, 25);

		txtfetchdate = new Text(shell, SWT.BORDER);
		txtfetchdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetchdate.setBounds(124, 42, 290, 25);

		txtfetchmkt = new Text(shell, SWT.BORDER);
		txtfetchmkt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetchmkt.setBounds(124, 10, 150, 25);
		txtfetchmkt.setFocus();

		txtfetchmktname = new Text(shell, SWT.BORDER);
		txtfetchmktname.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtfetchmktname.setForeground(SWTResourceManager.getColor(255, 0, 0));
		txtfetchmktname.setBounds(280, 10, 134, 25);
		txtfetchmktname.setEditable(false);
	}
}
