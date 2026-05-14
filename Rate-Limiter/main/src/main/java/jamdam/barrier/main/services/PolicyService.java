package jamdam.barrier.main.services;

import jamdam.barrier.main.configuration.RateLimitProperties;
import jamdam.barrier.main.entity.RateLimitPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    @Autowired
    private RateLimitProperties properties;

    public RateLimitPolicy getPolicy(
            String endpoint
    ) {
        endpoint =
                endpoint.replaceFirst("^/", "");
        System.out.println(
                "ENDPOINT = " + endpoint
        );

        System.out.println(
                "DEFAULT = " +
                        properties.getDefaultPolicy()
        );

        System.out.println(
                "MAP = " +
                        properties.getEndpointPolicies()
        );

        RateLimitPolicy policy =
                properties
                        .getEndpointPolicies()
                        .getOrDefault(
                                endpoint,
                                properties.getDefaultPolicy()
                        );

        System.out.println(
                "RETURNING = " + policy
        );

        return policy;
    }
}