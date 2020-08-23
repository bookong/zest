package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.mongo.Collection;
import com.github.bookong.zest.testcase.sql.Table;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.springframework.data.mongodb.core.MongoOperations;
import org.w3c.dom.Node;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public abstract class AbstractSourceData {

    protected List<AbstractTable> createTables(ZestWorker worker, String sourceId, String nodeName, Node node, boolean isTargetData) {
        List<Node> elements = ZestXmlUtil.getElements(node.getChildNodes());
        Object operation = worker.getSourceOperation(sourceId);
        if (operation == null) {
            throw new ZestException(Messages.parseSourceOperationNone());
        }

        List<AbstractTable> list = new ArrayList<>(elements.size() + 1);

        if (operation instanceof Connection) {
            for (Node item : elements) {
                if (!"Table".equals(item.getNodeName())) {
                    throw new ZestException(Messages.parseSourceOperationMatch(Connection.class.getName(), nodeName, "Table"));
                }
                list.add(new Table(worker, sourceId, item.getNodeName(), item, (Connection) operation, isTargetData));
            }
        } else if (operation instanceof MongoOperations) {
            for (Node item : elements) {
                if (!"Collection".equals(item.getNodeName())) {
                    throw new ZestException(Messages.parseSourceOperationMatch(MongoOperations.class.getName(), nodeName, "Collection"));
                }
                list.add(new Collection(worker, sourceId, item.getNodeName(), item, (MongoOperations) operation, isTargetData));
            }
        } else {
            throw new ZestException(Messages.parseSourceOperationUnknown(operation.getClass().getName()));
        }

        return list;
    }
}
