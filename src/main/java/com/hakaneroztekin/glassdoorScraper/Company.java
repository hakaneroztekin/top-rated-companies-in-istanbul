package com.hakaneroztekin.glassdoorScraper;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

@Data
@Component
public class Company {
    String title;
    Integer rate;
}
