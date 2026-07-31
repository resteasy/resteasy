package org.jboss.resteasy.core.request;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;

/**
 * {@link Variant} selection.
 *
 * @author Pascal S. de Kloe
 * @see "RFC 2296"
 */
public class ServerDrivenNegotiation {

    private Map<MediaType, QualityValue> requestedMediaTypes = null;
    private Map<String, QualityValue> requestedCharacterSets = null;
    private Map<String, QualityValue> requestedEncodings = null;
    private Map<Locale, QualityValue> requestedLanguages = null;
    private int mediaRadix = 1;

    public ServerDrivenNegotiation() {
    }

    public void setAcceptHeaders(List<String> headerValues) {
        requestedMediaTypes = null;
        if (headerValues == null)
            return;
        Map<MediaType, QualityValue> requested = null;
        for (String headerValue : headerValues) {
            Map<MediaType, QualityValue> mapping = AcceptHeaders.getMediaTypeQualityValues(headerValue);
            if (mapping == null)
                return;
            if (requested == null)
                requested = mapping;
            else
                requested.putAll(mapping);
        }
        requestedMediaTypes = requested;
        for (Iterator<MediaType> it = requested.keySet().iterator(); it.hasNext();) {
            mediaRadix = Math.max(mediaRadix, it.next().getParameters().size());
        }
    }

    public void setAcceptCharsetHeaders(List<String> headerValues) {
        requestedCharacterSets = null;
        if (headerValues == null)
            return;
        Map<String, QualityValue> requested = null;
        for (String headerValue : headerValues) {
            Map<String, QualityValue> mapping = AcceptHeaders.getStringQualityValues(headerValue);
            if (mapping == null)
                return;
            if (requested == null)
                requested = mapping;
            else
                requested.putAll(mapping);
        }
        requestedCharacterSets = requested;
    }

    public void setAcceptEncodingHeaders(List<String> headerValues) {
        requestedEncodings = null;
        if (headerValues == null)
            return;
        Map<String, QualityValue> requested = null;
        for (String headerValue : headerValues) {
            Map<String, QualityValue> mapping = AcceptHeaders.getStringQualityValues(headerValue);
            if (mapping == null)
                return;
            if (requested == null)
                requested = mapping;
            else
                requested.putAll(mapping);
        }
        requestedEncodings = requested;
    }

    public void setAcceptLanguageHeaders(List<String> headerValues) {
        requestedLanguages = null;
        if (headerValues == null)
            return;
        Map<Locale, QualityValue> requested = null;
        for (String headerValue : headerValues) {
            Map<Locale, QualityValue> mapping = AcceptHeaders.getLocaleQualityValues(headerValue);
            if (mapping == null)
                return;
            if (requested == null)
                requested = mapping;
            else
                requested.putAll(mapping);
        }
        requestedLanguages = requested;
    }

    public Variant getBestMatch(List<Variant> available) {
        VariantQuality bestQuality = null;
        Variant bestOption = null;
        for (Variant option : available) {
            VariantQuality quality = new VariantQuality();
            if (!applyMediaType(option, quality))
                continue;
            if (!applyCharacterSet(option, quality))
                continue;
            if (!applyEncoding(option, quality))
                continue;
            if (!applyLanguage(option, quality))
                continue;

            if (isBetterOption(bestQuality, bestOption, quality, option)) {
                bestQuality = quality;
                bestOption = option;
            }
        }
        return bestOption;
    }

