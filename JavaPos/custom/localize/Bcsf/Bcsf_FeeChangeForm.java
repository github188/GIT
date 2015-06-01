package custom.localize.Bcsf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;

import org.eclipse.swt.widgets.Group;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;

public class Bcsf_FeeChangeForm
{
	public Shell shell;
	public Button btn_focus;
	public Bcsf_FeePayBS feePay;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Bcsf_FeePayBS pay)
	{
		this.feePay = pay;
		final Display display = Display.getDefault();
		createContents();

		btn_focus.forceFocus();

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
		shell.setSize(600, 360);
		shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

		Group group = new Group(shell, SWT.NONE);
		group.setText("应付金额");
		group.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		group.setBounds(0, 0, 285, 130);

		CLabel lblNewLabel_1 = new CLabel(group, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("宋体", 50, SWT.BOLD));
		lblNewLabel_1.setBounds(13, 24, 262, 98);
		lblNewLabel_1.setText(String.valueOf(feePay.getTotalmoney()));

		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setText("实付金额");
		group_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		group_1.setBounds(291, 0, 300, 130);

		CLabel label = new CLabel(group_1, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setFont(SWTResourceManager.getFont("宋体", 50, SWT.BOLD));
		label.setText(String.valueOf(feePay.getRealmoney()));
		label.setBounds(10, 24, 280, 96);

		Group group_2 = new Group(shell, SWT.NONE);
		group_2.setText("找零");
		group_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		group_2.setBounds(0, 135, 591, 149);

		CLabel label_1 = new CLabel(group_2, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1.setAlignment(SWT.CENTER);
		label_1.setFont(SWTResourceManager.getFont("宋体", 70, SWT.BOLD));
		label_1.setText(String.valueOf(feePay.getChangemoney()));
		label_1.setBounds(41, 21, 541, 114);

		CLabel lblNewLabel = new CLabel(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NORMAL));
		lblNewLabel.setBounds(2, 289, 590, 35);
		lblNewLabel.setText("‘确认键’提交单据 ‘退出键’返回付款界面");

		btn_focus = new Button(shell, SWT.NONE);
		btn_focus.setBounds(588, 291, 1, 1);

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
		btn_focus.addKeyListener(key);

	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Validation:
				if (feePay.payComplete())
				{
					new MessageBox("单据提交成功");
					feePay.print();

					shell.close();
					shell.dispose();
				}
				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
		}
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
