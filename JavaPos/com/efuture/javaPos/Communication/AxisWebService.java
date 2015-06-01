package com.efuture.javaPos.Communication;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.WebServiceConfigClass;
import com.efuture.javaPos.Struct.WebServiceFunDef;

public class AxisWebService implements Serializable
{
	private static final long serialVersionUID = -4837984362078575710L;

	private Service service = null;
	private Call call = null;
	private boolean isConn = false;

	public AxisWebService()
	{

	}

	public static void main(String args[])
	{
		test();
	}

	public static void test()
	{
		// String endpoint =
		// "http://219.140.199.46:7000/axis/services/test?wsdl";
		try
		{
			String endpoint = "http://localhost:8080/WebServiceTest/services/HelloService";// "http://219.140.199.46:7000/axis/services/test";
			
			endpoint = "http://192.1.33.41:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=EFUT&receiverParty=&receiverService=&interface=SI_CREDIT_INFO_OUT_SYN&interfaceNamespace=urn%3Acb%3Aefut%3Acredit_limit_info_in";
			
			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setUsername("EFUT_USER");
			call.setPassword("123456");

			call.setTargetEndpointAddress(endpoint);

			// call.setOperationName(new
			// QName("http://219.140.199.46:7000/axis/services/test",
			// "getcard"));

			//call.setOperationName(new QName("http://localhost:8080/WebServiceTest/services/HelloService?wsdl", "callProc"));

			call.setOperationName(new QName("urn:cb:efut:credit_limit_info_in", "SI_CREDIT_INFO_OUT_SYN"));

			call.addParameter("KUNNR", org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN); // 接口的参数
			//call.addParameter("lx", org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN); // 接口的参数
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); // 设置返回类型
			// 查询
			String result = (String) call.invoke(new Object[] {"32"});
			// 交易
			// String result = (String) call.invoke(new Object[] {
			// "7375371930154213;1;4;1;WP;1;1001;GG01", "write_CZK" });
			// 给方法传递参数，并且调用方法
			System.out.println("result is " + result);

			/*
			 * call.addParameter("cardid",
			 * org.apache.axis.encoding.XMLType.XSD_STRING,
			 * javax.xml.rpc.ParameterMode.IN);
			 * 
			 * call.addParameter("money",
			 * org.apache.axis.encoding.XMLType.XSD_DOUBLE,
			 * javax.xml.rpc.ParameterMode.IN);
			 * 
			 * call.addParameter("thdh",
			 * org.apache.axis.encoding.XMLType.XSD_DOUBLE,
			 * javax.xml.rpc.ParameterMode.IN);
			 * 
			 * call.setReturnType(org.apache.axis.encoding.XMLType.XSD_INT);
			 * 
			 * call.setUseSOAPAction(true);
			 * 
			 * Integer i = (Integer) call.invoke(new Object[] { new
			 * String("2311083859953414"), "100.0", new Double(100) });
			 * 
			 * System.out.println("result is " + i);
			 */

		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}

	public boolean createWebServerConn()
	{
		try
		{
			if (WebServiceConfigClass.getDefault().getEndPoint() == null || WebServiceConfigClass.getDefault().getEndPoint().equals(""))
				return false;

			// 创建service
			service = new Service();

			// 创建call
			call = (Call) service.createCall();
			call.setMaintainSession(true);
			call.setTimeout(new Integer(WebServiceConfigClass.getDefault().getWebServicetimeout()));

			// 设置访问点
			call.setTargetEndpointAddress(new java.net.URL(WebServiceConfigClass.getDefault().getEndPoint()));

			call.setUseSOAPAction(true);

			isConn = true;

			return isConn;
		}
		catch (Exception ex)
		{
			System.err.println(ex.toString());

			return false;
		}
	}

	public boolean isConn()
	{
		return isConn;
	}

	// para[]:{"参数名,参数类型,参数值","参数名,参数类型,参数值"}
	public Object executeFunction(String para[])
	{
		return null;
	}

