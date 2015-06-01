package custom.localize.Hfhf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Hfhf_ElecMoneyForm
{
	public Shell shell = null;
	private Label Acount = null;
	private Label Money = null;

	public StyledText txtRuleInfo;
	public Text accountTxt;
	public TableEditor tEditor = null;
	public Text newEditor = null;
	public Text moneyTxt;
	public Label payName;
	public Table table;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Hfhf_PaymentElecMoney payment, SaleBS sale)
	{
		final Display display = Display.getDefault();

		createContents();

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		new Hfhf_ElecMoneyEvent(this, payment, sale);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			shell.layout();
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

	private void createContents()
	{
		shell = new Shell(GlobalVar.style_linux);
		shell.setText("Shell");
		shell.setSize(new Point(539, 435));
		shell.setLayout(null);

		Acount = new Label(shell, SWT.NONE);
		Acount.setBounds(23, 40, 96, 20);
		Acount.setText("付款帐号");
		Acount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		Money = new Label(shell, SWT.NONE);
		Money.setBounds(23, 80, 96, 20);
		Money.setText("付款金额");
		Money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		accountTxt = new Text(shell, SWT.BORDER);
		accountTxt.setBounds(122, 36, 390, 24);
		accountTxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		moneyTxt = new Text(shell, SWT.BORDER);
		moneyTxt.setBounds(122, 78, 390, 24);
		moneyTxt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		moneyTxt.setTextLimit(11);

		final Label label = new Label(shell, SWT.NONE);
		label.setBounds(23, 10, 96, 20);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("付款名称");

		payName = new Label(shell, SWT.NONE);
		payName.setBounds(122, 8, 289, 24);
		payName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		table.setBounds(10, 118, 517, 200);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tab_col_cardno = new TableColumn(table, SWT.CENTER);
		tab_col_cardno.setWidth(160);
		tab_col_cardno.setText("电子币账号");

		TableColumn tab_col_ye = new TableColumn(table, SWT.RIGHT);
		tab_col_ye.setWidth(155);
		tab_col_ye.setText("余额/可用余额");

		TableColumn tab_col_kfje = new TableColumn(table, SWT.RIGHT);
		tab_col_kfje.setWidth(100);
		tab_col_kfje.setText("可付金额");

		TableColumn tab_col_paymoney = new TableColumn(table, SWT.RIGHT);
		tab_col_paymoney.setWidth(95);
		tab_col_paymoney.setText("付款金额");

		txtRuleInfo = new StyledText(shell, SWT.BORDER);
		txtRuleInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		txtRuleInfo.setBounds(10, 324, 517, 100);
	}
}
