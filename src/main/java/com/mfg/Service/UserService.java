package com.mfg.Service;

import com.mfg.Entity.User;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Model.BasicResponse;
import com.mfg.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author I337864
 *
 */
@Component
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public boolean checkSapIdExisted(String sapId){
		User user = userRepository.findBySapId(sapId);
		if (user != null){
			return true;
		}
		return false;
	}

	public BasicResponse loginCheck(String sapId, String password){
		BasicResponse response = new BasicResponse();
		User user =userRepository.findBySapId(sapId);
		if (user == null){
			response.setMessage("User not existed.");
			response.setStatus("Fail");
		}else if (user != null && !password.equals(user.getPassword())){
			response.setMessage("Wrong password.");
			response.setStatus("Fail");
		}else {
			response.setStatus("Success");
		}
		return response;
	}

	public Long deleteUser(Long userId) throws Exception{
		if (!userRepository.exists(userId)){
			throw new NoDataFoundException("User not existed.");
		}
		return userRepository.deleteById(userId);
	}

	public User registerUser(User user) throws Exception{
		Pattern pattern = Pattern.compile("^[IDCidc][0-9]{6}");
		Matcher matcher = pattern.matcher(user.getSapId());
		if (!matcher.matches()){
			throw new BadRequestException("Wrong format of sapId.");
		}
		return userRepository.save(user);
	}

	public User getUserInfo(Long userId){
		return userRepository.findById(userId);
	}
	public User getUserInfo(String sapId){
		return userRepository.findBySapId(sapId);
	}

}