	public Object executeFunction(int cmdcode)
	{
		return executeFunction(cmdcode, null);
	}

	public Object executeFunction(int cmdcode, Object object)
	{
		return executeFunction(cmdcode, object, null, null);
	}

	public Object executeFunction(int cmdcode, String para[])
	{
		return executeFunction(cmdcode, null, para, null);
	}

	public Object executeFunction(int cmdcode, String para[], String returntype)
	{
		return executeFunction(cmdcode, null, para, returntype);
	}

	// 执行axis方法
	public Object executeFunction(int cmdcodeid, Object object, String para[], String returntype)
	{
		try
		{
			String cmdcode = String.valueOf(cmdcodeid);

			if (cmdcode == null || cmdcode.equals(""))
				return null;

			if (WebServiceConfigClass.getDefault().getFunList() == null)
				return null;

			if (WebServiceConfigClass.getDefault().getFunList().size() <= 0)
				return null;

			if (!WebServiceConfigClass.getDefault().getFunList().containsKey(cmdcode))
			{
				new MessageBox(Language.apply("配置文件中没有定义当前命令!"));
				return null;
			}

			// 设置传入参数
			if (!setParameter(cmdcode, para))
			{
				new MessageBox(Language.apply("设置参数失败!"));
				return null;
			}

			// 设置返回参数
			setReturnValue(cmdcode, returntype);

			return getExecute(cmdcode, object, para);
		}
		catch (Exception ex)
		{
			System.err.println(ex.toString());
			new MessageBox(Language.apply("调用WebService最外层异常!"));

			return null;
		}
		finally
		{
			if (call != null)
			{
				call.removeAllParameters();
				call.clearOperation();
				call.clearHeaders();
			}
		}
	}

