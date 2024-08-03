package com.backend.bank.service.implement;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.entity.*;
import com.backend.bank.entity.constant.TakeLoanStatus;
import com.backend.bank.repository.UserRepository;
import com.backend.bank.service.EmailService;
import com.backend.bank.service.UserService;
import com.backend.bank.exception.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.backend.bank.utils.AccountUtils.ACCOUNT_ALREADY_EXISTS;
import static com.backend.bank.utils.AccountUtils.randomAccountNumber;
import static com.backend.bank.utils.EmailUtils.EMAIL_CREATE_ACCOUNT_EVENT;
import static com.backend.bank.utils.EmailUtils.emailAccountCreationSuccess;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final EmailService emailService;

    @Override
    public UserDto signup(UserDto request) throws AccountAlreadyExistsException {
        boolean accountExists = userRepository.existsByEmail(request.getAccount().getEmail());

        if (accountExists) {
            throw new AccountAlreadyExistsException(ACCOUNT_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .address(Address.builder()
                        .street(request.getAddress().getStreet())
                        .city(request.getAddress().getCity())
                        .province(request.getAddress().getProvince())
                        .country(request.getAddress().getCountry())
                        .zipCode(request.getAddress().getZipCode())
                        .build()
                )
                .phoneNumber(request.getPhoneNumber())
                .account(Account.builder()
                        .accountNumber(randomAccountNumber())
                        .email(request.getAccount().getEmail())
                        .password(request.getAccount().getPassword())
                        .balance(BigDecimal.ZERO)
                        .role(Role.USER)
                        .status(TakeLoanStatus.ACTIVE)
                        .createdDate(LocalDate.now())
                        .lastModifiedDate(LocalDate.now())
                        .build()
                )
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .receiver(request.getAccount().getEmail())
                .subject(EMAIL_CREATE_ACCOUNT_EVENT)
                .body(emailAccountCreationSuccess(savedUser))
                .build();

        emailService.sendEmail(emailDetails);

        return userMapper.toDto(savedUser);
    }

    public UserDto login(UserDto request) throws AccountNotFoundException {
        return null;
    }
}
