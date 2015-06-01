package com.efuture.javaPos.UI;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bankpay.alipay.service.AliPayService;
import bankpay.alipay.tools.ParseXml;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.PosTable.NewSelectionAdapter;
import com.efuture.commonKit.QrcodeDisplay;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.DebugForm;
import com.efuture.javaPos.UI.Design.MenuFuncForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;
import com.swtdesigner.SWTResourceManager;

public class SalePayEvent
{
	protected PosTable table = null;
	protected PosTable table1 = null;
	protected SaleBS saleBS = null;
	public Text txt = null;
	protected Shell shell = null;
	protected Label lbl_ysje = null;
	protected Label payReqFee = null;
	protected Label unpayfee = null;
	protected Label lbl_money = null;
	protected boolean ShellIsDisposed = false;
	protected boolean ishhPay = false;

	protected long PosClockClickTime = -1;

	protected boolean isDeleting = false;

	// 切换付款方式名称的情况
	protected final static int HH_CHANGE_TYPE01 = 1;
	// 改变付款明细换货状态标志的情况
	protected final static int HH_CHANGE_TYPE02 = 2;
	// 改变付款明细名称的情况
	protected final static int HH_CHANGE_TYPE03 = 3;
	

	public void mouseModeInit()
	{
		table.setFocusedControl(txt);
		table1.setFocusedControl(txt);

		table.addNewSelectionListener(new NewSelectionAdapter()
		{
			public void widgetSelected(int oldindex, int index)
			{
				RowSelected(index);
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Enter);
			}

			public void mouseDown(MouseEvent arg0)
			{
				txt.setFocus();
			}
		});

