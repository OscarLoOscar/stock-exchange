package com.example.coin_exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coin_exchange.entity.StockQuoteYahoo;

@Repository
public interface CoinRepo extends JpaRepository<StockQuoteYahoo,Integer>{

} 