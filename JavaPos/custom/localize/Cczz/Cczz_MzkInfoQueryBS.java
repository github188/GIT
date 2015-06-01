package custom.localize.Cczz;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;


public class Cczz_MzkInfoQueryBS extends MzkInfoQueryBS
{
    public void QueryMzkInfo()
    {
        StringBuffer cardno = new StringBuffer();
        String track1;
        String track2;
        String track3;

        if (!GlobalInfo.isOnline)
        {
            new MessageBox("此功能必须联网使用");

            return;
        }

        while (true)
        {
            // 创建面值卡付款对象
            PaymentMzk mzk = CreatePayment.getDefault().getPaymentMzk();

            // 刷面值卡
            TextBox txt = new TextBox();

            if (!txt.open("请刷面值卡", "面值卡", "请将面值卡从刷卡槽刷入", cardno, 0, 0, false, mzk.getAccountInputMode()))
            {
                return;
            }

            ProgressBox progress = null;

            try
            {
                progress = new ProgressBox();

                progress.setText("正在查询面值卡信息，请等待.....");

                // 得到磁道信息
                track1 = txt.Track1;
                track2 = txt.Track2;
                track3 = txt.Track3;

                // 先发送冲正
                if (!mzk.sendAccountCz())
                {
                    continue;
                }

                // 再查询
                if (!mzk.findMzkInfo(track1, track2, track3))
                {
                    continue;
                }

                // 在客显上显示面值卡号及余额
                LineDisplay.getDefault().displayAt(0, 1, mzk.mzkret.cardno);
                LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(mzk.mzkret.ye));

                //
                progress.close();
                progress = null;

                // 显示卡信息
                StringBuffer info = new StringBuffer();
                info.append("卡  号: " + Convert.appendStringSize("", mzk.mzkret.cardno, 1, 20, 20, 0) + "\n");
                //info.append("持卡人: " + Convert.appendStringSize("",mzk.mzkinfo.cardname,1,20,20,0) + "\n");
                info.append("卡状态: " + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
                info.append("面  值: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.money), 1, 20, 20, 0) + "\n");
                info.append("余  额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

                if ((mzk.mzkret.str1 != null) && (mzk.mzkret.str1.trim().length() > 0))
                {
                    info.append("有效期: " + Convert.appendStringSize("", mzk.mzkret.str1, 1, 20, 20, 0) + "\n");
                }

                if (mzk.isRecycleType(mzk.mzkret.func))
                {
                    info.append("工本费: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
                    info.append("有效额: " +
                                Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0) +
                                "\n");
                }

                if (mzk.mzkret.str3.length() > 0)
                {
                    info.append("回收日期 " + Convert.appendStringSize("", mzk.mzkret.str3, 1, 20, 20, 0) + "\n");
                }

                new MessageBox(info.toString());
            }
            catch (Exception er)
            {
                er.printStackTrace();
                new MessageBox(er.getMessage());
            }
            finally
            {
                if (progress != null)
                {
                    progress.close();
                }
            }
        }
    }
}
