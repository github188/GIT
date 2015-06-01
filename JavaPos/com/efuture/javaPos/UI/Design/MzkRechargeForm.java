package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.MzkRechargeEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class MzkRechargeForm
{
	public Shell sShell;
	private Label lbl_cardno;
	private Label lbl_money;
	public StyledText status;
	public Text txt_cardno;
	public Text txt_money;
	public Label lbl_name;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open()
	{
		Display display = Display.getDefault();

		createSShell();

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, sShell);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, sShell, null);

		new MzkRechargeEvent(this);

		if (!sShell.isDisposed())
		{
			sShell.open();
			sShell.setActive();
		}

		while (!sShell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell()
	{
		sShell = new Shell(GlobalVar.style_linux);
		sShell.setLayout(new FormLayout());
		sShell.setText("Shell");
		sShell.setSize(new org.eclipse.swt.graphics.Point(436, 314));

		final Label label = new Label(sShell, SWT.NONE);
		final FormData formData_7 = new FormData();
		formData_7.bottom = new FormAttachment(0, 30);
		formData_7.top = new FormAttachment(0, 10);
		formData_7.right = new FormAttachment(0, 119);
		formData_7.left = new FormAttachment(0, 23);
		label.setLayoutData(formData_7);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("业务名称"));

		lbl_cardno = new Label(sShell, SWT.NONE);
		final FormData fd_lbl_cardno = new FormData();
		fd_lbl_cardno.bottom = new FormAttachment(0, 60);
		fd_lbl_cardno.top = new FormAttachment(0, 40);
		fd_lbl_cardno.right = new FormAttachment(0, 119);
		fd_lbl_cardno.left = new FormAttachment(0, 23);
		lbl_cardno.setLayoutData(fd_lbl_cardno);
		lbl_cardno.setText(Language.apply("充值卡号"));
		lbl_cardno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_money = new Label(sShell, SWT.NONE);
		final FormData fd_lbl_money = new FormData();
		fd_lbl_money.top = new FormAttachment(lbl_cardno, 12);
		fd_lbl_money.left = new FormAttachment(0, 23);
		fd_lbl_money.bottom = new FormAttachment(0, 92);
		fd_lbl_money.right = new FormAttachment(0, 119);
		lbl_money.setLayoutData(fd_lbl_money);
		lbl_money.setText(Language.apply("充值金额"));
		lbl_money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		status = new StyledText(sShell, SWT.BORDER);
		final FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(0, 291);
		formData_3.top = new FormAttachment(0, 110);
		formData_3.right = new FormAttachment(0, 411);
		formData_3.left = new FormAttachment(0, 23);
		status.setLayoutData(formData_3);
		status.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		status.setEditable(false);

		txt_cardno = new Text(sShell, SWT.BORDER);
		final FormData fd_txt_cardno = new FormData();
		fd_txt_cardno.bottom = new FormAttachment(0, 60);
		fd_txt_cardno.top = new FormAttachment(0, 36);
		fd_txt_cardno.right = new FormAttachment(0, 411);
		fd_txt_cardno.left = new FormAttachment(0, 122);
		txt_cardno.setLayoutData(fd_txt_cardno);
		txt_cardno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		txt_money = new Text(sShell, SWT.BORDER);
		final FormData fd_txt_money = new FormData();
		fd_txt_money.top = new FormAttachment(lbl_cardno, 10);
		fd_txt_money.left = new FormAttachment(0, 122);
		fd_txt_money.bottom = new FormAttachment(0, 94);
		fd_txt_money.right = new FormAttachment(0, 411);
		txt_money.setLayoutData(fd_txt_money);
		txt_money.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txt_money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txt_money.setTextLimit(7);

		lbl_name = new Label(sShell, SWT.NONE);
		final FormData fd_lbl_name = new FormData();
		fd_lbl_name.bottom = new FormAttachment(0, 32);
		fd_lbl_name.top = new FormAttachment(0, 8);
		fd_lbl_name.right = new FormAttachment(0, 411);
		fd_lbl_name.left = new FormAttachment(0, 122);
		lbl_name.setLayoutData(fd_lbl_name);
		lbl_name.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	}
}
