package com.efuture.javaPos.Struct;

// 收银机定义类
public class SyjMainDef
{
    static public String[] ref ={"syjh","syjdesc","issryyy","ists","iszp","isxyk","isth","ispf","isprint","isdisp","iskey","printfs","sswrfs","printxe","datatime","dataspace","ipaddr","priv","syjplacemkt","str1","str2","str3"};
    
	public String syjh; 			// 收银机号,主键
	public String syjdesc;          // 收银机描述
	public char issryyy; 			// 收银机是否输入营业员(N-超市,Y-输入,A-可不输,B-不输入营业员百货)
	public char ists; 				// 是否通收(收所有柜组Y/N)         
	public char iszp; 				// 是否支票台（Y/N）                  
	public char isxyk; 				// 是否信用卡台（Y/N）                 
	public char isth; 				// 是否退货台（Y/N）                  
	public char ispf; 				// 是否批发台（Y/N）                        
	public char isprint; 			// 是否打印小票（Y/N）                
	public char isdisp; 			// 是否连接顾客显示牌（Y/N）        
	public char iskey;	 			// 是否有钥匙控制（Y/N）                
    public char printfs; 			// 打印小票的方式，1-边扫描边打印、2-付款时打印        
    public char sswrfs; 			// 收银截断方式，0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元
    public double printxe; 			// 打印小票的最低限额                        
    public int datatime; 			// 保留历史小票的天数                        
    public long dataspace; 			// 保留数据所须的空闲磁盘空间        
    public String ipaddr; 			// 设定的IP地址,检查是否与当前IP匹配
    public String priv; 			// 保留标志
    public String syjplacemkt;		// 收银机所属物理门店号(大门店模式对应ManaFrame)
    public String str1;             // 备用字段1
    public String str2;             // 备用字段2
    public String str3;             // 备用字段3
}
