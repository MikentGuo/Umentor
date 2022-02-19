package com.Umentor.UmentorprojectforProgrammingIII.repository;

import com.Umentor.UmentorprojectforProgrammingIII.entity.User;
import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByUsernameIsNotNull();

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.enabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);

    List<User> findAllByRole(UserRole role);

}
