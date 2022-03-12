package com.bfh.buffer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author benfeihu
 */
public class CaffeineTest {

    private static String loader(String key) {
        System.out.println("loader is called");
        return key + System.currentTimeMillis();
    }

    @Test
    @SneakyThrows
    public void mannualLoad() {

        Cache<String, String> cache = Caffeine.newBuilder().build();
        // 手动存入数据
        cache.put("a", "AAA");
        // 获取存在的数据数据 AAA
        System.out.println(cache.getIfPresent("a"));
        // 获取不存在的数据 null
        System.out.println(cache.getIfPresent("b"));
        // 不存在时通过函数载入 1、loader is called    2、c1642065921139
        System.out.println(cache.get("c", CaffeineTest::loader));
        // 再获取 c 的值:   c1642065921139
        System.out.println(cache.getIfPresent("c"));
        // 存在时通过函数载入:   c1642065921139
        System.out.println(cache.get("c", CaffeineTest::loader));
        Thread.sleep(1000);
        // 手动失效
        cache.invalidate("c");
        // null
        System.out.println(cache.getIfPresent("c"));
        // loader is called
        // c1642065922140
        System.out.println(cache.get("c", CaffeineTest::loader));
        cache.put("c", "xxxx");
        // xxxx
        System.out.println(cache.getIfPresent("c"));
    }

    @Test
    @SneakyThrows
    public void syncLoaderExpire() {
        LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(CaffeineTest::loader);
        // 没有数据，调用 loader 函数载入数据
        System.out.println(loadingCache.get("c"));
        Thread.sleep(1000);
        // 只过了 1s ，数据没有过期，不需要调用 loader 函数
        System.out.println(loadingCache.get("c"));
        Thread.sleep(4000);
        // 总共过了5s，数据已经过期，想要重新载入
        System.out.println(loadingCache.get("c"));
        Thread.sleep(2000);
        loadingCache.put("c", "yyyy");
        Thread.sleep(2000);
        // 虽然距离上一次调用 loader 已经过去 4s，但是因为中间 put 了一次，不会调用 loader
        System.out.println(loadingCache.get("c"));
    }

    @Test
    @SneakyThrows
    public void syncLoader() {
        CacheLoader<String, String> tmpLoader1 = key -> {
            System.out.println("【" + Thread.currentThread().getName() + "】 "
                    + System.currentTimeMillis() + " : call tmpLoader 1");
            return key + "1-" + System.currentTimeMillis();
        };
        CacheLoader<String, String> tmpLoader2 = key -> {
            System.out.println("【" + Thread.currentThread().getName() + "】 "
                    + System.currentTimeMillis()  + " : call tmpLoader 2");
            return key + "2-" + System.currentTimeMillis();
        };
        /**
         * expireAfterWrite: 在缓存更新后某个时间失效缓存，在通过 get 访问时，会判断对应的 key 是否已经失效
         *                  如果是，就会移除对应的value，如果是自动加载，会重新调用加载函数
         * refreshAfterWrite: 是指经过一定时间没有更新或覆盖，则会在下一次获取该 key 时，会在后台异步去刷新缓存，如果新
         * 的缓存值还没 load 到时，，如果当前时刷新状态，即使有其他线程访问到旧值，依然只有一个线程在更新，不会出现多个线程同时
         * 刷新同一个 key 的缓存的情况
         */
        LoadingCache<String, String> loadingCache1 = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(tmpLoader1);
        LoadingCache<String, String> loadingCache2 = Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .build(tmpLoader2);

        // 主线程名：Test worker
        System.out.println(Thread.currentThread().getName());
        // 触发加载，主线程调用加载函数
        // 【Test worker】 1642073788195 : call tmpLoader 1
        //  c1-1642073788195
        System.out.println(loadingCache1.get("c"));
        // 触发加载，主线程调用加载函数
        // 【Test worker】 1642073788197 : call tmpLoader 2
        //  c2-1642073788197
        System.out.println(loadingCache2.get("c"));
        Thread.sleep(2000);
        // 值过期，触发同步更新
        // 【Test worker】 1642073790204 : call tmpLoader 1
        //  c1-1642073790204
        System.out.println(loadingCache1.get("c"));
        // 值过期，触发异步更新，返回旧值
        // 【ForkJoinPool.commonPool-worker-2】 1642073790206 : call tmpLoader 2
        //  c2-1642073788197
        System.out.println(loadingCache2.get("c"));
        Thread.sleep(100);
        // 异步更新完成，取出的为新值
        // c2-1642073790207
        System.out.println(loadingCache2.get("c"));

    }

    @Test
    @SneakyThrows
    public void test3() {
        CacheLoader<String, String> tmpLoader1 = key -> {
            System.out.println("【" + Thread.currentThread().getName() + "】 "
                    + System.currentTimeMillis() + " : call tmpLoader 1");
            return key + "1-" + System.currentTimeMillis();
        };
        LoadingCache<String, String> loadingCache1 = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .build(tmpLoader1);

        // 主线程名：Test worker
        System.out.println(Thread.currentThread().getName());
        // 触发加载，主线程调用加载函数
        // 【Test worker】 1642074262251 : call tmpLoader 1
        // c1-1642074262251
        System.out.println(loadingCache1.get("c"));
        Thread.sleep(2000);
        // 值过期，触发同步更新
        // 【Test worker】 1642074264257 : call tmpLoader 1
        // c1-1642074264257
        System.out.println(loadingCache1.get("c"));
        Thread.sleep(100);
    }

    @Test
    @SneakyThrows
    public void test4() {
        Cache<String,String> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> {
                    System.out.println(String.format("[%s]%s: remove %s=%s, because %s",
                            Thread.currentThread().getName(), System.currentTimeMillis(), key, value, cause));
                }).build();
        cache.put("a", "xxxx");
        cache.put("b", "yyyy");
        // [ForkJoinPool.commonPool-worker-9]1642075339535: remove a=xxxx, because EXPLICIT 异步释放
        cache.invalidate("a");
        Thread.sleep(2000);
        // null
        System.out.println(cache.getIfPresent("b"));
        // [ForkJoinPool.commonPool-worker-9]1642075341542: remove b=yyyy, because EXPIRED 异步释放
        Thread.sleep(100);
        System.out.println(System.currentTimeMillis() + " end");
    }

    @Test
    @SneakyThrows
    public void test5() {
        Cache<String, String> cache = Caffeine.newBuilder().refreshAfterWrite(1, TimeUnit.SECONDS).build();
        cache.put("a", "xxxx");
        System.out.println(System.currentTimeMillis() + " : " + cache.getIfPresent("a"));
//        Thread.sleep(2000);
//        System.out.println(System.currentTimeMillis() + " : " + cache.getIfPresent("a"));

    }


}
