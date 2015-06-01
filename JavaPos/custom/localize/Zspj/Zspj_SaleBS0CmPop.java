package custom.localize.Zspj;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import custom.localize.Bstd.Bstd_SaleBS;

public class Zspj_SaleBS0CmPop extends Bstd_SaleBS
{
	protected String forwardVip = "";
	//protected int checkIndex = -1;

	public void addMemoPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{	
		try
		{
			if(goods == null )
				return ;
			
			goods.str3 ="";
			sg.str1="";
			goods.str4="";
			
			return;
			/*//付款时传入的goods为Null;
			if (goods == null)
				return;
			
			// 会员状态
			String cardno = null;
			String cardtype = null;
			if (curCustomer != null)// && curCustomer.iszk == 'Y'))
			{
				cardno = curCustomer.code;
				cardtype = curCustomer.type;
			}

			GoodsPopDef popDef = new GoodsPopDef();

			// 非促销商品 或者在退货时，不查找促销信息
			((Zspj_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode, saleHead.rqsj, cardno, cardtype, saletype);

			goods.str3 = popDef.jsrq;
			sg.str1 = popDef.jsrq;
			goods.str4 = popDef.mode;*/
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void calcGoodsVIPRebate(int index)
	{
		String[] val = null;
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// ishy='V'、'Y'表示采用会员VIP折扣模式进行会员优惠,商品允许VIP折扣
		if (goodsDef.isvipzk == 'Y' && isMemberVipMode())
		{
			if (goodsDef.str3 == null || goodsDef.str3.equals("") || !forwardVip.equals(curCustomer.code))
			{
				addMemoPop(saleGoodsDef, goodsDef, null);
				forwardVip = curCustomer.code;
			}
			// 获取VIP折扣率定义
			CustomerVipZklDef zklDef = getGoodsVIPZKL(index);

			// 有VIP折扣率
			if (zklDef != null && val == null)
			{
				// jsrq =l_gobillno , to_char(l_gogoodspopsj) ,
				// to_char(l_gogoodsxl) ,to_char(l_gousedxl)
				// ,to_char(l_gojffs),l_gvstr1;
				if (goodsDef.str3 != null && goodsDef.str3.length() > 0)
				{
					val = goodsDef.str3.trim().split(",");
					if (val != null && val.length > 1)
					{
						zklDef.zkmode = '1';// 指定折扣价格（写死）
						zklDef.zkl = Double.parseDouble(val[1]); // 折扣价
					}

					if (val.length > 3)
						zklDef.maxsl = Double.parseDouble(val[2]) - Double.parseDouble(val[3]);// 可购买数量
					if (zklDef.maxsl < 0)
						zklDef.maxsl = 0;
				}

				// 本笔可销售数量
				double sl = saleGoodsDef.sl;

				// 检查会员限量,限量总是按最小单位定义
				if (zklDef.maxslmode != '0' && zklDef.maxsl > 0)
				{
					// 联网检查会员已购买数量
					/*
					 * if (zklDef.maxslmode != '1') { zklDef.maxsl =
					 * NetService.getDefault().findVIPMaxSl("VIP",
					 * curCustomer.code, curCustomer.type, zklDef.seqno,
					 * saleGoodsDef.code, saleGoodsDef.gz, saleGoodsDef.uid); if
					 * (zklDef.maxsl < 0) zklDef.maxsl = 0; }
					 */

					// 计算本笔交易可销售数量
					double zsl = 0;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

						if (i != index && sg.code.equals(saleGoodsDef.code) && sg.gz.equals(saleGoodsDef.gz) && sg.uid.equals(saleGoodsDef.uid) && sg.hyzke > 0)
						{
							zsl += sg.sl;
						}
					}

					if (zsl >= zklDef.maxsl)
					{
						sl = 0;
					}
					else
					{
						// 剩余能够打折的数量
						double sysl = zklDef.maxsl - zsl;

						// 如果是电子秤不进行拆行
						if (saleGoodsDef.flag == '2')
						{
							if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sysl, 4) > 0)
							{
								sl = 0;
							}
							else
							{
								sl = saleGoodsDef.sl;
							}
						}
						else
						{
							sl = Math.min(sysl, saleGoodsDef.sl);
						}
					}

					if (sl < 0)
						sl = 0;
				}

				if (GlobalInfo.sysPara.isVipMaxSlMsg == 'Y')
				{
					if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
					{
						if (zklDef.maxsl > 0)
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + "商品已经超出会员限量: " + ManipulatePrecision.doubleToString(zklDef.maxsl, 4, 1, true) + "\n\n" + "超出的部分以原价进行销售");
						}
						else
						{
							new MessageBox(saleGoodsDef.code + "[" + saleGoodsDef.name + "]\n\n" + "商品已经超出会员限量,当前商品以原价进行销售");
						}
					}
				}

				if (sl <= 0)
					return;

