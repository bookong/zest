package zest.test.param;

import java.util.Date;

import com.github.bookong.zest.core.testcase.data.TestParam;

/**
 * @author jiangxu
 *
 */
public class Param001 implements TestParam {
	
	public Long param1;
	
	public String param2;
	
	public int param3;
	
	public Long param5;
	
	public Double param6;
	
	public Float param7;
	
	public Date param8;
	
	public long param9;
	
	public double param10;
	
	public float param11;
	
	public FooAssertParamPart part = new FooAssertParamPart();
	
	public class FooAssertParamPart {
		public Long param1;
		
		@Override
		public String toString() {
			return "FooAssertParamPart [param1=" + param1 + "]";
		}
	}

	@Override
	public String toString() {
		return "Param001 [param1=" + param1 + ", param2=" + param2 + ", param3=" + param3 + ", param5=" + param5
				+ ", param6=" + param6 + ", param7=" + param7 + ", param8=" + param8 + ", param9=" + param9
				+ ", param10=" + param10 + ", param11=" + param11 + ", part=" + part + "]";
	}
}

