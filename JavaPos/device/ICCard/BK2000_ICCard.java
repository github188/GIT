package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

/**
 * 中商一卡通
 * @author wangyong
 *
 */
public class BK2000_ICCard implements Interface_ICCard {

	protected String cmd = null;		//接口
	protected String reqfile = null;	//请求文件
	protected String retfile = null;	//响应文件
	
	protected String cardreader_portno = null;		//读卡器端口号
	protected int cardreader_address = 1;	//读卡器的通讯地址
	protected String cardreader_loadSectorpwd = null;//读卡器加载某扇区密码
	protected String card_pwdCondition = null;	//密码类型
	protected String card_pwd = null;	//卡密码
	protected String read_cardno_address = null;//卡号地址
	protected int read_cardno_times = 1;	//(循环)读卡次数

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{"读卡器端口号（数字）"});
		v.add(new String[]{"设置读卡器的通讯地址"});
		v.add(new String[]{"读卡器加载某扇区密码"});
		v.add(new String[]{"密码条件"});
		v.add(new String[]{"卡密码"});
		v.add(new String[]{"读卡号地址（扇区号|块号）"});
		v.add(new String[]{"读卡次数"});
		return v;
	}

	public boolean open() {

    	if (DeviceName.deviceICCard.length() <= 0) return false;
    	
    	String[] arg = DeviceName.deviceICCard.split(",");
		if (arg.length > 0) cardreader_portno = arg[0];
		if (arg.length > 1) cardreader_address = Convert.toInt(arg[1]);
		if (arg.length > 2) cardreader_loadSectorpwd = arg[2];
		if (arg.length > 3) card_pwdCondition = arg[3];
		if (arg.length > 4) card_pwd = arg[4];
		if (arg.length > 5) read_cardno_address = arg[5];
		if (arg.length > 6) read_cardno_times = Convert.toInt(arg[6]);
		
		if (cardreader_portno == null || cardreader_portno.trim().length() <= 0) 
		{
			new MessageBox("读卡器端口设置,读取失败!");
			return false;
		}
		if (cardreader_address <= 0) cardreader_address = 1;
		if (cardreader_loadSectorpwd == null || cardreader_loadSectorpwd.trim().length() <= 0) 
		{
			new MessageBox("读卡器加载某扇区密码的设置,读取失败!");
			return false;
		}
		if (card_pwd == null || card_pwd.trim().length() <= 0) card_pwd = "123456789123";
		if (read_cardno_address == null || read_cardno_address.trim().length() <= 0) 
		{
			new MessageBox("读卡号地址设置,读取失败!");
			return false;
		}
		if (read_cardno_times <= 0)
		{
			read_cardno_times = 1;
		}
		
		cmd = "javaposbank.exe BK2000ICCARD";
		reqfile = "request.txt";
		retfile = "result.txt";

		return true;
	}
	
	public String findCard() {
		//1.设置读卡器地址
		//2.读取卡序列号
		//3.加载读卡器密码
		//4.读取卡号
		//5.打开/关闭蜂鸣
		BufferedReader br = null;
		PrintWriter pw = null;
		String strError = null;
		int i = 0;
		//循环读取卡号(在设定的次数内读到为止)
		while (i < read_cardno_times)
		{
			i++;
			strError = null;
			System.out.println("ICCARD_times=[" + String.valueOf(read_cardno_times) + "] start" );
			try
	        {
				// 先删除上次交易数据文件
				if (PathFile.fileExist(reqfile))
				{
					PathFile.deletePath(reqfile);
				   
					if (PathFile.fileExist(reqfile))
					{
						//new MessageBox("读卡请求文件 "+ reqfile + " 无法删除,请重试");
						strError = "error:读卡请求文件 "+ reqfile + " 无法删除,请重试";  
						continue;
					}
				}
				if (PathFile.fileExist(retfile))
				{
					PathFile.deletePath(retfile);
				   
					if (PathFile.fileExist(retfile))
					{
						//new MessageBox("读卡结果文件 " + retfile + " 无法删除,请重试");
						strError = "error:读卡结果文件 " + retfile + " 无法删除,请重试"; 
						System.out.println(strError);
						continue;
					}
				}
				
				//bankexe接口参数:调用类型,读书器通讯地址,扇区,块号,密码条件(0,1,2,3,4),卡密码A,卡密码B,端口号
				//bankexe接口参数举例:1,1,8,1,1,A,B,3
				//bankexe接口返回:函数返回(0表示成功,其它表示失败),卡号,卡序列号
				String reqstr = null;
				reqstr = "1" + "," + this.cardreader_address + "," + this.read_cardno_address.replace("|", ",") + "," + this.card_pwdCondition + "," + this.card_pwd + "," + this.card_pwd + "," + this.cardreader_portno;
				System.out.println("ICCARD_REQ:[" + reqstr + "]");
				
				// 写入请求
				pw = CommonMethod.writeFile(reqfile);
				pw.write(reqstr);
				pw.close();
				
	            // 调用接口模块
	            CommonMethod.waitForExec(cmd);

	            // 读取应答            
	            if (!PathFile.fileExist(retfile) || ((br = CommonMethod.readFileGBK(retfile)) == null))
	            {
	                //new MessageBox("读卡结果文件数据读取失败!");
	            	strError = "error:读卡结果文件数据读取失败!";
	            	continue;
	            }
	            String data = br.readLine();
	            System.out.println("ICCARD_RET:[" + data + "]");
	            //应答格式:返回值(0表示成功),卡号,卡序列号
	            //	      错误信息
	            String retstr[] = data.split(",");
	            if (retstr[0].equals("0"))
	            {            	
	            	if (retstr.length >= 2)
	            	{
	            		return retstr[1];
	            	}
	            }
	            else
	            {
	            	//new MessageBox("读IC卡失败:[" + retstr[0] + "]");
	            	strError = "error:读IC卡失败:[" + retstr[0] + "]";
	            }	            
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        	new MessageBox("IC卡读卡调用异常\n\n" + ex.getMessage());
	        	strError = "IC卡读卡调用异常\n\n" + ex.getMessage();
	        	//return null;
	        }
	        finally
	        {
	        	try
	        	{
	        		if (pw != null)
	            	{
	            		pw.close();
	            	}
	            	if (br != null)
	            	{
	            		br.close();
	            	}
	        	}
	        	catch(Exception ex)
	        	{
	        		ex.printStackTrace();
	        	}        	
	        	
				// 删除上次交易数据文件
				if (PathFile.fileExist(reqfile))
				{
					PathFile.deletePath(reqfile);
				}
				if (PathFile.fileExist(retfile))
				{
					PathFile.deletePath(retfile);
				}
				System.out.println("ICCARD_times=[" + String.valueOf(read_cardno_times) + "] end." );
				
	        }//end try
	        	        
		}//end while
		System.out.println(strError);
		return strError;
		
	}
	
	

	public boolean close()
	{
		return true;
	}
	
	public String getDiscription() {
		
		return "博卡2000";
	}
	public String updateCardMoney(String cardno, String operator, double ye) {
		// TODO 自动生成方法存根
		return null;
	}

}
