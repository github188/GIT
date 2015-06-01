package com.efuture.commonKit;



public class ExpressionDeal
{
	//private static final int None =0;	//无 
	//private static final int Add = 1;	//加号 
	//private static final int Dec = 2;	//减号 
	//private static final int Mul = 3;	//乘号 
	//private static final int Div = 4;	//除号 
	private static final int Sin = 5;	//正玄 
	private static final int Cos = 6;	//余玄 
	private static final int Tan = 7;	//正切 
	private static final int ATan = 8;	//余切 
	private static final int Sqrt = 9;	//平方根 
	private static final int Pow = 10;	//求幂
	private static final int Iif = 11;	//判断 
	
	public static String SpiltExpression(String strExpression) 
	{ 
		String strTemp=""; 
		String strExp=""; 
		
		if (strExpression == null || strExpression.trim().length() <= 0) return null;

		// 字符串操作表达式
		if (strExpression.trim().charAt(0) == '\'' || strExpression.trim().toUpperCase().startsWith("SUB"))
		{
			return StringExpress(strExpression); 
		}
		
		// 数值表达式
		while (strExpression.indexOf("(")!=-1)
		{ 
			strTemp=strExpression.substring(strExpression.lastIndexOf("(")+1,strExpression.length()); 
			strExp=strTemp.substring(0,strTemp.indexOf(")")); 

			//strExpression=strExpression.replace("("+strExp+")",String.valueOf(CalculateExpress(strExp))); 
			strExpression=replace(strExpression,"("+strExp+")",String.valueOf(CalculateExpress(strExp))); 
		} 

		if(strExpression.indexOf("+")!=-1 || strExpression.indexOf("-")!=-1 || 
		   strExpression.indexOf("*")!=-1 || strExpression.indexOf("/")!=-1 ||
		   strExpression.indexOf("\\")!=-1|| strExpression.indexOf("%")!=-1 ||
		   strExpression.toUpperCase().indexOf("IF?") != -1) 
		{ 
			strExpression=String.valueOf(CalculateExpress(strExpression)); 
		} 
		
		return strExpression; 
	} 
	
	private static double CalculateExpress(String strExpression) 
	{ 
		String strTemp=""; 
		String strTempB=""; 
		String strOne=""; 
		String strTwo=""; 
		double ReplaceValue=0; 
		int minus = 0;
		
		// 只有IF?，则添加操作符进行函数运算
		if (strExpression.trim().toUpperCase().startsWith("IF?")) strExpression = "0 + " + strExpression;
		
		while (strExpression.indexOf("+")!=-1 || strExpression.indexOf("-",minus)!=-1 || 
			   strExpression.indexOf("*")!=-1 || strExpression.indexOf("/")!=-1 ||
			   strExpression.indexOf("\\")!=-1|| strExpression.indexOf("%")!=-1) 
		{ 
			if (strExpression.indexOf("*")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("*")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("*")); 
				
				strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetNextPos(strTemp)); 
				
				ReplaceValue=parseDouble(GetExpType(strOne)) * parseDouble(GetExpType(strTwo)); 
				
				//strExpression=strExpression.replace(strOne+"*"+strTwo,String.valueOf(ReplaceValue)); 
				strExpression=replace(strExpression,strOne+"*"+strTwo,String.valueOf(ReplaceValue));
			} 
			else if (strExpression.indexOf("/")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("/")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("/")); 
	
				strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetNextPos(strTemp)); 

				ReplaceValue=parseDouble(GetExpType(strOne)) / parseDouble(GetExpType(strTwo)); 
				
				//strExpression=strExpression.replace(strOne+"/"+strTwo,String.valueOf(ReplaceValue)); 
				strExpression=replace(strExpression,strOne+"/"+strTwo,String.valueOf(ReplaceValue));
			} 
			else if (strExpression.indexOf("\\")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("\\")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("\\")); 
	
				strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetNextPos(strTemp)); 

				ReplaceValue=ManipulatePrecision.integerDiv(parseDouble(GetExpType(strOne)),parseDouble(GetExpType(strTwo))); 
				
				//strExpression=strExpression.replace(strOne+"\\"+strTwo,String.valueOf(ReplaceValue)); 
				strExpression=replace(strExpression,strOne+"\\"+strTwo,String.valueOf(ReplaceValue));
			} 
			else if (strExpression.indexOf("%")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("%")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("%")); 
	
				strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetNextPos(strTemp)); 

				ReplaceValue=parseDouble(GetExpType(strOne)) % parseDouble(GetExpType(strTwo)); 
				
				//strExpression=strExpression.replace(strOne+"%"+strTwo,String.valueOf(ReplaceValue)); 
				strExpression=replace(strExpression,strOne+"%"+strTwo,String.valueOf(ReplaceValue));
			} 			
			else if (strExpression.indexOf("+")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("+")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("+")); 
	
				strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetNextPos(strTemp)); 
	
				ReplaceValue=parseDouble(GetExpType(strOne)) + parseDouble(GetExpType(strTwo)); 
	
				//strExpression=strExpression.replace(strOne+"+"+strTwo,String.valueOf(ReplaceValue)); 
				strExpression=replace(strExpression,strOne+"+"+strTwo,String.valueOf(ReplaceValue));
			} 
			else if (strExpression.indexOf("-")!=-1) 
			{ 
				strTemp=strExpression.substring(strExpression.indexOf("-")+1,strExpression.length()); 
				strTempB=strExpression.substring(0,strExpression.indexOf("-")); 
				
				// 前面没有字符说明是负号,跳过
				if (strTempB.trim().length() <= 0)
				{
					minus = strExpression.indexOf("-",minus) + 1;
				}
				else
				{
					minus = 0;
					
					strOne=strTempB.substring(GetPrivorPos(strTempB)+1,strTempB.length()); 
					strTwo=strTemp.substring(0,GetNextPos(strTemp)); 
		
					ReplaceValue=parseDouble(GetExpType(strOne)) - parseDouble(GetExpType(strTwo)); 
		
					//strExpression=strExpression.replace(strOne+"-"+strTwo,String.valueOf(ReplaceValue)); 
					strExpression=replace(strExpression,strOne+"-"+strTwo,String.valueOf(ReplaceValue));
				}
			} 
		}
		
		return parseDouble(strExpression); 
	} 
	
	private static double CalculateExExpress(String strExpression,int ExpressType) 
	{ 
		double retValue=0; 
		 
		switch(ExpressType) 
		{ 
			case Sin: 
				retValue=Math.sin(parseDouble(strExpression)); 
				break; 
	
			case Cos: 
				retValue= Math.cos(parseDouble(strExpression)); 
				break; 
			 
			case Tan: 
				retValue= Math.tan(parseDouble(strExpression)); 
				break; 

			case ATan: 
				retValue= Math.atan(parseDouble(strExpression)); 
				break; 

			case Sqrt: 
				retValue= Math.sqrt(parseDouble(strExpression)); 
				break; 
			 
			case Pow: 
				retValue= Math.pow(parseDouble(strExpression),2); 
				break; 
			case Iif:
			{
				boolean ok = false;
				String[] s = strExpression.split("!");	//s[0]=判断条件,s[1]=true,s[2]=false
				if (s.length >= 3)
				{
					String exp = s[0].toUpperCase();
					if (exp.indexOf("LS") >= 0)
					{
						String[] v = exp.split("LS");
						if (v.length >= 2 && parseDouble(v[0]) < parseDouble(v[1])) ok = true;
						else ok = false;
					}
					else 
					if (exp.indexOf("GR") >= 0)
					{
						String[] v = exp.split("GR");
						if (v.length >= 2 && parseDouble(v[0]) > parseDouble(v[1])) ok = true;
						else ok = false;
					}
					else 
					if (exp.indexOf("EQ") >= 0)
					{
						String[] v = exp.split("EQ");
						if (v.length >= 2 && parseDouble(v[0]) == parseDouble(v[1])) ok = true;
						else ok = false;
					}
					else 
					if (exp.indexOf("LE") >= 0)
					{
						String[] v = exp.split("LE");
						if (v.length >= 2 && parseDouble(v[0]) <= parseDouble(v[1])) ok = true;
						else ok = false;
					}
					else
					if (exp.indexOf("GE") >= 0)
					{
						String[] v = exp.split("GE");
						if (v.length >= 2 && parseDouble(v[0]) >= parseDouble(v[1])) ok = true;
						else ok = false;
					}
					else
					if (exp.indexOf("NE") >= 0)
					{
						String[] v = exp.split("NE");
						if (v.length >= 2 && parseDouble(v[0]) != parseDouble(v[1])) ok = true;
						else ok = false;
					}
					
					// 条件判断为真
					if (ok) retValue= parseDouble(s[1]);
					else retValue= parseDouble(s[2]);
				}
				break; 
			}
		} 
		 
		if (retValue==0) return parseDouble(strExpression); 
		return retValue; 
	} 
	
	private static int GetNextPos(String strExpression) 
	{ 
		int[] ExpPos = new int[6]; 
	
		if (strExpression.trim().toUpperCase().startsWith("IF?")) return strExpression.length();
		
		ExpPos[0]=strExpression.indexOf("+"); 
		ExpPos[1]=strExpression.indexOf("-"); 
		ExpPos[2]=strExpression.indexOf("*"); 
		ExpPos[3]=strExpression.indexOf("/"); 
		ExpPos[4]=strExpression.indexOf("\\");
		ExpPos[5]=strExpression.indexOf("%");
		
		int tmpMin=strExpression.length(); 
		for (int count=1;count<=ExpPos.length;count++) 
		{ 
			if (tmpMin>ExpPos[count-1] && ExpPos[count-1]!=-1) 
			{ 
				tmpMin=ExpPos[count-1]; 
			} 
		} 
		return tmpMin; 
	} 
	
	private static int GetPrivorPos(String strExpression) 
	{ 
		int[] ExpPos=new int[6]; 

		// 负号,IF?
		if (strExpression.trim().charAt(0) == '-') return -1;
		if (strExpression.trim().toUpperCase().startsWith("IF?")) return -1;
		
		// 
		ExpPos[0]=strExpression.lastIndexOf("+"); 
		ExpPos[1]=strExpression.lastIndexOf("-"); 
		ExpPos[2]=strExpression.lastIndexOf("*"); 
		ExpPos[3]=strExpression.lastIndexOf("/"); 
		ExpPos[4]=strExpression.lastIndexOf("\\");
		ExpPos[5]=strExpression.lastIndexOf("%");
		
		int tmpMax=-1; 
		for (int count=1;count<=ExpPos.length;count++) 
		{ 
			if (tmpMax<ExpPos[count-1] && ExpPos[count-1]!=-1) 
			{ 
				tmpMax=ExpPos[count-1]; 
			} 
		} 
		
		return tmpMax; 
	} 

	private static String GetExpType(String strExpression) 
	{ 
		strExpression=strExpression.toUpperCase();
		
		if (strExpression.indexOf("SIN")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("N")+1,strExpression.length()),Sin));
		}

		if (strExpression.indexOf("COS")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("S")+1,strExpression.length()),Cos));
		} 

		if (strExpression.indexOf("TAN")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("N")+1,strExpression.length()),Tan)); 
		} 

		if (strExpression.indexOf("ATAN")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("N")+1,strExpression.length()),ATan));
		} 

		if (strExpression.indexOf("SQRT")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("T")+1,strExpression.length()),Sqrt)); 
		} 

		if (strExpression.indexOf("POW")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("W")+1,strExpression.length()),Pow));
		} 

		if (strExpression.indexOf("IF?")!=-1) 
		{ 
			return String.valueOf(CalculateExExpress(strExpression.substring(strExpression.indexOf("?")+1,strExpression.length()),Iif));
		} 
		
		return strExpression; 
	} 
	
	private static double parseDouble(String str)
	{
		try
		{
			if (str == null || str.trim().length() <= 0) return 0;
			return Double.parseDouble(str.trim());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}
	
	public static String StringExpress(String strExpression)
	{
		String strTemp=""; 
		String strTempB=""; 
		String strOne=""; 
		String strTwo=""; 
		String ReplaceValue = "";
		
		// 只有SUBSTR则添加操作符进行函数运算
		strExpression = strExpression.trim();
		if (strExpression.toUpperCase().startsWith("SUB")) strExpression = "'' + " + strExpression;
		
		// dh为逗号计数
		int dh = 0;
		for (int i=0;i<strExpression.length();i++)
		{
			if (strExpression.charAt(i) == '\'') { dh++; if (dh >= 2) dh = 0; }
			if (strExpression.charAt(i) == '+' && dh == 0)
			{ 
				strTemp=strExpression.substring(i+1,strExpression.length()); 
				strTempB=strExpression.substring(0,i); 
	
				strOne=strTempB.substring(GetStrPos(strTempB,"Privor")+1,strTempB.length()); 
				strTwo=strTemp.substring(0,GetStrPos(strTemp,"Next")); 
				
				ReplaceValue="'"+replace(GetExpStr(strOne),"'","") + replace(GetExpStr(strTwo),"'","")+"'";
	
				strExpression=replace(strExpression,strOne+"+"+strTwo,ReplaceValue);
				i = -1;
			} 
		}
		
		return strExpression;
	}
	
	private static String GetExpStr(String strExpression) 
	{ 
		String exp = strExpression.trim();
		
		// sub('12',3,4)
		if (exp.toUpperCase().startsWith("SUB"))
		{
			int p = exp.lastIndexOf(",");
			if (p < 0) return exp;
			String end = exp.substring(p+1,exp.lastIndexOf(")"));
			String start = exp.substring(exp.lastIndexOf(",",p-1)+1,p);
			String substr = exp.substring(exp.indexOf("(")+1,exp.lastIndexOf(",",p-1));
			
			// 检查原串是否需要运算
			int dh = 0;
			for (int i=0;i<substr.length();i++)
			{
				if (substr.charAt(i) == '\'') { dh++; if (dh >= 2) dh = 0; }
				if (substr.charAt(i) == '+' && dh == 0)
				{
					substr = StringExpress(substr);
					break;
				}
			}
			substr = substr.trim();
			substr = substr.substring(1,substr.length()-1);
			int s = (int)parseDouble(SpiltExpression(start));
			int e = (int)parseDouble(SpiltExpression(end));
			if (e > substr.length()) e = substr.length();
			
			return "'"+substr.substring(s,e)+"'";
		}
		
		return exp;
	}
	
	private static int GetStrPos(String strExpression,String dir) 
	{ 
		if (dir.compareToIgnoreCase("Next") == 0)
		{
			if (strExpression.trim().toUpperCase().startsWith("SUB"))
			{
				int dh = 0;
				for (int i=0;i<strExpression.length();i++)
				{
					if (strExpression.charAt(i) == '\'') { dh++; if (dh >= 2) dh = 0; }
					if (strExpression.charAt(i) == ')' && dh == 0)
					{
						return i+1;
					}
				}
				return strExpression.length();
			}
			else
			{
				int pos = strExpression.indexOf('\'');
				pos = strExpression.indexOf('\'',pos+1);
				return pos+1;
			}
		}
		else
		{
			int pos = strExpression.lastIndexOf('\'');
			pos = strExpression.lastIndexOf('\'',pos-1);
			return pos-1;
		}
	} 

	// 不用JDK1.5的replace，确保JDK1.4也能用
	public static String replace(String source,String find,String replace)
	{
		return replace(source,find,replace,false);
	}
	
	public static String replace(String source,String find,String replace,boolean bIgnoreCase)
	{   
		if (!(source != null && source.length() > 0) || !(find != null && find.length() > 0)) return source;
		if (replace == null) replace = "";   
		
		StringBuffer sb = new StringBuffer(source);   
		StringBuffer mod;   
		boolean bDone = false;   
		int prevIndex = 0,currIndex = 0;   
		if (bIgnoreCase) 
		{   
			source = source.toLowerCase();   
			find = find.toLowerCase();   
		}
		mod = new StringBuffer(source);   
		while (!bDone)
		{   
			if ((currIndex = mod.toString().indexOf(find, prevIndex)) != -1)
			{   
				sb = sb.replace(currIndex,currIndex + find.length(),replace);   
				mod= mod.replace(currIndex,currIndex + find.length(),replace);   
				prevIndex = currIndex + replace.length();
			}
			else
			{   
				bDone = true;
			}   
		}   
		return (sb.toString());   
	}
}