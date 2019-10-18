package com.inti.formation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.inti.formation.entities.MathematiqueService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MathematiqueServiceTest {

	@Test
	public void additionTest() {
		MathematiqueService mathServ = new MathematiqueService();
		
		
		assertEquals(mathServ.addition(5, 6), 11);
	}
}
