package com.efuture.javaPos.Struct;

import java.lang.reflect.Field;

import com.efuture.commonKit.Convert;

// 系统参数类
public class GlobalParaDef
{
	public boolean isJavaPosManager; // 是否使用的JavaPos的POS管理程序,存在20参数表示使用WEB版POS管理
	public String allowsyjip; // 是否允许款机IP与设置IP不一致时登录,20
	public String allowgh; // 是否允许同一个工号同时登录多个款机,21
	// 以上参数用于java_findsyj,java_checklogin存储过程中进行判断

	public String mktname; // 门店名称,13
	public String mktcode; // 门店编号,14
	public String shopname; // 商场名称,16
	public String jygs; // 经营公司编号,1M
	public String mystorekey ;// 微店密钥,17
	public String mystorecallid;//微店商户调用标识符,18
	public String mystoreurl;//微店http地址,19
	public int mystoreusestimes ; //一单中能使用微店券的张数,22
	public int timeoutpresendcoupon ; //一单中预上小票超时时间,22
	public int timeoutnocoupon ; //一单中无券消费时超时时间,22
	public int timeouthavecoupon ; //一单中存在券消费时超时时间,22

	
	public String textFilterChar; //Text文本框过滤字符,23(字符1;字符2...)
	public String quantityChange; // 销售界面条码输入框中是否 将数量键 变成 乘号 （*)事件 24(Y/N)
	public char yyygz; // 营业员控制柜组销售标志(Y/N/A-控制串柜可修改柜组),40
	public char isxh; // 是否允许销红（Y/N),41
	public char xhisshowsl; // 销红提示（Y/N),41
	public char customvsgoods; // 会员卡刷卡顺序(Y-前后皆可/A-必须在商品前/B-必须在商品后),42
	public char custommust; // 并且必须有会员才可以销售(Y/N),42 
	public char bdjyh; // 不定价商品的优惠是否有效(Y/N),43
	public char inputydoc; // 退货是否输入原收银机号和小票,44
	// Y-输入原商品方式的指定小票退货
	// A-调出原小票方式的指定小票退货
	// B-输入原商品方式的指定小票退货,只允许指定小票退货(输入小票信息时放弃则返回销售界面)
	// C-调出原小票方式的指定小票退货,只允许指定小票退货(输入小票信息时放弃则返回销售界面)
	// D-输入原收银机号和小票,但不按原单退货(只是记录原收银机号和小票)
	public char backyyyh;//退货是否输入营业员号(Y/N),44
	public char backinputmode;//退货输入框能否输入字母(Y/N),44
	public char backisinput;  //退货是否自动填入收银机号、小票号(Y/N),44
	
	public char isconnect; // 是否自动联网,45
	public long querytime; // 定时查询时间(秒),48
	public double maxxj; // 最高现金存量,4C
	public String custinfo; // 顾客信息采集标志（NNN关闭）,4E
	public String custinfocyclesel; // 顾客信息采集标志（NNN,N）,4E
	public String noshowcustinfogroup; // 标识不显示的顾客信息分组 (NNN,Y,01;02;03),4E
	public char rulepop; // 是否进行满减满赠处理,4F
	// N-不进行满减满赠
	// Y-选择满减或满赠规则
	// R-自动选 择满减或满赠规则
	// F-FOXTOWN满减方案,前台执行满减,不用选择
	// A-选择是满减还是满赠，前台提示选择满减还是满赠,如果选满赠，前端不处理)
	// S-西武满减方案
	// C-CMR+通用促销模型
	public char localfind; // 联网时是否本地优先查询,4H
							// (N-联网时查询网上/Y-联网优先本地查询再查网上/Z-联网只查询本地)
	public char lxprint; // 收银练习是否打印小票,4I
	public double chglimit; // 最大找零金额,4J
	public char zljd; // 找零精度，4J Y-根据收银机价格精度
						// 0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元、5-进位到角、6-进位到元
						// 7-五舍6入到角
	public char cutzl;//4J 是否将找零的分子钱直接截掉(Y,N)
	public char autoopendrawer; // 收银员登录是否自动开钱箱,4L
	public char isshowname; // 收银员登录后是否显示姓名(Y-是/N-否/A-工号+名字)4L
	public char codesale; // 是否允许使用商品代码销售,4M
	public char dzccodesale; // 是否允许直接输入电子称代码销售,4M (Y:输入商品金额/A:输入商品数量)
	public char isCalcAsPfj; // 如果电子称商品存在批发价，该商品应收是否按照批发价计算,(Y/N,Y:输入商品金额/A:输入商品数量,Y/N)
								// 4M
	public char forcebybarcode; // 如果barcode长度不满足code.length() >= 8时，强制按条码查找
								// (Y/N)4M
	public char isnumandcode;   // 是否使用 数量*编码的方式在商品条码框中输入  默认Y   4M
	public char cashsale; // 现金过量是否允许继续销售,4N
	public long downloadtime; // 定时下载数据时间间隔,4P (100,Y)
	public char downloadsaleupdate; // 在销售状态时也更新本地数据,4P (100,Y)
	public char fdprintyyy; // 是否分单打印营业员联,4Q(Y-打印营业员联/N-不打印营业员联/A-只打印营业员联)
	public char printyyygrouptype; // 打印营业员联分组方式,4Q(1-营业员+柜组/2-单品/3-柜组/4-营业员/5-手工发票号)
	public char fdprintyyytrack; // 打印营业员联输出栈号,4Q(1-小票栈/2-备卷栈/3-平推栈)(4Q=Y,2,3)
	public char printyyhsequence; // 打印顺序,4Q(A - 营业员先打 B - 小票先打)
	public char isinputjkdate; // 是否允许输入缴款日期,4R(N,2)
	public int printjknum; // 打印缴款单的次数,4R(N,2)
	public char isGetNetMaxJkdNo; // 是否从网上最大缴款单号,4R(N,2,N)
	public char printJKList; // 是否允许只打印缴款列表，4R(N,2,N,N)
	public char closedrawer; // 钱箱未关闭不允许继续销售,4T
	public String isthgh; // 允许红冲面值卡交易的工号,MX
	public String ismzkgh; // 允许红冲面值卡交易的工号,MZ
	public char grantpwd; // 员工授权是否输入密码,J1(N/Y/A  A-工号必须刷卡输入)
	public char grtpwdshow; // 员工授权帐号是否以暗码显示,J1 (N/Y)
	public char grantpasswordmsr; // 员工授权密码是否只能允许刷卡,J1(N/Y)
	public char payover; // 付款输入方式是否采用覆盖方式,J2
	public String payex; // 一般2级付款方式付款后回到上级，此处可配置例外付款方式,J2
	public String fpjepayex; // 发票开票金额除外付款方式,J2(N|XX,XX|XX,XX)
	public String dfpaymode; //付款跳转默认付款方式，J2(付款代码)
	public char paychgmore; // 是否多币种找零模式(Y-多币种找零/N-单币种找零显示合计/A-单币种找零显示付款明细/B-只允许使用人民币找零/C-多币种+补找零[ZMJC专用]),J3
	public String paychgyy; // 最后付款产生溢余不找零的付款方式代码(N,xx,xx,xx),J3
	public int msrspeed; // 键盘刷卡槽有效输入时间(ms,0表示允许键盘输入),J4
	public String icCardIsEnter; //配置了ic卡功能键是否使用Enter键也能调用读卡,J4  （默认Y,英文逗号分隔J4参数）
	public String cardsvrurl; // 面值卡服务器URL,J5
	public char cardrealpay; // 卡交易是否即时扣款,J6
	public char havebroken; // 是否启用断点保护(N/Y-断点保护自动登录/L-断点保护不自动登录),J7
	public char checknetlogin; // 是否检查联网状态登录合法性,J8
	public char rebatepriacemode; // 折让是否采用成交价方式输入,J9
	public char paysummarymode; // 付款统计采用主付款方式统计,JA
	public char autojfexchange; // 是否自动进行换购(Y/N),JB
	public String quitpwd; // 非维护员退出系统的密码(为空-不启用退出密码/不为空-密码),JC
	public char thmzk; // 退货时是否允许使用面值卡,JD
	public long num_down; // 下载一次及时更新的文件个数,JE
	public String validver; // 当前业务系统允许的有效版本号,为空不控制,JF
	public char iscardmsg; // 是否显示卡号（Y/N),JG
	public int printdelayline; // 打印XX行延迟(每打印XX行,延迟XX毫秒),JH
	public int printdelaysec; // 打印延迟XX毫秒,JH
	public char secMonitorPlayer; // 是否开启双屏广告播放,JI
	public String memcardsvrurl; // 会员卡服务器URL,JJ
	public String cardpasswd; // 面值卡查询是否需要输入密码,JK（Y --代表所有卡都输入密码/Y,0400
								// ---代表只有0400输入密码）
	public char onlyUseBReturn; // 是否只能使用后台退货,JL
	public String exceptBReturnType; // 当onlyUseBReturn等于Y时例外的单据类别 JL
	public char acceptfjkrule; // 返券卡是否启用活动规则匹配模式(Y/N),JM
	public String mjPaymentRule; // CRM满减促销的除券付款列表,用逗号分割,JN
	public char refundByPos; // 前台POS处理退货扣回,JO(N/Y/B)
	public String refundPayMode; // 扣回付款方式列表用逗号分割(Y,01,02,03|01,02),JO
	public String refundCouponPaymode; // 退券模式下扣回付款方式(Y,01,02,03|01,02) JO
	public double lczcmaxmoney; // 小于该限制金额的找零可进行零钞转存,JP
	public char isAutoLczc; // 是否强制零钞转存,JP
	public char isAutoPayByLczc; // 是否强制使用零钞转存付款 ,JP
	public char isReMSR; // 是否在付款时重新刷卡,JP
	public String serialmzkrule; // 连续面值卡规则,JQ
	public char iscardcode;//刷会员卡规则（Y/N)Y:截取=，N:不截取=JQ
	public double feechargelimit; // 零头的最大限制用参数设置,JR
	public char havePayRule; // 是否启用付款方式的收款规则(N/Y-只分摊有规则的付款/A-分摊所有的付款),JS
	public String fjkyetype; // 返券卡付款方式余额类型(xx=Z|A;xx=D|A,B,F;xx=T|A)(Z-纸打印券/T-退打印券/D-电子券),JT
	public char refundAllowBack; // 扣回金额大于0是否允许退货,JU
	public char refundScale; // 扣回金额价格精度截断方式，0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元,JU
	public char customerbyconnect; // 会员卡是否必须联网使用,JV
	public char customerbysale; // 开始交易前主动提示刷会员卡(Y/N),JV
	public char custDisconnetNoPeriod; // 在脱网时不控制会员的合法性(Y/N),JV
	public char apportMode; // 受限付款方式的分摊模式(A-顾客手工输入/B-按金额占比均摊/C-按商品倒序分摊/D-按商品顺序分摊/E-按可收比例分摊/F-按可收金额分摊),JW
	public String loopInputPay; // 连续输入方式的付款方式列表,JX
	public String printpaysummary; // 需要汇总打印的付款方式列表,JY
	public String isinputpremoney; // 是否输入备用金(N-不输入/Y-输入),JZ
	public String isNull;//  资和信百货接口中，textbox的值是否允许为空；(N-不输入/Y-输入),JZ
	public char calcjfbyconnect; // 是否联网实时计算积分(N:不时时积分 Y:时时积分并提示 A:时时积分但不提示)
									// O1
	public char sendhyjf; // 是否发送到会员服务器进行积分同步(N:不发送 Y:发送) O1
	public char sendsaletocrm; // 是否发送小票到会员服务器,O2
	public int mzkbillnum; // 打印面值卡联的份数,O3
	public int salebillnum; // 打印小票的份数(面值卡打印份数，小票打印份数),O3
	public char isGoodsSryPrn; // 打印小票时是否将同编码,同条码,同柜组商品进行汇总(Y/N),O3
	public char allowbankselfsale; // 是否允许单独进行银联消费交易(Y-允许提示 N-不允许 A-允许但不提示),O4
	public char isCheckReJe;       //是否检查银联返回金额大于剩余未付款金额,O4(Y-检查/N-不检查)
	public char calcfqbyreal; // 是否联网实时计算返券(N-不实时计算/Y-实时计算电子券/A-实时计算并打印纸券),O5
	public char calcmystorecouponbyreal; // 是否联网实时计算微店返券(N-不实时计算/A-实时计算并打印),O5
	public char issendmystorecouponwithhyk;	//是否仅当存在会员刷卡才发送微店流水(Y-都发送/N-仅存在会员时才发送),O5
	public String printInfo1; // 小票自定义打印信息1,O6
	public String printInfo2; // 小票自定义打印信息2,O7
	public char setPriceBackStatus; // 不指定小票退货时，是否输入价格,O8(N-不输入价格,Y-输入价格)
	public char isbackpricestatus; // 退货时取哪一种价格,O8(N-不取,Y-取原售价,F-取批发价)
	public char inputyyyfph; // 是否输入营业员手工单编号,O9
	public char batchtotalrebate; // 总折扣是否采用批量方式输入,OA(Y-循环按每个单品折扣计算/N-按总成交价计算总折扣后按商品价值分摊/A-按总成交价计算总折扣后按商品可折扣金额分摊)
	public char CloseShowZkCompetence; // 开启显示折扣权限,OA(Y - 不显示/N - 显示)
	public char FirstClearLsZk; // 按清除键是否先取消折扣,再取消交易,OA(Y - 不显示/N - 显示)
	public int bankprint; // 银联签购单打印次数(N,Y)OB
	public char displaybanktype; // 银联支付在退货交易时默认采用退货交易类别(N-撤销类别/Y-退货类别),OB
	public String paycodebanktype; // 银联支付在退货交易时仅指定付款代码默认采用退货交易类别,OB
	public String paycodebankform; // 银联支付在交易时哪些text可以响应键盘上下键以便编辑(按付款编码控制),OB
	public String custompayobj; // 自定义付款对象(付款对象类名1,代码1,代码2|付款对象类名2,代码3,代码4),OC
	public char findcustfjk; // 查找顾客卡信息的同时是否查询卡上的返券,OD
	public char payprecision; // 是否检查付款金额符合付款精度(N,0.01),OE
	public double goodsPrecision; // 商品成交价截取精度(0/0.01/0.1/1)，OE
	public char ischoiceExit; // 退出系统是是否选择重启或关机,OF
	public String backRefundMSR; // 代表后台退货时是否需要输入刷卡(Y/N-代表后台退货时是否需要输入刷卡),OG
	public String backgoodscodestyle; // 商品退货选择列表中编码显示方式(A-条码,B-编码),OG
	public char dzcbarcodestyle; // 是否将电子秤编码记录到goodsdef.barcode字段(Y/N),OG
	public String saleTicketBarcodeStyle; //显示小票信息界面中,商品编码列显示方式(A-输入码,B-条码,C-编码),OG
	public String validservicedate; // 系统使用有效期(YYYY-MM-DD,N),OH
	public String HCcontrol; // 按付款方式设置哪些不允许红冲/哪些需要授权(GRANT|SALE|0400,0002;GRANT|BACK|0401;NOHC|SALE|0403;NOHC|BACK|0401,0003),OI
	public char quickinputsku; // 超市是否允许直接按回车输入上一个商品,OJ
	public char goodsAmountInteger; // 超市是否修改数量只能为整数,OJ
	public char barcodeshowcode; // 商品是否输入条码显示编码,OJ(Y:显示编码 N:输入什么显示什么
									// A:输入编码显示条码)
	public char isinputnextgoods; // 超市查找无效商品时是否只能按确认和回车键才关闭提示框继续扫下个商品,OJ(Y-确认键/N-回车键)
	public char showgoodscode;//商品名称前,OJ(C显示商品编码/B条码/Y柜组)
	
