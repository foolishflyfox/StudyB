package com.bfh.lambda;

import org.junit.Test;

/**
 * @author benfeihu
 */
public class AnonymousTest {

    @Test
    public void test01() {
        int a = 2;
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                int a = 10;
                System.out.println(a);
            }
        };
        r1.run();
        Runnable r2 = () -> {
            // int a = 10;  // 编译出错
            System.out.println(a);
        };
        r2.run();
    }

    @Test
    public void test02() {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println(this.getClass().getName());  // com.bfh.lambda.AnonymousTest$2
            }
        };
        r1.run();

        Runnable r2 = () -> {
            // lambda 表达式中的 this 是其所在的类实例
            System.out.println(this.getClass().getName());  // com.bfh.lambda.AnonymousTest
        };
        r2.run();
    }

    static interface Task {
        public void execute();
    }
    public void doSomethine(Runnable r) {
        System.out.println("doSomethine(Runnable)");
        r.run();
    }
    public void doSomethine(Task task) {
        System.out.println("doSomethine(Task)");
        task.execute();
    }
    @Test
    public void test03() {
        doSomethine(new Task() {
            @Override
            public void execute() {
                System.out.println("execute task");
            }
        });
        // 下面替换为 lambda 表达式会导致编译出错
//        doSomethine(() -> {
//            System.out.println("xxx");
//        });
        // 通过强制类型转换可以解决上面的问题
        doSomethine((Task) () -> {
            System.out.println("lambda 1");
        });
        doSomethine((Runnable) () -> {
            System.out.println("lambda 2");
        });
    }
}
