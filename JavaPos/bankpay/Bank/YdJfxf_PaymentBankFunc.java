package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;

import device.ICCard.KTL512V;
import device.ICCard.NT512;

public class YdJfxf_PaymentBankFunc extends PaymentBankFunc
{
	
	public String[] getFuncItem()
	{
		String[] func = new String[4];


		//func[0] = "[" + PaymentBank.XYKQD + "]" + "移动积分初始化";
		func[0] = "[" + PaymentBank.XYKXF + "]" + "积分查询";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "积分取消";
		func[2] = "[" + PaymentBank.XYKJZ + "]" + "积分扣款";
		func[3] = "[" + PaymentBank.XKQT1 + "]" + "积分冲正";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 积分认证
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "消费金额";

				break;

			/*case PaymentBank.XYKCX: // 积分取消
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "取消";

				break;

			case PaymentBank.XYKQD: // 初始化
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "初始化";

				break;

			case PaymentBank.XKQT1: // 冲正
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "冲正";

				break;*/
				
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{				
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = "*按回车后开始积分密码认证*";
				grpTextStr[4] = null;

				break;

			/*case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车后开始积分取消";

				break;

			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车后开始移动积分初始化";

				break;
				
			case PaymentBank.XKQT1:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车后开始移动积分冲正";
				
				break;*/
		}

		return true;
	}

	public boolean XYKCheckRetCode()
	{
		// 根据返回值置返回标志
		if (bld.retcode.equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "移动积分接口调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';
			//bld.retmsg = XYKReadRetMsg(bld.retcode);// 根据返回码来查找错误信息

			return false;
		}
	}

	public String XYKReadRetMsg(String retcode)
	{
		String msg = "未知错误";
		try
		{
			switch (Convert.toInt(retcode.toString().trim()))
			{
				case 0:
					msg = "成功";
					break;
				case 1:
					msg = "小票号或柜员编号超长";
					break;
				case 2:
					msg = "网络错误";
					break;
				case 3:
					msg = "信息发送失败";
					break;
				case 4:
					msg = "信息接收失败";
					break;
				case 5:
					msg = "前置系统处理失败";
					break;
				case 6:
					msg = "充值失败";
					break;
				case 7:
					
					break;
				case 8:
					
					break;
				case 9:
					msg = "服务器不可用";
					break;
					
				default:
					break;

			}			
			
			if (!retcode.equals("0"))
			{
				msg = "交易失败:" + retcode + "|" + msg;
			}
			//System.out.println("移动积分交易函数返回:[" + msg + "], 交易类型:[" + c_getType(bld.type) + "]");
			return msg;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return msg;
		}
	}
		
