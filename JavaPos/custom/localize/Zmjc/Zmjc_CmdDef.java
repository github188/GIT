package custom.localize.Zmjc;

import com.efuture.javaPos.Communication.CmdDef;

public class Zmjc_CmdDef extends CmdDef
{


    public static final int ZMJC_GETSALECFG = 821;			// ZMJC 获取顾客信息
    public static final int ZMJC_GETFLIGHTS = 822;			// ZMJC 获取航班信息
    public static final int ZMJC_CHECKPASSPORT = 823;			// ZMJC 检查护照号信息
    public static final int ZMJC_GETGOODSPACKLIST = 824;	// ZMJC 获取打包码明细商品
    
    public static final int ZMJC_CHECKCUSTOMER = 825;	// ZMJC 检查顾客信息(JAVA_CHECKCUSTOMER) WANGYONG ADD BY 2013.8.21
    public static final int ZMJC_GETCUSTOMER_TH = 837;	// ZMJC 获取退货小票的顾客信息(java_GETBACKSALECUSTOMER) WANGYONG ADD BY 2013.8.21
    public static final int ZMJC_GETGZLIST = 838;	// ZMJC 根据商品信息获取对应的柜组列表(gzcode,gzname),用于多柜组的选择 WANGYONG ADD BY 2013.9.17
    
    public static final int ZMJC_FINDCLK = 839;//ZMJC 查找常旅卡信息 wangyong add by 2013.10.16
    public static final int SENDFJK_CLK = 840;  				//上传(常旅卡)返券卡信息
    public static final int ZMJC_HYKJH	= 860;		//会员卡激活    
    public static final int ZMJC_GETRETFLIGHT	= 861;		//获取回程航班信息
    public static final int ZMJC_CHECKZCD	= 862;		//验证暂存单
    //说明:=========825,837,838,839,840,860~870为ZMJC预留=========
    
    
}
