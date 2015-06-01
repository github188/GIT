package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

public class MwicJJLS_ICCard implements Interface_ICCard
{	
	String track;
	String reqfile = "request.txt";
	String retfile = "result.txt";

/*	public static void main(String[] args){		

		MwicJJLS_ICCard id = new MwicJJLS_ICCard();
		String spl = id.splitCardno("10214300130010000015000000000000000000000019480040000000");
		System.out.println(spl);
		
		String track1 = "10214300130010000015000000000000000000000019480040000000";
		String temp1 = track1.substring(0,40);
		String temp2 = String.valueOf((long) ManipulatePrecision.doubleConvert(1948.00 * 100, 2, 1));
		temp2 = Convert.increaseCharForward(temp2, '0', 8);	         
		String temp3 = id.encodecard(temp2);
		String temp4 = track1.substring(49);
		
		track1 = temp1 + temp2 + temp3 + temp4;
		String line = track1;
	}*/
	
	public boolean open()
	{
		return true;
	}

	public boolean close()
	{
		return true;
	}

	public String findCard()
	{
		//System.out.println("MwicJJLS_ICCard 0");
		//new MessageBox("测试!");
		
		if (DeviceName.deviceICCard.length() <= 0)
			return null;

		try
		{
			track = execute("read","1","");
			
			return splitCardno(track);
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("IC卡读卡调用异常\n\n" + ex.getMessage());
			return null;
		}
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{

		/*
		 * __str1 := '1'; //写卡标志 1 
		 * __str1 := __str1+ '02'; //储值卡 2-3 
		 * __str1 :=
		 * __str1+ __slength ;//储值卡长度 4-5 
		 * __str1 := __str1+ s_id ;//14位卡号 6-19
		 * __str1 := __str1+ encodecard(s_id) ;//卡号校验位 20 
		 * __str1 := __str1 +pad0r('0',__padlength) ;//空白 21-30 10 
		 * __str1 := __str1+ __stemp; //积分 31-38 4 
		 * __str1 := __str1+ encodecard(__stemp); // 积分校验位 39 
		 * __str1:= __str1+ '0' ;// 空白 40 
		 * __str1 := __str1+ sle_track4; // 剩余金额 41-48
		 * __str1 := __str1+ encodecard(sle_track4) ;// 剩余金额校验位 49 
		 * __str1 := __str1+ '0'; // 空白 50 // 
		 * __str1 := __str1+ '151201'; // 卡密码 51-56
		 * __str1 := __str1+ '000000'; // 卡密码 51-56
		 */
		
		if (DeviceName.deviceICCard.length() <= 0)
			return null;
		
		try
		{
			String track1 = execute("read","0","");
			
			//String track1 = track;
			
			if (!track.equals(track1)){
				new MessageBox("读卡器中的卡与要交易的卡信息不一至，请检查读卡器中的卡是否存在变动!");
				return null;
			}
			
			String temp1 = track1.substring(0,40);
			String temp2 = String.valueOf((long) ManipulatePrecision.doubleConvert(ye * 100, 2, 1));
			temp2 = Convert.increaseCharForward(temp2, '0', 8);	
			String temp3 = encodecard(temp2);
			String temp4 = track1.substring(49);
			
			track1 = temp1 + temp2 + temp3 + temp4;
			
			String line = track1;
			
			String result =	execute("write","1",line);
			if (result == null) return null;
			
			track = execute("read","0",line);
			
			result = splitCardno(track);
			
			return result;
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("IC卡读卡调用异常\n\n" + ex.getMessage());
			return null;
		}
		
	}
	
	private String execute(String oprtype,String issucbeep,String content)
	{
		try
		{
/*			SimpleDateFormat dateformat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"); 
			 
			Date startdate = new Date();*/
			
			//System.err.println("test:" + oprtype + ";" + content);
			
			StringBuffer line = new StringBuffer();
			
			String[] arg = DeviceName.deviceICCard.split(",");

			line.append(oprtype);
			line.append(",");
			
			if (arg.length > 0)
				line.append(arg[0]);
			else
				line.append("0");
			line.append(",");

			if (arg.length > 1)
				line.append(arg[1]);
			else
				line.append("56");
			line.append(",");

			if (arg.length > 2)
				line.append(arg[2]);
			else
				line.append("40");
            
			// 执行成功后是否鸣响
			line.append(",");
			line.append(issucbeep);
			
			// 内容
			line.append(",");
			line.append(content);

			// 先删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);

				if (PathFile.fileExist(reqfile))
				{
					new MessageBox("读卡请求文件 " + reqfile + " 无法删除,请重试");
					return null;
				}
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);

				if (PathFile.fileExist(retfile))
				{
					new MessageBox("读卡结果文件 " + retfile + " 无法删除,请重试");
					return null;
				}
			}

			// 写入请求
			PrintWriter pw = CommonMethod.writeFile(reqfile);
			pw.write(line.toString());
			pw.flush();
			pw.close();

			if (!PathFile.fileExist(reqfile))
			{
				new MessageBox("未成功生成request.txt请求文件!");
				return null;
			}

			// 调用接口模块
			CommonMethod.waitForExec("javaposic.exe JJLS");

			// 读取应答
			BufferedReader br = null;
			if (!PathFile.fileExist(retfile) || ((br = CommonMethod.readFileGBK(retfile)) == null))
			{
				new MessageBox("读卡结果文件数据读取失败!");
				return null;
			}
			
			String data = br.readLine();
			br.close();
			
			if (data == null || data.length() <= 0 ){
				new MessageBox("读卡结果文件数据读取失败!");
				return null;
			}
			
			String[] art1 = data.split(",");
			if (art1.length < 2 || !art1[0].equals("00")){
				new MessageBox("返回数据校验失败!");
				return null;
			}
			
			data = art1[1];
			
			//System.err.println("test:" + oprtype + ";" + content + "," + art1[0] + "," + art1[1]);
			
/*			Date enddate = new Date();
			try{
				long times = startdate.getTime()-enddate.getTime();
				double between=(double)times/1000;
				PosLog.getLog(getClass()).debug(oprtype + "时间:" + between + ";开始:" + dateformat2.format(startdate) + ";结束:" + dateformat2.format(enddate));
			}catch(Exception ex){
				
			}*/
			
			return data;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			// 删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);
			}
		}
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { "IC卡调用命令" });
		v.add(new String[] { "IC卡请求文件" });
		v.add(new String[] { "IC卡应答文件" });

		return v;
	}

	public String getDiscription()
	{

		return "江西联盛IC卡设备";
	}

	private String splitCardno(String track)
	{
		if (track == null || track.equals(""))
			return null;

		// 卡号，金额，积分
		String card,money,point;

		try
		{
			String cardflag = track.substring(0, 1);
			String cardtype = track.substring(1, 3);
			String cardlength = track.substring(3, 5);
			int length = 0;

			if (cardflag.equals("0"))
				length = Convert.toInt(cardlength.substring(1, 2));
			else
				length = Convert.toInt(cardlength);

			card= track.substring(5, 5+length); // 卡号

			// 积分卡
			if (cardtype.equals("01"))
			{
				String tmp = track.substring(30, 38);
				String tmp1 = track.substring(38, 39);
				if (!tmp1.equals(encodecard(tmp))){
					new MessageBox("校验位不正确!");
					return null;
				}
				
				money = String.valueOf("0.00"); // 金额
				point = String.valueOf(ManipulatePrecision.doubleConvert(Convert.toDouble(tmp) / 100, 2, 1));// 积分
			}
			else //储值卡
			{
				String tmp = track.substring(40, 48);
				String tmp1 = track.substring(48, 49);
				if (!tmp1.equals(encodecard(tmp))){
					new MessageBox("校验位不正确!");
					return null;
				}
				
				money = String.valueOf(ManipulatePrecision.doubleConvert(Convert.toDouble(tmp) / 100, 2, 1));
				point = String.valueOf("0.00");
			}
			return card+","+money+","+point;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
/*	//10-(偶数位和*3+奇数位)%10 = 校验位
	function  encodecard(str:string):string ;
	var
	 iret,i,j,x_sum,o_sum :integer; 	//x_sum偶位和；o_sum奇位和
	 s_result,s_code :string ;
	begin
	  s_code := str ;
	  i := length(str) ;
	  o_sum := 0 ;
	  x_sum := 0 ;
	  for j := 1 to i do
	  begin
	    if  (j mod 2 = 1) then  o_sum := o_sum + strtoint(s_code[j]) ;
	    if  (j mod 2 = 0) then  x_sum := x_sum + strtoint(s_code[j]) ;
	  end;
	  iret := (x_sum *3 + o_sum)mod 10 ;
	  if   iret = 0 then  iret := 10 ;
	  iret := 10 - iret ;
	  s_result := inttostr(iret) ;
	  result := s_result ;
	end ;*/

	public String encodecard(String card)
	{
		if (card == null || card.equals(""))
			return null;

		try
		{
			int odd = 0;
			int even = 0;
			int sum = 0;

			for (int i = 1; i <= card.length(); i++)
			{
				if (i % 2 == 1)
					odd += (Convert.toInt(String.valueOf(card.charAt(i-1))));
				if (i % 2 == 0)
					even += (Convert.toInt(String.valueOf(card.charAt(i-1))));
			}

			sum = (even * 3 + odd) % 10;

			if (sum == 0)
				sum = 10;

			sum = 10 - sum;

			return String.valueOf(sum);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
