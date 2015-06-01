package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentBankEvent
{
	private PaymentBankForm form = null;
	private Shell shell = null;
	private Combo cmbType = null;
	private Text txtSeqno = null;
	private Text txtAuthno = null;
	private Text txtDate = null;
	private Text txtAccount = null;
	private Text txtMoney = null;
	private StyledText txtStatus = null;
	private Label lblPayName = null;
	private Label lblSeqno = null;
	private Label lblAuthno = null;
	private Label lblDate = null;
	private Label lblAccount = null;
	private Label lblMoney = null;
	private SaleBS saleBS = null;
	private PaymentBank payObj = null;
	private int banktype = -1;
	private String track1, track2, track3;
	private PaymentBankFunc pbfunc = null;
	private boolean ShellIsDisposed = false;
	private boolean FunctionIsRun = false;
	private Label[] grpLabel = new Label[5];
	private Text[] grpText = new Text[5];
	private String[] grpLabelStr = { null, null, null, null, null };
	private String[] grpTextStr = { null, null, null, null, null };
	private boolean initDone = false;
	private boolean isSalePay = true;
	private long execstarttime = 0;

	public PaymentBankEvent(PaymentBankForm form, PaymentBank pay, int type)
	{
		// 传入的是消费,表示当前是交易付款模式
		this.payObj = pay;
		this.saleBS = pay.saleBS;
		if (type == PaymentBank.XYKXF)
			isSalePay = true;
		else
			isSalePay = false;

		// 接口对象
		this.form = form;
		this.banktype = type;
		this.pbfunc = CreatePayment.getDefault().getPaymentBankFunc(pay.paymode.code);

		// 初始化窗口
		init(form);
	}

	public PaymentBankEvent(PaymentBankForm form, int type)
	{
		this(form, "", type);
	}

	public PaymentBankEvent(PaymentBankForm form, String paycode, int type)
	{
		// 接口对象
		this.form = form;
		this.banktype = type;
		this.pbfunc = CreatePayment.getDefault().getPaymentBankFuncByMenu(getPaycodeByBankType(paycode, type));

		// 初始化窗口
		init(form);
	}

	public void initObject()
	{

	}

	private void init(PaymentBankForm pbf)
	{
		cmbType = pbf.getCmbType();
		txtAccount = pbf.getTxtAccount();
		txtDate = pbf.getTxtDate();
		txtSeqno = pbf.getTxtSeqno();
		txtAuthno = pbf.getTxtAuthno();
		txtMoney = pbf.getTxtMoney();
		shell = pbf.getSShell();
		lblPayName = pbf.getLblPayName();
		lblSeqno = pbf.getLblSeqno();
		lblAuthno = pbf.getLblAuthno();
		lblDate = pbf.getLblDate();
		lblAccount = pbf.getLblAccount();
		lblMoney = pbf.getLblMoney();
		txtStatus = pbf.getTxtStatus();

		grpLabel[0] = lblSeqno;
		grpLabel[1] = lblAuthno;
		grpLabel[2] = lblDate;
		grpLabel[3] = lblAccount;
		grpLabel[4] = lblMoney;

		grpText[0] = txtSeqno;
		grpText[1] = txtAuthno;
		grpText[2] = txtDate;
		grpText[3] = txtAccount;
		grpText[4] = txtMoney;
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
		key.inputMode = key.MsrInput;
		txtAccount.addKeyListener(key);
		txtAccount.setData("MSRINPUT");

		NewKeyListener key1 = new NewKeyListener();
		key1.event = event;
		txtSeqno.addKeyListener(key1);
		txtAuthno.addKeyListener(key1);
		txtDate.addKeyListener(key1);
		cmbType.addKeyListener(key1);

		NewKeyListener key2 = new NewKeyListener();
		key2.event = event;
		key2.inputMode = key.DoubleInput;
		txtMoney.addKeyListener(key2);

		// Rectangle rec =
		// Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		//
		initEvent();
	}

	private boolean isSalePay()
	{
		if (isSalePay && saleBS != null)
			return true;
		else
			return false;
	}

	public void initEvent()
	{
		if (isSalePay())
		{
			if (SellType.ISBACK(saleBS.saletype) && !saleBS.isRefundStatus())
			{
				txtStatus.setText("提示:请按上/下光标键选择交易类型\n再按回车键进行交易");
				lblMoney.setText("退款金额");

				if (this.payObj != null && this.payObj.paymode != null && this.payObj.paymode.name != null && this.payObj.paymode.name.indexOf("分期") >= 0)
				{
					// new MessageBox(this.payObj.paymode.name);
					String[] s = pbfunc.getBankClassConfig("getFuncItem", 0);
					if (s == null)
						s = pbfunc.getFuncItem();
					// 查找是否配置了分期的付款方式
					for (int x = 0; x < s.length; x++)
					{
						String str = s[x];
						// new MessageBox(str);
						if (str.indexOf("分期") >= 0 && str.indexOf("撤销") >= 0)
						{
							// new
							// MessageBox("查询到分期的付款方式"+str.substring(str.indexOf("[")+1,
							// str.indexOf("]")));
							banktype = Convert.toInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
							break;
						}
					}
				}

				if (banktype == 0)
				{
					// 根据参数判断仅指定的付款方式在退货小票时默认缺省为退货交易类别，其他为撤销交易类别
					boolean ispaycodebanktype = false;
					if (GlobalInfo.sysPara.paycodebanktype.length() > 0)
					{
						String paycodebanktype[] = GlobalInfo.sysPara.paycodebanktype.split(";");

						for (int i = 0; i < paycodebanktype.length; i++)
						{
							if (!this.payObj.paymode.code.equals(paycodebanktype[i]))
								continue;

							ispaycodebanktype = true;

							break;
						}
					}
					else
					{
						ispaycodebanktype = true;
					}

					// 设置默认采用退货交易类别且付款代码匹配时，缺省采用退货交易类别
					if (GlobalInfo.sysPara.displaybanktype == 'Y' && ispaycodebanktype)
					{
						banktype = PaymentBank.XYKTH;
					}
					else
					{
						banktype = PaymentBank.XYKCX;
					}
				}
			}
			else
			{
				lblMoney.setText("付款金额");
				if (this.payObj != null && this.payObj.paymode != null && this.payObj.paymode.name != null && this.payObj.paymode.name.indexOf("分期") >= 0)
				{
					String[] s = pbfunc.getBankClassConfig("getFuncItem", 0);
					if (s == null)
						s = pbfunc.getFuncItem();
					// 查找是否配置了分期的付款方式
					for (int x = 0; x < s.length; x++)
					{
						String str = s[x];
						if (str.indexOf("分期") >= 0)
						{
							// new
							// MessageBox("查询到分期的付款方式"+str.substring(str.indexOf("[")+1,
							// str.indexOf("]")));
							banktype = Convert.toInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
							break;
						}
					}
				}
				else
				{
					banktype = PaymentBank.XYKXF;
				}
			}

			lblPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

			// inputMoney由外部传入输入金额模式
			if (!isInputMoney())
			{
				double needPay = saleBS.calcPayBalance();
				txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay / payObj.paymode.hl, payObj.paymode));
			}
			else
			{
				// 以外部传入金额为缺省金额，只执行一次
				txtMoney.setText(saleBS.getPayMoneyByPrecision(Double.parseDouble(payObj.inputMoney), payObj.paymode));
			}
		}
		else
		{
			if (payObj != null && payObj.salepay != null)
			{
				lblPayName.setText(payObj.salepay.payname + (payObj.salepay.je < 0 ? "（扣回）" : ""));
			}
			else
			{
				lblPayName.setText("第三方支付");
			}

			if (payObj != null && payObj.salepay != null)
			{
				txtMoney.setText(ManipulatePrecision.doubleToString(Math.abs(payObj.salepay.ybje)));
			}
		}

		String[] s = pbfunc.getBankClassConfig("getFuncItem", 0);
		if (s == null)
			cmbType.setItems(pbfunc.getFuncItem());
		else
			cmbType.setItems(s);
		cmbType.setFocus();

		// 选中交易类型
		int typecount = 0;
		do
		{
			boolean find = false;
			for (int i = 0; i < cmbType.getItemCount(); i++)
			{
				if (Integer.parseInt(Convert.codeInString(cmbType.getItem(i).trim(), '[')) == banktype)
				{
					cmbType.select(i);
					find = true;

					// 菜单调用,直接定位功能,不选择交易类型
					if (payObj == null && pbfunc.getBankTypeByMenu())
						setLabelText(getType());
					break;
				}
			}
			if (find || banktype < 0)
				break;
			else
			{
				// 如果是退货或撤销，交换查找一下交易类型
				if (typecount <= 0 && (banktype == PaymentBank.XYKTH || banktype == PaymentBank.XYKCX))
				{
					typecount++;
					if (banktype == PaymentBank.XYKTH)
						banktype = PaymentBank.XYKCX;
					else
						banktype = PaymentBank.XYKTH;
					continue;
				}
				new MessageBox("支付接口不支持[" + banktype + "]号交易类型");
				shell.close();
				shell.dispose();
				return;
			}
		} while (true);

		// 小票交易时如果是消费，不允许修改交易类型,直接通过金卡工程接口确定那些项目需要输入
		if (isSalePay() && banktype == PaymentBank.XYKXF)
			setLabelText(getType());
	}

	public void keyPressed(KeyEvent e, int key)
	{
		if (ShellIsDisposed)
			return;

		switch (key)
		{

			case GlobalVar.ArrowUp:
				if (e.widget == txtDate)
				{
					if (txtAuthno.getEditable())
					{
						e.data = "focus";
						txtAuthno.selectAll();
						txtAuthno.setFocus();
					}
				}
				else if (e.widget == txtAuthno && txtAuthno.getEditable())
				{
					if (txtSeqno.getEditable())
					{
						e.data = "focus";
						txtSeqno.selectAll();
						txtSeqno.setFocus();
					}
				}
				else if (e.widget == txtSeqno && txtSeqno.getEditable())
				{
					if (txtDate.getEditable())
					{
						e.data = "focus";
						txtDate.selectAll();
						txtDate.setFocus();
					}
				}

				break;
		}

	}

	public void keyReleased(KeyEvent e, int key)
	{
		if (ShellIsDisposed)
			return;

		switch (key)
		{
			case GlobalVar.Enter:
				if (!FunctionIsRun)
				{
					enterInput(e);
				}
				break;
			case GlobalVar.MsrError:
				new MessageBox("刷卡失败,请重新刷卡...");
				txtAccount.selectAll();
				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
				break;
		}
	}

	private int getType()
	{
		String s = "";

		s = Convert.codeInString(cmbType.getItem(cmbType.getSelectionIndex()).trim(), '[');
		if (s.trim().length() <= 0)
			return cmbType.getSelectionIndex();
		else
			return Integer.parseInt(s);
	}

	private boolean checkType()
	{
		// 当金额小于0时代表扣回，必须使用消费
		if (payObj != null && payObj.salepay != null && payObj.salepay.je < 0)
		{
			if (banktype == PaymentBank.XYKXF)
				return true;
			else
			{
				new MessageBox("扣回付款必须使用消费");
				return false;
			}
		}
		else
		{
			if (isSalePay())
				return pbfunc.checkBankOperType(getType(), saleBS, payObj);
			else
				return pbfunc.checkBankOperType(getType(), null, payObj);
		}
	}

	private String getLabelStringByPaycode(String str)
	{
		// 明确银联接口对象对应的付款代码
		if (pbfunc.paycode == null && payObj != null && payObj.paymode != null)
			pbfunc.paycode = payObj.paymode.code;

		String[] s = str.split("\\|");
		for (int j = 0; j < s.length; j++)
		{
			String[] s1 = s[j].split(",");
			for (int n = 1; n < s1.length; n++)
			{
				if (s1[n].equalsIgnoreCase(pbfunc.paycode)) { return s1[0]; }
			}
		}

		return str;
	}

	private boolean setLabelText(int type)
	{
		// 此方法只调用一次
		if (initDone)
			return true;
		if (!initDone)
			initDone = true;

		Text txt = null;

		// 得到显示标签和输入框
		String[] s = pbfunc.getBankClassConfig("getFuncLabel", type);
		if (s == null)
			pbfunc.getFuncLabel(type, grpLabelStr);
		else
			grpLabelStr = s;
		s = pbfunc.getBankClassConfig("getFuncText", type);
		if (s == null)
			pbfunc.getFuncText(type, grpTextStr);
		else
			grpTextStr = s;

		// 设置显示标签和输入框
		for (int i = 0; i < 5; i++)
		{
			boolean editflag = false;
			String str = null;

			// 显示标签,并能根据付款代码确定标签输入
			str = grpLabelStr[i];
			if (str != null && str.toLowerCase().startsWith("calc|"))
				str = getLabelStringByPaycode(str.substring(5));
			if (str != null && !str.equals(""))
			{
				grpLabel[i].setText(str);
				editflag = true;
			}
			else
			{
				grpLabel[i].setText("");
				editflag = false;
			}

			// 输入框,allowempty标记输入框允许输入空,否则必须输入
			str = grpTextStr[i];
			if (str != null && str.toLowerCase().startsWith("calc|"))
				str = getLabelStringByPaycode(str.substring(5));
			if (str != null && str.equalsIgnoreCase("allowempty"))
				str = null;
			if (str != null && !str.equals(""))
			{
				grpText[i].setText(str);
				editflag = false;

				// 金额框总是允许编辑
				if (i >= 4)
				{
					editflag = true;
				}
			}
			else
			{
				if (editflag)
					editflag = true;

				// 除金额框外，其他输入框缺省都为空
				if (i < 4)
				{
					grpText[i].setText("");
				}
			}

			// 设置输入框编辑状态,有标签显示且输入框无值允许编辑
			grpText[i].setEditable(editflag);
			if (ConfigClass.MouseMode)
				grpText[i].setEnabled(editflag);
			if (txt == null && editflag)
				txt = grpText[i];
		}

		//
		if (txt == null)
			return false;

		// 设置输入焦点
		txt.selectAll();
		txt.setFocus();

		return true;
	}

	private boolean gotoNextInput(Text txt)
	{
		int i = 0;

		// 找到当前焦点输入框,前面的输入框都不允许编辑
		for (i = 0; i < 5; i++)
		{
			if (grpText[i].equals(txt))
				break;
			
			if (payObj != null && payObj.paymode != null && ("|" + GlobalInfo.sysPara.paycodebankform + "|").indexOf("|" + payObj.paymode.code + "|") < 0)
				grpText[i].setEditable(false);
		}

		if (txt.equals(txtSeqno) && !pbfunc.checkSeqno(txt)) { return false; }

		// 检查输入项是否符合要求
		if (txt.equals(txtDate) && !pbfunc.checkDate(txt))
		{
			return false;
		}
		else
		{
			// 项目必须输入
			String str = grpTextStr[i];
			if (str != null && str.toLowerCase().startsWith("calc|"))
				str = getLabelStringByPaycode(str.substring(5));
			if ((str == null || !str.equalsIgnoreCase("allowempty")) && (txt.getEnabled() && txt.getText().trim().length() <= 0)) { return false; }
		}

		// 找到下一个可编辑的输入框焦点
		for (i++; i < 5; i++)
		{
			if (grpText[i].getEditable())
			{
				if (payObj != null && payObj.paymode != null)
				{
					if (("|" + GlobalInfo.sysPara.paycodebankform + "|").indexOf("|" + payObj.paymode.code + "|") < 0)
						txt.setEditable(false);
				}
				grpText[i].selectAll();
				grpText[i].setFocus();
				return true;
			}
		}

		return false;
	}

	void enterInput(KeyEvent e)
	{
		if (e.widget.equals(cmbType))
		{
			initDone = false;

			// 检查交易类型
			if (!checkType()) { return; }

			// 退货交易如果选择消费撤销提示用户选择是否确实是退所有商品
			if (isSalePay() && SellType.ISBACK(saleBS.saletype) && getType() == PaymentBank.XYKCX)
			{
				if (GlobalInfo.sysPara.bankCXMsg.equalsIgnoreCase("Y") && new MessageBox("消费撤销是撤销原消费交易的整笔金额\n\n顾客确实是要退掉原交易的所有商品吗？", null, true).verify() != GlobalVar.Key1) { return; }
			}

			// 通过金卡工程接口确定那些项目需要输入
			if (setLabelText(getType()))
			{
				e.data = "focus";
			}
			txtStatus.setText("");
		}
		else
		{
			Text txt = (Text) e.widget;

			// 金额输入框是最后一个输入框,开始交易
			if (txt.equals(txtMoney))
			{
				// 检查交易类型
				if (!checkType())
				{
					cmbType.setFocus();
					e.data = "focus";
					return;
				}

				// 小票交易时，先生成付款对象,校验金额等信息是否合法
				if (isSalePay())
				{
					String currmoney = "";

					if (txt.getText() == null || (txt.getText() != null && !ManipulatePrecision.isDoubleOrNumber(txt.getText())))
					{
						currmoney = saleBS.getPayBalanceLabel();
					}
					else
					{
						currmoney = txt.getText();
					}

					if (!payObj.createSalePay(currmoney))
					{
						txtMoney.selectAll();
						return;
					}
				}

				// 转换交易金额
				double amount = 0;
				try
				{
					amount = Double.parseDouble(txtMoney.getText());
				}
				catch (Exception er)
				{
					amount = 0;
				}

				if (payObj != null && !payObj.checkJeValid(amount))
				{
					txtMoney.selectAll();
					return;
				}

				// 调用金卡工程接口
				txtStatus.setText("开始调用支付接口,请等待...");
				txtStatus.update();

				// 执行一次，本窗口不在相应ENTER
				ShellIsDisposed = true;

				// memo的0项总是传入付款方式代码,1小票号
				Vector memo = new Vector();
				if (payObj != null && payObj.paymode != null)
				{
					memo.add(payObj.paymode.code);
					memo.add(String.valueOf(payObj.salepay.fphm));
					memo.add(payObj.saleBS);
					memo.add(payObj.salepay);
				}

				// 设置等待执行时的回调显示
				execstarttime = System.currentTimeMillis();
				pbfunc.setWaitCallBack(new Runnable()
				{
					public void run()
					{
						long second = (System.currentTimeMillis() - execstarttime) / 1000;
						txtStatus.setText("开始调用支付接口,请等待(" + second + ")...");
					}
				});

				// 调用支付接口
				int type = getType();
				boolean ret = pbfunc.callBankFunc(type, amount, track1, track2, track3, txtSeqno.getText(), txtAuthno.getText(), txtDate.getText(), memo);

				// 窗口每次只允许一次银联调用
				FunctionIsRun = true;
				txtStatus.setText(((payObj != null && payObj.paymode != null) ? payObj.paymode.name : "第三方支付") + "接口调用完成!" + "\n" + pbfunc.getErrorMsg());

				// 将第三方支付结果记账到salepay付款对象
				if (payObj != null)
				{
					// 是交易小票付款无论支付结果成功与否都将数据记录到salepay对象或放弃salepay对象
					if (isSalePay())
					{
						payObj.accountPay(ret, pbfunc);
					}
					else
					{
						// 非交易小票付款(红冲等)支付只有支付成功才将数据记录到salepay对象
						if (ret)
						{
							// 原始金额小于0,表示是扣回付款,交易成功记账后,金额要恢复为负数
							if (payObj.salepay != null && payObj.salepay.je < 0)
							{
								payObj.accountPay(ret, pbfunc);

								payObj.salepay.ybje *= -1;
								payObj.salepay.je *= -1;
							}
							else
							{
								payObj.accountPay(ret, pbfunc);
							}
						}
					}
				}

				// 检查金卡工程交易是否成功
				if (!ret)
				{
					// 如果是显示ERROR消息模式，则弹出错误信息
					if (pbfunc.getErrorMsgShowMode())
					{
						new MessageBox(pbfunc.getErrorMsg());
					}

					//
					txtMoney.selectAll();

					// 恢复允许按键
					ShellIsDisposed = false;
				}
				else
				{
					try
					{
						new MessageBox(cmbType.getItem(cmbType.getSelectionIndex()).trim() + " 交易成功");
					}
					catch (Exception er)
					{
						new MessageBox("第三方支付交易成功!");
					}

					// 关闭窗口
					form.setDone(true);
					shell.close();
					shell.dispose();
				}
			}
			else
			{
				// 跳到下一个输入框
				if (gotoNextInput(txt))
				{
					e.data = "focus";
				}
			}
		}
	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		// 记录磁道信息
		this.track1 = track1;
		this.track2 = track2;
		this.track3 = track3;

		if ((track1 + track2 + track3).length() <= 0)
		{
			txtAccount.setText("请刷卡......");
			txtAccount.selectAll();
			return;
		}

		if (GlobalInfo.sysPara.iscardmsg == 'Y')
		{
			String temptrack[] = this.track2.split("=");

			if (temptrack[0].length() > 0)
			{
				temptrack[0] = temptrack[0] + "                ";
				new MessageBox(temptrack[0].substring(0, 4) + "  " + temptrack[0].substring(4, 8) + "  " + temptrack[0].substring(8, 12) + "  " + temptrack[0].substring(12, 16) + (temptrack[0].length() > 16 ? "  " + temptrack[0].substring(16, temptrack[0].length()) : ""));// 兼容19位卡号

			}
			else
			{
				new MessageBox("无卡信息读出");
			}
		}

		if (isSalePay())
		{
			if (saleBS.backPayment != null)
			{
				String Strcardno = track2;
				if (Strcardno.indexOf("=") > 0)
				{
					Strcardno = Strcardno.substring(0, Strcardno.indexOf("="));
				}

				for (int i = 0; i < saleBS.backPayment.size(); i++)
				{
					SalePayDef spd = (SalePayDef) saleBS.backPayment.get(i);

					if (spd.paycode.equals(payObj.paymode.code))
					{
						if (Strcardno.equals(spd.payno))
						{
							double needPay = spd.ybje;
							txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay, payObj.paymode));
							break;
						}
					}
				}
			}
		}

		//
		setLabelText(getType());
		gotoNextInput(txtAccount);

		e.data = "focus";
	}

	private boolean isInputMoney()
	{
		try
		{
			if (this.payObj == null || this.payObj.inputMoney == null || this.payObj.inputMoney.toString().trim().length() < 1)
				return false;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	private String getPaycodeByBankType(String paycode, int type)
	{
		// paycode为空时,根据banktype中配置的银联交易类型对应的付款代码来创建接口对象
		if (paycode == null || paycode.trim().length() <= 0)
		{
			if(!PathFile.fileExist(GlobalVar.ConfigPath + "//banktype.ini"))
				return paycode;
			
			BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "//banktype.ini");
			if (br != null)
			{
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() <= 0)
							continue;

						if (line.charAt(0) == ';')
							continue;

						if (line.split("=").length >= 2)
						{
							String[] infos = line.split("=");
							if (Convert.toInt(infos[0]) == type)
							{
								paycode = infos[1].trim();
								break;
							}
						}
						else
						{
							continue;
						}
					}
					br.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}

		return paycode;
	}
}
