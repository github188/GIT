package custom.localize.Jwyt;

import java.sql.ResultSet;
import java.util.ArrayList;
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
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jwyt_TaskExecute  extends TaskExecute
{
	public boolean sendAllPayinData(String keytext)
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
/*				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox("将要上传"+rq[0]+"非当天的缴款数据\n\n请确认这天的缴款数据真实有效以避免误传测试数据!\n\n你确定要上传这些缴款单吗？",null,true).verify() != GlobalVar.Key1)
				{
					//下一天
					rq[0] = dt.skipDate(rq[0], 1);					
					continue;
				}
				*/
				pb.setText("正在发送 " + rq[0] + " 的缴款数据.....");

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0) sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else sql = GlobalInfo.dayDB;
				if (sql != null)
				{
					ResultSet rs = null;
					PayinHeadDef payinhead = new PayinHeadDef();
					ArrayList payindetail = new ArrayList();

					try
					{
						boolean bOK;
						int seqno = 0;
						while (true)
						{
							bOK = true;
							if (rq.length >= 3) rs = sql
														.selectData("select * from PAYINHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and seqno > "
																+ seqno + " and seqno = " + rq[2] + " order by seqno");
							else rs = sql.selectData("select * from PAYINHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and seqno > "
									+ seqno + " order by seqno");
							if (rs != null && rs.next())
							{
								payindetail.clear();

								if (!sql.getResultSetToObject(payinhead))
								{
									allsendok = false;
									bOK = false;
									break;
								}
								seqno = payinhead.seqno;
								sql.resultSetClose();

								//
								pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(payinhead.seqno) + " 号缴款.....");

								// 读取缴款明细
								rs = sql.selectData("select * from PAYINDETAIL where syjh = '" + payinhead.syjh + "' and seqno = "
										+ String.valueOf(payinhead.seqno) + " order by rowno");
								while (rs != null && rs.next())
								{
									PayinDetailDef pd = new PayinDetailDef();

									if (!sql.getResultSetToObject(pd))
									{
										allsendok = false;
										bOK = false;
										break;
									}

									payindetail.add(pd);
								}
								sql.resultSetClose();
								if (!bOK) continue;

								// 数据送网成功,标记为已送网
								if (NetService.getDefault().sendPayin(payinhead, payindetail))
								{
									// 和前面selectData换一个对象执行,否则冲突
									sql.setSql("update PAYINHEAD set netbz = 'Y' where syjh = '" + payinhead.syjh + "' and seqno = "
											+ String.valueOf(payinhead.seqno));
									sql.executeSql();
								}
								else
								{
									//new MessageBox("重发未送网缴款失败:" + payinhead.seqno + "," + payinhead.rqsj);
									AccessDayDB.getDefault().writeWorkLog("重发未送网缴款失败:" + payinhead.seqno + "," + payinhead.rqsj,
																			StatusType.WORK_SENDERROR);

									allsendok = false;

									errorcount++;
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
/*				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox("将要上传"+rq[0]+"非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？",null,true).verify() != GlobalVar.Key1)
				{
					//下一天
					rq[0] = dt.skipDate(rq[0], 1);					
					continue;
				}*/
				
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
