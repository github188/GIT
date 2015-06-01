package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

// 其他业务类
public class SaleBS3Modify extends SaleBS2Goods
{
	protected String gd_Prefix = "GD_";
	public StringBuffer sbBarcode = null;

	// 测试使用
	public SaleBS3Modify()
	{
		super();
	}

	public OperUserDef deleteGoodsGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.privqx != 'Y' && staff.privqx != 'Q')
		{
			new MessageBox(Language.apply("该员工授权卡无法授权取消单品"));
			return null;
		}

		return staff;
	}

	public boolean allowDeleteGoods(int index)
	{
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货取消必须使用【取消】功能键"));
			return false;
		}

		if(SellType.ISHH(saletype))
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
			if(this.isHHGoods(saleGoodsDef))
			{
				new MessageBox(Language.apply("换货取消必须使用【取消】功能键"));
				return false;
			}
		}
		
		return true;
	}

	// 删除商品
	public boolean deleteGoods(int index)
	{
		SaleGoodsDef old_goods = null;

		if (!allowDeleteGoods(index))
			return false;

		// 根据参数决定删除商品是按光标选择还是编码删除
		double quantity = 0;// 要删除商品的数量
		String barcode = "";// 要删除商品的编码
		double tempSL = 0;// 保存旧的销售数量

		if (GlobalInfo.sysPara.removeGoodsModel == 'Y')// Y-按商编码删除商品的模式/N-现有用光标选择商品进行删除的模式
		{
			StringBuffer code = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入要删除的商品条码"), Language.apply("商品条码"), Language.apply("1、直接输入商品条码，删除列表中所有匹配的商品；") + "\r\n" + Language.apply("2、输入 数量*商品条码，倒序删除指定数量的商品。"), code, 0, 0, false, TextBox.AllInput);
			if (!done) { return false; }

			// 分解输入码 数量*编码，得到输入的数量、商品编码
			String[] s = convertQuantityBarcode(code.toString());
			if (s == null)
				return false;
			quantity = Convert.toDouble(s[0]);
			barcode = s[1];

			// 倒序搜索商品列表，找到匹配的商品编码（按inputbarcode字段匹配）
			index = -1;
			int deleteMode = code.toString().indexOf("*") >= 0 ? 2 : 1;// 求删除模式
			SaleGoodsDef temp = null;
			boolean result = true;

			for (int i = saleGoods.size() - 1; i >= 0; i--)
			{
				temp = (SaleGoodsDef) saleGoods.elementAt(i);
				if (temp.inputbarcode != null && temp.inputbarcode.equals(barcode))// 找到商品后将本行数量减去相应的值
				{
					index = i;// 得到要删的商品行号
					old_goods = temp;
					if (deleteMode == 1)// 删除所有匹配商品
					{
						if (!doneDeleteGoods(index, old_goods))
							result = false;
					}
					else
					{
						// 删除指定数量商品
						if (quantity <= 0.0)
							break;
						if (quantity >= old_goods.sl)
						{
							tempSL = old_goods.sl;
							if (!doneDeleteGoods(index, old_goods))
								result = false;

							quantity = quantity - tempSL;
						}
						else
						{
							tempSL = quantity;
							old_goods.sl = old_goods.sl - quantity;

							// 重算商品应收
							old_goods.hjje = ManipulatePrecision.doubleConvert(old_goods.sl * old_goods.jg, 2, 1);
							clearGoodsGrantRebate(index);
							calcGoodsYsje(index);

							// 重算小票应收
							calcHeadYsje();

							quantity = quantity - tempSL;
						}
					}
				}
			}

			if (-1 == index)
			{
				new MessageBox(Language.apply("没有找到要删除的商品条码。"));
				return false;
			}
			else
			{
				// new MessageBox("商品删除完成。");
				return result;
			}
		}
		else
		{
			// 删除前提示确认
			if (GlobalInfo.sysPara.removeGoodsMsg == 'Y')
			{
				old_goods = (SaleGoodsDef) saleGoods.elementAt(index);
				if (new MessageBox(Language.apply("你确定要删除此以下商品吗?\n\n[") + old_goods.barcode + "]" + old_goods.name, null, true).verify() != GlobalVar.Key1) { return false; }
			}

			// 现有用光标选择商品进行删除的模式，先将本行数量记0
			old_goods = (SaleGoodsDef) saleGoods.elementAt(index);
			return doneDeleteGoods(index, old_goods);
		}
	}

	/**
	 * 1、判读有权删除商品， 2、计算删除本行，对其他行商品产生的影响 3、网格中删除选择行号的商品并计算小票合计
	 * 
	 * @param index
	 * @param old_goods
	 * @param oldsl
	 * @return
	 */
	protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
		// 没有删除权限,不允许删除
		if ((curGrant.privqx != 'Y' && curGrant.privqx != 'Q'))
		{
			// 授权
			OperUserDef staff = deleteGoodsGrant(index);
			if (staff == null)
				return false;

			// 记录日志
			String log = "授权删除,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",单价:" + Convert.increaseDou(old_goods.jg, 10) + ",授权:" + Convert.increaseChar(staff.gh, ' ', 6) + ",商品:" + old_goods.barcode;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_DELETESALE);
		}
		else
		{
			String log = "删除商品,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",单价:" + Convert.increaseDou(old_goods.jg, 10) + ",授权:" + Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6) + ",商品:" + old_goods.barcode;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_DELETESALE);
		}

		// 修改老的盘点单数据 删除时记录删除标志
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"A".equals(old_goods.str8))
		{
			if ("D".equals(old_goods.str8))
				return false;
			old_goods.str8 = "D";
			old_goods.name += "[删除]";
			return true;
		}

		SaleGoodsDef cloneGoods = (SaleGoodsDef) old_goods.clone();
		old_goods.sl = 0;

		// 重算因为删除本行，对其他行商品产生的影响
		old_goods.hjje = old_goods.jg * old_goods.sl;
		clearGoodsGrantRebate(index);
		calcGoodsYsje(index);

		// 删除数量为零的商品
		if (0.0 == old_goods.sl)
		{
			if (!delSaleGoodsObject(index))
				return false;
		}

		// 计算小票合计
		calcHeadYsje();

		// 删除上次显示列表,刷新显示列表
		if (0.0 == old_goods.sl)
		{
			getDeleteGoodsDisplay(index, cloneGoods);
		}

		return true;
	}

	public OperUserDef inputQuantityGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.privqx != 'Y' && staff.privqx != 'Q')
		{
			new MessageBox(Language.apply("该员工授权卡无法授权修改数量"));
			return null;
		}

		return staff;
	}

	public boolean allowInputQuantity(int index)
	{
		if (isSpecifyBack())
		{
			new MessageBox(Language.apply("指定小票退货不能修改数量"));
			return false;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		// 电子秤商品不允许修改数量
		if (saleGoodsDef.flag == '2')
			return false;

		if(this.isHHGoods(saleGoodsDef))
		{
			new MessageBox(Language.apply("换货商品不能修改数量"));
			return false;
		}
		return true;
	}

	// 输入数量
	public boolean inputQuantity(int index)
	{
		return inputQuantity(index, -1);
	}

	// 输入数量
	public boolean inputQuantity(int index, double quantity)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;
		double newsl = -1;
		boolean flag = false;
		// 如果输入了
		if (quantity >= 0)
		{
			flag = true;
			newsl = quantity;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;
		// 判断是否允许修改数量
		if (!allowInputQuantity(index))
			return false;

		// 输入数量
		StringBuffer buffer = new StringBuffer();
		do
		{
			if (!flag)
			{
				buffer.delete(0, buffer.length());
				buffer.append(ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1, true));

				// 检查是否从电子称里获取重量金额
				boolean input = true;

				// 电子秤不让修改数量
				/*
				 * if (GlobalInfo.sysPara.elcScaleMode == '1') { if
				 * (!ElectronicScale.getDefault().isSendData()) { //电子秤商品 if
				 * (saleGoodsDef.flag == '2') { if
				 * (ElectronicScale.getDefault().setPrice(saleGoodsDef.jg)) {
				 * new MessageBox("请将[" + saleGoodsDef.name +
				 * "]商品放在电子称上\n按【确认键】获取重量后请将商品拿离", null, false,
				 * GlobalVar.Validation); if
				 * (ElectronicScale.getDefault().run()) { newsl =
				 * ElectronicScale.getDefault().getWeight();
				 * 
				 * if (newsl != 0) input = false; } else { new
				 * MessageBox("获取重量失败"); return false; } } else { new
				 * MessageBox("发送价格失败"); return false; } } } }
				 */

				if (input)
				{
					if (SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype) || (saleEvent.yyyh.getText().trim().equals(Language.apply("超市")) && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
					{
						if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "", buffer, 1, getMaxSaleGoodsQuantity(), true, TextBox.IntegerInput, -1)) { return false; }
					}
					else
					{
						if (!new TextBox().open(Language.apply("请输入该商品数量"), Language.apply("数量"), "", buffer, 0.0001, getMaxSaleGoodsQuantity(), true)) { return false; }
					}
					newsl = Double.parseDouble(buffer.toString());
				}
				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
				flag = true;
			}
			// 检查销红
			if (SellType.ISSALE(saletype) && (GlobalInfo.sysPara.isxh != 'Y') && (goodsDef.kcsl > 0))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					if (GlobalInfo.sysPara.xhisshowsl == 'Y')
						new MessageBox(Language.apply("销售数量已大于该商品库存【{0}】\n\n不能销售", new Object[]{goodsDef.kcsl + ""}));
					else
						new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));

					if (flag)
						return false;
					continue;
				}
			}

			// 指定小票退货
			if (isSpecifyBack(saleGoodsDef))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox(Language.apply("退货数量已大于该商品原销售数量\n\n不能退货"));
					if (flag)
						return false;
					continue;
				}
			}

			// 检查印花限量优惠
			if (stampList != null && stampList.size() > 0 && SellType.ISSALE(saletype) && goodsDef.poptype != '0' && goodsDef.infonum1 > -9999.00)
			{
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz) + newsl - saleGoodsDef.sl;
				if (goodsDef.infonum1 < hjsl)
				{
					new MessageBox(Language.apply("该商品只有【{0}】个促销数量\n\n商品数量修改无效", new Object[]{goodsDef.infonum1 +""}));
					if (flag)
						return false;
					continue;
				}
			}

			// 跳出循环
			break;
		} while (true);

		if (newsl < 0)
			return false;

		// 无权限
		if ((newsl < saleGoodsDef.sl) && (curGrant.privqx != 'Y') && (curGrant.privqx != 'Q'))
		{
			//
			OperUserDef staff = inputQuantityGrant(index);
			if (staff == null)
				return false;

			// 记录日志
			String log = "授权修改数量,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",数量:" + newsl + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		if (info != null)
			oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = newsl;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * newsl);
		double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / oldsl * newsl);
		clearGoodsGrantRebate(index);
		saleGoodsDef.lszre = lszre;
		saleGoodsDef.lszke = lszzk;

		getZZK(saleGoodsDef);
		calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox(Language.apply("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox(Language.apply("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效"));

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "A";
			}
			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		return true;
	}

	public OperUserDef inputPriceGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.privgj != 'Y')
		{
			new MessageBox(Language.apply("该员工授权卡无法授权修改价格"));
			return null;
		}

		return staff;
	}

	public boolean allowInputPrice(int index)
	{
		if (SellType.isJF(saletype) || SellType.isJS(saletype))
			return true;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 电子秤商品不允许修改数量
		if (saleGoodsDef.flag == '2')
			return false;

		// 指定小票退货不允许修改价格
		if (isSpecifyBack(saleGoodsDef))
			return false;

		// 商品不允许议价
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && goodsDef.lsj > 0 && goodsDef.ischgjg == 'N')
		{
			new MessageBox(Language.apply("该商品不允许改价"));
			return false;
		}

		if((this.isHHGoods(saleGoodsDef)))
		{
			new MessageBox(Language.apply("换货商品不允许改价"));
			return false;
		}
		return true;
	}

	// 输入金额
	public boolean inputPrice(int index)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		double newjg;
		String grantgh = null;

		// 检查是否允许输入价格
		if (!allowInputPrice(index)) { return false; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;

		GoodsDef goodsDef = null;
		if (goodsAssistant != null && goodsAssistant.size() > index)
			goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 定价商品授权改价
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && (goodsDef == null || goodsDef.lsj > 0) && curGrant.privgj != 'Y')
		{
			OperUserDef staff = inputPriceGrant(index);
			if (staff == null)
				return false;

			// 记录授权号
			grantgh = staff.gh;
		}

		// 输入价格
		StringBuffer buffer = new StringBuffer();
		newjg = saleGoodsDef.jg;
		do
		{
			buffer.delete(0, buffer.length());
			buffer.append(newjg);

			if (!new TextBox().open(Language.apply("请输入该商品价格"), Language.apply("价格"), "", buffer, 0.01, getMaxSaleGoodsMoney(), true)) { return false; }

			newjg = Convert.toDouble(buffer.toString());
			AccessDayDB.getDefault().writeWorkLog("商品编码" + saleGoodsDef.code + "原价格" + saleGoodsDef.jg + "修改为" + newjg);
			// 输入价格按价格金额截取
			if (goodsDef != null)
				newjg = getConvertPrice(Double.parseDouble(buffer.toString()), goodsDef);
			newjg = ManipulatePrecision.doubleConvert(newjg, 2, 1);

			// 检查价格(P:配件;Z:赠品)
			if (goodsDef != null && goodsDef.type != 'P' && goodsDef.type != 'Z' && newjg <= 0)
			{
				new MessageBox(Language.apply("该商品价格必须大于0"));
				continue;
			}

			// 最低限价
			if (goodsDef != null && newjg < goodsDef.maxzke)
			{
				new MessageBox(Language.apply("该项商品价格不能小于最低限价") + ManipulatePrecision.doubleToString(goodsDef.maxzke));
				continue;
			}

			// 是否允许在商品退货时,商品是否在下限和上限的价格之内
			if (!isAllowedBackPriceLimit(goodsDef, newjg))
				continue;

			// 跳出循环
			break;
		} while (true);

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 重算商品应收
		if (goodsDef != null && goodsDef.lsj > 0)
			saleGoodsDef.flag = '6'; // 标记该商品被议价
		saleGoodsDef.jg = newjg;
		saleGoodsDef.lsj = newjg;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		clearGoodsGrantRebate(index);

		if (goodsDef != null)
			calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 价格过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox(Language.apply("商品价格过大,导致销售金额达到上限\n\n商品价格修改无效"));

			// 恢复价格
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 价格过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox(Language.apply("商品价格过大,导致退货金额超过限额\n\n商品价格修改无效"));

			// 恢复数量
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.str8 = "A";
				saleGoodsDef.name += "[修改]";
			}

			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		// 记录授权日志
		if (grantgh != null)
		{
			String log = "授权修改价格,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",原价:" + oldGoodsDef.jg + ",新价格:" + saleGoodsDef.jg + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		return true;
	}

	// 检查商品是否允许折扣
	public boolean checkGoodsRebate(GoodsDef goodsDef)
	{
		return checkGoodsRebate(goodsDef, null);
	}

	// 检查商品是否允许折扣
	public boolean checkGoodsRebate(GoodsDef goodsDef, SpareInfoDef info)
	{
		if ((goodsDef.issqkzk != 'Y' && cursqktype == '1') || (goodsDef.isvipzk != 'Y' && cursqktype == '2') || (goodsDef.maxzkl * 100) >= 100)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public boolean checkGoodsGrantRange(GoodsDef goodsDef, String grantgz)
	{
		if (grantgz != null && grantgz.length() > 0 && !grantgz.equals("ALL") && grantgz.indexOf(goodsDef.gz) == -1)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	// 获得最低的折扣率
	public double getMaxRebateGrant(double grantzkl, GoodsDef goodsDef)
	{
		return getMaxRebateGrant(grantzkl, goodsDef.maxzkl);
	}

	// 获得最低的折扣率
	public double getMaxRebateGrant(double grantzkl, double goodsmaxzkl)
	{
		double maxzkl = grantzkl;

		if (goodsmaxzkl > maxzkl)
		{
			maxzkl = goodsmaxzkl;
		}

		return maxzkl;
	}

	public boolean breachRebateGrant(OperUserDef grant)
	{
		if (grant.priv.length() > 2 && grant.priv.charAt(2) == 'Y')
			return true;
		else
			return false;
	}

	public boolean breachRebateGrant(OperRoleDef grant)
	{
		if (grant.priv.length() > 2 && grant.priv.charAt(2) == 'Y')
			return true;
		else
			return false;
	}

	public OperUserDef inputRebateGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.dpzkl * 100 >= 100)
		{
			new MessageBox(Language.apply("该员工授权卡无法授权单品打折"));
			return null;
		}
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if (!checkGoodsGrantRange(goodsDef, staff.grantgz))
		{
			new MessageBox(Language.apply("该商品不在员工授权卡授权范围内"));
			return null;
		}
		return staff;
	}

	// 输入折扣
	public boolean inputRebate(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新、换退商品不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8') || (this.isHHGoods(saleGoodsDef))) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox(Language.apply("该商品不允许打折!"));

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣率
		double maxzkl = 0;
		if (grantflag)
		{
			// new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		// 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

		// 输入折扣
		String maxzklmsg = Language.apply("收银员正在对该商品进行打折");

		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg = Language.apply("收银员对该商品的单品折扣权限为 ") + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + Language.apply("%\n你目前最多在成交价基础上再打折 ") + ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入单品折扣百分比(%)") + (grantflag == true ? Language.apply("(允许突破商品最低折扣限制)") : ""), Language.apply("单品折扣"), maxzklmsg, buffer, lszkl, 100, true))
		{
			// 恢复数据
			saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		// 得到折扣率
		grantzkl = Double.parseDouble(buffer.toString());

		// 计算最终折扣
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	// 输入折让金额
	public boolean inputRebatePrice(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新、换退商品不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8') || (this.isHHGoods(saleGoodsDef))) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox(Language.apply("该商品不允许打折!"));

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣额
		double maxzkl = 0;
		if (grantflag)
		{
			// new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		double maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
		// goodsDef.maxzke为最低限价
		if ((goodsDef.maxzke * saleGoodsDef.sl) <= saleGoodsDef.hjje && saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl) < maxzre)
		{
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl), 2, 1);
		}

		// 输入折让
		String maxzremsg = Language.apply("收银员对该商品进行折让");

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = Language.apply("收银员对该商品的单品折扣权限为{0}%\n你目前对该商品最多只能够折让到{1} 元", new Object[]{ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszre, 2, 1, true)});
//				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多只能够折让到 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open(Language.apply("请输入单品折让后的成交价") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("单品折让"), maxzremsg, buffer, lszre, saleGoodsDef.hjje, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			lszre = Double.parseDouble(buffer.toString());

			// 清除所有手工折扣,按输入的成交价计算最终折让
			saleGoodsDef.lszke = 0;
			saleGoodsDef.lszre = 0;
			saleGoodsDef.lszzk = 0;
			saleGoodsDef.lszzr = 0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszre = maxzre - getZZK(saleGoodsDef) + saleGoodsDef.lszre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			if (lszre < 0)
				lszre = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				
				//maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多还可以再折让 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open(Language.apply("请输入单品要折让的金额") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("单品折让"), maxzremsg, buffer, 0, lszre, true))
//				if (!new TextBox().open("请输入单品要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, 0, lszre, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()), 2, 1);
		}

		if (getZZK(saleGoodsDef) > maxzre)
		{
			saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre, 2, 1);
		}

		if (saleGoodsDef.lszre < 0)
			saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	public boolean inputCJZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if((this.isHHGoods(saleGoodsDef)))
		{
			new MessageBox("换货商品不允许输入单品零头折扣");
			return false;
		}
		double cjzk = saleGoodsDef.cjzke;
		saleGoodsDef.cjzke = 0;
		getZZK(saleGoodsDef);

		double je = saleGoodsDef.hjje - saleGoodsDef.hjzk;

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open(Language.apply("请输入单品零头折扣"), Language.apply("零头折扣"), Language.apply("最大可进行的最大零头折让为 ") + ManipulatePrecision.doubleToString(GlobalInfo.sysPara.feechargelimit, 2, 1, true), buffer, 0, je, true))
		{
			saleGoodsDef.cjzke = ManipulatePrecision.sub(je, Double.parseDouble(buffer.toString()));
			return true;
		}
		else
		{
			saleGoodsDef.cjzke = cjzk;
			getZZK(saleGoodsDef);
			return false;
		}
	}

	public boolean inputSaleFphm()
	{
		Printer.getDefault().inputSaleFphm();
		return true;
	}

	public OperUserDef inputAllRebateGrant()
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.zpzkl * 100 >= 100)
		{
			new MessageBox(Language.apply("该员工授权卡无法授权总品打折"));
			return null;
		}

		return staff;
	}

	// 输入总折扣
	public boolean inputAllRebate()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折扣,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.zpzkl;
			grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;
		}

		// 计算商品能否打折
		boolean rebate = false;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			rebate = true;
			break;
		}

		if (!rebate)
		{
			new MessageBox(Language.apply("整单没有可打折的商品，不能手工折扣"));
			return false;
		}

		String maxzzklmsg = Language.apply("该收银员正在进行整单打折");

		// 总折扣计算模式为批量单品折扣模式
		if (GlobalInfo.sysPara.batchtotalrebate == 'Y')
		{
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = Language.apply("收银员对权限范围内商品的总折扣权限为{0}%\n你目前最多在权限内交易额基础上再打折{1}%", new Object[]{ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true)});
//				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%";
			}

			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open(Language.apply("请输入整单折扣百分比(%)"), Language.apply("整单折扣"), maxzzklmsg, buffer, grantzkl * 100, 100, true)) { return false; }

			// 得到折扣率
			double zkl = Double.parseDouble(buffer.toString());

			// 循环为每个单品打折
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记，削价 不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费,以旧换新 不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 计算权限允许的最大折扣额
				double maxzzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}

				// 计算最终折扣
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
				if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1))
				{
					// 提示
					new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + Language.apply("\n\n最多能打折 ") + ManipulatePrecision.doubleToString(maxzzkl * 100) + "%");

					//
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0)
					saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
			}
		}
		else
		{
			// 计算整单最打可打折金额
			double sumzzk = 0, sumlszzk = 0, lastzzk = 0, hjcjj = 0, hjzke = 0;
			int lastzzkrow = -1;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不在授权范围,只要是权限范围内的商品不管商品本身能不能打折，都参与总折计算，然后分摊时不分摊
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 累计可折扣金额
				sumzzk += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
				sumlszzk += saleGoodsDef.lszzk;
				hjcjj += saleGoodsDef.hjje - saleGoodsDef.hjzk;
				hjzke += saleGoodsDef.hjzk;

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 计算商品权限允许的最大折扣额,找可折让金额最大的商品
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
				if (maxzzk > lastzzk)
				{
					lastzzk = maxzzk;
					lastzzkrow = i;
				}
			}

			// 反算得到当前最大打折比例
			double lszkl = (sumzzk - hjzke + sumlszzk) / (hjcjj + sumlszzk);
			if (lszkl < 0)
				lszkl = 0;
			lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

			// 输入折扣
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = Language.apply("收银员对权限范围内商品的总折扣权限为{0}%\n你目前最多在权限内交易额基础上再打折{1}%", new Object[]{ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszkl, 2, 1, true)});
//				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
			}

			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open(Language.apply("请输入整单折扣百分比(%)") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("整单折扣"), maxzzklmsg, buffer, lszkl, 100, true)) { return false; }

			// 得到折扣金额,打折后按收银机定义四舍五入
			double zkl = Double.parseDouble(buffer.toString());
			double zzkje = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (hjcjj + sumlszzk), 2, 1);
			double tempysje = (saleHead.hjzje - saleHead.hjzke + sumlszzk) - zzkje;
			double tempyfje = getDetailOverFlow(tempysje);
			zzkje = ManipulatePrecision.sub(zzkje, ManipulatePrecision.sub(tempyfje, tempysje));

			// 把总折扣额分摊到每个商品
			double hjzzk = 0;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 计算商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

				// 取消其他手工折扣,计算最终折扣
				saleGoodsDef.sqkh = "";
				saleGoodsDef.sqktype = '\0';

				// 每个商品分摊的折让按金额占比计算
				if (i != lastzzkrow)
				{
					if (GlobalInfo.sysPara.batchtotalrebate == 'N')
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.lszzk) / (hjcjj + sumlszzk) * zzkje, 2, 1);
					}
					else
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(maxzzk / sumzzk * zzkje, 2, 1);
					}
					if (getZZK(saleGoodsDef) > maxzzk)
					{
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - maxzzk;
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0)
						saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk, getGoodsApportionPrecision());

					// 重算商品折扣合计
					getZZK(saleGoodsDef);

					// 计算已分摊的总折让
					hjzzk += saleGoodsDef.lszzk;
				}
			}

			// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
			if (lastzzkrow >= 0)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
				if (getZZK(saleGoodsDef) > lastzzk)
				{
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - lastzzk;
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0)
					saleGoodsDef.lszzk = 0;
				getZZK(saleGoodsDef);
			}
		}

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	// 输入总折让
	public boolean inputAllRebatePrice()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折让,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.zpzkl;
			grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;
		}

		// 计算整单最打可打折金额
		double sumzzr = 0, summxzzr = 0, sumlszzr = 0, lastzre = 0, hjzke = 0;
		int lastzrerow = -1;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);

			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 累计可折让金额
			summxzzr += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
			sumlszzr += saleGoodsDef.lszzr;
			hjzke += saleGoodsDef.hjzk;

			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}

			// 计算每个商品权限允许的最大折扣额,找可折让金额最大的商品
			double maxzkl = 0;
			if (grantflag)
			{
				// 允许突破最低折扣
				maxzkl = getMaxRebateGrant(grantzkl, 0);
			}
			else
			{
				// 不允许最低折扣
				maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
			}
			double maxzzr = 0;
			if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)), 2, 1);
			}
			else
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszzr)), 2, 1);
			}
			sumzzr += maxzzr;
			if (maxzzr > lastzre)
			{
				lastzre = maxzzr;
				lastzrerow = i;
			}
		}

		if (summxzzr <= 0)
		{
			new MessageBox(Language.apply("整单没有可打折的商品，不能手工折扣"));
			return false;
		}

		// 输入折让
		double zzrje = 0;

		String maxzzrmsg = Language.apply("该收银员正在进行整单折让");

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszzr = saleHead.hjzje - summxzzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = Language.apply("收银员对权限内商品的总折扣权限为{0}%\n你目前对整单成交价最多只能折让到{1}元", new Object[]{ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszzr, 2, 1, true)});
//				maxzzrmsg = "收银员对权限内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对整单成交价最多只能折让到 " + ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}

			if (!new TextBox().open(Language.apply("请输入整单折让后的成交价") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("整单折让"), maxzzrmsg, buffer, lszzr, saleHead.hjzje, true)) { return false; }

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;
				getZZK(saleGoodsDef);
			}
			calcHeadYsje();

			zzrje = saleHead.hjzje - saleHead.hjzke - Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszzr = summxzzr - hjzke + sumlszzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
			if (lszzr < 0)
				lszzr = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = Language.apply("收银员对权限范围内商品的总折扣权限为{0}%\n你目前对权限范围内商品最多还可以折让{1}元",new Object[]{ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true),ManipulatePrecision.doubleToString(lszzr, 2, 1, true)});
