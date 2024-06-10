package com.doddanna.tinyUrl.services;

import com.doddanna.tinyUrl.models.TokenRange;
import com.doddanna.tinyUrl.repositories.TokenRangeRepository;
import com.doddanna.tinyUrl.utils.Base62Converter;
import jakarta.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Service
public class TokenGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(TokenGenerationService.class);

    private static final String LOCK_PATH = "/token-generation-lock";
    private static final int RANDOM_BITS_LENGTH = 5; // Length of the random bits to append
    private final CuratorFramework curatorFramework;
    private final TokenRangeRepository tokenRangeRepository;
    private InterProcessMutex lock;
    private final SecureRandom secureRandom = new SecureRandom();
    Map<String,PriorityQueue<String>> orgLevelTokenMap;

    @Autowired
    public TokenGenerationService(CuratorFramework curatorFramework, TokenRangeRepository tokenRangeRepository) {
        this.curatorFramework = curatorFramework;
        this.tokenRangeRepository = tokenRangeRepository;
        this.orgLevelTokenMap=new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        lock = new InterProcessMutex(curatorFramework, LOCK_PATH);
        logger.info("TokenGenerationService initialized with Zookeeper lock path: {}", LOCK_PATH);
    }

    public String getNewToken(String orgId){
        String poll = orgLevelTokenMap.getOrDefault(orgId, new PriorityQueue<>()).poll();
        logger.info("Current polled token value is : "+poll);
        if(poll==null){
            try {
                allocateToken(orgId);
                poll=orgLevelTokenMap.getOrDefault(orgId, new PriorityQueue<>()).poll();
            }catch (Exception e){
                e.printStackTrace();;
            }
        }
        return poll;
    }


    @Transactional
    private void allocateToken(String orgId) throws Exception {
        logger.info("Attempting to allocate token for organization: {}", orgId);
        if (lock.acquire(5, TimeUnit.SECONDS)) {
            try {
                // Generate the next token range
                TokenRange tokenRange = null;
                if(orgLevelTokenMap.get(orgId)==null){
                    tokenRange=generateRandomTokenRange(orgId);
                    updateRangeMap(tokenRange,tokenRange.getOrgId());
                }else if(orgLevelTokenMap.get(orgId).size()<6){
                    tokenRange=generateRandomTokenRange(orgId);
                    updateRangeMap(tokenRange,tokenRange.getOrgId());
                }else{
                    logger.info("Range based inputs are present, hence not generating");
                    return;
                }
                logger.info("Allocated token range [{} - {}] for organization: {}", tokenRange.getStartRange(), tokenRange.getEndRange(), orgId);

                // Generate a token within the allocated range
                int tokenValue = secureRandom.nextInt(tokenRange.getEndRange() - tokenRange.getStartRange() + 1) + tokenRange.getStartRange();

                logger.info("Generated token: {} for organization: {}", tokenValue, orgId);
            } finally {
                lock.release();
                logger.info("Released lock for token allocation for organization: {}", orgId);
            }
        } else {
            logger.error("Could not acquire lock for token range allocation for organization: {}", orgId);
            throw new IllegalStateException("Could not acquire lock for token range allocation");
        }
    }

    public void updateRangeMap(TokenRange tokenRange,String orgId){
        List<String> collect = LongStream.range(tokenRange.getStartRange(),  tokenRange.getEndRange()).mapToObj(x -> Base62Converter.encode((int) (x))).toList();
        synchronized (this){
            orgLevelTokenMap.computeIfAbsent(orgId, k -> new PriorityQueue<>());
            orgLevelTokenMap.get(orgId).addAll(collect);
        }
    }

    private TokenRange generateRandomTokenRange(String orgId) {
        int startRange = secureRandom.nextInt(Integer.MAX_VALUE - 10);
        int endRange = startRange + 9;

        // Persist the token range
        TokenRange newRange = new TokenRange(orgId, startRange, endRange);
        return tokenRangeRepository.save(newRange);
    }

    private TokenRange getNextRangeValue(String orgId){
        TokenRange lastRange = tokenRangeRepository.findTopByOrderByIdDesc();
        int startRange = lastRange != null ? lastRange.getEndRange() + 1 : 1;
        int endRange = startRange + 10;
        TokenRange newRange = new TokenRange(orgId, startRange, endRange);
        return tokenRangeRepository.save(newRange);
    }

    private String generateTokenWithRandomBits(int tokenValue) {
        String token = Integer.toString(tokenValue, 64); // Convert to base36 for a shorter representation
        String randomBits = generateRandomBits(RANDOM_BITS_LENGTH);
        return token + randomBits;
    }

    private String generateRandomBits(int length) {
        StringBuilder randomBits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomBits.append(Integer.toString(secureRandom.nextInt(36), 36)); // Append random base36 characters
        }
        return randomBits.toString();
    }
}

