package com.efuture.javaPos.Logic;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ReadTextFile;
import com.efuture.defineKey.KeyCharExchange;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.ShortcutKeyDef;

public class ShortcutKeyBS 
{
	private ArrayList keyList = new ArrayList();
	private ShortcutKeyDef skd = null;
	
	ReadTextFile rtf = new ReadTextFile();
	
	public ShortcutKeyBS()
	{
		
	}
	
	//将快捷键显示在Table上
	public boolean init(Table tabShortKey,Vector tempShortcutSet)
	{
		try
		{
			if(GlobalInfo.keyList == null) return false;
			
			if(GlobalInfo.keyList.size() > 0)
			{
				for(int i = 0;i<GlobalInfo.keyList.size();i++)
				{
					String line = "";
					
					ShortcutKeyDef skd = (ShortcutKeyDef)GlobalInfo.keyList.get(i);
					
					String tempstr[] = skd.getKeyString().split(";");
					
					for (int j = 0;j < tempstr.length;j++)
					{
						if (tempstr[j] == null || tempstr[j].trim().length() <= 0) continue;  
						if ((Integer.parseInt(tempstr[j].trim()) >= GlobalVar.Key0) && (Integer.parseInt(tempstr[j].trim()) <= GlobalVar.Decimal))
	    				{
							int cvalue = Integer.parseInt(tempstr[j].trim());
	    					line = line + getKeyNum(cvalue);
	    				}
	    				else
	    				{
	    					if (line.length() > 0 && "1234567890.".indexOf(line.charAt(line.length()-1)) != -1)
	    					{
	    						line = line +"+"+ getFuncKeyName(Integer.parseInt(tempstr[j].trim()))+"+";
	    					}
	    					else
	    					{
	    						line = line + getFuncKeyName(Integer.parseInt(tempstr[j].trim()))+"+";
	    					}
	    				}
					}
					
					if (line.charAt(line.length() - 1) == '+')
					line = line.substring(0,line.length()-1);
					tempShortcutSet.add(String.valueOf(skd.getShortcutKey()));
					String strkey[] = {String.valueOf((i+1)),KeyCharExchange.keyexchange(skd.getShortcutKey()),line + "+"}; 
					
					TableItem item = new TableItem(tabShortKey, SWT.NONE);
                	item.setText(strkey);
				}
				
				String strkey[] = {String.valueOf((GlobalInfo.keyList.size()+1)),"",""};
				TableItem item = new TableItem(tabShortKey, SWT.NONE);
            	item.setText(strkey);
			}
			else
			{
				String strkey[] = {"1","",""};
				TableItem item = new TableItem(tabShortKey, SWT.NONE);
            	item.setText(strkey);
			}
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	

    //返回已有快捷键个数
    public int getValidKey()
    {
    	return keyList.size();
    }
    
    //设置快捷键
    public void setKeyList()
    {
    	GlobalInfo.keyList = keyList;
    }
    
	// 导入文档
    public boolean loadFile(String filename)
    {
    	try
    	{
    		if (rtf.loadFile(filename))
            {
                String line = null;
                
                while ((line = rtf.nextRecord()) != null)
                {
                	if (line.length() <= 0)
                    {
                        continue;
                    }
                	
                	String[] a = line.split("=");
                	
                	if (a.length < 2)
                    {
                        continue;
                    }
                	
                	skd = new ShortcutKeyDef();
                	skd.setShortcutKey(Integer.parseInt(a[0].trim()));
                	skd.setKeyString(a[1].trim());
                	
                	keyList.add(skd);
                	
                }
                
                rtf.close();
                
                return true;
            }
    		else
    		{
    			return false;
    		}
    	}
    	catch(Exception ex)
    	{
    		 ex.printStackTrace();
             new MessageBox(Language.apply("快捷键配置文件导入错误......"), null, false);

    		return false;
    	}
    }
    
    //判断是否可以做为快捷键的键值
    public boolean isOkKey(int code)
    {
    	try
    	{
    		if (isFuncKey(code) > -1 )
    		{
    			new MessageBox(Language.apply("对不起这个键已经是功能键!"), null, false); 
    			return true;
    		}
    					
    		return false;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    //判断是否是功能键值
    private int isFuncKey(int code)
    {
    	try
    	{
    		String e = String.valueOf(code);
            String[][] keypad = GlobalInfo.keypad;
            int validnum = GlobalInfo.validNum;

            if (keypad == null)
            {
                return -1;
            }

            for (int i = 0; i < validnum; i++)
            {	
                if (keypad[i][2].equals(e) || keypad[i][3].equals(e))
                {
                    return Integer.parseInt(keypad[i][1].trim());
                }
            }

            return -1;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return -1;
    	}
    }
    
    //获得功能键名
    public  String getFuncKeyName(int code)
    {
        String[][] keypad = GlobalInfo.keypad;
        int validnum = GlobalInfo.validNum;

        if (keypad == null)
        {
            return null;
        }
        
        for (int i=0;i < validnum; i++)
        {
        	if (code == Integer.parseInt(keypad[i][1].trim()))
        	{
        		return keypad[i][0].trim();
        	}
        }
        
        return null;
    }
    
    //获得keycode
    public  String getFuncKeyCode(String keyname)
    {
    	String[][] keypad = GlobalInfo.keypad;
    	int validnum = GlobalInfo.validNum;
    	
    	if (keypad == null)
    	{
    		return null;
    	}
    	
    	for (int i=0;i < validnum; i++)
        {
    		if (keyname.equals(keypad[i][0].trim()))
        	{
    			return keypad[i][1];
        	}
        }
    	
    	return null;
    }
    
    //保存键值
    public boolean saveShortcutKeyValue(Table tabShortKey,Vector tempShortcutSet)
    {
    	try
    	{
    		GlobalInfo.keyList.clear();
    		
    		for(int i = 0 ; i < tabShortKey.getItemCount();i++)
    		{
    			String line="";
    			
    			TableItem tableItem = tabShortKey.getItem(i);
    			
    			ShortcutKeyDef skd = new ShortcutKeyDef();
    			
    			if (tableItem.getText(1) == null || tableItem.getText(2) == null) continue;
    			
    			if(tableItem.getText(1).trim().equals("") || tableItem.getText(2).trim().equals("")) continue;
    			
    			skd.setShortcutKey(Convert.toInt(tempShortcutSet.elementAt(i)));
    			
    			String temstrvalue = tableItem.getText(2).replace('+',';');
    			
    			String tempstr[] = temstrvalue.split(";");
    			
    			for (int j = 0;j < tempstr.length;j++)
    			{
    				if ("1234567890.".indexOf(tempstr[j].charAt(0)) != -1 && "1234567890.".indexOf(tempstr[j].charAt(tempstr[j].length()-1)) != -1)
    				{
    					for (int k = 0;k < tempstr[j].length();k++)
    					{
    						char cvalue = tempstr[j].charAt(k);
    						line = line + getKeyCode(cvalue) + ";";
    					}
    				}
    				else
    				{
    					line = line + getFuncKeyCode(tempstr[j])+";";
    				}
    			}
    			
    			line = line.substring(0,line.length()-1);
    			skd.setKeyString(line);
    			
    			GlobalInfo.keyList.add(skd);
    		}
    		
    		 if(rtf.writeFile(GlobalVar.ShortcutKeyFile, GlobalInfo.keyList))
    		 {
    			 new MessageBox(Language.apply("快捷键保存成功!"), null, false);
    			 return true;
    		 }
    		 else
    		 {
    			 new MessageBox(Language.apply("对不起快捷键保存失败!"), null, false);
    			 return false;
    		 }
    		
    	}
    	catch(Exception ex)
    	{
    		new MessageBox(Language.apply("对不起保存快捷键出现未知异常!"), null, false);
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    private String getKeyNum(int keyValue)
    {
        switch (keyValue)
        {
            case GlobalVar.Key0: //数字0
                
            	
            return "0";

            case GlobalVar.Key1: //数字1
                

            return "1";   

            case GlobalVar.Key2: //数字2
                

            return "2";

            case GlobalVar.Key3: //数字3
                

            return "3";    

            case GlobalVar.Key4: //数字4


            return "4";	

            case GlobalVar.Key5: //数字0
           
            return "5";
            
            case GlobalVar.Key6: //数字6
            

            return "6";

            case GlobalVar.Key7: //数字7
                

            return "7";	

            case GlobalVar.Key8: //数字8
            
            return "8";	

            case GlobalVar.Key9: //数字9


            return "9";	

            case GlobalVar.Decimal: //小数点
                
            return ".";	
        }
        
        return "";
    }
    
    private String getKeyCode(char num)
    {
    	String keycode = "";
    	
    	switch(num)
		{
    		case '0':
    			keycode = "1";
    		break;	
			case '1':
				keycode = "2";
			break;
			case '2':
				keycode = "3";
			break; 
			case '3':
				keycode = "4";
			break;
			case '4':
				keycode = "5";
			break;
			case '5':
				keycode = "6";
			break;
			case '6':
				keycode = "7";
			break;
			case '7':
				keycode = "8";
			break;
			case '8':
				keycode = "9";
			break;
			case '9':
				keycode = "10";
			break;
			case '.':
				keycode = "11";
			break;	
		}
    	
    	return keycode;
    }

    
}
