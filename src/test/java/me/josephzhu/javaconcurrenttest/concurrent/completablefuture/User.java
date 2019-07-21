package me.josephzhu.javaconcurrenttest.concurrent.completablefuture;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private Boolean vip;
}
