package com.hakaneroztekin.glassdoorScraper;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


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
            System.out.println("Found " + companies.size() + " companies in the page");

            ArrayList<Company> companies;

            for(Element companyHTML : companiesHTML){
                Company newCompany = new Company();
                String companyInfo = companyHTML.getTextContent(); // get company info in a string (a simple approach)
                String[] parts = companyInfo.trim().split("\\d");
                String companyTitle = parts[0];

            }

        }
        catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
            System.err.println(e);
        }

    }

}