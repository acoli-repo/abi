package com.ta.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.ta.model.LoginData;

public class InputValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		//just validate the Customer instances
		return LoginData.class.isAssignableFrom(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
			"required.username", "username is invaliat.");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
				"required.password", "password is invaliat.");
		
	}
	
	
	
}