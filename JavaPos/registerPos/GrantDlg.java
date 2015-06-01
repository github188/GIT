package registerPos;

import java.io.IOException;
import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xvolks.jnative.misc.SHELLEXECUTEINFO;
import org.xvolks.jnative.util.Shell32;
import org.xvolks.jnative.util.constants.winuser.WindowsConstants;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.swtdesigner.SWTResourceManager;

/**
 * @author yinl
 * @create 2010-2-23 下午05:12:47
 * @descri 文件说明
 */

public class GrantDlg extends Dialog
{
	private RegDone doneDlg;
	private StyledText styledText;
	private Table tbProj;
	private Text textProjName;
	private Text textProjCode;
	protected Object result;
	protected Shell shell;
	private Sqldb sql;
	
	 
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public GrantDlg(Shell parent, int style)
	{
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public GrantDlg(Shell parent)
	{
		this(parent, SWT.NONE);
	}

	public void setDoneDialog(RegDone dlg)
	{
		this.doneDlg = dlg;
	}
	
	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open()
	{
		createContents();
        
		// 连接数据库
		shell.setText(doneDlg.dbpath);
		sql = new Sqldb("org.sqlite.JDBC","jdbc:sqlite:" + doneDlg.dbpath);
		if (!sql.isOpen())
		{
			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
			mb.setMessage("授权数据库 " + doneDlg.dbpath + " 打开失败!");
			mb.open();
			return result;
		}

		// 设置提示信息
		String str = "";
		str += "注册码从 " + doneDlg.spinnerStart.getSelection() + " 到 " + doneDlg.spinnerEnd.getSelection() + " 共 " + 
				(doneDlg.spinnerEnd.getSelection() - doneDlg.spinnerStart.getSelection() + 1) + " 个";
		if (doneDlg.textCustomer.getText().length() <= 0) str += ",试用到 " + doneDlg.textMarket.getText().trim();
		str += "\n";
		str += "有效期至 " + doneDlg.textMaxDate.getText().trim();
		styledText.setText(str);

		textProjCode.setText(doneDlg.lastprojcode); 
		textProjName.setText(doneDlg.lastprojname); 
		
		// 刷新列表
		refushTable(null,"code");
		
		//
		Rectangle rec = getParent().getBounds();
        shell.setLocation(rec.x + (rec.width - shell.getBounds().width) / 2,
                          rec.y + (rec.height - shell.getBounds().height )/ 2);
        
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}

	private void refushTable(String cond,String mode)
	{
		try
		{
			tbProj.removeAll();
			String str = "select projectcode,max(projectname),count(regcode),max(createdate) from regcode where ";
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
	
	/**
	 * Create contents of the dialog
	 */
	protected void createContents()
	{
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(546, 449);
		shell.setText("授权");

		final Label label = new Label(shell, SWT.NONE);
		label.setBounds(8, 75, 91, 30);
		label.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label.setText("项目编号:");

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(8, 115, 91, 30);
		label_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1.setText("项目名称:");

		textProjCode = new Text(shell, SWT.BORDER);
		textProjCode.setBounds(104, 78, 384, 25);
		textProjCode.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		textProjName = new Text(shell, SWT.BORDER);
		textProjName.setBounds(104, 118, 384, 25);
		textProjName.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		tbProj = new Table(shell, SWT.BORDER|SWT.FULL_SELECTION);
		tbProj.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				TableItem item = tbProj.getItem(tbProj.getSelectionIndex());
				textProjCode.setText(item.getText(0));
				textProjName.setText(item.getText(1));
			}
		});
		tbProj.setLinesVisible(true);
		tbProj.setHeaderVisible(true);
		tbProj.setBounds(8, 151, 522, 226);
		
		final TableColumn newColumnTableColumn = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn.setWidth(140);
		newColumnTableColumn.setText("项目编号");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_1.setWidth(140);
		newColumnTableColumn_1.setText("项目名称");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_3.setWidth(80);
		newColumnTableColumn_3.setText("授权注册数");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tbProj, SWT.NONE);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText("上次授权时间");

		final Button btnRegCode = new Button(shell, SWT.NONE);
		btnRegCode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (textProjCode.getText().trim().length() <= 0 || textProjName.getText().trim().length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("项目编号和项目名称不能为空!");
					mb.open();
					return;					
				}
				
				MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				String str = "注册资料\n\n门店名称: "+doneDlg.textCustomer.getText().trim()+"\n门店编号: "+doneDlg.textMarket.getText().trim() +
							 "\n客户模块: "+doneDlg.textModul.getText().trim()+
							 "\n注册码共: "+(doneDlg.spinnerEnd.getSelection() - doneDlg.spinnerStart.getSelection() + 1)+" 个" +
							 "\n\n你确定生成注册码并登记到授权数据库吗？";
				mb.setMessage(str);
				if (mb.open() != SWT.YES) return;
				
				try
				{
					java.io.FileWriter f = new java.io.FileWriter("regreport.txt",true);
					f.write("######################################\r\n");
					f.write("注册资料\r\n");
					f.write("门店名称: "+doneDlg.textCustomer.getText().trim()+"\r\n");
					f.write("门店编号: "+doneDlg.textMarket.getText().trim()+"\r\n");
					StringBuffer sb= new StringBuffer();
					if (doneDlg.chkbtn_1.getSelection() && doneDlg.chkbtn_1.getText().trim().length() > 0) sb.append(doneDlg.chkbtn_1.getText() +"、");
					if (doneDlg.chkbtn_2.getSelection() && doneDlg.chkbtn_2.getText().trim().length() > 0) sb.append(doneDlg.chkbtn_2.getText() +"、");
					if (doneDlg.chkbtn_4.getSelection() && doneDlg.chkbtn_4.getText().trim().length() > 0) sb.append(doneDlg.chkbtn_4.getText() +"、");
					if (doneDlg.chkbtn_8.getSelection() && doneDlg.chkbtn_8.getText().trim().length() > 0) sb.append(doneDlg.chkbtn_8.getText() +"、");
					if (doneDlg.chkbtn_16.getSelection()&& doneDlg.chkbtn_16.getText().trim().length()> 0) sb.append(doneDlg.chkbtn_16.getText()+"、");
					if (sb.length() > 0) f.write("授权模块: "+sb.substring(0,sb.length()-1)+"\r\n");
					f.write("注册码共: "+(doneDlg.spinnerEnd.getSelection() - doneDlg.spinnerStart.getSelection() + 1)+" 个\r\n");
					
					// 生成注册码到数据库
					String regcode="",strCustomer,strMarket,strKey;
					strCustomer = doneDlg.textCustomer.getText().trim();
					strMarket = doneDlg.textMarket.getText().trim();
					strKey = doneDlg.textModul.getText().trim();
					int n = 0;
					sql.beginTrans();
					for(int i=doneDlg.spinnerStart.getSelection();i<=doneDlg.spinnerEnd.getSelection();i++)
					{
						regcode = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,String.valueOf(i),strKey);
						
						// 临时注册码,strMarket = 有效期(YYYYMMDD)
						if (strCustomer == null || strCustomer.length() <= 0)
						{
							regcode += "-" + strMarket;
						}
						
						str = "insert into regcode(projectcode,projectname,createdate,mktname,mktcode,mktmodul,regcode,regflag,regmemo) values(?,?,?,?,?,?,?,?,?)";
						boolean ok = false;
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
							if (sql.executeSql()) ok = true;
						}
						n++;
						if (!ok)
						{
							mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
							mb.setMessage("登记第 " + n + " 个注册码授权失败!\n\n"+ regcode+"\n\n你要继续登记剩下的注册码吗？");
							if (mb.open() != SWT.YES) break;
						}
						
