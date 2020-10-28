package tech.cassandre.trading.bot.dto.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Currency.
 */
@SuppressWarnings("unused")
public final class CurrencyDTO {

    /** List of currencies. */
    private static final Map<String, CurrencyDTO> CURRENCIES = new HashMap<>();

    /** United Arab Emirates Dirham. */
    public static final CurrencyDTO AED = createCurrency("AED", "United Arab Emirates Dirham", null);

    /** Afghan Afghani. */
    public static final CurrencyDTO AFN = createCurrency("AFN", "Afghan Afghani", null);

    /** Albanian Lek. */
    public static final CurrencyDTO ALL = createCurrency("ALL", "Albanian Lek", null);

    /** Armenian Dram. */
    public static final CurrencyDTO AMD = createCurrency("AMD", "Armenian Dram", null);

    /** Anoncoin. */
    public static final CurrencyDTO ANC = createCurrency("ANC", "Anoncoin", null);

    /** Netherlands Antillean Guilder. */
    public static final CurrencyDTO ANG = createCurrency("ANG", "Netherlands Antillean Guilder", null);

    /** Angolan Kwanza. */
    public static final CurrencyDTO AOA = createCurrency("AOA", "Angolan Kwanza", null);

    /** Aeron. */
    public static final CurrencyDTO ARN = createCurrency("ARN", "Aeron", null);

    /** Argentine Peso. */
    public static final CurrencyDTO ARS = createCurrency("ARS", "Argentine Peso", null);

    /** Cosmos. */
    public static final CurrencyDTO ATOM = createCurrency("ATOM", "Cosmos", null);

    /** Australian Dollar. */
    public static final CurrencyDTO AUD = createCurrency("AUD", "Australian Dollar", null);

    /** Auroracoin. */
    public static final CurrencyDTO AUR = createCurrency("AUR", "Auroracoin", null);

    /** Aventus. */
    public static final CurrencyDTO AVT = createCurrency("AVT", "Aventus", null);

    /** Aruban Florin. */
    public static final CurrencyDTO AWG = createCurrency("AWG", "Aruban Florin", null);

    /** Azerbaijani Manat. */
    public static final CurrencyDTO AZN = createCurrency("AZN", "Azerbaijani Manat", null);

    /** Bosnia-Herzegovina Convertible Mark. */
    public static final CurrencyDTO BAM = createCurrency("BAM", "Bosnia-Herzegovina Convertible Mark", null);

    /** Basic Attention Token. */
    public static final CurrencyDTO BAT = createCurrency("BAT", "Basic Attention Token", null);

    /** Barbadian Dollar. */
    public static final CurrencyDTO BBD = createCurrency("BBD", "Barbadian Dollar", null);

    /** BlackCoin. */
    public static final CurrencyDTO BC = createCurrency("BC", "BlackCoin", null, "BLK");

    /** BitConnect. */
    public static final CurrencyDTO BCC = createCurrency("BCC", "BitConnect", null);

    /** BitcoinCash. */
    public static final CurrencyDTO BCH = createCurrency("BCH", "BitcoinCash", null);

    /** BitcoinAtom. */
    public static final CurrencyDTO BCA = createCurrency("BCA", "BitcoinAtom", null);

    /** BLK. */
    public static final CurrencyDTO BLK = getInstance("BLK");

    /** Bangladeshi Taka. */
    public static final CurrencyDTO BDT = createCurrency("BDT", "Bangladeshi Taka", null);

    /** Aten 'Black Gold' Coin. */
    public static final CurrencyDTO BGC = createCurrency("BGC", "Aten 'Black Gold' Coin", null);

    /** Bulgarian Lev. */
    public static final CurrencyDTO BGN = createCurrency("BGN", "Bulgarian Lev", null);

    /** Bahraini Dinar. */
    public static final CurrencyDTO BHD = createCurrency("BHD", "Bahraini Dinar", null);

    /** Bahraini Dinar. */
    public static final CurrencyDTO BIF = createCurrency("BIF", "Burundian Franc", null);

    /** Bermudan Dollar. */
    public static final CurrencyDTO BMD = createCurrency("BMD", "Bermudan Dollar", null);

    /** Brunei Dollar. */
    public static final CurrencyDTO BND = createCurrency("BND", "Brunei Dollar", null);

    /** Bolivian Boliviano. */
    public static final CurrencyDTO BOB = createCurrency("BOB", "Bolivian Boliviano", null);

    /** Brazilian Real. */
    public static final CurrencyDTO BRL = createCurrency("BRL", "Brazilian Real", "R$");

    /** Bahamian Dollar. */
    public static final CurrencyDTO BSD = createCurrency("BSD", "Bahamian Dollar", null);

    /** Bitcoin. */
    public static final CurrencyDTO BTC = createCurrency("BTC", "Bitcoin", null, "XBT");

    /** Bitcoin Gold. */
    public static final CurrencyDTO BTG = createCurrency("BTG", "Bitcoin Gold", null);

    /** XBT. */
    public static final CurrencyDTO XBT = getInstance("XBT");

    /** Bhutanese Ngultrum. */
    public static final CurrencyDTO BTN = createCurrency("BTN", "Bhutanese Ngultrum", null);

    /** Botswanan Pula. */
    public static final CurrencyDTO BWP = createCurrency("BWP", "Botswanan Pula", null);

    /** Belarusian Ruble. */
    public static final CurrencyDTO BYR = createCurrency("BYR", "Belarusian Ruble", null);

