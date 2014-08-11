package zest.param;

import java.util.List;
import java.util.Map;

public class ParamSub1 {
	private String str;
	private List<ParamSub1Sub1> subObjList;
	private Map<String, ParamSub1Sub1> subObjMap;
	private Map<Long, ParamSub1Sub1> subObjMapKeyLong;
	private Map<Integer, ParamSub1Sub1> subObjMapKeyIntger;
	private Map<Double, ParamSub1Sub1> subObjMapKeyDouble;
	private Map<Float, ParamSub1Sub1> subObjMapKeyFloat;

	public String getStr() {
		return str;
	}

	public List<ParamSub1Sub1> getSubObjList() {
		return subObjList;
	}

	public Map<String, ParamSub1Sub1> getSubObjMap() {
		return subObjMap;
	}

	public Map<Long, ParamSub1Sub1> getSubObjMapKeyLong() {
		return subObjMapKeyLong;
	}

	public Map<Integer, ParamSub1Sub1> getSubObjMapKeyIntger() {
		return subObjMapKeyIntger;
	}

	public Map<Double, ParamSub1Sub1> getSubObjMapKeyDouble() {
		return subObjMapKeyDouble;
	}

	public Map<Float, ParamSub1Sub1> getSubObjMapKeyFloat() {
		return subObjMapKeyFloat;
	}

}
