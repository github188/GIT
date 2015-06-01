package custom.localize.Ywjb;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;


public class Ywjb_MzkInfoQueryBS extends MzkInfoQueryBS
{
    public void QueryMzkInfo()
    {
        String track1;
        String track2;
        String track3;

        if (!GlobalInfo.isOnline)
        {
            new MessageBox("此功能必须联网使用");

            return;
        }

        // 创建面值卡付款对象
        Ywjb_PaymentMzk mzk = new Ywjb_PaymentMzk();

        // 刷面值卡
        /**
            TextBox txt = new TextBox();
        if (!txt.open("请刷储值卡", "储值卡", "请将储值卡从刷卡槽刷入", cardno, 0, 0,false, mzk.getAccountInputMode(),-1))
        {
            return;
        }
                */
        track2 = ICCard.getDefault().findCard();

        if (track2 == null || track2.indexOf("error:") >= 0)
        {
            new MessageBox("没有找得IC卡,请将IC卡放入扫描范围内");

            return;
        }

        track1 = "";
        track3 = "";

        ProgressBox progress = null;

        try
        {
            progress = new ProgressBox();

            progress.setText("正在查询面值卡信息，请等待.....");

            // 得到磁道信息

            // 先发送冲正
            if (!mzk.sendAccountCz())
            {
                return;
            }

            // 再查询
            if (!mzk.findMzkInfo(track1, track2, track3))
            {
                return;
            }

            // 在客显上显示面值卡号及余额
            LineDisplay.getDefault().displayAt(0, 1, mzk.getDisplayCardno());
            LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(mzk.mzkret.ye));

            
            //
            progress.close();
            progress = null;
            
            String validdate = mzk.mzkret.str1.indexOf("|") > 0 ?mzk.mzkret.str1.substring(0,mzk.mzkret.str1.indexOf("|")):mzk.mzkret.str1;
            String mkt = mzk.mzkret.str1.indexOf("|") > 0 ?mzk.mzkret.str1.substring(mzk.mzkret.str1.indexOf("|")+1):"";

            // 显示卡信息
            StringBuffer info = new StringBuffer();
            info.append("卡  号: " + Convert.appendStringSize("", mzk.getDisplayCardno(), 1, 20, 20, 0) + "\n");
            info.append("门  店: " + Convert.appendStringSize("", mkt, 1, 20, 20, 0) + "\n");
            info.append("有效期: " + Convert.appendStringSize("", validdate, 1, 20, 20, 0) + "\n");
            info.append("持卡人: " + Convert.appendStringSize("", mzk.mzkret.cardname, 1, 20, 20, 0) + "\n");
            info.append("卡状态: " + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
            info.append("面  值: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.money), 1, 20, 20, 0) + "\n");
            info.append("余  额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");
            info.append("工本费: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
            info.append("有效额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
            new MessageBox(info.toString());
        }
        catch (Exception er)
        {
            new MessageBox(er.getMessage());
            er.printStackTrace();
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
