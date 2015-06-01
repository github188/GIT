package posserver.tools.PressTest;

import java.util.Calendar;

public class PresCommonMethod {
	// 得到当前时间
	public static String getCurrentTime()
	{
		int h, m, s;
		String time = "";

		Calendar calendar = Calendar.getInstance();

		h = calendar.get(Calendar.HOUR_OF_DAY);
		if (h < 10)
			time = "0" + h + ":";
		else
			time = String.valueOf(h) + ":";

		m = calendar.get(Calendar.MINUTE);
		if (m < 10)
			time += "0" + m + ":";
		else
			time += String.valueOf(m) + ":";

		s = calendar.get(Calendar.SECOND);
		if (s < 10)
			time += "0" + s;
		else
			time += String.valueOf(s);

		return time;
	}
}
