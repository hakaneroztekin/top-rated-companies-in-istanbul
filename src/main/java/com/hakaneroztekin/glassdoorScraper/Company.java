package com.hakaneroztekin.glassdoorScraper;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

@Component
public class Company {
    Company(){};
    String title;
    Integer rate;
}
