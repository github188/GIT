package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.UI.Design.DebugForm;
import com.efuture.javaPos.UI.Design.MenuFuncForm;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.efuture.javaPos.UI.Design.SjjDateform;
import com.efuture.javaPos.UI.Design.SjjUserInfoForm;
import com.swtdesigner.SWTResourceManager;


public class SaleEvent
{
	public SaleBS saleBS = null;
	public SaleForm saleform = null;
	public Text yyyh = null;
	public Text gz = null;
	public Text code = null;
	public PosTable table = null;
	public PosTable poptable = null;
	public StyledText zhongwenStyledText;
	public Label hjje;
	public Label hjzk;
	public Label lbl_yfje;
	public Label yfje;
	public Label lbl_hjzje;
	public Label hjzje;
	public Label hjzsl;
	public Label hjzke;
	public Label fphm;
	public Label syyh;
	public Label fmType;
	public Label vipinfo;
	public Group group_4 = null;
	public Group group_5 = null;
	public boolean ShellIsDisposed = false;
	protected String lastsalebk = null;

	public Composite category = null;
	public Composite pay = null;
	public Composite finished = null;
	

	Vector v = new Vector();

	public SaleEvent(SaleForm saleform)
	{
		initForm(saleform);
	}
	
	//liwj add
	public void initForm(SaleForm saleform)
	{
		this.saleform = saleform;
		this.yyyh = saleform.yyyh;
		this.gz = saleform.gz;
		this.code = saleform.code;
		this.table = saleform.table;
		this.poptable = saleform.table_pop;
		this.zhongwenStyledText = saleform.zhongwenStyledText;
		this.hjzje = saleform.hjzje;
		this.yfje = saleform.yfje;
		this.hjje = saleform.hjje;
		this.hjzsl = saleform.hjzsl;
		this.hjzk = saleform.hjzk;
		this.hjzke = saleform.hjzke;
		this.fphm = saleform.fphm;
		this.syyh = saleform.syyh;
		this.fmType = saleform.fmType;
		this.lbl_hjzje = saleform.lbl_hjzje;
		this.lbl_yfje = saleform.lbl_yfje;
		this.vipinfo = saleform.vipinfo;
		this.group_4 = saleform.group_4;
		this.group_5 = saleform.group_5;

		// 增加监听器
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				preProcKeyInput(e, key);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		

		yyyh.addKeyListener(key);
		gz.addKeyListener(key);
		code.addKeyListener(key);

		if (ConfigClass.MouseMode)
			MouseEventInit();

		// 创建业务对象
		saleBS = CustomLocalize.getDefault().createSaleBS();

		saleBS.loadPosTableConfig();

		saleBS.setUI(saleform);
		saleBS.setSaleEvent(this);

		// 初始化交易
		if (GlobalInfo.posLogin != null && GlobalInfo.posLogin.funcmenu != null && GlobalInfo.posLogin.funcmenu.indexOf("0101") < 0)
		{
			if (GlobalInfo.posLogin.funcmenu.indexOf("0105") >= 0)
			{
				initOneSale(SellType.BATCH_SALE);
			}
			else if (GlobalInfo.posLogin.funcmenu.indexOf("0102") >= 0)
			{
				initOneSale(SellType.EXERCISE_SALE);
			}
			else if (GlobalInfo.posLogin.funcmenu.indexOf("0108") >= 0)
			{
				initOneSale(SellType.CHECK_INPUT);
			}
			else
			{
				initOneSale(SellType.RETAIL_SALE);
			}
		}
		else
			initOneSale(SellType.RETAIL_SALE);
	}

	// 预处理键盘输入
	public void preProcKeyInput(KeyEvent e, int key)
	{
		if (EBill.getDefault().isEnable() && EBill.getDefault().isEditBillFlag())
		{
			key = EBill.getDefault().keyReleased(this,e, key);
			keyReleased(e, key);
		}
		else
		{
			keyReleased(e, key);
		}
	}

