package custom.localize.Zspj;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsUnitsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Zspj_SaleBS2Goods extends Zspj_SaleBS1Zrx
{
	// int curLine = 0;
	// boolean isSameGoods = false;

	/*
	 * 合并商品 para:要合并的商品信息 return true:合并成功，false:合并失败或未找到合并商品
	 */
	/*
	 * private boolean HbGoods(SaleGoodsDef saleGoodsDef) { try { if
	 * (GlobalInfo.sysPara.isHbGoods == 'Y' && saleGoods.size() >= 1)// 是否合并商品 {
	 * // 拦截哪些交易类型不让合并 if (SellType.ISBATCH(saletype)) { // 同一张单中存在相同编码商品 if
	 * (isSameGoods) return true; else return false; }
	 * 
	 * SaleGoodsDef saleGoodsDefTmp = null; for (int i = 0; i <
	 * saleGoods.size(); i++) { saleGoodsDefTmp = (SaleGoodsDef)
	 * saleGoods.elementAt(i); if (saleGoodsDefTmp == null) continue;
	 * 
	 * if (saleGoodsDefTmp.lszke > 0 || saleGoodsDefTmp.lszre > 0 ||
	 * saleGoodsDefTmp.lszzk > 0 || saleGoodsDefTmp.lszzr > 0) continue;
	 * 
	 * // 判断合并条件,电子秤条码不能累加 if (saleGoodsDef.flag != '2' &&
	 * saleGoodsDefTmp.code.equals(saleGoodsDef.code) &&
	 * saleGoodsDefTmp.gz.equals(saleGoodsDef.gz) &&
	 * (saleGoodsDefTmp.barcode.equals(saleGoodsDef.barcode) ||
	 * saleGoodsDefTmp.barcode.equals(saleGoodsDef.inputbarcode)) &&
	 * saleGoodsDefTmp.bzhl == saleGoodsDef.bzhl && saleGoodsDefTmp.jg ==
	 * saleGoodsDef.jg && saleGoodsDefTmp.lsj == saleGoodsDef.lsj) {
	 * saleGoodsDefTmp.sl += saleGoodsDef.sl;// 新数量 saleGoodsDefTmp.hjje =
	 * ManipulatePrecision.doubleConvert(saleGoodsDefTmp.sl *
	 * saleGoodsDefTmp.jg, 2, 1); curLine = i; calcGoodsYsje(i);
	 * 
	 * return true;// 合并成功 } } } curLine = -1; } catch (Exception ex) {
	 * ex.printStackTrace(); } return false;// 合并失败 }
	 */

	public boolean inputGoodsAddInfo(GoodsDef goodsDef)
	{
		// 输入商品批号,加入商品明细时检查是否加入批号了
		if (goodsDef.isbatch == 'Y' || goodsDef.isbatch == 'A')
		{
			// A模式商品继承上一个商品的批号,不清除批号
			if (goodsDef.isbatch == 'Y')
				curBatch = "";
			if (goodsDef.isbatch == 'A' && saleGoods.size() <= 0)
				curBatch = "";

			// 输入批号
			if (curBatch == null || curBatch.equals(""))
			{
				StringBuffer bstr = new StringBuffer();
				if (new TextBox().open("请输入商品批号", "批号", "该商品要求输入商品批号", bstr, -1, -1, false, TextBox.AllInput))
				{
					curBatch = bstr.toString();
				}
				bstr = null;
			}
		}
		else
		{
			curBatch = "";
		}

		// 批发销售输入折扣分担
		curPfzkfd = 1;
		if (SellType.ISBATCH(saletype))
		{
			isSameGoods = false;
			if (GlobalInfo.sysPara.isHbGoods == 'Y' && saleGoods.size() >= 1)
			{
				SaleGoodsDef saleGoodsDefTmp = null;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDefTmp = (SaleGoodsDef) saleGoods.elementAt(i);
					if (saleGoodsDefTmp.code.equals(goodsDef.code))
					{
						// 同一单中存在重复商品
						isSameGoods = true;
						new MessageBox("当前输入商品与第【" + String.valueOf(i + 1) + "】行商品重复\r\n请直接修改数量或采用多张小票交易方式");
						return true;
					}
				}
			}
			StringBuffer bstr = new StringBuffer();
			bstr.append("100");
			if (!new TextBox().open("请输入商品的商家折扣分担(%):", "折扣分担", "请输入百分比,不能大于100,也不能小于0", bstr, 0, 100, true))
			{
				new MessageBox("未输入商品商家折扣分担\n\n商品查询失败！");
				return false;
			}
			curPfzkfd = ManipulatePrecision.doubleConvert(Double.parseDouble(bstr.toString()) / 100, 4, 1);
			bstr = null;
		}

		return true;
	}

	/*
	 * public boolean addSaleGoods(GoodsDef goodsDef, String yyyh, double
	 * quantity, double price, double allprice, boolean dzcm) { //
	 * 如果数量小于等于0，则要求重新扫码 if (quantity == 0 || quantity < 0) { new
	 * MessageBox("商品数量输入无效,请重新输入"); return false; }
	 * 
	 * // 指定小票退货 if (isSpecifyBack()) { if ((thSaleGoods == null) ||
	 * (thSaleGoods.sl <= 0)) { return false; }
	 * 
	 * // 将原退货数量保存到商品库存，用于改数量时检查退货数量是否超过原小票 goodsDef.kcsl = thSaleGoods.sl;
	 * 
	 * // thSaleGoods.syjh = saleHead.syjh; // 收银机号,主键 thSaleGoods.fphm =
	 * saleHead.fphm; // 小票号,主键 thSaleGoods.rowno = saleGoods.size() + 1; //
	 * 行号,主键 thSaleGoods.yyyh = yyyh; thSaleGoods.yfphm = thFphm;
	 * thSaleGoods.ysyjh = thSyjh;
	 * 
	 * // thSaleGoods.name = goodsDef.name; thSaleGoods.unit = goodsDef.unit;
	 * 
	 * // 重算折扣 thSaleGoods.hjje =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.hjje / thSaleGoods.sl) *
	 * quantity, 2, 1); // 合计金额 thSaleGoods.hyzke =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.hyzke / thSaleGoods.sl) *
	 * quantity, 2, 1); // 会员折扣额(来自会员优惠) thSaleGoods.yhzke =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.yhzke / thSaleGoods.sl) *
	 * quantity, 2, 1); // 优惠折扣额(来自营销优惠) thSaleGoods.lszke =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.lszke / thSaleGoods.sl) *
	 * quantity, 2, 1); // 零时折扣额(来自手工打折) thSaleGoods.lszre =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.lszre / thSaleGoods.sl) *
	 * quantity, 2, 1); // 零时折让额(来自手工打折) thSaleGoods.lszzk =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.lszzk / thSaleGoods.sl) *
	 * quantity, 2, 1); // 零时总品折扣 thSaleGoods.lszzr =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.lszzr / thSaleGoods.sl) *
	 * quantity, 2, 1); // 零时总品折让 thSaleGoods.plzke =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.plzke / thSaleGoods.sl) *
	 * quantity, 2, 1); // 批量折扣 thSaleGoods.zszke =
	 * ManipulatePrecision.doubleConvert((thSaleGoods.zszke / thSaleGoods.sl) *
	 * quantity, 2, 1); // 赠送折扣 thSaleGoods.hjzk = getZZK(thSaleGoods);
	 * 
	 * thSaleGoods.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1);
	 * 
	 * // 增加商品明细 addSaleGoodsObject(thSaleGoods, goodsDef,
	 * getGoodsSpareInfo(goodsDef, thSaleGoods)); } else { // 生成商品明细
	 * SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, yyyh, quantity,
	 * price, allprice, dzcm);
	 * 
	 * // 合并商品 if (!HbGoods(saleGoodsDef)) { // 增加商品明细
	 * addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef,
	 * saleGoodsDef)); // 计算商品应收 calcGoodsYsje(saleGoods.size() - 1); } }
	 * 
	 * // 计算小票应收 calcHeadYsje();
	 * 
	 * return true; }
	 */

	protected double doAmount(double ysl, StringBuffer sb)
	{
		double amount;
		try
		{
			// 如果数量大于0累加,小于0覆盖
			if (Convert.toDouble(sb) > 0)
			{
				amount = ysl + Convert.toDouble(sb);
			}
			else
			{
				if (!sb.toString().equals(""))
					new MessageBox("不允许输入负数");
				amount = ysl;
			}

			return amount;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
		if ((this.curGrant.privqx != 'Y') && (this.curGrant.privqx != 'Q'))
		{
			OperUserDef staff = deleteGoodsGrant(index);
			if (staff == null) { return false; }

			String log = "授权删除商品,小票号:" + this.saleHead.fphm + ",商品:" + old_goods.barcode + ",单价:" + old_goods.jg + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log, "708");
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

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (sg.sl <= 0)
			{
				// new MessageBox("盘点数量必须大于0");
				return;
			}

			sg.inputbarcode = goods.inputbarcode;
		}

		if (SellType.ISSALE(saletype))
		{
			if (sg.str6 == null) // str6表示修改过商品数量，就不再找
			{
				addMemoPop(sg, goods, info);

				// 判断是否为会员限购或限惠
				// jsrq =l_gobillno , to_char(l_gogoodspopsj) ,
				// to_char(l_gogoodsxl) ,to_char(l_gousedxl)
				// ,to_char(l_gojffs),l_gvstr1;
				// 商品特供标志Y，(未刷会员卡时，str3为空)
				if (goods != null && goods.str2 != null && goods.str2.equals("Y") && !isMemberVipMode())// (goods.str3==null
				// ||
				// goods.str3.equals("")))
				{
					if (SellType.ISSALE(saletype))
						new MessageBox("该商品为会员专供,您无法购买");
					else if (SellType.ISBACK(saletype))
						new MessageBox("该商品为会员专供,退货请刷会员卡");

					return;
				}

				if (goods != null && goods.str3 != null && goods.str3.length() > 0)
				{
					String[] field = goods.str3.split(",");
					// 1.未刷会员卡，但标志为限购商品时不允许购买
					// 2.若刷会员卡，但数量已完，也不允许购买
					if (SellType.ISSALE(saletype) && field.length > 3)
					{
						// 限购特供
						if (field[5].trim().equals("1"))
						{
							// // 未刷会员卡
							// if (!isMemberVipMode())
							// {
							// new MessageBox("该商品为会员专供,您无法购买");
							// return;
							// }

							// 针对特供商品，若刷会员卡了，但数量已无
							if (goods.str2.equals("Y") && Double.parseDouble(field[2]) - Double.parseDouble(field[3]) <= 0)
							{
								new MessageBox("该商品已达到购买数量[" + String.valueOf(Double.parseDouble(field[3])) + "]上限");
								return;
							}
						}
					}
				}
			}
		}

		super.addSaleGoodsObject(sg, goods, info);
	}

	/*
	 * public boolean doShowInfoFinish() { // 拦截批发，盘点交易不让光标跳行 if
	 * (SellType.ISBATCH(saletype) || SellType.ISCHECKINPUT(saletype)) {
	 * saleEvent.table.setSelection(checkIndex);
	 * saleEvent.table.showSelection(); return true; }
	 * 
	 * if (curLine != -1) { saleEvent.table.setSelection(curLine);
	 * saleEvent.table.showSelection(); }
	 * 
	 * return true; }
	 */
	public boolean isConfirmPrice(boolean isdzcm, double dzcprice, GoodsDef goodsDef)
	{
		if (isSpecifyBack())
			return false;
		if (!isSpecifyBack() && SellType.ISBACK(saletype) && isdzcm && dzcprice == 0 && goodsDef.lsj > 0)
			return false;

		/*
		 * if ((!isSpecifyBack() && SellType.ISBACK(saletype) &&
		 * GlobalInfo.sysPara.setPriceBackStatus == 'N' && goodsDef.lsj > 0))
		 * return false;
		 */

		return true;
	}

	/*
	 * public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods,
	 * SpareInfoDef info) { if (SellType.ISCHECKINPUT(this.saletype)) sg.code =
	 * goods.inputbarcode;
	 * 
	 * super.addSaleGoodsObject(sg, goods, info); }
	 */
	public boolean analyzeBarcode(String barcode, String[] key)
	{
		String[] precode = null;

		if (GlobalInfo.sysPara.prebarcode != null && !GlobalInfo.sysPara.prebarcode.equals("") && GlobalInfo.sysPara.prebarcode.length() > 0)
		{
			precode = GlobalInfo.sysPara.prebarcode.split(",");
			if (precode.length > 0)
			{
				for (int i = 0; i < precode.length; i++)
				{
					if (barcode.startsWith(precode[i]))
						return false;
				}
			}
		}
		return super.analyzeBarcode(barcode, key);
	}

	public String[] getCheckEditType(SaleGoodsDef sg)
	{
		String editflag = "A";
		String editname = "[新增]";
		// 合并的情况 查找是否存在想通编码的商品 如果存在 操作类型为修改
		if (GlobalInfo.sysPara.ischeckadditive == 'N' || GlobalInfo.sysPara.ischeckadditive == 'Y' || GlobalInfo.sysPara.ischeckadditive == 'A')
		{
			String str8 = "";
			String barcode = sg.barcode;
			SaleGoodsDef sg1 = null;
			boolean haveSameGoods = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg1 = (SaleGoodsDef) saleGoods.elementAt(i);
				if (barcode.equals(sg1.barcode))
				{
					if (sg1.str8 != null)
						str8 = sg1.str8;
					haveSameGoods = true;
					break;
				}
			}

			// 已存在该商品，进行合并
			if (haveSameGoods && !"A".equals(str8))
			{
				editflag = "U";
				editname = "[修改]";
			}
			// 不存在该商品
			else
			{
				editname = "";
			}
		}
		return new String[] { editflag, editname };

	}

	public boolean isGoodsAllowCheck(GoodsDef goodsDef)
	{
		if (!goodsDef.gz.equals(this.checkgz))
		{
			new MessageBox("该商品不在所盘点柜组内");
			return false;
		}

		if (goodsDef.managemode != '1' && goodsDef.managemode != '2')
		{
			new MessageBox("该商品不管理库存，禁止盘点");
			return false;
		}

		return true;
	}

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf, boolean iszdxp)
	{
		GoodsDef goodsDef = new GoodsDef();
		int searchFlag = 0;

		String yhsj = null;
		String scsj;

		// 设置查找商品的查找标志,1-超市销售/2-柜台销售检查营业员串柜/3-柜台销售不检查营业员串柜/4赠品
		if (GlobalInfo.syjDef.issryyy == 'N' || yyyh.equals("超市"))
		{
			searchFlag = 1; // 超市
		}
		else if ((GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && gz != null && gz.length() > 0 && !gz.equals("多个柜")) || iszdxp)
		{
			searchFlag = 2; // 控制串柜
		}
		else
		{
			searchFlag = 3; // 不控制串柜
		}

		// 退货时不查找优惠,优惠时间以交易时间为准
		if (SellType.ISBACK(saletype))
		{
			yhsj = "";
		}
		else
		{
			yhsj = saleHead.rqsj;
		}

		// 生鲜商品生产时间
		scsj = convertDzcmScsj(dzcmscsj, isdzcm);

		// 盘点输入不控制串柜输入
		// 中商原因太多，故盘点查找商品与销售查找商品做相同处理
		/*
		 * if (SellType.ISCHECKINPUT(saletype)) { searchFlag = 3; }
		 */

		// 看板销售传入标记9,如何选择了家电发货地点则
		if (jdfhddcode != null && jdfhddcode.length() > 0)
		{
			searchFlag = 9;
			scsj = saleHead.jdfhdd; // scsj标记发货地点
		}

		// 开始查找商品
		int result = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, saletype);
		switch (result)
		{
			case 0:
				break;
			case 4:// 商品存在多柜组
				StringBuffer gzstr = new StringBuffer();
				boolean done = true;

				if (SellType.ISCHECKINPUT(this.saletype))
					gzstr.append(this.checkgz);
				else
					done = new TextBox().open("请输入[" + code.trim() + "]商品的柜组", "柜组号", "该商品有多个柜组，请输入柜组号以便销售", gzstr, 0, 0, false);

				if (!done)
				{
					return null;
				}
				else
				{
					searchFlag = 2;
					int ret = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gzstr.toString(), scsj, yhsj, saletype);
					if (ret == 4)
					{
						new MessageBox("在指定柜组内未找到该商品\n请重新确定柜组是否正确");
						return null;
					}
					else if (ret != 0) { return null; }
				}

				break;

			default:
				return null;
		}

		// 检查营业员串柜情况
		if (GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && curyyygz.length() > 0)
		{
			String[] s = curyyygz.split(",");
			if (s.length > 1)
			{
				int i;
				for (i = 0; i < s.length; i++)
				{
					if (goodsDef.gz.equalsIgnoreCase(s[i]))
						break;
				}
				if (i >= s.length)
				{
					new MessageBox("该商品不是营业柜组范围内的商品\n\n营业员的营业柜组范围是\n" + curyyygz);
					return null;
				}
			}
		}

		// 使用代码销售时检查多单位商品
		if (code.equals(goodsDef.code) && goodsDef.isuid == 'Y') { return getMutiUnitChoice(goodsDef); }

		// 母商品选择子商品进行销售
		if (goodsDef.type == '6') { return getSubGoodsDef(goodsDef); }

		// 判断是否VIP折扣标志设置该单品是否享受VIP折扣
		if (GlobalInfo.sysPara.isHandVIPDiscount == 'A' && !isVIPZK)
		{
			goodsDef.name = "[" + goodsDef.name + "]";
			goodsDef.isvipzk = 'N';
		}

		return goodsDef;
	}

	public GoodsDef getMutiUnitChoice(GoodsDef goodsDef)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			Vector mutiUnit = new Vector();

			if (DataService.getDefault().getGoodsMutiUnit(mutiUnit, goodsDef.code) && mutiUnit.size() > 0)
			{
				String[] title = { "商品条码", "单位", "包装含量" };
				int[] width = { 200, 100, 100 };
				String[] content = null;
				Vector contents = new Vector();
				for (int i = 0; i < mutiUnit.size(); i++)
				{
					GoodsUnitsDef unitDef = (GoodsUnitsDef) mutiUnit.elementAt(i);
					content = new String[3];
					content[0] = unitDef.barcode;
					content[1] = unitDef.unit;
					content[2] = ManipulatePrecision.doubleToString(unitDef.bzhl, 4, 1);
					contents.add(content);
				}
				boolean isOK = false;
				int choice = -1;
				int searchFlag = 2;

				do
				{
					if (contents.size() <= 1 || (contents.size() > 1 && (choice = new MutiSelectForm().open("此商品存在多单位，请确定单位", title, width, contents)) >= 0))
					{
						if (choice < 0)
							choice = 0;
						GoodsUnitsDef unitDef = (GoodsUnitsDef) mutiUnit.elementAt(choice);

						if (!unitDef.uid.equals("00"))
						{
							new MessageBox("此商品不是最小单位,请重新选择");
							continue;
						}

						isOK = true;

						String yhsj = saleHead.rqsj;
						String scsj = "";
						if (saleHead.jdfhdd != null && saleHead.jdfhdd.compareTo("") != 0)
						{
							searchFlag = 9;
							scsj = saleHead.jdfhdd;
						}

						GoodsDef newGoods = new GoodsDef();

						int result = DataService.getDefault().getGoodsDef(newGoods, searchFlag, unitDef.barcode, goodsDef.gz, scsj, yhsj, saletype);
						if (result == 0)
							return newGoods;
						else
							return null;
					}
					else
					{
						new MessageBox("此商品存在多单位，但未选定多单位信息，不能销售");
						return null;
					}
				} while (!isOK);

				return null;
			}
			else
			{
				new MessageBox("此商品存在多单位，但多单位信息未找到，不能销售");
				return null;
			}
		}
		else
			return super.getMutiUnitChoice(goodsDef);
	}
}
