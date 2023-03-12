package ru.lds.telegram.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.lds.telegram.enums.AssetEnum;
import ru.lds.telegram.enums.ExchangeEnum;
import ru.lds.telegram.enums.PaymentSystemEnum;
import ru.lds.telegram.enums.TradeTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramButton {

    public ReplyKeyboardMarkup getMainMenu() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Узнать цену " + EmojiParser.parseToUnicode("💵"));
        rowFirst.add("Подписка на цену " + EmojiParser.parseToUnicode("⏰"));

        var rowSecond = new KeyboardRow();
        rowSecond.add("Настройки профиля " + EmojiParser.parseToUnicode("📝"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(rowFirst);
        keyboardRows.add(rowSecond);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(Boolean.TRUE);

        return keyboardMarkup;
    }

    public InlineKeyboardMarkup getKeyBoardPaymentSystem() {
        var myPaymentSystemButton = new InlineKeyboardButton();
        myPaymentSystemButton.setText(PaymentSystemEnum.MY_PAYMENT_SYSTEM.getName());
        myPaymentSystemButton.setCallbackData(PaymentSystemEnum.MY_PAYMENT_SYSTEM.name());

        var tinkoffButton = new InlineKeyboardButton();
        tinkoffButton.setText(PaymentSystemEnum.TINKOFF.getName());
        tinkoffButton.setCallbackData(PaymentSystemEnum.TINKOFF.name());

        var raifButton = new InlineKeyboardButton();
        raifButton.setText(PaymentSystemEnum.RAIFFEISENBANK.getName());
        raifButton.setCallbackData(PaymentSystemEnum.RAIFFEISENBANK.name());

        var rosbankButton = new InlineKeyboardButton();
        rosbankButton.setText(PaymentSystemEnum.ROSBANK.getName());
        rosbankButton.setCallbackData(PaymentSystemEnum.ROSBANK.name());

        var payeerButton = new InlineKeyboardButton();
        payeerButton.setText(PaymentSystemEnum.PAYEER.getName());
        payeerButton.setCallbackData(PaymentSystemEnum.PAYEER.name());

        var qiwiButton = new InlineKeyboardButton();
        qiwiButton.setText(PaymentSystemEnum.QIWI.getName());
        qiwiButton.setCallbackData(PaymentSystemEnum.QIWI.name());

        var anyButton = new InlineKeyboardButton();
        anyButton.setText(PaymentSystemEnum.ANY.getName());
        anyButton.setCallbackData(PaymentSystemEnum.ANY.name());

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
        buyButton.setText(TradeTypeEnum.BUY.getName() + EmojiParser.parseToUnicode(" ❌"));
        buyButton.setCallbackData(TradeTypeEnum.BUY.name());

        var sellButton = new InlineKeyboardButton();
        sellButton.setText(TradeTypeEnum.SELL.getName() + EmojiParser.parseToUnicode(" ✅"));
        sellButton.setCallbackData(TradeTypeEnum.SELL.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(sellButton, buyButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardExchange() {
        var myExchangeButton = new InlineKeyboardButton();
        myExchangeButton.setText(ExchangeEnum.MY_EXCHANGE.getName());
        myExchangeButton.setCallbackData(ExchangeEnum.MY_EXCHANGE.getName());

        var binanceButton = new InlineKeyboardButton();
        binanceButton.setText(ExchangeEnum.BINANCE.getName());
        binanceButton.setCallbackData(ExchangeEnum.BINANCE.getName());

        var huobiButton = new InlineKeyboardButton();
        huobiButton.setText(ExchangeEnum.HUOBI.getName());
        huobiButton.setCallbackData(ExchangeEnum.HUOBI.getName());

        var anyButton = new InlineKeyboardButton();
        anyButton.setText(ExchangeEnum.ANY.getName());
        anyButton.setCallbackData(ExchangeEnum.ANY.getName());

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
        usdtButton.setText(AssetEnum.USDT.name());
        usdtButton.setCallbackData(AssetEnum.USDT.name());

        var btcButton = new InlineKeyboardButton();
        btcButton.setText(AssetEnum.BTC.name());
        btcButton.setCallbackData(AssetEnum.BTC.name());

        var ethButton = new InlineKeyboardButton();
        ethButton.setText(AssetEnum.ETH.name());
        ethButton.setCallbackData(AssetEnum.ETH.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(usdtButton, ethButton, btcButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public ReplyKeyboardMarkup addKeyBoardSubscribe() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Добавить подписку " + EmojiParser.parseToUnicode("✅"));
        rowFirst.add("Мои подписки " + EmojiParser.parseToUnicode("⏰"));

        var rowSecond = new KeyboardRow();
        rowSecond.add("Удалить все подписки " + EmojiParser.parseToUnicode("❌❌❌"));

        return addWithReturnMainMenu(rowFirst, rowSecond);
    }

    public ReplyKeyboardMarkup getProfileMenu() {
        var rowFirst = new KeyboardRow();
        rowFirst.add("Мои биржи " + EmojiParser.parseToUnicode("⭐"));
        rowFirst.add("Мои платежные системы " + EmojiParser.parseToUnicode("💳"));

        var rowSecond= new KeyboardRow();
        rowSecond.add("Мои лимиты " + EmojiParser.parseToUnicode("💰"));

        return addWithReturnMainMenu(rowFirst, rowSecond);
    }

    private ReplyKeyboardMarkup addWithReturnMainMenu(KeyboardRow rowFirst, KeyboardRow rowSecond) {
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
}
