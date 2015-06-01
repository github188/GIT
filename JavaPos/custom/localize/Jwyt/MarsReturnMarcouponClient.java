package custom.localize.Jwyt;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class MarsReturnMarcouponClient extends MarsBaseClient
{
	private static final String METHOD = "returnMarcoupon";

	public static void main(String[] args)
	{/*
	 * String cryptogram = "bSAuUxxBmHY4rK0sN9d75w=="; String assistCode =
	 * "3417272919"; float backMoney = 1.236f; MarsResponseEntity re =
	 * requestForHTTP(URL,buildReqParam(cryptogram,assistCode
	 * ,backMoney),merKey,"1"); System.out.println(re.getContent());
	 */
	}

	public String buildReqParam(String tansSeq, String assistCode, String tcode, double backMoney)
	{
		JSONObject jObject = new JSONObject();
		jObject.put("method", METHOD);
		jObject.put("timestamp", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		// 必须为字符串
		jObject.put("transSeq", tansSeq);
		jObject.put("version", GlobalInfo.sysPara.marsVer);
		JSONObject paramsObj = new JSONObject();

		if (assistCode.equals(""))
			paramsObj.put("assistCode", null);
		else
			paramsObj.put("assistCode", assistCode);

		if (tcode.equals(""))
			paramsObj.put("tcode", null);
		else
			paramsObj.put("tcode", tcode);

		if (backMoney < 0)
			paramsObj.put("backMoney", null);
		else
			paramsObj.put("backMoney", String.valueOf(backMoney));

		jObject.put("params", paramsObj);
		return jObject.toJSONString();
	}

	public boolean parseResParam(MarsSaleRet result, String transSeq, String json)
	{
		// MarsSaleRet result = new MarsSaleRet();
		try
		{
			JSONObject content = JSONObject.parseObject(json);
			String tansSeqRtn = content.getString("transSeq");
			if (!tansSeqRtn.equals(transSeq))
			{
				new MessageBox("前后交易号不匹配!");
				return false;
			}
			result.transseq = tansSeqRtn;
			String method = content.getString("method");

			if (!METHOD.equals(method))
			{
				new MessageBox("交易失败\n" + "前后方法名返回不一致");
				return false;
			}

			result.method = method;
			result.status = content.getString("status");
			String message = null;

			if (!result.status.equals("00"))
			{
				message = content.getString("message");
				JSONObject error = JSONObject.parseObject(message);
				String text = error.getString("text");
				new MessageBox("短信码撤销失败\n" + (text != null ? text : ""));
				return false;
			}

			result.timestamp = content.getString("timestamp");
			message = content.getString("message");

			if (message == null || message.trim().equals(""))
			{
				new MessageBox("未获取到返回数据!");
				return false;
			}

			content = JSONObject.parseObject(message);

			result.couponid = content.getString("couponId");
			result.marketingid = content.getString("marketingId");
			result.balancemoney = content.getString("balanceMoney");
			result.balancesum = content.getString("balanceSum");
			result.availablenum = content.getString("availableNum");
			result.couponname = content.getString("couponName");
			result.coupontype = content.getString("couponType");
			result.moneycoupontype = content.getString("moneyCouponType");
			result.fixedfee = content.getString("fixedFee");
			result.effectivedate = content.getString("effectiveDate");
			result.expiredate = content.getString("expireDate");
			result.price = content.getString("price");

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("解析发生异常\n" + ex.getMessage());
			return false;
		}
	}
}
