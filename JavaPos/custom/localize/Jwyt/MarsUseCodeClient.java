package custom.localize.Jwyt;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class MarsUseCodeClient extends MarsBaseClient
{
	private static final String METHOD = "useCode";

	/**
	 * reqmsg所包含协议内容，如下： { "method":"useCode", //命令名称，必填
	 * "timestamp":"2011-11-08 11:35:06", //时间戳，必填 "transSeq":"124567890",
	 * //序列号，必填 "version":"1.0", //版本号，固定值 "params":
	 * {assistCode="xxx",deviceId:"xxx"} //填写要传递的辅助码和设备id }
	 */

	public String buildReqParam(String transSeq, String assistCode, double money)
	{
		JSONObject jObject = new JSONObject();
		jObject.put("method", METHOD);
		jObject.put("timestamp", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		// 必须为字符串
		jObject.put("transSeq", transSeq);
		jObject.put("version", GlobalInfo.sysPara.marsVer);

		JSONObject paramsObj = new JSONObject();
		paramsObj.put("assistCode", assistCode); // 必须为字符串
		paramsObj.put("deviceId", MarsConfig.DeviceID); // 必填项
		
		if (money < 0)
			paramsObj.put("consumeFee", null);
		else
			paramsObj.put("consumeFee", String.valueOf(money));

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
				new MessageBox("短信码用码失败\n" + (text != null ? text : ""));
				return false;
			}

			result.assistcode = content.getString("assistCode");
			result.timestamp = content.getString("timestamp");
			message = content.getString("message");

			if (message == null || message.trim().equals(""))
			{
				new MessageBox("未获取到返回数据!");
				return false;
			}

			content = JSONObject.parseObject(message);
			result.couponid = content.getString("couponId");
			result.tcodeid =  content.getString("tcodeId");
			result.marketingid = content.getString("marketingId");
			result.couponname = content.getString("couponName");
			result.availablenum = content.getString("availableNum");
			result.coupontype = content.getString("couponType");
			result.discountrate = content.getString("discountRate");
			result.effectivedate = content.getString("effectiveDate");
			result.expiredate = content.getString("expireDate");
			result.moneycoupontype = content.getString("moneyCouponType");
			result.price = content.getString("price");
			result.balancemoney = content.getString("balanceMoney");
			result.balancesum = content.getString("balanceSum");
			result.secrettype =  content.getString("secretType");
			result.fixedfee =  content.getString("fixedFee");

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