	public void MouseEventInit()
	{
		MouseAdapter mouse = new MouseAdapter()
		{
			public void mouseUp(MouseEvent arg0)
			{
				if (arg0.widget.equals(table))
				{
					Point selectedPoint = new Point(arg0.x, arg0.y);

					int index = table.getTopIndex();

					if (index < 0)
						return;

					while (index < table.getItemCount())
					{
						TableItem item = table.getItem(index);
						for (int i = 0; i < table.getColumnCount(); i++)
						{
							Rectangle rect = item.getBounds(i);
							if (rect.contains(selectedPoint))
							{
								if (i == 1) // 商品编码
								{
									NewKeyListener.sendKey(GlobalVar.Validation);
								}
								else if (i == 4) // 数量
								{
									NewKeyListener.sendKey(GlobalVar.Quantity);
								}
								else if (i == 5) // 定价
								{
									NewKeyListener.sendKey(GlobalVar.SetPrice);
								}
								else if (i == 6) // 折扣
								{
									NewKeyListener.sendKey(GlobalVar.Rebate);
								}
								else if (i == 7) // 应收金额
								{
									NewKeyListener.sendKey(GlobalVar.RebatePrice);
								}
							}
						}
						index++;
					}
				}
			}

			public void mouseDoubleClick(MouseEvent arg0)
			{
				if (arg0.widget.equals(table))
				{
					Point selectedPoint = new Point(arg0.x, arg0.y);

					int index = table.getTopIndex();

					if (index < 0)
						return;

					while (index < table.getItemCount())
					{
						TableItem item = table.getItem(index);
						for (int i = 0; i < table.getColumnCount(); i++)
						{
							Rectangle rect = item.getBounds(i);
							if (rect.contains(selectedPoint))
							{
								if (i == 2) // 商品名称
								{
									NewKeyListener.sendKey(GlobalVar.Del);
								}
							}
						}
						index++;
					}
				}
				else if (arg0.widget.equals(yyyh))
				{
					NewKeyListener.sendKey(GlobalVar.StaffText);
				}
				else if (arg0.widget.equals(code))
				{
					if (Display.getCurrent().getFocusControl().equals(yyyh) || Display.getCurrent().getFocusControl().equals(gz))
					{
						NewKeyListener.sendKey(GlobalVar.Enter);
					}
				}
			}
		};

		table.addMouseListener(mouse);
		yyyh.addMouseListener(mouse);
		code.addMouseListener(mouse);
	}

	// 检查能否退出
	public boolean checkAllowExit()
	{
		return saleBS.checkAllowExit();
	}

	// 检查能否初始化
	public boolean checkAllowInit()
	{
		return saleBS.checkAllowInit();
	}

