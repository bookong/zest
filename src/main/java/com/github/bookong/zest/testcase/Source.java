package com.github.bookong.zest.testcase;

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
        List<Node> elements = ZestXmlUtil.getElements(sourceNode.getChildNodes());
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(sourceNode);

        this.id = ZestXmlUtil.removeAttr(nodeName, attrMap, "Id");
        if (sourceIds.contains(getId())) {
            throw new ZestException(Messages.parseSourceIdDuplicate(getId()));
        }
        sourceIds.add(getId());

        if (StringUtils.isBlank(getId())) {
            throw new ZestException(Messages.parseSourceIdEmpty());
        }

        try {
            if (elements.size() != 2 //
                || !"Init".equals(elements.get(0).getNodeName()) //
                || !"Verify".equals(elements.get(1).getNodeName())) {
                throw new ZestException(Messages.parseSourceNecessary());
            }

            this.initData = new SourceInitData(worker, getId(), elements.get(0).getNodeName(), elements.get(0));
            this.verifyData = new SourceVerifyData(worker, getId(), elements.get(1).getNodeName(), elements.get(1));
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
