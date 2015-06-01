package custom.localize.Jnyz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_NetService;

public class Jnyz_NetService extends Cmls_NetService
{
	public static final int SENDMZKCX = 830; 			//上传面值卡撤销日志
	public static final int GETSALEHEAD = 109;		//获取网上小票头
	public static final int GETSALGOODS = 110;		//获取小票商品明细
	public static final int GETSALEPAY = 111;		//获取小票付款明细
	public static final int GETZFBJE = 112;		//获取支付宝金额
	
	public boolean findPLZK(CxRebateDef cx, String billno)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
							billno,
							String.valueOf(cx.zsl)
							};
		String[] args = {
							"mktcode",
							"jygs",
							"billno",
							"sl"
							};

		try
		{
			head = new CmdHead(CmdDef.BatchRebate);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"zkl"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					cx.zkl_result = Convert.toDouble(row[0]);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean getCustomer(CustomerDef cust, String track)
	{
		return getCustomer(getMemCardHttp(CmdDef.FINDCUSTOMER), cust, track);
	}

	public boolean getCustomer(Http h, CustomerDef cust, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		/*//判断是手动输入还是刷卡模式：1=手动模式，2=刷卡模式
		if(GlobalInfo.sysPara.msrspeed==0){
			cust.str2 = "1";
		}else if(GlobalInfo.sysPara.msrspeed==100){
			cust.str2 ="2";
//			根据不同键盘驱动解析磁道
				if(ConfigClass.KeyBoard1.trim().equals("device.KeyBoard.Wincor_KeyBoard")){
					track = track.substring(7);
				}
				//截取磁道号前面的符号
				if(track.indexOf(";")==0){
					track = track.substring(1);
				}
		}*/
		
		/*//处理卡磁道
		String track2 = track;
		String[] str = track2.split("<1>");
		if(str.length >1)
		{
			track = str[0];
			cust.str3 = str[1];
		}
		else
		{
			track = str[0];
		}*/
		
		String[] values = { "07","0000001",GlobalInfo.syjStatus.syyh,GlobalInfo.syjStatus.syjh,cust.str2,track, cust.str3};
		String[] args = { "operid","operno","syyh","syjh","usemode","track2", "track3" };

		try
		{
			head = new CmdHead(CmdDef.FINDCUSTOMER);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, "找不到该顾客卡信息!");

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		/*if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDMZK);

			
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));
			
			
			result = HttpCall(h, line, "储值卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
		}
	}
	catch (Exception er)
		{
			er.printStackTrace();
		}
		
	return false;*/
		
		
		return super.sendMzkSale(h, req, ret);
	}
	
	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		return super.sendMzkSale(h, req, ret, sendGgkAddress(req,cmdCode));
	}
	
	
	public boolean findHYZK(GoodsPopDef popDef,String code,String custtype,String gz,String catid,String ppcode,String specialInfo,Http http)
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

        CmdHead head = null;
        StringBuffer line = new StringBuffer();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String yhsj = df.format(new Date());;
        int result = -1;
        String[] values = 
                          {
                              code,custtype,GlobalInfo.sysPara.mktcode,gz,catid,ppcode, specialInfo, GlobalInfo.sysPara.jygs,yhsj
                          };
        String[] args = 
                        {
                            "code", "custtype","mktcode", "gz", "catid", "ppcode","specinfo","jygs","yhsj"
                        };

        try
        {
            head = new CmdHead(CmdDef.GETCRMVIPZK);
            line.append(head.headToString() +
                        Transition.SimpleXML(values, args));

            //不显示错误信息
            result = HttpCall(http, line, "");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                                                                   new String[]{"zk", "zkmk", "num1", "num2", "str1", "str2", "memo"});

                if (v.size() > 0)
                {	
                    String[] row = (String[]) v.elementAt(0);
                    
                    popDef.pophyj = Double.parseDouble(row[0]);
                    
                    popDef.num1 = Double.parseDouble(row[1]);
                    
                    popDef.num2 = Double.parseDouble(row[2]);
                    
                    popDef.num3 = Double.parseDouble(row[3]);
                    
                    popDef.num4 = Double.parseDouble(row[4]);
                    
                    popDef.str2 = row[5];
                    
                    popDef.memo = row[6];
                    
                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
	
	public boolean sendFjkSale(MzkRequestDef req, MzkResultDef ret)
	{
		//会员卡电子卷查询，后台过程判断需要传入0500判断
		if(req.paycode=="" || req.paycode.length()<=0){
			req.paycode = "0500";
		}
		
		if(req.paycode.trim().equals("0502"))
		{
			req.track1="2";
		}
		return super.sendFjkSale(getMemCardHttp(CmdDef.SENDFJK), req, ret);
	}
	
//	 会员卡交易
	public boolean sendHykSale(MzkRequestDef req, MzkResultDef ret)
	{
		return sendHykSale(getMemCardHttp(CmdDef.SENDHYK), req, ret);
	}

	public boolean sendHykSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		
//		根据不同键盘驱动解析磁道
//		KeyBoard key = new KeyBoard(ConfigClass.KeyBoard1);
//		req.track3="";
//		if(key.isValid()){
//			String keyName = key.keyboard.getDiscription();
//			if(keyName.indexOf("Wincor键盘") != -1){
//				if(!req.type.equals("01")||req.type.equals("02")){
//					req.track2 = req.track2.substring(7);
//				}
//			}
//			//截取磁道号前面的符号
//			if(req.track2.indexOf(";")==0){
//				req.track2 = req.track2.substring(1);
//			}
//		}
		
			req.track3="";
			if(ConfigClass.KeyBoard1.trim().equals("device.KeyBoard.Wincor_KeyBoard")){
				if(!req.type.equals("01")||req.type.equals("02")){
					req.track2 = req.track2.substring(7);
				}
			}
			//截取磁道号前面的符号
			if(req.track2.indexOf(";")==0){
				req.track2 = req.track2.substring(1);
			}
		
			
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDHYK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, "会员卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}
	
	
	//发送面值卡消费撤销日志
	public boolean sendMzkSaleCx(MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSaleCx(getCardHttp(),req,ret);
	}
	
	public boolean sendMzkSaleCx(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(SENDMZKCX);

			
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));
			
			
			result = HttpCall(h, line, "上传储值卡撤销失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
		}
	}
	catch (Exception er)
		{
			er.printStackTrace();
		}
		
	return false;
	}
	
	public boolean getFjkInfo(MzkRequestDef req, ArrayList fjklist)
	{
		return false;
	}
	
	// 发送销售小票
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		if (!GlobalInfo.isOnline) { return -1; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(commandCode);

			// 单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } });
			line = Transition.closeTable(line, "SaleHeadDef", 1);
			

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			// 付款明细
			String line2 = "";

			for (int i = 0; i < salePayment.size(); i++)
			{
				salePayDef = (SalePayDef) salePayment.elementAt(i);

				line2 += Transition.ItemDetail(salePayDef, SalePayDef.ref);
			}

			line2 = Transition.closeTable(line2, "salePayDef", salePayment.size());

			// 合并
			line = Transition.getHeadXML(line + line1 + line2);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			if (http == null)
			{
				result = HttpCall(line3, "上传小票失败!");
			}
			else
			{
				result = HttpCall(http, line3, "上传小票失败!");
			}
			// 返回应答数据
			if (result == 0 && retValue != null && line3.toString().trim().length() > 0)
			{
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(3,new String[] { "memo", "value"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					retValue.add(row[0]);
					retValue.add(row[1]);
				}
			}

			//
			return result;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return -1;
		}
		finally
		{
			saleGoodsDef = null;
			salePayDef = null;
		}
	}
