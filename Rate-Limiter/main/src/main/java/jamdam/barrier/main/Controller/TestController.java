package jamdam.barrier.main.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "Request Successful";
    }

    @GetMapping("/login")
    public String login(){
        return "Login Request Successful";
    }

    @GetMapping("/premium")
    public String Premium(){
        return "Premium Request Successful";
    }

    @GetMapping("/search")
    public String search(){
        return "Search Request Successful";
    }


}
