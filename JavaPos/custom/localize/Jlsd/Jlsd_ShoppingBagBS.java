package custom.localize.Jlsd;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleGoodsDef;


public class Jlsd_ShoppingBagBS
{
	SaleGoodsDef saleGoodsDef;
	String shoppingBag[] = {"D","Z","X"};
	String shoppingBagCount[] = {" "," "," "};
    public Vector getTableInfo(SaleGoodsDef saleGoodsDef)
    {
    	this.saleGoodsDef = saleGoodsDef;
        // 读取上次输入的数量
        Vector tableInfo = new Vector();
        String[] row = null;
        
        if(saleGoodsDef.str4 != null){
        	String jlsd = saleGoodsDef.str4;
        	String[] jlsdc = jlsd.split(",");
        	for (int i = 0; i < jlsdc.length; i++) {
				String str1 = jlsdc[i];
				String[] jlsdb = str1.split(":");
				shoppingBag[i] = jlsdb[0];
				shoppingBagCount[i] = jlsdb[1];
			}
        }
        	  // 设置购物袋数量初始输入值
	        for (int i = 0; i < shoppingBag.length; i++)
	        {
	            String clo = shoppingBag[i];
	           
	            
	                row    = new String[2];
	                row[0] =  clo ;
	
	                
	                for (int j = 0; j < shoppingBagCount.length; j++)
	                {
	                	 String count = shoppingBagCount[i];
	                	 	
	                    if (count !=" " && !" ".equals(count))
	                    {
	                        row[1] = count;
	                    }
	                }
	
	                if (row[1] == null)
	                {
	                    row[1] = " ";
	                }
	
	                tableInfo.add(row);
	            }

        return tableInfo;
    }

    //保存数量
    public boolean saveJlsd_Money(Vector tableInfo)
    {
        String[] jlsdPay = null;
        String jlsdstr="";

        for (int i = 0; i < tableInfo.size(); i++)
        {
        	jlsdPay = (String[]) tableInfo.elementAt(i);
        	for (int j = 0; j < jlsdPay.length; j++) {
				String string = jlsdPay[j];
				if(j==0){
					if(!string.equals("") && string.equals("大袋")){
						string = "D";
					}else if(!string.equals("") && string.equals("中袋")){
						string = "Z";
					}else if(!string.equals("") && string.equals("小袋")){
						string = "X";
					}
					jlsdstr+= string+":";
				}else if(j ==1){
					if(!string.equals(" ") && string!=" "){
						int count = Integer.parseInt(string);
						string = count+"";
					}
					jlsdstr+= string+",";
				}
				
			}
         }
        saleGoodsDef.str4 =  jlsdstr.substring(0,jlsdstr.length()-1);
        if (tableInfo.size()>0)
        {
        return true;
        }
        else
        {
        	if (new MessageBox("您未输入数量或数量为0\n是否继续",null,true).verify()!=GlobalVar.Key1)
        		return false;
        	else
        		return true;
        }
    }
}
