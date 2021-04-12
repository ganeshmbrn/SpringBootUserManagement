package com.git.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.git.entities.CountryMaster;

public interface CountryMasterRepoistory extends JpaRepository<CountryMaster, Integer>{

}
