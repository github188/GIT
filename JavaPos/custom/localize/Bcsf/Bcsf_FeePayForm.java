package custom.localize.Bcsf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;

public class Bcsf_FeePayForm
{
	public Shell shell;
	public PosTable table_pay;
	public PosTable table_detail;
	public Text txt_paymoney;

	public CLabel lbl_paytotal;
	public CLabel lbl_unpay;

	public CLabel lbl_payname;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Bcsf_DailyFeeBS feeBS)
	{
		final Display display = Display.getDefault();
		createContents();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		new Bcsf_FeePayEvent(this, feeBS);

		if (!shell.isDisposed())
		{
			shell.open();
			shell.redraw();
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
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(600, 360);

		table_pay = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table_pay.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		table_pay.setBounds(7, 10, 253, 275);
		table_pay.setHeaderVisible(true);
		table_pay.setLinesVisible(true);

		table_detail = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table_detail.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		table_detail.setBounds(266, 98, 320, 220);
		table_detail.setHeaderVisible(true);
		table_detail.setLinesVisible(true);

		CLabel lblNewLabel = new CLabel(shell, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NORMAL));
		lblNewLabel.setBounds(266, 10, 110, 30);
		lblNewLabel.setText("应缴金额");

		lbl_paytotal = new CLabel(shell, SWT.NONE);
		lbl_paytotal.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lbl_paytotal.setAlignment(SWT.RIGHT);
		lbl_paytotal.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NORMAL));
		lbl_paytotal.setText("10000000");
		lbl_paytotal.setBounds(396, 10, 185, 30);

		CLabel label_1 = new CLabel(shell, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NORMAL));
		label_1.setText("剩余金额");
		label_1.setBounds(266, 48, 110, 30);

		lbl_unpay = new CLabel(shell, SWT.NONE);
		lbl_unpay.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_unpay.setAlignment(SWT.RIGHT);
		lbl_unpay.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NORMAL));
		lbl_unpay.setText("10000000");
		lbl_unpay.setBounds(396, 48, 185, 30);

		lbl_payname = new CLabel(shell, SWT.NONE);
		lbl_payname.setAlignment(SWT.CENTER);
		lbl_payname.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NORMAL));
		lbl_payname.setText("人民币");
		lbl_payname.setBounds(8, 289, 95, 30);

		txt_paymoney = new Text(shell, SWT.BORDER);
		txt_paymoney.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NORMAL));
		txt_paymoney.setBounds(109, 289, 150, 30);
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
