package model.exceptions;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	private Map<Object, String> errors = new HashMap<>();

	public ValidationException(String msg) {
		super(msg);
	}

	public Map<? extends Object, String> getErrors() {
		return errors;
	}

	public void addError(Object fieldName, String errorMessage) {
		this.errors.put(fieldName, errorMessage);
	}

}