	public void initOneSale(String type)
	{
		// 刷新界面交互
		while (Display.getCurrent().readAndDispatch())
		{
			;
		}


		saleBS.initOneSale(type);
	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				table.moveUp();
				setCurGoodsBigInfo();

				break;

			case GlobalVar.ArrowDown:
				table.moveDown();
				setCurGoodsBigInfo();

				break;

			case GlobalVar.PageDown:
				table.PageDown();
				setCurGoodsBigInfo();

				break;

			case GlobalVar.PageUp:
				table.PageUp();
				setCurGoodsBigInfo();

				break;
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.MainList:
				showFuncMenu(); // 调用菜单

				break;

			case GlobalVar.Exit:
				exitInput();

				break;

			case GlobalVar.Enter:
				enterInput();

				break;

			case GlobalVar.Back:
				backInput();

				break;

			case GlobalVar.Quantity:
				inputQuantity();

				break;

			case GlobalVar.SetPrice:
				inputPrice();

				break;

			case GlobalVar.Del:
				deleteCurrentGoods();

				break;

			case GlobalVar.Clear:
				clearSell();

				break;

			case GlobalVar.Rebate:
				inputRebate();

				break;

			case GlobalVar.RebatePrice:
				inputRebatePrice();

				break;

			case GlobalVar.WholeRate:
				inputAllRebate();

				break;

			case GlobalVar.WholeRebate:
				inputAllRebatePrice();

				break;

			case GlobalVar.writeHang:
				writeHnag();

				break;

			case GlobalVar.readHang:
				readHang();

				break;

			case GlobalVar.StaffText:

				inputStaff();

				break;

			case GlobalVar.OperGrant:
				operGrantInput();

				break;

			case GlobalVar.MemberGrant:
				memberGrant();

				break;

			case GlobalVar.MoreUnit:
				break;

			case GlobalVar.Pay:
				payInput();

				break;
			case GlobalVar.Print:
				saleBS.rePrint();
				break;

			case GlobalVar.InputFphm:
				inputSaleFphm();
				break;

			case GlobalVar.HYRebate:
				chooseHyRebate();
				break;
			case GlobalVar.Validation:
				rebateDetail();
				break;
			case GlobalVar.JfExchange:
				findJfExchangeGoods();
				break;
			case GlobalVar.BuyInfo:
				saleBS.selectAllCustomerInfoTree();
				break;
			case GlobalVar.InputJdfhdd:
				saleBS.inputJdfhdd();
				break;

			case GlobalVar.InputPreSale:
				saleBS.preShopSale();
				break;
				
				
			case GlobalVar.InputMemoInfo:
				inputMemoInfo();
				break;

			case GlobalVar.PayBank: // 银联卡付款键
			case GlobalVar.PayCash: // 现金付款键
			case GlobalVar.PayCheque: // 支票付款键
			case GlobalVar.PayCredit: // 信用卡付款键
			case GlobalVar.PayMzk: // 面值卡付款键
			case GlobalVar.PayGift: // 礼券付款键
			case GlobalVar.PayTally: // 赊账付款键
				QuickPayInput(key);

				break;

			case GlobalVar.EBill:
				saleBS.openEBill();
				break;

			case GlobalVar.Debug: // 调试
				new DebugForm().open(saleBS, "saleBS");
				break;
			case GlobalVar.AutoTest:
				saleBS.autoTest();
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

			case GlobalVar.ExchangeSell:
				exchangeSale();
				break;

			case GlobalVar.ChangeGBillName:
				changeBillName();
				break;

			case GlobalVar.QueryGoodsInfo: // 商品模糊查询 wangyong add by 2010.5.28
				queryGoodsInfo();
				break;

			case GlobalVar.locategoods:
				locateGoods();
				break;
				
			case GlobalVar.InputStam:
				stampGrant();
				break;
		}
	}
	

	public void inputMemoInfo()
	{
		try
		{
			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				saleBS.inputMemoInfo(index);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void queryGoodsInfo()
	{
		try
		{
			saleBS.queryGoodsInfo();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void rebateDetail()
	{
		int index = table.getSelectionIndex();

		if (index >= 0 && saleBS.saleGoods != null && saleBS.saleGoods.size() > 0)
		{
			saleBS.rebateDetail(index);
		}
	}

	public void chooseHyRebate()
	{
		if (GlobalInfo.sysPara.isHandVIPDiscount == 'Y')
		{
			// 启用手工输入VIP折扣率模式
			int index = table.getSelectionIndex();
			saleBS.calcHandVIPDiscount(index);

			// 刷新商品列表
			updateTable(saleBS.getSaleGoodsDisplay());
			table.setSelection(index);

			// 显示汇总
			setTotalInfo();

			// 显示商品大字信息
			setCurGoodsBigInfo();
		}
		// 按键控制是否计算会员折扣开关
		else if (GlobalInfo.sysPara.isHandVIPDiscount == 'A')
		{
			// 非销售状态不处理
			if (!SellType.ISSALE(saleBS.saleHead.djlb))
				return;
			// 没有刷会员卡不处理
			if (saleBS.curCustomer == null)
				return;

			if (saleBS.isVIPZK)
			{
				if (new MessageBox(Language.apply("确定要取消之后销售商品的会员折扣吗？"), null, true).verify() == GlobalVar.Key1)
				{
					saleBS.isVIPZK = false;
				}
			}
			else
			{
				if (new MessageBox(Language.apply("确定要恢复之后销售商品的会员折扣吗？"), null, true).verify() == GlobalVar.Key1)
				{
					saleBS.isVIPZK = true;
				}
			}
		}
		else
		{
			// 不启用手工输入VIP折扣率模式
			if (saleBS.chooseHyRebate())
			{
				// 显示VIP顾客卡信息
				setVIPInfo(saleBS.getVipInfoLabel());
			}
		}
	}

	public void showFuncMenu()
	{
		try
		{

			// 显示功能菜单窗口
			new MenuFuncForm(saleform.getShell(), GlobalInfo.posLogin.funcmenu);

			// 如果小票号发生改变
			if (saleBS.saleHead.fphm != GlobalInfo.syjStatus.fphm)
			{
				// 刷新数据
				saleBS.refreshSaleData();

				// 刷新小票号显示
				setSYJInfo();

			}

			try
			{
				setCurGoodsBigInfo();
			}
			catch (Exception er)
			{

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打开功能菜单时发生异常\n\n") + ex.getMessage());
		}
	}

	public void exitInput()
	{
		try
		{
			// 如果当前Text有值那么清空
			if (saleform.getFocus().getText().length() > 0)
			{
				saleform.getFocus().setText("");
			}
			else
			{
				// 退出系统
				saleBS.exitSell();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("退出系统时发生异常\n\n") + ex.getMessage());
		}
	}

	public void enterInput()
	{
		try
		{
			saleBS.enterInput();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("完成输入时发生异常\n\n") + ex.getMessage());
		}
	}

	public void backInput()
	{
		try
		{
			saleBS.backSell();

			// 如果小票号发生改变
			if (saleBS.saleHead.fphm != GlobalInfo.syjStatus.fphm)
			{
				// 刷新数据
				saleBS.refreshSaleData();

				// 刷新小票号显示
				setSYJInfo();
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("进入退货时发生异常\n\n") + ex.getMessage());
		}
	}

	public void clearSell()
	{
		try
		{
			// 如果当前Text有值那么清空
			if (saleform.getFocus().getText().length() > 0)
			{
				saleform.getFocus().setText("");
			}
			else
			{
				int index = table.getSelectionIndex();

				saleBS.clearSell(index);

				try{
					// 刷新商品列表，更新断点数据
					saleBS.getSaleGoodsDisplay();
				}catch(Exception er)
				{
					er.printStackTrace();
				}
				finally
				{
					//如果刷新发现异常，并且salegoods里没有数据，直接清空界面。
					if (saleBS.saleGoods != null && saleBS.saleGoods.size() <= 0)
					{
						initGUI();
						saleBS.initSetYYYGZ(saleBS.saletype, false);
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("取消交易时发生异常\n\n") + ex.getMessage());
		}
	}

	public void operGrantInput()
	{
		try
		{
			saleBS.operGrant();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("人员授权时发生异常\n\n") + ex.getMessage());
		}
	}

	public void QuickPayInput(int key)
	{
		try
		{
			saleBS.quickPayButton(key);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("快捷付款时发生异常\n\n") + ex.getMessage());
		}
	}

	public void customKeyInput(int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.CustomKey0:
					saleBS.execCustomKey0(true);

					break;

				case GlobalVar.CustomKey1:
					saleBS.execCustomKey1(true);

					break;

				case GlobalVar.CustomKey2:
					saleBS.execCustomKey2(true);

					break;

				case GlobalVar.CustomKey3:
					saleBS.execCustomKey3(true);

					break;

				case GlobalVar.CustomKey4:
					saleBS.execCustomKey4(true);

					break;

				case GlobalVar.CustomKey5:
					saleBS.execCustomKey5(true);

					break;

				case GlobalVar.CustomKey6:
					saleBS.execCustomKey6(true);

					break;

				case GlobalVar.CustomKey7:
					saleBS.execCustomKey7(true);

					break;

				case GlobalVar.CustomKey8:
					saleBS.execCustomKey8(true);

					break;

				case GlobalVar.CustomKey9:
					saleBS.execCustomKey9(true);

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("自定义功能时发生异常\n\n") + ex.getMessage());
		}
	}

	public void deleteCurrentGoods()
	{
		try
		{
			int index = table.getSelectionIndex();

			if (index >= 0 && saleBS.saleGoods != null && saleBS.saleGoods.size() > 0)
			{
				// 删除商品
				if (saleBS.deleteGoods(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());

					if (table.getItemCount() > index)
					{
						table.setSelection(index);
					}
					else
					{
						table.setSelection(table.getItemCount() - 1);
					}

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("删除商品时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputQuantity()
	{
		try
		{
			if (code.getText().length() > 0 && GlobalInfo.sysPara.quantityChange.equals("Y"))
			{
				NewKeyListener.addKey(code, "*");

				return;
			}

			//
			if (!saleBS.allowEditGoods()) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品数量
				if (saleBS.inputQuantity(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(index);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入数量时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputStaff()
	{
		try
		{
			int index = table.getSelectionIndex();

			saleBS.yyyInput(index);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入营业员时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputPrice()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品价格
				if (saleBS.inputPrice(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(index);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入价格时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputRebate()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品折扣
				if (saleBS.inputRebate(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(index);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入折扣时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputCJZK()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品折扣
				if (saleBS.inputCJZK(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(index);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入折扣时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputSaleFphm()
	{
		try
		{
			saleBS.inputSaleFphm();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入发票号时发生异常\n\n") + ex.getMessage());
		}
	}

	public void locateGoods()
	{
		try
		{
			saleBS.locateGoods();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入发票号时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputRebatePrice()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品折扣
				if (saleBS.inputRebatePrice(index))
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(index);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入折让时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputAllRebatePrice()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			if (table.getItemCount() > 0)
			{
				// 输入商品折扣
				if (saleBS.inputAllRebatePrice())
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(table.getItemCount() - 1);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入总折扣时发生异常\n\n") + ex.getMessage());
		}
	}

	public void inputAllRebate()
	{
		try
		{
			if (!saleBS.allowEditGoods()) { return; }

			if (table.getItemCount() > 0)
			{
				// 输入商品折扣
				if (saleBS.inputAllRebate())
				{
					// 刷新商品列表
					updateTable(saleBS.getSaleGoodsDisplay());
					table.setSelection(table.getItemCount() - 1);

					// 显示汇总
					setTotalInfo();

					// 显示商品大字信息
					setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入总折让时发生异常\n\n") + ex.getMessage());
		}
	}

	public void writeHnag()
	{
		try
		{
			

			saleBS.writeHang();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("执行挂单时发生异常\n\n") + ex.getMessage());
		}
	}

	public void readHang()
	{
		try
		{
			if (table.getItemCount() > 0 && !GlobalInfo.sysPara.onlineGd.equals("A")) { return; }

			saleBS.readHang();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("执行解挂时发生异常\n\n") + ex.getMessage());
		}
	}

	public void payInput()
	{
		if (ShellIsDisposed) { return; }

		ShellIsDisposed = true;

		try
		{
			if (table.getItemCount() > 0)
			{
				saleBS.payInput();
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			new MessageBox(Language.apply("交易付款时发生异常\n\n") + er.getMessage());
		}
		finally
		{
			ShellIsDisposed = false;
		}
	}

	public void memberGrant()
	{
		try
		{
			// 顾客卡授权
			if (saleBS.memberGrant())
			{
				// 显示VIP顾客卡信息
				setVIPInfo(saleBS.getVipInfoLabel());

				// 刷新商品列表
				updateTable(saleBS.getSaleGoodsDisplay());
				table.setSelection(table.getItemCount() - 1);

				// 显示汇总
				setTotalInfo();

				// 显示商品大字信息
				setCurGoodsBigInfo();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入顾客卡时发生异常\n\n") + ex.getMessage());
		}
	}
	
	public void stampGrant()
	{
		try
		{
			// 输入印花促销券
			if (saleBS.inputStampPop())
			{
				saleBS.calcStampPop(0, saleBS.stampList.size() - 1);
				saleBS.calcHeadYsje();
				// 刷新商品列表
				updateTable(saleBS.getSaleGoodsDisplay());
				table.setSelection(table.getItemCount() - 1);

				// 显示汇总
				setTotalInfo();

				// 显示商品大字信息
				setCurGoodsBigInfo();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入印花券时发生异常\n\n") + ex.getMessage());
		}
	}

	public void initGUI()
	{
		// 清空大字显示
		setBigInfo("", "", "", -1);

		// 显示收银机信息
		setSYJInfo();

		// 清除VIP信息
		setVIPInfo("");

		// 清除商品列表
		clearTableItem();

		// 刷新应收提示
		setTypeInfo();

		// 显示上笔交易
		setCurGoodsBigInfo();

		// 清空输入框
		this.setGroupInputArea(Language.apply("输入区"));
		clearInput();
	}

	public void updateSaleGUI()
	{
		// 刷新商品列表
		updateTable(saleBS.getSaleGoodsDisplay());

		// 总是显示最后一行
		table.setSelection(table.getItemCount() - 1);
		table.showSelection();

		// 显示合计
		setTotalInfo();

		// 显示商品大字信息
		setCurGoodsBigInfo();

		// 显示顾客卡
		setVIPInfo(saleBS.getVipInfoLabel());

		// 刷新应收提示
		setTypeInfo();
	}

	public void clearInput()
	{
		yyyh.setText("");
		gz.setText("");
		code.setText("");
	}

	public void updateTable(boolean defaultFlag)
	{
		// table.exchangeContent(content);

		// 客显显示商品
		DisplayMode.getDefault().lineDisplayGoods();
	}

	public void updateTable(String[] content)
	{
		table.addRow(content);

		// 客显显示商品
		DisplayMode.getDefault().lineDisplayGoods();
	}

	public void clearTableItem()
	{
		table.clear();

		setTotalInfo("", "", "", "", "");
	}

	public void setCurGoodsBigInfo()
	{
		int num = table.getSelectionIndex();

		if (num >= 0)
		{
			// 显示选中商品信息
			String[] value = table.changeItemVar(num);

			value = saleBS.convertColumnValue(value, num);

			setBigInfo(value[2], value[7], value[6], num);

			// 广告屏显示商品资料
			saleBS.sendSecMonitor("goods", value, num);
		}
		else
		{
			// 显示上笔小票
			if (saleBS.lastsaleHead != null)
			{
				String sjfk = saleBS.getLastSjfk();
				String zlje = Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleBS.lastsaleHead.zl), 0, sjfk.length(), sjfk.length(), 1);
				if (SellType.ISBACK(saleBS.lastsaleHead.djlb))
				{
					setBigInfo(Language.apply("上笔实退: {0}\n上笔找回: ", new Object[]{sjfk}) + zlje, "", "", -1);
				}
				else
				{
					setBigInfo(Language.apply("上笔实收: {0}\n上笔找零: ", new Object[]{sjfk}) + zlje, "", "", -1);
				}
				saleBS.lastsaleHead = null;
			}
			else
			{
				setBigInfo("", "", "", -1);
			}

			// 广告屏显示
			saleBS.sendSecMonitor("welcome");
		}
	}

	public void setBigInfo(String styletext, String hjje, String hjzk, int goodsindex)
	{
		saleBS.bigInfoMemoSet(goodsindex);
		if (!zhongwenStyledText.isDisposed())
		{
			if (saleBS.reSetBigColor(goodsindex))
			{
				if (saleform.bkimg != null)
				{
					if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(222, 10, 158));
					}
					else
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(255, 0, 128));
					}
				}
				else
				{
					if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(0, 150, 0));
						zhongwenStyledText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					}
					else
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(0, 150, 0));
						zhongwenStyledText.setBackground(SWTResourceManager.getColor(255, 255, 0));
					}
				}
			}
			else
			{
				if (saleform.bkimg != null)
				{
					if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(222, 10, 158));
					}
					else
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(255, 0, 128));
					}
				}
				else
				{
					if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(222, 10, 158));
						zhongwenStyledText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					}
					else
					{
						zhongwenStyledText.setForeground(SWTResourceManager.getColor(255, 0, 128));
						zhongwenStyledText.setBackground(SWTResourceManager.getColor(255, 255, 0));
					}
				}
			}

			zhongwenStyledText.setText(styletext);
		}

		if (!this.hjje.isDisposed())
		{
			if (saleBS.saleGoods != null && saleBS.saleGoods.size() > 0)
			{
				this.hjje.setText(hjje);
			}
			else
			{
				String num = saleBS.getUnUploadSaleHead();

				if ("0".equals(num))
				{
					this.hjje.setText(hjje);
				}
				else
				{
					this.hjje.setText(Language.apply("未上传小票：") + num);
				}

			}
		}

		if (!this.hjzk.isDisposed())
			this.hjzk.setText(hjzk);
	}

	public void setVIPInfo(String vip)
	{
		if (saleform.bkimg != null)
		{
			if (vip.length() > 0)
			{
				vipinfo.setForeground(SWTResourceManager.getColor(255, 0, 128));
			}
			else
			{
				vipinfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			}
		}
		else
		{
			if (vip.length() > 0)
			{
				vipinfo.setForeground(SWTResourceManager.getColor(255, 0, 128));
				vipinfo.setBackground(SWTResourceManager.getColor(255, 255, 0));
			}
			else
			{
				vipinfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				vipinfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
		}

		this.vipinfo.setText(vip);

	}

	public void setTotalInfo()
	{
		setTotalInfo(saleBS.getSellPayMoneyLabel(), saleBS.getTotalPayMoneyLabel(), saleBS.getTotalMoneyLabel(), saleBS.getTotalQuantityLabel(), saleBS.getTotalRebateLabel());
	}

	public void setTotalInfo(String yfje, String ysje, String hjzje, String hjzsl, String hjzke)
	{
		if (saleBS.saleGoods != null && saleBS.saleGoods.size() > 0)
		{
			this.yfje.setText(yfje);
		}
		else
		{
			String num = saleBS.getUnUploadPayInHead();

			if ("0".equals(num))
			{
				this.yfje.setText(yfje);
			}
			else
			{
				this.yfje.setText(Language.apply("未上传缴款：") + num);
			}

		}

		this.hjzje.setText(hjzje);
		this.hjzsl.setText(hjzsl);
		this.hjzke.setText(hjzke);
	}

	public void setSYJInfo()
	{
		this.fphm.setText(saleBS.getSyjFphmInfoLabel());

		if (GlobalInfo.sysPara.isshowname == 'Y' && !GlobalInfo.posLogin.name.equals(""))
			this.syyh.setText(GlobalInfo.posLogin.name);
		else if (GlobalInfo.sysPara.isshowname == 'A' && !GlobalInfo.posLogin.name.equals(""))
			this.syyh.setText(saleBS.getSyyInfoLabel()+GlobalInfo.posLogin.name);
		else 
			this.syyh.setText(saleBS.getSyyInfoLabel());
	}

	public void setTypeInfo()
	{
		fmType.setText(saleBS.getDjlbLabel());

		if (saleform.bkimg != null)
		{
			if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
			{
				fmType.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

				changeBackGroundImage(true);
			}
			else
			{
				fmType.setForeground(SWTResourceManager.getColor(255, 0, 128));

				changeBackGroundImage(false);
			}
		}
		else
		{
			if (SellType.getDefault().COMMONBUSINESS(saleBS.saleHead.djlb, saleBS.hhflag, saleBS.saleHead))
			{
				fmType.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
				fmType.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
			else
			{
				fmType.setForeground(SWTResourceManager.getColor(255, 0, 128));
				fmType.setBackground(SWTResourceManager.getColor(255, 255, 0));
			}
		}
		this.lbl_yfje.setText(saleBS.getYfjeLabel());
	}

	protected void changeBackGroundImage(boolean salenormal)
	{
		String file = null;

		// 先获取BK文件名
		if (salenormal)
			file = ConfigClass.getBackgroundImageFile(saleform, "norm_" + saleBS.saleHead.djlb);
		else
			file = ConfigClass.getBackgroundImageFile(saleform, "warn_" + saleBS.saleHead.djlb);

		// 比较文件名和上次一致则不改变
		if (file.equalsIgnoreCase(lastsalebk))
			return;
		lastsalebk = file;

		// 先释放再加载
		ConfigClass.disposeBackgroundImage(saleform.bkimg);
		saleform.bkimg = ConfigClass.changeBackgroundImage(saleform, lastsalebk);
	}

	public void setGroupInputArea(String info)
	{
		group_5.setText(info);
	}

	public void setGroupGoodsList(String info)
	{
		group_4.setText(info);
	}

	public void findJfExchangeGoods()
	{
		if (table.getItemCount() > 0)
			saleBS.findJfExchangeGoods(table.getSelectionIndex());
	}

	// 换货键按下的操作
	private void exchangeSale()
	{
		saleBS.exchangeSale();
	}

	// 按更改商品名称键
	public void changeBillName()
	{
		try
		{
			if (saleBS.saleGoods == null || saleBS.saleGoods.size() < 1) { return; }

			int index = table.getSelectionIndex();

			if (index >= 0)
			{
				saleBS.changeBillName(index);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("更改商品发票名称时发生异常\n\n") + ex.getMessage());
		}
	}

}
