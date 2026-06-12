package utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FormatUtil {
    public static String formatAngka(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("id-ID"));
        DecimalFormat df = new DecimalFormat("#,###.##", symbols);
        return df.format(nominal);
    }
}
