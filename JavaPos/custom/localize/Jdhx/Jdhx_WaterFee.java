package custom.localize.Jdhx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 水费返回数据均存在salehead表中，djlb为d时交费，为e时撤销交费
 * str1 - 银行号|银行流水号
 * str2 - 交易类型
 * str3 -水费用户号
 * str4 - 和水厂进行通讯的数据报文 （宏信总部用，U51转小票时忽略此字段)
 * str5 - 水费交费成功标志 （宏信总部用，U51转小票时忽略此字段）
 * num3 -本月结余
 * num4 -上月结余
 * num5 -用水量
 * 
 */

public class Jdhx_WaterFee
{
	private static Jdhx_WaterFee waterFee = new Jdhx_WaterFee();

	public static Jdhx_WaterFee getDefault()
	{
		return waterFee;
	}

	protected FeeHeadDef head;
	protected Vector bodyDetail;

	final static int HEADLEN = 178;
	final static int BODYLEN = 91;

	static class Config
	{
		public String ip;
		public int port;
		public String yhbh = "19"; //
		public String operid = "hxcshxcs01";

		public boolean isValid()
		{
			if (ip == null || ip.trim().equals("") || port == 0 || operid == null || operid.trim().equals(""))
				return false;

			return true;
		}
	}

	static class BankInfo
	{
		public static final String ICBC = "01"; // 工行
		public static final String BCM = "02"; // 交行
		public static final String ABC = "03"; // 农行
		public static final String CCB = "04"; // 建行
		public static final String BOC = "05";// 中行
		public static final String BBC = "06";// 商行
		public static final String PSBC = "07";// 邮政储藏
		public static final String TCBC = "19"; // 商贸城

	}

	static class TradeType
	{
		public static final String QUERY = "01"; // 查询
		public static final String FEE = "02";// 缴费
		public static final String CZ = "03";// 冲正
		public static final String QRYBP = "04"; // 查询补票
		public static final String REPRINT = "05";// 重打发票
		public static final String CREATEPK = "06";// 建立批扣
		public static final String CANCELPK = "07";// 撤销批扣
		public static final String QRYPK = "08";// 查询批扣

		public static String getTradeName(String type)
		{
			if (type.equals(FEE))
				return "缴费";
			else if (type.equals(CZ))
				return "冲正";
			else if (type.equals(QRYBP))
				return "查询补票";
			else if (type.equals(REPRINT))
				return "重打发票";
			else
				return "未知交易";
		}
	}

	static class FeeHeadDef
	{
		public int packlen = HEADLEN; // 包长
		public String jylb = ""; // 交易类别
		public String fhm = ""; // 返回码
		public String yhbh = ""; // 银行号
		public String yhh = ""; // 用户编号
		public String xm = ""; // 姓名
		public String dz = ""; // 地址
		public int qfbs; // 欠费笔数
		public double qfzje; // 欠费总金额
		public double sjje; // 实缴金额
		public double sqjy; // 上期节余
		public double bqjy; // 本期节余
		public String qsrq = ""; // 清算日期
		public String lsh = ""; // 银行流水
		public String sfry = ""; // 操作人员码

		public static int XFL = 0; // 用水量

		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append(ManipulateStr.PadRight(packlen + "", 4, ' '));
			sb.append(ManipulateStr.PadRight(jylb, 2, ' '));
			sb.append(ManipulateStr.PadRight(fhm, 2, ' '));
			sb.append(ManipulateStr.PadRight(yhbh, 2, ' '));
			sb.append(ManipulateStr.PadRight(yhh, 8, ' '));
			sb.append(ManipulateStr.PadChineseRight(xm, 40, ' ')); //汉字占两字节
			sb.append(ManipulateStr.PadChineseRight(dz, 40, ' '));
			sb.append(ManipulateStr.PadRight(qfbs + "", 2, '0'));
			sb.append(ManipulateStr.PadRight(qfzje + "", 10, '0'));
			sb.append(ManipulateStr.PadRight(sjje + "", 10, '0'));
			sb.append(ManipulateStr.PadRight(sqjy + "", 10, '0'));
			sb.append(ManipulateStr.PadRight(bqjy + "", 10, '0'));
			sb.append(ManipulateStr.PadRight(qsrq, 8, ' '));
			sb.append(ManipulateStr.PadRight(lsh, 20, ' '));
			sb.append(ManipulateStr.PadRight(sfry, 10, ' '));

			return sb.toString();

		}

