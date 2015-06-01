package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDosDef;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.SocketDosPosServer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.DosServer.CbbhMzkReq;
import com.efuture.javaPos.Struct.DosServer.CbbhMzkRes;
import com.efuture.javaPos.Struct.DosServer.CbbhXsjMzkReq;
import com.efuture.javaPos.Struct.DosServer.CbbhXsjMzkRes;

import custom.localize.Bcrm.Bcrm_NetService;

public class Cbbh_Mzk_NetService extends Bcrm_NetService
{
	private SocketDosPosServer socketMzkCb=null;//重百储值卡
	private SocketDosPosServer socketMzkXsj=null;//重百新世纪储值卡
	

	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSale(null,req,ret,CmdDosDef.SENDMZK);
	}
	
	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{

		if(req==null || req.track2==null) return false;
		if(GlobalInfo.sysPara.isUnityMzkSrv=='Y')
		{//统一储值卡卡中心
			return this.sendMzkSale_Dos_CB(req, ret, cmdCode);
		}

		//根据轨道来判断是重百储值卡，还是新世纪储值卡:二磁轨信息为16位的则为新世纪提货卡
		if(req.track2.trim().getBytes().length==16)
		{
			return this.sendMzkSale_Dos_XSJ(req, ret, cmdCode);
		}		
		return this.sendMzkSale_Dos_CB(req, ret, cmdCode);
		
		/*if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("储值卡交易失败!"));

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

	}
		
	//重百储值卡
	public boolean sendMzkSale_Dos_CB(MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		try
		{
			socketMzkCb = createSocket(socketMzkCb, GlobalInfo.sysPara.cbMzkSvrAddress);
			if(socketMzkCb==null) return false;
			CbbhMzkReq cbreq = getCbMzkReq(req);
			if(cbreq==null) return false;
			
			Vector vecResponse = socketMzkCb.socketSend(socketMzkCb.getRequestOne(CmdDosDef.SENDMZK, cbreq));//vecByteArr
			if (vecResponse!=null && vecResponse.size()>0)
			{
				CbbhMzkRes res = (CbbhMzkRes)socketMzkCb.getClassObjValue((byte[])vecResponse.elementAt(0), CbbhMzkRes.class.newInstance());
				if(res.retcode==null)
				{
					showMsg("储值卡交易失败：" + socketMzkCb.getError());					
					return false;
				}
				if (!res.retcode.trim().equals("00"))
				{
					showMsg("储值卡交易失败：" + res.errmsg.trim());
					return false;
				}
				if (res!=null && ret!=null)
				{
					ret.cardno = res.cardno.trim();
					ret.ye = Convert.toDouble(res.amount.trim())/100;//比如10.05元则是1005（不含小数点）
					//ret.curdate = res.curdate.trim();交易日期
					//ret.curtime = res.curtime.trim();交易时间
					ret.cardname = res.name.trim();
					ret.money = Convert.toDouble(res.memo.trim())/100;//卡面额
					ret.func="          ";
					ret.str1 = req.track2;
				}				
								
				return true;
			}
			else
			{
				showMsg("储值卡交易失败：" + socketMzkCb.getError());
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this.showMsg("储值卡交易异常：" + ex.getMessage());
		}
		return false;
	}
	
	//新世纪储值卡
	public boolean sendMzkSale_Dos_XSJ(MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{	
		try
		{
			socketMzkXsj = createSocket(socketMzkXsj, GlobalInfo.sysPara.cbXsjMzkSvrAddress);
			if(socketMzkXsj==null) return false;
			
			CbbhXsjMzkReq cbreq = getCbXsjMzkReq(req);
			if(cbreq==null) return false;
			
			Vector vecResponse = socketMzkXsj.socketSend_XSJ(socketMzkXsj.getRequestOne_XSJ(CmdDosDef.SENDMZK, 1, cbreq));//vecByteArr
			if (vecResponse!=null && vecResponse.size()>0)
			{
				CbbhXsjMzkRes res = (CbbhXsjMzkRes)socketMzkXsj.getClassObjValue((byte[])vecResponse.elementAt(0), CbbhXsjMzkRes.class.newInstance());
				if(res.result==null)
				{
					showMsg("储值卡交易失败：" + socketMzkXsj.getError());					
					return false;
				}
				if (!res.result.trim().equals("00"))
				{
					showMsg("储值卡交易失败：" + res.errmsg.trim());
					return false;
				}
				if (res!=null && ret!=null)
				{
					ret.cardno = res.kh.trim();
					ret.ye = res.dqye;//不用转换，比如：10.05元则直接传10.05（含有小数点）
					//ret.curdate = res.time.trim();交易日期+时间
					ret.money = res.money;
					ret.func="          ";
					ret.str1 = req.track2;
				}				
								
				return true;
			}
			else
			{
				showMsg("储值卡交易失败：" + socketMzkXsj.getError());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this.showMsg("储值卡交易异常：" + ex.getMessage());
		}
		return false;
	}
	
	private CbbhMzkReq getCbMzkReq(MzkRequestDef req)
	{
		try
		{
			if(req==null) {
				new MessageBox("交易失败：请求信息为空");
				return null;			
			}
			CbbhMzkReq cbreq = new CbbhMzkReq();
			cbreq.seqno = String.valueOf(req.seqno);
			cbreq.type = req.type;
			
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{

				String a = String.valueOf(req.mktcode + req.syjh).substring(0,1);
				if(a.equals("6"))
				{
					a ="D";
				}
				else
				{
					a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				cbreq.termno = a + String.valueOf(req.mktcode + req.syjh).substring(1,String.valueOf(req.mktcode + req.syjh).length());
			}
			else
			{
				cbreq.termno = req.mktcode + req.syjh;//req.termno;
			}
			
			cbreq.syyh = req.syyh;
			cbreq.invno = String.valueOf(req.fphm);
			cbreq.amount = ManipulatePrecision.doubleToString(req.je).replace(".", "");//交易金额，精确到分，传入参数：10.05元则传1005（不含小数点）
			cbreq.track2 = req.track2;
			cbreq.track3 = req.track3;
			cbreq.passwd = GlobalInfo.sysPara.cardpasswd;//是否输入密码Y/N
			cbreq.memo = req.passwd;//面值卡密码
			return cbreq;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			showMsg("交易失败：解析请求信息时异常" + ex.getMessage());
			return null;
		}
	}
	
	private CbbhXsjMzkReq getCbXsjMzkReq(MzkRequestDef req)
	{
		try
		{
			if(req==null) {
				new MessageBox("交易失败：请求信息为空");
				return null;			
			}
			CbbhXsjMzkReq cbreq = new CbbhXsjMzkReq();
			cbreq.seqno = String.valueOf(req.seqno);
			cbreq.type = req.type;
			
			if(GlobalInfo.sysPara.isnewmktcode.equals("Y"))
			{

				String a = String.valueOf(req.mktcode + req.syjh).substring(0,1);
				if(a.equals("6"))
				{
					a = "D";
				}
				else
				{
					a = ManipulateStr.numToLetter(a).toUpperCase();
				}
				cbreq.termno = a + String.valueOf(req.mktcode + req.syjh).substring(1,String.valueOf(req.mktcode + req.syjh).length());
			}
			else
			{
				cbreq.termno = req.mktcode + req.syjh;//req.termno;
			}
			
			cbreq.syyh = req.syyh;
			cbreq.invno = String.valueOf(req.fphm);
			cbreq.amount = String.valueOf(req.je);//不用转换，比如：10.05元则直接传10.05（含有小数点）                       
			cbreq.track2 = req.track2;
			cbreq.passwd = req.passwd;//面值卡密码
			cbreq.memo = req.memo;
			return cbreq;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			showMsg("交易失败：解析请求信息时异常" + ex.getMessage());
			return null;
		}
	}
	
	private SocketDosPosServer createSocket(SocketDosPosServer socket, String server)
	{
		try
		{
			if(socket!=null) return socket;
			
			String[] srvArr = getSrv(server);
			if(srvArr==null || srvArr.length<3)
			{
				showMsg("通讯失败：本地socket连接初始化失败");
				return null;
			}
			return new SocketDosPosServer(srvArr[0].trim(), Convert.toInt(srvArr[1].trim()), Convert.toInt(srvArr[2].trim()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			showMsg("通讯失败：本地socket连接初始化异常" + ex.getMessage());
			return null;
		}
	}
	
	private String[] getSrv(String server)
	{
		try
		{
			if(server==null || server.trim().length()<0)
			{
				PosLog.getLog(this.getClass()).info("getSrv() 未配置：server=[" + server + "]");
				return null;
			}
			String[] arr = server.split("\\|");
			if(arr.length<3)
			{
				PosLog.getLog(this.getClass()).info("getSrv() 配置不正确：server=[" + server + "]");
				return null;
			}
			return arr;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			return null;
		}
	}
	
}
