package jamdam.barrier.main.services;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jamdam.barrier.main.DTO.RateLimitResult;
import jamdam.barrier.main.entity.RateLimitPolicy;
import jamdam.barrier.main.resolver.IpIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class Filter extends OncePerRequestFilter {
    @Autowired
    private BucketServices bucketServices;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private MetricsServices metricsServices;

    @Autowired
    private IpIdentifierResolver ipIdentifierResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId =
                ipIdentifierResolver.resolve(request);

        String endpoint =
                request.getRequestURI();

        RateLimitPolicy policy =
                policyService.getPolicy(endpoint);

        RateLimitResult result;

        try {

            result =
                    bucketServices.allow(
                            userId,
                            policy
                    );

        }
        catch (Exception e) {

            metricsServices.incrementRedisFailures();

            System.out.println(
                    "REDIS FAILURE: " +
                            e.getMessage()
            );

            response.setHeader(
                    "X-RateLimit-Status",
                    "DEGRADED"
            );

            result =
                    new RateLimitResult(
                            true,
                            policy.getCapacity(),
                            0,
                            0
                    );
        }

        metricsServices.incrementTotal();

        response.setHeader(
                "X-RateLimit-Limit",
                String.valueOf(
                        policy.getCapacity()
                )
        );

        response.setHeader(
                "X-RateLimit-Remaining",
                String.valueOf(
                        result.getRemainingTokens()
                )
        );

        response.setHeader(
                "X-RateLimit-Reset",
                String.valueOf(
                        result.getResetTime()
                )
        );

        if (!result.isAllowed()) {

            metricsServices.incrementBlocked();

            response.setHeader(
                    "Retry-After",
                    String.valueOf(
                            result.getRetryAfter()
                    )
            );

            response.setStatus(429);

            response.getWriter()
                    .write("Too many requests");

            return;
        }

        metricsServices.incrementAllowed();

        filterChain.doFilter(
                request,
                response
        );
    }
}
