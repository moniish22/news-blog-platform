package com.newsblog.config;

import com.newsblog.model.*;
import com.newsblog.repository.*;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.SlugGenerator;
import com.newsblog.util.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

/** Enables async emails and scheduled publishing, and seeds demo data on the first run. */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AppConfig {

    @Bean
    CommandLineRunner seedData(@Value("${app.newsblog.seed-data:true}") boolean seed,
                               UserRepository users, CategoryRepository categories, TagRepository tags,
                               ArticleRepository articles, CommentRepository comments,
                               NewsletterSubscriberRepository subscribers, ArticleAnalyticsRepository analytics,
                               ArticleViewRepository views, PasswordEncoder encoder, TextUtils text) {
        return args -> {
            if (!seed || users.count() > 0) return;
            log.info("Seeding demo data...");

            User admin = user(users, encoder, "Site Admin", "admin@newsblog.com", "Admin@123", Role.ADMIN, "Runs the platform.");
            User editor = user(users, encoder, "Emily Davis", "editor@newsblog.com", "Editor@123", Role.EDITOR, "Editor who reviews and publishes stories.");
            User john = user(users, encoder, "John Smith", "author@newsblog.com", "Author@123", Role.AUTHOR, "Full stack developer writing about Java and Spring.");
            User sarah = user(users, encoder, "Sarah Johnson", "sarah@newsblog.com", "Author@123", Role.AUTHOR, "Backend engineer. Microservices and cloud.");
            User reader = user(users, encoder, "Michael Brown", "reader@newsblog.com", "Reader@123", Role.READER, null);

            Category java = category(categories, "Java & Spring", "Core Java, Spring Boot and the wider ecosystem.");
            Category web = category(categories, "Web Development", "Frontend, backend and everything between.");
            Category devops = category(categories, "DevOps", "Build, ship and run software reliably.");
            Category career = category(categories, "Career", "Internships, interviews and growing as a developer.");

            Map<String, Tag> t = new HashMap<>();
            for (String n : List.of("Java", "Spring Boot", "Hibernate", "Docker", "REST API", "Microservices", "MySQL", "Internship", "Security")) {
                Tag tag = new Tag();
                tag.setName(n);
                tag.setSlug(SlugGenerator.slugify(n));
                t.put(n, tags.save(tag));
            }

            Article a1 = article(articles, text, "10 Best Java Full Stack Practices for 2024", ArticleStatus.PUBLISHED, john, java, 11, 1284, true,
                    "Structure your project by feature, keep controllers thin and let services own the business rules.",
                    "Write tests around services first", "Use DTOs at the web boundary", "Keep configuration in environment variables",
                    t.get("Java"), t.get("Spring Boot"), t.get("MySQL"));
            Article a2 = article(articles, text, "Spring Boot Microservices: A Complete Guide", ArticleStatus.PUBLISHED, sarah, java, 12, 956, true,
                    "Microservices trade simple deployment for independent scaling. Know the costs before you split a monolith.",
                    "Service discovery and an API gateway", "Circuit breakers for resilience", "Distributed tracing from day one",
                    t.get("Spring Boot"), t.get("Microservices"));
            articles.save(draftOf(article(articles, text, "Hibernate Performance Tuning: Best Practices", ArticleStatus.DRAFT, john, java, 13, 0, false,
                    "Most slow Hibernate apps suffer from the N+1 query problem. Here is how to find and fix it.",
                    "Fetch joins and entity graphs", "Batch fetching", "Second-level cache basics",
                    t.get("Hibernate"), t.get("Java"))));
            article(articles, text, "REST API Best Practices for Java Developers", ArticleStatus.PENDING_REVIEW, editor, web, 14, 0, false,
                    "Good APIs are predictable. Use the right HTTP methods, status codes and error format.",
                    "Version your API", "Return consistent error bodies", "Document with OpenAPI",
                    t.get("REST API"), t.get("Java"));
            Article sched = article(articles, text, "Docker for Java Developers: Getting Started", ArticleStatus.SCHEDULED, sarah, devops, -2, 0, false,
                    "A container gives your app the same environment everywhere. Start with a small Dockerfile.",
                    "Multi-stage builds", "docker-compose for MySQL", "Health checks",
                    t.get("Docker"), t.get("Java"));
            Article a6 = article(articles, text, "Spring Security 6 in Plain English", ArticleStatus.PUBLISHED, john, java, 3, 612, true,
                    "Authentication proves who you are; authorization decides what you may do. Spring Security separates the two cleanly.",
                    "SecurityFilterChain beans replace WebSecurityConfigurerAdapter", "Hash passwords with BCrypt", "Protect forms with CSRF tokens",
                    t.get("Spring Boot"), t.get("Security"));
            Article a7 = article(articles, text, "How to Write a README That Impresses Reviewers", ArticleStatus.PUBLISHED, sarah, career, 2, 340, false,
                    "Your README is the first thing a reviewer opens. Explain what the project does and how to run it in five minutes.",
                    "Add screenshots and a demo link", "List setup steps and environment variables", "Explain design decisions",
                    t.get("Internship"));

            for (Article a : List.of(a1, a2, a6, a7, sched)) {
                ArticleAnalytics an = new ArticleAnalytics();
                an.setArticle(a);
                an.setViews(a.getViews());
                an.setUniqueViews(Math.round(a.getViews() * 0.7));
                an.setAvgReadTime(95);
                an.setBounceRate(28);
                analytics.save(an);
                Random rnd = new Random(a.getId());
                int n = a.getStatus() == ArticleStatus.PUBLISHED ? 10 + rnd.nextInt(30) : 0;
                for (int i = 0; i < n; i++) {
                    ArticleView v = new ArticleView();
                    v.setArticle(a);
                    v.setIpAddress("10.0.0." + (1 + rnd.nextInt(200)));
                    v.setViewedAt(LocalDateTime.now().minusDays(rnd.nextInt(14)).minusMinutes(rnd.nextInt(600)));
                    views.save(v);
                }
            }

            Comment c1 = comment(comments, a1, reader, null, "Great article! Very helpful for my current project.", CommentStatus.APPROVED);
            comment(comments, a1, john, c1, "Thanks Michael, glad it helped!", CommentStatus.APPROVED);
            comment(comments, a2, reader, null, "Could you add more examples for Spring Security configuration?", CommentStatus.PENDING);
            comment(comments, a6, reader, null, "This saved me hours of debugging! Thank you.", CommentStatus.APPROVED);

            subscriber(subscribers, "john.doe@email.com", true);
            subscriber(subscribers, "sarah.smith@email.com", false);
            subscriber(subscribers, "michael.brown@email.com", true);
            subscriber(subscribers, "emily.davis@email.com", true);
            log.info("Demo data ready. Login: admin@newsblog.com / Admin@123 (see README for other roles)");
        };
    }

    private Article draftOf(Article a) { a.setPublishedAt(null); return a; }

    private User user(UserRepository repo, PasswordEncoder enc, String name, String email, String pw, Role role, String bio) {
        User u = new User();
        u.setName(name); u.setEmail(email); u.setPassword(enc.encode(pw)); u.setRole(role); u.setBio(bio);
        return repo.save(u);
    }

    private Category category(CategoryRepository repo, String name, String desc) {
        Category c = new Category();
        c.setName(name); c.setSlug(SlugGenerator.slugify(name)); c.setDescription(desc);
        return repo.save(c);
    }

    /** daysAgo > 0 = published in the past; daysAgo < 0 = scheduled in the future. */
    private Article article(ArticleRepository repo, TextUtils text, String title, ArticleStatus status, User author, Category cat,
                            int daysAgo, long views, boolean featured, String intro, String p1, String p2, String p3, Tag... tags) {
        Article a = new Article();
        a.setTitle(title);
        a.setSlug(SlugGenerator.slugify(title));
        a.setContent("<p>" + intro + "</p><h3>What to focus on</h3><ul><li>" + p1 + "</li><li>" + p2 + "</li><li>" + p3 + "</li></ul>"
                + "<p>Start small, measure the result, and improve one thing at a time. Every section above can be applied to your own project this week.</p>");
        a.setExcerpt(intro);
        a.setReadingTime(text.readingTime(a.getContent()));
        a.setStatus(status);
        a.setAuthor(author);
        a.setCategory(cat);
        a.setViews(views);
        a.setLikes(views / 40);
        a.setShares(views / 100);
        a.setFeatured(featured);
        a.setPublishedAt(LocalDateTime.now().minusDays(daysAgo));
        a.getTags().addAll(Arrays.asList(tags));
        return repo.save(a);
    }

    private Comment comment(CommentRepository repo, Article a, User u, Comment parent, String text, CommentStatus st) {
        Comment c = new Comment();
        c.setArticle(a); c.setUser(u); c.setParent(parent); c.setComment(text); c.setStatus(st);
        return repo.save(c);
    }

    private void subscriber(NewsletterSubscriberRepository repo, String email, boolean verified) {
        NewsletterSubscriber s = new NewsletterSubscriber();
        s.setEmail(email);
        s.setVerificationToken(UUID.randomUUID().toString());
        if (verified) s.setVerifiedAt(LocalDateTime.now());
        repo.save(s);
    }
}
