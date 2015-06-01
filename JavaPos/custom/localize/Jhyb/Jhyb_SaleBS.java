package custom.localize.Jhyb;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Cmls.Cmls_SaleBS;

public class Jhyb_SaleBS extends Cmls_SaleBS
{
	public boolean inputNewRebate(int index)
	{
		double grantzkl = 0.0;
		boolean grantflag = false;

		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) this.goodsSpare.elementAt(index);

		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) return false;

		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) return false;

		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		grantzkl = this.curGrant.dpzkl;

		saleGoodsDef.sqkh = this.cursqkh;
		saleGoodsDef.sqktype = this.cursqktype;
		saleGoodsDef.sqkzkfd = this.cursqkzkfd;
		
		grantflag = breachRebateGrant(this.curGrant);
		
		double minzkl  = 0.0;
		if (grantflag)
		{
			minzkl = getMaxRebateGrant(grantzkl, 0.0);
		}
		else
		{
			minzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		double maxzkl = goodsDef.num5;

		saleGoodsDef.lszke = 0.0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1.0 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;

		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1.0 - lszkl) * 100.0, 2, 1);

		String maxzklmsg = "收银员正在对该商品进行打折";

		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg = "该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前最少在成交价基础上再打折 "
					+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, ManipulatePrecision.doubleConvert(minzkl*100), lszkl, true))
		{
			this.saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		grantzkl = Double.parseDouble(buffer.toString());

		saleGoodsDef.lszke = 0.0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100.0 - grantzkl) / 100.0 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);

		if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;

		getZZK(saleGoodsDef);

		calcHeadYsje();

		return true;
	}

	public boolean inputRebate(int index)
	{
		double grantzkl = 0.0;
		boolean grantflag = false;

		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) this.goodsSpare.elementAt(index);
		//goodsDef.num5 = 0.4;
		if ((goodsDef.num5 > 0.0) && (goodsDef.num5 < 1.0))
		{
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) return false;

			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) return false;

			SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

			grantzkl = this.curGrant.dpzkl;

			saleGoodsDef.sqkh = this.cursqkh;
			saleGoodsDef.sqktype = this.cursqktype;
			saleGoodsDef.sqkzkfd = this.cursqkzkfd;
			
			grantflag = breachRebateGrant(this.curGrant);
			
			double minzkl  = 0.0;
			if (grantflag)
			{
				minzkl = getMaxRebateGrant(grantzkl, 0.0);
			}
			else
			{
				minzkl = getMaxRebateGrant(grantzkl, goodsDef);
			}

			double maxzkl = goodsDef.num5;

			saleGoodsDef.lszke = 0.0;
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1.0 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
			if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1))
			{
				saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1);
				saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
			}
			if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;

			double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
			lszkl = ManipulatePrecision.doubleConvert((1.0 - lszkl) * 100.0, 2, 1);

			String maxzklmsg = "收银员正在对该商品进行打折";

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzklmsg = "该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 "
						+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
			}

			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, ManipulatePrecision.doubleConvert(minzkl*100), lszkl, true))
			{
				this.saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			grantzkl = Double.parseDouble(buffer.toString());

			if(grantzkl==0) return true;
			
			saleGoodsDef.lszke = 0.0;
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100.0 - grantzkl) / 100.0 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
			if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;
			getZZK(saleGoodsDef);
			
			calcHeadYsje();

			return true;
		}

		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) return false;

		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) return false;

		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
		
		if ((this.curGrant.dpzkl * 100.0 >= 100.0) || (!checkGoodsGrantRange(goodsDef, this.curGrant.grantgz)))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			String log = "授权单品折扣,小票号:" + this.saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100.0 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			grantzkl = this.curGrant.dpzkl;
			grantflag = breachRebateGrant(this.curGrant);

			saleGoodsDef.sqkh = this.cursqkh;
			saleGoodsDef.sqktype = this.cursqktype;
			saleGoodsDef.sqkzkfd = this.cursqkzkfd;
		}

		double minzkl  = 0.0;
		if (grantflag)
		{
			minzkl = getMaxRebateGrant(grantzkl, 0.0);
		}
		else
		{
			minzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		//minzkl = 0.1;
		double maxzkl = 0.0;
		
		if(goodsDef.num5>0)maxzkl = goodsDef.num5;
		else{
			maxzkl = 1;
		}
		
//		计算最低折扣
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1.0 - minzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - minzkl), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - minzkl), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if ( saleGoodsDef.lszke< 0.0) saleGoodsDef.lszke = 0.0;
		double zdzk = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		zdzk = ManipulatePrecision.doubleConvert((1.0 - zdzk) * 100.0, 2, 1);
		
		saleGoodsDef.lszke = 0.0;
		
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1.0 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1.0 - maxzkl), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		
		if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;
				
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1.0 - lszkl) * 100.0, 2, 1);
		
       
		
		String maxzklmsg = "收银员正在对该商品进行打折";

		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前最少在成交价基础上再打折 "
					+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, zdzk,lszkl , true))
		{
			this.saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		grantzkl = Double.parseDouble(buffer.toString());

		saleGoodsDef.lszke = 0.0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100.0 - grantzkl) / 100.0 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		
		//if (getZZK(saleGoodsDef)> ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1-maxzkl), 2, 1))
		if (getZZK(saleGoodsDef) < ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1-maxzkl), 2, 1))
		{
			//saleGoodsDef.lszke += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1- maxzkl), 2, 1) -getZZK(saleGoodsDef) ;
			
			saleGoodsDef.lszke += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1- maxzkl), 2, 1) -getZZK(saleGoodsDef) ;
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		
		if (saleGoodsDef.lszke < 0.0) saleGoodsDef.lszke = 0.0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		getZZK(saleGoodsDef);

		calcHeadYsje();

		return true;
	}

	public void enterInputCODE()
	{
		if (this.saletype.equals("T1")) return;

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许修改商品状态");
			return;
		}

		boolean findok = false;

		if (this.saleEvent.code.getText().trim().length() > 30)
		{
			new MessageBox("非合法的商品编码不允许进行销售\n当前编码长度" + this.saleEvent.code.getText().length());
			this.saleEvent.code.selectAll();
			return;
		}

		boolean isdel = false;
		if (SellType.ISCHECKINPUT(this.saletype))
		{
			String code = this.saleEvent.code.getText().trim();
			if ((code.length() <= 0) && (this.saleGoods.size() > 0))
			{
				code = ((SaleGoodsDef) this.saleGoods.elementAt(this.saleGoods.size() - 1)).barcode;
			}

			if ((code.length() > 0) && (findCheckGoods(code, this.saleEvent.yyyh.getText(), getGzCode(this.saleEvent.gz.getText()))))
			{
				findok = true;
			}
		}
		else if (SellType.ISCOUPON(this.saletype))
		{
			if (findCoupon(this.saleEvent.code.getText(), this.saleEvent.yyyh.getText(), getGzCode(this.saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJS(this.saletype))
		{
			if (findJSDetail(this.saleEvent.code.getText(), this.saleEvent.yyyh.getText(), getGzCode(this.saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJF(this.saletype))
		{
			if (findJFDetail(this.saleEvent.code.getText(), this.saleEvent.yyyh.getText(), getGzCode(this.saleEvent.gz.getText())))
			{
				findok = true;
			}

		}
		else
		{
			String code = this.saleEvent.code.getText().trim();

			if (((GlobalInfo.sysPara.quickinputsku == 'Y') && (this.saleEvent.yyyh.getText().trim().equals("超市")))
					|| ((ConfigClass.isDeveloperMode()) && (code.length() <= 0) && (this.saleGoods.size() > 0)))
			{
				SaleGoodsDef sg = (SaleGoodsDef) this.saleGoods.elementAt(this.saleGoods.size() - 1);
				if ((sg.inputbarcode != null) && (sg.inputbarcode.length() > 0)) code = sg.inputbarcode;
				else
				{
					code = sg.barcode;
				}
			}
			if ((code.length() > 0) && (findGoods(code, this.saleEvent.yyyh.getText(), getGzCode(this.saleEvent.gz.getText()))))
			{
				GoodsDef gd = new GoodsDef();
				gd = (GoodsDef) this.goodsAssistant.elementAt(this.goodsAssistant.size() - 1);
				//gd.num5 = 0.8;
				
				if ((gd.num5 > 0.0) && (gd.num5 < 1.0)&& !SellType.ISBACK(this.saletype))
				{
					if (!inputNewRebate(this.saleGoods.size() - 1))
					{
						if(!inputNewRebatePrice(this.saleGoods.size() - 1)){
							isdel = true;
						}
						
					}
				}

				findok = true;
			}

		}

		if (findok)
		{
			getSaleGoodsDisplay();

			this.saleEvent.table.setSelection(this.saleEvent.table.getItemCount() - 1);
			if(isdel)this.saleEvent.deleteCurrentGoods();
			this.saleEvent.table.showSelection();

			this.saleEvent.setTotalInfo();

			this.saleEvent.setCurGoodsBigInfo();

			this.saleEvent.code.setText("");

			
			doShowInfoFinish();
			
		}
		else
		{
			this.saleEvent.code.selectAll();
		}
	}

	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if (("S".equals(this.saletype)) || ("M".equals(this.saletype))) return false;
		if (isNewUseSpecifyTicketBack(false)) { return false; }

		if ((GlobalInfo.sysPara.customvsgoods == 'B') && (checkMemberSale()))
		{
			if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?", null, true).verify() == 2)
			{
				this.curCustomer = null;

				this.saleHead.hykh = null;

				for (int i = 0; i < this.saleGoods.size(); i++)
				{
					calcGoodsYsje(i);
				}

				calcHeadYsje();
				this.saleEvent.updateSaleGUI();
			}
			return true;
		}

		if (GlobalInfo.sysPara.FirstClearLsZk == 'Y')
		{
			if ((index >= 0) && (this.saleGoods.size() > index))
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
				GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
				double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr);
				if ((sum > 0.0) && ((goodsDef.num5 <= 0.0D) || (goodsDef.num5 >= 1.0)))
				{
					if (new MessageBox("【" + saleGoodsDef.name + "】存在临时折扣\n你确定要取消此商品的临时折扣吗?", null, true).verify() == 2)
					{
						saleGoodsDef.lszke = 0.0;
						saleGoodsDef.lszre = 0.0;
						saleGoodsDef.lszzk = 0.0;
						saleGoodsDef.lszzr = 0.0;

						calcGoodsYsje(index);
						calcHeadYsje();
						this.saleEvent.updateSaleGUI();
					}
					return true;
				}
			}

		}

		return false;
	}

	public boolean inputRebatePrice(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

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
			//new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		double maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);	
		
		//new MessageBox("最低折让额: "+maxzre+"   "+maxzkl+" "+grantzkl+" "+goodsDef.maxzkl);
		// goodsDef.maxzke为最低限价
		if ((goodsDef.maxzke*saleGoodsDef.sl) <= saleGoodsDef.hjje && 
			saleGoodsDef.hjje - (goodsDef.maxzke*saleGoodsDef.sl) < maxzre) 
		{
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - (goodsDef.maxzke*saleGoodsDef.sl), 2, 1);
		}
		//new MessageBox("最低折让额: "+maxzre+"   goodsDef.maxzke"+goodsDef.maxzke);
		// 输入折让
		String maxzremsg = "收银员对该商品进行折让";
		
		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多只能够折让到 "+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}
			
			double max1 = saleGoodsDef.hjje;
			if(goodsDef.num5>0&&goodsDef.num5<1){
				max1 = ManipulatePrecision.doubleConvert(max1 * goodsDef.num5);
			}
			if (!new TextBox().open("请输入单品折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, lszre, max1, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			//得到折让额	      
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
			if (lszre < 0) lszre = 0;
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多还可以再折让 "+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}
			double min1 = 0;
			if(goodsDef.num5>0 && goodsDef.num5<1){
				min1 = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1-goodsDef.num5));
				//当前存在折扣时，计算还需要折扣多少金额
				min1 = ManipulatePrecision.doubleConvert(min1 - getZZK(saleGoodsDef) + saleGoodsDef.lszre);
				if (min1 <=0) min1 = 0;
			}
			if (!new TextBox().open("请输入单品要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, min1, lszre, true))
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
		
		if (saleGoodsDef.lszre < 0) saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}
	
	public boolean inputNewRebatePrice(int index)
	{
		double grantzkl = 0.0;
		boolean grantflag = false;

		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) this.goodsSpare.elementAt(index);
		//goodsDef.num5 = 0.3;


		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) return false;

		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) return false;

		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		grantzkl = this.curGrant.dpzkl;

		saleGoodsDef.sqkh = this.cursqkh;
		saleGoodsDef.sqktype = this.cursqktype;
		saleGoodsDef.sqkzkfd = this.cursqkzkfd;
		
		
		grantflag = breachRebateGrant(this.curGrant);
		
		double minzkl  = 0.0;
		if (grantflag)
		{
			minzkl = getMaxRebateGrant(grantzkl, 0.0);
		}
		else
		{
			minzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		
		double maxzkl = goodsDef.num5;

		double maxzre = ManipulatePrecision.doubleConvert((1.0 - maxzkl) * saleGoodsDef.hjje, 2, 1);

		if ((goodsDef.maxzke * saleGoodsDef.sl <= saleGoodsDef.hjje) && (saleGoodsDef.hjje - goodsDef.maxzke * saleGoodsDef.sl < maxzre))
		{
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - goodsDef.maxzke * saleGoodsDef.sl, 2, 1);
		}

		String maxzremsg = "收银员对该商品进行折让";

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				//maxzremsg = "该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前对该商品最少能够折让到 "
						//+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
				maxzremsg = "该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前对该商品最多能够折让到 "
				+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品折让后的成交价" + (grantflag ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * minzkl), lszre, true))
			{
				this.saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			lszre = Double.parseDouble(buffer.toString());

			saleGoodsDef.lszke = 0.0;
			saleGoodsDef.lszre = 0.0;
			saleGoodsDef.lszzk = 0.0;
			saleGoodsDef.lszzr = 0.0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);
		}
		else
		{
			double lszre = maxzre - getZZK(saleGoodsDef) + saleGoodsDef.lszre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			if (lszre < 0.0) lszre = 0.0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100.0, 2, 1, true) + "%\n你目前对该商品最少还可以再折让 "
						+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品要折让的金额" + (grantflag ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, lszre, ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1-minzkl)), true))
			{
				this.saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()), 2, 1);
		}

		//if (getZZK(saleGoodsDef) > maxzre)
		if (getZZK(saleGoodsDef) < maxzre)
		{
			saleGoodsDef.lszre += maxzre - getZZK(saleGoodsDef) ;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre, 2, 1);
		}

		if (saleGoodsDef.lszre < 0.0) saleGoodsDef.lszre = 0.0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		getZZK(saleGoodsDef);

		calcHeadYsje();

		return true;
	
	}
}
