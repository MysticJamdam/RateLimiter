package jamdam.barrier.main.Controller;

import jamdam.barrier.main.DTO.AllowRequest;
import jamdam.barrier.main.DTO.AllowResponse;
import jamdam.barrier.main.services.BucketServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratelimit")
public class LimitController {

    @Autowired
    BucketServices bucketServices;


    @PostMapping("/allow")
    public AllowResponse allow(@RequestBody AllowRequest allowRequest) {
        boolean allowed = bucketServices.allow(allowRequest.getUserId(), allowRequest.getCost());
        return new AllowResponse(allowed);
    }


}
