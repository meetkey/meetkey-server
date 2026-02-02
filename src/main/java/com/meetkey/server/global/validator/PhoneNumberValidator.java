package com.meetkey.server.global.validator;

import com.meetkey.server.global.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches("^\\+[1-9]\\d{1,14}$"); // 국제 번호 규격
    }
}
