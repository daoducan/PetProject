package com.objectmentor.utilities;

import static com.objectmentor.utilities.ArgsException.ErrorCode.INVALID_ARGUMENT_FORMAT;
import static com.objectmentor.utilities.ArgsException.ErrorCode.INVALID_ARGUMENT_NAME;
import static com.objectmentor.utilities.ArgsException.ErrorCode.UNEXPECTED_ARGUMENT;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class Args {

	private final Map<Character, ArgumentMarshaler> marshalers;					//chứa list key-value of rule key (l, d, p) và đối tượng tương ứng ví dụ 'l' tương ứng đối tượng 'BooleanArgumentMarshaler()'
	private final Set<Character> argsFound;										//chứa danh sách các rule key (l, d, p) để kiểm tra user có nhập thông tin 'l' hay 'd' hay 'p' không!!!
	private ListIterator<String> currentArgument;								//danh sách các đối số mà người dùng nhập vào. Danh sách này sẽ được gán giá trị và được đọc tuần tự qua các tham số

	public Args(String schema, String[] args) throws ArgsException {
		marshalers = new HashMap<Character, ArgumentMarshaler>();
		argsFound = new HashSet<Character>();
		parseSchema(schema);
		parseArgumentStrings(Arrays.asList(args));
	}

	private void parseSchema(String schema) throws ArgsException {
		for (String element : schema.split(",")) {
			if (element.length() > 0) {
				parseSchemaElement(element.trim());
			}
		}
	}

	private void parseSchemaElement(String element) throws ArgsException {
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if (elementTail.length() == 0)
			marshalers.put(elementId, new BooleanArgumentMarshaler());
		else if (elementTail.equals("*"))
			marshalers.put(elementId, new StringArgumentMarshaler());
		else if (elementTail.equals("#"))
			marshalers.put(elementId, new IntegerArgumentMarshaler());
		// else if (elementTail.equals("##"))
		// marshalers.put(elementId, new DoubleArgumentMarshaler());
		// else if (elementTail.equals("[*]"))
		// marshalers.put(elementId, new StringArrayArgumentMarshaler());
		else
			throw new ArgsException(INVALID_ARGUMENT_FORMAT, elementId,
					elementTail);
	}

	private void validateSchemaElementId(char elementId) throws ArgsException {
		if (!Character.isLetter(elementId)) {
			throw new ArgsException(INVALID_ARGUMENT_NAME, elementId, null);
		}
	}

	private void parseArgumentStrings(List<String> argsList)
			throws ArgsException {
		for (currentArgument = argsList.listIterator(); currentArgument
				.hasNext();) {
			String argString = currentArgument.next();
			if (argString.startsWith("-")) {
				parseArgumentCharacters(argString.substring(1));
			} else {
				currentArgument.previous();
				break;
			}
		}
	}

	private void parseArgumentCharacters(String argChars) throws ArgsException {
		for (int i = 0; i < argChars.length(); i++) {
			parseArgumentCharacter(argChars.charAt(i));
		}
	}

	private void parseArgumentCharacter(char argChar) throws ArgsException {
		ArgumentMarshaler m = marshalers.get(argChar);
		if (m == null) {
			throw new ArgsException(UNEXPECTED_ARGUMENT, argChar, null);
		} else {
			argsFound.add(argChar);
			try {
				m.set(currentArgument);
			} catch (ArgsException e) {
				e.setErrorArgumentId(argChar);
				throw e;
			}
		}
	}

	public boolean has(char arg) {
		return argsFound.contains(arg);
	}

	public int nextArgument() {
		return currentArgument.nextIndex();
	}

	public boolean getBoolean(char arg) {
		return BooleanArgumentMarshaler.getValue(marshalers.get(arg));
	}

	public String getString(char arg) {
		return StringArgumentMarshaler.getValue(marshalers.get(arg));
	}

	public int getInt(char arg) {
		return IntegerArgumentMarshaler.getValue(marshalers.get(arg));
	}

	// public double getDouble(char arg) {
	// return DoubleArgumentMarshaler.getValue(marshalers.get(arg));
	// }
	//
	// public String[] getStringArray(char arg) {
	// return StringArrayArgumentMarshaler.getValue(marshalers.get(arg));
	// }
}