package custom.localize.Ycgm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.Line;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class Excute
{
	static String server;
	static int port;
	static Socket curSocket;
	static String validate; //1 双验证，2 只富基验证 3 只同程验证
	static String deal; //Y,解析磁道号
	static boolean isOnline = true; //在退货预支付时，如果联网超时，就当脱网
	//请求数据
	static String type = null;
	static String license = null;
	static String branCode = null;
	static String posNo = null;
	static String operator = null;
	static String validNo = null;
	
	//应答数据
	//static String str;
	static String filename;
	static String valid;
	static String rs;
	static String err;
	static String transId;
	
	static {
		license = Convert.increaseChar(" ", ' ', 18);
		branCode = Convert.increaseChar(ConfigClass.Market, ' ', 20);
		posNo = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10);
		operator = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ',10);
		readConfig();
	}
	
	public static void readConfig()
	{
		BufferedReader br = null;
		String line = null;
		String sp[];
		try{
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/CRM.ini");
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

				if (sp[0].trim().compareToIgnoreCase("ServertSocket") == 0)
				{
					server = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Port") == 0)
				{
					port = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("license") == 0)
				{
					license = sp[1].trim();
					license = Convert.increaseChar(license, ' ', 18);
				}
				else if (sp[0].trim().compareToIgnoreCase("Validate") == 0)
				{
					validate = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("deal") == 0)
				{
					deal = sp[1].trim();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("读取配置文件出现问题");
		}
		finally
		{
			if (br != null) 
			{
				try{
					br.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
					
			}
		}
		
	}
	//处理所有非法情况下的默认错误信息
	public static boolean dealErrorInfo(String line)
	{
		if (line.getBytes().length != 111)
		{			
			return false;
		}
		
		String rstype = line.substring(4,6);
		String rs = line.substring(6,7);
		String errMsg = Convert.newSubString(line,7, 107).trim();
		valid = Convert.newSubString(line, 107, 111);
		
		if ("1".equals(rs))
		{
			err = rstype + "交易失败\n错误描述：" + errMsg;
			new MessageBox(err);
		}
		
//		if (!valid.equals(validNo))
//		{
//			err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
//			new MessageBox(err);
//		}
		
		return true;
	}
	
	//查询会员积分或者储值卡金额
	public static String queryJfOrCzInfo(String track2, String other, String password)
	{
		String result = "";
		try{
			String line = null;
			type = "MQ";
			String track = "";
			
			
			track2 = track2.trim();
			PosLog.getLog("Track-old").warn(track2 + " -End"); 
			//宜昌国贸的发送信息给同程CRM时，需要我们判断是轨道还是手输入卡号
			//这里通过轨道中有 = 来判断为轨道信息
//			if (track.length() <= 20 && track2.indexOf("=") <= -1)
//			{
//				track = Convert.increaseChar("", ' ', 40);
//				other = Convert.increaseChar(track2, ' ', 20);
//			}
//			else
//			{
//				
//				
			    if  (track2.length() >= 40)
			    	track = track2.substring(0, 40);
			    else
			    	track = Convert.increaseChar(track2, ' ', 40);
			    
				other = Convert.increaseChar(other, ' ', 20);
//			}

			

			password = Convert.increaseChar(password, ' ', 20);
			validNo = getRandom();
			validNo = Convert.increaseChar(validNo, ' ', 4);
			
			line = type + license + branCode + posNo + track + other + password + operator + validNo ; 
			

			result = readDataFromCrmServer(line);
			if (null == result || "".equals(result))
				return null;
			
			
//			rs = result.substring(6,7); //代表处理结果 0-处理成功
//			if (!"0".equals(rs)) 
//				return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("调用Socket服务查询会员信息失败！\n" + e.getMessage());
		}
		return result;
	}
	
	//获取交易请求 ID
	public static boolean getTransID(SaleHeadDef salehead)
	{
		if (null != salehead.str6 && !salehead.str6.equals(""))
			return true;
		
		String line = null;
		try{
			
	
			String type = "MI";
			validNo = getRandom();			
			line = type + license + branCode + posNo + operator + validNo ; 
			
			String result = readDataFromCrmServer(line);
			if (null == result || "".equals(result))
				return false;
			
			
			rs = result.substring(6,7);
	//		valid = result.substring(39, 43);			
	//		if (!valid.equals(validNo))
	//		{
	//			err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
	//			
	//			new MessageBox(err);
	//		}
			if ("0".equals(rs))
			{
				transId = result.substring(7, 39);
				transId = Convert.increaseChar(transId, ' ', 32);
				salehead.str6 = transId;
			}
		}
		catch(Exception e )
		{
			return false;
		}
		
		return true;
	}
	
	//会员销售数据（Pos 端提交的会员销售请求）CustomerDef cust,
	public static String sendTransInfo(SaleHeadDef salehead, String line, boolean save)
	{
		String str = "";
		try
		{			

			boolean flag = false;
			
			//如果是失败时重新提交，就不用保存销售数据
			if (save)
			{
				//保存交易数据，失败时下次提交
				saveData("MS",line,GlobalInfo.syjStatus.fphm + "", false);
			}				
					
			//先检查是否有冲正数据
			saleOrTransBack();
			
			type = "MS";
			
			boolean f = true;
			f = getTransID(salehead);					

			//当获取交易ID时失败时，退出
			if (!f)
			{
				new MessageBox("获取交易ID失败！！！");
				//标示已经保存交易数据
				return "save";
			}
			
			str = type + license + transId + line;
			
			//保存冲正信息，在交易失败时进行冲正
			
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
			
			String czstr = "MR" +license + 2 + transId + branCode + posNo + billNo + tranDate + validNo;
			filename =saveData("MR",czstr,GlobalInfo.syjStatus.fphm + "", true);
			
			String result = readDataFromCrmServer(str);
			if (null == result || "".equals(result))
				return null;
			
			validNo = result.substring(74,78);
			
			File file = new File(ConfigClass.LocalDBPath +"SocketMS-" + GlobalInfo.syjStatus.fphm + ".dat");
			if (file.exists())
			{
				file.delete();
			}
//			if (!valid.equals(validNo))
//			{
//				err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
//				
//				new MessageBox(err);
//				return null;
//			}
		
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("提交销售数据出现异常。" + e.getMessage());
			return null;
		}	
		finally{
			str = null;
			filename = null;
		}
		
	}
	
	//记录提交数据，提交失败时使用 flag标记为false时，连接同程网络失败，保存提交数据，进行脱网销售，下次连上同程CRM时再提交
	public static String saveData(String type, String line,String id, boolean flag)
	{
		String name = null;
		File file = null;
		FileOutputStream out = null;
		try{
			if (type.equals("MD"))
			{
				if (flag)
				{
					name = ConfigClass.LocalDBPath +"SocketMD-" + id +".cz";
				}
				else
				{
					name = ConfigClass.LocalDBPath +"SocketMD-" + id +".dat";	
				}
			}
			else if (type.equals("MS"))
			{
				if (flag)
				{
					name = ConfigClass.LocalDBPath +"SocketMS-" + id +".cz";
				}
				else
				{
					name = ConfigClass.LocalDBPath +"SocketMS-" + id +".dat";
				}
			}
			else if (type.equals("MN"))
			{
				if (flag)
				{
					name = ConfigClass.LocalDBPath +"SocketMN-" + id +".cz";
				}
				else
				{
					name = ConfigClass.LocalDBPath +"SocketMN-" + id +".dat";
				}
			}
			
			file = new File(name);
			out = new FileOutputStream(file);
			out.write(line.getBytes());
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
				
		return name;
	}

	//原单退货（根据小票提供信息退货）
	public static String returnGoods(SaleHeadDef salehead, String line)
	{
		try
		{
			type = "MN";
			boolean f = true;
			f = getTransID(salehead);					

			//当获取交易ID时失败时，退出
			if (!f)
			{
				saveData("MN",line,GlobalInfo.syjStatus.fphm + "", false);
				new MessageBox("获取交易ID失败！！！");
				return null;
			}
			line = type + license + transId  + line;
			
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
		
			String czstr = "MR" +license + 2 + transId + branCode + posNo + billNo + tranDate + validNo;			
			filename =saveData("MR",czstr,GlobalInfo.syjStatus.fphm + "", true);
			
			String result = readDataFromCrmServer(line);
			
			if (null == result || "".equals(result))
				return null;
			
			validNo = result.substring(74,78);
			
			File file = new File(ConfigClass.LocalDBPath +"SocketMN-" + GlobalInfo.syjStatus.fphm + ".dat");
			if (file.exists())
			{
				file.delete();
			}
			
//			if (!valid.equals(validNo))
//			{
//				err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
//				
//				new MessageBox(err);
//				return null;
//			}
//			String rs = result.substring(6,7);
//			if (!"0".equals(rs))
//				return null;
	
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("提交退货数据出现异常。" + e.getMessage());
			return null;
		}	
		finally{
			filename = null;
		}
		
	}
	
	public static String sendRefund(SaleHeadDef salehead, String line)
	{
		try
		{
			type = "MW";
			boolean f = true;
			f = getTransID(salehead);					

			//当获取交易ID时失败时，退出
			if (!f)
			{
				//saveData("MW",line,GlobalInfo.syjStatus.fphm + "", false);
				new MessageBox("获取交易ID失败！！！");
				if (!isOnline)
				{
					return "save";
				}
				else
				{
					return null;
				}
			}
			line = type + license + transId  + line;
			
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
		
//			String czstr = "MR" +license + 2 + transId + branCode + posNo + billNo + tranDate + validNo;			
//			filename =saveData("MR",czstr,GlobalInfo.syjStatus.fphm + "", true);
			
			String result = readDataFromCrmServer(line);
			
			if (!isOnline)
			{
				return "save";
			}
			
			if (null == result || "".equals(result))
				return null;
			

			
			validNo = result.substring(74,78);
			
//			File file = new File(ConfigClass.LocalDBPath +"SocketMN-" + GlobalInfo.syjStatus.fphm + ".dat");
//			if (file.exists())
//			{
//				file.delete();
//			}
			
//			if (!valid.equals(validNo))
//			{
//				err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
//				
//				new MessageBox(err);
//				return null;
//			}
//			String rs = result.substring(6,7);
//			if (!"0".equals(rs))
//				return null;
	
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("提交退货数据出现异常。" + e.getMessage());
			return null;
		}	
		finally{
			filename = null;
		}
	}
	
	//销售扣款数据（Pos 端提交的会员销售扣款请求）
	public static String trans(SaleHeadDef salehead,String track2, String code, double money)
	{
		String line = "";
		try
		{
			//先查找是否有交易失败，如果存在进行交易冲正（扣款冲正，销售提交冲正，退货冲正);
			saleOrTransBack();
		    //开始扣款
			type = "MD";
			
			boolean flag = true;
			flag = getTransID(salehead);					

			//当获取交易ID时失败时，退出
			if (!flag)
			{
				new MessageBox("获取交易ID失败！！！");
				return null;
			}
			
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
			String track = Convert.increaseChar(track2, ' ', 40);
			String password = Convert.increaseChar("", ' ', 20);
			validNo = getRandom();
			
			//付款明细
			String payType = Convert.increaseChar(code, ' ',4) ;
			String amt = Convert.increaseChar(money + "", ' ', 13);
			
			line = type +license + transId + branCode + posNo + billNo + tranDate + track + password + validNo + payType + amt;
			
			String czstr = "MR" +license + 1 + transId + branCode + posNo + billNo + tranDate + validNo;			
			filename =saveData("MR",czstr,GlobalInfo.syjStatus.fphm + "", true);
			
			String result = readDataFromCrmServer(line);
			if (null == result || "".equals(result))
				return null;
			
			validNo = result.substring(39,43);
	//		if (!valid.equals(validNo))
	//		{
	//			err = "返回效验序列号" + valid + "同原效验序列号" + validNo + "不一致";
	//			
	//			new MessageBox(err);
	//			return null;
	//		}
			String rs = result.substring(6,7);
			if (!"0".equals(rs))
			{
				new MessageBox("扣款失败！！！");
				return null;
			}
	
			return result;
		}
		catch(Exception e)
		{
			new MessageBox("发送扣款信息出现异常。" + e.getMessage());
			return null;
		}		
	}
	
	//会员销售（扣款）数据冲正（该笔销售 Pos 端未能接受到服务发回的信息时，发起该冲正）
	public static String saleOrTransBack()
	{		
		try
		{			
			File file = new File(ConfigClass.LocalDBPath);
			File[] list = file.listFiles();
	
			for (int i = 0; i < list.length; i++)
			{
					// 读取文件
					String name = list[i].getName();
					if (name.startsWith("DEL_Socket"))
					{
						list[i].delete();
						if (list[i].exists())
						{
							new MessageBox("删除保存的" + list[i].getPath() + "冲正文件失败");
						}
					}                     
					if (!name.startsWith("Socket") || !name.endsWith(".cz"))
						continue;
					
					BufferedReader input = null;
					input = new BufferedReader(new FileReader(list[i]));
					String line = input.readLine();
					input.close();

					String result = readDataFromCrmServer(line);
					if (null == result || "".equals(result))
						continue;
				
					String rs = result.substring(6,7);
					if ("0".equals(rs))
					{
						
						list[i].delete();
						if (list[i].exists())
						{
							new MessageBox("删除保存的" + list[i].getPath() + "冲正文件失败");
						}
					}
					else
					{
						new MessageBox(list[i].getPath() + "文件冲正失败！！");						
					}
			}
			
	        
			return "true";
		}
		catch(Exception e)
		{
			new MessageBox("会员销售（扣款）数据冲正出现异常。" + e.getMessage()	);
			return null;
		}

	}
	
	//生成四位随机数
	public static String getRandom()
	{
		String crcstr = String.valueOf(Math.round(Math.random() * 10000));

		if (crcstr.length() > 3)
		{
			return crcstr.substring(0, 4);
		}
		else
		{
			return Convert.increaseChar(crcstr, '0', 4);
		}

	}
	
	//Socket服务
	private static String readDataFromCrmServer(String srcXml)
	{
		byte[] readBuffer = new byte[1024];

		try
		{
			isOnline = true;
			PosLog.getLog("Socket-requst").warn(srcXml);
			if (curSocket == null)
				curSocket = new Socket(server, port);

			curSocket.setSoTimeout(ConfigClass.ReceiveTimeout);
			// 发送数据
			curSocket.getOutputStream().write(srcXml.getBytes());
			curSocket.getOutputStream().flush();

			long interval = 0; // 时间间隔
			int tmpData = 0; // 读取的数据
			int i = 0; // 缓存记数器

			long starttime = System.currentTimeMillis();

			do
			{
				tmpData = curSocket.getInputStream().read();

				if (tmpData == -1)
					break;

				readBuffer[i++] = (byte) tmpData;
				interval = System.currentTimeMillis() - starttime;

				if (interval > ConfigClass.ReceiveTimeout)
				{
					//退货预警处理
					isOnline = false; //脱网
					new MessageBox("读取CRM返回数据超时");					
					return null;
				}

				if (i > 1024)
				{
					new MessageBox("缓冲区溢出");
					return null;
				}
			} while (tmpData != -1);
			
			String ret = new String(readBuffer,0, i, "GBK");
			
			PosLog.getLog("Socket-result").warn(ret + "  " + isOnline   );
			
			//“1”为处理失败，111长度为处理异常返回信息
			String rs = ret.substring(6,7);
			if (!"1".equals(rs) && i != 111)
				return ret;
						
			dealErrorInfo(ret);
			
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			//连接异常，当脱网处理
			isOnline = false;
			return null;
		}
		finally
		{
			try
			{
				if (curSocket != null)
					curSocket.close();
				    curSocket = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				curSocket = null;
			}
		}
	}

}
