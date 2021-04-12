package com.git.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.git.dao.CityMasterRepoistory;
import com.git.dao.CountryMasterRepoistory;
import com.git.dao.StateMasterRepoistory;
import com.git.dao.UserDetailsRepoistory;
import com.git.entities.CityMaster;
import com.git.entities.CountryMaster;
import com.git.entities.StateMaster;
import com.git.entities.UserDetails;
import com.git.util.ApplicationConstants;
import com.git.util.UnlockAccount;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserDetailsRepoistory userDetailsRepo;

	@Autowired
	CountryMasterRepoistory countryMasterRepo;

	@Autowired
	StateMasterRepoistory stateMasterRepo;

	@Autowired
	CityMasterRepoistory cityMasterRepo;

	@Override
	public String checkLogin(String emailId, String password) {

		Optional<UserDetails> userDetails = userDetailsRepo.findByEmailIdAndPassword(emailId, password);
		if (userDetails.isPresent()) {
			if ("LOCKED".equals(userDetails.get().getAccountStatus())) {
				return ApplicationConstants.ACCOUNTLOCKED;
			}else {
				return ApplicationConstants.WELCOMEMESSAGE;
			}
		} else {
			return ApplicationConstants.INVALIDLOGIN;
		}
	}

	@Override
	public Map<Integer, String> getAllCountries() {
		List<CountryMaster> allCountries = countryMasterRepo.findAll();

		return allCountries.stream()
			  .collect(Collectors.toMap(CountryMaster::getCountryId, CountryMaster::getCountryName));
	}

	@Override
	public Map<Integer, String> getStatesByCountry(Integer countryId) {
		List<StateMaster> statesList = stateMasterRepo.findAll();

		return statesList.stream().filter(state -> countryId.equals(state.getCountryId()))
			   .collect(Collectors.toMap(StateMaster::getStateId, StateMaster::getStateName));
	}

	@Override
	public Map<Integer, String> getCityByState(Integer stateId) {
		List<CityMaster> citiesList = cityMasterRepo.findAll();

		return citiesList.stream().filter(city -> stateId.equals(city.getStateId()))
			   .collect(Collectors.toMap(CityMaster::getCityId, CityMaster::getCityName));
	}

	@Override
	public UserDetails getUserDetailsByEmailId(String emailId) {
		UserDetails userDetails = userDetailsRepo.findByEmailId(emailId);
		return userDetails;
	}

	@Override
	public Boolean saveUserDetails(UserDetails userDetails) {
		userDetails.setPassword(generateRandomPassword());
		UserDetails udetails = userDetailsRepo.save(userDetails);
		return udetails != null;
	}

	@Override
	public String unlockAccount(UnlockAccount acc) {
		String msg = "";
		UserDetails findByEmailId = userDetailsRepo.findByEmailId(acc.getEmailId());
		if (findByEmailId.getPassword().equals(acc.getTemporaryPwd())) {
			if (acc.getNewPwd().equals(acc.getConfirmPwd())) {
				findByEmailId.setPassword(acc.getNewPwd());
				findByEmailId.setAccountStatus("UNLOCKED");
				userDetailsRepo.save(findByEmailId);
				msg = ApplicationConstants.LOGINSUCCESS;
			} else {
				msg = ApplicationConstants.CONFIRMORNEWPWDMESSAGE;
			}
		} else {
			msg = ApplicationConstants.TEMPORARYPWDCORRECT;
		}
		return msg;
	}

	@Override
	public Boolean forgotPassword(String emailId) {
		return userDetailsRepo.findByEmailId(emailId) != null;
	}
	
	private String generateRandomPassword() {
		return "";
	}
}