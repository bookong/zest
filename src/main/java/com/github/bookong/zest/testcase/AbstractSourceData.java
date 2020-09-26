/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.executor.AbstractExecutor;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code SourceInitData} and {@code SourceVerifyData} abstract base class.
 *
 * @author Jiang Xu
 */
public abstract class AbstractSourceData {

    /**
     * Create subordinate table data.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param sourceId
     *          Data source id.
     * @param node
     *          XML element.
     * @param isVerifyElement
     *          Is it under {@code SourceVerifyData}.
     * @param tableEntityClassMap
     *          Contains the <em>EntityClass</em> attributes bound on the  &lt;Table&gt; element in XML.
     * @return table data.
     */
    protected List<AbstractTable> createTables(ZestWorker worker, ZestData zestData, String sourceId, Node node,
                                               boolean isVerifyElement, Map<String, String> tableEntityClassMap) {
        XmlNode xmlNode = new XmlNode(node);
        String nodeName = node.getNodeName();
        AbstractExecutor executor = worker.getExecutor(sourceId);
        executor.checkSupportedOperatorClass(worker, sourceId);

        List<Node> children = xmlNode.getFixedNodeList(Messages.parseSourceOperationMatch(executor.supportedOperatorClass().getName(),
                                                                                          nodeName, Xml.TABLE),
                                                       Xml.TABLE);

        List<AbstractTable> list = new ArrayList<>();
        for (Node item : children) {
            AbstractTable table = executor.createTable();
            table.init(worker, zestData, sourceId, item, isVerifyElement, tableEntityClassMap);
            list.add(table);
        }

        return list;
    }
}
