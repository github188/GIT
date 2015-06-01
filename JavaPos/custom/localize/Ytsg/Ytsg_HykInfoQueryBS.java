package custom.localize.Ytsg;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;


public class Ytsg_HykInfoQueryBS extends HykInfoQueryBS {
	public CustomerDef findMemberCard(String track2) {
		ProgressBox progress = null;
		CustomerDef cust = null;
		try {
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询会员卡信息，请等待....."));

			// 查找会员卡
			cust = new CustomerDef();

				/**
				 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
				 */
				Ytsg_SaleBS yt = new Ytsg_SaleBS();
				String memberInfoReturn = yt.findMemberDHYCard(track2);
				if (memberInfoReturn != null
						&& memberInfoReturn.trim().length() > 0) {
					/**
					 * char ResultCode[2] // 应答代码 
					 * char CardID[19] // 卡号 
					 * char Bonus[12] // 当前积分余额 
					 * char ValidBonus[12] // 当前可用积分 
					 * char CertificateType[2] // 证件类型：居民身份证 01、士官证 02、学生证 03、驾驶证 04、护照、05、港澳通行证 06、其他07 
					 * char Certificate[32] // 证件号 
					 * char Sex[2] // 性别 男01 女02 
					 * char Phone[11] // 手机
					 * char MemberLevel[1] //会员级别：注册会员 0、金卡会员 1、白金卡会员 2、钻石卡会员 3 
					 * char Address[80] // 地址 
					 * char Email[40] // 电子邮件
					 * char ResultText[42] // 回应信息
					 */
					if (memberInfoReturn.substring(0, 2).equals("00")) {
						byte[] mir = memberInfoReturn.getBytes();
						cust.code = new String(subBytes(mir, 2, 19)).trim();
						cust.valuememo = Double.parseDouble(new String(subBytes(mir, 33, 12)).trim());
						cust.type = new String(subBytes(mir, 92, 1)).trim();
						cust.name = new String(subBytes(mir, 213, 32)).trim();
						cust.status = "Y";
						cust.maxdate="0";
					} else {// 大会员库里面也查不到那就是卡失效了
						new MessageBox("该顾客卡已失效!");
						return null;
					}

				}
			
			if (cust.code == null || cust.code.trim().equals("")) {
				new MessageBox(Language.apply("查询的会员卡信息无效!\n请找后台人员"));
				return null;
			}
		} finally {
			if (progress != null)
				progress.close();
		}

		return cust;
	}

	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for (int i = begin; i < begin + count; i++)
			bs[i - begin] = src[i];
		return bs;
	}
}
