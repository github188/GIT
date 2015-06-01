package custom.localize.Zmsy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Zmjc.Zmjc_DataService;

public class Zmsy_DataService extends Zmjc_DataService
{
	protected Zmsy_NetService netservice = (Zmsy_NetService)NetService.getDefault();
	protected Zmsy_AccessDayDB  dayDB = (Zmsy_AccessDayDB)AccessDayDB.getDefault();
	
	public boolean sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust)
	{
		boolean blnRet = super.sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust);
		if (blnRet && saleHead.str8!=null && saleHead.str8.charAt(0)=='Y')
		{
			//有税款机，小票上传成功之后获取打印序号（将其打印在小票上）
			findPrintSeq(saleHead);
		}	
		return blnRet;
	}
	
	public void findPrintSeq(SaleHeadDef saleHead)
	{
		StringBuffer sbSJSeq = new StringBuffer();
		StringBuffer sbHBSeq = new StringBuffer();
		if (findPrintSeq(saleHead.syjh, saleHead.fphm, sbSJSeq, sbHBSeq))
		{
			saleHead.num7=Convert.toDouble(sbSJSeq.toString());
			saleHead.num8=Convert.toDouble(sbHBSeq.toString());
			PosLog.getLog(this.getClass().getSimpleName()).info("salehead.num7_sjseq=[" + sbSJSeq.toString() + "],salehead.num8_hbseq=[" + sbHBSeq.toString() + "].");
			
			//保存序号到本地
			dayDB.savePrintSeq(saleHead.syjh, saleHead.fphm, Convert.toDouble(sbSJSeq.toString()), Convert.toDouble(sbHBSeq));
		}
	}
	
	public boolean findPrintSeq(String syjh, long fphm, StringBuffer sbSJSeq, StringBuffer sbHBSeq)
	{
		return netservice.findPrintSeq(syjh, fphm, sbSJSeq, sbHBSeq);
	}
	
	//获取补税金额
	public boolean sendLimitJEStr(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline)		
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr网络连接失败");
			//new MessageBox("网络连接失败,无法获取补税金额信息！");
			return false;
		}
		
		if (strList==null || strList.trim().length()<1)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr拼装限额字符串失败：strList=[" + String.valueOf(strList) + "]");
			return true;
		}
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("获取补税金额......");
	        return netservice.sendLimitJEStr(strList, gwkh, sbList);
	        
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
    	finally
        {
    		 if (pb != null)
             {
                 pb.close();
                 pb = null;
             }
        }
	}
	

	//获取暂缴税金
	public boolean sendLimitJEStr_ZJSJ(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline)		
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr_ZJSJ网络连接失败");
			//new MessageBox("网络连接失败,无法获取暂缴税金信息！");
			return false;
		}
		/*if(1==1) //test
		{
			sbList.append("0,C017255,3348901064736,0.1,39,390,0,0,,,0");
			return true;
		}*/
		if (strList==null || strList.trim().length()<1)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr_ZJSJ拼装限额字符串失败：strList=[" + String.valueOf(strList) + "]");
			return true;
		}
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("获取暂缴税金......");
	        return netservice.sendLimitJEStr_ZJSJ(strList, gwkh, sbList);
	        
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
    	finally
        {
    		 if (pb != null)
             {
                 pb.close();
                 pb = null;
             }
        }
	}
	
	public boolean checkLessGoodsSL(String strList, String gwkh, StringBuffer sbList)
	{
		if (!GlobalInfo.isOnline)		
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("checkGoodsSL网络连接失败");
			new MessageBox("网络连接失败,联网检查非超额商品的限量！");
			return false;
		}
		
		if (strList==null || strList.trim().length()<1)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("checkGoodsSL拼装限额字符串失败：strList=[" + String.valueOf(strList) + "]");
			return true;
		}
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("正在检查商品限量......");
	        return netservice.checkLessGoodsSL(strList, gwkh, sbList);
	        
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
    	finally
        {
    		 if (pb != null)
             {
                 pb.close();
                 pb = null;
             }
        }
	}
	
	public boolean findGwkInfo(GwkDef gwk)
	{
		try
		{
			if (GlobalInfo.isOnline)
			{	
				gwk.syjh=GlobalInfo.syjDef.syjh;
				gwk.syyh=GlobalInfo.posLogin.gh;
				/*if (GlobalInfo.syjDef.priv==null || GlobalInfo.syjDef.priv.length()<=0) GlobalInfo.syjDef.priv="N";
				gwk.ismsj=GlobalInfo.syjDef.priv.charAt(0);*/
				
				gwk.code = gwk.zjlb + gwk.passport;//购物卡号为证件类别+证件号
				if (netservice.findGwkInfo(gwk.code ,gwk.zjlb ,gwk.passport ,gwk.gklb , gwk))
				{
					((Zmsy_SaleBS)GlobalInfo.saleform.sale.saleBS).setFlightsRows(gwk.num2);
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("查询购物卡失败,网络不通");
				new MessageBox("查询购物卡失败,网络不通");
			}
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return false;
	}
	
	public int sendGwkInfo(GwkDef gwk, StringBuffer sbMsg)
	{
		if (GlobalInfo.isOnline)
		{			
			gwk.syjh=GlobalInfo.syjDef.syjh;
			gwk.syyh=GlobalInfo.posLogin.gh;
			if (GlobalInfo.syjDef.priv==null || GlobalInfo.syjDef.priv.length()<=0) GlobalInfo.syjDef.priv="N";
			if (GlobalInfo.saleform.sale.saleBS.saleHead.str8!=null && GlobalInfo.saleform.sale.saleBS.saleHead.str8.length()>=2)
			{
				gwk.ismsj=GlobalInfo.syjDef.priv.charAt(0) + "" + GlobalInfo.saleform.sale.saleBS.saleHead.str8.charAt(1);
			}
			else
			{
				gwk.ismsj=GlobalInfo.syjDef.priv.charAt(0) + "N";
			}			
			
			if (gwk.zjlb!=null && gwk.zjlb.trim().equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ)==false) gwk.address = "";//当证件类型不是身份证时，则清除住址
			gwk.ljrq = gwk.ljrq + " " + gwk.ljsj;
			gwk.code = gwk.zjlb + gwk.passport;
			if (netservice.sendGwkInfo(gwk, sbMsg)) return 1;
			return 0;
				
		}
		else
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("非联网状态下无法保存卡");
			sbMsg.append("非联网状态下无法保存卡");
			return 0;
		}
	}
	
	public int sendHGPT(String code, double ldcs, double zssl, double gwje, String xgjs, StringBuffer sbMsg)
	{
		if (GlobalInfo.isOnline)
		{			
			if (netservice.sendHGPTInfo(code, ldcs, zssl, gwje, xgjs, sbMsg)) return 1;
			return 0;
				
		}
		else
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("非联网状态下无法将海关信息发送到数据库");
			sbMsg.append("非联网状态下无法将海关信息发送到数据库");
			return 0;
		}
	}
	
	

	/**
	 * 检查提货单号；联网，脱网
	 * @param fphm
	 * @param syjh
	 * @param thdh
	 * @return boolean 返回是否成功
	 */
	public boolean checkTHDH(String fphm, String syjh, String thdh)
	{
		PosLog.getLog(this.getClass().getSimpleName()).info("checkTHDH() fphm=[" + fphm + "],syjh=[" + syjh + "],thdh=[" + thdh + "].");
		if (GlobalInfo.isOnline)
		{	
			//联网过程检查提货单号合法性  
			if(netservice.checkTHDH( String.valueOf(fphm), syjh, thdh.toString() )) return true;
		}
		else
		{
			//脱网检查提货单号的正确性；
			Vector v = new Vector();
			
			//从本地数据库取提货单信息
			dayDB.getTHD(thdh.toString(), v);
			
			//脱网检查提货单号不成功
			if(v != null || v.size() != 0) return true;
		}
		return false;
	}	
	
	/**
	 * 提货单号保存到本地DAY，把本地取到的未上传提货单信息上传，将上传的提货单号上传标志插入到DAY.DB3库的THD表
	 * @param fphm
	 * @param syjh
	 * @param thdh
	 * @return boolean 返回是否成功
	 */
	public boolean saveTHDH(String fphm, String syjh, String thdh)
	{
		//保存到本地提货单；将提货单插入到DAY.DB3库的THD表
		THDDef thd = new THDDef();
		thd.fphm = Convert.toLong(fphm);
		thd.syjh = syjh;
		thd.thdh = thdh.toString();
		thd.net_bz = 'N';
		
		//提货单号保存到本地数据库
		if(dayDB.saveTHDH(thd))
		{
			Vector v = new Vector();
			
			//从本地取提货单未上传数据
			dayDB.getTHD("", v);
			for(int i = 0; i < v.size(); i++)
			{
				//把本地取到的提货单信息loop上传
				THDDef thdDef = (THDDef)v.get(i);
				String sFPHM = String.valueOf( thdDef.fphm );
				String sTHDH = thdDef.thdh;
				
				//过程上传提货单号 SENDTHDH
				if(netservice.sendTHD( sFPHM, syjh, sTHDH ))
				{
					//上传成功后修改上传标志，并保存本地
					thdDef.net_bz = 'Y';
					
					//修改本地数据库提货单上传状态 
					dayDB.updateTHDH( thdDef.net_bz, sFPHM, syjh, sTHDH );
				}
				else
				{
					//上传失败,下次再发
				}
			}
			//录入提货单结束
			return true;
		}
		else
		{
			//保存本地提货单不成功
			return false;
		}
	}
	
	public boolean getNetMemoInfo()
	{
		boolean blnRet = false;
		try
		{
			if (GlobalInfo.isOnline)
			{
				//从POSSERVER获取航班信息,并保存到local.db3 用于购物卡界面的航班下拉
				blnRet = netservice.getFlights();
				((Zmsy_SaleBS)GlobalInfo.saleform.sale.saleBS).clearFlights();//联网后,清除航班信息
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return blnRet;
	}
			
	 public boolean deleteOneSell(String syjh, long fphm)
	 {
		 //2012.8.14燕双要求：当超过限量时（过程判断），则删除此小票
		 PosLog.getLog(this.getClass().getSimpleName()).info("上传小票时，过程反133[当超过限量时（过程判断），则删除此小票]，所以删除本地小票syjh=[" + syjh  + "],fphm=[" + String.valueOf(fphm) + "].");
		 return dayDB.deleteSale(syjh, fphm);
	 }
	
}
