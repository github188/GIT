package com.royalstone.pos.card;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.GoodsList;
import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.core.PosSheet;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.gui.LoanCardConfirmMainCard;
import com.royalstone.pos.gui.LoanCardConfirmSubCard;
import com.royalstone.pos.gui.MSRInput;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Value;

/**
 * 挂账卡主过程，由业务方法调用。
 * 封装了包括从刷卡、查询、用户确认、支付等的动作
 * @author liangxinbiao
 */
public class LoanCardProcess {

	private SimpleDateFormat sdfDateTime =
		new SimpleDateFormat("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmssSSS");

	private String payTotal = "0";
	private String exceptionInfo;
	private String cardNo;
	private String cardBalance;

	private LoanCardPayVO payVO;
	private ILoanCard loanCard;
	private boolean isConfirm = false;

	public LoanCardProcess() {

		File dir = new File("autorever");
		if (!dir.exists())
			dir.mkdir();

	}

	/**
	 * 在一单开始时刷卡
	 * @return 卡号
	 */
	public LoanCardQueryVO readLoanCardNum() {
		MSRInput msrInput = new MSRInput("请刷卡:","loan");

		msrInput.show();

		try {
			while (!msrInput.isFinish())
				Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String inputCode = msrInput.getInputcode();

		if (!msrInput.isConfirm()) {
			isConfirm = false;
			return null;
		} else {
			isConfirm = true;
		}

		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "卡号错误,按清除键继续!";
			return null;
		}

		String cardNo = null;
		String secrety = null;

		String cardValue[] = inputCode.split("=");
		if (cardValue.length != 2) {
			cardNo = cardValue[0];
			secrety = "0";
		} else {
			cardNo = cardValue[0];
			secrety = cardValue[1];
		}

		loanCard = LoanCardFactory.createInstance();
		LoanCardQueryVO queryVO = loanCard.query(cardNo, secrety);
		if (queryVO != null && queryVO.getExceptioninfo() == null) {

			if (!checkValid(queryVO))
				return null;
		}
		if (exceptionInfo == null) {
			if (queryVO != null && queryVO.getExceptioninfo() != null) {
				exceptionInfo = queryVO.getExceptioninfo() + ",按清除键继续!";
			} else {
				if (queryVO == null)
					exceptionInfo = "网络故障,按清除键继续!";
			}
		}

		return queryVO;
	}

