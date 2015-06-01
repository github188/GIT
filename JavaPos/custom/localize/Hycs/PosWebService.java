package custom.localize.Hycs;

/**
 * PosWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

public interface PosWebService extends javax.xml.rpc.Service {
    public java.lang.String getPosWebServiceSoapAddress();

    public PosWebServiceSoap getPosWebServiceSoap() throws javax.xml.rpc.ServiceException;

    public PosWebServiceSoap getPosWebServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}