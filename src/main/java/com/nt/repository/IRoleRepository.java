package com.nt.repository;

import com.nt.model.AppRole;
import com.nt.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {


    Optional<Object> findByRoleName(AppRole appRole);
}
