package org.hacker.mvc.model.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hacker.module.common.Assert;

public class DateValidator  implements ConstraintValidator<DateFormat, Object> {
	private String format;
	
	@Override
	public void initialize(DateFormat arg0) {
		format = arg0.format();
		System.out.println(format);
	}

	@Override
	public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
		return Assert.checkDateFormat(arg0.toString(), format);
	}

}
