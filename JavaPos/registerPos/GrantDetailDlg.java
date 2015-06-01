package registerPos;

import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.Sqldb;
import com.swtdesigner.SWTResourceManager;

/**
 * @author yinl
 * @create 2010-2-23 下午05:12:47
 * @descri 文件说明
 */

public class GrantDetailDlg extends Dialog
{
	private Table tbGrant;
	private Table tbMarket;
	protected Object result;
	protected Shell shell;
	private Sqldb sql;
	private String projcode;
	private String projname;
	 
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public GrantDetailDlg(Shell parent, int style)
	{
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public GrantDetailDlg(Shell parent)
	{
		this(parent, SWT.NONE);
	}
	
	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open(Sqldb db,String code,String name)
	{
		createContents();

		//
		this.sql = db;
		this.projcode = code;
		this.projname = name;
		shell.setText("["+projcode+"] " + projname);
		refushTable("MKT");
		
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

	private void refushTable(String mode)
	{
		try
		{
			if (mode.equalsIgnoreCase("MKT"))
			{
				tbMarket.removeAll();
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("select mktcode,max(mktname),count(regcode),max(createdate) from regcode where projectcode='"+projcode+"' and regflag='REGCODE' group by mktcode");
				sqlstr.append(" union ");
				sqlstr.append("select '有效期',max(substr(regmemo,1,10)),count(regcode),max(createdate) from regcode where projectcode='"+projcode+"' and regflag='REGDATE' order by mktcode");
				ResultSet rs = sql.selectData(sqlstr.toString());
				if (rs != null)
				{
					while(rs.next())
					{
						TableItem item = new TableItem(tbMarket, SWT.NONE);
						item.setText(0, "["+(rs.getString(1)!=null?rs.getString(1):"")+"] "+(rs.getString(2)!=null?rs.getString(2):""));
						item.setText(1, rs.getString(3)!=null?rs.getString(3):"");
						item.setText(2, rs.getString(4)!=null?rs.getString(4):"");
					}
					rs.close();
					if (tbMarket.getItemCount() > 0)
					{
						tbMarket.select(0);
						refushTable("REG");
					}
				}				
			}
			else
			{
				int index = tbMarket.getSelectionIndex();
				if (index < 0) return;
				TableItem mktitem = tbMarket.getItem(index);
				String mktcode = Convert.codeInString(mktitem.getText(0),'[');
				tbGrant.removeAll();
				ResultSet rs = null;
				if (mktcode.equals("有效期"))
				{
					rs = sql.selectData("select regcode,mktmodul,regmemo,createdate from regcode where projectcode='"+projcode+"' and regflag='REGDATE' order by createdate");
				}
				else
				{
					rs = sql.selectData("select regcode,mktmodul,mktname,createdate from regcode where projectcode='"+projcode+"' and mktcode = '"+mktcode+"' and regflag='REGCODE' order by substr(regcode,25,5)");
				}
				if (rs != null)
				{
					int i = 0;
					while(rs.next())
					{
						TableItem item = new TableItem(tbGrant, SWT.NONE);
						item.setText(0, String.valueOf(++i));
						item.setText(1, rs.getString(1)!=null?rs.getString(1):"");
						item.setText(2, rs.getString(2)!=null?rs.getString(2):"");
						item.setText(3, rs.getString(3)!=null?rs.getString(3):"");
						item.setText(4, rs.getString(4)!=null?rs.getString(4):"");
					}
					rs.close();
				}
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
		shell.setSize(764, 531);
		shell.setText("授权");

		tbMarket = new Table(shell, SWT.BORDER|SWT.FULL_SELECTION);
		tbMarket.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				refushTable("REG");
			}
		});
		tbMarket.setLinesVisible(true);
		tbMarket.setHeaderVisible(true);
		tbMarket.setBounds(8, 10, 303, 452);

		final TableColumn newColumnTableColumn = new TableColumn(tbMarket, SWT.NONE);
		newColumnTableColumn.setAlignment(SWT.CENTER);
		newColumnTableColumn.setWidth(188);
		newColumnTableColumn.setText("门店信息");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tbMarket, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(85);
		newColumnTableColumn_3.setText("授权注册数");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tbMarket, SWT.NONE);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText("最近授权时间");

		tbGrant = new Table(shell, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		tbGrant.setBounds(317, 10, 434, 452);
		tbGrant.setLinesVisible(true);
		tbGrant.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_5 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_5.setWidth(40);
		newColumnTableColumn_5.setText("行号");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(260);
		newColumnTableColumn_4.setText("注册码");

		final TableColumn newColumnTableColumn_1_1 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_1_1.setAlignment(SWT.CENTER);
		newColumnTableColumn_1_1.setWidth(75);
		newColumnTableColumn_1_1.setText("模块号");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_1.setWidth(200);
		newColumnTableColumn_1.setText("门店信息");

		final TableColumn newColumnTableColumn_6 = new TableColumn(tbGrant, SWT.NONE);
		newColumnTableColumn_6.setWidth(140);
		newColumnTableColumn_6.setText("授权时间");

		final Button buttonClose = new Button(shell, SWT.NONE);
		buttonClose.setBounds(8, 469, 91, 28);
		buttonClose.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		buttonClose.setText("关闭本窗口");
		buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				shell.close();
				shell.dispose();
			}
		});		

		final Button btnDelReg = new Button(shell, SWT.NONE);
		btnDelReg.setBounds(634, 469, 117, 28);
		btnDelReg.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnDelReg.setText("删除选中注册项");
		btnDelReg.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				TableItem[] items = tbGrant.getSelection();
				if (items == null || items.length <= 0) return;
				MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				mb.setMessage("你确定要删除选中的 " + items.length + " 个注册项吗？");
				if (mb.open() != SWT.YES) return;
				mb.setMessage("删除注册项后将无法恢复,你确定要删除吗？");
				if (mb.open() != SWT.YES) return;

				sql.beginTrans();
				int i = 0,n=0;
				for (;i<items.length;i++)
				{
					if (!sql.executeSql("delete from regcode where projectcode='"+projcode+"' and regcode = '"+items[i].getText(1)+"'")) break;
					else n += sql.getAffectRow(); 
				}
				if (i >= items.length)
				{
					mb.setMessage("共删除了 "+ n + " 个注册项\n\n你确定要提交删除操作吗?");
					if (mb.open() == SWT.YES) 
					{
						sql.commitTrans();
						refushTable("REG");						
					}
					else sql.rollbackTrans();
				}
				else
				{
					sql.rollbackTrans();
				}
			}
		});
		
		final Button btnDelMkt = new Button(shell, SWT.NONE);
		btnDelMkt.setBounds(511, 469, 117, 28);
		btnDelMkt.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NONE));
		btnDelMkt.setText("删除已注册门店");
		btnDelMkt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				int index = tbMarket.getSelectionIndex();
				if (index < 0) return;
				TableItem mktitem = tbMarket.getItem(index);
				String mktcode = Convert.codeInString(mktitem.getText(0),'[');
				if (mktcode.equals("有效期")) return;
				MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				mb.setMessage("你确定要删除以下门店已生成的注册码吗？\n\n"+mktitem.getText(0));
				if (mb.open() != SWT.YES) return;
				mb.setMessage("删除已生成门店的注册码后将无法恢复,你确定要删除吗？");
				if (mb.open() != SWT.YES) return;

				sql.beginTrans();
				if (sql.executeSql("delete from regcode where projectcode='"+projcode+"' and mktcode = '"+mktcode+"' and regflag='REGCODE'"))
				{
					mb.setMessage(mktitem.getText(0) + "\n\n被删除 "+ sql.getAffectRow() + " 个注册码\n\n你确定要提交删除操作吗?");
					if (mb.open() == SWT.YES)
					{
						sql.commitTrans();
						refushTable("MKT");
					}
					else sql.rollbackTrans();
				}
			}
		});		
	}
}
