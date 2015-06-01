package posserver.Configure.SysConfig;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import posserver.Configure.CmdDef.CmdDefStruct;

public class FrmTabList
{
	Vector VInitValue;
	Vector VSelectVal;
	
	Shell parentshell;
	
	/**
	 * Open the window
	 */
	public void open(Vector initvalue,Vector selectval,Shell parentshe) 
	{
		VInitValue = initvalue;
		VSelectVal = selectval;
		parentshell = parentshe;
		
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	/*
	 * 
	 */
	
	
	private Table tabListCmDef;
	private Shell shell = null;

	/**
	 * This method initializes sShell
	 */
	private void createContents()
	{
		shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setLayout(new FillLayout());
		shell.setText("编辑");
		shell.setSize(new Point(309, 466));
		Rectangle rctparent = parentshell.getBounds();
		Rectangle rct = shell.getBounds();
		
		rct.x = rctparent.x + rctparent.width / 2 - rct.width /2;
		rct.y = rctparent.y + rctparent.height / 2 - rct.height /2;
		shell.setBounds(rct);
		
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());

		tabListCmDef = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		final FormData fd_tabListCmdText = new FormData();
		fd_tabListCmdText.bottom = new FormAttachment(0, 395);
		fd_tabListCmdText.right = new FormAttachment(100, -5);
		fd_tabListCmdText.top = new FormAttachment(0, 5);
		fd_tabListCmdText.left = new FormAttachment(0, 5);
		tabListCmDef.setLayoutData(fd_tabListCmdText);
		tabListCmDef.setLinesVisible(true);
		tabListCmDef.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_2_2 = new TableColumn(tabListCmDef, SWT.NONE);
		newColumnTableColumn_2_2.setWidth(73);
		newColumnTableColumn_2_2.setText("代码");

		final TableColumn newColumnTableColumn_2_3 = new TableColumn(tabListCmDef, SWT.NONE);
		newColumnTableColumn_2_3.setWidth(216);
		newColumnTableColumn_2_3.setText("描述");
		
		for (int i = 0;i < VInitValue.size();i++)
		{
			CmdDefStruct cds = (CmdDefStruct)VInitValue.get(i);
			
			TableItem ti = new TableItem(tabListCmDef,SWT.NULL);
			ti.setText(new String[]{cds.CmdCode,cds.CmdMemo});
		
			for (int j = 0 ;j < VSelectVal.size();j++)
			{
				String str = (String)VSelectVal.get(j);
				if (str.equals(cds.CmdCode))
				{
					ti.setChecked(true);
				}
			}
		}
		
		final Button btnOk = new Button(composite, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnOkClick();
			}
		});
		final FormData fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(0, 425);
		fd_btnOk.top = new FormAttachment(0, 404);
		fd_btnOk.right = new FormAttachment(0, 234);
		fd_btnOk.left = new FormAttachment(0, 178);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("确定");

		final Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnCancelClick();
			}
		});
		final FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(0, 425);
		fd_btnCancel.top = new FormAttachment(0, 404);
		fd_btnCancel.right = new FormAttachment(0, 298);
		fd_btnCancel.left = new FormAttachment(0, 242);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("取消");

		final Button btnAllSel = new Button(composite, SWT.NONE);
		btnAllSel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
				for (int i = 0;i < tabListCmDef.getItemCount(); i++)
				{
					TableItem ti = tabListCmDef.getItem(i);
					
					ti.setChecked(true);
				}
			}
		});
		final FormData fd_btnAllSel = new FormData();
		fd_btnAllSel.bottom = new FormAttachment(0, 425);
		fd_btnAllSel.top = new FormAttachment(0, 404);
		fd_btnAllSel.right = new FormAttachment(0, 62);
		fd_btnAllSel.left = new FormAttachment(0, 6);
		btnAllSel.setLayoutData(fd_btnAllSel);
		btnAllSel.setText("全选");

		final Button btnFsel = new Button(composite, SWT.NONE);
		btnFsel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				for (int i = 0;i < tabListCmDef.getItemCount(); i++)
				{
					TableItem ti = tabListCmDef.getItem(i);
					
					ti.setChecked(!ti.getChecked());
				}
			}
		});
		final FormData fd_btnFsel = new FormData();
		fd_btnFsel.bottom = new FormAttachment(0, 425);
		fd_btnFsel.top = new FormAttachment(0, 404);
		fd_btnFsel.right = new FormAttachment(0, 126);
		fd_btnFsel.left = new FormAttachment(0, 70);
		btnFsel.setLayoutData(fd_btnFsel);
		btnFsel.setText("返选");
	}

	public void btnOkClick()
	{
		this.VSelectVal.clear();
		for (int i = 0;i < VInitValue.size();i++)
		{
			CmdDefStruct cds = (CmdDefStruct)VInitValue.get(i);
			
			TableItem ti = this.tabListCmDef.getItem(i);
			
			if (ti.getChecked())
			{
				VSelectVal.add(cds.CmdCode);
			}
		}
		
		shell.close();
	}
	
	public void btnCancelClick()
	{
		shell.close();
	}
}
