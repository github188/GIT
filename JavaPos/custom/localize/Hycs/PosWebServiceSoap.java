/**
 * PosWebServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Hycs;

public interface PosWebServiceSoap extends java.rmi.Remote {
    public void getVipCard(int condType, java.lang.String condValue, java.lang.String cardCodeToCheck, java.lang.String verifyCode, java.lang.String storeCode, javax.xml.rpc.holders.BooleanHolder getVipCardResult, javax.xml.rpc.holders.StringHolder msg, VipCardHolder vipCard) throws java.rmi.RemoteException;
    public void updateVipInfo(VipInfoUpdated vipInfo, javax.xml.rpc.holders.BooleanHolder updateVipInfoResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException;
    public void updateVipCent(int vipId, double updateCent, int updateType, java.lang.String storeCode, java.lang.String posId, int billId, javax.xml.rpc.holders.BooleanHolder updateVipCentResult,javax.xml.rpc.holders.DoubleHolder validCent, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException;
    public void getCashCard(int condType, java.lang.String condValue, java.lang.String cardCodeToCheck, java.lang.String verifyCode, java.lang.String password, java.lang.String storeCode, javax.xml.rpc.holders.BooleanHolder getCashCardResult, javax.xml.rpc.holders.StringHolder msg, CashCardHolder cashCard) throws java.rmi.RemoteException;
    public void prepareTransCashCardPayment2(java.lang.String storeCode, java.lang.String posId, int billId, java.lang.String cashier, java.lang.String accountDate, CashCardPayment[] payments, javax.xml.rpc.holders.BooleanHolder prepareTransCashCardPayment2Result, javax.xml.rpc.holders.StringHolder msg, javax.xml.rpc.holders.IntHolder transId) throws java.rmi.RemoteException;
    public void confirmTransCashCardPayment(int transId, int serverBillId, double transMoney, javax.xml.rpc.holders.BooleanHolder confirmTransCashCardPaymentResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException;
    public void cancelTransCashCardPayment(int transId, int serverBillId, double transMoney, javax.xml.rpc.holders.BooleanHolder cancelTransCashCardPaymentResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException;
}
