package com.efuture.javaPos.Global;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.Calculator;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.DownloadData;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Logic.ConnNetWorkBS;
import com.efuture.javaPos.Logic.DisConnNetWorkBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.RemoveDayBS;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CallInfoDef;
import com.efuture.javaPos.Struct.NewsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.TasksDef;
import com.efuture.javaPos.Struct.WorkLogDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.PersonnelGoForm;

public class TaskExecute
{
	public static TaskExecute currentTaskExecute = null;

	public static TaskExecute getDefault()
	{
		if (TaskExecute.currentTaskExecute == null)
		{
			TaskExecute.currentTaskExecute = CustomLocalize.getDefault().createTaskExecute();
		}

		return TaskExecute.currentTaskExecute;
	}

	public boolean openDrawGrant()
	{
		if (GlobalInfo.posLogin.priv.length() > 5 && GlobalInfo.posLogin.priv.charAt(5) != 'Y')
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("收银开钱箱授权"));

			if (staff != null)
			{
				if (staff.priv.length() > 5 && staff.priv.charAt(5) != 'Y')
				{
					new MessageBox(Language.apply("当前工号没有开钱箱权限!"));
					return false;
				}

				String log = Language.apply("授权临时打开钱箱,授权工号:") + staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	public boolean executeFuncKey(int keyValue)
	{
		boolean exec = false;

		// 功能键必须登录以后才能使用
		if (GlobalInfo.posLogin == null)
			return false;
		// 收银员离开不允许使用功能键
		if (GlobalInfo.syjStatus.status == StatusType.STATUS_LEAVE)
			return false;

		// 先执行扩展
		exec = otherExecuteFuncKey(keyValue);
		if (!exec)
		{
			switch (keyValue)
			{
			case GlobalVar.Caculator: // 计算器键
			{
				new Calculator();

				exec = true;
				break;
			}
			case GlobalVar.OpenDraw: // 开钱箱键
			{
				// 检查钱箱授权
				if (openDrawGrant())
				{
					StringBuffer buffer = new StringBuffer();
					if (new TextBox().open(Language.apply("请输入收银员密码"), "PASSWORD", Language.apply("非交易状态下开钱箱,必须输入正确的收银员密码"), buffer, 0, 0, false, TextBox.AllInput, GlobalVar.MaxlengthOfPasswd))
					{
						if (ManipulatePrecision.getEncrypt(buffer.toString()).equals(GlobalInfo.posLogin.passwd))
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("非交易状态下打开钱箱") + "(" + Convert.increaseLong(GlobalInfo.syjStatus.fphm, 7) + ")", StatusType.WORK_OPENCASHDRAWER);

							// 开钱箱
							CashBox.getDefault().openCashBox();
						}
						else
						{
							new MessageBox(Language.apply("密码不正确,不能打开钱箱"));
						}
					}
				}

				exec = true;
				break;
			}
			case GlobalVar.Leave: // 离开键
			{
				if (new MessageBox(Language.apply("你确定要离开并锁定收银机吗?"), null, true).verify() == GlobalVar.Key1)
				{
					new PersonnelGoForm();
				}

				exec = true;
				break;
			}
			case GlobalVar.Call: // 呼叫键
			{
				String[] title = { Language.apply("代码"), Language.apply("描述") };
				int[] width = { 60, 440 };
				String[] content = null;
				Vector contents = new Vector();
				for (int i = 0; GlobalInfo.callInfo != null && i < GlobalInfo.callInfo.size(); i++)
				{
					CallInfoDef info = (CallInfoDef) GlobalInfo.callInfo.elementAt(i);
					content = new String[2];
					content[0] = info.code;
					content[1] = info.text;
					contents.add(content);
				}

				int choice = new MutiSelectForm().open(Language.apply("请选择呼叫信息"), title, width, contents, true);
				if (choice >= 0)
				{
					ProgressBox pb = new ProgressBox();
					pb.setText(Language.apply("正在发送呼叫信息，请等待....."));

					NetService.getDefault().sendCallInfo((CallInfoDef) GlobalInfo.callInfo.elementAt(choice));

					pb.close();
					pb = null;
				}

				exec = true;
				break;
			}
			case GlobalVar.MzkInfo: // 面值卡查询键
			{
				MzkInfoQueryBS mzk = CustomLocalize.getDefault().createMzkInfoQueryBS();
				mzk.QueryMzkInfo();
				mzk = null;

				exec = true;
				break;
			}
			case GlobalVar.HykInfo: // 顾客卡查询键
			{
				HykInfoQueryBS hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
				hyk.QueryHykInfo();
				hyk = null;

				exec = true;
				break;
			}
			}
		}

