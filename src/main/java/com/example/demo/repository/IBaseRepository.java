package com.example.demo.repository;

import com.example.demo.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IBaseRepository<ENTITY extends BaseEntity> extends JpaRepository<ENTITY, Long> {
}
