package custom.localize.Zmjc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PaymentChangeAddForm
{
	public Text text;
	public PosTable table;
	public Shell shell;
	public Label lbl_ysje;
	boolean done = false;

	/**
	 * Open the window
	 */
	public boolean open(Zmjc_PaymentChange paychg)
	{
		final Display display = Display.getDefault();
		createContents();

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		new PaymentChangeAddEvent(paychg, this);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			table.setFocus();
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

		return done;
	}

	public void setDone(boolean done)
	{
		this.done = done;
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		shell.setSize(565, 383);

		shell.setText(Language.apply("找零"));

		table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
		table.setBounds(8, 55, 541, 274);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.RIGHT);
		newColumnTableColumn_1.setWidth(60);
		newColumnTableColumn_1.setText(Language.apply("序"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText(Language.apply("名称"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.RIGHT);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(110);
		newColumnTableColumn_3.setText(Language.apply("找零汇率"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(100);
		newColumnTableColumn_4.setText(Language.apply("找零"));
		
		final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.RIGHT);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(100);
		newColumnTableColumn_5.setText(Language.apply("补RMB"));
		

		lbl_ysje = new Label(shell, SWT.NONE);
		lbl_ysje.setBounds(18, 18, 62, 22);
		lbl_ysje.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		lbl_ysje.setText(Language.apply("找零:"));

		text = new Text(shell, SWT.RIGHT | SWT.BORDER);
		text.setBounds(111, 12, 352, 30);
		text.setEnabled(false);
		text.setFont(SWTResourceManager.getFont("", 20, SWT.NONE));
		text.setText("");
	}
}
