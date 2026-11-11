package com.acme.center.platform.iam.application.internal.commandservices;

import com.acme.center.platform.iam.application.commandservices.UserCommandService;
import com.acme.center.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.center.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.acme.center.platform.iam.domain.model.aggregates.User;
import com.acme.center.platform.iam.application.commands.SignInCommand;
import com.acme.center.platform.iam.application.commands.SignUpCommand;
import com.acme.center.platform.iam.domain.repositories.RoleRepository;
import com.acme.center.platform.iam.domain.repositories.UserRepository;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) {
            return Result.failure(new ApplicationError("INVALID_CREDENTIALS", "Invalid username or password"));
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return Result.failure(new ApplicationError("INVALID_CREDENTIALS", "Invalid username or password"));
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (command == null || command.username() == null || command.username().isBlank()
                || command.password() == null || command.password().isBlank()) {
            return Result.failure(ApplicationError.validationError("credentials", "Username and password are required"));
        }
        if (userRepository.existsByUsername(command.username())) {
            return Result.failure(ApplicationError.conflict("User", "Username already exists"));
        }
        var requestedRoles = command.roles() == null || command.roles().isEmpty()
                ? java.util.List.of(com.acme.center.platform.iam.domain.model.entities.Role.getDefaultRole())
                : command.roles();
        var roles = requestedRoles.stream()
                .map(role -> roleRepository.findByName(role.getName()))
                .toList();

        if (roles.stream().anyMatch(java.util.Optional::isEmpty)) {
            return Result.failure(ApplicationError.notFound("Role", "one or more role names"));
        }

        var resolvedRoles = roles.stream()
                .map(java.util.Optional::get)
                .toList();

        var user = new User(command.username(), hashingService.encode(command.password()), resolvedRoles);
        try {
            return Result.success(userRepository.save(user));
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("User", "Username already exists"));
        }
    }
}