	public char isblankcheckgoods; // 1 -- 允许无空白盘点单时创建盘点单(Y-先联网找盘点单找不到的话自动创建盘点单/N-不允许使用空白盘点单/A-脱网盘点可输入柜组日期仓位单号长度,组长度,是否累加/B-联网盘点输入柜组日期仓位单号长度,组长度,可在后台调出远盘点信息进行修改),OK
	public int checklength; // 2--盘点单长度,OK
	public int checkgrouplen; //3-- 盘点单组长度(<0:只能输入单号且过程内自动生成组号|>0必须手工输入组号,长度必须相等|=0可输或不输组号,输取手工,不取自动产生),OK
	public char ischeckadditive; //4-- 盘点是否累加(Y/N/A/B 单行累加,多行累加,多行累加上传不汇总)),OK
	public char ischecksaveprint; // 5--盘点是否保存后直接打印(Y/N),OK
	public char ischeckgz; // 6--盘点是否需要控制柜组(Y/N),OK
	public char ischeckcode; // 7--是否只检查盘点单号(Y/N),OK
	public char ischeckquantity; // 8--不定价商品是否盘点数量(Y/N),OK
	public char isaddwithzeroquantity;//9--数量为0是否继续累加(Y/N),OK
	
	public String printpaymode; // 打印卡联的付款方式列表,OL(付款代码1,付款代码1|付款代码3)
	public boolean debugtracelog; // 是否开启跟踪日志,OM(Y/N)
	public String mzkStatistics; // 面值卡统计中设置统计的付款方式(0400,0050|01,002) ON
	public String cmdCustList; // 会员卡URL命令列表,OO,默认为(10.11,12,13,23,24,26,36,45,49,52,58,59,64,66,69,70,103,105,106,107,109)
	public char unionVIPMode; // 联名卡规则查询模式(A-先选择规则后刷会员卡/B-先刷会员卡后过滤规则/C-付款时不弹出规则选择框，默认为A),OP
	public char issetprinter; // 付款时时候断开打印机以便使用其他打印程序(Y/N),OY
	public int maxSaleGoodsCount; // 最大商品行数,OZ(最大商品行数,最大付款行数,最大商品数量,最大商品单价,最大整单成交价)
	public int maxSalePayCount; // 最大付款行数,OZ(最大商品行数,最大付款行数,最大商品数量,最大商品单价,最大整单成交价)
	public double maxSaleGoodsQuantity; // 最大商品数量,OZ(最大商品行数,最大付款行数,最大商品数量,最大商品单价,最大整单成交价)
	public double maxSaleGoodsMoney; // 最大商品单价,OZ(最大商品行数,最大付款行数,最大商品数量,最大商品单价,最大整单成交价)
	public double maxSaleMoney; // 最大整单成交价,OZ(最大商品行数,最大付款行数,最大商品数量,最大商品单价,最大整单成交价)
	public String fjkkhhl; // 扣回交易时返券卡的折现汇率,E7(券种1,汇率|券种2,汇率|券种3,汇率|...)
	public char printInBill; // 是否将红冲和退货小票输出到打印发票的主栈，P1
	public char isHcPrintBill; // 是否打印红冲小票，P1(Y,Y)
	public char removeGoodsModel; // 增加是否启用按编码删除商品模式的参数,P2(Y-按商编码删除商品的模式/N-现有用光标选择商品进行删除的模式)
	public char removeGoodsMsg; // 删除商品是否提示确认,P2(Y/N)
	public char isMoneyInputDefault; // 是否默认剩余金额,P3(Y-是/N-否|付款代码)
	public String MoneyInputDefaultPay; // 默认剩余金额付款代码,P3(Y-是/N-否|付款代码,付款代码)
	public String onlineGd; // 挂单解挂模式,P4(Y-连网挂单/N-本地挂单/A-连网挂单累加)
	public String isPrintGd; // 是否打印挂单P4(Y-打印/N-不打印)
	public int gdTimes; // 挂单次数 P4
	public char exitsyswhenexistgd; // 若存在挂单，是否允许退出系统 P4
	
	public String overNightBegin; // 设置为时间区间时的起始时间
									// 通宵营业时间设置。空为不控制，(YYYY/MM/DD|HH:MM:SS -
									// 营业起始日期时间,YYYY/MM/DD|HH:MM:SS -
									// 营业结束日期时间)(HH:MM:SS - 营业到第二天的时间) P5
	public String overNightEnd; // 设置为时间区间时的结束时间
								// 通宵营业时间设置。空为不控制，(YYYY/MM/DD|HH:MM:SS -
								// 营业起始日期时间,YYYY/MM/DD|HH:MM:SS -
								// 营业结束日期时间)(HH:MM:SS - 营业到第二天的时间) P5
	public String overNightTime; // 设置为某一时间点时
									// 通宵营业时间设置。空为不控制，(YYYY/MM/DD|HH:MM:SS -
									// 营业起始日期时间,YYYY/MM/DD|HH:MM:SS -
									// 营业结束日期时间)(HH:MM:SS - 营业到第二天的时间) P5
	public char isHandVIPDiscount; // 是否启用手工VIP折扣输入。（Y-启用/N-不启用手工输入VIP折扣率|最低VIP折扣率/A-控制单品是否享受VIP折扣）P6
	public double handVIPDiscount; // 最低手工VIP折扣率。（Y-启用/N-不启用手工输入VIP折扣率|最低VIP折扣率）P6
	public String searchPosAndCUST; // 同时查询POS和会员库，P7
	public char isMoreSelectQuerygoods; // 查询商品信息时是否支持多选,P8(Y/N)
	public String setTextLimit;         // 查询商品库存信息时限制输入XX位内容, P8（默认为13,设置为0则不控制）
	public char useGoodsFrameMode; // 启用商品经营配置例外价处理模式,P9(Y/N)
	public char allowGoodsFrameSale; // 允许商品无经营配置时以门店号为经营柜组进行销售,P9(Y,Y)
	public String checkGoodsDate; // 盘点日期，PA
	public String checkGoodsCw; // 盘点仓位，PB
	public char isInputSaleAppend; // 小票完成前提示录入附加信息，PC(Y/M/N
									// 2)(整个PC参数采用|分隔多项)Y-提示输入,M-不提示直接弹出,N-不提示输入
	public char saprintyyytrack; // 小票附加信息打印线，PC(Y2)(第二位)
	public String saleAppendSaleType; // 只有在指定的交易类型时才会提示录入附加信息,PC
	public char saleAppendStatus; // 附加信息的处理方式(Y-既上传又打印/N-既不上传又不打印/S-只上传不打印/P-只打印不上传),PC
	public char issaleby0; // 整单为0是否允许销售,PD
	public char isGoodsMoney0; //单价为0是否允许销售,PD
	public char iscloseJkUI; // 缴款结束后是否关闭缴款界面(默认Y),PE(Y/N)
	public char isshowAllBcData; // 收银员销售统计是否显示所有班次信息(默认Y),PF(Y/N)
	public char isusepaySelect; // 是否启用付款方式快速定位功能(默认为Y),PG(Y/N)
	public char ClawBackCalcModel; // 扣回计算模式,PH(A-所有实际收款减去扣回；B-实际收款和扣回相同付款方式的相互抵消，并保留实际付款中未被抵消的付款；C-实际收款和扣回相同付款方式的相互抵消，并保留实际付款中未被抵消的付款。如果扣回金额大于实际付款，记为0；D-只显示现金类的金额汇总。)
	public char jdxxfkflag; // 家电下乡返款时是否指定远小票和收银机号 PI
	public char isInputPayMoney; // 是否手工输入付款金额（默认为N），PJ（Y/N）
	public char isinputjdfhdd; // 是否输入家电销售地点(默认为N),PK(Y/N/S)
								// Y每笔自动弹出选择框输入家电发货地点,N不弹出,S继承上一笔
	public char iscfgtable; // 是否采用配置项来配置销售界面的Table,PL（Y/N）
	public String jdfhdd; // 家电发货地点,PM(code,name;code1,name1;)
	public String nodeletepaycode; // 不能删除的付款方式|不能直接付款的付款方式必须自动生成,PN(paycode,paycode|paycode,paycode)
	public String noinputpaycode; // 不能删除的付款方式|不能直接付款的付款方式必须自动生成,PN(paycode,paycode|paycode,paycode)
	public String verifyDzcmname; // 验证电子秤码,PO (EAN13/UPCA/EAN13_UPCA)
	public double backgoodsminmoney; // 退货时商品下限的金额,PR
	public double backgoodsmaxmoney; // 退货时商品上限的金额,PR
	public char isEARNESTZT; // 定金消费时是否允许非自提商品销售，PS
	public char salepayDisplayRate; // 付款界面是否显示汇率，PS
	public char isRealPrintPOP; // 实时打印时，在计算促销前是否关闭实时打印(Y-不关闭/N-关闭)，PS
	public String withdrawauotbsmoney; // 缴款自动将笔数和金额显示出来,PT
										// (缴款代码=付款代码,付款代码|缴款代码=付款代码,付款代码)
	public char isForceRound; // 是否强制对商品进行价格精度计算(Y/N) PU
	public double lackpayfee; // 剩余金额小于此金额时可以不需要付款，PV
	public char oldqpaydet; // 退货时券交易是否查询券益余，PW(Y-查询/N -不查询)
	public double vipzklimit; // 临时折扣为多少时不打VIP折扣,PX
	public char isShowMktWarm; // 是否显示本地门店号和数据库门店不一致的警告，PY(Y-显示/N -不显示)
	public char uploadOldInfo; // 是否自动上传未上传的隔日小票,PZ(0-自动上传/1-提示上传/2-不自动上传)（0,Y）
	public char isPreSale; // 是否启动预销售,PZ(Y-启动/N-不启动)
	public char sendsaleissuccess; // 完成交易失败时是否开始新交易,PZ(Y-开始 /N-不开始)
	// public char debugValid; // debug模式下是否效验IP地址
	public char usemzklog; // 是否启用储值卡交易日志,S1(Y/N)
	public char cancelBankGrant; // 退出付款或删除金卡工程时，是否允许用其他途径退出
									// S3（Y-允许/N-不允许/A-需要授权）
	public String disableCmd; // 收银机不发送的命令列表S2(1,2,3,4)
	public char isVipMaxSlMsg; // 会员超出限量是否提示S4(Y/N)
	public char isModifySaleFP; // 是否修改服务器数据库小票头中的发票信息S5(Y/N)
	public char elcScaleMode; // 电子称调用模式S6（1-只取一次重量，2-轮询查找重量）
	public int elcScalecycletime; // 电子称轮询时间间隔S6
	public char isHbGoods; // N/Y是否合并商品S7（扫码时，默认为N）
	public String limitGH; // 付款限额授权工号 逗号分隔 S8
	public String localNotCheckMultiGz; // 本地查询不检查多柜组取第一行 S9
	public char custLocalfind;			//本地查找会员 SA
	
