package custom.localize.Zmsy;

import java.io.BufferedReader;
import java.io.PrintWriter;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.BankLogDef;

public class ReadZJNO
{
	private static final String exePath="C:\\pcard2\\";

	 /**
     * 获取身份证信息
     * @return 0成功 其它为失败
     *   成功时返回格式为 0,姓名,性别,民族,出生日期,地址,身份证号码,签发机关,有效开始日期,有效截止日期,新地址
     */
    public static String getSfzInfo()
    {
    	try
    	{
    		String strRequest = ",,,,,,,,,,";//传10个逗号
    		String strResult = "";
    		String strRequestPath = exePath + "request.txt";
    		String strResultPath = exePath + "result.txt";
    		String strExePath = exePath + "javaposbank.exe";
    		
    		//删除响应文件
    		if (PathFile.fileExist(strResultPath))
            {
                PathFile.deletePath(strResultPath);
                
                if (PathFile.fileExist(strResultPath))
                {
            		return "读取护照号失败,删除通讯文件失败";   	
                }
            }
    		
    		//写入请求信息
    		PrintWriter pw = null;
    		try
	         {
	            pw = CommonMethod.writeFile(strRequestPath);
	            if (pw != null)
	            {
	                pw.println(strRequest);
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
    		
	         //调用接口
	         if (PathFile.fileExist(strExePath))
             {
	     		//读取javaposbank.exe PERSONINFO
             	CommonMethod.waitForExec(strExePath + " PERSONINFO");
             }
             else
             {
                 return "找不到读取身份证的工程接口文件:" + strExePath;
             }
    		
	         //读取数据
	         BufferedReader br = null;
	         if (!PathFile.fileExist(strResultPath) || ((br = CommonMethod.readFileGBK(strResultPath)) == null))
	         {	                
	                return "读取身份证应答数据失败!";
	          }
	         strResult = br.readLine();

	         if (strResult == null || strResult.trim().length() <= 0)
	         {
	            return "未读取到身份证数据!";
	         }
	         
	         //返回身份证数据
	         return strResult;
    		
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return "读取身份证数据时异常:" + ex.getMessage();
    	}
    }
    
   
    /**
     * 中航信API
     * @param name 顾客姓名
     * @param certId 证件号
     * @param certType 证件类型（身份证是NI，其他证件都是PP）
     * @param phone 手机号(纯数字，有可能是中国的11位手机号，也有可能是国际旅客的国外手机号)
     * @return 
     */
    public static ZhxFlightApiDef getFlight(String name, String certId, String certType, String phone)
    {
    	BankLogDef log = null;    	
    	ProgressBox pb = null;
    	try
    	{
    		pb = new ProgressBox();
    		pb.setText("正在从【中航信】获取顾客航班信息,请等待...");
    		/*返回信息例子
    		{"type":"0","deptdate":"2014-09-02","depttime":"14:15","depairport":"三亚","desairport":"北京首都","intertype":"1","pflightno":"CZ6715"}
    		
    		pflightno航班号——旅客所乘航班的航班号；（10个字符 ）
    		deptdate起飞日期——具体的航班日期；格式：yyyy：mm:dd
    		depttime起飞时间——航班的计划起飞时间；格式：hh:MM
    		depairport——出发机场 （最多20个汉字）
    		type标志位——0表示获取成功，1表示获取失败，-1表示IP禁止,-2表示无法获取ip
    		desairport——目的地机场（最多20个汉字）
    		intertype——国际航班标志位,0表示国际航班，1表示国内航班*/
    		
    		PosLog.getLog(ReadZJNO.class.getSimpleName()).info("getFlight() inpara: name=[" + name + "],certId=[" + certId + "],certType=[" + certType + "],phone=[" + phone + "],timeout=[" + String.valueOf(GlobalInfo.sysPara.ZHXTimeout) + "].");
    		//com.umtrip.service.UmetripDFS.UmetripDFSTask trip = new com.umtrip.service.UmetripDFS.UmetripDFSTask();
    		String startTime = ManipulateDateTime.getCurrentDateTime();
    		long start = System.currentTimeMillis();
    		String strResponseJson = com.umtrip.service.UmetripDFS.UmetripDFSTask.getInformation(name, certId, certType, "", GlobalInfo.sysPara.ZHXTimeout);//phone 暂不传入
    		long end = System.currentTimeMillis();
    		double diff = end-start;
    		diff=diff/1000;
    		if(diff<0)
    		{
    			PosLog.getLog(ReadZJNO.class.getSimpleName()).info("getFlight() start=【" + start + "】,end=【" + end + "】,diff=【" + diff + "】");
    		}
    		String inPara = certType + "|" + certId + "|" + name + "|" + phone + "|" + String.valueOf(GlobalInfo.sysPara.ZHXTimeout);
    		String type="-999";
    		JSONObject retJson = null;
    		try
    		{
    			PosLog.getLog(ReadZJNO.class.getSimpleName()).info("getFlight() strResponseJson=【" + strResponseJson + "】");
        		retJson = JSONObject.fromObject(strResponseJson);
        		PosLog.getLog(ReadZJNO.class.getSimpleName()).info("getFlight() spliteretJson start");
    			type = retJson.getString("type");
    		}
    		catch(Exception ex)
    		{
    			PosLog.getLog(ReadZJNO.class.getSimpleName()).error(ex);
        		PosLog.getLog(ReadZJNO.class.getSimpleName()).info(ex);
    		}
    		
    		/*BufferedReader br = null;

			br = CommonMethod.readFile("c:\\f.txt");

			if (br == null)
			{
				new MessageBox(Language.apply("打开商品发票名称列表文件失败!"));

				return null ;
			}

			String line = null;
			String strJson="";

			Vector goodsNames = new Vector();

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}
					///goodsNames.add(line);
					strJson=line;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}*/
			String outPara = "||||||";
    		PosLog.getLog(ReadZJNO.class.getSimpleName()).info("getFlight() type=[" + type + "]");
			if (type!=null && type.trim().equalsIgnoreCase("0"))
			{
				ZhxFlightApiDef flight = new ZhxFlightApiDef();
				flight.pflightno = String.valueOf(retJson.getString("pflightno"));
				flight.deptdate = String.valueOf(retJson.getString("deptdate"));
				flight.depttime = String.valueOf(retJson.getString("depttime"));
				flight.depairport = String.valueOf(retJson.getString("depairport"));
				flight.desairport = String.valueOf(retJson.getString("desairport"));
				flight.type = String.valueOf(retJson.getString("type"));
				flight.intertype = String.valueOf(retJson.getString("intertype"));				
				
				outPara = flight.pflightno + "|" + flight.deptdate + "|" + flight.depttime + "|" + 
							flight.depairport + "|" + flight.desairport + "|" + flight.intertype;
				log = getLog(startTime, diff, inPara, outPara, strResponseJson, type);
	    		return flight;
			}
			else
			{
				log = getLog(startTime, diff, inPara, outPara, strResponseJson, type);
				new MessageBox("从【中航信】获取航班信息失败：" + getZhxApiError(type));
				return null;
			}
			
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(ReadZJNO.class.getSimpleName()).error(ex);
    		PosLog.getLog(ReadZJNO.class.getSimpleName()).info(ex);
			new MessageBox("从【中航信】获取顾客航班信息时异常：" + ex.getMessage());
    		return null;
    	}
		finally
		{
   		 	if (pb != null)
            {
                pb.close();
                pb = null;
            }
   		 	//记录日志
   		 	if (log!=null)
   		 	{
   		 		AccessDayDB.getDefault().writeBankLog(log);
				if (NetService.getDefault().sendBankLog(log))
				{
					log.net_bz = 'Y';
					AccessDayDB.getDefault().updateBankLog(log);
				}
				
   		 	}   		 
		}
    }
    
    private static BankLogDef getLog(String rqsj, double invokeTime, String inPara, String outPara, String outJSON, String result)
    {
    	//if(1==1) return null;
    	
    	BankLogDef log = null;
    	try
    	{
    		log = new BankLogDef();
    		Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
			if (obj == null)
			{
				log.rowcode = 1;
			}
			else
			{
				log.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
			}
			log.net_bz='N';
			log.retbz=' ';
    		log.type = "ZHX";    		
    		log.rqsj = rqsj;
    		log.bankinfo=GlobalInfo.sysPara.mktcode;//mkt
    		log.syjh = GlobalInfo.syjDef.syjh;
    		log.fphm = GlobalInfo.syjStatus.fphm;
    		log.syyh = GlobalInfo.syjStatus.syyh;
    		log.crc = String.valueOf(invokeTime);//调用耗时（秒）
    		log.retcode = result;//是否成功（0成功，其它失败）
    		log.retmsg=getZhxApiError(result);//错误信息
    		log.memo1 = inPara;//输入参数：证件类型|证件号码|姓名|手机号|超时时间（秒）
    		log.memo2 = outPara;//输出参数：航班号|起飞日期|起飞时间|出发机场|目的地机场|国际航班标志位
    		log.tempstr = outJSON;//接口返回的JSON串
    		
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(ReadZJNO.class.getSimpleName()).error(ex);
    	}
    	return log;
    }
    
    private static String getZhxApiError(String type)
    {
    	if(type==null) return "其它错误";
    	String errInfo;
    	switch(Convert.toInt(type))
    	{
    		case 0:
    			errInfo="获取成功";
    			break;
    		case -1:
    			errInfo="IP禁止";
    			break;
    		case 1:
    			errInfo="获取失败";
    			break;
    		case 2:
    			errInfo="无法获取ip";
    			break;
    		default:
    			errInfo="未知错误";
    		
    	}
    	//errInfo = "从【中航信】获取航班信息失败：" + errInfo;
    	PosLog.getLog(ReadZJNO.class.getSimpleName()).info("从【中航信】获取航班信息失败：" + errInfo);
    	return errInfo;
    }
}
