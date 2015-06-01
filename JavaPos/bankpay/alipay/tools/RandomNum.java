package bankpay.alipay.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomNum {
	   	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    public static final String numberChar = "0123456789";


	    public  String generateString(int length) //参数为返回随机数的长度
	    {
	     StringBuffer sb = new StringBuffer();
	     Random random = new Random();
	     for (int i = 0; i < length; i++)
	     {
	      sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
	     }
	    return sb.toString(); 
	    }
	    
	    public String getTime ()
	    {
	    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
	    	String time = df.format(new Date()).toString();
	    	return time;
	    }
	    
	    public static void main(String [] args)
	    {
	    	RandomNum r = new RandomNum();
	    	System.out.println(r.getTime());
	    }

}
