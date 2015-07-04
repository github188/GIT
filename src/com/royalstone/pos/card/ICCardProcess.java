/*
 * 创建日期 2004-7-15
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.card;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.UnsupportedCommOperationException;

import com.royalstone.pos.gui.ICInput;
import com.royalstone.pos.hardware.iccard.ConfigRCException;
import com.royalstone.pos.hardware.iccard.ICCardInfo;
import com.royalstone.pos.hardware.iccard.RC500;
import com.royalstone.pos.hardware.iccard.util.FromSigned;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Value;
 
/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class ICCardProcess {

	private String payTotal;
	private String exceptionInfo;
	private ICCardInfo cardInfo;
	private RC500 rc500;
	private int tender;
	private String balance;

	public boolean process() {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("ICCard.properties"));

			rc500 = new RC500(prop.getProperty("PortNo"));
			RC500.setKeyOfSector(
				FromSigned.fromStrToByteArray(prop.getProperty("KeyOfSector")));
				
				
			ICInput icInput=new ICInput();
			boolean wait=true;
			while(wait && !icInput.isFinish()){
				try{
					icInput.show();
					cardInfo = ICCardInfo.load(rc500);
					icInput.showStar();					
					wait=false;
					icInput.dispose();
				}catch(ConfigRCException e){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}									
				}
			}

			if(cardInfo==null){
				rc500.close();
				return false;
			}
			
			tender = pos.core.getValue().getValueToPay();
			if (tender > cardInfo.getCredit()) {
				exceptionInfo = "卡余额不足!";
				rc500.close();
				return false;
			}
			payTotal=Integer.toString(tender);
			balance = (new Value(cardInfo.getCredit() - tender)).toString();
			return true;
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}

		try{
			rc500.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		exceptionInfo = "读卡错误!";
		return false;
	}

	public boolean performPay() {
		cardInfo.setCredit(cardInfo.getCredit() - tender);
		try {
			cardInfo.save(rc500);
			rc500.beep();
			rc500.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try{
			rc500.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		exceptionInfo = "写卡错误!";
		return false;
	}

	public String getExceptionInfo() {
		return exceptionInfo;
	}

	public String getPayTotal() {
		return payTotal;
	}

	public String getCardNo() {
		if (cardInfo != null) {
			return cardInfo.getSerialNumber();
		}
		return "";
	}

	public String getCardBalance() {
		return balance;
	}
	
	public static void main(String args[]) throws Exception{
		String a="38184";
		String b="38216";
		Date date=new Date(Long.parseLong(a));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(sdf.format(date));
		
		date=new Date(Long.parseLong("1089907161816")+Long.parseLong(b));
		System.out.println(sdf.format(date));
				
		System.out.println(sdf.parse("2004-07-16").getTime());
		System.out.println(sdf.parse("2004-07-16").getTime()-Integer.parseInt(a));
		System.out.println(Long.parseLong("1089907161816")+Long.parseLong(a));
		
		System.out.println(sdf.parse("2004-08-17").getTime()-sdf.parse("2004-07-16").getTime());
		
		System.out.println(38216-38184);
		
		
	}

}
