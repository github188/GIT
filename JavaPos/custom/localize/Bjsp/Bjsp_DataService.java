package custom.localize.Bjsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;


import net.futurn.IFuturnService;
import net.futurn.entity.InfoEntity;



import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


import custom.localize.Cmls.Cmls_DataService;

public class Bjsp_DataService extends Cmls_DataService
{
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline) { return false; }
		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("Bjsp_DataService-sendSaleData()");

		boolean again;

		// 送网小票返回数据
		Vector retValue = new Vector();

		// sql对象为空,非重发小票
		if (sql == null)
		{
			again = false;
		}
		else
		{
			again = true;
		}

		// 发送小票
		int result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);
		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("发送小票：" + result);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0) return false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2) return false;

		// 得到返回数据,可对返回数据进行处理
		if (retValue.size() > 0)
		{
			String memo = retValue.elementAt(0).toString();
			double value = Double.parseDouble(CommonMethod.isNull(retValue.elementAt(1).toString(), "0"));

			updateSendSaleData(saleHead, memo, value, sql);
		}

		// 发送小票成功后更新小票送网标志
		if (sql == null)
		{
			// 更新小票送网标志
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
		}
		else
		{
			// 重发未送网小票时，不能用sql的execute(sqltext)方法
			// 和前面selectData换一个对象执行,否则冲突
			// 更新小票送网标志
			sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
			sql.executeSql();
		}
		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("更新本地库送网标记");
		// 需要将小票发送到独立会员服务器
		if (GlobalInfo.sysPara.sendsaletocrm == 'Y')
		{
			sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
			//    		写入工作日志
			AccessDayDB.getDefault().writeWorkLog("将小票发送到独立会员服务器");

		}

		// 需要联网实时计算返券
		if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
		{
			getSellRealFQ(saleHead);
		}

		// 需要联网实时计算积分
		if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
		{
			getCustomerSellJf(saleHead);
		}
		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("将小票送往WebService");
		// 需要将小票送往WebService
		if (sendSaleWebService(saleHead, saleGoods, salePayment) != 0)
		{
			//    		写入工作日志
			AccessDayDB.getDefault().writeWorkLog("小票送往WebService失败!");

			return false;
		}
		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("小票送往WebService成功!");

		return true;
	}

	public int sendSaleWebService(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		Vector saleGoods2 = null;
		try
		{
			//if (GlobalInfo.axis == null || !((Bjsp_AxisWebService)GlobalInfo.axis).isConn()) return 0;
			
			
			if (saleGoods == null || saleGoods.size() <= 0) return 0;
			String sale_no = "";

			saleGoods2 = (Vector) saleGoods.clone();
			// 将重复的数据去掉
			for (int i = 0; i < saleGoods2.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods2.elementAt(i);

				for (int j = i + 1; j < saleGoods2.size(); j++)
				{
					SaleGoodsDef sgd1 = (SaleGoodsDef) saleGoods2.elementAt(j);

					if (!sgd.batch.equals(sgd1.batch)) continue;

					saleGoods2.remove(j);

					i = i - 1;

					break;
				}
			}
			// 打包交易号
			for (int i = 0; i < saleGoods2.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods2.elementAt(i);

				if (sgd.batch == null || sgd.batch.equals(""))
				{
					if (sgd.fph == null || sgd.fph.equals("")) continue;

					sgd.batch = sgd.fph;

				}

				sale_no = sgd.batch + "," + sale_no;
			}
			if (sale_no == null || sale_no.trim().equals("")) return 0;

			if (!(new File(GlobalVar.ConfigPath + "//BJSPWebservice.ini").exists())){
				new MessageBox("BJSPWebservice.ini文件不存在,无法获取URL地址!");
				return -1;
			}

			BufferedReader br;
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/BJSPWebservice.ini");
			if (br == null){
				new MessageBox("找不到URL地址!");
				return -1;
			}

			String line = "";
			try
			{
				if ((line = br.readLine()) != null)
				{
					if ((line == null) || (line.length() <= 0))
					{
						new MessageBox("找不到URL地址!");
						return -1;
					}
				}
			}
			catch (IOException e)
			{
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}
		
			String ReceiveMoneyType = "";
			Vector vpay = new Vector();
			for (int i = 0; i < salePayment.size(); i++)
			{
				boolean b = false;
				SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
				
				for(int j = 0;j<vpay.size();j++){
					String a[] = (String[]) vpay.elementAt(j);
					//如果有相同的支付方式
					if(a[0].equals(spd.paycode)){
						if(spd.flag=='1'){
							a[1] = String.valueOf(Double.parseDouble(a[1])+spd.je);
							b = true;
							break;
						}else if(spd.flag=='2'){
							a[1] = String.valueOf(Double.parseDouble(a[1])-spd.je);
							b = true;
							break;
						}
					}
				}
				if(!b){
					vpay.add(new String[]{spd.paycode,String.valueOf(spd.je)});
				}
			}
			/*
			// 拼付款方式
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
				ReceiveMoneyType = ReceiveMoneyType + spd.paycode + "|" + spd.je + ",";
			}
*/
			for (int ii = 0; ii < vpay.size(); ii++)
			{
				String a[] = (String[]) vpay.elementAt(ii);
				ReceiveMoneyType = ReceiveMoneyType + a[0] + "|" + a[1] + ",";
			}
			/**
			 
			 Object ret = ((Bjsp_AxisWebService)GlobalInfo.axis).executeFunction(new String[]{sale_no.substring(0,sale_no.length() - 1),saleHead.syjh + "," + String.valueOf(saleHead.fphm),"true",ReceiveMoneyType.substring(0,ReceiveMoneyType.length() -1),saleHead.rqsj,String.valueOf(saleHead.djlb)});
			 
			 //	解析返回值
			 String retstr = ((Bjsp_AxisWebService)GlobalInfo.axis).resolveStringFormat(ret);
			 
			 */

			String fphm1 = String.valueOf(saleHead.fphm);
			for (int i = fphm1.length(); i < 8; i++)
			{
				fphm1 = "0" + fphm1;
			}
			String rqsj1 = saleHead.rqsj.substring(0, 4) + "-" + saleHead.rqsj.substring(5, 7) + "-" + saleHead.rqsj.substring(8);
			String syjh1 = String.valueOf(saleHead.syjh);
			for (int i = syjh1.length(); i < 4; i++)
			{
				syjh1 = "0" + syjh1;
			}
//			写入工作日志
			AccessDayDB.getDefault().writeWorkLog("发送小票-IFuturnService");

/*			IFuturnService service = new FuturnServiceImpl();*/
			Service serviceModel = (Service) new ObjectServiceFactory().create(IFuturnService.class);

			XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());

			//String url = "http://192.168.200.171/oms_sap/services/futurnService";
			IFuturnService service = (IFuturnService) factory.create(serviceModel, line);
			//new MessageBox();
			String ssbz = "";
//			//
			if(saleHead.djlb.equals("2")){
				ssbz = "2";
			}else if(saleHead.djlb.equals("5")){
				ssbz = "5";
			}
			PosLog.getLog(getClass()).info("sale_no:"+sale_no.substring(0, sale_no.length() - 1)+" 收银机号:"+syjh1+" 小票号:"+fphm1+" 付款方式:"+ReceiveMoneyType.substring(0, ReceiveMoneyType.length() - 1)+" 日期时间:"+rqsj1);
			InfoEntity info = service.receiveMoneyCallPdaMw(sale_no.substring(0, sale_no.length() - 1), syjh1, fphm1, false,
															ReceiveMoneyType.substring(0, ReceiveMoneyType.length() - 1), rqsj1, ssbz);
			if (info == null)
			{
				new MessageBox("返回值为NULL请与信息部联系!");

				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDWEBSERVICE, GlobalInfo.balanceDate + "," + saleHead.fphm);
				return -1;
			}

			int retnum = Integer.parseInt(info.getCode());
//			写入工作日志
				

			if (retnum != 0)
			{
				switch (retnum)
				{
					case -1:
						new MessageBox("WebService初始化失败,请与信息部联系!");
						break;
					case -2:
						new MessageBox("WebServiceORA执行出错,请与信息部联系!");
						break;
					case -3:
						new MessageBox("WebService更新销售状态出错,请与信息部联系!");
						break;
					default:
						new MessageBox("WebService未知异常,请与信息部联系!");
				}
				// 记录小票未发送到WebService任务
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDWEBSERVICE, GlobalInfo.balanceDate + "," + saleHead.fphm);
			}

			return retnum;
		}
		catch (Exception ex)
		{
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDWEBSERVICE, GlobalInfo.balanceDate + "," + saleHead.fphm);
			new MessageBox("发送小票到WebService服务器异常\n请与信息部联系!" + ex.getMessage());
			ex.printStackTrace();

			return -1;
		}
		finally
		{
			if (saleGoods2 != null)
			{
				saleGoods2.clear();

				saleGoods2 = null;
			}
		}

	}
}
