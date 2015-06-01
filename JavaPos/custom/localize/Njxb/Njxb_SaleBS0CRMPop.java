package custom.localize.Njxb;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.SalePayForm;


import custom.localize.Cmls.Cmls_SaleBS;

public class Njxb_SaleBS0CRMPop extends Cmls_SaleBS
{
	boolean havePaymode = false;
	public boolean sgVIPZK = false;  //用于判断是否打手工会员折扣
	boolean ispaycw = false;         //用于判断是否已经支付除外付款方式
	boolean cxRebate = false;
	
//	阶梯且循环
	 public double caculateSL1(CxRebateDef cx,SaleGoodsDef sgd,double use_sl,String[] zk,int index)
	 {
		 double sumzk = 0;
		 for (int i = 0; i < use_sl;i++)
		 {
			 cx.cursl ++;
			 
			 int n = zk.length - 1;
			 for (; n >=0; n-- )
			 {
				 String rule = zk[n];
				 //new MessageBox(rule);
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));

				 //代表为最后一级，需要循环
				 if (cx.cursl >= sl && n == (zk.length - 1))
				 {
					 cx.cursl = 0;
					 sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1-zkl));
					 break;
				 }
				 
				 if (cx.cursl >= sl)
				 {
					 sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1-zkl));
					 break;
				 }
				 
			 }
		 }
		 
		 return sumzk;
	 }
	 
	 public double caculateSL(CxRebateDef cx,SaleGoodsDef sgd,double use_sl,String[] zk,int index)
	 {
		 if (index >= zk.length) return 0;
		
		 if (index ==0)
		 {
			 int n = 0;
			 for (; n < zk.length; n++ )
			 {
				 String rule = zk[n];
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double sl1 = -1;
				 if ((n + 1)< zk.length)
				 {
					 sl1 = Convert.toDouble(zk[n+1].substring(0,zk[n+1].indexOf(":")));
				 }
				
				 if (sl1 < 0)
				 {
					 
					 double use_sl1 = ManipulatePrecision.doubleConvert(cx.cursl +use_sl - sl);
					 if (use_sl1 <=0)
					 {
						 continue; 
					 }
					 use_sl = use_sl1;
					 index = n;
					 break;
				 }
				 else if (cx.cursl >sl && cx.cursl >= sl1)
				 {
					 
					 continue;
				 }
				 else if (cx.cursl >=sl && cx.cursl < sl1)
				 {
					 if (sl1 - cx.cursl <= use_sl) index = n + 1;
					 else index = n;
					 break;
				 }
				 else if (ManipulatePrecision.doubleConvert(cx.cursl +use_sl) >= sl1 && ManipulatePrecision.doubleConvert(cx.cursl +use_sl) > sl )
				 {
					 index = n;
					 break;
					 
				 }
				 else if (ManipulatePrecision.doubleConvert(cx.cursl +use_sl) >= sl )
				 {
					 index = n;
					 break;
				 }
			 }
			 
			 if (n >= zk.length) 
			 {
				 cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl +use_sl);
				 return 0;
			 }
		 }
		 
		 String rule = zk[index];
		 
		 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
		 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));
		 
		 boolean done_b = false;
		 if ((index + 1)< zk.length)
		 {
			 double sl1 = Convert.toDouble(zk[index+1].substring(0,zk[index+1].indexOf(":")));
			 if (cx.cursl >= sl1)  done_b  = true;
		 }
		 

		 //证明有下一及
		 if ((index + 1)< zk.length)
		 {
			 String rule1 = zk[index+1];
			 double sl1 = Convert.toDouble(rule1.substring(0,rule1.indexOf(":")));
			 
			 //本级需要计算的数量
			 double x_sl = (cx.cursl+use_sl - sl1);
			 if (x_sl <0) 
			 {
				 cx.cursl =  ManipulatePrecision.doubleConvert(cx.cursl +use_sl);
				 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * (use_sl) * (1 - zkl));
				 return zkje;
			 }
			 else
			 {
				 x_sl = ManipulatePrecision.doubleConvert(sl1 - sl);
			 }
			 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * x_sl * (1 - zkl));
			 
			 //当前已经计算的数量
			 cx.cursl =  ManipulatePrecision.doubleConvert(cx.cursl +x_sl);
			 //当前未计算的数量
			 double y_sl = ManipulatePrecision.doubleConvert(use_sl - x_sl);
			 return zkje + caculateSL(cx,sgd,y_sl,zk,(index+1));
		 }
		 else //没有下一集，用本级计算
		 {
			 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * use_sl * (1 - zkl));
			 return zkje;
		 }
	 }
	 
	public boolean calcCXRebate()
	 {
		 // 价随量变的促销
		 Vector group = new Vector();
		 // 先进行分组
		 for (int i = 0 ; i < goodsSpare.size(); i++)
		 {
			 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			 if (sid.Zklist == null || sid.Zklist.length() <=0 || sid.Zklist.equals("0"))
			 {
				 continue;
			 }
			 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			 GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			 int n = 0;
			 for (;n < group.size(); n++)
			 {
				 CxRebateDef cx = (CxRebateDef) group.elementAt(n);
				 String condition  = null;
				 if (sid.Zklist.indexOf("1:") == 0) condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 else condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 //？？ cx.seq.equals(sid.seq) 不相同
				 if (cx.addrule.equals(sid.addrule) && cx.Zklist.equals(condition)  && cx.pmbillno.equals(sid.pmbillno) && cx.bz.equals(sid.bz))
				 {
					 boolean cond = true;
					 if (cx.addrule.length() > 0 && cx.addrule.charAt(0) == 'Y')
					 {
						 cond = cond && cx.gys.equals(goodsDef.str1);
					 }
					 
					 if (cx.addrule.length() > 1 && cx.addrule.charAt(1) == 'Y')
					 {
						 cond = cond && cx.gz.equals(goodsDef.gz);
					 }
					 
					 if (cx.addrule.length() > 2 && cx.addrule.charAt(2) == 'Y')
					 {
						 cond = cond && cx.pp.equals(goodsDef.ppcode);
					 }
					 
					 if (cx.addrule.length() > 3 && cx.addrule.charAt(3) == 'Y')
					 {
						 cond = cond && cx.catid.equals(goodsDef.catid);
					 }
					 
					 if (cx.addrule.length() > 4 && cx.addrule.charAt(4) == 'Y')
					 {
						 cond = cond && cx.code.equals(goodsDef.code);
					 }
					 
					 if (cond)
					 {
						 cx.zsl = ManipulatePrecision.doubleConvert(cx.zsl + saleGoodsDef.sl);
						 cx.list.add(String.valueOf(i));
						 break;
					 }
				 }
			 }
			 
			 if (n >=  group.size())
			 {
				 CxRebateDef cx = new CxRebateDef();
				 cx.pmbillno = sid.pmbillno;/*促销单号*/
				 cx.addrule = sid.addrule;/*累计规则 'YYYYY'*/
				 if (sid.Zklist.indexOf("1:") == 0) cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 else cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 cx.pmrule = sid.pmrule;/*价随量变规则*/
				 cx.etzkmode2 = sid.etzkmode2;/*1-阶梯折扣 2-统一打折*/
				 cx.seq = sid.seq; /*规则序号*/
				 cx.zkfd = Convert.toDouble(sid.zkfd);
				 cx.bz = sid.bz;
				 cx.maxnum = sid.maxnum;
				 
				 cx.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				 cx.list.add(String.valueOf(i));
				 
				 cx.gys = goodsDef.str1;
				 cx.gz  = goodsDef.gz;
				 cx.pp = goodsDef.ppcode;
				 cx.catid = goodsDef.catid;
				 cx.code = goodsDef.code;
				 
				 group.add(cx);
			 }
		 }
		 
		 if (group.size() <=0) return false;
		 //检查促销生效
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 
			 //new MessageBox("开始检查促销生效信息"+cx.Zklist+" "+i);

			 
			 String zklist = cx.Zklist;
			 String[] zk = zklist.split("\\|");
			 int n = zk.length - 1;
			 for (; n >=0; n--)
			 {
				 String rule = zk[n];
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));
				 if (cx.zsl < sl)
				 {
					 continue;
				 }
				 else
				 {
					 cx.sl_cond = sl;
					 cx.zkl_result = zkl;
					 break;
				 }
			 }
			 
			 if (n < 0)
			 {
				 group.removeElementAt(i);
				 i--;
			 }
		 }
		 
		 boolean done = false;
		 //开始计算促销
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 
			 String zkcheck1 = cx.Zklist;
			 String[] zkcheck2 = zkcheck1.split("\\|");
			 if (zkcheck2[0] != null)
			 {
				 double sl1 = Convert.toDouble(zkcheck2[0].substring(0,zkcheck2[0].indexOf(":")));
				 //new MessageBox(String.valueOf(sl1));
				 if (sl1 > 1)
				 {
					 cx.Zklist = "1:1|"+cx.Zklist;
				 }
				 //new MessageBox(cx.Zklist);
			 }
			 
			 for (int x = 0; x < cx.list.size(); x++)
			 {
				 int index = Convert.toInt(cx.list.elementAt(x));
				 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(index);
				 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				 double yhzke = 0;
				 //new MessageBox("计算促销 "+Convert.toInt(cx.etzkmode2)+" "+cx.bz);
				 //  不循环且为阶梯折扣时
				 if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("N"))
				 {
					 yhzke = caculateSL(cx,saleGoodsDef,saleGoodsDef.sl,cx.Zklist.split("\\|"),0); 
				 }
				 else if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("Y"))
				 {
					 yhzke = caculateSL1(cx,saleGoodsDef,saleGoodsDef.sl,cx.Zklist.split("\\|"),0); 
					 //new MessageBox("done");
				 }
				 else if (Convert.toInt(cx.etzkmode2) == 2 && cx.bz.trim().equals("Y"))
				 {
					 if (cx.cursl + saleGoodsDef.sl > cx.maxnum)
					 {
						 double num1 = ManipulatePrecision.doubleConvert(cx.maxnum - cx.cursl);
						 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg *(num1) * (1 - cx.zkl_result));
						 cx.cursl = cx.maxnum;
					 }
					 else
					 {
						 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
						 cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + saleGoodsDef.sl);
					 }
				 }
				 else
				 {
					 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
				 }
				 
				 if (yhzke >= 0)
				 {
					 
					 saleGoodsDef.hyzke = 0;
					 saleGoodsDef.zszke = 0;
					 saleGoodsDef.lszke = 0;
					 saleGoodsDef.lszre = 0;
					 saleGoodsDef.lszzk = 0;
					 saleGoodsDef.lszzr = 0;
					 saleGoodsDef.yhzke = yhzke;
					 saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					 getZZK(saleGoodsDef);
					 saleGoodsDef.yhzkfd = cx.zkfd;
					 saleGoodsDef.yhdjbh =cx.pmbillno;
					 if (sid.str1 != null)
					 {
						 StringBuffer buf = new StringBuffer(sid.str1.trim());
						 for (int z =0 ; z < buf.length(); z++)
						 {
							 buf.setCharAt(z, '0');
						 }
						 sid.str1 = buf.toString();
						 saleGoodsDef.isvipzk = 'N';
					 }
					
					 done = true;
				 }
			 }
			 
			 
		 }
		 
		 if (done)
		 {
			 	//重算应收
				calcHeadYsje();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				return true;
		 }
		 return false;
		 
		 
		 
		 
	 }
	
	class CxRebateDef
	 {
			public String pmbillno;/*促销单号*/
			public String addrule ;/*累计规则 'YYYYY'*/
			public String Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
			public String pmrule;/*价随量变规则*/
			public String etzkmode2;/*1-阶梯折扣 2-统一打折*/
			public String seq; /*规则序号*/
			public double zkfd;
			public String bz;/*阶梯折扣时  Y/N是否循环   统一折扣时  Y/N是否启用上限数量*/
			public double maxnum; /*上限数量*/
			
			public double zsl;
			
			public double sl_cond; // 满足数量条件
			public double zkl_result;//折扣率
			public double cursl; //计算阶梯折扣时，记录当前数量
			
			//供应商 柜组 品牌 类别 商品
			public String gys;
			public String gz;
			public String pp;
			public String catid;
			public String code;
			
			Vector list = new Vector();
	 };
	public boolean doCrmPop()
	{
		boolean haveCrmPop = false;

		//清空，放满减描述
		saleHead.str2 = "";

		// 默认总是不进行分摊付款的
		apportionPay = false;

		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 先进行直接打折
		int i = 0;
		if(!sgVIPZK){
			double hjzszk = 0;
			for (i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

				String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
				double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

				// 不计算换购商品
				if (((SpareInfoDef) goodsSpare.elementAt(i)).char2 == 'Y') continue;

				if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);

				if (mjrule.charAt(0) == '1')
				{
					double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
					double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

					double minje = saleGoodsDef.hjje * zkl;

					if (dz < minje)
					{
						saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
						saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
						saleGoodsDef.zsdjbh = goodsPop1.djbh;
					}
					else
					{
						saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
						saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
						saleGoodsDef.zsdjbh = goodsPop1.djbh;
					}
					// 计算价格精度
					if (saleGoodsDef.zszke > 0) saleGoodsDef.zszke = getConvertRebate(i, saleGoodsDef.zszke);

					getZZK(saleGoodsDef);
					hjzszk += saleGoodsDef.zszke;

					haveCrmPop = true;
				}
			}

			if (hjzszk > 0)
			{
				// 重算应收
				calcHeadYsje();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();

				new MessageBox("有商品参加活动促销，总共可打折 " + ManipulatePrecision.doubleToString(hjzszk));
			}

			// 在VIP促销需要除券计算模式下，计算VIP前先提示输入券付款
			//boolean vippaycw = false;
			if (GlobalInfo.sysPara.vipPayExcp == 'Y' && checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y'
					&& GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
			{
				// 提示先输入券付款
				if (new MessageBox("券付款不参与VIP折扣,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
				{
					// 开始预付除外付款方式
					isPreparePay = payPopPrepare;

					// 打开付款窗口
					new SalePayForm().open(saleEvent.saleBS);

					// 付款完成，开始新交易
					if (this.saleFinish)
					{
						sellFinishComplete();

						// 预先付款就已足够,不再继续后续付款
						doRulePopExit = true;
						return false; // 表示没有满减促销,取消付款时无需恢复
					}
				}

				// 进入实付剩余付款方式,只允许非券付款方式进行付款
				isPreparePay = payPopOther;

				// 标记已输入除外付款，后面满减时不再输入除外付款
				//vippaycw = true;
				ispaycw = true;
			}

			// 如果为VIP折扣区间的打折方式，在满减前计算    	
			if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null
					&& GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
			{
				// vipzk2表示按下付款键时才计算VIP折扣
				for (int k = 0; k < saleGoods.size(); k++)
				{
					getVIPZK(k, vipzk2);
				}

				// 重算小票应收  
				calcHeadYsje();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();

				//显示会员卡折扣总金额
				//if (saleHead.hyzke > 0) new MessageBox("会员折扣总金额 ：" + saleHead.hyzke);
				if (saleHead.hyzke > 0){
					if(new MessageBox("会员折扣总金额 ：" + saleHead.hyzke +"\n 是否修改会员折扣?",null,true).verify() == GlobalVar.Key1){
						sgVIPZK = true;
						return false;
					}
				}
				
			}
		}
		sgVIPZK = false;
		
		// 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			double zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

			if (zkl < goodsPop1.pophyjzkfd)
			{

				if (mjrule.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(mjrule);
					for (int z = 2; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					mjrule = buff.toString();
				}
				else
				{
					mjrule = mjrule.charAt(0) + "000";
				}
				((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;

				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') saleGoodsDef.str3 = mjrule
						+ String.valueOf(Convert.increaseInt(goodsPop1.yhspace, 5).substring(4))
						+ saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
				else saleGoodsDef.str3 = mjrule + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
			}
		}

		// 检查是否需要分摊
		for (int j = 0; j < saleGoods.size(); j++)
		{
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			//GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			//String[] rules = goodsPop1.memo.split(",");
			if (mjrule.charAt(0) == '9' && mjrule.length() > 3 && mjrule.charAt(2) == '1') apportionPay = true;
			if (mjrule.charAt(1) == '1') apportionPay = true;
			if (apportionPay)
			{
				break;
			}
		}

		// 再查找是否存在满减或减现
		int j = 0;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		// 先按商品分组促销规则
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			String ruleCode = goodsDef.specinfo;

			if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);
			// 选择了不参与减现继续下一个商品
			if (mjrule.equals("N") || (mjrule.charAt(1) != '1'))
			{
				continue;
			}

			// 查找是否相同促销规则
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				int oldIndex = Integer.parseInt((String) calPop.row_set.elementAt(0));
				SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(oldIndex);
				GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant.elementAt(oldIndex));
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(oldIndex);
				String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

				if (mjrule1.charAt(0) == '9') mjrule1 = mjrule1.substring(1);
				// 判断是否为同规则促销
				if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule, saleGoodsDef1, goodsDef1, goodsPop1, mjrule1))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = saleGoodsDef.code;
				calPop.gz = saleGoodsDef.gz;
				calPop.uid = saleGoodsDef.uid;
				calPop.rulecode = ruleCode;
				calPop.catid = saleGoodsDef.catid;
				calPop.ppcode = saleGoodsDef.ppcode;
				calPop.popDef = goodsPop;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 无规则促销
		if (set.size() <= 0) { return haveCrmPop; }

		// 满减前先对所有商品进行舍分处理
		this.calcSellPayMoney(true);

		// 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
		rulePopSet = set;

		// 检查是否要除券
		boolean havepaycw = false;
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			if (calPop.popDef.catid.equals("Y"))
			{
				/*            	
				 if (set.size() >= 2)
				 {
				 new MessageBox("本笔交易存在不同的活动促销\n\n请分单进行收银");
				 doRulePopExit = true;
				 return false;
				 }
				 if (calPop.row_set.size() != saleGoods.size())
				 {
				 new MessageBox("本笔交易部分商品参与活动促销,部分不参与\n\n请分单进行收银");
				 doRulePopExit = true;
				 return false;
				 }       
				 */
				havepaycw = true;
				break;
			}
		}

		// 前面已经进行了VIP除外付款输入,不再输入除外付款
		if (ispaycw) havepaycw = false;

		//int cxgz = set.size();
		// 循环两次
		// 第一次先检查是否有满足条件的规则,如果没有则直接返回
		// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
		int nwhile = 1;
		do
		{
			// 开始计算商品分组参与计算的合计金额
			for (i = 0; i < set.size(); i++)
			{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if ((nwhile >= 2) && havepaycw)
				{
					// 提示先输入券付款
					if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0
							&& new MessageBox("本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
					{
						// 开始预付除外付款方式
						isPreparePay = payPopPrepare;

						// 打开付款窗口
						new SalePayForm().open(saleEvent.saleBS);

						// 付款完成，开始新交易
						if (this.saleFinish)
						{
							sellFinishComplete();

							// 预先付款就已足够,不再继续后续付款
							doRulePopExit = true;
							return false;
						}
					}

					// 进入实付剩余付款方式,只允许非券付款方式进行付款
					isPreparePay = payPopOther;

					// 券除外付款只输入一次
					havepaycw = false;
				}

				// 计算同规则商品参与促销的合计
				calPop = (CalcRulePopDef) set.elementAt(i);
				double sphj = 0;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					sphj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef));
				}

				// 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
				// 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
				/**
				 if (cxgz <= 1)
				 {
				 double cwpayje = 0;
				 for (j=0;j<salePayment.size();j++)
				 {
				 SalePayDef pay = (SalePayDef)salePayment.elementAt(j);
				 cwpayje += pay.je;
				 }
				 cwpayje -= salezlexception;
				 sphj = ManipulatePrecision.doubleConvert(sphj - cwpayje,2,1);
				 }
				 else
				 {*/
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					if (spinfo.payft == null) continue;
					for (int n = 0; n < spinfo.payft.size(); n++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(n);

						if (!calPop.popDef.catid.equals("Y"))
						{
							String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
							int x = 0;
							for (x = 0; x < pay.length; x++)
							{
								if (s[1].equals(pay[x].trim()))
								{
									break;
								}
							}

							if (x >= pay.length) sphj -= Convert.toDouble(s[3]);
						}
						else
						{
							sphj -= Convert.toDouble(s[3]);
						}
					}
				}

				if (sphj <= 0)
				{
					set.remove(i);
					i--;
					continue;
				}

				// 满减限额
				double limitje = 0;
				if (calPop.popDef.sl <= 0) limitje = 99999999;
				else limitje = calPop.popDef.sl;

				// 检查是否满足条件
				if (calPop.popDef.gz.equals("1")) // 按金额满减
				{
					double mjje = 0;
					calPop.popje = sphj;

					int num = 0;

					// 已参与满减的金额
					double yfmj = 0;

					if (GlobalInfo.sysPara.mjtype == 'Y')
					{
						//检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔 
						if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0))
						{
							String[] row = calPop.popDef.str3.split(";");

							for (int c = row.length - 1; c >= 0; c--)
							{
								if ((row[c] == null) || (row[c].split(",").length != 4))
								{
									continue;
								}

								double a = Convert.toDouble(row[c].split(",")[0]); //参加下限
								double b = Convert.toDouble(row[c].split(",")[1]); //参加上限
								double t = Convert.toDouble(row[c].split(",")[2]); //满减条件
								double je = Convert.toDouble(row[c].split(",")[3]); //满减金额

								if ((je == 0) || (b == 0))
								{
									continue;
								}

								if ((ManipulatePrecision.doubleConvert(sphj - yfmj) >= a) && (ManipulatePrecision.doubleConvert(sphj - yfmj) <= b))
								{
									// 如果满减条件为0，直接取定义的满减金额
									if (t == 0)
									{
										if (je < limitje)
										{
											mjje = je;
										}
										else
										{
											mjje = limitje;
										}

										break;
									}

									//浮点运算1       = 0.999999,需要进位到两位小数再取整
									//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
									num = ManipulatePrecision.integerDiv(sphj - yfmj, t);

									double bcje = 0;
									if (num > 0)
									{
										bcje = num * je;
									}

									if (bcje > limitje)
									{
										bcje = limitje;
									}

									mjje += bcje;
									yfmj = ManipulatePrecision.doubleConvert(num * t + yfmj);
									if (mjje >= limitje || (GlobalInfo.sysPara.mjloop == 'N' && yfmj > 0))
									{
										break;
									}
									else
									{
										continue;
									}
								}
								else
								{
									continue;
								}
							}
						}
					}
					else
					{
						if (calPop.popDef.poplsj > 0)
						{
							//浮点运算1       = 0.999999,需要进位到两位小数再取整
							//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
							num = ManipulatePrecision.integerDiv(sphj - yfmj, calPop.popDef.poplsj);
						}

						// 满足促销条件，不超过满减限额，不超过参与打折的金额
						double bcje = num * calPop.popDef.pophyj;
						if (bcje + mjje > limitje) bcje = limitje - mjje;
						if (bcje > 0 && (bcje + mjje <= calPop.popje))
						{
							mjje += bcje;
							yfmj += num * calPop.popDef.poplsj;
						}

						// 检查是否存在附加促销条件
						// 允许递归计算满减
						if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
						{

						}
						else if (calPop.popDef.str3 != null && calPop.popDef.str3.trim().length() > 0)
						{
							String[] row = calPop.popDef.str3.split(";");

							for (int c = 0; c < row.length; c++)
							{
								if (row[c] == null || row[c].split(",").length != 2) continue;

								double a = Convert.toDouble(row[c].split(",")[0]); //满减条件
								double b = Convert.toDouble(row[c].split(",")[1]); //满减金额

								if (a == 0 || b == 0) continue;

								//浮点运算1       = 0.999999,需要进位到两位小数再取整
								//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								num = ManipulatePrecision.integerDiv(sphj - yfmj, a);

								// 满足促销条件，不超过满减限额，不超过参与打折的金额
								bcje = num * b;
								if (bcje + mjje > limitje) bcje = limitje - mjje;
								if (bcje > 0 && (bcje + mjje <= calPop.popje))
								{
									mjje += bcje;
									yfmj += num * a;
								}

								if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
								{
									break;
								}
							}
						}
					}

					if (mjje > 0)
					{
						calPop.mult_Amount = mjje;
					}
					else
					{
						set.remove(i);
						i--;
					}
				}
				else if (calPop.popDef.gz.equals("2")) // 按百分比减现
				{
					// 无效的减现比例
					if ((calPop.popDef.poplsjzkl <= 0) || (calPop.popDef.poplsjzkl >= 1) || (sphj * calPop.popDef.poplsjzkl > limitje))
					{
						set.remove(i);
						i--;
					}
					else
					{
						calPop.popje = sphj;
					}
				}
				else
				{
					set.remove(i);
					i--;
				}
			}

			// 无有效的、满足条件的规则促销
			if (set.size() <= 0) { return haveCrmPop; }

			// 循环计数,如果不需要除券,则不用进行第二次循环
			nwhile++;
			if (!havepaycw) nwhile++;
		} while (nwhile <= 2);

		String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

		boolean exsit = false;
		for (int jj = 0; jj < salePayment.size(); jj++)
		{
			SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
			for (int ii = 0; ii < pay.length; ii++)
			{

				if (spay.paycode.equals(pay[ii].trim()))
				{
					exsit = true;
					break;
				}
			}

			if (exsit) break;
		}

		// 满减和收券选其一时
		if (!(GlobalInfo.sysPara.ismj == 'Y' && exsit))
		{
			// str2记录规则串描述供小票打印
			saleHead.str2 = "";

			// 分摊满减折扣金额
			for (i = 0; i < set.size(); i++)
			{
				calPop = (CalcRulePopDef) set.elementAt(i);
				double je = 0;
				double hj = 0;

				// 按金额满减
				if (calPop.popDef.gz.equals("1"))
				{
					je = calPop.mult_Amount;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
					new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 "
							+ ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 按百分比减现
				if (calPop.popDef.gz.equals("2"))
				{
					je = calPop.popje * calPop.popDef.poplsjzkl;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + (String) calPop.row_set.elementAt(x);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
					new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 "
							+ ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 记录规则促销单据信息
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					hj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));
					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
					saleGoodsDef.zszkfd = popDef.poplsjzkfd;
				}

				// 分摊满减折扣到各商品
				double yfd = 0;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					double lszszk = 0;

					if (popDef.str5.equals("B"))
					{
						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
						}
						else
						{
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
						}
						
						if (lszszk <= 0) continue;

						if (!createMDPayment(lszszk))
						{
							havePaymode = true;
							return false;
						}

						// liwj add
						SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
						String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(lszszk) };
						if (spinfo.payft == null) spinfo.payft = new Vector();
						spinfo.payft.add(s);
					}
					else if (popDef.str5.equals("A"))
					{
						//						把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度

						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
							saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke + lszszk, 2, 1);
						}
						else
						{
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
							double oldzszke = saleGoodsDef.zszke;
							saleGoodsDef.zszke += lszszk;
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke);
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke,
																	getGoodsApportionPrecision());
							lszszk = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke - oldzszke, 2, 1);
						}
						getZZK(saleGoodsDef);
					}

					// 计算已分摊的金额
					yfd += lszszk;
				}
			}

			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			// 提示收银员查看满减结果
			//new MessageBox("请核对促销活动的相关折扣金额!");

			//
			haveCrmPop = true;
		}
		return haveCrmPop;

	}

	protected boolean createMDPayment(double lszszk)
	{
		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		if (mdCode[0].trim().equals(""))
		{
			new MessageBox("满抵参数未定义!\n无法计入满抵");
			return false;
		}
		PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
		if (paymode == null)
		{
			new MessageBox("[" + mdCode[0] + "]" + "付款方式未定义!\n无法计入满抵");
			return false;
		}
		// 付款记账

		//创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
		if (pay == null) return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(String.valueOf(lszszk));

		if (pay.paymode.code.equals(mdCode[0]))
		{
			if (mdCode.length > 1) pay.salepay.idno = mdCode[1];
		}

		// liwj add
		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (sp != null) sp.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(sp);
		payAssistant.add(pay);

		return true;
	}

	public boolean paySellPop()
	{
		if (calcCXRebate())
		{
			cxRebate = true;
		}
		
		// 处理CRM促销
		doRulePopExit = false;

		haveRulePop = doCrmPop();

		if (doRulePopExit) return false; // 不再继续进行付款

		if (sgVIPZK) return false;
		
		if (havePaymode)
		{
			havePaymode = false;
			return false;
		}
		return true;
	}
	
	public void paySellCancel()
	{
		sgVIPZK = false;
		ispaycw = false;
		//paySellCancel_Extend();

		super.paySellCancel();
		
		if (cxRebate)
		{
			cxRebate = false;
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
		}
	}
	
	public void initNewSale()
	{
		sgVIPZK = false;
		ispaycw = false;
		if (crmPop != null)
		{
			crmPop.removeAllElements();
		}
		else
		{
			crmPop = new Vector();
		}

		//
		super.initNewSale();
	}
	
	//手工会员折扣不写入数据
	 public boolean doRulePopWriteData()
	 {
		 if(sgVIPZK) return true;
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
}
