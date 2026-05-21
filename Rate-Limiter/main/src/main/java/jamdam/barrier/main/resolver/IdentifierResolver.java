package jamdam.barrier.main.resolver;

import jakarta.servlet.http.HttpServletRequest;

public interface IdentifierResolver{
    String resolve(
            HttpServletRequest servletRequest
    );
}
