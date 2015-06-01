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

//珠海恒宇读卡设备
//齐齐哈尔百花商业股份有限公司，调用动态库（模块名：HYIC；动态库(dll文件）：HYUSB_PINPAD.dll；函数：；）
public class HY_ICCard implements Interface_ICCard
{
	String line;
	String path;
	
    public boolean open()
    {
        if (DeviceName.deviceICCard.length() <= 0) return false;
        line = DeviceName.deviceICCard.trim();
    	return true;
    }

    public boolean close()
    {
    	return true;
    }
    
    public String findCard()
    {
    	try{
    		path = "C:\\JavaPOS\\";
			if (PathFile.fileExist(path + "request.txt"))
			{
				PathFile.deletePath(path + "request.txt");
			   
				if (PathFile.fileExist(path + "request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist(path + "result.txt"))
			{
				PathFile.deletePath(path + "result.txt");
			   
				if (PathFile.fileExist(path + "result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile(path + "request.txt");
			pw.write(line);
			pw.close();
			
	        // 调用接口模块
	        if (PathFile.fileExist("javaposbank.exe"))
	        {
	        	CommonMethod.waitForExec("javaposbank.exe HYIC");
	        }
	        else
	        {
	            new MessageBox("找不到IC卡模块 javaposbank.exe");
	            return null;
	        }
	
	        // 读取应答
	        BufferedReader br = null;
	        if (!PathFile.fileExist(path + "result.txt") || ((br = CommonMethod.readFileGBK(path + "result.txt")) == null))
	        {
	            new MessageBox("读取卡号应答数据失败!");
	            return null;
	        }
	        String cardno = br.readLine().trim();
	        br.close();
	        
	        return cardno;
	    }
    catch(Exception ex)
    {
    	ex.printStackTrace();
    	return null;
    }
    }
    
    public String updateCardMoney(String cardno,String operator,double ye)
    {
    	return null;
    }
    
    public Vector getPara()
    {
		Vector v = new Vector();
		v.add(new String[]{"卡类型：","A","B"});
		v.add(new String[]{"扇区：","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"});
		v.add(new String[]{"区块：", "1","2","3","4"});
		v.add(new String[]{"蜂鸣次数："});
		v.add(new String[]{"蜂鸣时间(<5000)："});
		v.add(new String[]{"蜂鸣间隔(<5000)："});
		return v;
    }
    
    public String getDiscription()
    {
    	return "珠海恒宇读卡设备";
    }
}
