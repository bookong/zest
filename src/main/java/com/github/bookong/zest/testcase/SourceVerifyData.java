package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class SourceVerifyData extends AbstractSourceData {

    private boolean                    ignoreCheck;

    private boolean                    onlyCheckCoreData;

    /** 执行完，检测数据源的数据 */
    private Map<String, AbstractTable> verifyDataMap = new LinkedHashMap<>();

    public SourceVerifyData(ZestWorker worker, String sourceId, String nodeName, Node node){
        try {
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);
            this.ignoreCheck = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, Xml.IGNORE, false);
            this.onlyCheckCoreData = ZestXmlUtil.removeBooleanAttr(nodeName, attrMap, Xml.ONLY_CORE_DATA, false);

            for (AbstractTable table : createTables(worker, sourceId, nodeName, node, true)) {
                verifyDataMap.put(table.getName(), table);
            }

            ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceVerifyError(), e);
        }
    }

    public boolean isIgnoreCheck() {
        return ignoreCheck;
    }

    public boolean isOnlyCheckCoreData() {
        return onlyCheckCoreData;
    }

    public Map<String, AbstractTable> getVerifyDataMap() {
        return verifyDataMap;
    }
}