	private boolean setParameter(String cmdcode, String para[])
	{
		try
		{
			WebServiceFunDef wsfd = (WebServiceFunDef) WebServiceConfigClass.getDefault().getFunList().get(cmdcode);

			if ((para == null || para.length <= 0) && wsfd.getJavaPosMappingName().length() <= 0 && wsfd.getParameterName().length() <= 0 && wsfd.getParameterType().length() <= 0) { return true; }

			if ((wsfd.getParameterName().split(",")).length != (wsfd.getParameterType().split(",")).length)
			{
				new MessageBox(Language.apply("传入参数与类型不匹配!"));

				return false;
			}

			if (wsfd.getParameterName().length() > 0 && wsfd.getParameterType().length() > 0)
			{
				String paraname[] = wsfd.getParameterName().split(",");
				String paratype[] = wsfd.getParameterType().split(",");

				for (int i = 0; i < paraname.length; i++)
				{
					addParameter(paratype[i].charAt(0), paraname[i]);
				}
			}

			if (para != null && para.length >= 3)
			{
				for (int i = 0; i < para.length; i++)
				{
					String[] str = para[i].split(",");

					addParameter(str[1].charAt(0), str[0]);
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	private void addParameter(char type, String paraname)
	{
		switch (type)
		{
			case 'i':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_INT, ParameterMode.IN);
				break;
			case 'l':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_LONG, ParameterMode.IN);
				break;
			case 'c':
			case 's':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_STRING, ParameterMode.IN);
				break;
			case 'f':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_FLOAT, ParameterMode.IN);
				break;
			case 'd':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_DOUBLE, ParameterMode.IN);
				break;
			case 'b':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_BOOLEAN, ParameterMode.IN);
				break;
			case 't':
				call.addParameter(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), paraname), XMLType.XSD_DATETIME, ParameterMode.IN);
				break;
		}
	}

	private void setReturnValue(String cmdcode, String returntype)
	{
		char type = ' ';
		try
		{
			WebServiceFunDef wsfd = (WebServiceFunDef) WebServiceConfigClass.getDefault().getFunList().get(cmdcode);

			if ((wsfd.getParameterType() == null || wsfd.getParameterType().length() <= 0) && (returntype == null || returntype.length() <= 0))
				return;

			if (returntype != null && returntype.length() > 0)
			{
				type = returntype.charAt(0);
			}
			else
			{
				type = wsfd.getReturnType().charAt(0);
			}

			switch (type)
			{
				case 'i':
					call.setReturnType(XMLType.XSD_INT);
					break;
				case 'l':
					call.setReturnType(XMLType.XSD_LONG);
					break;
				case 'c':
				case 's':
					call.setReturnType(XMLType.XSD_STRING);
					break;
				case 'f':
					call.setReturnType(XMLType.XSD_FLOAT);
					break;
				case 'd':
					call.setReturnType(XMLType.XSD_DOUBLE);
					break;
				case 'b':
					call.setReturnType(XMLType.XSD_BOOLEAN);
					break;
				case 't':
					call.setReturnType(XMLType.XSD_DATETIME);
					break;
			}

		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("设置返回值异常!"));
			ex.printStackTrace();
		}
	}

	private Object getExecute(String cmdcode, Object object, String para[])
	{
		Object[] objectlist = null;
		Object reobject = null;
		long start = 0;

		try
		{
			WebServiceFunDef wsfd = (WebServiceFunDef) WebServiceConfigClass.getDefault().getFunList().get(cmdcode);

			if (wsfd.getMethodName() == null || wsfd.getMethodName().equals(""))
				return null;

			call.setOperationName(new QName(WebServiceConfigClass.getDefault().getNamingSpace(), wsfd.getMethodName()));

			if (object != null || para != null)
			{
				if ((objectlist = getObjectList(cmdcode, object, para)) == null)
					return null;
			}

			call.setSOAPActionURI(WebServiceConfigClass.getDefault().getNamingSpace() + wsfd.getMethodName());

			if (GlobalInfo.statusBar != null)
				GlobalInfo.statusBar.setHelpMessage(Language.apply("正在发送{0}号命令WebService", new Object[]{cmdcode}));

			start = System.currentTimeMillis();

			// 调试模式记录请求串备查
			if (ConfigClass.DebugMode)
			{
				String request = "";
				for (int i = 0; i < objectlist.length; i++)
				{
					request = request + String.valueOf(objectlist[i]) + ",";
				}

				System.out.println(Language.apply("请求WebService服务:") + wsfd.getMethodName() + " \r\n" + request.substring(0, request.length() - 1));
			}

			reobject = call.invoke(objectlist);

			if (ConfigClass.DebugMode)
			{
				if (reobject != null)
				{
					System.out.println(Language.apply("回复WebService服务:") + wsfd.getMethodName() + " \r\n" + reobject.toString());
				}
				else
				{
					System.out.println(Language.apply("回复WebService服务:") + wsfd.getMethodName() + " \r\nNULL");
				}
			}

			return reobject;
		}
		catch (Exception ex)
		{
			System.err.println(ex.toString());
			new MessageBox(Language.apply("调用WebService失败!") + ex.toString());

			return null;
		}
		finally
		{
			long end = System.currentTimeMillis();

			if (GlobalInfo.statusBar != null)
				GlobalInfo.statusBar.setHelpMessage(Language.apply("响应的{0}号命令WebService耗时: ", new Object[]{cmdcode}) + (end - start) + " ms");
		}
	}

	private Object[] getObjectList(String cmdcode, Object object, String para[])
	{
		String[] fieldStr = null;
		Field field1 = null;
		String[] paravalue = null;

		try
		{
			WebServiceFunDef wsfd = (WebServiceFunDef) WebServiceConfigClass.getDefault().getFunList().get(cmdcode);

			if (object.getClass() == String.class)
			{
				paravalue = new String[2];
				paravalue[0]=cmdcode;
				paravalue[1] = (String)object;
			}
			else
			{
				// eclipse提示此行代码无效
				//if ((wsfd.getJavaPosMappingName() == null || wsfd.getJavaPosMappingName().equals("")) && (para == null || para.length <= 0) && object == null) { return new Object[] {}; }

				String str = "";

				if (wsfd.getJavaPosMappingName() != null && !wsfd.getJavaPosMappingName().equals("") && object != null)
				{
					Class classobj = object.getClass();

					String name[] = wsfd.getJavaPosMappingName().split(",");

					for (int x = 0; x < name.length; x++)
					{
						fieldStr = name[x].trim().split("#");

						for (int i = 0; i < fieldStr.length; i++)
						{
							if (fieldStr[i].startsWith("$"))
							{
								str = str + fieldStr[i].substring(1) + ((i == 0) ? ";" : "");
							}
							else
							{
								field1 = classobj.getDeclaredField(fieldStr[i]);

								if (field1.get(object) != null && field1.get(object).toString().trim().length() > 0)
									str = str + field1.get(object).toString().trim() + ((i == fieldStr.length - 1) ? "" : ";");
								else
									str = str + " " + ";";
							}
						}
						if (x != name.length - 1)
							str = str + ",";
					}
				}

				if (para != null && para.length >= 3)
				{
					for (int i = 0; i < para.length; i++)
					{
						String[] parastr = para[i].split(",");

						str = str + parastr[2] + ",";

						wsfd.setParameterType(wsfd.getParameterType() + "," + parastr[1]);
					}
				}

				paravalue = (str.endsWith(";") ? str.substring(0, str.length() - 1).split(",") : str.substring(0).split(","));
			}
			Object obj[] = new Object[paravalue.length];

			String paratype[] = wsfd.getParameterType().split(",");

			for (int i = 0; i < paravalue.length; i++)
			{
				switch (paratype[i].charAt(0))
				{
					case 'i':
						obj[i] = new Integer(paravalue[i]);
						break;
					case 'l':
						obj[i] = new Long(paravalue[i]);
						break;
					case 'c':
					case 's':
						obj[i] = new String(paravalue[i]);
						break;
					case 'f':
						obj[i] = new Float(paravalue[i]);
						break;
					case 'd':
						obj[i] = new Double(paravalue[i]);
						break;
					case 'b':
						obj[i] = new Boolean(paravalue[i]);
						break;
					default:
						obj[i] = new String(paravalue[i]);
				}
			}

			return obj;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打包传送参数异常!") + ex.getMessage());
			return null;
		}
	}

	public boolean isCheckSuccess(int cmdcodeid, Object ret)
	{
		try
		{
			if (ret == null)
				return false;

			String cmdcode = String.valueOf(cmdcodeid);

			String success = ret.toString();

			WebServiceFunDef wsfd = (WebServiceFunDef) WebServiceConfigClass.getDefault().getFunList().get(cmdcode);

			if (wsfd.getRetSuccessError() == null || wsfd.getRetSuccessError().equals(""))
				return true;

			String successerres[] = wsfd.getRetSuccessError().split("\\|");

			if (successerres.length < 2)
			{
				new MessageBox(Language.apply("配置的成功与错误标志格式不正确!"));
				return false;
			}

			for (int i = 0; i < successerres.length; i++)
			{
				String successerr[] = successerres[i].split(",");

				switch (i)
				{
					case 0:
						for (int j = 0; j < successerr.length; j++)
						{
							String recode = successerr[j].split("-")[0];
							if (success.trim().equals(recode))
								return true;
						}
						break;
					case 1:
						for (int j = 0; j < successerr.length; j++)
						{
							String recode = successerr[j].split("-")[0];

							if (success.trim().equals(recode))
							{
								new MessageBox(Language.apply("WebService报错提示: ") + successerr[j]);
								return false;
							}
						}
						break;
					default:
						new MessageBox(Language.apply("调用WebService未知错误"));
						return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("解析成功和错误描述异常!") + ex.getMessage());
			return false;
		}
	}
}
