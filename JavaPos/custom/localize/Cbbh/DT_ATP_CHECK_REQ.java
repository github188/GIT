/**
 * DT_ATP_CHECK_REQ.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package custom.localize.Cbbh;

public class DT_ATP_CHECK_REQ  implements java.io.Serializable {
    /* 门店 */
    private java.lang.String PLANT;

    /* 商品编码 */
    private java.lang.String MATERIAL;

    /* 计量单位 */
    private java.lang.String UNIT;

    /* 库存地点 */
    private java.lang.String STGE_LOC;

    public DT_ATP_CHECK_REQ() {
    }

    public DT_ATP_CHECK_REQ(
           java.lang.String PLANT,
           java.lang.String MATERIAL,
           java.lang.String UNIT,
           java.lang.String STGE_LOC) {
           this.PLANT = PLANT;
           this.MATERIAL = MATERIAL;
           this.UNIT = UNIT;
           this.STGE_LOC = STGE_LOC;
    }


    /**
     * Gets the PLANT value for this DT_ATP_CHECK_REQ.
     * 
     * @return PLANT   * 门店
     */
    public java.lang.String getPLANT() {
        return PLANT;
    }


    /**
     * Sets the PLANT value for this DT_ATP_CHECK_REQ.
     * 
     * @param PLANT   * 门店
     */
    public void setPLANT(java.lang.String PLANT) {
        this.PLANT = PLANT;
    }


    /**
     * Gets the MATERIAL value for this DT_ATP_CHECK_REQ.
     * 
     * @return MATERIAL   * 商品编码
     */
    public java.lang.String getMATERIAL() {
        return MATERIAL;
    }


    /**
     * Sets the MATERIAL value for this DT_ATP_CHECK_REQ.
     * 
     * @param MATERIAL   * 商品编码
     */
    public void setMATERIAL(java.lang.String MATERIAL) {
        this.MATERIAL = MATERIAL;
    }


    /**
     * Gets the UNIT value for this DT_ATP_CHECK_REQ.
     * 
     * @return UNIT   * 计量单位
     */
    public java.lang.String getUNIT() {
        return UNIT;
    }


    /**
     * Sets the UNIT value for this DT_ATP_CHECK_REQ.
     * 
     * @param UNIT   * 计量单位
     */
    public void setUNIT(java.lang.String UNIT) {
        this.UNIT = UNIT;
    }


    /**
     * Gets the STGE_LOC value for this DT_ATP_CHECK_REQ.
     * 
     * @return STGE_LOC   * 库存地点
     */
    public java.lang.String getSTGE_LOC() {
        return STGE_LOC;
    }


    /**
     * Sets the STGE_LOC value for this DT_ATP_CHECK_REQ.
     * 
     * @param STGE_LOC   * 库存地点
     */
    public void setSTGE_LOC(java.lang.String STGE_LOC) {
        this.STGE_LOC = STGE_LOC;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_ATP_CHECK_REQ)) return false;
        DT_ATP_CHECK_REQ other = (DT_ATP_CHECK_REQ) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.PLANT==null && other.getPLANT()==null) || 
             (this.PLANT!=null &&
              this.PLANT.equals(other.getPLANT()))) &&
            ((this.MATERIAL==null && other.getMATERIAL()==null) || 
             (this.MATERIAL!=null &&
              this.MATERIAL.equals(other.getMATERIAL()))) &&
            ((this.UNIT==null && other.getUNIT()==null) || 
             (this.UNIT!=null &&
              this.UNIT.equals(other.getUNIT()))) &&
            ((this.STGE_LOC==null && other.getSTGE_LOC()==null) || 
             (this.STGE_LOC!=null &&
              this.STGE_LOC.equals(other.getSTGE_LOC())));
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
        if (getPLANT() != null) {
            _hashCode += getPLANT().hashCode();
        }
        if (getMATERIAL() != null) {
            _hashCode += getMATERIAL().hashCode();
        }
        if (getUNIT() != null) {
            _hashCode += getUNIT().hashCode();
        }
        if (getSTGE_LOC() != null) {
            _hashCode += getSTGE_LOC().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_ATP_CHECK_REQ.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:cb:efut:atp_check_in", "DT_ATP_CHECK_REQ"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PLANT");
        elemField.setXmlName(new javax.xml.namespace.QName("", "PLANT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MATERIAL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MATERIAL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("UNIT");
        elemField.setXmlName(new javax.xml.namespace.QName("", "UNIT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("STGE_LOC");
        elemField.setXmlName(new javax.xml.namespace.QName("", "STGE_LOC"));
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
