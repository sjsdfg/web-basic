package cn.sjsdfg.redis.limiter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @date 2019/9/8
 */
@Component
public class FunnelRateLimiter {
    private Map<String, Funnel> funnelMap = new HashMap<>();

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", userId, actionKey);
        Funnel funnel = funnelMap.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingRate);
            funnelMap.put(key, funnel);
        }
        return funnel.watering(1);
    }

    @Data
    @NoArgsConstructor
    private static class Funnel {
        /**
         * 漏斗容量
         */
        private int capacity;
        /**
         * 漏斗流水速率
         */
        private float leakingRate;
        /**
         * 漏斗剩余空间
         */
        private int leftQuota;
        /**
         * 漏斗剩余空间
         */
        private long leakingTs;

        Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }

        private void makeSpace() {
            long nowTs = System.currentTimeMillis();
            // 距离上一次漏水过去了多久
            long deltaTs = nowTs - leakingTs;
            // 减少的空间
            int deltaQuota = (int) (deltaTs * leakingRate);
            if (deltaQuota < 0) { // 间隔时间太长，整数数字过大溢出
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }
            if (deltaQuota < 1) { // 腾出空间太小，最小单位是 1
                return;
            }
            // 剩余空间
            this.leftQuota += deltaQuota;
            this.leakingTs = nowTs;
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
            System.out.println(this);
        }

        public boolean watering(int quota) {
            makeSpace();
            if (this.leftQuota >= quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }
}
