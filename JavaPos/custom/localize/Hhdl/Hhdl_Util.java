package custom.localize.Hhdl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;

public class Hhdl_Util
{
	public static long getMillisByDatetime(String date, String time)
	{
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);

		String hour = time.substring(0, 2);
		String minute = time.substring(2, 4);
		String second = time.substring(4, 6);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Convert.toInt(year));
		calendar.set(Calendar.MONTH, Convert.toInt(month) - 1);
		calendar.set(Calendar.DATE, Convert.toInt(day));
		calendar.set(Calendar.HOUR_OF_DAY, Convert.toInt(hour));
		calendar.set(Calendar.MINUTE, Convert.toInt(minute));
		calendar.set(Calendar.SECOND, Convert.toInt(second));

		return calendar.getTimeInMillis();
	}

	public static void main(String[] args)
	{
		StringBuffer sb = new StringBuffer();

		Hhdl_Util.checkBarcodeSale("270445598762507131", sb);

	}

	public static boolean checkBarcodeSale(String barcode, StringBuffer code)
	{
		String date;
		String time;
		long earlytime = GlobalInfo.sysPara.saletimelimit * 60 * 60 * 1000;
		try
		{
			if (barcode == null || barcode.equals(""))
				return false;

			if (barcode.length() == 17 && barcode.startsWith("21"))
			{
				// 21000074130707091
				date = new ManipulateDateTime().getDateByEmpty().substring(0, 2) + barcode.substring(8, 14);
				time = barcode.substring(14, 16) + "0000";

				long codetimeMills = getMillisByDatetime(date, time);
				long curtimeMillis = getMillisByDatetime(new ManipulateDateTime().getDateByEmpty(), new ManipulateDateTime().getTimeByEmpty());

				if (curtimeMillis <= codetimeMills - earlytime)
				{
					code.append(barcode.substring(2, 8)); // +
					// new MessageBox(code.toString()); // barcode.substring(16,
					// 17)
					return true;
				}
				else
				{
					new MessageBox("此商品不能销售");
					return false;
				}
			}
			else if (barcode.length() == 18 && barcode.startsWith("27"))
			{
				String tmpCode = barcode.substring(2, 7);

				if (GlobalInfo.sysPara.isEnable17code == '1')
					tmpCode = "22" + tmpCode;
				else
					tmpCode = barcode.substring(0, 7);
				
				int shelflife = ((Hhdl_NetService) NetService.getDefault()).getGoodsShelfLife(tmpCode);

				// 271234598765306071
				date = barcode.substring(15, 17);
				date += barcode.substring(13, 15);
				date += barcode.substring(11, 13);
				date = new ManipulateDateTime().getDateByEmpty().substring(0, 2) + date;
				time = "000000";

				long codetimeMills = getMillisByDatetime(date, time);
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date codedate = new Date(codetimeMills);
				//System.out.println(dateFormat.format(codedate));

				Date newCodedate = addDate(codedate, shelflife);
				String newStrDate = dateFormat.format(newCodedate);

				if (new ManipulateDateTime().compareDate(dateFormat.format(new Date()), newStrDate) <= 0)
				{

					/*
					 * long curtimeMillis = getMillisByDatetime(new
					 * ManipulateDateTime().getDateByEmpty(), time);
					 * 
					 * long shelflifems = 0;// shelflife * 24 * 60 * 60 * 1000;
					 * 
					 * if (Math.abs(curtimeMillis - codetimeMills) < 999 ||
					 * curtimeMillis <= codetimeMills + shelflifems) {
					 */
					if (GlobalInfo.sysPara.isEnable17code == '1')
						code.append("22" + barcode.substring(2, 11) + barcode.substring(17));
					else
						code.append(barcode.substring(0, 11) + barcode.substring(17));

					return true;
				}
				else
				{
					new MessageBox("此商品不能销售");
					return false;
				}
			}
			else if (barcode.length() == 21 && barcode.startsWith("24"))
			{
				// 220123498765130607091
				date = new ManipulateDateTime().getDateByEmpty().substring(0, 2) + barcode.substring(12, 18);
				time = barcode.substring(18, 20) + "0000";

				long codetimeMills = getMillisByDatetime(date, time);
				long curtimeMillis = getMillisByDatetime(new ManipulateDateTime().getDateByEmpty(), new ManipulateDateTime().getTimeByEmpty());

				if (curtimeMillis <= codetimeMills - earlytime)
				{
					code.append(barcode.substring(0, 12) + barcode.substring(20));
					// new MessageBox(code.toString());
					return true;
				}
				else
				{
					new MessageBox("此商品不能销售");
					return false;
				}
			}
			else
			{
				code.append(barcode);
				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			code.append(barcode);
			return true;
		}
	}

	public static Date addDate(Date d, long day) throws ParseException
	{
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}
}
