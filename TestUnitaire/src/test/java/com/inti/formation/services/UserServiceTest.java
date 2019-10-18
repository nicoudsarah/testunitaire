package com.inti.formation.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.inti.formation.TestUnitaireApplication;
import com.inti.formation.DAO.UserDAO;
import com.inti.formation.dao.UserDaoTest;
import com.inti.formation.entities.User;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestUnitaireApplication.class)
public class UserServiceTest {

	//@Autowired
	//@Mock
	@InjectMocks
	private UserService userServiceToMock;  //on crée un faux objet à travers l'annotation @Mock
	@Autowired
	private UserService userService;
	//@Autowired
	@Mock
	private UserDAO userDao;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);
	
	@Before //on initialise les mocks toujours au début
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void givenUsers_getHalfOfNumber() {
		LOGGER.info("----------- Testing givenUsers_getHalfOfNumber Method ----------");
		LOGGER.info("----------- Constructing Utilisateurs ----------");
		// userServiceToMock = Mockito.mock(UserService.class); Use can use this instead of using annotation
		LOGGER.info("----------- Mocking getAll() methode of IUtilisateurService ----------");
		Mockito.when(userServiceToMock.getAllUsers()).thenReturn(
				Arrays.asList(new User(10, "dalii"), new User(1, "dalii"), new User(2, "dalii"), new User(18, "dalii")));
		//dans le mockito, quand le userservicetoMock fait appel à sa méthode getAll, il va retourner le tableau des 4 utilisateurs
		LOGGER.info("----------- Method Mocked ----------");
		LOGGER.info("----------- Verifying results ----------");
		//Ici, on a falsifier la méthode afin qu'elle récupère la valeur du Mock
		assertEquals(2, userService.getUserNbrHalf(userServiceToMock.getAllUsers())); //on test si l'entité qui contient désormais la liste que l'on a définit dans le mock qu'on lui insère est égàle à 2
	}
	
	@Test
	public void should_store_when_save_is_called() {
		LOGGER.info("------------- Executing should_store_when_save_" 
				+ "is_called test Of UserServiceImplTest --------------");
		User myUser = new User();
		userServiceToMock.addUser(myUser);
		Mockito.verify(userDao).save(myUser);
	}
}
