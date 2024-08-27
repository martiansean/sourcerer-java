package com.sourcerer.sourcerer1.news;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/news")
class NewsController {
    
    private final JdbcNewsRepository newsRepository;

    NewsController(JdbcNewsRepository runRepository) {
        this.newsRepository = runRepository;
    }

    @GetMapping
    List<News> findAll() {
        return newsRepository.getNews();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    News addNews(@Valid @RequestBody News news) {
        newsRepository.addNews(news);
        return news;
    }

    @PostMapping("/rss")
    List<Article> getRSS(@Valid @RequestBody RssUrl rssUrl) {
        return newsRepository.getRSS(rssUrl);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/scrape")
    ScrapedArticle scrape(@Valid @RequestBody RssUrl rssUrl) {
        return newsRepository.scrapedArticle(rssUrl);
    }

    @GetMapping("/scrape")
    List<ScrapedArticle> getScrapedArticles() {
        return newsRepository.getScrapedArticles();
    }
    

}