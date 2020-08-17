package zest.param;

import java.util.ArrayList;
import java.util.List;

import com.github.bookong.zest.testcase.ZestParam;

/**
 * @author jiangxu
 */
public class Param3 implements ZestParam {

    private List<Param0> list = new ArrayList<>();

    public List<Param0> getList() {
        return list;
    }

    public void setList(List<Param0> list) {
        this.list = list;
    }
}
