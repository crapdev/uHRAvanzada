package com.riwi.Eventify;

import com.riwi.Eventify.repository.EventRepository;
import com.riwi.Eventify.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventifyApplicationTests {

	@Test
	void contextLoads() {
	}

    public static class EventServiceTest {
        private EventRepository eventRepository;
        private EventService eventService;

        @BeforeEach
        void setUp(){
            ventRepository = Mockito.mock(EventRepository.class);
            eventService = new EventService(eventRepository);
        }
    }
}
