package com.royalstone.pos.card;

import java.io.Serializable;

/**
 * ��ֵ����ѯֵ�����Ǵӷ��������صĴ�ֵ����Ϣ
 *
 * @author liangxinbiao
 */
public class SHCardQueryVO implements Serializable {

	private String memberid;
	private String exceptioninfo;
	private String detail;
	private String ifnewcard;
    private String cardNO;

    public String getCardNO() {
        return cardNO;
    }

    public void setCardNO(String cardNO) {
        this.cardNO = cardNO;
    }

	public SHCardQueryVO() {
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getMemberid() {
		return memberid;
	}
	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}
	public String getIfnewcard() {
		return ifnewcard;
	}
	public void setIfnewcard(String ifnewcard) {
		this.ifnewcard = ifnewcard;
	}
	public String getExceptioninfo() {
		return exceptioninfo;
	}
	public void setExceptioninfo(String exceptioninfo) {
		this.exceptioninfo = exceptioninfo;
	}

}
