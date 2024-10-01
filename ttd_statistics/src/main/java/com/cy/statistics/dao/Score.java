package com.cy.statistics.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Haechi
 */
@Document(collection = "score")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    private String id;

    private int oneStar;

    private int twoStar;

    private int threeStar;

    private int fourStar;

    private int fiveStar;
}