//				maxzzrmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对权限范围内商品最多还可以折让 " + ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}

			if (!new TextBox().open(Language.apply("请输入整单要折让的金额") + (grantflag == true ? Language.apply("(允许突破最低折扣)") : ""), Language.apply("整单折让"), maxzzrmsg, buffer, 0, lszzr, true)) { return false; }
			zzrje = Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}

		// 把总折让额分摊到每个商品
		double hjzzr = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);

			// new MessageBox(saleGoodsDef.flag +" "+saleGoodsDef.type);
			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}

			// 取消其他手工折扣,计算最终折扣
			saleGoodsDef.sqkh = "";
			saleGoodsDef.sqktype = '\0';

			// 每个商品分摊的折让按金额占比计算
			if (i != lastzrerow)
			{
				// 计算商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - saleGoodsDef.hjzk) / sumzzr * zzrje, 2, 1);
				// new MessageBox(maxzkl +" "+(saleGoodsDef.hjje -
				// saleGoodsDef.hjzk) + " "+sumzzr+" "+zzrje);
				double maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
				if (getZZK(saleGoodsDef) > maxzzr)
				{
					saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
					saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
				}
				if (saleGoodsDef.lszzr < 0)
					saleGoodsDef.lszzr = 0;
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr);
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr, getGoodsApportionPrecision());

				// new MessageBox(saleGoodsDef.name
				// +"  "+saleGoodsDef.lszzr+" "+maxzkl);
				// 重算商品折扣合计
				getZZK(saleGoodsDef);

				// 计算已分摊的总折让
				hjzzr += saleGoodsDef.lszzr;
			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzrerow >= 0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzrerow);
			saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(zzrje - hjzzr, 2, 1);
			// BUG lastzre是
			if (saleGoodsDef.lszzr > lastzre)
			// if (getZZK(saleGoodsDef) > lastzre)
			{
				saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - lastzre;
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
			}
			if (saleGoodsDef.lszzr < 0)
				saleGoodsDef.lszzr = 0;
			getZZK(saleGoodsDef);
		}

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	// 检查挂单信息
	public boolean checkHaveHangInfo()
	{
		int n = getHangFileCount();

		if (n > 0)
		{
			if (new MessageBox(Language.apply("还有{0}张挂单小票,你确定不解挂吗？", new Object[]{n + ""}), null, true).verify() == GlobalVar.Key1)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	public int getHangFileCount()
	{
		int n = 0;

		TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
		String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

		File file = new File(path);
		if (file.isDirectory())
		{
			for (int i = 0; i < file.list().length; i++)
			{
				if (file.list()[i].indexOf(gd_Prefix) == 0)
				{
					n++;
				}
			}
		}

		return n;
	}

	public Vector getHangFileInfo()
	{
		Vector v = new Vector();

		TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
		String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

		File file = new File(path);
		if (file.isDirectory())
		{
			ManipulateDateTime mdt = new ManipulateDateTime();
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++)
			{
				if (filelist[i].indexOf(gd_Prefix) == 0)
				{
					// 读取挂单数据
					FileInputStream in = null;
					ObjectInputStream si = null;
					String saletype1;
					SaleHeadDef saleHead1;
					try
					{
						in = new FileInputStream(path + "//" + filelist[i]);
						si = new ObjectInputStream(in);
						saletype1 = (si.readObject()).toString();
						saleHead1 = (SaleHeadDef) si.readObject();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						continue;
					}
					finally
					{
						try
						{
							si.close();
							si = null;
							in.close();
							in = null;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}

					//
					String fileInfo[] = new String[5];
					fileInfo[0] = filelist[i].substring(gd_Prefix.length());
					mdt.setTimeInMill(file.listFiles()[i].lastModified());
					fileInfo[1] = mdt.getDateTimeString();
					fileInfo[2] = saleHead1.syyh;
					fileInfo[3] = SellType.getDefault().typeExchange(saletype1, 'N', saleHead);
					fileInfo[4] = ManipulatePrecision.doubleToString(saleHead1.ysje);
					v.add(fileInfo);
				}
			}
		}
		return v;
	}

	public int getHangFileIndex(boolean update)
	{
		PrintWriter pw = null;
		BufferedReader br = null;
		int maxGD = 0;

		try
		{
			TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
			String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;
			String name = path + "//IndexGD.txt";
			File indexFile = new File(name);

			// 读取挂单计数
			if (indexFile.exists())
			{
				br = CommonMethod.readFile(name);
				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}
					else
					{
						maxGD = Integer.parseInt(line);
					}
				}

				br.close();
				br = null;
			}
			else
			{
				maxGD = 1;
			}

			// 设置挂单数+1
			if (update)
			{
				pw = CommonMethod.writeFile(name);
				pw.println(maxGD + 1);
				pw.flush();
				pw.close();
				pw = null;
			}

			return maxGD;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("获取挂单号错!\n\n") + e.getMessage().trim());

			return -1;
		}
		finally
		{
			try
			{
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean writeHangGrant()
	{
		// Y - 挂单解挂 A - 挂单
		if (curGrant.priv.length() > 6 && curGrant.priv.charAt(6) != 'Y' && curGrant.priv.charAt(6) != 'A')
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("收银挂单授权"));

			if (staff != null)
			{
				if (staff.priv.length() > 6 && (staff.priv.charAt(6) != 'Y' && staff.priv.charAt(6) != 'A'))
				{
					new MessageBox(Language.apply("当前工号没有挂单权限!"));
					return false;
				}

				String log = Language.apply("授权写入挂单,授权工号:") + staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				return false;
			}
		}
		if(SellType.ISHH(saletype))
		{
			new MessageBox(Language.apply("换货小票不允许挂单!"));
			return false;
		}
			
		return true;
	}

	// 上传挂单信息
	protected boolean sendHang(int invno, StringBuffer strnetcode)
	{
		// 买券和盘点不进行联网挂单
		if (SellType.ISCHECKINPUT(this.saletype) || SellType.ISCOUPON(this.saletype) || SellType.ISJFSALE(this.saletype)) { return true; }

		long xph = saleHead.fphm;
		saleHead.fphm = invno;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			sgd.fphm = invno;
		}

		if (!DataService.getDefault().sendSaleGd(saleHead, saleGoods))
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				sgd.fphm = xph;
			}

			saleHead.fphm = xph;

			return false;
		}

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			sgd.fphm = xph;
		}

		strnetcode.append(saleHead.syjh + "-" + String.valueOf(saleHead.fphm));

		saleHead.fphm = xph;

		return true;
	}

	// 写入挂单
	public boolean writeHang()
	{
		if (saleEvent.table.getItemCount() <= 0) 
			return false;
		
		// 练习交易不允许挂单
		if (SellType.ISEXERCISE(this.saletype))
			return false;

		if (getHangFileCount() + 1 > GlobalInfo.sysPara.gdTimes)
		{
			new MessageBox(Language.apply("当前挂单数已超过系统限定笔数"));
			return false;
		}

		// 检查挂单权限
		if (!writeHangGrant())
			return false;

		if (isRealTimePrint())
		{
			for (int i = 0; i < realTimePrintFlag.length(); i++)
			{
				if (realTimePrintFlag.charAt(i) == 'N')
				{
					SaleBillMode.getDefault().printRealTimeDetail(i);
				}
			}

		}

		FileOutputStream f = null;
		try
		{
			TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
			String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

			// 读取挂单最大值
			int maxGD = getHangFileIndex(true);
			if (maxGD < 0)
				return false;

			// 写入挂单文件
			f = new FileOutputStream(path + "//" + gd_Prefix + maxGD);

			ObjectOutputStream s = new ObjectOutputStream(f);

			// 写入交易类型
			s.writeObject(new String(saletype));

			// 写入交易对象
			brokenAssistant.removeAllElements();
			writeSellObjectToStream(s);

			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			StringBuffer strnetcode = new StringBuffer();

			if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
			{
				if (!sendHang(maxGD, strnetcode))
				{
					new MessageBox(Language.apply("当前交易挂单失败!"));

					File gdFile = new File(path + "//" + gd_Prefix + maxGD);
					if (gdFile.exists())
					{
						gdFile.delete();
					}
					return false;
				}
			}

			// 记录日志
			AccessDayDB.getDefault().writeWorkLog("收银员进行 " + maxGD + " 号挂单成功,挂单金额: " + ManipulatePrecision.doubleToString(saleHead.ysje));

			// 提示
			StringBuffer info = new StringBuffer();
			info.append(Language.apply("挂 单 号: ") + Convert.appendStringSize("", String.valueOf(maxGD), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("挂单金额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ysje) + Language.apply(" 元"), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("挂单时间: ") + Convert.appendStringSize("", ManipulateDateTime.getCurrentDateTime(), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("收银机号: ") + Convert.appendStringSize("", ConfigClass.CashRegisterCode, 1, 20, 20, 0) + "\n");
			info.append(Language.apply("收银员号: ") + Convert.appendStringSize("", GlobalInfo.posLogin.gh, 1, 20, 20, 0) + "\n");
			if (strnetcode.length() > 0)
				info.append(Language.apply("网络挂单: ") + Convert.appendStringSize("", strnetcode.substring(0), 1, 20, 20, 0) + "\n");

			new MessageBox(info.toString());

			if (GlobalInfo.sysPara.isPrintGd.trim().equals("Y") || GlobalInfo.sysPara.isPrintGd.trim().equals("A"))
			{
				// 由于要打印挂单，所以在即扫即打时先把已打印部分做打印放弃
				realTimePrintCancelSale();
				// 打印
				printHang(maxGD);

			}

			// 开始新交易
			saleEvent.initOneSale(this.saletype);

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("当前交易挂单失败!\n\n") + e.getMessage().trim());

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean checkExistHang()
	{
		Vector v = getHangFileInfo();
		
		if (v.size() <= 0)
			return false;
		
		return true;
	}

	// 读取挂单
	public boolean readHang()
	{
		// 练习交易不允许挂单
		if (SellType.ISEXERCISE(this.saletype))
			return false;

		// 已有商品不能解挂
		if (saleGoods.size() > 0 && !GlobalInfo.sysPara.onlineGd.equals("A"))
			return false;

		// 检查解挂授权
		if (!readHangGrant())
			return false;

		FileInputStream in = null;
		ObjectInputStream si = null;
		int indexID = 0;

		// 是否需要联网解挂
		boolean isonlinegd = false;

		try
		{
			//
			StringBuffer slstr = new StringBuffer();
			String path = "";
			File gdFile = null;

			int maxGD = getHangFileIndex(false) - 1;
			if (maxGD > 0)
				slstr.append(maxGD);

			do
			{
				// 如果在连网挂单可以不在解挂列表中选择,直接按回车退出选择挂单界面
				boolean cannotchoice = false;
				String strmsg = Language.apply("请输入挂单号");
				if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
				{
					cannotchoice = true;
					strmsg = Language.apply("请输入挂单号或网络挂单号");
				}

				// 输入挂单号
				int choice = -1;
				Vector v = getHangFileInfo();
				MutiSelectForm msf = new MutiSelectForm();
				choice = msf.open(strmsg, new String[] { Language.apply("挂单号"), Language.apply("挂单时间"), Language.apply("收银员号"), Language.apply("交易类型"), Language.apply("交易金额") }, new int[] { 80, 210, 120, 100, 135 }, v, true, 700, 400, 673, 285, false, cannotchoice);

				if (choice < 0 && choice != -2)
				{
					return false;
				}
				else if (choice == -2 && msf.InputText.length() > 0)
				{
					String strinvno = msf.InputText;
					if (GlobalInfo.sysPara.onlineGd.equals("Y") || GlobalInfo.sysPara.onlineGd.equals("A"))
					{
						if (getHang(strinvno))
						{
							isonlinegd = true;

							break;
						}
						else
						{
							// 联网挂单失败则初始化交易
							// initNewSale();
						}
					}
				}
				else if (choice >= 0)
				{
					// 本地挂单
					String[] row = (String[]) v.elementAt(choice);
					indexID = Integer.parseInt(row[0]);

					// 查找挂单
					TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
					path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;
					gdFile = new File(path + "//" + gd_Prefix + indexID);
					if (!gdFile.exists())
					{
						new MessageBox(Language.apply("找不到{0}号挂单!", new Object[]{" " + indexID + " "}));
					}
					else
					{
						isonlinegd = false;

						break;
					}
				}
			} while (true);

			// 如果不是联网挂单需要联网解挂
			if (!isonlinegd)
			{
				// 读取挂单
				in = new FileInputStream(path + "//" + gd_Prefix + indexID);
				si = new ObjectInputStream(in);

				// 先检查交易类型
				String saletype1 = (si.readObject()).toString();
				if (!saletype1.equals(saletype))
				{
					si.close();
					si = null;

					new MessageBox(Language.apply("此挂单必须在{0}状态下才能解挂!", new Object[]{SellType.getDefault().typeExchange(saletype1, 'N', saleHead)}));

					return false;
				}

				// 先初始化交易
				initNewSale();

				// 读取交易对象
				readStreamToSellObject(si);

				// 关闭文件
				si.close();
				si = null;
				in.close();
				in = null;

				if (gdFile != null)
				{
					// 删除挂单文件
					gdFile.delete();
				}
			}

			// 刷新数据
			refreshSaleData();

			// 记录日志
			AccessDayDB.getDefault().writeWorkLog("收银员进行 " + indexID + " 号挂单解挂,解挂金额: " + ManipulatePrecision.doubleToString(saleHead.ysje));

			// 计算应付金额
			calcHeadYfje();

			// 刷新界面显示
			saleEvent.updateSaleGUI();

			// 焦点到编码输入框
			if (saleGoods.size() > 0 && GlobalInfo.syjDef.issryyy == 'Y')
			{
				SaleGoodsDef g = (SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1);
				saleEvent.yyyh.setText(g.yyyh);
				saleEvent.gz.setText(g.gz);
				saleEvent.saleform.setFocus(saleEvent.code);
				curyyyfph = g.fph;
			}

			// 检查是否存在付款
			if (salePayment.size() > 0)
			{
				// 先清除全部付款对象列表
				payAssistant.removeAllElements();

				// 根据付款信息创建付款对象
				SalePayDef sp = null;
				for (int i = 0; i < salePayment.size(); i++)
				{
					sp = (SalePayDef) salePayment.elementAt(i);

					// 创建付款对象
					Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
					if (pay == null)
					{
						// 放弃所有已付款
						salePayment.removeAllElements();
						payAssistant.removeAllElements();
						return true;
					}

					// 增加已付款
					payAssistant.add(pay);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(indexID + " " + Language.apply("号挂单解挂失败!\n\n") + e.getMessage().trim());

			return false;
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
				if (si != null)
					si.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	// 获得挂单信息
	public boolean getHang(String invno)
	{
		try
		{
			// 先初始化交易
			if (GlobalInfo.sysPara.onlineGd.equals("Y"))
				initNewSale();

			// 设置挂单标志
			isonlinegdjging = true;

			SaleHeadDef salegdhead = new SaleHeadDef();
			salegdhead.djlb = saleHead.djlb;

			Vector salegdgoods = new Vector();

			if (!DataService.getDefault().getSaleGdInfo(invno, salegdhead, salegdgoods))
			{
				new MessageBox(Language.apply("网上没有查找到当前挂单号!"));
				return false;
			}

			if (!saletype.equals(salegdhead.djlb))
			{
				new MessageBox(Language.apply("此挂单必须在{0}状态下才能解挂!", new Object[]{SellType.getDefault().typeExchange(salegdhead.djlb, 'N', saleHead)}));
				return false;
			}

			if (SellType.ISBACK(saletype) && salegdgoods.size() > 0 && ((SaleGoodsDef) salegdgoods.get(0)).yfphm > 0 && ((SaleGoodsDef) salegdgoods.get(0)).ysyjh.trim().length() > 0)
			{
				thFphm = ((SaleGoodsDef) salegdgoods.get(0)).yfphm;
				thSyjh = ((SaleGoodsDef) salegdgoods.get(0)).ysyjh;

				// 指定小票退货
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);
					sgd.fphm = saleHead.fphm;
					sgd.syjh = saleHead.syjh;
					sgd.rowno = saleGoods.size() + 1;

					sgd.hjzk = getZZK(sgd);

					// 重算商品应收
					sgd.hjje = ManipulatePrecision.doubleConvert(sgd.sl * sgd.jg, 2, 1);

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());

					calcGoodsYsje(saleGoods.size() - 1);
				}

				// 查找原交易会员卡资料
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = salegdhead.hykh;
					curCustomer.name = salegdhead.hykh;
					curCustomer.ishy = 'Y';
				}

				salegdhead.sqkh = saleHead.sqkh;
				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

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
							new MessageBox(Language.apply("超出退货的最大限额,限额为{0}元不能退货", new Object[]{ManipulatePrecision.doubleToString(staff.thxe)}));

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
							new MessageBox(Language.apply("授权退货,限额为{0}元", new Object[]{ManipulatePrecision.doubleToString(curGrant.thxe)}));
						}
					}
				}

				return true;
			}
			else
			{
				// 其它交易
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					if (!memberGrant())
					{
						salegdhead.hykh = "";
						salegdhead.hysq = "";
						salegdhead.hytype = "";
					}
				}

				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

				String strmsg = "";
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					saleEvent.code.setText("");

					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);

					if (!findGoods(sgd.inputbarcode, sgd.yyyh, sgd.gz))
					{
						// 未找到该商品
						strmsg += "[" + sgd.inputbarcode + "]" + sgd.name + "\n";

						continue;
					}

					// 查找商品成功
					SaleGoodsDef sgd1 = (SaleGoodsDef) saleGoods.get(saleGoods.size() - 1);

					clearGoodsGrantRebate(saleGoods.size() - 1);

					GoodsDef gd1 = (GoodsDef) goodsAssistant.get(saleGoods.size() - 1);

					if (gd1.isdzc == 'Y')
					{
						String[] codeInfo = new String[4];
						boolean isdzcm = analyzeBarcode(gd1.inputbarcode, codeInfo);

						// 如果电子称商品不是通过电子称码来找的商品，所以要将这些值赋上去
						// 否则是通过电子称码来查找商品，则数量价格可以自己解析
						if (!isdzcm)
						{
							sgd1.jg = sgd.jg;
							sgd1.sl = sgd.sl;
							sgd1.hjje = sgd.hjje;
							sgd1.lszke = sgd.lszke;
							sgd1.flag = sgd.flag;
						}
					}
					else
					{
						sgd1.jg = sgd.jg;
						sgd1.sl = sgd.sl;
						sgd1.hjje = sgd.hjje;
						sgd1.lszke = sgd.lszke;

						sgd1.hjje = ManipulatePrecision.doubleConvert(sgd1.jg * sgd1.sl, 2, 1);
					}

					// 只重新赋值手工折扣，会员、促销等折扣靠findGoods重新找商品自动运算
					sgd1.lszke = sgd.lszke;
					sgd1.lszre = sgd.lszre;
					sgd1.lszzk = sgd.lszzk;
					sgd1.lszzr = sgd.lszzr;
					sgd1.cjzke = sgd.cjzke;
					sgd1.ltzke = sgd.ltzke;
					sgd1.qtzke = sgd.qtzke;
					sgd1.qtzre = sgd.qtzre;

					sgd1.sqkh = sgd.sqkh;
					sgd1.sqktype = sgd.sqktype;
					sgd1.sqkzkfd = sgd.sqkzkfd;
					sgd1.batch = sgd.batch;

					calcGoodsYsje(saleGoods.size() - 1);
				}

				calcHeadYsje();

				if (strmsg.trim().length() > 0)
				{
					new MessageBox(Language.apply("未找到解挂单中以下商品:\n") + strmsg);
				}

				return true;
			}
		}
		finally
		{
			isonlinegdjging = false;
		}
	}

	// 打印挂单小票
	public void printHang(int maxGD)
	{
		ProgressBox progress = new ProgressBox();
		progress.setText(Language.apply("正在打印挂单信息，请等待....."));

		HangBillMode.getDefault().printBill(maxGD, saleHead.ysje);

		progress.close();
	}

	public boolean readHangGrant()
	{
		// Y - 挂单解挂 B - 解挂
		if (curGrant.priv.length() > 6 && curGrant.priv.charAt(6) != 'Y' && curGrant.priv.charAt(6) != 'B')
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("收银解挂授权"));

			if (staff != null)
			{
				if (staff.priv.length() > 6 && staff.priv.charAt(6) != 'Y' && staff.priv.charAt(6) != 'B')
				{
					new MessageBox(Language.apply("当前工号没有解挂权限!"));
					return false;
				}

				String log = Language.apply("授权解挂单,授权工号:") + staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	// 人员授权
	public boolean operGrant()
	{
		OperUserDef staff = DataService.getDefault().personGrant();

		if (staff == null)
			return false;

		// 设置本笔交易授权
		curGrant.privth = staff.privth;
		curGrant.privqx = staff.privqx;
		curGrant.privdy = staff.privdy;
		curGrant.privgj = staff.privgj;
		curGrant.priv = staff.priv;
		curGrant.dpzkl = staff.dpzkl;
		curGrant.zpzkl = staff.zpzkl;
		curGrant.thxe = staff.thxe;
		curGrant.privje1 = staff.privje1;
		curGrant.privje2 = staff.privje2;
		curGrant.privje3 = staff.privje3;
		curGrant.privje4 = staff.privje4;
		curGrant.privje5 = staff.privje5;
		curGrant.grantgz = staff.grantgz;

		// 设置当前授权卡为员工卡
		cursqkh = staff.gh;
		cursqktype = '1';
		cursqkzkfd = staff.privje1;

		// 设置本笔小票员工授权卡号
		saleHead.ghsq = cursqkh;

		// 提示
		if (GlobalInfo.sysPara.grtpwdshow == 'Y')
			new MessageBox(Language.apply("员工卡授权本笔交易成功"));
		else
			new MessageBox(Language.apply("员工卡[{0}]授权本笔交易", new Object[]{cursqkh}));

		// 当前为退货交易，记录退货授权
		if (SellType.ISBACK(saletype))
		{
			saleHead.thsq = cursqkh;

			new MessageBox(Language.apply("授权退货,限额为{0}元", new Object[]{ManipulatePrecision.doubleToString(curGrant.thxe)}));
		}

		return true;
	}

	// 会员授权
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许重新刷卡"));
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡"));
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
			return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}

	public boolean memberGrantFinish(CustomerDef cust)
	{
		// 记录当前顾客卡
		curCustomer = cust;

		// 具有积分功能
		customerIsJf(cust);

		// 具有会员功能
		customerIsHy(cust);

		// 具有折扣功能
		customerIsZk(cust);

		return true;
	}

	public void customerIsJf(CustomerDef cust)
	{
		// 具有积分功能
		if (cust.isjf == 'Y')
		{
			saleHead.jfkh = cust.code;
		}
	}

	public void customerIsHy(CustomerDef cust)
	{
		saleHead.hykh = cust.code;
		saleHead.hytype = cust.type;

		// 具有会员功能,H-会员价模式/Y,V-会员折上折模式
		if (cust.ishy == 'Y' || cust.ishy == 'V' || cust.ishy == 'H')
		{
			// 重算所有商品应收
			for (int i = 0; i < saleGoods.size(); i++)
			{
				calcGoodsYsje(i);
			}

			// 计算小票应收
			calcHeadYsje();
		}
	}

	public void customerIsZk(CustomerDef cust)
	{
		// 具有折扣功能
		if (cust.iszk == 'Y')
		{
			// 记录到小票
			saleHead.hysq = cust.code;

			// 设置当前授权卡为顾客卡
			cursqkh = cust.code;
			cursqktype = '2';
			cursqkzkfd = 1;

			// 授权
			String msg = "";
			if (cust.func == null || cust.func.length() <= 0)
				cust.func = "A";
			if (cust.func.charAt(0) != 'Y' && cust.func.charAt(0) != 'N')
			{
				curGrant.zpzkl = cust.zkl;
				curGrant.dpzkl = cust.zkl;
				msg = Language.apply("顾客卡授权打折\n\n总品及单品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'Y')
			{
				curGrant.zpzkl = cust.zkl;
				msg = Language.apply("顾客卡授权打折\n\n总品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'N')
			{
				curGrant.dpzkl = cust.zkl;
				msg = Language.apply("顾客卡授权打折\n\n单品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}

			// 提示
			new MessageBox(msg);
		}
	}

	public boolean isPreTakeStatus()
	{
		if (SellType.ISPREPARETAKE(this.saletype)) { return true; }

		return false;
	}

	// 模糊查询商品信息
	public void queryGoodsInfo()
	{
		try
		{
			sbBarcode = new StringBuffer();// 商品条码[单选时用]
			new com.efuture.javaPos.UI.Design.GoodsInfoQueryForm(sbBarcode, new ICallBack()
			{
				public void exec(String barcode)
				{
					if (GlobalInfo.sysPara.isMoreSelectQuerygoods == 'Y')
						addQueryGoodsInfo(barcode);
				}
			});
			if (GlobalInfo.sysPara.isMoreSelectQuerygoods == 'N' && sbBarcode != null)
			{
				addQueryGoodsInfo(sbBarcode.toString());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 添加模糊查询的商品
	public void addQueryGoodsInfo(String barcode)
	{
		if (barcode.toString().trim().length() > 0)
		{
			try
			{
				saleEvent.saleform.code.setText(barcode);
				saleEvent.saleBS.enterInput();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				saleEvent.saleform.code.setText("");
			}
		}
	}

	// 输入印花促销码
	public boolean inputStampPop()
	{
		StringBuffer stampCode = new StringBuffer();
		// 印花码
		TextBox txt = new TextBox();
		if (!txt.open(Language.apply("请扫描印花券码"), Language.apply("印花码"), Language.apply("已扫入{0}条印花码", new Object[]{stampList.size()+ ""}), stampCode, 0, 0, false, TextBox.AllInput)) { return false; }

		if (stampCode.toString().length() > 0)
		{
			if (stampList == null)
				stampList = new Vector();
			stampList.add(stampCode.toString());
			return true;
		}
		return false;
	}
}