    /** Belize Dollar. */
    public static final CurrencyDTO BZD = createCurrency("BZD", "Belize Dollar", null);

    /** Canadian Dollar. */
    public static final CurrencyDTO CAD = createCurrency("CAD", "Canadian Dollar", null);

    /** Congolese Franc. */
    public static final CurrencyDTO CDF = createCurrency("CDF", "Congolese Franc", null);

    /** Swiss Franc. */
    public static final CurrencyDTO CHF = createCurrency("CHF", "Swiss Franc", null);

    /** Chilean Unit of Account (UF). */
    public static final CurrencyDTO CLF = createCurrency("CLF", "Chilean Unit of Account (UF)", null);

    /** Chilean Peso. */
    public static final CurrencyDTO CLP = createCurrency("CLP", "Chilean Peso", null);

    /** Chinacoin. */
    public static final CurrencyDTO CNC = createCurrency("CNC", "Chinacoin", null);

    /** Chinese Yuan. */
    public static final CurrencyDTO CNY = createCurrency("CNY", "Chinese Yuan", null);

    /** Colombian Peso. */
    public static final CurrencyDTO COP = createCurrency("COP", "Colombian Peso", null);

    /** Costa Rican Colón. */
    public static final CurrencyDTO CRC = createCurrency("CRC", "Costa Rican Colón", null);

    /** Cuban Peso. */
    public static final CurrencyDTO CUP = createCurrency("CUP", "Cuban Peso", null);

    /** Cape Verdean Escudo. */
    public static final CurrencyDTO CVE = createCurrency("CVE", "Cape Verdean Escudo", null);

    /** Czech Republic Koruna. */
    public static final CurrencyDTO CZK = createCurrency("CZK", "Czech Republic Koruna", null);

    /** Dash. */
    public static final CurrencyDTO DASH = createCurrency("DASH", "Dash", null);

    /** Decred. */
    public static final CurrencyDTO DCR = createCurrency("DCR", "Decred", null);

    /** DigiByte. */
    public static final CurrencyDTO DGB = createCurrency("DGB", "DigiByte", null);

    /** Djiboutian Franc. */
    public static final CurrencyDTO DJF = createCurrency("DJF", "Djiboutian Franc", null);

    /** Danish Krone. */
    public static final CurrencyDTO DKK = createCurrency("DKK", "Danish Krone", null);

    /** Dogecoin. */
    public static final CurrencyDTO DOGE = createCurrency("DOGE", "Dogecoin", null, "XDC", "XDG");

    /** XDC. */
    public static final CurrencyDTO XDC = getInstance("XDC");

    /** XDG. */
    public static final CurrencyDTO XDG = getInstance("XDG");

    /** Dominican Peso. */
    public static final CurrencyDTO DOP = createCurrency("DOP", "Dominican Peso", null);

    /** Digitalcoin. */
    public static final CurrencyDTO DGC = createCurrency("DGC", "Digitalcoin", null);

    /** Devcoin. */
    public static final CurrencyDTO DVC = createCurrency("DVC", "Devcoin", null);

    /** Darkcoin. */
    public static final CurrencyDTO DRK = createCurrency("DRK", "Darkcoin", null);

    /** Algerian Dinar. */
    public static final CurrencyDTO DZD = createCurrency("DZD", "Algerian Dinar", null);

    /** Eidoo. */
    public static final CurrencyDTO EDO = createCurrency("EDO", "Eidoo", null);

    /** Estonian Kroon. */
    public static final CurrencyDTO EEK = createCurrency("EEK", "Estonian Kroon", null);

    /** Egoldcoin. */
    public static final CurrencyDTO EGD = createCurrency("EGD", "Egoldcoin", null);

    /** Egyptian Pound. */
    public static final CurrencyDTO EGP = createCurrency("EGP", "Egyptian Pound", null);

    /** EOS. */
    public static final CurrencyDTO EOS = createCurrency("EOS", "EOS", null);

    /** Ethiopian Birr. */
    public static final CurrencyDTO ETB = createCurrency("ETB", "Ethiopian Birr", null);

    /** Ether Classic. */
    public static final CurrencyDTO ETC = createCurrency("ETC", "Ether Classic", null);

    /** Ether. */
    public static final CurrencyDTO ETH = createCurrency("ETH", "Ether", null);

    /** Euro. */
    public static final CurrencyDTO EUR = createCurrency("EUR", "Euro", null);

    /** Fijian Dollar. */
    public static final CurrencyDTO FJD = createCurrency("FJD", "Fijian Dollar", null);

    /** First Blood. */
    @SuppressWarnings("checkstyle:ConstantName")
    public static final CurrencyDTO _1ST = createCurrency("1ST", "First Blood", null);

    /** Falkland Islands Pound. */
    public static final CurrencyDTO FKP = createCurrency("FKP", "Falkland Islands Pound", null);

    /** Feathercoin. */
    public static final CurrencyDTO FTC = createCurrency("FTC", "Feathercoin", null);

    /** British Pound Sterling. */
    public static final CurrencyDTO GBP = createCurrency("GBP", "British Pound Sterling", null);

    /** Georgian Lari. */
    public static final CurrencyDTO GEL = createCurrency("GEL", "Georgian Lari", null);

    /** Ghanaian Cedi. */
    public static final CurrencyDTO GHS = createCurrency("GHS", "Ghanaian Cedi", null);

    /** Gigahashes per second. */
    @SuppressWarnings("checkstyle:ConstantName")
    public static final CurrencyDTO GHs = createCurrency("GHS", "Gigahashes per second", null);

