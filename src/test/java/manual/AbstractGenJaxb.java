package manual;

import org.apache.commons.lang.StringUtils;

/**
 * 根据 XSD 文件生成对应的 JAXB 解析类源代码<br>
 * 执行前最好删除 /doomed-editor/src/main/java/com/gamehomer/doomed/sdk/xml/* 下源代码文件<br>
 * 执行以后需要刷新工程
 * 
 * @author jiangxu
 */
public abstract class AbstractGenJaxb {

    /**
     * @param xsdRelativePath XSD 文件相对位置
     * @param codePackage 产生代码的包名
     */
    public static void gen(final String xsdRelativePath, final String codePackage) {
        String str = "/target/test-classes/"
                     + StringUtils.replace(AbstractGenJaxb.class.getName().substring(0, AbstractGenJaxb.class.getName().indexOf(AbstractGenJaxb.class.getSimpleName())), ".", "/");
        String path = AbstractGenJaxb.class.getResource("").getPath();
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
