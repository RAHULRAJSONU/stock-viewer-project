package com.training.stock.stockservice.resource;

import com.training.stock.stockservice.resource.pojo.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/stock")
public class StockResource {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(StockResource.class);

    @GetMapping("{username}")
    public List<Quote> getStock(@PathVariable("username")final String usenname){
        //List<String> quotes = restTemplate.getForObject("http://localhost:8300/rest/db"+usenname, List.class);
        ResponseEntity<List<String>> quoteResponse = restTemplate.exchange("http://DB-SERVICE/rest/db/"+usenname, HttpMethod.GET,
                null,new ParameterizedTypeReference<List<String>>(){});
        List<String> quotes = quoteResponse.getBody();
        LOGGER.info("getStock() invoked..**************************");
        LOGGER.info(quotes.toString());
        return quotes
                .stream()
                .map(quote -> {
                    Stock stock = getStockPrice(quote);
                    return new Quote(quote,stock.getQuote().getPrice());
                })
                .collect(Collectors.toList());
    }

    private Stock getStockPrice(String quote) {
        try {
            return YahooFinance.get(quote);
        }catch(IOException ioe){
            ioe.printStackTrace();
            return new Stock(quote);
        }
    }
}
