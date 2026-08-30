package com.example.main_back_end.service.implServices;

import com.example.main_back_end.dto.AddressDto;
import com.example.main_back_end.dto.PassportDto;
import com.example.main_back_end.dto.request.CompleteProfileRequest;
import com.example.main_back_end.dto.response.ProfileResponse;
import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.entity.Users;
import com.example.main_back_end.model.Address;
import com.example.main_back_end.model.Passport;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.repository.AuthUserRepository;
import com.example.main_back_end.repository.UsersRepository;
import com.example.main_back_end.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final AuthUserRepository authUserRepository;
    private final UsersRepository usersRepository;

    @Override
    public ApiResponse<ProfileResponse> getMyProfile(String username) {
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElse(null);

        if (authUser == null) {
            return ApiResponse.error("Foydalanuvchi topilmadi");
        }

        Users user = usersRepository.findByAuthUser(authUser)
                .orElseGet(() -> Users.builder()
                        .authUser(authUser)
                        .firstName("")
                        .lastName("")
                        .address(new Address())
                        .passport(new Passport())
                        .build());

        ProfileResponse response = new ProfileResponse(
                user.getId() != null ? user.getId() : UUID.randomUUID(),
                authUser.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                mapToAddressDto(user.getAddress()),
                mapToPassportDto(user.getPassport()),
                user.getBirthDate(),
                authUser.getRole() != null ? authUser.getRole().name() : "USER"
        );

        return ApiResponse.success("Profil ma'lumotlari muvaffaqiyatli olindi", response);
    }

    @Override
    @Transactional
    public ApiResponse<ProfileResponse> completeOrUpdateProfile(String username, CompleteProfileRequest request) {
        if (request == null) {
            return ApiResponse.error("So'rov ma'lumotlari bo'sh bo'lishi mumkin emas");
        }

        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElse(null);

        if (authUser == null) {
            return ApiResponse.error("Foydalanuvchi topilmadi");
        }

        Users user = usersRepository.findByAuthUser(authUser)
                .orElse(new Users());

        user.setAuthUser(authUser);
        user.setFirstName(request.firstName() != null ? request.firstName() : user.getFirstName());
        user.setLastName(request.lastName() != null ? request.lastName() : user.getLastName());
        user.setBirthDate(request.birthDateDto());
        user.setAddress(mapToAddress(request.addressDto(), user.getAddress()));
        user.setPassport(mapToPassport(request.passportDto(), user.getPassport()));

        usersRepository.save(user);

        ProfileResponse response = new ProfileResponse(
                user.getId(),
                authUser.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                mapToAddressDto(user.getAddress()),
                mapToPassportDto(user.getPassport()),
                user.getBirthDate(),
                authUser.getRole() != null ? authUser.getRole().name() : "USER"
        );

        return ApiResponse.success("Profil ma'lumotlari saqlandi", response);
    }

    private Address mapToAddress(AddressDto dto, Address existing) {
        Address address = existing != null ? existing : new Address();
        if (dto == null) {
            return address;
        }
        address.setCountry(dto.country());
        address.setCity(dto.city());
        address.setRegion(dto.region());
        address.setDistrict(dto.district());
        address.setStreet(dto.street());
        address.setHome(dto.houseNumber());
        return address;
    }

    private Passport mapToPassport(PassportDto dto, Passport existing) {
        Passport passport = existing != null ? existing : new Passport();
        if (dto == null) {
            return passport;
        }
        passport.setPassportSeries(dto.passportSeries());
        passport.setPassportNumber(dto.passportNumber());
        passport.setIssuedBy(dto.issuedBy());
        passport.setIssueDate(dto.issueDate());
        return passport;
    }

    private AddressDto mapToAddressDto(Address address) {
        if (address == null) {
            return new AddressDto(null, null, null, null, null, 0);
        }
        return new AddressDto(
                address.getCountry(),
                address.getCity(),
                address.getRegion(),
                address.getDistrict(),
                address.getStreet(),
                address.getHome()
        );
    }

    private PassportDto mapToPassportDto(Passport passport) {
        if (passport == null) {
            return new PassportDto(null, null, null, null);
        }
        return new PassportDto(
                passport.getPassportSeries(),
                passport.getPassportNumber(),
                passport.getIssuedBy(),
                passport.getIssueDate()
        );
    }
}
