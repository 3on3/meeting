package com.project.api.metting.repository;

import com.project.api.metting.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface GroupRepository extends JpaRepository<Group, String>, GroupRepositoryCustom {

}
