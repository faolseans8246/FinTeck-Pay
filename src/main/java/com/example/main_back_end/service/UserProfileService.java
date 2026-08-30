package com.example.main_back_end.service;

import com.example.main_back_end.dto.request.CompleteProfileRequest;
import com.example.main_back_end.dto.response.ProfileResponse;
import com.example.main_back_end.payload.ApiResponse;

public interface UserProfileService {

    ApiResponse<ProfileResponse> getMyProfile(String username);

    ApiResponse<ProfileResponse> completeOrUpdateProfile(String username, CompleteProfileRequest request);
}
