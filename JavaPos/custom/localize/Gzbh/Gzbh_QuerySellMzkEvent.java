package custom.localize.Gzbh;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Gzbh_QuerySellMzkEvent
{

	private Table tabQueryMzkSell = null;
	protected Shell shell = null;
	private Gzbh_QueryMzkSellBS qwlbs = null;

	private int currow = 0;

	public Gzbh_QuerySellMzkEvent(Gzbh_QuerySellMzkForm qsmf)
	{
		tabQueryMzkSell = qsmf.getTabQueryMzkSell();
		shell = qsmf.getShell();
		qwlbs = new Gzbh_QueryMzkSellBS();

		//设定键盘事件
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
		tabQueryMzkSell.addKeyListener(key);
		key.inputMode = key.IntegerInput;

		if (qwlbs.initQueryMzkSell(tabQueryMzkSell))
		{
			setSelection(0, true);
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.Exit:

					try
					{
						shell.close();
						shell.dispose();
						shell = null;
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.ArrowUp:

					if (currow > 0)
					{
						currow = currow - 1;

						setSelection(currow, false);
					}

					break;
				case GlobalVar.ArrowDown:

					if ((currow < (tabQueryMzkSell.getItemCount() - 1)) && (tabQueryMzkSell.getItemCount() >= 0))
					{
						currow = currow + 1;

						setSelection(currow, true);
					}

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void setSelection(int index, boolean flag)
	{
		setSelection(index, -1, flag);
	}

	public void setSelection(int index, int indexb, boolean flag)
	{
		int i = 0;

		if (flag)
		{
			i = index - 1;
		}
		else
		{
			i = index + 1;
		}

		TableItem item = null;

		if (indexb < 0)
		{
			if ((i >= 0) && (i < tabQueryMzkSell.getItemCount()))
			{
				item = tabQueryMzkSell.getItem(i);
				item.setBackground(SWTResourceManager.getColor(255, 255, 255));
				item.setForeground(SWTResourceManager.getColor(0, 0, 0));
			}
		}
		else
		{
			if ((indexb >= 0) && (i < tabQueryMzkSell.getItemCount()))
			{
				item = tabQueryMzkSell.getItem(indexb);
				item.setBackground(SWTResourceManager.getColor(255, 255, 255));
				item.setForeground(SWTResourceManager.getColor(0, 0, 0));
			}
		}

		if ((index < tabQueryMzkSell.getItemCount()) && (index >= 0))
		{
			item = tabQueryMzkSell.getItem(index);
			item.setBackground(SWTResourceManager.getColor(43, 61, 219));
			item.setForeground(SWTResourceManager.getColor(255, 255, 255));
		}

		showSelection(index);
	}

	public void showSelection(int curRow)
	{
		if ((curRow < tabQueryMzkSell.getItemCount()) && (curRow >= 0))
		{
			TableItem item = tabQueryMzkSell.getItem(curRow);
			tabQueryMzkSell.showItem(item);
		}
	}

}
