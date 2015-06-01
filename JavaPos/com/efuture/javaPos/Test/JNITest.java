package com.efuture.javaPos.Test;


import com.efuture.commonKit.Convert;

import device.RdPlugins.EontimeCard_RdPlugin;
import device.RdPlugins.WanDaMember_RdPlugin;

public class JNITest
{
	
	public static void main(String[] args)
	{
		testEontimeCardJNI();
	}
	
	public static void testWanDaMemberJNI(String[] args)
	{
//		String str = "12[4]123";
//		//System.out.println(Convert.codeInString(str, '['));
//		String num = "122";
//		try
//		{
//			//System.out.println(str.substring(3,2));
//			//Integer.parseInt(num);
//			System.out.println(Double.parseDouble(num));
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//		//Q031P019       ,1234                ,;9999999999025288=000001000640422600,2          ,1,000000008800
		String i = "2";
		String l = "02";
		String j = "1028"; //4
		String h = "123456";//6
		//String f =   "1          ";
		String f = "000000000022"; //11
		String g = "123456789012"; //12
		//String a = "123456789012345";  //15
		String a = "12345          ";  //15
		//String b = "12345678901234567890"; //20
		String b = "123                 "; //20
		String c = "1234567890123456789"; //19
		String d = ";9999999999025288=000001000640422600";
		//String d = "123456789012345678901234567890123456789"; //40
		String e ="1234567890123456789012345678901234567890123456789012345678901234"; //64
		String r = "YE";
		String m = "000000000012";
		System.out.println("===========Start===========\n");
		try
		{
			///*
			//RdPlugins.getDefault().getPlugins1().exec(1,a + "," + b);
			//r = (String)RdPlugins.getDefault().getPlugins1().getObject();
			WanDaMember_RdPlugin p = new WanDaMember_RdPlugin();
			p.load();
			//p.exec(1, a + "," + b );
			//p.exec(2,a+ "," +b+ "," + d + "," + f);
			//p.exec(3,a+ "," +b+ "," +d+ "," +f+ "," +i+ "," +g);		
			//p.exec(4,a+ "," +b+ "," +d+ "," +f+ "," +"3714"+ "," +"2"+ "," +"1029"+ "," +f);
			//p.exec(5,a+ "," +b+ "," +d+ "," +f+ "," +g+ "," +h+ "," +j+ "," +f+ "," +g);
        	  //p.exec(5,a+ "," +b+ "," +d+ "," +f+ "," +"3561"+ "," +"25"+ "," +"1028"+ "," +f+ "," +"1");
			//p.exec(6,a+ "," +b+ "," +d+ "," +f+ "," +g+ "," +h+ "," +j+ "," +f);
			  // p.exec(6,a+ "," +b+ "," +d+ "," +f+ "," +"3591"+ "," +"2"+ "," +"1029"+ "," +"12");
			//p.exec(7,a+ "," +b+ "," +g);
			//p.exec(8,a+ "," +b+ "," +d+ "," +l);
			p.exec(10,  a + "," + b );
			//p.exec(12,a+ "," +b+ "," + d );
			//p.exec(13,a+ "," +b+ "," +d+ "," +f+ "," +g+ "," +m+ "," +m+ "," + m + ",01");
			p.exec(13,"0999,021909,9999999999025288=000001000640422600,99,230.0,2.3,0,0,01");
			r = (String)p.getObject();
			//System.out.println(r);

			p.release();
			
			System.out.println("result = " + r +"\n Len :" + r.length());
		if(r == null)
		{
			System.out.println("调用动态库出错");
		}
		
		//*/
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		System.out.println("\n++++++++++++ END +++++++++++");
	}

	
	public static void testEontimeCardJNI()
	{		
			String s1 = "2";
			String s2 = "02";
			String s4 = "1028"; //4
			String s6 = "123456";//6
			//String f =   "1          ";
			String s11 = "000000000022"; //11
			String s12 = "123456789012"; //12
			//String a = "123456789012345";  //15
			String s15 = "12345          ";  //15
			//String b = "12345678901234567890"; //20
			String s20 = "123                 "; //20
			String s19 = "1234567890123456789"; //19
			String s40 = ";9999999999025288=000001000640422600";
			//String d = "123456789012345678901234567890123456789"; //40
			String s64 ="1234567890123456789012345678901234567890123456789012345678901234"; //64
			
			String r = "YE";
			String m = "000000000012";
			System.out.println("===========Start===========\n");
		try
		{

			EontimeCard_RdPlugin card = new EontimeCard_RdPlugin();
			card.load();
//			card.exec(1, s15 + "," + s20 );
			// -
//		    card.exec(2,s20+ "," + s40 + "," + s12); //
			// 
//		    card.exec(3,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s1 + "," + s12 + "," + s12 + "," + s12 + "," + s2);	
			
//				card.exec(4,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 + "," + s6 + "," +"3714"+ "," + s12);
				//
//				card.exec(5,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 + "," + s6 + "," + s4 + "," + s12 + "," + s1 + "," + s12 + "," + s12  + "," + s12);				
//	        	  card.exec(5,s15 + "," + s20 + "," + s40 + "," + s11 + "," +"3561"+ "," +"25"+ "," +"1028"+ "," + s11 + "," +"1");
				
//				card.exec(6,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 + "," + s6 + "," + s4 + "," + s12);
				  // card.exec(6,s15 + "," + s20 + "," + s40 + "," + s11 + "," +"3591"+ "," + s1 + "," +"1029"+ "," +"12");
				
//				card.exec(7,s15 + "," + s20 + "," + s12);
				
//				card.exec(8,s15 + "," + s20 + "," + s40 + "," + s2);
				
//			card.exec(9,  s15 + "," + s20 );
				
//			card.exec(10,  s15 + "," + s20 );
			
			//
//			card.exec(11,  s15 + "," + s20 + "," + s40);
			
				// 
//			card.exec(12,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 );
				// 
//			card.exec(13,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 + "," + s6 + "," + s4 + "," + s12);				
//			card.exec(13,"0999,021909,9999999999025288=000001000640422600,99,230.0,2.3,0,0,01");
			
//			card.exec(14,s15 + "," + s20 + "," + s40 + "," + s12 + "," + s12 + "," + s6 + "," + s4 + "," + s12 + "," + s12);

//			card.exec(15,"02408826780240053008800000000132014101015:45:4200000.3900010.3900000.00100000.0000000.0006180995030182385598883300000.39123456");
			
			r = (String)card.getObject();
				//System.out.println(r);

			card.release();

		   System.out.println("\nresult = " + r +"\n\nLen :" + r.length());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
