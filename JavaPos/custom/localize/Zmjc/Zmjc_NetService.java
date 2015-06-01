package custom.localize.Zmjc;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bcrm.Bcrm_NetService;

public class Zmjc_NetService extends Bcrm_NetService
{
	/**
	 * 查找常旅卡信息
	 * @param cardNO
	 * @param clk
	 * @return
	 * wangyong by 2013.10.16
	 */
	public boolean findClkInfo(String cardNO, ClkDef clk)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { cardNO };
		String[] args = { "cardno" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append(Language.apply("findClkInfo() 查询请求："));
			sbLog.append("cardNO=[" + String.valueOf(cardNO) + "]");
			writelog(sbLog.toString());
			
			writeLogForFindClkInfo(Language.apply("findClkInfo() 请求前clk类值："), clk);//记录clk请求前日志
			//日志结束
			
			cmdHead = new CmdHead(Zmjc_CmdDef.ZMJC_FINDCLK);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("获取常旅卡信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, ClkDef.ref);
				if (v != null && v.size() > 0 )
                {
                    String[] row = (String[]) v.elementAt(0);
                    //获取对应的旅卡信息
                    if (Transition.ConvertToObject(clk, row))
                    {
                    	clk.cardno = cardNO;
                    	writeLogForFindClkInfo(Language.apply("findClkInfo() 响应后clk类值："), clk);//记录clk响应后日志
                        return true;
                    }
                }
				else
				{
					writelog(Language.apply("findClkInfo() 响应成功result=【{0}】，但无返回值line=【{1}】。", new Object[]{result + "",line.toString()}));
//					writelog("findClkInfo() 响应成功result=【" + result +"】，但无返回值line=【" + line.toString() + "】。");
				}
			}
			else
			{
				writelog(Language.apply("findClkInfo() 响应失败result=【{0}】.", new Object[]{result + ""}));
//				writelog("findClkInfo() 响应失败result=【" + result +"】.");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	
	//获取顾客信息 
	public boolean getSaleCfg()
	{
		//可参考 getSyjGrange() 函数
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
//		String lines = "";
		int result = -1;
//		String[] values = { ConfigClass.CashRegisterCode };
//		String[] args = { "syjh" };
		
		try
		{
			aa = new CmdHead(Zmjc_CmdDef.ZMJC_GETSALECFG);//GETSALECFG
			line.append(aa.headToString() + Transition.buildEmptyXML());
//			lines=aa.headToString()+Transition.buildEmptyXML();
//			line.append(lines);
			
			result = HttpCall(line, Language.apply("获取客户信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SaleCfgDef.ref);

				Zmjc_AccessLocalDB  accessLocalDB = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
				// 写入本地数据库
				if (!accessLocalDB.writeSaleCfg(v))
				{
					new MessageBox(Language.apply("保存客户信息失败!"));
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
//		return true;
	}
	
	//获取航班信息
	public boolean getFlights()
	{
		//可参考 getSyjGrange() 函数
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
//		String[] values = { ConfigClass.CashRegisterCode };
//		String[] args = { "syjh" };

		try
		{
			aa = new CmdHead(Zmjc_CmdDef.ZMJC_GETFLIGHTS);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			
			result = HttpCall(line, Language.apply("获取航班信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, FlightsDef.ref);

				Zmjc_AccessLocalDB  accessLocalDB = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
				// 写入本地数据库
				if (!accessLocalDB.writeFlights(v))
				{
					new MessageBox(Language.apply("保存航班信息失败!"));
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
//		return true;
	}
	
	//获取回程航班信息  yuxp add in 20140630
	public boolean getRetFlights()
	{
		//可参考 getSyjGrange() 函数
		if (!GlobalInfo.isOnline) { return false; }
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {GlobalInfo.sysPara.mktcode};
		String[] args = { "rfmkt" };

		try
		{
			aa = new CmdHead(Zmjc_CmdDef.ZMJC_GETRETFLIGHT);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));		
			result = HttpCall(line, Language.apply("获取回程航班信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, RetFlightDef.ref);

				Zmjc_AccessLocalDB  accessLocalDB = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
				// 写入本地数据库
				if (!accessLocalDB.writeRetFlights(v))
				{
					new MessageBox(Language.apply("保存回程航班信息失败!"));
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}
	
	public int sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Vector retValue, Http http, int commandCode)
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
			
			//顾客信息
			String line4 = "";
			String[] values = { "", "", "", "", "", "","", ""};
			if (saleCust!=null && saleCust.custCount()>0)
			{
				//当顾客信息不为空时
				values = new String[]{ CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCPASSPORTNO).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNATIONALITY).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCID).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCOTHERNO).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNUMBER).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNAME).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCSEX).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCMEMO).value, "")};
			}			
			
			String[] args = { CustInfoDef.CUST_SCPASSPORTNO, CustInfoDef.CUST_SCNATIONALITY, CustInfoDef.CUST_SCID, CustInfoDef.CUST_SCOTHERNO, CustInfoDef.CUST_SCNUMBER, CustInfoDef.CUST_SCNAME, CustInfoDef.CUST_SCSEX, CustInfoDef.CUST_SCMEMO };
			line4 = Transition.ItemDetail(values, args);
			line4 = Transition.closeTable(line4, "saleCustDef", 1);
				

			// 合并
			line = Transition.getHeadXML(line + line1 + line2 + line4);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			if (http == null)
			{
				//PosLog.getLog(this.getClass().getSimpleName()).info("sendSaleDataCust() localhttp");
				result = HttpCall(line3, Language.apply("上传小票失败!"));
			}
			else
			{
				//PosLog.getLog(this.getClass().getSimpleName()).info("sendSaleDataCust() gwkhttp=" + http.getSvrURL());
				result = HttpCall(http, line3, Language.apply("上传小票失败!"));
			}
			
			
			
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
			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("sendSaleDataCust() 上传小票 result=[" + result + "],line=[" + line3.toString() + "].");
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
	
	/**
	 * 检查护照号信息,同时获取大类限额信息
	 * @return boolean
	 */
	public int checkPassPort(String scpassportno, Vector retVec)
	{
		if (!GlobalInfo.isOnline) 
		{//当是脱网时,不检查护照号 
			
			retVec.add("N");//当前护照号未被使用 IsUse
			retVec.add("");//无大类限额信息 dlJE
			return 1; 			
		}
		
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ConfigClass.CashRegisterCode, scpassportno };
		String[] args = { "syjh", "scpassportno" };
		CmdHead sc = null;
		
		try
		{
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_CHECKPASSPORT);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("检查护照信息失败!"));
			
			if (result == 0)// 原 ==
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "IsUse", "dlJE"});

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(0);
						//IsUse
						retVec.addElement(row[0]);
						//dlJE
						retVec.addElement(row[1]);
						
