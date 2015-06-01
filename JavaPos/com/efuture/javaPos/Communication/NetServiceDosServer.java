package com.efuture.javaPos.Communication;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.DosServer.DosCancelTHHReq;
import com.efuture.javaPos.Struct.DosServer.DosCancelTHHRes;
import com.efuture.javaPos.Struct.DosServer.DosCheckSaleBackReq;
import com.efuture.javaPos.Struct.DosServer.DosCheckSaleBackRes;
import com.efuture.javaPos.Struct.DosServer.DosFindCustCouponsReq;
import com.efuture.javaPos.Struct.DosServer.DosFindCustCouponsRes;
import com.efuture.javaPos.Struct.DosServer.DosFindCustParaRes;
import com.efuture.javaPos.Struct.DosServer.DosFindCustPopReq;
import com.efuture.javaPos.Struct.DosServer.DosFindCustPopRes;
import com.efuture.javaPos.Struct.DosServer.DosFindCustReq;
import com.efuture.javaPos.Struct.DosServer.DosFindCustRes;
import com.efuture.javaPos.Struct.DosServer.DosGetCustReCouponsReq;
import com.efuture.javaPos.Struct.DosServer.DosGetCustReCouponsRes;
import com.efuture.javaPos.Struct.DosServer.DosSaleRefoundReq;
import com.efuture.javaPos.Struct.DosServer.DosSaleRefoundRes;
import com.efuture.javaPos.Struct.DosServer.DosSendCouponPayReq;
import com.efuture.javaPos.Struct.DosServer.DosSendCouponPayRes;
import com.efuture.javaPos.Struct.DosServer.DosSendSaleComReq;
import com.efuture.javaPos.Struct.DosServer.DosSendSaleHeadReq;
import com.efuture.javaPos.Struct.DosServer.DosSendSaleOKRes;
import com.efuture.javaPos.Struct.DosServer.DosSendSalePayReq;

public class NetServiceDosServer
{

	protected SocketDosPosServer socketDos = null;
	protected String errorMessage = "";

