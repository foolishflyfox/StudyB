package com.bfh.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * @author benfeihu
 */
public class RetryDemo {
    public static void main(String[] args) {
        Value v = new Value(14);
        FailRetry.with(new RetryPolicy(4)).run(() -> {
            System.out.println("Process with v.getV() = " + v.getV());
            if (v.getV() % 2 == 0 || v.getV() % 3 == 0) {
                v.setV(v.getV()+1);
                throw new RuntimeException("v.getV() is times of 2 or 3");
            }
        });
    }

    @AllArgsConstructor
    @Data
    static class Value {
        int v;
    }

    @Data
    @AllArgsConstructor
    static class RetryPolicy {
        private int retryTimes;
    }

    static class FailRetry {

        private RetryPolicy retryPolicy;

        private FailRetry(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
        }

        static FailRetry with(RetryPolicy retryPolicy) {
            return new FailRetry(retryPolicy);
        }

        @SneakyThrows
        public void run(Runnable runnable) {
            int cnt = 0;
            Exception execption = null;
            while (cnt < retryPolicy.getRetryTimes()) {
                cnt++;
                try {
                    runnable.run();
                } catch (Exception e) {
                    execption = e;
                    continue;
                }
                execption = null;
                break;
            }
            if (cnt == retryPolicy.getRetryTimes() && execption != null) {
                throw  execption;
            }
        }
    }
}
