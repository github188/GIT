package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import custom.localize.Nxmx.Nxmx_SaleFormTouch;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class PosFormFuncTab extends Composite
{

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */

	SaleForm saleForm = null;

	public PosFormFuncTab(Composite parent, int style)
	{
		super(parent, style);

		this.setLayout(new FormLayout());

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		final FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, 0);
		fd_composite.right = new FormAttachment(100, 0);
		fd_composite.top = new FormAttachment(0, 0);
		fd_composite.left = new FormAttachment(0, 0);
		tabFolder.setLayoutData(fd_composite);
		// tabFolder.setBounds(24, 20, 321, 244);

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText(Language.apply("收银"));

		saleForm = new Nxmx_SaleFormTouch(this,tabFolder, SWT.NONE);
		// saleForm = new SaleForm(tabFolder,SWT.NONE);
		tbtmNewItem.setControl(saleForm);
		// composite1.setSize(400,400);

		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText(Language.apply("门店管理"));

		tbtmNewItem_1.setControl(new MktFrame(tabFolder, SWT.NONE));

		this.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);

	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public SaleForm getSaleForm()
	{
		return saleForm;

	}

}
