package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.util.*;

/**
 * @author Jiang Xu
 */
public class SourceInitData extends AbstractSourceData {

    /** 执行前，初始化数据源用的数据 */
    private List<AbstractTable> tableList = new ArrayList<>();

    public SourceInitData(ZestWorker worker, String sourceId, Node node, Map<String, String> tableEntityClassMap){
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();

            tableList.addAll(createTables(worker, sourceId, node, false, tableEntityClassMap));
        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceInitError(), e);
        }
    }

    public List<AbstractTable> getTableList() {
        return tableList;
    }
}
