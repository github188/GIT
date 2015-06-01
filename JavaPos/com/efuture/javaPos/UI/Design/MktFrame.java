package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.efuture.javaPos.Global.GlobalVar;

public class MktFrame extends Composite
{

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public MktFrame(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FormLayout());
		this.setSize(800, 600);
		final Browser browser = new Browser(this, SWT.NONE);
		final FormData fd_browser_1 = new FormData();
		fd_browser_1.left = new FormAttachment(0, 0);
		fd_browser_1.top = new FormAttachment(0, 0);
		fd_browser_1.bottom = new FormAttachment(100, -20);
		fd_browser_1.right = new FormAttachment(100, -33);
		browser.setLayoutData(fd_browser_1);
		browser.setBounds(10, 10,901, 506);
		final FormData fd_browser = new FormData();
		fd_browser.bottom = new FormAttachment(0, 516);
		fd_browser.top = new FormAttachment(0, 10);
		fd_browser.right = new FormAttachment(0, 924);
		fd_browser.left = new FormAttachment(0, 10);
		//browser.setLayoutData(fd_browser);
		//browser.setUrl("http://172.17.6.126:8080/JavaPosManagerMaka/#0301");
		browser.setUrl("http://127.0.0.1:8080/congou/Main.html");
		
		this.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);
		//browser.setBounds(0, 0, this.getBounds().width, this.getBounds().height);
		//
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
