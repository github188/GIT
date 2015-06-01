package custom.localize.Hycs;

public class PosWebServiceSoapProxy implements PosWebServiceSoap {
  private String _endpoint = null;
  private PosWebServiceSoap posWebServiceSoap = null;
  
  public PosWebServiceSoapProxy() {
    _initPosWebServiceSoapProxy();
  }
  
  public PosWebServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initPosWebServiceSoapProxy();
  }
  
  private void _initPosWebServiceSoapProxy() {
    try {
      posWebServiceSoap = (new PosWebServiceLocator()).getPosWebServiceSoap();
      if (posWebServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)posWebServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)posWebServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (posWebServiceSoap != null)
      ((javax.xml.rpc.Stub)posWebServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public PosWebServiceSoap getPosWebServiceSoap() {
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    return posWebServiceSoap;
  }
  
  public void getVipCard(int condType, java.lang.String condValue, java.lang.String cardCodeToCheck, java.lang.String verifyCode, java.lang.String storeCode,javax.xml.rpc.holders.BooleanHolder getVipCardResult, javax.xml.rpc.holders.StringHolder msg, VipCardHolder vipCard) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.getVipCard(condType, condValue, cardCodeToCheck, verifyCode, storeCode, getVipCardResult, msg, vipCard);
  }
  
  public void updateVipInfo(VipInfoUpdated vipInfo, javax.xml.rpc.holders.BooleanHolder updateVipInfoResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.updateVipInfo(vipInfo, updateVipInfoResult, msg);
  }
  
  public void updateVipCent(int vipId, double updateCent, int updateType, java.lang.String storeCode, java.lang.String posId, int billId, javax.xml.rpc.holders.BooleanHolder updateVipCentResult,javax.xml.rpc.holders.DoubleHolder validCent, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.updateVipCent(vipId, updateCent, updateType, storeCode, posId, billId, updateVipCentResult ,validCent , msg);
  }
  
  public void getCashCard(int condType, java.lang.String condValue, java.lang.String cardCodeToCheck, java.lang.String verifyCode, java.lang.String password, java.lang.String storeCode, javax.xml.rpc.holders.BooleanHolder getCashCardResult, javax.xml.rpc.holders.StringHolder msg, CashCardHolder cashCard) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.getCashCard(condType, condValue, cardCodeToCheck, verifyCode, password, storeCode, getCashCardResult, msg, cashCard);
  }
  
  public void prepareTransCashCardPayment2(java.lang.String storeCode, java.lang.String posId, int billId, java.lang.String cashier, java.lang.String accountDate, CashCardPayment[] payments, javax.xml.rpc.holders.BooleanHolder prepareTransCashCardPayment2Result, javax.xml.rpc.holders.StringHolder msg, javax.xml.rpc.holders.IntHolder transId) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.prepareTransCashCardPayment2(storeCode, posId, billId, cashier, accountDate, payments, prepareTransCashCardPayment2Result, msg, transId);
  }
  
  public void confirmTransCashCardPayment(int transId, int serverBillId, double transMoney, javax.xml.rpc.holders.BooleanHolder confirmTransCashCardPaymentResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.confirmTransCashCardPayment(transId, serverBillId, transMoney, confirmTransCashCardPaymentResult, msg);
  }
  
  public void cancelTransCashCardPayment(int transId, int serverBillId, double transMoney, javax.xml.rpc.holders.BooleanHolder cancelTransCashCardPaymentResult, javax.xml.rpc.holders.StringHolder msg) throws java.rmi.RemoteException{
    if (posWebServiceSoap == null)
      _initPosWebServiceSoapProxy();
    posWebServiceSoap.cancelTransCashCardPayment(transId, serverBillId, transMoney, cancelTransCashCardPaymentResult, msg);
  }
  
  
}