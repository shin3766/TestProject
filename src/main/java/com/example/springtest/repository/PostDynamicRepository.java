package com.example.springtest.repository;

import com.example.springtest.dto.PageDto;
import com.example.springtest.dto.PostListItemDto;
import com.example.springtest.dto.PostSearchConditionParam;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostDynamicRepository {

    private final JdbcTemplate jdbcTemplate;

    public PageDto findListByCondition(PostSearchConditionParam con) {
        int offset = con.offset();
        int size = con.size();

        String selectCount = "SELECT COUNT(*) ";
        String selectItem = "SELECT " +
                "ID, " +
                "TITLE, " +
                "CREATED_AT ";
        String from = "FROM post ";
        String where = "WHERE CONTENT LIKE '%" + con.keyword() + "%' ";
        String limit = "LIMIT " + offset + ", " + size;

        String query = selectItem + from + where + limit;
        String countQuery = selectCount + from + where;

        long totalElement = executeCountQuery(countQuery);
        long totalPage = totalElement % size == 0 ? totalElement / size : totalElement / size + 1;


            return jdbcTemplate.query(query, rs -> {
                var result = new ArrayList<PostListItemDto>();
                while (rs.next()) {
                    var id = rs.getLong(1);
                    var title = rs.getString(2);
                    var createdAt = rs.getTimestamp("CREATED_AT").toLocalDateTime();

                    result.add(new PostListItemDto(id, title, createdAt));
                }
                return new PageDto(result, totalElement, totalPage, con.page(), size);
            });
    }

    private long executeCountQuery(String countQuery) {
        return jdbcTemplate.queryForObject(countQuery, Long.class);
    }
}