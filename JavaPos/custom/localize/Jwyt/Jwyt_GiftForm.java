package custom.localize.Jwyt;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Jwyt_GiftForm
{
	private PosTable table;
	private Shell shell;
	private String retindex = "";

	public static void main(String args[])
	{
		try
		{
			Jwyt_GiftForm form = new Jwyt_GiftForm();

			Vector content = new Vector();
			for (int j = 0; j < 10; j++)
			{

				String[] txt = new String[] { j + 1 + "", "test", "test", "test", "" };
				content.add(txt);
			}
			form.open(content);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String open(Vector content)
	{
		Display display = Display.getDefault();

		createContents();

		createEvent(content);

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			table.setFocus();
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

		return retindex;
	}

	protected void createEvent(Vector content)
	{
		// 设定键盘事件
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyReleased(e, key);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		table.addKeyListener(key);

		shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

		initForm(content);
	}

	private void initForm(Vector content)
	{
		if (content == null)
			return;

		for (int i = 0; i < content.size(); i++)
		{
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String[] retItem = (String[]) content.get(i);
			String[] item = new String[5];
			item[0] = (i + 1) + "";
			item[1] = retItem[0];
			item[2] = retItem[1];
			item[3] = retItem[2];
			item[4] = "";

			tableItem.setText(item);
		}

		if (table.getItemCount() > 0)
		{
			table.setSelection(0);
			table.setFocus();
		}
	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				if (table.getSelectionIndex() != 0)
					table.setSelection(table.getSelectionIndex() - 1);
				else
					table.setSelection(0);
				break;

			case GlobalVar.ArrowDown:
				if (table.getSelectionIndex() == table.getItemCount() - 1)
					table.setSelection(table.getItemCount() - 1);
				else
					table.setSelection(table.getSelectionIndex() + 1);
				break;
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		TableItem item = null;
		switch (key)
		{
			case GlobalVar.Enter:
				if (table.getSelectionIndex() < 0)
					break;
				item = table.getItem(table.getSelectionIndex());
				item.setText(4, "Y");

				break;
			case GlobalVar.Del:
				if (table.getSelectionIndex() < 0)
					break;
				item = table.getItem(table.getSelectionIndex());
				item.setText(4, "");

				break;
			case GlobalVar.Validation:
				for (int i = 0; i < table.getItemCount(); i++)
				{
					item = table.getItem(i);
					if (!item.getText(4).equals("Y"))
						continue;
					retindex = retindex + i + ",";
				}

				shell.close();
				shell.dispose();

				break;
			case GlobalVar.Exit:
				retindex = null;
				shell.close();
				shell.dispose();

				break;
		}
	}

	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		shell.setText("礼品列表");
		shell.setSize(750, 530);

		table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		table.setBounds(10, 40, 722, 415);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("序号");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(121);
		tblclmnNewColumn_1.setText("商品编码");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setWidth(192);
		tblclmnNewColumn_2.setText("商品名称");

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.RIGHT);
		tblclmnNewColumn_4.setWidth(184);
		tblclmnNewColumn_4.setText("数量");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setWidth(121);
		tblclmnNewColumn_3.setText("是否领取");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lblNewLabel.setBounds(10, 10, 700, 25);
		lblNewLabel.setText("请从下列列表中选取一个或多个礼品");

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lblNewLabel_1.setBounds(10, 467, 206, 25);
		lblNewLabel_1.setText("按【回车键】选中商品");

		Label label = new Label(shell, SWT.NONE);
		label.setText("按【确认键】完成领用");
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		label.setBounds(526, 467, 206, 25);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("按【删除键】取消选中");
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		label_1.setBounds(262, 467, 206, 25);
	}

	protected void checkSubclass()
	{

	}
}
