package com.sourcerer.sourcerer1.news;

import java.util.List;


public interface NewsRepository {

    List<News> getNews();

    News addNews(News news);

    List<Article> getRSS(RssUrl rssUrl);

    ScrapedArticle scrapedArticle(RssUrl rssUrl);

    List<ScrapedArticle> getScrapedArticles();

    Analysis analyzeSentiment(Article article);
}
