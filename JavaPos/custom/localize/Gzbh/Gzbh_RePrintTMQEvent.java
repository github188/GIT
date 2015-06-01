package custom.localize.Gzbh;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Gzbh_RePrintTMQEvent
{

	private Table tabRePrintJFK = null;
	protected Shell shell = null;
	private Gzbh_RePrintTMQBS rpjfkbs = null;

	private int currow = 0;

	public Gzbh_RePrintTMQEvent(Gzbh_RePrintTMQForm rpjfkf)
	{
		tabRePrintJFK = rpjfkf.getTabRePrintJFK();
		shell = rpjfkf.getShell();
		rpjfkbs = new Gzbh_RePrintTMQBS();

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
		tabRePrintJFK.addKeyListener(key);
		key.inputMode = key.IntegerInput;

		ProgressBox progress = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查积条码现金券交易信息，请等待.....");
			if (rpjfkbs.initRePrintJFK(tabRePrintJFK))
			{
				setSelection(0, true);
			}
		}
		finally
		{
			if (progress != null) progress.close();
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

				case GlobalVar.Print:
					ProgressBox progress = null;
					try
					{
						progress = new ProgressBox();
						progress.setText("正在打印条码现金券交易信息，请等待.....");
						int index = tabRePrintJFK.getSelectionIndex();

						if (index < 0 || index > rpjfkbs.mzkInfo.size() - 1)
						{
							index = 0;
						}

						String[] mzkInfo = (String[]) rpjfkbs.mzkInfo.get(index);
						//打印选中的行
						rpjfkbs.rePrintJfk(mzkInfo);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						if (progress != null) progress.close();
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

					if ((currow < (tabRePrintJFK.getItemCount() - 1)) && (tabRePrintJFK.getItemCount() >= 0))
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
			if ((i >= 0) && (i < tabRePrintJFK.getItemCount()))
			{
				item = tabRePrintJFK.getItem(i);
				item.setBackground(SWTResourceManager.getColor(255, 255, 255));
				item.setForeground(SWTResourceManager.getColor(0, 0, 0));
			}
		}
		else
		{
			if ((indexb >= 0) && (i < tabRePrintJFK.getItemCount()))
			{
				item = tabRePrintJFK.getItem(indexb);
				item.setBackground(SWTResourceManager.getColor(255, 255, 255));
				item.setForeground(SWTResourceManager.getColor(0, 0, 0));
			}
		}

		if ((index < tabRePrintJFK.getItemCount()) && (index >= 0))
		{
			item = tabRePrintJFK.getItem(index);
			item.setBackground(SWTResourceManager.getColor(43, 61, 219));
			item.setForeground(SWTResourceManager.getColor(255, 255, 255));
		}

		showSelection(index);
	}

	public void showSelection(int curRow)
	{
		if ((curRow < tabRePrintJFK.getItemCount()) && (curRow >= 0))
		{
			TableItem item = tabRePrintJFK.getItem(curRow);
			tabRePrintJFK.showItem(item);
		}
	}

}
