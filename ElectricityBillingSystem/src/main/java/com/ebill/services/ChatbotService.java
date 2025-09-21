package com.ebill.services;

import com.ebill.models.Bill;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatbotService {
    private final LocalizationService loc = LocalizationService.getInstance();
    private final DataService dataService = DataService.getInstance();

    private Bill lastQueriedBill = null;

    private enum Intent {
        GET_BILL_INFO,
        CHECK_PAYMENT_STATUS,
        GET_UNITS_CONSUMED,
        GREETING,
        HELP,
        UNKNOWN
    }

    public String getResponse(String userInput) {
        String input = userInput.toLowerCase().trim();
        
        Intent intent = determineIntent(input);
        Optional<Integer> meterNumberOpt = extractMeterNumber(input);

        if (meterNumberOpt.isPresent()) {
            Optional<Bill> billOpt = dataService.findBillByMeterNumber(meterNumberOpt.get());
            if (billOpt.isPresent()) {
                lastQueriedBill = billOpt.get();
            } else {
                lastQueriedBill = null;
                return loc.getString("chatbot.meter_not_found").replace("{meterNumber}", String.valueOf(meterNumberOpt.get()));
            }
        }

        switch (intent) {
            case GREETING:
                return loc.getString("chatbot.greeting");

            case HELP:
                return loc.getString("chatbot.help");

            case GET_BILL_INFO:
                if (lastQueriedBill != null) {
                    return formatResponse("chatbot.bill_info", lastQueriedBill);
                } else {
                    return loc.getString("chatbot.ask_for_meter");
                }

            case CHECK_PAYMENT_STATUS:
                if (lastQueriedBill != null) {
                    return formatResponse("chatbot.payment_status", lastQueriedBill);
                } else {
                    return loc.getString("chatbot.ask_for_meter");
                }
                
            case GET_UNITS_CONSUMED:
                 if (lastQueriedBill != null) {
                    return formatResponse("chatbot.units_info", lastQueriedBill);
                } else {
                    return loc.getString("chatbot.ask_for_meter");
                }

            case UNKNOWN:
            default:
                if (lastQueriedBill != null) {
                    return formatResponse("chatbot.bill_info", lastQueriedBill);
                }
                return loc.getString("chatbot.fallback");
        }
    }

    private Intent determineIntent(String input) {
        if (input.contains("hello") || input.contains("hi")) return Intent.GREETING;
        if (input.contains("help")) return Intent.HELP;
        if (input.contains("status") || input.contains("paid")) return Intent.CHECK_PAYMENT_STATUS;
        if (input.contains("unit") || input.contains("consumed")) return Intent.GET_UNITS_CONSUMED;
        if (input.contains("bill") || input.contains("amount") || input.matches(".*\\d{4,}.*")) return Intent.GET_BILL_INFO;
        return Intent.UNKNOWN;
    }

    private Optional<Integer> extractMeterNumber(String input) {
        Pattern pattern = Pattern.compile("\\d{4,}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            try {
                return Optional.of(Integer.parseInt(matcher.group()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private String formatResponse(String responseKey, Bill bill) {
        String template = loc.getString(responseKey);
        return template
                .replace("{customerName}", bill.getCustomerName())
                .replace("{meterNumber}", String.valueOf(bill.getMeterNumber()))
                .replace("{month}", bill.getBillingMonth())
                .replace("{amount}", String.format("%.2f", bill.getBillAmount()))
                .replace("{status}", bill.getPaymentStatus())
                .replace("{units}", String.valueOf(bill.getUnitsConsumed()));
    }
}