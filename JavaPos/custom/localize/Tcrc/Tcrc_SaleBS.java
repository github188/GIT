package custom.localize.Tcrc;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bhls.Bhls_SaleBS;

public class Tcrc_SaleBS extends Bhls_SaleBS {
//	 设置付款金额输入框的缺省值
	public void setMoneyInputDefault(Text txt, PayModeDef paymode)
	{
		if (paymode.type == '1' && paymode.code .equals("01"))
		{
			// 一级主付款方式,允许直接输入付款金额
			txt.setEditable(true);
			double needPay = calcPayBalance();
			txt.setText(ManipulatePrecision.doubleToString(needPay, ManipulatePrecision.getDoubleScale(GlobalInfo.sysPara.lackpayfee), 0));
			txt.selectAll();
		}
		else
		{
			super.setMoneyInputDefault(txt, paymode);
		}
	}
    public boolean getPayModeByNeed(PayModeDef paymode)
    {
        // 无满减的实际付款，所有付款方式都可以
        if (isPreparePay == payNormal)
        {
            return true;
        }

        // 满减预先付款只先付券类付款方式
        if (isPreparePay == payPopPrepare)
        {
            String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

            for (int i = 0; i < pay.length; i++)
            {
                if (paymode.code.equals(pay[i].trim()) || DataService.getDefault().isChildPayMode(paymode.code, pay[i].trim()))
                {
                    return true;
                }
            }
            
            return false;
        }

        // 满减后再付款只允许付非券类付款方式
        // 券类的付款方式必须在满减前输入完成
        if (isPreparePay == payPopOther)
        {
            String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

            for (int i = 0; i < pay.length; i++)
            {
                if (paymode.code.equals(pay[i].trim()))
                {
                    return false;
                }
            }

            return true;
        }

        return true;
    }
    
