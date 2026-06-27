package library_manage.services;

import java.util.ArrayList;
import library_manage.Model.User;
import library_manage.util.ServiceResult;
import library_manage.util.TxtDataStore;

public class UserServices {
	private final ArrayList<User> users;

	public UserServices() {
		this.users = TxtDataStore.loadUsers();
	}

	public User findOrCreateUser(String name, boolean graduate) { // we using this in TransactionController when we want to find or create a user when borrowing a book from the transaction panel
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
		TxtDataStore.saveUsers(users);
		return newUser;
	}

	public ServiceResult getAllUsers() {
		return new ServiceResult(true, "Users retrieved successfully", users);
	}

	public ServiceResult searchUserByName(String name) { // this using in TransactionController when we want to search for a user by name from the transaction panel for displaying the user information and the borrowed books
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

	public User getUserById(int id) {
		for (User user : users) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void saveUsers() {
		TxtDataStore.saveUsers(users);
	}

	public int getUserCount() { // this is using in TransactionControllerWhen we want to display the number of users on the screen in the statistics panel
		return users.size();
	}
}
