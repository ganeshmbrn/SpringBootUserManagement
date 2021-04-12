package com.git.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.git.entities.CityMaster;

public interface CityMasterRepoistory extends JpaRepository<CityMaster, Integer> {

}
