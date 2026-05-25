package library_manage.services;

import java.util.ArrayList;

import library_manage.Model.User;
import library_manage.util.ServiceResult;

public class UserServices {
	private final ArrayList<User> users;

	public UserServices() {
		this.users = new ArrayList<>();
	}

	public User findOrCreateUser(String name, boolean graduate) {
		if (name == null || name.trim().isEmpty()) {
			return null;
		}

		for (User user : users) {
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}

		User newUser = new User(name, graduate);
		users.add(newUser);
		return newUser;
	}

	public ServiceResult getAllUsers() {
		return new ServiceResult(true, "Users retrieved successfully", users);
	}

	public ServiceResult searchUserByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			return new ServiceResult(false, "Name cannot be empty");
		}

		for (User user : users) {
			if (user.getName().equalsIgnoreCase(name)) {
				return new ServiceResult(true, "User found", user);
			}
		}

		return new ServiceResult(false, "User not found");
	}
}
