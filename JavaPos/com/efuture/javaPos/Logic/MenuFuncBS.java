package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.Calculator;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.defineKey.KeyConfigForm;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.MzkRechargeBillMode;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.ArkGroupSaleStatForm;
import com.efuture.javaPos.UI.Design.BankLogQueryForm;
import com.efuture.javaPos.UI.Design.BusinessPersonnelStatForm;
import com.efuture.javaPos.UI.Design.DisplaySaleTicketForm;
import com.efuture.javaPos.UI.Design.GoodsInfoQueryForm;
import com.efuture.javaPos.UI.Design.ImportSmallTicketBackupForm;
import com.efuture.javaPos.UI.Design.JfQueryInfoForm;
import com.efuture.javaPos.UI.Design.LoginForm;
import com.efuture.javaPos.UI.Design.MessageQueryForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.MzkRechargeForm;
import com.efuture.javaPos.UI.Design.MzkSeqNoResetForm;
import com.efuture.javaPos.UI.Design.PassModifyForm;
import com.efuture.javaPos.UI.Design.PersonnelGoForm;
import com.efuture.javaPos.UI.Design.PosFormFuncTab;
import com.efuture.javaPos.UI.Design.PreMoneyForm;
import com.efuture.javaPos.UI.Design.QueryWorkLogForm;
import com.efuture.javaPos.UI.Design.RemoveDayForm;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.efuture.javaPos.UI.Design.SaleFormTouch;
import com.efuture.javaPos.UI.Design.SaleTicketListForm;
import com.efuture.javaPos.UI.Design.SetSystemTimeForm;
import com.efuture.javaPos.UI.Design.ShortcutKeyForm;
import com.efuture.javaPos.UI.Design.SysParaForm;
import com.efuture.javaPos.UI.Design.SyySaleStatForm;
import com.efuture.javaPos.UI.Design.WithdrawForm;

public class MenuFuncBS
{
	protected MessageBox me = null;
	protected DisConnNetWorkBS dcnwb = null;
	protected ConnNetWorkBS cnwbs = null;
	protected MzkInfoQueryBS mzkbs = null;
	protected MzkStatisticsBS mzksbs = null;
	protected HykInfoQueryBS hykbs = null;
	protected CleanUpLocalDataBaseBS cudbbs = null;
	protected DeleteCzDataBS dcdbs = null;
	protected FjkInfoQueryBS fjkbs = null;
	public int printButtonHandle = 0;

	public MenuFuncBS()
	{
		dcnwb = CustomLocalize.getDefault().createDisConnNetWorkBS();
		cnwbs = CustomLocalize.getDefault().createConnNetWorkBS();
		mzkbs = CustomLocalize.getDefault().createMzkInfoQueryBS();
		mzksbs = CustomLocalize.getDefault().createMzkStatisticsBS();
		hykbs = CustomLocalize.getDefault().createHykInfoQueryBS();
		cudbbs = CustomLocalize.getDefault().createCleanUpLocalDataBaseBS();
		dcdbs = CustomLocalize.getDefault().createDeleteCzDataBS();
		fjkbs = CustomLocalize.getDefault().createFjkInfoQueryBS();
	}

