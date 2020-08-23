package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class SourceInitData extends AbstractSourceData {

    /** 执行前，初始化数据源用的数据 */
    private List<AbstractTable> initDataList = new ArrayList<>();

    public SourceInitData(ZestWorker worker, String sourceId, String nodeName, Node initNode){
        try {
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(initNode);
            initDataList.addAll(createTables(worker, sourceId, nodeName, initNode, false));
            ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceInitError(), e);
        }
    }

    public List<AbstractTable> getInitDataList() {
        return initDataList;
    }
}
