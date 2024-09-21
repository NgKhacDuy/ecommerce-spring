package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entites.Role;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
