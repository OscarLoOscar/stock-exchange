package com.example.coin_exchange.cookie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.coin_exchange.infra.ApiUtil;
import com.example.coin_exchange.infra.UriScheme;
import com.example.coin_exchange.model.apiResponse.YahooApiResponse;
import com.example.coin_exchange.redis.core.RedisHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FinanceService {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${yahoo.finance.api.url}")
    private String yahooFinanceApiUrl;

    @Value("${redis.crumb_key}")
    private String CRUMB_KEY;

    @Value("${redis.cookie_key}")
    private String COOKIE_KEY;

    @Value("${yahoo.finance.api.symbols}")
    private String yahooFinanceApiSymbols;

    private String cookie;
    private String crumb;

    @Scheduled(fixedRateString = "${redis.cookie.refresh.interval}")
    public void refreshCookie() {
        log.info("Checking if cookies and crumb need refreshing");
        try {
            if (checkIfExpired()) {
                log.info("Cookies are expired, refreshing...");
                resetCookieCrumb();
                redisHelper.set(CRUMB_KEY, crumb);
                redisHelper.set(COOKIE_KEY, cookie);
                log.info("New crumb and cookie are saved in Redis");
            } else {
                log.info("Cookies are still valid");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unauthorized access - Invalid Crumb.", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred in scheduled task", e);
        }
    }

    private boolean checkIfExpired() throws JsonProcessingException {
        String firstSymbol = getFirstSymbol();
        ResponseEntity<YahooApiResponse> response = callYahooFinanceApi(firstSymbol);

        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    private String getFirstSymbol() {
        List<String> symbolList = List.of(yahooFinanceApiSymbols.split(","));
        return symbolList.get(0);
    }

    private ResponseEntity<YahooApiResponse> callYahooFinanceApi(String symbol) throws JsonProcessingException {
        return ResponseEntity.of(Optional.of(fetchDataWithCrumb(symbol)));
    }

    public YahooApiResponse fetchDataWithCrumb(String symbol) throws JsonProcessingException {
        String url = buildUrl(symbol);
        HttpHeaders headers = buildHeaders();
        HttpEntity<YahooApiResponse> entity = new HttpEntity<>(headers);
        ResponseEntity<YahooApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooApiResponse.class);
        return response.getBody();
    }

    private String buildUrl(String symbol) {
        UriComponentsBuilder uri = ApiUtil.uriBuilder(UriScheme.HTTPS, yahooFinanceApiUrl);
        uri.queryParam("symbols", symbol);
        uri.queryParam("crumb", getCrumb());
        log.info("URL: {}", uri.build(false).toUriString());
        return uri.build(false).toUriString();
    }

    private HttpHeaders buildHeaders() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", redisHelper.get(COOKIE_KEY, String.class));
        return headers;
    }

    private void resetCookieCrumb() {
        setCookie();
        setCrumb();
    }

    private synchronized void setCookie() {
        if (cookie == null) {
            try {
                URL url = new URI("https://fc.yahoo.com?p=us").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null) {
                    cookie = cookie.split(";")[0];
                    log.info("cookie: {}", cookie);
                }
                connection.disconnect();
            } catch (Exception e) {
                log.debug("Failed to set cookie from HTTP request.", e);
            }
        }
    }

    private synchronized void setCrumb() {
        if (crumb == null) {
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URI("https://query2.finance.yahoo.com/v1/test/getcrumb").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", cookie);
                connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestMethod("GET");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                crumb = response.toString();
            } catch (Exception e) {
                log.debug("Failed to set crumb from HTTP request.", e);
            }
        }
    }

    public synchronized String getCookie() {
        if (cookie == null || cookie.isEmpty()) {
            resetCookieCrumb();
        }
        return cookie;
    }

    public synchronized String getCrumb() {
        if (crumb == null || crumb.isBlank()) {
            resetCookieCrumb();
        }
        return crumb;
    }
}
