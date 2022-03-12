spring 官方文档：https://docs.spring.io/spring-framework/docs/

将普通 gradle java 项目变为 spring-boot 项目，修改 build.gradle
```groove
plugins {
    id 'org.springframework.boot' version '2.0.1.RELEASE'
    id 'io.spring.dependency-management' version '0.6.1.RELEASE'
    id 'java'
}

group = 'com.bfh'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenLocal()
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    mavenCentral()
}

dependencies {
    compile 'org.springframework.boot:spring-boot-starter'
    testCompile 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```
