package device.MSR;

public class MzdJC_MSR extends WincorI8_MSR
{
	public boolean parseTrack(StringBuffer trackbuffer, StringBuffer track1, StringBuffer track2, StringBuffer track3)
	{
		int i = 0;
		int j = trackbuffer.length();
		int k;
		System.out.println("读卡获取的信息："+trackbuffer.toString());
		int flag = 2;
		
		while ((i < j) && (flag <= 3))
		{
			// 确定磁道起始符
			while ((i < j) && ((trackbuffer.charAt(i) == '\n') || (trackbuffer.charAt(i) == '\r') || (trackbuffer.charAt(i) == '%') || (trackbuffer.charAt(i) == ';') || (trackbuffer.charAt(i) == '+')))
			{
				i++;
			}

			k = i;

			// 确定磁道结束符
			while ((k < j) && ((trackbuffer.charAt(k) != '\n') && (trackbuffer.charAt(k) != '\r') && (trackbuffer.charAt(k) != '?') && (trackbuffer.charAt(k) != '/')))
			{
				k++;
			}

			// 分别赋值给相应的磁道
			if ((i < j) && (k < j))
			{
				switch (flag)
				{
					case 1:
						track1.append(trackbuffer.substring(i, k));

						break;

					case 2:
						track2.append(trackbuffer.substring(i, k));

						break;

					case 3:
						track3.append(trackbuffer.substring(i, k));

						break;
				}
			}

			//
			i = k + 1;
			flag++;
		}
		
		return true;
	}
}
