package custom.localize.Cczz;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_TaskExecute;

public class Cczz_TaskExecute extends Bhls_TaskExecute
{
	public boolean sendAllSaleData(String keytext)
	{
		if (!GlobalInfo.isOnline) return false;

		// 将关键字分解为开始日期和结束日期
		String[] rq = keytext.split(",");
		if (rq.length < 2) return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();
		int errorcount = 0;

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				pb.setText("正在发送 " + rq[0] + " 的销售数据.....");

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
						while (true)
						{
							bOK = true;
							if (rq.length >= 3) rs = sql
														.selectData("select * from SALEHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and fphm > "
																+ String.valueOf(fphm) + " and fphm = " + rq[2] + " order by fphm");
							else rs = sql.selectData("select * from SALEHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and fphm > "
									+ String.valueOf(fphm) + " order by fphm");
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
								sql.resultSetClose();

								//
								pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(salehead.fphm) + " 号小票.....");

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

								// 数据送网成功,标记为已送网
								if (!DataService.getDefault().sendSaleData(salehead, salegoods, salepay, sql))
								{
									AccessDayDB.getDefault().writeWorkLog("重发未送网小票失败:" + salehead.fphm + "," + salehead.rqsj,
																			StatusType.WORK_SENDERROR);

									new MessageBox("未上传小票号:" + salehead.fphm + "\n小票生成日期:" + salehead.rqsj + "\n记下信息请与信息部联系!", null, false);

									allsendok = false;

									errorcount++;
								}

								// 打印小票前先查询满赠信息并设置到打印模板供打印
								if (!SellType.ISEXERCISE(salehead.djlb))
								{
									DataService dataservice = (DataService) DataService.getDefault();
									dataservice.getSaleTicketMSInfo(salehead, salegoods, salepay);
								}

								// 如果批量发送过程中,发生错误两次,则不再显示网络错误
								if (errorcount >= 2) NetService.getDefault().setErrorMsgEnable(false);
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
}
