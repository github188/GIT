package custom.localize.Ccsj;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Ccsj_SaleBS extends Bstd_SaleBS
{
	public boolean inputRebate(int index)
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
			//new MessageBox("允许突破最低折扣");
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
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);
		
		// 输入折扣
		String maxzklmsg = "收银员正在对该商品进行打折";
		
		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg =  "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 "+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}
		
		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag == true ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, lszkl, lszkl+100, true))
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
	//	if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	
	}
	public double getConvertRebate(int i, double zkje,double jd)
    {
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
        double je;
        double zk;
        double jgjd;

        if (jd <= 0)
        {
	        if (goodsDef.jgjd == 0)
	        {
	            jgjd = 0.01;
	        }
	        else
	        {
	            jgjd = goodsDef.jgjd;
	        }
        }
	    else
	    {
	    	jgjd = jd;
	    }
	        	
        je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef), 2, 1);

    	je = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(je / jgjd,2,1), 0, 1) * jgjd, 2, 1);
        
        zk = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - je, 2, 1);

        zk = ManipulatePrecision.doubleConvert(zkje + zk, 2, 1);

     //   if (zk < 0)
    //    {
    //        zk = 0;
    //    }

        return zk;
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
			if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?", null, true).verify() == GlobalVar.Key1)
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
				if (sum != 0)
				{
					if (new MessageBox("【" + saleGoodsDef.name + "】存在临时折扣\n你确定要取消此商品的临时折扣吗?", null, true).verify() == GlobalVar.Key1)
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
}
