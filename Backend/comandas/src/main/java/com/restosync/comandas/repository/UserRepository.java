package com.restosync.comandas.repository;

import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 
    Optional<User> findByEmail(String email);
 
    boolean existsByEmail(String email);
 
    List<User> findAllByActive(Boolean active);
 
    List<User> findAllByRole(UserRole role);
 
    List<User> findAllByActiveOrderByNameAsc(Boolean active);

    long countByRoleAndActive(UserRole role, Boolean active);
}
 
