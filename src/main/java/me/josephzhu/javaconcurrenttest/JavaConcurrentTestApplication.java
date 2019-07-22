package me.josephzhu.javaconcurrenttest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
@Slf4j
public class JavaConcurrentTestApplication {

    public static void main(String[] args) throws Exception {
        new JavaConcurrentTestApplication().test();
    }

    public void test() throws InterruptedException {

        String payload = IntStream.rangeClosed(1, 1000).mapToObj(__ -> "a").collect(Collectors.joining(""));

        while (true) {
            List<String> data = new ArrayList<>();
            IntStream.rangeClosed(1, 1000000).forEach(j -> data.add(payload));
            log.info("size:{}", data.size());
        }
    }
}