	public char isBackPaymentCover;		//  退货非扣回付款时，是否覆盖相同帐号的付款方式 SB
	public String disablePrinterCounter;		//  打印结束后，哪些款台是要求立即关闭打印设备的 SD
	public char custConfig;				//是否启用会用配置项（VIPCARD.ini）,SE
	public String paymentFilter;		//付款方式配对使用。例如：0402只能和0401、0403一起使用。当存在0402时，其他付款方式无法使用。SF(0402:0401,0403|0402:0401,0403)
	public String couponSaleType;		//券销售类型（A-输入成交金额模式 B-输入券金额模式（后台需支持），SG
	public String defaultmzkpass;		//面值卡默认密码,SH
	public String hyMaxdateMsg;         //小票中会员有效期提前X天提醒(默认0,0为不判断时间，每次提示),SI
	public String bankCXMsg;			//撤消时,是否提示,SJ(Y提示,N不提示)
	public String dosPosSvrAddress;		//CRM通讯地址,SK（IP|端口|超时时间毫秒）
	public String dosPosSvrCmdList;		//CRM通讯命令列表，SL（1,10,11,58,13,49,24,45）
	public String bacthfdisvisible;			//批发类型是否隐藏折扣分担提示,SM（Y/N)
	public double batchfdrate;				//批发类型折扣分担比例,SM(1--100);
	public String stampprefix;				//印花码前缀符 ,SN
	public char localgoodsisdelgoods;		//定位商品是否删除商品SO
	public char isUseNewMzkRange;		//面值卡使用范围到单品SP(N/Y) WANGYONG ADD BY 2015.1.16
	
	// 以后增加系统参数只需要在本.java文件中加入参数和在下面SysParaDef中加入参数描述即可,读取参数转换的地方不再需要修改
	// 后台参数可用两种方式定义,第2种方式参数代码任意只要不重复即可
	// 1、参数代码为 #成员名
	// 2、参数名称为 #成员名 描述

	// 以下参数为各项目自身参数
	public char printpopbill; // BHLS,是否打印促销联,PP HTSC 是否打印换购联
	public String mername; // SFKS,银联商户名称,SH
	public String customerUnpayment; // HZJB,参加会员打折不能付的付款方式,HP(付款代码，付款代码)
	public String noprintCashier; // HZJB,判断哪些款机不打印小票，HI（款机号，款机号）
	public String hdqrsCRM; // HZJB,总部CRM，HQ
	public String slipPrinter_area; // SZXW,平推打印的范围(消费初始位置，消费结束位置；退货初始位置，退货结束位置；签购单打印初始位置，签购单打印结束位置)，Z1
	public char iscrmtjprice; // BCRM,是否启用CRM促销特价，F1
	public char ispregetmsinfo; // BCRM,判断是否在交易前查询满赠信息（Y-查询/N-不查询），PQ
	public String vipPromotionCrm; // BCRM,VIP促销类型,E3（1-查询商品时计算，成交价=原有折扣价*会员折扣率,2-付款时计算，计算方式见vipCalcType
	public char vipPayExcp; // BCRM,VIP折扣是否除外受限付款方式(Y/N),E3(1/2,Y/N)
	public String vipCalcType; // BCRM,VIP折上折计算方式(Y/N),E3(1/2,Y/N,1-取会员类折上折和商品会员折的低价/2-取商品会员折定义的折上折)
	public char ismj; // BCRM,存在除券付款是否参与满减(Y/N)，T3
	public char mjtype; // BCRM,满减规则定义(N-满XXX减XX/Y-满XXX-YYY减XX),E6
	public char mjloop; // BCRM,是否循环参与满减(Y/N),E5
	public char couponRuleType; // 收券模式（Y-商品金额用于计算收券金额/N-商品金额-已收金额用于计算收券金额）
	public char printMode; // CCZZ,打印顺序(A-先机制后平推/B-先平推后机制),ZZ
	public String cupCardPwd; // CCZZ,cup卡密码,MK
	public String cardexptcmd;			//HB 当对卡POSSERVER进行单独布署时，不需向其发送的命令列表(23,26)
	public char isenablefq;	  //BSTD是否启用券功能,HC (N/Y)
	public char istsfq;		 //BSTD是否通收返券,HC (N/Y
	public char isbackoverpre; // BSTD,促销是否采用后单压前单方式(Y/N),HD
	public char isenablezklog;//BSTD,促销是否开启折扣日志 (Y,N),HE
	public int hykinputmode;//BSTD,超市会员卡加入参数控制输入模式 (Y,N),HF
	public char isusecoinbag;//BSTD,超市手工输入卡号是否允许使用零钱包(Y,N),HF
	public String hykhandinputflag;//BSTD,用于标识超市手工输入卡号的前缀标志(@|#...),HF
	
	public char isprintpopflag;//BSTD,是否打印商品促销标志 (Y,N),HG
	public char isprintdzcsl;//BSTD,是否将电子称数量打印成整数(Y,N),HG
	
	public char enableiputzszrebate;//BSTD,是否采用折上折的方式输入折让金额 (Y,N),HH
	public char enabledzcCoefficient;//BSTD,是否启用电子称转换系数(Y,N),HH
	public String commMerchantId;//BSTD,公共商户ID,HI
	public String visiblepaycode;//BSTD,超市需隐藏的付款编码,HJ
	public char backticketctrl;//BSTD,是否强制控制退货小票不允许再退,HK
	public char issplitdzc;//BSTD,会员限量中是否对电子称进行拆分,HL
	public String backPaycode;//退货时使用的支付方式,HM
	public String isWater; // JDHX 刷会员卡是否提示缴纳水费 HN
	public String isShowCatid; // LRTD部类合计金额提示， 部类：1=食品 2=生鲜 3=非食品 4=硬百 5=软百 0=不提示  HO
	public char isDouble;   //BSTD,满一定金额用x元的券；值为Y，按照已付款金额可用券金额成倍数增长；值为N，只能用x元券； HP
	
	public char printYXQ; // BHCM,是否在小票上打印券有效期,BH
	public int mzkChkLength; // ZSBH 面值卡消费校验码长度 ZB
	public String showerrorcmd; // ZSBH 控制是否显示通讯返回的提示信息,ZH
	public String salesReturncodeList; //ZSBH,ZSPJ银行卡允许隔日退货的角色代码列表,YT(01,02)逗号隔开
	public String hbPaymentCode; //ZSBH,ZSPJ电子红包付款代码,YU
	public String hbPaymentUrl;//ZSBH,ZSPJ电子红包服务器地址,YN
	
	public char isBankDzj;//ZSPJ平价银行电子券
	public char issendcrmnohyk; // ZSPJ ,在非会员状态下是否将小票发送至CRM,ZP
	public String prebarcode; // ZSPJ, 区别条码与电子秤码前缀编码,ZJ
	public char crmswitch; //ZSPJ, 控制命令不往CRM发送,ZI(Y-发/N-不发)
	public double isSalePrecision; //XJLY,销售精度控制标准，E8(0-不控制/XX-若取到会员折扣，单品的基本售价大于等于XX销售精度以对应折扣定义单据表头为准;若单品的基本售价小于XX,按正常销售商品的精度控制)
	public String mdcode; //KSSS,满抵付款代码,MD (满抵付款代码,idno中的信息  如：0105,A)
	public String isprintdjq;  //haws,重打印时是否打印兑奖券  DJ
	
	public String disablepaycodewhenrebate; // 吴裕泰
											// JWYT,当存在促销时禁止使用的付款方式(各付款编码用逗号分隔)
											// ,WA
	public char isctrlthpay; // 吴裕泰 JWYT,在指定小票退货时，是否严格控制支付方式，金额与原小票付款一致,WB	
	
	public String marsmerchantId;  //吴裕泰 JWYT,mars商户号 WC
	public String marsKey;  //吴裕泰 JWYT, mars密钥 WD
	public String marsurl; //吴裕泰 JWYT, 服务器url WE
	public String marsVer;	//吴裕泰 JWYT,mar版本 WF
	
	public char isGroupJSLB; // bjcx 团购是否参与价随量变，WC
	public char isxxcj;//超市发信息采集（Y/是,N/否）WD
	public String xfcardmerchantno; //超市发薪福卡商户号BC
	public String xfcardmpwd; //超市发薪福卡接入密码BS
	public String xfcardsrvurl; //超市发薪福卡服务器地址BF
	public double scoreAmountLimit; //超市发积分消费提示门槛BG
	public String limitpaytype;	//超市发售卡被限的付款方式,CS
	public char isenableyhps;	//超市发是否启用印花派送,CF
	