    /** Gibraltar Pound. */
    public static final CurrencyDTO GIP = createCurrency("GIP", "Gibraltar Pound", null);

    /** Gambian Dalasi. */
    public static final CurrencyDTO GMD = createCurrency("GMD", "Gambian Dalasi", null);

    /** Guinean Franc. */
    public static final CurrencyDTO GNF = createCurrency("GNF", "Guinean Franc", null);

    /** Gnosis. */
    public static final CurrencyDTO GNO = createCurrency("GNO", "Gnosis", null);

    /** Golem. */
    public static final CurrencyDTO GNT = createCurrency("GNT", "Golem", null);

    /** Guatemalan Quetzal. */
    public static final CurrencyDTO GTQ = createCurrency("GTQ", "Guatemalan Quetzal", null);

    /** Genesis Vision. */
    public static final CurrencyDTO GVT = createCurrency("GVT", "Genesis Vision", null);

    /** Guyanaese Dollar. */
    public static final CurrencyDTO GYD = createCurrency("GYD", "Guyanaese Dollar", null);

    /** Hong Kong Dollar. */
    public static final CurrencyDTO HKD = createCurrency("HKD", "Hong Kong Dollar", null);

    /** Hive. */
    public static final CurrencyDTO HVN = createCurrency("HVN", "Hive", null);

    /** Honduran Lempira. */
    public static final CurrencyDTO HNL = createCurrency("HNL", "Honduran Lempira", null);

    /** Croatian Kuna. */
    public static final CurrencyDTO HRK = createCurrency("HRK", "Croatian Kuna", null);

    /** Haitian Gourde. */
    public static final CurrencyDTO HTG = createCurrency("HTG", "Haitian Gourde", null);

    /** Hungarian Forint. */
    public static final CurrencyDTO HUF = createCurrency("HUF", "Hungarian Forint", null);

    /** Iconomi. */
    public static final CurrencyDTO ICN = createCurrency("ICN", "Iconomi", null);

    /** Indonesian Rupiah. */
    public static final CurrencyDTO IDR = createCurrency("IDR", "Indonesian Rupiah", null);

    /** Israeli New Sheqel. */
    public static final CurrencyDTO ILS = createCurrency("ILS", "Israeli New Sheqel", null);

    /** Indian Rupee. */
    public static final CurrencyDTO INR = createCurrency("INR", "Indian Rupee", null);

    /** I/OCoin. */
    public static final CurrencyDTO IOC = createCurrency("IOC", "I/OCoin", null);

    /** IOTA. */
    public static final CurrencyDTO IOT = createCurrency("IOT", "IOTA", null);

    /** Iraqi Dinar. */
    public static final CurrencyDTO IQD = createCurrency("IQD", "Iraqi Dinar", null);

    /** Iranian Rial. */
    public static final CurrencyDTO IRR = createCurrency("IRR", "Iranian Rial", null);

    /** Icelandic Króna. */
    public static final CurrencyDTO ISK = createCurrency("ISK", "Icelandic Króna", null);

    /** iXcoin. */
    public static final CurrencyDTO IXC = createCurrency("IXC", "iXcoin", null);

    /** Jersey Pound. */
    public static final CurrencyDTO JEP = createCurrency("JEP", "Jersey Pound", null);

    /** Jamaican Dollar. */
    public static final CurrencyDTO JMD = createCurrency("JMD", "Jamaican Dollar", null);

    /** Jordanian Dinar. */
    public static final CurrencyDTO JOD = createCurrency("JOD", "Jordanian Dinar", null);

    /** Japanese Yen. */
    public static final CurrencyDTO JPY = createCurrency("JPY", "Japanese Yen", null);

    /** KuCoin Shares. */
    public static final CurrencyDTO KCS = createCurrency("KCS", "KuCoin Shares", null);

    /** Kenyan Shilling. */
    public static final CurrencyDTO KES = createCurrency("KES", "Kenyan Shilling", null);

    /** Kyrgystani Som. */
    public static final CurrencyDTO KGS = createCurrency("KGS", "Kyrgystani Som", null);

    /** Cambodian Riel. */
    public static final CurrencyDTO KHR = createCurrency("KHR", "Cambodian Riel", null);

    /** KickCoin. */
    public static final CurrencyDTO KICK = createCurrency("KICK", "KickCoin", null);

    /** Comorian Franc. */
    public static final CurrencyDTO KMF = createCurrency("KMF", "Comorian Franc", null);

    /** North Korean Won. */
    public static final CurrencyDTO KPW = createCurrency("KPW", "North Korean Won", null);

    /** South Korean Won. */
    public static final CurrencyDTO KRW = createCurrency("KRW", "South Korean Won", null);

    /** Kuwaiti Dinar. */
    public static final CurrencyDTO KWD = createCurrency("KWD", "Kuwaiti Dinar", null);

    /** Cayman Islands Dollar. */
    public static final CurrencyDTO KYD = createCurrency("KYD", "Cayman Islands Dollar", null);

    /** Kazakhstani Tenge. */
    public static final CurrencyDTO KZT = createCurrency("KZT", "Kazakhstani Tenge", null);

    /** Laotian Kip. */
    public static final CurrencyDTO LAK = createCurrency("LAK", "Laotian Kip", null);

    /** Lebanese Pound. */
    public static final CurrencyDTO LBP = createCurrency("LBP", "Lebanese Pound", null);