    /**
     * Tests whether {@code option} is preferable over the current {@code bestOption}.
     *
     * <p>
     * Tiebreaker hierarchy (each level is applied only when all prior levels are equal):
     * </p>
     * <ol>
     * <li>Overall quality -- RFC 2296 Section 3.3: {@code round5(qt * qc * qe * ql)}</li>
     * <li>Request media type specificity -- more specific Accept entry wins</li>
     * <li>Variant media type specificity -- more specific variant type wins</li>
     * <li>Variant explicitness -- more dimensions (media type, encoding, language) wins</li>
     * <li>Non-wildcard language match beats wildcard</li>
     * <li>Accept-Language position -- earlier position in header wins</li>
     * <li>Language match precision -- EXACT_COUNTRY &gt; EXACT &gt; BASE_LANGUAGE</li>
     * </ol>
     *
     * @see "RFC 2296 Section 3.3"
     * @see "RFC 2296 Section 3.5"
     */
    private static boolean isBetterOption(VariantQuality bestQuality, Variant best,
            VariantQuality optionQuality, Variant option) {
        if (best == null)
            return true;

        // Compare overall quality.
        int signum = bestQuality.getOverallQuality().compareTo(optionQuality.getOverallQuality());
        if (signum != 0)
            return signum < 0;

        // Overall quality is the same.
        // Assuming the request has an Accept header, a VariantQuality has a non-null
        // requestMediaType if and only if it the corresponding Variant has a non-null mediaType.
        // If bestQuality and optionQuality both have a non-null requestMediaType, we compare them
        // for specificity.
        MediaType bestRequestMediaType = bestQuality.getRequestMediaType();
        MediaType optionRequestMediaType = optionQuality.getRequestMediaType();
        if (bestRequestMediaType != null && optionRequestMediaType != null) {
            if (bestRequestMediaType.getType().equals(optionRequestMediaType.getType())) {
                if (bestRequestMediaType.getSubtype().equals(optionRequestMediaType.getSubtype())) {
                    int bestCount = bestRequestMediaType.getParameters().size();
                    int optionCount = optionRequestMediaType.getParameters().size();
                    if (optionCount > bestCount) {
                        return true; // more matching parameters
                    } else if (optionCount < bestCount) {
                        return false; // less matching parameters
                    }
                } else if (bestRequestMediaType.getSubtype().equals("*")) {
                    return true;
                } else if (optionRequestMediaType.getSubtype().equals("*")) {
                    return false;
                }
            } else if (bestRequestMediaType.getType().equals("*")) {
                return true;
            } else if (optionRequestMediaType.getType().equals("*")) {
                return false;
            }
        }

        // Compare variant media types for specificity.
        MediaType bestType = best.getMediaType();
        MediaType optionType = option.getMediaType();
        if (bestType != null && optionType != null) {
            if (bestType.getType().equals(optionType.getType())) {
                // Same type
                if (bestType.getSubtype().equals(optionType.getSubtype())) {
                    // Same subtype
                    int bestCount = bestType.getParameters().size();
                    int optionCount = optionType.getParameters().size();
                    if (optionCount > bestCount)
                        return true; // more matching parameters
                    else if (optionCount < bestCount)
                        return false; // less matching parameters
                } else if ("*".equals(bestType.getSubtype())) {
                    return true; // more specific subtype
                } else if ("*".equals(optionType.getSubtype())) {
                    return false; // less specific subtype
                }
            } else if ("*".equals(bestType.getType())) {
                return true; // more specific type
            } else if ("*".equals(optionType.getType())) {
                return false; // less specific type;
            }
        }

        // Compare specificity of the variants.
        final int bestExplicitness = getExplicitness(best);
        final int optionExplicitness = getExplicitness(option);
        if (bestExplicitness != optionExplicitness)
            return bestExplicitness < optionExplicitness;

        // Language tiebreaker hierarchy (applied when overall quality is equal):
        // 1. Non-wildcard match always beats wildcard match
        // 2. Position in Accept-Language header (lower position = higher priority)
        // 3. Match precision (EXACT_COUNTRY > EXACT > BASE_LANGUAGE)
        boolean bestIsWildcard = bestQuality.getLanguageMatchPrecision() == LanguageMatchPrecision.WILDCARD;
        boolean optionIsWildcard = optionQuality.getLanguageMatchPrecision() == LanguageMatchPrecision.WILDCARD;
        if (bestIsWildcard != optionIsWildcard)
            return bestIsWildcard;

        if (bestQuality.getLanguageMatchPosition() != optionQuality.getLanguageMatchPosition())
            return bestQuality.getLanguageMatchPosition() > optionQuality.getLanguageMatchPosition();

        return bestQuality.getLanguageMatchPrecision().compareTo(optionQuality.getLanguageMatchPrecision()) < 0;
    }

    private static int getExplicitness(Variant variant) {
        int explicitness = 0;
        if (variant.getMediaType() != null)
            ++explicitness;
        if (variant.getEncoding() != null)
            ++explicitness;
        if (variant.getLanguage() != null)
            ++explicitness;
        return explicitness;
    }

