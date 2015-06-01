package custom.localize.Jwyt;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class MarsValidateCodeClient extends MarsBaseClient
{
	private static final String METHOD = "validateCode";

	/**
	 * 返回数据格式： { "method" : "validateCode ", //命令名称，必填，validateCode为固定的内容
	 * "timestamp" :"时间戳" //必填，格式为yyyy-MM-dd HH:mm:ss, 填写当前时间 "transSeq" :"序列号",
	 * //序列号，必填，请求方定义的唯一值，用来双方校对数据，25位以内,字母或数字 "version":"1.0", //版本号，固定值
	 * "params" : { “cryptogram”: “扫码串” //扫码后获取的字符串，如果为空，必有辅助码； "assistCode":
	 * "辅助码", //如果扫描串为空，必需填写此辅助码； “encrypt” : true //ture表示反馈信息加密 “deviceId”
	 * :”设备id” //必填 } }
	 */
	public String buildReqParam(String transSeq, String cryptogram, String assistCode, boolean encrypt)
	{
		JSONObject jObject = new JSONObject();
		jObject.put("method", METHOD);
		jObject.put("timestamp", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		// 必须为字符串
		jObject.put("transSeq", transSeq);
		jObject.put("version", GlobalInfo.sysPara.marsVer);
		JSONObject paramsObj = new JSONObject();

		if (assistCode.equals(""))
			paramsObj.put("assistCode", null);
		else
			paramsObj.put("assistCode", assistCode);

		if (cryptogram.equals(""))
			paramsObj.put("cryptogram", null);
		else
			paramsObj.put("cryptogram", cryptogram);

		paramsObj.put("encrypt", String.valueOf(encrypt));
		paramsObj.put("deviceId", MarsConfig.DeviceID);

		jObject.put("params", paramsObj);
		return jObject.toJSONString();
	}

	public MarsSaleRet parseResParam(String transSeq, String json)
	{
		MarsSaleRet result = new MarsSaleRet();
		try
		{
			JSONObject content = JSONObject.parseObject(json);
			String tansSeqRtn = content.getString("transSeq");
			if (!tansSeqRtn.equals(transSeq))
			{
				new MessageBox("前后交易号不匹配!");
				return null;
			}
			result.transseq = tansSeqRtn;

			String method = content.getString("method");

			if (!METHOD.equals(method))
			{
				new MessageBox("交易失败\n" + "前后方法名返回不一致");
				return null;
			}

			result.method = method;

			String status = content.getString("status");
			String message = null;
			if (!status.equals("00"))
			{
				message = content.getString("message");
				JSONObject error = JSONObject.parseObject(message);
				String text = error.getString("text");
				new MessageBox("短信码验码失败\n" + (text != null ? text : ""));
				return null;
			}

			result.status = status;
			result.assistcode = content.getString("assistCode");
			result.timestamp = content.getString("timestamp");
			message = content.getString("message");

			if (message == null || message.trim().equals(""))
			{
				new MessageBox("未获取到返回数据!");
				return null;
			}

			content = JSONObject.parseObject(message);
			result.couponid = content.getString("couponId");
			result.marketingid = content.getString("marketingId");
			result.isgift = content.getString("isGift");
			result.tcodeid = content.getString("tcodeId");
			result.couponname = content.getString("couponName");
			result.availablenum = content.getString("availableNum");
			result.coupontype = content.getString("couponType");
			result.price = content.getString("price");
			result.discountrate = content.getString("discountRate");
			result.balancemoney = content.getString("balanceMoney");
			result.moneycoupontype = content.getString("moneyCouponType");
			result.secrettype = content.getString("secretType");
			result.balancesum = content.getString("balanceSum");
			result.effectivedate = content.getString("effectiveDate");
			result.expiredate = content.getString("expireDate");
			result.fixedfee = content.getString("fixedFee");

			return result;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("解析发生异常\n" + ex.getMessage());
			return null;
		}
	}

	public static void main(String[] args)
	{/*
	 * String merchantId = "1"; String cryptogram = "ptIyAo8cM1m1r0K4FqtGVg==";
	 * String assistCode = "0863579082"; boolean encrypt = false; String
	 * deivceId = "0001"; MarsResponseEntity re = requestForHTTP(URL,
	 * buildReqParam(cryptogram, assistCode, encrypt, deivceId), merKey,
	 * merchantId); System.out.println(re.getContent());
	 */
	}
}
