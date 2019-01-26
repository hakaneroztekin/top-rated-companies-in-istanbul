package com.hakaneroztekin.glassdoorScraper;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GlassdoorScraper implements CommandLineRunner{

    @Override
    public void run(String [] args) throws Exception{
        main(args);
    }

    public static void main(String[] args) {

        try{
            UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
            userAgent.visit("https://www.glassdoor.com/Reviews/istanbul-reviews-SRCH_IL.0,8_IM1160.htm");                        //visit a url
            //System.out.println(userAgent.doc.innerHTML());               //print the document as HTML

            String title = userAgent.doc.findFirst("<title>").getChildText(); //get child text of title element.
            System.out.println("\nWebsite: " + title);    //print the title

            Elements companiesHTML = userAgent.doc.findEach("<div class=\"eiHdrModule module snug \"");       //find non-nested tables
            System.out.println("Found " + companiesHTML.size() + " companies in the page");

            List<Company> companies = new ArrayList<>();

            for(Element companyHTML : companiesHTML){
                Company newCompany = new Company();
                String companyTitle, companyRate;
                String companyInfo = companyHTML.getTextContent(); // get company info in a string (a simple approach)
                String[] splitInfo = companyInfo.trim().split("(Star)");

                String companyTitleAndRate = splitInfo[0]; // get company title and rate. eg: Yapi Kredi 3.6
                String[] titleAndRate = companyTitleAndRate.split("\\s+"); // split title and rate.
                String titleAndRateString = String.join(" ", titleAndRate);

                companyRate = titleAndRate[titleAndRate.length-1]; // get rate. eg: 3.6
                companyTitle = titleAndRateString.toString().replace(companyRate, ""); // Extract the company name. eg: Yapi Kredi

                newCompany.setTitle(titleAndRate[0]); // eg: Vodafone
                newCompany.setTitle(titleAndRate[1]); // eg: 3.8

                //System.out.println(companyTitleAndRate);
                System.out.println("Name: " + companyTitle + " Rate: " + companyRate);
                companies.add(newCompany);
            }

        }
        catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
            System.err.println(e);
        }

    }

}