    /** Lebanese Pound. */
    public static final CurrencyDTO LSK = createCurrency("LSK", "Lisk", null);

    /** Sri Lankan Rupee. */
    public static final CurrencyDTO LKR = createCurrency("LKR", "Sri Lankan Rupee", null);

    /** Liberian Dolla. */
    public static final CurrencyDTO LRD = createCurrency("LRD", "Liberian Dollar", null);

    /** Lesotho Loti. */
    public static final CurrencyDTO LSL = createCurrency("LSL", "Lesotho Loti", null);

    /** Litecoin. */
    public static final CurrencyDTO LTC = createCurrency("LTC", "Litecoin", null, "XLT");

    /** XLT. */
    public static final CurrencyDTO XLT = getInstance("XLT");

    /** Lithuanian Litas. */
    public static final CurrencyDTO LTL = createCurrency("LTL", "Lithuanian Litas", null);

    /** Latvian Lats. */
    public static final CurrencyDTO LVL = createCurrency("LVL", "Latvian Lats", null);

    /** Libyan Dinar. */
    public static final CurrencyDTO LYD = createCurrency("LYD", "Libyan Dinar", null);

    /** Moroccan Dirham. */
    public static final CurrencyDTO MAD = createCurrency("MAD", "Moroccan Dirham", null);

    /** Moldovan Leu. */
    public static final CurrencyDTO MDL = createCurrency("MDL", "Moldovan Leu", null);

    /** MegaCoin. */
    public static final CurrencyDTO MEC = createCurrency("MEC", "MegaCoin", null);

    /** Malagasy Ariary. */
    public static final CurrencyDTO MGA = createCurrency("MGA", "Malagasy Ariary", null);

    /** Macedonian Denar. */
    public static final CurrencyDTO MKD = createCurrency("MKD", "Macedonian Denar", null);

    /** Melonport. */
    public static final CurrencyDTO MLN = createCurrency("MLN", "Melonport", null);

    /** Myanma Kyat. */
    public static final CurrencyDTO MMK = createCurrency("MMK", "Myanma Kyat", null);

    /** Mongolian Tugrik. */
    public static final CurrencyDTO MNT = createCurrency("MNT", "Mongolian Tugrik", null);

    /** Macanese Pataca. */
    public static final CurrencyDTO MOP = createCurrency("MOP", "Macanese Pataca", null);

    /** Mauritanian Ouguiya. */
    public static final CurrencyDTO MRO = createCurrency("MRO", "Mauritanian Ouguiya", null);

    /** Mason Coin. */
    public static final CurrencyDTO MSC = createCurrency("MSC", "Mason Coin", null);

    /** Mauritian Rupee. */
    public static final CurrencyDTO MUR = createCurrency("MUR", "Mauritian Rupee", null);

    /** Maldivian Rufiyaa. */
    public static final CurrencyDTO MVR = createCurrency("MVR", "Maldivian Rufiyaa", null);

    /** Malawian Kwacha. */
    public static final CurrencyDTO MWK = createCurrency("MWK", "Malawian Kwacha", null);

    /** Mexican Peso. */
    public static final CurrencyDTO MXN = createCurrency("MXN", "Mexican Peso", null);

    /** Malaysian Ringgit. */
    public static final CurrencyDTO MYR = createCurrency("MYR", "Malaysian Ringgit", null);

    /** Mozambican Metical. */
    public static final CurrencyDTO MZN = createCurrency("MZN", "Mozambican Metical", null);

    /** Namibian Dollar. */
    public static final CurrencyDTO NAD = createCurrency("NAD", "Namibian Dollar", null);

    /** No BS Crypto. */
    public static final CurrencyDTO NOBS = createCurrency("NOBS", "No BS Crypto", null);

    /** NEO. */
    public static final CurrencyDTO NEO = createCurrency("NEO", "NEO", null);

    /** Nigerian Naira. */
    public static final CurrencyDTO NGN = createCurrency("NGN", "Nigerian Naira", null);

    /** Nicaraguan Córdoba. */
    public static final CurrencyDTO NIO = createCurrency("NIO", "Nicaraguan Córdoba", null);

    /** Namecoin. */
    public static final CurrencyDTO NMC = createCurrency("NMC", "Namecoin", null);

    /** Norwegian Krone. */
    public static final CurrencyDTO NOK = createCurrency("NOK", "Norwegian Krone", null);

    /** Nepalese Rupee. */
    public static final CurrencyDTO NPR = createCurrency("NPR", "Nepalese Rupee", null);

    /** Novacoin. */
    public static final CurrencyDTO NVC = createCurrency("NVC", "Novacoin", null);

    /** Nextcoin. */
    public static final CurrencyDTO NXT = createCurrency("NXT", "Nextcoin", null);

    /** New Zealand Dollar. */
    public static final CurrencyDTO NZD = createCurrency("NZD", "New Zealand Dollar", null);

    /** OmiseGO. */
    public static final CurrencyDTO OMG = createCurrency("OMG", "OmiseGO", null);

    /** Omani Rial. */
    public static final CurrencyDTO OMR = createCurrency("OMR", "Omani Rial", null);

    /** Panamanian Balboa. */
    public static final CurrencyDTO PAB = createCurrency("PAB", "Panamanian Balboa", null);

    /** Peruvian Nuevo Sol. */
    public static final CurrencyDTO PEN = createCurrency("PEN", "Peruvian Nuevo Sol", null);

    /** Papua New Guinean Kina. */
    public static final CurrencyDTO PGK = createCurrency("PGK", "Papua New Guinean Kina", null);

