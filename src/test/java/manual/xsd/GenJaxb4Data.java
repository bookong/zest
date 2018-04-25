package manual.xsd;

import com.github.bookong.zest.core.ZestGlobalConstant;

import manual.AbstractGenJaxb;

public class GenJaxb4Data extends AbstractGenJaxb {

    public static void main(String[] args) {
        gen("schema/data.xsd", ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
    }
}
