package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.domain.entities.User;
import app.domain.repositories.UserRepository;

@Service
public class UserServiceDefault implements UserService {

	@Autowired
	UserRepository userRepository;

	@Override
	public User getUser(Integer id) {
		return this.userRepository.findOne(id);
	}
	
	@Override
	public User getUserByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	@Override
	public User getUserByPhone(String phone) {
		return this.userRepository.findByPhone(phone);
	}
	
	@Override
	public User getUserByDocument(String document) {
		return this.userRepository.findByDocument(document);
	}

	@Override
	public User save(User user) {
		return this.userRepository.save(user);
	}

	@Override
	public void deleteUser(Integer id) {
		this.userRepository.delete(id);
	}
}