	public boolean WriteRequestLog(int type, double money, String oldseqno, String oldauthno, String olddate)
	{
		try
		{
			bld = new BankLogDef();

			Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

			if (obj == null)
			{
				bld.rowcode = 1;
			}
			else
			{
				bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
			}

			bld.rqsj = ManipulateDateTime.getCurrentDateTime();
			bld.syjh = GlobalInfo.syjDef.syjh;
			bld.fphm = GlobalInfo.syjStatus.fphm;
			//bld.syyh = GlobalInfo.posLogin.gh;
			if (GlobalInfo.posLogin != null && GlobalInfo.posLogin.gh != null)
			{
				bld.syyh = GlobalInfo.posLogin.gh;
			}
			else
			{
				bld.syyh = "";
			}
			bld.type = String.valueOf(type);
			bld.je = money;
			bld.oldrq = olddate;
			
			bld.typename = getChangeType(getFuncItem(),bld.type);
			bld.classname = this.getClass().getName();

			if ((oldseqno != null) && !oldseqno.trim().equals(""))
			{
				bld.oldtrace = Long.parseLong(oldseqno);
			}
			else
			{
				bld.oldtrace = 0;
			}

			bld.cardno = "";
			bld.trace = 0;
			bld.bankinfo = "";
			bld.crc = "";
			bld.retcode = "";
			bld.retmsg = "";
			bld.retbz = 'N';
			bld.net_bz = 'N';
			bld.allotje = 0;
			bld.memo = getMemo(type, money, oldseqno, oldauthno, olddate);

			//
			if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
			bld = null;

			return false;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{		
		try
		{				
			switch (type)
			{
				/*case PaymentBank.XYKQD://初始化
					
					return this.sp_initconfig('M');*/
					
				case PaymentBank.XYKXF://积分密码认证
					String shopperNo = "";
					ProgressBox pb = null;
					try
					{
						pb = new ProgressBox();
						pb.setText("请顾客输入移动积分密码...");
						if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("1"))
						{
							shopperNo = new KTL512V().findCard(); //ICCard.getDefault().findCard();
						}
						else if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("2"))
						{
							shopperNo = new NT512().findCard();
						}
						else
						{
							new MessageBox("移动密码键盘配置不正确,请联系电脑部!", null, false);
							this.c_setret('N', "EF", "失败:移动密码键盘配置不正确.");
							return false;
						}
						
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						if (pb != null)
						{
							pb.close();
							pb = null;
						}
					}
					if (shopperNo.trim().length() != 18)
					{
						this.c_setret('N', "EF", "失败:移动积分密码输入不合法.");
						//new MessageBox("认证失败:移动积分密码输入不合法.\n\n", null, false);
						return false;
					}
					bld.cardno = shopperNo.trim();
					return this.sp_questShopper(bld.fphm, bld.syyh, shopperNo.trim());					
					
				/*case PaymentBank.XYKCX://取消认证
					String shopperNoCalcel = "";
					ProgressBox pbcancel = null;
					try
					{
						pb = new ProgressBox();
						pb.setText("请顾客输入要取消的移动积分密码...");
						shopperNo = ICCard.getDefault().findCard();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						if (pbcancel != null)
						{
							pbcancel.close();
							pbcancel = null;
						}
					}
					if (shopperNoCalcel.trim().length() != 18)
					{
						bld.retcode = "EF";
						bld.retmsg = "失败:移动积分密码不合法.";
						return false;
					}
					bld.cardno = shopperNoCalcel.trim();
					return this.sp_cancelShopper(bld.fphm, bld.syyh, shopperNoCalcel);
										
					break;
				case PaymentBank.XYKJZ://提交扣款
					//this.sp_commitShopper(fphm, syyh, shopperNo, paymoney);
					break;
				case PaymentBank.XKQT1: // 冲正
					
					return this.sp_reversalShopper(bld.fphm, bld.syyh);*/
					
				default:
					this.c_setret('N', "EF", "移动积分交易失败:此界面不支持该交易类型.");
					new MessageBox("此接口不支持该交易类型.\n\n", null, false);
					return false;

			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public boolean XYKReadResult()
	{
		// 判断是否重打印，重打印不生成请求和响应文件
		BufferedReader br = null;

		try
		{
			String[] ret = null;
			if (!PathFile.fileExist("c:\\javapos\\result.txt")
					|| ((br = CommonMethod.readFileGBK("c:\\javapos\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
						
			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{
				return false;
			}
			
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}

			ret = line.split("\\,");
			if (ret.length <= 0)
			{
				//返回数据不合法
				this.c_setret('N', "EF", "返回数据不合法");
				return false;
			}
			
			bld.retmsg = XYKReadRetMsg(ret[0]);
			if (Convert.toInt(bld.type) == PaymentBank.XYKXF && ret.length >= 5)
			{
				//券认证时会有状态message返回
				bld.retmsg += "|" + ret[5];
			}
			
			if (!ret[0].toString().trim().equals("0"))
			{
				//交易失败
				System.out.println("移动积分交易函数返回:[" + bld.retmsg + "], 交易类型:[" + c_getType(bld.type) + "], shopperNo=[" + bld.cardno + "], FPHM=[" + String.valueOf(bld.fphm) + "]");
				this.c_setret('N', ret[0].toString().trim(), bld.retmsg + ",初始化失败");
				return false;
			}
			
			bld.retmsg = XYKReadRetMsg(ret[1]);
			System.out.println("移动积分交易函数返回:[" + bld.retmsg + "], 交易类型:[" + c_getType(bld.type) + "], shopperNo=[" + bld.cardno + "], FPHM=[" + String.valueOf(bld.fphm) + "]");
			
			if (!ret[1].toString().trim().equals("0"))
			{
				//交易失败
				this.c_setret('N', ret[1].toString().trim(), bld.retmsg);
				return false;
			}
			
			bld.retcode = "00";	
						
			switch (Convert.toInt(bld.type))
			{
				case PaymentBank.XYKXF:		//积分密码认证

					double ye = Double.parseDouble(ret[2].toString().trim())/100;//券余额
					if (ye <= 0)
					{
						//new MessageBox("积分余额为 " + ye + " ,不能付款!",null,true);
						this.c_setret('N', "EF", "bld.retmsg");
						return false;
					}
					
					//写冲正文件
					this.c_writeCz(bld.fphm, bld.syyh, bld.cardno);
					
					String cardType = ret[3].toString().trim();//券类别
					String sponsor = ret[4].toString().trim();//券发行商
					//String message = ret[5].toString().trim();//状态信息
					String shopperNo = bld.cardno;//积分密码
					if (!cardType.equals("003"))
					{
						//不可找零券
						bld.je = ye;
						bld.memo = cardType + "," + sponsor;//卡类别+卡发行商
					}
					else
					{
						//可找零券
						boolean iscalcel = false;
						double minJe = 0.1;
						double maxJe = -1;						
						maxJe = (ye > bld.je ? bld.je : ye);

						while(true)
						{
							final StringBuffer sbje = new StringBuffer();
							sbje.append(String.valueOf(bld.je > maxJe ? maxJe : bld.je));
							if (new TextBox().open("消费金额:", "积分密码认证成功", "积分余额：" + ye, sbje, minJe, maxJe, true, TextBox.AllInput))
				        	{
								bld.je = Double.parseDouble(sbje.toString().trim());
								bld.memo = cardType + "," + sponsor;//卡类别+卡发行商
								this.errmsg = "";
								break;
				        	}
							else
							{
								if (GlobalVar.Key1 == new MessageBox("是否取消当前积分？",null,true).verify())
								{
									iscalcel = true;
									break;
								}
							}
						}
						 
						if (iscalcel)//取消
						{							
							int sendCancelTimes = 0;
							
							while (true)
							{
								//发送取消认证
								if (sp_cancelShopper(bld.fphm,bld.syyh,shopperNo)) break;
								
								sendCancelTimes++;
								if(sendCancelTimes >= 3)
								{
									//发送3次及以上仍失败时,提示是否强制退出界面
									int intret = new MessageBox("取消认证已经发送 " + sendCancelTimes + " 次失败，是否强行退该界面？",null,true).verify();
									if (intret == GlobalVar.Key1)
									{
										break;
									}
								}
								
							}
							this.c_setret('N', "EF", "操作失败,交易被款员取消");
							return false;
						}

						
						
					}
					
					break;
					
				case PaymentBank.XYKQD:		//初始化
				case PaymentBank.XYKCX:		//取消认证
				case PaymentBank.XYKJZ:		//提交扣款
				case PaymentBank.XKQT1: 	//冲正
					//无返回参数值
					break;
				default:
					break;

			}
						
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("ex", "读取应答异常:" + ex.getMessage());
			new MessageBox("读取移动积分交易应答数据异常!" + ex.getMessage(), null, false);
			br = null;
			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}
	}
	
	public void c_setret(char retbz, String retcode, String retmsg)
	{
		try
		{
			bld.retbz = retbz;
			bld.retcode = retcode;
			bld.retmsg = retmsg;
			this.errmsg = bld.retmsg;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public String c_getType(String type)
	{
		String ret = "";
		try
		{
			switch (Convert.toInt(type))
			{
				case PaymentBank.XYKQD://初始化					
					ret = "初始化";
					
					break;					
				case PaymentBank.XYKXF://积分密码认证
					ret = "查询";
					
					break;
				case PaymentBank.XYKCX://取消认证
					ret = "取消";
					
					break;
				case PaymentBank.XYKJZ://提交扣款
					ret = "提交";
					
					break;
				case PaymentBank.XKQT1: // 冲正
					ret = "冲正";
					
					break;					
				default:
					ret = "未知";
				
					break;

			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return String.valueOf(type) + "|" + ret;
	}
	
	public boolean c_deletePayFile()
	{
		try
		{
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\javapos\\request.txt"))
			{
				PathFile.deletePath("c:\\javapos\\request.txt");

				if (PathFile.fileExist("c:\\javapos\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\javapos\\result.txt"))
			{
				PathFile.deletePath("c:\\javapos\\result.txt");

				if (PathFile.fileExist("c:\\javapos\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	
	public boolean c_execbank(int type)
	{
		ProgressBox pb = null;
		try
		{
			String str = "";
			switch (type)
			{
				case PaymentBank.XYKQD://初始化
					str = "正在初始化移动积分业务...";
					break;
				case PaymentBank.XYKXF://积分密码认证
					str = "正在验证移动积分密码。。。";
					break;				
					
				case PaymentBank.XYKCX://取消认证
					str = "正在取消移动积分券...";
					break;
				case PaymentBank.XYKJZ://提交扣款
					str = "正在提交移动积分消费扣款...";
					break;
				case PaymentBank.XKQT1: // 冲正
					str = "正在冲正移动积分业务...";
					break;
				default:
					str = "未知交易";
					break;

			}
			
			pb = new ProgressBox();
			pb.setText(str);
			
			// 调用接口模块
			if (PathFile.fileExist("c:\\javapos\\javaposbank.exe"))
			{
				CommonMethod
						.waitForExec("c:\\javapos\\javaposbank.exe DFHQ");
			}
			else
			{
				new MessageBox("找不到移动积分调用模块 javaposbank.exe");
				XYKSetError("XX", "找不到移动积分调用模块 javaposbank.exe");
				return false;
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param type 交易类型
	 * @param paymoney 交易金额
	 * @param fphm 小票号
	 * @param syyh 收银员号
	 * @param shopperNo 积分密码
	 * @param configPara 初始化参数列表
	 * @param syjh 收银机号
	 * @return
	 */
	public boolean c_WriteRequest(int type, double paymoney, long fphm,
									String syyh, String shopperNo,String configPara,String syjh)
	{
		try
		{
			String reqstr = "";
			String initstr = configPara.trim() + "," + syjh + ",";//configPara=前置系统IP地址,前置系统得端口号,SOCKET的超时(以秒为单位),冲正超时时间,集团编号,门店编号

			switch (type)
			{
				case PaymentBank.XYKQD://初始化
					reqstr = "1," + initstr;
					break;
				case PaymentBank.XYKXF://积分密码认证
					reqstr = "7," + initstr + String.valueOf(fphm) + "," + syyh + "," + shopperNo;
					break;
				case PaymentBank.XYKCX://取消认证
					reqstr = "8," + initstr + String.valueOf(fphm) + "," + syyh + "," + shopperNo;
					bld.cardno = shopperNo;
					break;
				case PaymentBank.XYKJZ://提交扣款
					reqstr = "9," + initstr + String.valueOf(fphm) + "," + syyh + "," 
								+ shopperNo + "," + String.valueOf((long) ManipulatePrecision.doubleConvert(paymoney * 100, 2, 1));
					bld.cardno = shopperNo;
					break;
				case PaymentBank.XKQT1: // 冲正
					reqstr = "10," + initstr + String.valueOf(fphm) + "," + syyh;
					break;
				default:
					break;

			}
			bld.type = String.valueOf(type);

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("c:\\javapos\\request.txt");
				if (pw != null)
				{
					pw.println(reqstr);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入移动积分请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}
	
	private boolean c_exec(int type)
	{
		//initconfig 
		return this.c_exec(type, 0, 0, "", "", ConfigClass.CustomItem3.toString().trim(), GlobalInfo.syjDef.syjh);
	}
		
	private boolean c_exec(int type, long fphm,String syyh, String shopperNo)
	{
		//query,cancel
		return this.c_exec(type, 0, fphm, syyh, shopperNo, ConfigClass.CustomItem3.toString().trim(), GlobalInfo.syjDef.syjh);
	}
	
	private boolean c_exec(int type, double paymoney, long fphm, String syyh, String shopperNo)
	{
		//commit
		return this.c_exec(type, paymoney, fphm, syyh, shopperNo, ConfigClass.CustomItem3.toString().trim(), GlobalInfo.syjDef.syjh);
	}
	
	private boolean c_exec(int type, long fphm, String syyh)
	{
		//reversal
		return this.c_exec(type, 0, fphm, syyh, "", ConfigClass.CustomItem3.toString().trim(), GlobalInfo.syjDef.syjh);
	}
	
	private boolean c_exec(int type, double paymoney, long fphm,
			String syyh, String shopperNo,String configPara,String syjh)
	{
		try
		{
			if (configPara != null && configPara.split("\\,").length != 6)
			{
				//未启用移动积分消费
				return false;
			}

			// 先删除上次交易数据文件
			if (!c_deletePayFile()) return false;
			

			// 写入请求数据日志
			if (type != PaymentBank.XYKXF)
			{
				if (!this.WriteRequestLog(type, paymoney, "", "", "")) { return false; }
			}
			
			// 写入请求数据
			if (!c_WriteRequest(type, paymoney, fphm,
								syyh, shopperNo,configPara,syjh))
			{
				return false;
			}
			
			//调用javaposbank.exe
			if (!c_execbank(type)) return false;
			
			// 读取应答数据
			if (!XYKReadResult())
			{
				return false;
			}

			// 检查交易是否成功
			XYKCheckRetCode();

			/*// 打印签购单
			if (XYKNeedPrintDoc())
			{
				XYKPrintDoc();
			}*/
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (type != PaymentBank.XYKXF)
			{
				// 写入应答数据日志
				this.WriteResultLog();
				
			}
		}
		return false;
	}


	//判断是否是积分消费冲正文件
	private boolean isCzFile(String filename)
	{
		if (filename.startsWith("SP_") && filename.endsWith(".JFCZ"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//获取冲正数量
	public int c_getCzCount()
	{
		int retcount = 0;
		try
		{
			File file = new File(ConfigClass.LocalDBPath);
           	File[] filename = file.listFiles();

            for (int i = 0; i < filename.length; i++)
            {
            	if (isCzFile(filename[i].getName()))
            	{	
        	        // 读取文件
                    String name = filename[i].getAbsolutePath();
                    
            		// 检查文件是否为未删除文件
                	File a =  new File(ConfigClass.LocalDBPath + "/BAK_" + filename[i].getName());
                	if (a.exists())
                	{
                		 //删除冲正文件
                		c_deleteFile(name);
                		a.delete();
                		continue;
                	}
                	
                	retcount++;
        	        
            	}
            }            
           
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retcount;
	}
	
	private boolean c_writeCz(long fphm,String syyh,String shopperNo)
	{
		FileOutputStream f = null;
		try
		{
			//如果已经存在冲正,则不再写
			if (c_getCzCount() > 0) return true;
			
			String name = ConfigClass.LocalDBPath + c_getCzFileName(fphm,syyh,shopperNo);
			
			MzkRequestDef spreq = new MzkRequestDef();
			spreq.fphm = fphm;
			spreq.syyh = syyh;
			spreq.track2 = shopperNo;
            
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        s.writeObject(spreq);
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	        return true;			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
		return false;
	}
	private boolean c_deleteFile(String filename)
	{
		try
		{
			File file = new File(filename);
			if (file.exists())
			{
				file.delete();
				
				if (file.exists())
	        	{
	        		new MessageBox("移动积分消费冲正文件没有被删除,请检查磁盘!");
	        		
	        		// 加入日志
	        		AccessDayDB.getDefault().writeWorkLog("移动积分消费冲正文件 "+ file.getName() +" 没有删除成功");
	        		
	        		// 在本地记录此冲正文件已经删除，不需要上传冲正数据
	        		File a =  new File(ConfigClass.LocalDBPath + "/BAK_"+ file.getName());
	        		if (!a.createNewFile())
	        		{}
	        		
	        		return false;
	        	}
	        	else
	        	{
	        		return true;
	        	}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
		
	private String c_getCzFileName(long fphm,String syyh,String shopperNo)
	{
		try
		{
			return "SP_" + String.valueOf(fphm) + "_" + syyh.trim() + "_" + shopperNo.trim() + ".JFCZ";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}

	public boolean c_deleteCz()
	{
		return this.c_deleteCz(0,null,null);
	}
	
	public boolean c_deleteCz(long fphm,String syyh,String shopperNo)
	{
		boolean isdeleteok = true;
		try
		{			
			if (fphm > 0 
					&& (syyh != null && syyh.trim().length() > 0)
					&& (shopperNo != null && shopperNo.trim().length() > 0))
			{
				//按指定文件名
				isdeleteok = c_deleteFile(ConfigClass.LocalDBPath + c_getCzFileName(fphm,syyh,shopperNo));	
				
			}
			else
			{
				//不指定文件名,删除 *.JFCZ
				File file = new File(ConfigClass.LocalDBPath);
	           	File[] filenames = file.listFiles();

	            for (int i = 0; i < filenames.length; i++)
	            {
	            	if (isCzFile(filenames[i].getName()))
	            	{	
	        	        // 读取文件
	                    String filename = filenames[i].getAbsolutePath();
	                    
	            		// 检查文件是否为未删除文件
	                	File a =  new File(ConfigClass.LocalDBPath + "/BAK_" + filenames[i].getName());
	                	if (a.exists())
	                	{
	                		 //删除未先前删除的冲正文件
	                		c_deleteFile(filename);
	                		a.delete();
	                		continue;
	                	}
	                	
	                	isdeleteok = c_deleteFile(filename);	
	            	}
	            }		        
			}
					
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			isdeleteok = false;
		}
		return isdeleteok;
	}
	
	//发送冲正
	public boolean c_sendAccountCz()
	{
		boolean blnret = true;
		FileInputStream f = null;
		try
		{
			File file = new File(ConfigClass.LocalDBPath);
           	File[] filenames = file.listFiles();

            for (int i = 0; i < filenames.length; i++)
            {
            	if (isCzFile(filenames[i].getName()))
            	{	
        	        // 读取文件
                    String filename = filenames[i].getAbsolutePath();
                    
            		// 检查文件是否为未删除文件
                	File a =  new File(ConfigClass.LocalDBPath + "/BAK_" + filenames[i].getName());
                	if (a.exists())
                	{
                		 //删除未先前删除的冲正文件
                		c_deleteFile(filename);//ConfigClass.LocalDBPath + 
                		a.delete();
                		continue;
                	}
                	                	
            		f = new FileInputStream(filename);
                    ObjectInputStream s = new ObjectInputStream(f);

                    // 读取冲正数据
                    MzkRequestDef req = (MzkRequestDef) s.readObject();

                    // 关闭文件
                    s.close();
                    s = null;
                    f.close();
                    f = null;                    
            		
            		// 发送冲正交易
                    blnret = this.sp_reversalShopper(req.fphm, req.syyh);
                    if (!blnret) continue;
            		                    
                    // 删除冲正文件
                    c_deleteFile(filename);
            		
            	}
            }	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blnret = false;
		}
		
		return blnret;
	}

		
	/**
	 * [移动业务初始化]
	 * initType == L 表示登录过程中进行的初始化
	 *          == M 表示通过菜单初始化
	 * initpara == 前置系统IP地址,前置系统得端口号,SOCKET的超时(以秒为单位),冲正超时时间,集团编号,门店编号
	 *          == 空时表示没开启移动业务
	 *     syjh == 收银机号
	 */
	public boolean sp_initconfig()
	{
		
		boolean blnret = false;
		try
		{			
			blnret = c_exec(PaymentBank.XYKQD);
			if(blnret)
			{
				//提示初始化成功
				new MessageBox("移动积分消费业务初始化成功!", null, false);
			}
			else
			{						
				//提示初始化失败
				new MessageBox("移动积分消费业务初始化失败,不能进行移动积分消费业务!", null, false);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnret;
	}
	
	//认证
	public boolean sp_questShopper(long fphm,String syyh,String shopperNo)
	{
		boolean blnret = false;
		try
		{
			blnret = c_exec(PaymentBank.XYKXF, fphm, syyh, shopperNo);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnret;
	}

	//取消
	public boolean sp_cancelShopper(long fphm,String syyh,String shopperNo)
	{
		boolean blnret = false;
		try
		{
			blnret = c_exec(PaymentBank.XYKCX, fphm, syyh, shopperNo);
			if(blnret)
			{
				//删除冲正文件
				this.c_deleteCz(fphm, syyh, shopperNo);
				
				//提示取消失败;
				new MessageBox("移动积分密码(" + shopperNo + ")取消成功!", null, false);
			}
			else
			{
				new MessageBox("移动积分密码(" + shopperNo + ")取消失败!", null, false);				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnret;
	}
	
	//提交
	public boolean sp_commitShopper(long fphm,String syyh,String shopperNo,double paymoney)
	{
		boolean blnret = false;
		try
		{
			blnret = c_exec(PaymentBank.XYKJZ, paymoney, fphm, syyh, shopperNo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnret;
	}
	
	//冲正
	public boolean sp_reversalShopper(long fphm,String syyh)
	{
		boolean blnret = false;
		try
		{
			blnret = c_exec(PaymentBank.XKQT1, fphm, syyh);
			if (!blnret)
			{
				//最多删两次
				blnret = c_exec(PaymentBank.XKQT1, fphm, syyh);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return blnret;
	}
		
}