    /** Philippine Peso. */
    public static final CurrencyDTO PHP = createCurrency("PHP", "Philippine Peso", null);

    /** Pakistani Rupee. */
    public static final CurrencyDTO PKR = createCurrency("PKR", "Pakistani Rupee", null);

    /** Polish Zloty. */
    public static final CurrencyDTO PLN = createCurrency("PLN", "Polish Zloty", null);

    /** PotCoin. */
    public static final CurrencyDTO POT = createCurrency("POT", "PotCoin", null);

    /** Peercoin. */
    public static final CurrencyDTO PPC = createCurrency("PPC", "Peercoin", null);

    /** Paraguayan Guarani. */
    public static final CurrencyDTO PYG = createCurrency("PYG", "Paraguayan Guarani", null);

    /** Qatari Rial. */
    public static final CurrencyDTO QAR = createCurrency("QAR", "Qatari Rial", null);

    /** QuarkCoin. */
    public static final CurrencyDTO QRK = createCurrency("QRK", "QuarkCoin", null);

    /** Qtum. */
    public static final CurrencyDTO QTUM = createCurrency("QTUM", "Qtum", null);

    /** Augur. */
    public static final CurrencyDTO REP = createCurrency("REP", "Augur", null);

    /** Romanian Leu. */
    public static final CurrencyDTO RON = createCurrency("RON", "Romanian Leu", null);

    /** Serbian Dinar. */
    public static final CurrencyDTO RSD = createCurrency("RSD", "Serbian Dinar", null);

    /** Russian Ruble. */
    public static final CurrencyDTO RUB = createCurrency("RUB", "Russian Ruble", null);

    /** Old Russian Ruble. */
    public static final CurrencyDTO RUR = createCurrency("RUR", "Old Russian Ruble", null);

    /** Rwandan Franc. */
    public static final CurrencyDTO RWF = createCurrency("RWF", "Rwandan Franc", null);

    /** Saudi Riyal. */
    public static final CurrencyDTO SAR = createCurrency("SAR", "Saudi Riyal", null);

    /** Stablecoin. */
    public static final CurrencyDTO SBC = createCurrency("SBC", "Stablecoin", null);

    /** Solomon Islands Dollar. */
    public static final CurrencyDTO SBD = createCurrency("SBD", "Solomon Islands Dollar", null);

    /** Siacoin. */
    public static final CurrencyDTO SC = createCurrency("SC", "Siacoin", null);

    /** Seychellois Rupee. */
    public static final CurrencyDTO SCR = createCurrency("SCR", "Seychellois Rupee", null);

    /** Sudanese Pound. */
    public static final CurrencyDTO SDG = createCurrency("SDG", "Sudanese Pound", null);

    /** Swedish Krona. */
    public static final CurrencyDTO SEK = createCurrency("SEK", "Swedish Krona", null);

    /** Singapore Dollar. */
    public static final CurrencyDTO SGD = createCurrency("SGD", "Singapore Dollar", null);

    /** Saint Helena Pound. */
    public static final CurrencyDTO SHP = createCurrency("SHP", "Saint Helena Pound", null);

    /** Sierra Leonean Leone. */
    public static final CurrencyDTO SLL = createCurrency("SLL", "Sierra Leonean Leone", null);

    /** SmartCash. */
    public static final CurrencyDTO SMART = createCurrency("SMART", "SmartCash", null);

    /** Somali Shilling. */
    public static final CurrencyDTO SOS = createCurrency("SOS", "Somali Shilling", null);

    /** Surinamese Dollar. */
    public static final CurrencyDTO SRD = createCurrency("SRD", "Surinamese Dollar", null);

    /** Startcoin. */
    public static final CurrencyDTO START = createCurrency("START", "startcoin", null);

    /** Steem. */
    public static final CurrencyDTO STEEM = createCurrency("STEEM", "Steem", null);

    /** São Tomé and Príncipe Dobra. */
    public static final CurrencyDTO STD = createCurrency("STD", "São Tomé and Príncipe Dobra", null);

    /** Stellar. */
    public static final CurrencyDTO STR = createCurrency("STR", "Stellar", null);

    /** Stratis. */
    public static final CurrencyDTO STRAT = createCurrency("STRAT", "Stratis", null);

    /** Salvadoran Colón. */
    public static final CurrencyDTO SVC = createCurrency("SVC", "Salvadoran Colón", null);

    /** Syrian Pound. */
    public static final CurrencyDTO SYP = createCurrency("SYP", "Syrian Pound", null);

    /** Swazi Lilangeni. */
    public static final CurrencyDTO SZL = createCurrency("SZL", "Swazi Lilangeni", null);

    /** Thai Baht. */
    public static final CurrencyDTO THB = createCurrency("THB", "Thai Baht", null);

    /** Tajikistani Somoni. */
    public static final CurrencyDTO TJS = createCurrency("TJS", "Tajikistani Somoni", null);

    /** Turkmenistani Manat. */
    public static final CurrencyDTO TMT = createCurrency("TMT", "Turkmenistani Manat", null);

    /** Tunisian Dinar. */
    public static final CurrencyDTO TND = createCurrency("TND", "Tunisian Dinar", null);

    /** Tongan Paʻanga. */
    public static final CurrencyDTO TOP = createCurrency("TOP", "Tongan Paʻanga", null);

    /** Terracoin. */
    public static final CurrencyDTO TRC = createCurrency("TRC", "Terracoin", null);

