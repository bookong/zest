package com.github.bookong.zest.testcase.param;

import com.github.bookong.zest.testcase.ZestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class Param extends ZestParam {

    private int                 intValue;
    private Integer             intObjValue;
    private long                longValue;
    private Long                longObjValue;
    private boolean             boolValue;
    private Boolean             boolObjValue;
    private double              doubleValue;
    private Double              doubleObjValue;
    private float               floatValue;
    private Float               floatObjValue;
    private String              strValue;
    private Date                date1;
    private Date                date2;
    private Date                date3;
    private Date                date4;
    private List<Param1>        listObj;
    private Param1              obj;
    private Map<String, String> nonsupportMapObj;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Integer getIntObjValue() {
        return intObjValue;
    }

    public void setIntObjValue(Integer intObjValue) {
        this.intObjValue = intObjValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public Long getLongObjValue() {
        return longObjValue;
    }

    public void setLongObjValue(Long longObjValue) {
        this.longObjValue = longObjValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Boolean getBoolObjValue() {
        return boolObjValue;
    }

    public void setBoolObjValue(Boolean boolObjValue) {
        this.boolObjValue = boolObjValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Double getDoubleObjValue() {
        return doubleObjValue;
    }

    public void setDoubleObjValue(Double doubleObjValue) {
        this.doubleObjValue = doubleObjValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public Float getFloatObjValue() {
        return floatObjValue;
    }

    public void setFloatObjValue(Float floatObjValue) {
        this.floatObjValue = floatObjValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public Date getDate3() {
        return date3;
    }

    public void setDate3(Date date3) {
        this.date3 = date3;
    }

    public Date getDate4() {
        return date4;
    }

    public void setDate4(Date date4) {
        this.date4 = date4;
    }

    public List<Param1> getListObj() {
        return listObj;
    }

    public void setListObj(List<Param1> listObj) {
        this.listObj = listObj;
    }

    public Param1 getObj() {
        return obj;
    }

    public void setObj(Param1 obj) {
        this.obj = obj;
    }

    public Map<String, String> getNonsupportMapObj() {
        return nonsupportMapObj;
    }

    public void setNonsupportMapObj(Map<String, String> nonsupportMapObj) {
        this.nonsupportMapObj = nonsupportMapObj;
    }
}
