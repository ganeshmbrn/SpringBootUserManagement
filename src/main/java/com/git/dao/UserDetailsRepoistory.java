package com.git.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.git.entities.UserDetails;

public interface UserDetailsRepoistory extends JpaRepository<UserDetails,Integer>{

	//checking EmailID & Password with database
	public Optional<UserDetails> findByEmailIdAndPassword(String emailId,String password);
	
	public UserDetails findByEmailId(String emailId);
	
}
