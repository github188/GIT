package custom.localize.Bjsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import net.futurn.IFuturnService;
import net.futurn.entity.SaleDetailEntity;
import net.futurn.entity.SaleEntity;
import net.futurn.entity.SalesResultEntity;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.SaleShowAccountForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Bjsp_SaleBS extends Cmls_SaleBS
{

	private Vector saleGoods1 = new Vector(); // 商品信息

	private Vector goods1 = new Vector();

	private Vector goodsPop1 = new Vector();

	private Vector goodsSpare1 = new Vector();

	private SaleHeadDef saleHead1 = new SaleHeadDef();

	private int[] saleIDv = new int[10];

	private String result = null;

	public Vector cardIdv = new Vector();
	//boolean isread = false;
	int keycount = 0;

	//private byte[] lock = new byte[0];
	public void execCustomKey(boolean keydownonsale, int key)
	{
		//	synchronized(lock) 
		//	{
		//		try
		//		{
		//			keycount++;
		//			long time = Math.round(Math.random()*(1000-100)+100);
		//			Thread.sleep(time);
		//			if (keycount > 1)
		//			{
		//new MessageBox("请不要连续按刷卡键!");
		//System.out.println("请不要连续按刷卡键!");
		//				return; 
		//			}

		//System.out.println("start execCustomKey saleGoods.size():" + saleGoods.size() + " keycount:" + keycount + " time:" + time);

		//if (isread) return;

		//isread = true;

		if (key == 103)
		{
			//	this.readCardRFID();
		}
		if (key == 108)
		{
			this.readCardRFIDOnline(key);
		}
		if (key == 109)
		{
			this.readCardRFIDOnline(key);
		}

		//System.out.println("end execCustomKey saleGoods.size():" + saleGoods.size() + " keycount:" + keycount + " time:" + time);
		//		}
		//		catch(Exception ex)
		//		{
		//			new MessageBox(ex.getMessage());
		//		}
		//		finally
		//		{
		//			//isread = false;
		//			keycount = 0;
		//		}
		//	}
	}

	public void readCardRFID()
	{
		// 已有商品不允许刷卡
		if (saleGoods.size() > 0)
		{
			new MessageBox("已有商品，不允许刷卡！");
			return;
		}

		/*
		 * if (saleEvent.yyyh.getText().trim().equals("") ||
		 * saleEvent.yyyh.getText() == null) { new MessageBox("请输入营业员号！");
		 * return; }
		 */

		// 先初始化交易
		initNewSale();
		new Bjsp_SaleBS();
		new MessageBox("请刷IC卡...");
		saleIDv = createSalegoodsFormCard();
		if (saleIDv[0] == -1)
		{// 从卡上获取销售单信息
			new MessageBox("获取卡数据失败！请重试");
			return;
		}
		else if (saleIDv[0] == 0)
		{
			new MessageBox("此IC卡已付款 \r\n或无销售单");
			return;
		}
		else if (saleIDv[0] == 0) { return; }

		// 读取交易对象
		//	saleGoods.clear();
		//	goodsAssistant.clear();
		//	crmPop.clear();
		//	goodsSpare.clear();

		saleGoods.addAll(saleGoods1);
		saleHead = saleHead1;
		crmPop.addAll(goodsPop1);
		goodsAssistant.addAll(goods1);
		goodsSpare.addAll(goodsSpare1);

		/*
		 // 读取交易对象
		 saleGoods = saleGoods1;
		 saleHead = saleHead1;
		 goodsAssistant = goods1;
		 crmPop = goodsPop1;
		 goodsSpare = goodsSpare1;
		 */

		// 刷新数据
		refreshSaleData();

		// 计算应付金额
		calcHeadYfje();

		// 刷新界面显示
		saleEvent.updateSaleGUI();

		// 焦点到编码输入框
		if (saleGoods.size() > 0 && GlobalInfo.syjDef.issryyy == 'Y')
		{
			// SaleGoodsDef g = (SaleGoodsDef)
			// saleGoods.elementAt(saleGoods.size() - 1);
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		// 检查是否存在付款
		if (salePayment.size() > 0)
		{
			// 先清除全部付款对象列表
			payAssistant.removeAllElements();

			// 根据付款信息创建付款对象
			SalePayDef sp = null;
			for (int i = 0; i < salePayment.size(); i++)
			{
				sp = (SalePayDef) salePayment.elementAt(i);

				// 创建付款对象
				Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
				if (pay == null)
				{
					// 放弃所有已付款
					salePayment.removeAllElements();
					payAssistant.removeAllElements();
					return;
				}
				// 增加已付款
				payAssistant.add(pay);
			}

		}
		return;
	}

	public int getMemberInputMode()
	{
		return TextBox.MsrInput;
	}

	public int[] createSalegoodsFormCard()
	{
		saleGoods1.clear();
		goods1.clear();
		goodsPop1.clear();
		goodsSpare1.clear();

		int[] n = new int[10];
		result = this.getRFIDcard(1); // 1---读卡内容  2---读卡号

		if (result == null || result.trim().indexOf("error") > -1)
		{
			if (result != null) new MessageBox(result);
			n[0] = -2;
			return n;
		}

		int[] order = this.getHex(result);
		/*
		 * byte[] b = this.getByte(order); MD5_Encoding md5 = new
		 * MD5_Encoding(); String md5Code = md5.getMD5ofStr(b);
		 * 
		 * if(!(md5Code.substring(0, 4).equals(result.trim().substring(0, 4)))){
		 * new MessageBox("MD5效验错误！"); n[0] = -2; return n; }
		 */
		if (order.length <= 0)
		{
			n[0] = -1;
			return n;
		}

		int start = 4;
		n[0] = 0; // 记录小票号起始位置
		double hjzje = 0;
		int zsl = 0;
		int js = 0;

		while (true)
		{
			int yyyh = this.getReadInt2(order, start); // 营业员号
			String yyyh1 = String.valueOf(yyyh);
			for (int y = yyyh1.length(); y < 4; y++)
			{
				yyyh1 = "0" + yyyh1;
			}
			int code = this.getReadInt(order, start + 2); // 销售编码--系统商品码
			String code1 = String.valueOf(code);
			for (int y = code1.length(); y < 6; y++)
			{
				code1 = "0" + code1;
			}

			int id = this.getReadInt(order, start + 6); // 销售单id
			String id1 = String.valueOf(id);
			for (int y = id1.length(); y < 9; y++)
			{
				id1 = "0" + id1;
			}
			if (code == 0) break;
			String saleID = this.getReadChar(order, start + 10); // 流水号--小票号
			if (!saleID.trim().equals("000000000000"))
			{
				break;
			}

			n[js] = start + 10;
			++js;
			int goodsNum = order[start + 22]; // 商品明细数
			start = start + 23;

			for (int i = 1; i <= goodsNum; i++)
			{
				// int saleDetailId = this.getReadInt(order,start);
				Bjsp_SaleBS sb = new Bjsp_SaleBS();
				//	String counterNo = "";
				String kh = sb.hexToCn(order, start + 4, 50).trim(); // 款号
				String sm = sb.hexToCn(order, start + 54, 30).trim(); // 色码
				String cm = sb.hexToCn(order, start + 84, 30).trim(); // 尺码

				double dj = this.getReadInt(order, start + 114);
				dj = ManipulatePrecision.mul(dj, 0.01);
				int sl = order[start + 118]; // 数量
				// new MessageBox("数量:"+ sl + "字节：" +(start + 116) );
				start = start + 120;

				zsl = zsl + sl;
				hjzje = ManipulatePrecision.doubleConvert(hjzje + dj * sl);
				SaleGoodsDef sgd = new SaleGoodsDef();
				
				StringBuffer buff1 = new StringBuffer();
				GoodsDef gd= findGoodsInfo(code1, "", "","",false,buff1,true);
				if (gd == null) 
				{
					new MessageBox("商品编码"+code1+"没有找到");
					return n;
				}
				
				SpareInfoDef sid = new SpareInfoDef();
				sgd = creatSaleGoods(gd, kh, sm, cm, dj, sl, id1, yyyh1,0);
				
				sid.str1 = "   ";
				saleGoods1.add(sgd);
				goods1.add(gd);
				goodsPop1.add(new GoodsPopDef());
				goodsSpare1.add(sid);
			}
		}
		saleHead1 = creatsaleHead(hjzje, zsl, "BJSPIC");
		return n;

	}

	private int getReadInt2(int[] i, int n)
	{
		if ((i[n] | i[n + 1] | i[n + 2] | i[n + 3]) < 0) new MessageBox("读取数据失败！");
		return ((i[n + 1] << 8) + (i[n] << 0));
	}

	private byte[] getByte(int[] order)
	{
		byte[] b = new byte[order.length - 2];
		for (int i = 2; i < order.length; i++)
		{
			Integer a = new Integer(order[i]);
			b[i - 2] = a.byteValue();
		}
		return b;
	}

	private String getReadChar(int[] order, int i)
	{
		String s = "";
		for (int n = i; n < i + 12; n++)
		{
			if (order[n] == 48)
			{
				s = s + "0";
			}
			// new MessageBox("字节："+n+"\r\n内容："+s);
		}
		return s;
	}

	public boolean payComplete()
	{
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("payComplete()" );

		// 检查付款是否足够
		if (!comfirmPay() || calcPayBalance() > 0 || (saleHead.sjfk <= 0 && GlobalInfo.sysPara.issaleby0 != 'Y'))
		{
			new MessageBox("付款金额不足!");
			return false;
		}
		String a = saleHead.str1;
		// 付款完成处理
		if (!payCompleteDoneEvent()) return false;

		// 找零处理
		PaymentChange pc = calcSaleChange();
		if (pc == null)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			return false;
		}

		// 付款确认
		new SaleShowAccountForm().open(saleEvent.saleBS);
		if (saleFinish && a.equals("BJSPIC"))
		{
			// if(true){
			boolean b = setRFIDcard();
			if (!b)
			{
				new MessageBox("付款完成，写卡失败！");
			}
		}
		cardIdv.clear();
		// 恢复状态，允许再次触发最后交易完成方法
		waitlab = false;
		// 如果付款完成 且 存在销售单文件 则记录小票号

		// 交易未成功
		if (!saleFinish)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			// 清除找零
			pc.clearChange();
		}
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("payComplete()-saleFinish" );
		return saleFinish;
	}

	private SaleHeadDef creatsaleHead(double hjzje, int zsl, String flag)
	{
		SaleHeadDef saleHead1 = new SaleHeadDef();
		saleHead1.hjzje = hjzje;
		saleHead1.ysje = hjzje;
		saleHead1.hjzsl = zsl;
		saleHead1.sqktype = '1';
		saleHead1.netbz = 'N';
		saleHead1.printbz = 'N';
		saleHead1.hcbz = 'N';
		saleHead1.hhflag = 'N';
		saleHead1.str1 = flag;
		return saleHead1;
	}

