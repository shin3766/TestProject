package com.example.springtest.controller;


import com.example.springtest.dto.MessageDto;
import com.example.springtest.dto.profiledto.ProfileRequestDto;
import com.example.springtest.dto.profiledto.ProfileResponseDto;
import com.example.springtest.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ProfileResponseDto getProfileById(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody ProfileRequestDto requestDto) {
        try {
            ProfileResponseDto response = profileService.updateProfile(id, requestDto);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDto("Failed to update profile"));
        }
    }

}