	public String bankreate;//京客隆银联满减参数,BK(格式:卡1bin前6位;卡2bin前6位;...,满金额/减金额,封顶金额|...)
	
	public char ishmkzsz;//广缘超市,是否全场折上折 GH 
	public String hmklevel; //广缘超市，惠民卡级别 GB 
	public double hmkrebate;//广缘超市，惠民卡折扣值GY
	
	public double limitDisRate; // JCGJ 超过多少折扣后，不参加组合促销 WE
	public char isMustCustCoupon; // JCGJ 买券时是否必须刷会员卡 WF
	public double maxPriceInCardSale; // JCGJ 售卡时能输入的最大金额 WG
	public String grantRole; // JCGJ 超市模式下可以授权重打印的角色code WH
	
	public char isprinticandmzk;//爱家控制是否打印IC卡及面值卡消费凭证,WI
	public String cyCrmUrl ; //延百尚家--长益CRM前置机IP及端口;WJ(例如127.0.0.1:6000)
	public String cyCrmUsrPwd;//延百尚家--长益CRM前置机登录用户及密码;WK(用户名，密码)
	public char isSuperMarketPop; // 是否启用超市数量促销;WL(Y:启用/N:不启用)
	public char isEnableLHCard;	//是否启用联华储值卡 ;WM(Y:启用/N:不启用)
	
	public String promotionDiscountPayCode;	// JJLS 产生促销折扣的付款代码，逗号分隔	WN
	public double cardDiscountRate;	// JJLS 卡折扣的汇率  WO
	public String cardDiscountPayCode;	// JJLS 产生卡折扣的付款代码，逗号分隔  WO
	public String cardRebatePayCode;	// JJLS 产生返利折扣的付款代码，逗号分隔  WP
	public String ICCardPayment;	// JJLS 九江联盛IC卡付款代码 WQ
	public String yePrintPayCode;	// JJLS 九江联盛需要打印余额的付款方式代码 W2
	public String cardDiscountPayCode1;	// JJLS 产生卡折扣的付款代码，逗号分隔  WW
	
	
	public String feeCode;  // NJXB 黄金手续费代码 	WR
	public double feeRate;  // NJXB 黄金手续费费率 	WR
	public String feePayment;  // NJXB 黄金手续费费率 	WR
	
	public String bZlPayCode;		//ZMJC 补找零付款方式编码 WS
	public char isEnableCustInput;		//ZMJC 是否启用顾客信息录入 WT(是否启用顾客信息录入,是否启用顾客信息录入)
	public char isEnableCustInput_TH;		//ZMJC 是否启用顾客信息录入 WT(是否启用顾客信息录入,是否启用顾客信息录入)
	public String mktZWB;			//ZMJC 门店主币种 WU
	public int saletimelimit;	//hhdl 控制商品销售时间 WV,
	public char isEnable17code;	//是否启用17位码，WV
	public String gwkHGUrl;//ZMSY 海关平台信息（Y|http://192.168.3.18:8888/pip/SearchCusInfWS|100） ,WW，即是否调用海关平台|海关平台地址|超时时间（秒）
	public String gwkSvrUrl;//ZMSY 购物卡服务器配置（Y|http://172.17.6.193:8080/PosServerGWK/PosServer）,WX,即：是否启用|地址
	public String gwkSvrCmdlist;//ZMSY 购物卡服务器命令列表（15,38,49,53,54,55,826,827,828,829,832,833,834,836,845,846），WY:含购物卡、小票相关的命令
	public String gwkQuan_iszl;//ZMSY 购物卡券是否多次使用（Y允许多次使用，即允许多次使用/N不允许多次使用，只可溢余，即一次性用完），WZ	
	
	public String CuseJFSaleRule; // HRSL,积分消费规则 WY
		
	public String EBillandSgd;  //WQBH,是否允许手工单和电子单据一起付款 (N/Y) W1
	public String isPrintDHY;   //WQBH,是否打印大会员积分调整签购单(N/Y) W1
	public String isAutoCheckIn; //WQBH,是否登录收银员时大会员自动签到(N/Y)W1  默认Y
	public String isNEWDHY;//WQBH,是否使用新会员(N/Y) 默认N  W1
	public String WHstoreId;//WQBH,大会员接口中万汇电商门店ID  W7
	
	public String sgQuan_paycode;//ZMSY 手工券付款方式编码(中间用逗号隔开) W2
	public String isUseClk;//ZMJC 是否启用常旅卡功能 W3
	public int enterNum;//ZMJC 付款明细,过虑回车数量 W4
	public char isInputZCD;		//ZMJC 是否录入暂存单 W5(Y/N) 默认N
	public char isUsePopLimit;//ZMJC 是否启用普通分期促销限量W6（N/Y）默认为N
	public char isInvokeZHX;//ZMSY  是否调用中航信接口(是否调用中航信接口|超时时间(秒));W8（N,15）
	public int ZHXTimeout;//ZMSY 中航信超时时间(是否调用中航信接口|超时时间(秒));W8（N,10）
	public char isUseNewBankZS;//NBBH 新银行追送（是否启用新银行追送,银行卡付款方式列表(当有多个时,中间用|隔开),追送的付款方式代码）;W9(N,03|04,0301)
	public String BankPaycode;//NBBH 新银行追送（是否启用新银行追送,银行卡付款方式列表,追送的付款方式代码）;W9(N,03|04,0301)
	public String BankZSPaycode;//NBBH 新银行追送（是否启用新银行追送,银行卡付款方式列表,追送的付款方式代码）;W9(N,03|04,0301)
	
	public String taxcompanyname;	//LY ,临沂东方税控销方名称
	public String taxcompanyid;		//LD，临沂东方税控销方ID
	public String taxlimitpay;		//LF，临沂东方税控不参与计税的付款编码
	public String taxfailedtip;		//LL，税控失败后的自定义消息
	public char isMultiMkt;		//LW，是否多门店
	
	public double hflczcperje;		//HFHF,红府零钞每笔存入金额 LC
	public double hflczcacctuplimit;	//HFHF,红府零钞账户的余额上限 LC
	
	// 以下是专卖参数，均以Z字打头，希望各位仁兄别在用Z字打头的参数了
	public char iscfginputarea; // 是否启用输入区配置 ,Z1
	public char iscodeupper; // 条码或编码是否转为大写 Z2,
	public char whenprintbill; // 小票打印时机(Y-上传小票后打，N-上传小票前打)Z3
	public char backpayctrl; // 退货时对实收金额和应付金额进行控制(Y-不允许交易/N-默认不控制);Z4
	public String jfPayCodeList;	//参与积分的付款方式列表(付款方式之间用逗号隔开)Z5
	
	public String isDel;	//CCZZ,是否能删除商品ID
	public char isPrintCPR;	//CCZZ,是否打印停车联IE
	public char isprintgkl ; //CCZZ,打印顾客意见联 Y6

	public char noCustSendToPop;		//CBBH,未刷会员卡时小票是否送到CRM,ES
	public char noCustFindPop; 			//CBBH,未刷会员卡时是否查找CRM规则促销,ET
	public char isUseBankReadTrack;		//CBBH,是否启用银联刷卡器,IF
	public String cbMzkSvrAddress;		//CBBH,重百储值卡服务器地址,IH（IP|端口|超时时间毫秒）
	public String cbXsjMzkSvrAddress;		//CBBH,重百新世纪储值卡服务器地址,II（IP|端口|超时时间毫秒）
	public String bankPayList; 			//CBBH,银联(含预付卡)付款方式列表,IJ
	public char isUnityMzkSrv;		//CBBH,是否统一提货卡服务器（为重百server）,IK（N/Y）
	public String noBackPaycodeList;//CBBH,不允许退换(大换小)货的付款方式列表,IL(0405,0402,0400,0401,0508,42)
	public char istcl;//CBBH,是否打印停车联 IM
	public char isfp;//CBBH,是否打印发票 IN
	public char iser;//CBBH,是否打印二维码 IO
	public String isbackpay;//CBBH,退货不允许使用的付款方式 IP
	public String issalepay;//CBBH,销售不允许使用的付款方式 IQ
	public String isnewpop;//CBBH,是否是新促销 IR
	public String isnewmktcode;//CBBH,是否转换门店 IS
	public String isprintback;//CBBH,是否打印退货小票 IT
	public char sljd;//CBBH,数量精度0,默认;1,四舍五入到3位;2,截断到3位

	public int printNo;//NBBH 排序打印的数量 DL （0,1,2）
	
	public char printhgbill; //HTSC 是否打印换购联


	public String WebserviceURL;   //HYCS webservice访问地址  W9
	public String WebserviceUser;   //HYCS webservice认证用户  W9
	public String WebservicePw;   //HYCS webservice认证密码  W9
	
	// 参数以ParaValueDef结构按行存放在表中,系统启动是按代码转换为SysParaDef的相应成员
	public class SysParaDef
	{
		// 参数组定义
		public String[][] sysparagroup = { { "MARKET", "门店资料" }, { "LOGIN", "登录相关" }, { "IDLETIME", "定时空闲相关" }, { "GOODS", "商品扫码相关" }, { "CUSTOMER", "会员相关" }, { "BACK", "退货相关" }, { "POP", "促销相关" }, { "PRINT", "打印相关" }, { "SALE", "交易相关" }, { "PAY", "付款找零" }, { "PAYIN", "缴款相关" }, { "GRANT", "授权相关" }, { "MZK", "卡交易相关" }, { "PDCHECK", "盘点相关" }, { "BCRM", "百货CRM相关" }, { "HZJB", "解百项目相关" }, { "CCZZ", "卓展项目相关" }, { "OTHER", "其他相关" } };

