package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.DosServer.DosSendSaleComReq;

public class Cbbh_Crm_NetService extends Cbbh_Mzk_NetService
{

	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{
		if(this.isUseDosPosServer(commandCode))
		{
			//上传CRM
			return this.sendSaleData_Dos(saleHead, saleGoods, salePayment, retValue, '1');
		}

		try
		{
			//上传到业务
			if(SellType.ISHH(saleHead.djlb))
			{
				Vector saleGoodsHH = new Vector();
				SaleGoodsDef saleGoodsDef = null;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
					saleGoodsDef.flag=(saleGoodsDef.str13 + " ").charAt(0);	//换货标识				
					saleGoodsHH.add(saleGoodsDef);					
				}
				return super.sendSaleData(saleHead, saleGoodsHH, salePayment, retValue, http, commandCode);
			}
			return super.sendSaleData(saleHead, saleGoods, salePayment, retValue, http, commandCode);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 999;
		}
		
	}
	public boolean getSysPara(Http http, boolean done, int ID)
	{
		if(this.isUseDosPosServer(ID))//ID==CmdDosDef.GETCRMPARA)
		{
			//取CRM系统参数
			return this.getSysPara_Dos(done, ID);
		}
		else
		{
			return super.getSysPara(http, done, ID);
		}
	}
	
	//刷会员卡
	public boolean getCustomer(Http h, CustomerDef cust, String track)
	{
		/*
		 压力测试
		ProgressBox pb = null;
		try
		{
			//int okCount=0;
			int errCount=0;
			pb = new ProgressBox();
			//test
			for(int i=1; i<=1000; i++)
			{
		        pb.setText("(" + i + "/" + "1000)正在查询会员卡，失败次数：" + errCount);
		        if(!this.getCustomer_Dos(cust, track))
		        {
		        	errCount++;
		        }		        		
			}
			new MessageBox("完成：失败次数：" + errCount);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		finally
		{
			if (pb != null)
	         {
	             pb.close();
	             pb = null;
	         }
		}*/
		 
		
		return this.getCustomer_Dos(cust, track);
	}
	
	//查找CRM促销
	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, Http http)
	{
		 return this.findPopRuleCRM_Dos(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype);
	}
	 
	//获取小票实时返券
	public boolean getSellRealFQ(String[] row, String mktcode, String syjh, String fphm, Http http)
	{
		return super.getSellRealFQ_Dos(row, mktcode, syjh, fphm);
		
	}

	//返券卡消费
	public boolean sendFjkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if(req!=null && req.type.equalsIgnoreCase("05")) 
		{
			//卡查询操作
			//先通过查会员卡接口，把轨道变为卡号
			CustomerDef cust = new CustomerDef();
			if(req.track2.trim().indexOf("+") != -1 || req.track2.trim().indexOf("/") != -1)
			{
				req.track2 = req.track2.trim().replace('+','=').trim();
				req.track2 = req.track2.trim().replace(';',' ').trim();
				req.track2 = req.track2.trim().replace('/',' ').trim();
			}
//			new MessageBox(req.track2);
			boolean query = getCustomer_Dos(cust, req.track2);
			if(!query)
			{
				return false;
			}
			req.track2 = cust.code;
			return findFjkSale_Dos(req, ret);
		}
		return super.sendFjkSale_Dos(req, ret);
		
	}
	

	/*// 获得退货小票扣回金额
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd, int cmdcode)
	{
		return super.getRefundMoney_Dos(mkt, syjh, fphm, rmd, cmdcode);
		
	}*/
	

	protected Vector getSocketSaleCom(SaleHeadDef saleHead,Vector saleGoods)
	{
		Vector vecCom = new Vector();
		SaleGoodsDef goods;
		DosSendSaleComReq req;
		try
		{
			if(saleGoods==null || saleGoods.size()<=0)
			{
				this.showMsg("转换小票商品明细时失败：商品明细为空");
				return null;
			}
			
			//转换商品明细
			for (int i = 0; i < saleGoods.size(); i++)
			{
				goods = (SaleGoodsDef) saleGoods.elementAt(i);
				req = new DosSendSaleComReq();
				req.yyyh = goods.yyyh;
				req.barcode = goods.barcode;
				req.code = goods.code;
				req.type = goods.type;
				req.gz = goods.gz;
				req.dzxl = goods.catid;
				req.pp = goods.ppcode;
				req.spec = goods.uid;//
				if(req.spec==null || req.spec.length()<=0) req.spec="00";//若没有值，则填00 wangyong update for yuanjun 2014.12.19
				if(req.gz==null || req.gz.length()<=0 || req.gz.equalsIgnoreCase("0000")) req.gz=GlobalInfo.sysPara.mktcode;//若没有值，则填门店号 wangyong update for yuanjun 2014.12.19
				req.batch = goods.batch;
				req.yhdjbh = goods.yhdjbh;
				req.name = goods.name;
				req.unit = goods.unit;
				req.bzhl = goods.bzhl;
				req.sl = goods.sl;
				if (req.sl==0) req.sl=1;//由于ERP便于计算，所以传的0，但会造成CRM上传失败，所以默认为1 wangyong for yuanjun 2014.12.19 
				req.lsj = goods.lsj;
				req.jg = goods.jg;
				req.zje = goods.hjje;
				req.hyzke = goods.hyzke;
				req.yhzke = goods.yhzke;
				req.yhzkfd = 1;//goods.yhzkfd; 由于SAP促销，此字段记录有误，造成CRM小票无法上传，所以与缘俊、小谌商量后将此字段写死为0或1 by 2014.12.18 wangyong
				req.lszke = goods.lszke;
				req.lszre = goods.lszre;
				req.zzke = goods.lszzk;
				req.zzre = goods.lszzr;
				req.plzke = goods.plzke;
				req.zszke = 0;//goods.zszke;//JAVAPOS zszke表示满减，DOSPOS表示其它（暂无）
				req.sqkh = goods.sqkh;
				req.sqktype = goods.sqktype;
				req.pfzkfd = goods.lszkfd;//
				req.spzkfd = goods.spzkfd==0 ? 1:goods.spzkfd;//默认为1 for yuanj BY 2014.3.10
				req.xxtax = goods.xxtax;
				req.flag = goods.flag;
				req.yjhxcode = goods.yjhxcode;
				req.ysyjh = goods.ysyjh;
				req.yfphm = goods.yfphm;
				req.fhdd = goods.str13;//goods.fhdd;换货单中传退换货标记 T退货(换退)商品 S换销商品
								
				req.commemo = "";//未用,或传"0,0" /* 备注：最大收券额,会员卡类型          */
				req.comstr1 = "";//未用				
				req.comstr3 = goods.str3;///*活动规则;商品属性码;满减规则,返券规则,返礼规则(逗号分格);忽略其他积分优惠（1是，0否）;积分倍率;促销档期;会员商品限量促销单;限量标志*/
				/*comstr3=
				 	     活动规则 1010(4位分别填充打折，满减，返券，返礼)2-任选促销/1-存在促销/0-无促销
						商品属性码 :（从商品里取商品属性码）
						满减规则,返券规则,返礼规则(逗号分格) memo
						忽略其他积分优惠（1是，0否）：（long） yhpfzkl
						积分倍率 pfzkfd
						促销档期 yhpfj
						会员商品限量促销单：无(不传）
						限量标志:无(不传）*/
				
				req.comstr9 = "";//未用
				req.comnum1 = 0;//未用
				req.comnum2 = 0;//未用
				req.popje = goods.zszke;//满减的折扣额
				req.zsdjbh = goods.zsdjbh;//满减、满赠（返礼）促销单号
				req.zszkfd = goods.zszkfd;//满减促销折扣分担
				req.comnum4 = 0;//未用
				req.comstr4 = "";//未用
				
				//券分摊，按250长度截取分布到(comstr2+comstr5+6+7+8)
				String str2_Tmp=goods.str2;//记录分摊金额(付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额)
				str2_Tmp = Convert.increaseChar(str2_Tmp, ' ', 250*5);				
				req.comstr2 = str2_Tmp.substring(0,250*1).trim();
				req.comstr5 = str2_Tmp.substring(250*1,250*2).trim();
				req.comstr6 = str2_Tmp.substring(250*2,250*3).trim();
				req.comstr7 = str2_Tmp.substring(250*3,250*4).trim();
				req.comstr8 = str2_Tmp.substring(250*4,250*5).trim();
				
				vecCom.add(req);
			}

			return vecCom;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this.showMsg("转换小票商品明细时异常：" + ex.getMessage());
			return null;
		}
		
	}
}
