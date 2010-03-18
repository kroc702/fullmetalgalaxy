/**
 * 
 */
package com.fullmetalgalaxy.client.ressources.fonts;



import com.fullmetalgalaxy.client.ressources.fonts.automatica.FontAutomaticaBundle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Vincent Legendre
 *
 */
public class ImageFont
{
  public final static ImageFontBundle s_FontTitleBundle = (ImageFontBundle)GWT
      .create( FontAutomaticaBundle.class );

  public static AbstractImagePrototype getImage(ImageFontBundle p_font, char p_ch)
  {
    switch( p_ch )
    {
    case 32: /*   */
      return p_font.ch32();
    case 33: /* ! */
      return p_font.ch33();
    case 34: /* " */
      return p_font.ch34();
    case 35: /* # */
      return p_font.ch35();
    case 36: /* $ */
      return p_font.ch36();
    case 37: /* % */
      return p_font.ch37();
    case 38: /* & */
      return p_font.ch38();
    case 39: /* ' */
      return p_font.ch39();
    case 40: /* ( */
      return p_font.ch40();
    case 41: /* ) */
      return p_font.ch41();
    case 42: /* * */
      return p_font.ch42();
    case 43: /* + */
      return p_font.ch43();
    case 44: /* , */
      return p_font.ch44();
    case 45: /* - */
      return p_font.ch45();
    case 46: /* . */
      return p_font.ch46();
    case 47: /* / */
      return p_font.ch47();
    case 48: /* 0 */
      return p_font.ch48();
    case 49: /* 1 */
      return p_font.ch49();
    case 50: /* 2 */
      return p_font.ch50();
    case 51: /* 3 */
      return p_font.ch51();
    case 52: /* 4 */
      return p_font.ch52();
    case 53: /* 5 */
      return p_font.ch53();
    case 54: /* 6 */
      return p_font.ch54();
    case 55: /* 7 */
      return p_font.ch55();
    case 56: /* 8 */
      return p_font.ch56();
    case 57: /* 9 */
      return p_font.ch57();
    case 58: /* : */
      return p_font.ch58();
    case 59: /* ; */
      return p_font.ch59();
    case 60: /* < */
      return p_font.ch60();
    case 61: /* = */
      return p_font.ch61();
    case 62: /* > */
      return p_font.ch62();
    default:
    case 63: /* ? */
      return p_font.ch63();
    case 64: /* @ */
      return p_font.ch64();
    case 65: /* A */
      return p_font.ch65();
    case 66: /* B */
      return p_font.ch66();
    case 67: /* C */
      return p_font.ch67();
    case 68: /* D */
      return p_font.ch68();
    case 69: /* E */
      return p_font.ch69();
    case 70: /* F */
      return p_font.ch70();
    case 71: /* G */
      return p_font.ch71();
    case 72: /* H */
      return p_font.ch72();
    case 73: /* I */
      return p_font.ch73();
    case 74: /* J */
      return p_font.ch74();
    case 75: /* K */
      return p_font.ch75();
    case 76: /* L */
      return p_font.ch76();
    case 77: /* M */
      return p_font.ch77();
    case 78: /* N */
      return p_font.ch78();
    case 79: /* O */
      return p_font.ch79();
    case 80: /* P */
      return p_font.ch80();
    case 81: /* Q */
      return p_font.ch81();
    case 82: /* R */
      return p_font.ch82();
    case 83: /* S */
      return p_font.ch83();
    case 84: /* T */
      return p_font.ch84();
    case 85: /* U */
      return p_font.ch85();
    case 86: /* V */
      return p_font.ch86();
    case 87: /* W */
      return p_font.ch87();
    case 88: /* X */
      return p_font.ch88();
    case 89: /* Y */
      return p_font.ch89();
    case 90: /* Z */
      return p_font.ch90();
    case 91: /* [ */
      return p_font.ch91();
    case 92: /* \ */
      return p_font.ch92();
    case 93: /* ] */
      return p_font.ch93();
    case 94: /* ^ */
      return p_font.ch94();
    case 95: /* _ */
      return p_font.ch95();
    case 96: /* ` */
      return p_font.ch96();
    case 97: /* a */
      return p_font.ch97();
    case 98: /* b */
      return p_font.ch98();
    case 99: /* c */
      return p_font.ch99();
    case 100: /* d */
      return p_font.ch100();
    case 101: /* e */
      return p_font.ch101();
    case 102: /* f */
      return p_font.ch102();
    case 103: /* g */
      return p_font.ch103();
    case 104: /* h */
      return p_font.ch104();
    case 105: /* i */
      return p_font.ch105();
    case 106: /* j */
      return p_font.ch106();
    case 107: /* k */
      return p_font.ch107();
    case 108: /* l */
      return p_font.ch108();
    case 109: /* m */
      return p_font.ch109();
    case 110: /* n */
      return p_font.ch110();
    case 111: /* o */
      return p_font.ch111();
    case 112: /* p */
      return p_font.ch112();
    case 113: /* q */
      return p_font.ch113();
    case 114: /* r */
      return p_font.ch114();
    case 115: /* s */
      return p_font.ch115();
    case 116: /* t */
      return p_font.ch116();
    case 117: /* u */
      return p_font.ch117();
    case 118: /* v */
      return p_font.ch118();
    case 119: /* w */
      return p_font.ch119();
    case 120: /* x */
      return p_font.ch120();
    case 121: /* y */
      return p_font.ch121();
    case 122: /* z */
      return p_font.ch122();
    case 123: /* { */
      return p_font.ch123();
    case 124: /* | */
      return p_font.ch124();
    case 125: /* } */
      return p_font.ch125();
    case 126: /* ~ */
      return p_font.ch126();
    case 127: /*  */
      return p_font.ch127();
    case 128: /*  */
      return p_font.ch128();
    case 129: /*  */
      return p_font.ch129();
    case 130: /*  */
      return p_font.ch130();
    case 131: /*  */
      return p_font.ch131();
    case 132: /*  */
      return p_font.ch132();
    case 133: /*  */
      return p_font.ch133();
    case 134: /*  */
      return p_font.ch134();
    case 135: /*  */
      return p_font.ch135();
    case 136: /*  */
      return p_font.ch136();
    case 137: /*  */
      return p_font.ch137();
    case 138: /*  */
      return p_font.ch138();
    case 139: /*  */
      return p_font.ch139();
    case 140: /*  */
      return p_font.ch140();
    case 141: /*  */
      return p_font.ch141();
    case 142: /*  */
      return p_font.ch142();
    case 143: /*  */
      return p_font.ch143();
    case 144: /*  */
      return p_font.ch144();
    case 145: /*  */
      return p_font.ch145();
    case 146: /*  */
      return p_font.ch146();
    case 147: /*  */
      return p_font.ch147();
    case 148: /*  */
      return p_font.ch148();
    case 149: /*  */
      return p_font.ch149();
    case 150: /*  */
      return p_font.ch150();
    case 151: /*  */
      return p_font.ch151();
    case 152: /*  */
      return p_font.ch152();
    case 153: /*  */
      return p_font.ch153();
    case 154: /*  */
      return p_font.ch154();
    case 155: /*  */
      return p_font.ch155();
    case 156: /*  */
      return p_font.ch156();
    case 157: /*  */
      return p_font.ch157();
    case 158: /*  */
      return p_font.ch158();
    case 159: /*  */
      return p_font.ch159();
    case 160: /*   */
      return p_font.ch160();
    case 161: /* ¡ */
      return p_font.ch161();
    case 162: /* ¢ */
      return p_font.ch162();
    case 163: /* £ */
      return p_font.ch163();
    case 164: /* € */
      return p_font.ch164();
    case 165: /* ¥ */
      return p_font.ch165();
    case 166: /* Š */
      return p_font.ch166();
    case 167: /* § */
      return p_font.ch167();
    case 168: /* š */
      return p_font.ch168();
    case 169: /* © */
      return p_font.ch169();
    case 170: /* ª */
      return p_font.ch170();
    case 171: /* « */
      return p_font.ch171();
    case 172: /* ¬ */
      return p_font.ch172();
    case 173: /* ­ */
      return p_font.ch173();
    case 174: /* ® */
      return p_font.ch174();
    case 175: /* ¯ */
      return p_font.ch175();
    case 176: /* ° */
      return p_font.ch176();
    case 177: /* ± */
      return p_font.ch177();
    case 178: /* ² */
      return p_font.ch178();
    case 179: /* ³ */
      return p_font.ch179();
    case 180: /* Ž */
      return p_font.ch180();
    case 181: /* µ */
      return p_font.ch181();
    case 182: /* ¶ */
      return p_font.ch182();
    case 183: /* · */
      return p_font.ch183();
    case 184: /* ž */
      return p_font.ch184();
    case 185: /* ¹ */
      return p_font.ch185();
    case 186: /* º */
      return p_font.ch186();
    case 187: /* » */
      return p_font.ch187();
    case 188: /* Œ */
      return p_font.ch188();
    case 189: /* œ */
      return p_font.ch189();
    case 190: /* Ÿ */
      return p_font.ch190();
    case 191: /* ¿ */
      return p_font.ch191();
    case 192: /* À */
      return p_font.ch192();
    case 193: /* Á */
      return p_font.ch193();
    case 194: /* Â */
      return p_font.ch194();
    case 195: /* Ã */
      return p_font.ch195();
    case 196: /* Ä */
      return p_font.ch196();
    case 197: /* Å */
      return p_font.ch197();
    case 198: /* Æ */
      return p_font.ch198();
    case 199: /* Ç */
      return p_font.ch199();
    case 200: /* È */
      return p_font.ch200();
    case 201: /* É */
      return p_font.ch201();
    case 202: /* Ê */
      return p_font.ch202();
    case 203: /* Ë */
      return p_font.ch203();
    case 204: /* Ì */
      return p_font.ch204();
    case 205: /* Í */
      return p_font.ch205();
    case 206: /* Î */
      return p_font.ch206();
    case 207: /* Ï */
      return p_font.ch207();
    case 208: /* Ð */
      return p_font.ch208();
    case 209: /* Ñ */
      return p_font.ch209();
    case 210: /* Ò */
      return p_font.ch210();
    case 211: /* Ó */
      return p_font.ch211();
    case 212: /* Ô */
      return p_font.ch212();
    case 213: /* Õ */
      return p_font.ch213();
    case 214: /* Ö */
      return p_font.ch214();
    case 215: /* × */
      return p_font.ch215();
    case 216: /* Ø */
      return p_font.ch216();
    case 217: /* Ù */
      return p_font.ch217();
    case 218: /* Ú */
      return p_font.ch218();
    case 219: /* Û */
      return p_font.ch219();
    case 220: /* Ü */
      return p_font.ch220();
    case 221: /* Ý */
      return p_font.ch221();
    case 222: /* Þ */
      return p_font.ch222();
    case 223: /* ß */
      return p_font.ch223();
    case 224: /* à */
      return p_font.ch224();
    case 225: /* á */
      return p_font.ch225();
    case 226: /* â */
      return p_font.ch226();
    case 227: /* ã */
      return p_font.ch227();
    case 228: /* ä */
      return p_font.ch228();
    case 229: /* å */
      return p_font.ch229();
    case 230: /* æ */
      return p_font.ch230();
    case 231: /* ç */
      return p_font.ch231();
    case 232: /* è */
      return p_font.ch232();
    case 233: /* é */
      return p_font.ch233();
    case 234: /* ê */
      return p_font.ch234();
    case 235: /* ë */
      return p_font.ch235();
    case 236: /* ì */
      return p_font.ch236();
    case 237: /* í */
      return p_font.ch237();
    case 238: /* î */
      return p_font.ch238();
    case 239: /* ï */
      return p_font.ch239();
    case 240: /* ð */
      return p_font.ch240();
    case 241: /* ñ */
      return p_font.ch241();
    case 242: /* ò */
      return p_font.ch242();
    case 243: /* ó */
      return p_font.ch243();
    case 244: /* ô */
      return p_font.ch244();
    case 245: /* õ */
      return p_font.ch245();
    case 246: /* ö */
      return p_font.ch246();
    case 247: /* ÷ */
      return p_font.ch247();
    case 248: /* ø */
      return p_font.ch248();
    case 249: /* ù */
      return p_font.ch249();
    case 250: /* ú */
      return p_font.ch250();
    case 251: /* û */
      return p_font.ch251();
    case 252: /* ü */
      return p_font.ch252();
    case 253: /* ý */
      return p_font.ch253();
    case 254: /* þ */
      return p_font.ch254();
    case 255: /* ÿ */
      return p_font.ch255();
    }
  }

  public static String getHTML(ImageFontBundle p_font, String p_text)
  {
    StringBuffer html = new StringBuffer();
    html.append( "<div class=\"FontTitleBundle\">" );
    // Image image = new Image();
    for( int i = 0; i < p_text.length(); i++ )
    {
      html.append( ImageFont.getImage( p_font, p_text.charAt( i ) ).getHTML() );
      // ImageFont.getImage( p_font, p_text.charAt( i ) ).applyTo( image );
      // image.
    }
    html.append( "</div>" );
    return html.toString();
  }



}
