package com.efuture.javaPos.PrintTemplate;

public class PrintTemplateItem
{
    public String code;	//项目代码
    public int rowno; 	//行号
    public int colno; 	//列号
    public int length;	//长度
    public int alignment;//对齐
    public String text;	//[,文本内容]

    public String tostring()
    {
        return code + "  " + rowno + "    " + colno + "     " + length +
               "     " + text;
    }
}
