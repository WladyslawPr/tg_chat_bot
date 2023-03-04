package com.dev.vlpr.controller.rest;

import com.dev.vlpr.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class ActivationController {

    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Registration successfully");
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
