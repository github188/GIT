package custom.localize.Gbyw;

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
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Gbyw_PaymentMzkBankForm
{
	public Shell sShell = null;
	public Label Acount = null;
	public Label YeTips = null;
	public Label Money = null;
	public Label lableMoney;
	public StyledText status = null;
	public Text AccountTxt = null;
	public Text txtAcount;
	public Text Yetxt = null;
	public Text Moneytxt = null;
	public Text txtMoney;
	public Label payName;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Gbyw_PaymentBankMzk pay, SaleBS sale)
	{
		Display display = Display.getDefault();

		createSShell();

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, sShell);

		new Gbyw_PaymentMzkBankEvent(this, pay, sale);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, sShell, null);

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

		Acount = new Label(sShell, SWT.NONE);
		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 23);
		formData.bottom = new FormAttachment(0, 62);
		formData.right = new FormAttachment(0, 119);
		Acount.setLayoutData(formData);
		Acount.setText("卡    号");
		Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lableMoney = new Label(sShell, SWT.NONE);
		final FormData fd_lableMoney = new FormData();
		fd_lableMoney.top = new FormAttachment(Acount, 16);
		fd_lableMoney.left = new FormAttachment(0, 23);
		fd_lableMoney.right = new FormAttachment(0, 119);
		lableMoney.setLayoutData(fd_lableMoney);
		lableMoney.setText("付款金额");
		lableMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		status = new StyledText(sShell, SWT.BORDER);
		fd_lableMoney.bottom = new FormAttachment(status, -19);
		final FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(0, 300);
		formData_3.top = new FormAttachment(0, 117);
		formData_3.right = new FormAttachment(0, 411);
		formData_3.left = new FormAttachment(0, 23);
		status.setLayoutData(formData_3);
		status.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		status.setEditable(false);
		status.setEnabled(false);

		txtAcount = new Text(sShell, SWT.BORDER);
		final FormData fd_txtAcount = new FormData();
		fd_txtAcount.left = new FormAttachment(Acount, 3);
		fd_txtAcount.bottom = new FormAttachment(0, 63);
		fd_txtAcount.right = new FormAttachment(0, 411);
		txtAcount.setLayoutData(fd_txtAcount);
		txtAcount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMoney = new Text(sShell, SWT.BORDER);
		final FormData fd_txtMoney = new FormData();
		fd_txtMoney.bottom = new FormAttachment(status, -17);
		fd_txtMoney.top = new FormAttachment(txtAcount, 13);
		fd_txtMoney.left = new FormAttachment(0, 122);
		fd_txtMoney.right = new FormAttachment(0, 411);
		txtMoney.setLayoutData(fd_txtMoney);
		txtMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMoney.setTextLimit(11);

		final Label label = new Label(sShell, SWT.NONE);
		final FormData formData_7 = new FormData();
		formData_7.bottom = new FormAttachment(0, 30);
		formData_7.top = new FormAttachment(0, 10);
		formData_7.right = new FormAttachment(0, 119);
		formData_7.left = new FormAttachment(0, 23);
		label.setLayoutData(formData_7);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("付款名称");

		payName = new Label(sShell, SWT.NONE);
		formData.top = new FormAttachment(payName, 10);
		fd_txtAcount.top = new FormAttachment(payName, 7);
		final FormData formData_8 = new FormData();
		formData_8.bottom = new FormAttachment(0, 32);
		formData_8.top = new FormAttachment(0, 8);
		formData_8.right = new FormAttachment(0, 411);
		formData_8.left = new FormAttachment(0, 122);
		payName.setLayoutData(formData_8);
		payName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final FormData formData_9 = new FormData();
		formData_9.bottom = new FormAttachment(0, 104);
		formData_9.top = new FormAttachment(0, 80);
		formData_9.right = new FormAttachment(0, 429);
		formData_9.left = new FormAttachment(0, 140);

		if (ConfigClass.MouseMode)
		{
			ControlBarForm ctrlform = new ControlBarForm(sShell, SWT.NONE, GlobalVar.ConfigPath + "\\PaymentMzkForm.ini");
			ctrlform.setControlBarForm();
		}
	}
}
