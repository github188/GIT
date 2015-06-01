package com.efuture.javaPos.Plugin.EBill;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.AxisWebService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.WebServiceConfigClass;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.UI.SaleEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Wqbh.Wqbh_SaleBS;
import device.ICCard.KTL512VWQ;

public class EBill
{
	private static EBill instance = null;
	private AxisWebService axis = new AxisWebService();

	private boolean isEnable = false;
	public boolean editBillFlag = false;
	private String curBillNO = null;
	private Vector goodsList;
	public static Vector codeList = new Vector();
	
	public Vector typeVec = null;
	public Vector backVec = null;
	
	public static EBill getDefault()
	{
		if (instance  == null)
		{
			if (new File(GlobalVar.ConfigPath+"\\Ebill.ini").exists())
			{
				BufferedReader br = null;
				try{
					br=CommonMethod.readFile(GlobalVar.ConfigPath+"\\Ebill.ini");
					String line = br.readLine();
					
					Class cl = Class.forName("custom.localize.SPEbill."+line);
					instance = (EBill) cl.newInstance();
				}catch(Exception er)
				{
					er.printStackTrace();
				}
				finally
				{
					if (br != null)
					{
						try
						{
							br.close();
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						br = null;
					}
				}
			}
			else
				instance = new EBill();
		}
		return instance;
	}

	public boolean isEnable()
	{
		return isEnable;
	}
	
	public boolean isBack()
	{
		if (backVec != null && backVec.size() > 0)
		{
			return true;
		}
		return false;
	}

	private void clear()
	{
		editBillFlag = false;
		curBillNO = null;

		if (goodsList != null)
		{
			goodsList.removeAllElements();
			goodsList.clear();
		}
	}
	
	//查询退货小票清单
	public boolean getBackSaleBill(SaleBS saleBS)
	{
		//安卓平板
		if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Android.ini"))
		{
			isEnable = true;
			return false;
		}
		isEnable = false;
		return false;
	}
	
	private void inputType(StringBuffer codetype, StringBuffer name)
	{
		Vector typeVec = new Vector();		
		//typeVec.add(new String[] { "01", "会员卡" });
		typeVec.add(new String[] { "02", Language.apply("纸制单据编号") });
		//typeVec.add(new String[] { "03", "手机号码" });
		//typeVec.add(new String[] { "04", "柜组编码" });
		typeVec.add(new String[] { "05", Language.apply("专柜通号") });

		String[] title = { Language.apply("查找类型"), Language.apply("条件名称") };
		int[] width = { 100, 400 };

		int choice = new MutiSelectForm().open(Language.apply("请选择交易方式"), title, width, typeVec);
		if (choice <=0) choice = 0;
		
		String[] v = (String[]) typeVec.elementAt(choice);
		codetype.append(v[0]);
		name.append(v[1]);
		
	}
	
	private boolean getSaleBill_Cczz(SaleBS saleBS,CustomerDef cust,String code)
	{
		try{
/*		saleBS.saleHead.hjzje=head.hjzje;
		saleBS.saleHead.hjzke=head.hjzke;
		saleBS.saleHead.hjzsl=head.hjzsl;
		saleBS.saleHead.str4 =head.str1 ;
		saleBS.saleHead.memo =head.memo ;*/
			if (saleBS.curCustomer != null)
			{
				if(saleBS.curCustomer.code != null || saleBS.curCustomer.code.length() >0)
				{
					if(saleBS.curCustomer.valstr2 == null || saleBS.curCustomer.valstr2.length() <= 0){
						StringBuffer buff = new StringBuffer();
						if (!CczzService.findcustId(saleBS.curCustomer.code, buff))
						{
							new MessageBox(Language.apply("找不到账号信息"));
							return false;
						}
						else
						{
							saleBS.curCustomer.valstr2= buff.toString();
						}
					}
				}
				
				if(!cust.valstr2.equals(saleBS.curCustomer.valstr2)){
					new MessageBox(Language.apply("添加失败：单据与当前会员卡不属于同一个账号"));
					return false;
				}
			}
			
			
			codeList.add(code);
		 
			
		saleBS.curCustomer = cust;
		saleBS.saleEvent.setVIPInfo(saleBS.getVipInfoLabel());
//		
////		根据序号的查询原卡号，检测是否和当前会员卡号一致
//		SaleHeadDef shd = new SaleHeadDef();
//		boolean done1 = CczzService.getSaleHead(code, "N", shd);
//		if (!done1)
//		{
//			new MessageBox("没有找到单据！");
//			return false;
//		}
//		else if (!shd.cczz_custID.equals(saleBS.curCustomer.valstr2))
//		{
//			new MessageBox("单据与当前会员卡不属于同一个账号，请重新输入");
//			return false;
//		}
		
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = CczzService.getSaleGoods(code,"N",v1);
		if(done){
			for (int i = 0; i < v1.size(); i++)
			{
				SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
				
				StringBuffer buff1 = new StringBuffer();
				buff1.append(sg.sl);
				GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
				if (gdf == null)
				{
					return false;
				}
				else
				{
					SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
					
					sgd.syjh = GlobalInfo.syjDef.syjh;
					sgd.barcode=sg.barcode;
					sgd.code=sg.code;
					sgd.yyyh=sg.yyyh;
					sgd.name=sg.name;
					sgd.gz=sg.gz;
					sgd.unit=sg.unit;
					sgd.memo=sg.memo;
					sgd.jg=sg.jg;
					sgd.sl=sg.sl;
					sgd.hjje=sg.hjje;
					sgd.hjzk=sg.hjzk;
					sgd.yhzke=sg.yhzke;
					sgd.lszke=sg.lszke;
					sgd.hyzke=sg.hyzke;
					sgd.qtzke=sg.qtzke;
					sgd.str7=sg.str7;
					sgd.str9=code;
					sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
					// 重算折扣
					saleBS.getZZK(sgd);
					if(saleBS.saleGoods.size() == 0){
						saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
					else
					{
						boolean sfcz=false;
						for (int j = 0; j < saleBS.saleGoods.size(); j++) {
							SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
							if(sgd.fph.equals(sgdef.fph)){
								sfcz = true;
								break;
							}
						}
						if(sfcz ==false)
							saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
				}
			}
		}
		else
		{
			saleBS.curCustomer = null;
		}

		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	
	private boolean getSaleBill_JH100(SaleBS saleBS,CustomerDef cust,String code)
	{
		try{
/*		saleBS.saleHead.hjzje=head.hjzje;
		saleBS.saleHead.hjzke=head.hjzke;
		saleBS.saleHead.hjzsl=head.hjzsl;
		saleBS.saleHead.str4 =head.str1 ;
		saleBS.saleHead.memo =head.memo ;*/
			if (saleBS.curCustomer != null)
			{
				if(saleBS.curCustomer.code != null || saleBS.curCustomer.code.length() >0)
				{
					if(saleBS.curCustomer.valstr2 == null || saleBS.curCustomer.valstr2.length() <= 0){
						StringBuffer buff = new StringBuffer();
						if (!CczzService.findcustId(saleBS.curCustomer.code, buff))
						{
							new MessageBox(Language.apply("找不到账号信息"));
							return false;
						}
						else
						{
							saleBS.curCustomer.valstr2= buff.toString();
						}
					}
				}
				
				if(!cust.valstr2.equals(saleBS.curCustomer.valstr2)){
					new MessageBox(Language.apply("添加失败：单据与当前会员卡不属于同一个账号"));
					return false;
				}
			}
			
			
			codeList.add(code);
		 
			
		saleBS.curCustomer = cust;
		saleBS.saleEvent.setVIPInfo(saleBS.getVipInfoLabel());
		
//		根据序号的查询原卡号，检测是否和当前会员卡号一致
//		SaleHeadDef shd = new SaleHeadDef();
//		boolean done1 = CczzService.getSaleHead(code, "N", shd);
//		if (!done1)
//		{
//			new MessageBox("没有找到单据！");
//			return false;
//		}
//		else if (!shd.cczz_custID.equals(saleBS.curCustomer.valstr2))
//		{
//			new MessageBox("单据与当前会员卡不属于同一个账号，请重新输入");
//			return false;
//		}
//		
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = CczzService.getSaleGoods(code,"N",v1);
		if(done){
			for (int i = 0; i < v1.size(); i++)
			{
				SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
				
				StringBuffer buff1 = new StringBuffer();
				buff1.append(sg.sl);
				GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
				if (gdf == null)
				{
					return false;
				}
				else
				{
					SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
					
					sgd.syjh = GlobalInfo.syjDef.syjh;
					sgd.barcode=sg.barcode;
					sgd.code=sg.code;
					sgd.yyyh=sg.yyyh;
					sgd.name=sg.name;
					sgd.gz=sg.gz;
					sgd.unit=sg.unit;
					sgd.memo=sg.memo;
					sgd.jg=sg.jg;
					sgd.sl=sg.sl;
					sgd.hjje=sg.hjje;
					sgd.hjzk=sg.hjzk;
					sgd.yhzke=sg.yhzke;
					sgd.lszke=sg.lszke;
					sgd.hyzke=sg.hyzke;
					sgd.qtzke=sg.qtzke;
					sgd.str7=sg.str7;
					sgd.str9=code;
					sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
					// 重算折扣
					saleBS.getZZK(sgd);
					if(saleBS.saleGoods.size() == 0){
						saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
					else
					{
						boolean sfcz=false;
						for (int j = 0; j < saleBS.saleGoods.size(); j++) {
							SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
							if(sgd.fph.equals(sgdef.fph)){
								sfcz = true;
								break;
							}
						}
						if(sfcz ==false)
							saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
				}
			}
		}
		else
		{
			saleBS.curCustomer = null;
		}

		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	private boolean getSaleBill_Wqbh(SaleBS saleBS,CustomerDef cust,String code)
	{
		try{

		if(saleBS.curCustomer ==null) saleBS.curCustomer = cust;
		saleBS.saleEvent.setVIPInfo(saleBS.getVipInfoLabel());
		
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = CczzService.getSaleGoods(code,"N",v1);
		if(done){
			for (int i = 0; i < v1.size(); i++)
			{
				SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
				
				StringBuffer buff1 = new StringBuffer();
				buff1.append(sg.sl);
				GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
				if (gdf == null)
				{
					return false;
				}
				else
				{
					SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
					
					sgd.syjh = GlobalInfo.syjDef.syjh;
					sgd.barcode=sg.barcode;
					sgd.code=sg.code;
					sgd.yyyh=sg.yyyh;
					sgd.name=sg.name;
					sgd.gz=sg.gz;
					sgd.unit=sg.unit;
					sgd.memo=sg.memo;
					sgd.jg=sg.jg;
					sgd.sl=sg.sl;
					sgd.hjje=sg.hjje;
					sgd.hjzk=sg.hjzk;
					sgd.yhzke=sg.yhzke;
					sgd.lszke=sg.lszke;
					sgd.hyzke=sg.hyzke;
					sgd.qtzke=sg.qtzke;
					sgd.str7=sg.str7;
					sgd.str9=code;
					sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
					// 重算折扣
					saleBS.getZZK(sgd);
					if(saleBS.saleGoods.size() == 0){
						saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
					else
					{
						boolean sfcz=false;
						for (int j = 0; j < saleBS.saleGoods.size(); j++) {
							SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
							if(sgd.fph.equals(sgdef.fph)){
								sfcz = true;
								break;
							}
						}
						if(sfcz ==false)
							saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					}
				}
			}
		}
		else
		{
			saleBS.curCustomer = null;
		}

		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	//根据提货号获取明细
	private boolean getSaleGoodsBill_Cczz(SaleBS saleBS,String code)
	{
		try{
		if (saleBS.curCustomer == null)
		{
			new MessageBox(Language.apply("请先刷会员卡"));
			return false;
		}
		
		if(saleBS.curCustomer.valstr2 == null || saleBS.curCustomer.valstr2.length() <= 0)
		{
			StringBuffer buff = new StringBuffer();
			if (!CczzService.findcustId(saleBS.curCustomer.code, buff))
			{
				new MessageBox(Language.apply("找不到账号信息"));
				return false;
			}
			else
			{
				saleBS.curCustomer.valstr2= buff.toString();
			}
		}
		
		//根据序号的查询原卡号，检测是否和当前会员卡号一致
		SaleHeadDef shd = new SaleHeadDef();
		boolean done1 = CczzService.getSaleHead(code, "N", shd);
		if (!done1)
		{
			new MessageBox(Language.apply("没有找到单据！"));
			return false;
		}
		else if (!shd.cczz_custID.equals(saleBS.curCustomer.valstr2))
		{
			new MessageBox(Language.apply("单据与当前会员卡不属于同一个账号，请重新输入"));
			return false;
		}
			
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = CczzService.getSaleGoods(code,"N",v1);
		
		if(done)
		{
				if(v1.size() <= 0)
				{
					new MessageBox(Language.apply("没有找到单据！"));
					saleBS.curCustomer = null;
					return false;
				}
				
				for (int i = 0; i < v1.size(); i++)
				{
					SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
					
					StringBuffer buff1 = new StringBuffer();
					buff1.append(sg.sl);
					GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
					if (gdf == null)
					{
						return false;
					}
					else
					{
						SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
						
						sgd.syjh = GlobalInfo.syjDef.syjh;
						sgd.barcode=sg.barcode;
						sgd.code=sg.code;
						sgd.yyyh=sg.yyyh;
						sgd.name=sg.name;
						sgd.gz=sg.gz;
						sgd.unit=sg.unit;
						sgd.memo=sg.memo;
						sgd.jg=sg.jg;
						sgd.sl=sg.sl;
						sgd.hjje=sg.hjje;
						sgd.hjzk=sg.hjzk;
						sgd.yhzke=sg.yhzke;
						sgd.lszke=sg.lszke;
						sgd.hyzke=sg.hyzke;
						sgd.qtzke=sg.qtzke;
						sgd.str7=sg.str7;
						sgd.str9=code;
						sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
						// 重算折扣
						saleBS.getZZK(sgd);
						
						if(saleBS.saleGoods.size() == 0){
							saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
						}
						else
						{
							boolean sfcz=false;
							for (int j = 0; j < saleBS.saleGoods.size(); j++) {
								SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
								if(sgd.fph.equals(sgdef.fph)){
									sfcz = true;
									break;
								}
							}
							if(sfcz ==false)
								saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
						}
					}
				}
		}
		
		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	
	//根据提货号获取明细
	private boolean getSaleGoodsBill_JH100(SaleBS saleBS,String code)
		{
			try{
			/*if (saleBS.curCustomer == null)
			{
				//new MessageBox(Language.apply("请先刷会员卡"));
				return true;
			}*/
			
			//if(saleBS.curCustomer.valstr2 == null || saleBS.curCustomer.valstr2.length() <= 0)
			
			//{
			/*	StringBuffer buff = new StringBuffer();
				if (!CczzService.findcustId("", buff))
				{
					new MessageBox(Language.apply("找不到账号信息"));
					return false;
				}
				else
				{
					//saleBS.curCustomer.valstr2= buff.toString();
				}*/
			//}
			
			//根据序号的查询原卡号，检测是否和当前会员卡号一致
			/*SaleHeadDef shd = new SaleHeadDef();
			boolean done1 = CczzService.getSaleHead(code, "N", shd);
			if (!done1)
			{
				new MessageBox(Language.apply("没有找到单据！"));
				return false;
			}
			/*else if (!shd.cczz_custID.equals(saleBS.curCustomer.valstr2))
			{
				//new MessageBox(Language.apply("单据与当前会员卡不属于同一个账号，请重新输入"));
				return true;
			}*/
				
			//根据序号得到商品
			Vector v1 = new Vector();
			boolean done = CczzService.getSaleGoods(code,"N",v1);
			
			if(done)
			{
					if(v1.size() <= 0)
					{
						new MessageBox(Language.apply("没有找到单据！"));
						//saleBS.curCustomer = null;
						return false;
					}
					
					for (int i = 0; i < v1.size(); i++)
					{
						SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
						
						StringBuffer buff1 = new StringBuffer();
						buff1.append(sg.sl);
						GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
						if (gdf == null)
						{
							return false;
						}
						else
						{
							SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
							
							sgd.syjh = GlobalInfo.syjDef.syjh;
							sgd.barcode=sg.barcode;
							sgd.code=sg.code;
							sgd.yyyh=sg.yyyh;
							sgd.name=sg.name;
							sgd.gz=sg.gz;
							sgd.unit=sg.unit;
							sgd.memo=sg.memo;
							sgd.jg=sg.jg;
							sgd.sl=sg.sl;
							sgd.hjje=sg.hjje;
							sgd.hjzk=sg.hjzk;
							sgd.yhzke=sg.yhzke;
							sgd.lszke=sg.lszke;
							sgd.hyzke=sg.hyzke;
							sgd.qtzke=sg.qtzke;
							sgd.str7=sg.str7;
							sgd.str9=code;
							sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
							// 重算折扣
							saleBS.getZZK(sgd);
							
							if(saleBS.saleGoods.size() == 0){
								saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
							}
							else
							{
								boolean sfcz=false;
								for (int j = 0; j < saleBS.saleGoods.size(); j++) {
									SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
									if(sgd.fph.equals(sgdef.fph)){
										sfcz = true;
										break;
									}
								}
								if(sfcz ==false)
									saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
							}
						}
					}
			}
			
			saleBS.calcHeadYsje();
			saleBS.refreshSaleForm();
			
			return true;
			}catch(Exception er)
			{
				er.printStackTrace();
				return false;
			}
		}
	
//	根据提货号获取明细
	private boolean getSaleGoodsBill_Wqbh(SaleBS saleBS,String code)
	{
		try{
			/*
		if (saleBS.curCustomer == null)
		{
			new MessageBox("请先刷会员卡");
			return false;
		}
		
		if(saleBS.curCustomer.valstr2 == null || saleBS.curCustomer.valstr2.length() <= 0)
		{
			StringBuffer buff = new StringBuffer();
			if (!CczzService.findcustId(saleBS.curCustomer.code, buff))
			{
				new MessageBox("找不到账号信息");
				return false;
			}
			else
			{
				saleBS.curCustomer.valstr2= buff.toString();
			}
		}
		*/
		//根据序号的查询原卡号，检测是否和当前会员卡号一致
		SaleHeadDef shd = new SaleHeadDef();
		boolean done1 = CczzService.getSaleHead(code, "N", shd);
		if (!done1)
		{
			new MessageBox(Language.apply("没有找到单据！"));
			return false;
		}
		CustomerDef cust = null;
		
		if(shd.hykh.trim().length()>15){
			//大会员刷卡不允许使用单据号调单
			new MessageBox(Language.apply("大会员刷卡不允许使用单据号调单"));
			return false;
		}
		
		
		//百货会员查询
		if(shd.hykh.trim().length()>0){
			HykInfoQueryBS hq = new HykInfoQueryBS();
			cust = hq.findMemberCard("@"+shd.hykh.trim());
			if(cust!=null){
				//saleBS.curCustomer = cust;
				//saleBS.saleEvent.setVIPInfo(saleBS.getVipInfoLabel());
			}else{
//				if (new MessageBox("查找开单会员失败,是否继续取单？" +"\n\n1-继续取单/ 2-取消", null, false).verify() == GlobalVar.Key2){
				if (new MessageBox(Language.apply("查找开单会员失败,是否继续取单？\n\n1-继续取单/ 2-取消"), null, false).verify() == GlobalVar.Key2){
					return false;
				}
			}
		}
		
		if (saleBS.curCustomer == null && cust!=null)
		{
			saleBS.curCustomer = cust;
			saleBS.saleEvent.setVIPInfo(saleBS.getVipInfoLabel());
			saleBS.saleHead.hykh = cust.code;
			saleBS.saleHead.hytype = cust.type;
			
		}else if(saleBS.curCustomer != null&&cust!=null){
			if(!saleBS.curCustomer.code.trim().equals(cust.code.trim())){
				new MessageBox(Language.apply("会员卡信息不一致,请分开调单付款!"));
				return false;
			}
		}
		/*
		else if (!shd.cczz_custID.equals(saleBS.curCustomer.valstr2))
		{
			new MessageBox("单据与当前会员卡不属于同一个账号，请重新输入");
			return false;
		}
			*/
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = CczzService.getSaleGoods(code,"N",v1);
		
			if(done){
				if(v1.size() <= 0){
					new MessageBox(Language.apply("没有找到单据！"));
					saleBS.curCustomer = null;
					return false;
				}
					for (int i = 0; i < v1.size(); i++)
					{
						SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
						
						StringBuffer buff1 = new StringBuffer();
						buff1.append(sg.sl);
						GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
						if (gdf == null)
						{
							return false;
						}
						else
						{
							SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
							
							sgd.syjh = GlobalInfo.syjDef.syjh;
							sgd.barcode=sg.barcode;
							sgd.code=sg.code;
							sgd.yyyh=sg.yyyh;
							sgd.name=sg.name;
							sgd.gz=sg.gz;
							sgd.unit=sg.unit;
							sgd.memo=sg.memo;
							sgd.jg=sg.jg;
							sgd.sl=sg.sl;
							sgd.hjje=sg.hjje;
							sgd.hjzk=sg.hjzk;
							sgd.yhzke=sg.yhzke;
							sgd.lszke=sg.lszke;
							sgd.hyzke=sg.hyzke;
							sgd.qtzke=sg.qtzke;
							sgd.str7=sg.str7;
							sgd.str9=code;
							sgd.fph = code+";"+sg.fph;//小票单号;行号;跟随状态
							// 重算折扣
							saleBS.getZZK(sgd);
							
							if(saleBS.saleGoods.size() == 0){
								saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
							}
							else
							{
								boolean sfcz=false;
								for (int j = 0; j < saleBS.saleGoods.size(); j++) {
									SaleGoodsDef sgdef = (SaleGoodsDef) saleBS.saleGoods.elementAt(j);
									if(sgd.fph.equals(sgdef.fph)){
										sfcz = true;
										break;
									}
								}
								if(sfcz ==false)
									saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
							}
						}
					}
		}
		
		

		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	private boolean getSaleBill_Android(SaleBS saleBS,SaleHeadDef head,String code, String type)
	{
		try{
		saleBS.saleHead.hjzje=head.hjzje;
		saleBS.saleHead.hjzke=head.hjzke;
		saleBS.saleHead.hjzsl=head.hjzsl;
		saleBS.saleHead.str4 =head.str1 ;
		saleBS.saleHead.memo =head.memo ;
		
		
		//根据序号得到商品
		Vector v1 = new Vector();
		boolean done = AndroidService.getSaleGoods(code,type.toString(),"A",v1);
		if(done){
			for (int i = 0; i < v1.size(); i++)
			{
				SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
				
				StringBuffer buff1 = new StringBuffer();
				buff1.append(sg.sl);
				GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
				if (gdf == null)
				{
					return false;
				}
				else
				{
					SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
					
					sgd.syjh = GlobalInfo.syjDef.syjh;
					sgd.barcode=sg.barcode;
					sgd.code=sg.code;
					sgd.yyyh=sg.yyyh;
					sgd.name=sg.name;
					sgd.gz=sg.gz;
					sgd.unit=sg.unit;
					sgd.memo=sg.memo;
					sgd.jg=sg.jg;
					sgd.sl=sg.sl;
					sgd.hjje=sg.hjje;
					sgd.hjzk=sg.hjzk;
					sgd.yhzke=sg.yhzke;
					sgd.lszke=sg.lszke;
					sgd.hyzke=sg.hyzke;
					sgd.qtzke=sg.qtzke;
					// 重算折扣
					saleBS.getZZK(sgd);
					saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
				}
			}
		}
		

		saleBS.calcHeadYsje();
		saleBS.refreshSaleForm();
		
		return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	private boolean getBackBill_Android(SaleBS saleBS,SaleHeadDef head,String code,String type)
	{
		try{
			saleBS.initNewSale();
			saleBS.saletype = SellType.RETAIL_BACK;
			Vector v1 = new Vector();
			boolean done = AndroidService.getSaleGoods(code,type.toString(),"A",v1);
			if (!done) return false;
			
			saleBS.thFphm = Convert.toLong(head.yfphm);
			saleBS.thSyjh = head.ysyjh;
			backVec = v1;
			done = saleBS.findBackTicketInfo();
			saleBS.saleHead.str4 = head.str1;
			return done;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
		finally
		{
			backVec = null;
		}
		
	}
	
	private boolean getBill_Android(SaleBS saleBS)
	{
		if (!AndroidService.inithttp()) return false;
		
		StringBuffer buff=new StringBuffer();
		StringBuffer type = new StringBuffer();
		StringBuffer name = new StringBuffer();
		
		inputType(type,name);
		String code="";
//		if (new TextBox().open("请输入"+name.toString(), name.toString()+"查询", "终端单据查询", buff, 0, 0, false, TextBox.AllInput))
		if (new TextBox().open(Language.apply("请输入")+name.toString(), name.toString()+Language.apply("查询"), Language.apply("终端单据查询"), buff, 0, 0, false, TextBox.AllInput))
		code=buff.toString();
		//根据序号得到小票清单
		SaleHeadDef head  = new SaleHeadDef();
		boolean flag = AndroidService.getSaleHead(code,type.toString(),"", head);
		if (flag)
		{
			if (head.djlb.equals("01"))
			{
				return getSaleBill_Android(saleBS,head,code.toString(),type.toString());
			}
			else if (head.djlb.equals("04"))
			{
				return getBackBill_Android(saleBS,head,code.toString(),type.toString());
			}
		}
		return false;
		
	}
	
	//查询销售小票清单
	public boolean getSaleBill(SaleBS saleBS)
	{
		//if(saleBS.saleGoods.size() > 0){new MessageBox("请先完成当前交易！");return false;}
		// 清空数据
		clear();

		if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Android.ini") || CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Cczz.ini")|| CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Wqbh.ini")||CommonMethod.isFileExist(GlobalVar.ConfigPath + "/JH100.ini"))
		{
			isEnable = true;
		}
		
		if (!isEnable)
		{
			new MessageBox(Language.apply("专柜电子开票功能未启用"));
			return false;
		}

		if (editBillFlag && saleBS.saleGoods != null && saleBS.saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("请先完成当前交易"));
			return false;
		}
		if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/JH100.ini"))
		{
			return getBill_JH100(saleBS);
		}
		if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Cczz.ini"))
		{
			return getBill_cczz(saleBS);
		}
		else if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Wqbh.ini"))
		{
			return getBill_wqbh(saleBS);
		}
		else if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Android.ini"))
		{
			return getBill_Android(saleBS);
		}
		else
		{
			if (!doSaleHead(saleBS))
				return false;
	
			if (!doSaleGoods(saleBS))
				return false;
		}

		return true;
	}

	private boolean getBill_wqbh(SaleBS saleBS)
	{
		
		if(GlobalInfo.sysPara.EBillandSgd.equals("N") && saleBS.saleGoods.size()>0){
			SaleGoodsDef sg = ((SaleGoodsDef)saleBS.saleGoods.elementAt(0));
			if(sg.str9== null || sg.str9.trim().equals("")){
				new MessageBox(Language.apply("已录入手工单,不允许调取电子单!"));
				return false;
			}
		}
		//读取Wqbh.ini配置文件，设置posserver连接
		if (!WqbhService.inithttp()) return false;
		
	
		Vector typeVec = new Vector();
		
		try
		{
			typeVec.add(new String[] { "01", Language.apply("会员卡") });
			typeVec.add(new String[] { "02", Language.apply("单据编号") });

			String[] title = { Language.apply("查找类型"), Language.apply("条件名称") };
			int[] width = { 100, 400 };

			int choice = new MutiSelectForm().open(Language.apply("请选择交易方式"), title, width, typeVec);
			
			if (choice == -1)
			{
				return false;
			}
			else if (choice == 0)
			{
				Vector cardTypeVec = new Vector();
				cardTypeVec.add(new String[] { "01", Language.apply("百货会员卡") });
				cardTypeVec.add(new String[] { "02", Language.apply("大会员卡") });
				String[] title1 = { Language.apply("序"), Language.apply("会员卡类型") };
				int[] width1 = { 100, 400 };
				int choice1 = new MutiSelectForm().open(Language.apply("请选择会员卡类型"), title1, width1, cardTypeVec);
				CustomerDef cust = new CustomerDef();
				String tk2 = "";
				
				if(choice1==-1){
					return false;
				}else if(choice1 ==0 ){
					HykInfoQueryBS hq = CustomLocalize.getDefault().createHykInfoQueryBS();
					tk2 = hq.readMemberCard();
					if(tk2.trim().equals("")) return false;
					cust = hq.findMemberCard(tk2);
					
				}else{
//					 弹出刷卡窗口  ---new
					String[] title_dhy = { "输入类型" };
					int[] width_dhy = { 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "刷卡输入" });
					contents.add(new String[] { "手机号输入" });
					
					//int inputtyep = -1;
					int choice_dhy = new MutiSelectForm().open("请选择输入方式", title_dhy, width_dhy, contents);
					if (choice_dhy == -1||choice_dhy == 0)
					{
						 //弹出刷卡窗口
						StringBuffer cardno = new StringBuffer();
						TextBox txt = new TextBox();
						if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrInput))
							return false;
						
						tk2 = txt.Track2;
					}else{

						ProgressBox pb = null;
						pb = new ProgressBox();
						pb.setText("正在输入卡号和密码,请等待...");
						tk2 = new KTL512VWQ().findCard();
						if (pb != null)
						{
							pb.close();
							pb = null;
						}
					
					}
					
					if (tk2 == null || tk2.equals("")) return false;
					
					Wqbh_SaleBS ws = new Wqbh_SaleBS();
					// 查找会员卡   调用dll 获取大会员详细信息及积分余额
					cust = ws.sendMemberDHYInfo(tk2);
					if (cust == null) return false;
				}
				
				//若本笔销售有会员卡，对比会员卡是否一致
				if(saleBS.saleHead.hykh!=null && saleBS.saleHead.hykh.length()>0){
					if(!saleBS.saleHead.hykh.trim().equals(cust.code.trim())) {
						new MessageBox(Language.apply("会员卡信息不一致,请分开调单付款!"));
						return false;
					}
				}
				
				Vector v1 = new Vector();
				//按会员卡号获取开票列表
				//更改为以会员卡号查找开票单
				boolean done = WqbhService.getkplist(v1, cust.code, "N");
				
				if (done && v1.size() > 0)
				{
					for (int i= 0; i < v1.size();i++)
					{
						String[] row = (String[]) v1.elementAt(i);
						
						if (row[1].equals("N"))
						{
							row[1] = Language.apply("开票");
							v1.set(i,row);
						}
						else if (row[1].equals("P"))
						{
							row[1] = Language.apply("付款");
							v1.set(i,row);
						}
						else if (row[1].equals("Y"))
						{
							row[1] = Language.apply("提货");
							v1.set(i,row);
						}
						else if (row[1].equals("Q"))
						{
							row[1] = Language.apply("取消开票");
							v1.set(i,row);
						}
					}
					
					MutiSelectForm ms = new MutiSelectForm();
					if(ms.open(Language.apply("请选择单据:按回车键选中，确认键确定，退出键返回主界面"),new String[]{Language.apply("单据号"),Language.apply("开票日期"),Language.apply("金额"),Language.apply("数量"),Language.apply("柜组名称"),Language.apply("选中")}, new int[]{100,230,100,60,100,60}, v1,false,689,319,660,192,true,true,4,false) != -1)
					{
						if(saleBS.saleHead.hykh==null || saleBS.saleHead.hykh.length()<1){
							saleBS.saleHead.hykh = cust.code;
							saleBS.saleHead.hytype = cust.type;
							if(choice1==1){
								saleBS.saleHead.str6 = tk2;
							}
						}
						
						for (int i= 0; i < v1.size();i++)
						{
							String[] row = (String[]) v1.elementAt(i);
							if (row[5].equals("Y"))
							{
								//循环查找已选中的开票单号
								getSaleBill_Wqbh(saleBS, cust, row[0]);
							}
						}
					}
					else
					{
						return false;
					}
					
				}
				else
				{
					new MessageBox(Language.apply("未找到相关票据！"));
				}
				
				
			}
			else
			{
				StringBuffer thcode = new StringBuffer();
				if (new TextBox().open(Language.apply("请输入提货单号"),Language.apply("提货单号输入"), Language.apply("提示:请输入提货单号"),thcode, -1, -1));
        		{
        			if (!(thcode.toString().trim().length() > 0)) return false;
        			getSaleGoodsBill_Wqbh(saleBS,thcode.toString());
        		}
			}
        		
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		return false;
	}

	private boolean getBill_cczz(SaleBS saleBS)
	{
		if (!CczzService.inithttp()) return false;
		
		StringBuffer buff=new StringBuffer();
		StringBuffer type = new StringBuffer();
		StringBuffer name = new StringBuffer();
		
		String code="";
		
		Vector typeVec = new Vector();
		StringBuffer sb = new StringBuffer();

		try
		{
			typeVec.add(new String[] { "01", Language.apply("会员卡") });
			typeVec.add(new String[] { "02", Language.apply("单据编号") });
			typeVec.add(new String[] { "03", Language.apply("清单编号") });

			String[] title = { Language.apply("查找类型"), Language.apply("条件名称") };
			int[] width = { 100, 400 };

			int choice = new MutiSelectForm().open(Language.apply("请选择交易方式"), title, width, typeVec);
			String msg = "";

			if (choice == -1)
			{
				return false;
			}
			else if (choice == 0)
			{
				HykInfoQueryBS hq = CustomLocalize.getDefault().createHykInfoQueryBS();
				String tk2 = hq.readMemberCard();
				CustomerDef cust = hq.findMemberCard(tk2);
				saleBS.saleHead.hykh = cust.code;
				
				StringBuffer buff1 =new StringBuffer();
				if (!CczzService.findcustId(cust.code, buff1))
				{
					new MessageBox(Language.apply("找不到账号信息"));
					return false;
				}
				else
				{
					cust.valstr2 = buff1.toString();
				}
				
				Vector v1 = new Vector();
				boolean done = CczzService.getkplist(v1, cust.valstr2, "N");
				
				if (done && v1.size() > 0)
				{
					for (int i= 0; i < v1.size();i++)
					{
						String[] row = (String[]) v1.elementAt(i);
						
						if (row[1].equals("N"))
						{
							row[1] = Language.apply("开票");
							v1.set(i,row);
						}
						else if (row[1].equals("P"))
						{
							row[1] = Language.apply("付款");
							v1.set(i,row);
						}
						else if (row[1].equals("Y"))
						{
							row[1] = Language.apply("提货");
							v1.set(i,row);
						}
						else if (row[1].equals("Q"))
						{
							row[1] = Language.apply("取消开票");
							v1.set(i,row);
						}
					}
					
					MutiSelectForm ms = new MutiSelectForm();
					if(ms.open(Language.apply("请选择单据:按回车键选中，确认键确定，退出键返回主界面"),new String[]{Language.apply("单据号"),Language.apply("单据类型"),Language.apply("金额"),Language.apply("数量"),Language.apply("柜组名称"),Language.apply("选中")}, new int[]{100,100,100,100,100,100}, v1,false,689,319,660,192,true,true,4,false) != -1)
					{
						for (int i= 0; i < v1.size();i++)
						{
							String[] row = (String[]) v1.elementAt(i);
							if (row[5].equals("Y"))
							{
								getSaleBill_Cczz(saleBS, cust, row[0]);
							}
						}
					}
					else
					{
						return false;
					}
					
				}
				else
				{
					new MessageBox(Language.apply("未找到相关票据！"));
				}
				
				
			}
			else
			{
				StringBuffer thcode = new StringBuffer();
				if (new TextBox().open(Language.apply("请输入提货单号"),Language.apply("提货单号输入"), Language.apply("提示:请输入提货单号"),thcode, -1, -1));
        		{
        			getSaleGoodsBill_Cczz(saleBS,thcode.toString());
        		}
			}
        		
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		
/*		code=buff.toString();
		//根据序号得到小票清单
		SaleHeadDef head  = new SaleHeadDef();
		boolean flag = AndroidService.getSaleHead(code,type.toString(),"", head);
		if (flag)
		{
			if (head.djlb.equals("01"))
			{
				return getSaleBill_Android(saleBS,head,code.toString(),type.toString());
			}
			else if (head.djlb.equals("04"))
			{
				return getBackBill_Android(saleBS,head,code.toString(),type.toString());
			}
		}*/  
		return false;
	}
	
	
	@SuppressWarnings("unused")
	private boolean getBill_JH100(SaleBS saleBS)
	{
		if (!CczzService.inithttp_JH100()) return false;
		
		StringBuffer buff=new StringBuffer();
		StringBuffer type = new StringBuffer();
		StringBuffer name = new StringBuffer();
		
		String code="";
		
		Vector typeVec = new Vector();
		StringBuffer sb = new StringBuffer();

		try
		{
			typeVec.add(new String[] { "01", Language.apply("清单编号") });
			typeVec.add(new String[] { "02", Language.apply("会员清单编号") });

			String[] title = { Language.apply("查找类型"), Language.apply("条件名称") };
			int[] width = { 100, 400 };

			int choice = new MutiSelectForm().open(Language.apply("请选择交易方式"), title, width, typeVec);
			String msg = "";

			if (choice == -1)
			{
				return false;
			}
			else if (choice == 0)
			{
				StringBuffer thcode = new StringBuffer();
				if (new TextBox().open(Language.apply("请输入提货单号"),Language.apply("提货单号输入"), Language.apply("提示:请输入提货单号"),thcode, -1, -1));
        		{
        			getSaleGoodsBill_JH100(saleBS,thcode.toString());
        		}
			}
			else if (choice == 1)
			{
				HykInfoQueryBS hq = CustomLocalize.getDefault().createHykInfoQueryBS();
				String tk2 = hq.readMemberCard();
				CustomerDef cust = hq.findMemberCard(tk2);
				saleBS.saleHead.hykh = cust.code;
				
				StringBuffer buff1 =new StringBuffer();
				if (!CczzService.findcustId(cust.code, buff1))
				{
					new MessageBox(Language.apply("找不到账号信息"));
					return false;
				}
				else
				{
					cust.valstr2 = buff1.toString();
				}
				
				Vector v1 = new Vector();
				boolean done = CczzService.getkplist(v1, cust.valstr2, "N");
				
				if (done && v1.size() > 0)
				{
					for (int i= 0; i < v1.size();i++)
					{
						String[] row = (String[]) v1.elementAt(i);
						
						if (row[1].equals("N"))
						{
							row[1] = Language.apply("开票");
							v1.set(i,row);
						}
						else if (row[1].equals("P"))
						{
							row[1] = Language.apply("付款");
							v1.set(i,row);
						}
						else if (row[1].equals("Y"))
						{
							row[1] = Language.apply("提货");
							v1.set(i,row);
						}
						else if (row[1].equals("Q"))
						{
							row[1] = Language.apply("取消开票");
							v1.set(i,row);
						}
					}
					
					MutiSelectForm ms = new MutiSelectForm();
					if(ms.open(Language.apply("请选择单据:按回车键选中，确认键确定，退出键返回主界面"),new String[]{Language.apply("单据号"),Language.apply("单据类型"),Language.apply("金额"),Language.apply("数量"),Language.apply("柜组名称"),Language.apply("选中")}, new int[]{100,100,100,100,100,100}, v1,false,689,319,660,192,true,true,4,false) != -1)
					{
						for (int i= 0; i < v1.size();i++)
						{
							String[] row = (String[]) v1.elementAt(i);
							if (row[5].equals("Y"))
							{
								getSaleBill_JH100(saleBS, cust, row[0]);
							}
						}
					}
					else
					{
						return false;
					}
					
				}
				else
				{
					new MessageBox(Language.apply("未找到相关票据！"));
				}
			}
			
        		
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		
/*		code=buff.toString();
		//根据序号得到小票清单
		SaleHeadDef head  = new SaleHeadDef();
		boolean flag = AndroidService.getSaleHead(code,type.toString(),"", head);
		if (flag)
		{
			if (head.djlb.equals("01"))
			{
				return getSaleBill_Android(saleBS,head,code.toString(),type.toString());
			}
			else if (head.djlb.equals("04"))
			{
				return getBackBill_Android(saleBS,head,code.toString(),type.toString());
			}
		}*/
		return false;
	}

	public boolean sendSaleBill(SaleHeadDef saleHead)
	{
		if (CommonMethod.isFileExist(GlobalVar.ConfigPath + "/Android.ini"))
		{
			return AndroidService.SendInvoiceOK(saleHead.str4,saleHead.syjh,saleHead.fphm);
		}
		
		return doSalePay(saleHead.mkt, saleHead.str4, saleHead.ysje, saleHead.hjzke, "Y");
	}

	public boolean clearSaleBill()
	{
		return doSalePay(GlobalInfo.sysPara.mktcode, this.curBillNO, 0.00, 0.00, "N");
	}

	private Vector getGoodsList()
	{
		return goodsList;
	}

	public boolean isEditBillFlag()
	{
		return editBillFlag;
	}

	private boolean checkSuccess(String xml)
	{
		Vector succVec = null;

		try
		{
			XmlUtil xmlTool = new XmlUtil(xml);

			succVec = xmlTool.fromXml("NewDataSet", "issuccess", 0, new String[] { "issucc", "msg" });

			if (succVec == null || succVec.size() == 0)
				return false;

			String[] successLine = (String[]) succVec.get(0);

			if (!successLine[0].equals("True"))
			{
				if (successLine[1] != null)
					new MessageBox(successLine[1]);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public int keyReleased(SaleEvent evt, KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Clear:
				evt.initOneSale(evt.saleBS.saletype);
				clearSaleBill();
				break;

			case GlobalVar.Enter:
			case GlobalVar.Quantity:
			case GlobalVar.SetPrice:
			case GlobalVar.Del:
			case GlobalVar.Rebate:
			case GlobalVar.RebatePrice:
			case GlobalVar.WholeRate:
			case GlobalVar.WholeRebate:
			case GlobalVar.writeHang:
			case GlobalVar.readHang:
			case GlobalVar.StaffText:
			case GlobalVar.ExchangeSell:
				key = -1;
				break;

		}
		return key;
	}

	protected boolean doSaleHead(SaleBS saleBS)
	{
		XmlUtil xmlTool = null;
		String invo = null;
		String reqType = null;

		Vector typeVec = new Vector();
		StringBuffer sb = new StringBuffer();

		try
		{
			typeVec.add(new String[] { "01", Language.apply("会员卡") });
			typeVec.add(new String[] { "02", Language.apply("单据编号") });
			typeVec.add(new String[] { "03", Language.apply("手机号码") });
			typeVec.add(new String[] { "04", Language.apply("柜组编码") });

			String[] title = { Language.apply("查找类型"), Language.apply("条件名称") };
			int[] width = { 100, 400 };

			int choice = new MutiSelectForm().open(Language.apply("请选择交易方式"), title, width, typeVec);
			String msg = "";

			if (choice == -1)
			{
				return false;
			}
			else if (choice == 0)
			{
				reqType = "01";

				if (saleBS.memberGrant())
					return false;

				invo = saleBS.saleHead.hykh;

				if (invo == null || invo.trim().equals(""))
					return false;
			}
			else
			{
				if (choice == 1)
				{
					reqType = "02";
					msg = Language.apply("请输入待查询的单据编号");
				}

				if (choice == 2)
				{
					reqType = "03";
					msg = Language.apply("请输入待查询的手机号码");
				}

				if (choice == 3)
				{
					reqType = "04";
					msg = Language.apply("请输入待查询的柜组编码");
				}

				if (new TextBox().open(msg, Language.apply("终端单据查询"), Language.apply("终端单据查询"), sb, 0, 0, false, TextBox.AllInput))
					invo = sb.toString();
				else
					return false;
			}

			xmlTool = new XmlUtil();
			String xml = xmlTool.toXml("SaleInvoices", new String[] { saleBS.saleHead.mkt, invo, reqType }, new String[] { "MKT", "INVO", "TYPE" });
			String resultXml = (String) axis.executeFunction(201, xml);

			if ((resultXml) == null)
			{
				new MessageBox(Language.apply("处理单据返回数据失败"));
				return false;
			}

			if (!checkSuccess(resultXml))
			{
				new MessageBox(Language.apply("获取小票列表失败"));
				return false;
			}

			xmlTool = new XmlUtil(resultXml);
			Vector xmlDetail = xmlTool.fromXml("NewDataSet", "SaleInvoices", 0, SaleInvoicesDef.ref);
			Vector headList = new Vector();

			for (int i = 0; i < xmlDetail.size(); i++)
			{
				String[] data = (String[]) xmlDetail.get(i);
				SaleInvoicesDef sid = new SaleInvoicesDef();
				Transition.ConvertToObject(sid, data);
				if(sid.STATUS.equals("A"))sid.STATUS=Language.apply("开票");
				if(sid.DJLB.equals("1"))sid.DJLB=Language.apply("销售");else if(sid.DJLB.equals("4"))sid.DJLB=Language.apply("退货");
				if(sid.STATUS.equals("F")){
					new MessageBox(Language.apply("此单据已被取消"));
					return false;
				}
				headList.add(sid);
			}

			curBillNO = selectSaleInvoice(headList);

			if (curBillNO == null || curBillNO.trim().equals(""))
			{
				new MessageBox(Language.apply("获取单据失败"));
				return false;
			}

			// 记录获取下来的单据号
			saleBS.saleHead.str4 = curBillNO;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("处理单据头发生异常"));
			return false;
		}
	}

	private String selectSaleInvoice(Vector invo)
	{
		try
		{
			if (invo == null || invo.size() == 0)
				return null;

			Vector tabContent = new Vector();

			for (int i = 0; i < invo.size(); i++)
			{
				SaleInvoicesDef head = (SaleInvoicesDef) invo.get(i);
				tabContent.add(new String[] { String.valueOf(i + 1), head.BILLNO, head.STATUS, head.INVNO, head.DJLB, head.DATE, head.CHECKER, head.CUSTNO, head.OUGHTPAY, head.FACTPAY, head.POPZK });
			}

			String[] title = { Language.apply("序"), Language.apply("单据编号"), Language.apply("状 态"), Language.apply("票 号"), Language.apply("类 别"), Language.apply("日期"), Language.apply("开票员"), Language.apply("会员卡"), Language.apply("应  付"), Language.apply("实  付"), Language.apply("折  扣") };
			int[] width = { 30, 120, 65, 65, 65, 200, 80, 100, 100, 100, 100 };

			int choice = new MutiSelectForm().open(Language.apply("请从列表中选择一单进行交易"), title, width, tabContent, false, 800, 500, 775, 385, false, false, -1, false);

			if (choice == -1)
				return null;

			return ((SaleInvoicesDef) invo.get(choice)).BILLNO;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected boolean doGoods(SaleBS saleBS)
	{
		try
		{
			XmlUtil xmlTool = new XmlUtil();
			String xml = xmlTool.toXml("SaleGoods", new String[] { curBillNO }, new String[] { "billno" });
			String resultXml = (String) axis.executeFunction(202, xml);

			if ((resultXml) == null)
			{
				new MessageBox(Language.apply("单据明细返回数据失败"));
				return false;
			}

			if (!checkSuccess(resultXml))
			{
				new MessageBox(Language.apply("获取单据明细失败"));
				return false;
			}

			xmlTool = new XmlUtil(resultXml);
			Vector xmlDetail = xmlTool.fromXml("NewDataSet", "SaleGoods", 0, SaleCommodDef.ref);

			int goodsCount = 0;

			for (int i = 0; i < xmlDetail.size(); i++)
			{
				String[] data = (String[]) xmlDetail.get(i);
				SaleCommodDef goods = new SaleCommodDef();
				Transition.ConvertToObject(goods, data);

				if (!saleBS.findGoods(goods.BARCODE, goods.SALEMAN, goods.MFID, "ignore"))
				{
					return false;
				}
				else
				{
					if (saleBS.saleGoods == null || saleBS.saleGoods.size() < i + 1)
					{
						clearSaleBill();
					}

					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.get(i);

					if (sg.code.equals(goods.GDID) && sg.barcode.equals(goods.BARCODE) && sg.gz.equals(goods.MFID))
					{
						sg.sl = Convert.toDouble(goods.SL);
						sg.lsj = Convert.toDouble(goods.SJJE);
						sg.jg = Convert.toDouble(goods.PRICE);
						// 记录手工折扣
						sg.lszke += Convert.toDouble(goods.GTZK);
						sg.lszre += Convert.toDouble(goods.GTZR);
						sg.lszzk += Convert.toDouble(goods.GTZZK);
						sg.lszzr += Convert.toDouble(goods.GTZZR);

						// 重算折扣
						saleBS.getZZK(sg);
						saleBS.refreshSaleForm();

						goodsCount++;
					}

					goods = null;
				}
			}

			if (goodsCount != xmlDetail.size())
			{
				new MessageBox(Language.apply("单据处理有误,POS商品数量与获取单据中数量不匹配"));
				// saleBS.saleEvent.clearSell();
				// saleBS.refreshSaleForm();
				saleBS.initOneSale(saleBS.saletype);

				return false;
			}
			else
			{
				saleBS.calcHeadYsje();
				saleBS.refreshSaleForm();
				editBillFlag = true;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	protected boolean doSaleGoods(SaleBS saleBS)
	{
		if (curBillNO == null || curBillNO.trim().equals(""))
			return false;

		if (!doGoods(saleBS))
		{
			clearSaleBill();
			return false;
		}

		return true;

	}

	protected boolean doSalePay(String mktcode, String billno, double hjje, double zkje, String flag)
	{
		try
		{
			XmlUtil xmlTool = new XmlUtil();
			String xml = xmlTool.toXml("KpInvoice", new String[] { mktcode, billno, String.valueOf(hjje), String.valueOf(zkje), flag }, new String[] { "mkt", "billno", "cjje", "zkje", "flag" });
			String resultXml = (String) axis.executeFunction(203, xml);

			if ((resultXml) == null)
			{
				new MessageBox(Language.apply("单据发送至手持终端服务器响应失败"));
				return false;
			}

			if (!checkSuccess(resultXml))
			{
				new MessageBox(Language.apply("单据发送至手持终端服务器失败"));
				return false;
			}

			Vector retVec = xmlTool.fromXml("NewDataSet", "KpInvoice", 0, new String[] { "retcode", "retmsg" });

			if (retVec == null || retVec.size() == 0)
			{
				new MessageBox(Language.apply("单据发送至手持终端服务器有误"));
				return false;
			}

			String[] retLine = (String[]) retVec.get(0);

			if (!retLine[0].equals("00"))
			{
				if (retLine[1] != null)
					new MessageBox(retLine[1]);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			editBillFlag = false;
		}

	}

	public boolean init()
	{
		try
		{
			if (WebServiceConfigClass.getDefault().getEndPoint() == null || WebServiceConfigClass.getDefault().getEndPoint().equals(""))
				return false;

			if (axis.createWebServerConn())
				isEnable = true;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public int getChoice(Vector choice)
	{
		try{
			for (int i = 0 ; i < backVec.size();i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) backVec.elementAt(i);
				String[] row = (String[]) choice.elementAt(sgd.yrowno);
				row[6] = "Y";
				row[7] = String.valueOf(sgd.sl);
			}
			return 0;
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return -1;
		}
		
	}
}
