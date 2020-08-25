package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

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

    public Source(ZestWorker worker, String nodeName, Node sourceNode, Set<String> sourceIds){
        try {
            List<Node> children = ZestXmlUtil.getElements(sourceNode.getChildNodes());
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(sourceNode);
            this.id = ZestXmlUtil.removeNotEmptyAttr(nodeName, attrMap, Xml.ID);
            ZestXmlUtil.duplicateCheck(Xml.ID, sourceIds, getId());

            if (children.size() != 2 //
                || !Xml.INIT.equals(children.get(0).getNodeName()) //
                || !Xml.VERIFY.equals(children.get(1).getNodeName())) {
                throw new ZestException(Messages.parseSourceNecessary());
            }

            this.initData = new SourceInitData(worker, getId(), children.get(0).getNodeName(), children.get(0));
            this.verifyData = new SourceVerifyData(worker, getId(), children.get(1).getNodeName(), children.get(1));

            ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
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
