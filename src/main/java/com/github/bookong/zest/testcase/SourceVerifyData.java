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
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.Messages;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data used for verification.
 *
 * @author Jiang Xu
 */
public class SourceVerifyData extends AbstractSourceData {

    private boolean                    ignoreVerify;
    private Map<String, AbstractTable> tableMap = new LinkedHashMap<>();

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
    public SourceVerifyData(ZestWorker worker, ZestData zestData, String sourceId, Node node,
                            Map<String, String> tableEntityClassMap){
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs(Xml.IGNORE);

            this.ignoreVerify = xmlNode.getAttrBoolean(Xml.IGNORE, false);

            for (AbstractTable table : createTables(worker, zestData, sourceId, node, true, tableEntityClassMap)) {
                tableMap.put(table.getName(), table);
            }

        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceVerifyError(), e);
        }
    }

    /**
     * @return whether to ignore the verification of the result.
     */
    public boolean isIgnoreVerify() {
        return ignoreVerify;
    }

    /**
     * @return after the main logic of the test is executed, the data used for the test result.
     */
    public Map<String, AbstractTable> getTableMap() {
        return tableMap;
    }
}
