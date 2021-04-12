package com.git.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.git.entities.StateMaster;

public interface StateMasterRepoistory extends JpaRepository<StateMaster, Integer> {

}