				// 拆分商品行
				if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl, sl, 4) > 0)
				{
					// 特供
					if (val != null && val.length > 4 && val[5] != null && val[5].equals("1"))
					{
						new MessageBox("该商品已超过数量上限,现可购数量[" + sl + "]个");

						saleGoodsDef.sl = sl;
						saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
						double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / saleGoodsDef.sl * sl);
						double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / saleGoodsDef.sl * sl);
						clearGoodsGrantRebate(index);
						saleGoodsDef.lszre = lszre;
						saleGoodsDef.lszke = lszzk;
						saleGoodsDef.str6 = "Y";
						return;
					}
					else
						SplitSaleGoodsRow(index, sl);
				}

				// 特供只控制会员购买数量
				if (val != null && val.length > 4 && val[5] != null && val[5].equals("1"))
					return;

				// 进行折上折
				if (zklDef.iszsz == 'Y' || zklDef.iszsz == 'A')
				{
					boolean iscontinue = true;
					if (zklDef.iszsz == 'A' && zklDef.zkl > 0)
					{
						// 如果已打折扣已经大于该会员最大可以打的折扣,则不进行会员打折
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
						{
							iscontinue = false;
						}
					}

					if (iscontinue)
					{
						// 得到商品目前已打折比率
						double cjjdn = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * zklDef.zklareadn);
						double cjjup = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * zklDef.zklareaup);

						// 计算折扣区间
						double vipzkl = 1;
						if (saleGoodsDef.hjje - getZZK(saleGoodsDef) >= cjjdn && saleGoodsDef.hjje - getZZK(saleGoodsDef) <= cjjup) // 折扣在区间内
						{
							vipzkl = zklDef.inareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef) > cjjup) // 折扣在区间上
						{
							vipzkl = zklDef.upareazkl;
						}
						else if (saleGoodsDef.hjje - getZZK(saleGoodsDef) < cjjdn) // 折扣在区间下
						{
							vipzkl = zklDef.dnareazkl;
						}

						// 根据折扣模式计算折扣
						if (zklDef.zkmode == '1' && saleGoodsDef.jg > vipzkl && vipzkl >= 0)
						{
							// 按指定价格成交
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (saleGoodsDef.jg - vipzkl) * sl, 2) < 0)
							{
								saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - vipzkl) * sl - getZZK(saleGoodsDef), 2, 1);
								setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
							}
						}
						else if (zklDef.zkmode == '3' && vipzkl > 0)
						{
							// 成交价基础上再减价金额,再减不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert((vipzkl * sl), 2, 1);
							if (saleGoodsDef.hjje - (getZZK(saleGoodsDef) + zke) < 0)
								zke = saleGoodsDef.hjje - getZZK(saleGoodsDef);
							saleGoodsDef.hyzke += zke;
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
						else if (1 > vipzkl && vipzkl >= 0)
						{
							// 成交价基础上再打折指定折扣率
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - vipzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)) * (sl / saleGoodsDef.sl), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}

						if (zklDef.iszsz == 'A' && zklDef.zkl > 0)
						{
							// 如果已打折扣已经大于该会员最大可以打的折扣,则不进行会员打折
							if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) > 0)
							{
								saleGoodsDef.hyzke = ManipulatePrecision.sub(saleGoodsDef.hyzke, ManipulatePrecision.doubleConvert(getZZK(saleGoodsDef) - ((1 - zklDef.zkl) * sl * saleGoodsDef.jg), 2, 1));
							}
						}
					}
				}
				else
				{
					if (val != null && val.length > 4 && val[5] != null && val[5].equals("2") && zklDef.maxsl == 0)
						return;

					// 不折上折时，取商品VIP折扣和综合折扣较低者
					if (zklDef.zkmode == '1' && saleGoodsDef.jg > zklDef.zkl && zklDef.zkl >= 0)
					{
						// 指定价格
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (saleGoodsDef.jg - zklDef.zkl) * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 原价和新价的差额记折扣
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((saleGoodsDef.jg - zklDef.zkl) * sl - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);

						}
					}
					else if (zklDef.zkmode == '3' && zklDef.zkl > 0)
					{
						// 指定减价金额
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), zklDef.zkl * sl, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							// 减价金额不能超过商品价值
							double zke = ManipulatePrecision.doubleConvert(zklDef.zkl * sl);
							if (saleGoodsDef.hjje - zke < 0)
								zke = saleGoodsDef.hjje;
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
					else if (zklDef.zkmode == '2' && 1 > zklDef.zkl && zklDef.zkl >= 0)
					{
						// 指定折扣率
						if (ManipulatePrecision.doubleCompare(getZZK(saleGoodsDef), (1 - zklDef.zkl) * sl * saleGoodsDef.jg, 2) < 0)
						{
							// 清空其他折扣,只保留会员折扣
							clearGoodsAllRebate(index);

							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - zklDef.zkl) * sl * saleGoodsDef.jg - getZZK(saleGoodsDef), 2, 1);
							setGoodsVIPRebateInfo(saleGoodsDef, zklDef);
						}
					}
				}
			}
		}
	}
}