	/**
	 * 在一单开始时刷卡的情况下的挂账卡的查询和确认是否支付
	 * @param inputCode 卡号
	 * @return 是否确认支付
	 */
	public boolean reprocess(String inputCode) {

		if (pos.core.getValue().getValueToPay() < 0) {
			exceptionInfo = "挂账卡支付不能为负,按清除键继续!";
			return false;
		}

		String cardNo = null;
		String secrety = null;

		String cardValue[] = inputCode.split("=");
		if (cardValue.length != 2) {
			cardNo = cardValue[0];
			secrety = "0";
		} else {
			cardNo = cardValue[0];
			secrety = cardValue[1];
		}

		loanCard = LoanCardFactory.createInstance();
		LoanCardQueryVO queryVO = loanCard.query(cardNo, secrety);

		if (queryVO != null && queryVO.getExceptioninfo() == null) {

			if (!canconsume(queryVO))
				return false;

			if (!checkSheetMax(queryVO))
				return false;

			try {
				int iTenderAmount;
				if (pos.core.getValue().getValueToPay()
					> Integer.parseInt(queryVO.getDetail())) {
					iTenderAmount = Integer.parseInt(queryVO.getDetail());
				} else {
					iTenderAmount = pos.core.getValue().getValueToPay();
				}
				if (iTenderAmount < 0)
					iTenderAmount = 0;
				String tenderAmount = (new Value(iTenderAmount)).toValStr();

				if (pos.core.getValue().getValueToPay() > iTenderAmount) {
					exceptionInfo = "挂账卡可消费金额不足,按清除键继续!";
					return false;
				}

				boolean isConfirm = false;
				if (queryVO.getSubcardNo() != null
					&& !queryVO.getSubcardNo().equals("")) { //子卡
					LoanCardConfirmSubCard loanCardConfirm =
						new LoanCardConfirmSubCard();
					loanCardConfirm.setCardNo(cardNo);
					loanCardConfirm.setTenderAmount(tenderAmount);
					loanCardConfirm.setCardAmount(
						(new Value(Integer.parseInt(queryVO.getDetail())))
							.toString());
					String cardBalance =
						(new Value(Integer.parseInt(queryVO.getDetail())
							- iTenderAmount))
							.toString();
					loanCardConfirm.setAtferTenderAmount(cardBalance);
					loanCardConfirm.setCustName(queryVO.getCustName());
					loanCardConfirm.setCarID(queryVO.getCarID());

					String mainAftercardBalance =
						(new Value(Integer.parseInt(queryVO.getMainBalance())
							- iTenderAmount))
							.toString();

					loanCardConfirm.setMainCardBalance(
						(new Value(Integer.parseInt(queryVO.getMainBalance())))
							.toString());
					loanCardConfirm.setAfterMaincCardBalance(
						mainAftercardBalance);

					loanCardConfirm.show();
					isConfirm = loanCardConfirm.isConfirm();

					this.cardNo = queryVO.getSubcardNo();
					this.cardBalance = cardBalance;

				} else { //主卡
					LoanCardConfirmMainCard loanCardConfirm =
						new LoanCardConfirmMainCard();
					loanCardConfirm.setCardNo(cardNo);
					loanCardConfirm.setTenderAmount(tenderAmount);
					loanCardConfirm.setCardAmount(
						(new Value(Integer.parseInt(queryVO.getDetail())))
							.toString());
					String cardBalance =
						(new Value(Integer.parseInt(queryVO.getDetail())
							- iTenderAmount))
							.toString();
					loanCardConfirm.setAtferTenderAmount(cardBalance);
					loanCardConfirm.setCustName(queryVO.getCustName());
					if (queryVO.getCredit() != null
						&& !queryVO.getCredit().equals("")
						&& !queryVO.getCredit().equals("0")) {
						loanCardConfirm.setPercent(
							(
								new Value(
									(int) ((Double
										.parseDouble(queryVO.getDetail())
										/ Double.parseDouble(
											queryVO.getCredit()))
								* 10000)))
								.toString()
								+ "%");
					} else {
						loanCardConfirm.disablePercent();
					}

					loanCardConfirm.show();
					isConfirm = loanCardConfirm.isConfirm();

					this.cardNo = cardNo;
					this.cardBalance = cardBalance;
				}

				if (isConfirm) {

					PosContext context = PosContext.getInstance();

					payVO = new LoanCardPayVO();
					if (queryVO.getSubcardNo() != null
						&& !queryVO.getSubcardNo().trim().equals("")) {
						payVO.setCardno(queryVO.getCardNo());
						payVO.setSubcardno(cardNo);
					} else {
						payVO.setCardno(cardNo);
					}
					payVO.setCashierid(context.getCashierid());
					payVO.setCdseq("0");
					payVO.setPassword(secrety);
					payVO.setPayvalue(Integer.toString(iTenderAmount));
					payVO.setPosid(context.getPosid());
					payVO.setSheetid(Integer.toString(context.getSheetid()));
					payVO.setShopid(context.getStoreid());
					payVO.setTime(sdfTime.format(new Date()));

					payTotal =
						Integer.toString(
							(int) Math.rint(
								Double.parseDouble(tenderAmount) * 100));

				}

				return isConfirm;
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionInfo = "无效操作,按清除键继续!";
			}
		}
		if (exceptionInfo == null) {
			if (queryVO != null && queryVO.getExceptioninfo() != null) {
				exceptionInfo = queryVO.getExceptioninfo();
			} else {
				if (queryVO == null)
					exceptionInfo = "网络故障,按清除键继续!";
			}
		}
		return false;
	}

