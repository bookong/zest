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
 * @author Jiang Xu
 */
public class Source {

    private String           id;

    private SourceInitData   initData;

    private SourceVerifyData verifyData;

    public Source(ZestWorker worker, Node node, Set<String> sourceIds){
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
            this.initData = new SourceInitData(worker, getId(), children.get(0), tableEntityClassMap);
            this.verifyData = new SourceVerifyData(worker, getId(), children.get(1), tableEntityClassMap);

        } catch (Exception e) {
            throw new ZestException(Messages.parseSourceError(getId()), e);
        }
    }

    public String getId() {
        return id;
    }

    public SourceInitData getInitData() {
        return initData;
    }

    public SourceVerifyData getVerifyData() {
        return verifyData;
    }
}
