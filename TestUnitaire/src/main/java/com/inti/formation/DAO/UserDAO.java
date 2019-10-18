package com.inti.formation.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inti.formation.entities.User;

@Repository
public interface UserDAO extends JpaRepository<User, Integer>{
	
	public User findById (long id);

}
