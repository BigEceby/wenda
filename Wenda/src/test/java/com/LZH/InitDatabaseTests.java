package com.LZH;

import com.LZH.dao.QuestionDAO;
import com.LZH.dao.UserDAO;
import com.LZH.model.Question;
import com.LZH.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Test
	public void initDatabase() {
		Random random = new Random();

		for(int i=0 ; i <= 10 ; i++){
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			user.setName(String.format("User%d",i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			user.setPassword("pswd");
			userDAO.updatePassword(user);

			Date date = new Date();
			date.setTime(date.getTime()+1000*3600*i);
			Question question = new Question();
			question.setTitle(String.format("Title%d",i));
			question.setContent(String.format("CONTENT %d",i));
			question.setCreatedDate(date);
			question.setUserId(i+1);
			questionDAO.addQuestion(question);


		}

		System.out.println(questionDAO.selectLatestQuestions(0,0,10));
	}

}
