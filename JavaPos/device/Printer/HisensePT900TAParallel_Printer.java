package device.Printer;

import com.efuture.javaPos.Global.Language;


public class HisensePT900TAParallel_Printer extends Parallel_Printer
{

  public String getDiscription()
  {
    return Language.apply("HisensePT900TA并口打印机");
  }
  
  public void setBigChar(boolean status)
  {
      if (status)
      {
//          char[] con1 = { 0x1C, 0x57,0x01};
    	  char[] con1 = { 0x1B, 0x21,0x10};//倍高
          port.sendString(String.valueOf(con1));

    	  char[] con2 = { 0x1B, 0x21,0x20};//倍宽
          port.sendString(String.valueOf(con2));
      }
      else
      {	
//    	  char[] con = { 0x1C, 0x57,0x00};
//          port.sendString(String.valueOf(con));

    	  char[] con = { 0x1B, 0x21,0x00};
          port.sendString(String.valueOf(con));
      }
  }
}