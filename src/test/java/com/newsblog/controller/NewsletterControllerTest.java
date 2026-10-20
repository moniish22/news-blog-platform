package com.newsblog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NewsletterControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void subscribePageIsPublic() throws Exception {
        mvc.perform(get("/newsletter/subscribe")).andExpect(status().isOk());
    }

    @Test
    void anyoneCanSubscribe() throws Exception {
        mvc.perform(post("/newsletter/subscribe").with(csrf()).param("email", "new.person@example.com"))
                .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("msg"));
    }

    @Test
    void invalidEmailShowsError() throws Exception {
        mvc.perform(post("/newsletter/subscribe").with(csrf()).param("email", "nope"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void subscriberListIsStaffOnly() throws Exception {
        mvc.perform(get("/newsletter/manage")).andExpect(status().is3xxRedirection());
    }
}
