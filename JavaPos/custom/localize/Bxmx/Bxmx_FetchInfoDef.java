package custom.localize.Bxmx;

import java.io.Serializable;

import com.efuture.javaPos.Global.GlobalInfo;

public class Bxmx_FetchInfoDef implements Serializable
{
	private static final long serialVersionUID = 10L;
	public String fetchmkt;
	public String fetchdate;
	public String fetcher;
	public String fetchtel;
	public String fetchmemo;
	public String srcmkt;
	public String fetchmktname;

	public Bxmx_FetchInfoDef()
	{
		srcmkt = GlobalInfo.sysPara.mktcode;
	}

	public String getString()
	{
		StringBuffer info = new StringBuffer();
		info.append(fetchmkt);
		info.append("#");
		info.append(fetchdate);
		info.append("#");
		info.append(fetcher);
		info.append("#");
		info.append(fetchtel);
		info.append("#");
		info.append(fetchmemo);
		info.append("#");
		info.append(srcmkt);
		info.append("#");
		info.append(fetchmktname);

		return info.toString();
	}
}
