package custom.localize.Jjls;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;

public class Jjls_CardCheckEvent
{
	public Text oldcard;
	public Label rescardNo;
	public Label resje;
	public Label resljzs;
	public Label resljje;
	public Shell sShell;
	
	private double ljje;
	private int ljzs;
	
	public Jjls_CardCheckEvent(Jjls_CardCheckForm form)
	{
		this.oldcard = form.oldcard;
		this.sShell = form.shell;
		this.rescardNo = form.rescardNo;
		this.resje = form.resje;
		this.resljzs = form.resljzs;
		this.resljje = form.resljje;

		sShell.setBounds((GlobalVar.rec.x - sShell.getSize().x) / 2, (GlobalVar.rec.y - sShell.getSize().y) / 2, sShell.getSize().x,
							sShell.getSize().y - GlobalVar.heightPL);
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

			public void msrFinish(KeyEvent e, String track1, String track2, String track3)
			{
				msrRead(e, track1, track2, track3);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		key.inputMode = key.inputMode;

		oldcard.setData("MSRINPUT");
		oldcard.addKeyListener(key);
	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		/*
		if (key == GlobalVar.Enter)
		{
			if (e.widget.equals(oldcard) && oldcard.getText().length() > 0)
			{
				boolean done = cardbs.getCustInfo("", oldcard.getText().trim(), "", "oldc", oldcard, text);
				if (done)
				{
					e.data = "focus";
					newcard.setFocus();
					newcard.selectAll();
				}
			}
			else if (e.widget.equals(newcard) && newcard.getText().length() > 0)
			{
				boolean done = cardbs.getCustInfo("", newcard.getText().trim(), "", "newc", newcard, text);
				if (done)
				{
					e.data = "focus";
					oldcard.setFocus();
					oldcard.selectAll();
					oldcard.setText("");
					text.setText("");
					newcard.setText("");
				}
			}
		}
		if (key == GlobalVar.Exit)
		{
			close();
		}
		else if (e.widget.equals(newcard) && key == GlobalVar.Validation)
		{
			boolean carddone = cardbs.validCard();
			if (carddone)
			{
				close();
			}
		}
		*/
		if (key == GlobalVar.Exit)
		{
			close();
		}
	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		if (e.widget.equals(oldcard) && oldcard.getText().length() > 0)
		{
			track3 = String.valueOf(ICCard.getDefault().getICCardMoney());
			String cardNo = track2;
			String je = track3;
			if (cardNo.length() == 14 && je.length() > 0);
			{
				ljzs++;
				ljje += Double.parseDouble(je);
				rescardNo.setText(cardNo);
				resje.setText(je);
				resljje.setText(String.valueOf(ljje));
				resljzs.setText(String.valueOf(ljzs));
				
				e.data = "focus";
				oldcard.setFocus();
				oldcard.selectAll();
				oldcard.setText("");
			}
			
		}
		/*
		if (e.widget.equals(oldcard) && oldcard.getText().length() > 0)
		{
			PaymentMzk.
			
			boolean done = cardbs.getCustInfo(track1.trim(), track2.trim(), track3.trim(), "oldc", oldcard, text);
			if (done)
			{
				e.data = "focus";
				newcard.setFocus();
				newcard.selectAll();
			}
		}
		else if (e.widget.equals(newcard) && newcard.getText().length() > 0)
		{
			boolean done = cardbs.getCustInfo(track1.trim(), track2.trim(), track3.trim(), "newc", newcard, text);
			if (done)
			{
				e.data = "focus";
				oldcard.setFocus();
				oldcard.selectAll();
				oldcard.setText("");
				text.setText("");
				newcard.setText("");
			}
		}
		*/
	}

	public void close()
	{
		sShell.close();
		sShell.dispose();
	}

}