//						String[] rowtemp    = new String[4];//
//						rowtemp[0] = "0";//
//						rowtemp[1] = "提示信息";//
//						rowtemp[2] = "Y";//
//						rowtemp[3] = "限制10000,剩下3000";//
//				        sppn.add(rowtemp);
					}
				}

				return 0;//返回成功
			}
			else
			{
				return result;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return 999;
		}
	}
	
	/**
	 * 获取打包码明细商品
	 * @param goodsPackList
	 * @param goodsCode
	 * @param gz
	 * @param jg
	 * @return
	 */
	public boolean getGoodsPackList(Vector goodsPackList, String goodsCode, String gz, double lsj)
	{
		if (!GlobalInfo.isOnline) 
		{
			//当是脱网时
			return false;	
		}
		
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { goodsCode, gz, String.valueOf(lsj) };
		String[] args = { "goodsCode", "gz", "jg" };//商品编码,柜组,零售价
		CmdHead sc = null;
		
		try
		{
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_GETGOODSPACKLIST);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, "");//获取打包码明细失败!
			
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "goodscode", "goodsbarcode", "je", "sl", "sjje", "zkfd","str1","str2","num1","num2"});//商品编码,商品条码,销售单价,数量,合计金额(销售单价*数量),折扣分担,备用1234

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						goodsPackList.addElement(v.elementAt(i));
						
						/*String[] rowtemp = new String[4];//
						rowtemp[0] = "00010233";//
						rowtemp[1] = "等放松点放松点";//
						rowtemp[2] = "10";//
						rowtemp[3] = "0.4";//
				        goodsPackList.add(rowtemp);
				        String[] rowtemp1 = new String[4];//
						rowtemp1[0] = "00010236";//
						rowtemp1[1] = "测试1";//
						rowtemp1[2] = "10";//
						rowtemp1[3] = "0.6";//
				        goodsPackList.add(rowtemp1);*/
					}
				}

				return true;//返回成功
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}
	
	//查找价随量变
	public boolean findBatchRule(SpareInfoDef sid, String code, String gz, String uid, String gys, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (cardno == null)
		{
			cardno = " ";
		}

		if (cardtype == null)
		{
			cardtype = " ";
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
							code,
							gz,
							catid,
							ppcode,
							gys,
							time,
							cardno,
							cardtype,
							isfjk,
							grouplist
							};
		String[] args = {
							"mktcode",
							"jygs",
							"code",
							"gz",
							"catid",
							"ppcode",
							"gys",
							"rqsj",
							"cardno",
							"cardtype",
							"isfjk",
							"grouplist"
							};

		try
		{
			head = new CmdHead(CmdDef.BatchRebate);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "");
		    
			String[] retname = {"pmbillno","addrule","Zklist","pmrule","zkmode","seq","zkfd","bz","maxnum"};
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					sid.pmbillno = row[0];
					sid.addrule = row[1];
					sid.Zklist = row[2];
					sid.pmrule = row[3];
					sid.etzkmode2 = row[4];
					sid.seq = row[5];
					sid.zkfd = row[6];
					sid.bz = row[7] != null?row[7]:"";
					sid.maxnum = Convert.toDouble(row[8]);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz,
            String uid, String rulecode, String catid,
            String ppcode, String time, String cardno,
            String cardtype, String isfjk, String grouplist, String djlb, Http http)
	{
			return this.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb, getMemCardHttp(CmdDef.FINDCRMPOP), CmdDef.FINDCRMPOP);
	}
    public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz,
                                  String uid, String rulecode, String catid,
                                  String ppcode, String time, String cardno,
                                  String cardtype, String isfjk, String grouplist, String djlb, Http http, int cmdcode)
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

        if (cardno == null)
        {
            cardno = " ";
        }

        if (cardtype == null)
        {
            cardtype = " ";
        }

        CmdHead head = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        String[] values = 
                          {
                              GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs ,code, gz, uid,
                              rulecode, catid, ppcode, time, cardno, cardtype, isfjk, grouplist, djlb
                          };
        String[] args = 
                        {
                            "mktcode","jygs", "code", "gz", "uid", "rule", "catid",
                            "ppcode", "rqsj", "cardno", "cardtype", "isfjk", "grouplist", "djlb"
                        };

        try
        {
            head = new CmdHead(cmdcode);//CmdDef.FINDCRMPOP);
            line.append(head.headToString() +
                        Transition.SimpleXML(values, args));

            //不显示错误信息
            result = HttpCall(http, line, "");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                                                                   GoodsPopDef.ref);

                if (v.size() > 0)
                {
                    String[] row = (String[]) v.elementAt(0);

                    if (Transition.ConvertToObject(popDef, row))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
    
    /*public int getGoodsDef(GoodsDef goodsDef, String searchFlag, String code, String gz, String scsj, String yhsj, String djlb)
	{
    	if (!GlobalInfo.isOnline) { return -1; }
    	
    	try
    	{
			// 提示错误
			NetService.getDefault().setErrorMsgEnable(true);
    		return super.getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, djlb);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return 999;
    	}
    	finally
    	{
			// 不提示错误
			NetService.getDefault().setErrorMsgEnable(false);
    	}
    	
	}*/
    
    /**
     * 检查顾客信息录入是否正确
     */
    public int checkCustomer(SaleCustDef saleCust)
	{
		if (!GlobalInfo.isOnline) 
		{//当是脱网时,不检查顾客信息 			
			return 0; 			
		}
		
		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead sc = null;
		
		try
		{
			
//			顾客信息
			String[] values = { "", "", "", "", "", "","", ""};
			if (saleCust!=null && saleCust.custCount()>0)
			{
				//当顾客信息不为空时
				values = new String[]{ CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCPASSPORTNO).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNATIONALITY).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCID).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCOTHERNO).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNUMBER).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCNAME).value, ""), 
				                       CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCSEX).value, ""), CommonMethod.isNull( saleCust.custItem(CustInfoDef.CUST_SCMEMO).value, "")};
			}			
			
			String[] args = { CustInfoDef.CUST_SCPASSPORTNO, CustInfoDef.CUST_SCNATIONALITY, CustInfoDef.CUST_SCID, CustInfoDef.CUST_SCOTHERNO, CustInfoDef.CUST_SCNUMBER, CustInfoDef.CUST_SCNAME, CustInfoDef.CUST_SCSEX, CustInfoDef.CUST_SCMEMO };
			
			
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_CHECKCUSTOMER);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("检查顾客信息失败!"));
			
			if (result == 0)
			{
				return 0;//返回成功
			}
			else
			{
				return result;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(er);
			return 999;
		}
	}
	

	/**
	 * 获取原退货小票的顾客信息
	 */
	public int getBackSaleCustomerInfo(String thSyjh, long thFphm, SaleCustDef saleCust)
	{
		if (!GlobalInfo.isOnline) 
		{
			return -999; 			
		}
		
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { thSyjh, String.valueOf(thFphm) };
		String[] args = { "syjh", "fphm" };
		CmdHead sc = null;
		
		try
		{
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_GETCUSTOMER_TH);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("获取原退货小票的顾客信息失败!"));
			
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { CustInfoDef.CUST_SCPASSPORTNO, 
				                                                                     CustInfoDef.CUST_SCNATIONALITY, 
				                                                                     CustInfoDef.CUST_SCID, 
				                                                                     CustInfoDef.CUST_SCOTHERNO, 
				                                                                     CustInfoDef.CUST_SCNUMBER, 
				                                                                     CustInfoDef.CUST_SCNAME, 
				                                                                     CustInfoDef.CUST_SCSEX, 
				                                                                     CustInfoDef.CUST_SCMEMO});

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(0);
						if (row.length>=8)
						{
							ParaNodeDef node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCPASSPORTNO;
							node.value = row[0];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNATIONALITY;
							node.value = row[1];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCID;
							node.value = row[2];
							saleCust.custAdd(node.code, node);

							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCOTHERNO;
							node.value = row[3];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNUMBER;
							node.value = row[4];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCNAME;
							node.value = row[5];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCSEX;
							node.value = row[6];
							saleCust.custAdd(node.code, node);
							
							node = new ParaNodeDef();
							node.code = CustInfoDef.CUST_SCMEMO;
							node.value = row[7];
							saleCust.custAdd(node.code, node);
							
							return 0;//返回成功	
						}
						 
				
					}
				}
				 
				PosLog.getLog(this.getClass().getSimpleName()).info("getBackSaleCustomerInfo(" + thSyjh + "," + thFphm + ") 获取原退货小票的顾客信息成功，但无数据: line=[" + line.toString() + "]");
				return -1;

			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("getBackSaleCustomerInfo(" + thSyjh + "," + thFphm + ") 获取原退货小票的顾客信息失败: result=[" + result + "]");
				return result;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(er);

			return 999;
		}
	}
	
	public int HttpCall(Http h, StringBuffer arg, String noMsg, boolean append)
	{
		int result = super.HttpCall(h, arg, noMsg, append);
		if (result != 0 && (Convert.toInt(this.getCmdCode()) != CmdDef.GETTASK) && (Convert.toInt(this.getCmdCode()) != CmdDef.GETNEWS))
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("result=[" + String.valueOf(result) + "],cmdcode=[" + this.getCmdCode() + "],ErrorMessage=[" + this.getErrorMessage() + "].");
		}
		return result;
	}
	

	//记录购物卡日志
	private void writeLogForFindClkInfo(String flag, ClkDef clk)
	{
		try
		{
			StringBuffer sbLog = new StringBuffer();
			if (clk==null)
			{
				sbLog.append(flag + " clk为null");
			}
			else
			{
				sbLog.append(flag + ":");
				sbLog.append("syjh=[" + String.valueOf(clk.cardno) + "],");
				sbLog.append("fphm=[" + String.valueOf(clk.cardname) + "],");
				sbLog.append("syyh=[" + String.valueOf(clk.nation) + "],");
				sbLog.append("code=[" + String.valueOf(clk.passport) + "],");
				sbLog.append("zkl=[" + String.valueOf(clk.ljhb) + "],");
				sbLog.append("name=[" + String.valueOf(clk.iszk) + "],");
				sbLog.append("nation=[" + String.valueOf(clk.zklb) + "],");
				sbLog.append("passport=[" + String.valueOf(clk.isaq) + "],");
				sbLog.append("ljhb=[" + String.valueOf(clk.je_aq) + "],");
				sbLog.append("ljrq=[" + String.valueOf(clk.isbq) + "],");
				sbLog.append("ljsj=[" + String.valueOf(clk.je_bq) + "],");
				sbLog.append("gklb=[" + String.valueOf(clk.str1) + "],");
				sbLog.append("ZJLB=[" + String.valueOf(clk.str2) + "],");
				sbLog.append("gender=[" + String.valueOf(clk.str3) + "],");
				sbLog.append("birth=[" + String.valueOf(clk.str4) + "],");
				sbLog.append("age=[" + String.valueOf(clk.num1) + "],");
				sbLog.append("email=[" + String.valueOf(clk.num2) + "],");
				sbLog.append("mobile=[" + String.valueOf(clk.num3) + "],");
				sbLog.append("isdx=[" + String.valueOf(clk.num4) + "].");
				
			}
			
			writelog(sbLog.toString());
		}
		catch(Exception ex)
		{
			writelog(ex);
		}
	}
	
	//日志
	protected void writelog(String loginfo)
	{
		try
		{
			PosLog.getLog(getClassSimpleName()).info(String.valueOf(loginfo));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();			
		}
	}
	protected void writelog(Exception ex)
	{
		try
		{
			PosLog.getLog(getClassSimpleName()).error(ex);
		}
		catch(Exception e)
		{
			ex.printStackTrace();
		}
	}
	
	protected String getClassSimpleName()
	{
		return this.getClass().getSimpleName();
	}
	
	public boolean sendFjkSale_Clk(MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(Zmjc_CmdDef.SENDFJK_CLK);//CmdDef.SENDFJK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(line, Language.apply("(常旅)返券卡交易失败!!"));

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
	
	/**
	 * 会员卡激活
	 * @param cardno
	 * @param cid
	 * @param phone
	 * @param cidType
	 * @return 0表示成功
	 */
	public int hykJH(String cardno, String cid, String phone, String cidType)
	{
		if (!GlobalInfo.isOnline) 
		{ 			
			new MessageBox("会员卡激活失败：脱网状态无法激活！");
			return -1; 			
		}
		
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { cardno, cid, phone, cidType, "", "", "", "0", "0", "0" };
		String[] args = { "cardno", "cid", "phone", "cidType", "str1", "str2", "str3", "num1", "num2", "num3" };
		CmdHead sc = null;
		
		try
		{
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_HYKJH);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(line, Language.apply("会员卡激活失败"));
			
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "opkey", "opmsg", "str1", "str2", "str3", "num1", "num2", "num3"});

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(0);
						String opkey=row[0];
						
						if (opkey!=null && opkey.equalsIgnoreCase("1"))
						{//OPKEY详细状态信息（1校验通过，2身份证校验不通过，3手机号校验不通过，4校验异常）
							return 0;
						}
						new MessageBox(Language.apply("会员卡激活失败") + ":\n" + String.valueOf(row[1]));	
						return -1;
					}
				}

				return 0;//返回成功
			}
			else
			{
				return result;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();

			return 999;
		}
	}
		
	/**
	 * 顾客暂存单校验 add by yuxp in 20140630
	 */
	public int CheckZCDInfo(RetFlightDef zcd, String fphm, String syjh,
			String hcsj, String hcrq, String phone) {
		if (!GlobalInfo.isOnline) {
			new MessageBox("脱网状态下无法校验顾客暂存单");
			return 0;
		}

		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { fphm, syjh, GlobalInfo.sysPara.mktcode, zcd.rfid, zcd.rfname, hcsj, hcrq, phone,
				 "0", "0", "0", "", "", "", "", "" };
		String[] args = { "vbillno", "vsyjh", "vmkt", "vrfid", "vhchb", "vhcsj",
				"vhcrq", "vlxfs", "vnum1", "vnum2", "vnum3", "vstr1", "vstr2",
				"vstr3", "vstr4", "vstr5" };
		CmdHead sc = null;

		try {
			sc = new CmdHead(Zmjc_CmdDef.ZMJC_CHECKZCD);
			line.append(sc.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("客户暂存单校验失败"));

			if (result == 0) {
				/*Vector v = new XmlParse(line.toString()).parseMeth(0,
						new String[] { "retcode", "retmsg" });

				if (v.size() > 0) {
					for (int i = 0; i < v.size(); i++) {
						String[] row = (String[]) v.elementAt(0);
						String retcode = row[0];

						if (retcode != null && retcode.equalsIgnoreCase("1")) {
							return 1;
						}
						new MessageBox(Language.apply("客户暂存单校验失败") + ":\n"
								+ String.valueOf(row[1]));
						return 0;
					}
				}*/

				return 1;// 返回成功
			} else {
				return -1;
			}
		} catch (Exception er) {
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return 999;
		}
	}
	
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
			
			if(shd!=null && shd.str2!=null && shd.str2.equalsIgnoreCase("H"))
			{//wangyong add by 2014.7.29 
				new MessageBox("原单小票存在换购，所以不允许退货!");
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

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到退货小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleDetailList.add(sgd);
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
	
	public int getGoodsDef(GoodsDef goodsDef, String searchFlag, String code, String gz, String scsj, String yhsj, String djlb)
	{
		int intRet = super.getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, djlb);
		if (intRet==0 && goodsDef!=null)
		{
			goodsDef.dblMaxYhSl = goodsDef.num2;//普通分期促销限量数量 wangyong add by 2014.7.21
		}
		return intRet;
	}
}
