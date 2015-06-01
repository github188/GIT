package com.efuture.javaPos.UI;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MzkRechargeBS;
import com.efuture.javaPos.UI.Design.MzkRechargeForm;

public class MzkRechargeEvent
{
	public Shell shell = null;
	public Text txt_cardno;
	public Text txt_money;
	public Label lbl_name;
	public StyledText status;
	public MzkRechargeBS mzkrechargebs;

	private boolean czisok = false;

	public MzkRechargeEvent(MzkRechargeForm form)
	{
		this.shell = form.sShell;
		this.lbl_name = form.lbl_name;
		this.txt_cardno = form.txt_cardno;
		this.txt_money = form.txt_money;
		this.status = form.status;

		mzkrechargebs = CustomLocalize.getDefault().createMzkRechargeBS();

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

			public void msrFinish(KeyEvent e, String track1, String track2, String track3)
			{
				msrRead(e, track1, track2, track3);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		key.inputMode = TextBox.MsrKeyInput;
		txt_cardno.addKeyListener(key);
		txt_cardno.setData("MSRINPUT");
		txt_cardno.setFocus();

		NewKeyListener key1 = new NewKeyListener();
		key1.setEditableResponseEvent(true);
		key1.event = event;
		key1.inputMode = key.DoubleInput;
		txt_money.addKeyListener(key1);

		NewKeyListener key2 = new NewKeyListener();
		key2.event = event;
		status.addKeyListener(key2);

		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		initEvent();

	}

	public void initEvent()
	{

		this.lbl_name.setText(Language.apply("面值卡充值"));

		if (!mzkrechargebs.sendRechargeAccountCz())
			status.setText(Language.apply("无法使用充值\n系统发送上次充值冲正未成功\n"));
		else
			czisok = true;
	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Enter:
				enterInput(e);
				break;

			case GlobalVar.Exit:
				if (e.widget.equals(txt_money) || e.widget.equals(status) || e.widget.equals(txt_cardno))
				{
					shell.close();
					shell.dispose();
				}

				break;
		}
	}

	void enterInput(KeyEvent e)
	{
		if (!czisok)
			return;

		if (e.widget.equals(status))
		{
			return;
		}
		else if (e.widget.equals(txt_cardno))
		{
			msrRead(e, "", txt_cardno.getText(), "");
		}
		else if (e.widget.equals(txt_money))
		{
			try
			{
				if (txt_money.getText().trim().equals("") && Convert.toDouble(txt_money.getText()) <= 0)
					return;

				double money = ManipulatePrecision.doubleConvert(Convert.toDouble(txt_money.getText()), 2, 1);

				if (!mzkrechargebs.verifyMessage(txt_cardno.getText(), String.valueOf(money)))
					return ;

				if (mzkrechargebs.mzkAccount(money))
				{
					// 充值成功让框不可编辑
					txt_cardno.setEnabled(false);
					txt_money.setEnabled(false);

					new MessageBox(Language.apply("面值卡{0}充值成功", new Object[]{mzkrechargebs.getDisplayCardno()}));

					status.setText(Language.apply("充值成功\n\n") + mzkrechargebs.getDisplayStatusInfo());

					ProgressBox prg = new ProgressBox();
					try
					{
						prg.setText(Language.apply("正在打印充值凭证,请稍等..."));
						mzkrechargebs.printRechargeBill();

						if (e != null)
							e.data = "focus";
						status.setFocus();
					}
					finally
					{
						if (prg != null)
						{
							prg.close();
							prg = null;
						}
					}
				}
				else
				{
					new MessageBox(Language.apply("面值卡{0}充值失败", new Object[]{mzkrechargebs.getDisplayCardno()}));

					shell.close();
					shell.dispose();
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
		}
	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		// 查询面值卡
		if (mzkrechargebs.findMzk(track1, track2, track3))
		{
			// 显示卡号和状态提示
			txt_cardno.setText(mzkrechargebs.getDisplayCardno());
			status.setText(mzkrechargebs.getDisplayStatusInfo());

			if (e != null)
				e.data = "focus";
			txt_money.setFocus();
			txt_money.selectAll();
		}
		else
		{
			txt_cardno.setText(Language.apply("请重新刷卡"));
			txt_cardno.selectAll();
		}
	}
}
