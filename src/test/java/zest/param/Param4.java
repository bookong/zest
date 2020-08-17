package zest.param;

import java.util.HashMap;
import java.util.Map;

import com.github.bookong.zest.testcase.ZestParam;

/**
 * @author jiangxu
 */
public class Param4 implements ZestParam {

    private Map<String, Param0> map = new HashMap<>();

    public Map<String, Param0> getMap() {
        return map;
    }

}
