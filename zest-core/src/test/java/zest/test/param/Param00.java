package zest.test.param;

import java.util.Date;

import com.github.bookong.zest.core.testcase.data.TestParam;

/**
 * @author jiangxu
 *
 */
public class Param00 implements TestParam {
	
	private Long param1;
	
	private String param2;
	
	private int param3;
	
	private Long param5;
	
	private Double param6;
	
	private Float param7;
	
	private Date param8;
	
	private long param9;
	
	private double param10;
	
	private float param11;
	
	private boolean param12;
	
	private FooAssertParamPart part = new FooAssertParamPart();
	
	class FooAssertParamPart {
		private Long param1;
		
		@Override
		public String toString() {
			return "FooAssertParamPart [param1=" + param1 + "]";
		}
	}

	@Override
	public String toString() {
		return "Param00 [param1=" + param1 + ", param2=" + param2 + ", param3=" + param3 + ", param5=" + param5
				+ ", param6=" + param6 + ", param7=" + param7 + ", param8=" + param8 + ", param9=" + param9
				+ ", param10=" + param10 + ", param11=" + param11 + ", param12=" + param12 + ", part=" + part + "]";
	}
}

