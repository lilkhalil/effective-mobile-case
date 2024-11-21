package ru.mirea.lilkhalil.tasks.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.JwtDto;
import ru.mirea.lilkhalil.tasks.dto.SignInRqDto;
import ru.mirea.lilkhalil.tasks.dto.SignUpRqDto;
import ru.mirea.lilkhalil.tasks.exception.UserAlreadyExistsException;
import ru.mirea.lilkhalil.tasks.mapper.UserMapper;
import ru.mirea.lilkhalil.tasks.repository.UserRepository;
import ru.mirea.lilkhalil.tasks.service.AuthenticationService;
import ru.mirea.lilkhalil.tasks.service.JwtService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtDto signUp(SignUpRqDto signUpRqDto) {

        String email = signUpRqDto.getEmail();

        if (userRepository.findByEmail(email).isPresent())
            throw new UserAlreadyExistsException(email);

        User user = userMapper.signUpRqDtoToUser(signUpRqDto)
                        .setRole(User.Role.ROLE_USER)
                        .setPassword(passwordEncoder.encode(signUpRqDto.getPassword()));

        userRepository.save(user);

        return JwtDto.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    @Override
    public JwtDto signIn(SignInRqDto signInRqDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRqDto.getEmail(),
                signInRqDto.getPassword()
        ));

        UserDetails user = userDetailsService
                .loadUserByUsername(signInRqDto.getEmail());

        return JwtDto.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
