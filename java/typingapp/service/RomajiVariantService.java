package typingapp.service;

import java.util.*;
import java.util.Objects;

public class RomajiVariantService {
	
    private final RomajiNormalizationService normalizationService;
    
    // 定数定義
    // スキップマーク：このマークが付くと表記揺れを生成しない
    private static final String SKIP_MORA = "Д";
    // 長音マーク：長音符として扱う
    private static final String CHO_ON = "ζ";
       
    public RomajiVariantService(RomajiNormalizationService normalizationService) {
        Objects.requireNonNull(normalizationService, "normalizationService must not be null");
    	this.normalizationService = normalizationService;
    }
    
    private String normalizeMora(String mora) {

        if (mora == null || mora.isBlank()) {
            return "";
        }

        if (mora.startsWith(SKIP_MORA)) {
            return mora.substring(SKIP_MORA.length());
        }

        return mora.replace("-", "").replace(CHO_ON, "-");
    }
    
    /** ゲームUI用：変換済みプレビューを取得 */
    public String getPreview(String moraRomajiJoined) {

        if (moraRomajiJoined == null || moraRomajiJoined.isBlank()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String mora : moraRomajiJoined.split("-", -1)) {
            sb.append(normalizeMora(mora));
        }

        return sb.toString();
    }
    
    public List<String> generateVariantsForMora(String mora) {

        if (mora == null || mora.isBlank()) {
            return List.of();
        }

        // スキップマークがある場合は表記揺れ変換を行わない
        if (mora.startsWith(SKIP_MORA)) {
            String skipped = mora.substring(SKIP_MORA.length());
            return skipped.isEmpty() ? List.of() : List.of(skipped);
        }

        String normalized = normalizeMora(mora);

        if (mora.equals(CHO_ON)) {
            return List.of("", "-", "ー");
        }

        normalized = normalizationService.normalize(normalized);

        return normalizationService.getVariants(normalized);
    }
    
//    丸ごとリファクタリング前のコード
//    public List<String> generateVariantsForMora(String mora) {
//		  List<String> variants = new ArrayList<>();
//
//        var result = normalizationService.normalizeWithCandidates(mora);
//
//        for (String candidate : result.getCandidates()) {
//            String normalized = convertMora(candidate);
//            
//            // 空文字列は追加しない
//            if (!normalized.isBlank()) {
//                variants.add(normalized);
//            }
//        }
//
//        // variants が空の場合は mora をそのまま返す
//        if (variants.isEmpty()) {
//            System.err.println("警告: mora '" + mora + "' から variants を生成できませんでした");
//            return List.of(mora);
//        }
//
//        return variants;
//    }
}