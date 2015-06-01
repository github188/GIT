package bankpay.Bank;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

public class JNative_test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new JNative_test();
	}
	
	public JNative_test()
	{
			JNative jn;
			try
			{
				// dll的说明
				/**
				动态链接库名：ChaseInterface.dll
				入口函数名：	Abmcs
				函数声明：		int Abmcs(void *strIn, void *strOut)
				参数说明：		strIn	—交易请求包(输入),详见输入字符串定义说明
				          		strOut	—交易返回包(输出),详见输出字符串定义说明
				*/
				jn = new JNative("C:\\jnative\\ChaseInterface.dll","Abmcs");
				jn.setRetVal(Type.INT);//设置返回的类型
				Pointer p = new Pointer(MemoryBlockFactory.createMemoryBlock(16*6)); //创建指针
				jn.setParameter(0,"111111111111111111111111111111111111111111111111111111111111111111111"); //传入值
				jn.setParameter(1,p); 
				jn.invoke(); 
				System.out.println(jn.getRetValAsInt()+"```````````"+getValueFromPoniter(p)); //显示指针的返回值
			}
			catch (NativeException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	public String getValueFromPoniter(Pointer p){//就这我怎么也去不到指针里的值，一个char型指针到底要怎么取值啊 
		String c = "::"; 

		try
		{
			c =p.getAsString();
		}
		catch (NativeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return c; 
	}  

}
