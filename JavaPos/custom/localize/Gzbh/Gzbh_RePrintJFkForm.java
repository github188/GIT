package custom.localize.Gzbh;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Gzbh_RePrintJFkForm
{

	private Table tabRePrintJFK = null;
	protected Shell shell = null;

	public Gzbh_RePrintJFkForm()
	{
		this.open();
	}

	public void open()
	{
		Display display = Display.getDefault();
		createContents();

		new Gzbh_RePrintJFkEvent(this);

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
	}

	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800, 510);

		shell.setBounds(area.width / 2 - shell.getSize().x / 2, area.height / 2 - shell.getSize().y / 2, shell.getSize().x, shell.getSize().y
				- GlobalVar.heightPL);
		shell.setText("补打积分卡消费日志");

		tabRePrintJFK = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabRePrintJFK.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabRePrintJFK.setLinesVisible(true);
		tabRePrintJFK.setHeaderVisible(true);
		tabRePrintJFK.setBounds(10, 61, 774, 407);

		final TableColumn newColumnTableColumn = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn.setWidth(65);
		newColumnTableColumn.setText("序号");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn_2.setWidth(115);
		newColumnTableColumn_2.setText("付款笔数");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn_3.setWidth(130);
		newColumnTableColumn_3.setText("付款金额");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn_4.setWidth(115);
		newColumnTableColumn_4.setText("冲正笔数");

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn_5.setWidth(130);
		newColumnTableColumn_5.setText("冲正金额");

		final TableColumn newColumnTableColumn_8 = new TableColumn(tabRePrintJFK, SWT.NONE);
		newColumnTableColumn_8.setWidth(210);
		newColumnTableColumn_8.setText("结算时间");

	}

	public Table getTabRePrintJFK()
	{
		return tabRePrintJFK;
	}

	public Shell getShell()
	{
		return shell;
	}
}
