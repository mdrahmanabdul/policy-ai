package org.policyai.repos;

import java.util.Optional;

import org.policyai.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

}
