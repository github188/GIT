package custom.localize.Jdhx;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.NewsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.TasksDef;

public class Jdhx_TaskExecute extends TaskExecute
{
	// 添加自动上传小票的内容
	public void executeTimeTask(boolean isTimer)
	{
		Http h = null;

		//
		if (isTimer)
			h = GlobalInfo.timeHttp;
		else
			h = GlobalInfo.localHttp;

		// 检查网络通知
		if (GlobalInfo.isOnline)
		{
			NewsDef news = NetService.getDefault().getNews(h);

			if (news != null)
			{
				// 删除网络通知
				NetService.getDefault().deleteNews(h, news.seqno);

				// 将通知加入到历史通知
				AccessLocalDB.getDefault().writeNews(news);

				if (news.title.trim().length() > 0 || news.text.trim().length() > 0)
					// 弹出通知显示窗口
					new MessageBox(news.title + "\n" + news.text);
			}
		}

		// 检查网络任务
		if (GlobalInfo.isOnline || GlobalInfo.syjStatus.netstatus == 'Z')
		{
			TasksDef task = NetService.getDefault().getTask(h);
			if (task != null)
			{
				// 主动脱网状态,非联网命令不执行
				if (!(GlobalInfo.syjStatus.netstatus == 'Z' && task.type != StatusType.TASK_ORDERCONNECT))
				{
					// 删除网络任务
					NetService.getDefault().deleteTask(h, task.seqno);

					// 保存必须执行的任务到任务表
					long seqno = 0;
					if (StatusType.isMustTask(task.type))
					{
						seqno = AccessLocalDB.getDefault().writeTask(task);
					}

					// 执行任务
					boolean ret = executeTask(task);

					// 任务执行成功，删除任务表
					if (ret && seqno > 0)
					{
						AccessLocalDB.getDefault().deleteTask(seqno);
					}
				}
			}

			// 上传未上传的小票
			task = new TasksDef();
			task.type = StatusType.TASK_SENDINVOICE;
			task.keytext = TaskExecute.getKeyTextByBalanceDate();
			sendAllSaleDataOnBackground(task.keytext);
			// executeTask(task);
			// 保留原来显示的内容 离开界面时，不设置界面
			if (GlobalInfo.saleform != null && !GlobalInfo.saleform.isDisposed() && GlobalInfo.saleform.sale != null && GlobalInfo.saleform.sale.zhongwenStyledText != null && !GlobalInfo.saleform.sale.zhongwenStyledText.isDisposed())
			{
				String show = GlobalInfo.saleform.sale.zhongwenStyledText.getText();
				GlobalInfo.saleform.sale.setBigInfo(show, "", "", -1);
			}
		}
	}

	public boolean sendAllSaleDataOnBackground(String keytext)
	{
		if (!GlobalInfo.isOnline)
			return false;

		// 将关键字分解为开始日期和结束日期
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		boolean allsendok = true;
		int errorcount = 0;

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				if (GlobalInfo.sysPara.uploadOldInfo != '0' && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox(Language.apply("将要上传{0}非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？", new Object[] { rq[0] }), null, true).verify() != GlobalVar.Key1)
				{
					// 下一天
					rq[0] = dt.skipDate(rq[0], 1);
					continue;
				}

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

								// pb.setText(Language.apply("正在发送 {0} 的 {1} 号小票.....",
								// new Object[] { rq[0],
								// String.valueOf(salehead.fphm) }));

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

								// 数据送网成功,标记为已送网
								if (!DataService.getDefault().sendSaleData(salehead, salegoods, salepay, sql))
								{
									AccessDayDB.getDefault().writeWorkLog(Language.apply("重发未送网小票失败:") + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

									// new
									// MessageBox(Language.apply("未上传小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!",
									// new Object[] { salehead.fphm + "",
									// salehead.rqsj + "" }), null, false);

									allsendok = false;

									errorcount++;
								}

								// 如果批量发送过程中,发生错误两次,则不再显示网络错误
								if (errorcount >= 2)
									NetService.getDefault().setErrorMsgEnable(false);

								// 同时向JSTORE发送
								if (fphm > 0)
									this.sendInvoiceToJSTORE(rq[0] + "," + String.valueOf(fphm), true);
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
			// 恢复网络通讯报错显示
			NetService.getDefault().setErrorMsgEnable(true);
		}

		return allsendok;
	}

}