    private boolean applyMediaType(Variant option, VariantQuality quality) {
        if (requestedMediaTypes == null)
            return true;
        MediaType mediaType = option.getMediaType();
        if (mediaType == null)
            return true;

        String type = mediaType.getType();
        if ("*".equals(type))
            type = null;
        String subtype = mediaType.getSubtype();
        if ("*".equals(subtype))
            subtype = null;
        Map<String, String> parameters = mediaType.getParameters();
        if (parameters.isEmpty())
            parameters = null;

        QualityValue bestQuality = QualityValue.NOT_ACCEPTABLE;
        int bestMatchCount = -1;
        MediaType bestRequestMediaType = null;

        for (MediaType requested : requestedMediaTypes.keySet()) {
            int matchCount = 0;
            if (type != null) {
                String requestedType = requested.getType();
                if (requestedType.equals(type))
                    matchCount += mediaRadix * 100;
                else if (!"*".equals(requestedType))
                    continue;
            }
            if (subtype != null) {
                String requestedSubtype = requested.getSubtype();
                if (requestedSubtype.equals(subtype))
                    matchCount += mediaRadix * 10;
                else if (!"*".equals(requestedSubtype))
                    continue;
            }
            Map<String, String> requestedParameters = requested.getParameters();
            if (requestedParameters != null && requestedParameters.size() > 0) {
                if (!hasRequiredParameters(requestedParameters, parameters))
                    continue;
                matchCount += requestedParameters.size();
            }

            if (matchCount > bestMatchCount) {
                bestMatchCount = matchCount;
                bestQuality = requestedMediaTypes.get(requested);
                bestRequestMediaType = requested;
            } else if (matchCount == bestMatchCount) {
                QualityValue qualityValue = requestedMediaTypes.get(requested);
                if (bestQuality.compareTo(qualityValue) < 0) {
                    bestQuality = qualityValue;
                    bestRequestMediaType = requested;
                }
            }
        }

        if (!bestQuality.isAcceptable())
            return false;

        quality.setMediaTypeQualityValue(bestQuality);
        quality.setRequestMediaType(bestRequestMediaType);
        return true;
    }

    private boolean hasRequiredParameters(Map<String, String> required, Map<String, String> available) {
        if (available == null) {
            return false;
        }
        for (Entry<String, String> requiredEntry : required.entrySet()) {
            String name = requiredEntry.getKey();
            String value = requiredEntry.getValue();
            String availableValue = available.get(name);
            if (availableValue == null && "charset".equals(name)) {
                if (requestedCharacterSets != null
                        && !requestedCharacterSets.containsKey(null)
                        && !requestedCharacterSets.containsKey(value))
                    return false;
            } else if (!value.equals(availableValue))
                return false;
        }
        return true;
    }

    private boolean applyCharacterSet(Variant option, VariantQuality quality) {
        if (requestedCharacterSets == null)
            return true;
        MediaType mediaType = option.getMediaType();
        if (mediaType == null)
            return true;
        String charsetParameter = mediaType.getParameters().get("charset");
        if (charsetParameter == null)
            return true;
        QualityValue value = requestedCharacterSets.get(charsetParameter);
        if (value == null) // try wildcard
            value = requestedCharacterSets.get(null);
        if (value == null) // no match
            return false;
        if (!value.isAcceptable())
            return false;
        quality.setCharacterSetQualityValue(value);
        return true;
    }

    private boolean applyEncoding(Variant option, VariantQuality quality) {
        if (requestedEncodings == null)
            return true;
        String encoding = option.getEncoding();
        if (encoding == null)
            return true;
        QualityValue value = requestedEncodings.get(encoding);
        if (value == null) // try wildcard
            value = requestedEncodings.get(null);
        if (value == null) // no match
            return false;
        if (!value.isAcceptable())
            return false;
        quality.setEncodingQualityValue(value);
        return true;
    }

    private boolean hasCountry(Locale locale) {
        return !locale.getCountry().isBlank();
    }

