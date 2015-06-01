package custom.localize.Wqbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_TaskExecute;

public class Wqbh_TaskExecute extends Bcrm_TaskExecute
{
	public boolean sendAllSaleData(String keytext)
	{
		if (!GlobalInfo.isOnline) return false;
		//GlobalInfo.sysPara.mktcode = "01,2303";
		keytext=keytext.replaceAll("↑", "").trim();
		if (!GlobalInfo.sysPara.mktcode.equals("01,2303"))
		{
			return super.sendAllSaleData(keytext);
		}
		else
		{   
			boolean a = true;
			if(GlobalInfo.syjStatus.status!=StatusType.STATUS_START ){
				a = sendAllTaxSaleData(keytext);
			}
			
			boolean b = super.sendAllSaleData(keytext);
			if (a && b) return true;
		}
		return false;

	}
	
	private boolean sendAllTaxSaleData(String keytext)
	{
		String[] rq = keytext.split(",");
		if (rq.length >= 3)
		{
			Wqbh_SaleBillMode ws = new Wqbh_SaleBillMode();
			return ws.sendLastSaleFax(Long.parseLong(rq[2])+1,GlobalInfo.syjDef.syjh);
		}
		else if (rq.length == 2)
		{
			boolean allsendok = true;
			ProgressBox pb = new ProgressBox();
			int errorcount = 0;

			try
			{
				ManipulateDateTime dt = new ManipulateDateTime();
				//boolean flginfo = true;
				while (dt.compareDate(rq[0], rq[1]) <= 0)
				{
					pb.setText("正在发送 " + rq[0] + " 的税控数据.....");

					// 打开每日数据库
					Sqldb sql = null;
					if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0) sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
					else sql = GlobalInfo.dayDB;
					if (sql != null)
					{
						ResultSet rs = null;
						SaleHeadDef salehead = new SaleHeadDef();
						Vector salegoods = new Vector();
						Vector salepay = new Vector();

						try
						{
							boolean bOK;
							long fphm = 0;
							//double num1 = 0;
							while (true)
							{
								bOK = true;
								rs = sql.selectData("select * from SALEHEAD where (num1 = '-1') and fphm > " + String.valueOf(fphm)
										+ " order by fphm");
								if (rs != null && rs.next())
								{
									salegoods.removeAllElements();
									salepay.removeAllElements();

									if (!sql.getResultSetToObject(salehead))
									{
										allsendok = false;
										bOK = false;
										break;
									}
									fphm = salehead.fphm;
									if(SellType.ISBACK(salehead.djlb)) continue;
									//num1 = salehead.num1;
									sql.resultSetClose();

									//
									pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(salehead.fphm) + " 号小票税控信息.....");

									// 读取商品明细
									rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = "
											+ String.valueOf(salehead.fphm) + " order by rowno");
									while (rs != null && rs.next())
									{
										SaleGoodsDef sg = new SaleGoodsDef();

										if (!sql.getResultSetToObject(sg))
										{
											allsendok = false;
											bOK = false;
											break;
										}

										salegoods.add(sg);
									}
									sql.resultSetClose();
									if (!bOK) continue;

									// 读取付款明细
									rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = "
											+ String.valueOf(salehead.fphm) + " order by rowno");
									while (rs != null && rs.next())
									{
										SalePayDef sp = new SalePayDef();

										if (!sql.getResultSetToObject(sp))
										{
											allsendok = false;
											bOK = false;
											break;
										}

										salepay.add(sp);
									}
									sql.resultSetClose();
									if (!bOK) continue;
									Wqbh_SaleBillMode ws = new Wqbh_SaleBillMode();
									if (!ws.getDQTaxInfo(salehead, salegoods, salepay, false))
									{
										//如果不成功，则标记税控上传标志为-1
										AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "num1", "-1");
										AccessDayDB.getDefault().writeWorkLog("重发税控小票信息失败:" + salehead.fphm + "," + salehead.rqsj,
																				StatusType.WORK_SENDERROR);

										new MessageBox("未上传税控小票号:" + salehead.fphm + "\n小票生成日期:" + salehead.rqsj + "\n记下信息请与信息部联系!", null, false);

										allsendok = false;

										errorcount++;
										//return false;
									}
									else
									{
										AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "num1", "1");
										AccessDayDB.getDefault().writeWorkLog("重新上传税控小票"+salehead.fphm+"成功");
									}

									// 如果批量发送过程中,发生错误三次,则不再继续发送
									if (errorcount >= 3) return false;
								}
								else
								{
									break;
								}
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
						finally
						{
							sql.resultSetClose();
						}
					}

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}

					// 下一天
					rq[0] = dt.skipDate(rq[0], 1);
					//flginfo = true;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				pb.close();

				// 恢复网络通讯报错显示
				NetService.getDefault().setErrorMsgEnable(true);
			}

			return allsendok;
		}
		return false;
	}
	
	public boolean sendAllAgainData(char type, String keytext)
	{
		keytext=keytext.replaceAll("↑", "").trim();
		return super.sendAllAgainData(type, keytext);
	}
}
