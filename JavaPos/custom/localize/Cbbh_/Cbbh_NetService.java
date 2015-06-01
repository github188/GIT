package custom.localize.Cbbh;

import java.util.Iterator;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Cbbh_NetService extends Cbbh_Crm_NetService//Bcrm_NetService
{
	
	 public static final int GETGOODSPOPLIST = 821;            //获取商品促销列表
	 public static final int SELECTGOODSPOP = 822;            //选择商品促销
	 public static final int ALLSALELIST = 823;            //获取整单促销规则
	 public static final int GETKPGOODSLIST = 824;            //获取开票商品明细
	 public static final int CHECKPAYMENT = 825;            //检查受限付款方式
	 public static final int GETCARDINFO = 826;            //获取卡电子劵信息
	 public static final int GETGOODSINFO = 827;            //获取商品子母码信息
	 public static final int GETZZFPHEAD = 828;            //获取转正发票头信息
	 public static final int GETZZFPDETAIL = 829;            //获取转正发票商品明细信息
	 public static final int GETZZFPPAYSALEDETAIL = 830;            //获取转正发票付款明细信息

	 public static final int GETTICKETPOP = 831;            //重新获取整单参与的促销
	 
	 public static final int GETGOODSPOPLIST_BACK = 841;            //获取商品促销列表(退换货)
	 public static final int SELECTGOODSPOP_BACK = 842;            //选择商品促销(退换货)
	 public static final int ALLSALELIST_BACK = 843;            //获取整单促销规则(退换货)
	 public static final int GETKPGOODSLIST_BACK = 844;            //获取开票商品明细(退换货) 
	 public static final int CHECKPAYMENT_BACK = 845;            //检查受限付款方式(退换货)
	 
	 public static final int GETWCCRULES = 833;					//查询微信券的收券规则
	 
		
	 public boolean findWCCRules(String billno,Vector wccrules)
	 {
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			String[] values = { billno};

			String[] args = { "billno"};

			cmdHead = new CmdHead(GETWCCRULES);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查询微信券收券规则数据失败!");
			
			Cbbh_WCCRuleDef rule;
			if (result == 0)
			{
				Vector data = new XmlParse(line.toString()).parseMeth(0, Cbbh_WCCRuleDef.ref);
				if (data.size() > 0)
				{
					for(Iterator it = data.iterator();it.hasNext();)
					{
						rule = new Cbbh_WCCRuleDef();
						String[] goodsline = (String [])it.next();
						if(Transition.ConvertToObject(rule, goodsline)) wccrules.add(rule);
					}
					return true;
				}
				
				return false;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	 }
	 
	public boolean sendCheckGoods(String djbh, CheckGoodsDef chkgd, StringBuffer checkgroupid, String checkcw, String checkrq, String isLastLine, String lineState)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			String[] values = { GlobalInfo.syjStatus.syjh, GlobalInfo.syjStatus.syyh, String.valueOf(chkgd.row), djbh, chkgd.code, chkgd.gz, chkgd.pdsl, chkgd.pdje, checkrq, lineState, chkgd.handInputcode };

			String[] args = { "syjh", "syyh", "rowno", "djbh", "code", "gz", "pdsl", "pdje", "pdrq", "oprtype", "inputcode" };

			cmdHead = new CmdHead(CmdDef.SENDCHECKGOODS);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "上传商品盘点数据失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "groupid" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					checkgroupid.append(row1[0]);
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	//返券卡消费
	public boolean sendFjkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
//		if(GlobalInfo.sysPara.isnewpop.equals("Y"))return true;
		if(!req.paycode.equals("1001"))return true;
		
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
	
	/**
	 * 输入修改商品：查找全部单品促销规则
	 * @param type:1=百货调用，2=家电开票销售调用
	 * @param popGoods:保存的临时促销商品
	 * @param custno：会员卡号
	 * @return：促销商品集合
	 */
	public Vector getTotalPop(String fphm,String type,Vector popGoods,String custno,String saletype)
	{
		CrmPopDetailDef cpd = null;
		
		Vector outgoodslist = new Vector();

		if (!GlobalInfo.isOnline) { return null; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			
			if(SellType.ISBACK(saletype))
			{
				aa = new CmdHead(GETGOODSPOPLIST_BACK);
			}
			else
			{
				aa = new CmdHead(GETGOODSPOPLIST);
			}

			// 明细打XML
			String line = "";
			
			if(popGoods.size() <= 0)
			{
				return outgoodslist;
			}

			for (int i = 0; i < popGoods.size(); i++)
			{
				cpd = (CrmPopDetailDef) popGoods.elementAt(i);
				line += Transition.ItemDetail(cpd, CrmPopDetailDef.ref);
			}

			line = Transition.closeTable(line, "CrmPopDetailDef", popGoods.size());
			
			
			String line1= Transition.ItemDetail(new String[]{type,custno}, new String[]{"type","custno"});
			line1 = Transition.closeTable(line1, "CrmPopDetailDef", 2);


//			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("获取整单促销信息失败!"));

			if (result == 0)
			{
				Vector goodslist = new XmlParse(line2.toString()).parseMeth(1, CrmPopDetailDef.ref);
				
				for(int i=0;i<goodslist.size();i++)
				{
					cpd = new CrmPopDetailDef();
					String[] goodsline = (String[]) goodslist.elementAt(i);
					if(Transition.ConvertToObject(cpd, goodsline))
					{
						outgoodslist.add(cpd);
					}
					/*cpd.billno=Convert.toInt(goodsline[0]);//单号
					cpd.rowno=Convert.toInt(goodsline[1]);//行号
					cpd.gdid=goodsline[2];//代码
					cpd.barcode=goodsline[3];//条码
					cpd.catid=goodsline[4];//类别
					cpd.ppid=goodsline[5];//品牌
					cpd.sj=Convert.toDouble(goodsline[6]);//建议售价
					cpd.minsj=Convert.toDouble(goodsline[7]);//最低售价
					cpd.sl=Convert.toDouble(goodsline[8]);//销售数量
					cpd.sjje=Convert.toDouble(goodsline[9]);//销售金额
					cpd.gdpopsj=Convert.toDouble(goodsline[10]);//常规促销售价
					cpd.gdpopzk=Convert.toDouble(goodsline[11]);//常规促销折扣
					cpd.gdpopzkfd=Convert.toDouble(goodsline[12]);//常规促销折扣供应商分担
					cpd.gdpopno=goodsline[13];//常规促销单号
					cpd.rulepopzk=Convert.toDouble(goodsline[14]);//规则促销折扣
					cpd.rulepopzkfd=Convert.toDouble(goodsline[15]);//规则促销折扣分担
					cpd.rulepopno=Convert.toDouble(goodsline[16]);//规则促销单号
					cpd.rulepopmemo=goodsline[17];//规则促销描述
					cpd.rulepopyhfs=goodsline[18];//规则促销优惠方式
					cpd.rulesupzkfd=Convert.toDouble(goodsline[19]);//规则促销供应商分担
					cpd.ruleticketno=Convert.toDouble(goodsline[20]);//规则促销返券类型
					cpd.zdrulepopzk=Convert.toDouble(goodsline[21]);//整单规则促销折扣
					cpd.zdrulepopzkfd=Convert.toDouble(goodsline[22]);//整单规则促销折扣分担
					cpd.zdrulepopno=goodsline[23];//整单规则促销单号
					cpd.zdrulepopmome=goodsline[24];//整单规则促销描述
					cpd.zdrulepopyhfs=goodsline[25];//整单规则促销优惠方式
					cpd.zdrulesupzkfd=Convert.toDouble(goodsline[26]);//整单规则促销供应商分担
					cpd.zdruleticketno=Convert.toDouble(goodsline[27]);//整单规则促销返券类型
					cpd.ticketno=Convert.toDouble(goodsline[28]);//所用电子券编号
					cpd.ticketname=goodsline[29];//所用电子券名称
					cpd.ticketzk=Convert.toDouble(goodsline[30]);//所用电子券抵扣金额
					cpd.jdsl=Convert.toDouble(goodsline[31]);//积点数量
					cpd.cjj=Convert.toDouble(goodsline[32]);//成交价
					cpd.cjje=Convert.toDouble(goodsline[33]);//成交金额
*/
					
				}
				
				//整单商品促销信息
				Vector goodspoplist = new XmlParse(line2.toString()).parseMeth(2, new String[] { "type","rowno1","trowno","popno","popmemo" });
				Cbbh_SaleBS.setpoplist(goodspoplist);
				
				//记录日志
				saveloginfo(fphm,aa.getCmdCode(),popGoods,outgoodslist);
				return outgoodslist;
			}
			else
			{
				//记录日志
				saveloginfo(fphm,aa.getCmdCode(),popGoods,outgoodslist);
				return outgoodslist;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return outgoodslist;
		}
		finally
		{
			cpd = null;
		}
	}

	/**
	 * 选择促销后：获取全部单品新促销规则
	 * @param popGoods:保存的临时促销商品
	 * @param type:促销类型
	 * @param rowno:商品行号
	 * @param popno:促销单号
	 * @param custno：会员卡号
	 * @return：促销商品集合
	 */
	public Vector getChoosePop(CrmPopDetailDef cpd1,String fphm,Vector popGoods,String type,String rowno,String popno,String custno,String saletype)
	{
		CrmPopDetailDef cpd = cpd1;
		
		popGoods.clear();
		popGoods.add(cpd);
		
		Vector outgoodslist = new Vector();

		if (!GlobalInfo.isOnline) { return null; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			
			if(SellType.ISBACK(saletype))
			{
				aa = new CmdHead(SELECTGOODSPOP_BACK);
			}
			else
			{
				aa = new CmdHead(SELECTGOODSPOP);
			}

			// 明细打XML
			String line = "";


				//cpd = (CrmPopDetailDef) popGoods.elementAt(i);
			line += Transition.ItemDetail(cpd, CrmPopDetailDef.ref);
			

			line = Transition.closeTable(line, "CrmPopDetailDef", popGoods.size());
			
			
			String line1= Transition.ItemDetail(new String[]{type,rowno,popno,custno}, new String[]{"type","rowno","popno","custno"});
			line1 = Transition.closeTable(line1, "CrmPopDetailDef", 2);


//			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("选择促销后获取新促销规则信息失败!"));

			if (result == 0)
			{
				Vector goodslist = new XmlParse(line2.toString()).parseMeth(2,CrmPopDetailDef.ref);
				
				for(int i=0;i<goodslist.size();i++)
				{
					cpd = new CrmPopDetailDef();
					String[] goodsline = (String[]) goodslist.elementAt(i);
					if(Transition.ConvertToObject(cpd, goodsline))
					{
						outgoodslist.add(cpd);
					}
				}
				/*for(int i=0;i<outgoodslist.size();i++)
				{
					cpd = new CrmPopDetailDef();
					String[] goodsline = (String[]) outgoodslist.elementAt(i);
					if(Transition.ConvertToObject(cpd, goodsline))
					{
						outgoodslist.add(cpd);
					}
				}*/
				
				//记录日志
				saveloginfo(fphm,aa.getCmdCode(),popGoods,outgoodslist);
				return outgoodslist;
			}
			else
			{
				//记录日志
				saveloginfo(fphm,aa.getCmdCode(),popGoods,outgoodslist);
				return outgoodslist;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return outgoodslist;
		}
		finally
		{
			cpd = null;
		}
	}
	
	/**
	 * 付款：获取整单促销，接劵，送劵列表
	 * @param popSaleGoods：保存的临时促销商品
	 * @param type：0输完商品后自动计算最大用券金额，1修改用券数量，2退货按付款键,3家电按付款键,4家电修改用券数量,5家电退货按付款键
	 * @param custno：会员卡号
	 * @param ticketno：用劵类型
	 * @param yqje：用劵金额
	 * @return：接劵集合
	 */
	public Vector getTicketPop(String fphm,Vector popSaleGoods,String type,String custno,String ticketno,String yqje,String saletype)
	{
		
		CrmPopDetailDef cpd = null;
		
		Vector jjlist = new Vector();
		
//		if(!Cbbh_SaleBS.sendBack){return jjlist;}

		if (!GlobalInfo.isOnline) { return null; }
		
		if(popSaleGoods.size() <= 0){return jjlist;}

		CmdHead aa = null;
		int result = -1;
		
		try
		{
			if(SellType.ISBACK(saletype))
			{
				aa = new CmdHead(ALLSALELIST_BACK);
			}
			else
			{
				aa = new CmdHead(ALLSALELIST);
			}

			// 明细打XML
			String line = "";

			for (int i = 0; i < popSaleGoods.size(); i++)
			{
				cpd = (CrmPopDetailDef) popSaleGoods.elementAt(i);
				line += Transition.ItemDetail(cpd, CrmPopDetailDef.ref);
			}

			line = Transition.closeTable(line, "CrmPopDetailDef", popSaleGoods.size());
			
			String line1= Transition.ItemDetail(new String[]{type,custno,ticketno,yqje}, new String[]{ "type", "custno", "ticketno", "yqje"});
			line1 = Transition.closeTable(line1, "CrmPopDetailDef", 2);
			
			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);
			
			result = HttpCall(line2, Language.apply("获取促销规则信息失败!"));

			if (result == 0)
			{
				Vector ret = new XmlParse(line2.toString()).parseMeth(1,new String[]{"flag","msg"});
				
				if(ret.size() > 0)
				{
					String[] ret1 = (String[]) ret.elementAt(0);
					/*if(!ret1[0].equals("0"))
					{
						new MessageBox(ret1[1]);
						return null;
					}
					//退货时如果有扣回劵提示扣回劵信息
					if(type.equals("2") && ret1[1].trim().length() > 0)
					{
						new MessageBox(ret1[1]);
					}*/
					if(ret1[1].trim().length() > 0)
					{
						new MessageBox(new StringBuffer(ret1[1]).toString());
						return null;
					}
				}
				
				Vector goodslist = new XmlParse(line2.toString()).parseMeth(2, CrmPopDetailDef.ref);
				if(goodslist.size() >0)
				{
					
					Cbbh_SaleBS.crmpopgoodsdetail.clear();
					
					for(int i=0;i<goodslist.size();i++)
					{
						CrmPopDetailDef cpd1 = new CrmPopDetailDef();
						String[] goodsline = (String[]) goodslist.elementAt(i);
						if(Transition.ConvertToObject(cpd1, goodsline))
						{
							Cbbh_SaleBS.crmpopgoodsdetail.add(cpd1);
						}
					}
					//记录日志
					saveloginfo(fphm,aa.getCmdCode(),popSaleGoods,Cbbh_SaleBS.crmpopgoodsdetail);
					
					/*outgoodslist = new Vector();
					for(int i=0;i<goodslist.size();i++)
					{
						CrmPopDetailDef cpd1 = new CrmPopDetailDef();
						String[] goodsline = (String[]) goodslist.elementAt(i);
						if(Transition.ConvertToObject(cpd1, goodsline))
						{
							outgoodslist.add(cpd1);
						}
					}*/
					
				}
				
				jjlist = new XmlParse(line2.toString()).parseMeth(3,new String[]{ "billno", "rowno", "ruletype", "rulepopzk", "rulepopzkfd", "rulepopno", "rulepopmemo", "ticketno", "ticketname", "qmz", "fqsl","jdsl" });
				
				StringBuffer msglog = new StringBuffer();
				for(int i =0;i<jjlist.size();i++)
				{
					String jjlog[] = (String[]) jjlist.elementAt(i);
					msglog.append("billno:"+jjlog[0]+",");
					msglog.append("rowno:"+jjlog[1]+",");
					msglog.append("ruletype:"+jjlog[2]+",");
					msglog.append("rulepopzk:"+jjlog[3]+",");
					msglog.append("rulepopzkfd:"+jjlog[4]+",");
					msglog.append("rulepopno:"+jjlog[5]+",");
					msglog.append("rulepopmemo:"+jjlog[6]+",");
					msglog.append("ticketno:"+jjlog[7]+",");
					msglog.append("ticketname:"+jjlog[8]+",");
					msglog.append("qmz:"+jjlog[9]+",");
					msglog.append("fqsl:"+jjlog[10]+",");
					msglog.append("jdsl:"+jjlog[11]);
				}
				PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+aa.getCmdCode()+"&号命令返回/整单参与接劵，送劵，满送信息:【"+msglog.toString()+"】");
				return jjlist;
			}
			else
			{
				return null;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return null;
		}
		finally
		{
			cpd = null;
		}
	}
	
/**
 * 获取家电开票单据
 * popGoods:开票商品明细
 * code:开票单据号
 */
	public Vector getJdKpGoods(Vector popGoods,String code)
	{
		CrmPopDetailDef cpd = null;
		
		Vector outgoodslist = new Vector();

		if (!GlobalInfo.isOnline) { return null; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(GETGOODSPOPLIST);

			// 明细打XML
			String line = "";
			
			if(popGoods.size() <= 0)
			{
				return outgoodslist;
			}

			for (int i = 0; i < popGoods.size(); i++)
			{
				cpd = (CrmPopDetailDef) popGoods.elementAt(i);
				line += Transition.ItemDetail(cpd, CrmPopDetailDef.ref);
			}

			line = Transition.closeTable(line, "CrmPopDetailDef", popGoods.size());
			
			
			String line1= Transition.ItemDetail(new String[]{code}, new String[]{"custno"});
			line1 = Transition.closeTable(line1, "CrmPopDetailDef", 2);


//			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("获取开票单据信息失败!"));

			if (result == 0)
			{
				Vector goodslist = new XmlParse(line2.toString()).parseMeth(1, CrmPopDetailDef.ref);
				
				for(int i=0;i<goodslist.size();i++)
				{
					cpd = new CrmPopDetailDef();
					String[] goodsline = (String[]) goodslist.elementAt(i);
					if(Transition.ConvertToObject(cpd, goodsline))
					{
						outgoodslist.add(cpd);
					}
					
				}
				
				//整单商品促销信息
				Vector goodspoplist = new XmlParse(line2.toString()).parseMeth(2, new String[] { "type","rowno1","trowno","popno","popmemo" });
				Cbbh_SaleBS.setpoplist(goodspoplist);
				
//				记录日志
				saveloginfo("",aa.getCmdCode(),popGoods,outgoodslist);
				
				return outgoodslist;
			}
			else
			{
				return outgoodslist;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return outgoodslist;
		}
		finally
		{
			cpd = null;
		}
	}
	
//	 发送销售小票
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{

		if(GlobalInfo.sysPara.isnewpop.equals("Y"))
		{
			//==2 家电上传小票时把单位名称转换为单位代码
			if(saleHead.num5 == 2)
			{
				for(int i =0;i<saleGoods.size();i++)
				{
					String unitname = ((SaleGoodsDef)saleGoods.elementAt(i)).unit;
					String unit = ((SaleGoodsDef)saleGoods.elementAt(i)).str2;
					
					//显示单位名称
					((SaleGoodsDef)saleGoods.elementAt(i)).unit =unit;
					((SaleGoodsDef)saleGoods.elementAt(i)).str2 = unitname;
				}
			}
			
			
			if(commandCode == 45)
			{
				//上传CRM
				return this.sendSaleData_Dos(saleHead, saleGoods, salePayment, retValue, '1');
			}
			
		}
		else
		{
			return super.sendSaleData(saleHead, saleGoods, salePayment, null);
		}
			

		Vector sg = new Vector();
		sg = savepopgoods(saleHead,saleGoods);
			
		
		CrmPopDetailDef cpd = null;
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

			for (int i = 0; i < sg.size(); i++)
			{
				cpd = (CrmPopDetailDef) sg.elementAt(i);
				//cpd.ysyjid = cpd.str1;
				//cpd.yinvno = Convert.toInt(cpd.str2); wangyong del 错了 2014.12.24
				line1 += Transition.ItemDetail(cpd, CrmPopDetailDef.sendsaleref);
			}


			line1 = Transition.closeTable(line1, "CrmPopDetailDef", sg.size());

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
			

			PosLog.getLog(this.getClass()).info(line.toString());

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			if (http == null)
			{
				result = HttpCall(line3, Language.apply("上传小票失败!"));
			}
			else
			{
				result = HttpCall(http, line3, Language.apply("上传小票失败!"));
			}
			

			//记录日志
			saveloginfo(String.valueOf(saleHead.fphm),aa.getCmdCode(),sg,null);
			
			// 返回应答数据
			if (result == 0 && retValue != null && line3.toString().trim().length() > 0)
			{
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(3, new String[] { "memo", "value" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					retValue.add(row[0]);
					retValue.add(row[1]);
				}

				PosLog.getLog(this.getClass()).info(line3.toString());
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
			cpd = null;
			salePayDef = null;
			if(GlobalInfo.sysPara.isnewpop.equals("Y"))
			{
				//==2 家电把单位名称转换回来
				if(saleHead.num5 == 2)
				{
					for(int i =0;i<saleGoods.size();i++)
					{
						String unitname = ((SaleGoodsDef)saleGoods.elementAt(i)).str2;
						String unit = ((SaleGoodsDef)saleGoods.elementAt(i)).unit;
						//显示单位名称
						((SaleGoodsDef)saleGoods.elementAt(i)).unit = unitname;
						((SaleGoodsDef)saleGoods.elementAt(i)).str2 = unit;
					}
				}
			}
		}
	}
	
	//重新给值
	public Vector savepopgoods(SaleHeadDef saleHead,Vector salegoods)
	{
		 Vector v = new Vector();
		
		for(int i=0;i<salegoods.size();i++)
		{
			
				SaleGoodsDef cgd = (SaleGoodsDef) salegoods.elementAt(i);
				
				CrmPopDetailDef cpd = new CrmPopDetailDef();
				
				String gdlist[] = cgd.str9.split(",");//常规促销
				String rulelist[] = cgd.str10.split(",");//规则促销
				String zdlist[] = cgd.str11.split(",");//整单规则促销
				String shopcardlist[] = cgd.str12.split(",");//店长卡
				String catcardlist[] = cgd.str13.split(",");//品类卡
				String tickelist[] = cgd.str15.split(",");//所用电子券
				
				
				cpd.barcode = cgd.barcode;//条码
				cpd.rowno = cgd.rowno;//行号
				cpd.gdid = cgd.code;//代码

				cpd.gbname = cgd.name;//品名
				cpd.vpec = cgd.uid;//规格
				cpd.guid =cgd.str3;//单位ID
				cpd.unit=cgd.unit;//单位
				cpd.unitname=cgd.str2;//单位名称
				
				
				cpd.catid = cgd.catid;//类别	
				cpd.ppid = cgd.ppcode;
				cpd.sj = cgd.jg;
				cpd.minsj = cgd.jg;
				cpd.sl = cgd.sl;
				cpd.sjje = cgd.sl*cgd.jg;
				cpd.gdpopsj = Convert.toDouble(gdlist[0]);//常规促销售价
				cpd.gdpopzk =Convert.toDouble(gdlist[1]);//常规促销折扣
				cpd.gdpopzkfd = Convert.toDouble(gdlist[2]);//常规促销折扣供应商分担
				if(gdlist.length > 3)
				{
					cpd.gdpopno = gdlist[3];//常规促销单号
				}
				else
				{
					cpd.gdpopno="";
				}
				cpd.rulepopzk =Convert.toDouble(rulelist[0]);//规则促销折扣
				cpd.rulepopzkfd = Convert.toDouble(rulelist[1]);//规则促销折扣分担
				if(rulelist.length > 2)
				{
					cpd.rulepopno = rulelist[2];//规则促销单号
				}
				else
				{
					cpd.rulepopno ="";
				}
				if(rulelist.length > 3)
				{
					cpd.rulepopmemo = rulelist[3];//规则促销描述
				}
				else
				{
					cpd.rulepopmemo = "";
				}
				if(rulelist.length > 4)
				{
					cpd.rulepopyhfs = rulelist[4];//规则促销优惠方式
				}
				else
				{
					cpd.rulepopyhfs = "";
				}
				cpd.rulesupzkfd = Convert.toDouble(rulelist[5]);//规则促销供应商分担
				cpd.ruleticketno = Convert.toDouble(rulelist[6]);//规则促销返券类型
				cpd.zdrulepopzk = Convert.toDouble(zdlist[0]);//整单规则促销折扣
				cpd.zdrulepopzkfd = Convert.toDouble(zdlist[1]);//整单规则促销折扣分担
				if(zdlist.length > 2)
				{
					cpd.zdrulepopno = zdlist[2];//整单规则促销单号
				}
				else
				{
					cpd.zdrulepopno = null;
				}
				if(zdlist.length > 3)
				{
					cpd.zdrulepopmome = zdlist[3];//整单规则促销描述
				}
				else
				{
					cpd.zdrulepopmome = null;
				}
				if(zdlist.length > 4)
				{
					cpd.zdrulepopyhfs = zdlist[4];//整单规则促销优惠方式
				}
				else
				{
					cpd.zdrulepopyhfs = "";
				}
				cpd.zdrulesupzkfd = Convert.toDouble(zdlist[5]);//整单规则促销供应商分担
				cpd.zdruleticketno = Convert.toDouble(zdlist[6]);//整单规则促销返券类型
				if(shopcardlist.length > 1)
				{
					if(!shopcardlist[0].equals("null") )
					{

						cpd.shopcard = shopcardlist[0];//店长卡号
					}
					else
					{

						cpd.shopcard = "";//店长卡号
					}
					cpd.shopcardzk = Convert.toDouble(shopcardlist[1]);//店长卡折扣
				}
				if(catcardlist.length > 1)
				{
					if(!catcardlist[0].equals("null"))
					{
						cpd.catcard = catcardlist[0];//品类卡号
					}
					else
					{
						cpd.catcard = "";
					}
					cpd.catcardzk = Convert.toDouble(catcardlist[1]);//品类卡折扣
				}
				cpd.ppcardzk = Convert.toDouble(cgd.str14);//品牌折扣 
				
				if(tickelist.length > 2)
				{
					cpd.ticketno = Convert.toDouble(tickelist[0]);//所用电子券编号
					if(!tickelist[1].equals("null") )
					{
						cpd.ticketname = tickelist[1];//所用电子券名称
					}
					else
					{
						cpd.ticketname = "";
					}
					cpd.ticketzk = Convert.toDouble(tickelist[2]);//所用电子券抵扣金额
				}
				cpd.lszk = cgd.num14;//临时折扣
				cpd.jdsl = cgd.num15;//积点数量
				cpd.cjje = cpd.sjje-(cpd.gdpopzk-cpd.rulepopzk-cpd.zdrulepopzk-cpd.lszk);//成交金额
				if(cpd.sl==0)
					cpd.cjj=cpd.cjje;
				else
					cpd.cjj =cpd.cjje / cpd.sl;//成交价
				
				if(saleHead != null)
				{
					if(SellType.ISBACK(saleHead.djlb) && (saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0))
					{
						//2=换货的商品
						cgd.num1 = 2;
						cpd.num1 =cgd.num1;
					}
				}
				
 				cpd.ysyjid = cgd.ysyjh;//原收银机
				cpd.yinvno = Convert.toInt(cgd.yfphm);//原小票
				cpd.str1 = cgd.ysyjh;
				cpd.num1 = cgd.yfphm;
				
				
				v.add(cpd);
		}
		return v;
	}
			
	//获取退货信息
	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm };
		String[] args = { "syjh", "code" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETBACKSALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("退货小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到退货小票头,退货小票不存在或已确认!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox(Language.apply("退货小票头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETBACKSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("退货小票明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, CrmPopDetailDef.sendsaleref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到退货小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				CrmPopDetailDef spd = new CrmPopDetailDef();

				if (Transition.ConvertToObject(spd, row,CrmPopDetailDef.sendsaleref))
				{
					saleDetailList.add(spd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询小票付款明细
			head = new CmdHead(CmdDef.GETBACKPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("付款明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到付款小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

	//获取开票商品
	public boolean getSaleGoodsBill(String code, Vector v1,String saletype)
	{
		
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		try
		{
			String[] values = {code };
			String[] args = {"code" };
			
	//		 查询开票小票明细
			if(SellType.ISBACK(saletype))
			{
				head = new CmdHead(GETKPGOODSLIST_BACK);
			}
			else
			{
				head = new CmdHead(GETKPGOODSLIST);
			}
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");
	
			if(result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CrmPopDetailDef.ref);
	
				if (v.size() < 1)
				{
					new MessageBox(Language.apply("没有查询到开票小票明细,开票小票不存在或已确认!"));
					return false;
				}
	
				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);
	
					CrmPopDetailDef spd = new CrmPopDetailDef();
	
					if (Transition.ConvertToObject(spd, row,CrmPopDetailDef.ref))
					{
						v1.add(spd);
					}
					
				}
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("退货小票明细查询失败!"));
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}
	
	/**
	 * 检查付款方式
	 * @param popSaleGoods
	 * @param type
	 * @param custno
	 * @param ticketno
	 * @param yqje
	 * @param saletype
	 * @return
	 */
	public boolean sendCheckPayMent(String fphm,Vector popSaleGoods,Vector salepay,String type,String custno,String paycode,String payje,String payno,Vector outpopgoodslist,Vector outpoplist,String saletype)
	{		
		CrmPopDetailDef cpd = null;
		
		if (!GlobalInfo.isOnline) { return false; }
		
		if(popSaleGoods.size() <= 0){return false;}
		
		CmdHead aa = null;
		int result = -1;
		
		try
		{
			
			if(SellType.ISBACK(saletype))
			{
				aa = new CmdHead(CHECKPAYMENT_BACK);
			}
			else
			{
				aa = new CmdHead(CHECKPAYMENT);
			}
			//商品明细打XML
			String line = "";

			for (int i = 0; i < popSaleGoods.size(); i++)
			{
				cpd = (CrmPopDetailDef) popSaleGoods.elementAt(i);
				line += Transition.ItemDetail(cpd, CrmPopDetailDef.ref);
			}

			line = Transition.closeTable(line, "CrmPopDetailDef", popSaleGoods.size());
			
			//已付款方式明细打XML
			String line3 = "";
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salepay.elementAt(i);
				line3 += Transition.ItemDetail(spd, SalePayDef.ref);
			}
	
			if(salepay.size() <=0)
			{
				line3= Transition.ItemDetail(new String[]{"0","","0",""}, new String[]{ "rowno", "paycode", "je", "payno"});
				line3 = Transition.closeTable(line3, "SalePayDef", 1);
			}
			else
			{
				line3 = Transition.closeTable(line3, "SalePayDef", salepay.size());
			}
			
			String line1= Transition.ItemDetail(new String[]{type,custno,paycode,payje,payno}, new String[]{ "type", "custno", "paycode", "payje","payno"});
			line1 = Transition.closeTable(line1, "CrmPopDetailDef", 1);
			
			
			
			// 合并
			line = Transition.getHeadXML(line+line3+line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);
			
			result = HttpCall(line2, Language.apply("检查受限付款方式失败!"));

			if (result == 0)
			{
				Vector ret = new XmlParse(line2.toString()).parseMeth(2,new String[]{"flag","msg"});
				

				Vector goodslist = new XmlParse(line2.toString()).parseMeth(3, CrmPopDetailDef.ref);
				if(goodslist.size() >0)
				{
					for(int i=0;i<goodslist.size();i++)
					{
						CrmPopDetailDef cpd1 = new CrmPopDetailDef();
						String[] goodsline = (String[]) goodslist.elementAt(i);
						if(Transition.ConvertToObject(cpd1, goodsline))
						{
							outpopgoodslist.add(cpd1);
						}
					}
				}
				
				if(ret.size() > 0)
				{
					String[] ret1 = (String[]) ret.elementAt(0);
					if(ret1[1].trim().length() > 0)
					{
						
						if(ret1[1].indexOf("ORA") != -1)
						{
							new MessageBox(new StringBuffer(ret1[1]).toString());
							return false;
						}
						else if(!ret1[1].trim().equals("N"))
						{
							MessageBox msg = new MessageBox(ret1[1], null, true);
							
							if(msg.verify() == GlobalVar.Key1)
							{
								//记录日志
								saveloginfo(fphm,aa.getCmdCode(),popSaleGoods,outpopgoodslist);
								
								Vector jjlist = new XmlParse(line2.toString()).parseMeth(4,new String[]{ "billno", "rowno", "ruletype", "rulepopzk", "rulepopzkfd", "rulepopno", "rulepopmemo", "ticketno", "ticketname", "qmz", "fqsl","jdsl" });
								
								if(jjlist.size() > 0)
								{
									for(int i=0;i<jjlist.size();i++)
									{
										String[] popjj = (String[]) jjlist.elementAt(i);
										outpoplist.add(popjj);
									}
								}
								
								StringBuffer msglog = new StringBuffer();
								for(int i =0;i<jjlist.size();i++)
								{
									String jjlog[] = (String[]) jjlist.elementAt(i);
									msglog.append("billno:"+jjlog[0]+",");
									msglog.append("rowno:"+jjlog[1]+",");
									msglog.append("ruletype:"+jjlog[2]+",");
									msglog.append("rulepopzk:"+jjlog[3]+",");
									msglog.append("rulepopzkfd:"+jjlog[4]+",");
									msglog.append("rulepopno:"+jjlog[5]+",");
									msglog.append("rulepopmemo:"+jjlog[6]+",");
									msglog.append("ticketno:"+jjlog[7]+",");
									msglog.append("ticketname:"+jjlog[8]+",");
									msglog.append("qmz:"+jjlog[9]+",");
									msglog.append("fqsl:"+jjlog[10]+",");
									msglog.append("jdsl:"+jjlog[11]);
								}
								PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+aa.getCmdCode()+"&号命令返回/整单参与接劵，送劵，满送信息:【"+msglog.toString()+"】");
							
								return true;
							}
							else
							{
								return false;
							}
						}
					}
					else
					{
						Vector jjlist = new XmlParse(line2.toString()).parseMeth(4,new String[]{ "billno", "rowno", "ruletype", "rulepopzk", "rulepopzkfd", "rulepopno", "rulepopmemo", "ticketno", "ticketname", "qmz", "fqsl","jdsl" });
						
						if(jjlist.size() > 0)
						{
							for(int i=0;i<jjlist.size();i++)
							{
								String[] popjj = (String[]) jjlist.elementAt(i);
								outpoplist.add(popjj);
							}
						}
						
						StringBuffer msglog = new StringBuffer();
						for(int i =0;i<jjlist.size();i++)
						{
							String jjlog[] = (String[]) jjlist.elementAt(i);
							msglog.append("billno:"+jjlog[0]+",");
							msglog.append("rowno:"+jjlog[1]+",");
							msglog.append("ruletype:"+jjlog[2]+",");
							msglog.append("rulepopzk:"+jjlog[3]+",");
							msglog.append("rulepopzkfd:"+jjlog[4]+",");
							msglog.append("rulepopno:"+jjlog[5]+",");
							msglog.append("rulepopmemo:"+jjlog[6]+",");
							msglog.append("ticketno:"+jjlog[7]+",");
							msglog.append("ticketname:"+jjlog[8]+",");
							msglog.append("qmz:"+jjlog[9]+",");
							msglog.append("fqsl:"+jjlog[10]+",");
							msglog.append("jdsl:"+jjlog[11]);
						}
						PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+aa.getCmdCode()+"&号命令返回/整单参与接劵，送劵，满送信息:【"+msglog.toString()+"】");
					
					}
				}
				//记录日志
				saveloginfo(fphm,aa.getCmdCode(),popSaleGoods,outpopgoodslist);
			}
			else
			{
				return false;
			}
			
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return false;
		}
		finally
		{
			cpd = null;
		}
		
		return true;
	}
	
	/**
	 * 获取会员卡电子劵信息
	 * @param hykh：会员卡号
	 * @return
	 */
	public Vector getCardInfo(String hykh)
	{
		Vector cardinfo = null;
		
		if (!GlobalInfo.isOnline) { return null; }
		
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		try
		{
			String[] values = {hykh };
			String[] args = {"hykh" };
			
			head = new CmdHead(GETCARDINFO);
		
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");
	
			if(result == 0)
			{
				cardinfo = new XmlParse(line.toString()).parseMeth(0, new String[]{ "billno", "rowno", "ruletype", "rulepopzk", "rulepopzkfd", "rulepopno", "rulepopmemo", "ticketno", "ticketname", "qmz", "fqsl","jdsl" });
		
			}
			else
			{
				return null;
			}
		}catch (Exception er)
		{
				PosLog.getLog(getClass()).error(er);
				er.printStackTrace();
				return null;
		}
		return cardinfo;
	}
	
	/**
	 * 获取商品子母码
	 * @param code
	 * @return
	 */
	public Vector getGoodsList(String code)
	{
		Vector goodslist = null;
		
		
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		try
		{
			String[] values = {code };
			String[] args = {"code" };
			
			head = new CmdHead(GETGOODSINFO);
		
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");
	
			if(result == 0)
			{
				goodslist = new XmlParse(line.toString()).parseMeth(0, new String[]{ "code", "name", "spcm", "sphs"});
		
			}
			else
			{
				return null;
			}
		}catch (Exception er)
		{
				PosLog.getLog(getClass()).error(er);
				er.printStackTrace();
				return null;
		}
		return goodslist;
	}
	
	//获取转正发票打印信息
	public boolean getZzFpInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm };
		String[] args = { "syjh", "code" };

		try
		{
			// 查询转正发票小票头
			head = new CmdHead(GETZZFPHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("转正发票小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到转正发票小票头,转正小票不存在或已确认!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox(Language.apply("转正发票小票头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询转正发票小票明细
			head = new CmdHead(GETZZFPDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("转正发票小票明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, CrmPopDetailDef.sendsaleref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到转正发票小票明细,转正小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				CrmPopDetailDef spd = new CrmPopDetailDef();

				if (Transition.ConvertToObject(spd, row,CrmPopDetailDef.sendsaleref))
				{
					saleDetailList.add(spd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询转正发票付款明细
			head = new CmdHead(GETZZFPPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("付款明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到付款小票明细,转正小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
		
	}
	
	//重新获取整单参与的促销
	/*public Vector getTicketPop(String fphm)
	{
		
		Vector jjlist = new Vector();

		if (!GlobalInfo.isOnline) { return null; }
		
		CmdHead aa = null;
		int result = -1;
		
		try
		{
			
			aa = new CmdHead(GETTICKETPOP);

			// 明细打XML
			StringBuffer line = new StringBuffer();
			
			String[] values = {""};
			String[] args = { "mktcode"};
			
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("获取整单参与的促销信息失败!"));

			if (result == 0)
			{
								
				jjlist = new XmlParse(line.toString()).parseMeth(1,new String[]{ "billno", "rowno", "ruletype", "rulepopzk", "rulepopzkfd", "rulepopno", "rulepopmemo", "ticketno", "ticketname", "qmz", "fqsl","jdsl" });
				
				StringBuffer msglog = new StringBuffer();
				for(int i =0;i<jjlist.size();i++)
				{
					String jjlog[] = (String[]) jjlist.elementAt(i);
					msglog.append("billno:"+jjlog[0]+",");
					msglog.append("rowno:"+jjlog[1]+",");
					msglog.append("ruletype:"+jjlog[2]+",");
					msglog.append("rulepopzk:"+jjlog[3]+",");
					msglog.append("rulepopzkfd:"+jjlog[4]+",");
					msglog.append("rulepopno:"+jjlog[5]+",");
					msglog.append("rulepopmemo:"+jjlog[6]+",");
					msglog.append("ticketno:"+jjlog[7]+",");
					msglog.append("ticketname:"+jjlog[8]+",");
					msglog.append("qmz:"+jjlog[9]+",");
					msglog.append("fqsl:"+jjlog[10]+",");
					msglog.append("jdsl:"+jjlog[11]);
				}
				PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+aa.getCmdCode()+"&号命令返回/整单参与接劵，送劵，满送信息:【"+msglog.toString()+"】");
				return jjlist;
			}
			else
			{
				return null;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return null;
		}
		finally
		{

		}
		
	}*/
	
	//保存通讯促销商品信息日志
	public void saveloginfo(String fphm,String code,Vector inputgoods,Vector outputgoods)
	{
		StringBuffer msglog = new StringBuffer();
		
		if(inputgoods == null)inputgoods = new Vector();
		
		if(outputgoods == null)outputgoods = new Vector();
		
		if((inputgoods.size() > 0 && outputgoods.size() >0) && inputgoods.size() != outputgoods.size())
		{
			new MessageBox("传入促销商品信息与返回促销商品信息不等！");
		}
		
		if(inputgoods.size() > 0)
		{
			//传过去的明细
			for (int i = 0; i < inputgoods.size(); i++) {
				CrmPopDetailDef inputcpd = (CrmPopDetailDef) inputgoods.elementAt(i);
				
				msglog = new StringBuffer();
				msglog = savegoodslog(inputcpd);
				PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+code+"&号命令传过去的促销明细:\n第"+(i+1)+"条【"+msglog.toString()+"】");
			}
		}
		else
		{
			msglog = new StringBuffer();
			PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+code+"&号命令传过去的促销明细:*】");
			
		}
		
		if(outputgoods.size() > 0)
		{
			//返回来的明细
			for (int j = 0; j < outputgoods.size(); j++) {
				CrmPopDetailDef outputcpd = (CrmPopDetailDef) outputgoods.elementAt(j);
				msglog = new StringBuffer();
				msglog = savegoodslog(outputcpd);
				PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+code+"&号命令返回来的促销明细:\n第"+(j+1)+"条【"+msglog.toString()+"】");
			}
		}
		else
		{
			msglog = new StringBuffer();
			PosLog.getLog(this.getClass()).info("[小票号："+fphm+"]调用&"+code+"&号命令返回来的促销明细:*】");
		}
	}
	
	private StringBuffer savegoodslog(CrmPopDetailDef cpd)
	{
		StringBuffer msglog = new StringBuffer();
		msglog.append("gbname:"+cpd.gbname+",");
		msglog.append("barcode:"+cpd.barcode+",");
		msglog.append("rowno:"+cpd.rowno+",");
		msglog.append("gdid:"+cpd.gdid+",");
		msglog.append("catid:"+cpd.catid+",");
		msglog.append("ppid:"+cpd.ppid+",");
		msglog.append("sj:"+cpd.sj+",");
		msglog.append("minsj:"+cpd.minsj+",");
		msglog.append("sl:"+cpd.sl+",");
		msglog.append("sjje:"+cpd.sjje+",");
		msglog.append("gdpopsj:"+cpd.gdpopsj+",");//常规促销售价
		msglog.append("gdpopzk:"+cpd.gdpopzk+",");//常规促销折扣
		msglog.append("gdpopzkfd:"+cpd.gdpopzkfd+",");//常规促销折扣供应商分担
		msglog.append("gdpopno:"+cpd.gdpopno+",");//常规促销单号
		msglog.append("rulepopzk:"+cpd.rulepopzk+",");//规则促销折扣
		msglog.append("rulepopzkfd:"+cpd.rulepopzkfd+",");//规则促销折扣分担
		msglog.append("rulepopno:"+cpd.rulepopno+",");//规则促销单号
		msglog.append("rulepopmemo:"+cpd.rulepopmemo+",");//规则促销描述
		msglog.append("rulepopyhfs:"+cpd.rulepopyhfs+",");//规则促销优惠方式
		msglog.append("rulesupzkfd:"+cpd.rulesupzkfd+",");//规则促销供应商分担
		msglog.append("ruleticketno:"+cpd.ruleticketno+",");//规则促销返券类型
		msglog.append("zdrulepopzk:"+cpd.zdrulepopzk+",");//整单规则促销折扣
		msglog.append("zdrulepopzkfd:"+cpd.zdrulepopzkfd+",");//整单规则促销折扣分担
		msglog.append("zdrulepopno:"+cpd.zdrulepopno+",");//整单规则促销单号
		msglog.append("zdrulepopmome:"+cpd.zdrulepopmome+",");//整单规则促销描述
		msglog.append("zdrulepopyhfs:"+cpd.zdrulepopyhfs+",");//整单规则促销优惠方式
		msglog.append("zdrulesupzkfd:"+cpd.zdrulesupzkfd+",");//整单规则促销供应商分担
		msglog.append("zdruleticketno:"+cpd.zdruleticketno+",");//整单规则促销返券类型
		msglog.append("ticketno:"+cpd.ticketno+",");//所用电子券编号
		msglog.append("ticketname:"+cpd.ticketname+",");//所用电子券名称
		msglog.append("ticketzk:"+cpd.ticketzk+",");//所用电子券抵扣金额
		msglog.append("jdsl:"+cpd.jdsl+",");//积点数量
		msglog.append("lszk:"+cpd.lszk+",");//临时折扣
		msglog.append("cjje:"+cpd.cjje+",");//成交金额
		msglog.append("cjj:"+cpd.cjj+",");//成交价
		msglog.append("num1:"+cpd.num1+",");//退换货标记
		msglog.append("str1:"+cpd.str1+",");//原收银机
		msglog.append("str2:"+cpd.str2+",");//原小票
		
		msglog.append("gbname:"+cpd.gbname+",");//品名
		msglog.append("vpec:"+cpd.vpec+",");//规格
		msglog.append("guid:"+cpd.guid+",");//单位ID
		msglog.append("unit:"+cpd.unit+",");//单位
		msglog.append("unitname:"+cpd.unitname+",");//单位名称
		
		return msglog;
	}

	
	/*public void setcrmpopgoods(Vector crmpopgoodsdetail) {
		popgoods = crmpopgoodsdetail;
	}*/
	
}
