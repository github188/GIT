package custom.localize.Lnbe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Group;

import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Lnbe_WriteGrantForm
{
	private Text txt_grantgh;
	private Shell shell;
	private Group group;
	private CLabel lbl_gh;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
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
	}

	protected void createContents()
	{
		shell = new Shell(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(355, 183);
		shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

		group = new Group(shell, SWT.NONE);
		group.setBounds(4, 5, 340, 100);
		group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		group.setText("请输入授权卡号");

		lbl_gh = new CLabel(group, SWT.NONE);
		lbl_gh.setBounds(10, 35, 99, 46);
		lbl_gh.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NORMAL));
		lbl_gh.setText("工  号");

		txt_grantgh = new Text(group, SWT.BORDER);
		txt_grantgh.setBounds(115, 36, 215, 45);
		txt_grantgh.setText(GlobalInfo.posLogin.gh);
		txt_grantgh.selectAll();
		txt_grantgh.setFont(SWTResourceManager.getFont("宋体", 30, SWT.NORMAL));

		CLabel lblNewLabel_1 = new CLabel(shell, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NORMAL));
		lblNewLabel_1.setBounds(4, 111, 340, 32);
		lblNewLabel_1.setText("请将卡放置于读卡器上,按‘确认键’进行写卡");

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
		txt_grantgh.addKeyListener(key);

	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{

			case GlobalVar.Validation:
			case GlobalVar.Enter:
				if (ICCard.getDefault().updateCardMoney(txt_grantgh.getText().trim(), "", 0))
				{
					shell.close();
					shell.dispose();
				}

				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
		}
	}
}
