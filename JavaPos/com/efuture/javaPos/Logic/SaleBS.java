package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TestThread;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS.FindGoodsItem;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.ManaFrameDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.Struct.ShopPreSaleDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.CheckGoodsForm;

import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;
import com.efuture.javaPos.UI.Design.SaleForm;

// 业务类
public class SaleBS extends SaleBS5Pay
{
	protected OperUserDef thgrantuser = null;

	public HashMap clist = null;
	public Vector tab = null;

	public boolean newrowInfo = false;

	public SaleBS()
	{
		super();
	}

	public void inputMemoInfo(int index)
	{
		if (saleGoods == null || this.saleGoods.size() == 0)
			return;

		StringBuffer buffer = new StringBuffer();

		NewKeyListener.curInputMode = 4;
		if (new TextBox().open(Language.apply("请输入第{0}行商品的附加信息", new Object[] { index + 1 + "" }), Language.apply("商品备注"), Language.apply("提示:请输入商品备注信息"), buffer, -1, -1))
		{
			String memo = buffer.toString().trim();
			if (memo != null && memo.length() > 0)
			{
				SaleGoodsDef goods = (SaleGoodsDef) saleGoods.get(index);
				goods.str1 = memo;
				this.saleEvent.table.modifyRow(rowInfo(goods), index);
				writeBrokenData();
			}
		}

		NewKeyListener.curInputMode = -1;
	}

	public String[] convertColumnValue(String[] srcValue, int index)
	{
		try
		{
			if (srcValue == null || srcValue.length == 0)
				return srcValue;

			if (tab == null || tab.size() == 0)
				return srcValue;

			SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(index);

			if (goodsDef != null)
			{
				srcValue[2] = goodsDef.name;

				if (SellType.ISCHECKINPUT(saletype))
				{
					srcValue[6] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
					srcValue[7] = ManipulatePrecision.doubleToString(goodsDef.hjje, 2, 1);
				}
				else
				{
					srcValue[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
					srcValue[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);
				}
			}
			return srcValue;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return srcValue;
		}
	}

	public void refreshSaleForm()
	{
		// 刷新商品列表
		// saleEvent.updateTable(getSaleGoodsDisplay());
		// 当前方法已经刷新商品列表并且,发送客显,不必再外面再次进刷新商品列表,否则就会向客显放送次指令

		getSaleGoodsDisplay();

		// 总是显示最后一行
		saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);
		saleEvent.table.showSelection();

		// 显示合计
		saleEvent.setTotalInfo();

		// 显示商品大字信息
		saleEvent.setCurGoodsBigInfo();

		saleEvent.code.setText("");
	}

	// 设置销售界面
	public void setUI(SaleForm saleform)
	{
		clist = saleform.clist;
		setSaleForm();
	}

	public void afterInitPay()
	{

	}

	// 设置盘点界面
	public boolean setCheckGUI()
	{
		return false;
	}

	// 此功能修改界面注释 add by lwj
	public void setSaleForm()
	{
		// Label lbl_barcode = (Label) clist.get("lbl_barcode");
		// lbl_barcode.setText("aaaaaaaaaaa");
	}

	public boolean allowEditGoods()
	{
		// 前台售卡时不允许修改商品
		if (saletype.equals(SellType.CARD_SALE))
		{
			new MessageBox(Language.apply("售卡不允许修改交易信息"));
			return false;
		}

		// 团购时不允许修改商品
		if (saletype.equals(SellType.GROUPBUY_SALE) && !Groupbuy_Change())
		{
			new MessageBox(Language.apply("团购不允许修改交易信息"));
			return false;
		}

		// 会员卡必须在商品输完后刷,那么刷卡以后不能修改商品
		if (GlobalInfo.sysPara.customvsgoods == 'B' && checkMemberSale())
		{
			new MessageBox(Language.apply("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入"));
			return false;
		}

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许修改商品状态"));
			return false;
		}

		if ((SellType.PREPARE_BACK.equals(this.saletype)))
		{
			new MessageBox(Language.apply("预售退货状态下不允许修改商品状态"));
			return false;
		}

		// 已经积分换购了的商品不允许进行修改,只能删除
		if (saleEvent.table.getSelectionIndex() >= 0 && goodsSpare.size() > saleEvent.table.getSelectionIndex())
		{
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(saleEvent.table.getSelectionIndex());
			if (info != null && info.char2 == 'Y')
			{
				new MessageBox(Language.apply("当前商品是已进行积分换购,不允许修改\n\n请删除后重新输入"));
				return false;
			}
		}

