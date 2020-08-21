//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-27 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.08.21 at 03:35:16 下午 CST 
//


package com.github.bookong.zest.support.xml.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{https://www.bookong.net/schema/zest/data}SqlTable" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{https://www.bookong.net/schema/zest/data}MongoCollection" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{https://www.bookong.net/schema/zest/data}RedisData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sqlTable",
    "mongoCollection",
    "redisData"
})
@XmlRootElement(name = "Init")
public class Init {

    @XmlElement(name = "SqlTable")
    protected List<SqlTable> sqlTable;
    @XmlElement(name = "MongoCollection")
    protected List<MongoCollection> mongoCollection;
    @XmlElement(name = "RedisData")
    protected List<RedisData> redisData;

    /**
     * Gets the value of the sqlTable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sqlTable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSqlTable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SqlTable }
     * 
     * 
     */
    public List<SqlTable> getSqlTable() {
        if (sqlTable == null) {
            sqlTable = new ArrayList<SqlTable>();
        }
        return this.sqlTable;
    }

    /**
     * Gets the value of the mongoCollection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mongoCollection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMongoCollection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MongoCollection }
     * 
     * 
     */
    public List<MongoCollection> getMongoCollection() {
        if (mongoCollection == null) {
            mongoCollection = new ArrayList<MongoCollection>();
        }
        return this.mongoCollection;
    }

    /**
     * Gets the value of the redisData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the redisData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRedisData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RedisData }
     * 
     * 
     */
    public List<RedisData> getRedisData() {
        if (redisData == null) {
            redisData = new ArrayList<RedisData>();
        }
        return this.redisData;
    }

}