		public static FeeHeadDef parseFeeHeadDef(String retInfo) throws UnsupportedEncodingException
		{
			FeeHeadDef feeHead = null;

			byte[] head = retInfo.getBytes("GBK");
			if (head.length < HEADLEN)
				return feeHead;

			byte[] byteinfo = fetchFieldByte(head, 0, HEADLEN);

			try
			{
				feeHead = new FeeHeadDef();
				feeHead.packlen = Convert.toInt(fetchFieldString(byteinfo, 0, 4));
				feeHead.jylb = fetchFieldString(byteinfo, 4, 6);
				feeHead.fhm = fetchFieldString(byteinfo, 6, 8);
				feeHead.yhbh = fetchFieldString(byteinfo, 8, 10);
				feeHead.yhh = fetchFieldString(byteinfo, 10, 18);
				feeHead.xm = fetchFieldString(byteinfo, 18, 58).trim();
				feeHead.dz = fetchFieldString(byteinfo, 58, 98).trim();
				feeHead.qfbs = Convert.toInt(fetchFieldString(byteinfo, 98, 100));
				feeHead.qfzje = Convert.toDouble(fetchFieldString(byteinfo, 100, 110));
				feeHead.sjje = Convert.toDouble(fetchFieldString(byteinfo, 110, 120));
				feeHead.sqjy = Convert.toDouble(fetchFieldString(byteinfo, 120, 130));
				feeHead.bqjy = Convert.toDouble(fetchFieldString(byteinfo, 130, 140));
				feeHead.qsrq = fetchFieldString(byteinfo, 140, 148);
				feeHead.lsh = fetchFieldString(byteinfo, 148, 168);
				feeHead.sfry = fetchFieldString(byteinfo, 168, 178);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				new MessageBox("解析返回数据失败!");
				feeHead = null;
			}
			return feeHead;
		}
	}

	static class FeeBodyDef
	{
		public String cbrq; // 抄表日期
		public String cbqk; // 抄表情况
		public double dhjg; // 到户单价
		public int byss; // 本月示数
		public int syss; // 上月示数
		public int xfl; // 用水量
		public double sfje; // 消费金额
		public double znj; // 本笔违约金
		public double syjy; // 上月节余
		public double byjy; // 本月节余
		public double sfhj; // 本笔应收金额

		public static Vector parseFeeBodyDef(String retInfo)
		{
			Vector bodyDetail = new Vector();
			try
			{
				byte[] detail = retInfo.getBytes("GBK");
				if (detail.length < HEADLEN + BODYLEN)
					return bodyDetail;

				byte[] subDetail = fetchFieldByte(detail, HEADLEN, detail.length);

				int count = 0;
				do
				{
					FeeBodyDef body = new FeeBodyDef();
					byte[] item = fetchFieldByte(subDetail, count * BODYLEN, (count + 1) * BODYLEN);

					body.cbrq = fetchFieldString(item, 0, 8);
					body.cbqk = fetchFieldString(item, 8, 18).trim();
					body.dhjg = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 18, 23)) / 1000, 2, 1);
					body.byss = Convert.toInt(fetchFieldString(item, 23, 29));
					body.syss = Convert.toInt(fetchFieldString(item, 29, 35));
					body.xfl = Convert.toInt(fetchFieldString(item, 35, 41));
					body.sfje = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 41, 51)) / 1000, 2, 1);
					body.znj = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 51, 61)), 2, 1);
					body.syjy = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 61, 71)), 2, 1);
					body.byjy = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 71, 81)), 2, 1);
					body.sfhj = ManipulatePrecision.doubleConvert(Convert.toDouble(fetchFieldString(item, 81, 91)), 2, 1);

					// 记录下总用水量
					FeeHeadDef.XFL += body.xfl;

					bodyDetail.add(body);
					count++;
				}
				while (count * BODYLEN < subDetail.length);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				new MessageBox("解析返回数据失败!");
			}

			return bodyDetail;
		}
	}

	protected Config config;

	public FeeHeadDef getFeeHead()
	{
		return head;
	}

	public boolean execute(SaleHeadDef saleHead, String type, Vector saleGoods, Vector salePayment)
	{
		if (!type.equals(TradeType.QUERY) && !type.equals(TradeType.FEE) && !type.equals(TradeType.CZ) && !type.equals(TradeType.QRYBP) && !type.equals(TradeType.REPRINT) && !type.equals(TradeType.CREATEPK) && !type.equals(TradeType.CANCELPK) && !type.equals(TradeType.QRYPK))
		{
			new MessageBox("水费接口不支持此交易类型!");
			return false;
		}

		if (!readConfig())
		{
			new MessageBox("读取水费配置文件失败,请联系信息部门!");
			return false;
		}

		StringBuffer sb = new StringBuffer();

		try
		{
			if (type.equals(TradeType.QUERY))
			{
				// 清空数据
				head = null;
				bodyDetail = null;
				FeeHeadDef.XFL = 0;

//				saleHead.hykh = saleHead.str3 = "";

				TextBox txt = new TextBox();
				// 用户编号
				String id = "";
				if (saleHead.str3 ==  null || saleHead.str3.equals(""))
				{
									
					do
					{
						
						if (!txt.open("请输入用户编号或刷会员卡", "用户编号", "请输入用户编号或会员卡查询缴费情况", sb, 0, 0, false, TextBox.MsrKeyInput))
							return false;
						
						if (txt.Track2.trim().equals(""))
							continue;
						
						break;
					}
					while (true);
					
					// 根据会员号或手机号来查询水费用户号
					if (txt.Track2.length() > 9 || txt.Track2.length() == 11)
					{
						CustomerDef cust = new CustomerDef();
						if (!DataService.getDefault().getCustomer(cust, txt.Track2))
							return false;
						
						if (cust != null)
						{
							if (cust.str1 == null || cust.str1.trim().equals(""))
							{
								new MessageBox("该会员未绑定水费用户号\n请输入水费用户号查询后进行会员绑定");
								return false;
							}
							
							saleHead.hykh = cust.code;
							saleHead.hykname = cust.name;
							id  = cust.str1;
							
						}
					}
					
					if ( txt.Track2.length() >0 && txt.Track2.length() < 9)
					{
						txt.Track2 = Convert.increaseCharForward(txt.Track2, '0', 8);
						id = txt.Track2.trim();
					}			
				}
				else
				{
					id = saleHead.str3.trim();
				}
				
				if (txt.Track2 != null && txt.Track2.length() >0 && txt.Track2.length() < 9)
				{
					txt.Track2 = Convert.increaseCharForward(txt.Track2, '0', 8);
				}
				FeeHeadDef query = new FeeHeadDef();
				query.jylb = type;
				query.yhh = id;
				query.yhbh = config.yhbh;
				query.sfry = config.operid;

				sb.delete(0, sb.length());
				if (!senddata(query, sb))
					return false;

				head = FeeHeadDef.parseFeeHeadDef(sb.toString());

				if (head == null)
					return false;

				PosLog.getLog("查询-请求:").info(query.toString());
				PosLog.getLog("查询-返回:").info(sb.toString());
				
				if (head.fhm.equals("00"))
				{
					bodyDetail = FeeBodyDef.parseFeeBodyDef(sb.toString());
					if (bodyDetail == null)
						return false;
					
					// 保存每月用水信息，后台报表分析用
					saleHead.memo = Convert.newSubString(sb.toString(), HEADLEN);
					System.out.println("body:" + saleHead.memo);
					saleHead.str3 = head.yhh; // 用于绑定会员卡
//					saleHead.str6 = head.xm; // 用于记录用户名
					saleHead.num5 = FeeHeadDef.XFL; // 记录水量
					WaterFeeForm feeForm = new WaterFeeForm(saleHead);
					return feeForm.open(head, bodyDetail);
				}

				if (saleHead.str3 ==  null || saleHead.str3.equals(""))
				{
					String info = "";
					if (head.xm != null && !head.xm.trim().equals(""))
					{
						info = "用户编号:" + head.yhh + "    用户名:" + head.xm + "\n";
					}
					new MessageBox(info +  getError(head.fhm));					
				}
				return false;
			}
			else if (type.equals(TradeType.FEE))
			{
				if (head == null)
				{
					new MessageBox("请先查询用户缴费情况!");
					return false;
				}

				// 发送缴费前先记录下上期节余和本期节余，方便冲正时用
				saleHead.str2 = type;
				saleHead.str3 = head.yhh;
				saleHead.num3 = head.sqjy;
				saleHead.num4 = head.bqjy;

				FeeHeadDef fee = new FeeHeadDef();
				fee.jylb = type; // 交易类别
				// fee.fhm = head.fhm; // 返回码
				//
				fee.yhbh = config.yhbh; // 银行号
				fee.yhh = head.yhh; // 用户编号
				fee.xm = head.xm; // 姓名
				fee.dz = head.dz; // 地址
				fee.qfbs = head.qfbs; // 欠费笔数
				fee.qfzje = head.qfzje; // 欠费总金额
				fee.sjje = saleHead.ysje;// 实缴金额
				fee.sqjy = head.sqjy; // 上期节余
				fee.bqjy = head.bqjy; // 本期节余
				fee.qsrq = head.qsrq; // 清算日期
				// fee.lsh = head.lsh; // 银行流水
				fee.sfry = config.operid; // 操作人员码

				PosLog.getLog("缴费-请求:").info(fee.toString());
				
				sb.delete(0, sb.length());
				if (!senddata(fee, sb))
				{
					ManipulateDateTime mdt = new ManipulateDateTime();
					saveCzData(mdt.getDateTimeByEmpty(), saleHead, fee);
					return false;
				}

				head = FeeHeadDef.parseFeeHeadDef(sb.toString());
				if (head == null)
					return false;

				PosLog.getLog("缴费-返回:").info(sb.toString());
				
				if (!head.fhm.equals("00"))
				{
					
//					ManipulateDateTime mdt = new ManipulateDateTime();
//					saveCzData(mdt.getDateTimeByEmpty(), saleHead, fee);
//					
//					MessageBox msg = new MessageBox(getError(head.fhm)+ "\n\n缴纳用户水费失败，是否冲正后再缴纳?",null, true);
//					if (msg.verify() == GlobalVar.Key1 && execute(saleHead, TradeType.CZ))
//					{
//						msg = new MessageBox("冲正成功，退到付款界面继续缴费");
//					}
					
					MessageBox msg = new MessageBox(getError(head.fhm));
					return false;
				}

//				
				saleHead.str1 = head.yhbh + "|" + head.lsh + "|"; // 银行编号
//				saleHead.str1 = head.yhbh ;// 银行编号				
				PosLog.getLog("缴费-返回  str1:").info(saleHead.str1);
				
				saleHead.str4 = fee.toString(); // 银行流水号
				saleHead.num5 = FeeHeadDef.XFL;// 记录用水量
				writeWaterFeeBill(head, saleHead, saleGoods, salePayment);

				return true;
			}
			else if (type.equals(TradeType.CZ))
			{
				saleHead.str2 = type;
//				saleHead.str3 = head.yhh;

				FeeHeadDef cz = new FeeHeadDef();
				cz.jylb = type; // 交易类别
				// cz.fhm = head.fhm; // 返回码
				cz.yhbh = config.yhbh; // 银行号
				cz.yhh = saleHead.str3; // 用户编号
				cz.xm = Convert.newSubString(saleHead.str4, 18, 58); // 姓名
				cz.dz = Convert.newSubString(saleHead.str4, 58, 98);; // 地址
				// cz.qfbs = head.qfbs; // 欠费笔数
				// cz.qfzje = head.qfzje; // 欠费总金额
				cz.sjje = saleHead.ysje;// 实缴金额
				cz.sqjy = saleHead.num3; // 上期节余
				cz.bqjy = saleHead.num4; // 本期节余
				cz.qsrq = "20990101";// head.qsrq; // 清算日期
				// cz.lsh = head.lsh; // 银行流水
				cz.sfry = config.operid; // 操作人员码

				sb.delete(0, sb.length());
				
				// 保存冲正信息，以防冲正失败，做手动冲正
				if (!senddata(cz, sb))
					return false;

				head = FeeHeadDef.parseFeeHeadDef(sb.toString());
				if (head == null)
					return false;

				PosLog.getLog("红冲-请求:").info(cz.toString());
				PosLog.getLog("红冲-返回:").info(sb.toString());
				
				if (!head.fhm.equals("00"))
				{
					new MessageBox(getError(head.fhm));
					return false;
				}
				
				saleHead.str3 = head.yhh;
				
				// 成功则记下返回码
				saleHead.str1 = head.yhbh + "|" + head.lsh + "|"; // 银行编号
//				saleHead.str1 = head.yhbh;// 银行编号
				saleHead.str4 = cz.toString(); // 银行流水号
				writeWaterFeeBill(head, saleHead, saleGoods, salePayment);
				return true;
			}
			else
			{
				new MessageBox("未开发的接口功能!");
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("缴纳水费异常：" + ex.getMessage());
		}
		return false;
	}
	
	public boolean saveCzData(String filename, SaleHeadDef saleHead, FeeHeadDef cz)
	{
		try
		{
			PrintWriter pw = null;
	         try
	         {
	        	cz.jylb = TradeType.CZ;
	        	cz.qfbs = 0;
	        	cz.qfzje = 0;
	        	cz.qsrq = "21990101";
	        	String lineSeparator = System.getProperty("line.separator"); // 获得系统环境的换行符
	        	StringBuffer sb = new StringBuffer();
	        	sb.append("fphm = " + saleHead.fphm + lineSeparator); // 小票号
	        	sb.append("date = " + saleHead.rqsj + lineSeparator);  //日期时间
	        	sb.append("userid = " + cz.yhh + lineSeparator);  //水费用户ID
	        	sb.append("username = " + cz.xm + lineSeparator);  //用户名
	        	sb.append("data = " + cz.toString() + lineSeparator); // 冲正信息
	        	// 文件名格式  water_日期时间_用户号.dat,例如: water_20141111143700_00032268.dat
	        	if (!CommonMethod.isFileExist(ConfigClass.LocalDBPath + "water"))
	        	{
	        		new File(ConfigClass.LocalDBPath + "water").mkdirs();
	        	}
	            pw = CommonMethod.writeFile( ConfigClass.LocalDBPath + "water//water_" + filename + "_" + cz.yhh +".dat");
	            if (pw != null)
	            {
	                pw.println(sb.toString());
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return true;
	}

	public boolean senddata(FeeHeadDef head, StringBuffer retBuf)
	{
		Socket socket = null;
		byte[] readBuffer = new byte[5000];

		try
		{

			System.out.println("int:" + head.toString());
			socket = new Socket(config.ip, config.port);
			socket.setSoTimeout(ConfigClass.ReceiveTimeout);

			socket.getOutputStream().write(head.toString().getBytes());
			socket.getOutputStream().flush();

			// 读取数据
			socket.getInputStream().read(readBuffer);

			String recv = new String(readBuffer, "GBK").trim();
			System.out.println("out:" + recv);
			retBuf.append(recv);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean senddata(String info, StringBuffer retBuf)
	{
		Socket socket = null;
		byte[] readBuffer = new byte[5000];

		try
		{

			System.out.println("int:" + info.toString());
			socket = new Socket(config.ip, config.port);
			socket.setSoTimeout(ConfigClass.ReceiveTimeout);

			socket.getOutputStream().write(info.toString().getBytes());
			socket.getOutputStream().flush();

			// 读取数据
			socket.getInputStream().read(readBuffer);

			String recv = new String(readBuffer, "GBK").trim();
			System.out.println("out:" + recv);
			retBuf.append(recv);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			String cmd = "269 0100  00026866方开珍                                  东小区6幢405室                           1      8.14      8.00      0.19      0.1420141022                            hx20141015正常       2650   218   215     3      7950         0      0.19      0.14      8.00";

			FeeHeadDef.parseFeeHeadDef(cmd);
			FeeBodyDef.parseFeeBodyDef(cmd);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected static byte[] fetchFieldByte(byte[] src, int start, int end)
	{
		try
		{
			byte[] field = new byte[end - start];
			for (int i = start, j = 0; i < end; i++, j++)
			{
				field[j] = src[i];
			}

			return field;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	protected static String fetchFieldString(byte[] src, int start, int end)
	{
		try
		{
			byte[] field = fetchFieldByte(src, start, end);

			return new String(field, "GBK");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String getError(String code)
	{
		if (code.equals("00"))
			return "用户交易成功";
		else if (code.equals("01"))
			return "欠费大于3个月,不能交费，到公司柜台处理";
		else if (code.equals("02"))
			return "停水用户,不能交费";
		else if (code.equals("03"))
			return "单位用户,不能交费";
		else if (code.equals("04"))
			return "用户用水记录没有录入或没有欠费记录";
		else if (code.equals("05"))
			return "用户正在批扣,不能在柜面交纳水费";
		else if (code.equals("06"))
			return "没有用户交费记录,不能补打发票";
		else if (code.equals("07"))
			return "补打发票失败,请确认日期";
		else if (code.equals("08"))
			return "冲正要素不符,不能冲正";
		else if (code.equals("09"))
			return "交费月份费用已缴或者没有要交纳月份数据,请确认日期";
		else if (code.equals("10"))
			return "交费金额不符,不能交费";
		else if (code.equals("11"))
			return "用户还有费用没有交纳,请一次缴清";
		else if (code.equals("12"))
			return "费用没有全部冲正,请重新重调全部费用";
		else if (code.equals("13"))
			return "用户发票已经补打，不能再次补打";
		else
			return "未知错误";
	}

	private boolean readConfig()
	{
		BufferedReader br = null;
		try
		{
			if (config != null && config.isValid())
				return true;

			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/WaterFeeConfig.ini");
			if (br == null)
				return false;

			String line;
			String[] sp;

			config = new Config();

			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("serverip") == 0)
				{
					config.ip = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("port") == 0)
				{
					config.port = Convert.toInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("yhbh") == 0)
				{
					config.yhbh = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("operid") == 0)
				{
					config.operid = sp[1].trim();
				}
			}

			if (!config.isValid())
			{
				config = null;
				return false;
			}

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	static class WaterFeeForm
	{
		private SaleHeadDef saleHead;
		private Shell shell;
		protected PosTable posTable;
		protected Label dz;
		protected Label xm;
		protected Label yhh;
		protected Label qfbs;
		protected Label qfzje;
		protected Label sqjy;
		protected Label bqjy;
		protected Label qsrq;
		protected Label sjje;
		protected boolean ret;

		public WaterFeeForm(SaleHeadDef salehead)
		{
			this.saleHead = salehead;
		}

		public boolean open(FeeHeadDef feeHead, Vector feeDetail)
		{
			Display display = Display.getDefault();

			createContents();

			createEvent(feeHead, feeDetail);

			// 创建触屏操作按钮栏
			ControlBarForm.createMouseControlBar(this, shell);

			// 加载背景图片
			Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

			if (!shell.isDisposed())
			{
				shell.open();
				posTable.setFocus();
			}

			while (!shell.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}

			// 释放背景图片
			ConfigClass.disposeBackgroundImage(bkimg);
			return ret;
		}

		protected void createEvent(FeeHeadDef feeHead, Vector feeDetail)
		{
			// 设定键盘事件
			NewKeyEvent event = new NewKeyEvent()
			{
				public void keyDown(KeyEvent e, int key)
				{
					keyPressed(e, key);
				}

				public void keyUp(KeyEvent e, int key)
				{
					keyReleased(e, key);
				}
			};

			NewKeyListener key = new NewKeyListener();
			key.event = event;

			shell.addKeyListener(key);
			posTable.addKeyListener(key);

			loadData(feeHead, feeDetail);

			shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);
		}

		protected void loadData(FeeHeadDef feeHead, Vector feeDetail)
		{
			dz.setText(feeHead.dz);
			xm.setText(feeHead.xm);
			yhh.setText(feeHead.yhh);
			qfbs.setText(feeHead.qfbs + "");
			qfzje.setText(feeHead.qfzje + "");
			sqjy.setText(feeHead.sqjy + "");
			bqjy.setText(feeHead.bqjy + "");
			qsrq.setText(feeHead.qsrq);
			sjje.setText(feeHead.sjje + "");

			for (int i = 0; i < feeDetail.size(); i++)
			{
				TableItem tableItem = new TableItem(posTable, SWT.NONE);
				FeeBodyDef body = (FeeBodyDef) feeDetail.get(i);
				if (body == null)
					continue;

				String[] item = new String[12];
				item[0] = (i + 1) + "";
				item[1] = body.cbrq;
				item[2] = body.cbqk;
				item[3] = body.dhjg + "";
				item[4] = body.byss + "";
				item[5] = body.syss + "";
				item[6] = body.xfl + "";
				item[7] = body.sfje + "";
				item[8] = body.znj + "";
				item[9] = body.syjy + "";
				item[10] = body.byjy + "";
				item[11] = body.sfhj + "";

				tableItem.setText(item);
			}
			if (posTable.getItemCount() > 0)
				posTable.setSelection(0);
		}

		public void keyPressed(KeyEvent e, int key)
		{
			switch (key)
			{
			case GlobalVar.ArrowUp:
				if (posTable.getSelectionIndex() != 0)
					posTable.setSelection(posTable.getSelectionIndex() - 1);
				else
					posTable.setSelection(0);
				break;

			case GlobalVar.ArrowDown:
				if (posTable.getSelectionIndex() == posTable.getItemCount() - 1)
					posTable.setSelection(posTable.getItemCount() - 1);
				else
					posTable.setSelection(posTable.getSelectionIndex() + 1);
				break;
			}
		}

		public void keyReleased(KeyEvent e, int key)
		{
			//当重新进行会员卡绑定时，避免出现两次提示
			boolean flag = true;
			switch (key)
			{
			case GlobalVar.MemberGrant:
				if (saleHead.hykh != null && saleHead.hykh.length() > 0)
				{
					MessageBox msg = new MessageBox("该水费用户(" + saleHead.str3 + ")已绑定会员卡" + saleHead.hykh + ",\n是否重新进行会员卡绑定?\n\n 1-重新绑定会员  2-解除绑定会员  其他键-退出",null,false);
					int mKey = msg.verify();
//					if (mKey != GlobalVar.Key1)
//					{					
//						break;
//					}
					if (mKey == GlobalVar.Key1)
					{
//						flag = false;
					}
					else if (mKey == GlobalVar.Key2)
					{
						Jdhx_NetService netsrv = (Jdhx_NetService) NetService.getDefault();
						if (netsrv.waterFeebindCustomer(saleHead.hykh, ""))
						{
							new MessageBox("水费用户号解除绑定会员卡成功!");
							saleHead.hykh = "";
							saleHead.hykname = "";
						}
						break;
					}
					else
					{
						break;
					}
				}

				HykInfoQueryBS hybs = CustomLocalize.getDefault().createHykInfoQueryBS();
				String track = hybs.readMemberCard();
				if (track == null || track.trim().equals(""))
					break;
				CustomerDef cust = new CustomerDef();
				if (DataService.getDefault().getCustomer(cust, track))
				{
					if (cust == null)
						break;

					if (cust.str1 != null && !cust.str1.equals("") && cust.str1.equals(saleHead.str3) && flag)
					{
						MessageBox msg = new MessageBox("会员卡 " + cust.code + " 已绑定该水费用户(" + cust.str1 + ")",null,false);
						saleHead.hykh = cust.code;
						saleHead.hykname = cust.name;
						break;
					}
					else if (cust.str1 != null && !cust.str1.equals("") && !cust.str1.equals(saleHead.str3) )
					{

//						MessageBox msg = new MessageBox("该水费用户(" + saleHead.str3 + ")已绑定会员卡" + saleHead.hykh + ",\n是否重新进行会员卡绑定?",null,true);
						MessageBox msg = new MessageBox("会员卡 " + cust.code + " 已绑定水费用户(" + cust.str1 + "),\n是否将该水费用户(" + saleHead.str3  + ")绑定到此会员卡",null,true);
						int mKey = msg.verify();
						if (mKey != GlobalVar.Key1)
						{					
							break;
						}
						saleHead.hykh = cust.code;
						saleHead.hykname = cust.name;
						
					}

					Jdhx_NetService netsrv = (Jdhx_NetService) NetService.getDefault();
					if (netsrv.waterFeebindCustomer(cust.code, saleHead.str3))
					{
						new MessageBox("水费用户号绑定会员卡成功!");
						saleHead.hykh = cust.code;
						saleHead.hykname = cust.name;
					}
				}
				break;
			case GlobalVar.Validation:
				shell.close();
				shell.dispose();
				ret = true;
				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
				saleHead.hykh = saleHead.str3 = "";
				ret = false;
				break;
			}
		}

		protected void createContents()
		{
			shell = new Shell(GlobalVar.style);
			shell.setText("消费查询明细");
			shell.setSize(790, 500);

			Label lblNewLabel = new Label(shell, SWT.NONE);
			lblNewLabel.setText("用户号:");
			lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			lblNewLabel.setBounds(10, 12, 70, 25);

			Label label = new Label(shell, SWT.NONE);
			label.setText("姓  名:");
			label.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label.setBounds(10, 51, 70, 25);

			Label label_1 = new Label(shell, SWT.NONE);
			label_1.setText("地  址:");
			label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_1.setBounds(10, 122, 70, 25);

			Label label_2 = new Label(shell, SWT.NONE);
			label_2.setAlignment(SWT.RIGHT);
			label_2.setText("欠费笔数:");
			label_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_2.setBounds(268, 13, 103, 25);

			Label label_3 = new Label(shell, SWT.NONE);
			label_3.setText("欠费总金额:");
			label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_3.setBounds(262, 51, 115, 25);

			Label label_4 = new Label(shell, SWT.NONE);
			label_4.setText("上期节余:");
			label_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_4.setBounds(552, 12, 95, 25);

			Label label_5 = new Label(shell, SWT.NONE);
			label_5.setText("本期节余:");
			label_5.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_5.setBounds(552, 52, 95, 25);

			Group group = new Group(shell, SWT.NONE);
			group.setText("缴费明细:");
			group.setBounds(2, 159, 780, 273);

			posTable = new PosTable(group, SWT.BORDER | SWT.FULL_SELECTION);
			posTable.setLinesVisible(true);
			posTable.setHeaderVisible(true);
			posTable.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
			posTable.setBounds(5, 22, 770, 244);

			TableColumn tableColumn = new TableColumn(posTable, SWT.NONE);
			tableColumn.setWidth(38);
			tableColumn.setText("序号");

			TableColumn tableColumn_1 = new TableColumn(posTable, SWT.CENTER);
			tableColumn_1.setWidth(73);
			tableColumn_1.setText("抄表日期");

			TableColumn tableColumn_8 = new TableColumn(posTable, SWT.CENTER);
			tableColumn_8.setWidth(60);
			tableColumn_8.setText("抄表情况");

			TableColumn tableColumn_2 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_2.setWidth(70);
			tableColumn_2.setText("到户单价");

			TableColumn tableColumn_3 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_3.setWidth(70);
			tableColumn_3.setText("本月示数");

			TableColumn tableColumn_4 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_4.setWidth(70);
			tableColumn_4.setText("上月示数");

			TableColumn tblclmnNewColumn = new TableColumn(posTable, SWT.CENTER);
			tblclmnNewColumn.setWidth(50);
			tblclmnNewColumn.setText("用水量");

			TableColumn tblclmnNewColumn_1 = new TableColumn(posTable, SWT.RIGHT);
			tblclmnNewColumn_1.setWidth(67);
			tblclmnNewColumn_1.setText("水费金额");

			TableColumn tblclmnNewColumn_2 = new TableColumn(posTable, SWT.RIGHT);
			tblclmnNewColumn_2.setWidth(60);
			tblclmnNewColumn_2.setText("违约金");

			TableColumn tableColumn_5 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_5.setWidth(60);
			tableColumn_5.setText("上月节余");

			TableColumn tableColumn_6 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_6.setWidth(60);
			tableColumn_6.setText("本月节余");

			TableColumn tableColumn_7 = new TableColumn(posTable, SWT.RIGHT);
			tableColumn_7.setWidth(86);
			tableColumn_7.setText("应收金额");

			dz = new Label(shell, SWT.NONE);
			dz.setForeground(SWTResourceManager.getColor(255, 0, 0));
			dz.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			dz.setBounds(87, 122, 662, 25);

			xm = new Label(shell, SWT.NONE);
			xm.setForeground(SWTResourceManager.getColor(255, 0, 0));
			xm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			xm.setBounds(88, 51, 128, 25);

			yhh = new Label(shell, SWT.NONE);
			yhh.setForeground(SWTResourceManager.getColor(255, 0, 0));
			yhh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			yhh.setBounds(88, 12, 135, 25);

			qfbs = new Label(shell, SWT.NONE);
			qfbs.setForeground(SWTResourceManager.getColor(255, 0, 0));
			qfbs.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			qfbs.setBounds(381, 12, 130, 25);

			qfzje = new Label(shell, SWT.NONE);
			qfzje.setForeground(SWTResourceManager.getColor(255, 0, 0));
			qfzje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			qfzje.setBounds(383, 51, 130, 25);

			sqjy = new Label(shell, SWT.NONE);
			sqjy.setForeground(SWTResourceManager.getColor(255, 0, 0));
			sqjy.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			sqjy.setBounds(653, 12, 120, 25);

			bqjy = new Label(shell, SWT.NONE);
			bqjy.setForeground(SWTResourceManager.getColor(255, 0, 0));
			bqjy.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			bqjy.setBounds(653, 51, 120, 25);

			Label label_6 = new Label(shell, SWT.NONE);
			label_6.setText("清算日期:");
			label_6.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_6.setBounds(552, 92, 95, 25);

			qsrq = new Label(shell, SWT.NONE);
			qsrq.setForeground(SWTResourceManager.getColor(255, 0, 0));
			qsrq.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			qsrq.setBounds(653, 91, 120, 25);

			Label label_8 = new Label(shell, SWT.NONE);
			label_8.setText("实缴金额:");
			label_8.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label_8.setBounds(283, 91, 95, 25);

			sjje = new Label(shell, SWT.NONE);
			sjje.setForeground(SWTResourceManager.getColor(255, 0, 0));
			sjje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			sjje.setBounds(383, 90, 120, 25);

			Label label22 = new Label(shell, SWT.NONE);
			label22.setText("【退出键】结束操作         【会员授权键】会员绑定        【确认键】进行缴费");
			label22.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
			label22.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
			label22.setBounds(10, 443, 763, 23);
		}

		protected void checkSubclass()
		{

		}
	}

	protected boolean writeWaterFeeBill(FeeHeadDef feeHead, SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		StringBuffer sb = new StringBuffer();
		PrintWriter pw = null;
		try
		{

			sb.append("           水费缴款凭证              \r\n");
			sb.append("---------------------------------\r\n");
			sb.append(Convert.appendStringSize("", "收银机号", 0, 10, 10, 0) + Convert.appendStringSize("", "收银员号" , 1, 10, 10, 0) + Convert.appendStringSize("", "发票号" , 1, 10, 10, 0) + "\r\n");
			sb.append(Convert.appendStringSize("", GlobalInfo.syjStatus.syjh , 0, 10, 10, 0) + Convert.appendStringSize("", GlobalInfo.syjStatus.syyh , 1, 10, 10, 0) + Convert.appendStringSize("", String.valueOf(GlobalInfo.syjStatus.fphm) , 1, 10, 10, 0) + "\r\n");
			sb.append("---------------------------------\r\n");
			sb.append("用 户 号:" + Convert.appendStringSize("", feeHead.yhh , 1, 10, 10, 0));
			sb.append("交易类型:" + Convert.appendStringSize("", TradeType.getTradeName(feeHead.jylb) , 1, 10, 10, 0) + "\r\n");
			sb.append("用 户 名:" + Convert.appendStringSize("", feeHead.xm , 1, 10, 10, 0) );			
//			sb.append("欠费金额:" + Convert.appendStringSize("", feeHead.qfzje +"" , 1, 10, 10, 0));
			sb.append("欠费笔数:" + Convert.appendStringSize("", feeHead.qfbs +"" , 1, 10, 10, 0) + "\r\n");
			
			String info = saleHead.str4 + saleHead.memo;
			PosLog.getLog("打印详细:" + info.length() + "L").info(info);
			Vector v = new Vector();
			v = FeeBodyDef.parseFeeBodyDef(info);
			PosLog.getLog("Vector:" ).info(v.size() + "个" + feeHead.jylb);
			for (int i = 0; i < v.size(); i++)
			{
				FeeBodyDef body = (FeeBodyDef) v.get(i);
				
				sb.append("       第" + (i + 1) + "笔缴费 \r\n");
				sb.append("抄表日期:" + Convert.appendStringSize("", body.cbrq , 1, 10, 10, 0));
				sb.append("抄表情况:" + Convert.appendStringSize("", body.cbqk , 1, 10, 10, 0) + "\r\n");				
				sb.append("到户单价:" + Convert.appendStringSize("", body.dhjg + "" , 1, 10, 10, 0));
				sb.append("本月示数:" + Convert.appendStringSize("", String.valueOf(body.byss) , 1, 10, 10, 0) + "\r\n");				
				sb.append("用 水 量:" + Convert.appendStringSize("", body.xfl + "", 1, 10, 10, 0));
				sb.append("上月示数:" + Convert.appendStringSize("", String.valueOf(body.syss) , 1, 10, 10, 0) + "\r\n");
				sb.append("基本水费:" + Convert.appendStringSize("", body.sfhj + "", 1, 10, 10, 0));
				sb.append("违 约 金:" + Convert.appendStringSize("", String.valueOf(body.znj) , 1, 10, 10, 0) + "\r\n");
			}
			
			if (v.size() > 0) sb.append("                        \r\n");
			sb.append("实缴金额:" + Convert.appendStringSize("", saleHead.ysje +"" , 1, 10, 10, 0));
			sb.append("总用水量:" +  Convert.appendStringSize("", saleHead.num5 +"" , 1, 10, 10, 0) + "\r\n");	
			sb.append("本期节余:" + Convert.appendStringSize("", saleHead.num4 +"" , 1, 10, 10, 0));			
			sb.append("上期节余:" + Convert.appendStringSize("", saleHead.num3 +"" , 1, 10, 10, 0) + "\r\n");	
			
			if (saleHead.bcjf > 0 || saleHead.ljjf > 0)
			{	
				sb.append("积分卡号:" + Convert.appendStringSize("", saleHead.jfkh +"" , 1, 10, 10, 0) + "\r\n");
				sb.append("本次积分:" + Convert.appendStringSize("", saleHead.bcjf +"" , 1, 10, 10, 0));
				sb.append("累计积分:" + Convert.appendStringSize("", saleHead.ljjf +"" , 1, 10, 10, 0) + "\r\n");
			}
			
			for(int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef pay = (SalePayDef) salePayment.get(i); 
				sb.append("付款名称:" + Convert.appendStringSize("", pay.payname , 1, 10, 10, 0));
				sb.append("     " + Convert.appendStringSize("", pay.je +"" , 1, 10, 10, 0) + "\r\n");
				
				if (!"".equals(pay.payno) && ( pay.kye > 0 || pay.paycode.startsWith("04")) )
				{
					sb.append("账户余额:" + Convert.appendStringSize("", pay.payno , 1, 10, 10, 0));
					sb.append("     " + Convert.appendStringSize("", pay.kye +"" , 1, 10, 10, 0) + "\r\n");
				}
			}
		
			
			sb.append("打印时间: ");// bcjf  ljjf
			sb.append(ManipulateDateTime.getCurrentDateTime() + "\r\n");

			sb.toString();

			if (PathFile.fileExist(GlobalVar.HomeBase + "\\wfeeprn.txt"))
				PathFile.deletePath(GlobalVar.HomeBase + "\\wfeeprn.txt");

			pw = CommonMethod.writeFileAppendGBK(GlobalVar.HomeBase + "\\wfeeprn.txt");

			if (pw != null)
			{
				pw.println(sb);
				pw.flush();
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
			if (pw != null)
				pw.close();
		}
	}

	public void printBill()
	{

		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{
			String printName = "";

			if (!PathFile.fileExist(GlobalVar.HomeBase + "\\wfeeprn.txt"))
			{
				new MessageBox("未发现打印文件!");
				return;
			}
			else
			{
				printName = GlobalVar.HomeBase + "\\wfeeprn.txt";
			}

			pb = new ProgressBox();
			pb.setText("正在打印水费缴费单,请稍等...");

			Printer.getDefault().startPrint_Normal();

			// 打印两份
			for (int i = 0; i < 1; i++)
			{
				br = CommonMethod.readFileGBK(printName);

				if (br == null)
				{
					new MessageBox("打开" + printName + "打印文件失败!");
					return;
				}

				String line = null;
				int num = 0; //记录打印的行数，当使用定长模板时，保证每张小票的长度是定长的

				while ((line = br.readLine()) != null)
				{
					Printer.getDefault().printLine_Journal(line);
					num++;
				}

				//得到当前打印行在分页打印中最后一页的位置，然后走纸到当前页的尾部
				if (SaleBillMode.getDefault().getPagePrint() == 1 && SaleBillMode.getDefault().getPageFeet() > 0)
				{
					num = num % SaleBillMode.getDefault().getPageFeet();
					while(num > 0 && num < SaleBillMode.getDefault().getPageFeet() )
					{
						Printer.getDefault().printLine_Journal("\n");
						num ++;
					}
				}
				else if (SaleBillMode.getDefault().getPagePrint() != 1)
				{
					num = 0;
					while(num < 6 )
					{
						Printer.getDefault().printLine_Journal("\n");
						num ++;
					}
				}

				br.close();
				br = null;
			}
			// 不走纸，发票打印和非发票打印不好控制
//			Printer.getDefault().cutPaper_Journal();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
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
			if (pb != null)
			{
				pb.close();
			}
		}

	}
}
