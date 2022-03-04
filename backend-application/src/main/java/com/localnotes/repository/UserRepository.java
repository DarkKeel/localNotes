package com.localnotes.repository;

import com.localnotes.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String name);

    Optional<User> findByPublicId(String id);

    @Query("select u from User u where u.email = :email and u.publicId not like :publicId")
    Optional<User> findDuplicateUserByEmail(String publicId, String email);
}
