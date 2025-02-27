package com.example.coin_exchange.infra;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiUtil {

  public static UriComponentsBuilder uriBuilder(UriScheme uriScheme, String domain, String path, String endpoint,
      MultiValueMap<String, String> queryParams, String... pathSegments) {
    return UriComponentsBuilder.newInstance()//
        .scheme(uriScheme.getProtocol())//
        .host(domain)//
        .pathSegment(pathSegments)//
        .path(path)//
        .pathSegment(endpoint)//
        .queryParams(queryParams);
  }

  public static UriComponentsBuilder uriComponentsBuilder(UriScheme uriScheme, String domain, String path,
      String endpoint) {
    return UriComponentsBuilder.newInstance()//
        .scheme(uriScheme.getProtocol())//
        .host(domain)//
        .path(path)//
        .pathSegment(endpoint.split("/"));
  }

  public static UriComponentsBuilder uriBuilder(UriScheme uriScheme, String domain) {
    return UriComponentsBuilder.newInstance()//
        .scheme(uriScheme.getProtocol())//
        .host(domain);
  }
}
