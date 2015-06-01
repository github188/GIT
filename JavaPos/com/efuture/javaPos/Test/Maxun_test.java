package com.efuture.javaPos.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;

import mediaPlayer.MainFun;

public class Maxun_test {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String a = "05,622235******2272   ,000000000100,000000000000,000000,160307,160714,20121221,1702,                                     ,                                                                                                        ,40000167,      ,00,130204040084001,130204040084,               ,  ,TC:9F33BDA7BC2C,        ,                                                  ,                                        ,000001,000001,01  ,C牡丹贷记卡         ,消费                ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ,1,                                                                                                    ,银联商户号(UNION PAY MERCHANT CODE):102361053110119总计:    RMB: 1.00发卡行(ISSUE BANK): 中国工商银行                           为防止银行卡卡号泄漏，保障持卡人用卡安全，已对打印卡号中的部分号码予以屏蔽，如有疑问请立即联系银行。                                                                 ,                        ,          ,          ";
		String result[] = a.split(",");//		String a1= result[0].split(",")[0];//		System.out.println(result[0].split(",")[0]);
//		System.out.println(result[1].substring(0,2));
//		System.out.println(Double.parseDouble(result[2]));
//		System.out.println(result[2]);
//		System.out.println(ManipulatePrecision.doubleConvert(Double.parseDouble(result[2])/1000, 2, 1));
		System.out.println(result[13]);
//		System.out.println(result[1].substring(99,109));
		
		String[] a1= new String[2];
	Vector v = new Vector();
	a1[0] ="1";
	a1[1] ="2";
	v.add(a1);
	a1 = new String[2];
	a1[0] ="3";
	a1[1] ="4";
	v.add(a1);
	
	double a2 = 0/0.01;
		System.out.println(a2+"true");
	
		
		
	}
	
	public Maxun_test() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		a_Obj a = null;
		b_Obj b = null;
		//Maxun Add
		a = new a_Obj();
		Class cl = Class.forName("com.efuture.javaPos.Test.b_Obj".toString());
		b = (b_Obj) cl.newInstance();
		//Maxun Add
		
		System.out.println("result = "+b.c(1,1));
		
		String dd = "1,2,3";
		String aaa[] = dd.split(",");
		for(int i =0;i<aaa.length;i++){
			
		}
			
		
	}

}