	/**
	 * 是否启用dosposServer
	 * @param cmdcode -1表示初始化连接，其它表示javapos的通讯命令
	 * @return
	 */
	public boolean isUseDosPosServer(int cmdcode)
	{
		try
		{
			
			if(GlobalInfo.sysPara==null)
			{
				PosLog.getLog(this.getClass()).info("GlobalInfo.sysPara==null");
				return false;
			}
			if (cmdcode == -1)
			{
				if(GlobalInfo.sysPara.dosPosSvrAddress == null) {
					PosLog.getLog(this.getClass()).info("SK 参数为空");
					return false;
				}
				//表示初始化连接,若配置了参数，则表示启用
				if (GlobalInfo.sysPara.dosPosSvrAddress != null && GlobalInfo.sysPara.dosPosSvrAddress.trim().length() > 0) { return true; }
			}
			else
			{
				if(GlobalInfo.sysPara.dosPosSvrCmdList == null) {
					PosLog.getLog(this.getClass()).info("SL 参数为空");
					return false;
				}
				//检查命令列表是否调用DosPosServer
				if ((GlobalInfo.sysPara.dosPosSvrCmdList != null) && (GlobalInfo.sysPara.dosPosSvrCmdList.trim().length() > 0))
				{
					String cmdlist = "," + GlobalInfo.sysPara.dosPosSvrCmdList.trim() + ",";
					if (cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) >= 0)
					{
						if (socketDos == null) createSocketDosConnection();
						return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
		}
		return false;
	}
	
	/**
	 * 创建dosposServer连接
	 * @return
	 */
	public boolean createSocketDosConnection()
	{
		try
		{
			if (isUseDosPosServer(-1))
			{
				if (GlobalInfo.sysPara.dosPosSvrAddress != null && GlobalInfo.sysPara.dosPosSvrAddress.trim().length() > 0)
				{
					String[] arr = GlobalInfo.sysPara.dosPosSvrAddress.split("\\|");
					if (arr.length>=3)
					{
						socketDos = new SocketDosPosServer(arr[0].trim(), Convert.toInt(arr[1].trim()), Convert.toInt(arr[2].trim()));
						
						PosLog.getLog(this.getClass()).info("dosPosSvrCmdList_SL=[" + String.valueOf(GlobalInfo.sysPara.dosPosSvrCmdList).trim() + "]");
						return true;
					}					
				}
				else
				{
					PosLog.getLog(this.getClass()).info("createSocketDosConnection() 初始化失败：SK系统参数配置不正确.");
				}				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
		}
		return false;
	}
	
	protected void showMsg(String msg)
	{
		errorMessage = msg;
		PosLog.getLog(this.getClass()).info(msg);
		new MessageBox(msg, null, false);
	}
	
	public boolean checkNet()
	{
		if(this.socketDos==null) this.createSocketDosConnection();
		return getServerTime_Dos();
	}

	/**
	 * 握手通讯 1/1
	 * 命令通讯前先进行握手
	 * @return
	 */
	public boolean getServerTime_Dos()
	{
		try
		{
			Vector vecByteArr = new Vector();
			vecByteArr.add(socketDos.getRequestHead(CmdDosDef.GETSERVERTIME, 0));//head,只发包体，没有包体
			Vector vecResponse = socketDos.socketSend(vecByteArr);
			if (vecResponse!=null && vecResponse.size()==0)
			{
				//接收，只有包头，没有包体
				return true;
			}	
			else
			{
				showMsg("socket握手通讯失败，" + socketDos.getError());
			}
			
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}
	
	/**
	 * 查找会员信息 10/10
	 * @param cust
	 * @param track
	 * @return
	 */
	public boolean getCustomer_Dos(CustomerDef cust, String track)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosFindCustReq req = new DosFindCustReq();
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{
				String a = GlobalInfo.sysPara.mktcode.substring(0,1);
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					 a = ManipulateStr.numToLetter(GlobalInfo.sysPara.mktcode.substring(0,1)).toUpperCase();
				}
				
				req.mkt = a + GlobalInfo.sysPara.mktcode.substring(1,GlobalInfo.sysPara.mktcode.length());
			}
			else
			{
				req.mkt = GlobalInfo.sysPara.mktcode;
			}
			req.jygs = GlobalInfo.sysPara.jygs;
			req.track = track;
			/*
			byte[] body = socketDos.getClassByte(req);
			
			Vector vecByteArr = new Vector();
			vecByteArr.add(socketDos.getRequestHead(CmdDosDef.FINDCUSTOMER, 1));//head
			vecByteArr.add(socketDos.getBodyLen(body.length));//body_len
			vecByteArr.add(body);//body
			 */			
			Vector vecResponse = socketDos.socketSend(socketDos.getRequestOne(CmdDosDef.FINDCUSTOMER, req));//vecByteArr
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosFindCustRes res = (DosFindCustRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosFindCustRes.class.newInstance());
				if(res.msg==null)
				{
					showMsg("查找该顾客卡信息失败，" + socketDos.getError());					
					return false;
				}
				if (res.msg.charAt(0)!='1')
				{
					showMsg("失败，" + res.msg.trim());
					return false;
				}
				if (res!=null && cust!=null)
				{
					cust.code = res.cardno.trim();
					cust.type = res.type.trim();
					cust.name = res.name.trim();
					cust.status = String.valueOf(res.status);
					cust.maxdate = res.expdate.trim();				
					cust.track = res.track.trim();
					cust.memo = res.memo.trim();
					cust.valuememo = res.valuememo;
					cust.ishy = res.ishy;
					cust.iszk = res.iszk;
					cust.isjf = res.isjf;
					cust.func = res.func;//.trim()				
					cust.zkl = res.zkl;
					cust.value1 = res.value1;
					cust.value2 = res.value2;
					cust.value3 = res.value3;
					cust.value4 = res.value4;
					cust.value5 = res.value5;
				}				
								
				return true;
				
			}	
			else
			{
				showMsg("查找该顾客卡信息失败，" + socketDos.getError());
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}

		return false;
	}
	
	/**
	 * 查找促销平台促销信息 75/11
	 * @param popDef
	 * @param code
	 * @param gz
	 * @param uid
	 * @param rulecode
	 * @param catid
	 * @param ppcode
	 * @param time
	 * @param cardno
	 * @param cardtype
	 * @return
	 */
	public boolean findPopRuleCRM_Dos(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosFindCustPopReq req = new DosFindCustPopReq();
			req.mkt = GlobalInfo.sysPara.jygs + "," + GlobalInfo.sysPara.mktcode;//"801,0501";
			String[] codeArr = code.split("\\|");
			req.barcode = codeArr[0];
			if(codeArr.length>1)
				req.code = codeArr[1];
			else				
				req.code = req.barcode;
			
			req.gz = gz;
			req.spec = uid;
			req.spsx = rulecode;
			req.dzxl = catid;
			req.pp = ppcode;
			req.yhsj = time;
			req.cardno = cardno;
			req.custtype = cardtype;

			Vector vecResponse = socketDos.socketSend(CmdDosDef.FINDCRMPOP, req);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosFindCustPopRes res = (DosFindCustPopRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosFindCustPopRes.class.newInstance());
				if(res!=null && popDef!=null)
				{
					popDef.seqno = res.seqno;
					popDef.djbh = res.djbh.trim();					
					popDef.type = res.type;
					popDef.code = res.code.trim();
					popDef.gz = res.gz.trim();
					popDef.catid = res.dzxl.trim();
					popDef.ppcode = res.pp.trim();
					popDef.uid = res.spec.trim();
					popDef.ksrq = res.ksrq.trim();
					popDef.jsrq = res.jsrq.trim();
					popDef.kssj = res.kssj.trim();
					popDef.jssj = res.jssj.trim();
					popDef.sl = (int)res.yhsl;
					popDef.yhspace = (int)res.yhspace;
					popDef.poplsj = res.yhlsj;
					popDef.pophyj = res.yhhyj;
					popDef.poppfj = res.yhpfj;
					popDef.poplsjzkl = res.yhzkl;
					popDef.pophyjzkl = res.yhhyzkl;
					popDef.poppfjzkl = res.yhpfzkl;
					popDef.poplsjzkfd = res.zkfd;
					popDef.pophyjzkfd = res.hyzkfd;
					popDef.poppfjzkfd = res.pfzkfd;
					popDef.rule = res.rule.trim();
					popDef.mode = res.rulemode.trim();//
					//popDef.seqno = res.popbillno;//重百暂未处理此字段
					popDef.memo = res.memo.trim();
					popDef.str1 = res.str1.trim();
					popDef.str2 = res.str2.trim();
					popDef.str3 = res.str3.trim();
					popDef.str4 = res.str4.trim();
					popDef.num7 = 1;
					if(res.str5!=null)
					{
						String[] arr = res.str5.trim().split(",");
						if(arr.length>=1)
						{
							//arr[0] 会员折扣单据编号
							popDef.num7 = Convert.toDouble(arr[1]);//会员折扣率
						}
					}				
					popDef.str5="";//一定要赋值，否则计算docrm时会报错
					popDef.num1 = res.num1;
					popDef.num2 = res.num2;
				}				
				
				return true;
				
			}	
			else
			{
				//showMsg("查找该顾客卡信息失败，" + socketDos.getError());
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;
	}

	/**
	 * 计算小票返券信息 76/58
	 * @param row
	 * @param mktcode
	 * @param syjh
	 * @param fphm
	 * @return
	 */
	public boolean getSellRealFQ_Dos(String[] row, String mktcode, String syjh, String fphm)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosGetCustReCouponsReq req = new DosGetCustReCouponsReq();
			req.jygs =  GlobalInfo.sysPara.jygs;
			req.mkt = GlobalInfo.sysPara.mktcode;
			req.ysyjh = syjh;
			req.yfphm = fphm;
			req.vcdbz = "";
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.GETSELLREALFQ, req);//socket
			if (vecResponse!=null)// && vecResponse.size()>0
			{
				DosGetCustReCouponsRes res;
				String memo="";
				for(int i=0; i<vecResponse.size(); i++)
				{
					res = (DosGetCustReCouponsRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(i), DosGetCustReCouponsRes.class.newInstance());
					if(res != null)
					{
						//待处理
						//只打type=98时打印memo
						//         99时，当JE>0时打info
						//			4时打印 本次返券:info \n券有效期：memo
						//char type[20+1];        //券类型
						//char code[20+1];        //券号
						//char info[250+1];       //券描述
						//double sl;              //数量
						//double je;              //金额
						if(res.type==null) continue;
						if(res.type.trim().equalsIgnoreCase("98"))
						{
							memo = memo + res.memo.trim() + "\n";						
						}
						else if(res.type.trim().equalsIgnoreCase("99"))
						{
							if(res.je>0) memo = memo + res.info.trim() + "\n";
						}
						else if(res.type.trim().equalsIgnoreCase("4"))
						{
							memo = memo + "本次返券:" + res.info.trim() + "\n" 
										+ "券有效期:" + res.memo.trim() + "\n";
						}
					}	
				}
				if(row!=null) row[0]=memo;				
				return true;
				
			}	
			else
			{
				//showMsg("查找该顾客卡信息失败，" + socketDos.getError());
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;
	}
	
	
	/**
	 * 下载促销参数定义 77/13
	 * @param done 是否删除本地参数
	 * @param ID 命令ID
	 * @return
	 */
	public boolean getSysPara_Dos(boolean done, int ID)
	{
		if (!checkNet()) { return false; }

		try
		{
			String req = Convert.increaseChar(GlobalInfo.sysPara.mktcode, '\0', 10 + 1);
			Vector vecResponse = socketDos.socketSend(CmdDosDef.GETCRMPARA, req);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosFindCustParaRes res;
				String[] arr;
				Vector v = new Vector();
				for(int i=0; i<vecResponse.size(); i++)
				{
					res = (DosFindCustParaRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(i), DosFindCustParaRes.class.newInstance());
					if(res != null)
					{
						arr = new String[3];
						arr[0] = res.code.trim();
						arr[1] = res.name.trim();
						arr[2] = res.value.trim();
						v.add(arr);
					}	
				}

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeSysPara(v, done))
				{
					new MessageBox("保存系统参数失败!");
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
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;		
	}
	
	/**
	 * 上传返券卡信息（查询+消费） 80/49
	 * @param req 请求
	 * @param ret 返回
	 * @return
	 */
	public boolean sendFjkSale_Dos(MzkRequestDef req, MzkResultDef ret)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosSendCouponPayReq reqPara = new DosSendCouponPayReq();
			reqPara.type = req.type;
			reqPara.seqno = req.seqno;
			reqPara.termno = req.termno;
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{

				String a = req.mktcode.substring(0,1);
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				reqPara.mktcode = a + req.mktcode.substring(1,req.mktcode.length());
			}
			else
			{
				reqPara.mktcode = req.mktcode;
			}
			reqPara.jygs = GlobalInfo.sysPara.jygs;
			reqPara.syjh = req.syjh;
			reqPara.fphm = req.fphm;
			reqPara.syyh = req.syyh;
			reqPara.djlb = req.invdjlb.charAt(0);
			reqPara.paycode = req.paycode;
			reqPara.payje = req.je;
			reqPara.track1 = req.track1;
			reqPara.track2 = req.track2;
			reqPara.track3 = req.track3;
			reqPara.passwd = req.passwd;
			if(req.memo==null || req.memo.length()<=0)
			{
				reqPara.memo = req.memo;
			}
			else
			{
				reqPara.memo = req.memo.split(",")[0].trim();//重百CRM只传券种（比如a），不传其它 by yuanjun
			}
						
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.SENDFJK, reqPara);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosSendCouponPayRes res = (DosSendCouponPayRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosSendCouponPayRes.class.newInstance());
				if(res!=null && ret!=null)
				{
					if(res.msg==null)
					{
						showMsg("返券卡交易失败，" + socketDos.getError());					
						return false;
					}
					if (res.msg.charAt(0)!='Y')
					{
						showMsg("返券卡交易失败，" + res.msg.trim());
						return false;
					}
					ret.cardno = res.cardno.trim();
					ret.cardname = res.name.trim();
					ret.cardpwd = res.pwd.trim();
					ret.ispw = res.ispwd;
					ret.func = res.func;//.trim()
					ret.ye = res.ye;
					ret.money = res.money;
					ret.value1 = res.value1;
					ret.value2 = res.value2;
					ret.value3 = res.value3;
					ret.memo = res.memo.trim();	
					return true;				
				}				
				
				
			}	
			else
			{				
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;		
	}
	
	public boolean findFjkSale_Dos(MzkRequestDef req, MzkResultDef ret)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosFindCustCouponsReq reqPara = new DosFindCustCouponsReq();
			reqPara.termno = req.termno;
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{
				String a = GlobalInfo.sysPara.mktcode.substring(0,1);
				
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					 a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				
				reqPara.mkt = a + GlobalInfo.sysPara.mktcode.substring(1,GlobalInfo.sysPara.mktcode.length());
			}
			else
			{
				reqPara.mkt = req.mktcode;
			}
			reqPara.jygs = GlobalInfo.sysPara.jygs;
			reqPara.syjh = req.syjh;
			reqPara.syyh = req.syyh;
			reqPara.djlb = req.invdjlb.charAt(0);
			reqPara.paycode = req.paycode;
			reqPara.track1 = req.track1;
			reqPara.track2 = req.track2;
			reqPara.track3 = req.track3;
			reqPara.passwd = req.passwd;
			reqPara.memo = req.memo;
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.FINDFJK, reqPara);//socket
			String memo="";
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosFindCustCouponsRes res;
				for(int i=0; i<vecResponse.size(); i++)
				{
					res = (DosFindCustCouponsRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(i), DosFindCustCouponsRes.class.newInstance());
					if(res != null)
					{
						if(i>0) memo = memo + "|";
						memo = memo 
							 + res.type + ","//券类型
							 + res.name.trim() + ","//券说明
							 + res.ye + ","//券余额
							 + res.hl + ","//券汇率
							 + res.flag;//电子券1/手工券2
					}	
				}
				
				ret.cardno = req.track2.trim();
				ret.cardname = "";
				ret.cardpwd = req.passwd.trim();
				ret.ispw = ' ';
				ret.func = "                     ";//.trim()
				ret.ye = 0;
				ret.money = 0;
				ret.value1 = 0;
				ret.value2 = 0;
				ret.value3 = 0;
				ret.memo = memo.trim();	
				PosLog.getLog(this.getClass()).info("findFjkSale_Dos track=[" + req.track2.trim()+ "],memo=[" + memo + "].");
				return true;
			}
			else
			{				
				PosLog.getLog(this.getClass()).info("findFjkSale_Dos 未找到券信息track2=[" + req.track2.trim() + "].");
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;		
	}
	
	/**
	 * 获取小票扣回信息 83/24
	 * 调用顺序：86--》52--》83
	 * @param mkt
	 * @param syjh
	 * @param fphm
	 * @param rmd
	 * @param cmdcode
	 * @return
	 */
	public boolean getRefundMoney_Dos(String mkt, String syjh, long fphm, String ishh, RefundMoneyDef rmd, int cmdcode)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosSaleRefoundReq reqPara = new DosSaleRefoundReq();
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{
				String a = mkt.substring(0,1);
				
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					 a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				
				reqPara.mkt = a + mkt.substring(1,mkt.length());
			}
			else
			{
				reqPara.mkt = mkt;
			}
			reqPara.jygs = GlobalInfo.sysPara.jygs;
			reqPara.syjh = syjh;
			reqPara.fphm = fphm;		
			reqPara.ishh = String.valueOf(ishh);
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.GETREFUNDMONEY_EX, reqPara);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosSaleRefoundRes res = (DosSaleRefoundRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosSaleRefoundRes.class.newInstance());
				if(res!=null && rmd!=null)
				{
					if(res.msg==null)
					{
						showMsg("联网检查能否退货时失败：" + socketDos.getError());					
						return false;
					}
					if (res.msg.charAt(0)!='Y')
					{
						showMsg("联网检查能否退货时失败：" + res.msg.substring(1,res.msg.length()).trim());
						return false;
					}
					if(res.msg.charAt(0) == 'Y' && res.msg.charAt(1) != ' ')
					{
						showMsg(res.msg.substring(1,res.msg.length()).trim());
					}
					rmd.jfkhje = res.jfkhje;
					rmd.jfdesc = res.jfkhdesc.trim();
					rmd.fqkhje = res.fqkhje;
					rmd.fqdesc = res.fqkhdesc.trim();
					rmd.qtkhje = res.qtkhje;
					rmd.qtdesc = res.qtkhdesc.trim();

					rmd.refunddesc1 = res.goodpays.trim();
					rmd.refunddesc2 = res.sellpays.trim();
					return true;
				}						
			}	
			else
			{				
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;	
				
	}
	
	
	/**
	 * 检查小票是否能退货 86
	 * @param mkt
	 * @param syjh
	 * @param fphm
	 * @return true能退货，false不能退货
	 */
	public boolean checkSaleTH_Dos(String mkt, String syjh, long fphm)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosCheckSaleBackReq reqPara = new DosCheckSaleBackReq();
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{
				String a = mkt.substring(0,1);
				
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					 a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				
				reqPara.mkt = a + mkt.substring(1,mkt.length());
			}
			else
			{
				reqPara.mkt = mkt;
			}
			reqPara.jygs = GlobalInfo.sysPara.jygs;
			reqPara.syjh = syjh;
			reqPara.fphm = fphm;						
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.CHECKSALETH, reqPara);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosCheckSaleBackRes res = (DosCheckSaleBackRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosCheckSaleBackRes.class.newInstance());
				if(res!=null)
				{
					if(res.status!='Y')
					{
						showMsg("验证原小票是否能退货，" + String.valueOf(res.msg).trim());					
						return false;
					}
					
					return true;
				}						
			}	
			else
			{				
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;	
				
	}
	
	/**
	 * 取消退换货
	 * @param mkt
	 * @param syjh
	 * @param fphm
	 * @param ishh
	 * @param transid
	 * @return
	 */
	public boolean cancelSaleTH_Dos(String mkt, String syjh, long fphm, String ishh, long transid)
	{
		if (!checkNet()) { return false; }

		try
		{
			DosCancelTHHReq reqPara = new DosCancelTHHReq();
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{
				String a = mkt.substring(0,1);
				
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					 a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				
				reqPara.mkt = a + mkt.substring(1,mkt.length());
			}
			else
			{
				reqPara.mkt = mkt;
			}
			reqPara.jygs = GlobalInfo.sysPara.jygs;
			reqPara.syjh = syjh;
			reqPara.fphm = fphm;
			reqPara.ishh = ishh;
			reqPara.transid = transid;
			
			Vector vecResponse = socketDos.socketSend(CmdDosDef.CANCEL_TRANSID, reqPara);
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosCancelTHHRes res = (DosCancelTHHRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosCancelTHHRes.class.newInstance());
				if(res!=null)
				{
					if(res.msg.charAt(0)!='Y')
					{
						showMsg("取消退换货失败，" + res.msg.substring(1,res.msg.length()).trim());					
						return false;
					}
					
					return true;
				}						
			}	
			else
			{				
				return false;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return false;	
				
	}
	
	/**
	 * 上传正式小票到CRM
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param retValue
	 * @return
	 */
	public int sendSaleData_Dos(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return sendSaleData_Dos(saleHead, saleGoods, salePayment, retValue, '1');
	}
	/**
	 * 上传小票到CRM
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param retValue
	 * @param flag 1正式上传，2预上传
	 * @return
	 */
	public int sendSaleData_Dos(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, char flag)
	{
		if (!checkNet()) { return -1; }

		try
		{
			if(flag!='1')
			{
				//预上传
				saleHead.bc='#';
			}
			
			//小票头byteVec,转换小票头类
			DosSendSaleHeadReq saleHeadReq = getSocketSaleHead(saleHead, saleGoods, salePayment);	
			if(saleHeadReq==null) return -2;
			Vector saleHeadByteVec = socketDos.getClassByteAndLen(saleHeadReq);
			if(saleHeadByteVec==null || saleHeadByteVec.size()<=0)
			{
				showMsg("小票上传失败,获取小票头数据流时失败：" + socketDos.getError());
				return -2;
			}
			
			//小票商品明细combody_len+combody，转换小票商品明细类
			Vector saleGoodsReq = getSocketSaleCom(saleHead, saleGoods);
			if(saleGoodsReq==null || saleGoodsReq.size()<=0) return -2;
			Vector saleComByteVec = socketDos.getClassByte(saleGoodsReq);
			if(saleComByteVec==null || saleComByteVec.size()<=0)
			{
				showMsg("小票上传失败,获取小票商品明细数据流时失败：" + socketDos.getError());
				return -2;
			}
			
			//小票付款明细paybody_len+paybody，转换小票付款明细类
			Vector salePayReq = getSocketSalePay(salePayment);
			if(salePayReq==null || salePayReq.size()<=0) return -2;
			Vector salePayByteVec = socketDos.getClassByte(salePayReq);
			if(salePayByteVec==null)
			{
				showMsg("小票上传失败,获取小票付款明细数据流时失败：" + socketDos.getError());
				return -2;
			}
			
			//获取包头
			Vector bodyHeadVec = new Vector();
			byte[] bodyHead = socketDos.getRequestHead(CmdDosDef.SENDCRMSELL, 1+saleGoods.size()+salePayment.size());
			bodyHeadVec.add(bodyHead);
			//组装包头+包体（小票头+商品+付款）
			Vector socketBody = socketDos.copyByteVector(bodyHeadVec, saleHeadByteVec);//包头+小票头包体
			socketBody = socketDos.copyByteVector(socketBody, saleComByteVec);//包体+小票商品明细包体
			socketBody = socketDos.copyByteVector(socketBody, salePayByteVec);//包体+小票付款明细包体
			
			Vector vecResponse = socketDos.socketSend(socketBody);//socket
			if (vecResponse!=null && vecResponse.size()>0)
			{
				DosSendSaleOKRes res = (DosSendSaleOKRes)socketDos.getClassObjValue((byte[])vecResponse.elementAt(0), DosSendSaleOKRes.class.newInstance());
				if(res!=null)
				{
					//有返回则表示成功
					/*if(res.msg==null)
					{
						showMsg("小票上传失败，" + socketDos.getError());					
						return -2;
					}
					if (res.msg.charAt(0)!='Y')
					{
						showMsg("小票上传失败：" + res.msg.trim());
						return -2;
					}*/
					if(retValue!=null)
					{
						//retValue.add(res.msg.trim());//返回提示信息
						retValue.add(res.bcjf);//本次积分
						retValue.add(res.ljjf);//积分余额
					}
					
					return 0;//成功
				}						
			}	
			else
			{				
				return -1;
			}			
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(this.getClass()).error(er);
		}		

		return -3;
		
	}
	
	protected DosSendSaleHeadReq getSocketSaleHead(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		DosSendSaleHeadReq req = new DosSendSaleHeadReq();
		req.comnum = (short)saleGoods.size();
		req.paynum = (short)salePayment.size();
		req.bc = saleHead.bc;
		req.syjh = saleHead.syjh;		
		req.fphm = saleHead.fphm;
		req.djlb = saleHead.djlb;
		req.rqsj = saleHead.rqsj;
		req.syyh = saleHead.syyh;
		req.hykh = saleHead.hykh;
		if(saleHead.sqkh==null || saleHead.sqkh.trim().length()<=0)
		{
			//默认填会员卡号
			req.sqkh = saleHead.hykh;
		}
		else
		{
			req.sqkh = saleHead.sqkh;
		}
		
		req.sqktype = saleHead.sqktype;
		req.ysje = saleHead.ysje;
		req.sjfk = saleHead.sjfk;
		req.zl = saleHead.zl;
		req.sysy = saleHead.sswr_sysy+saleHead.fk_sysy;//
		req.hyzke = saleHead.hyzke;
		req.yhzke = saleHead.yhzke;
		
		req.lszke = 0;
		req.lszre = 0;
		SaleGoodsDef goods=null;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			goods = (SaleGoodsDef) saleGoods.elementAt(i);
			req.lszke += (goods.lszke + goods.lszzk);
			req.lszre += (goods.lszre + goods.lszzr);
		}
		
		if(saleHead.buyerinfo==null || saleHead.buyerinfo.trim().length()<=0)
		{
			req.custinfo="000000";//若无值，则按此格式传 wangyong by yuanj for 2014.3.10
		}
		else
		{
			req.custinfo = saleHead.buyerinfo;
		}		
		req.hhflag = ' ';//saleHead.hhflag;
		if (SellType.ISHH(saleHead.djlb)) req.hhflag='Y';
		req.custtype = saleHead.hytype;
		
		if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
		{
			String a =GlobalInfo.sysPara.mktcode.substring(0,1);
			if(a.equals("6"))
			{
				a = "D";
			}
			else
			{
				a =  ManipulateStr.numToLetter(a).toUpperCase();
			}
			req.mkt = a + GlobalInfo.sysPara.mktcode.substring(1,GlobalInfo.sysPara.mktcode.length());
			
			req.mkt = GlobalInfo.sysPara.jygs + "," + req.mkt;
		}
		else
		{
			req.mkt = GlobalInfo.sysPara.jygs + "," + saleHead.mkt;
		}
		
		req.str1 = String.valueOf((long)saleHead.num10);// 退换货事务号 //saleHead.str1;		
		return req;
	}
	
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
				req.batch = goods.batch;
				req.yhdjbh = goods.yhdjbh;
				req.name = goods.name;
				req.unit = goods.unit;
				req.bzhl = goods.bzhl;
				req.sl = goods.sl;
				req.lsj = goods.lsj;
				req.jg = goods.jg;
				req.zje = goods.hjje;
				req.hyzke = goods.hyzke;
				req.yhzke = goods.yhzke;
				req.yhzkfd = goods.yhzkfd;
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
	
	protected Vector getSocketSalePay(Vector salePayment)
	{
		Vector vecPay = new Vector();
		SalePayDef pay;
		DosSendSalePayReq req;
		try
		{
			for (int i = 0; i < salePayment.size(); i++)
			{
				pay = (SalePayDef) salePayment.elementAt(i);
				req = new DosSendSalePayReq();
				req.paycode = pay.paycode;
				req.ybje = pay.ybje;
				req.hl = pay.hl;
				req.payno = pay.payno;
				req.idno = pay.idno;
				req.kye = pay.num1;
				req.flag = pay.flag;
				
				vecPay.add(req);
			}
			return vecPay;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this.showMsg("转换小票付款明细时异常：" + ex.getMessage());
			return null;
		}
		
	}
}
