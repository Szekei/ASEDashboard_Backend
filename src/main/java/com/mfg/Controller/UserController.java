package com.mfg.Controller;

import com.mfg.Entity.User;
import com.mfg.Model.BasicResponse;
import com.mfg.Service.UserService;
import com.mfg.config.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 *
 * @author I337864
 *
 */
@Api(value = "User Controller", description = "Operations pertaining to user management.")
@RequestMapping("/api")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "Return if the credential is authorized to login.")
	@PostMapping(value = "/login", produces = "application/json")
	public BasicResponse checkUser(@RequestBody User user, HttpSession session) {
		BasicResponse response = new BasicResponse();
		String sapId = user.getSapId();
		String password = user.getPassword();
        response = userService.loginCheck(sapId, password);
        if (response.getStatus().equals("Success")){
        	User loginUser = userService.getUserInfo(sapId);
        	user.setUserName(loginUser.getUserName());
        	user.setEmail(loginUser.getEmail());
            session.setAttribute(Constants.USER_SESSION_KEY, user);
        }
		return response;
	}

	@ApiOperation(value = "Return the info of this user.")
	@GetMapping(value = "/user/{id}", produces = "application/json")
	public User getUserInfo(@Valid @PathVariable("id") Long id) {
		return userService.getUserInfo(id);
	}

	@ApiOperation(value = "Register a new user.")
	@PostMapping(value="/user", produces = "application/json")
	public BasicResponse register(@RequestBody User user) throws Exception{
		BasicResponse response = new BasicResponse();
		user = userService.registerUser(user);
		if (user != null){
			response.setStatus("Success");
		}else {
			response.setStatus("Fail");
		}
		return response;
	}

	@ApiOperation(value = "Delete a user.")
	@DeleteMapping(value="/user/{id}", produces = "application/json")
	public BasicResponse deleteUser(@Valid @PathVariable("id") Long id) throws Exception{
		BasicResponse response = new BasicResponse();
		Long result = userService.deleteUser(id);
		if (result > 0){
			response.setStatus("Success");
			return response;
		}
		response.setStatus("Fail");
		return response;

	}

	//Check whether the sapId is duplicated
	@ApiOperation(value = "Return if the sapId exists already.")
	@GetMapping(value="/user/checkSapId/{sapId}", produces = "application/json")
	public BasicResponse checkUserName(@Valid @PathVariable("sapId") String sapId){
		BasicResponse response = new BasicResponse();
		if (userService.checkSapIdExisted(sapId)){
			response.setStatus("true");
		}else
			response.setStatus("false");
		return response;
	}

	@ApiOperation(value = "Return if the user has session already.")
	@GetMapping(value = "/user/session", produces = "application/json")
	public BasicResponse checkUserSession(HttpSession session){
		BasicResponse response = new BasicResponse();
		if (session.getAttribute(Constants.USER_SESSION_KEY) == null){
			response.setStatus("false");
			return response;
		}else{
			User sessionUser = (User) session.getAttribute(Constants.USER_SESSION_KEY);
			response.setStatus("true");
			response.setMessage(sessionUser.getSapId());
			return response;
		}
	}

	@ApiOperation(value = "User logout, and remove user's session.")
	@GetMapping(value = "user/logout", produces = "application/json")
	public BasicResponse logout(HttpSession session) {
		BasicResponse response = new BasicResponse();
		if (session.getAttribute(Constants.USER_SESSION_KEY) == null) {
			response.setStatus("Fail");
			response.setMessage("No login session.");
			return response;
		} else {
			session.removeAttribute(Constants.USER_SESSION_KEY);
			response.setStatus("Success");
			return response;
		}
	}

}