	public void backToSaleStatus() 
	{ 
		super.backToSaleStatus();
		// 定金签发状态恢复到正常销售状态
		if (SellType.ISPREPARE(saletype) || SellType.ISPREPARETAKE(saletype))
		{
			saletype = SellType.RETAIL_SALE;
		}
	}
	public boolean checkGoodsGrantRange(String gh, String gz)
	{
		Tcrc_DataService dataService = (Tcrc_DataService)(DataService.getDefault());
		if (dataService.FINDGRANT(gh, gz))
		{
			return true;
		}
		
		return false;
	}
	public boolean inputAllRebate()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		//String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			//grantgz = staff.grantgz;
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
			//grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;
		}

		// 计算商品能否打折
		boolean rebate = false;
		String message1 = "工号["+saleHead.sqkh+"]不能对以下商品打折，请先删除或单品授权\n";
		String message2 = "以下商品不能打折，请删除后再整单打折\n";
		int num1 = 0;
		int num2 = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			
			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				rebate = false;
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				rebate = false;
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				rebate = false;
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(saleHead.sqkh, goodsDef.gz))
			{
				num1 = num1 +1;
				message1 = message1+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				rebate = false;
				continue;
			}

			if(num1==0 && num2==0){
				rebate = true;
			}else{
				rebate = false;
			}
			
		}

		if (!rebate)
		{
			if(num1 !=0 ) new MessageBox(message1);
			if(num2 !=0 ) new MessageBox(message2);
			return false;
		}
		
		String maxzzklmsg = "该收银员正在进行整单打折";
		
		// 总折扣计算模式为批量单品折扣模式
		if (GlobalInfo.sysPara.batchtotalrebate == 'Y')
		{
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%";
			}
			
			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)", "整单折扣", maxzzklmsg, buffer,grantzkl * 100, 100, true)) { return false; }

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
				if (!checkGoodsGrantRange(saleHead.sqkh, goodsDef.gz))
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
					new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + "\n\n最多能打折 "
							+ ManipulatePrecision.doubleToString(maxzzkl * 100) + "%");

					//
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
			}
		}
		else 
		{
			// 计算整单最打可打折金额        
			double sumzzk = 0, sumlszzk = 0, lastzzk = 0,hjcjj = 0,hjzke = 0;
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
				if (!checkGoodsGrantRange(saleHead.sqkh, goodsDef.gz))
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
			if (lszkl < 0) lszkl = 0;
			lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

			// 输入折扣
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 "+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
			}
			
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折扣",maxzzklmsg, buffer, lszkl, 100, true)) { return false; }

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
				if (!checkGoodsGrantRange(saleHead.sqkh, goodsDef.gz))
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
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje-saleGoodsDef.hjzk+saleGoodsDef.lszzk) / (hjcjj+sumlszzk) * zzkje, 2, 1);
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
					if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk,getGoodsApportionPrecision());
					
					// 重算商品折扣合计
					getZZK(saleGoodsDef);

					// 计算已分摊的总折让
					hjzzk += saleGoodsDef.lszzk;
				}
			}

			// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
			if (lastzzkrow >=0)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
				if (getZZK(saleGoodsDef) > lastzzk)
				{
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - lastzzk;
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;			
				getZZK(saleGoodsDef);
			}
		}

		// 重算小票应收
		calcHeadYsje();

		return true;
	}
	public OperUserDef inputRebateGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null) return null;
		if (staff.dpzkl * 100 >= 100)
		{
			new MessageBox("该员工授权卡无法授权单品打折");
			return null;
		}
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if (!checkGoodsGrantRange(staff.gh ,goodsDef.gz))
		{
			new MessageBox("该商品不在员工授权卡授权范围内");
			return null;
		}
		return staff;
	}
	
	//输入总折让
	public boolean inputAllRebatePrice()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null) return false;

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
		double sumzzr = 0, summxzzr = 0,sumlszzr = 0, lastzre = 0,hjzke = 0;
		int lastzrerow = -1;
		String message1 = "工号["+saleHead.sqkh+"]不能对以下商品打折，请先删除或单品授权\n";
		String message2 = "以下商品不能打折，请删除后再整单打折\n";
		int num1 = 0;
		int num2 = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
			
			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(saleHead.sqkh, goodsDef.gz))
			{
				num1 = num1 +1;
				message1 = message1+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
				continue;
			}

			// 累计可折让金额
			summxzzr += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
			sumlszzr += saleGoodsDef.lszzr;
			hjzke += saleGoodsDef.hjzk;
			
			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				num2 = num2 +1;
				message2 = message2+"["+(i+1)+"]"+saleGoodsDef.name+"\n";
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
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)) , 2, 1);
			}
			else
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszzr)) , 2, 1);
			}
			sumzzr += maxzzr;
			if (maxzzr > lastzre)
			{
				lastzre = maxzzr;
				lastzrerow = i;
			}
		}
		
		if(num1!=0 || num2!=0){
			if(num1 !=0 ) new MessageBox(message1);
			if(num2 !=0 ) new MessageBox(message2);
			return false;
		}
		
			
		if (summxzzr <= 0)
		{
			new MessageBox("整单没有可打折的商品，不能手工折扣");
			return false;
		}

		// 输入折让
		double zzrje = 0;
		
		String maxzzrmsg = "该收银员正在进行整单折让";
		
		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszzr = saleHead.hjzje - summxzzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对整单成交价最多只能折让到 "+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入整单折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让", maxzzrmsg, buffer, lszzr, saleHead.hjzje, true)) { return false; }

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
			if (lszzr < 0) lszzr = 0;
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限范围内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对权限范围内商品最多还可以折让 "+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入整单要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让", maxzzrmsg, buffer, 0, lszzr, true)) { return false; }
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

			//new MessageBox(saleGoodsDef.flag +" "+saleGoodsDef.type);
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
				//new MessageBox(maxzkl +" "+(saleGoodsDef.hjje - saleGoodsDef.hjzk) + " "+sumzzr+" "+zzrje);
				double maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
				if (getZZK(saleGoodsDef) > maxzzr)
				{
					saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
					saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
				}
				if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr);
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr,getGoodsApportionPrecision());
				
				//new MessageBox(saleGoodsDef.name +"  "+saleGoodsDef.lszzr+" "+maxzkl);
				// 重算商品折扣合计
				getZZK(saleGoodsDef);

				// 计算已分摊的总折让
				hjzzr += saleGoodsDef.lszzr;
			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzrerow >=0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzrerow);
			saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(zzrje - hjzzr, 2, 1);
			//BUG lastzre是
			if (saleGoodsDef.lszzr > lastzre)
			//if (getZZK(saleGoodsDef) > lastzre)
			{
				saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - lastzre;
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
			}
			if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
			getZZK(saleGoodsDef);
		}
		
		// 重算小票应收
		calcHeadYsje();

		return true;
	}
}
