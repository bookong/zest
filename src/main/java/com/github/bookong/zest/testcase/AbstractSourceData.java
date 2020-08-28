package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import org.springframework.data.mongodb.core.MongoOperations;
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

        Object operation = worker.getOperator(sourceId);
        List<AbstractTable> list = new ArrayList<>();

        if (operation instanceof Connection) {
            List<Node> children = xmlNode.getFixedNodeList(Messages.parseSourceOperationMatch(Connection.class.getName(),
                                                                                              nodeName, Xml.TABLE),
                                                           Xml.TABLE);

            for (Node item : children) {
                list.add(new Table(worker, sourceId, item, (Connection) operation, isVerifyElement));
            }

        } else if (operation instanceof MongoOperations) {
            List<Node> children = xmlNode.getFixedNodeList(Messages.parseSourceOperationMatch(MongoOperations.class.getName(),
                                                                                              nodeName, Xml.COLLECTION),
                                                           Xml.COLLECTION);

            for (Node item : children) {
                list.add(new Collection(worker, sourceId, item, isVerifyElement));
            }
        } else {
            throw new ZestException(Messages.parseSourceOperationUnknown(operation.getClass().getName()));
        }

        return list;
    }
}
