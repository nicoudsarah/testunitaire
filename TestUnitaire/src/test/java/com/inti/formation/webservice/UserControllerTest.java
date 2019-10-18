package com.inti.formation.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inti.formation.TestUnitaireApplication;
import com.inti.formation.dao.UserDaoTest;
import com.inti.formation.entities.User;
import com.inti.formation.services.UserService;
import com.inti.formation.webservices.UserController;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestUnitaireApplication.class)
public class UserControllerTest {

	//permet d'executer des requetes web
	@Autowired
	WebApplicationContext webApplicationContext;
	// Utilisé pour mocker le web context
	
	//permet de rendre le webCOntexte comme étant un mock (un faux car on en pas vraiment besoin, on veut juste simuler)
	protected MockMvc mvc;
	//used for the web service adressing. You need to initiate it in the subclasses constructors
	
	// attribut contenant l'url
	protected String uri;
	
	// etape effectuée avant le test
	// grace au mvcBuilder, on cré une fausse image du webApplication
	// ligne suivante pour prendre en compte les opéartions mock(il y en a pas ici, mais on le met quand meêm au cas ou)
	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		MockitoAnnotations.initMocks(this);
	}

	//Création d'un faux userController
	@InjectMocks
	private UserController userController;
	
	// Création d'un faux userService
	@Mock
	private UserService userServiceToMock;
	
	@Autowired
	private UserService userService;

	// LOGGER = sysout
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);

	// constructeur pour définir l'adresse uri
	public UserControllerTest() {
		super();
		this.uri = "/user";
	}

	@Test
	public void getAllENtityList() {
		//contient la réponse HTTP de notre mock Mvc
		MvcResult mvcResult;
		try {
			//Creation d'un utilisateur dalii qu'on enregistre dans une DB
			LOGGER.info("---------- Testing getAllENtityList ----------");
			LOGGER.info("---------- Constructing Utilisateur ----------");
			LOGGER.info("---------- Saving Utilisateur ----------");
			userService.addUser(new User(2, "dalii"));
			
			
			// reçoit l'image mvc qui va performer la fonction get avec une adresse uri, accepter un objet Json et le retourner
			// c'est la requète faite au faux webservice et il recupère un type json
			LOGGER.info("---------- Mocking Context Webservice ----------");
			mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/all").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
			
			// Récupération du status de la requete précédente
			LOGGER.info("---------- Getting HTTP Status ----------");
			int status = mvcResult.getResponse().getStatus();
			
			// Si le status vaut 200, le webservice est dispo et c'est bon
			LOGGER.info("---------- Verifying HTTP Status ----------");
			assertEquals(200, status);
			
			// Récupération du body (contenu) de la réponse
			LOGGER.info("---------- Getting HTTP Response ----------");
			String content = mvcResult.getResponse().getContentAsString();
			
			//avec mapFromJson, on lui indique ce qui est concerné et ce qu'il doit retourner (ici, un tableau)
			LOGGER.info("---------- Deserializing JSON Response ----------");
			User[] userList = this.mapFromJson(content, User[].class);
			// On vérifie qu'il a bien enregistré l'objet (il ne faut pas que ce soit vide)
			assertTrue(userList.length > 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void createEntity() {
		LOGGER.info("---------- Testing createEntity Method ----------");
		LOGGER.info("---------- Constructing Utilisateur ----------");
		User user = new User(50, "sala7");
		
		MvcResult mvcResult;
		try {
			//consversion java en Json
			LOGGER.info("---------- Serializing Utilisateur Object ----------");
			String inputJson = this.mapToJson(user);
			
			// execute une requete post, lui donne une url, lui file le inputJson et le retourne
			LOGGER.info("---------- Mocking Context Webservice and invoking the webservice ----------");
			mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/adduser")
					.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
			
			// récupère le status
			LOGGER.info("---------- Getting HTTP Status ----------");
			int status = mvcResult.getResponse().getStatus();
			
			//Vérifie le status
			LOGGER.info("---------- Verifying HTTP Status ----------");
			assertEquals(200, status);
			
			// récupère le contenu
			LOGGER.info("---------- Searching for Utilisateur ----------");
			User userFound = userService.getUserById(new Long(50));
			
			//vérifie que c'est la bonne personnegi
			LOGGER.info("---------- Verifying  Utilisateur ----------");
			assertEquals(userFound.getUserName(), user.getUserName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	 public void addUser() {
        LOGGER.info(
                "--------------- Executing addUser test of the UserControlleur ---------------");
        User userTest = new User();
        userController.addNewUser(userTest);
        verify(userServiceToMock).addUser(userTest);
    }
	
	// nnnnmmmmmmmmmmmmmmmmm
	@Test
	public void statusMethod() throws Exception {
		LOGGER.info("--------------- Testing statusMethod Method ---------------");
		try {
			LOGGER.info("---------- Constructing Utilisateur ----------");
			User user = new User(50, "sala7");
			String inputJson;
			LOGGER.info("---------- Serializing Utilisateur Object ----------");
			inputJson = this.mapToJson(user);
			MvcResult mvcResult;
			LOGGER.info("---------- Mocking Context Webservice and invoking the webservice ----------");
			mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri + "/adduser")
					.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
			LOGGER.info("---------- Getting HTTP Status ----------");
			int status = mvcResult.getResponse().getStatus();
			LOGGER.info("---------- Verifying  Utilisateur ----------");
			assertEquals(200, status);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
		
		@Test
		public void updateEntity() {
			try {
				LOGGER.info("---------- Testing updateEntity Method ----------");
				
				LOGGER.info("---------- Constructing Utilisateur ----------");
				User oldUser = new User(2, "Lemon");
				LOGGER.info("---------- Saving Utilisateur ----------");
				userService.addUser(oldUser);
				LOGGER.info("---------- Modifying Utilisateur ----------");
				
				User newUser = new User(2, "Lemonade");
				LOGGER.info("---------- Serializing Utilisateur Object ----------");
				
				String inputJson = this.mapToJson(newUser);
				LOGGER.info("---------- Mocking Context Webservice and invoking the webservice ----------");
				
				//utilisation de la méthode put, ajoute du fichier json
				MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri + "/2")
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
				LOGGER.info("---------- Getting HTTP Status ----------");
				
				int status = mvcResult.getResponse().getStatus();
				LOGGER.info("---------- Verifying HTTP Status ----------");
				
				assertEquals(200, status);
				LOGGER.info("---------- Searching for Utilisateur ----------");
				
				User userFound = userService.getUserById(new Long(2));
				LOGGER.info("---------- Verifying  Utilisateur ----------");
				
				assertEquals(userFound.getUserName(), newUser.getUserName());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Test
		public void deleteEntity() {
			LOGGER.info("---------- Testing deleteEntity Method ----------");
			
			try {
				LOGGER.info("---------- Constructing Utilisateur ----------");
				LOGGER.info("---------- Saving Utilisateur ----------");
				userService.addUser(new User(2, "Lemon"));
				LOGGER.info("---------- Mocking Context Webservice and invoking the webservice ----------");
				
				//méthode delete
				MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri +"/2")).andReturn();
				LOGGER.info("---------- Getting HTTP Status ----------");
				
				int status = mvcResult.getResponse().getStatus();
				LOGGER.info("---------- Verifying HTTP Status ----------");
				
				assertEquals(200, status);
				LOGGER.info("---------- Searching for Utilisateur ----------");
				
				User userFound = userService.getUserById(new Long(2));
				LOGGER.info("---------- Verifying  Utilisateur ----------");
				// on vérifie si l'utilisateur existe
				assertEquals(userFound, null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	
//		Serialize the given object into Json
//		@param obj
//		@return String
//		@throws JsonProcessingException
//				
		// Cette  méthode permet que l'objet java soit transformé en objet Json
		protected final String mapToJson(Object obj) throws JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
		}
		
		
//		Deserialize a givenJson string into an object
//		@param json
//		@param clazz
//		@return 
//		@throws JsonProcessingException
//		@throws JsonMappingException
//		@throws IOException
//		
		// Cette  méthode permet que l'objet Json soit transformé en objet java
		protected final <T> T mapFromJson(String json, Class<T> clazz)
				throws JsonParseException, JsonMappingException, IOException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, clazz);
		}

}
