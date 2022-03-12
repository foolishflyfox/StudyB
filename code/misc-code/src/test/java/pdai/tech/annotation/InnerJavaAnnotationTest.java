package pdai.tech.annotation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InnerJavaAnnotationTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test01() {
        @SuppressWarnings("all")
        List list1 = new ArrayList<Integer>();
        @SuppressWarnings({"rawtypes"})
        List list2 = new ArrayList();
        list2.add(list1);
    }
}
