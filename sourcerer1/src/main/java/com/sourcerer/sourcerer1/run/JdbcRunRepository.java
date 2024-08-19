package com.sourcerer.sourcerer1.run;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Run MyNewRun = new Run(1, "New Run", LocalDateTime.of(2004, 6, 29, 0, 0, 0),LocalDateTime.now(),20);
        return MyNewRun;
    }
    public Optional<Run> findById(Integer id) {
        return jdbcClient.sql("SELECT id,title,started_on,completed_on,miles FROM Run WHERE id = :id" )
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    public void create(Run run) {
        var updated = jdbcClient.sql("INSERT INTO Run(id,title,started_on,completed_on,miles) values(?,?,?,?,?)")
                .params(List.of(run.id(),run.title(),run.startedOn(),run.completedOn(),run.miles().toString()))
                .update();
        Assert.state(updated == 1, "Failed to create run " + run.title());
    }

    public void update(Run run, Integer id) {
        var updated = jdbcClient.sql("update run set title = ?, started_on = ?, completed_on = ?, miles = ? where id = ?")
                .params(List.of(run.title(),run.startedOn(),run.completedOn(),run.miles().toString(), id))
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