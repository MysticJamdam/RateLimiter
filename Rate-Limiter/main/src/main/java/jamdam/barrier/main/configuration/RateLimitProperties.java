package jamdam.barrier.main.configuration;

import jamdam.barrier.main.entity.RateLimitPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private RateLimitPolicy defaultPolicy;

    private Map<String, RateLimitPolicy>
            endpointPolicies = new HashMap<>();

    public RateLimitPolicy getDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(
            RateLimitPolicy defaultPolicy
    ) {
        this.defaultPolicy = defaultPolicy;
    }

    public Map<String, RateLimitPolicy>
    getEndpointPolicies() {

        return endpointPolicies;
    }

    public void setEndpointPolicies(
            Map<String, RateLimitPolicy>
                    endpointPolicies
    ) {
        this.endpointPolicies = endpointPolicies;
    }
}
