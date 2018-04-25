package zest.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangxu
 */
public class Param1 extends Param0 {

    private long                 longBase        = 1L;
    private Long                 longClass       = null;
    private Long                 longNeedNull    = 3L;

    private int                  intBase         = 4;
    private Integer              intClass        = null;
    private Integer              intNeedNull     = 6;

    private boolean              booleanBase     = false;
    private Boolean              booleanClass    = null;
    private Boolean              booleanNeedNull = Boolean.TRUE;

    private double               doubleBase      = 10D;
    private Double               doubleClass     = null;
    private Double               doubleNeedNull  = 12D;

    private float                floatBase       = 13F;
    private Float                floatClass      = null;
    private Float                floatNeedNull   = 15F;

    private String               str             = null;
    private String               strNeedNull     = "Not null str";

    private Date                 date1           = null;
    private Date                 date2           = null;
    private Date                 date3           = null;
    private Date                 date4           = null;
    private Date                 date5           = null;
    private Date                 dateNeedNull    = new Date();

    private List<Long>           longList        = new ArrayList<>();
    private List<Integer>        intList         = new ArrayList<>();
    private List<Boolean>        booleanList     = new ArrayList<>();
    private List<Double>         doubleList      = new ArrayList<>();
    private List<Float>          floatList       = new ArrayList<>();
    private List<String>         strList         = new ArrayList<>();
    private List<Date>           dateList        = new ArrayList<>();

    private Map<String, Long>    longMap         = new HashMap<>();
    private Map<String, Integer> intMap          = new HashMap<>();
    private Map<String, Boolean> booleanMap      = new HashMap<>();
    private Map<String, Double>  doubleMap       = new HashMap<>();
    private Map<String, Float>   floatMap        = new HashMap<>();
    private Map<String, String>  strMap          = new HashMap<>();
    private Map<String, Date>    dateMap         = new HashMap<>();

    private Param2               obj             = new Param2("existed obj");

    public long getLongBase() {
        return longBase;
    }

    public Long getLongClass() {
        return longClass;
    }

    public Long getLongNeedNull() {
        return longNeedNull;
    }

    public int getIntBase() {
        return intBase;
    }

    public Integer getIntClass() {
        return intClass;
    }

    public Integer getIntNeedNull() {
        return intNeedNull;
    }

    public boolean isBooleanBase() {
        return booleanBase;
    }

    public Boolean getBooleanClass() {
        return booleanClass;
    }

    public Boolean getBooleanNeedNull() {
        return booleanNeedNull;
    }

    public double getDoubleBase() {
        return doubleBase;
    }

    public Double getDoubleClass() {
        return doubleClass;
    }

    public Double getDoubleNeedNull() {
        return doubleNeedNull;
    }

    public float getFloatBase() {
        return floatBase;
    }

    public Float getFloatClass() {
        return floatClass;
    }

    public Float getFloatNeedNull() {
        return floatNeedNull;
    }

    public String getStr() {
        return str;
    }

    public String getStrNeedNull() {
        return strNeedNull;
    }

    public Date getDate1() {
        return date1;
    }

    public Date getDate2() {
        return date2;
    }

    public Date getDate3() {
        return date3;
    }

    public Date getDate4() {
        return date4;
    }

    public Date getDate5() {
        return date5;
    }

    public Date getDateNeedNull() {
        return dateNeedNull;
    }

    public List<Long> getLongList() {
        return longList;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public List<Boolean> getBooleanList() {
        return booleanList;
    }

    public List<Double> getDoubleList() {
        return doubleList;
    }

    public List<Float> getFloatList() {
        return floatList;
    }

    public List<String> getStrList() {
        return strList;
    }

    public List<Date> getDateList() {
        return dateList;
    }

    public Map<String, Long> getLongMap() {
        return longMap;
    }

    public Map<String, Integer> getIntMap() {
        return intMap;
    }

    public Map<String, Boolean> getBooleanMap() {
        return booleanMap;
    }

    public Map<String, Double> getDoubleMap() {
        return doubleMap;
    }

    public Map<String, Float> getFloatMap() {
        return floatMap;
    }

    public Map<String, String> getStrMap() {
        return strMap;
    }

    public Map<String, Date> getDateMap() {
        return dateMap;
    }

    public Param2 getObj() {
        return obj;
    }

}
