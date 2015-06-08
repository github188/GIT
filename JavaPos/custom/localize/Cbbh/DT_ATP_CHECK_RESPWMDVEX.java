/**
 * DT_ATP_CHECK_RESPWMDVEX.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Cbbh;

public class DT_ATP_CHECK_RESPWMDVEX  implements java.io.Serializable {
    /* 承诺日期 */
    private java.lang.String COM_DATE;

    /* 承诺的数量 */
    private java.lang.String COM_QTY;

    public DT_ATP_CHECK_RESPWMDVEX() {
    }

    public DT_ATP_CHECK_RESPWMDVEX(
           java.lang.String COM_DATE,
           java.lang.String COM_QTY) {
           this.COM_DATE = COM_DATE;
           this.COM_QTY = COM_QTY;
    }


    /**
     * Gets the COM_DATE value for this DT_ATP_CHECK_RESPWMDVEX.
     * 
     * @return COM_DATE   * 承诺日期
     */
    public java.lang.String getCOM_DATE() {
        return COM_DATE;
    }


    /**
     * Sets the COM_DATE value for this DT_ATP_CHECK_RESPWMDVEX.
     * 
     * @param COM_DATE   * 承诺日期
     */
    public void setCOM_DATE(java.lang.String COM_DATE) {
        this.COM_DATE = COM_DATE;
    }


    /**
     * Gets the COM_QTY value for this DT_ATP_CHECK_RESPWMDVEX.
     * 
     * @return COM_QTY   * 承诺的数量
     */
    public java.lang.String getCOM_QTY() {
        return COM_QTY;
    }


    /**
     * Sets the COM_QTY value for this DT_ATP_CHECK_RESPWMDVEX.
     * 
     * @param COM_QTY   * 承诺的数量
     */
    public void setCOM_QTY(java.lang.String COM_QTY) {
        this.COM_QTY = COM_QTY;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_ATP_CHECK_RESPWMDVEX)) return false;
        DT_ATP_CHECK_RESPWMDVEX other = (DT_ATP_CHECK_RESPWMDVEX) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.COM_DATE==null && other.getCOM_DATE()==null) || 
             (this.COM_DATE!=null &&
              this.COM_DATE.equals(other.getCOM_DATE()))) &&
            ((this.COM_QTY==null && other.getCOM_QTY()==null) || 
             (this.COM_QTY!=null &&
              this.COM_QTY.equals(other.getCOM_QTY())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCOM_DATE() != null) {
            _hashCode += getCOM_DATE().hashCode();
        }
        if (getCOM_QTY() != null) {
            _hashCode += getCOM_QTY().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_ATP_CHECK_RESPWMDVEX.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:cb:efut:atp_check_in", ">DT_ATP_CHECK_RESP>WMDVEX"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("COM_DATE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "COM_DATE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("COM_QTY");
        elemField.setXmlName(new javax.xml.namespace.QName("", "COM_QTY"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
