package com.newsblog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void anonymousUsersCannotComment() throws Exception {
        mvc.perform(post("/comments").with(csrf()).param("articleId", "1").param("comment", "Hi"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "reader@newsblog.com", roles = "READER")
    void readerCanPostAComment() throws Exception {
        mvc.perform(post("/comments").with(csrf()).param("articleId", "1").param("comment", "Great read"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "reader@newsblog.com", roles = "READER")
    void readersCannotModerate() throws Exception {
        mvc.perform(post("/moderation/comments/1/approve").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "editor@newsblog.com", roles = "EDITOR")
    void editorsCanModerate() throws Exception {
        mvc.perform(post("/moderation/comments/1/approve").with(csrf())).andExpect(status().is3xxRedirection());
    }
}
