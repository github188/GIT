/**
 * SI_ATP_CHECK_OUT_SYNServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Cbbh;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import com.efuture.javaPos.Global.GlobalInfo;

public class SI_ATP_CHECK_OUT_SYNServiceLocator extends org.apache.axis.client.Service implements custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNService {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SI_ATP_CHECK_OUT_SYNServiceLocator() {
    }


    public SI_ATP_CHECK_OUT_SYNServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SI_ATP_CHECK_OUT_SYNServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for HTTPS_Port
    private java.lang.String HTTPS_Port_address = "https://192.1.33.41:50001/XISOAPAdapter/MessageServlet?senderParty=&senderService=EFUT&receiverParty=&receiverService=&interface=SI_ATP_CHECK_OUT_SYN&interfaceNamespace=urn%3Acb%3Aefut%3Aatp_check_in";

    public java.lang.String getHTTPS_PortAddress() {
        return HTTPS_Port_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String HTTPS_PortWSDDServiceName = "HTTPS_Port";

    public java.lang.String getHTTPS_PortWSDDServiceName() {
        return HTTPS_PortWSDDServiceName;
    }

    public void setHTTPS_PortWSDDServiceName(java.lang.String name) {
        HTTPS_PortWSDDServiceName = name;
    }

    public custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN getHTTPS_Port() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(HTTPS_Port_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getHTTPS_Port(endpoint);
    }

    public custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN getHTTPS_Port(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub _stub = new custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub(portAddress, this);
            _stub.setPortName(getHTTPS_PortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setHTTPS_PortEndpointAddress(java.lang.String address) {
        HTTPS_Port_address = address;
    }


    // Use to get a proxy class for HTTP_Port
    private java.lang.String HTTP_Port_address = "http://192.1.33.41:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=EFUT&receiverParty=&receiverService=&interface=SI_ATP_CHECK_OUT_SYN&interfaceNamespace=urn%3Acb%3Aefut%3Aatp_check_in";

    public java.lang.String getHTTP_PortAddress() {
        return HTTP_Port_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String HTTP_PortWSDDServiceName = "HTTP_Port";

    public java.lang.String getHTTP_PortWSDDServiceName() {
        return HTTP_PortWSDDServiceName;
    }

    public void setHTTP_PortWSDDServiceName(java.lang.String name) {
        HTTP_PortWSDDServiceName = name;
    }

    public custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN getHTTP_Port() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(HTTP_Port_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getHTTP_Port(endpoint);
    }

    public custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN getHTTP_Port(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub _stub = new custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub(portAddress, this);
            _stub.setPortName(getHTTP_PortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setHTTP_PortEndpointAddress(java.lang.String address) {
        HTTP_Port_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN.class.isAssignableFrom(serviceEndpointInterface)) {
                custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub _stub = new custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub(new java.net.URL(HTTPS_Port_address), this);
                _stub.setPortName(getHTTPS_PortWSDDServiceName());
                return _stub;
            }
            if (custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYN.class.isAssignableFrom(serviceEndpointInterface)) {
                custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub _stub = new custom.localize.Cbbh.SI_ATP_CHECK_OUT_SYNBindingStub(new java.net.URL(HTTP_Port_address), this);
                _stub.setPortName(getHTTP_PortWSDDServiceName());
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
        if ("HTTPS_Port".equals(inputPortName)) {
            return getHTTPS_Port();
        }
        else if ("HTTP_Port".equals(inputPortName)) {
            return getHTTP_Port();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:cb:efut:atp_check_in", "SI_ATP_CHECK_OUT_SYNService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:cb:efut:atp_check_in", "HTTPS_Port"));
            ports.add(new javax.xml.namespace.QName("urn:cb:efut:atp_check_in", "HTTP_Port"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("HTTPS_Port".equals(portName)) {
            setHTTPS_PortEndpointAddress(address);
        }
        else 
if ("HTTP_Port".equals(portName)) {
            setHTTP_PortEndpointAddress(address);
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
    
	public static void main(String[] args)
	{
		SI_ATP_CHECK_OUT_SYNServiceLocator Service = new SI_ATP_CHECK_OUT_SYNServiceLocator();
		SI_ATP_CHECK_OUT_SYNBindingStub Call = null;
		DT_ATP_CHECK_REQ request = new DT_ATP_CHECK_REQ();
		request.setMATERIAL("84000003002"); //code
		request.setPLANT("3002");//mkt
		request.setUNIT("EA");//unit
		request.setSTGE_LOC("0001");//库存地点
		DT_ATP_CHECK_RESPWMDVEX[] response = null;
		try {
			Call = (SI_ATP_CHECK_OUT_SYNBindingStub)Service.getHTTP_Port();
			Call.setUsername("EFUT_USER");
			Call.setPassword("123456");
			response = Call.SI_ATP_CHECK_OUT_SYN(request);
			if(response != null && response.length > 0)
			{
				String date = null;
				date = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
				for(DT_ATP_CHECK_RESPWMDVEX item : response)
					if(item.getCOM_DATE().equals(date))
						System.out.println(Double.valueOf(item.getCOM_QTY()).doubleValue());
			}

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
