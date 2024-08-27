package com.sourcerer.sourcerer1.news;

import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.jsoup.nodes.*;
import org.jsoup.select.*;

@Repository
public class JdbcNewsRepository implements NewsRepository {

    private final JdbcClient jdbcClient;

    public JdbcNewsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<News> getNews() {
        return jdbcClient.sql("select * from news")
                .query(News.class)
                .list();
    }

    @Override
    public News addNews(News news) {
        var updated = jdbcClient.sql("INSERT INTO News(name,url) values(?,?)")
                .params(List.of(news.name(), news.url()))
                .update();
        if (updated == 0) {
            return news;
        } else {
            Assert.state(updated == 1, "Failed to create news " + news.name());
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Error in creating news");
        }

    }

    @Override
    public List<Article> getRSS(RssUrl url) {
        List<Article> ArticleList = new ArrayList<>();
        try {
            URI feedUri = new URI(url.url());
            URL feedUrl = feedUri.toURL();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            List<SyndEntry> FeedList = feed.getEntries();

            for (int i = 0; i < FeedList.size() - 1; i++) {
                Article NewArticle = new Article(FeedList.get(i).getTitle(),
                        FeedList.get(i).getDescription().getValue(), FeedList.get(i).getUri());
                ArticleList.add(NewArticle);
            }
            return ArticleList;
        } catch (Exception ex) {
            ex.printStackTrace();
            String ErrorMsg = "ERROR: " + ex.getMessage();
            System.out.println(ErrorMsg);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error "+ ErrorMsg);
        }
    }

    @Override
    public ScrapedArticle scrapedArticle(RssUrl Myurl) {
        String content = "";
        String scrapeUrl = Myurl.url();
        String SourceName = Myurl.name().orElse("Unknown");

        try {
            Document doc = Jsoup.connect(scrapeUrl).get();
            Elements groupDivs = doc.select("div.group");
            for (Element groupDiv : groupDivs) {
                Elements paragraphs = groupDiv.select("p");
                for (Element paragraph : paragraphs) {
                    content += paragraph.text();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String ErrorMsg = "ERROR: " + ex.getMessage();
            System.out.println(ErrorMsg);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error "+ ErrorMsg);
        }

        var updated = jdbcClient.sql("INSERT INTO ScrapedArticle(SourceName,content) values(?,?,?)")
                .params(List.of(SourceName, content))
                .update();
        if (updated == 0) {
            return new ScrapedArticle(SourceName, content);
        } else {
            Assert.state(updated == 1, "Failed to create article");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Failed to create article");
        }
    }

    @Override
    public List<ScrapedArticle> getScrapedArticles() {
        return jdbcClient.sql("select * from ScrapedArticle")
                .query(ScrapedArticle.class)
                .list();
    }
}
