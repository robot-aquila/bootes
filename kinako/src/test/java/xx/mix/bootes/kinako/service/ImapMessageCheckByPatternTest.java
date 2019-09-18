package xx.mix.bootes.kinako.service;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ImapMessageCheckByPatternTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	static Instant SOME_TIME = T("2019-09-18T04:54:00Z");
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private ImapMessageCheckByPattern service;
	private ImapMessage message;

	@Before
	public void setUp() throws Exception {
		message = new ImapMessage(SOME_TIME, "foo@bar.com", "Hello", "Dear! Bla bla blar");
		service = new ImapMessageCheckByPattern(null, null, null);
	}
	
	@Test
	public void testApprove_WoPatterns() {
		assertTrue(service.approve(message));
	}
	
	@Test
	public void testApprove_SenderPattern() {
		service = new ImapMessageCheckByPattern("^(foo|get|met)@bar\\.com$", null, null);
		
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "foo@bar.com", "Hello", "Dear! Bla bla blar")));
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "get@bar.com", "Aloha", "Lorem ipsum")));
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "ups@bar.com", "Aloha", "Lorem ipsum")));
	}
	
	@Test
	public void testApprove_SubjectPattern() {
		service = new ImapMessageCheckByPattern(null, "(hello|aloha)", null);
		
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "foo@bar.com", "SPAM", "Dear! Bla bla blar")));
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "get@zef.bug", "Hello", "Lorem ipsum")));
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "usp@bom.dom", "Aloha", "Lorem ipsum")));
	}
	
	@Test
	public void testApprove_BodyPattern() {
		service = new ImapMessageCheckByPattern("", "", "^dear");
		
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "zulu@charlie.com", "doesn't matter", "Dear Bobby!")));
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "get@zef.bug", "Hello", "Lorem ipsum")));
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "usp@bom.dom", "Aloha", "Dear friend!")));
	}
	
	@Test
	public void testApprove_AllPatterns() {
		service = new ImapMessageCheckByPattern("@tag.bug$", "tRiGgEr", "mars");
		
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "zina@tag.bug", "My own trigger", "mars attack")));
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "zina@msdog.ru", "My own trigger", "mars attack")));
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "zina@tag.bug", "no required text in subj", "mars attack")));
		assertFalse(service.approve(new ImapMessage(SOME_TIME, "zina@tag.bug", "My own trigger", "venus attack")));
		assertTrue(service.approve(new ImapMessage(SOME_TIME, "babkamura@tag.bug", "Triggered now!", "go to mars!")));
	}

}
