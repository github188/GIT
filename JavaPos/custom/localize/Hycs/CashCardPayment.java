package custom.localize.Hycs;

/**
 * CashCardPayment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

public class CashCardPayment  implements java.io.Serializable {
    private int cardId;

    private double payMoney;

    public CashCardPayment() {
    }

    public CashCardPayment(
           int cardId,
           double payMoney) {
           this.cardId = cardId;
           this.payMoney = payMoney;
    }


    /**
     * Gets the cardId value for this CashCardPayment.
     * 
     * @return cardId
     */
    public int getCardId() {
        return cardId;
    }


    /**
     * Sets the cardId value for this CashCardPayment.
     * 
     * @param cardId
     */
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }


    /**
     * Gets the payMoney value for this CashCardPayment.
     * 
     * @return payMoney
     */
    public double getPayMoney() {
        return payMoney;
    }


    /**
     * Sets the payMoney value for this CashCardPayment.
     * 
     * @param payMoney
     */
    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CashCardPayment)) return false;
        CashCardPayment other = (CashCardPayment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cardId == other.getCardId() &&
            this.payMoney == other.getPayMoney();
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
        _hashCode += getCardId();
        _hashCode += new Double(getPayMoney()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CashCardPayment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "CashCardPayment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CardId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payMoney");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "PayMoney"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