    /** Turkish Lira",. */
    public static final CurrencyDTO TRY = createCurrency("TRY", "Turkish Lira", null);

    /** Trinidad and Tobago Dollar. */
    public static final CurrencyDTO TTD = createCurrency("TTD", "Trinidad and Tobago Dollar", null);

    /** New Taiwan Dollar. */
    public static final CurrencyDTO TWD = createCurrency("TWD", "New Taiwan Dollar", null);

    /** Tanzanian Shilling. */
    public static final CurrencyDTO TZS = createCurrency("TZS", "Tanzanian Shilling", null);

    /** Ukrainian Hryvnia. */
    public static final CurrencyDTO UAH = createCurrency("UAH", "Ukrainian Hryvnia", null);

    /** Ugandan Shilling. */
    public static final CurrencyDTO UGX = createCurrency("UGX", "Ugandan Shilling", null);

    /** United States Dollar. */
    public static final CurrencyDTO USD = createCurrency("USD", "United States Dollar", null);

    /** Tether USD Anchor. */
    public static final CurrencyDTO USDT = createCurrency("USDT", "Tether USD Anchor", null);

    /** Unitary Status Dollar eCoin. */
    public static final CurrencyDTO USDE = createCurrency("USDE", "Unitary Status Dollar eCoin", null);

    /** Ultracoin. */
    public static final CurrencyDTO UTC = createCurrency("UTC", "Ultracoin", null);

    /** Uruguayan Peso. */
    public static final CurrencyDTO UYU = createCurrency("UYU", "Uruguayan Peso", null);

    /** Uzbekistan Som. */
    public static final CurrencyDTO UZS = createCurrency("UZS", "Uzbekistan Som", null);

    /** Venezuelan Bolívar. */
    public static final CurrencyDTO VEF = createCurrency("VEF", "Venezuelan Bolívar", null);

    /** Hub Culture's Vet. */
    public static final CurrencyDTO VET = createCurrency("VET", "Hub Culture's Vet", null, "VEN");

    /** Hub Culture's Ven. */
    public static final CurrencyDTO VEN = createCurrency("VEN", "Hub Culture's Ven", null, "XVN");

    /** Tezos. */
    public static final CurrencyDTO XTZ = createCurrency("XTZ", "Tezos", null);

    /** XVN. */
    public static final CurrencyDTO XVN = getInstance("XVN");

    /** Viberate. */
    public static final CurrencyDTO VIB = createCurrency("VIB", "Viberate", null);

    /** Vietnamese Dong. */
    public static final CurrencyDTO VND = createCurrency("VND", "Vietnamese Dong", null);

    /** Vanuatu Vatu. */
    public static final CurrencyDTO VUV = createCurrency("VUV", "Vanuatu Vatu", null);

    /** WorldCoin. */
    public static final CurrencyDTO WDC = createCurrency("WDC", "WorldCoin", null);

    /** Samoan Tala. */
    public static final CurrencyDTO WST = createCurrency("WST", "Samoan Tala", null);

    /** CFA Franc BEAC. */
    public static final CurrencyDTO XAF = createCurrency("XAF", "CFA Franc BEAC", null);

    /** Asch. */
    public static final CurrencyDTO XAS = createCurrency("XAS", "Asch", null);

    /** Xaurum. */
    public static final CurrencyDTO XAUR = createCurrency("XAUR", "Xaurum", null);

    /** East Caribbean Dollar. */
    public static final CurrencyDTO XCD = createCurrency("XCD", "East Caribbean Dollar", null);

    /** Special Drawing Rights. */
    public static final CurrencyDTO XDR = createCurrency("XDR", "Special Drawing Rights", null);

    /** NEM. */
    public static final CurrencyDTO XEM = createCurrency("XEM", "NEM", null);

    /** Stellar Lumen. */
    public static final CurrencyDTO XLM = createCurrency("XLM", "Stellar Lumen", null);

    /** Monero. */
    public static final CurrencyDTO XMR = createCurrency("XMR", "Monero", null);

    /** Rai Blocks. */
    public static final CurrencyDTO XRB = createCurrency("XRB", "Rai Blocks", null);

    /** CFA Franc BCEAO. */
    public static final CurrencyDTO XOF = createCurrency("XOF", "CFA Franc BCEAO", null);

    /** CFP Franc. */
    public static final CurrencyDTO XPF = createCurrency("XPF", "CFP Franc", null);

    /** Primecoin. */
    public static final CurrencyDTO XPM = createCurrency("XPM", "Primecoin", null);

    /** Ripple. */
    public static final CurrencyDTO XRP = createCurrency("XRP", "Ripple", null);

    /** YbCoin. */
    public static final CurrencyDTO YBC = createCurrency("YBC", "YbCoin", null);

    /** Yemeni Rial. */
    public static final CurrencyDTO YER = createCurrency("YER", "Yemeni Rial", null);

    /** South African Rand. */
    public static final CurrencyDTO ZAR = createCurrency("ZAR", "South African Rand", null);

    /** Zcash. */
    public static final CurrencyDTO ZEC = createCurrency("ZEC", "Zcash", null);

    /** ZenCash. */
    public static final CurrencyDTO ZEN = createCurrency("ZEN", "ZenCash", null);

    /** Zambian Kwacha. */
    public static final CurrencyDTO ZMK = createCurrency("ZMK", "Zambian Kwacha", null);

    /** ziftrCOIN. */
    public static final CurrencyDTO ZRC = createCurrency("ZRC", "ziftrCOIN", null);

