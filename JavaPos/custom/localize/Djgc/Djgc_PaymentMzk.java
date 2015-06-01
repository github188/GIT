package custom.localize.Djgc;

import com.efuture.javaPos.Payment.PaymentMzk;

public class Djgc_PaymentMzk extends PaymentMzk
{
  public String getDisplayCardno()
  {
    String cardNo = this.mzkret.cardno;
    if (cardNo.length() > 4) cardNo = cardNo.substring(0, cardNo.length() - 4) + "****";
    return cardNo;
  }
}
