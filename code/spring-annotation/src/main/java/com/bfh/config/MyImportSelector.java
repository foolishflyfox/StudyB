package com.bfh.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 通过 @Import 导入该类指定的类，该类并不导入容器中
 * @author benfeihu
 */
public class MyImportSelector implements ImportSelector {
    /**
     * AnnotationMetadata：当前标注 @Import 注解的类的所有注解信息
     * @param importingClassMetadata
     * @return 要导入到容器中的组件全类名
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {"com.bfh.bean.Blue", "com.bfh.bean.Yellow"};
    }
}
