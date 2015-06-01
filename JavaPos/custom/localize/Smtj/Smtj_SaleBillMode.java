package custom.localize.Smtj;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

import com.efuture.javaPos.Global.Language;

import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Smtj_SaleBillMode extends Cmls_SaleBillMode
{
	int printtimes = 1;          //打印小票次数
	String gwQrcode = "";        //官网二维码地址
	String gwQrcodeName = "";    //官网二维码地址说明
	String ticketQrcode = "";    //本笔小票二维码地址
	String ticketName = "";      //本笔小票二维码说明
	protected void printSellBill()
    {
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
			(GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals(Language.apply("超市")))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
    	}
		try{
		
        // 设置打印方式
        printSetPage();

        // 多联小票打印不同抬头
		printDifTitle();
		
        // 打印头部区域
        printHeader();

        // 打印明细区域
        printDetail();

        // 打印汇总区域
        printTotal();

        // 打印付款区域
        printPay();

        // 打印尾部区域
        printBottom();

        // 打印赠品联
        printGift();

        ///////在顾客联后打印官网二维码,以及本笔小票的二维码信息///////////
        if(printtimes == 1)
        {
        	printQrcode();
        	printLine(gwQrcodeName);
        	        	
        	if(salehead.str3.indexOf("@") >= 0)
        	{
        		printSaleQrcode();
        		printLine(ticketName);
        		
        	}
        }
        
        printtimes++;
        
        if(printtimes == 4)
        {
        	printtimes = 1;
        }
            
        // 切纸
        printCutPaper();
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
    }
	
	//打印世贸天阶官网二维码
	protected void printQrcode()
	{
		BufferedReader br = null;
		String line = null;
		
		try
		{
			if (new File(GlobalVar.ConfigPath + "//PrintQrcode.ini").exists())
			{
				br = CommonMethod.readFile(GlobalVar.ConfigPath + "//PrintQrcode.ini");
				
				if (br == null)
				{
					new MessageBox(com.efuture.javaPos.Global.Language.apply("配置文件PrintQrcode.ini导入错误"), null, false);
	
					return ;
				}

				
				String[] sp;
				
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

					if (sp[0].trim().compareToIgnoreCase("gwQrcode") == 0)
					{
						gwQrcode = sp[1].trim();
					}
					else if (sp[0].trim().compareToIgnoreCase("gwQrcodeName") == 0)
					{
						gwQrcodeName = sp[1].trim();
					}
					else if (sp[0].trim().compareToIgnoreCase("ticketName") == 0)
					{
						ticketName = sp[1].trim();
					}
					else if (sp[0].trim().compareToIgnoreCase("ticketQrcode") == 0)
					{	
						ticketQrcode = sp[1].trim();
						for(int i=2;i<sp.length;i++)
						{
							ticketQrcode += "=" + sp[i].trim();
						}
					}

				}
				printLine("#Qrcode:" + gwQrcode);
				br.close();		
			}
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
		
	}
	
	//打印本笔小票的二维码
	protected void printSaleQrcode()
	{
		printLine("#Qrcode:" + ticketQrcode + "&smtjcode=" + QrcodeContents());
	}
	
	
	//本笔小票二维码串（经营公司，门店，小票号，收银机号，MD5加密交易时间）
	protected String QrcodeContents()
	{
		SaleHeadDef salehead = getSalehead();
		GlobalParaDef sysPara = GlobalInfo.sysPara;
		String line = sysPara.jygs + "," + salehead.mkt + "," + salehead.fphm + "," + salehead.syjh;
		
		return line;
	}
	
	/**
	 * md5加密
	 * 
	 * @param enStr
	 *            需要加密的字符串
	 * @return 返回加密后的字符串
	 */
	public static String MD5Encrypt(String enStr) 
	{
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(enStr.getBytes()); 
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if(i<0) i+= 256;
			if(i<16) buf.append("0"); 
			buf.append(Integer.toHexString(i));} 
//			System.out.println("result: " + buf.toString());
			//32位的加密
//			System.out.println("result: " + buf.toString().substring(8,24));
			//16位的加密 
		} catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block e.printStackTrace(); } 
		}
		return buf.toString();
	}
}
