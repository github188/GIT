package custom.localize.Hfhf;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Hfhf.Hfhf_VipScore.ScoreDetail;

public class Hfhf_QueryScoreDetailForm
{
	public Shell shell;
	private Text txttodate;
	private Text txtfromdate;
	private Text txtcardno;
	private Label label;
	private Label label_2;

	public static void main(String args[])
	{
		Hfhf_QueryScoreDetailForm form = new Hfhf_QueryScoreDetailForm();

		form.open();
	}

	public void open()
	{
		final Display display = Display.getDefault();

		createContents();
		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		addListener();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

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

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);

	}

	protected void addListener()
	{
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

		key.inputMode = 100;
		txtcardno.addKeyListener(key);
		txtcardno.setData("MSRINPUT");
		txtcardno.setFocus();
		txtcardno.selectAll();

		txtfromdate.addKeyListener(key);
		txttodate.addKeyListener(key);

	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		String cardno = Hfhf_CrmModule.getDefault().getCardNo(track2);

		txtcardno.setFocus();
		txtcardno.selectAll();

		txtcardno.setText(cardno);
	}

	public String convertDate(String date)
	{
		// 20120101
		StringBuffer sb = new StringBuffer();
		sb.append(date.substring(0, 4));
		sb.append("-");
		sb.append(date.substring(4, 6));
		sb.append("-");
		sb.append(date.substring(6));

		sb.append("00:00:00");
		return sb.toString();
	}

	public void keyPressed(KeyEvent e, int key)
	{
	}

	public void keyReleased(KeyEvent e, int key)
	{

		switch (key)
		{
			case GlobalVar.Enter:
				if (e.widget == txtcardno)
				{
					if (txtcardno.getText().trim().equals(""))
					{
						txtcardno.setFocus();
						txtcardno.selectAll();
						break;
					}
					e.data = "focus";
					txtfromdate.setFocus();
					txtfromdate.selectAll();
				}
				else if (e.widget == txtfromdate)
				{
					if (txtfromdate.getText().trim().equals(""))
					{
						txtfromdate.setFocus();
						txtfromdate.selectAll();
						break;
					}
					e.data = "focus";
					txttodate.setFocus();
					txttodate.selectAll();
				}
				if (e.widget == txttodate)
				{
					if (txttodate.getText().trim().equals(""))
					{
						txttodate.setFocus();
						txttodate.selectAll();
						break;
					}

					Vector details = Hfhf_CrmModule.getDefault().queryPointDetail(txtcardno.getText(), convertDate(txtfromdate.getText()), convertDate(txttodate.getText()));

					if (details != null)
					{
						Vector content = new Vector();
						String[] title = { "序", "会员卡号", "积分", "积分说明", "单据类型", "单据号", "交易时间", "消费终端号" };
						int[] width = { 50, 120, 100, 120, 100, 120, 120, 150 };

						String[] row = null;

						for (int i = 0; i < details.size(); i++)
						{
							ScoreDetail item = (ScoreDetail) details.get(i);
							row = new String[details.size()];

							row[0] = String.valueOf(i);
							row[1] = item.Cardno;
							row[2] = String.valueOf(item.Points);
							row[3] = item.Remarks;
							row[4] = item.BillType;
							row[5] = item.SrcId;
							row[6] = item.SaleDate;
							row[7] = item.TerminalId;

							content.add(row);
						}

						new MutiSelectForm().open("积分明细列表", title, width, content, false, 800, 600, 775, 480, false, false, -1, false);

						shell.close();
						shell.dispose();
					}
				}
				break;

			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
		}

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void createContents()
	{
		shell = new Shell(GlobalVar.style_linux);
		shell.setSize(new Point(365, 150));
		shell.setLayout(null);
		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		label_1.setText("终止时间");
		label_1.setBounds(12, 103, 90, 25);

		txttodate = new Text(shell, SWT.BORDER);
		txttodate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		txttodate.setBounds(110, 102, 225, 25);

		txtfromdate = new Text(shell, SWT.BORDER);
		txtfromdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		txtfromdate.setBounds(110, 61, 225, 25);

		txtcardno = new Text(shell, SWT.BORDER);
		txtcardno.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		txtcardno.setBounds(109, 22, 225, 25);

		label = new Label(shell, SWT.NONE);
		label.setText("会员卡号");
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		label.setBounds(12, 21, 90, 25);

		label_2 = new Label(shell, SWT.NONE);
		label_2.setText("起始时间");
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		label_2.setBounds(12, 64, 90, 25);
	}
}
