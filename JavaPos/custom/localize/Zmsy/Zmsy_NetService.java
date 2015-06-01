package custom.localize.Zmsy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.InvoiceInfoDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zmjc.FlightsDef;
import custom.localize.Zmjc.Zmjc_NetService;

/**
 * 中免三亚
 * @author sf
 *
 */
public class Zmsy_NetService extends Zmjc_NetService
{
	public static Http GWKHttp = null;// 购物卡独立HTTP

	//获取补税金额
	public boolean sendLimitJEStr(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { strList, gwkh };
		String[] args = { "vcode", "vgwkh" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("sendLimitJEStr() FINDTAX 请求：");
			sbLog.append("vcode=[" + String.valueOf(strList) + "],");
			sbLog.append("vgwkh=[" + String.valueOf(gwkh) + "],");
			writelog(sbLog.toString());
						
			//日志结束
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_FINDTAX);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_FINDTAX), line, "获取补税金额信息失败!");
			
			if (result == 0)
			{
				String[] retname = new String[]{ "rcode" };
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);
				//根据航班查询实时的航班信息
				if(v != null && v.size() > 0)
				{
					String[] retArr = (String[])v.elementAt(0);
					if (retArr.length>=1)
					{
						sbList.append(retArr[0]); 
						
						//日志开始
						sbLog = new StringBuffer();
						sbLog.append("sendLimitJEStr() 响应成功：");
						sbLog.append("rcode=[" + String.valueOf(retArr[0]) + "].");
						writelog(sbLog.toString());			
						//日志结束
					}
					return true;
				}
				else
				{
					writelog("sendLimitJEStr() 响应成功result=【" + result +"]，但无返回值line=[" + line.toString() + "].");
				}
			}
			else
			{
				writelog("sendLimitJEStr() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	

	//获取暂缴税金
	public boolean sendLimitJEStr_ZJSJ(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { strList, gwkh };
		String[] args = { "vcode", "vgwkh" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("sendLimitJEStr_ZJSJ() JAVA_FINDTAX_ALL 请求：");
			sbLog.append("vcode=[" + String.valueOf(strList) + "],");
			sbLog.append("vgwkh=[" + String.valueOf(gwkh) + "],");
			writelog(sbLog.toString());
						
			//日志结束
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_FINDTAX_ALL);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_FINDTAX_ALL), line, "获取暂缴税金信息失败!");
			
			if (result == 0)
			{
				String[] retname = new String[]{ "rcode" };
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);
				
				if(v != null && v.size() > 0)
				{
					String[] retArr = (String[])v.elementAt(0);
					if (retArr.length>=1)
					{
						sbList.append(retArr[0]); 
						
						//日志开始
						sbLog = new StringBuffer();
						sbLog.append("sendLimitJEStr_ZJSJ() 响应成功：");
						sbLog.append("rcode=[" + String.valueOf(retArr[0]) + "].");
						writelog(sbLog.toString());			
						//日志结束
					}
					return true;
				}
				else
				{
					writelog("sendLimitJEStr_ZJSJ() 响应成功result=【" + result +"]，但无返回值line=[" + line.toString() + "].");
				}
			}
			else
			{
				writelog("sendLimitJEStr_ZJSJ() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	
	//检查非超额商品的限量
	public boolean checkLessGoodsSL(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { strList, gwkh };
		String[] args = { "vcode", "vgwkh" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("checkLessGoodsSL() CALUNUM 请求：");
			sbLog.append("vcode=[" + String.valueOf(strList) + "],");
			sbLog.append("vgwkh=[" + String.valueOf(gwkh) + "],");
			writelog(sbLog.toString());
						
			//日志结束
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_CALUNUM);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_CALUNUM), line, "检查非超额商品的限量信息失败!");
			
			if (result == 0)
			{
				String[] retname = new String[]{ "rcode" };
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);
				//根据航班查询实时的航班信息
				if(v != null && v.size() > 0)
				{
					//hb.addElement(v.elementAt(0));
					String[] retArr = (String[])v.elementAt(0);
					if (retArr.length>=1)
					{
						sbList.append(retArr[0]); 
						
						//日志开始
						sbLog = new StringBuffer();
						sbLog.append("checkLessGoodsSL() 响应成功：");
						sbLog.append("rcode=[" + String.valueOf(retArr[0]) + "].");
						writelog(sbLog.toString());			
						//日志结束
					}
					return true;
				}
				else
				{
					writelog("checkLessGoodsSL() 响应成功result=【" + result +"]，但无返回值line=[" + line.toString() + "].");
				}
			}
			else
			{
				writelog("checkLessGoodsSL() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	
	/**
	 * 提货地点
	 * @return boolean 返回是否成功写入本地
	 *//*
	public boolean getTourist_THPlace()
	{
		// TODO Auto-generated method stub
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.ZMSY_FINDTHPLACE);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			
			result = HttpCall(line, "根据轨道号获取购物卡信息失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, THPlaceDef.ref);
				
				if (v != null && v.size() > 0)
                {
					Zmsy_AccessLocalDB  accessLocalDB = (Zmsy_AccessLocalDB)AccessLocalDB.getDefault();
					// 写入本地数据库
					if (!accessLocalDB.writeTourist_THPlace(v))
					{
						new MessageBox(" 写入本地证件类型失败!");
						return false;
					}
					return true;
                }
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
		return false;
	}*/
	
	/*
	*//**
	 * 证件类型
	 * @return boolean 返回是否成功写入本地
	 *//*
	public boolean getZJType()
	{
		// TODO Auto-generated method stub
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.ZMSY_FINDZJTYPE);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			
			result = HttpCall(line, "根据轨道号获取购物卡信息失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, ZJTypeDef.ref);
				
				if ( v != null && v.size() > 0)
                {
					Zmsy_AccessLocalDB  accessLocalDB = (Zmsy_AccessLocalDB)AccessLocalDB.getDefault();
					// 写入本地数据库
					if (!accessLocalDB.writeZJType(v))
					{
						new MessageBox(" 写入本地航班信息失败!");
						return false;
					}
					return true;
                }
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
		return false;
	}
	*/
	
	
	/**
	 * 获取购物卡信息
	 * @param cardNO 购物卡号
	 * @param zjType 证件类型
	 * @param zjNO 证件号码(护照号)
	 * @param custType 顾客类别
	 * @param gwk 返回的购物卡信息
	 * @return
	 */
	public boolean findGwkInfo(String cardNO,String zjType, String zjNO, String custType, GwkDef gwk)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { cardNO, zjNO, zjType, custType };
		String[] args = { "cardno", "passport", "ZJLB", "gklb" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("findGwkInfo() 查询请求：");
			sbLog.append("cardNO=[" + String.valueOf(cardNO) + "],");
			sbLog.append("zjNO=[" + String.valueOf(zjNO) + "],");
			sbLog.append("zjType=[" + String.valueOf(zjType) + "],");
			sbLog.append("custType=[" + String.valueOf(custType) + "],");
			writelog(sbLog.toString());
			
			writeLogForFindGwkInfo("findGwkInfo() 请求前gwk类值：", gwk);//记录gwk请求前日志
			//日志结束
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_GETGWKINFO);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_GETGWKINFO), line, "获取购物卡信息失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GwkDef.ref);
				if (v != null && v.size() > 0 )
                {
                    String[] row = (String[]) v.elementAt(0);
                    //获取对应的购物卡信息
                    if (Transition.ConvertToObject(gwk, row))
                    {
                    	String[] arr = gwk.ljrq.split(" ");
                    	gwk.ljrq = arr[0];
                    	if(arr.length>=2)
                    	{
                    		gwk.ljsj=arr[1];
                    	}
                    	writeLogForFindGwkInfo("findGwkInfo() 响应后gwk类值：", gwk);//记录gwk响应后日志
                        return true;
                    }
                }
				else
				{
					writelog("findGwkInfo() 响应成功result=【" + result +"]，但无返回值line=【" + line.toString() + "】。");
				}
			}
			else
			{
				writelog("findGwkInfo() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	
	/**
	 * 保存购物卡信息到POSDB
	 * @return boolean 返回是否成功获取
	 */
	public boolean sendGwkInfo(GwkDef gwk, StringBuffer sbMsg)
	{
		// TODO Auto-generated method stub
		if (!GlobalInfo.isOnline) { 
			sbMsg.append("非联网状态下无法保存卡");
			return false; 
			}
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		
		try
		{
			writeLogForFindGwkInfo("sendGwkInfo() 请求前gwk类值：", gwk);//记录gwk响应后日志
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_SENDGWK);			
			line.append(cmdHead.headToString() + Transition.ConvertToXML(gwk));

			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_GETGWKINFO), line, "保存购物卡信息到POSDB失败!");
			
			if (result == 0)
			{
				writelog("sendGwkInfo() 响应成功result=【" + result +"].");
				return true;//返回成功
			}
			else
			{
				writelog("sendGwkInfo() 响应失败result=【" + result +"].");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
	}
	
	public boolean sendBankLog(BankLogDef bcd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			cmdHead = new CmdHead(CmdDef.SENDBANKCARD);
			line.append(cmdHead.headToString() + Transition.ConvertToXML(bcd));

			result = HttpCall(getGwkHttp(CmdDef.SENDBANKCARD), line, Language.apply("金卡日志上传失败!"));

			if (result == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 保存海关平台信息到POSDB
	 * @param code 购物号
	 * @param ldcs 购物次数double
	 * @param zssl 征税数量 double
	 * @param gwje 已购物金额double
	 * @param xgjs 18类串
	 * @return boolean
	 */
	public boolean sendHGPTInfo(String code, double ldcs, double zssl, double gwje, String xgjs, StringBuffer sbMsg)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code, String.valueOf(ldcs), String.valueOf(zssl), String.valueOf(gwje), xgjs };
		String[] args = { "code", "ldcs", "zssl", "gwje", "xgjs" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("sendHGPTInfo() 查询请求：");
			sbLog.append("code=[" + String.valueOf(code) + "],");
			sbLog.append("ldcs=[" + String.valueOf(ldcs) + "],");
			sbLog.append("zssl=[" + String.valueOf(zssl) + "],");
			sbLog.append("gwje=[" + String.valueOf(gwje) + "],");
			sbLog.append("xgjs=[" + String.valueOf(xgjs) + "].");
			writelog(sbLog.toString());			
			//日志结束
			
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_SENDHGPT);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_SENDHGPT), line, "保存海关平台信息到POSDB失败!");
			
			if (result == 0)
			{
				writelog("sendHGPTInfo() 响应成功result=【" + result +"].");
				return true;
			}
			else
			{
				writelog("sendHGPTInfo() 响应失败result=【" + result +"].");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
	}
	
	/**
	 *  实时查找航班
	 * @param ljhb in离境航班
	 * @param ljrq in离境日期  yyyy-mm-dd
	 * @param gklb in顾客类别
	 * @param GwkDef out实时航班信息
	 * @return
	 */
	public boolean findHangBan(String ljhb, String ljrq, String ljsj, String gklb, GwkDef gwk)
	{
		if (!GlobalInfo.isOnline) { 
			new MessageBox("网络不通，查找实时航班信息失败!");
			return false; 
		}
		
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ljhb , ljrq, ljsj, gklb };
		String[] args = { "ljhb", "ljrq", "ljsj", "gklb" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("findHangBan() 查询请求：");
			sbLog.append("ljhb=[" + String.valueOf(ljhb) + "],");
			sbLog.append("ljrq=[" + String.valueOf(ljrq) + "],");
			sbLog.append("ljsj=[" + String.valueOf(ljsj) + "],");
			sbLog.append("gklb=[" + String.valueOf(gklb) + "].");
			writelog(sbLog.toString());			
			//日志结束
			
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_FINDFLIGHT);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_FINDFLIGHT), line, "查找实时航班信息失败!");
			
			if (result == 0)
			{
				String[] retname = new String[]{ "ljrq", "ljsj", "thdd", "gklb" };
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);
				//根据航班查询实时的航班信息
				if(v != null && v.size() > 0)
				{
					//hb.addElement(v.elementAt(0));
					String[] retArr = (String[])v.elementAt(0);
					if (retArr.length>=4)
					{
						gwk.ljrq = retArr[0]; 
						gwk.ljsj = retArr[1];
						gwk.thdd = retArr[2];
						gwk.gklb = retArr[3];
						

						//日志开始
						sbLog = new StringBuffer();
						sbLog.append("findHangBan() 响应成功：");
						sbLog.append("ljrq=[" + String.valueOf(gwk.ljrq) + "],");
						sbLog.append("ljsj=[" + String.valueOf(gwk.ljsj) + "],");
						sbLog.append("thdd=[" + String.valueOf(gwk.thdd) + "],");
						sbLog.append("gklb=[" + String.valueOf(gwk.gklb) + "].");
						writelog(sbLog.toString());			
						//日志结束
					}
					return true;
				}
				else
				{
					writelog("findHangBan() 响应成功result=【" + result +"]，但无返回值line=[" + line.toString() + "].");
				}
			}
			else
			{
				writelog("findHangBan() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
		return false;
	}
	

	//获取航班信息
	public boolean getFlights(Vector vecFlights)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMJC_GETFLIGHTS);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			//writelog("getFlights() 1");
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMJC_GETFLIGHTS), line, "获取航班信息失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, FlightsDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecFlights != null)
				{
					/*for(int i=0; i<v.size(); i++)
					{
						vecFlights.addElement(v.elementAt(i));
					}
					*/
					String[] row;
					FlightsDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new FlightsDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecFlights.addElement(f);
						
					}
				}
				

				/*Zmjc_AccessLocalDB  accessLocalDB = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
				// 写入本地数据库
				if (!accessLocalDB.writeFlights(v))
				{
					new MessageBox("保存航班信息失败!");
				}*/
				writelog("getFlights() 响应成功result=【" + result +"】，vecFlights.size=【" + vecFlights.size() + "】.");
				return true;
			}
			else
			{
				writelog("getFlights() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
	
	

	//记录购物卡日志
	private void writeLogForFindGwkInfo(String flag, GwkDef gwk)
	{
		try
		{
			StringBuffer sbLog = new StringBuffer();
			if (gwk==null)
			{
				sbLog.append(flag + " gwk为null");
			}
			else
			{
				sbLog.append(flag + ":");
				sbLog.append("syjh=[" + String.valueOf(gwk.syjh) + "],");
				sbLog.append("fphm=[" + String.valueOf(gwk.fphm) + "],");
				sbLog.append("syyh=[" + String.valueOf(gwk.syyh) + "],");
				sbLog.append("code=[" + String.valueOf(gwk.code) + "],");
				sbLog.append("zkl=[" + String.valueOf(gwk.zkl) + "],");
				sbLog.append("name=[" + String.valueOf(gwk.name) + "],");
				sbLog.append("nation=[" + String.valueOf(gwk.nation) + "],");
				sbLog.append("passport=[" + String.valueOf(gwk.passport) + "],");
				sbLog.append("ljhb=[" + String.valueOf(gwk.ljhb) + "],");
				sbLog.append("ljrq=[" + String.valueOf(gwk.ljrq) + "],");
				sbLog.append("ljsj=[" + String.valueOf(gwk.ljsj) + "],");
				//sbLog.append("ljdd=[" + String.valueOf(gwk.ljdd) + "],");
				sbLog.append("gklb=[" + String.valueOf(gwk.gklb) + "],");
				sbLog.append("ZJLB=[" + String.valueOf(gwk.zjlb) + "],");
				sbLog.append("gender=[" + String.valueOf(gwk.gender) + "],");
				sbLog.append("birth=[" + String.valueOf(gwk.birth) + "],");
				sbLog.append("age=[" + String.valueOf(gwk.age) + "],");
				sbLog.append("email=[" + String.valueOf(gwk.email) + "],");
				sbLog.append("mobile=[" + String.valueOf(gwk.mobile) + "],");
				sbLog.append("isdx=[" + String.valueOf(gwk.isdx) + "],");
				sbLog.append("fzjg=[" + String.valueOf(gwk.fzjg) + "],");
				sbLog.append("xe=[" + String.valueOf(gwk.xe) + "],");
				sbLog.append("sxje=[" + String.valueOf(gwk.sxje) + "],");
				sbLog.append("status=[" + String.valueOf(gwk.status) + "],");
				sbLog.append("ispdxe=[" + String.valueOf(gwk.ispdxe) + "],");
				sbLog.append("sjcd=[" + String.valueOf(gwk.sjcd) + "],");
				sbLog.append("bsjs=[" + String.valueOf(gwk.bsjs) + "],");
				sbLog.append("xgjs=[" + String.valueOf(gwk.xgjs) + "],");
				sbLog.append("shts=[" + String.valueOf(gwk.shts) + "],");
				sbLog.append("qje=[" + String.valueOf(gwk.qje) + "],");
				sbLog.append("isAccessHG=[" + String.valueOf(gwk.isAccessHG) + "],");
				sbLog.append("thdd=[" + String.valueOf(gwk.thdd) + "],");
				sbLog.append("gender=[" + String.valueOf(gwk.gender) + "],");
				sbLog.append("message=[" + String.valueOf(gwk.message) + "],");
				sbLog.append("address=[" + String.valueOf(gwk.address) + "],");
				sbLog.append("num1=[" + String.valueOf(gwk.num1) + "],");
				sbLog.append("num2=[" + String.valueOf(gwk.num2) + "],");
				sbLog.append("num3=[" + String.valueOf(gwk.num3) + "],");
				sbLog.append("num4=[" + String.valueOf(gwk.num4) + "],");
				sbLog.append("str1=[" + String.valueOf(gwk.str1) + "],");
				sbLog.append("str2=[" + String.valueOf(gwk.str2) + "],");
				sbLog.append("str3=[" + String.valueOf(gwk.str3) + "],");
				sbLog.append("str4=[" + String.valueOf(gwk.str4) + "].");
			}
			
			writelog(sbLog.toString());
		}
		catch(Exception ex)
		{
			writelog(ex);
		}
	}
	
	/*//日志
	protected void writelog(String loginfo)
	{
		try
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(String.valueOf(loginfo));
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
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		catch(Exception e)
		{
			ex.printStackTrace();
		}
	}*/
	protected String getClassSimpleName()
	{
		return this.getClass().getSimpleName();
	}

	
	/**
	 * 检查提货单号合法性 CHECKTHDH
	 * @param fphm
	 * @param syjh
	 * @param thdd
	 * @return boolean 检查提货单号是否合法
	 */
	public boolean checkTHDH(String fphm, String syjh, String thdd )
	{
		// TODO Auto-generated method stub
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { fphm, syjh, thdd };
		String[] args = { "fphm", "syjh", "thdd" };
		
		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_CHECKTHDH);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_CHECKTHDH), line, "检查提货单号合法性信息失败!");
			
			if (result == 0)
			{
				return true;
			}
			else
			{
				writelog("checkTHDH() 检查提货单号 响应失败result=【" + result +"】,fphm[" + fphm + "],syjh=[" + syjh + "],thdh=[" + thdd + "],line=【" + line + "】。");
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
		return false;
	} 
	
	/**
	 * 上传提货单信息 SENDTHDH 
	 * @param fphm
	 * @param syjh
	 * @param thdh
	 * @return boolean 返回提货单是否成功上传
	 */
	public boolean sendTHD( String fphm, String syjh, String thdh )
	{
		if (!GlobalInfo.isOnline) { return false; }
		PosLog.getLog(this.getClass().getSimpleName()).info("sendTHD() fphm=[" + fphm + "],syjh=[" + syjh + "],thdh=[" + thdh + "].");
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { fphm, syjh, thdh };
		String[] args = { "fphm", "syjh", "thdh" };
		
		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_SENDTHDH);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_SENDTHDH), line, "上传提货单信息失败!");
			
			if (result == 0)
			{
				return true;
			}
			else
			{
				writelog("sendTHD() 发送提货单号 响应失败result=【" + result +"】,fphm[" + fphm + "],syjh=[" + syjh + "],thdh=[" + thdh + "],line=【" + line + "】。");
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
		return false;
	}
	
	public int sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Vector retValue, Http http, int commandCode)
	{
		try
		{
			if (http==null)
			{
				//上传至MGR或001
				http = getGwkHttp(commandCode);
			}
			return super.sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust, retValue, http, commandCode);
		}
		catch(Exception ex)
		{
			writelog(ex);
			return -1;
		}
	}
	

	
	//---报表start
	
	/**
	 * 获取报表查询条件：品牌信息
	 */
	public boolean getRpt_PPInfo(Vector vecPP)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_GETRPT_PPINFO);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			//writelog("getFlights() 1");
			result = HttpCall(line, "获取品牌信息失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, RptDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecPP != null)
				{
					
					String[] row;
					RptDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new RptDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecPP.addElement(f);
						
					}
				}
				
				writelog("getRpt_PPInfo() 响应成功result=【" + result +"】，vecPP.size=【" + vecPP.size() + "】.");
				return true;
			}
			else
			{
				writelog("getRpt_PPInfo() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
	

	/**
	 * 获取报表查询条件：柜组信息
	 */
	public boolean getRpt_GZInfo(Vector vecGZ)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_GETRPT_GZINFO);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			//writelog("getFlights() 1");
			result = HttpCall(line, "获取柜组信息失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, RptDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecGZ != null)
				{
					
					String[] row;
					RptDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new RptDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecGZ.addElement(f);
						
					}
				}
				
				writelog("getRpt_GZInfo() 响应成功result=【" + result +"】，vecGZ.size=【" + vecGZ.size() + "】.");
				return true;
			}
			else
			{
				writelog("getRpt_GZInfo() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
	

	/**
	 * 获取报表查询信息：单品库存查询
	 */
	public boolean getRPT_KC(String code, Vector vecKC)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code };
		String[] args = { "code" };
		
		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_GETRPT_KC);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			//writelog("getFlights() 1");
			result = HttpCall(line, "获取单品库存查询失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, RptDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecKC != null)
				{
					
					String[] row;
					RptDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new RptDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecKC.addElement(f);
						
					}
				}
				
				writelog("getRPT_KC() 响应成功result=【" + result +"】，vecKC.size=【" + vecKC.size() + "】.");
				return true;
			}
			else
			{
				writelog("getRPT_KC() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
	

	/**
	 * 获取报表查询信息：商品库存列表
	 */
	public boolean getRPT_KCList(String gz, String pp, Vector vecKCList)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { gz , pp};
		String[] args = { "gz" , "pp" };

		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_GETRPT_KCLIST);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			//writelog("getFlights() 1");
			result = HttpCall(line, "获取商品库存列表失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, RptDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecKCList != null)
				{
					
					String[] row;
					RptDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new RptDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecKCList.addElement(f);
						
					}
				}
				
				writelog("getRPT_KCList() 响应成功result=【" + result +"】，vecKCList.size=【" + vecKCList.size() + "】.");
				return true;
			}
			else
			{
				writelog("getRPT_KCList() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
	
	
	//---报表end
	
	/**
	 * 获取购物卡独立PosServer连接
	 * @param cmdcode 命令编号
	 * @return http
	 */
	public Http getGwkHttp(int cmdcode)
	{
		try
		{
			if ((GlobalInfo.sysPara.gwkSvrUrl == null) 
					|| (GlobalInfo.sysPara.gwkSvrUrl.trim().length() <= 0) 
					|| (GlobalInfo.sysPara.gwkSvrUrl.trim().charAt(0)!='Y')
					|| (GlobalInfo.sysPara.gwkSvrCmdlist == null)
					|| (GlobalInfo.sysPara.gwkSvrCmdlist.trim().length() <= 0))
			{
				return GlobalInfo.localHttp;
			}
			else
			{
				//解析参数 是否启用|PosServer地址|命令列表
				//GlobalInfo.sysPara.gwksvrurl=Y|http://....|826,827
				String[] arr = GlobalInfo.sysPara.gwkSvrUrl.split("\\|");
				if (arr.length<2 || arr[0].trim().charAt(0)!='Y') return GlobalInfo.localHttp;
				
				//检查是否匹配命令
				String cmdlist = "," + GlobalInfo.sysPara.gwkSvrCmdlist.trim() + ",";
				if (cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) < 0) return GlobalInfo.localHttp;

				// 解析URL
				String url = arr[1].trim();			

				// 发送请求到独立卡服务器
				boolean isinit = false;
				if (GWKHttp == null || (GWKHttp != null && !GWKHttp.isSameHttp(url)))
				{
					isinit=true;
					this.writelog("GWKHttp 初始化  SYAPARA_WX=【" + GlobalInfo.sysPara.gwkSvrUrl + "】");
				}
				for (int i = 0; GlobalInfo.httpStatus != null && i < GlobalInfo.httpStatus.size(); i++)
				{
					if (url.equalsIgnoreCase((String) GlobalInfo.httpStatus.elementAt(i)))
					{
						GlobalInfo.httpStatus.remove(i);
						isinit=true;
						this.writelog("GWKHttp 超时重新初始化 SYAPARA_WX=【" + GlobalInfo.sysPara.gwkSvrUrl + "】");
						break;
					}
				}
				if (isinit)
				{
					GWKHttp = new Http(url);
					GWKHttp.init();
					GWKHttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
					GWKHttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
				}
				return GWKHttp;
			}
		}
		catch(Exception ex)
		{
			this.writelog(ex);
			this.writelog("GWKHttp解析初始化异常，所以启用本地连接， SYAPARA_WX=【" + GlobalInfo.sysPara.gwkSvrUrl + "】");
			return GlobalInfo.localHttp;
		}
		
	}
	

	// 发送返券卡
	public boolean sendFjkSale(MzkRequestDef req, MzkResultDef ret)
	{
		/*if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1) continue;

						if (line.trim().charAt(0) == ';') continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
							//Object[] obj = new Object[] { iplist[0], a };
							Object[] obj = null;
							if (iplist.length > 2) obj = new Object[] { iplist[0], a, iplist[2] };
							else obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.SENDFJK) };
							
							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendFjkSale(htp, req, ret);
					}
				}
			}
		}*/

		return sendFjkSale(getGwkHttp(CmdDef.SENDFJK), req, ret);
	}
	
	
	public boolean findGwkInfo_TH(String syjh, long fphm, GwkDef gwk)
	{
		if (!GlobalInfo.isOnline) { return false; }
		
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, String.valueOf(fphm) };
		String[] args = { "syjh", "fphm" };
		
		try
		{
			//日志开始
			StringBuffer sbLog = new StringBuffer();
			sbLog.append("findGwkInfo_TH() 查询请求：");
			sbLog.append("syjh=[" + String.valueOf(syjh) + "],");
			sbLog.append("fphm=[" + String.valueOf(fphm) + "].");
			writelog(sbLog.toString());
			
			writeLogForFindGwkInfo("findGwkInfo_TH() 请求前gwk类值：", gwk);//记录gwk请求前日志
			//日志结束
			
			cmdHead = new CmdHead(Zmsy_CmdDef.ZMSY_GETGWKINFO_TH);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));
			
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_GETGWKINFO_TH), line, "获取原小票购物卡信息失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GwkDef.ref);
				if (v != null && v.size() > 0 )
                {
                    String[] row = (String[]) v.elementAt(0);
                    //获取对应的购物卡信息
                    if (Transition.ConvertToObject(gwk, row))
                    {
                    	String[] arr = gwk.ljrq.split(" ");
                    	gwk.ljrq = arr[0];
                    	if(arr.length>=2)
                    	{
                    		gwk.ljsj=arr[1];
                    	}
                    	writeLogForFindGwkInfo("findGwkInfo_TH() 响应后gwk类值：", gwk);//记录gwk响应后日志
                        return true;
                    }
                }
				else
				{
					writelog("findGwkInfo_TH() 响应成功result=【" + result +"]，但无返回值line=【" + line.toString() + "】。");
				}
			}
			else
			{
				writelog("findGwkInfo_TH() 响应失败result=【" + result +"].");
			}
		}
		catch (Exception er)
		{
			writelog(er);
		}
		return false;
	}
	

	/**
	 * 获取打印序列号
	 * @param syjh 收银机号
	 * @param fphm 发票号码
	 * @param sbSJSeq 税金序号,即税单号
	 * @param sbHBSeq 航班序号,即分货号
	 * @return boolean 过程返回是否成功 
	 */
	public boolean findPrintSeq(String syjh, long fphm, StringBuffer sbSJSeq, StringBuffer sbHBSeq)
	{
		
		if (!GlobalInfo.isOnline) { 
			writelog("findPrintSeq() 获取打印序列号：网络不通");
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, String.valueOf(fphm) };
		String[] args = { "syjh", "fphm" };

		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_FIND_PRINTSEQ);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_FIND_PRINTSEQ), line, "获取打印序列号失败!");
			if (result == 0)
			{
				String[]  ref = {"sbSJSeq", "sbHBSeq"};
				Vector v = new XmlParse(line.toString()).parseMeth(0, ref);
				if (v != null && v.size() > 0 )
				{
					String[] row;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						sbSJSeq.append(row[0]); //税金号
						sbHBSeq.append(row[1]); //航班号
					}
				}
				else
				{
					writelog("findPrintSeq() 响应成功，但内容为空 result=【" + result +"]，line=【" + line.toString() + "】.");
				}
				
				return true;
			}
			else
			{
				writelog("findPrintSeq() 响应失败result=【" + result +"]，line=【" + line.toString() + "】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
		
	}
	
	//找小票退货信息
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
			result = HttpCall(getGwkHttp(CmdDef.GETBACKSALEHEAD), line, "");

			if (result != 0)
			{
				new MessageBox("退货小票头查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票头,退货小票不存在或已确认!");
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox("退货小票头转换失败!");
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
			result = HttpCall(getGwkHttp(CmdDef.GETBACKSALEDETAIL), line, "");

			if (result != 0)
			{
				new MessageBox("退货小票明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					setBackGoods_ZJSJ(sgd);
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
			result = HttpCall(getGwkHttp(CmdDef.GETBACKPAYSALEDETAIL), line, "");

			if (result != 0)
			{
				new MessageBox("付款明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到付款小票明细,退货小票不存在或已确认!");
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
			this.writelog(ex);
			new MessageBox("获取退货小票信息时失败：" + ex.getMessage());
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}


	// 获得交易小票信息
	public boolean getInvoiceInfo(InvoiceInfoDef inv)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ConfigClass.CashRegisterCode, GlobalInfo.balanceDate };
		String[] args = { "syjh", "jzrq" };

		try
		{
			aa = new CmdHead(CmdDef.GETINVOICE);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getGwkHttp(CmdDef.GETINVOICE), line, "获取交易小票信息失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, InvoiceInfoDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(inv, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			this.writelog(ex);
		}

		return false;
	}
	
	public boolean getBackGoodsDetail(Vector backgoods, String oldsyj, String oldfphm, String code, String gz, String uid)
	{
		
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { oldsyj, oldfphm, code, gz, uid };
		String[] args = { "oldsyj", "oldfphm", "code", "gz", "uid" };

		try
		{
			head = new CmdHead(CmdDef.GETBACKGOODSINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getGwkHttp(CmdDef.GETBACKGOODSINFO), line, "原退货小票上未找到此商品!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

				for (int j = 0; j < v.size(); j++)
				{
					String[] row = (String[]) v.elementAt(j);
					SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
					if (Transition.ConvertToObject(saleGoodsDef, row))
					{
						setBackGoods_ZJSJ(saleGoodsDef);
						backgoods.add(saleGoodsDef);
					}
				}

				if (backgoods.size() <= 0)
				{
					new MessageBox("原退货小票上未找到此商品!");
				}
				else return true;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}
	
	//处理退货时的暂缴税金
	private void setBackGoods_ZJSJ(SaleGoodsDef saleGoodsDef)
	{
		try
		{
			if (saleGoodsDef != null)
			{
				if (saleGoodsDef.str6!=null)//即购即提字段值(暂缴税金|暂缴税率|完税（价）金额|使用免税额度)
				{
					String[] strArr = saleGoodsDef.str6.split("\\|");
					saleGoodsDef.num12 = Convert.toDouble(strArr[0]);
				}
			}
		}
		catch(Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}
		
	}
	
	public boolean getRPT_YYY(String syjh, String yyyh, String rq, String str1, String str2, String str3, Vector vecYYY)
	{
		if (!GlobalInfo.isOnline) { 
			
			return false; 
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh,yyyh,rq,str1,str2,str3 };
		String[] args = { "syjh","yyyh","rq","str1","str2","str3" };
		
		try
		{
			aa = new CmdHead(Zmsy_CmdDef.ZMSY_GETRPT_YYY);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));
			//writelog("getFlights() 1");
			result = HttpCall(getGwkHttp(Zmsy_CmdDef.ZMSY_GETRPT_YYY), line, "获取营业员统计失败!");
			//writelog("getFlights() 2");
			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, RptYYYDef.ref);
				//writelog("getFlights() 3");
				if (v != null && v.size()>0 && vecYYY != null)
				{
					
					String[] row;
					RptYYYDef f ;
					for (int i = 0; i < v.size(); i++)
					{
						row = (String[]) v.elementAt(i);
						f = new RptYYYDef();
						
						if (!Transition.ConvertToObject(f, row)) { continue; }
						vecYYY.addElement(f);
						
					}
				}
				
				writelog("getRPT_YYY() 响应成功result=【" + result +"】，vecYYY.size=【" + vecYYY.size() + "】.");
				return true;
			}
			else
			{
				writelog("getRPT_YYY() 响应失败result=【" + result +"】.");
				return false;
			}
		}
		catch (Exception er)
		{
			writelog(er);
			return false;
		}
//		return true;
	}
}
