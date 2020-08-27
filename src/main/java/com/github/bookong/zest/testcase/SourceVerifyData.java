package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class SourceVerifyData extends AbstractSourceData {

    private boolean                    ignoreCheck;

    /** 执行完，检测数据源的数据 */
    private Map<String, AbstractTable> verifyDataMap = new LinkedHashMap<>();

    public SourceVerifyData(ZestWorker worker, String sourceId, Node node){
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs(Xml.IGNORE);

            this.ignoreCheck = xmlNode.getAttrBoolean(Xml.IGNORE, false);

            for (AbstractTable table : createTables(worker, sourceId, node, true)) {
                verifyDataMap.put(table.getName(), table);
            }

        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceVerifyError(), e);
        }
    }

    public boolean isIgnoreCheck() {
        return ignoreCheck;
    }

    public Map<String, AbstractTable> getVerifyDataMap() {
        return verifyDataMap;
    }
}
