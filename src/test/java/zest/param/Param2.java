package zest.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.bookong.zest.core.testcase.ZestTestParam;

public class Param2 implements ZestTestParam {

    private String               str         = null;

    private List<Long>           longList    = new ArrayList<>();
    private List<Integer>        intList     = new ArrayList<>();
    private List<Boolean>        booleanList = new ArrayList<>();
    private List<Double>         doubleList  = new ArrayList<>();
    private List<Float>          floatList   = new ArrayList<>();
    private List<String>         strList     = new ArrayList<>();
    private List<Date>           dateList    = new ArrayList<>();

    private Map<String, Long>    longMap     = new HashMap<>();
    private Map<String, Integer> intMap      = new HashMap<>();
    private Map<String, Boolean> booleanMap  = new HashMap<>();
    private Map<String, Double>  doubleMap   = new HashMap<>();
    private Map<String, Float>   floatMap    = new HashMap<>();
    private Map<String, String>  strMap      = new HashMap<>();
    private Map<String, Date>    dateMap     = new HashMap<>();

    public Param2(){
    }

    public Param2(String str){
        this.str = str;
    }

    public String getStr() {
        return str;
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

}