		return true;
	}

	public boolean checkAllowInit()
	{
		// 有商品输入,不能退出
		if (this.saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("请先完成当前这笔交易!"), null, false);

			return false;
		}

		return true;
	}

	public String getLastSjfk()
	{
		return ManipulatePrecision.doubleToString(lastsaleHead.sjfk);
	}

	public boolean checkAllowExit()
	{
		if (!checkAllowInit()) { return false; }

		if (checkHaveHangInfo()) { return false; }

		return true;
	}

	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] rowinfos = super.rowInfo(goodsDef);
		if (!newrowInfo)
			return rowinfos;

		int rowindex = saleGoods.indexOf(goodsDef);
		rowinfos[0] = String.valueOf(rowindex + 1);
		int index = 0;
		boolean default1 = true;

		// 检查有没有匹配的类型
		String[] info = new String[saleEvent.table.getColumnCount()];

		for (int i = 0; i < Math.min(rowinfos.length, info.length); i++)
		{
			info[i] = rowinfos[i];
		}

		for (int i = 0; i < tab.size(); i++)
		{
			boolean fit = false;
			String[] lines = (String[]) tab.elementAt(i);
			if (lines[2].split(",").length <= 1)
			{
				if ((SellType.ISCHECKINPUT(this.saletype) && lines[2].equals("CHECK_INPUT")))
					fit = true;
				else if (lines[2].equals(this.saletype))
					fit = true;
			}
			else
			{
				String[] types = lines[2].split(",");
				for (int j = 0; j < types.length; j++)
				{
					if ((SellType.ISCHECKINPUT(this.saletype) && types[j].equals("CHECK_INPUT")))
					{
						fit = true;
						break;
					}
					else if (types[j].equals(this.saletype))
					{
						fit = true;
						break;
					}
				}
			}

			if (fit)
			{
				if (lines[1].split(",").length > 1)
				{
					default1 = false;

					if (lines[1].split(",")[1].equals("cjdj"))
					{
						// 成交单价
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), goodsDef.sl), 2, 1);
					}
					else if (lines[1].split(",")[1].equals("ysje"))
					{
						// 应收金额
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
					}
					else
					{
						info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
					}

				}
				index++;
			}
		}

		if (default1)
		{
			// 首先填入默认值
			index = 0;
			for (int i = 0; i < tab.size(); i++)
			{
				String[] lines = (String[]) tab.elementAt(i);
				if (lines[2].equals("Default"))
				{
					if (lines[1].split(",").length > 1)
					{
						if (lines[1].split(",")[1].equals("cjdj"))
						{
							// 成交单价
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), goodsDef.sl), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("ysje"))
						{
							// 应收金额
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("zkje"))
						{
							// 折扣金额，百分比显示折扣
							info[index] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
						}
						else if (lines[1].split(",")[1].equals("memo"))
						{
							info[index] = (goodsDef.str1 == null ? "" : goodsDef.str1.trim());
						}
						else
						{
							info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
						}

					}

					index++;
				}
			}
		}

		return info;
	}

	public void loadPosTableConfig()
	{
		if (GlobalInfo.sysPara.iscfgtable != 'Y')
			return;

		String cfgfile = GlobalVar.ConfigPath + "\\Sale_POSTable.ini";
		File file = new File(cfgfile);
		if (!file.exists())
			return;

		// 读取配置文件
		Vector v = CommonMethod.readFileByVector(cfgfile);

		if (v == null)
			return;

		Vector tab = new Vector();
		try
		{
			String key = null;
			for (int i = 0; i < v.size(); i++)
			{
				String[] lines = (String[]) v.elementAt(i);

				String line = lines[0];

				if (line == null || line.charAt(0) == ';')
					continue;

				if (line.trim().charAt(0) == '[' && line.trim().charAt(line.trim().length() - 1) == ']')
				{
					key = line.substring(1, line.length() - 1);
					continue;
				}

				if (line != null && lines[1] != null)
				{
					lines[2] = key;
					tab.add(lines);
				}
			}

			if (tab != null)
				this.tab = tab;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void initTable(String type)
	{
		if (tab != null)
		{
			// int index = 0;
			boolean default1 = true;

			// 先删除所有表头否则当自定义列比原列少时会多出几列
			while (saleEvent.table.getColumnCount() > 0)
				saleEvent.table.getColumns()[0].dispose();

			// 检查有没有匹配的类型
			for (int i = 0; i < tab.size(); i++)
			{
				boolean fit = false;
				String[] lines = (String[]) tab.elementAt(i);
				if (lines[2].split(",").length <= 1)
				{
					if ((SellType.ISCHECKINPUT(type) && lines[2].equals("CHECK_INPUT")))
						fit = true;
					else if (lines[2].equals(type))
						fit = true;
				}
				else
				{
					String[] types = lines[2].split(",");
					for (int j = 0; j < types.length; j++)
					{
						if ((SellType.ISCHECKINPUT(type) && types[j].equals("CHECK_INPUT")))
						{
							fit = true;
							break;
						}
						else if (types[j].equals(type))
						{
							fit = true;
							break;
						}
					}
				}

				if (lines[1].split(",").length > 1)
					newrowInfo = true;

				if (fit)
				{
					default1 = false;
					/*
					 * if (index < saleEvent.table.getColumnCount()) {
					 * TableColumn col = saleEvent.table.getColumn(index);
					 * col.setText(lines[0]);
					 * 
					 * String[] item = lines[1].split(",")[0].split(":"); if
					 * (item.length>0) { if (Integer.parseInt(item[0].trim())<0)
					 * col.setAlignment(SWT.LEFT); else
					 * if(Integer.parseInt(item[0].trim())==0)
					 * col.setAlignment(SWT.CENTER); else
					 * col.setAlignment(SWT.RIGHT); } if (item.length>1)
					 * col.setWidth(Integer.parseInt(item[1].trim())); else
					 * col.setWidth(150);
					 * //col.setWidth(Integer.parseInt(lines[1].split(",")[0]));
					 * } else {
					 */
					final TableColumn col = new TableColumn(saleEvent.table, SWT.NONE);
					col.setText(lines[0]);

					String[] item = lines[1].split(",")[0].split(":");
					if (item.length > 0)
					{
						if (Integer.parseInt(item[0].trim()) < 0)
							col.setAlignment(SWT.LEFT);
						else if (Integer.parseInt(item[0].trim()) == 0)
							col.setAlignment(SWT.CENTER);
						else
							col.setAlignment(SWT.RIGHT);
					}
					if (item.length > 1)
						col.setWidth(Integer.parseInt(item[1].trim()));
					else
						col.setWidth(150);
					// col.setWidth(Integer.parseInt(lines[1].split(",")[0]));
				}
				// index++;
				// }
			}

			if (default1)
			{
				// 首先填入默认值
				// index = 0;

				for (int i = 0; i < tab.size(); i++)
				{
					String[] lines = (String[]) tab.elementAt(i);

					if (lines[1].split(",").length > 1)
						newrowInfo = true;

					if (lines[2].equals("Default"))
					{
						/*
						 * if (index < saleEvent.table.getColumnCount()) {
						 * TableColumn col = saleEvent.table.getColumn(index);
						 * col.setText(lines[0]);
						 * 
						 * String[] item = lines[1].split(",")[0].split(":"); if
						 * (item.length>0) { if
						 * (Integer.parseInt(item[0].trim())<0)
						 * col.setAlignment(SWT.LEFT); else
						 * if(Integer.parseInt(item[0].trim())==0)
						 * col.setAlignment(SWT.CENTER); else
						 * col.setAlignment(SWT.RIGHT); } if (item.length>1)
						 * col.setWidth(Integer.parseInt(item[1].trim())); else
						 * col.setWidth(150); //
						 * col.setWidth(Integer.parseInt(lines
						 * [1].split(",")[0])); } else {
						 */
						final TableColumn col = new TableColumn(saleEvent.table, SWT.NONE);
						col.setText(lines[0]);
						String[] item = lines[1].split(",")[0].split(":");
						if (item.length > 0)
						{
							if (Integer.parseInt(item[0].trim()) < 0)
								col.setAlignment(SWT.LEFT);
							else if (Integer.parseInt(item[0].trim()) == 0)
								col.setAlignment(SWT.CENTER);
							else
								col.setAlignment(SWT.RIGHT);
						}
						if (item.length > 1)
							col.setWidth(Integer.parseInt(item[1].trim()));
						else
							col.setWidth(150);
						// col.setWidth(Integer.parseInt(lines[1].split(",")[0]));
					}
					// index++;
				}
				// saleEvent.table.initialize();
			}
			// }
		}
		else
		{
			if (SellType.ISCHECKINPUT(type))
			{
				// 修改列表头
				saleEvent.table.getColumn(0).setWidth(50);
				saleEvent.table.getColumn(4).setText(Language.apply("帐存"));
				saleEvent.table.getColumn(5).setText(Language.apply("单价"));
				saleEvent.table.getColumn(5).setWidth(90);
				saleEvent.table.getColumn(6).setText(Language.apply("实盘数量"));
				saleEvent.table.getColumn(6).setWidth(100);
				saleEvent.table.getColumn(7).setText(Language.apply("实盘金额"));
			}
			else
			{
				if (saleEvent.table.getColumn(7).getText().equals(Language.apply("实盘金额")))
				{
					// 还原列表头
					saleEvent.table.getColumn(0).setWidth(30);
					saleEvent.table.getColumn(4).setText(Language.apply("数量"));
					saleEvent.table.getColumn(5).setText(Language.apply("单价"));
					saleEvent.table.getColumn(5).setWidth(100);
					saleEvent.table.getColumn(6).setText(Language.apply("折扣"));
					saleEvent.table.getColumn(6).setWidth(112);
					saleEvent.table.getColumn(7).setText(Language.apply("应收金额"));
					saleEvent.table.getColumn(7).setWidth(110);
				}
			}
		}
	}

	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
		if (SellType.ISCHECKINPUT(type))
		{
			saleEvent.yyyh.setText(Language.apply("盘点"));
			saleEvent.gz.setText("");
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else
		{
			if (SellType.ISCOUPON(type))
			{
				saleEvent.yyyh.setText(Language.apply("买券"));
				saleEvent.gz.setText(Language.apply("买券柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (SellType.isJS(type))
			{
				saleEvent.yyyh.setText(Language.apply("结算"));
				saleEvent.gz.setText(Language.apply("结算柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (SellType.isJF(type))
			{
				saleEvent.yyyh.setText(Language.apply("缴费"));
				saleEvent.gz.setText(Language.apply("缴费柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (GlobalInfo.syjDef.issryyy == 'N')
			{
				saleEvent.yyyh.setText(Language.apply("超市"));
				saleEvent.gz.setText(Language.apply("超市柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (GlobalInfo.syjDef.issryyy == 'B')
			{
				saleEvent.yyyh.setText(Language.apply("百货"));
				saleEvent.gz.setText(Language.apply("任意柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}

			else if (GlobalInfo.syjDef.issryyy == 'C') // 专柜模式，默认为收银员
			{
				saleEvent.yyyh.setText(GlobalInfo.posLogin.gh);
				saleEvent.gz.setText("");
				saleEvent.saleform.setFocus(saleEvent.yyyh);
			}
			else if (SellType.ISJFSALE(type))
			{
				saleEvent.yyyh.setText(Language.apply("买积分"));
				saleEvent.gz.setText(Language.apply("买积分柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else
			{
				if (iscsinput)
				{
					saleEvent.yyyh.setText(Language.apply("超市"));
					saleEvent.gz.setText(Language.apply("超市柜"));
					saleEvent.saleform.setFocus(saleEvent.code);
				}
				else
				{
					saleEvent.saleform.setFocus(saleEvent.yyyh);
				}
			}
		}
	}

	// 设置界面信息
	public void setInfoGUI()
	{

	}

	// 二次授权
	public boolean twoBackPersonGrant()
	{
		boolean ok = false;
		if (GlobalInfo.sysPara.isthgh != null && !GlobalInfo.sysPara.isthgh.trim().equals(""))
		{
			String s[] = GlobalInfo.sysPara.isthgh.split(",");
			for (int i = 0; i < s.length; i++)
			{
				if (s[i].trim().equals(GlobalInfo.posLogin.gh))
				{
					ok = true;
					break;
				}
			}

			if (!ok)
			{
				OperUserDef staff = DataService.getDefault().personGrant(Language.apply("二次退货授权"));
				if (staff == null)
				{
					// 退回对应的销售类型
					djlbBackToSale();

					// 初始化交易
					initOneSale(this.saletype);
				}
				else
				{
					for (int i = 0; i < s.length; i++)
					{
						if (s[i].trim().equals(staff.gh))
						{
							ok = true;
							break;
						}
					}

					if (!ok)
					{
						new MessageBox(Language.apply("该收银员无二次退货授权权限,不能退货"));
						// 退回对应的销售类型
						djlbBackToSale();

						// 初始化交易
						initOneSale(this.saletype);
					}

					// String log = Language.apply("授权二次退货, 小票号{0}授权工号:",new
					// Object[]{saleHead.fphm + " "}) + staff.gh;
					String log = "授权二次退货, 小票号" + saleHead.fphm + " 授权工号:" + staff.gh;
					AccessDayDB.getDefault().writeWorkLog(log);
				}
			}
		}
		return ok;
	}

	protected boolean initGui(String type)
	{
		// 显示客显
		DisplayMode.getDefault().lineDisplayWelcome();

		// 双屏显示
		this.sendSecMonitor("welcome");

		// 设置单据类别
		this.saletype = type;

		// 收银员只有退货权且收银允许退货,切换到对应的退货单据类型
		if ((GlobalInfo.posLogin.privth == 'T') && SellType.ISSALE(this.saletype) && (GlobalInfo.syjDef.isth == 'Y'))
		{
			djlbSaleToBack();
		}

		// 初始化数据
		initNewSale();

		// 上笔交易如果是超市销售,在百货超市皆可模式下，本笔交易缺省按上笔也采用超市销售模式
		boolean iscsinput = false;
		if (saleEvent.yyyh.getText().trim().equals(Language.apply("超市")))
			iscsinput = true;

		// 清空GUI显示
		saleEvent.initGUI();

		setInfoGUI();

		// 显示自定义TABLE列
		initTable(type);

		// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
		initSetYYYGZ(type, iscsinput);

		// 读取断点数据
		boolean brokenflag = false;
		if (checkBrokenData() != null)
		{
			brokenflag = readBrokenData();

			// 没有断点清除数据并刷新界面
			if (!brokenflag)
			{
				initNewSale();
				saleEvent.initGUI();
				initSetYYYGZ(type, iscsinput);
			}
		}
		if (brokenflag)
			return false;

		// 检查一次商品列表与界面列表个数是否一致，避免内存对象已清除但界面未刷新的问题
		if (saleGoods.size() != saleEvent.table.getItemCount())
		{
			initNewSale();
			saleEvent.initGUI();
			initSetYYYGZ(type, iscsinput);
		}
		return true;
	}

	protected void initBackSell()
	{
		// 如果是要输入家电发货地点,并且是新指定小票退货则输入家电发货地点
		if (GlobalInfo.sysPara.isinputjdfhdd != 'N' && !isNewUseSpecifyTicketBack(false))
		{
			if (!SellType.ISCHECKINPUT(this.saletype))
			{
				if (GlobalInfo.sysPara.isinputjdfhdd == 'S' && jdfhddcode.trim().length() > 0)
				{
					if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.syjDef.issryyy == 'B')
					{
						saleHead.jdfhdd = jdfhddcode;

						saleEvent.yyyh.setText(Language.apply("家电"));
						saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
					}
				}
				else
				{
					inputJdfhdd();
				}
			}
		}

		// 退货交易初始化以后，恢复授权信息
		if (SellType.ISBACK(this.saletype) && thgrantuser != null)
		{
			saleHead.thsq = thgrantuser.gh;
			curGrant.privth = thgrantuser.privth;
			curGrant.thxe = thgrantuser.thxe;

			thgrantuser = null;
		}

		// 判断退货是否输入收银机号
		if (SellType.isJS(this.saletype))
		{
		}
		else if (isSpecifyTicketBack())
		{
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);
			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);
				if (SellType.PREPARE_BACK.equals(this.saletype))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();

					if (!isbackticket)
					{
						if (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C')
						{
							// 退回对应的销售类型
							djlbBackToSale();

							// 初始化交易
							initOneSale(this.saletype);
						}
						else if (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y')
						{
							twoBackPersonGrant();
						}
					}
				}
			}
			else if ((done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y'))
			{
				// 二次授权
				twoBackPersonGrant();

			}
			else if ((done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'C' || GlobalInfo.sysPara.inputydoc == 'B'))
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
			else if (SellType.isJF(saletype))
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
	}

	protected void initBusiness()
	{
		if (SellType.ISPREPARETAKE(this.saletype))
		{
			boolean done = false;
			while (true)
			{
				RetSYJForm frm = new RetSYJForm();

				if (frm.open(thSyjh, thFphm, Language.apply("请输入预售小票的收银机号和小票号")) == frm.Done)
				{
					thSyjh = RetSYJForm.syj;
					thFphm = Long.parseLong(RetSYJForm.fph);

					done = findPreSaleInfo();

					// 如果没有查询到预销售信息，重新输入原收银机号和小票号
					if (!done)
						continue;

					break;
				}
				else
				{
					done = false;
					break;
				}
			}

			// 如果没有输入原收银机号和小票号，返回到零售销售状态
			if (!done)
			{
				initOneSale(SellType.RETAIL_SALE);
				return;
			}
		}

		// 盘点
		if (SellType.ISCHECKINPUT(this.saletype))
		{
			StringBuffer buffer = new StringBuffer();
			do
			{
				checkdjbh = null;
				String lentext = Language.apply("任意长度的");
				String grouplentext = "";

				if (GlobalInfo.sysPara.checklength > 0)
					lentext = Language.apply("长度{0}位的", new Object[] { GlobalInfo.sysPara.checklength + "" });

				if (GlobalInfo.sysPara.checkgrouplen > 0)
					grouplentext = Language.apply("与长度{0}位的盘点组号", new Object[] { GlobalInfo.sysPara.checkgrouplen + "" });

				if (new TextBox().open(Language.apply("请输入{0}盘点单号", new Object[] { lentext + "" }) + grouplentext, Language.apply("单号"), Language.apply("请输入要盘点的空白盘点单号,格式为\n\n盘点单号+盘点组号"), buffer, 0, 0, false))
				{
					checkdjbh = buffer.toString().trim();

					if (checkdjbh.length() <= 0)
						continue;

					if (GlobalInfo.sysPara.checklength > 0 && checkdjbh.length() < GlobalInfo.sysPara.checklength)
					{
						// new MessageBox("必须输入" +
						// GlobalInfo.sysPara.checklength + "位长度的单号!");
						continue;
					}

					if (GlobalInfo.sysPara.checkgrouplen > 0 && checkdjbh.length() != (GlobalInfo.sysPara.checklength + GlobalInfo.sysPara.checkgrouplen))
					{
						continue;
					}

					if (GlobalInfo.sysPara.checkgrouplen < 0 && GlobalInfo.sysPara.checklength != checkdjbh.length())
					{
						continue;
					}

					// 联网查询盘点单
					if (checkgoods == null)
						checkgoods = new Vector();
					else
						checkgoods.removeAllElements();

					// 联网盘点
					if (GlobalInfo.sysPara.isblankcheckgoods != 'A' && GlobalInfo.sysPara.isblankcheckgoods != 'B')
					{
						if (!NetService.getDefault().getCheckGoodsList(checkgoods, checkdjbh, "", ""))
							continue;

						// 不允许使用空白盘点单时
						if (checkgoods.size() <= 0 && GlobalInfo.sysPara.isblankcheckgoods == 'N')
						{
							new MessageBox(Language.apply("没有当前盘点单,不允许进行盘点!"));
							continue;
						}

						// 盘点单不存在提示是否生成新的盘点单
						if (checkgoods.size() <= 0 && new MessageBox("[" + checkdjbh + Language.apply("]盘点单不存在,你要生成新的盘点单吗？"), null, true).verify() != GlobalVar.Key1)
						{
							continue;
						}
					}

					// 输入柜组，日期，仓位
					if (GlobalInfo.sysPara.isblankcheckgoods == 'A' || GlobalInfo.sysPara.isblankcheckgoods == 'B')
					{
						CheckGoodsForm checkGoodsForm = new CheckGoodsForm(this);
						checkGoodsForm.open();
						if (checkGoodsForm.isExit == 'Y')
						{
							continue;
						}

						if (GlobalInfo.sysPara.isblankcheckgoods == 'B')
						{
							checkeditflag = "";
							totalcheckrow = -1;
							// 查询是否存在原单
							Vector v = new Vector();
							if (NetService.getDefault().getOldCheckInfo(v, checkdjbh, checkgz, checkrq, checkcw, CmdDef.GETOLDCHECKINFO))
							{
								if (v.size() > 0)
								{
									if (v.size() == 1)
									{
										int retcode = Integer.parseInt(((String[]) v.get(0))[14]);
										String retmsg = ((String[]) v.get(0))[15];
										if (retcode != 0)
										{
											new MessageBox(retmsg);
											continue;
										}
									}
									initSellData();
									SaleGoodsDef sgd = null;
									GoodsDef goodsDef = null;
									for (int i = 0; i < v.size(); i++)
									{
										String rowno = ((String[]) v.get(i))[6];
										String gz = ((String[]) v.get(i))[1];
										String code = ((String[]) v.get(i))[7];
										String name = ((String[]) v.get(i))[8];
										String unit = ((String[]) v.get(i))[9];
										String bzhl = ((String[]) v.get(i))[10];
										String uid = ((String[]) v.get(i))[11];
										String pdsl = ((String[]) v.get(i))[12];
										String pdje = ((String[]) v.get(i))[13];

										sgd = new SaleGoodsDef();
										goodsDef = new GoodsDef();
										sgd.barcode = sgd.code = code;
										sgd.sl = Double.parseDouble(pdsl);
										sgd.hjje = Double.parseDouble(pdje);
										sgd.bzhl = Double.parseDouble(bzhl);
										sgd.uid = uid;
										sgd.gz = gz;
										sgd.name = name;
										sgd.unit = unit;
										sgd.rowno = Integer.parseInt(rowno);
										sgd.jg = ManipulatePrecision.doubleConvert((sgd.hjje / sgd.sl), 2, 1);
										goodsDef.code = code;
										goodsDef.bzhl = Double.parseDouble(bzhl);
										goodsDef.uid = uid;
										goodsDef.gz = gz;
										goodsDef.name = name;
										goodsDef.unit = unit;
										goodsDef.lsj = sgd.jg;
										// 加入商品列表
										addSaleGoodsObject(sgd, goodsDef, new SpareInfoDef());
									}
									String editflag = ((String[]) v.get(0))[16];
									String totalrow = ((String[]) v.get(0))[18];
									checkeditflag = editflag.trim();
									totalcheckrow = Integer.parseInt(totalrow);
									curcheckrow = totalcheckrow;
									// 刷新界面显示
									saleEvent.clearTableItem();
									saleEvent.updateSaleGUI();
								}
							}
							else
							{
								continue;
							}
						}
					}

					CheckGoodsDef chkgd = null;

					for (int i = 0; i < checkgoods.size(); i++)
					{
						chkgd = (CheckGoodsDef) checkgoods.elementAt(i);

						if (chkgd.gz != null && chkgd.gz.trim().length() > 0 && chkgd.gz.charAt(0) == 'Y')
						{
							break;
						}
					}

					if (chkgd != null && chkgd.gz != null && chkgd.gz.trim().length() > 0 && chkgd.gz.charAt(0) == 'Y')
					{
						String checkgroup = null;

						if (GlobalInfo.sysPara.checkgrouplen > 0)
						{
							checkgroup = checkdjbh.substring(0, GlobalInfo.sysPara.checklength) + "-" + checkdjbh.substring(GlobalInfo.sysPara.checklength, (GlobalInfo.sysPara.checklength + GlobalInfo.sysPara.checkgrouplen));
						}
						else
						{
							checkgroup = checkdjbh.substring(0, GlobalInfo.sysPara.checklength);
						}

						if (new MessageBox("[" + checkgroup + Language.apply("]\n盘点单组已盘点,是否重盘该盘点组号?"), null, true).verify() != GlobalVar.Key1)
						{
							continue;
						}
					}

					if (setCheckGUI())
						break;

					// 可以开始输入盘点商品
					if (this.checkgz.length() > 0)
					{
						saleEvent.gz.setText(this.checkgz);
					}
					else if (GlobalInfo.sysPara.checklength > 0)
					{
						saleEvent.gz.setText(checkdjbh.substring(0, GlobalInfo.sysPara.checklength) + (checkdjbh.length() > GlobalInfo.sysPara.checklength ? "-" + checkdjbh.substring(GlobalInfo.sysPara.checklength) : ""));
					}
					else
					{
						saleEvent.gz.setText(checkdjbh);
					}
					break;
				}
				else
				{
					new MessageBox(Language.apply("没有输入盘点单号将不能进行盘点输入"));
					saleEvent.gz.setText(Language.apply("无盘点单号"));
					break;
				}
			} while (true);

			// 将盘点单号赋给小票号
			saleHead.yfphm = checkdjbh;
		}

		// 家电下乡返款
		if (SellType.JDXX_FK.equals(saletype))
		{
			if (GlobalInfo.sysPara.jdxxfkflag == 'Y')
			{
				boolean done = false;

				RetSYJForm frm = new RetSYJForm();

				if (frm.open(thSyjh, thFphm, Language.apply("请输入是家电小票的收银机号和小票号")) == frm.Done)
				{
					thSyjh = RetSYJForm.syj;
					thFphm = Long.parseLong(RetSYJForm.fph);
					done = true;
				}
				else
				{
					done = false;
				}

				// 如果没有输入原收银机号和小票号，返回到零售销售状态
				if (!done)
				{
					initOneSale(SellType.RETAIL_SALE);
					return;
				}
			}
		}

		// 退家电下乡返款
		if (SellType.JDXX_BACK.equals(saletype))
		{
			boolean done = false;
			while (true)
			{
				RetSYJForm frm = new RetSYJForm();

				if (frm.open(thSyjh, thFphm, Language.apply("请输入原家电返款小票的收银机号和小票号")) == frm.Done)
				{
					thSyjh = RetSYJForm.syj;
					thFphm = Long.parseLong(RetSYJForm.fph);

					done = findBackTicketInfo();

					// 如果没有查询到预销售信息，重新输入原收银机号和小票号
					if (!done)
						continue;

					break;
				}
				else
				{
					done = false;
					break;
				}
			}

			// 如果没有输入原收银机号和小票号，返回到零售销售状态
			if (!done)
			{
				initOneSale(SellType.RETAIL_SALE);
				return;
			}
		}

		// 换货
		if (SellType.ISHH(saletype))
		{
			boolean done = false;
			while (true)
			{
				RetSYJForm frm = new RetSYJForm();

				if (frm.open(thSyjh, thFphm, Language.apply("请输入原小票的收银机号和小票号")) == frm.Done)
				{
					thSyjh = RetSYJForm.syj;
					thFphm = Long.parseLong(RetSYJForm.fph);

					done = findBackTicketInfo();

					// 如果没有查询到预销售信息，重新输入原收银机号和小票号
					if (!done)
						break;//continue;

					break;
				}
				else
				{
					done = false;
					break;
				}
			}

			// 如果没有输入原收银机号和小票号，返回到零售销售状态
			if (!done)
			{
				initOneSale(SellType.RETAIL_SALE);
				return;
			}
		}
		
		// 团购
		if (SellType.GROUPBUY_SALE.equals(saletype) && !Groupbuy_Change())
		{
			boolean done = false;
			StringBuffer buffer = new StringBuffer();
			String billNo = "";
			while (true)
			{
				if (new TextBox().open(Language.apply("请输入团购单号:"), "", Language.apply("后台已生成的团购单号"), buffer, -1, -1, false, TextBox.IntegerInput))
				{
					billNo = buffer.toString();
					done = findGroupBuyInfo(billNo);
					if (!done)
						continue;
					break;
				}
				else
				{
					done = false;
					break;
				}
			}

		}
	}

	protected void initOperation()
	{
		//更新一次收银机状态
		querySyjStatus();
		// 销售交易主动提示刷会员卡
		if (GlobalInfo.sysPara.customerbysale == 'Y' && saletype.equals(SellType.RETAIL_SALE))
		{
			NewKeyListener.sendKey(GlobalVar.MemberGrant);
		}
	}

	// 初始化一笔新交易
	public void initOneSale(String type)
	{
		if (!initGui(type))
			return;

		initBackSell();
		initBusiness();
		initOperation();
	}

	public void yyyInput(int index)
	{
		if ((GlobalInfo.syjDef.issryyy != 'N' && GlobalInfo.syjDef.issryyy != 'B') && (!saleEvent.yyyh.getText().equals("超市") && !saleEvent.yyyh.getText().equals("百货") || (saleGoods.size() <= 0)))
		{
			saleEvent.saleform.setFocus(saleEvent.yyyh);
		}
	}

	// 输入家电发货地点
	public void inputJdfhdd()
	{
		if (this.saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("已输入商品,不允许输入发货地点!"));
			return;
		}
		Vector contents = null;

		if (GlobalInfo.sysPara.jdfhdd != null && GlobalInfo.sysPara.jdfhdd.trim().length() > 0)
		{
			String[] strs = GlobalInfo.sysPara.jdfhdd.trim().split(";");

			contents = new Vector();

			for (int i = 0; i < strs.length; i++)
			{
				String[] strs1 = strs[i].split(",");
				if (strs1.length < 2)
					continue;
				contents.add(strs1);
			}
		}
		else
		{
			contents = CommonMethod.readFileByVector(GlobalVar.ConfigPath + "/Jdfhdd.ini");
		}

		if (contents != null && contents.size() > 0)
		{
			String[] title = { Language.apply("代码"), Language.apply("发货地点") };
			int[] width = { 150, 350 };
			int choice = -1;
			do
			{
				choice = new MutiSelectForm().open(Language.apply("请选择本笔交易的发货地点"), title, width, contents, true);
			} while (choice < 0);

			jdfhddcode = ((String[]) contents.elementAt(choice))[0];
			jdfhddname = ((String[]) contents.elementAt(choice))[1];

			saleHead.jdfhdd = jdfhddcode;
			if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.syjDef.issryyy == 'B')
			{
				saleEvent.yyyh.setText(Language.apply("家电"));
				saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
			}
		}
	}

	public void enterInput()
	{
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
		{
			saleEvent.saleform.getFocus().selectAll();
			return;
		}

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许修改商品状态"));
			return;
		}

		// 扫瞄后回车前刷新界面显示
		Display.getCurrent().update();

		// 营业员输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.yyyh))
		{
			enterInputYYY();

			return;
		}

		// 柜组输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.gz))
		{
			enterInputGZ();

			return;
		}

		// 条码输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.code))
		{
			enterInputCODE();

			return;
		}
	}

	public boolean yyhExtendAction(OperUserDef staff)
	{
		if (GlobalInfo.sysPara.inputyyyfph == 'Y')
		{
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入发票号"), Language.apply("发票号"), Language.apply("请根据营业员的单据输入发票号码"), req, 0, 0, false, TextBox.IntegerInput);
			if (done)
			{
				curyyyfph = req.toString();
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	public String getGZDisplay(OperUserDef staff)
	{
		String gzName = staff.yyygz;
		ResultSet rs;
		if ((rs = GlobalInfo.localDB.selectData("select * from MANAFRAME where gz = '" + staff.yyygz + "'")) != null)
		{
			try
			{
				if (rs.next())
				{
					ManaFrameDef manaFrameDef = new ManaFrameDef();
					if (GlobalInfo.localDB.getResultSetToObject(manaFrameDef))
					{
						if (manaFrameDef.name.length() > 0)
						{
							gzName = gzName + "[" + manaFrameDef.name + "]";
						}
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return gzName;
	}

	public void enterInputYYY()
	{
		// 不输入营业员，认为超市销售
		if ((saleEvent.yyyh.getText().length() <= 0 || saleEvent.yyyh.getText().equals(Language.apply("超市"))) && (GlobalInfo.syjDef.issryyy == 'A') && saleTypeControl())
		{
			saleEvent.yyyh.setText(Language.apply("超市"));
			saleEvent.gz.setText(Language.apply("超市柜"));
			saleEvent.saleform.setFocus(saleEvent.code);

			// 输入超市柜的时候也需要输入发票单
			csExtendAction();

			// 家电销售
			if (jdfhddcode != null && jdfhddcode.length() > 0)
			{
				saleEvent.yyyh.setText(Language.apply("家电"));
				saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
			}

			return;
		}

		//
		if (saleEvent.yyyh.getText().length() <= 0)
		{
			new MessageBox(Language.apply("营业员不能为空，请重新输入!"), null, false);
			saleEvent.yyyh.selectAll();

			return;
		}

		// 查找营业员
		OperUserDef staff = null;

		if ((staff = findYYYH(saleEvent.yyyh.getText())) != null)
		{
			if (staff.type != '2')
			{
				new MessageBox(Language.apply("该工号不是营业员!"), null, false);
				saleEvent.yyyh.selectAll();

				return;
			}

			// 检查工号过期
			String expireDate = staff.maxdate + " 0:0:0";
			ManipulateDateTime mdt = new ManipulateDateTime();

			if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
			{
				new MessageBox(Language.apply("该工号已过期!"), null, false);
				saleEvent.yyyh.selectAll();

				return;
			}

			// 营业员扩展操作
			if (!yyhExtendAction(staff))
				return;

			// 不控制营业员创柜
			curyyygz = staff.yyygz;
			if (GlobalInfo.sysPara.yyygz != 'N')
			{
				// 营业员定义了所属柜组
				if (staff.yyygz.length() > 0)
				{
					String[] s = staff.yyygz.split(",");
					if (s.length <= 1)
					{
						saleEvent.gz.setText(getGZDisplay(staff)); // 只有一个所属柜
					}
					else
					{
						saleEvent.gz.setText(Language.apply("多个柜")); // 有多个所属柜,先按不控制柜方式查找商品
					}
					if (GlobalInfo.sysPara.yyygz == 'A') // A-可修改柜组
					{
						saleEvent.gz.selectAll();
						saleEvent.saleform.setFocus(saleEvent.gz);
					}
					else
					{
						saleEvent.saleform.setFocus(saleEvent.code);
					}
				}
				else
				{
					if (GlobalInfo.sysPara.yyygz == 'A')
					{
						if (getGzCode(saleEvent.gz.getText()).trim().equals(Language.apply("超市柜")))
						{
							saleEvent.gz.setText("");
						}
					}

					saleEvent.saleform.setFocus(saleEvent.gz);
				}
			}
			else
			{
				saleEvent.gz.setText(Language.apply("任意柜"));
				saleEvent.saleform.setFocus(saleEvent.code);
			}

			// 家电销售
			if (jdfhddname != null && jdfhddname.length() > 0)
			{
				saleEvent.gz.setText(jdfhddname);
			}
		}
		else
		{
			saleEvent.yyyh.selectAll();
		}
	}

	public void openEBill()
	{
		EBill.getDefault().getSaleBill(this);
	}

	public void csExtendAction()
	{

	}

	// 通过交易类型判断是否可以是不输入营业员
	public boolean saleTypeControl()
	{

		return true;
	}

	public void enterInputGZ()
	{
		saleEvent.saleform.setFocus(saleEvent.code);
	}

	public void enterInputCODE()
	{

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许修改商品状态"));
			return;
		}

		boolean findok = false;

		if (saleEvent.code.getText().trim().length() > 30)
		{
			new MessageBox(Language.apply("非合法的商品编码不允许进行销售\n当前编码长度") + saleEvent.code.getText().length());
			saleEvent.code.selectAll();
			return;
		}

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			String code = saleEvent.code.getText().trim();

			if (code.length() <= 0 && saleGoods.size() > 0)
			{

				code = ((SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1)).barcode;
			}

			if (code.length() > 0 && findCheckGoods(code, saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.ISCOUPON(saletype))
		{
			// 买券
			if (findCoupon(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJS(saletype))
		{
			// 缴费
			if (findJSDetail(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJF(saletype))
		{
			// 结算
			if (findJFDetail(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.ISJFSALE(saletype))
		{
			// 买积分
			if (findJf(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else
		{
			// 超市或开发模式直接按回车 = 扫描上一个商品
			String code = saleEvent.code.getText().trim();
			// if (code.length() <= 0)
			// {
			// GoodsSearchForm window = new GoodsSearchForm();
			// window.open();
			// }

			if ((GlobalInfo.sysPara.quickinputsku == 'Y' && saleEvent.yyyh.getText().trim().equals("超市") || ConfigClass.isDeveloperMode()) && code.length() <= 0 && saleGoods.size() > 0)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1);
				if (sg.inputbarcode != null && sg.inputbarcode.length() > 0)
					code = sg.inputbarcode;
				else
					code = sg.barcode;
			}

			if (code.length() > 0 && findGoods(code, saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}

		// 清除输入框
		if (findok)
		{
			refreshSaleForm();
			// new MessageBox(saleEvent.code.getText());
			doShowInfoFinish();
		}
		else
		{
			saleEvent.code.selectAll();
		}
	}

	public boolean doShowInfoFinish()
	{
		return true;
	}

	protected boolean isExistHangBill()
	{
		if (GlobalInfo.sysPara.exitsyswhenexistgd == 'N' && checkExistHang())
		{
			new MessageBox(Language.apply("系统存在挂单数据,不允许退出!"));
			return false;
		}

		return true;
	}

	public boolean allowQuickExitSell()
	{
		if (ConfigClass.DebugMode && (NewKeyListener.searchKeyCode(GlobalVar.MainList) > 0))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void exitSell()
	{
		// 检查是否允许退出
		if (!checkAllowExit()) { return; }

		// 如果是退货则回到销售状态
		if (SellType.ISBACK(this.saletype) && (GlobalInfo.posLogin.privth != 'T'))
		{
			if (new MessageBox(Language.apply("你确定从退货切换到销售状态吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else if (SellType.ISBACK(this.saletype) && GlobalInfo.posLogin.privth == 'T')
		{

			if (!isExistHangBill())
				return;

			if (new MessageBox(Language.apply("你确定要退出收银系统吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 关闭销售界面
				saleEvent.saleform.dispose();

				// 退出系统
				AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员注销登录"), StatusType.WORK_RELOGIN);
				GlobalInfo.background.quitSysInfo();
			}
		}
		else if (!SellType.getDefault().COMMONBUSINESS(this.saletype, this.hhflag, this.saleHead) && ManipulateStr.textInString("0101", GlobalInfo.posLogin.funcmenu, ",", false))
		{
			if (new MessageBox(Language.apply("你确定返回到正常销售状态吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				this.saletype = SellType.RETAIL_SALE;
				this.hhflag = 'N';

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else
		{
			// 调试模式且定义了菜单功能键可直接退出系统,否则弹出系统功能菜单
			if (allowQuickExitSell())
			{
				if (!isExistHangBill())
					return;

				if (new MessageBox(Language.apply("你确定要退出收银系统吗?"), null, true).verify() == GlobalVar.Key1)
				{
					// 关闭销售界面
					saleEvent.saleform.dispose();

					// 退出系统
					AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员登出"), StatusType.WORK_RELOGIN);
					GlobalInfo.background.quitSysInfo();
				}
			}
			else
			{
				saleEvent.showFuncMenu();
			}
		}
	}

	public OperUserDef clearSellGrant()
	{
		OperUserDef staff = DataService.getDefault().personGrant();

		if (staff == null) { return null; }

		if (staff.privqx != 'Y')
		{
			new MessageBox(Language.apply("该员工授权卡无法授权取消交易"));

			return null;
		}

		return staff;
	}

	public boolean clearSell(int index)
	{
		// 先取消VIP或临时折扣
		if (cancelMemberOrGoodsRebate(index)) { return true; }

		if (saleGoods.size() <= 0)
		{
			// 退货交易切换回销售交易
			if (SellType.ISBACK(saletype))
			{
				djlbBackToSale();
			}

			//
			initOneSale(this.saletype);

			return true;
		}

		if (new MessageBox(Language.apply("你确定要取消本笔交易输入吗?"), null, true).verify() != GlobalVar.Key1) { return false; }

		// 没有取消权限
		String grantgh;

		if (operPermission(clearPermission, curGrant))
		{
			OperUserDef staff = clearSellGrant();

			if (staff == null) { return false; }

			grantgh = staff.gh;
		}
		else
		{
			grantgh = saleHead.syyh;
		}

		//
		if (!SellType.ISEXERCISE(this.saletype))
		{
			// 记录日志
			String log = "取消交易,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",金额:" + Convert.increaseChar(ManipulatePrecision.doubleToString(saleHead.ysje), '0', 10) + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_CLEARSALE);

			// 记汇总
			SaleSummaryDef saleSummaryDef = new SaleSummaryDef();
			saleSummaryDef.zl = 0;
			saleSummaryDef.sysy = 0;
			saleSummaryDef.sjfk = 0;
			saleSummaryDef.zkje = 0;
			saleSummaryDef.ysje = 0;
			saleSummaryDef.qxbs = 1;
			saleSummaryDef.qxje = saleHead.ysje;

			// 写入全天销售统计
			saleSummaryDef.bc = '0';
			saleSummaryDef.syyh = Language.apply("全天");
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);

			// 写入当班收银员销售统计
			saleSummaryDef.bc = saleHead.bc;
			saleSummaryDef.syyh = saleHead.syyh;
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);
		}

		// 退货交易切换回销售交易
		if (SellType.ISBACK(saletype))
		{
			djlbBackToSale();
		}

		// 初始化新交易
		initOneSale(this.saletype);

		return true;
	}

	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if ((SellType.PREPARE_BACK.equals(this.saletype) || SellType.PREPARE_TAKE.equals(this.saletype)))
			return false;
		if (isNewUseSpecifyTicketBack(false))
			return false;

		// 必须后刷VIP卡模式,有VIP卡则先取消VIP
		if (GlobalInfo.sysPara.customvsgoods == 'B' && checkMemberSale())
		{
			if (new MessageBox(Language.apply("已经刷了VIP卡,你确定要取消VIP卡吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 记录当前顾客卡
				curCustomer = null;

				// 记录到小票
				saleHead.hykh = null;

				// 重算所有商品应收
				for (int i = 0; i < saleGoods.size(); i++)
				{
					calcGoodsYsje(i);
				}

				// 计算小票应收
				calcHeadYsje();
				saleEvent.updateSaleGUI();
			}
			return true;
		}

		// 参数控制清除键来清除临时折扣
		if (GlobalInfo.sysPara.FirstClearLsZk == 'Y')
		{
			// 如果有临时折扣,则取消临时折扣
			if (index >= 0 && saleGoods.size() > index)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr);
				if (sum > 0)
				{
					if (new MessageBox("【" + saleGoodsDef.name + Language.apply("】存在临时折扣\n你确定要取消此商品的临时折扣吗?"), null, true).verify() == GlobalVar.Key1)
					{
						saleGoodsDef.lszke = 0;
						saleGoodsDef.lszre = 0;
						saleGoodsDef.lszzk = 0;
						saleGoodsDef.lszzr = 0;

						// 计算小票应收
						calcGoodsYsje(index);
						calcHeadYsje();
						saleEvent.updateSaleGUI();
					}
					return true;
				}
			}
		}

		// 返回false,执行取消交易的处理
		return false;
	}

	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox(Language.apply("该收银机不允许退货!"));

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
		if (SellType.isJS(this.saletype))
		{
		}
		else if (EBill.getDefault().getBackSaleBill(this)) // android 指定小票退货
		{

		}
		else if (isSpecifyTicketBack())
		{
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);

			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);

				if (this.saletype.equals(SellType.PREPARE_BACK))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();
				}
			}
			else if (done == frm.Clear)
			{
				thSyjh = null;
				thFphm = 0;
			}
			else
			{
				// 放弃,不修改上次输入的原收银机号和小票号
			}

			return;
		}

		// 从销售状态切换到相应的退货状态
		if ((saleGoods.size() <= 0) && SellType.ISSALE(saletype))
		{
			// 检查权限
			thgrantuser = null;
			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')) || (curGrant.thxe <= 0))
			{
				OperUserDef staff = backSellGrant();

				if (staff == null) { return; }

				//检查授权退货
				if (((staff.privth != 'Y') && (staff.privth != 'T')) || (staff.thxe <= 0))
				{
					new MessageBox(Language.apply("请核实授权工号是否具有退货权限及限额"));
					return;
				}
				
				// 本次授权
				thgrantuser = staff;

				// 记录日志
				String log = "授权退货,小票号:" + GlobalInfo.syjStatus.fphm + ",最大退货限额:" + thgrantuser.thxe + ",授权:" + thgrantuser.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				thgrantuser = (OperUserDef) (GlobalInfo.posLogin.clone());

				if (cursqktype == '1')
				{
					thgrantuser.gh = cursqkh;
				}
				else
				{
					thgrantuser.gh = GlobalInfo.posLogin.gh;
				}
			}

			// 提示退货权限
			if (curGrant.privth != 'T')
			{
				new MessageBox(Language.apply("授权退货,限额为{0}元", new Object[] { ManipulatePrecision.doubleToString(thgrantuser.thxe) }));
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype);
		}
		else
		{
			new MessageBox(Language.apply("请先完成当前交易!"), null, false);
		}
	}

	public void sendSecMonitor(String label)
	{
		sendSecMonitor(label, null, -1);
	}

	// format: [1]#@#[2]#@#[3]
	// [1]:text
	// [2]:font
	// [3]:color ex. 255_255_255
	public void sendSecMonitor(String label, String[] value, int index)
	{
		if (SecMonitor.secMonitor == null)
			return;

		if (label.equalsIgnoreCase("goods"))
		{
			String line = "";
			line = Convert.appendStringSize("", Language.apply("商品名:"), 0, 7, 90, 0);
			line = Convert.appendStringSize(line, value[2] + "[" + value[1] + "]", 7, 34, 90, 0);

			line = Convert.appendStringSize(line, Language.apply("数量:"), 44, 5, 90, 0);
			line = Convert.appendStringSize(line, value[4], 49, 6, 90, 1);

			line = Convert.appendStringSize(line, Language.apply("应付:"), 56, 5, 90, 0);
			line = Convert.appendStringSize(line, value[7], 61, 9, 90, 1);

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("会员号:"), 0, 7, 90, 0);
			line1 = Convert.appendStringSize(line1, getVipInfoLabel(), 7, 34, 90, 0);

			line1 = Convert.appendStringSize(line1, Language.apply("总量:"), 44, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getTotalQuantityLabel(), 49, 8, 90, 1);

			line1 = Convert.appendStringSize(line1, Language.apply("总付:"), 56, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getSellPayMoneyLabel(), 61, 9, 90, 1);

			line += ("#@#" + (20 + GlobalVar.secFont) + "#@#0_0_255");
			line1 += ("#@#" + (20 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowGoodsInfo(line, line1, index);
		}
		else if (label.equalsIgnoreCase("pay") || label.equalsIgnoreCase("total"))
		{
			String line = "";
			line = Convert.appendStringSize(line, Language.apply("应付金额:"), 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10, 11, 90, 1);
			line = Convert.appendStringSize(line, Language.apply("已付金额:"), 25, 9, 90, 0);
			line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleHead.sjfk, 2, 1), 36, 11, 90, 1);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("未付金额:"), 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, getPayBalanceLabel(), 10, 11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont));

			if (label.equalsIgnoreCase("pay"))
				SecMonitor.secMonitor.monitorShowPayInfo(line, line1);
			else
				SecMonitor.secMonitor.monitorShowTotalInfo(line, line1);
		}
		else if (label.equalsIgnoreCase("change"))
		{
			String line = "";
			line = Convert.appendStringSize(line, Language.apply("应付金额:"), 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10, 11, 90, 1);
			line = Convert.appendStringSize(line, Language.apply("已付金额:"), 25, 9, 90, 0);
			line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleHead.sjfk, 2, 1), 36, 11, 90, 1);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, Language.apply("找零金额:"), 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(saleHead.zl), 10, 11, 90, 1);
			line1 = Convert.appendStringSize(line1, Language.apply("损益金额:"), 25, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(saleHead.fk_sysy), 36, 11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowChangeInfo(line, line1);
		}
		else
		{
			SecMonitor.secMonitor.monitorShowWelcomeInfo();
		}
	}

	public void payInput()
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			checkSell();
		}
		else
		{
			paySell();
		}
	}

	public void quickPayButton(int key)
	{
		// 如果没有定义面值卡查询键,将面值卡付款键作为面值卡查询键使用
		if ((key == GlobalVar.PayMzk) && (NewKeyListener.searchKeyCode(GlobalVar.MzkInfo) <= 0))
		{
			MzkInfoQueryBS mzk = CustomLocalize.getDefault().createMzkInfoQueryBS();
			mzk.QueryMzkInfo();
			mzk = null;
		}
		else
		{
			sendQuickPayButton(key);
		}
	}

	public void sendQuickPayButton(int key)
	{
		// 如果前一个Pay键没有把付款窗口激活,后一个发出的key会又进入发送按键，导致循环。
		// 所以把quickpaystart标记true,禁止再次进入，当可以激活付款窗口时标记quickpaystart为false
		if (comfirmPay() && !quickpaystart)
		{
			quickpaystart = true;

			NewKeyListener.sendKey(GlobalVar.Pay); // 自动按下付款键
			// NewKeyListener.sendKey(key);
			quickpaykey = key;
		}
		else
		{
			quickpaystart = false;
		}
	}

	private int oldret = 0;// 记录旧有的自动测试选择

	public void autoTest()
	{
		if (oldret == 0)
		{
			int ret = new MessageBox(Language.apply("请选择自动测试处理功能？\n\n1 - 记录测试脚本\n2 - 执行测试脚本\n3 - 执行交易测试"), null, false).verify();
			if (ret != GlobalVar.Key1 && ret != GlobalVar.Key2 && ret != GlobalVar.Key3)
				return;

			if (ret == GlobalVar.Key1)
			{
				// 写入测试脚本
				if (!NewKeyListener.Recordstatus)
				{
					oldret = 1;
					GlobalInfo.statusBar.setHelpMessage(Language.apply("开始记录测试脚本!"));
					NewKeyListener.KeyRecord = "%2000,\n";
					NewKeyListener.Recordstatus = true;
				}
			}
			else if (ret == GlobalVar.Key2)
			{
				// 执行测试脚本
				if (TestThread.status == 0)
				{
					oldret = 2;
					TestThread.status = 1;
					new TestThread().start();
				}
			}
			else
			{
				// 执行交易测试
				if (TestThread.status == 0)
				{
					oldret = 3;
					TestThread.status = 2;
					TestThread.saleBS = this;
					TestThread.saleEvent = saleEvent;
					new TestThread().start();
				}
			}
		}
		else
		{
			// 停止动作
			if (oldret == 1)
			{
				// 写入测试脚本
				NewKeyListener.Recordstatus = false;
				write(GlobalVar.ConfigPath + "/AutoTest.ini", NewKeyListener.KeyRecord);
			}
			else if (oldret == 2)
			{
				// 执行测试脚本
				TestThread.status = 0;
			}
			else
			{
				// 执行压力测试
				TestThread.status = 0;
			}
			oldret = 0;
		}
	}

	public static void write(String path, String content)
	{
		try
		{
			File f = new File(path);
			if (f.exists())
			{
				GlobalInfo.statusBar.setHelpMessage(Language.apply("文件已存在，将覆盖原文件..."));
			}
			else
			{
				GlobalInfo.statusBar.setHelpMessage(Language.apply("文件不存在，正在创建..."));
				if (f.createNewFile())
				{
					GlobalInfo.statusBar.setHelpMessage(Language.apply("文件创建成功！"));
				}
				else
				{
					GlobalInfo.statusBar.setHelpMessage(Language.apply("文件创建失败！"));
				}

			}

			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(content);
			output.close();
			GlobalInfo.statusBar.setHelpMessage(Language.apply("测试脚本写入成功！"));
			new MessageBox(Language.apply("测试脚本写入成功!"));
		}
		catch (Exception e)
		{
			new MessageBox(Language.apply("测试脚本写入失败!"), null, false);
			e.printStackTrace();
		}
	}

	public boolean getReprintAuth()
	{
		return true;
	}

	// 重新打印上一张小票
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0)
				return;

			if (!CheckGoodsMode.getDefault().isLoad())
				return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1)
				return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("当前打印为即扫即打并且已有商品交易,不能重打!"), null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		MessageBox me = new MessageBox(Language.apply("你确实要重印上一张小票吗?"), null, true);
		try
		{
			if (me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				Object obj = null;
				String fphm = null;

				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					OperUserDef user = null;
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = Language.apply("授权重打印上一笔小票,授权工号:") + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}

				if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + ConfigClass.CashRegisterCode + "'")) != null)
				{
					try
					{
						fphm = String.valueOf(obj);

						if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm)) != null)
						{

							if (!rs.next())
							{
								new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
								return;
							}

							saleheadprint = new SaleHeadDef();

							if (!GlobalInfo.dayDB.getResultSetToObject(saleheadprint)) { return; }
						}
						else
						{
							new MessageBox(Language.apply("查询小票头失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salegoodsprint = new Vector();
							while (rs.next())
							{
								SaleGoodsDef sg = new SaleGoodsDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sg)) { return; }

								salegoodsprint.add(sg);

								ret = true;
							}

							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询小票明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					try
					{
						if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where syjh = '" + ConfigClass.CashRegisterCode + "' and fphm = " + fphm + " order by rowno")) != null)
						{
							boolean ret = false;
							salepayprint = new Vector();
							while (rs.next())
							{
								SalePayDef sp = new SalePayDef();

								if (!GlobalInfo.dayDB.getResultSetToObject(sp)) { return; }

								salepayprint.add(sp);

								ret = true;
							}
							if (!ret)
							{
								new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
								return;
							}
						}
						else
						{
							new MessageBox(Language.apply("查询付款明细失败!"), null, false);
							return;
						}
					}
					catch (Exception ex)
					{
						new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
						ex.printStackTrace();
						return;
					}
					finally
					{
						GlobalInfo.dayDB.resultSetClose();
					}

					saleheadprint.printnum++;
					AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm), String.valueOf(saleheadprint.printnum));
					ProgressBox pb = new ProgressBox();
					pb.setText(Language.apply("现在正在重打印小票,请等待....."));
					try
					{
						printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
					}
					finally
					{
						pb.close();
					}
				}
				else
				{
					new MessageBox(Language.apply("当前没有销售数据,不能打印!"));
				}
			}
		}
		finally
		{
			saleheadprint = null;

			if (salegoodsprint != null)
			{
				salegoodsprint.clear();
				salegoodsprint = null;
			}

			if (salepayprint != null)
			{
				salepayprint.clear();
				salepayprint = null;
			}
		}
	}

	public void preShopSale()
	{
		ArrayList listgoods = null;
		boolean findflag = false;
		StringBuffer billid = null;
		try
		{
			if (!SellType.ISSALE(this.saletype))
				return;

			// 先清除当前数据
			if (saleGoods.size() > 0)
			{
				if (new MessageBox(Language.apply("确定清除当前所录入的商品吗?"), null, true).verify() == GlobalVar.Key1)
					initOneSale(this.saletype);
				else
					return;
			}

			billid = new StringBuffer();
			if (!new TextBox().open(Language.apply("请输入单号"), "", Language.apply("请输入预售单号"), billid, 0, 0, false, TextBox.AllInput))
				return;

			listgoods = new ArrayList();

			if (NetService.getDefault().getShopPreSaleGoods(listgoods, billid.toString()))
			{
				if (listgoods.size() == 0)
					return;

				for (int i = 0; i < listgoods.size(); i++)
				{
					preSale = (ShopPreSaleDef) listgoods.get(i);

					if (preSale == null)
					{
						new MessageBox(Language.apply("获取单据中商品数据有误,请重试"));
						initOneSale(this.saletype);
						findflag = false;
						break;
					}

					preSale.index = i;

					if (findGoods(preSale.barcode, "", ""))
					{
						SaleGoodsDef salegoods = (SaleGoodsDef) this.saleGoods.get(i);
						if (salegoods == null)
						{
							new MessageBox(Language.apply("查找出的商品数据有误,请重试"));
							initOneSale(this.saletype);
							findflag = false;
							break;
						}

						calcHeadYsje();
						saleEvent.updateSaleGUI();
					}
					else
					{
						if (new MessageBox(Language.apply("未找到[{0}]的商品\n是否继续查找下一个", new Object[] { preSale.barcode }), null, true).verify() == GlobalVar.Key2)
							break;
					}
					findflag = true;
				}
				return;
			}
			else
			{
				new MessageBox(Language.apply("未找到此单据对应的商品明细\n请确认该单据是否已经被处理"));
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
		finally
		{
			if (findflag && billid != null && !billid.toString().equals(""))
				saleHead.str1 = billid.toString();

			preSale = null;
		}

	}

	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		String type = "SalePrintMode.ini";
		if (vsalehead != null && vsalehead.djlb != null)
			type = vsalehead.djlb;
		SaleHeadDef tempsalehead = null;
		Vector tempsalegoods = null;
		Vector tempsalepay = null;

		try
		{
			tempsalehead = SaleBillMode.getDefault(type).getSalehead();
			tempsalegoods = SaleBillMode.getDefault(type).getSalegoods();
			tempsalepay = SaleBillMode.getDefault(type).getSalepay();

			// 联网获取赠送打印清单
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead, vsalegoods, vsalepay);
			SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, gifts);

			// 检查是否需要重打印赠品联授权
			boolean bok = true;
			if (vsalehead.printnum > 0 && SaleBillMode.getDefault(type).needMSInfoPrintGrant())
			{
				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
				{
					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("重打印赠券授权"));

					if (staff == null || staff.priv.charAt(1) != 'Y')
					{
						new MessageBox(Language.apply("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限"));
						bok = false;
					}
				}
			}
			if (!bok)
			{
				SaleBillMode.getDefault(type).setSaleTicketMSInfo(vsalehead, null);
			}

			if (vsalehead != null && vsalegoods != null && vsalepay != null)
			{
				SaleBillMode.getDefault(type).setTemplateObject(vsalehead, vsalegoods, vsalepay);
				SaleBillMode.getDefault(type).printBill();
			}
			else
			{
				new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			SaleBillMode.getDefault(type).setTemplateObject(tempsalehead, tempsalegoods, tempsalepay);
		}
	}

	// 是否指定小票退货
	public boolean isSpecifyTicketBack()
	{
		if (SellType.ISBACK(saletype) && ((GlobalInfo.sysPara.inputydoc == 'Y') || GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C' || GlobalInfo.sysPara.inputydoc == 'D'))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// 获取预售小票信息
	public boolean findPreSaleInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{

			thsaleHead = new SaleHeadDef();
			thsaleGoods = new Vector();
			thsalePayment = new Vector();

			// 联网查询原小票信息
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("开始查找预售小票操作....."));
			if (!DataService.getDefault().getPreSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
			{
				pb.close();
				pb = null;
				return false;
			}
			pb.close();
			pb = null;

			// 生成退货商品明细
			saleGoods.clear();
			lastGoodsDetail.clear();
			for (int i = 0; i < thsaleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);

				sgd.yfphm = sgd.fphm;
				sgd.ysyjh = sgd.syjh;
				sgd.syjh = ConfigClass.CashRegisterCode;
				sgd.fphm = GlobalInfo.syjStatus.fphm;
				sgd.rowno = saleGoods.size() + 1;

				saleGoods.add(sgd);
			}

			if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
			{
				curCustomer = new CustomerDef();
				curCustomer.code = thsaleHead.hykh;
				curCustomer.name = thsaleHead.hykh;
				curCustomer.ishy = 'Y';
			}

			// 设置原小票头信息
			saleHead.ysje = thsaleHead.ysje;
			saleyfje = saleHead.ysje;

			saleHead.hykh = thsaleHead.hykh;
			saleHead.jfkh = thsaleHead.jfkh;
			saleHead.thsq = thsaleHead.thsq;
			saleHead.ghsq = thsaleHead.ghsq;
			saleHead.hysq = thsaleHead.hysq;
			saleHead.sqkh = thsaleHead.sqkh;
			saleHead.sqktype = thsaleHead.sqktype;
			saleHead.sqkzkfd = thsaleHead.sqkzkfd;
			saleHead.hhflag = thsaleHead.hhflag;
			saleHead.jdfhdd = thsaleHead.jdfhdd;
			saleHead.salefphm = thsaleHead.salefphm;

			// 重算小票头
			calcHeadYsje();

			// 计算预付欠款
			double yfqk = 0;
			for (int i = 0; i < thsalePayment.size(); i++)
			{
				SalePayDef pay = (SalePayDef) thsalePayment.get(i);

				if (CreatePayment.getDefault().isPaymentPreDebt(pay.paycode))
				{
					yfqk += pay.je;
				}
			}

			double dj = yfqk;
			Payment pay = null;
			if (SellType.ISPREPARETAKE(this.saletype))// 预付定金
			{
				dj = ManipulatePrecision.doubleConvert(saleyfje - yfqk);
				pay = CreatePayment.getDefault().createPaymentByPayMode(CreatePayment.getDefault().getPaymentPreDepositMode(), this);
			}
			else
			{
				pay = CreatePayment.getDefault().createPaymentByPayMode(CreatePayment.getDefault().getPaymentPreDebtMode(), this);
			}

			if (pay != null && dj > 0)
			{
				pay.inputPay(String.valueOf(dj));

				memoPayment.add(pay);
			}
			// 刷新界面显示
			saleEvent.clearTableItem();
			saleEvent.updateSaleGUI();

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}

	// 获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 只记录原单小票号和款机号,但不按原单找商品
				return false;
			}

			// 如果是新指定小票进入
			if (SellType.ISHH(saletype) || saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("开始查找退货小票操作....."));
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox(Language.apply("此小票有满赠礼品，请先到后台退回礼品再办理退货！"));
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + Language.apply("\n是否继续退货？"), null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE) && !SellType.ISHH(saletype))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox(Language.apply("原小票是[{0}]交易\n\n与当前退货交易类型不匹配", new Object[] { SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) }));
						// new MessageBox("原小票是[" +
						// SellType.getDefault().typeExchange(thsaleHead.djlb,
						// thsaleHead.hhflag, thsaleHead) +
						// "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("原数量"), Language.apply("原折扣"), Language.apply("原成交价"), Language.apply("退货"), Language.apply("退货数量") };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);

					if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
						row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}

					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { Language.apply("序"), Language.apply("付款名称"), Language.apply("账号"), Language.apply("付款金额") };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					// 选择要退货的商品
					cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open(Language.apply("开单营业员号："), "", Language.apply("请输入有效开单营业员号"), backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox(Language.apply("该工号不是营业员!"), null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
						continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = sgd.sl;
					sgd.str13 = "";
					if(SellType.ISHH(saletype)) sgd.str13 = "T";

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*
					 * 业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 if
					 * (!DataService.getDefault().getCustomer(curCustomer,
					 * thsaleHead.hykh)) { curCustomer.code = thsaleHead.hykh;
					 * curCustomer.name = "无效卡"; curCustomer.ishy = 'Y';
					 * 
					 * new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); }
					 */
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox(Language.apply("超出退货的最大限额，不能退货"));

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox(Language.apply("授权退货,限额为 {0} 元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}

	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods, Vector thsalePayment)
	{

	}

	public void exchangeSale()
	{
		if (saletype.equals(SellType.RETAIL_SALE) || saletype.equals(SellType.RETAIL_BACK))
		{
			// 记录按下换货键之前的换货标记状态
			char oldHhflag = hhflag;
			if (hhflag == 'N')
			{
				hhflag = 'Y';
			}
			else
			{
				hhflag = 'N';
			}

			// 销售状态按换货键，直接先进行换货退货
			String type = saletype;
			if (saletype.equals(SellType.RETAIL_SALE) && hhflag == 'Y')
			{
				type = SellType.RETAIL_BACK;
			}

			// 如果初始化失败，还原换货标记
			if (!saleEvent.saleform.setSaleType(type))
			{
				hhflag = oldHhflag;
			}
		}
		else
		{
			new MessageBox(Language.apply("当前状态不能进行换货交易!"));
		}
	}

	public boolean reSetBigColor(int goodsIndex)
	{
		return false;
	}

	public void rebateDetail(int index)
	{
		if (saleGoods.size() <= index)
			return;

		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		StringBuffer line = new StringBuffer();

		if (sgd.lszke > 0)
			line.append(Language.apply("临时折扣额:") + ManipulatePrecision.doubleToString(sgd.lszke, 2, 1, false, 10) + "\n");
		if (sgd.lszre > 0)
			line.append(Language.apply("临时折让额:") + ManipulatePrecision.doubleToString(sgd.lszre, 2, 1, false, 10) + "\n");
		if (sgd.lszzk > 0)
			line.append(Language.apply("总品折扣额:") + ManipulatePrecision.doubleToString(sgd.lszzk, 2, 1, false, 10) + "\n");
		if (sgd.lszzr > 0)
			line.append(Language.apply("总品折让额:") + ManipulatePrecision.doubleToString(sgd.lszzr, 2, 1, false, 10) + "\n");
		if (sgd.hyzke > 0)
			line.append(Language.apply("会员折扣额:") + ManipulatePrecision.doubleToString(sgd.hyzke, 2, 1, false, 10) + "\n");
		if (sgd.yhzke > 0)
			line.append(Language.apply("优惠折扣额:") + ManipulatePrecision.doubleToString(sgd.yhzke, 2, 1, false, 10) + "\n");
		if (sgd.zszke > 0)
			line.append(Language.apply("赠送折扣额:") + ManipulatePrecision.doubleToString(sgd.zszke, 2, 1, false, 10) + "\n");
		if (sgd.cjzke > 0)
			line.append(Language.apply("厂家折扣额:") + ManipulatePrecision.doubleToString(sgd.cjzke, 2, 1, false, 10) + "\n");
		if (sgd.ltzke > 0)
			line.append(Language.apply("零头折扣额:") + ManipulatePrecision.doubleToString(sgd.ltzke, 2, 1, false, 10) + "\n");
		if (sgd.qtzke > 0)
			line.append(Language.apply("其他折扣额:") + ManipulatePrecision.doubleToString(sgd.qtzke, 2, 1, false, 10) + "\n");
		if (sgd.qtzre > 0)
			line.append(Language.apply("其他折让额:") + ManipulatePrecision.doubleToString(sgd.qtzre, 2, 1, false, 10) + "\n");

		if (line.length() > 0)
			line.append("---------------------\n" + Language.apply("折扣合计共:") + ManipulatePrecision.doubleToString(sgd.hjzk, 2, 1, false, 10));

		if (line.length() > 0)
			new MessageBox(line.toString());
	}

	// 更改商品发票打印名称
	public boolean changeBillName(int index)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return false;

		if (!PathFile.fileExist(GlobalVar.ConfigPath + "/GoodsBillName.ini"))
		{
			new MessageBox(Language.apply("没有找到商品发票名称列表文件"));
			return false;
		}

		BufferedReader br = null;

		br = CommonMethod.readFileGB2312(GlobalVar.ConfigPath + "/GoodsBillName.ini");

		if (br == null)
		{
			new MessageBox(Language.apply("打开商品发票名称列表文件失败!"));

			return false;
		}

		String line = null;

		Vector goodsNames = new Vector();

		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				goodsNames.add(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		String[] title = { Language.apply("代码"), Language.apply("商品发票名称") };
		int[] width = { 60, 440 };
		String[] content = null;
		Vector contents = new Vector();
		for (int i = 0; i < goodsNames.size(); i++)
		{
			String goodName = (String) goodsNames.elementAt(i);
			content = new String[2];
			content[0] = String.valueOf(i + 1);
			content[1] = goodName;
			contents.add(content);
		}

		String goodcode = ((SaleGoodsDef) saleGoods.elementAt(index)).code;

		String billname = ((SaleGoodsDef) saleGoods.elementAt(index)).str9;

		String memo = "";

		if (billname != null && billname.length() > 0)
		{
			memo = Language.apply("请为商品{0}选择发票名称,现发票名称为: ", new Object[] { goodcode }) + billname;
		}
		else
		{
			memo = Language.apply("请为商品 {0} 选择发票名称", new Object[] { goodcode });
		}

		int choice = new MutiSelectForm().open(memo, title, width, contents, true);
		if (choice >= 0)
		{
			((SaleGoodsDef) saleGoods.elementAt(index)).str9 = ((String[]) contents.elementAt(choice))[1];
		}

		return true;
	}

	public void execCustomKey(boolean keydownonsale, int key)
	{
		if (ConfigClass.QuickPay != null && ConfigClass.QuickPay.containsKey(String.valueOf(key)))
		{
			if (keydownonsale)
			{
				sendQuickPayButton(key);
			}
			else
			{
				// 定位付款方式
				int last = payButtonToPayModePosition(key);
				if (last >= 0)
				{
					salePayEvent.gotoPayModeLocation(last);
				}
			}
		}
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey0);
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey1);
	}

	public void execCustomKey2(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey2);
	}

	public void execCustomKey3(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey3);
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey4);
	}

	public void execCustomKey5(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey5);
	}

	public void execCustomKey6(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey6);
	}

	public void execCustomKey7(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey7);
	}

	public void execCustomKey8(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey8);
	}

	public void execCustomKey9(boolean keydownonsale)
	{
		execCustomKey(keydownonsale, GlobalVar.CustomKey9);
	}

	public boolean exchangeSale(boolean ishhPay)
	{
		return !ishhPay;
	}

	public boolean HHinit()
	{
		return true;
	}

	public boolean chooseHyRebate()
	{
		if (curCustomer != null && checkMemberSale())
		{
			if (curCustomer.iszk == 'Y')
			{
				curCustomer.iszk = 'N';
			}
			else
			{
				curCustomer.iszk = 'Y';
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void calcHandVIPDiscount(int Goodsindex)
	{
		GoodsDef gd1 = (GoodsDef) goodsAssistant.get(Goodsindex);

		// 求商品记录中的最底折扣率
		double maxzkl = gd1.maxzkl * 100;
		if (maxzkl == 100)
		{
			new MessageBox(Language.apply("此商品不允许VIP折扣"));
			return;
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入该商品VIP折扣率"), Language.apply("折扣率"), "", buffer, GlobalInfo.sysPara.handVIPDiscount, 100, true)) { return; }

		double inputDiscount = Double.parseDouble(buffer.toString().trim());

		maxzkl = GlobalInfo.sysPara.handVIPDiscount >= maxzkl ? GlobalInfo.sysPara.handVIPDiscount : maxzkl;
		if (inputDiscount < maxzkl)
		{
			new MessageBox(Language.apply("你输入的折扣率小于最低VIP折扣率，请重新输入!"));
		}
		else
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Goodsindex);
			double sjje = saleGoodsDef.hjje - saleGoodsDef.hjzk;

			saleGoodsDef.hyzke = sjje - (sjje * inputDiscount / 100);

			// 不允许参加后台VIP折扣
			gd1.isvipzk = 'N';

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();
		}
	}

	public String getGzCode(String gz)
	{
		if (gz.length() > 0 && gz.indexOf("[") > -1 && gz.indexOf("]") > -1)
		{
			return gz.substring(0, gz.indexOf("["));
		}
		else
		{
			return gz;
		}
	}

	public Vector AllManaframe = new Vector();

	public void getAllGroup()
	{
		Vector vc = AccessLocalDB.getDefault().getAllManaframe();

		if (vc != null)
			AllManaframe = vc;
	}

	GoodsInfoQueryBS gib;

	public Vector AllGoods = new Vector();

	public void getGoods(String groupcode)
	{
		AllGoods.clear();

		ArrayList listgoods = new ArrayList();

		if (gib == null)
		{
			gib = CustomLocalize.getDefault().createGoodsInfoQueryBS();
		}

		if (gib.arrItem == null)
		{
			gib.readTemplateFile();
		}

		FindGoodsItem item = null;
		int i = 0;
		for (; i < gib.arrItem.size(); i++)
		{
			item = (FindGoodsItem) gib.arrItem.get(i);

			if (item.strCodeType.equals("99"))
			{
				break;
			}
		}

		if (i >= gib.arrItem.size())
		{
			new MessageBox(Language.apply("未定义99号商品查询指令!"));
			return;
		}

		boolean result = gib.getGoodsList(listgoods, "99", groupcode, "");

		if (result)
		{
			for (int j = 0; j < listgoods.size(); j++)
			{
				GoodsDef goods = (GoodsDef) listgoods.get(j);

				String value[] = { goods.barcode, converName(goods.name) + "\n" + "¥ " + ManipulatePrecision.doubleToString(goods.lsj), goods.name, ManipulatePrecision.doubleToString(goods.lsj), goods.code, goods.gz, goods.unit };

				AllGoods.add(value);
			}
		}
	}

	private String converName(String strname)
	{
		String returnname = strname;

		returnname = Convert.newSubString(strname, 0, 8);
		returnname = returnname + "\n" + Convert.newSubString(strname, 9, strname.getBytes().length);

		return returnname;
	}

	public void bigInfoMemoSet(int goodsindex)
	{

	}

	public boolean locateGoods()
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return false;
		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入商品条码或编码"), Language.apply("商品定位"), "", buffer, 1)) { return false; }
		String code = buffer.toString();
		boolean isLocated = false;
		SaleGoodsDef goodsDef = null;
		int index = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			goodsDef = (SaleGoodsDef) saleGoods.get(i);
			if (goodsDef.code.equals(code) || goodsDef.barcode.equals(code))
			{
				isLocated = true;
				index = i;
				break;
			}
		}

		if (isLocated)
		{
			saleEvent.table.setSelection(index);
			
			if(GlobalInfo.sysPara.localgoodsisdelgoods=='Y')
				deleteGoods(index);
			
		}

		return true;
	}

	public boolean checkCust()
	{
		// TODO Auto-generated method stub
		return true;
	}

	public boolean checkCust(MzkResultDef mrd)
	{
		return true;
	}

	// 获取预售小票信息
	public boolean findGroupBuyInfo(String billNo)
	{
		Vector thsaleGoods = null;
		try
		{
			thsaleGoods = new Vector();
			// 联网查询原小票信息
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("开始查找团购小票操作....."));
			if (!DataService.getDefault().getGroupBuy(billNo, thsaleGoods))
			{
				pb.close();
				pb = null;
				return false;
			}
			pb.close();
			pb = null;

			// 生成退货商品明细
			saleGoods.clear();
			lastGoodsDetail.clear();
			saleHead.str3 = billNo;
			for (int i = 0; i < thsaleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);

				sgd.yfphm = sgd.fphm;
				sgd.ysyjh = sgd.syjh;
				sgd.syjh = ConfigClass.CashRegisterCode;
				sgd.fphm = GlobalInfo.syjStatus.fphm;
				sgd.rowno = saleGoods.size() + 1;
				saleHead.hykh = sgd.str2;
				saleHead.hytype = sgd.str3;
				sgd.str3 = "";
				saleGoods.add(sgd);
			}

			// 查找原交易会员卡资料
			if (saleHead.hykh != null && !saleHead.hykh.trim().equals(""))
			{
				curCustomer = new CustomerDef();
				curCustomer.code = saleHead.hykh;
				curCustomer.name = saleHead.hykh;
				curCustomer.ishy = 'Y';
			}

			// 计算小票应收
			calcHeadYsje();

			// 刷新界面显示
			saleEvent.clearTableItem();
			saleEvent.updateSaleGUI();

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}
		}
	}

	public void checkAfterPay()
	{

	}

	public boolean checkIsSalePay(String code)
	{
		return false;
	}

	// isDelete代表是否删除付款的时候
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
		// TODO 自动生成方法存根
		return false;
	}

	public boolean checkDeleteSalePay(String string)
	{
		// TODO 自动生成方法存根
		return checkDeleteSalePay(string, false);
	}
}
