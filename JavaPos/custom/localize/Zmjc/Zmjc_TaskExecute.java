package custom.localize.Zmjc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zmjc_TaskExecute extends TaskExecute
{
	public boolean sendAllSaleData(String keytext)
	{
		if (!GlobalInfo.isOnline)
			return false;

		// 将关键字分解为开始日期和结束日期
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();
		int errorcount = 0;

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox(Language.apply("将要上传{0}非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？", new Object[]{rq[0]}), null, true).verify() != GlobalVar.Key1)
				{
					// 下一天
					rq[0] = dt.skipDate(rq[0], 1);
					continue;
				}

				pb.setText(Language.apply("正在发送{0}的销售数据.....", new Object[]{rq[0]}));

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else
					sql = GlobalInfo.dayDB;
				if (sql != null)
				{
					ResultSet rs = null;
					SaleHeadDef salehead = new SaleHeadDef();
					Vector salegoods = new Vector();
					Vector salepay = new Vector();
					SaleCustDef saleCust = new SaleCustDef();

					try
					{
						boolean bOK;
						long fphm = 0;
						while (true)
						{
							bOK = true;
							if (rq.length >= 3)
								rs = sql.selectData("select * from SALEHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and fphm > " + String.valueOf(fphm) + " and fphm = " + rq[2] + " order by fphm");
							else
								rs = sql.selectData("select * from SALEHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and fphm > " + String.valueOf(fphm) + " order by fphm");
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
								pb.setText(Language.apply("正在发送{0}的{1}号小票.....", new Object[]{rq[0],String.valueOf(salehead.fphm)}));

								// 读取商品明细
								rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
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
								if (!bOK)
									continue;

								// 读取付款明细
								rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
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
								if (!bOK)
									continue;
								
								 //读取顾客信息
								ParaNodeDef node;
								bOK=false;
								rs = sql.selectData("select * from SALECUST where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " ");
								if (rs != null && rs.next())
								{
									saleCust = new SaleCustDef();
									//while (rs != null && rs.next())
									//{					
										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCPASSPORTNO;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);		
										
										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCNATIONALITY;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);
																			
										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCID;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);

										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCOTHERNO;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);

										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCNUMBER;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);


										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCNAME;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);

										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCSEX;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);

										node = new ParaNodeDef();
						            	node.code=CustInfoDef.CUST_SCMEMO;
						            	node.name = "";
						            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
										saleCust.custAdd(node.code, node);
										bOK=true;
									//}
									sql.resultSetClose();
									if (!bOK)
										continue;
								}
								else
								{
									//没有找到顾客信息
									saleCust = null;
								}
								

								// 数据送网成功,标记为已送网
								if (!DataService.getDefault().sendSaleDataCust(salehead, salegoods, salepay, saleCust, sql))
								{
									AccessDayDB.getDefault().writeWorkLog("重发未送网小票失败:" + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

									new MessageBox(Language.apply("未上传小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!", new Object[]{salehead.fphm + "",salehead.rqsj}), null, false);

									allsendok = false;

									errorcount++;
								}

								// 如果批量发送过程中,发生错误两次,则不再显示网络错误
								if (errorcount >= 2)
									NetService.getDefault().setErrorMsgEnable(false);

								/*// 同时向JSTORE发送
								if (fphm > 0)
									this.sendInvoiceToJSTORE(rq[0] + "," + String.valueOf(fphm), true);*/
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

	public boolean sendAllSaleAppendData(String keytext)
	{
		if (!GlobalInfo.isOnline)
			return false;

		// 将关键字分解为开始日期和结束日期
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;
		boolean allsendok = false;

		//
		ProgressBox pb = new ProgressBox();
		ArrayList seqnolist = null;

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				pb.setText(Language.apply("正在发送{0}的工作日志.....", new Object[]{rq[0]}));

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else
					sql = GlobalInfo.dayDB;
				if (sql != null)
				{
					ResultSet rs = null;

					seqnolist = new ArrayList();

					try
					{
						Vector saleappend = new Vector();

						rs = sql.selectData("select * from SaleAppend where (netbz <> 'Y' or netbz is null or netbz = '')");
						while (rs != null && rs.next())
						{
							saleappend.clear();
							SaleAppendDef sad = new SaleAppendDef();

							sql.getResultSetToObject(sad);

							saleappend.add(sad);

							seqnolist.add(new String[] { sad.syjh, String.valueOf(sad.fphm), String.valueOf(sad.rowno) });
						}

						// 数据送网成功,标记为已送网
						if (saleappend.size() > 0 && (allsendok = NetService.getDefault().sendSaleAppend(saleappend)))
						{
							//
							sql.beginTrans();
							for (int i = 0; i < seqnolist.size(); i++)
							{
								String[] strs = ((String[]) seqnolist.get(i));
								sql.setSql("update SaleAppend set netbz = 'Y' where syjh = '" + strs[0] + "' and fphm = " + strs[1] + " and rowno = " + strs[2]);
								sql.executeSql();
							}
							sql.commitTrans();
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

			if (seqnolist != null)
			{
				seqnolist.clear();
				seqnolist = null;
			}

			// 恢复网络通讯报错显示
			NetService.getDefault().setErrorMsgEnable(true);
		}

		return allsendok;
	}
	
}