/*	private GoodsDef creatGoods(String code, String kh, String sm, String cm, double jg, int sl)
	{
		GoodsDef gd = new GoodsDef();
		gd.barcode = code;
		gd.catid = code;
		gd.fxm = code;
		gd.type = '1';
		return gd;
	}*/

	private SaleGoodsDef creatSaleGoods(GoodsDef gdf, String str, String str1, String str2, double dj, int num, String id, String yyyh, double zk)
	{
		
		SaleGoodsDef sgd=goodsDef2SaleGoods(gdf, yyyh, num, dj, 0, false);
		
		sgd.yhzke = zk;
		sgd.hjzk = zk;
		sgd.unit = "件";
	
		sgd.flag = '4';
		sgd.fph = id;
		sgd.fphm = saleHead.fphm;
		sgd.name = str + "/" + str1 + "/" + str2;
		sgd.inputbarcode = gdf.code;
		sgd.isvipzk = 'Y';
		sgd.yyyh = yyyh;

		return sgd;
	}

	// 输入卡号 从网上取销售单
	public Vector createSalegoodsFormWeb(String cardId)
	{
		Vector v = new Vector();

		return v;
	}

	public int getReadInt(int[] i, int n)
	{
		if ((i[n] | i[n + 1] | i[n + 2] | i[n + 3]) < 0) new MessageBox("读取数据失败！");
		return ((i[n + 3] << 24) + (i[n + 2] << 16) + (i[n + 1] << 8) + (i[n] << 0));
	}

	// a 开始位 c 长度（字节）
	private String hexToCn(int[] b, int a, int c)
	{
		String hs = null;
		String stmp;
		int j = 0;
		String temp = "";
		for (int n = a; n < a + c; n++)
		{
			++j;
			stmp = Integer.toHexString(b[n]);
			if (j == 1)
			{
				temp = stmp;
			}
			else
			{
				hs = hs + "\\u" + stmp + temp;
				j = 0;
			}
		}
		String[] strs = hs.split("\\\\u");
		String returnStr = "";
		// 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""
		for (int i = 1; i < strs.length; i++)
		{
			returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
		}
		return returnStr;
	}

	public boolean setRFIDcard()
	{
		// 先删除上次交易数据文件
		if (PathFile.fileExist("request.txt"))
		{
			PathFile.deletePath("request.txt");

			if (PathFile.fileExist("request.txt"))
			{
				new MessageBox("读卡请求文件request.txt无法删除,请重试");
				return false;
			}
		}

		if (PathFile.fileExist("c://javapos//result.txt"))
		{
			PathFile.deletePath("c://javapos//result.txt");

			if (PathFile.fileExist("c://javapos//result.txt"))
			{
				new MessageBox("读卡结果文件result.txt无法删除,请重试");
				return false;
			}
		}
		StringBuffer line = new StringBuffer();
		line = line.append("2,15,115200,-1,ffffffffffff,0,,,10,50");

		String temp = "";
		if (String.valueOf(saleHead1.fphm).length() < 8)
		{
			temp = String.valueOf(saleHead1.fphm);
			for (int i = String.valueOf(saleHead1.fphm).length(); i < 8; i++)
			{
				temp = "0" + temp;
			}
		}

		String saleId = toHexString(saleHead1.syjh + temp); // 收银流水号=收银机号+小票号

		for (int i = 0; i <= saleIDv.length; i++)
		{
			if (saleIDv[i] == 0) break;
			line.append("," + saleIDv[i] + ",12," + saleId);
			String a = result.substring(0, saleIDv[i] * 2);
			String b = result.substring(saleIDv[i] * 2 + 24);
			result = a + saleId + b;
		}
		// result = "00000000";
		if (result == null) { return false; }
		int[] order = this.getHex(result);
		byte[] b = this.getByte(order);
		MD5_Encoding md5 = new MD5_Encoding();
		String md5Code = md5.getMD5ofStr(b);

		line.append("," + "0," + "2," + md5Code.substring(0, 4)); // MD5效验码
		// 前2个字节
		// 写入请求
		PrintWriter pw = CommonMethod.writeFile("c://javapos//request.txt");
		pw.write(line.toString());
		pw.close();
		String str = null;
		boolean b1 = false;
		while (true)
		{
			str = callBankExe();
			if (str == null || str.trim().length() == 0)
			{
				b1 = true;
				break;
			}
			if (str.trim().equals("1")) return false;
			if (new MessageBox("写卡错误：\r\n" + str + "\r\n 是否重新写卡？", null, true).verify() == GlobalVar.Key2)
			{
				break;
			}
		}
		return b1;
	}

	private String callBankExe()
	{
		if (PathFile.fileExist("c://javapos//result.txt"))
		{
			PathFile.deletePath("c://javapos//result.txt");

			if (PathFile.fileExist("c://javapos//result.txt"))
			{
				new MessageBox("读卡结果文件result.txt无法删除,请重试");
				return "1";
			}
		}
		// 调用接口模块
		if (PathFile.fileExist("c://javapos//javaposbank.exe"))
		{
			try
			{
				CommonMethod.waitForExec("c://javapos//javaposbank.exe MWURF35LT1");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			new MessageBox("找不到IC卡模块 javaposbank.exe");
			return "1";
		}
		// 读取应答
		BufferedReader br = null;
		if (!PathFile.fileExist("c://javapos//result.txt") || ((br = CommonMethod.readFileGBK("c://javapos//result.txt")) == null))
		{
			new MessageBox("读取卡号应答数据失败!");
			return "1";
		}
		String cardno = null;
		try
		{
			cardno = br.readLine();
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return cardno;
	}

	public String getRFIDcard(int i)
	{
		// 先删除上次交易数据文件
		if (PathFile.fileExist("c://javapos//request.txt"))
		{
			PathFile.deletePath("c://javapos//request.txt");

			if (PathFile.fileExist("c://javapos//request.txt"))
			{
				new MessageBox("读卡请求文件request.txt无法删除,请重试");
				return null;
			}
		}

		if (PathFile.fileExist("c://javapos//result.txt"))
		{
			PathFile.deletePath("c://javapos//result.txt");

			if (PathFile.fileExist("c://javapos//result.txt"))
			{
				new MessageBox("读卡结果文件result.txt无法删除,请重试");
				return null;
			}
		}

		StringBuffer line = new StringBuffer();
		if (i == 1)
		{
			line = line.append("1,15,115200,-1,ffffffffffff,0,,,10,50");
		}
		if (i == 2)
		{
			line = line.append("3,15,115200,-1,ffffffffffff,0,,,10,50");
		}

		// 写入请求
		PrintWriter pw = CommonMethod.writeFile("c://javapos//request.txt");
		pw.write(line.toString());
		pw.close();

		// 调用接口模块
		if (PathFile.fileExist("c://javapos//javaposbank.exe"))
		{
			try
			{
				CommonMethod.waitForExec("c://javapos//javaposbank.exe MWURF35LT1");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			new MessageBox("找不到IC卡模块 javaposbank.exe");
			return null;
		}

		// 读取应答
		BufferedReader br = null;
		if (!PathFile.fileExist("c://javapos//result.txt") || ((br = CommonMethod.readFileGBK("c://javapos//result.txt")) == null))
		{
			new MessageBox("读取卡号应答数据失败!");
			return null;
		}
		String cardno = null;
		try
		{
			cardno = br.readLine();
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return cardno;

	}

	// 十六进制字符串转十进制数组
	private int[] getHex(String s)
	{

		int len = s.length() / 2;
		int i[] = new int[len];

		for (int j = 0; j <= (s.length() - 2); j = j + 2)
		{
			String a = s.substring(j, j + 2);
			i[j / 2] = Integer.parseInt(a, 16);
		}
		return i;
	}

	// 字节数组转十六进制字符串
	public String getHexString(byte[] b) throws Exception
	{

		String result = "";

		for (int i = 0; i < b.length; i++)
		{

			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);

		}
		return result;

	}

	public String toHexString(String s)
	{
		String str = "";
		for (int i = 0; i < s.length(); i++)
		{
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	public int querySalesByCardT(String cardId)
	{
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("网上查询销售单：" + cardId);

		try
		{
			saleGoods1.clear();
			goods1.clear();
			goodsPop1.clear();
			goodsSpare1.clear();

			/*
			 * StringBuffer buff = new StringBuffer(); TextBox a = new
			 * TextBox(); a.open("请输入卡号", "卡号", "请输入销售单号", buff, 0, 0, false,
			 * TextBox.AllInput); String cardId = buff.toString();
			 */

			double hjzje = saleHead.hjzje;
			int zsl = saleHead.hjzsl;

			for (int i = 0; i < 3; i++)
			{
				int id = Convert.toInt("");
				String id1 = String.valueOf(id);
				for (int y = id1.length(); y < 9; y++)
				{
					id1 = "0" + id1;
				}
				//new MessageBox("id=" + id1);
				int yyyh = Convert.toInt("0808"); // web上取得营业员号
				String yyyh1 = String.valueOf(yyyh);
				for (int y = yyyh1.length(); y < 4; y++)
				{
					yyyh1 = "0" + yyyh1;
				}

				for (int j = 0; j < 4; j++)
				{
					int code = Convert.toInt("1100001");
					String code1 = String.valueOf(code);

				//	for (int y = code1.length(); y < 6; y++)
					{
				//		code1 = "0" + code1;
					}
					//	new MessageBox("销售编码："+code1);
					String kh = "01";
					String sm = "02";
					String cm = "L";
					Double dj1 = Double.valueOf(10090.0);
					Integer sl1 = Integer.valueOf(1);

					double dj = dj1.doubleValue();
					int sl = sl1.intValue();

					zsl = zsl + sl;
					hjzje = ManipulatePrecision.doubleConvert(hjzje + dj * sl);

					SaleGoodsDef sgd = new SaleGoodsDef();
					
					SpareInfoDef sid = new SpareInfoDef();
					StringBuffer buff1 = new StringBuffer();
					GoodsDef gd= findGoodsInfo(id1, yyyh1, "","",false,buff1,true);
					if (gd == null) 
					{
						new MessageBox("商品编码"+id1+"没有找到");
						return -3	;
					}
					sgd = creatSaleGoods(gd, kh, sm, cm, dj, sl, id1, yyyh1,0);
					if (sgd == null) return -3;
					
					sid.str1 = "   ";
					saleGoods1.add(sgd);
					goods1.add(gd);
					goodsPop1.add(new GoodsPopDef());
					goodsSpare1.add(sid);

					//写入工作日志
					AccessDayDB.getDefault().writeWorkLog("cardId=" + cardId + "; id=" + id + "; code=" + code1 + "; dj=" + dj + "; sl=" + sl);
				}
			}
			saleHead1 = creatsaleHead(hjzje, zsl, "CARDRFID");

			//System.out.println("querySalesByCard saleGoods1:" + saleGoods1.size());
			return 0;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	
		
	}
	
	public int querySalesByCard(String cardId)
	{
//		写入工作日志
		writeLog("网上查询销售单：" + cardId);
		AccessDayDB.getDefault().writeWorkLog("网上查询销售单：" + cardId);
		if (!(new File(GlobalVar.ConfigPath + "//BJSPWebservice.ini").exists())){
			new MessageBox("BJSPWebservice.ini文件不存在!");
			return -3;
		}

		BufferedReader br;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//BJSPWebservice.ini");
		if (br == null){
			writeLog("文件内容为空,找不到URL地址!");
			new MessageBox("文件内容为空,找不到URL地址!");
			return -3;
		}

		String line = "";
		try
		{
			if ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					writeLog("找不到URL地址!");
					new MessageBox("找不到URL地址!");
					return -3;
				}
			}
		}
		catch (IOException e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		try
		{
			saleGoods1.clear();
			goods1.clear();
			goodsPop1.clear();
			goodsSpare1.clear();

			/*
			 * StringBuffer buff = new StringBuffer(); TextBox a = new
			 * TextBox(); a.open("请输入卡号", "卡号", "请输入销售单号", buff, 0, 0, false,
			 * TextBox.AllInput); String cardId = buff.toString();
			 */
			
			String extStr = "";
			if (SellType.ISBACK(saletype))
			{
				extStr = "refunds";
			}
			writeLog("中间件调用start");
			Service serviceModel = (Service) new ObjectServiceFactory().create(IFuturnService.class);

			XFireProxyFactory factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());

			//String url = "http://192.168.200.171/oms_sap/services/futurnService";

			IFuturnService service = (IFuturnService) factory.create(serviceModel, line);
			SalesResultEntity entity = service.querySalesByCard(cardId,extStr);
			/*
			if (SellType.ISBACK(saletype))
			{
				extStr = "refunds";
			}
			IFuturnService service = new FuturnServiceImpl();
			SalesResultEntity entity = service.querySalesByCard(cardId, extStr);*/
			if (entity == null) { 
				writeLog("中间件调用end ，entity == null");
				return -1; 
				}
			//--System.out.println("code = " + entity.getInfo().getCode());
//			--System.out.println("codeInfo = " + entity.getInfo().getCodeInfo());
			writeLog("中间件调用end");
			
			double hjzje = saleHead.hjzje;//目前是折后，换为折前
			double hjzke = 0;//wangyong add by 2014.10.15
			int zsl = saleHead.hjzsl;
			if(entity.getSales()==null)
			{
				writeLog("entity.getSales()=null没有取到商品明细");
				new MessageBox("没有取到商品明细!");
				return -2; 
			}
			writeLog("entity.getSales().size()=[" + entity.getSales().size() + "]");
			for (int i = 0; i < entity.getSales().size(); i++)
			{
				try
				{
					SaleEntity saleEntity = (SaleEntity) entity.getSales().get(i);
					if (saleEntity==null)
					{
						writeLog("i=[" + i + "],saleEntity=null");
						continue;
					}
					writeLog("i=[" + i + "],saleEntity.getSaleDetails().size()=[" + saleEntity.getSaleDetails().size() + "]");
					//int id = Convert.toInt(saleEntity.getSaleCode());
					//String id1 = String.valueOf(id);
					String id1 ="";//=saleEntity.getSaleCode();
				/*	for (int y = id1.length(); y < 9; y++)
					{
						id1 = "0" + id1;
					}
					*/
					int sidd = Convert.toInt(saleEntity.getSid());
					String sidd1 = String.valueOf(sidd);
					//new MessageBox("id=" + id1);
//					--System.out.println("SaleDetailsNum()=" + saleEntity.getSaleDetailsNum());
					int yyyh = Convert.toInt(saleEntity.getSaleUser()); // web上取得营业员号
					String yyyh1 = String.valueOf(yyyh);
					for (int y = yyyh1.length(); y < 4; y++)
					{
						yyyh1 = "0" + yyyh1;
					}
					writeLog("saleEntity.getSaleDetails() start");
					for (int j = 0; j < saleEntity.getSaleDetails().size(); j++)
					{
						try
						{
							SaleDetailEntity saleDetail = (SaleDetailEntity) saleEntity.getSaleDetails().get(j);
							if (saleDetail==null)
							{
								writeLog("j=[" + j + "],saleDetail=null");
								continue;
							}
							writeLog("j=[" + j + "]");
							
							String code =  saleDetail.getSaleCode() ;
							writeLog("j code=[" + code + "]");
//							--System.out.println("code=" + code);
							//new MessageBox(String.valueOf(code));
							String code1 = String.valueOf(code);
							id1=code1;//add 2015.3.5
		/*
							for (int y = code1.length(); y < 6; y++)
							{
								code1 = "0" + code1;
							}
							*/
							//	new MessageBox("销售编码："+code1);
							String kh = saleDetail.getProSku();
							String sm = saleDetail.getProColor();
							String cm = saleDetail.getProSize();
							Double dj1 = saleDetail.getSellprice();//（新字段）原价
							             //saleDetail.getSalePrice();（旧字段）的折后价
							Integer sl1 = saleDetail.getSaleSum();
							
							
							Double zk1 = saleDetail.getSellzk();//（新字段）折扣
							double zk=zk1.doubleValue();
							
							//new MessageBox(id1+" "+kh+" "+sm);
							double dj = dj1.doubleValue();
							int sl = sl1.intValue();

							zsl = zsl + sl;
							hjzke = hjzke + zk;
							hjzje = ManipulatePrecision.doubleConvert(hjzje + dj * sl);
//							写入工作日志
							
							AccessDayDB.getDefault().writeWorkLog("cardId=" + cardId + "; id=" + id1 + "; code=" + code1 + "; dj=" + dj + "; sl=" + sl+"; sid="+sidd + "; zk=" + zk);
							writeLog("cardId=" + cardId + "; id=" + id1 + "; code=" + code1 + "; dj=" + dj + "; sl=" + sl+"; sid="+sidd + "; zk=" + zk);
							
							SaleGoodsDef sgd = new SaleGoodsDef();
							
							SpareInfoDef sid = new SpareInfoDef();
							StringBuffer buff1 = new StringBuffer();
							
							GoodsDef gd= findGoodsInfo(id1, yyyh1, "","",false,buff1,false);
							
							if (gd == null) 
							{
								//--System.out.println("debug7"  + "商品编码"+id1+"没有找到");
								writeLog("debug7"  + "商品编码"+id1+"没有找到");
								new MessageBox("商品编码"+id1+"没有找到");
								return -3	;
							}
							sgd = creatSaleGoods(gd, kh, sm, cm, dj, sl, id1, yyyh1, zk);
							sgd.batch = sidd1;
							if (sgd == null) return -3;
							
							sid.str1 = "   ";
							saleGoods1.add(sgd);
							goods1.add(gd);
							goodsPop1.add(new GoodsPopDef());
							goodsSpare1.add(sid);

							//写入工作日志
							AccessDayDB.getDefault().writeWorkLog("cardId=" + cardId + "; id=" + id1 + "; code=" + code1 + "; dj=" + dj + "; sl=" + sl+"; sid="+sidd);
							writeLog("cardId=" + cardId + "; id=" + id1 + "; code=" + code1 + "; dj=" + dj + "; sl=" + sl+"; sid="+sidd);
						}
						catch(Exception ex)
						{
							writeLog("j=[" + j + "] ex");
							writeLog(ex);
							new MessageBox("商品" + id1 + "解析异常");
							return -999;
						}
						
					}
				}
				catch(Exception ex)
				{
					writeLog("i=[" + i + "] ex");
					writeLog(ex);
					new MessageBox("商品数据解析异常");
					return -999;
				}
				
			}
			saleHead1 = creatsaleHead(hjzje, zsl, "CARDRFID");
			saleHead1.yhzke = hjzke;
			saleHead1.hjzke = hjzke;
			saleHead1.djlb = saletype;
			saleHead1.ysje = ManipulatePrecision.doubleConvert(saleHead1.hjzje - saleHead1.hjzke, 2, 1);

			//System.out.println("querySalesByCard saleGoods1:" + saleGoods1.size());
			return 0;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			System.out.println(er.getStackTrace());
			System.out.println(er.getMessage());
			new MessageBox("读取数据异常，请重试！\n" + er.getMessage());
			return -999;
		}
	}

	public void readCardRFIDOnline(int key)
	{
		// 已有商品不允许刷卡

		// 先初始化交易
		int cSize = cardIdv.size();
		if (cSize == 0)
		{
			initNewSale();
		}

		new Bjsp_SaleBS();
		String cardId = "";

		//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("获取卡号方式：" + key);

		if (key == 108)
		{
			StringBuffer buff = new StringBuffer();
			TextBox a = new TextBox();
			a.open("请输入卡号", "卡号", "请输入销售单号", buff, 0, 0, false, TextBox.AllInput);
			cardId = buff.toString();
//			写入工作日志
			AccessDayDB.getDefault().writeWorkLog("输入卡号：" + cardId);

		}
		else if (key == 109)
		{
			writeLog("请刷IC卡，从网上获取销售单...");
			new MessageBox("请刷IC卡，从网上获取销售单...");
			String str = getRFIDcard(2);
			if (str == null || str.indexOf("error") != -1)
			{
				writeLog("读卡错误：" + str);
				new MessageBox("读卡错误：" + str);
				return;
			}
//			写入工作日志
			writeLog("刷卡号：" + str);
			AccessDayDB.getDefault().writeWorkLog("刷卡号：" + str);

			Long i = Long.valueOf(str.substring(0, 8), 16);

			cardId = String.valueOf(i);
			for (int a = cardId.length(); a < 10; a++)
			{
				cardId = "0" + cardId;
			}
		}

		if (cardId.equals(""))
		{
			writeLog("获取卡号失败！");
			new MessageBox("获取卡号失败！");
			return;
		}
		int n = 0;
		while (n < cSize)
		{
			if (cardIdv.elementAt(n).equals(cardId))
			{
				writeLog("此卡信息已录入");
				new MessageBox("此卡信息已录入");
				return;
			}
			n++;
		}

		int qsbc = querySalesByCard(cardId);
		//int qsbc = querySalesByCardT(cardId);
//		写入工作日志
		writeLog("访问服务器返回值：" + qsbc);
		AccessDayDB.getDefault().writeWorkLog("访问服务器返回值：" + qsbc);

		if (qsbc == -1)
		{
			writeLog("卡号" + cardId + "为空 \r\n或者访问服务器错误！");
			new MessageBox("卡号" + cardId + "为空 \r\n或者访问服务器错误！");
			return;
		}
		if (qsbc == -3 || qsbc == -2 || qsbc==-999) return;

				
		// 读取交易对象
		if (cardIdv.size() == 0)
		{
			saleGoods.clear();
			goodsAssistant.clear();
			crmPop.clear();
			goodsSpare.clear();
		}

		cardIdv.add(cardId);

		//System.out.println("saleGoods:" + saleGoods.size());
		//System.out.println("saleGoods1:" + saleGoods1.size());

		saleGoods.addAll(saleGoods1);
		saleHead = saleHead1;
		crmPop.addAll(goodsPop1);
		goodsAssistant.addAll(goods1);
		goodsSpare.addAll(goodsSpare1);
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("数据赋值" );

		/*
		 saleGoods.add(saleGoods1.toArray());
		 saleHead = saleHead1;
		 goodsAssistant.add(goods1.toArray());
		 crmPop.add(goodsPop1.toArray());
		 goodsSpare.add(goodsSpare1.toArray());
		 */

		/*
		 saleGoods = saleGoods1;
		 saleHead = saleHead1;
		 goodsAssistant = goods1;
		 crmPop = goodsPop1;
		 goodsSpare = goodsSpare1;
		 */

		// 刷新数据
		refreshSaleData();
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("刷新数据" );

		// 计算应付金额
		calcHeadYfje();
		//退货类型&
		//	if()

		// 刷新界面显示
		saleEvent.updateSaleGUI();
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("刷新界面" );

		// 焦点到编码输入框
		if (saleGoods.size() > 0 && GlobalInfo.syjDef.issryyy == 'Y')
		{
			// SaleGoodsDef g = (SaleGoodsDef)
			// saleGoods.elementAt(saleGoods.size() - 1);
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		// 检查是否存在付款
		if (salePayment.size() > 0)
		{
			// 先清除全部付款对象列表
			payAssistant.removeAllElements();

			// 根据付款信息创建付款对象
			SalePayDef sp = null;
			for (int i = 0; i < salePayment.size(); i++)
			{
				sp = (SalePayDef) salePayment.elementAt(i);

				// 创建付款对象
				Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
				if (pay == null)
				{
					// 放弃所有已付款
					salePayment.removeAllElements();
					payAssistant.removeAllElements();
					return;
				}
				// 增加已付款
				payAssistant.add(pay);
			}

		}
//		写入工作日志
		AccessDayDB.getDefault().writeWorkLog("读取网上销售单成功" );

		return;
	}

	public boolean clearSell(int index)
	{
		// 先取消VIP或临时折扣
		if (cancelMemberOrGoodsRebate(index)) { return true; }

		if (saleGoods.size() <= 0)
		{
			// 退货交易切换回销售交易
			if (SellType.ISBACK(saletype))
			{
				djlbBackToSale();
			}

			//
			initOneSale(this.saletype);

			return true;
		}

		if (new MessageBox("你确定要取消本笔交易输入吗?", null, true).verify() != GlobalVar.Key1) {

		return false;

		}

		// 没有取消权限
		String grantgh;

		if (operPermission(clearPermission, curGrant))
		{
			OperUserDef staff = clearSellGrant();

			if (staff == null) { return false; }

			grantgh = staff.gh;
		}
		else
		{
			grantgh = saleHead.syyh;
		}

		//
		if (!SellType.ISEXERCISE(this.saletype))
		{
			// 记录日志
			String log = "取消交易,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",金额:"
					+ Convert.increaseChar(ManipulatePrecision.doubleToString(saleHead.ysje), '0', 10) + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_CLEARSALE);

			// 记汇总
			SaleSummaryDef saleSummaryDef = new SaleSummaryDef();
			saleSummaryDef.zl = 0;
			saleSummaryDef.sysy = 0;
			saleSummaryDef.sjfk = 0;
			saleSummaryDef.zkje = 0;
			saleSummaryDef.ysje = 0;
			saleSummaryDef.qxbs = 1;
			saleSummaryDef.qxje = saleHead.ysje;

			// 写入全天销售统计
			saleSummaryDef.bc = '0';
			saleSummaryDef.syyh = "全天";
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);

			// 写入当班收银员销售统计
			saleSummaryDef.bc = saleHead.bc;
			saleSummaryDef.syyh = saleHead.syyh;
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);
		}

		// 退货交易切换回销售交易
		if (SellType.ISBACK(saletype))
		{
			djlbBackToSale();
		}
		cardIdv.clear();
		// 初始化新交易
		initOneSale(this.saletype);

		return true;
	}

	//	 获得挂单信息
	public boolean getHang(String invno)
	{
		try
		{
			// 先初始化交易
			if (GlobalInfo.sysPara.onlineGd.equals("Y")) initNewSale();

			// 设置挂单标志 
			isonlinegdjging = true;

			SaleHeadDef salegdhead = new SaleHeadDef();
			salegdhead.djlb = saleHead.djlb;

			Vector salegdgoods = new Vector();

			if (!DataService.getDefault().getSaleGdInfo(invno, salegdhead, salegdgoods))
			{
				new MessageBox("网上没有查找到当前挂单号!");
				return false;
			}

			if (!saletype.equals(salegdhead.djlb))
			{
				new MessageBox("此挂单必须在 " + SellType.getDefault().typeExchange(salegdhead.djlb, 'N', saleHead) + " 状态下才能解挂!");
				return false;
			}

			if (SellType.ISBACK(saletype) && salegdgoods.size() > 0 && ((SaleGoodsDef) salegdgoods.get(0)).yfphm > 0
					&& ((SaleGoodsDef) salegdgoods.get(0)).ysyjh.trim().length() > 0)
			{
				thFphm = ((SaleGoodsDef) salegdgoods.get(0)).yfphm;
				thSyjh = ((SaleGoodsDef) salegdgoods.get(0)).ysyjh;

				// 指定小票退货
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);
					sgd.fphm = saleHead.fphm;
					sgd.syjh = saleHead.syjh;
					sgd.rowno = saleGoods.size() + 1;

					sgd.hjzk = getZZK(sgd);

					// 重算商品应收
					sgd.hjje = ManipulatePrecision.doubleConvert(sgd.sl * sgd.jg, 2, 1);

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
					AccessDayDB.getDefault().writeWorkLog("商品：" + sgd.barcode + "金额：" + sgd.jg + "数量：" + sgd.sl);

					calcGoodsYsje(saleGoods.size() - 1);
				}

				// 查找原交易会员卡资料
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = salegdhead.hykh;
					curCustomer.name = salegdhead.hykh;
					curCustomer.ishy = 'Y';
				}

				salegdhead.sqkh = saleHead.sqkh;
				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

				// 重算小票头
				calcHeadYsje();

				//为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额,限额为:" + ManipulatePrecision.doubleToString(staff.thxe) + " 元不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				return true;
			}
			else
			{
				// 其它交易
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					if (!memberGrant())
					{
						salegdhead.hykh = "";
						salegdhead.hysq = "";
						salegdhead.hytype = "";
					}
				}

				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

				String strmsg = "";
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					saleEvent.code.setText("");

					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);

					if (!findGoods(sgd.inputbarcode, sgd.yyyh, sgd.gz))
					{
						// 未找到该商品
						strmsg += "[" + sgd.inputbarcode + "]" + sgd.name + "\n";

						continue;
					}

					// 查找商品成功
					SaleGoodsDef sgd1 = (SaleGoodsDef) saleGoods.get(saleGoods.size() - 1);

					clearGoodsGrantRebate(saleGoods.size() - 1);

					GoodsDef gd1 = (GoodsDef) goodsAssistant.get(saleGoods.size() - 1);

					if (gd1.isdzc == 'Y')
					{
						String[] codeInfo = new String[4];
						boolean isdzcm = analyzeBarcode(gd1.inputbarcode, codeInfo);

						// 如果电子称商品不是通过电子称码来找的商品，所以要将这些值赋上去
						// 否则是通过电子称码来查找商品，则数量价格可以自己解析
						if (!isdzcm)
						{
							sgd1.jg = sgd.jg;
							sgd1.sl = sgd.sl;
							sgd1.hjje = sgd.hjje;
							sgd1.lszke = sgd.lszke;
							sgd1.flag = sgd.flag;
						}
					}
					else
					{
						sgd1.jg = sgd.jg;
						sgd1.sl = sgd.sl;
						sgd1.hjje = sgd.hjje;
						sgd1.lszke = sgd.lszke;

						sgd1.hjje = ManipulatePrecision.doubleConvert(sgd1.jg * sgd1.sl, 2, 1);
					}

					// 只重新赋值手工折扣，会员、促销等折扣靠findGoods重新找商品自动运算
					sgd1.lszke = sgd.lszke;
					sgd1.lszre = sgd.lszre;
					sgd1.lszzk = sgd.lszzk;
					sgd1.lszzr = sgd.lszzr;
					sgd1.cjzke = sgd.cjzke;
					sgd1.ltzke = sgd.ltzke;
					sgd1.qtzke = sgd.qtzke;
					sgd1.qtzre = sgd.qtzre;

					sgd1.sqkh = sgd.sqkh;
					sgd1.sqktype = sgd.sqktype;
					sgd1.sqkzkfd = sgd.sqkzkfd;
					sgd1.batch = sgd.batch;

					calcGoodsYsje(saleGoods.size() - 1);
					AccessDayDB.getDefault().writeWorkLog("商品：" + sgd1.barcode + "金额：" + sgd1.jg + "数量：" + sgd1.sl);

				}

				calcHeadYsje();

				if (strmsg.trim().length() > 0)
				{
					new MessageBox("未找到解挂单中以下商品:\n" + strmsg);
				}

				return true;
			}
		}
		finally
		{
			AccessDayDB.getDefault().writeWorkLog("合计总金额：" + saleHead.hjzje + "合计总数量：" + saleHead.hjzsl);

			isonlinegdjging = false;
		}
	}

	public void payInput()
	{
		if(SellType.ISBACK(saletype)){
			if(!checkThxe()) return;
		}
		if (SellType.ISCHECKINPUT(saletype))
		{
			checkSell();
		}
		else
		{
			paySell();
		}
	}

	private boolean checkThxe()
	{
		
//		 检查是否超出退货限额
		if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
		{
			OperUserDef staff = backSellGrant();
			if (staff == null)
			{
				return false;
			}
			else
			{
				if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
				{
					new MessageBox("超出退货的最大限额,限额为:" + ManipulatePrecision.doubleToString(staff.thxe) + " 元不能退货");
					return false;
				}
				else
				{
					// 记录日志
					saleHead.thsq = staff.gh;
					curGrant.privth = staff.privth;
					curGrant.thxe = staff.thxe;

					String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
					AccessDayDB.getDefault().writeWorkLog(log);

					//
					new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
				}
			}
			
		}
		return true;
	}
	
	private void writeLog(String infos)
    {
    	try
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).info(infos);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }
    	
    private void writeLog(Exception ex)
    {
    	try
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).info(ex);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		PosLog.getLog(this.getClass().getSimpleName()).error(e);
    	}
    }
}
