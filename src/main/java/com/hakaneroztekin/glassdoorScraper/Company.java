package com.hakaneroztekin.glassdoorScraper;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

@Data
@Component
public class Company implements Comparable<Company> {
    String title;
    Double rate;
    Integer reviewCount;

    @Override
    public int compareTo(Company otherCompany) {
        if (rate == null || otherCompany.getRate() == null) {
            return 0;
        }
        return getRate().compareTo(otherCompany.getRate());
    }

}
