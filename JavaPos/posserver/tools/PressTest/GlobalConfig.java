package posserver.tools.PressTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalVar;

public class GlobalConfig {
	public static String identify = "test";
	public static String ip = "127.0.0.1";
	public static String port = "8080";
	public static String path = "";
	public static int connectTimeout = 1000;
	public static int receiveTimeout = 2000;
	public static String dataBaseUrl="./PosSrvPressure.db3";
	public static String isSendStatus = "N";
	public static int testTimer = 0;

	public static String srvUrl;
	public static int threadCount; // 线程数量
	public static int cmdDelaytime; // 命令延时时间
	public static int threadDelaytime; // 线程启动延时时间
	public static int exceptiondelaytime;// 线程遇到异常时停多长时间
	
	// 命令列表
	public static Vector cmdlist = new Vector();
	

	public static void loadConfig() {
		// 读取命令列表
		readCmdFile();
		
		// 读取倒置文件
		String line = "./Config.ini";
		System.out.println(line);
		if (!PathFile.fileExist(line))
			return;

		Vector v = CommonMethod.readFileByVector(line);

		if (v == null) {
			return;
		}

		for (int i = 0; i < v.size(); i++) {
			String[] row = (String[]) v.elementAt(i);
			if ("Identify".equalsIgnoreCase(row[0])) {
				identify = row[1];
			} else if ("ServerIP".equalsIgnoreCase(row[0])) {
				ip = row[1];
			} else if ("ServerPath".equalsIgnoreCase(row[0])) {
				path = row[1];
			} else if ("ServerPort".equalsIgnoreCase(row[0])) {
				port = row[1];
			} else if ("ConnectTimeout".equalsIgnoreCase(row[0])) {
				connectTimeout = Integer.parseInt(row[1]);
			} else if ("ReceiveTimeout".equalsIgnoreCase(row[0])) {
				receiveTimeout = Integer.parseInt(row[1]);
			} else if ("DataBaseUrl".equalsIgnoreCase(row[0])) {
				dataBaseUrl =  row[1];
			} else if("IsSendStatus".equalsIgnoreCase(row[0])){
				isSendStatus = row[1];
			} else if("TestTimer".equalsIgnoreCase(row[0])){
				testTimer = Convert.toInt(row[1]);
			}
			else {
				continue;
			}
		}
	}
	
	// 读取命令配置文件
	public static void readCmdFile()
	{
		if (!PathFile.fileExist("./TestCmd.ini"))
				return;
		
		BufferedReader br = CommonMethod.readFileGB2312("./TestCmd.ini");
		String line = "";
		String[] itemCmd = new String[2];
		boolean isLine = false;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(";") && line.length() > 1)
				{
					itemCmd[0] = line.substring(1, line.length());
					continue;
				}
				if (line.length() > 18 && line.indexOf("#@#") > 1)
				{
					itemCmd[1] = line.trim();
					isLine = true;
				}

				if (isLine)
				{
					cmdlist.add(itemCmd);
					itemCmd = new String[2];
					isLine = false;
				}
			}
			if (br != null)
				br.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
