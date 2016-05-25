/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.i18n;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public enum Language {
	Abkhaz("ab"),
	Afar("aa"),
	Afrikaans("af"),
	Akan("ak"),
	Albanian("sq"),
	Amharic("am"),
	Arabic("ar"),
	Aragonese("an"),
	Armenian("hy"),
	Assamese("as"),
	Avaric("av"),
	Avestan("ae"),
	Aymara("ay"),
	Azerbaijani("az"),
	Bambara("bm"),
	Bashkir("ba"),
	Basque("eu"),
	Belarusian("be"),
	Bengali("bn"),
	Bihari("bh"),
	Bislama("bi"),
	Bosnian("bs"),
	Breton("br"),
	Bulgarian("bg"),
	Burmese("my"),
	Catalan_Valencian("ca"),
	Chamorro("ch"),
	Chechen("ce"),
	Chichewa_Chewa_Nyanja("ny"),
	Chinese("zh"),
	Chuvash("cv"),
	Cornish("kw"),
	Corsican("co"),
	Cree("cr"),
	Croatian("hr"),
	Czech("cs"),
	Danish("da"),
	Divehi_Dhivehi_Maldivian("dv"),
	Dutch("nl"),
	Dzongkha("dz"),
	English("en"),
	Esperanto("eo"),
	Estonian("et"),
	Ewe("ee"),
	Faroese("fo"),
	Fijian("fj"),
	Finnish("fi", "Suomi"),
	French("fr"),
	Fula_Fulah_Pulaar_Pular("ff"),
	Galician("gl"),
	Georgian("ka"),
	German("de"),
	Greek_Modern("el"),
	Guarani("gn"),
	Gujarati("gu"),
	Haitian_Haitian_Creole("ht"),
	Hausa("ha"),
	Hebrew_modern("he"),
	Herero("hz"),
	Hindi("hi"),
	Hiri_Motu("ho"),
	Hungarian("hu"),
	Interlingua("ia"),
	Indonesian("id"),
	Interlingue("ie"),
	Irish("ga"),
	Igbo("ig"),
	Inupiaq("ik"),
	Ido("io"),
	Icelandic("is"),
	Italian("it"),
	Inuktitut("iu"),
	Japanese("ja"),
	Javanese("jv"),
	Kalaallisut_Greenlandic("kl"),
	Kannada("kn"),
	Kanuri("kr"),
	Kashmiri("ks"),
	Kazakh("kk"),
	Khmer("km"),
	Kikuyu_Gikuyu("ki"),
	Kinyarwanda("rw"),
	Kirghiz_Kyrgyz("ky"),
	Komi("kv"),
	Kongo("kg"),
	Korean("ko"),
	Kurdish("ku"),
	Kwanyama_Kuanyama("kj"),
	Latin("la"),
	Luxembourgish_Letzeburgesch("lb"),
	Luganda("lg"),
	Limburgish_Limburgan_Limburger("li"),
	Lingala("ln"),
	Lao("lo"),
	Lithuanian("lt"),
	Luba_Katanga("lu"),
	Latvian("lv"),
	Manx("gv"),
	Macedonian("mk"),
	Malagasy("mg"),
	Malay("ms"),
	Malayalam("ml"),
	Maltese("mt"),
	Maori("mi"),
	Marathi("mr"),
	Marshallese("mh"),
	Mongolian("mn"),
	Nauru("na"),
	Navajo_Navaho("nv"),
	Norwegian_Bokmål("nb"),
	North_Ndebele("nd"),
	Nepali("ne"),
	Ndonga("ng"),
	Norwegian_Nynorsk("nn"),
	Norwegian("no"),
	Nuosu("ii"),
	South_Ndebele("nr"),
	Occitan_after_1500("oc"),
	Ojibwa("oj"),
	Old_Church_Slavonic_Church_Slavic_Church_Slavonic_Old_Bulgarian_Old_Slavonic("cu"),
	Oromo("om"),
	Oriya("or"),
	Ossetian_Ossetic("os"),
	Panjabi_Punjabi("pa"),
	Pali("pi"),
	Persian("fa"),
	Polish("pl"),
	Pashto_Pushto("ps"),
	Portuguese("pt"),
	Quechua("qu"),
	Romansh("rm"),
	Kirundi("rn"),
	Romanian_Moldavian_Moldovan("ro"),
	Russian("ru"),
	Sanskrit("sa"),
	Sardinian("sc"),
	Sindhi("sd"),
	Northern_Sami("se"),
	Samoan("sm"),
	Sango("sg"),
	Serbian("sr"),
	Scottish_Gaelic_Gaelic("gd"),
	Shona("sn"),
	Sinhala_Sinhalese("si"),
	Slovak("sk"),
	Slovene("sl"),
	Somali("so"),
	Southern_Sotho("st"),
	Spanish_Castilian("es"),
	Sundanese("su"),
	Swahili("sw"),
	Swati("ss"),
	Swedish("sv", "Svenska"),
	Tamil("ta"),
	Telugu("te"),
	Tajik("tg"),
	Thai("th"),
	Tigrinya("ti"),
	Tibetan_Standard_Tibetan_Central("bo"),
	Turkmen("tk"),
	Tagalog("tl"),
	Tswana("tn"),
	Tonga_Tonga_Islands("to"),
	Turkish("tr"),
	Tsonga("ts"),
	Tatar("tt"),
	Twi("tw"),
	Tahitian("ty"),
	Uighur_Uyghur("ug"),
	Ukrainian("uk"),
	Urdu("ur"),
	Uzbek("uz"),
	Venda("ve"),
	Vietnamese("vi"),
	Volapk("vo"),
	Walloon("wa"),
	Welsh("cy"),
	Wolof("wo"),
	Western_Frisian("fy"),
	Xhosa("xh"),
	Yiddish("yi"),
	Yoruba("yo"),
	Zhuang_Chuang("za"),
	Zulu("zu");

	private final String code;
	private final String localName;

	Language(String code) {

		this(code, null);
	}

	Language(String code, String localName) {

		this.code = code;
		this.localName = localName;
	}

	public String getLanguageCode() {

		return this.code;
	}

	/**
	 * Returns the Language from its corresponding language code
	 * 
	 * @param code
	 * @return Language
	 */
	public static Language getLanguage(String code) {

		for (Language language : Language.values()) {
			if (language.code.equals(code)) {
				return language;
			}
		}
		return null;
	}

	public Node toXML(Document doc) {

		Element languageElement = doc.createElement("language");
		Element nameElement = doc.createElement("name");
		nameElement.setTextContent(this.name());
		
		Element codeElement = doc.createElement("code");
		codeElement.setTextContent(this.code);
		
		Element localNameElement = doc.createElement("localName");
		localNameElement.setTextContent(this.localName == null ? this.name() : this.localName);
		
		languageElement.appendChild(nameElement);
		languageElement.appendChild(codeElement);
		languageElement.appendChild(localNameElement);
		
		return languageElement;
	}
}
