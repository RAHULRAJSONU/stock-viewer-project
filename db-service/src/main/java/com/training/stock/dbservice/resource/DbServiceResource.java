package com.training.stock.dbservice.resource;

import com.training.stock.dbservice.model.Quote;
import com.training.stock.dbservice.model.pojo.Quotes;
import com.training.stock.dbservice.repository.QuotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/db")
public class DbServiceResource {

    @Autowired
    private QuotesRepository quotesRepository;

    @GetMapping("/{username}")
    public List<String> getQuotes(@PathVariable("username")final String username){
        return getQuotesByUserName(username);
    }

    private List<String> getQuotesByUserName(@PathVariable("username") String username) {
        return quotesRepository.findByUserName(username)
            .stream()
            .map(Quote::getQuote)
            .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public List<String> add(@RequestBody final Quotes quotes){
        quotes.getQuotes()
                .stream()
                .map(quote -> new Quote(quotes.getUserName(),quote))
                .forEach(quote -> quotesRepository.save(quote));
        return getQuotesByUserName(quotes.getUserName());
    }

    @PostMapping("/delete/{username}")
    public List<String> delete(@PathVariable("username")final String username){
        List<Quote> quotes = quotesRepository.findByUserName(username);
        quotesRepository.delete(quotes);
        return getQuotesByUserName(username);
    }
}