    /**
     * Finds the best matching Accept-Language range for the variant's language and assigns the
     * corresponding quality value, match position, and match precision to the {@link VariantQuality} bean.
     *
     * <p>
     * The quality value comes from the <b>longest matching range</b> in the Accept-Language header
     * (RFC 7231 Section 5.3.5). Matching follows RFC 4647 Section 3.3.1 (Basic Filtering): a range
     * matches a tag if it equals the tag or is a prefix where the next character is {@code '-'}.
     * </p>
     *
     * <p>
     * As a practical extension beyond strict RFC 4647, a "reverse prefix" match is allowed: range
     * {@code en-US} matches tag {@code en} at {@link LanguageMatchPrecision#BASE_LANGUAGE} priority.
     * This avoids 406 responses when the server has only a base-language variant.
     * </p>
     *
     * <p>
     * When an EXACT or EXACT_COUNTRY match is found, the loop exits immediately (this is the longest
     * possible match). BASE_LANGUAGE matches continue looping because a longer match may exist later
     * in the header.
     * </p>
     *
     * <p>
     * "Family position inheritance": when a more specific range (e.g. {@code en-US}) produces an
     * EXACT_COUNTRY match at the same quality as a prior BASE_LANGUAGE match from a family range
     * (e.g. {@code en}), the position from the earlier family match is retained. This prevents
     * penalizing specific variants that belong to a requested language family.
     * </p>
     *
     * @param option  the variant being evaluated
     * @param quality the quality bean to populate
     * @return {@code true} if the variant's language is acceptable, {@code false} otherwise
     *
     * @see "RFC 7231 Section 5.3.5"
     * @see "RFC 4647 Section 3.3.1"
     */
    private boolean applyLanguage(Variant option, VariantQuality quality) {
        if (requestedLanguages == null)
            return true;
        Locale variantLanguage = option.getLanguage();
        if (variantLanguage == null)
            return true;

        final boolean variantHasCountry = hasCountry(variantLanguage);
        final String variantLang = variantLanguage.getLanguage().toLowerCase(Locale.ROOT);
        final String variantCountry = variantHasCountry ? variantLanguage.getCountry().toLowerCase(Locale.ROOT) : "";
        final QualityValue wildcardQuality = requestedLanguages.get(null);
        QualityValue bestQuality = null;
        int bestPosition = Integer.MAX_VALUE;
        int wildcardPosition = 0;
        LanguageMatchPrecision bestPrecision = LanguageMatchPrecision.WILDCARD;
        int position = 0;

        for (Entry<Locale, QualityValue> entry : requestedLanguages.entrySet()) {
            Locale range = entry.getKey();
            QualityValue rangeQuality = entry.getValue();
            position++;

            if (range == null) {
                wildcardPosition = position;
                continue;
            }

            if (!range.getLanguage().toLowerCase(Locale.ROOT).equals(variantLang))
                continue;

            boolean rangeHasCountry = hasCountry(range);

            if (rangeHasCountry && variantHasCountry) {
                if (range.getCountry().toLowerCase(Locale.ROOT).equals(variantCountry)) {
                    // EXACT_COUNTRY: longest possible match -- use this quality (RFC 7231 Section 5.3.5).
                    // Preserve earlier family position when quality is unchanged.
                    if (bestQuality == null || rangeQuality.compareTo(bestQuality) != 0) {
                        bestPosition = position;
                    }
                    bestQuality = rangeQuality;
                    bestPrecision = LanguageMatchPrecision.EXACT_COUNTRY;
                    break;
                } else {
                    // Country mismatch (e.g. range en-GB, variant en-US): not a match per RFC 4647.
                    continue;
                }
            } else if (rangeHasCountry == variantHasCountry) {
                // EXACT: language matches, neither side has a country.
                // Preserve earlier family position when quality is unchanged.
                if (bestQuality == null || rangeQuality.compareTo(bestQuality) != 0) {
                    bestPosition = position;
                }
                bestQuality = rangeQuality;
                bestPrecision = LanguageMatchPrecision.EXACT;
                break;
            } else {
                // BASE_LANGUAGE: language matches but country presence differs.
                // Either range "en" matching variant "en-US" (RFC 4647 prefix match),
                // or range "en-US" matching variant "en" (practical reverse-prefix extension).
                // Keep looping -- a longer EXACT/EXACT_COUNTRY match may appear later.
                if (bestQuality == null || rangeQuality.compareTo(bestQuality) > 0) {
                    bestQuality = rangeQuality;
                    bestPosition = position;
                    bestPrecision = LanguageMatchPrecision.BASE_LANGUAGE;
                }
            }
        }

        // Wildcard fallback: when no explicit range matched, or when the only match is a
        // reverse-prefix BASE_LANGUAGE match with q=0. An EXACT or EXACT_COUNTRY match with q=0
        // means the user explicitly excluded this language and must NOT be overridden by a wildcard
        // (RFC 7231 Section 5.3.5: quality comes from the longest matching range).
        if (bestQuality == null
                || (!bestQuality.isAcceptable() && bestPrecision == LanguageMatchPrecision.BASE_LANGUAGE)) {
            if (wildcardQuality != null) {
                bestQuality = wildcardQuality;
                bestPosition = wildcardPosition;
                bestPrecision = LanguageMatchPrecision.WILDCARD;
            }
        }

        if (bestQuality == null)
            return false;
        if (!bestQuality.isAcceptable())
            return false;

        quality.setLanguageQualityValue(bestQuality);
        quality.setLanguageMatchPosition(bestPosition);
        quality.setLanguageMatchPrecision(bestPrecision);
        return true;
    }

}
