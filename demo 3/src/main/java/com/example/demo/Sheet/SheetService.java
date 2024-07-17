package com.example.demo.Sheet;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SheetService {

    public String solve(String sheet_name, String customer_id, String bot_key, String flow_key,
                        String auth_token, boolean showStatus, String mapped_items) {

        String data = "function onOpen() {\n" +
                "  var ui = SpreadsheetApp.getUi();\n" +
                "  ui.createMenu('Engati')\n" +
                "      .addItem('Send API Trigger to all customers', 'sendApiTriggerToAll')\n" +
                "      .addItem('Send API Trigger to newly added customers', 'sendApiTrigger')\n" +
                "      .addToUi();\n" +
                "}\n" +
                "\n" +
                "function checkValidNumber(number) {\n" +
                "  var length = number.length;\n" +
                "  for(var i = 0; i < length; i++) {\n" +
                "    if(!(number[i] >= '0' && number[i] <= '9')) {\n" +
                "      return false;\n" +
                "    }\n" +
                "  }\n" +
                "  return true;\n" +
                "}\n" +
                "\n" +
                "function isRowComplete(sheet, rowIndex) {\n" +
                "  var headers = sheet.getRange(1, 1, 1, sheet.getLastColumn()).getValues()[0];\n" +
                "  var rowValues = sheet.getRange(rowIndex + 1, 1, 1, sheet.getLastColumn()).getValues()[0];\n" +
                "  \n" +
                "  var statusPresent = showStatus_holder;\n" +
                "  if(!statusPresent) {\n" +
                "    return rowValues.every(function(cell) {\n" +
                "      return cell.toString().trim() !== \"\" && cell !== undefined && cell !== null;\n" +
                "    });\n" +
                "  } else {\n" +
                "    return rowValues.slice(0, -2).every(function(cell) {\n" +
                "      return cell.toString().trim() !== \"\" && cell !== undefined && cell !== null;\n" +
                "    });\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "function sendApiTrigger() {\n" +
                "\n" +
                "  var spreadsheet = SpreadsheetApp.getActiveSpreadsheet();\n" +
                "  var sheet = spreadsheet.getSheetByName(\"sheet_name_holder\");\n" +
                "  var dataRange = sheet.getDataRange();\n" +
                "  var data = dataRange.getValues();\n" +
                "\n" +
                "   var url = 'https://qaapi.engati.com/bot-api/v2.0/customer/customer_id_holder/bot/bot_key_holder/flow/flow_key_holder';  \n" +
                "\n" +
                "  var auth = \"Basic auth_token_holder\";\n" +
                "  var headers = {\n" +
                "    'Content-Type': 'application/json',\n" +
                "    'Authorization': auth\n" +
                "  };\n" +
                "  \n" +
                "  var mapped_items = mapped_items_holder;\n" +
                "\n" +
                "  var dataMapping = {};\n" +
                "  for(var i = 0; i < data[0].length; i++) {\n" +
                "    if(data[0][i] == \"Status\" || data[0][i] == \"Status Updated On\") {\n" +
                "      continue;\n" +
                "    }\n" +
                "    dataMapping[data[0][i]] = i;\n" +
                "  }\n" +
                "  \n" +
                "  var showStatus = showStatus_holder;\n" +
                "  if(showStatus) {\n" +
                "    var length = data[0].length;\n" +
                "    if(data[0][length - 1] != \"Status Updated On\") {\n" +
                "      var lastColumn = sheet.getLastColumn();\n" +
                "      sheet.insertColumnAfter(lastColumn);\n" +
                "      sheet.getRange(1, lastColumn + 1).setValue('Status');\n" +
                "      lastColumn = sheet.getLastColumn();\n" +
                "      sheet.insertColumnAfter(lastColumn);\n" +
                "      sheet.getRange(1, lastColumn + 1).setValue('Status Updated On');\n" +
                "      dataRange = sheet.getDataRange();\n" +
                "      data = dataRange.getValues();\n" +
                "    }\n" +
                "  }\n" +
                "  \n" +
                "  var date = new Date();\n" +
                "  var formattedTime = Utilities.formatDate(date, Session.getScriptTimeZone(), 'yyyy-MM-dd HH:mm:ss');\n" +
                "\n" +
                "  var numberIndex = dataMapping[mapped_items[\"user.phone_no\"]];\n" +
                "  var statusIndex = data[0].length - 2;\n" +
                "  var statusTime = data[0].length - 1;\n" +
                "  \n" +
                "  var scriptProperties = PropertiesService.getScriptProperties();\n" +
                "  var lastIndex = parseInt(scriptProperties.getProperty('apiTriggerLastIndex')) || 1;\n" +
                "  var i = lastIndex;\n" +
                "\n" +
                "  while(i < data.length) {\n" +
                "    if(!isRowComplete(sheet, i)) {\n" +
                "      break;\n" +
                "    }\n" +
                "    var number = data[i][numberIndex].toString();\n" +
                "    \n" +
                "    if(!checkValidNumber(number)) {\n" +
                "      if(showStatus) {\n" +
                "        data[i][statusIndex] = \"Enter valid mobile number\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "        dataRange.setValues(data);\n" +
                "      }\n" +
                "      i++;\n" +
                "      continue;\n" +
                "    }\n" +
                "\n" +
                "    var userNumber = number;\n" +
                "    if(number.length == 10) {\n" +
                "      userNumber = \"91\" + number;\n" +
                "    }\n" +
                "\n" +
                "    var payload = {};\n" +
                "  \n" +
                "    for(var key in mapped_items) {\n" +
                "      var value = mapped_items[key];\n" +
                "      if(value == mapped_items[\"user.phone_no\"]) {\n" +
                "        payload[key] = userNumber;\n" +
                "      }\n" +
                "      else {\n" +
                "        var index = dataMapping[value];\n" +
                "        payload[key] = data[i][index];\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    var payloadJson = JSON.stringify(payload);\n" +
                "\n" +
                "    var options = {\n" +
                "      method: 'post',\n" +
                "      headers: headers,\n" +
                "      payload: payloadJson\n" +
                "    };\n" +
                "\n" +
                "    var response = UrlFetchApp.fetch(url, options);\n" +
                "    Logger.log(response.getContentText());\n" +
                "\n" +
                "    var result = response.getResponseCode();\n" +
                "\n" +
                "    if(showStatus) {\n" +
                "      if(result == 200) {\n" +
                "        data[i][statusIndex] = \"Success\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "      }\n" +
                "      else {\n" +
                "        data[i][statusIndex] = \"Failed\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "      }\n" +
                "      dataRange.setValues(data);\n" +
                "    }\n" +
                "    i++;\n" +
                "  }\n" +
                "  scriptProperties.setProperty('apiTriggerLastIndex', i);\n" +
                "}\n" +
                "\n" +
                "function sendApiTriggerToAll() {\n" +
                "\n" +
                "  var spreadsheet = SpreadsheetApp.getActiveSpreadsheet();\n" +
                "  var sheet = spreadsheet.getSheetByName(\"sheet_name_holder\");\n" +
                "  var dataRange = sheet.getDataRange();\n" +
                "  var data = dataRange.getValues();\n" +
                "\n" +
                "   var url = 'https://qaapi.engati.com/bot-api/v2.0/customer/customer_id_holder/bot/bot_key_holder/flow/flow_key_holder';  \n" +
                "\n" +
                "  var auth = \"Basic auth_token_holder\";\n" +
                "  var headers = {\n" +
                "    'Content-Type': 'application/json',\n" +
                "    'Authorization': auth\n" +
                "  };\n" +
                "  \n" +
                "  var mapped_items = mapped_items_holder;\n" +
                "\n" +
                "  var dataMapping = {};\n" +
                "  for(var i = 0; i < data[0].length; i++) {\n" +
                "    if(data[0][i] == \"Status\" || data[0][i] == \"Status Updated On\") {\n" +
                "      continue;\n" +
                "    }\n" +
                "    dataMapping[data[0][i]] = i;\n" +
                "  }\n" +
                "  \n" +
                "  var showStatus = showStatus_holder;\n" +
                "  if(showStatus) {\n" +
                "    var length = data[0].length;\n" +
                "    if(data[0][length - 1] != \"Status Updated On\") {\n" +
                "      var lastColumn = sheet.getLastColumn();\n" +
                "      sheet.insertColumnAfter(lastColumn);\n" +
                "      sheet.getRange(1, lastColumn + 1).setValue('Status');\n" +
                "      lastColumn = sheet.getLastColumn();\n" +
                "      sheet.insertColumnAfter(lastColumn);\n" +
                "      sheet.getRange(1, lastColumn + 1).setValue('Status Updated On');\n" +
                "      dataRange = sheet.getDataRange();\n" +
                "      data = dataRange.getValues();\n" +
                "    }\n" +
                "  }\n" +
                "  \n" +
                "  var date = new Date();\n" +
                "  var formattedTime = Utilities.formatDate(date, Session.getScriptTimeZone(), 'yyyy-MM-dd HH:mm:ss');\n" +
                "\n" +
                "  var numberIndex = dataMapping[mapped_items[\"user.phone_no\"]];\n" +
                "  var statusIndex = data[0].length - 2;\n" +
                "  var statusTime = data[0].length - 1;\n" +
                "  \n" +
                "  var i = 1;\n" +
                "\n" +
                "  while(i < data.length) {\n" +
                "    if(!isRowComplete(sheet, i)) {\n" +
                "      break;\n" +
                "    }\n" +
                "    var number = data[i][numberIndex].toString();\n" +
                "    \n" +
                "    if(!checkValidNumber(number)) {\n" +
                "      if(showStatus) {\n" +
                "        data[i][statusIndex] = \"Enter valid mobile number\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "        dataRange.setValues(data);\n" +
                "      }\n" +
                "      i++;\n" +
                "      continue;\n" +
                "    }\n" +
                "\n" +
                "    var userNumber = number;\n" +
                "    if(number.length == 10) {\n" +
                "      userNumber = \"91\" + number;\n" +
                "    }\n" +
                "\n" +
                "    var payload = {};\n" +
                "  \n" +
                "    for(var key in mapped_items) {\n" +
                "      var value = mapped_items[key];\n" +
                "      if(value == mapped_items[\"user.phone_no\"]) {\n" +
                "        payload[key] = userNumber;\n" +
                "      }\n" +
                "      else {\n" +
                "        var index = dataMapping[value];\n" +
                "        payload[key] = data[i][index];\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    var payloadJson = JSON.stringify(payload);\n" +
                "\n" +
                "    var options = {\n" +
                "      method: 'post',\n" +
                "      headers: headers,\n" +
                "      payload: payloadJson\n" +
                "    };\n" +
                "\n" +
                "    var response = UrlFetchApp.fetch(url, options);\n" +
                "    Logger.log(response.getContentText());\n" +
                "\n" +
                "    var result = response.getResponseCode();\n" +
                "\n" +
                "    if(showStatus) {\n" +
                "      if(result == 200) {\n" +
                "        data[i][statusIndex] = \"Success\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "      }\n" +
                "      else {\n" +
                "        data[i][statusIndex] = \"Failed\";\n" +
                "        data[i][statusTime] = formattedTime;\n" +
                "      }\n" +
                "      dataRange.setValues(data);\n" +
                "    }\n" +
                "    i++;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n";


        data = data.replaceAll("customer_id_holder", customer_id);
        data = data.replaceAll("bot_key_holder", bot_key);
        data = data.replaceAll("auth_token_holder", auth_token);
        data = data.replaceAll("flow_key_holder", flow_key);
        data = data.replaceAll("sheet_name_holder", sheet_name);
        data = data.replaceAll("showStatus_holder", String.valueOf(showStatus));
        data = data.replaceAll("mapped_items_holder", mapped_items);
        //  System.out.println(data);
        return data;
    }
}

