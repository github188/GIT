/**
 * VipCard.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Hycs;

public class VipCard  implements java.io.Serializable {
    private int cardId;

    private java.lang.String cardCode;

    private java.lang.String vipName;

    private int cardTypeId;

    private java.lang.String cardTypeName;

    private boolean canCent;

    private boolean canOwnCoupon;

    private boolean canDisc;

    private double validCent;

    private double yearCent;

    private double stageCent;

    private java.lang.String hello;

    private java.lang.String idCardCode;

    private java.lang.String birthday;

    private int sexType;

    private java.lang.String mobile;

    private java.lang.String address;

    private java.lang.String EMail;

    public VipCard() {
    }

    public VipCard(
           int cardId,
           java.lang.String cardCode,
           java.lang.String vipName,
           int cardTypeId,
           java.lang.String cardTypeName,
           boolean canCent,
           boolean canOwnCoupon,
           boolean canDisc,
           double validCent,
           double yearCent,
           double stageCent,
           java.lang.String hello,
           java.lang.String idCardCode,
           java.lang.String birthday,
           int sexType,
           java.lang.String mobile,
           java.lang.String address,
           java.lang.String EMail) {
           this.cardId = cardId;
           this.cardCode = cardCode;
           this.vipName = vipName;
           this.cardTypeId = cardTypeId;
           this.cardTypeName = cardTypeName;
           this.canCent = canCent;
           this.canOwnCoupon = canOwnCoupon;
           this.canDisc = canDisc;
           this.validCent = validCent;
           this.yearCent = yearCent;
           this.stageCent = stageCent;
           this.hello = hello;
           this.idCardCode = idCardCode;
           this.birthday = birthday;
           this.sexType = sexType;
           this.mobile = mobile;
           this.address = address;
           this.EMail = EMail;
    }


    /**
     * Gets the cardId value for this VipCard.
     * 
     * @return cardId
     */
    public int getCardId() {
        return cardId;
    }


    /**
     * Sets the cardId value for this VipCard.
     * 
     * @param cardId
     */
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }


    /**
     * Gets the cardCode value for this VipCard.
     * 
     * @return cardCode
     */
    public java.lang.String getCardCode() {
        return cardCode;
    }


    /**
     * Sets the cardCode value for this VipCard.
     * 
     * @param cardCode
     */
    public void setCardCode(java.lang.String cardCode) {
        this.cardCode = cardCode;
    }


    /**
     * Gets the vipName value for this VipCard.
     * 
     * @return vipName
     */
    public java.lang.String getVipName() {
        return vipName;
    }


    /**
     * Sets the vipName value for this VipCard.
     * 
     * @param vipName
     */
    public void setVipName(java.lang.String vipName) {
        this.vipName = vipName;
    }


    /**
     * Gets the cardTypeId value for this VipCard.
     * 
     * @return cardTypeId
     */
    public int getCardTypeId() {
        return cardTypeId;
    }


    /**
     * Sets the cardTypeId value for this VipCard.
     * 
     * @param cardTypeId
     */
    public void setCardTypeId(int cardTypeId) {
        this.cardTypeId = cardTypeId;
    }


    /**
     * Gets the cardTypeName value for this VipCard.
     * 
     * @return cardTypeName
     */
    public java.lang.String getCardTypeName() {
        return cardTypeName;
    }


    /**
     * Sets the cardTypeName value for this VipCard.
     * 
     * @param cardTypeName
     */
    public void setCardTypeName(java.lang.String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }


    /**
     * Gets the canCent value for this VipCard.
     * 
     * @return canCent
     */
    public boolean isCanCent() {
        return canCent;
    }


    /**
     * Sets the canCent value for this VipCard.
     * 
     * @param canCent
     */
    public void setCanCent(boolean canCent) {
        this.canCent = canCent;
    }


    /**
     * Gets the canOwnCoupon value for this VipCard.
     * 
     * @return canOwnCoupon
     */
    public boolean isCanOwnCoupon() {
        return canOwnCoupon;
    }


    /**
     * Sets the canOwnCoupon value for this VipCard.
     * 
     * @param canOwnCoupon
     */
    public void setCanOwnCoupon(boolean canOwnCoupon) {
        this.canOwnCoupon = canOwnCoupon;
    }


    /**
     * Gets the canDisc value for this VipCard.
     * 
     * @return canDisc
     */
    public boolean isCanDisc() {
        return canDisc;
    }


    /**
     * Sets the canDisc value for this VipCard.
     * 
     * @param canDisc
     */
    public void setCanDisc(boolean canDisc) {
        this.canDisc = canDisc;
    }


    /**
     * Gets the validCent value for this VipCard.
     * 
     * @return validCent
     */
    public double getValidCent() {
        return validCent;
    }


    /**
     * Sets the validCent value for this VipCard.
     * 
     * @param validCent
     */
    public void setValidCent(double validCent) {
        this.validCent = validCent;
    }


    /**
     * Gets the yearCent value for this VipCard.
     * 
     * @return yearCent
     */
    public double getYearCent() {
        return yearCent;
    }


    /**
     * Sets the yearCent value for this VipCard.
     * 
     * @param yearCent
     */
    public void setYearCent(double yearCent) {
        this.yearCent = yearCent;
    }


    /**
     * Gets the stageCent value for this VipCard.
     * 
     * @return stageCent
     */
    public double getStageCent() {
        return stageCent;
    }


    /**
     * Sets the stageCent value for this VipCard.
     * 
     * @param stageCent
     */
    public void setStageCent(double stageCent) {
        this.stageCent = stageCent;
    }


    /**
     * Gets the hello value for this VipCard.
     * 
     * @return hello
     */
    public java.lang.String getHello() {
        return hello;
    }


    /**
     * Sets the hello value for this VipCard.
     * 
     * @param hello
     */
    public void setHello(java.lang.String hello) {
        this.hello = hello;
    }


    /**
     * Gets the idCardCode value for this VipCard.
     * 
     * @return idCardCode
     */
    public java.lang.String getIdCardCode() {
        return idCardCode;
    }


    /**
     * Sets the idCardCode value for this VipCard.
     * 
     * @param idCardCode
     */
    public void setIdCardCode(java.lang.String idCardCode) {
        this.idCardCode = idCardCode;
    }


    /**
     * Gets the birthday value for this VipCard.
     * 
     * @return birthday
     */
    public java.lang.String getBirthday() {
        return birthday;
    }


    /**
     * Sets the birthday value for this VipCard.
     * 
     * @param birthday
     */
    public void setBirthday(java.lang.String birthday) {
        this.birthday = birthday;
    }


    /**
     * Gets the sexType value for this VipCard.
     * 
     * @return sexType
     */
    public int getSexType() {
        return sexType;
    }


    /**
     * Sets the sexType value for this VipCard.
     * 
     * @param sexType
     */
    public void setSexType(int sexType) {
        this.sexType = sexType;
    }


    /**
     * Gets the mobile value for this VipCard.
     * 
     * @return mobile
     */
    public java.lang.String getMobile() {
        return mobile;
    }


    /**
     * Sets the mobile value for this VipCard.
     * 
     * @param mobile
     */
    public void setMobile(java.lang.String mobile) {
        this.mobile = mobile;
    }


    /**
     * Gets the address value for this VipCard.
     * 
     * @return address
     */
    public java.lang.String getAddress() {
        return address;
    }


    /**
     * Sets the address value for this VipCard.
     * 
     * @param address
     */
    public void setAddress(java.lang.String address) {
        this.address = address;
    }


    /**
     * Gets the EMail value for this VipCard.
     * 
     * @return EMail
     */
    public java.lang.String getEMail() {
        return EMail;
    }


    /**
     * Sets the EMail value for this VipCard.
     * 
     * @param EMail
     */
    public void setEMail(java.lang.String EMail) {
        this.EMail = EMail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VipCard)) return false;
        VipCard other = (VipCard) obj;
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
            ((this.vipName==null && other.getVipName()==null) || 
             (this.vipName!=null &&
              this.vipName.equals(other.getVipName()))) &&
            this.cardTypeId == other.getCardTypeId() &&
            ((this.cardTypeName==null && other.getCardTypeName()==null) || 
             (this.cardTypeName!=null &&
              this.cardTypeName.equals(other.getCardTypeName()))) &&
            this.canCent == other.isCanCent() &&
            this.canOwnCoupon == other.isCanOwnCoupon() &&
            this.canDisc == other.isCanDisc() &&
            this.validCent == other.getValidCent() &&
            this.yearCent == other.getYearCent() &&
            this.stageCent == other.getStageCent() &&
            ((this.hello==null && other.getHello()==null) || 
             (this.hello!=null &&
              this.hello.equals(other.getHello()))) &&
            ((this.idCardCode==null && other.getIdCardCode()==null) || 
             (this.idCardCode!=null &&
              this.idCardCode.equals(other.getIdCardCode()))) &&
            ((this.birthday==null && other.getBirthday()==null) || 
             (this.birthday!=null &&
              this.birthday.equals(other.getBirthday()))) &&
            this.sexType == other.getSexType() &&
            ((this.mobile==null && other.getMobile()==null) || 
             (this.mobile!=null &&
              this.mobile.equals(other.getMobile()))) &&
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.EMail==null && other.getEMail()==null) || 
             (this.EMail!=null &&
              this.EMail.equals(other.getEMail())));
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
        if (getVipName() != null) {
            _hashCode += getVipName().hashCode();
        }
        _hashCode += getCardTypeId();
        if (getCardTypeName() != null) {
            _hashCode += getCardTypeName().hashCode();
        }
        _hashCode += (isCanCent() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCanOwnCoupon() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCanDisc() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Double(getValidCent()).hashCode();
        _hashCode += new Double(getYearCent()).hashCode();
        _hashCode += new Double(getStageCent()).hashCode();
        if (getHello() != null) {
            _hashCode += getHello().hashCode();
        }
        if (getIdCardCode() != null) {
            _hashCode += getIdCardCode().hashCode();
        }
        if (getBirthday() != null) {
            _hashCode += getBirthday().hashCode();
        }
        _hashCode += getSexType();
        if (getMobile() != null) {
            _hashCode += getMobile().hashCode();
        }
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getEMail() != null) {
            _hashCode += getEMail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VipCard.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "VipCard"));
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
        elemField.setFieldName("vipName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "VipName"));
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
        elemField.setFieldName("cardTypeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CardTypeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CanCent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canOwnCoupon");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CanOwnCoupon"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canDisc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CanDisc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validCent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "ValidCent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("yearCent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "YearCent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stageCent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "StageCent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hello");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Hello"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idCardCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "IdCardCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("birthday");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Birthday"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sexType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "SexType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Mobile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "EMail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
