package com.efuture.javaPos.Struct;

public class ContentItemForTouchScrn
{

	private static final long serialVersionUID = 1L;
	public static String[] refCate = { "cateid", "catename","catelevelid","parentcateid" };
	//public static String[] refCateLocal = { "cateid", "catename","parentcateid", "islast" };
	
	public static String[] refGoods = { "barcode", "gz","code", "name","lsj", "hyj" };
	//public static String[] refGoodsLocal = { "barcode", "gz","code", "name","lsj", "hyj" };
	
	public String barcode;			// 商品条码,主键				
	public String gz;				// 商品柜组,主键,唯一键			
	public String code;				// 商品编码,唯一键
	public String name;			//商品名称
	public double lsj;				// 零售价					
	public double hyj;				// 会员价
	
	public int cateid; 				// 当前序号
	public String catename; 	//当前类别名称
	public int catelevelid;		//当前Id
	public int parentcateid; 	 // 父类Id
	
	public String aliasesName; // 别名，用于显示在按钮上的文本信息

}
