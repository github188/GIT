package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.UI.Design.SaleMemoForm;
import com.efuture.javaPos.UI.Design.SaleShowAccountForm;

public class SaleShowAccountEvent
{
	Shell sShell = null;
	public Group grp_zl_sy;
	public Label txt_yfje;
	public Label txt_zl;
	public Label txt_sfje;
	public Label status;
	private SaleBS saleBS;
	public Group grp_yfje;
	public Group grp_sfje;
	public Table table;
	public SaleShowAccountForm form = null;
	public boolean ShellIsDisposed = false;
	private StringBuffer waitKeyCloseForm = null;

	public Composite composite = null;
	public Composite composite_1 = null;
	public Composite composite_2 = null;
	public Composite composite_4 = null;

	public SaleShowAccountEvent(final SaleShowAccountForm form, SaleBS saleBS)
	{
		this.form = form;
		this.grp_zl_sy = form.grp_zl_sy;
		this.txt_sfje = form.txt_sfje;
		this.txt_yfje = form.txt_yfje;
		this.txt_zl = form.txt_zl;
		this.status = form.status;
		this.grp_sfje = form.grp_sfje;
		this.grp_yfje = form.grp_yfje;
		this.saleBS = saleBS;
		this.sShell = form.sShell;
		this.table = form.table;
		this.composite = form.composite;
		this.composite_1 = form.composite_1;
		this.composite_2 = form.composite_2;
		this.composite_4 = form.composite_4;

		// Rectangle rec =
		// Display.getCurrent().getPrimaryMonitor().getClientArea();
		sShell.setLocation((GlobalVar.rec.x - sShell.getSize().x) / 2, (GlobalVar.rec.y - sShell.getSize().y) / 2);

		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				e.doit = false;
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyRelease(e, key);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		sShell.addKeyListener(key);
		table.addKeyListener(key);
		composite.addKeyListener(key);
		composite_1.addKeyListener(key);
		composite_2.addKeyListener(key);
		composite_4.addKeyListener(key);
		initEvent();

		//
		waitKeyCloseForm = new StringBuffer();
	}

	public void keyRelease(KeyEvent e, int key)
	{
		// 如果状态已经是等待按键关闭窗口,则按键后关闭窗口
		if (waitKeyCloseForm.toString().equals("Y"))
		{
			sShell.close();
			sShell.dispose();
			return;
		}

		if (ShellIsDisposed)
			return;

		e.doit = false;

		switch (key)
		{
			case GlobalVar.Enter:
				// 避免连贯性回车直接付款，回车键不允许完成付款

				if (GlobalInfo.ModuleType.indexOf("ZM") == 0)
				{
					// 中免要求回车确认
					ShellIsDisposed = true;
					composite.setVisible(false);
					composite_1.setVisible(true);
					saleBS.saleFinishDone(status, waitKeyCloseForm);
					form.done = true;

					// 如果要等待按键,则不立即关闭找零窗口
					if (waitKeyCloseForm.toString().equals("Y"))
					{
						status.setText(Language.apply("请按任意键关闭该找零窗口"));
						ShellIsDisposed = false;
					}
					else
					{
						saleBS.deleteLcZc();
						sShell.close();
						sShell.dispose();
					}
				}
				break;
			case GlobalVar.Pay:
			case GlobalVar.Validation:
				if (GlobalInfo.ModuleType.indexOf("ZM") != 0)
				{
					ShellIsDisposed = true;
					composite.setVisible(false);
					composite_1.setVisible(true);
					saleBS.saleFinishDone(status, waitKeyCloseForm);
					form.done = true;

					// 如果要等待按键,则不立即关闭找零窗口
					if (waitKeyCloseForm.toString().equals("Y"))
					{
						status.setText(Language.apply("请按任意键关闭该找零窗口"));
						ShellIsDisposed = false;
					}
					else
					{
						saleBS.deleteLcZc();
						sShell.close();
						sShell.dispose();
					}
				}

				break;

			case GlobalVar.ArrowDown:
			case GlobalVar.ArrowUp:
			case GlobalVar.PageDown:
			case GlobalVar.PageUp:
			case GlobalVar.ArrowLeft:
			case GlobalVar.ArrowRight:
				break;
			case GlobalVar.PayLcZc:
				if (!saleBS.doLcZc(txt_zl, grp_zl_sy))
					return;
				break;
			case GlobalVar.Exit:
				saleBS.deleteLcZc();

				ShellIsDisposed = true;
				sShell.close();
				sShell.dispose();

				break;
			case GlobalVar.InputAppendInfo:
				new SaleMemoForm(saleBS.saleHead, saleBS.saleGoods, saleBS.salePayment, null, 0);
				break;

		}
	}

	// 设定显示金额
	public void initEvent()
	{
		// 强制零钞转存的情况

		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			saleBS.activeLczc();
			// NewKeyListener.sendKey(GlobalVar.PayLcZc);
		}

		if (SellType.ISSALE(saleBS.saletype))
		{
			grp_sfje.setText(Language.apply("实付金额"));
			grp_yfje.setText(Language.apply("应付金额"));
		}
		else
		{
			grp_sfje.setText(Language.apply("实退金额"));
			grp_yfje.setText(Language.apply("应退金额"));
		}

		this.txt_sfje.setText(saleBS.getSaleSfje());
		this.txt_yfje.setText(ManipulatePrecision.doubleToString(saleBS.saleyfje));

		this.grp_zl_sy.setText(saleBS.getChangeTitleLabel());

		if (GlobalInfo.ModuleType.indexOf("ZM") != 0)
		{
			this.txt_zl.setText(ManipulatePrecision.doubleToString(saleBS.saleHead.zl));
			status.setText(Language.apply("按\"确认\"键或\"付款\"键开始付款，按其他键退出付款"));
		}
		else
		{
			// 中免设置找零+补找零
			saleBS.setZL(txt_zl, status, null);
		}

		// 客显显示找零
		DisplayMode.getDefault().lineDisplayChange();

		// 双屏显示找零
		saleBS.sendSecMonitor("change");

		// 多币种找零
		Vector zllist = saleBS.getSaleChangeList();
		if (zllist == null || zllist.size() <= 0)
		{
			this.txt_zl.setVisible(true);
			this.table.setVisible(false);
		}
		else
		{
			// 多币种找零,显示找零列表
			this.table.setVisible(true);
			this.txt_zl.setVisible(false);

			//
			for (int i = 0; i < zllist.size(); i++)
			{
				TableItem item = new TableItem(this.table, SWT.NONE);
				item.setText((String[]) zllist.elementAt(i));
			}
			this.table.setSelection(0);
		}

		// status.setText("按\"确认\"键或\"付款\"键开始付款，按其他键退出付款");
	}
}
