package device.ICCard;

import java.util.Vector;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * 新明华IC卡接口，调用mwhrf_bj.dll
 * @author Administrator
 *
 */

public class URFR430_ICCard extends Mwic_ICCard
{
	short st=1;
	int icdev ;//通讯设备标识符
	byte Snr[]=new byte[5];//返回的卡序列号
	
	public String getDiscription()
	{
		return "明华URF-R430/RF-EYE-U010 IC卡设备";
	}
	
	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"扇区",""});
		v.add(new String[]{"块号",""});
		v.add(new String[]{"密码",""});
		return v;
	}
	
	public String findCard()
	{
		String stri="";
		mwrf epen = null;
    	if (DeviceName.deviceICCard.length() <= 0) return null;
    	
    	String[] device = DeviceName.deviceICCard.split(",");

    	short sector = 0; // 扇区
		short block = 1; // 块号
		String pwd = "343033373936"; //"72657A696E2A"; // 读密码
    	if (device.length > 0) sector = Short.parseShort(device[0]);
    	if (device.length > 1) block  = Short.parseShort(device[1]);
    	if (device.length > 2) pwd	  = device[2];
    	
//    	new MessageBox(sector+"\n"+block+"\n");
    	
    	try
    	{

    		//加载动态库
			epen = (mwrf) Native.loadLibrary("mwhrf_bj", mwrf.class);   
			if (epen != null)   
				System.out.println("DLL加载成功！"); 
//				new MessageBox("DLL加载成功!");
			else
	//			System.out.println("DLL加载失败！");	
				new MessageBox("DLL加载失败!");
			
			//初始化设备
			DevConnect(epen);
			
			for(short i=0;i<sector;i++)
			{
				byte[] key=new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
//				byte[] key = String2Byte(pwd);
				//将密码装入读写模块
				st=epen.rf_load_key(icdev, (short)0, i, key);
				if(st!=0)
				{
					System.out.println("加载 "+i+" 扇区密码失败!");
					new MessageBox("加载 "+i+" 扇区密码失败!");
				}
			}
			//执行蜂鸣
			epen.rf_beep(icdev, (short)30);
			
			//寻卡
			st=epen.rf_card(icdev,(short)1,Snr);
			if(st==0)
			{
				byte[] Snrhex=new byte[9];
				epen.hex_a(Snr,Snrhex,(short)4);
				String str=new String(Snrhex,0,8);
				System.out.println(str);
			}
			else
			{
				System.out.println("寻卡失败！");
				new MessageBox("寻卡失败！");
			}
			//验证某一扇区密码
			st=epen.rf_authentication(icdev, (short)0, sector);
			if(st!=0)
			{
				System.out.println(sector+"扇区密码验证 错误!");
				new MessageBox(sector+"扇区密码验证 错误!");
			}
			
			byte[] rdata=new byte[32];
			//读取数据
			st=epen.rf_read_hex(icdev, (short)(sector*4), rdata);
			if(st==0)
			{
				stri=new String(rdata);
				stri = stri.replaceAll("F","");
//				new MessageBox("读数据成功，数据： "+stri);
				
			}
			else
			{
				System.out.println("读数据失败！");
			}
    	} 
    	catch(Exception ex)
        {
        	ex.printStackTrace();
        	System.out.println(ex.getMessage());
        	return "";
        }
        finally
        {
        	//关闭设备
        	epen.rf_usbclose(icdev);
        }
    		
    	return stri;
	}
	
	
	//初始化
	public void DevConnect(mwrf epen)
	{
		byte[] ver=new byte[20];
		//传送内容
		icdev =epen.rf_usbopen();
		//获取硬件版本号
		st=epen.rf_get_status(icdev, ver);
		if(st==0)
		{
			String str=new String(ver,0,18);
			System.out.println("设备初始化成功！" + str); 
//			new MessageBox("设备初始化成功！" + str);
			
		}
		else
		{
			System.out.println("设备连接失败!");
			new MessageBox("设备连接失败！");
		}
		
	}
	
	
	public interface mwrf extends Library{
		public int rf_usbopen();
		public short rf_usbclose(int icdev);
		public short rf_get_status(int icdev,byte[] ver);
		public short rf_beep(int icdev,short time);
		public short rf_load_key(int icdev,short mode,short sector,byte[] key);
		public short rf_load_key_hex(int icdev,short mode,short sector,String key);
		public short rf_card(int icdev,short mode,byte[] Snr);
		public short rf_authentication(int icdev,short mode,short sector);
		public short rf_read(int icdev,short addr,byte[] data);
		public short rf_read_hex(int icdev,short addr,byte[] data);
		public short rf_write(int icdev,short addr,byte[] data);
		public short rf_write_hex(int icdev,short addr,byte[] data);
		public short rf_changeb3(int icdev,short SecNr,byte[] KeyA,short _B0,short _B1,short _B2,short _B3,short _Bk,byte[] _KeyB);
		
		public short rf_pro_rst(int icdev, byte[] _Data);
		public short rf_pro_trn(int icdev, byte[] problock,byte[] recv);

		public short hex_a(byte[] hex,byte[] a,short len);
		public short a_hex(byte[] a,byte[] hex,short len);	
	}

}


