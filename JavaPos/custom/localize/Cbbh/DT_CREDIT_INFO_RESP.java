/**
 * DT_CREDIT_INFO_RESP.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Cbbh;

public class DT_CREDIT_INFO_RESP  implements java.io.Serializable {
    /* 金额 */
    private java.lang.String SKFOR;

    public DT_CREDIT_INFO_RESP() {
    }

    public DT_CREDIT_INFO_RESP(
           java.lang.String SKFOR) {
           this.SKFOR = SKFOR;
    }


    /**
     * Gets the SKFOR value for this DT_CREDIT_INFO_RESP.
     * 
     * @return SKFOR   * 金额
     */
    public java.lang.String getSKFOR() {
        return SKFOR;
    }


    /**
     * Sets the SKFOR value for this DT_CREDIT_INFO_RESP.
     * 
     * @param SKFOR   * 金额
     */
    public void setSKFOR(java.lang.String SKFOR) {
        this.SKFOR = SKFOR;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_CREDIT_INFO_RESP)) return false;
        DT_CREDIT_INFO_RESP other = (DT_CREDIT_INFO_RESP) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.SKFOR==null && other.getSKFOR()==null) || 
             (this.SKFOR!=null &&
              this.SKFOR.equals(other.getSKFOR())));
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
        if (getSKFOR() != null) {
            _hashCode += getSKFOR().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_CREDIT_INFO_RESP.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:cb:efut:credit_limit_info_in", "DT_CREDIT_INFO_RESP"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SKFOR");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SKFOR"));
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
