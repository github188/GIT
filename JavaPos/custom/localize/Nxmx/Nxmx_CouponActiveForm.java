package custom.localize.Nxmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Nxmx_CouponActiveForm
{
	public Table table = null;
	public StyledText txtStatus = null;
	public Text txtlimitMoney = null;
	public Text txtcouponCode = null;
	public Label account = null;
	private String limitje= "";
	public boolean isActiveFromMenu = false;

	public Shell shell = null;

	public Nxmx_CouponActiveForm()
	{

	}

	public void open(String memo)
	{
		if (memo.equals(""))
			return;
		
		if (memo.indexOf("#")>-1)
			isActiveFromMenu = false;
		else if (memo.indexOf("%")>-1)
			isActiveFromMenu = true;
		
		if(!isActiveFromMenu)
			limitje = memo.split("#")[1].trim();

		final Display display = Display.getDefault();
		createContents();

		// 创建触屏操作按钮栏
		// ControlBarForm.createMouseControlBar(this,shell);

		new Nxmx_CouponActiveEvent(this);

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
				display.sleep();
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);
	}

	protected void createContents()
	{

		shell = new Shell(SWT.SYSTEM_MODAL);
		// Rectangle area =
		// Display.getDefault().getPrimaryMonitor().getClientArea();

		shell.setSize(490, 420);

		shell.setBounds(GlobalVar.rec.x / 2 - shell.getSize().x / 2, GlobalVar.rec.y / 2 - shell.getSize().y / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

		shell.setText("券激活界面");

		account = new Label(shell, SWT.NONE);
		account.setBounds(10, 48, 82, 20);
		account.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		account.setText("请 扫 码");

		txtcouponCode = new Text(shell, SWT.BORDER);
		txtcouponCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtcouponCode.setBounds(100, 45, 377, 26);

		final Label label_3 = new Label(shell, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText("券 限 额");
		label_3.setBounds(10, 85, 80, 20);

		txtlimitMoney = new Text(shell, SWT.BORDER);
		txtlimitMoney.setBounds(100, 82, 377, 26);
		txtlimitMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		if (!isActiveFromMenu)
		{
			txtlimitMoney.setText(limitje);
			txtlimitMoney.setEditable(false);
		}
		else
		{
			txtlimitMoney.setText("");
		}

		txtStatus = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		txtStatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtStatus.setBounds(10, 284, 467, 125);
		txtStatus.setEnabled(false);
		txtStatus.setEditable(false);
		txtStatus.setText("按【确认键】进行券激活");

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 119, 467, 159);

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(135);
		newColumnTableColumn_2.setText("券序号");

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.CENTER);
		newColumnTableColumn.setWidth(132);
		newColumnTableColumn.setText("券条码");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.CENTER);
		newColumnTableColumn_1.setWidth(170);
		newColumnTableColumn_1.setText("券状态");

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("券 激 活");
		label.setBounds(10, 10, 82, 26);

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText("请仔细核对券最终的激活状态");
		label_1.setBounds(99, 10, 389, 25);

		//
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
