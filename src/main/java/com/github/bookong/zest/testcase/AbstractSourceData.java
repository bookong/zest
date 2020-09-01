package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Xu
 */
public abstract class AbstractSourceData {

    protected List<AbstractTable> createTables(ZestWorker worker, String sourceId, Node node, boolean isVerifyElement) {
        XmlNode xmlNode = new XmlNode(node);
        String nodeName = node.getNodeName();
        AbstractExecutor executor = worker.getExecutor(sourceId);
        executor.checkSupportedOperatorClass(worker, sourceId);

        List<Node> children = xmlNode.getFixedNodeList(Messages.parseSourceOperationMatch(Connection.class.getName(),
                                                                                          nodeName, Xml.TABLE),
                                                       Xml.TABLE);

        List<AbstractTable> list = new ArrayList<>();
        for (Node item : children) {
            list.add(executor.createTable(worker, sourceId, item, isVerifyElement));
        }

        return list;
    }
}
