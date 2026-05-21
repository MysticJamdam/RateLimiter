package jamdam.barrier.main.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IpIdentifierResolver implements IdentifierResolver{
    @Override
    public String resolve(HttpServletRequest servletRequest) {
        return servletRequest.getRemoteAddr();
    }
}
