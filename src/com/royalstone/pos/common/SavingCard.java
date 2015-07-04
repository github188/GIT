
package com.royalstone.pos.common;

import java.io.Serializable;
import java.util.Date;


public class SavingCard implements Serializable
{
	private String cardNO;
    private String type; //通过CardType的flag查找出来的
    private String secrety;
    private String detail;
    private Date lastUseDate;
    private String lastShopID;
    private String lastPosID;
    private String lastCashierID;
    private String payMoney;
    private String times;
    private String endDate;
    private String mode;
    private String ifNewCard;

    public SavingCard() {
    }
    public SavingCard(String cardNO, String type, String secrety, String detail, String endDate, String mode, String ifNewCard) {
        this.cardNO = cardNO;
        this.type = type;
        this.secrety = secrety;
        this.detail = detail;
        this.endDate = endDate;
        this.mode = mode;
        this.ifNewCard = ifNewCard;
    }

    public String getCardNO() {
        return cardNO;
    }

    public void setCardNO(String cardNO) {
        this.cardNO = cardNO;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecrety() {
        return secrety;
    }

    public void setSecrety(String secrety) {
        this.secrety = secrety;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getLastUseDate() {
        return lastUseDate;
    }

    public void setLastUseDate(Date lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public String getLastShopID() {
        return lastShopID;
    }

    public void setLastShopID(String lastShopID) {
        this.lastShopID = lastShopID;
    }

    public String getLastPosID() {
        return lastPosID;
    }

    public void setLastPosID(String lastPosID) {
        this.lastPosID = lastPosID;
    }

    public String getLastCashierID() {
        return lastCashierID;
    }

    public void setLastCashierID(String lastCashierID) {
        this.lastCashierID = lastCashierID;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getIfNewCard() {
        return ifNewCard;
    }

    public void setIfNewCard(String ifNewCard) {
        this.ifNewCard = ifNewCard;
    }
}
