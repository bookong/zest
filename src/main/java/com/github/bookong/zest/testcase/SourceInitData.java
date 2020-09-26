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

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Data used for initialization.
 *
 * @author Jiang Xu
 */
public class SourceInitData extends AbstractSourceData {

    private List<AbstractTable> tableList = new ArrayList<>();

    /**
     * Construct a new instance.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param sourceId
     *          Data source id.
     * @param node
     *          XML element.
     * @param tableEntityClassMap
     *          Contains the <em>EntityClass</em> attributes bound on the  &lt;Table&gt; element in XML.
     */
    public SourceInitData(ZestWorker worker, ZestData zestData, String sourceId, Node node,
                          Map<String, String> tableEntityClassMap){
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();

            tableList.addAll(createTables(worker, zestData, sourceId, node, false, tableEntityClassMap));
        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceInitError(), e);
        }
    }

    /**
     * @return the data used for initialization before the main logic is executed.
     */
    public List<AbstractTable> getTableList() {
        return tableList;
    }
}
