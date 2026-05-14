package jamdam.barrier.main.services;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jamdam.barrier.main.entity.RateLimitPolicy;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getRemoteAddr();
        System.out.println("FILTER HIT");
        String endpoint =
                request.getRequestURI();

        RateLimitPolicy policy =
                policyService.getPolicy(endpoint);

        boolean allowed =
                bucketServices.allow(
                        userId,
                        policy
                );
        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }
        filterChain.doFilter(request, response);
    }


}