	/**
	 * 在支付时才刷卡的情况下的卡的查询和确认是否支付
	 * @return 是否确认支付
	 */
	public boolean process() {

		if (pos.core.getValue().getValueToPay() < 0) {
			exceptionInfo = "挂账卡支付不能为负,按清除键继续!";
			return false;
		}

		MSRInput msrInput = new MSRInput();
		msrInput.show();

		try {
			while (!msrInput.isFinish())
				Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String inputCode = msrInput.getInputcode();

		if (!msrInput.isConfirm())
			return false;

		if (inputCode == null && inputCode.equals("")) {
			exceptionInfo = "卡号错误,按清除键继续!";
			return false;
		}

		String cardNo = null;
		String secrety = null;

		String cardValue[] = inputCode.split("=");
		if (cardValue.length != 2) {
			cardNo = cardValue[0];
			secrety = "0";
		} else {
			cardNo = cardValue[0];
			secrety = cardValue[1];
		}

		loanCard = LoanCardFactory.createInstance();
		LoanCardQueryVO queryVO = loanCard.query(cardNo, secrety);

		if (queryVO != null && queryVO.getExceptioninfo() == null) {

			if (!checkValid(queryVO))
				return false;
			//yao 修改 05_03_07	
			if (!checkSheetMax(queryVO))
				return false;
            //-------------------------
			try {
				int iTenderAmount;
				if (pos.core.getValue().getValueToPay()
					> Integer.parseInt(queryVO.getDetail())) {
					iTenderAmount = Integer.parseInt(queryVO.getDetail());
				} else {
					iTenderAmount = pos.core.getValue().getValueToPay();
				}
				if (iTenderAmount < 0)
					iTenderAmount = 0;
				String tenderAmount = (new Value(iTenderAmount)).toValStr();

				boolean isConfirm = false;
				if (queryVO.getSubcardNo() != null
					&& !queryVO.getSubcardNo().equals("")) { //子卡
					LoanCardConfirmSubCard loanCardConfirm =
						new LoanCardConfirmSubCard();
					loanCardConfirm.setCardNo(cardNo);
					loanCardConfirm.setTenderAmount(tenderAmount);
					loanCardConfirm.setCardAmount(
						(new Value(Integer.parseInt(queryVO.getDetail())))
							.toString());
					String cardBalance =
						(new Value(Integer.parseInt(queryVO.getDetail())
							- iTenderAmount))
							.toString();
					loanCardConfirm.setAtferTenderAmount(cardBalance);
					loanCardConfirm.setCustName(queryVO.getCustName());
					loanCardConfirm.setCarID(queryVO.getCarID());

					String mainAftercardBalance =
						(new Value(Integer.parseInt(queryVO.getMainBalance())
							- iTenderAmount))
							.toString();

					loanCardConfirm.setMainCardBalance(
						(new Value(Integer.parseInt(queryVO.getMainBalance())))
							.toString());
					loanCardConfirm.setAfterMaincCardBalance(
						mainAftercardBalance);

					loanCardConfirm.show();
					isConfirm = loanCardConfirm.isConfirm();

					this.cardNo = queryVO.getSubcardNo();
					this.cardBalance = cardBalance;

				} else { //主卡
					LoanCardConfirmMainCard loanCardConfirm =
						new LoanCardConfirmMainCard();
					loanCardConfirm.setCardNo(cardNo);
					loanCardConfirm.setTenderAmount(tenderAmount);
					loanCardConfirm.setCardAmount(
						(new Value(Integer.parseInt(queryVO.getDetail())))
							.toString());
					String cardBalance =
						(new Value(Integer.parseInt(queryVO.getDetail())
							- iTenderAmount))
							.toString();
					loanCardConfirm.setAtferTenderAmount(cardBalance);
					loanCardConfirm.setCustName(queryVO.getCustName());
					if (queryVO.getCredit() != null
						&& !queryVO.getCredit().equals("")
						&& !queryVO.getCredit().equals("0")) {
						loanCardConfirm.setPercent(
							(
								new Value(
									(int) ((Double
										.parseDouble(queryVO.getDetail())
										/ Double.parseDouble(
											queryVO.getCredit()))
								* 10000)))
								.toString()
								+ "%");
					} else {
						loanCardConfirm.disablePercent();
					}

					loanCardConfirm.show();
					isConfirm = loanCardConfirm.isConfirm();

					this.cardNo = cardNo;
					this.cardBalance = cardBalance;
				}

				if (isConfirm) {

					PosContext context = PosContext.getInstance();

					payVO = new LoanCardPayVO();
					if (queryVO.getSubcardNo() != null
						&& !queryVO.getSubcardNo().trim().equals("")) {
						payVO.setCardno(queryVO.getCardNo());
						payVO.setSubcardno(cardNo);
					} else {
						payVO.setCardno(cardNo);
					}
					payVO.setCashierid(context.getCashierid());
					payVO.setCdseq("0");
					payVO.setPassword(secrety);
					payVO.setPayvalue(Integer.toString(iTenderAmount));
					payVO.setPosid(context.getPosid());
					payVO.setSheetid(Integer.toString(context.getSheetid()));
					payVO.setShopid(context.getStoreid());
					payVO.setTime(sdfTime.format(new Date()));

					payTotal =
						Integer.toString(
							(int) Math.rint(
								Double.parseDouble(tenderAmount) * 100));

				}

				return isConfirm;
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionInfo = "无效操作,按清除键继续!";
			}
		}
		if (exceptionInfo == null) {
			if (queryVO != null && queryVO.getExceptioninfo() != null) {
				exceptionInfo = queryVO.getExceptioninfo() + ",按清除键继续!";
			} else {
				if (queryVO == null)
					exceptionInfo = "网络故障,按清除键继续!";
			}
		}
		return false;
	}

	/**
	 * 挂账卡的支付，在支付前先写冲正文件，当支付成功时删除冲正文件
	 * @return 是否支付成功
	 */
	public boolean performPay() {
		synchronized (pos.Lock) {
			try {
				String filename =
					"autorever"
						+ File.separator
						+ sdfDateTime.format(new Date())
						+ ".xml";
				FileOutputStream fs = new FileOutputStream(filename);
				Document doc = new Document(payVO.toElement());
				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.output(doc, fs);
				fs.flush();
				fs.close();
				String result = loanCard.pay(payVO);
				if (result != null && result.equals("1")) {
					File file = new File(filename);
					file.delete();
					return true;
				} else {
					exceptionInfo = result + ",按清除键继续!";
					return false;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionInfo = "无效操作,按清除键继续!";
				return false;
			}
		}
	}

	/**
	 *
	 * @return 返回支付总额
	 */
	public String getPayTotal() {
		return payTotal;
	}

	/**
	 * @return 返回错误提示
	 */
	public String getExceptionInfo() {
		return exceptionInfo;
	}

	/**
	 * 测试挂账卡是否能消费，包括是否能在本店消费和是否含有不能用这张挂账卡消费的商品
	 * @param queryVO
	 * @return 是否合法
	 */
	public boolean checkValid(LoanCardQueryVO queryVO) {
		PosContext context = PosContext.getInstance();
		if (queryVO.getSubcardNo() != null
			&& !queryVO.getSubcardNo().trim().equals("")) { //子卡

			boolean found = false;
			ArrayList shopList = queryVO.getShopIDs();
			if (shopList != null) {
				for (int i = 0; i < shopList.size(); i++) {
					if (((String) shopList.get(i))
						.equals(context.getStoreid())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				exceptionInfo = "此卡不能在本店消费,按清除键继续!";
				return false;
			}

			ArrayList deptList = queryVO.getDeptIDs();
			if (deptList == null || !checkSheet(deptList)) {
				exceptionInfo = "这单含有不能被此卡消费的商品,按清除键继续!";
				return false;
			}

		} else { //主卡
			boolean found = false;
			ArrayList shopList = queryVO.getShopIDs();
			if (shopList != null) {
				for (int i = 0; i < shopList.size(); i++) {
					if (((String) shopList.get(i))
						.equals(context.getStoreid())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				exceptionInfo = "此卡不能在本店消费,按清除键继续!";
				return false;
			}
		}

		return true;
	}

	/**
	 * 测试挂账卡是否含有不能用这张挂账卡消费的商品
	 * @param queryVO
	 * @return
	 */
	public boolean canconsume(LoanCardQueryVO queryVO) {
		PosContext context = PosContext.getInstance();
		if (queryVO.getSubcardNo() != null
			&& !queryVO.getSubcardNo().trim().equals("")) {
			ArrayList deptList = queryVO.getDeptIDs();
			if (deptList == null || !checkSheet(deptList)) {
				exceptionInfo = "这单含有不能被此卡消费的商品,按清除键继续!";
				return false;
			}
		}
		return true;
	}

	/**
	 * 测试当前单所卖商品的小类时否在deptList内
	 * @param deptList
	 * @return
	 */
	private boolean checkSheet(ArrayList deptList) {
		DecimalFormat df = new DecimalFormat("000000");
		for (int i = 0; i < deptList.size(); i++) {
			deptList.set(
				i,
				df.format(Integer.parseInt((String) deptList.get(i))));
		}
		PosSheet sheet = pos.core.getPosSheet();
		for (int i = 0; i < sheet.getSaleLen(); i++) {
			Sale sale = sheet.getSale(i);
			int qty = sheet.getSoldQty(sale.getGoods());
		   //-------------------------------
		    String strDepid=sale.getDeptid();
			String strBigGroup="";
			String strMidGroup="";
		    if(strDepid!=null&&strDepid.length()==6){
		        strBigGroup=df.format(Integer.parseInt(strDepid.substring(0,2)));
		        strMidGroup=df.format(Integer.parseInt(strDepid.substring(1,4)));
		    }
		    if(deptList.contains("000000") && qty > 0)
		      break; 
		    
		    if(!deptList.contains(strBigGroup) && qty > 0)
			   if(!deptList.contains(strMidGroup) && qty > 0)	
		   //----------------------------------
			      if (!deptList.contains(sale.getDeptid()) && qty > 0)
				     return false;
		}
		return true;
	}

	/**
	 * 测试挂账是否超出消费限额
	 * @param queryVO
	 * @return
	 */
	private boolean checkSheetMax(LoanCardQueryVO queryVO) {
		try {
			DecimalFormat df = new DecimalFormat("000000");
			
		    /*TODO 药品限购*/
		    PosPriceData codenew = new PosPriceData();
		    codenew.setSaleCode(df.format(Integer.parseInt(queryVO.getOilGoodsID())));

			PosSheet sheet = pos.core.getPosSheet();
			GoodsList goodsList = pos.core.getGoodList();
			Goods oil =
				goodsList.find(
						codenew);
			int toPay = sheet.getValue().getValueToPay();
			int maxCanPay =
				(int) (oil.getPrice()
					* Double.parseDouble(queryVO.getMaxOilQtyPerTrans()));
					
			/*yao修改  05_03_07 原代码为 if (toPay > maxCanPay) 
			 * 
			 */
			//------- 
			int maxOil=(int)Double.parseDouble(queryVO.getMaxOilQtyPerTrans()); 
			if (maxOil!=0&&toPay > maxCanPay) {
			//------
				exceptionInfo = "此单超过消费限额,按清除键继续!";
				return false;
			}

		} catch (Exception ex) {
			exceptionInfo = "找不到参考油品,按清除键继续!";
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @return 卡号
	 */
	public String getCardNo() {
		return cardNo;
	}

	/**
	 *
	 * @return 挂账消费后的余额
	 */
	public String getCardBalance() {
		return cardBalance;
	}

	/**
	 * @return 用户是否刷卡
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

}
