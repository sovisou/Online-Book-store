package com.onlinebookstore.repository.role;

import com.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<User, Long> {
}
