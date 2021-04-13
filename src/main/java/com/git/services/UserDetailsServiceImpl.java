package com.git.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.git.constants.ApplicationConstants;
import com.git.dao.CityMasterRepoistory;
import com.git.dao.CountryMasterRepoistory;
import com.git.dao.StateMasterRepoistory;
import com.git.dao.UserDetailsRepoistory;
import com.git.entities.CityMaster;
import com.git.entities.CountryMaster;
import com.git.entities.StateMaster;
import com.git.entities.UserDetails;
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

	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	Environment environment;

	@Override
	public String checkLogin(String emailId, String password) {

		Optional<UserDetails> userDetails = userDetailsRepo.findByEmailIdAndPassword(emailId, password);
		if (userDetails.isPresent()) {
			if ("LOCKED".equals(userDetails.get().getAccountStatus())) {
				return ApplicationConstants.ACCOUNTLOCKED;
			} else {
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
		String fromEmailId = environment.getProperty("spring.mail.username");		
		userDetails.setPassword(generateRandomPassword(10));
		userDetails.setAccountStatus("LOCKED");
		UserDetails udetails = userDetailsRepo.save(userDetails);
		
		if (udetails.getUserDetailsSeqId() != null) {
			return sendEmailForAccountActivation(fromEmailId,"Unlock IES Account",udetails);
		} else {
			return false;
		}
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

	private String generateRandomPassword(Integer rangeOfChars) {
		// using Apache Commons lang API
		return RandomStringUtils.randomAlphanumeric(rangeOfChars);
	}

	private Boolean sendEmailForAccountActivation(String fromEmailId, String emailSubject,UserDetails userDetails) {
		Boolean mailStatus = Boolean.FALSE;
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message);
		try {
			messageHelper.setFrom(fromEmailId);
			messageHelper.setTo(userDetails.getEmailId());
			messageHelper.setSubject(emailSubject);
			messageHelper.setText(getEmailBodyContent(userDetails), true);
			mailStatus = Boolean.TRUE;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailStatus;
	}

	private String getEmailBodyContent(UserDetails userDetails) {

		StringBuffer sb = new StringBuffer();
		sb.append("Hi ").append(userDetails.getFirstName() + ", ")
		        .append(userDetails.getLastName() + ":" + "<br/>")
				.append("Welcome To IES family, your registration is almost complete.<br/>")
				.append("Please Unlock you account using below details.<br/>")
				.append("Temporary Password : " + userDetails.getPassword()).append("<br/>")
				.append("<a href='http://localhost:7171/unlockAccount'>Link to unlock account</a><br/>")
				.append("Thanks,<br/>").append("IES Team");

		return sb.toString();
	}
}