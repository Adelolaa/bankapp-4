package com.example.bankapp4.repository;


import com.example.bankapp4.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository <Role, Long> {


        Optional<Role> findByRoleName(String roleName);



}
