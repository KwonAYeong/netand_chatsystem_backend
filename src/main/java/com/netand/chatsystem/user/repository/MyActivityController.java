package com.netand.chatsystem.user.repository;

import com.netand.chatsystem.user.dto.MentionMessageResDTO;
import com.netand.chatsystem.user.service.MyActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-activity")
public class MyActivityController {

    private final MyActivityService myActivityService;

    @GetMapping("/mentions/{userId}")
    public ResponseEntity<List<MentionMessageResDTO>> getMentionMessages(
            @PathVariable Long userId
    ) {
        List<MentionMessageResDTO> resDTO = myActivityService.getMentions(userId);
        return ResponseEntity.ok(resDTO);
    }
}