package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.request.BalanceAddRequest;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    default BigDecimal toAmount(BalanceAddRequest request) {
        return request.getBalance();
    }
}
