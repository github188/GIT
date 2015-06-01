package bankpay.alipay.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Tools {
	  // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public Md5Tools() {
    }

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
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

    
    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return resultString;
    }

    public static void main(String[] args) {
    	Md5Tools getMD5 = new Md5Tools();
    	String str ="_input_charset=utf-8&extend_params="+
    			"{'MACHINE_ID':'1013','AGENT_ID':'11275293f1','STORE_ID':'202','STORE_TYPE':'202'}"+
    			"&it_b_pay=20m&out_trade_no=201407111700592028866&partner=2088201565141845&product_code=QR_CODE_OFFLINE&service=alipay.acquire.precreate&subject=二维码支付-&total_fee=0.10ai1ce2jkwkmd3bddy97z0xnz3lxqk731";
        System.out.println(getMD5.GetMD5Code(str));
        System.out.println(getMD5.MD5Encrypt(str));
    }

}
