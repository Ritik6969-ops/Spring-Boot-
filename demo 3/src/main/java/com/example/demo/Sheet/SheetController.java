package com.example.demo.Sheet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin("*")
@RestController
@RequestMapping(path = "api/v1/script")
public class SheetController {

    private final SheetService sheetService;

    @Autowired
    public  SheetController(SheetService sheetService)  {
        this.sheetService = sheetService;
    }

    @GetMapping
    public String getScript(@RequestBody Map<String, Map<String, String>> data) throws JsonProcessingException {

        Map<String, String> customerData = data.get("content");
        String customer_id = customerData.get("customer_id");
        String bot_key = customerData.get("bot_key");
        String flow_key = customerData.get("flow_key");
        String auth_token = customerData.get("auth_token");
        String sheet_name = customerData.get("sheet_name");
        boolean showStatus = Boolean.parseBoolean(customerData.get("showStatus"));

        Map<String, String> mapped_items = data.get("mapped_items");
        ObjectMapper objectMapper = new ObjectMapper();
        String mappedItems = objectMapper.writeValueAsString(mapped_items);

        String result = sheetService.solve(sheet_name, customer_id, bot_key, flow_key, auth_token, showStatus, mappedItems);
        return result;
    }
}