		return exec;
	}

	public boolean otherExecuteFuncKey(int keyValue)
	{
		return false;
	}

	public static String getKeyTextByBalanceDate()
	{
		return GlobalInfo.balanceDate + "," + GlobalInfo.balanceDate;
	}

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
		}
	}

	public boolean executeTask(TasksDef task)
	{
		boolean ret = true;

		switch (task.type)
		{
		case StatusType.TASK_SENDINVOICE:
			ret = sendAllSaleData(task.keytext);
			break;
		case StatusType.TASK_SENDPAYJK:
			ret = sendAllPayinData(task.keytext);
			break;
		case StatusType.TASK_SENDWORKLOG:
			ret = sendAllWorkLog(task.keytext);
			break;
		case StatusType.TASK_SENDERRORLOG:
			break;
		case StatusType.TASK_SENDINVOICEAGAIN:
			ret = sendAllAgainData(StatusType.TASK_SENDINVOICE, task.keytext);
			break;
		case StatusType.TASK_SENDPAYJKAGAIN:
			ret = sendAllAgainData(StatusType.TASK_SENDPAYJK, task.keytext);
			break;
		case StatusType.TASK_SENDWORKLOGAGAIN:
			ret = sendAllAgainData(StatusType.TASK_SENDWORKLOG, task.keytext);
			break;
		case StatusType.TASK_SENDBANKLOGAGAIN:
			ret = sendAllAgainData(StatusType.TASK_SENDBANKLOG, task.keytext);
			break;
		case StatusType.TASK_DELETEDATA:
			ret = DeleteDataBase(task.keytext);
			break;
		case StatusType.TASK_POSINIT:
			ret = DeleteDataBase(task.keytext);
			break;
		case StatusType.TASK_ORDERSHUTDOWN:
			ret = orderShutDown();
			break;
		case StatusType.TASK_ORDERDISCONNECT:
			ret = orderDisconnect();
			break;
		case StatusType.TASK_EXECPROC:
			ret = execProcess(task.keytext);
			break;
		case StatusType.TASK_SENDMZKLOG:
			break;
		case StatusType.TASK_ORDERCONNECT:
			ret = orderConnect();
			break;
		case StatusType.TASK_DOWNLOADAGAIN:
			ret = AgainDownloadDB();
			break;
		case StatusType.TASK_SENDBANKLOG:
			ret = sendBankLog(task.keytext);
			break;
		case StatusType.TASK_SETINVNO:
			ret = setInvNo(task.keytext);
			break;
		case StatusType.TASK_SENDINVTOEXTEND:
			ret = sendInvoiceToExtend(task.keytext);
			break;
		case StatusType.TASK_DELETELOCALTASK:
			ret = deleteLocalTask();
			break;
		case StatusType.TASK_SENDWEBSERVICE:
			ret = sendInvoiceToWebService(task.keytext);
			break;
		case StatusType.TASK_SENDSALEAPPEND:
			ret = sendAllSaleAppendData(task.keytext);
			break;
		case StatusType.TASK_SENDHYKJF:
			ret = sendHykJf(task.keytext);
			break;
		case StatusType.TASK_SENDSALETOJSTORE:
			ret = this.sendInvoiceToJSTORE(task.keytext, false);
		}

		return ret;
	}

	public boolean execProcess(String keytext)
	{
		try
		{
			CommonMethod.waitForExec(keytext);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public boolean AgainDownloadDB()
	{
		// 删除数据库日期戳
		DownloadData.deleteDBDate();

		//
		new MessageBox(Language.apply("下次启动收银机时,将重新下载基础数据库!"));

		return true;
	}

	/***
	 * 当接收的命令为“POSInit”就是开机时执行收银机初始化操作，否则执行
	 * 
	 * @param keytext
	 * @return
	 */
	public boolean DeleteDataBase(String keytext)
	{
		if ("posinit".equalsIgnoreCase(keytext))
		{
			// 记录日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令进行初始化操作!"), StatusType.WORK_POSINIT);

			// 删除历史日数据
			RemoveDayBS.createRemovePathFlag(keytext);

			//
			new MessageBox(Language.apply("收银机接收到初始化命令，\n下次启动时将删除本机所有历史数据。请立即重启"));
		}
		else
		{
			// 记录日志
			// AccessDayDB.getDefault().writeWorkLog("收银机被命令删除包括 " + keytext +
			// " 之前的历史数据!", StatusType.WORK_ORDERDELETETICKET);
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令删除包括 {0} 之前的历史数据!", new Object[] { keytext }), StatusType.WORK_ORDERDELETETICKET);

			// 删除历史日数据
			RemoveDayBS.createRemovePathFlag(keytext);

			//
			// new MessageBox("下次启动收银机时,将删除包括 " + keytext +
			// " 之前的历史数据!\n\n请现在立即启动收银机");
			new MessageBox(Language.apply("下次启动收银机时,将删除包括 {0} 之前的历史数据!\n\n请现在立即启动收银机", new Object[] { keytext }));
		}

		return true;
	}

	public boolean orderShutDown()
	{
		// 记录日志
		AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令关机!"), StatusType.WORK_ORDERSHUTDOWN);

		// 关机
		Display display = Display.getDefault();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				GlobalInfo.background.quitSysInfo();
			}
		});

		return true;
	}

	public boolean orderDisconnect()
	{
		// 记录日志
		AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令脱网运行!"), StatusType.WORK_ORDERDISCONNECT);

		// 脱网操作
		DisConnNetWorkBS dcnwb = CustomLocalize.getDefault().createDisConnNetWorkBS();
		dcnwb.setDisConnNet();

		//
		new MessageBox(Language.apply("收银机被命令进入脱网运行!"));

		return true;
	}

	public boolean setInvNo(String keytext)
	{
		try
		{
			// 记录日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令重新设定小票号为{0}", new Object[] { keytext }), StatusType.WORK_ORDERRESETTICKETID);

			GlobalInfo.syjStatus.fphm = Integer.parseInt(keytext);
			AccessLocalDB.getDefault().writeSyjStatus();

			new MessageBox(Language.apply("收银机被命令重新设定小票号为{0}", new Object[] { keytext }));
			return true;
		}
		catch (Exception er)
		{
			// new MessageBox("重新设定小票号为 " + keytext + " 失败");
			new MessageBox(Language.apply("重新设定小票号为 {0} 失败", new Object[] { keytext }));
			return false;
		}
	}

	public boolean orderConnect()
	{
		// 记录日志
		AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令联网运行!"));

		// 联网操作
		ConnNetWorkBS cnwb = CustomLocalize.getDefault().createConnNetWorkBS();
		cnwb.setConnNet();

		//
		new MessageBox(Language.apply("收银机被命令进入联网运行!"));

		return true;
	}

	public boolean deleteLocalTask()
	{
		// 记录日志
		AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令删除所有历史任务!"));

		GlobalInfo.localDB.executeSql("delete from TASKS");

		new MessageBox(Language.apply("收银机所有历史任务已被删除!"));

		return true;
	}

	public boolean sendAllWorkLog(String keytext)
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
		ArrayList seqnolist = null;

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				// pb.setText("正在发送 " + rq[0] + " 的工作日志.....");
				pb.setText(Language.apply("正在发送 {0} 的工作日志.....", new Object[] { rq[0] }));

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else
					sql = GlobalInfo.dayDB;
				if (sql != null)
				{
					ResultSet rs = null;
					WorkLogDef wl = new WorkLogDef();

					seqnolist = new ArrayList();

					try
					{
						if (rq.length >= 3)
							rs = sql.selectData("select * from WORKLOG where (netbz <> 'Y' or netbz is null or netbz = '') and seqno = " + rq[2]);
						else
							rs = sql.selectData("select * from WORKLOG where (netbz <> 'Y' or netbz is null or netbz = '')");
						while (rs != null && rs.next())
						{
							sql.getResultSetToObject(wl);

							// 断点数据不发到后台POS库，影响POS关机速度
							if (wl.code.equals(StatusType.WORK_WRITEBRODATA) || wl.code.equals(StatusType.WORK_READBRODATA) || wl.code.equals(StatusType.WORK_CLEARBRODATA))
								continue;

							// 数据送网成功,标记为已送网
							if (NetService.getDefault().sendWorkLog(wl))
							{
								// 和前面selectData换一个对象执行,否则冲突
								seqnolist.add(String.valueOf(wl.seqno));
							}
							else
							{
								allsendok = false;
								errorcount++;
							}

							// 如果批量发送过程中,发生错误两次,则不再显示网络错误
							if (errorcount >= 2)
								NetService.getDefault().setErrorMsgEnable(false);
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

					//
					sql.beginTrans();
					for (int i = 0; i < seqnolist.size(); i++)
					{
						sql.setSql("update WORKLOG set netbz = 'Y' where seqno = " + seqnolist.get(i));
						sql.executeSql();
					}
					sql.commitTrans();
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

//				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox("将要上传" + rq[0] + "非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？", null, true).verify() != GlobalVar.Key1)
				if (GlobalInfo.sysPara.uploadOldInfo != '0' && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox(Language.apply("将要上传{0}非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？" , new Object[]{rq[0]}), null, true).verify() != GlobalVar.Key1)

				// if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new
				// MessageBox("将要上传" + rq[0] +
				// "非当天的交易数据\n\n请确认这天的交易数据真实有效以避免误传测试数据!\n\n你确定要上传这些交易小票吗？",
				// null, true).verify() != GlobalVar.Key1)

				{
					// 下一天
					rq[0] = dt.skipDate(rq[0], 1);
					continue;
				}

				// pb.setText("正在发送 " + rq[0] + " 的销售数据.....");
				pb.setText(Language.apply("正在发送 {0} 的销售数据.....", new Object[] { rq[0] }));

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

								//
								// pb.setText("正在发送 " + rq[0] + " 的 " +
								// String.valueOf(salehead.fphm) + " 号小票.....");
								pb.setText(Language.apply("正在发送 {0} 的 {1} 号小票.....", new Object[] { rq[0], String.valueOf(salehead.fphm) }));

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

									// new MessageBox("未上传小票号:" + salehead.fphm
									// + "\n小票生成日期:" + salehead.rqsj +
									// "\n记下信息请与信息部联系!", null, false);
									new MessageBox(Language.apply("未上传小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!", new Object[] { salehead.fphm + "", salehead.rqsj + "" }), null, false);

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
				// pb.setText("正在发送 " + rq[0] + " 的工作日志.....");
				pb.setText(Language.apply("正在发送 {0} 的工作日志.....", new Object[] { rq[0] }));

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

	public boolean sendAllPayinData(String keytext)
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
				// if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new
				// MessageBox("将要上传" + rq[0] +
				// "非当天的缴款数据\n\n请确认这天的缴款数据真实有效以避免误传测试数据!\n\n你确定要上传这些缴款单吗？",
				// null, true).verify() != GlobalVar.Key1)
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0 && new MessageBox(Language.apply("将要上传{0}非当天的缴款数据\n\n请确认这天的缴款数据真实有效以避免误传测试数据!\n\n你确定要上传这些缴款单吗？", new Object[] { rq[0] }), null, true).verify() != GlobalVar.Key1)
				{
					// 下一天
					rq[0] = dt.skipDate(rq[0], 1);
					continue;
				}

				// pb.setText("正在发送 " + rq[0] + " 的缴款数据.....");
				pb.setText(Language.apply("正在发送 {0} 的缴款数据.....", new Object[] { rq[0] }));

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else
					sql = GlobalInfo.dayDB;
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
							if (rq.length >= 3)
								rs = sql.selectData("select * from PAYINHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and seqno > " + seqno + " and seqno = " + rq[2] + " order by seqno");
							else
								rs = sql.selectData("select * from PAYINHEAD where (netbz <> 'Y' or netbz is null or netbz = '') and seqno > " + seqno + " order by seqno");
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
								// pb.setText("正在发送 " + rq[0] + " 的 " +
								// String.valueOf(payinhead.seqno) +
								// " 号缴款.....");
								pb.setText(Language.apply("正在发送{0}的{1}号缴款.....", new Object[] { rq[0], String.valueOf(payinhead.seqno) }));

								// 读取缴款明细
								rs = sql.selectData("select * from PAYINDETAIL where syjh = '" + payinhead.syjh + "' and seqno = " + String.valueOf(payinhead.seqno) + " order by rowno");
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
								if (!bOK)
									continue;

								// 数据送网成功,标记为已送网
								if (NetService.getDefault().sendPayin(payinhead, payindetail))
								{
									// 和前面selectData换一个对象执行,否则冲突
									sql.setSql("update PAYINHEAD set netbz = 'Y' where syjh = '" + payinhead.syjh + "' and seqno = " + String.valueOf(payinhead.seqno));
									sql.executeSql();
								}
								else
								{
									new MessageBox(Language.apply("重发未送网缴款失败:") + payinhead.seqno + "," + payinhead.rqsj);
									AccessDayDB.getDefault().writeWorkLog(Language.apply("重发未送网缴款失败:") + payinhead.seqno + "," + payinhead.rqsj, StatusType.WORK_SENDERROR);

									allsendok = false;

									errorcount++;
								}

								// 如果批量发送过程中,发生错误两次,则不再显示网络错误
								if (errorcount >= 2)
									NetService.getDefault().setErrorMsgEnable(false);
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

	public boolean sendAllAgainData(char type, String keytext)
	{
		// 将关键字分解为开始日期和结束日期
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		//
		String text = "";
		switch (type)
		{
		case StatusType.TASK_SENDINVOICE:
			text = Language.apply("销售数据");
			break;
		case StatusType.TASK_SENDPAYJK:
			text = Language.apply("缴款数据");
			break;
		case StatusType.TASK_SENDWORKLOG:
			text = Language.apply("工作日志");
			break;
		case StatusType.TASK_SENDBANKLOG:
			text = Language.apply("金卡工程日志");
			break;
		}

		// 记录日志
		AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机被命令重发") + text + ":" + keytext, StatusType.WORK_ORDERSENDDATA);

		// 设置数据重发标志
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				// pb.setText("正在重发 " + rq[0] + " 的" + text + ".....");
				pb.setText(Language.apply("正在重发{0}的{1}.....", new Object[] { rq[0], text }));

				// 打开每日数据库
				Sqldb sql = null;
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				else
					sql = GlobalInfo.dayDB;
				if (sql != null)
				{
					// 设置重发标志
					switch (type)
					{
					case StatusType.TASK_SENDINVOICE:
						if (rq.length >= 3)
							sql.executeSql("update SALEHEAD set netbz = 'C' where fphm = " + rq[2]);
						else
							sql.executeSql("update SALEHEAD set netbz = 'C'");
						break;
					case StatusType.TASK_SENDPAYJK:
						if (rq.length >= 3)
							sql.executeSql("update PAYINHEAD set netbz = 'C' where seqno = " + rq[2]);
						else
							sql.executeSql("update PAYINHEAD set netbz = 'C'");
						break;
					case StatusType.TASK_SENDWORKLOG:
						if (rq.length >= 3)
							sql.executeSql("update WORKLOG set netbz = 'C' where seqno = " + rq[2]);
						else
							sql.executeSql("update WORKLOG set netbz = 'C'");
						break;
					case StatusType.TASK_SENDBANKLOG:
						if (rq.length >= 3)
							sql.executeSql("update BANKLOG set net_bz = 'C' where rowcode = " + rq[2]);
						else
							sql.executeSql("update BANKLOG set net_bz = 'C'");
						break;
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
			return false;
		}
		finally
		{
			pb.close();
		}

		// 写入任务表
		long seqno = AccessLocalDB.getDefault().writeTask(type, keytext);
		TasksDef task = new TasksDef();
		task.type = type;
		task.keytext = keytext;

		// 执行任务
		boolean ok = executeTask(task);

		// 如果任务执行成功，删除任务表
		if (ok)
			AccessLocalDB.getDefault().deleteTask(seqno);

		return true;
	}

	public boolean sendBankLog(String keytext)
	{
		Sqldb sql = null;
		ProgressBox pb = null;

		try
		{
			if (!GlobalInfo.isOnline)
				return false;

			// 将关键字分解为开始日期和结束日期
			String[] rq = keytext.split(",");
			if (rq.length < 2)
				return true;

			boolean allsendok = true;

			pb = new ProgressBox();
			int errorcount = 0;

			ManipulateDateTime dt = new ManipulateDateTime();

			while (dt.compareDate(rq[0], rq[1]) <= 0)
			{
				// pb.setText("正在发送 " + rq[0] + " 的金卡工程日志.....");
				pb.setText(Language.apply("正在发送 {0} 的金卡工程日志.....", new Object[] { rq[0] }));

				// 打开每日数据库
				if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
				{
					sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
				}
				else
				{
					sql = GlobalInfo.dayDB;
				}

				if (sql != null)
				{
					ResultSet rs = null;

					BankLogDef bcd = new BankLogDef();

					int rowcode = 0;

					while (true)
					{
						if (rq.length >= 3)
							rs = sql.selectData("select * from BANKLOG where (net_bz <> 'Y' or net_bz is null or net_bz = '') and rowcode > " + rowcode + " and rowcode = " + rq[2] + " order by rowcode");
						else
							rs = sql.selectData("select * from BANKLOG where (net_bz <> 'Y' or net_bz is null or net_bz = '') and rowcode > " + rowcode + " order by rowcode");

						if (rs != null && rs.next())
						{
							if (!sql.getResultSetToObject(bcd))
							{
								allsendok = false;

								break;
							}

							rowcode = bcd.rowcode;
							sql.resultSetClose();

							// 数据送网成功,标记为已送网
							if (NetService.getDefault().sendBankLog(bcd))
							{
								// 和前面selectData换一个对象执行,否则冲突
								sql.setSql("update BANKLOG set net_bz = 'Y' where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'");
								sql.executeSql();
							}
							else
							{
								allsendok = false;

								errorcount++;
							}

							// 如果批量发送过程中,发生错误两次,则不再显示网络错误
							if (errorcount >= 2)
								NetService.getDefault().setErrorMsgEnable(false);
						}
						else
						{
							break;
						}
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

			return allsendok;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}

			if (pb != null)
			{
				pb.close();
				pb = null;
			}

			// 恢复网络通讯报错显示
			NetService.getDefault().setErrorMsgEnable(true);
		}
	}

	// 发送小票到会员服务器里面
	public boolean sendInvoiceToExtend(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			// pb.setText("正在发送 " + rq[0] + " 的销售数据.....");
			pb.setText(Language.apply("正在发送 {0} 的销售数据.....", new Object[] { rq[0] }));

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
					bOK = true;
					rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");
					if (rs != null && rs.next())
					{
						salegoods.removeAllElements();
						salepay.removeAllElements();

						if (!sql.getResultSetToObject(salehead))
						{
							allsendok = false;
							bOK = false;
							return bOK;
						}
						sql.resultSetClose();

						//
						// pb.setText("正在发送 " + rq[0] + " 的 " +
						// String.valueOf(salehead.fphm) + " 号小票到CRM...");
						pb.setText(Language.apply("正在发送 {0} 的{1} 号小票到CRM...", new Object[] { rq[0], String.valueOf(salehead.fphm) }));

						// 读取商品明细
						rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!sql.getResultSetToObject(sg))
							{
								allsendok = false;
								return bOK;
							}

							salegoods.add(sg);
						}
						sql.resultSetClose();

						// 读取付款明细
						rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!sql.getResultSetToObject(sp))
							{
								allsendok = false;
								bOK = false;
								return bOK;
							}

							salepay.add(sp);
						}
						sql.resultSetClose();

						// 发送小票到CRM服务器
						int ret = NetService.getDefault().sendExtendSaleData(salehead, salegoods, salepay, null);
						if (ret != 0 && ret != 2)
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("重发未送网小票到会员服务器失败:") + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

							if (!NetService.getDefault().isStopService())
								// new MessageBox("未上传到会员服务器小票号:" +
								// salehead.fphm + "\n小票生成日期:" + salehead.rqsj +
								// "\n记下信息请与信息部联系!", null, false);
								new MessageBox(Language.apply("未上传到会员服务器小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!", new Object[] { salehead.fphm + "", salehead.rqsj + "" }), null, false);

							return false;
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

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pb.close();
		}

		return allsendok;
	}

	// 发送小票到JSTORE里面
	public boolean sendInvoiceToJSTORE(String keytext, boolean isSendNotTodayInvoice)
	{
		if (!AccessRemoteDB.getDefault().isConnection(false))
			return false;

		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			// pb.setText("正在发送 " + rq[0] + " 的 " + rq[1] + " 号小票到JSTORE.....");

			// 打开每日数据库
			Sqldb sql = null;
			if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
			{
				// 是否发送非当天的小票
				if (!isSendNotTodayInvoice)
					return true;

				sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
			}
			else
			{
				sql = GlobalInfo.dayDB;
			}
			if (sql != null)
			{
				ResultSet rs = null;
				SaleHeadDef salehead = new SaleHeadDef();
				Vector salegoods = new Vector();
				Vector salepay = new Vector();

				try
				{
					boolean bOK;
					bOK = true;
					rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");
					if (rs != null && rs.next())
					{
						salegoods.removeAllElements();
						salepay.removeAllElements();

						if (!sql.getResultSetToObject(salehead))
						{
							allsendok = false;
							bOK = false;
							return bOK;
						}
						sql.resultSetClose();

						// pb.setText("正在发送 " + rq[0] + " 的 " + rq[1] +
						// " 号小票到JSTORE.....");
						pb.setText(Language.apply("正在发送 {0} 的 {1} 号小票到JSTORE.....", new Object[] { rq[0], rq[1] }));

						// 读取商品明细
						rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!sql.getResultSetToObject(sg))
							{
								allsendok = false;
								return bOK;
							}

							salegoods.add(sg);
						}
						sql.resultSetClose();

						// 读取付款明细
						rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!sql.getResultSetToObject(sp))
							{
								allsendok = false;
								bOK = false;
								return bOK;
							}

							salepay.add(sp);
						}
						sql.resultSetClose();

						int ret = AccessRemoteDB.getDefault().writeSale(salehead, salegoods, salepay);
						if (ret != 0 && ret != 2)
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("重发未送网小票到JSTORE失败:") + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

							return false;
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

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pb.close();
		}

		return allsendok;
	}

	public boolean sendInvoiceToWebService(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			// pb.setText("正在发送 " + rq[0] + " 的WebService销售数据.....");
			pb.setText(Language.apply("正在发送 {0} 的WebService销售数据.....", new Object[] { rq[0] }));

			// 打开每日数据库
			Sqldb sql = null;
			if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
			{
				sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
			}
			else
			{
				sql = GlobalInfo.dayDB;
			}

			if (sql != null)
			{
				ResultSet rs = null;
				SaleHeadDef salehead = new SaleHeadDef();
				Vector salegoods = new Vector();
				Vector salepay = new Vector();

				try
				{
					boolean bOK;
					bOK = true;
					rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");

					if (rs != null && rs.next())
					{
						salegoods.removeAllElements();
						salepay.removeAllElements();

						if (!sql.getResultSetToObject(salehead))
						{
							allsendok = false;
							bOK = false;
							return bOK;
						}

						sql.resultSetClose();

						//
						// pb.setText("正在发送 " + rq[0] + " 的 " +
						// String.valueOf(salehead.fphm) +
						// " 号小票到WebService...");
						pb.setText(Language.apply("正在发送 {0} 的 {1} 号小票到WebService...", new Object[] { rq[0], String.valueOf(salehead.fphm) }));

						// 读取商品明细
						rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!sql.getResultSetToObject(sg))
							{
								allsendok = false;
								return bOK;
							}

							salegoods.add(sg);
						}
						sql.resultSetClose();

						// 读取付款明细
						rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!sql.getResultSetToObject(sp))
							{
								allsendok = false;
								bOK = false;
								return bOK;
							}

							salepay.add(sp);
						}
						sql.resultSetClose();

						// 发送小票到WebService
						int ret = DataService.getDefault().sendSaleWebService(salehead, salegoods, salepay);

						if (ret != 0)
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("重发未送网小票到WebService失败:") + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

							// new MessageBox("未上传到WebService小票号:" +
							// salehead.fphm + "\n小票生成日期:" + salehead.rqsj +
							// "\n记下信息请与信息部联系!", null, false);
							new MessageBox(Language.apply("未上传到WebService小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!", new Object[] { salehead.fphm + "", salehead.rqsj }), null, false);

							return false;
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

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pb.close();
		}

		return allsendok;
	}

	public boolean sendHykJf(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		//
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			// pb.setText("正在发送 " + rq[0] + " 的小票积分销售数据.....");
			pb.setText(Language.apply("正在发送 {0} 的小票积分销售数据.....", new Object[] { rq[0] }));

			// 打开每日数据库
			Sqldb sql = null;
			if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
			{
				sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
			}
			else
			{
				sql = GlobalInfo.dayDB;
			}

			if (sql != null)
			{
				ResultSet rs = null;
				SaleHeadDef salehead = new SaleHeadDef();

				rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");

				if (rs != null && rs.next())
				{
					if (!sql.getResultSetToObject(salehead)) { return false; }

					sql.resultSetClose();

					// pb.setText("正在发送 " + rq[0] + " 的 " +
					// String.valueOf(salehead.fphm) + " 号小票时时积分...");
					pb.setText(Language.apply("正在发送 {0} 的 {1} 号小票时时积分...", new Object[] { rq[0], String.valueOf(salehead.fphm) }));

					// 发送小票时时积分
					boolean ret = DataService.getDefault().sendHykJf(salehead);

					if (!ret)
					{
						AccessDayDB.getDefault().writeWorkLog(Language.apply("重发小票积分到会员服务器失败:") + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

						if (!NetService.getDefault().isStopService())
							// new MessageBox("未上传实时积分小票号:" + salehead.fphm +
							// "\n小票生成日期:" + salehead.rqsj + "\n记下信息请与信息部联系!",
							// null, false);
							new MessageBox(Language.apply("未上传实时积分小票号:{0}\n小票生成日期:{1}\n记下信息请与信息部联系!", new Object[] { salehead.fphm + "", salehead.rqsj }), null, false);

						return false;
					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			pb.close();
		}
	}
}
