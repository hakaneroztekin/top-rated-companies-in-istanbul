package com.hakaneroztekin.glassdoorScraper;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Component
public class GlassdoorScraper implements CommandLineRunner{
    static List<Company> companies = new ArrayList<>();

    @Override
    public void run(String [] args) throws Exception{
        main(args);
    }

    public static void main(String[] args) {

        try{
            UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
            String URL = "https://www.glassdoor.com/Reviews/istanbul-reviews-SRCH_IL.0,8_IM1160.htm";
            userAgent.visit(URL);                        //visit a url
            //System.out.println(userAgent.doc.innerHTML());               //print the document as HTML

            String title = userAgent.doc.findFirst("<title>").getChildText(); //get child text of title element.
            System.out.println("\nWebsite: " + title);    //print the title

            Integer totalCompaniesInThePage, parsedCompaniesCount = 0;
            parsedCompaniesCount = scrapeCompanies(userAgent); // scrape the first page


            Element totalCompanyCountString = userAgent.doc.findFirst("<div class=\"count margBot floatLt tightBot\"");       // get total company count, currently it is 1,607
            // totalCompanyCountString does not only hold the number of companies yet, so we need to extract the total company count.
            String[] companyCountParts = totalCompanyCountString.getTextContent().split("\\s+"); // split title and rate.
            Integer totalCompanyCount;
            totalCompanyCount = NumberFormat.getNumberInstance(Locale.US).parse(companyCountParts[4]).intValue(); // used to convert comma string to int. 1,607 -> 1607
            //System.out.println(totalCompanyCount);

            // So that we know the total # of the companies, so we can iterate till we reach that count
            while(parsedCompaniesCount <= totalCompanyCount){
                URL = getNextURL(getPageNumber(parsedCompaniesCount));
                userAgent.visit(URL);  //visit a url
                totalCompaniesInThePage = scrapeCompanies(userAgent);
                parsedCompaniesCount += totalCompaniesInThePage;
                System.out.print("Completed!\n");
            }

        }
        catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
            System.err.println(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static Integer scrapeCompanies(UserAgent userAgent){
        Elements companiesHTML = userAgent.doc.findEach("<div class=\"eiHdrModule module snug \"");       //find non-nested tables
        Integer companyCountInThePage = 0;
        //System.out.println("Found " + companiesHTML.size() + " companies in the page");

        //List<Company> companies = new ArrayList<>();

        for(Element companyHTML : companiesHTML) {
            Company newCompany = new Company();
            String companyTitle, companyRate;
            String companyInfo = companyHTML.getTextContent(); // get company info in a string (a simple approach)
            String[] splitInfo = companyInfo.trim().split("(Star)");

            String companyTitleAndRate = splitInfo[0]; // get company title and rate. eg: Yapi Kredi 3.6
            String[] titleAndRate = companyTitleAndRate.split("\\s+"); // split title and rate.
            String titleAndRateString = String.join(" ", titleAndRate);

            companyRate = titleAndRate[titleAndRate.length - 1]; // get rate. eg: 3.6
            companyTitle = titleAndRateString.replace(companyRate, ""); // Extract the company name. eg: Yapi Kredi

            // check if company name is duplicated for the companies without a logo
            companyTitle = duplicateCheck(companyTitle);

            newCompany.setTitle(companyTitle); // eg: Vodafone
            newCompany.setTitle(companyRate); // eg: 3.8
            companyCountInThePage++;
            //System.out.println(companyTitleAndRate);
           // System.out.println("Name: " + companyTitle + " Rate: " + companyRate);
            companies.add(newCompany);
        }

        return companyCountInThePage;
    }

    public static String getNextURL(Integer pageNumber){
        // Example URL's for pages after the first page;
        // https://www.glassdoor.com/Reviews/istanbul-reviews-SRCH_IL.0,8_IM1160_IP2.htm
        // https://www.glassdoor.com/Reviews/istanbul-reviews-SRCH_IL.0,8_IM1160_IP3.htm
        // So that they have the base part and the last part with the page count (i.e #.htm)
        String nextURL = "https://www.glassdoor.com/Reviews/istanbul-reviews-SRCH_IL.0,8_IM1160_IP" + pageNumber + ".htm";
        return nextURL;
    }

    public static Integer getPageNumber(Integer i){
        Integer pageNumber = (int) (i / 10);
        System.out.print("Page " + pageNumber + " ");

        return pageNumber;
    }

    // Glassdoor mixes company logo section with title section for companies without logo.
    // For example, if company name is Amazon if it's logo is not added then title is catched like this: Amazon Logo Amazon
    // So here we check for duplicate then extract the company name, i.e, Amazon
    public static String duplicateCheck(String companyTitle){
        Boolean isDuplicate = companyTitle.contains("Logo");
        if(isDuplicate){
            String [] splitCompanyTitle = companyTitle.split("(Logo)");
            if(splitCompanyTitle[0].trim().equals(splitCompanyTitle[1].trim()))
                return splitCompanyTitle[0];
        }
        return companyTitle;
    }
}