	public void printButtonEvent()
	{
	}

	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		return false;
	}

	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			// 记录菜单功能日志
			if (mfd != null && mfd.workflag == 'Y')
			{
				AccessDayDB.getDefault().writeWorkLog("进入 \"[" + mfd.code + "]" + mfd.name + "\" 菜单", mfd.code);
			}

			// 关闭菜单窗口
			if (execExtendFuncMenu(mfd, mffe)) { return; }

			switch (Integer.parseInt(mfd.code))
			{
				case StatusType.MN_LSSALE:
					openLsSale(mfd, mffe);

					break;

				case StatusType.MN_LXSALE:
					openLxSale(mfd, mffe);

					break;

				case StatusType.MN_STAMPCHANGE:
					openStampSale(mfd, mffe);
					break;

				case StatusType.MN_SALEHC:
					openSaleHc(mfd, mffe);

					break;

				case StatusType.MN_BACKSALE:
					openBackSale(mfd, mffe);

					break;

				case StatusType.MN_PFSALE:
					openPfSale(mfd, mffe);

					break;

				case StatusType.MN_MQSALE:
					openMQSale(mfd, mffe);

					break;

				case StatusType.MN_DJSALE:
					openDjSale(mfd, mffe);

					break;

				case StatusType.MN_CHECK:
					openCheck(mfd, mffe);

					break;

				case StatusType.MN_YSSALE:
					openYsSale(mfd, mffe);

					break;
				case StatusType.MN_YSSALE1:
					openYsSale1(mfd, mffe);

					break;

				case StatusType.MN_SHSALE:
					openShSale(mfd, mffe);
					break;

				case StatusType.MN_JDFHSALE:
					openJdFhSale(mfd, mffe);

					break;

				case StatusType.MN_JFSALE:
					openJfSale(mfd, mffe);

					break;

				case StatusType.MN_JSSALE:
					openJsSale(mfd, mffe);

					break;

				case StatusType.MN_HDQ:
					// openActiveCoupon(mfd, mffe);
					break;

				case StatusType.MN_QTXSTJ:
					openQtxStj(mfd, mffe);

					break;

				case StatusType.MN_YYYTJ:
					openYyyTj(mfd, mffe);

					break;

				case StatusType.MN_GZXSTJ:
					openGzXsTj(mfd, mffe);

					break;

				case StatusType.MN_XSCX:
					openXsCx(mfd, mffe);

					break;

				case StatusType.MN_XSLIST:
					openXsList(mfd, mffe);

					break;

				case StatusType.MN_SCSM:
					StoredCardStatistics(mfd, mffe);

					break;

				case StatusType.MN_MZKXX:
					openMzkXx(mfd, mffe);

					break;

				case StatusType.MN_FJKXX:
					openFjkXx(mfd, mffe);
					// openJfXx(mfd, mffe);

					break;

				case StatusType.MN_GOODSFIND:
					openGoodsFind(mfd, mffe);

					break;

				case StatusType.MN_CUSTFUNC:
					openCustFunc(mfd, mffe);

					break;

				case StatusType.MN_NEWS:
					openNews(mfd, mffe);

					break;

				case StatusType.MN_WORK:
					openWork(mfd, mffe);

					break;

				case StatusType.MN_HYK:
					openHYK(mfd, mffe);

					break;

				case StatusType.MN_JSQ:
					openJSQ(mfd, mffe);

					break;

				case StatusType.MN_KJJDEF:
					openKjjDef(mfd, mffe);

					break;

				case StatusType.MN_LWCZ:
					openLwCz(mfd, mffe);

					break;

				case StatusType.MN_TWCZ:
					openTwCz(mfd, mffe);

					break;

				case StatusType.MN_XPHSZ:
					openXphSz(mfd, mffe);

					break;

				case StatusType.MN_CXSZXTSJ:
					openCxSzXtSj(mfd, mffe);

					break;

				case StatusType.MN_SCXSSJ:
					openScXsSj(mfd, mffe);

					break;

				case StatusType.MN_ZLBDSJK:
					openZlBdSjk(mfd, mffe);

					break;

				case StatusType.MN_JPDY:
					openKeyBoardConfig(mfd, mffe);

					break;

				case StatusType.MN_MZKSEQNORESET:
					openMzkSeqNoResetForm(mfd, mffe);

					break;

				case StatusType.MN_MZKDELETECZ:
					openDeleteCzData(mfd, mffe);

					break;

				case StatusType.MN_SYYDL:
					openSyyDl(mfd, mffe);

					break;

				case StatusType.MN_SYYLK:
					openSyyLk(mfd, mffe);

					break;

				case StatusType.MN_MMXG:
					openMmXg(mfd, mffe);

					break;

				case StatusType.MN_JKDSL:
					openJkdSl(mfd, mffe);

					break;

				case StatusType.MN_EXITST:
					openExitSt(mfd, mffe);

					break;
				case StatusType.MN_FASTRERUN:
					openFastRun(mfd, mffe);

					break;

				case StatusType.MN_HELP:
					openHelp(mfd, mffe);

					break;

				case StatusType.MN_GY:
					openGy(mfd, mffe);

					break;

				// 金卡工程交易
				case StatusType.MN_XYKLOG:
					openBankLog(mfd, mffe);

					break;

				case StatusType.MN_XYKXF:
				case StatusType.MN_XYKCX:
				case StatusType.MN_XYKTH:
				case StatusType.MN_XYKQD:
				case StatusType.MN_XYKJZ:
				case StatusType.MN_XYKYE:
				case StatusType.MN_XYKCD:
				case StatusType.MN_XYKQT1:
				case StatusType.MN_XYKQT2:
				case StatusType.MN_XYKQT3:
				case StatusType.MN_XYKQT4:
				case StatusType.MN_XYKQT5:
				case StatusType.MN_XYKQT6:
				case StatusType.MN_XYKQT7:
				case StatusType.MN_XYKQT8:
				case StatusType.MN_XYKQT9:
					openBankFunc(mfd, mffe, Integer.parseInt(mfd.code));

					break;

				case StatusType.MN_POSIPLIST:
					openPosIPList(mfd, mffe);

					break;

				case StatusType.MN_RELOADBASE:
					reloadbase(mfd, mffe);
					break;

				case StatusType.MN_IMPORTSMALLTICKET:
					openImportSmallTicket(mfd, mffe);
					break;

				case StatusType.MN_MODIFYINVNO:
					openModifyInvno(mfd, mffe);
					break;

				case StatusType.MN_JDXXFK:
					openJdxxFk(mfd, mffe);
					break;

				case StatusType.MN_JDXXTH:
					openJdxxfBack(mfd, mffe);
					break;

				case StatusType.MN_GROUPBUYSALE:
					openGroupbuySale(mfd, mffe);
					break;

				case StatusType.MN_CARDSALE:
					openCardSale(mfd, mffe);
					break;

				case StatusType.MN_MZKRECHARGE:
					openMzkRecharge(mfd, mffe);

				case StatusType.MN_MZKCHGPASS:
					openMzkChgPass(mfd, mffe);

					break;
				case StatusType.MN_JFXX:
					openJfXx(mfd, mffe);
					break;
				case StatusType.MN_MJFSALE:
					openMJfSale(mfd, mffe);
					break;
				case StatusType.MN_MODIFYJKD:
					
					
					getCurrWithdrawInfo();

					
					break;
				default:
					menuFuncMessageBox(mfd, mffe);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("菜单功能执行异常\n\n") + ex.getMessage());
		}
	}
	
	public boolean getCurrWithdrawInfo()
    {
		String[] title = {"单号","缴款时间","收银员","缴款金额"};
		int[] width = {150,150,100,100};
		
		String date = new ManipulateDateTime().getDateByEmpty();
		StringBuffer buff =new StringBuffer();
		buff.append(date);
		if (new TextBox().open(Language.apply("请输入缴款单日期"), Language.apply("日期"), Language.apply("请输入缴款单日期（YYYYMMDD）"), buff, 0, 0, false, TextBox.IntegerInput))
		{
			date = buff.toString();
			
		}
		else
		{
			return false;
		}
		
		Vector con = new Vector();
		
		
        ResultSet rs = null;
        String sqlstr = null;
        Sqldb sql = null;
        try
        {
        	
        	
        	if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date.trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				return false;
			}
        	
        	if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
        	
        	if (GlobalInfo.posLogin.operrange == 'Y')
            {
        		sqlstr = "select * from PAYINHEAD";
            }
        	else
        	{
        		sqlstr = "select * from PAYINHEAD where syyh ='" + GlobalInfo.posLogin.gh +"'";
        	}
        	
            if ((rs = sql.selectData(sqlstr)) != null)
            {
                while (rs.next())
                {
                	PayinHeadDef payinhead = new PayinHeadDef();
                	if (sql.getResultSetToObject(payinhead))
                	{
                		
	                    String[] tempstr = payinhead.rqsj.split(" ");
	
	                    if (payinhead.netbz == 'Y')
	                    {
	                        String[] withdrawinfo = 
	                                                {
	                                                    "↑" +
	                                                    payinhead.seqno,
	                                                    tempstr[1],
	                                                    payinhead.syyh,
	                                                    ManipulatePrecision.doubleToString(payinhead.je)
	                                                };
	                        con.add(withdrawinfo);
	                    }
	                    else
	                    {
	                        String[] withdrawinfo = 
	                                                {
	                                                    "  " +
	                                                    payinhead.seqno,
	                                                    tempstr[1],
	                                                    payinhead.syyh,
	                                                    ManipulatePrecision.doubleToString(payinhead.je)
	                                                };
	                        con.add(withdrawinfo);
	                    }
                	}
                }
            }
            
            if (con.size() <= 0)
            {
            	return false;
            }
            
            int choice = new MutiSelectForm().open(Language.apply(date+" 请选择要修改的缴款单号"), title, width, con,false,589,319,560,192,false,false,-1,false,0, 0, null, null, null, StatusType.MN_MODIFYJKD);
			
            
            try
            {
            	
			if (choice >= 0)
			{
				String[] row = (String[]) con.get(choice);
				StringBuffer buff1 =new StringBuffer();
				String oldrow = row[0].replaceAll("↑", "").trim();
				buff1.append(row[0].replaceAll("↑", ""));
				String row1 = "";
				if (new TextBox().open(Language.apply("请输入更改后的缴款单号"), Language.apply("单号"), Language.apply("单号"), buff1, 0, 0, false, TextBox.IntegerInput))
				{
					row1 = buff1.toString();
				}
				else
				{
					return false;
				}
				String line1 = "update PAYINHEAD set netbz = 'N' , seqno = '" +row1+"' where seqno = " + oldrow + " ";
				String line2 = "update PAYINDETAIL set seqno = '" +row1+"' where seqno = '" + oldrow + "' ";
			
            
				System.out.println(line1+"\n"+line2);
           
	            if (sql.executeSql(line1))
	            {
	            }
	            else
	            {
	                return false;
	            }
	            
	            if (sql.executeSql(line2))
	            {
	            	new MessageBox("修改成功");
	                return true;
	            }
	            else
	            {
	                return false;
	            }
			}
            }
            catch (Exception er)
            {
            	er.printStackTrace();

                return false;
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
        	if(sql != null)
        	{
        		sql.resultSetClose();
        	}
        }
    }


	private void reloadbase(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		cudbbs.reloadBaseDB();
	}

	public static void openPosIPList(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			if (!PathFile.fileExist(GlobalVar.ConfigPath + "/IPList.ini"))
			{
				// 通过菜单调用进行提示,否则不提示
				if (mfd != null || mffe != null)
				{
					new MessageBox(Language.apply("没有找到服务器IP列表清单文件 IPList.ini"));
				}
			}
			else
			{
				Vector v = CommonMethod.readFileByVector(GlobalVar.ConfigPath + "/IPList.ini");
				if (v.size() > 0)
				{
					do
					{
						String[] title = { Language.apply("服务器描述"), Language.apply("服务器地址") };
						int[] width = { 200, 400 };
						int choice = new MutiSelectForm().open(Language.apply("请选择你要重连的POSSERVER服务器(") + GlobalInfo.ModuleType + ")", title, width, v, false, 660, 319, false);
						if (choice < 0)
							break;

						// 创建HTTP连接
						String[] newposid = null;
						String addr = ((String[]) v.elementAt(choice))[1];
						if (addr.indexOf(",") > 0)
						{
							newposid = addr.split(",");
							addr = newposid[0];
						}
						int port = ConfigClass.ServerPort;
						String path = ConfigClass.ServerPath;
						if (addr == null || addr.trim().length() <= 0)
						{
							new MessageBox(Language.apply("POSSERVER服务器地址不能为空"));
							continue;
						}
						else
							addr = addr.trim();
						if (addr.toLowerCase().indexOf("http://") >= 0)
						{
							String url = addr;
							int n = url.toLowerCase().indexOf("http://") + 7;
							int m = url.indexOf(":", n);
							if (m >= 0)
							{
								addr = url.substring(n, m);
								n = m + 1;
								m = url.indexOf("/", n);
								if (m >= 0)
									port = Convert.toInt(url.substring(n, m));
							}
							else
							{
								m = url.indexOf("/", n);
								if (m >= 0)
									addr = url.substring(n, m);
							}
							if (m >= 0)
								path = url.substring(m);
						}
						addr = addr.trim();
						path = path.trim();
						Http p = new Http(addr, port, path);
						p.init();
						p.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
						p.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时

						// 获取服务器时间
						TimeDate time = new TimeDate();
						if (NetService.getDefault().getServerTime(p, time))
						{
							// 记录日志
							AccessDayDB.getDefault().writeWorkLog("收银机改变POSSERVER服务地址:" + addr);
							// AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机改变POSSERVER服务地址:")
							// + addr);

							if (GlobalInfo.localHttp != null)
								GlobalInfo.localHttp.disconncet();
							GlobalInfo.localHttp = p;

							// 设置本机时间
							new ManipulateDateTime().setDateTime(time);
							GlobalInfo.isOnline = true;

							// timHttp设null,等待后台进程重联
							ConfigClass.ServerIP = addr;
							ConfigClass.ServerPort = port;
							ConfigClass.ServerPath = path;
							if (GlobalInfo.timeHttp != null)
								GlobalInfo.timeHttp.disconncet();
							GlobalInfo.timeHttp = null;

							// 动态改变款机号,便于开发调试,连接不同的服务器
							if (newposid != null && ConfigClass.DebugMode)
							{
								if (newposid.length > 1 && newposid[1] != null && newposid[1].trim().length() > 0)
									ConfigClass.CashRegisterCode = newposid[1].trim();
								if (newposid.length > 2 && newposid[2] != null && newposid[2].trim().length() > 0)
									ConfigClass.CDKey = newposid[2].trim();
								if (newposid.length > 3)
									ConfigClass.Market = newposid[3].trim();
							}

							new MessageBox(Language.apply("POSSERVER服务器连接成功,改为由地址\n\n") + GlobalInfo.localHttp.getSvrURL() + "\n\n的POSSERVER服务器进行访问服务");
							break;
						}
						else
						{
							new MessageBox(Language.apply("POSSERVER服务器连接失败,请选择其他服务器"));
						}
					} while (true);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
		}
	}

	public void menuFuncMessageBox(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new MessageBox("[" + mfd.code + Language.apply("]号功能暂无开发!"), null, false);
	}

	// 打开导入小票备份
	private void openImportSmallTicket(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new ImportSmallTicketBackupForm();
	}

	public void openMQSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.PURCHANSE_COUPON);
	}

	// 打开销售开单
	private void openLsSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.RETAIL_SALE);
	}

	// 打开收银练习
	private void openLxSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.EXERCISE_SALE);
	}

	// 打开收银练习
	private void openStampSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.STAMP_SALE);
	}

	// 打开收银红冲
	private void openSaleHc(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new DisplaySaleTicketForm(StatusType.MN_SALEHC);
	}

	// 打开后台退货
	private void openBackSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new DisplaySaleTicketForm(StatusType.MN_BACKSALE);
	}

	// 打开批发开票
	private void openPfSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (GlobalInfo.syjDef.ispf != 'Y')
		{
			new MessageBox(Language.apply("此收银机不是批发台\n只有在批发台才能进行【批发开票】"));

			return;
		}

		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.BATCH_SALE);
	}

	// 打开预收定金
	public void openDjSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.EARNEST_SALE);
	}

	// 打开商品盘点
	private void openCheck(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.CHECK_INPUT);
	}

	// 打开预售销售
	private void openYsSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.PREPARE_SALE);
	}

	// 打开预售销售
	private void openYsSale1(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.PREPARE_SALE1);
	}

	// 打开预售提货
	private void openShSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.PREPARE_TAKE);

	}

	// 打开家电发货
	private void openJdFhSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{

	}

	// 打开家电发货
	private void openJsSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.JS_FK);
	}

	// 打开家电发货
	private void openJfSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.JF_FK);
	}

	// 打开客户化功能菜单
	private void openCustFunc(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		File file = new File(GlobalVar.ConfigPath + "//CustFunc.ini");

		if (file.exists())
		{
			BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "//CustFunc.ini");
			String line = null;
			Vector contents = new Vector();

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.charAt(0) == ';')
					{
						continue;
					}

					if (line.split("=").length != 2)
					{
						continue;
					}

					contents.add(line.split("="));
				}

				if (contents.size() <= 0)
				{
					new MessageBox(Language.apply("CustFunc.ini 此文件内的格式错误"));
				}

				String[] title = { Language.apply("描述"), Language.apply("路径") };
				int[] width = { 460, 400 };

				int choice = new MutiSelectForm().open(Language.apply("请选择你要执行的附加程序"), title, width, contents);

				String name = ((String[]) contents.elementAt(choice))[1].trim();
				String desc = ((String[]) contents.elementAt(choice))[0].trim();

				boolean lock = false;
				if (desc.indexOf(Language.apply("描述")) >= 0)
				{
					if (SecMonitor.secMonitor != null)
					{
						SecMonitor.secMonitor.sendCmd("VIDEOCTRL|STOP");
						lock = true;
					}
				}

				try
				{
					CommonMethod.waitForExec(name);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				if (lock)
				{
					SecMonitor.secMonitor.sendCmd("VIDEOPLAY|");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			new MessageBox(Language.apply("没有找到CustFunc.ini配置文件"));
		}
	}

	// 打开收银员销售统计
	public void openQtxStj(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("授权查看报表"));

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox(Language.apply("该授权人员无查看报表权限"));

				return;
			}
		}

		new SyySaleStatForm();
	}

	// 打开营业员销售统计
	public void openYyyTj(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("授权查看报表"));

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox(Language.apply("该授权人员无查看报表权限"));

				return;
			}
		}

		new BusinessPersonnelStatForm();
	}

	protected void openMzkChgPass(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		// openMzkXx(mfd, mffe);
	}

	// 打开面值卡充值框
	protected void openMzkRecharge(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
		if (new File(ConfigClass.LocalDBPath + "Invoice\\" + date + "\\list.txt").exists())
		{
			String[] title = { Language.apply("功能") };
			int[] width = { 500 };
			Vector v = new Vector();
			v.add(new String[] { Language.apply("充值交易") });
			v.add(new String[] { Language.apply("重打印充值凭证") });
			int choice = new MutiSelectForm().open(Language.apply("请选择功能项"), title, width, v);
			if (choice <= 0)
			{
				new MzkRechargeForm().open();
			}
			else
			{

				String[] title1 = { Language.apply("凭证明细") };
				int[] width1 = { 500 };
				Vector v1 = CommonMethod.readFileByVector(ConfigClass.LocalDBPath + "Invoice\\" + date + "\\list.txt", "GBK");

				choice = new MutiSelectForm().open(Language.apply("请选择凭证"), title1, width1, v1);
				if (choice >= 0)
				{
					String line = ((String[]) v1.elementAt(choice))[0];
					String time = line.trim().substring(line.trim().lastIndexOf("  "));
					String line2 = ConfigClass.LocalDBPath + "Invoice//" + date + "//" + time.replaceAll(":", "").trim() + ".txt";
					if (new File(line2).exists())
					{
						BufferedReader br = CommonMethod.readFileGBK(line2);

						String line1 = null;
						try
						{
							Vector v2 = new Vector();
							while ((line1 = br.readLine()) != null)
							{
								v2.add(line1);
							}

							MzkRechargeBillMode.getDefault().Reprint(v2);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
							try
							{
								if (br != null)
								{
									br.close();
								}
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else
					{
						new MessageBox(Language.apply("对不起，没有找到重打印文件，此单无法重打印"));
					}
				}
			}
		}
		else
		{
			new MzkRechargeForm().open();
		}
	}

	// 打开柜组销售统计
	public void openGzXsTj(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("授权查看报表"));

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox(Language.apply("该授权人员无查看报表权限"));

				return;
			}
		}

		new ArkGroupSaleStatForm();
	}

	// 打开销售小票查询
	private void openXsCx(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		// 传入false代表查询小票
		new DisplaySaleTicketForm(StatusType.MN_XSCX);
	}

	// 打开当日小票列表
	public void openXsList(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new SaleTicketListForm();
	}

	// 面值卡收款统计
	private void StoredCardStatistics(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if ((GlobalInfo.posLogin.priv.length() > 4) && (GlobalInfo.posLogin.priv.charAt(4) != 'Y'))
		{
			OperUserDef staff = DataService.getDefault().personGrant(Language.apply("授权查看报表"));

			if (staff == null) { return; }

			if (staff.priv.charAt(4) != 'Y')
			{
				new MessageBox("该授权人员无查看报表权限");

				return;
			}
		}

		mzksbs.PrintMZKStatistics();
	}

	// 打开面值卡余额查询
	protected void openMzkXx(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		mzkbs.QueryMzkInfo();
	}

	// 打开返券卡查询
	protected void openFjkXx(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		fjkbs.QueryFjkInfo();
	}

	// 打开积分查询
	protected void openJfXx(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		JfQueryInfoForm window = new JfQueryInfoForm();
		window.open();
	}

	// 打开商品查询
	private void openGoodsFind(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new GoodsInfoQueryForm(null, null);
	}

	// 打开查询网上通知
	private void openNews(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new MessageQueryForm();
	}

	// 打开查询工作日志
	private void openWork(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new QueryWorkLogForm();
	}

	// 打开查询会员卡信息
	private void openHYK(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		hykbs.QueryHykInfo();
	}

	// 打开计算器
	private void openJSQ(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new Calculator();
	}

	// 打开快捷键定义
	private void openKjjDef(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new ShortcutKeyForm();
	}

	// 打开联网操作
	public void openLwCz(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		me = new MessageBox(Language.apply("你确定要进行联网操作吗?"), null, true);

		if (me.verify() == GlobalVar.Key1)
		{
			//
			if (cnwbs.setConnNet() && (GlobalInfo.isOnline == true))
			{
				new MessageBox(Language.apply("连接网络成功,收银机进入联网运行!"), null, false);
			}
			else
			{
				new MessageBox(Language.apply("连接网络失败!"), null, false);
			}
		}
	}

	// 打开脱网操作
	private void openTwCz(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		me = new MessageBox(Language.apply("你确定要进行脱网操作吗?"), null, true);

		if (me.verify() == GlobalVar.Key1)
		{
			if (dcnwb.setDisConnNet() && (GlobalInfo.isOnline == false))
			{
				new MessageBox(Language.apply("断开网络成功,收银机进入脱网运行!"), null, false);
			}
			else
			{
				new MessageBox(Language.apply("断开网络失败!"), null, false);
			}
		}
	}

	// 打开小票号设置
	private void openXphSz(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new SetSaleTicketIdBS();
	}

	// 打开重新设置系统时间
	private void openCxSzXtSj(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new SetSystemTimeForm(false);
	}

	// 打开重新删除销售数据
	private void openScXsSj(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new RemoveDayForm();
	}

	// 打开整理本地数据库
	private void openZlBdSjk(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		cudbbs.executeCleanUp();
	}

	// 打开收银员登录
	public void openSyyDl(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (!GlobalInfo.saleform.sale.saleBS.isExistHangBill())
			return;

		// 提示确认
		if (mfd != null && new MessageBox(Language.apply("你确定要重新登录吗?"), null, true).verify() != GlobalVar.Key1)
			return;

		// 关闭菜单功能窗口
		if (mffe != null)
			mffe.dispose();

		// 关闭收银主窗口
		if ((GlobalInfo.saleform != null) && GlobalInfo.saleform.closeForm())
		{
			// 记录登出日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员注销登录"), StatusType.WORK_RELOGIN);

			// 先发送监控状态为关机，再发送开机未登录状态，使监控状态不被识别为非法关机
			GlobalInfo.syjStatus.status = StatusType.STATUS_SHUTDOWN;
			DataService.getDefault().sendSyjStatus();

			// 显示登录背景
			GlobalInfo.background.setVersionEanble(true);

			// 登录
			if (new LoginForm().open(null))
			{
				if ("Y".equals(GlobalInfo.sysPara.isinputpremoney))
				{
					// 输入备用金
					new PreMoneyForm().open();
				}

				// 显示销售界面
				GlobalInfo.background.setVersionEanble(false);

				if (ConfigClass.TouchSaleForm.equalsIgnoreCase("Touch"))
				{
					GlobalInfo.saleform = new SaleFormTouch(GlobalInfo.mainshell, SWT.NONE); // tabForm.getSaleForm();
				}
				else if (ConfigClass.TouchSaleForm.equalsIgnoreCase("Nxmx"))
				{
					PosFormFuncTab tabForm = new PosFormFuncTab(GlobalInfo.mainshell, SWT.NONE);
					GlobalInfo.saleform = tabForm.getSaleForm();
				}
				else
				{
					GlobalInfo.saleform = new SaleForm(GlobalInfo.mainshell, SWT.NONE);
				}
			}
			else
			{
				// 退出系统
				GlobalInfo.background.quitSysInfo();
			}
		}
	}

	// 打开收银员离开
	private void openSyyLk(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		me = new MessageBox(Language.apply("你确定要离开并锁定收银机吗?"), null, true);

		if (me.verify() == GlobalVar.Key1)
		{
			new PersonnelGoForm();
		}
	}

	/*
	 * private void openActiveCoupon(MenuFuncDef mfd, MenuFuncEvent mffe) { try
	 * { Nxmx_CouponActiveForm couponActiveForm = new Nxmx_CouponActiveForm();
	 * couponActiveForm.open("%"); } catch(Exception ex) { ex.printStackTrace();
	 * } }
	 */

	// 打开密码修改
	private void openMmXg(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (GlobalInfo.isOnline)
		{
			new PassModifyForm();
		}
		else
		{
			new MessageBox(Language.apply("密码必须在联网状态下修改!"), null, false);
		}
	}

	// 打开缴款单输入
	private void openJkdSl(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new WithdrawForm();
	}

	// 打开退出系统
	private void openExitSt(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			if (!GlobalInfo.saleform.sale.saleBS.isExistHangBill())
				return;

			me = new MessageBox(Language.apply("你确定要退出收银系统吗?"), null, true);

			if (me.verify() == GlobalVar.Key1)
			{
				// 关闭菜单功能窗口
				if (mffe != null)
					mffe.dispose();

				// 关闭收银主窗口
				if ((GlobalInfo.saleform != null) && GlobalInfo.saleform.closeForm())
				{
					// 记录登出日志
					AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员登出"), StatusType.WORK_RELOGIN);

					// 退出系统
					GlobalInfo.background.quitSysInfo();
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	private void openFastRun(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			if (!GlobalInfo.saleform.sale.saleBS.isExistHangBill())
				return;

			me = new MessageBox(Language.apply("你确定要退出并重进收银系统吗?"), null, true);

			if (me.verify() == GlobalVar.Key1)
			{
				// 关闭菜单功能窗口
				if (mffe != null)
					mffe.dispose();

				// 关闭收银主窗口
				if ((GlobalInfo.saleform != null) && GlobalInfo.saleform.closeForm())
				{
					// 记录登出日志
					AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员登出"), StatusType.WORK_RELOGIN);

					// 退出系统
					LoadSysInfo.getDefault().setFastRunning(true);
					GlobalInfo.background.quitSysInfo();
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	private void openHelp(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		// 调试模式或维护员,允许通过菜单代码执行菜单功能
		if (ConfigClass.DebugMode || GlobalInfo.posLogin.type == '3')
		{
			StringBuffer buffer = new StringBuffer();
			if (new TextBox().open(Language.apply("请输入菜单ID编号"), Language.apply("编号"), Language.apply("可通过菜单功能的ID编号,直接执行相应菜单功能"), buffer, 0, 0, false, TextBox.IntegerInput))
			{
				MenuFuncDef newmfd = new MenuFuncDef();
				newmfd.code = buffer.toString();
				newmfd.name = Language.apply("菜单编号功能");
				newmfd.workflag = 'Y';
				execFuncMenu(newmfd, mffe);
			}
		}
		else
		{
			this.menuFuncMessageBox(mfd, mffe);
		}
	}

	// 打开关于
	public void openGy(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		StringBuffer msg = new StringBuffer();

		// 得到设备模块版本
		String devVersion = "";

		try
		{
			devVersion = new device.DeviceInfo().getAssemblyVersion();
		}
		catch (Exception ex)
		{
			devVersion = "0.0.0 bulid 0000.00.00";
		}

		// 得到付款模块版本
		String payVersion = "";

		try
		{
			payVersion = new bankpay.BankInfo().getAssemblyVersion();
		}
		catch (Exception ex)
		{
			payVersion = "0.0.0 bulid 0000.00.00";
		}
		//
		msg.append(AssemblyInfo.AssemblyCompany + "\n\n");
		msg.append(GlobalInfo.ModuleType + " - " + AssemblyInfo.AssemblyProduct + "\n\n");

		msg.append(Language.apply("程序模块   ") + AssemblyInfo.AssemblyVersion + "\n");

		msg.append(Language.apply("客户模块   ") + CustomLocalize.getDefault().getAssemblyVersion() + "\n");

		msg.append(Language.apply("付款模块   ") + payVersion + "\n");
		
		msg.append(Language.apply("设备模块   ") + devVersion + "\n\n");

		msg.append(Language.apply("本系统使用权授予\n\n") + GlobalInfo.sysPara.mktcode + " - " + GlobalInfo.sysPara.mktname + "\n");

		String key = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
		String strdate = ManipulatePrecision.DecodeString(GlobalInfo.sysPara.validservicedate, key);
		String[] s = strdate.split(",");
		if (s.length >= 2)
			msg.append(Language.apply("有效期: ") + s[0] + "\n\n");

		msg.append(Language.apply("本机地址   ") + GlobalInfo.ipAddr);

		//
		GlobalInfo.statusBar.setHelpMessage(Language.apply("‘确认键’查看系统文件更新时间，‘付款键’维护本地数据库"));

		// 查看文件信息
		int verify = new MessageBox(msg.toString()).verify();

		if (verify == GlobalVar.Validation)
		{
			FileInfoView();
		}

		// 查看表信息
		if (verify == GlobalVar.Pay)
		{
			TableViewBS tibs = new TableViewBS();
			tibs.tableInfoView();
		}

		// 查看参数
		if (verify == GlobalVar.MainList)
		{
			new SysParaForm().open();
		}
	}

	public void FileInfoView()
	{
		String[] title = { Language.apply("类型"), Language.apply("文件名"), Language.apply("修改时间") };
		int[] width = { 120, 170, 250 };
		Vector contents = new Vector();
		contents.add(new String[] { Language.apply("程序模块"), "javaPos.jar", getJavaPosModifiedTime() });
		contents.add(new String[] { Language.apply("客户模块"), "localize.jar", getLocalizeModifiedTime() });
		contents.add(new String[] { Language.apply("付款模块"), "bankpay.jar", getBankpayModifiedTime() });
		contents.add(new String[] { Language.apply("设备模块"), "device.jar", getDeviceModifiedTime() });

		new MutiSelectForm().open(Language.apply("模块信息"), title, width, contents, false);
	}

	// 获得javapos.jar最近修改时间
	public String getJavaPosModifiedTime()
	{
		return PathFile.fileLastmodified("./javaPos.jar");
	}

	// 获得localize.jar最近修改时间
	public String getLocalizeModifiedTime()
	{
		return PathFile.fileLastmodified("./javaPos.ExtendJar/localize.jar");
	}
	
	// 获得localize.jar最近修改时间
	public String getBankpayModifiedTime()
	{
		return PathFile.fileLastmodified("./javaPos.ExtendJar/bankpay.jar");
	}

	// 获得device.jar最近修改时间
	public String getDeviceModifiedTime()
	{
		return PathFile.fileLastmodified("./javaPos.ExtendJar/device.jar");
	}

	// 金卡工程交易
	public void openBankFunc(MenuFuncDef mfd, MenuFuncEvent mffe, int type)
	{
		if (type >= StatusType.MN_XYKQT1)
		{
			CreatePayment.getDefault().getPaymentBankForm().open(type - StatusType.MN_XYKXF - 1);
		}
		else
		{
			CreatePayment.getDefault().getPaymentBankForm().open(type - StatusType.MN_XYKXF);
		}
	}

	private void openBankLog(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new BankLogQueryForm();
	}

	private void openMzkSeqNoResetForm(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new MzkSeqNoResetForm();
	}

	private void openDeleteCzData(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		dcdbs.DeleteCzData();
	}

	private void openModifyInvno(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new DisplaySaleTicketForm(StatusType.MN_MODIFYINVNO);
	}

	private void openKeyBoardConfig(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		new KeyConfigForm(Display.getDefault(), GlobalVar.style_linux, false);
	}

	// 打开家电下乡返款
	private void openJdxxFk(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.JDXX_FK);
	}

	// 打开家电下乡返款退货
	private void openJdxxfBack(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.JDXX_BACK);
	}

	// 打开团购销售
	private void openGroupbuySale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.GROUPBUY_SALE);
	}

	// 打开前提售卡
	private void openCardSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.CARD_SALE);
	}

	// 打开买积分销售
	public void openMJfSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (mffe != null)
			mffe.dispose();

		GlobalInfo.saleform.setSaleType(SellType.PURCHANSE_JF);
	}

}
