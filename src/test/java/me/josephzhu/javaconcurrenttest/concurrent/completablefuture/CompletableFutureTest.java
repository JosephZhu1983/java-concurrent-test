package me.josephzhu.javaconcurrenttest.concurrent.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureTest {

    private static Long orderId = 123L;
    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    @Test
    public void test() throws ExecutionException, InterruptedException {
        /*
        1、同时查订单和天气
        2、查到了订单同时查用户、商户、距离、优惠券
        3、查到了距离和天气计算配送费
        4、查到了用户计算订单费
        5、计算总费用
         */
        long begin = System.currentTimeMillis();

        Order order1 = CompletableFuture.supplyAsync(() -> Services.getOrder(orderId))
                .thenApplyAsync(order -> {
                    CompletableFuture.allOf(CompletableFuture.runAsync(() -> order.setUser(Services.getUser(order.getUserId()))),
                            CompletableFuture.runAsync(() -> order.setMerchant(Services.getMerchant(order.getCouponId()))),
                            CompletableFuture.runAsync(() -> order.setCouponPrice(Services.getCouponDiscount(order.getCouponId())))).join();
                    return order;
                }).thenApplyAsync(order -> {
                    order.setOrderPrice(Services.calcOrderPrice(order.getItemPrice(), order.getUser().getVip()));
                    return order;
                }).join();
        log.info("order:{} took:{}", order1, System.currentTimeMillis() - begin);


    }

    @Test
    public void normal() {
        long begin = System.currentTimeMillis();

        Future<String> weather1 = threadPool.submit(Services::getWeatherA);
        Future<String> weather2 = threadPool.submit(Services::getWeatherB);

        Order order = Services.getOrder(orderId);
        order.setUser(Services.getUser(order.getUserId()));
        order.setMerchant(Services.getMerchant(order.getMerchantId()));
        order.setCouponPrice(Services.getCouponDiscount(order.getCouponId()));
        Integer distance = null;
        try {
            distance = Services.getWalkDistance(order.getFrom(), order.getTo());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        distance = Services.getDirectDistance(order.getFrom(), order.getTo());
        String weather = null;
        if (weather1.isDone()) {
            try {
                weather = weather1.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                weather = weather2.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        order.setDeliverPrice(Services.calcDeliverPrice(order.getMerchant().getAverageWaitMinutes(), distance, weather));
        order.setOrderPrice(Services.calcOrderPrice(order.getItemPrice(), order.getUser().getVip()));
        order.setTotalPrice(order.getOrderPrice().add(order.getDeliverPrice()).subtract(order.getCouponPrice()));
        log.info("order:{} took:{}", order, System.currentTimeMillis() - begin);
    }
}
