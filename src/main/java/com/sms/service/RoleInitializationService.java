package com.sms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.sms.entity.Role;
import com.sms.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@Service
@Component
public class RoleInitializationService {

    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleInitializationService.class);

    public RoleInitializationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initializeRoles() {
        createRoleIfNotExists("ROLE_SUPERADMIN");
        createRoleIfNotExists("ROLE_ADMIN_PUSAT");
        createRoleIfNotExists("ROLE_OPERATOR_PUSAT");
        createRoleIfNotExists("ROLE_ADMIN_PROVINSI");
        createRoleIfNotExists("ROLE_OPERATOR_PROVINSI");
        createRoleIfNotExists("ROLE_ADMIN_SATKER");
        createRoleIfNotExists("ROLE_OPERATOR_SATKER");
        createRoleIfNotExists("ROLE_USER");

        logger.info("Role initialization completed");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            logger.info("Created role: {}", roleName);
        }
    }
}
