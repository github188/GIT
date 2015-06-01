package com.efuture.javaPos.Global;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ReadTextFile;


//这个类用于键盘按键设置的类
public class KeyPadSet
{
    private int max_Num = 100;
    private int validnum = 0;
    String[][] keypad = new String[max_Num][4];
    private String filename = null;
    ReadTextFile rtf = new ReadTextFile();

    //读取按键配置文档，将读取数据存入内存
    public KeyPadSet(String filename)
    {
        this.filename = filename;
    }

    //返回最大键值个数
    public int getMax()
    {
        return max_Num;
    }

    //返回已有键值个数
    public int getValidKey()
    {
        return validnum;
    }

    public void setKeyMem()
    {
        GlobalInfo.keypad   = keypad;
        GlobalInfo.validNum = validnum;
    }

    //导入文档
    public boolean loadFile()
    {
    	
        try
        {
            if (rtf.loadFile(filename))
            {
                int i = 0;
                String line;

                while ((line = rtf.nextRecord()) != null)
                {
                    if (line.length() <= 0)
                    {
                        continue;
                    }

                    String[] a = line.split(",");

                    if (a.length < 3)
                    {
                        continue;
                    }

                    keypad[i][0] = a[0].trim().split("=")[0].trim();
                    keypad[i][1] = a[0].trim().split("=")[1].trim();
                    keypad[i][2] = a[1].trim();
                    keypad[i][3] = a[2].trim();

                    //PublicMethod.DEBUG_MSG(line + "   " + i);
                    //PublicMethod.DEBUG_MSG(String.valueOf(keypad[i][2].length()));

                    i++;
                }

                rtf.close();
                
                validnum = i;

                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MessageBox(Language.apply("键盘配置文件导入错误,马上退出系统"), null, false);

            return false;
        }
    }

    //寻找是否已有键值
    public String search(String e,int index)
    {
        for (int i = 0; i < validnum; i++)
        {
            if (keypad[i][2].equals(e) || keypad[i][3].equals(e))
            {
                if (index != i) return keypad[i][0];
            }
            else
            {
            }
        }

        return "null";
    }

    public boolean deleteValue(String name, String keycode)
    {
        for (int i = 0; i < validnum; i++)
        {
            if (keypad[i][0].equals(name))
            {
                if (keypad[i][2].equals(keycode))
                {
                    keypad[i][2] = "0";

                    return true;
                }
                else if (keypad[i][3].equals(keycode))
                {
                    keypad[i][3] = "0";

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    //
    public boolean setKeypad(int i, int n, String keycode)
    {
        if ((i <= 100) & (n < 4) & (n > 1))
        {
            keypad[i][n] = keycode;

            return true;
        }
        else
        {
            return false;
        }
    }

    //设置新的键值
    public void setWholeKey(String[][] key)
    {
        keypad = key;
    }

    public Vector getKey1()
    {
        Vector v = new Vector();

        for (int i = 0; i < validnum; i++)
        {
            v.add(keypad[i]);
        }

        return v;
    }

    //得到键值组
    public String[][] getKey()
    {
        return keypad;
    }

    //打出键值组。测试时有用
    public void PrintString()
    {
        for (int i = 0; i < 100; i++)
        {
            if (keypad[i][1] != null)
            {
                System.out.println(keypad[i][1] + "=" + keypad[i][3]);
            }
            else
            {
                break;
            }
        }
    }

    //储存键值组
    public void writeFile()
    {
        rtf.writeFile(keypad, validnum);
        
        GlobalInfo.keypad = keypad;
        GlobalInfo.validNum = validnum;        
    }
}