		// 参数描述
		public GlobalParaDesc isJavaPosManager = new GlobalParaDesc("LOGIN", "后台POS管理系统是否为WEB版管理系统", "N", new String[][] { { "N", "老式PB的POS管理" }, { "Y", "新版WEB的POS管理" } });
		public GlobalParaDesc allowsyjip = new GlobalParaDesc("LOGIN", "是否允许款机IP与设置IP不一致时登录", "N", new String[][] { { "N", "不能登录" }, { "Y", "允许登录" } });
		public GlobalParaDesc allowgh = new GlobalParaDesc("LOGIN", "是否允许同一个工号同时登录多个款机", "N", new String[][] { { "N", "必须签退才能登录其他款机" }, { "Y", "允许同时登录多个款机" } });
		public GlobalParaDesc mktname = new GlobalParaDesc("MARKET", "门店名称", "", new String[][] { { "", "申请注册码后不要随意改变" } });
		public GlobalParaDesc mktcode = new GlobalParaDesc("MARKET", "门店编号", "", new String[][] { { "", "申请注册码后不要随意改变" } });
		public GlobalParaDesc shopname = new GlobalParaDesc("MARKET", "商场名称", "", null);
		public GlobalParaDesc jygs = new GlobalParaDesc("MARKET", "经营公司编号", "", null);
		public GlobalParaDesc yyygz = new GlobalParaDesc("GOODS", "是否控制营业员串柜销售", "Y", new String[][] { { "Y", "不允许串柜销售" }, { "N", "允许串柜销售" }, { "A", "控制串柜且可修改营业柜组" } });
		public GlobalParaDesc isxh = new GlobalParaDesc("GOODS", "是否允许负库存销售", "Y", new String[][] { { "Y", "允许销红" }, { "N", "不允许销红" } });
		public GlobalParaDesc customvsgoods = new GlobalParaDesc("CUSTOMER", "销售时什么时候刷会员卡", "Y", new String[][] { { "Y", "商品输入前后皆可" }, { "A", "必须在商品前刷卡" }, { "B", "必须在商品后刷卡" } });
		public GlobalParaDesc custommust = new GlobalParaDesc("CUSTOMER", "必须刷会员卡才可以销售", "N", new String[][] { { "N", "允许非会员销售" }, { "Y", "只允许会员销售" } });
		public GlobalParaDesc bdjyh = new GlobalParaDesc("GOODS", "暂时未做用处", "Y", null);
		public GlobalParaDesc inputydoc = new GlobalParaDesc("BACK", "指定小票退货的模式", "A", new String[][] { { "N", "不指定小票退货" }, { "Y", "指定原小票输入退货商品" }, { "A", "调出原小票选择商品" }, { "B", "必须指定原小票输入退货商品,否则不能退货" }, { "C", "必须调出原小票选择退货商品,否则不能退货" } });
		public GlobalParaDesc ghlist = new GlobalParaDesc("BACK", "退货授权工号", "", new String[][] {{ " ", "工号001,002" }});
		public GlobalParaDesc isconnect = new GlobalParaDesc("IDLETIME", "是否定时自动联网", "Y", new String[][] { { "Y", "断网后自动重联" }, { "N", "不自动联网" } });
		public GlobalParaDesc querytime = new GlobalParaDesc("IDLETIME", "定时与后台服务器通讯时间", "180", new String[][] { { "", "以秒为单位" } });
		public GlobalParaDesc maxxj = new GlobalParaDesc("SALE", "款台最大现金存量", "0", new String[][] { { "", "0为不控制存量,当收现金超过设置必须缴款才能收银" } });
		public GlobalParaDesc custinfo = new GlobalParaDesc("SALE", "是否启用顾客信息采集", "GNNN", new String[][] { { "", "GXXX-老式类别模式,T-新式树形结构" } });
		public GlobalParaDesc custinfocyclesel = new GlobalParaDesc("SALE", "是否循环采集顾客信息", "N", new String[][] { { "N", "否" }, { "Y", "是" } });
		public GlobalParaDesc noshowcustinfogroup = new GlobalParaDesc("SALE", "在采集列表中不显示的顾客信息分组", "", new String[][] { { "", "多个分组代码以;分隔" } });
		public GlobalParaDesc rulepop = new GlobalParaDesc("POP", "满减促销模式", "N", new String[][] { { "N", "无满减促销" }, { "Y", "老连锁选择满减或满赠规则" }, { "R", "老连锁自动选择满减或满赠规则" }, { "F", "FOXTOWN满减方案,前台执行满减,不用选择" }, { "A", "选择是满减还是满赠，前台提示选择满减还是满赠,如果选满赠，前端不处理" }, { "S", "西武满减方案" }, { "C", "百货CRM+CMPOP促销模型" } });
		public GlobalParaDesc localfind = new GlobalParaDesc("GOODS", "联网时是否本地优先查询", "N", new String[][] { { "N", "联网时只查询网上数据" }, { "Y", "联网优先在本地查询再查网上" }, { "Z", "联网也只查询本地数据" } });
		public GlobalParaDesc lxprint = new GlobalParaDesc("PRINT", "收银练习是否打印小票", "N", new String[][] { { "N", "不打印" }, { "Y", "打印" } });
		public GlobalParaDesc chglimit = new GlobalParaDesc("PAY", "最大允许找零金额", "99", new String[][] { { "", "0为不限制" } });
		public GlobalParaDesc zljd = new GlobalParaDesc("PAY", "找零金额的截取精度", "Y", new String[][] { { "Y", "按收银机精度" }, { "0", "精确到分" }, { "1", "四舍五入到角" }, { "2", "截断到角" }, { "3", "四舍五入到元" }, { "4", "截断到元" } });
		public GlobalParaDesc autoopendrawer = new GlobalParaDesc("LOGIN", "收银员登录后是否自动打开钱箱", "Y", new String[][] { { "Y", "自动弹开钱箱,以便放入备用金" }, { "N", "不自动弹出钱箱" } });
		public GlobalParaDesc isshowname = new GlobalParaDesc("LOGIN", "收银界面收银员信息显示内容", "N", new String[][] { { "Y", "显示名字" }, { "N", "显示工号" } });
		public GlobalParaDesc codesale = new GlobalParaDesc("GOODS", "是否允许使用商品代码销售", "Y", new String[][] { { "Y", "允许" }, { "N", "不允许" } });
		public GlobalParaDesc dzccodesale = new GlobalParaDesc("GOODS", "是否允许电子称商品直接用代码销售", "N", new String[][] { { "N", "必须使用电子秤规则条码销售" }, { "Y", "输入秤重价格,反算数量" }, { "A", "输入秤重数量,计算价格" } });
		public GlobalParaDesc isCalcAsPfj = new GlobalParaDesc("GOODS", "电子秤是否直接以促销价打印条码", "Y", new String[][] { { "Y", "电子秤的单价被同步为系统促销单的价格,促销价同步到商品主档PFJ" }, { "N", "电子秤的单价与商品主档价格同步" } });
		public GlobalParaDesc cashsale = new GlobalParaDesc("GOODS", "款台现金过量是否允许继续销售", "N", new String[][] { { "N", "不能继续销售必须先缴款" }, { "Y", "提醒但可以继续销售" } });
		public GlobalParaDesc downloadtime = new GlobalParaDesc("IDLETIME", "定时下载增量数据的时间间隔", "600", new String[][] { { "", "以秒为单位,0则不进行增量更新" } });
		public GlobalParaDesc downloadsaleupdate = new GlobalParaDesc("IDLETIME", "在销售状态时也定时更新增量数据", "N", new String[][] { { "N", "只在空闲未输入商品时更新增量数据" }, { "Y", "无论是否输入商品都定时更新增量数据" } });
		public GlobalParaDesc fdprintyyy = new GlobalParaDesc("PRINT", "打印小票时是否分单打印营业员联", "N", new String[][] { { "Y", "打印小票也打印营业员联" }, { "N", "不打印营业员联" }, { "A", "只打印营业员联" } });
		public GlobalParaDesc printyyygrouptype = new GlobalParaDesc("PRINT", "营业员联的分组打印方式", "1", new String[][] { { "1", "按营业员+柜组分组" }, { "2", "按单品分组" }, { "3", "按柜组分组" }, { "4", "按营业员分组" }, { "5", "按手工发票号分组" } });
		public GlobalParaDesc fdprintyyytrack = new GlobalParaDesc("PRINT", "营业员联的打印输出栈", "3", new String[][] { { "1", "小票栈" }, { "2", "备卷栈" }, { "3", "平推栈" } });
		public GlobalParaDesc printyyhsequence = new GlobalParaDesc("PRINT", "小票和营业员联的打印顺序", "A", new String[][] { { "A", "先打营业员联再打小票联" }, { "B", "先打小票联再打营业员联" } });
		public GlobalParaDesc isinputjkdate = new GlobalParaDesc("PAYIN", "缴款单是否输入缴款销售日期", "N", new String[][] { { "N", "默认为当天晚缴款,不输入销售日期" }, { "Y", "输入缴款的销售日期" } });
		public GlobalParaDesc printjknum = new GlobalParaDesc("PAYIN", "缴款单的打印份数", "2", new String[][] { { "", "0则不打印缴款单" } });
		public GlobalParaDesc isGetNetMaxJkdNo = new GlobalParaDesc("PAYIN", "是否从网上同步最大缴款单号", "N", new String[][] { { "N", "否" }, { "Y", "是" } });
		public GlobalParaDesc printJKList = new GlobalParaDesc("PAYIN", "是否允许只打印缴款列表", "N", new String[][] { { "N", "否" }, { "Y", "是" } });
		public GlobalParaDesc closedrawer = new GlobalParaDesc("SALE", "交易完成弹开钱箱后未关闭不允许继续下笔交易", "Y", new String[][] { { "Y", "等待钱箱关闭后才开始下笔交易" }, { "N", "按键确认后即开始下笔交易" } });
		public GlobalParaDesc ismzkgh = new GlobalParaDesc("GRANT", "允许红冲面值卡交易的工号列表", "", new String[][] { { "", "多个工号用,号分隔" } });
		public GlobalParaDesc grantpwd = new GlobalParaDesc("GRANT", "员工卡授权是否输入密码进行验证", "N", new String[][] { { "N", "不输入密码" }, { "Y", "要输入密码" } });
		public GlobalParaDesc grtpwdshow = new GlobalParaDesc("GRANT", "员工卡授权是否以暗码显示帐号", "N", new String[][] { { "N", "显示工号" }, { "Y", "不显示工号,显示*" } });
		public GlobalParaDesc grantpasswordmsr = new GlobalParaDesc("GRANT", "员工卡授权的密码是否只能允许刷卡", "N", new String[][] { { "N", "可以手输或刷卡" }, { "Y", "只能刷不能输入" } });
		public GlobalParaDesc payover = new GlobalParaDesc("PAY", "交易付款输入是否采用覆盖方式", "N", new String[][] { { "N", "每次输入作为一行新付款" }, { "Y", "相同付款以最后一次输入为准" } });
		public GlobalParaDesc payex = new GlobalParaDesc("PAY", "二级付款方式付款输入后不回到上级付款的付款列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc fpjepayex = new GlobalParaDesc("PRINT", "发票打印中开票金额除外付款方式", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc paychgmore = new GlobalParaDesc("PAY", "交易找零是否存在多币种找零", "N", new String[][] { { "Y", "多币种找零" }, { "N", "单币种找零,找零最后一个可找零付款方式" }, { "A", "单币种找零,在找零界面显示付款明细" } });
		public GlobalParaDesc paychgyy = new GlobalParaDesc("PAY", "最后付款产生溢余后溢余金额不找零的付款方式", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc msrspeed = new GlobalParaDesc("GRANT", "键盘刷卡槽有效输入时间", "0", new String[][] { { "", "毫秒单位,0表示总是可以手输" } });
		public GlobalParaDesc cardsvrurl = new GlobalParaDesc("MZK", "独立储值卡服务器URL", "", new String[][] { { "", "http://IP地址:端口/服务路径/PosServer" } });
		public GlobalParaDesc cardrealpay = new GlobalParaDesc("MZK", "储值卡交易采用即时扣款模式", "N", new String[][] { { "N", "所有付款完成后集中扣款" }, { "Y", "每刷一张卡立即扣款" } });
		public GlobalParaDesc havebroken = new GlobalParaDesc("SALE", "交易是否启用断点保护", "Y", new String[][] { { "Y", "断电保护自动登录" }, { "L", "断电保护不自动登录" }, { "N", "不启用断电保护" } });
		public GlobalParaDesc checknetlogin = new GlobalParaDesc("LOGIN", "是否联网检查登录合法性", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc rebatepriacemode = new GlobalParaDesc("SALE", "手工折让是否采用成交价方式输入", "Y", new String[][] { { "Y", "输入最终成交价模式" }, { "N", "输入折让减价金额模式" } });
		public GlobalParaDesc paysummarymode = new GlobalParaDesc("REPORT", "收银报表付款统计采用主付款方式统计", "Y", new String[][] { { "Y", "按一级主付款汇总统计" }, { "N", "按各付款代码分别统计" } });
		public GlobalParaDesc autojfexchange = new GlobalParaDesc("GOODS", "扫码后自动提示商品是否进行换购", "N", new String[][] { { "N", "按换购键检查商品是否进行换购" }, { "Y", "自动检测并提示商品进行换购" } });
		public GlobalParaDesc quitpwd = new GlobalParaDesc("GRANT", "非维护员退出系统的密码", "9999", new String[][] { { "", "为空则不启用退出密码,否则非维护员必须输入正确密码才能返回操作系统" } });
		public GlobalParaDesc thmzk = new GlobalParaDesc("BACK", "退货时是否允许退款到储值卡", "N", new String[][] { { "N", "不允许退款到储值卡" }, { "Y", "允许退款到储值卡" } });
		public GlobalParaDesc num_down = new GlobalParaDesc("IDLETIME", "实时更新每次下载的增量文件个数", "10", new String[][] { { "", "0则不下载增量数据" } });
		public GlobalParaDesc validver = new GlobalParaDesc("MARKET", "当前业务系统允许的有效版本号", "", new String[][] { { "", "版本不匹配的客户端不允许联网,为空则不检查版本匹配" } });
		public GlobalParaDesc iscardmsg = new GlobalParaDesc("PAY", "刷银联卡付款时是否提示卡号", "N", new String[][] { { "N", "否" }, { "Y", "是" } });
		public GlobalParaDesc printdelayline = new GlobalParaDesc("PRINT", "打印多少行后进行延时", "0", new String[][] { { "", "0进行延时,一般串并口打印机为避免缓冲不足需要延时" } });
		public GlobalParaDesc printdelaysec = new GlobalParaDesc("PRINT", "打印延时每次延时多少毫秒", "0", new String[][] { { "", "以毫秒为单位" } });
		public GlobalParaDesc secMonitorPlayer = new GlobalParaDesc("LOGIN", "款机是否允许双屏广告播放", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc memcardsvrurl = new GlobalParaDesc("CUSTOMER", "独立会员/CRM服务器URL", "", new String[][] { { "", "http://IP地址:端口/服务路径/PosServer" } });
		public GlobalParaDesc cardpasswd = new GlobalParaDesc("MZK", "储值卡是否先输入密码再联网查询卡资料", "N", new String[][] { { "Y", "传入卡密码联网验证" }, { "N", "返回卡密码本地验证" } });
		public GlobalParaDesc onlyUseBReturn = new GlobalParaDesc("BACK", "是否只能使用后台退货", "N", new String[][] { { "N", "可以同时使用前台退货和后台退货" }, { "Y", "只能使用后台退货" } });
		public GlobalParaDesc exceptBReturnType = new GlobalParaDesc("BACK", "只能后台退货时例外的交易类型", "", new String[][] { { "", "多个交易类型用,号分隔" } });
		public GlobalParaDesc acceptfjkrule = new GlobalParaDesc("MZK", "电子返券是否启用活动规则匹配模式", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc mjPaymentRule = new GlobalParaDesc("POP", "CRM满减促销的除券付款列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc refundByPos = new GlobalParaDesc("BACK", "退货时是否要计算处理退货扣回", "N", new String[][] { { "N", "不处理" }, { "Y", "要计算" } });
		public GlobalParaDesc refundPayMode = new GlobalParaDesc("BACK", "退货扣回付款方式列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc refundCouponPaymode = new GlobalParaDesc("BACK", "退券交易时扣回付款列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc lczcmaxmoney = new GlobalParaDesc("CUSTOMER", "会员零钞转存限制金额", "0", new String[][] { { "", "小于该金额的找零金额可进行零钞转存" } });
		public GlobalParaDesc isAutoLczc = new GlobalParaDesc("CUSTOMER", "强制将零钞存入会员零钞账户", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isAutoPayByLczc = new GlobalParaDesc("CUSTOMER", "强制使用会员零钞账户中的零钞进行付款", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isReMSR = new GlobalParaDesc("CUSTOMER", "使用会员卡进行支付时(积分、零钞等)是否允许重新刷会员卡", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc iscardcode = new GlobalParaDesc("CUSTOMER", "bstd刷会员卡截取等号（Y/N)Y", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc serialmzkrule = new GlobalParaDesc("MZK", "储值卡连号输入模式的起止规则", "0,-1", new String[][] { { "", "付款代码=卡号起始位,卡号结束位|付款代码=卡号起始位,卡号结束位" } });
		public GlobalParaDesc feechargelimit = new GlobalParaDesc("SALE", "最大允许零头折让的金额", "0", null);
		public GlobalParaDesc havePayRule = new GlobalParaDesc("PAY", "付款方式分摊收款规则的启用方式", "N", new String[][] { { "N", "不启用" }, { "Y", "只分摊有规则的付款方式" }, { "A", "分摊所有付款方式" } });
		public GlobalParaDesc fjkyetype = new GlobalParaDesc("MZK", "电子返券余额券种类型", "", new String[][] { { "", "付款代码=券类|券种;付款代码=券类|券种;(券类=Z-纸打印券/T-退打印券/D-电子券,券种=A,B,F)" } });
		public GlobalParaDesc refundAllowBack = new GlobalParaDesc("BACK", "后台返回的需要扣回金额大于0是否允许退货", "Y", new String[][] { { "Y", "允许退货" }, { "N", "不能退货" } });
		public GlobalParaDesc refundScale = new GlobalParaDesc("BACK", "判断扣回金额是否足够的精度方式", "0", new String[][] { { "0", "精确到分" }, { "1", "四舍五入到角" }, { "2", "截断到角" }, { "3", "四舍五入到元" }, { "4", "截断到元" } });
		public GlobalParaDesc customerbyconnect = new GlobalParaDesc("CUSTOMER", "会员卡必须联网使用", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc customerbysale = new GlobalParaDesc("CUSTOMER", "开始交易前是否主动提示刷会员卡", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc custDisconnetNoPeriod = new GlobalParaDesc("CUSTOMER", "脱网不检查会员的合法性", "N", new String[][] { { "Y", "仅记录不检查" }, { "N", "要检查合法性" } });
		public GlobalParaDesc apportMode = new GlobalParaDesc("POP", "受限付款方式的分摊模式", "A", new String[][] { { "A", "顾客手工输入" }, { "B", "按金额占比均摊" }, { "C", "按商品倒序分摊" }, { "D", "按商品顺序分摊" }, { "E", "按比例大小分摊" }, { "F", "按可收大小分摊" } });
		public GlobalParaDesc loopInputPay = new GlobalParaDesc("PAY", "自动连续输入付款的付款方式列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc printpaysummary = new GlobalParaDesc("PRINT", "小票打印中需要汇总的付款方式列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc isinputpremoney = new GlobalParaDesc("LOGIN", "登录后是否输入备用金", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc calcjfbyconnect = new GlobalParaDesc("CUSTOMER", "交易完成后立即联网实时计算并返回积分", "N", new String[][] { { "N", "不实时计算" }, { "Y", "实时积分并提示" }, { "A", "实时积分不提示" } });
		public GlobalParaDesc sendhyjf = new GlobalParaDesc("CUSTOMER", "得到实时计算积分后是否同步到会员服务器", "N", new String[][] { { "Y", "同步积分" }, { "N", "不同步" } });
		public GlobalParaDesc sendsaletocrm = new GlobalParaDesc("SALE", "是否同步发送小票到CRM会员服务器", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc mzkbillnum = new GlobalParaDesc("PRINT", "储值卡交易打印储值卡联的份数", "0", new String[][] { { "", "0则不打印储值卡联" } });
		public GlobalParaDesc salebillnum = new GlobalParaDesc("PRINT", "交易小票的打印份数", "1", null);
		public GlobalParaDesc isGoodsSryPrn = new GlobalParaDesc("PRINT", "交易小票打印时是否商品行汇总打印", "N", new String[][] { { "N", "不汇总" }, { "Y", "同编码、条码、柜组的商品行汇总" } });
		public GlobalParaDesc allowbankselfsale = new GlobalParaDesc("PAY", "是否允许通过菜单功能单独进行银联消费交易", "N", new String[][] { { "N", "不允许" }, { "Y", "允许且交易时选择已刷消费" }, { "A", "允许但交易时不选择已刷消费" } });
		public GlobalParaDesc calcfqbyreal = new GlobalParaDesc("SALE", "交易完成后立即联网实时计算返券", "N", new String[][] { { "N", "不实时计算" }, { "Y", "实时计算电子券" }, { "A", "实时计算打印券" } });
		public GlobalParaDesc printInfo1 = new GlobalParaDesc("PRINT", "小票自定义打印信息1", "", new String[][] { { "", "YYYYMMDD,YYYYMMDD,打印信息;YYYYMMDD,YYYYMMDD,打印信息;" } });
		public GlobalParaDesc printInfo2 = new GlobalParaDesc("PRINT", "小票自定义打印信息2", "", new String[][] { { "", "YYYYMMDD,YYYYMMDD,打印信息;YYYYMMDD,YYYYMMDD,打印信息;" } });
		public GlobalParaDesc setPriceBackStatus = new GlobalParaDesc("BACK", "不指定小票退货时是否手工输入退货价格", "Y", new String[][] { { "Y", "手工输入" }, { "N", "不输价格" } });
		public GlobalParaDesc isbackpricestatus = new GlobalParaDesc("BACK", "不指定小票退货时缺省提示的退货价格", "N", new String[][] { { "N", "0" }, { "Y", "零售价" }, { "F", "批发价" } });
		public GlobalParaDesc backminmonyrange = new GlobalParaDesc("BACK", "不指定小票退货时退货金额最小金额", "0", new String[][] { { "", "0不限制" } });
		public GlobalParaDesc backmaxmoneyrange = new GlobalParaDesc("BACK", "不指定小票退货时退货金额最大金额", "0", new String[][] { { "", "0不限制" } });
		public GlobalParaDesc inputyyyfph = new GlobalParaDesc("SALE", "输入营业员后是否输入手工单编号", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc batchtotalrebate = new GlobalParaDesc("SALE", "总折扣商品分摊计算方式", "Y", new String[][] { { "Y", "按每个单品分别计算各自承担" }, { "N", "总折扣按商品价值占比分摊" }, { "A", "总折扣按商品可折扣金额占比分摊" } });
		public GlobalParaDesc CloseShowZkCompetence = new GlobalParaDesc("SALE", "手工折扣输入框不显示当前折扣权限", "N", new String[][] { { "Y", "不显示" }, { "N", "显示权限" } });
		public GlobalParaDesc FirstClearLsZk = new GlobalParaDesc("SALE", "取消键是否优先取消选中商品的手工折扣再取消交易", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc bankprint = new GlobalParaDesc("PRINT", "银联签购单打印份数", "2", null);
		public GlobalParaDesc displaybanktype = new GlobalParaDesc("PAY", "银联退货默认使用的交易类别", "N", new String[][] { { "N", "消费撤销" }, { "Y", "隔日退货" } });
		public GlobalParaDesc paycodebanktype = new GlobalParaDesc("PAY", "银联退货默认使用隔日退货的付款代码", "", new String[][] { { "", "如果未指定代码则所有银联付款都是隔日退货,否则仅指定的代码是隔日退货" } });
		public GlobalParaDesc custompayobj = new GlobalParaDesc("PAY", "自定义付款对象列表", "", new String[][] { { "", "付款对象类名1,代码1,代码2|付款对象类名2,代码3,代码4" } });
		public GlobalParaDesc findcustfjk = new GlobalParaDesc("CUSTOMER", "菜单查询会员卡信息的同时是否查询卡上的返券", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc payprecision = new GlobalParaDesc("PAY", "是否检查付款金额符合付款方式精度", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc goodsPrecision = new GlobalParaDesc("SALE", "付款前对所有商品成交价零头截取精度", "0", new String[][] { { "0", "不截取" }, { "0.01", "截断到分" }, { "0.1", "截断到角" }, { "1", "截断到元" } });
		public GlobalParaDesc ischoiceExit = new GlobalParaDesc("LOGIN", "退出系统时是否提示选择重启或关机", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc backRefundMSR = new GlobalParaDesc("MZK", "后台退货交易储值卡类付款是否提示刷卡退款", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc backgoodscodestyle = new GlobalParaDesc("BACK", "指定小票退货商品选择列表中编码显示方式", "A", new String[][] { { "A", "条码" }, { "B", "编码" } });
		public GlobalParaDesc validservicedate = new GlobalParaDesc("MARKET", "系统使用有效期串", "", new String[][] { { "", "授权加密串(YYYY-MM-DD,N)" } });
		public GlobalParaDesc HCcontrol = new GlobalParaDesc("GRANT", "红冲小票按付款方式设置哪些不允许红冲/哪些需要授权", "", new String[][] { { "", "GRANT|SALE|0400,0002;NOHC|BACK|0401" } });
		public GlobalParaDesc quickinputsku = new GlobalParaDesc("GOODS", "超市是否允许直接按回车输入上一个商品", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc goodsAmountInteger = new GlobalParaDesc("GOODS", "超市修改商品数量只能是整数数量", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc barcodeshowcode = new GlobalParaDesc("GOODS", "商品界面商品条码栏是否显示编码", "N", new String[][] { { "N", "输什么显示什么" }, { "Y", "总显示编码" }, { "A", "总显示条码" } });
		public GlobalParaDesc isinputnextgoods = new GlobalParaDesc("GOODS", "超市查找无效商品时是否只能按确认和回车键才关闭提示框继续扫下个商品", "N", new String[][] { { "Y", "确认键" }, { "N", "回车键" } });
		public GlobalParaDesc isblankcheckgoods = new GlobalParaDesc("PDCHECK", "是否允许无空白盘点单时创建盘点单", "Y", new String[][] { { "Y", "无空白盘点单自动创建盘点单" }, { "N", "必须有空白盘点单才能盘点" }, { "A", "检查盘点日期柜组仓位" } });
		public GlobalParaDesc checklength = new GlobalParaDesc("PDCHECK", "盘点单号长度", "0", null);
		public GlobalParaDesc checkgrouplen = new GlobalParaDesc("PDCHECK", "盘点单组长度", "0", new String[][] { { "", "<0表示只能输入单号且过程内自动生成组号,>0必须手工输入组号且长度相等,=0可输或不输组号|输取手工不取自动产生" } });
		public GlobalParaDesc ischeckadditive = new GlobalParaDesc("PDCHECK", "同商品盘点数量是否累加", "N", new String[][] { { "N", "不累加" }, { "Y", "每次输入累加" }, { "A", "以最后一次输入为准" }, { "B", "以最后一次输入为准" } });
		public GlobalParaDesc ischecksaveprint = new GlobalParaDesc("PDCHECK", "盘点单保存上传后是否立即打印", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc ischeckgz = new GlobalParaDesc("PDCHECK", "盘点单是否检查盘点商品与本次盘点柜组匹配", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc ischeckcode = new GlobalParaDesc("PDCHECK", "是否检查商品属于空白盘点单", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc ischeckquantity = new GlobalParaDesc("PDCHECK", "不定价商品是否盘点金额", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc printpaymode = new GlobalParaDesc("PRINT", "需要打印卡联的付款方式", "", new String[][] { { "", "付款代码1,付款代码2|付款代码3" } });
		public GlobalParaDesc debugtracelog = new GlobalParaDesc("MARKET", "是否开启跟踪日志", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc mzkStatistics = new GlobalParaDesc("REPORT", "储值卡统计报表中需要查询统计的", "", new String[][] { { "", "付款代码1,付款代码2|付款代码3" } });
		public GlobalParaDesc cmdCustList = new GlobalParaDesc("CUSTOMER", "发送到会员URL服务器的命令列表", "", new String[][] { { "", "多个命令用,号分隔,设置了命令列表则不在列表内的命令都发往主POSSERVER" } });
		public GlobalParaDesc unionVIPMode = new GlobalParaDesc("CUSTOMER", "联名会员卡查询方式", "A", new String[][] { { "A", "先选择规则后刷会员卡" }, { "B", "先刷会员卡后过滤规则" } });
		public GlobalParaDesc issetprinter = new GlobalParaDesc("PAY", "付款时时候断开打印机以便其他打印程序可使用(例如银联金卡工程)", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc maxSaleGoodsCount = new GlobalParaDesc("GOODS", "交易商品最大行数", "200", null);
		public GlobalParaDesc maxSalePayCount = new GlobalParaDesc("GOODS", "交易付款最大行数", "200", null);
		public GlobalParaDesc maxSaleGoodsQuantity = new GlobalParaDesc("GOODS", "交易单行商品最大数量", "9999.99", null);
		public GlobalParaDesc maxSaleGoodsMoney = new GlobalParaDesc("GOODS", "交易单行商品最大单价", "9999999.99", null);
		public GlobalParaDesc maxSaleMoney = new GlobalParaDesc("GOODS", "交易整单最大成交价", "9999999.99", null);
		public GlobalParaDesc fjkkhhl = new GlobalParaDesc("MZK", "退货扣回时返券卡的折现汇率", "", new String[][] { { "", "券种1,汇率|券种2,汇率|券种3,汇率|..." } });
		public GlobalParaDesc printInBill = new GlobalParaDesc("PRINT", "红冲和退货小票是否打印到发票主栈", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isHcPrintBill = new GlobalParaDesc("PRINT", "红冲交易是否打印红冲小票", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc removeGoodsModel = new GlobalParaDesc("GOODS", "交易删除商品的模式", "N", new String[][] { { "N", "光标选择删除" }, { "Y", "超市扫码删除" } });
		public GlobalParaDesc removeGoodsMsg = new GlobalParaDesc("GOODS", "删除商品时是否提示确认", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isMoneyInputDefault = new GlobalParaDesc("PAY", "付款输入框缺省是否显示剩余付款金额", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc MoneyInputDefaultPay = new GlobalParaDesc("PAY", "指定显示剩余付款金额的付款列表", "", new String[][] { { "", "不为空则指定代码才缺省剩余付款其他缺省0" } });
		public GlobalParaDesc onlineGd = new GlobalParaDesc("SALE", "挂单解挂模式", "N", new String[][] { { "N", "本地挂单解挂" }, { "Y", "联网挂单解挂" }, { "A", "允许多个挂单联网解挂合并" } });
		public GlobalParaDesc isPrintGd = new GlobalParaDesc("PRINT", "是否打印挂单小票", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc overNightBegin = new GlobalParaDesc("MARKET", "门店通宵营业起始日期时间点", "", new String[][] { { "", "YYYY/MM/DD|HH:MM:SS" } });
		public GlobalParaDesc overNightEnd = new GlobalParaDesc("MARKET", "门店通宵营业结束日期时间点", "", new String[][] { { "", "YYYY/MM/DD|HH:MM:SS" } });
		public GlobalParaDesc overNightTime = new GlobalParaDesc("MARKET", "门店通宵营业每天转钟时间点", "", new String[][] { { "", "HH:MM:SS" } });
		public GlobalParaDesc isHandVIPDiscount = new GlobalParaDesc("CUSTOMER", "是否启用手工VIP折扣输入", "N", new String[][] { { "N", "不启用" }, { "Y", "手工输入" }, { "A", "手工输入且" } });
		public GlobalParaDesc handVIPDiscount = new GlobalParaDesc("CUSTOMER", "手工VIP折扣率最低限制", "0", new String[][] { { "", "举例0.5表示5折" } });
		public GlobalParaDesc searchPosAndCUST = new GlobalParaDesc("CMLS", "百货新连锁版查询CRM相关数据同时查询POS和会员库", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isMoreSelectQuerygoods = new GlobalParaDesc("GOODS", "商品扫码模糊查询是否支持多选", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc useGoodsFrameMode = new GlobalParaDesc("SALE", "启用商品经营配置例外价处理模式", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc allowGoodsFrameSale = new GlobalParaDesc("SALE", "允许商品无经营配置时以门店号为经营柜组进行销售", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc checkGoodsDate = new GlobalParaDesc("PDCHECK", "系统设置的盘点日", "", new String[][] { { "", "YYYYMMDD" } });
		public GlobalParaDesc checkGoodsCw = new GlobalParaDesc("PDCHECK", "系统盘点仓位定义", "", null);
		public GlobalParaDesc isInputSaleAppend = new GlobalParaDesc("SALEAPPEND", "交易完成前是否提示录入小票附加资料", "N", new String[][] { { "N", "不录入" }, { "Y", "提示录入" }, { "M", "直接录入" } });
		public GlobalParaDesc saprintyyytrack = new GlobalParaDesc("SALEAPPEND", "小票附加资料打印栈", "2", new String[][] { { "1", "小票主栈" }, { "2", "备卷栈" }, { "3", "平推栈" } });
		public GlobalParaDesc saleAppendSaleType = new GlobalParaDesc("SALEAPPEND", "只在指定交易类型时才会提示录入附加信息", "", new String[][] { { "", "多个交易类型用,分隔" } });
		public GlobalParaDesc saleAppendStatus = new GlobalParaDesc("SALEAPPEND", "小票附加资料的处理方式", "Y", new String[][] { { "Y", "上传且打印" }, { "N", "不上传也不打印" }, { "S", "仅上传" }, { "P", "仅打印" } });
		public GlobalParaDesc issaleby0 = new GlobalParaDesc("SALE", "整单成交额为0允许销售", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isGoodsMoney0 = new GlobalParaDesc("SALE", "单价成交额为0允许销售", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc iscloseJkUI = new GlobalParaDesc("PAYIN", "缴款结束后是否关闭缴款界面", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isshowAllBcData = new GlobalParaDesc("REPORT", "收银员销售统计是否显示本人所有班次数据", "Y", new String[][] { { "Y", "是" }, { "N", "只显示当班数据" } });
		public GlobalParaDesc isusepaySelect = new GlobalParaDesc("PAY", "二级付款方式是否启用代码快速定位功能", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc ClawBackCalcModel = new GlobalParaDesc("PAY", "退款找零界面应退金额显示模式", "A", new String[][] { { "A", "所有实际收款减去扣回" }, { "B", "实际收款和扣回相同付款两两相抵,保留未相抵部分" }, { "C", "实际收款和扣回相同付款两两相抵,减掉未相抵部分" }, { "D", "只显示现金类的金额汇总" } });
		public GlobalParaDesc jdxxfkflag = new GlobalParaDesc("SALEAPPEND", "家电下乡返款时是否指定远小票和收银机号", "Y", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isInputPayMoney = new GlobalParaDesc("PAY", "付款输入框总是允许手工输入付款金额", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isinputjdfhdd = new GlobalParaDesc("SALEAPPEND", "交易是否输入家电发货地点", "N", new String[][] { { "N", "不自动弹出" }, { "Y", "每笔自动弹出" }, { "S", "直接继承上笔可按键修改" } });
		public GlobalParaDesc iscfgtable = new GlobalParaDesc("GOODS", "启用动态自定义商品销售列表界面", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc jdfhdd = new GlobalParaDesc("SALEAPPEND", "家电发货地点字典清单", "", new String[][] { { "", "code,name;code1,name1;" } });
		public GlobalParaDesc nodeletepaycode = new GlobalParaDesc("PAY", "不允许删除的付款方式列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc noinputpaycode = new GlobalParaDesc("PAY", "不允许选择付款的付款方式列表", "", new String[][] { { "", "多个付款方式用,号分隔" } });
		public GlobalParaDesc verifyDzcmname = new GlobalParaDesc("GOODS", "电子秤码校验位规则", "", new String[][] { { "EAN13", "EAN13" }, { "UPCA", "UPCA" }, { "EAN13_UPCA", "EAN13_UPCA" } });
		public GlobalParaDesc backgoodsminmoney = new GlobalParaDesc("BACK", "不指定小票退货时退货金额最小金额", "0", new String[][] { { "", "0不限制" } });
		public GlobalParaDesc backgoodsmaxmoney = new GlobalParaDesc("BACK", "不指定小票退货时退货金额最大金额", "0", new String[][] { { "", "0不限制" } });
		public GlobalParaDesc isEARNESTZT = new GlobalParaDesc("SALE", "定金交易是否允许非自提商品销售", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc salepayDisplayRate = new GlobalParaDesc("PAY", "付款界面列表框是否显示付款汇率", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isRealPrintPOP = new GlobalParaDesc("POP", "计算满减促销前不暂停边扫边打", "N", new String[][] { { "Y", "不暂停" }, { "N", "暂停边扫边打" } });
		public GlobalParaDesc withdrawauotbsmoney = new GlobalParaDesc("PAYIN", "自动填入应缴笔数金额的缴款付款代码", "", new String[][] { { "", "缴款代码=付款代码,付款代码|缴款代码=付款代码,付款代码" } });
		public GlobalParaDesc isForceRound = new GlobalParaDesc("GOODS", "商品数量乘单价后的成交价立即进行价格精度计算", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc lackpayfee = new GlobalParaDesc("PAY", "剩余付款金额小于本参数值则认为付款已足额", "0.01", null);
		public GlobalParaDesc oldqpaydet = new GlobalParaDesc("MZK", "退货交易退券付款是否检查原券溢余", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc vipzklimit = new GlobalParaDesc("CUSTOMER", "会员VIP折扣受限手工临时折门槛", "0", new String[][] { { "", "当手工折扣低于本门槛折扣率时不进行VIP折" } });
		public GlobalParaDesc printpopbill = new GlobalParaDesc("CCZZ", "卓展项目是否打印促销联", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc mername = new GlobalParaDesc("SFKS", "FOXTOWN银联商户名称", "", null);
		public GlobalParaDesc customerUnpayment = new GlobalParaDesc("HZJB", "解百项目参加会员打折不能付的付款方式", "", new String[][] { { "", "多个代码用,号分隔" } });
		public GlobalParaDesc noprintCashier = new GlobalParaDesc("HZJB", "解百项目哪些款机不打印小票", "", new String[][] { { "", "多个款机号用,号分隔" } });
		public GlobalParaDesc hdqrsCRM = new GlobalParaDesc("HZJB", "解百项目总部CRM的URL地址", "", null);
		public GlobalParaDesc slipPrinter_area = new GlobalParaDesc("SZXW", "西武项目平推打印的范围", "", new String[][] { { "", "消费初始位置,消费结束位置;退货初始位置,退货结束位置;签购单打印初始位置,签购单打印结束位置" } });
		public GlobalParaDesc iscrmtjprice = new GlobalParaDesc("BCRM", "百货CRM是否启用CRM促销特价", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc ispregetmsinfo = new GlobalParaDesc("BCRM", "百货CRM是否在交易前查询满赠信息", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc vipPromotionCrm = new GlobalParaDesc("BCRM", "百货CRM计算VIP折扣的方式", "2", new String[][] { { "1", "成交价=原有折扣价*会员折扣率" }, { "2", "起点折上折按会员类别上定义的折上折起点折扣率及折上折折扣率" } });
		public GlobalParaDesc vipPayExcp = new GlobalParaDesc("BCRM", "百货CRM计算VIP折扣是否除外受限付款", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc vipCalcType = new GlobalParaDesc("BCRM", "百货CRM计算VIP折上折计算方式", "1", new String[][] { { "1", "取会员类折上折和商品会员折的低价" }, { "2", "取商品会员折定义的折上折" } });
		public GlobalParaDesc ismj = new GlobalParaDesc("BCRM", "百货CRM存在除券付款是否参与满减", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc mjtype = new GlobalParaDesc("BCRM", "百货CRM满减规则定义", "N", new String[][] { { "N", "满XXX减XX" }, { "Y", "满XXX-YYY减XX" } });
		public GlobalParaDesc mjloop = new GlobalParaDesc("BCRM", "百货CRM是否启用多级满减", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc couponRuleType = new GlobalParaDesc("BCRM", "百货CRM电子券收券模式", "Y", new String[][] { { "Y", "商品金额用于计算收券金额" }, { "N", "商品剩余可收金额用于计算收券金额" } });
		public GlobalParaDesc printMode = new GlobalParaDesc("CCZZ", "小票打印顺序", "A", new String[][] { { "A", "先打机制单后打平推" }, { "B", "先打平推后打机制单" } });
		public GlobalParaDesc isbackoverpre = new GlobalParaDesc("BSTD", "促销模型是否采用后单压前单方式", "Y", new String[][] { { "Y", "采用后单压前单" }, { "N", "后单过期找前单" } });
		public GlobalParaDesc printYXQ = new GlobalParaDesc("BCRM", "百货CRM是否在小票上打印券有效期", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isShowMktWarm = new GlobalParaDesc("MARKET", "是否显示本地门店号和数据库门店不一致的警告", "Y", new String[][] { { "Y", "显示警告提示框" }, { "N", "不显示警告提示框" } });
		public GlobalParaDesc uploadOldInfo = new GlobalParaDesc("MARKET", "是否自动上传未上传的隔日小票", "0", new String[][] { { "0", "自动上传" }, { "1", "提示上传" }, { "2", "不自动上传" } });
		public GlobalParaDesc isPreSale = new GlobalParaDesc("MARKET", "是否启动预销售", "Y", new String[][] { { "Y", "启动" }, { "N", "不启动" } });
		public GlobalParaDesc usemzklog = new GlobalParaDesc("MZK", "是否启用储值卡交易日志", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc cancelBankGrant = new GlobalParaDesc("MZK", "退出付款或删除金卡工程时，是否允许用其他途径退出", "Y", new String[][] { { "Y", "是" }, { "N", "否" }, { "A", "需要授权" } });
		public GlobalParaDesc isVipMaxSlMsg = new GlobalParaDesc("CUSTOMER", "会员超出限量是否提示", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc isModifySaleFP = new GlobalParaDesc("SALE", "是否修改服务器数据库小票头中的发票信息", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
		public GlobalParaDesc elcScaleMode = new GlobalParaDesc("SALE", "电子称调用模式", "N", new String[][] { { "1", "只取一次重量" }, { "N", "否" }, { "2", "轮询查找重量" } });
		public GlobalParaDesc disableCmd = new GlobalParaDesc("OTHER", "禁用的通讯名利列表", "", new String[][] { { "", "多个命令用,号分隔" } });
		public GlobalParaDesc isHbGoods = new GlobalParaDesc("GOODS", "是否合并商品（扫码时）", "N", new String[][] { { "Y", "合并" }, { "N", "不合并" } });
		public GlobalParaDesc isGroupJSLB = new GlobalParaDesc("GOODS", "团购是否参与价随量变", "N", new String[][] { { "N", "不参与" }, { "N", "参与" } });
		// public GlobalParaDesc debugValid= new
		// GlobalParaDesc("MARKET","debug模式下是否效验IP地址","N",new
		// String[][]{{"Y","效验"},{"N","不效验"}});
		public GlobalParaDesc limitGH = new GlobalParaDesc("GRANT", "付款限额授权工号", "", new String[][] { { "", "多个工号用,号分隔" } });
		public GlobalParaDesc localNotCheckMultiGz = new GlobalParaDesc("GOODS", "本地查询不检查多柜组取第一行", "N", new String[][] { { "Y", "不检查" }, { "N", "检查" } });
		public GlobalParaDesc custConfig = new GlobalParaDesc("CUSTOMER", "是否启动会员配置信息", "Y", new String[][] { { "Y", "启用" }, { "N", "不启用" } });
		public GlobalParaDesc isSalePrecision = new GlobalParaDesc("OTHER", "销售精度控制标准", "0", new String[][] { { "", "0为不控制" } });
		public GlobalParaDesc limitDisRate = new GlobalParaDesc("OTHER", "超过该折扣后不参加组合促销", "0", new String[][] { { "", "0为不控制" } });
		public GlobalParaDesc isMustCustCoupon = new GlobalParaDesc("CUSTOMER", "买券时是否必须刷会员卡", "N", new String[][] { { "Y", "必须刷卡" }, { "N", "不控制" } });
		public GlobalParaDesc maxPriceInCardSale = new GlobalParaDesc("OTHER", "售卡时允许的最大商品价格", "9999999", new String[][] { { "", "9999999为不控制" } });
		public GlobalParaDesc isBackPaymentCover = new GlobalParaDesc("PAY", "退货非扣回付款时，是否覆盖相同帐号的付款方式", "Y", new String[][] { { "Y", "覆盖" }, { "N", "不覆盖" } });
		public GlobalParaDesc paymentFilter = new GlobalParaDesc("PAY", "付款配对", "", new String[][] { { "", "例子：0402:0401,0403|0402:0401,0403" } });
		public GlobalParaDesc isSuperMarketPop = new GlobalParaDesc("BCRM", "是否开启超市数量促销", "N", new String[][] { { "Y", "开启" }, { "N", "不开启" } });
		public GlobalParaDesc couponSaleType = new GlobalParaDesc("GOODS", "券销售类型", "A", new String[][] { { "A", "输入成交金额模式" }, { "N", " 输入券金额模式" } });
		public GlobalParaDesc printhgbill = new GlobalParaDesc("HP", "怀特项目是否打印换购联", "N", new String[][] { { "Y", "是" }, { "N", "否" } });
	};

	public void paraInitDefault()
	{
		GlobalParaDef.SysParaDef paradesc = this.new SysParaDef();

		Class classInst = this.getClass();
		Field[] flds = classInst.getDeclaredFields();

		Class descInst = paradesc.getClass();
		Field[] descflds = descInst.getDeclaredFields();

		for (int i = 0; i < flds.length; i++)
		{
			try
			{
				// 得到参数描述定义
				GlobalParaDesc para = null;
				int j = 0;
				for (; j < descflds.length; j++)
				{
					if (flds[i].getName().equalsIgnoreCase(descflds[j].getName()))
						break;
				}
				if (j < descflds.length)
				{
					para = (GlobalParaDesc) descflds[j].get(paradesc);
				}

				if (para != null)
				{
					String value = (para.valdef != null ? para.valdef.trim() : "");

					if (flds[i].getType().getName().equalsIgnoreCase("boolean"))
					{
						flds[i].setBoolean(this, (value != null && value.equals("Y")));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("int"))
					{
						flds[i].setInt(this, Convert.toInt(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("long"))
					{
						flds[i].setLong(this, Convert.toLong(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("double") || flds[i].getType().getName().equalsIgnoreCase("float"))
					{
						flds[i].setDouble(this, Convert.toDouble(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("char"))
					{
						if (value != null && value.length() > 0)
							flds[i].setChar(this, value.charAt(0));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("java.lang.String"))
					{
						flds[i].set(this, value);
					}

					// System.out.println(flds[i].getName() + " : " +
					// flds[i].get(this));
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}	
	}

	public void paraConvertByCode(String code, String value, String name)
	{
		// 得到CODE要对应的成员名,name表示为(#成员名 参数描述)
		if (name != null && name.trim().startsWith("#"))
		{
			int pos = name.indexOf(' ');
			if (pos > 0)
				code = name.substring(1, pos).trim();
		}
		else
		{
			if (!code.startsWith("#"))
				return;
			code = code.substring(1).trim();
		}

		Class classInst = this.getClass();
		Field[] flds = classInst.getDeclaredFields();
		for (int i = 0; i < flds.length; i++)
		{
			try
			{
				if (flds[i].getName().equalsIgnoreCase(code))
				{
					value = (value != null ? value.trim() : "");

					if (flds[i].getType().getName().equalsIgnoreCase("boolean"))
					{
						flds[i].setBoolean(this, (value != null && value.equals("Y")));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("int"))
					{
						flds[i].setInt(this, Convert.toInt(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("long"))
					{
						flds[i].setLong(this, Convert.toLong(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("double") || flds[i].getType().getName().equalsIgnoreCase("float"))
					{
						flds[i].setDouble(this, Convert.toDouble(value));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("char"))
					{
						if (value != null && value.length() > 0)
							flds[i].setChar(this, value.charAt(0));
					}
					else if (flds[i].getType().getName().equalsIgnoreCase("java.lang.String"))
					{
						flds[i].set(this, value);
					}

					// System.out.println(flds[i].getName() + " : " +
					// flds[i].get(this));

					break;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
