package com.git.services;

import java.util.Map;

import com.git.entities.UserDetails;
import com.git.util.UnlockAccount;


public interface UserDetailsService {
	
	public String checkLogin(String emailId,String password);
	
	public Map<Integer,String> getAllCountries();
	
	public Map<Integer,String> getStatesByCountry(Integer countryId);
	
	public Map<Integer,String> getCityByState(Integer stateId);
	
	//checking email Id is existed or not (Email validation)
	public UserDetails getUserDetailsByEmailId(String emailId);
	
	public Boolean saveUserDetails(UserDetails userDetails);
	
	public String unlockAccount(UnlockAccount acc);
	
	public Boolean forgotPassword(String emailId);
}