						f.write(regcode+"\r\n");
					}
					sql.commitTrans();
					f.write("======================================\r\n\r\n");
					f.close();

					// 提示
					if (n == (doneDlg.spinnerEnd.getSelection() - doneDlg.spinnerStart.getSelection() + 1))
					{
						mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);
						mb.setMessage("成功登记 " + n + " 个注册码授权!");
						mb.open();
					}
					
					// 刷新TABLE
					refushTable(textProjCode.getText().trim(),"CODE");
				}
				catch(Exception ex)
				{
					mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);		
					mb.setMessage("登记注册码授权异常!\n\n"+ ex.getMessage());
					mb.open();
				}
			}
		});
		btnRegCode.setBounds(202, 383, 92, 30);
		btnRegCode.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnRegCode.setText("授权注册码");

		final Button buttonClose = new Button(shell, SWT.NONE);
		buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (sql != null && sql.isOpen()) sql.Close();
				doneDlg.lastprojcode = textProjCode.getText().trim();
				doneDlg.lastprojname = textProjName.getText().trim();
				
				shell.close();
				shell.dispose();				
			}
		});
		buttonClose.setBounds(475, 383, 55, 30);
		buttonClose.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		buttonClose.setText("退出");

		styledText = new StyledText(shell, SWT.READ_ONLY | SWT.BORDER);
		styledText.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		styledText.setText("注册码: 从 63001 到 63002 共 100 个\n有效期: 至 2010-01-01,15");
		styledText.setBounds(8, 10, 522, 58);

		final Button btnMaxDate = new Button(shell, SWT.NONE);
		btnMaxDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (textProjCode.getText().trim().length() <= 0 || textProjName.getText().trim().length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("项目编号和项目名称不能为空!");
					mb.open();
					return;					
				}
				
				String regcode,strMaxDate,strCustomer,strMarket,strKey;
				strMaxDate = doneDlg.textMaxDate.getText().trim();
				strKey = doneDlg.textModul.getText().trim();
				strCustomer = doneDlg.textCustomer.getText().trim();
				strMarket = doneDlg.textMarket.getText().trim();
				//strKey = ManipulatePrecision.getRegisterCode(strCustomer,strMarket,"99999",strKey);
				if (strKey == null || strKey.length() <= 0)
				{
					MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					mb.setMessage("模块编号不能为空");
					mb.open();
					return;
				}
				
				MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				String str = "注册资料\n\n门店名称: "+doneDlg.textCustomer.getText().trim()+"\n门店编号: "+doneDlg.textMarket.getText().trim() +
							 "\n客户模块: "+doneDlg.textModul.getText().trim()+"\n有效期至: "+doneDlg.textMaxDate.getText().trim()+
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
					f.write("有效期至: "+doneDlg.textMaxDate.getText().trim()+"\r\n");
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
		});
		btnMaxDate.setBounds(105, 383, 92, 30);
		btnMaxDate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnMaxDate.setText("授权有效期");
        
        Button btnDelReport = new Button(shell, SWT.NONE);
        btnDelReport.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent arg0) {
        		PathFile.deletePath("regreport.txt");
        		
        		MessageBox mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);		
				mb.setMessage("已删除上次授权报告文件!");
				mb.open();
        	}
        });
        btnDelReport.setBounds(7, 383, 92, 30);
        btnDelReport.setText("创建新报告");
        btnDelReport.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));
        
		final Button btnReport = new Button(shell, SWT.NONE);
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
		btnReport.setBounds(300, 383, 92, 30);
		btnReport.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnReport.setText("查本次报告");

		final Button btnProjCode = new Button(shell, SWT.NONE);
		btnProjCode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushTable(textProjCode.getText().trim(),"CODE");
				textProjCode.selectAll();
				textProjCode.setFocus();
			}
		});
		btnProjCode.setText("查询");
		btnProjCode.setBounds(494, 76, 36, 27);

		final Button btnProjName = new Button(shell, SWT.NONE);
		btnProjName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushTable(textProjName.getText().trim(),"NAME");
				textProjName.selectAll();	
				textProjName.setFocus();
			}
		});
		btnProjName.setBounds(494, 116, 36, 27);
		btnProjName.setText("查询");

		final Button buttonDB = new Button(shell, SWT.NONE);
		buttonDB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{		
	            try
				{
					SHELLEXECUTEINFO lpExecInfo = new SHELLEXECUTEINFO();   
					lpExecInfo.lpVerb = "open";   
		            lpExecInfo.lpFile = doneDlg.dbpath;
		            lpExecInfo.nShow = WindowsConstants.SW_SHOWNORMAL;  
					Shell32.ShellExecuteEx(lpExecInfo);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}			
			}
		});
		buttonDB.setBounds(412, 383, 60, 30);
		buttonDB.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		buttonDB.setText("数据库");
	}
}
