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
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The data source object, on the one hand, it corresponds to the &lt;Source&gt; part of the test case data, on the
 * other hand, the code needs to correspond to the operator bound by the @ZestSource annotation.
 * 
 * @author Jiang Xu
 */
public class Source {

    private String           id;
    private SourceInitData   initData;
    private SourceVerifyData verifyData;

    /**
     * Construct a new instance.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     * @param zestData
     *          An object containing unit test case data.
     * @param node
     *          XML element.
     * @param sourceIds
     *          The parsed XML element &lt;Source&gt; {@code Id}, used to determine whether it is duplicate.
     */
    public Source(ZestWorker worker, ZestData zestData, Node node, Set<String> sourceIds){
        try {
            XmlNode xmlNode = new XmlNode(node);
            this.id = xmlNode.getAttr(Xml.ID);
            if (StringUtils.isBlank(getId())) {
                throw new ZestException(Messages.parseCommonAttrEmpty(Xml.ID));
            }

            xmlNode.checkSupportedAttrs(Xml.ID);
            XmlNode.duplicateCheck(Xml.ID, sourceIds, getId());

            List<Node> children = xmlNode.getSpecifiedNodes(Messages.parseSourceNecessary(), Xml.INIT, Xml.VERIFY);

            Map<String, String> tableEntityClassMap = new HashMap<>();
            this.initData = new SourceInitData(worker, zestData, getId(), children.get(0), tableEntityClassMap);
            this.verifyData = new SourceVerifyData(worker, zestData, getId(), children.get(1), tableEntityClassMap);

        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceError(getId()), e);
        }
    }

    /**
     * @return data source id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return data used for initialization.
     */
    public SourceInitData getInitData() {
        return initData;
    }

    /**
     * @return data used for verification.
     */
    public SourceVerifyData getVerifyData() {
        return verifyData;
    }
}
