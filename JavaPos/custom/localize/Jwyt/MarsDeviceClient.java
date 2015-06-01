package custom.localize.Jwyt;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class MarsDeviceClient extends MarsBaseClient
{
	private static final String METHOD = "device";

	/**
	 * 设备密钥获取接口 要求功能：根据设备Id获取密钥 reqmsg所包含协议内容，如下： { "method":"device, //命令名称，必填
	 * "timestamp":"2011-11-08 11:35:06", //时间戳，必填 "transSeq":"124567890",
	 * //序列号，必填 "version":"1.0", //版本号，固定值 "params": {deviceId:"xxx"} //设备id }
	 * 返回json字符串： { "tansSeq": "xxx", //同传递过来的值 "method":"deviceSecKey",
	 * //命令名称,同传递过来的值 "status":"xx", //如果是00，表示成功，其它值为失败
	 * //message中,如果是失败，则描述失败信息，如成功，传回凭证相关数据 "message":{ "deviceId":"xxx",
	 * "secKey":"xxxxx", "validCode":"xxxxx", //验证码 "date":"2013-10-10 10:12:00"
	 * }, "timestamp":"2011-11-08 11:35:06", //时间戳 }
	 */
	public String buildReqParam(String transSeq, boolean encrypt)
	{
		JSONObject jObject = new JSONObject();
		jObject.put("method", METHOD);
		jObject.put("timestamp", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		// 必须为字符串
		jObject.put("transSeq", transSeq);
		jObject.put("version", GlobalInfo.sysPara.marsVer);

		JSONObject paramsObj = new JSONObject();
		paramsObj.put("encrypt", String.valueOf(encrypt));
		paramsObj.put("deviceId", MarsConfig.DeviceID);

		jObject.put("params", paramsObj);
		return jObject.toJSONString();
	}

	public MarsDeviceRet parseResParam(String transSeq, String json)
	{
		MarsDeviceRet result = null;
		try
		{
			JSONObject content = JSONObject.parseObject(json);

			String status = content.getString("status");
			String message = null;
			if (!status.equals("00"))
			{
				message = content.getString("message");
				JSONObject error = JSONObject.parseObject(message);
				String text = error.getString("text");
				new MessageBox("获取Mars系统参数失败\n" + (text != null ? text : ""));
				return null;
			}

			
			message = content.getString("message");

			if (message == null || message.trim().equals(""))
			{
				new MessageBox("未获取到返回数据!");
				return null;
			}

			String tansSeqRtn = content.getString("transSeq");
			if (!tansSeqRtn.equals(transSeq))
			{
				new MessageBox("前后交易号不匹配!");
				return null;
			}

			String method = content.getString("method");

			if (!METHOD.equals(method))
			{
				new MessageBox("交易失败\n" + "前后方法名返回不一致");
				return null;
			}

			result = new MarsDeviceRet();

			content = JSONObject.parseObject(message);
			result.deviceId = content.getString("deviceId");
			result.secKey = content.getString("secKey");
			result.validCode = content.getString("validCode");
			result.equipmentSn = content.getString("equipmentSn");
			result.date = content.getString("date");

			return result;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("解析发生异常\n" + ex.getMessage());
			return null;
		}
	}

}
