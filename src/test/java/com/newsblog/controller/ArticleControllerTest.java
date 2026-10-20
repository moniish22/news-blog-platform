package com.newsblog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void homePageIsPublic() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("NewsBlog")));
    }

    @Test
    void publishedArticleIsVisibleToAnonymousVisitors() throws Exception {
        mvc.perform(get("/article/10-best-java-full-stack-practices-for-2024"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("10 Best Java Full Stack Practices for 2024")));
    }

    @Test
    void draftArticleIsHiddenFromThePublic() throws Exception {
        mvc.perform(get("/article/hibernate-performance-tuning-best-practices")).andExpect(status().isNotFound());
    }

    @Test
    void listSearchArchiveAndTagPagesLoad() throws Exception {
        for (String url : new String[]{"/articles", "/articles/archive", "/search?q=spring", "/category", "/tags", "/category/java-spring", "/tags/java"}) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
    }

    @Test
    void writingRequiresLogin() throws Exception {
        mvc.perform(get("/author/articles/new")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "reader@newsblog.com", roles = "READER")
    void readersCannotWriteArticles() throws Exception {
        mvc.perform(get("/author/articles/new")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "author@newsblog.com", roles = "AUTHOR")
    void authorsSeeTheEditor() throws Exception {
        mvc.perform(get("/author/articles/new")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "author@newsblog.com", roles = "AUTHOR")
    void authorDashboardLoads() throws Exception {
        mvc.perform(get("/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@newsblog.com", roles = "ADMIN")
    void adminPagesLoad() throws Exception {
        for (String url : new String[]{"/dashboard", "/admin", "/admin/users", "/admin/settings", "/admin/analytics", "/moderation/comments", "/newsletter/manage", "/newsletter/send"}) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser(username = "reader@newsblog.com", roles = "READER")
    void adminAreaIsForbiddenForReaders() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }
}