    /** Zimbabwean Dollar. */
    public static final CurrencyDTO ZWL = createCurrency("ZWL", "Zimbabwean Dollar", null);

    /** March 30th. */
    public static final CurrencyDTO H18 = createCurrency("H18", "March 30th", null);

    /** June 29th. */
    public static final CurrencyDTO M18 = createCurrency("M18", "June 29th", null);

    /** September 28th. */
    public static final CurrencyDTO U18 = createCurrency("U18", "September 28th", null);

    /** December 28th. */
    public static final CurrencyDTO Z18 = createCurrency("Z18", "December 28th", null);

    /** March 29th. */
    public static final CurrencyDTO H19 = createCurrency("H19", "March 29th", null);

    /** June 28th. */
    public static final CurrencyDTO M19 = createCurrency("M19", "June 28th", null);

    /** Bankera Coin. */
    public static final CurrencyDTO BNK = createCurrency("BNK", "Bankera Coin", null);

    /** Binance Coin. */
    public static final CurrencyDTO BNB = createCurrency("BNB", "Binance Coin", null);

    /** Quantstamp. */
    public static final CurrencyDTO QSP = createCurrency("QSP", "Quantstamp", null);

    /** Iota. */
    public static final CurrencyDTO IOTA = createCurrency("IOTA", "Iota", null);

    /** Yoyow. */
    public static final CurrencyDTO YOYO = createCurrency("YOYO", "Yoyow", null);

    /** Bitshare. */
    public static final CurrencyDTO BTS = createCurrency("BTS", "Bitshare", null);

    /** Icon. */
    public static final CurrencyDTO ICX = createCurrency("ICX", "Icon", null);

    /** Monaco. */
    public static final CurrencyDTO MCO = createCurrency("MCO", "Monaco", null);

    /** Cindicator. */
    public static final CurrencyDTO CND = createCurrency("CND", "Cindicator", null);

    /** Verge. */
    public static final CurrencyDTO XVG = createCurrency("XVG", "Verge", null);

    /** Po.et. */
    public static final CurrencyDTO POE = createCurrency("POE", "Po.et", null);

    /** Tron. */
    public static final CurrencyDTO TRX = createCurrency("TRX", "Tron", null);

    /** Cardano. */
    public static final CurrencyDTO ADA = createCurrency("ADA", "Cardano", null);

    /** FunFair. */
    public static final CurrencyDTO FUN = createCurrency("FUN", "FunFair", null);

    /** Hshare. */
    public static final CurrencyDTO HSR = createCurrency("HSR", "Hshare", null);

    /** ETHLend. */
    public static final CurrencyDTO LEND = createCurrency("LEND", "ETHLend", null);

    /** aelf. */
    public static final CurrencyDTO ELF = createCurrency("ELF", "aelf", null);

    /** Storj. */
    public static final CurrencyDTO STORJ = createCurrency("STORJ", "Storj", null);

    /** Modum. */
    public static final CurrencyDTO MOD = createCurrency("MOD", "Modum", null);

    /** Code. */
    private final String code;

    /** Attributes. */
    private final CurrencyDTO.CurrencyAttributes attributes;

    /**
     * Constructor.
     *
     * @param newCode currency code
     */
    public CurrencyDTO(final String newCode) {
        this.code = newCode;
        this.attributes = getInstance(newCode).attributes;
    }

    /**
     * Builder.
     *
     * @param builder builder
     */
    public CurrencyDTO(final CurrencyDTO.Builder builder) {
        this.code = builder.code;
        this.attributes = getInstance(builder.code).attributes;
    }

    /**
     * For builder.
     */
    protected CurrencyDTO() {
        code = null;
        attributes = null;
    }

    /**
     * Constructor.
     *
     * @param newAlternativeCode alternative code
     * @param newAttributes      attributes.
     */
    private CurrencyDTO(final String newAlternativeCode, final CurrencyDTO.CurrencyAttributes newAttributes) {
        this.code = newAlternativeCode;
        this.attributes = newAttributes;
    }

    /**
     * Gets the set of available currencies.
     *
     * @return available currencies
     */
    public static SortedSet<CurrencyDTO> getAvailableCurrencies() {
        return new TreeSet<>(CURRENCIES.values());
    }

    /**
     * Gets the set of available currency codes.
     *
     * @return currency codes
     */
    public static SortedSet<String> getAvailableCurrencyCodes() {

        return new TreeSet<>(CURRENCIES.keySet());
    }

    /**
     * Returns a Currency instance for the given currency code.
     *
     * @param currencyCode currency code
     * @return currency
     */
    public static CurrencyDTO getInstance(final String currencyCode) {
        CurrencyDTO currency = getInstanceNoCreate(currencyCode.toUpperCase());
        return Objects.requireNonNullElseGet(currency, () -> createCurrency(currencyCode.toUpperCase(), null, null));
    }

    /**
     * Returns a Currency instance for the given currency code.
     *
     * @param currencyCode currency code
     * @return currency
     */
    public static CurrencyDTO getInstanceNoCreate(final String currencyCode) {
        return CURRENCIES.get(currencyCode.toUpperCase());
    }

