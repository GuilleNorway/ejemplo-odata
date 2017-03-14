package com.cairone.odataexample.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.cairone.odataexample.entities.PersonaEntity;

public interface PersonaRepository
		extends JpaRepository<PersonaEntity, Integer>, QueryDslPredicateExecutor<PersonaEntity> {

}
