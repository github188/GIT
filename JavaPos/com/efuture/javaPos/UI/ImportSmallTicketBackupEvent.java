package com.efuture.javaPos.UI;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.ImportSmallTicketBackupBS;
import com.efuture.javaPos.UI.Design.ImportSmallTicketBackupForm;

public class ImportSmallTicketBackupEvent
{
	private Shell shell = null;

	public Table table_SmallTicketList = null;
	public Table tabPay = null;
	public Table tabTicketDeatilInfo = null;
	public StyledText txtGiveChangeMoney = null;
	public StyledText txtFactInceptMoney = null;
	public StyledText txtAgioMoney = null;
	public StyledText txtShouldInceptMoney = null;

	private ImportSmallTicketBackupBS istbBS = null;

	public ImportSmallTicketBackupEvent(ImportSmallTicketBackupForm preForm)
	{
		this.shell = preForm.shell;

		istbBS = new ImportSmallTicketBackupBS();
		istbBS.setImportSmallTicketBackupEvent(this);

		this.table_SmallTicketList = preForm.table_SmallTicketList;
		this.tabPay = preForm.tabPay;
		this.tabTicketDeatilInfo = preForm.tabTicketDeatilInfo;
		this.txtGiveChangeMoney = preForm.txtGiveChangeMoney;
		this.txtFactInceptMoney = preForm.txtFactInceptMoney;
		this.txtAgioMoney = preForm.txtAgioMoney;
		this.txtShouldInceptMoney = preForm.txtShouldInceptMoney;

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

		table_SmallTicketList.setFocus();
		table_SmallTicketList.addKeyListener(key);

		//Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2));
		
		istbBS.getFileList(table_SmallTicketList);
	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowDown:
				istbBS.readFileContent(table_SmallTicketList.getSelectionIndex());
				istbBS.fullSmallTicketTable();
				break;
			case GlobalVar.ArrowUp:
				istbBS.readFileContent(table_SmallTicketList.getSelectionIndex());
				istbBS.fullSmallTicketTable();
				break;
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.Enter://回车事件
					istbBS.checkSmallTicket();

					break;
				case GlobalVar.Validation://确认事件
					if (istbBS.writeSmallTicketData())
					{
						shell.close();
						shell.dispose();
						shell = null;
					}
					break;
				case GlobalVar.Exit://退出事件

					shell.close();
					shell.dispose();
					shell = null;
					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
