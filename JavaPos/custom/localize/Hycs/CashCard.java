package custom.localize.Hycs;

/**
 * CashCard.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

public class CashCard  implements java.io.Serializable {
    private int cardId;

    private java.lang.String cardCode;

    private int cardTypeId;

    private double balance;

    public CashCard() {
    }

    public CashCard(
           int cardId,
           java.lang.String cardCode,
           int cardTypeId,
           double balance) {
           this.cardId = cardId;
           this.cardCode = cardCode;
           this.cardTypeId = cardTypeId;
           this.balance = balance;
    }


    /**
     * Gets the cardId value for this CashCard.
     * 
     * @return cardId
     */
    public int getCardId() {
        return cardId;
    }


    /**
     * Sets the cardId value for this CashCard.
     * 
     * @param cardId
     */
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }


    /**
     * Gets the cardCode value for this CashCard.
     * 
     * @return cardCode
     */
    public java.lang.String getCardCode() {
        return cardCode;
    }


    /**
     * Sets the cardCode value for this CashCard.
     * 
     * @param cardCode
     */
    public void setCardCode(java.lang.String cardCode) {
        this.cardCode = cardCode;
    }


    /**
     * Gets the cardTypeId value for this CashCard.
     * 
     * @return cardTypeId
     */
    public int getCardTypeId() {
        return cardTypeId;
    }


    /**
     * Sets the cardTypeId value for this CashCard.
     * 
     * @param cardTypeId
     */
    public void setCardTypeId(int cardTypeId) {
        this.cardTypeId = cardTypeId;
    }


    /**
     * Gets the balance value for this CashCard.
     * 
     * @return balance
     */
    public double getBalance() {
        return balance;
    }


    /**
     * Sets the balance value for this CashCard.
     * 
     * @param balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CashCard)) return false;
        CashCard other = (CashCard) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cardId == other.getCardId() &&
            ((this.cardCode==null && other.getCardCode()==null) || 
             (this.cardCode!=null &&
              this.cardCode.equals(other.getCardCode()))) &&
            this.cardTypeId == other.getCardTypeId() &&
            this.balance == other.getBalance();
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
        if (getCardCode() != null) {
            _hashCode += getCardCode().hashCode();
        }
        _hashCode += getCardTypeId();
        _hashCode += new Double(getBalance()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CashCard.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "CashCard"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CardId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CardCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardTypeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CardTypeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Balance"));
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

