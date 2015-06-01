package com.efuture.javaPos.Test;

/**
 * @author yinl
 * @create 2010-3-12 上午03:08:55
 * @descri 文件说明
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

//import com.sun.tools.javac.Main;

// 定义一个接口, 用来转换各种数学符号为Java类库中的表达式. 
interface IOperator 
{    
	String SIN = "sin";    
	String COS = "cos";    
	String TAN = "tan";    
	String ASIN = "asin";    
	String ACOS = "acos";    
	String ATAN = "atan";    
	String EXP = "exp";    
	String LOG = "log";    
	String POW = "pow";    
	String SQRT = "sqrt";    
	String FABS = "fabs";    
	String MINUS = "minus";    
	
	String J_SIN = "Math.sin";    
	String J_COS = "Math.cos";    
	String J_TAN = "Math.tan";    
	String J_ASIN = "Math.asin";    
	String J_ACOS = "Math.acos";    
	String J_ATAN = "Math.atan";    
	String J_EXP = "Math.exp";    
	String J_LOG = "Math.log10";    
	String J_POW = "Math.pow";    
	String J_SQRT = "Math.sqrt";    
	String J_FABS = "Math.abs";    
}

/**   
* 利用Simpson公式计算积分,在输入被积公式时候请注意使用如下格式.   
* 1.只使用圆括号() , 没有别的括号可以使用.如: 1/(1+sin(x))   
* 2.在输入超越函数的时候,变量和数值用括号扩起来 如:sin(x) 而不要写为 sinx   
* 3.在两个数或者变量相乘时候,不要省略乘号* 如:2*a 不要写为 2a   
* 4.在写幂运算的时候,请使用如下格式:    
* 利用动态编译来计算Simpson积分,使用该方法 编程相对简单,运行效率有点慢.   
* @author icerain   
*   
*/   
public class ExpressionCalc implements IOperator 
{    
	public static void main(String[] args) throws Exception 
	{    
		ExpressionCalc sim = new ExpressionCalc();   
		long start = System.currentTimeMillis();
		System.out.println("Start:" + start);
		System.out.println(sim.Calc("0+(1-5)"));
		long end = System.currentTimeMillis();
		System.out.println("End  :" + end + "   耗时:"+(end - start));
		System.exit(0);    
	}    

	public ExpressionCalc()
	{      
	}    

	public String Calc(String source)
	{      
		String expression = source; 
		expression = getSourceCode(source,null,null);
		
		double result = 0;    
		try 
		{    
			result = run(compile(expression));    
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return String.valueOf(result);
	}    
	
	/**    
	* 得到用户输入表达式转换为Java中的可计算表达式的函数   
	* @param ex 输入的表达式 如: 1/(1 + sin(x))    
	* @param var 表达式中的变量 如: x   
	* @param value 变量的取值 如: 4.3   
	* @return Java中可以直接计算的表达式 如: 1/(1 + Math.sin(x))   
	*/   
	private String getSourceCode(String ex, String var, String value) 
	{    
		String expression = ex;    

		// 计算多个变量的函数的时候使用
		if (var != null) expression = expression.replaceAll(var, value);    
	  
		//处理数学符号    
		if (expression.contains(SIN)) {    
			expression = expression.replaceAll(SIN, J_SIN);    
		} else if (expression.contains(COS)) {    
			expression = expression.replaceAll(COS, J_COS);    
		} else if (expression.contains(TAN)) {    
			expression = expression.replaceAll(TAN, J_TAN);    
		} else if (expression.contains(ASIN)) {    
			expression = expression.replaceAll(ASIN, J_ASIN);    
		} else if (expression.contains(ACOS)) {    
			expression = expression.replaceAll(ACOS, J_ACOS);    
		} else if (expression.contains(ATAN)) {    
			expression = expression.replaceAll(ATAN, J_ATAN);    
		} else if (expression.contains(EXP)) {    
			expression = expression.replaceAll(EXP, J_EXP);    
		} else if (expression.contains(LOG)) {    
			expression = expression.replaceAll(LOG, J_LOG);    
		} else if (expression.contains(POW)) {    
			expression = expression.replaceAll(POW, J_POW);    
		} else if (expression.contains(SQRT)) {    
			expression = expression.replaceAll(SQRT, J_SQRT);    
		} else if (expression.contains(FABS)) {    
			expression = expression.replaceAll(FABS, J_FABS);    
		}    
	
		return expression;    
	}    

	/** 编译JavaCode，返回java文件*/   
	private synchronized File compile(String code) throws Exception 
	{    
		File file;    
		
		// 创建一个临时java源文件,当Jvm 退出时 删除该文件    
		file = File.createTempFile("JavaRuntime", ".java", new File(System.getProperty("user.dir")));    
		file.deleteOnExit();    
	
		// 得到文件名和类名    
		String filename = file.getName();    
		String classname = getClassName(filename);  
		
		// 将代码输出到源代码文件中,动态构造一个类,用于计算    
		PrintWriter out = new PrintWriter(new FileOutputStream(file));    
		out.write("public class " + classname + "{" + "public static double main1(String[] args)" + "{");    
		out.write("double result = " + code + ";");    
		out.write("return new Double(result);");    
		out.write("}}");    

		//关闭文件流    
		out.flush();    
		out.close();    

		//设置编译参数    
		//Process process = Runtime.getRuntime().exec("javac " + filename);    
		//System.out.println(process.getOutputStream().toString());    
		//String[] args = new String[] { "-d", System.getProperty("user.dir"),filename };    
		//int status = Main.compile(args);    
		//System.out.println("Compile Status: " + status);
		//输出运行的状态码.    
		//    状态参数与对应值     
		//    　　EXIT_OK 0     
		//    　　EXIT_ERROR 1     
		//    　　EXIT_CMDERR 2     
		//    　　EXIT_SYSERR 3     
		//    　　EXIT_ABNORMAL 4    
		return file;    
	}    

	/**   
	* 运行程序 如果出现Exception 则不做处理 抛出!   
	* @param file 运行的文件名   
	* @return 得到的Simpson积分公式的结果   
	* @throws Exception 抛出Exception 不作处理   
	*/   
	private synchronized double run(File file) throws Exception 
	{    
		String filename = file.getName();    
		String classname = getClassName(filename);    
		Double tempResult = null;    

		//当Jvm 退出时候 删除生成的临时文件    
		new File(file.getParent(),classname + ".class").deleteOnExit();    
		try 
		{    
			Class cls = Class.forName(classname);    
			
			// 映射main1方法    
			Method calculate = cls.getMethod("main1", new Class[] { String[].class });    
	
			//执行计算方法 得到计算的结果    
			tempResult = (Double) calculate.invoke(null,new Object[] { new String[0] });    
		} 
		catch (Exception ex) 
		{    
			ex.printStackTrace(); 
		}    
	
		//返回值    
		return tempResult.doubleValue();    
	}

	/** 得到类的名字 */   
	private String getClassName(String filename) 
	{    
		return filename.substring(0, filename.length() - 5);    
	}
}    