    /**
     * Factory.
     *
     * @param commonCode       commonly used code for this currency: "BTC"
     * @param name             Name of the currency: "Bitcoin"
     * @param unicode          Unicode symbol for the currency: "\u20BF" or "฿"
     * @param alternativeCodes Alternative codes for the currency: "XBT"
     * @return currency
     */
    private static CurrencyDTO createCurrency(final String commonCode, final String name, final String unicode, final String... alternativeCodes) {
        CurrencyDTO.CurrencyAttributes attributes = new CurrencyDTO.CurrencyAttributes(commonCode, name, unicode, alternativeCodes);
        CurrencyDTO currency = new CurrencyDTO(commonCode, attributes);
        for (String code : attributes.codes) {
            if (commonCode.equals(code)) {
                // common code will always be part of the currencies map
                CURRENCIES.put(code, currency);
            } else if (!CURRENCIES.containsKey(code)) {
                // alternative codes will never overwrite common codes
                CURRENCIES.put(code, new CurrencyDTO(code, attributes));
            }
        }
        return currency;
    }

    /**
     * Get currency code.
     *
     * @return currency code
     */
    public String getCurrencyCode() {
        return code;
    }

    /**
     * Gets the equivalent object with the passed code.
     *
     * <p>This is useful in case some currencies share codes, such that {@link #getInstance(String)}
     * may return the wrong currency.
     *
     * @param newCode The code the returned object will evaluate to
     * @return A Currency representing the same currency but having the passed currency code
     */
    public CurrencyDTO getCodeCurrency(final String newCode) {
        if (newCode.equals(this.code)) {
            return this;
        }
        CurrencyDTO currency = getInstance(newCode);
        if (currency.equals(this)) {
            return currency;
        }
        if (!attributes.codes.contains(newCode)) {
            throw new IllegalArgumentException("Code not listed for this currency");
        }
        return new CurrencyDTO(newCode, attributes);
    }

    /**
     * Gets the equivalent object with an ISO 4217 code, or if none a code which looks ISO compatible (starts with an X), or the constructed currency code if neither exist.
     *
     * @return currency
     */
    public CurrencyDTO getIso4217Currency() {
        if (attributes.isoCode == null) {
            return this;
        }
        // The logic for setting isoCode is in CurrencyAttributes
        return getCodeCurrency(attributes.isoCode);
    }

    /**
     * Gets the equivalent object that was created with the "commonly used" code.
     *
     * @return currency
     */
    public CurrencyDTO getCommonlyUsedCurrency() {
        return getCodeCurrency(attributes.commonCode);
    }

    /**
     * Gets the set of all currency codes associated with this currency.
     *
     * @return currency
     */
    public Set<String> getCurrencyCodes() {
        return attributes.codes;
    }

    /**
     * Gets the unicode symbol of this currency.
     *
     * @return unicode
     */
    public String getSymbol() {
        return attributes.unicode;
    }

    /**
     * Getter for code.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the name that is suitable for displaying this currency.
     *
     * @return display name
     */
    public String getDisplayName() {
        return attributes.name;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CurrencyDTO other = (CurrencyDTO) obj;
        return attributes.equals(other.attributes);
    }

    /**
     * Currency attributes.
     */
    private static class CurrencyAttributes {

        /** Codes. */
        private final Set<String> codes;

        /** Iso code. */
        private final String isoCode;

        /** common code. */
        private final String commonCode;

        /** Name. */
        private final String name;

        /** Unicode. */
        private final String unicode;

        /**
         * Constructor.
         *
         * @param newCommonCode       common code
         * @param newName             name
         * @param newUnicode          unicode
         * @param newAlternativeCodes alternative codes
         */
        CurrencyAttributes(final String newCommonCode, final String newName, final String newUnicode, final String... newAlternativeCodes) {
            if (newAlternativeCodes.length > 0) {
                this.codes = new TreeSet<>(Arrays.asList(newAlternativeCodes));
                this.codes.add(newCommonCode);
            } else {
                this.codes = Collections.singleton(newCommonCode);
            }

            String possibleIsoProposalCryptoCode = null;

            java.util.Currency javaCurrency = null;
            for (String code : this.codes) {
                if (javaCurrency == null) {
                    try {
                        javaCurrency = java.util.Currency.getInstance(code);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                if (code.startsWith("X")) {
                    possibleIsoProposalCryptoCode = code;
                }
            }

            if (javaCurrency != null) {
                this.isoCode = javaCurrency.getCurrencyCode();
            } else {
                this.isoCode = possibleIsoProposalCryptoCode;
            }

            this.commonCode = newCommonCode;

            if (newName != null) {
                this.name = newName;
            } else if (javaCurrency != null) {
                this.name = javaCurrency.getDisplayName();
            } else {
                this.name = newCommonCode;
            }

            if (newUnicode != null) {
                this.unicode = newUnicode;
            } else if (javaCurrency != null) {
                this.unicode = javaCurrency.getSymbol();
            } else {
                this.unicode = newCommonCode;
            }
        }

        @Override
        public int hashCode() {
            return commonCode.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CurrencyDTO.CurrencyAttributes other = (CurrencyDTO.CurrencyAttributes) obj;
            if (commonCode == null) {
                return other.commonCode == null;
            } else {
                return commonCode.equalsIgnoreCase(other.commonCode);
            }
        }
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static CurrencyDTO.Builder builder() {
        return new CurrencyDTO.Builder();
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** Code. */
        private String code;

        /**
         * Set code.
         *
         * @param newCode code
         * @return builder
         */
        public CurrencyDTO.Builder code(final String newCode) {
            this.code = newCode;
            return this;
        }

        /**
         * Create wallet.
         *
         * @return wallet
         */
        public CurrencyDTO create() {
            return new CurrencyDTO(this);
        }

    }

}
