package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.Design.RetSYJForm;

public class RetSYJEvent
{
	RetSYJForm form = null;
	int curInputMode = GlobalInfo.sysPara.msrspeed;

	public RetSYJEvent(RetSYJForm form)
	{
		this.form = form;

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

		if (GlobalInfo.sysPara.backinputmode == 'A')
		{
			GlobalInfo.sysPara.msrspeed = 0;
			key.inputMode = key.MsrKeyInput;
		}
		else
		{
			key.inputMode = key.IntegerInput;
		}

		form.fphm.addKeyListener(key);
		form.syjh.addKeyListener(key);

		// Rectangle rec =
		// Display.getCurrent().getPrimaryMonitor().getClientArea();
		form.shell.setBounds(((GlobalVar.rec.x - form.shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - form.shell.getSize().y) / 2, form.shell.getSize().x, form.shell.getSize().y - GlobalVar.heightPL);
	}

	public void keyPressed(KeyEvent e, int key)
	{
	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Enter:
				if (e.widget.equals(form.syjh))
				{
					if (form.syjh.getText().length() > 0)
					{
						form.fphm.setFocus();
						form.fphm.selectAll();
						e.data = "focus";
					}
					else
					{
						form.syjh.selectAll();
						form.lbl_help.setText(Language.apply("请输入原收银机号"));
					}
				}
				else if (e.widget.equals(form.fphm))
				{
					if (form.fphm.getText().length() > 0)
					{
						RetSYJForm.syj = form.syjh.getText();
						RetSYJForm.fph = form.fphm.getText();
						form.doneflag = form.Done;
						close();
					}
					else
					{
						form.fphm.selectAll();
						form.lbl_help.setText(Language.apply("请输入原小票号码"));
					}
				}
				break;

			case GlobalVar.Exit:
				form.doneflag = form.Cancel;
				close();
				break;
			case GlobalVar.Clear:
				form.doneflag = form.Clear;
				close();
				break;
		}
	}

	public void close()
	{
		GlobalInfo.sysPara.msrspeed = this.curInputMode;
		form.shell.close();
		form.shell.dispose();
	}
}
