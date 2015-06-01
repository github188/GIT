package custom.localize.Bcrm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Bcrm_SaleBS extends Bcrm_SaleBS2JfExchange
{
	// 计算本笔销售积分
	public double calcSaleBCJF()
	{
		return 0;
	}

	protected boolean memberAfterGoodsMode()
	{
		// 先刷会员卡,再输商品
		return false;
	}

	public boolean getCouponFirst(String paycode)
	{
		try
		{
			// 券类
			String[] pay = CreatePayment.getDefault().getCustomPaymentDefine("PaymentCoupon");
			if (pay != null)
			{
				for (int k = 0; k < pay.length; k++)
				{
					if (paycode.equals(pay[k])) { return true; }
				}
			}
			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean memberGrant()
	{
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode() && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡"));
			return false;
		}

		//
		return super.memberGrant();
	}

	public boolean preGetMSinfo()
	{
		if (GlobalInfo.sysPara.ispregetmsinfo == 'N') { return true; }

		if (!GlobalInfo.isOnline) return true;

		// 汇总数据
		if (!saleSummary())
		{
			new MessageBox(Language.apply("预上传小票时交易数据汇总失败!"));
			return false;
		}

		// 预上传小票
		boolean done = true;
		Bcrm_NetService netservice = (Bcrm_NetService) NetService.getDefault();
		char bc = saleHead.bc;
		try
		{
			saleHead.bc = '$';
			int result = netservice.sendSaleData(saleHead, saleGoods, salePayment, null, Bcrm_NetService.getDefault()
																										.getMemCardHttp(CmdDef.PRESENDCRMSELL),
													CmdDef.PRESENDCRMSELL);
			if (result != 0 && result != 2)
			{
				new MessageBox(Language.apply("预上传小票失败，无法获得满赠信息"));
				done = false;
			}
		}
		finally
		{
			saleHead.bc = bc;
		}
		// 查询小票实时赠品信息
		Vector v = new Vector();
		if (done
				&& netservice.getSaleTicketMSInfo(v, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm), "N",
													Bcrm_NetService.getDefault().getMemCardHttp(CmdDef.PREGETMSINFO), CmdDef.PREGETMSINFO))
		{
			if (v.size() < 1) { return true; }

			GiftGoodsDef def = null;

			StringBuffer line = new StringBuffer();

			line.append(Language.apply("本次小票存在返券\n"));
			double je1 = 0;
			for (int i = 0; i < v.size(); i++)
			{
				def = (GiftGoodsDef) v.get(i);

				//数量
				//String sl = String.valueOf(def.sl);

				//金额
				String je = ManipulatePrecision.doubleToString(def.je);
				String[] infos = def.info.split("&");
				line.append(Convert.appendStringSize("", infos[0], 1, 16, 16, 1) + ":" + Convert.appendStringSize("", je, 1, 10, 10, 0) + "\n");
				je1 += def.je;
				/**
				 if (infos != null && infos.length > 1)
				 {
				 line.append(infos[1] + "\n");
				 }*/
				//line.append("赠券金额  :"+Convert.appendStringSize("",je,1,16,16,0) + "\n");
				//line.append("赠券数量  :"+Convert.appendStringSize("",sl,1,16,16,0) + "\n");
				//描述
				/**
				 if (infos != null && infos.length > 0)
				 {
				 line.append("赠券信息  :"+ Convert.appendStringSize("",infos[0],1,16,16,0) + "\n");
				 }*/
				//line.append("----------\n");
			}
			line.append(Convert.increaseChar("-", '-', 27) + "\n");
			line.append(Language.apply("返券总金额为：") + ManipulatePrecision.doubleToString(je1));
			new MessageBox(line.toString());
		}

		return true;
	}

	public boolean payCompleteDoneEvent()
	{
		if (!super.payCompleteDoneEvent()) { return false; }

		if (!preGetMSinfo()) return false;

		return true;
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null) return "";
		else
		{
			if (SellType.ISBACK(saletype) || curCustomer.iszk == 'Y') return "[" + curCustomer.code + "]" + curCustomer.name;
			else return Language.apply("(不折扣)")+"[" + curCustomer.code + "]" + curCustomer.name;
		}
	}
	
    public boolean doRulePopWriteData()
    {
    	FileOutputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Bhlspop.dat";
            
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        
	        // 将交易对象写入对象文件
	        s.writeObject(saleGoods);
	        s.writeObject(goodsSpare);
	        s.writeObject(goodsAssistant);
	        s.writeObject(crmPop);
	        
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	        return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public boolean delRulePopReadData()
    {
    	FileInputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Bhlspop.dat";
            
	        f = new FileInputStream(name);
	        ObjectInputStream s = new ObjectInputStream(f);
	        
	        // 读交易对象
	        Vector saleGoods1 = (Vector) s.readObject();
	        Vector spare1 = (Vector) s.readObject();
	        Vector goodsAssistant1 = (Vector) s.readObject();
	        Vector crmPop1 = (Vector) s.readObject();
			// 赋对象
	    	saleGoods = saleGoods1;
	    	goodsSpare = spare1;
	    	goodsAssistant = goodsAssistant1;
	    	crmPop = crmPop1;
	    	
            // 关闭断点文件
            s.close();
            s = null;
            f.close();
            f = null;

        	return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void custMethod()
    {
		if (!SellType.ISBACK(saletype)) { return; }

		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		if (mdCode[0].trim().equals("")) { return; }
		PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
		if (paymode == null) { return; }

		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(j);
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(j);

			if (sgd.num6 > 0)
			{
				double je = getDetailOverFlow((sgd.num6 * sgd.sl / sgd.ysl), GlobalInfo.syjDef.sswrfs);
				createMDPayment(je);

				SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
				String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(je) };
				if (spinfo.payft == null) spinfo.payft = new Vector();
				spinfo.payft.add(s);
			}
			
		}
		//		 重算应收
		calcHeadYsje();
    }
    
	public void doBrokenData()
	{
		if (GlobalInfo.sysPara.mdcode.split(",")[0].trim().equals("")) { return; }
		SalePayDef sp = null;
		for (int i = salePayment.size() - 1; i > -1; i--)
		{
			sp = (SalePayDef) salePayment.elementAt(i);
			if (sp.paycode.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
			{
				SalePayDef spay = (SalePayDef) salePayment.elementAt(i);
				if ((spay == null)) { return; }

				// 得到该付款的唯一序号
				int seqno = (int) spay.num5;

				// 查找所有商品对应该付款的分摊，并删除
				for (int ii = 0; ii < goodsSpare.size(); ii++)
				{
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(ii);
					if (info == null || info.payft == null) continue;
					for (int j = 0; j < info.payft.size(); j++)
					{
						String[] s = (String[]) info.payft.elementAt(j);
						if (Convert.toInt(s[0]) == seqno)
						{
							info.payft.removeElementAt(j);
							j--;
						}
					}
				}
				
				salePayment.remove(i);
			}
		}
	}
	
	public boolean checkDeleteSalePay(String ax, boolean isDelete)
	{
		String code = "";
		if (ax.trim().indexOf("]") > -1)
		{
			code = ax.substring(1, ax.trim().indexOf("]"));
		}
		else
		{
			code = ax;
		}
		if (code.equals(GlobalInfo.sysPara.mdcode.split(",")[0])) { return true; }
		return false;
	}
	
	public boolean checkIsSalePay(String code)
	{
		if (code.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
		{
			return true;
		}
		else
		{
			return false;
		}
	
	}
}
