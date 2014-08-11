package zest.param;

import java.util.Date;

import com.github.bookong.zest.core.testcase.data.TestParam;

/**
 * @author jiangxu
 *
 */
public class Param implements TestParam {
	private long baseLong;
	private Long classLong;
	private Long nullLong = 77L;
	private int baseInt;
	private Integer classInt;
	private Integer nullInt = 88;
	private boolean baseBool;
	private Boolean classBool;
	private Boolean nullBool = Boolean.TRUE;
	private double baseDouble;
	private Double classDouble;
	private Double nullDouble = 99.0;
	private float baseFloat;
	private Float classFloat;
	private Float nullFloat = 99.0F;
	private String str;
	private String nullStr = "Not null str";
	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;
	private Date dateNull = new Date();
	
	private ParamSub1 paramSub1;
	
	public ParamSub1 getParamSub1() {
		return paramSub1;
	}
	public long getBaseLong() {
		return baseLong;
	}
	public Long getClassLong() {
		return classLong;
	}
	public Long getNullLong() {
		return nullLong;
	}
	public int getBaseInt() {
		return baseInt;
	}
	public Integer getClassInt() {
		return classInt;
	}
	public Integer getNullInt() {
		return nullInt;
	}
	public boolean isBaseBool() {
		return baseBool;
	}
	public Boolean getClassBool() {
		return classBool;
	}
	public Boolean getNullBool() {
		return nullBool;
	}
	public double getBaseDouble() {
		return baseDouble;
	}
	public Double getClassDouble() {
		return classDouble;
	}
	public Double getNullDouble() {
		return nullDouble;
	}
	public float getBaseFloat() {
		return baseFloat;
	}
	public Float getClassFloat() {
		return classFloat;
	}
	public Float getNullFloat() {
		return nullFloat;
	}
	public String getStr() {
		return str;
	}
	public String getNullStr() {
		return nullStr;
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
	public Date getDateNull() {
		return dateNull;
	}
}
