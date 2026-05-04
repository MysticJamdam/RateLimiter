package jamdam.barrier.main.services;

import jamdam.barrier.main.entity.TokenBucket;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketServices {
    public Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public TokenBucket createTokenBucket(double capacity, double fillingRate ){
        TokenBucket tokenBucket = new TokenBucket();
        tokenBucket.setFillingRate(fillingRate);
        tokenBucket.setCapacity(capacity);
        tokenBucket.setTokens(capacity);
        tokenBucket.setLastRefillTime(System.currentTimeMillis());
        return tokenBucket;
    }

    public void refill(TokenBucket tokenBucket){
        long curr =  System.currentTimeMillis();
        double elapsedSeconds = (curr - tokenBucket.getLastRefillTime()) / 1000.00;
        if (elapsedSeconds < 0.001) return;
        double newTokens = (elapsedSeconds * tokenBucket.getFillingRate()) + tokenBucket.getTokens();
        tokenBucket.setTokens(Math.min(newTokens, tokenBucket.getCapacity()));
        tokenBucket.setLastRefillTime(curr);
    }

    public boolean allow(String userId, int cost){
        if(cost <= 0){
            throw new  IllegalArgumentException("cost must be greater than 0");
        }
        TokenBucket tokenBucket = buckets.computeIfAbsent(userId, id -> createTokenBucket(100, 10));
        synchronized (tokenBucket) {
            if (System.currentTimeMillis() > tokenBucket.getLastRefillTime()) {
                refill(tokenBucket);
            }
            if (tokenBucket.getTokens() >= cost) {
                tokenBucket.setTokens(tokenBucket.getTokens() - cost);
                return true;
            }
            return false;
        }
    }
}
