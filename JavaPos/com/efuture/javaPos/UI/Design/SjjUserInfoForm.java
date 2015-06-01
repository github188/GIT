package com.efuture.javaPos.UI.Design;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

import custom.localize.Htsc.Htsc_NetService;
import custom.localize.Htsc.Htsc_SaleBS;

public class SjjUserInfoForm extends Htsc_SaleBS {

	protected Shell shell;
	private Text code;
	private Label label;
	private Text name;
	private Label label_1;
	private Text phone;
	private Table table;
	private Text tel;
	private Text address;
	private String names = "";
	private String tels = "";
	private String phones = "";
	private Button save;


	// private MessageBox mb =null;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SjjUserInfoForm window = new SjjUserInfoForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
	
		createContents();
		shell.open();
		shell.layout();
		phone.setFocus();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {

		shell = new Shell(GlobalVar.style);
		
		shell.setSize(800, 548);
		// 弹出框居中
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		shell.setLocation(x, y);

		shell.setText("顾客信息");

		// 加载背景图片
		ImageData data = new ImageData(ConfigClass.BackImagePath
				+ "salebkimage.jpg");
		data = data.scaledTo(shell.getClientArea().width,
				shell.getClientArea().height);
		Display display = Display.getDefault();
		Image originalImage = new Image(display, data);

		// 设置背景图片
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		shell.setBackgroundImage(originalImage);

		code = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		code.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 
			{
				//System.out.println(e.keyCode);
				if (e.keyCode == 27) 
				{
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
					
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) 
				{
					name.setFocus();
				}
				if (e.keyCode == 16777226) 
				{
					selectCode();
				}
				if (e.keyCode == 16777227) 
				{
					name.setFocus();
				}
				
				if (e.keyCode == 16777217) 
				{
					address.setFocus();
				}
				if (e.keyCode == 16777218) 
				{
					name.setFocus();
				}
			}
		});

		code.setText("");
		code.setBounds(79, 34, 227, 23);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(39, 40, 36, 17);
		lblNewLabel.setText("代码：");

		label = new Label(shell, SWT.NONE);
		label.setText("姓名：");
		label.setBounds(39, 119, 36, 17);

		name = new Text(shell, SWT.BORDER);
		name.setTextLimit(8);
		name.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// System.out.println(e.keyCode);
				if (e.keyCode == 27) 
				{
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) {
					if (null != name.getText() && !("").equals(name.getText())) 
					{
						Htsc_NetService netService = (Htsc_NetService) NetService
								.getDefault();
						// 调用查询结果
						Vector v = netService.getCustList(phone.getText(),
								tel.getText(), name.getText());
						// 遍历结果集
						if (v.size()>0) 
						{
							table.removeAll();
							for (int i = 0; i < v.size(); i++) 
							{
								TableItem item = new TableItem(table, SWT.NONE);
								String[] items = (String[]) v.get(i);
								item.setText(items);
							}
							table.setFocus();
							table.setSelection(0);   
						}
						else 
						{
							tel.setFocus();
						}

					} 
					else 
					{
						if(null != name.getText()&&!("").equals(name.getText()))
						{
							tel.setFocus();
						}
						else
						{
							name.setFocus();
							new MessageBox("姓名不能为空", null, false, 0, 0, 100);
						}
						
						
					}
					
				}
				if (e.keyCode == 16777226) 
				{
					selectCode();
					
				}
				if (e.keyCode == 16777227) 
				{
					if(null != name.getText()&&!("").equals(name.getText()))
					{
						addInfo();
					}
					else
					{
						new MessageBox("姓名不能为空", null, false, 0, 0, 100);
					}
					//name.setFocus();
				}
				
				if (e.keyCode == 16777217) 
				{
					phone.setFocus();
				}
				if (e.keyCode == 16777218) 
				{
					tel.setFocus();
				}
			}
		});
		name.setText("");
		name.setFocus();
		name.setBounds(79, 116, 227, 23);
		label_1 = new Label(shell, SWT.NONE);
		label_1.setText("手机：");
		label_1.setBounds(39, 81, 36, 17);

		phone = new Text(shell, SWT.BORDER);
		phone.setTextLimit(15);
		phone.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == 27) {
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) 
				{
					if (null != phone.getText()&& !("").equals(phone.getText())) 
					{
						//校验手机号
						if(!isMobileNO(phone.getText()))
						{
							new MessageBox("手机号格式不正确", null, false, 0, 0, 100);
						}
						else
						{
						Htsc_NetService netService = (Htsc_NetService) NetService
								.getDefault();
						// 调用查询结果
						Vector v = netService.getCustList(phone.getText(),
								tel.getText(), name.getText());
						// 遍历结果集
						if (v.size()>0) 
						{
							table.removeAll();
							for (int i = 0; i < v.size(); i++)
							{
								TableItem item = new TableItem(table, SWT.NONE);
								String[] items = (String[]) v.get(i);
								item.setText(items);
							}
								table.setFocus();
								table.setSelection(0);
							}
							else 
							{
								name.setFocus();
							}
						}
					} 
					else 
					{
						if(null != phone.getText()&&!("").equals(phone.getText()))
						{
							name.setFocus();
						}
						else
						{
							new MessageBox("手机不能为空", null, false, 0, 0, 100);
							phone.setFocus();
						}
						
					}

				}
				if (e.keyCode == 16777226) 
				{
					selectCode();
				}
				if (e.keyCode == 16777227) 
				{
					if(null != phone.getText()&&!("").equals(phone.getText()))
					{
						addInfo();
					}
					else
					{
						new MessageBox("手机不能为空", null, false, 0, 0, 100);
						phone.setFocus();
					}
					//name.setFocus();
				}
				
				if (e.keyCode == 16777217) 
				{
					code.setFocus();
				}
				if (e.keyCode == 16777218) 
				{
					name.setFocus();
				}
			}
		});
		phone.setText("");
		phone.setBounds(79, 78, 227, 23);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == 27) 
				{
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) 
				{
				
					TableItem [] items = table.getSelection();
					code.setText(items[0].getText(0));
					name.setText(items[0].getText(1));
					phone.setText(items[0].getText(2));
					tel.setText(items[0].getText(3));
					address.setText(items[0].getText(4));
					phone.setFocus();
					
					//给全局赋值 关闭窗口后再打开时显示顾客信息
					GlobalInfo.custInfoDef.custno= items[0].getText(0);
					GlobalInfo.custInfoDef.custname = items[0].getText(1);
					GlobalInfo.custInfoDef.custphone = items[0].getText(2);
					GlobalInfo.custInfoDef.custtel = items[0].getText(3);
					GlobalInfo.custInfoDef. custaddr = items[0].getText(4);
					GlobalInfo.tempDef.str3 = code.getText();
					//System.out.println(items);
				}

			}
		});

		table.addMouseListener(new MouseListener() 
		{

			public void mouseUp(MouseEvent arg0) {
			}

			public void mouseDown(MouseEvent arg0) {

			}

			// 双击鼠标获得单元格数据
			public void mouseDoubleClick(MouseEvent e) 
			{
				doubleCheck(e);
				name.setFocus();
			}
		});

		table.setToolTipText("");
		table.setBounds(39, 234, 719, 262);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setText("代码");
		tblclmnNewColumn.setWidth(100);

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("姓名");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("手机");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("固话");

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(315);
		tableColumn.setText("住址");

		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("固话：");
		label_2.setBounds(39, 159, 36, 17);

		tel = new Text(shell, SWT.BORDER);
		tel.setTextLimit(10);
		tel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == 27) {
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) 
				{
					if (null != tel.getText() && !("").equals(tel.getText())) 
					{
						Htsc_NetService netService = (Htsc_NetService) NetService
								.getDefault();
						// 调用查询结果
						Vector v = netService.getCustList(phone.getText(),
								tel.getText(), name.getText());
						// 遍历结果集
						if (v.size()>0) 
						{
							table.removeAll();
							for (int i = 0; i < v.size(); i++)
							{
								TableItem item = new TableItem(table, SWT.NONE);
								String[] items = (String[]) v.get(i);
								item.setText(items);
							}
							table.setSelection(0);
							table.setFocus();

						}
						else 
						{
							address.setFocus();
						}
					} 
					else 
					{
						address.setFocus();
					}
				}
					
					if (e.keyCode == 16777226) 
					{
						selectCode();
					}
					if (e.keyCode == 16777227) 
					{
						addInfo();
						//name.setFocus();
					}

					if (e.keyCode == 16777217) 
					{
						phone.setFocus();
					}
					if (e.keyCode == 16777218) 
					{
						address.setFocus();
					}
				
			}
		});
		tel.setText("");
		tel.setBounds(79, 153, 227, 23);
		

		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setText("住址：");
		label_3.setBounds(39, 199, 36, 17);

		address = new Text(shell, SWT.BORDER);
		address.setTextLimit(100);
		address.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == 27) {
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 13 || e.keyCode ==16777296) 
				{
					if(
							(null == name.getText()||("").equals(name.getText()))&&
							(null == tel.getText()||("").equals(tel.getText()))&&
							(null == phone.getText()||("").equals(phone.getText()))
						)
					{
						name.setFocus();
					}
					else
					{
						save.setFocus();
					}
				}
				if (e.keyCode == 16777226) 
				{
					selectCode();
				}
				if (e.keyCode == 16777227) 
				{
					addInfo();
					//name.setFocus();
				}
				
				if (e.keyCode == 16777217) 
				{
					tel.setFocus();
				}
				if (e.keyCode == 16777218) 
				{
					code.setFocus();
				}
			}
		});
		address.setText("");
		address.setBounds(79, 193, 340, 23);

		// 新增按钮
		Button add = new Button(shell, SWT.NONE);
		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Htsc_NetService netService = (Htsc_NetService) NetService
						.getDefault();
				String custNo = netService.selectCustNo();
				// 给客户代码赋值
				code.setText(custNo);
				name.setText("");
				phone.setText("");
				tel.setText("");
				address.setText("");
				table.removeAll();
				// new MessageBox("TEST");
			}
		});
		add.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 27) {
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
			}
		});
	
		add.setBounds(479, 30, 80, 27);
		add.setText("[F1]新增代码");
		// 保存
		save = new Button(shell, SWT.NONE);
		save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		save.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 27) {
					shell.close();
					new MessageBox("顾客代码："+GlobalInfo.custInfoDef.custno+"  "+
							"姓名："+GlobalInfo.custInfoDef.custname+"  "+
							"手机："+GlobalInfo.custInfoDef.custphone+"  "+
							"电话："+GlobalInfo.custInfoDef.custtel, null, false);
					return;
				}
				if (e.keyCode == 16777226) 
				{
					selectCode();
				}
				if (e.keyCode == 16777227) 
				{
					addInfo();
					name.setFocus();
				}
				
			}
		});
		save.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				addInfo();
			}
		});
		save.setText("[F2]保存");
		save.setBounds(578, 30, 80, 27);

		Button back = new Button(shell, SWT.NONE);
		back.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 27) 
				{
					shell.close();
					new MessageBox("顾客代码："+code.getText()+"  "+
					"姓名："+name.getText()+"  "+
					"手机："+phone.getText()+"  "+
					"电话："+tel.getText(), null, false);
					return;
					
				}
				
			}
		});
		back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell.close();
			}
		});
		back.setText("[ESC]退出");
		back.setBounds(678, 30, 80, 27);
		
		if(null !=GlobalInfo.custInfoDef.custphone&&!"".equals(GlobalInfo.custInfoDef.custphone))
		{
			code.setText(GlobalInfo.custInfoDef.custno);
			name.setText(GlobalInfo.custInfoDef.custname);
			phone.setText(GlobalInfo.custInfoDef.custphone);
			tel.setText(GlobalInfo.custInfoDef.custtel);
			address.setText(GlobalInfo.custInfoDef.custaddr);
		}

	}
	
	//保存或修改
	public void addInfo()
	{
		Htsc_NetService netService = (Htsc_NetService) NetService
				.getDefault();

		
		boolean custNo = netService.addOrModUserInfo(code.getText(),
				name.getText(), phone.getText(), tel.getText(),
				address.getText());
		if (!custNo) 
		{
			new MessageBox("保存操作失败", null, false, 0, 0, 100);
			name.setFocus();
		} 
		else 
		{
			new MessageBox("保存操作成功", null, false, 0, 0, 100);
			// 调用查询结果
			Vector v = netService.getCustList(phone.getText(),
					tel.getText(), name.getText());
			// 遍历结果集
			if (null == v)
			{
				new MessageBox("查询结果集失败", null, false, 0, 0, 100);

			} 
			else 
			{
				table.removeAll();
				for (int i = 0; i < v.size(); i++) 
				{
					TableItem item2 = new TableItem(table, SWT.NONE);
					String[] items = (String[]) v.get(i);
					item2.setText(items);
				}
				table.setFocus();
				table.setSelection(0);
				code.setText("");
				name.setText("");
				phone.setText("");
				tel.setText("");
				address.setText("");
				
				
				//table.forceFocus();

			}

		}
		
		

	}
	//双击后选值
	public void doubleCheck(MouseEvent e)
	{
		// 获得单元格的位置
		TableItem[] items = table.getItems();

		Point pt = new Point(e.x, e.y);
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {

				Rectangle rect = items[i].getBounds(j);
				if (rect.contains(pt)) {
					// System.out.println("第" + i + "行" + ", 第" + j +
					// "列" );
					code.setText(items[i].getText(0));
					name.setText(items[i].getText(1));
					phone.setText(items[i].getText(2));
					tel.setText(items[i].getText(3));
					address.setText(items[i].getText(4));
					// 将顾客代码给小票头的
					// System.out.println(code.getText());
					GlobalInfo.tempDef.str3 = code.getText();
					new MessageBox("设置成功", null, false, 0, 0, 100);

				}
			}

		}
	}
	
	//查询代码
	public void selectCode()
	{
		Htsc_NetService netService = (Htsc_NetService) NetService
				.getDefault();
		String custNo = netService.selectCustNo();
		// 给客户代码赋值
		code.setText(custNo);
		//name.setFocus();
		name.setText("");
		
		phone.setText("");
		
		tel.setText("");
		
		address.setText("");
		
		table.removeAll();
		
		name.setFocus();
	}
	
	/**
	  * 验证手机号码
	  * @param mobiles
	  * @return
	  */
	 public boolean isMobileNO(String mobiles){
	  boolean flag = false;
	  try
	  {
	  // PosLog.getLog(getClass()).info("isMobileNO========>"+mobiles);
	   Pattern p = Pattern.compile("^([0-9])\\d{10}$");
	   Matcher m = p.matcher(mobiles);
	   //PosLog.getLog(getClass()).info("isMobileNO========>"+m.matches());
	   flag = m.matches();
	  }
	  catch(Exception e)
	  {
	  
	   flag = false;
	  }
	  return flag;
	 }
	 
}