		table1.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Del);
			}

			public void mouseDown(MouseEvent arg0)
			{
				txt.setFocus();
			}
		});
	}

	public SalePayEvent(SaleBS saleBS, SalePayForm pay)
	{
		if (pay != null)
		{
			this.table = pay.table;
			this.table1 = pay.table1;
			this.txt = pay.text;
			this.shell = pay.shell;
			this.lbl_ysje = pay.lbl_ysje;
			this.saleBS = saleBS;
			this.saleBS.setSalePayEvent(this);
			this.payReqFee = pay.payReqFee;
			this.unpayfee = pay.unpayfee;
			this.lbl_money = pay.lbl_money;

			mouseModeInit();

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
			};

			NewKeyListener key = new NewKeyListener();
			key.event = event;
			key.inputMode = key.DoubleInput;
			key.isControl = true;

			txt.addKeyListener(key);
			table.addKeyListener(key);
			table1.addKeyListener(key);

			// Rectangle rec =
			// Display.getCurrent().getPrimaryMonitor().getClientArea();
			shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);
			shell.setActive();
			txt.setFocus();

			// 初始化
			initPayment();

			initOperation();

			/*
			 * 在SalePayForm中处理 // 通过快捷付款键进入付款窗口,立即执行按键处理 if (saleBS.quickpaykey
			 * != 0) { txt.setFocus();
			 * NewKeyListener.sendKey(saleBS.quickpaykey); saleBS.quickpaykey =
			 * 0; }
			 */}
	}

	public void initOperation()
	{
		saleBS.afterInitPay();
	}

	public void refreshFeeLabel()
	{
		// 显示应付款和剩余款
		payReqFee.setText(saleBS.getSellPayMoneyLabel());
		unpayfee.setText(saleBS.getPayBalanceLabel());
	}

	public void initPayment()
	{
		// 处理换货标记
		if (saleBS.hhflag == 'Y')
			this.ishhPay = saleBS.HHinit();

		// 计算应付金额产生的零头折扣
		saleBS.calcSellPayMoney(true);

		// 根据付款类型显示应付金额提示
		lbl_ysje.setText(saleBS.getPayAccountInfo());

		// 显示应付款和剩余款
		refreshFeeLabel();

		// 客显显示应付款
		if (saleBS.saleHead.sjfk > 0)
		{
			DisplayMode.getDefault().lineDisplayPay();

			saleBS.sendSecMonitor("pay");
		}
		else
		{
			DisplayMode.getDefault().lineDisplayTotal();

			saleBS.sendSecMonitor("total");
		}

		// 显示付款方式列表,初始化只显示主付款方式,主付款方式的上级代码为0
		showPayModeBySuper("0");

		// 显示已付款列表
		showSalePaymentDisplay();

		// 计算付款结果
		calcPayResult();
	}

	public void showSalePaymentDisplay()
	{
		Vector vector = saleBS.getSalePaymentDisplay();
		PaymentStatusChange(vector, HH_CHANGE_TYPE03);
		table1.exchangeContent(vector);
		table1.assignLast();
	}

	public void refreshSalePayment()
	{
		showSalePaymentDisplay();
		unpayfee.setText(saleBS.getPayBalanceLabel());
		payReqFee.setText(saleBS.getSellPayMoneyLabel());
	}

	public void showPayModeBySuper(String code)
	{
		// 刷新付款列表
		Vector vector = saleBS.getPayModeBySuper(code);

		// 换货模式下付款显示前面加标记
		if (ishhPay == true)
			PaymentStatusChange(vector, HH_CHANGE_TYPE01);
		table.exchangeContent(vector);
		table.setSelection(0);

		// 刷新一次金额输入框的初始值
		keyReleased(null, GlobalVar.ArrowUp);
	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				table.moveUp();

				break;

			case GlobalVar.ArrowDown:
				table.moveDown();

				break;

			case GlobalVar.PageUp:
				table1.moveUp();

				break;
			case GlobalVar.PageDown:
				table1.moveDown();

				break;
		}
	}

	public void RowSelected(int index)
	{
		// 得到当前付款代码
		String[] ax = table.changeItemVar(index);
		String paycode = ax[0];

		// 得到付款方式,显示付款方式缺省付款金额
		PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);
		saleBS.setMoneyInputDefault(txt, paymode);

		// 字体大小
		int height = 0;
		FontData[] fd = lbl_money.getFont().getFontData();
		if (fd.length > 0)
			height = fd[0].getHeight();

		// 主付款显示付款名
		if (paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code) || paymode.isbank == 'N')
		{
			int newheight = height;
			if (ax[1].length() <= 3)
				newheight = 25;
			else if (ax[1].length() <= 4)
				newheight = 19;
			else
				newheight = 15;

			if (newheight != height)
				lbl_money.setFont(SWTResourceManager.getFont("宋体", newheight, SWT.NONE));

			// (显示宽度不够)不显示汇率
			String payName = ax[1];
			if (payName.lastIndexOf("<") >= 0)
			{
				payName = payName.substring(0, payName.lastIndexOf("<"));
			}

			lbl_money.setText(payName);// ax[1]
		}
		else
		{
			if (height != 25)
				lbl_money.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));
			lbl_money.setText(Language.apply("付款码"));
		}

		txt.setFocus();
	}

	public void keyReleased(KeyEvent e, int key)
	{
		if (ShellIsDisposed) { return; }

		// 得到当前付款方式
		int index = table.getSelectionIndex();
		if (index == -1)
		{
			table.setSelection(0);
			index = 0;
		}

		// 得到当前付款代码
		String[] ax = table.changeItemVar(index);
		String paycode = ax[0];

		// 得到付款方式
		PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);

		switch (key)
		{
			case GlobalVar.ArrowUp:
			case GlobalVar.ArrowDown:
			{
				RowSelected(index);
				break;
			}

			case GlobalVar.Del:
				deletePay();

				break;

			case GlobalVar.Enter:
				payEnter(paymode);

				break;

			case GlobalVar.Rebate:
			case GlobalVar.RebatePrice:
			case GlobalVar.WholeRate:
			case GlobalVar.WholeRebate:
				showRebateDetail(key);
				break;

			case GlobalVar.Validation: // 显示付款分摊情况
				showPayApportion();
				break;

			case GlobalVar.Pay:
				if (saleBS.calcPayBalance() > 0)
					paySelect();
				else
					payEnter(paymode);
				break;

			case GlobalVar.PayBank: // 银联卡付款键
			case GlobalVar.PayCash: // 现金付款键
			case GlobalVar.PayCheque: // 支票付款键
			case GlobalVar.PayCredit: // 信用卡付款键
			case GlobalVar.PayMzk: // 面值卡付款键
			case GlobalVar.PayGift: // 礼券付款键
			case GlobalVar.PayTally: // 赊账付款键
			{
				int last = saleBS.payButtonToPayModePosition(key);

				if (last >= 0)
				{
					gotoPayModeLocation(last);
				}

				break;
			}

			case GlobalVar.Exit:
				payExit(paymode);

				break;

			case GlobalVar.CustomKey0:
			case GlobalVar.CustomKey1:
			case GlobalVar.CustomKey2:
			case GlobalVar.CustomKey3:
			case GlobalVar.CustomKey4:
			case GlobalVar.CustomKey5:
			case GlobalVar.CustomKey6:
			case GlobalVar.CustomKey7:
			case GlobalVar.CustomKey8:
			case GlobalVar.CustomKey9:
				customKeyInput(key);

				break;

			case GlobalVar.MainList:
				showFuncMenu();
				break;

			case GlobalVar.ExchangeSell:
				exchangeSale(paymode);
				break;
			case GlobalVar.StaffText:
				showBackPayment();
				break;
			case GlobalVar.Debug:
				new DebugForm().open(saleBS, "saleBS");
				break;
		}
	}

	public void showBackPayment()
	{
		saleBS.showBackPayment();
	}

	public void showFuncMenu()
	{
		String func = saleBS.getFuncMenuByPaying();

		try
		{
			if (func != null)
			{
				// 显示功能菜单窗口
				new MenuFuncForm(shell, func, false);
			}
			else
			{
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打开功能菜单时发生异常\n\n") + ex.getMessage());
		}
	}

	public void customKeyInput(int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.CustomKey0:
					saleBS.execCustomKey0(false);

					break;

				case GlobalVar.CustomKey1:
					saleBS.execCustomKey1(false);

					break;

				case GlobalVar.CustomKey2:
					saleBS.execCustomKey2(false);

					break;

				case GlobalVar.CustomKey3:
					saleBS.execCustomKey3(false);

					break;

				case GlobalVar.CustomKey4:
					saleBS.execCustomKey4(false);

					break;

				case GlobalVar.CustomKey5:
					saleBS.execCustomKey5(false);

					break;

				case GlobalVar.CustomKey6:
					saleBS.execCustomKey6(false);

					break;

				case GlobalVar.CustomKey7:
					saleBS.execCustomKey7(false);

					break;

				case GlobalVar.CustomKey8:
					saleBS.execCustomKey8(false);

					break;

				case GlobalVar.CustomKey9:
					saleBS.execCustomKey9(false);

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("自定义功能时发生异常\n\n") + ex.getMessage());
		}
	}

	// 根据付款编码直接转到相应的付款
	public void paySelect(String paycode)
	{
		int k;
		for (k = 0; k < GlobalInfo.payMode.size(); k++)
		{
			PayModeDef paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));

			if (paycode.equals(paymode.code))
				break;
		}

		if (k < GlobalInfo.payMode.size())
			gotoPayModeLocation(k);
	}

	public void paySelect()
	{
		if (GlobalInfo.sysPara.isusepaySelect != 'Y')
			return;

		// 输入付款代码
		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入付款代码或顺序号"), Language.apply("付款代码"), Language.apply("请输入付款代码以便快速使用该付款方式进行付款"), buffer, 0, 0, false)) { return; }

		// 查找付款方式并定位
		PayModeDef paymode = null;
		String s = buffer.toString().trim();
		int k;
		for (k = 0; k < GlobalInfo.payMode.size(); k++)
		{
			paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));

			if (s.equals(paymode.code))
				break;
		}
		if (k < GlobalInfo.payMode.size())
		{
			gotoPayModeLocation(k);
		}
		else
		{
			// 顺序号定位
			int pos = Convert.toInt(s) - 1;
			if (pos >= 0 && pos < table.getItemCount())
			{
				// 选中付款方式,如果不是直接付款的付款方式，立即回车进入详细付款界面
				String paycode = table.changeItemVar(pos)[0];
				paymode = DataService.getDefault().searchPayMode(paycode);
				table.setSelection(pos);
				keyReleased(null, GlobalVar.ArrowUp);
				if (!CreatePayment.getDefault().allowQuickInputMoney(paymode))
				{
					keyReleased(null, GlobalVar.Enter);
				}
			}
		}
	}

	public void gotoPayModeLocation(int pos)
	{
		PayModeDef modeDef = (PayModeDef) GlobalInfo.payMode.elementAt(pos);
		StringBuffer buffer = new StringBuffer();

		// 设置付款列表
		Vector v = saleBS.getPayModeBySuper(modeDef.sjcode, buffer, modeDef.code);

		// 定位付款方式
		if (buffer.length() > 0)
		{
			if (ishhPay == true)
				PaymentStatusChange(v, HH_CHANGE_TYPE01);
			table.exchangeContent(v);
			table.setSelection(Integer.parseInt(buffer.toString()));

			// 选中付款方式
			keyReleased(null, GlobalVar.ArrowUp);

			// 如果不是直接付款的付款方式，立即回车进入详细付款界面
			if (!CreatePayment.getDefault().allowQuickInputMoney(modeDef))
			{
				keyReleased(null, GlobalVar.Enter);
			}
		}
	}

	public void close()
	{
		ShellIsDisposed = true;

		//
		shell.close();
		shell.dispose();
	}

	public boolean deletePay()
	{
		if (isDeleting)
			return false;

		int index = table1.getSelectionIndex();

		if (index < 0) { return false; }

		String[] ax = table1.changeItemVar(index);
		String msg = ax[0] + Language.apply(" 付款 ") + ax[2];

		if (saleBS.checkDeleteSalePay(ax[0], true))
		{
			new MessageBox(Language.apply("该付款方式不允许被删除!\n\n"));
			return true;
		}
		isDeleting = true;
		try
		{
			if (new MessageBox(Language.apply("你确定要删除此以下付款吗?\n\n") + msg, null, true).verify() == GlobalVar.Key1)
			{
				// index 关联的副刊
				Payment p = (Payment) saleBS.payAssistant.get(index);

				// 删除付款方式
				if (saleBS.deleteSalePay(index))
				{
					table1.deleteRow(index);
					table1.assignLast();

					int index1 = -1;

					while ((index1 = p.getJoinPay()) >= 0)
					{
						saleBS.deleteSalePay(index1, true);
						table1.deleteRow(index1);
						table1.assignLast();
					}

					// 刷新一次金额输入框的初始值
					keyReleased(null, GlobalVar.ArrowUp);

					// 重算付款
					calcPayResult();

					return true;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			isDeleting = false;
		}

		return false;
	}

	public boolean calcPayResult()
	{
		// 显示余额
		if (!unpayfee.isDisposed())
			unpayfee.setText(saleBS.getPayBalanceLabel());

		// 刷新界面显示
		Display.getCurrent().update();

		// 付款足够,完成付款,如果没有付款方式则也不允许付款完成
		if (saleBS.comfirmPay() && (saleBS.calcPayBalance() <= 0))
		{
			if (saleBS.salePayment.size() <= 0)
			{
				new MessageBox(Language.apply("没有付款行,不能完成交易!"));
				return false;
			}

			ShellIsDisposed = true;

			if (saleBS.payComplete())
			{
				close();

				return true;
			}
			else
			{
				ShellIsDisposed = false;
			}
		}

		if (saleBS.salePayment.size() > 0)
		{
			// 客显显示已付款
			if (saleBS.saleHead.sjfk > 0)
			{
				DisplayMode.getDefault().lineDisplayPay();

				saleBS.sendSecMonitor("pay");
			}
			else
			{
				DisplayMode.getDefault().lineDisplayTotal();

				saleBS.sendSecMonitor("total");
			}
		}

		return false;
	}

	public boolean checkPayCodeNumberEquals(String txt, String code)
	{
		try
		{
			if (Integer.parseInt(txt) == Integer.parseInt(code)) { return true; }
		}
		catch (Exception ex)
		{
		}

		return false;
	}

	public void payEnter(PayModeDef paymode)
	{
		if (saleBS.checkIsSalePay(paymode.code))
		{
			new MessageBox(Language.apply("不允许使用该付款方式付款!\n\n"));
			return;
		}
		// 非主付款方式,TXT输入的是付款代码
		if (GlobalInfo.sysPara.isInputPayMoney != 'Y' && !(paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code)) && (txt.getText().trim().length() > 0) && paymode.isbank != 'N')
		{
			String paycode = txt.getText().trim();

			//
			txt.setText("");

			// 查找当前付款代码
			for (int i = 0; i < table.getItemCount(); i++)
			{
				String[] ax = table.changeItemVar(i);

				if (paycode.equals(ax[0]) || checkPayCodeNumberEquals(paycode, ax[0]))
				{
					table.setSelection(i);
					keyReleased(null, GlobalVar.Enter);
					return;
				}
			}

			// 顺序号定位
			int pos = Convert.toInt(paycode) - 1;
			if (pos >= 0 && pos < table.getItemCount())
			{
				table.setSelection(pos);
				keyReleased(null, GlobalVar.Enter);
				return;
			}

			return;
		}

		// 付款不足或是已存在的付款方式,进行付款记账
		if ((saleBS.calcPayBalance() > 0) || (GlobalInfo.sysPara.payover == 'Y' && saleBS.existPayment(paymode.code, "", true) >= 0))
		{
			String money = txt.getText().trim();

			// 检查下级付款方式个数
			int submode = 0;
			if (paymode.ismj != 'Y')
			{
				submode = saleBS.getPayModeBySuper(paymode.code).size();

				if (submode == 0)
				{
					new MessageBox(Language.apply("此付款方式不是末级，但是没有子集\n请重新配置付款方式或受限模板"));
					return;
				}
			}

			// 末级付款或没有下级付款方式,则记账;非末级付款进入下级付款
			if (paymode.ismj == 'Y' || submode == 0)
			{
				// 付款记账
				if (saleBS.payAccount(paymode, money))
				{
/*					//支付宝支付begin
					if(paymode.code.equals("0801")&&GlobalInfo.isOnline)
					{
						if(!aliPayAction.aliPay(saleBS,money))
						{
							return;
						}
					}
					//支付宝支付end
					else//正常支付
					{*/
					// 标记付款对象是否换货
					PaymentStatusChange(saleBS.salePayment, HH_CHANGE_TYPE02);

					// 刷新已付款列表,已付款列表的换货状态
					showSalePaymentDisplay();

					// 如果回到第2级付款方式，返回到1级
					// 如果是覆盖模式下的直接付款的付款方式，跳到下一个付款方式，否则停留在当前付款位置并刷新
					if (saleBS.backToInit(paymode))
					{
						showPayModeBySuper("0");
					}
					else if (saleBS.goToNextPaymode(paymode))
					{
						keyPressed(null, GlobalVar.ArrowDown);
						keyReleased(null, GlobalVar.ArrowDown);
					}
					else
					{
						// 刷新一次金额输入框的初始值
						keyReleased(null, GlobalVar.ArrowUp);
					}
					
				}
				else
				{
					txt.selectAll();
				}
			}
			else
			{
				showPayModeBySuper(paymode.code);

				// 如果只有一个下级付款,则直接选择
				if (submode == 1)
				{
					// 判断此时付款方式是否为直接付款，如果为直接付款，不直接显示明细窗口
					String[] ax = table.changeItemVar(0);
					String paycode = ax[0];
					PayModeDef paymode1 = DataService.getDefault().searchPayMode(paycode);
					if (paymode1.isbank != 'N')
					{
						keyReleased(null, GlobalVar.Enter);
						return;
					}
				}
			}
		}
		else
		{
			if (Convert.toDouble(txt.getText().trim()) > 0)
			{
				new MessageBox(Language.apply("付款已经足够,本次输入的金额无效!"));
				txt.selectAll();
				return;
			}
		}

		// 检查付款
		calcPayResult();
	}

	public void payExit(PayModeDef paymode)
	{
		// 非主付款方式,返回上一级付款列表
		if (!(paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code)))
		{
			// 找到上级付款方式
			PayModeDef pay = DataService.getDefault().searchPayMode(paymode.sjcode);

			if (pay != null)
			{
				// 显示再上级付款的下级,也就是本级的上级
				showPayModeBySuper(pay.sjcode);
			}
		}
		else
		{
			// 退出付款界面
			if (saleBS.exitPaySell())
			{
				close();
			}
			else
			{
				// 刷新已付款列表
				showSalePaymentDisplay();
			}
		}
	}

	// 换货情况下的界面切换
	private void PaymentStatusChange(Vector vector, int type)
	{
		switch (type)
		{
			case HH_CHANGE_TYPE01:
				for (int i = 0; i < vector.size(); i++)
				{
					((String[]) vector.get(i))[1] = "H&" + ((String[]) vector.get(i))[1];
				}
				break;

			case HH_CHANGE_TYPE02:
				if (vector.size() > 0)
				{
					if (ishhPay)
					{
						((SalePayDef) vector.get(vector.size() - 1)).str3 = "Y";
						((SalePayDef) vector.get(vector.size() - 1)).payname += Language.apply("(换)");
					}
					else
					{
						((SalePayDef) vector.get(vector.size() - 1)).str3 = "N";
					}
				}
				break;

			case HH_CHANGE_TYPE03:
				Vector salePayment = saleBS.salePayment;
				for (int i = 0; i < salePayment.size(); i++)
				{
					if (((SalePayDef) salePayment.get(i)).str3 != null && ((SalePayDef) salePayment.get(i)).str3.equals("Y"))
						((String[]) vector.get(i))[0] += "&H";
				}
				break;
		}
	}

	// 换货键按下的操作
	private void exchangeSale(PayModeDef paymode)
	{
		if (saleBS.hhflag == 'Y')
		{
			// 只有换货状态时才能使用换货键
			ishhPay = saleBS.exchangeSale(ishhPay);
			showPayModeBySuper(paymode.sjcode);
		}
	}

	private void showPayApportion()
	{
		try
		{
			int index = table1.getSelectionIndex();
			if (index < 0)
				return;

			saleBS.showPayApportion(index);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public PosTable getTable1()
	{
		return table1;
	}

	private void showRebateDetail(int key)
	{
		try
		{
			saleBS.payShowRebateDetail(key);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	

}
