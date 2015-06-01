package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 80
 * CmdMemo      = 券交易送网（电子券扣款）
*/

public class DosSendCouponPayReq 
{
	public static String refSocket[] =
	{
		 "type|S|3",			/* 交易类型,'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正*/
		 "seqno|L|4",			/* 交易流水号                  */
		 "termno|S|21",			/* 终端号   退货扣回的时候termno传4，交易类型传销售  */
		 "mktcode|S|11",		/* 经营公司，门店号（逗号分隔）                     */
		 "jygs|S|11",
		 "syjh|S|5",			/* 收银机号                       */
		 "fphm|L|4",			/* 小票号                     */
		 "syyh|S|9",			/* 收银员号                     */
		 "djlb|C|1",			/*小票类别                      */
		 "paycode|S|5",			/* 付款方式                      */
		 "payje|D|8",			/* 金额                        */
		 "track1|S|121",		/* 磁道一                               */
		 "track2|S|121",		/* 磁道二                               */
		 "track3|S|121",		/* 磁道三                               */
		 "passwd|S|21",			/* 密码                                 */
		 "memo|S|251"			/* 备注 券种  */
	};
	
	public String type;			//[2+1];	/* 交易类型,'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正*/
	public long seqno;			//;			/* 交易流水号                  */
	public String termno;		//[20+1];	/* 终端号   退货扣回的时候termno传4，交易类型传销售  */
	public String mktcode;		//[10+1];	/* 经营公司，门店号（逗号分隔）                     */
	public String jygs;			//[10+1];
	public String syjh;			//[4+1];	/* 收银机号                       */
	public long fphm;			//			/* 小票号                     */
	public String syyh;			//[8+1];	/* 收银员号                     */
	public char djlb; 			//			/*小票类别                      */
	public String paycode;		//[4+1];	/* 付款方式                      */
	public double payje;		//			/* 金额                        */
	public String track1;		//[120+1];	/* 磁道一                               */
	public String track2;		//[120+1];	/* 磁道二                               */
	public String track3;		//[120+1];	/* 磁道三                               */
	public String passwd;		//[20+1];	/* 密码                                 */
	public String memo;			//[250+1];	/* 备注 券种  */
}
