package com.nt.repository;

import com.nt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String username);

    boolean existsUserByUserName(String userName);

    boolean existsUserByEmail(String email);

    boolean existsByUserName(String user1);
}
