package manual.xsd;

import com.github.bookong.zest.core.ZestGlobalConstant;

import org.apache.commons.lang.StringUtils;

/**
 * 根据 data.xsd 创建对应的实体
 */
public class GenJaxb4Data {

    public static void main(String[] args) {
        gen("schema/data.xsd", ZestGlobalConstant.DATA_XML_CODE_PACKAGE);
    }

    private static void gen(final String xsdRelativePath, final String codePackage) {
        String str = "/target/test-classes/"
                     + StringUtils.replace(GenJaxb4Data.class.getName().substring(0,
                                                                                  GenJaxb4Data.class.getName().indexOf(GenJaxb4Data.class.getSimpleName())),
                                           ".", "/");
        String path = GenJaxb4Data.class.getResource("").getPath();
        path = path.substring(0, path.indexOf(str));
        String xsdPath = path + "/" + xsdRelativePath;
        String sourceCodePath = path + "/src/main/java/";

        try {
            // 产生 xml 解析类
            com.sun.tools.xjc.XJCFacade.main(new String[] { "-p", codePackage, "-d", sourceCodePath, xsdPath });
            Thread.sleep(5000);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
