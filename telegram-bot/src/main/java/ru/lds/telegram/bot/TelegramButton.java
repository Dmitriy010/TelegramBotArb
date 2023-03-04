package ru.lds.telegram.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.lds.telegram.enums.Asset;
import ru.lds.telegram.enums.Exchange;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.TradeType;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramButton {

    public InlineKeyboardMarkup getKeyBoardPaymentSystem() {
        var myPaymentSystemButton = new InlineKeyboardButton();
        myPaymentSystemButton.setText(PaymentSystem.MY_PAYMENT_SYSTEM.getName());
        myPaymentSystemButton.setCallbackData(PaymentSystem.MY_PAYMENT_SYSTEM.name());

        var tinkoffButton = new InlineKeyboardButton();
        tinkoffButton.setText(PaymentSystem.TINKOFF.getName());
        tinkoffButton.setCallbackData(PaymentSystem.TINKOFF.name());

        var raifButton = new InlineKeyboardButton();
        raifButton.setText(PaymentSystem.RAIFFEISENBANK.getName());
        raifButton.setCallbackData(PaymentSystem.RAIFFEISENBANK.name());

        var rosbankButton = new InlineKeyboardButton();
        rosbankButton.setText(PaymentSystem.ROSBANK.getName());
        rosbankButton.setCallbackData(PaymentSystem.ROSBANK.name());

        var payeerButton = new InlineKeyboardButton();
        payeerButton.setText(PaymentSystem.PAYEER.getName());
        payeerButton.setCallbackData(PaymentSystem.PAYEER.name());

        var qiwiButton = new InlineKeyboardButton();
        qiwiButton.setText(PaymentSystem.QIWI.getName());
        qiwiButton.setCallbackData(PaymentSystem.QIWI.name());

        var anyButton = new InlineKeyboardButton();
        anyButton.setText(PaymentSystem.ANY.getName());
        anyButton.setCallbackData(PaymentSystem.ANY.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(myPaymentSystemButton));
        rowsInLine.add(List.of(tinkoffButton, raifButton, payeerButton));
        rowsInLine.add(List.of(rosbankButton, qiwiButton));
        rowsInLine.add(List.of(anyButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardTradeType() {
        var buyButton = new InlineKeyboardButton();
        buyButton.setText(TradeType.BUY.getName() + EmojiParser.parseToUnicode(" ❌"));
        buyButton.setCallbackData(TradeType.BUY.name());

        var sellButton = new InlineKeyboardButton();
        sellButton.setText(TradeType.SELL.getName() + EmojiParser.parseToUnicode(" ✅"));
        sellButton.setCallbackData(TradeType.SELL.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(sellButton, buyButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardExchange() {
        var myExchangeButton = new InlineKeyboardButton();
        myExchangeButton.setText(Exchange.MY_EXCHANGE.getName());
        myExchangeButton.setCallbackData(Exchange.MY_EXCHANGE.getName());

        var binanceButton = new InlineKeyboardButton();
        binanceButton.setText(Exchange.BINANCE.getName());
        binanceButton.setCallbackData(Exchange.BINANCE.getName());

        var huobiButton = new InlineKeyboardButton();
        huobiButton.setText(Exchange.HUOBI.getName());
        huobiButton.setCallbackData(Exchange.HUOBI.getName());

        var anyButton = new InlineKeyboardButton();
        anyButton.setText(Exchange.ANY.getName());
        anyButton.setCallbackData(Exchange.ANY.getName());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(myExchangeButton));
        rowsInLine.add(List.of(binanceButton, huobiButton));
        rowsInLine.add(List.of(anyButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardAssetType() {
        var usdtButton = new InlineKeyboardButton();
        usdtButton.setText(Asset.USDT.name());
        usdtButton.setCallbackData(Asset.USDT.name());

        var btcButton = new InlineKeyboardButton();
        btcButton.setText(Asset.BTC.name());
        btcButton.setCallbackData(Asset.BTC.name());

        var ethButton = new InlineKeyboardButton();
        ethButton.setText(Asset.ETH.name());
        ethButton.setCallbackData(Asset.ETH.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(usdtButton, ethButton, btcButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public ReplyKeyboardMarkup addKeyBoardExchange() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Доступные биржи " + EmojiParser.parseToUnicode("✨"));
        rowFirst.add("Мои биржи " + EmojiParser.parseToUnicode("⭐"));

        var rowSecond = new KeyboardRow();
        rowSecond.add("Добавить биржу " + EmojiParser.parseToUnicode("✅"));
        rowSecond.add("Удалить биржу " + EmojiParser.parseToUnicode("❌"));

        var rowThird = new KeyboardRow();
        rowThird.add("Удалить все биржи " + EmojiParser.parseToUnicode("❌❌❌"));

        var rowFourth = new KeyboardRow();
        rowFourth.add("Основное меню " + EmojiParser.parseToUnicode("⏪"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowFirst);
        keyboardRows.add(rowSecond);
        keyboardRows.add(rowThird);
        keyboardRows.add(rowFourth);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(Boolean.TRUE);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup addKeyBoardPaymentSystem() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Доступные платежные системы " + EmojiParser.parseToUnicode("💸"));
        rowFirst.add("Мои платежные системы " + EmojiParser.parseToUnicode("💳"));

        var rowSecond = new KeyboardRow();
        rowSecond.add("Добавить платежную систему " + EmojiParser.parseToUnicode("✅"));
        rowSecond.add("Удалить платежную систему " + EmojiParser.parseToUnicode("❌"));

        var rowThird = new KeyboardRow();
        rowThird.add("Удалить все платежные системы " + EmojiParser.parseToUnicode("❌❌❌"));

        var rowFourth = new KeyboardRow();
        rowFourth.add("Основное меню " + EmojiParser.parseToUnicode("⏪"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowFirst);
        keyboardRows.add(rowSecond);
        keyboardRows.add(rowThird);
        keyboardRows.add(rowFourth);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(Boolean.TRUE);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup addKeyBoardsubscribe() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Мои подписки " + EmojiParser.parseToUnicode("📋"));

        var rowSecond = new KeyboardRow();
        rowSecond.add("Подписаться на цену " + EmojiParser.parseToUnicode("⏰"));
        rowSecond.add("Удалить подписку " + EmojiParser.parseToUnicode("❌"));

        var rowThird = new KeyboardRow();
        rowThird.add("Основное меню " + EmojiParser.parseToUnicode("⏪"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowFirst);
        keyboardRows.add(rowSecond);
        keyboardRows.add(rowThird);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(Boolean.TRUE);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup getMainMenu() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Узнать цену " + EmojiParser.parseToUnicode("💵"));
        rowFirst.add("Подписки " + EmojiParser.parseToUnicode("⏰"));

        var rowThird = new KeyboardRow();
        rowThird.add("Биржи " + EmojiParser.parseToUnicode("⭐"));
        rowThird.add("Платежные системы " + EmojiParser.parseToUnicode("💳"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowFirst);
        keyboardRows.add(rowThird);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(Boolean.TRUE);

        return keyboardMarkup;
    }
}
