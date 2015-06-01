/**
 * PosWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Hycs;

public class PosWebServiceLocator extends org.apache.axis.client.Service implements PosWebService {

    public PosWebServiceLocator() {
    }


    public PosWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PosWebServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PosWebServiceSoap
    private java.lang.String PosWebServiceSoap_address = "http://10.2.0.105:88/PosWebService.asmx";

    public java.lang.String getPosWebServiceSoapAddress() {
        return PosWebServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PosWebServiceSoapWSDDServiceName = "PosWebServiceSoap";

    public java.lang.String getPosWebServiceSoapWSDDServiceName() {
        return PosWebServiceSoapWSDDServiceName;
    }

    public void setPosWebServiceSoapWSDDServiceName(java.lang.String name) {
        PosWebServiceSoapWSDDServiceName = name;
    }

    public PosWebServiceSoap getPosWebServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PosWebServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPosWebServiceSoap(endpoint);
    }

    public PosWebServiceSoap getPosWebServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            PosWebServiceSoapStub _stub = new PosWebServiceSoapStub(portAddress, this);
            _stub.setPortName(getPosWebServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPosWebServiceSoapEndpointAddress(java.lang.String address) {
        PosWebServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (PosWebServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                PosWebServiceSoapStub _stub = new PosWebServiceSoapStub(new java.net.URL(PosWebServiceSoap_address), this);
                _stub.setPortName(getPosWebServiceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PosWebServiceSoap".equals(inputPortName)) {
            return getPosWebServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "PosWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "PosWebServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PosWebServiceSoap".equals(portName)) {
            setPosWebServiceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
