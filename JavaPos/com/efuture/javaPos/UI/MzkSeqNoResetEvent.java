package com.efuture.javaPos.UI;

import java.util.Vector;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MzkSeqNoResetBS;
import com.efuture.javaPos.UI.Design.MzkSeqNoResetForm;

public class MzkSeqNoResetEvent
{
	private MzkSeqNoResetBS mzkSeqNoResetBS = null;
	private Text txtNewSeqNo = null;
	private Label lbOldSeqNo = null;
	private Table tabCzInfo = null;
	private Button btnOk = null;
	private Button btnDel = null;
	private Button btnExit = null;
	private Shell sShell = null;

	public MzkSeqNoResetEvent(MzkSeqNoResetForm msnrf)
	{
		txtNewSeqNo = msnrf.txtNewMzkSeqNo;
		lbOldSeqNo = msnrf.lbOldMzkSeqNo;
		tabCzInfo = msnrf.table;
		btnOk = msnrf.buttonOk;
		btnDel = msnrf.buttonDel;
		btnExit = msnrf.buttonExit;

		sShell = msnrf.getShell();

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
		txtNewSeqNo.addKeyListener(key);

		tabCzInfo.addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				txtNewSeqNo.setFocus();
			}

			public void mouseDoubleClick(MouseEvent e)
			{
				txtNewSeqNo.setFocus();
			}
		});

		btnOk.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				txtNewSeqNo.setFocus();
				NewKeyListener.sendKey(GlobalVar.Validation);
			}

		});
		btnDel.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				txtNewSeqNo.setFocus();
				NewKeyListener.sendKey(GlobalVar.Del);
			}

		});
		btnExit.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				txtNewSeqNo.setFocus();
				NewKeyListener.sendKey(GlobalVar.Exit);
			}

		});

		mzkSeqNoResetBS = CustomLocalize.getDefault().createMzkSeqNoResetBS();
		initCtrl();
	}

	protected void initCtrl()
	{
		lbOldSeqNo.setText(mzkSeqNoResetBS.getSeqNo());
		txtNewSeqNo.selectAll();
		txtNewSeqNo.setFocus();
		showCzColumnData(tabCzInfo, mzkSeqNoResetBS.getCzData());
	}

	public void keyPressed(KeyEvent e, int key)
	{
	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Validation:
				if (mzkSeqNoResetBS.resetSeqNo(txtNewSeqNo.getText().trim()))
				{
					this.lbOldSeqNo.setText(mzkSeqNoResetBS.getSeqNo());
				}
				break;
			case GlobalVar.Exit:
				try
				{
					sShell.close();
					sShell.dispose();
					sShell = null;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				break;
			case GlobalVar.ArrowDown:
				if (e.getSource() == txtNewSeqNo)
				{
					int count = tabCzInfo.getItemCount();
					int index = tabCzInfo.getSelectionIndex();
					if (count >= 0 && index < tabCzInfo.getItemCount() - 1)
					{
						tabCzInfo.setSelection(index + 1);
					}
				}
				break;
			case GlobalVar.ArrowUp:
				if (e.getSource() == txtNewSeqNo)
				{
					if (tabCzInfo.getSelectionIndex() > 0) tabCzInfo.setSelection(tabCzInfo.getSelectionIndex() - 1);
				}
				break;
			case GlobalVar.Del:
				if (e.getSource() == txtNewSeqNo)
				{
					int index = tabCzInfo.getSelectionIndex();

					if (tabCzInfo.getItemCount() == 0 || index == -1) break;

					TableItem item = tabCzInfo.getItem(index);
					String filename = item.getText(4).trim();

					if (new MessageBox(Language.apply("删除冲正数据可能导致某些交易未被正确冲回!\n\n请确保本机的冲正数据都是已被后台处理过的\n\n你确定要删除这些无用的冲正数据吗？"), null, true).verify() != GlobalVar.Key1) { return; }

					if (mzkSeqNoResetBS.deleteCzFile(filename))
					{
						String tipStr = Language.apply("卡号:[{0}]  金额:[{1}]  交易类型：[{2} ]\n{3}冲正文件删除成功", new Object[]{item.getText(2).trim(),item.getText(3).trim(),item.getText(1).trim(),filename});
//						String tipStr = "卡号:[" + item.getText(2).trim() + "]  金额:[" + item.getText(3).trim() + "]  交易类型：[" + item.getText(1).trim()
//						+ "]\n" + filename + "  冲正文件删除成功";
						new MessageBox(tipStr);
					}
					else
					{
						String tipStr = filename + Language.apply("冲正文件删除失败");
						new MessageBox(tipStr);
					}

					tabCzInfo.removeAll();
					showCzColumnData(tabCzInfo, mzkSeqNoResetBS.getCzData());

					if (tabCzInfo.getItemCount() == 1 || index == 0) tabCzInfo.setSelection(0);
					else tabCzInfo.setSelection(index - 1);
				}
				break;
		}
	}

	protected void showCzColumnData(Table tab, Vector v)
	{
		try
		{
			if (v == null) return;
			for (int i = 0; i < v.size(); i++)
			{
				String[] data = (String[]) v.elementAt(i);
				TableItem item = new TableItem(tab, SWT.NONE);
				item.setText(data);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
