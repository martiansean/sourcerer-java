package com.sourcerer.sourcerer1.run;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.io.InputStreamReader;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Repository
public class JdbcRunRepository implements RunRepository {

    private final JdbcClient jdbcClient;

    public JdbcRunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Run> findAll() {
        return jdbcClient.sql("select * from run")
                .query(Run.class)
                .list();
    }

    public Run getHello() {
        Run MyNewRun = new Run(1, "New Run", LocalDateTime.of(2004, 6, 29, 0, 0, 0), LocalDateTime.now(), 20);
        return MyNewRun;
    }

    public String[] getRSS() {
        try {
            URI feedUri = new URI("https://www.bangkokpost.com/rss/data/world.xml");
            URL feedUrl = feedUri.toURL();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            List<SyndEntry> FeedList = feed.getEntries();

            String[] CompiledRes = new String[FeedList.size()];
            for (int i=0; i< FeedList.size()-1; i++) {
                System.out.println(i);
                CompiledRes[i] = FeedList.get(i).getTitle() + "\n" + FeedList.get(i).getDescription().getValue() + "\n" + FeedList.get(i).getUri();
            }
            return CompiledRes;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            String ErrorMsg = "ERROR: "+ex.getMessage();
            String[] Err = {ErrorMsg};
            return Err;
        }
    }

    public Optional<Run> findById(Integer id) {
        return jdbcClient.sql("SELECT id,title,started_on,completed_on,miles FROM Run WHERE id = :id")
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    public void create(Run run) {
        var updated = jdbcClient.sql("INSERT INTO Run(id,title,started_on,completed_on,miles) values(?,?,?,?,?)")
                .params(List.of(run.id(), run.title(), run.startedOn(), run.completedOn(), run.miles().toString()))
                .update();
        Assert.state(updated == 1, "Failed to create run " + run.title());
    }

    public void update(Run run, Integer id) {
        var updated = jdbcClient
                .sql("update run set title = ?, started_on = ?, completed_on = ?, miles = ? where id = ?")
                .params(List.of(run.title(), run.startedOn(), run.completedOn(), run.miles().toString(), id))
                .update();

        Assert.state(updated == 1, "Failed to update run " + run.title());
    }

    public void delete(Integer id) {
        var updated = jdbcClient.sql("delete from run where id = :id")
                .param("id", id)
                .update();

        Assert.state(updated == 1, "Failed to delete run " + id);
    }

    public int count() {
        return jdbcClient.sql("select * from run").query().listOfRows().size();
    }

    public void saveAll(List<Run> runs) {
        runs.stream().forEach(this::create);
    }
}