/*
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd)
	{
		return this.getRefundMoney(mkt, syjh, fphm, rmd, 24);
	}

	
	
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd, int cmdcode)*/
//	调用扣回
	public boolean getRefundMoney(String mkt, String syjh, long fphm,String khbz)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mkt, khbz, syjh, String.valueOf(fphm) };
		String[] args = { "mkt", "jygs", "syjh", "fphm" };

		try
		{
			head = new CmdHead(24);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(24), line, "联网退货扣回金额失败!");

			if (result == 0)
			{
					return true; 
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}
	
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd)
	{
		boolean done = super.getRefundMoney(mkt, syjh, fphm, rmd, CmdDef.GETREFUNDMONEY);
		System.out.println(done + " " + GlobalInfo.sysPara.searchPosAndCUST);
		if (done && GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
		{
			RefundMoneyDef rmd1 = new RefundMoneyDef();
			boolean done1 = super.getRefundMoney(mkt, syjh, fphm, rmd1, CmdDef.GETREFUNDMONEY + 200);
			if (done1)
			{
				rmd.jfkhje = ManipulatePrecision.doubleConvert(rmd.jfkhje + rmd1.jfkhje);
				rmd.jfdesc = rmd.jfdesc + rmd1.jfdesc;
				rmd.fqkhje = ManipulatePrecision.doubleConvert(rmd.fqkhje + rmd1.fqkhje);
				rmd.fqdesc = rmd.fqdesc + rmd1.fqdesc;
				rmd.qtkhje = ManipulatePrecision.doubleConvert(rmd.qtkhje + rmd1.qtkhje);
				rmd.qtdesc = rmd.qtdesc + rmd1.qtdesc;
				rmd.jdxxfkje = ManipulatePrecision.doubleConvert(rmd.jdxxfkje + rmd1.jdxxfkje);
				rmd.jdxxfkdesc = rmd.jdxxfkdesc + rmd1.jdxxfkdesc;
			}
			done = done || done1;
		}
		return done;
	}
	
	
	public boolean getMzkInfo(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{

		return super.getMzkInfo(h, req, ret, sendGgkAddress(req,cmdCode));
	}
	
	
	//访问刮刮卡的posserver地址
	protected int sendGgkAddress(MzkRequestDef req,int cmdCode)
	{
//		是否为刮刮卡查询
		if(req.str2 != null && req.str2.equals("8") && (Jnyz_CustomGlobalInfo.getDefault().sysPara.ggkUrl != null && Jnyz_CustomGlobalInfo.getDefault().sysPara.ggkUrl.length() > 0))
		{
				cmdCode = 143;
				String ggkUrl = Jnyz_CustomGlobalInfo.getDefault().sysPara.ggkUrl;
				String[] s = new String[] { ggkUrl, "," + cmdCode + "," };
				for(int i = 0;i<GlobalInfo.otherHttp.size();i++)
				{
					String[] ss = (String[]) GlobalInfo.otherHttp.get(i);
					if(ss[1].equals(","+cmdCode+","))continue;
					GlobalInfo.otherHttp.add(s);
				}
				
				if(GlobalInfo.otherHttp.size() <= 0)GlobalInfo.otherHttp.add(s);
		}
		
		return cmdCode;
		
	}
	
//	获取网上小票头
	public boolean getReceipt(String ysyjh, String yfphm,SaleHeadDef salehead) {
				
		CmdHead head = null;
		int result = -1;
		head = new CmdHead(GETSALEHEAD);
		
		// 单头打XML
		StringBuffer line = new StringBuffer();
		String[] values = 
        {
        		ConfigClass.CashRegisterCode,GlobalInfo.posLogin.gh,ysyjh,yfphm,"A",""
        };
		String[] args = 
        {
        		"syjh","syyh","ysyjh","yfphm","type","memo"
        };
		line.append(head.headToString() + Transition.SimpleXML(values, args));

           //不显示错误信息
        result = HttpCall(getMemCardHttp(GETSALEHEAD), line, "");
        
        if (result == 0)
		{
        	String retmsg = line.substring(line.indexOf("<retmsg>")+8,line.indexOf("</retmsg>"));
        	if(!retmsg.equals("成功"))
        	{
        		if(retmsg.equals("无重打印权限者只允许打印一次"))
        		{
        			return true;
        		}
//        		new MessageBox(retmsg);
        		return false;
        	}
		}
        
        return false;
	}

	//获取网上小票
	public boolean getReceipt(String ysyjh, String yfphm,SaleHeadDef salehead,Vector salegoods,Vector salepay) {
				
		CmdHead head = null;
		int result = -1;
		head = new CmdHead(GETSALEHEAD);
		
		// 单头打XML
		StringBuffer line = new StringBuffer();
		String[] values = 
        {
        		ConfigClass.CashRegisterCode,GlobalInfo.posLogin.gh,ysyjh,yfphm,"B",""
        };
		String[] args = 
        {
        		"syjh","syyh","ysyjh","yfphm","type","memo"
        };
		line.append(head.headToString() + Transition.SimpleXML(values, args));

           //不显示错误信息
        result = HttpCall(getMemCardHttp(GETSALEHEAD), line, "");
        
        if (result == 0)
		{
        	String retmsg = line.substring(line.indexOf("<retmsg>")+8,line.indexOf("</retmsg>"));
        	if(!retmsg.equals("成功") && !retmsg.equals("无重打印权限者只允许打印一次"))
    		{
        		new MessageBox(retmsg);
        		return false;
        	}
			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() > 0)
			{
				String[] row = (String[]) v.elementAt(0);

				if (!Transition.ConvertToObject(salehead, row)) {return false;}
			}
		}
        else
        {
        	new MessageBox("获取打印小票头失败！");
	    	return false;
        }
        
        CmdHead goods = null;
        int result1 = -1;
        goods = new CmdHead(GETSALGOODS);
		
		// 单头打XML
		StringBuffer linegoods = new StringBuffer();
		
		linegoods.append(goods.headToString() + Transition.SimpleXML(new String[] {salehead.billno}, new String[] {"billno"}));

        //不显示错误信息
		result1 = HttpCall(getMemCardHttp(GETSALGOODS), linegoods, "");
		
		
		if (result1 == 0)
		{
			Vector v = new XmlParse(linegoods.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() > 0)
			{
				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					SaleGoodsDef sgd = new SaleGoodsDef();

					if (Transition.ConvertToObject(sgd, row))
					{
						salegoods.add(sgd);
					}
					else
					{
						salegoods.clear();
						salegoods = null;
						return false;
					}
				}
			}
		}
        else
        {
        	new MessageBox("获取打印小票商品明细失败！");
	    	return false;
        }
		
		 CmdHead pay = null;
	     int result2 = -1;
	     pay = new CmdHead(GETSALEPAY);
			
			// 单头打XML
		StringBuffer linepay = new StringBuffer();
			
		linepay.append(pay.headToString() + Transition.SimpleXML(new String[] {salehead.billno}, new String[] {"billno"}));

	        //不显示错误信息
		result2 = HttpCall(getMemCardHttp(GETSALEPAY), linepay, "");
			
		if (result2 == 0)
		{
			Vector v = new XmlParse(linepay.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() > 0)
			{
				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						SalePayDef pd = new SalePayDef();

						if (Transition.ConvertToObject(pd, row))
						{
							salepay.add(pd);
						}
						else
						{
							salepay.clear();
							salepay = null;
							return false;
						}
					}
				}
			}
		}
	    else
	    {
	        new MessageBox("获取打印小票付款明细失败！");
	    	return false;
	    }
        
		return true;
	}

	//获取支付宝单据金额
	public double getZfbJe(String syjh, String fphm) {
		double zfbje = 0;
		CmdHead head = null;
		int result = -1;
		head = new CmdHead(GETZFBJE);
		
		// 单头打XML
		StringBuffer line = new StringBuffer();
		String[] values = 
        {
        		ConfigClass.Market,syjh,fphm
        };
		String[] args = 
        {
        		"mkt","syjh","fphm"
        };
		line.append(head.headToString() + Transition.SimpleXML(values, args));

           //不显示错误信息
        result = HttpCall(getMemCardHttp(GETZFBJE), line, "");
        
        if (result == 0)
		{
			Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"vje"});

			if (v.size() > 0)
			{
				String[] row = (String[]) v.elementAt(0);
				zfbje = Convert.toDouble(row[0]);
				
			}
		}
        
		return zfbje;
	}

}
