package custom.localize.Gzbh;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Gzbh_QuerySellMzkForm
{

	private Table tabQueryMzkSell = null;
	protected Shell shell = null;

	public Gzbh_QuerySellMzkForm()
	{
		this.open();
	}

	public void open()
	{
		Display display = Display.getDefault();
		createContents();

		new Gzbh_QuerySellMzkEvent(this);

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
		shell.setText("查询积分卡消费日志");

		tabQueryMzkSell = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabQueryMzkSell.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabQueryMzkSell.setLinesVisible(true);
		tabQueryMzkSell.setHeaderVisible(true);
		tabQueryMzkSell.setBounds(10, 61, 774, 407);

		final TableColumn newColumnTableColumn = new TableColumn(tabQueryMzkSell, SWT.NONE);
		newColumnTableColumn.setWidth(65);
		newColumnTableColumn.setText("序号");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabQueryMzkSell, SWT.NONE);
		newColumnTableColumn_1.setWidth(96);
		newColumnTableColumn_1.setText("小票号");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabQueryMzkSell, SWT.NONE);
		newColumnTableColumn_2.setWidth(300);
		newColumnTableColumn_2.setText("积分卡号");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabQueryMzkSell, SWT.NONE);
		newColumnTableColumn_3.setWidth(86);
		newColumnTableColumn_3.setText("金额");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabQueryMzkSell, SWT.NONE);
		newColumnTableColumn_4.setWidth(218);
		newColumnTableColumn_4.setText("消费时间");

	}

	public Table getTabQueryMzkSell()
	{
		return tabQueryMzkSell;
	}

	public Shell getShell()
	{
		return shell;
	}
}
