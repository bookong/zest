package zest.param;

import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;

import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Jiang Xu
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String str = "{ \"_id\" : ObjectId(\"5f3ba63bff4b095da83484dd\"), \"userId\" : NumberLong(3), \"content\" : \"FFFxxx\", \"_class\" : \"com.github.bookong.example.zest.springboot.base.entity.Remark\" }";
        BSONDecoder decoder = new BasicBSONDecoder();
        byte[] tmp = str.getBytes(StandardCharsets.UTF_8);
        byte[] tmp2 = new byte[tmp.length + 1];
        for (int i=0; i<tmp.length; i++) {
            tmp2[i] = tmp[i];
        }
        tmp2[tmp2.length - 1] = '\0';
        BSONObject bsonObject = decoder.readObject(tmp2);
        System.out.println(bsonObject.get("userId"));
    }
}
