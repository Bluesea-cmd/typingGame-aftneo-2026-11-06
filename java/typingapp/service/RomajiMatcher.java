package typingapp.service;

import java.util.*;

public class RomajiMatcher {

    private final RomajiVariantService variantService;

    // モーラごとの variants
    private List<List<String>> allMoraVariants;

    // 現在の状態群
    private List<State> states;

    private String moraJoined;
    
    private String preview;
    
    //追加
    private final List<List<State>> history = new ArrayList<>();
    
    public enum InputResult {
        CORRECT,   // 正しく1文字進んだ
        WRONG,     // 間違えた（状態はそのまま）
        COMPLETE   // 最後まで入力完了
    };
       
    public record InputRecord(
    	    InputType type,
    	    int previewIndex
    	) {}
    
    public record CharacterStatus(
            char character,
            boolean correct,
            int previewIndex
    ) {}
    
    private final List<InputRecord> inputHistory = new ArrayList<>();
    private final List<CharacterStatus> characterHistory = new ArrayList<>();

    private int correctInputLength;

    /** 状態クラス */
    private record State(
        int moraIndex,
        int charIndex,
        int previewIndex,
        String currentVariant,
        List<String> selectedVariants
    ) {}
    
    RomajiMatcher(RomajiVariantService variantService) {
        this.variantService = Objects.requireNonNull( variantService, "variantService must not be null");
    }
    
    public enum InputType {
        CORRECT,
        WRONG
    }
    
    public List<State> getStates() {
        return states;
    }
    
    /** 問題開始時に呼ぶ */
    public void init(String moraRomajiJoined) {
    	
        this.moraJoined = Objects.requireNonNull( moraRomajiJoined, "moraRomajiJoined must not be null");
    	this.preview = variantService.getPreview(moraJoined);
    	
        allMoraVariants = new ArrayList<>();

        String[] moraArray = moraRomajiJoined.split("-", -1);

        for (String mora : moraArray) {

            if (mora.isEmpty()) {
                continue;
            }

            allMoraVariants.add(
                    variantService.generateVariantsForMora(mora));
        }

        System.out.println("===== INIT =====");

        for (int i = 0; i < allMoraVariants.size(); i++) {
            System.out.println(i + " : " + allMoraVariants.get(i));
        }

        initializeStates();
        history.clear();
        inputHistory.clear();

        characterHistory.clear();
        
        correctInputLength = 0;
        history.add(copyStates(states));
    }
    
    private void initializeStates() {

        states = new ArrayList<>();

        if (allMoraVariants == null || allMoraVariants.isEmpty()) {
            return;
        }

        for (String variant : allMoraVariants.get(0)) {
        	if (variant == null || variant.isEmpty()) {
        		continue;
        	}
        	
        	states.add(new State( 0, 0, 0, variant,new ArrayList<>()));
        }
    }
    
    private List<State> copyStates(List<State> src) {
        return new ArrayList<>(src);
    }

    public InputResult input(char c) {

        if (states == null
                || states.isEmpty()) {

            return InputResult.WRONG;
        }

        List<State> nextStates =
                new ArrayList<>();

        /*
         * 現在の候補をすべて調べる。
         *
         * 例:
         *
         * s
         *
         * の時点では、
         *
         * sha
         * sya
         *
         * の両方を候補として持つ。
         */
        for (State s : states) {

            // すでに完成している状態は無視
            if (s.moraIndex()
                    >= allMoraVariants.size()) {

                continue;
            }

            String currentVariant =
                    s.currentVariant();

            if (currentVariant == null
                    || currentVariant.isEmpty()) {

                continue;
            }

            System.out.println(
                    "OK - モーラ"
                    + s.moraIndex()
                    + "のバリアント: "
                    + allMoraVariants.get(
                            s.moraIndex()
                    )
            );

            /*
             * 現在のvariantと一致する場合
             */
            if (s.charIndex()
                    < currentVariant.length()
                    && currentVariant.charAt(
                            s.charIndex()
                    ) == c) {

                System.out.println(
                        "    → 一致! charIndex="
                        + s.charIndex()
                        + " / 必要="
                        + (currentVariant.length() - 1)
                );

                /*
                 * 現在のvariantが完成
                 */
                if (s.charIndex() + 1
                        == currentVariant.length()) {

                    System.out.println(
                            "    → バリアント「"
                            + currentVariant
                            + "」完成!"
                    );

                    /*
                     * 末尾の「ん」のn一文字は
                     * 確定させない。
                     *
                     * nn / n' の候補が残っていれば、
                     * そちらを優先できるようにする。
                     */
                    if ((currentVariant.equals("n")
                            || currentVariant.equals("nn")
                            || currentVariant.equals("n'"))
                            && s.charIndex() == 0
                            && s.moraIndex()
                            == allMoraVariants.size() - 1) {

                        System.out.println(
                                "    → 特殊: "
                                + "末尾の「ん」は確定保留"
                        );

                        nextStates.add(
                                new State(
                                        s.moraIndex(),
                                        s.charIndex() + 1,
                                        nextPreviewIndex( currentVariant, s.charIndex(), s.previewIndex()),
                                        currentVariant,
                                        s.selectedVariants()
                                )
                        );

                        continue;
                    }

                    /*
                     * 今回選択されたvariantを
                     * 確定履歴へ追加
                     */
                    List<String> selected =
                            new ArrayList<>(
                                    s.selectedVariants()
                            );

                    selected.add(currentVariant);

                    int nextMora =
                            s.moraIndex() + 1;

                    /*
                     * 次のモーラへ進む
                     */
                    if (nextMora
                            < allMoraVariants.size()) {

                        System.out.println(
                                "    → 次のモーラ("
                                + nextMora
                                + ")へ遷移。バリアント: "
                                + allMoraVariants.get(
                                        nextMora
                                )
                        );

                        for (String nextVariant :
                                allMoraVariants.get(
                                        nextMora
                                )) {

                            if (nextVariant == null
                                    || nextVariant.isEmpty()) {
                                continue;
                            }

                            nextStates.add(
                                    new State(
                                            nextMora,
                                            0,
                                            nextPreviewIndex( currentVariant, s.charIndex(), s.previewIndex()),
                                            nextVariant,
                                            selected
                                    )
                            );
                        }

                    } else {

                        /*
                         * 全モーラ完成
                         */
                        System.out.println(
                                "    → 全モーラ完成!"
                        );

                        nextStates.add(
                                new State(
                                        nextMora,
                                        0,
                                        nextPreviewIndex( currentVariant, s.charIndex(), s.previewIndex()),
                                        null,
                                        selected
                                )
                        );
                    }

                } else {

                    /*
                     * variantの途中
                     */
                    System.out.println(
                            "    → charIndex進行: "
                            + s.charIndex()
                            + " → "
                            + (s.charIndex() + 1)
                    );

                    nextStates.add(
                            new State(
                                    s.moraIndex(),
                                    s.charIndex() + 1,
                                    nextPreviewIndex( currentVariant, s.charIndex(), s.previewIndex()),
                                    currentVariant,
                                    s.selectedVariants()
                            )
                    );
                }
            }
        }

        /*
         * 正しい候補が1つ以上残った
         */
        if (!nextStates.isEmpty()) {

            correctInputLength++;

            states = deduplicate(nextStates);

            int previewIndex = getCorrectPreview().length();

            characterHistory.add(
                    new CharacterStatus(
                            c,
                            true,
                            previewIndex
                    )
            );

            history.add(
                    copyStates(states)
            );

            inputHistory.add(
                    new InputRecord(
                            InputType.CORRECT,
                            previewIndex
                    )
            );

            if (isComplete()) {

                return InputResult.COMPLETE;
            }

            return InputResult.CORRECT;
        }

        /*
         * ★重要
         *
         * 間違った文字の場合、
         * 状態を一切変更しない。
         *
         * 以前のコードにあった
         *
         * 「次のモーラへ再同期」
         *
         * は削除する。
         *
         * これによって、
         *
         * 正解: s
         * ミス : x
         * 正解: h
         *
         * のような入力でも、
         *
         * s
         * ↓
         * x → 状態維持
         * ↓
         * h
         *
         * とできる。
         */
        State best =
                getBestState();

        int previewIndex =
                best == null
                        ? 0
                        : getCorrectPreview().length();

        inputHistory.add(
                new InputRecord(
                        InputType.WRONG,
                        previewIndex
                )
        );

        characterHistory.add(
                new CharacterStatus(
                        c,
                        false,
                        previewIndex
                )
        );

        System.out.println(
                "× ミス入力（状態維持）: '"
                + c + "'"
        );

        return InputResult.WRONG;
    }

    
//    /** 1文字入力するたびに呼ぶ */
//    public InputResult input(char c) {
//        List<State> nextStates = new ArrayList<>();
//        
//        for (int stateIdx = 0; stateIdx < states.size(); stateIdx++) {
//            State s = states.get(stateIdx);
//        	
//            // すでに終了してる状態は無視
//            if (s.moraIndex >= allMoraVariants.size()) {
//                System.out.println("スキップ (終了済み状態)");
//                continue;
//            }
//
//            List<String> variants = allMoraVariants.get(s.moraIndex);
//            
//            if (variants == null || variants.isEmpty()) {
//                System.err.println("警告: モーラ " + s.moraIndex + " がバリアントなし");
//                continue;
//            }
//            
//            System.out.println("OK - モーラ" + s.moraIndex + "のバリアント: " + variants);
//            
//                // 今のvariantと一致しているものだけ進める
//            	if (s.charIndex < s.currentVariant.length()
//            	        && s.currentVariant.charAt(s.charIndex) == c) {
//            	    
//            	    System.out.println("    → 一致! charIndex=" + s.charIndex + " / 必要=" + (s.currentVariant.length()-1));
//
//            	    if (s.charIndex + 1 == s.currentVariant.length()) {
//            	        System.out.println("    → バリアント「" + s.currentVariant + "」完成!");
//                     	
//            	    	//追加
//            	    	// 最後の「ん」は n 1文字では確定しない
//            	    	if ((s.currentVariant.equals("n")|| s.currentVariant.equals("nn")|| s.currentVariant.equals("n'")) && s.charIndex == 0 && s.moraIndex() == allMoraVariants.size() - 1) {
//            	    	    System.out.println("    → 特殊: 末尾の「ん」は確定保留");
//            	            nextStates.add(
//            	                new State(
//            	                    s.moraIndex(),
//            	                    s.charIndex() + 1,
//            	                    nextPreviewIndex(s, c),
//            	                    s.currentVariant(),
//            	                    s.selectedVariants()
//            	                )
//            	            );
//
//            	            continue;
//            	        }
//            	        
//            	       // ここまで追加
//            	        List<String> historyList = new ArrayList<>(s.selectedVariants());	//追加
//            	        
//            	        historyList.add(s.currentVariant());
//            	        
//            	        System.out.println("    → 確定履歴: " + historyList);
//            	        
//            	        int nextMora = s.moraIndex() + 1;
//            	        int nextPreview = nextPreviewIndex(s, c);
//            	        
//            	        if (nextMora < allMoraVariants.size()) {
//            	        	System.out.println("    → 次のモーラ(" + nextMora + ")へ遷移。バリアント: " + allMoraVariants.get(nextMora));
//
//            	            for (String nextVariant : allMoraVariants.get(nextMora)) {
//            	                nextStates.add( new State( nextMora, 0, nextPreview, nextVariant, historyList));
//            	            }
//            	        } else {
//            	        	System.out.println("    → 全モーラ完成!");
//            	            nextStates.add( new State( nextMora, 0, nextPreview, null, historyList));
//            	        }
//
//            	    } else {
//            	        System.out.println("    → charIndex進行: " + s.charIndex + " → " + (s.charIndex+1));
//            	        nextStates.add( new State( s.moraIndex, s.charIndex + 1, nextPreviewIndex(s, c),s.currentVariant, s.selectedVariants()));
//            	    }
//            	    
//            	} else {
//            	    System.out.println("    × 不一致");
//            	    
////            	     再同期処理
//            	    int nextMora = s.moraIndex() + 1;
//
//            	    // 現在モーラを飛ばす
//            	    if (s.charIndex() > 0) {
//            	        nextMora++;
//            	    }
//
//            	    if (nextMora < allMoraVariants.size()) {
//
//            	        for (String nextVariant : allMoraVariants.get(nextMora)) {
//
//            	            if(nextVariant.isEmpty()) {
//            	                continue;
//            	            }
//            	            
//            	            if (nextVariant.charAt(0) == c) {
//
//            	                List<String> historyList = new ArrayList<>(s.selectedVariants());
//
//            	                // 現在のモーラは未完成なので履歴には追加しない
//
//            	                nextStates.add(
//            	                    new State(
//            	                        nextMora,
//            	                        1,
//            	                        getPreviewIndexAfterMora(nextMora),
//            	                        nextVariant,
//            	                        historyList
//            	                    )
//            	                );
//
//            	                System.out.println(" → 再同期 : " + nextVariant);
//            	            }
//            	        }
//            	    }
//            	}
//        }
//        
//        if (!nextStates.isEmpty()) {
//
//        	correctInputLength++;
//        	
//        	states = deduplicate(nextStates);
//        	
//        	characterHistory.add(new CharacterStatus(c, true, getMaxPreviewIndex()));
//        	
//        	history.add(copyStates(states));
//
////        	// 追加
////        	inputHistory.add(InputType.CORRECT);
//        	
//        	int previewIndex = getMaxPreviewIndex();
//
//        	inputHistory.add(
//        	    new InputRecord(
//        	        InputType.CORRECT,
//        	        previewIndex
//        	    )
//        	);
//        	
//        	if (isComplete()) {
//        	    return InputResult.COMPLETE;
//        	}
//
//        	return InputResult.CORRECT;
//        }
//        
//        State best = getBestState();
//
//        int previewIndex = (best == null) ? 0 : best.previewIndex();
//
//        inputHistory.add(
//            new InputRecord(
//                InputType.WRONG,
//                previewIndex
//            )
//        );
//        
////        // 追加
////        inputHistory.add(InputType.WRONG);
//        
////        int pos = getCorrectInputLength();
////
////        if (!wrongPositions.contains(pos)) {
////            wrongPositions.add(pos);
////        }
//        
//        System.out.println("× ミス入力（状態維持）");
//
//        characterHistory.add( new CharacterStatus(c, false, getMaxPreviewIndex())
//        );
//
//        return InputResult.WRONG;
//    }


public void backspace() {

    // 削除できる入力履歴がなければ何もしない
    if (inputHistory.isEmpty()) {
        return;
    }

    // 最後に入力した文字の記録を取得して削除
    InputRecord last =
            inputHistory.remove(inputHistory.size() - 1);

    // 文字色・入力文字の履歴も1文字分だけ削除
    if (!characterHistory.isEmpty()) {
        characterHistory.remove(characterHistory.size() - 1);
    }

    // ミス入力の場合、判定状態は巻き戻さない
    if (last.type() == InputType.WRONG) {
        return;
    }

    // 正解入力の場合だけ、判定状態を1文字分戻す
    if (history.size() > 1) {
        history.remove(history.size() - 1);

        states = copyStates(
                history.get(history.size() - 1)
        );
    }

    // 正解入力文字数を更新
    correctInputLength = getMaxPreviewIndex();
}
    
    // 追加
//    private boolean isCompleteState(State s) {
//
//        if (s.moraIndex != allMoraVariants.size()) {
//            return false;
//        }
//
//        String typed = s.typed;
//
//        // 最後が nn で終わっているなら完成
//        if (typed.endsWith("nn")) {
//            return true;
//        }
//
//        // n単体終了は禁止
//        if (typed.endsWith("n")) {
//            return false;
//        }
//
//        return true;
//    }
    
    /** 完全一致判定 */
    public boolean isComplete() {

        // まず状態的に完了状態が存在すること
        boolean hasCompleteState = states.stream().anyMatch(s -> s.moraIndex() == allMoraVariants.size());

        if (!hasCompleteState) {
            return false;
        }

        // 履歴に未解消のミス入力が残っている場合は完了扱いにしない
        boolean hasUnresolvedWrong = inputHistory.stream().anyMatch(r -> r.type() == InputType.WRONG);

        if (hasUnresolvedWrong) {
            return false;
        }

        return true;
        
////        System.out.println("allMoraVariants = " + allMoraVariants.size());
////
////        for (State s : states) {
////            System.out.println( "complete check : mora=" + s.moraIndex + " typed=" + s.typed);
////        }
//////	  リファクタリング前2 
//////        return states.stream().anyMatch(this::isCompleteState);
//    	//戻す
//    	return states.stream().anyMatch(s -> s.moraIndex == allMoraVariants.size());
    }

    /** 状態の重複削除（重要） */
    private List<State> deduplicate(List<State> list) {

    	Map<String, State> map = new LinkedHashMap<>();

    	for (State s : list) {
    	    String key = s.moraIndex() + ":" +
    	    			 s.charIndex() + ":" +
    	    			 s.previewIndex() + ":" +
    	    			 s.currentVariant();

    	    State old = map.get(key);

    	    if (old == null || s.previewIndex() > old.previewIndex()) {
    	        map.put(key, s);
    	    }
    	}

    	return new ArrayList<>(map.values());
    }
    
    /**
     * 入力候補の文字位置から、表示位置の次の値を求める。
     * shi の h など、標準表記に対応しない文字では表示位置を進めない。
     */
    private int nextPreviewIndex(String variant, int charIndex, int currentIndex) {
        if (variant == null || charIndex < 0 || charIndex >= variant.length()) {
            return currentIndex;
        }

        // 標準表記 si / ti / tu / wi に対して
        // shi / chi / tsu / whi は途中に余分な文字がある。
        if (charIndex == 1) {
            switch (variant) {
                case "shi":
                case "syu":
                case "chi":
                case "tsu":
                case "whi":
                case "whe":         
                case "kwa":
                case "qwa":
                case "qyi":
                case "qwi":
                case "qye":
                case "qwe":
                case "qwo":               
                case "fwa":
                case "fwi":
                case "fyi":
                case "fwe":
                case "fye":
                case "fwo":      
                case "vyi":
                case "vye":
                case "jya":
                case "zya":
                case "jyu":
                case "zyu":
                case "jye":
                case "zye":
                case "jyo":
                case "zyo":
                case "xyi":
                case "lyi":
                case "xye":
                case "lye":
                case "xtsu":
                case "ltsu":
                	return currentIndex;
                default:
                    break;
            }
        }

        return currentIndex + 1;
    }
    
    private int getMaxPreviewIndex() {

        int max = 0;

        for (State s : states) {
            if (s.previewIndex() > max) {
                max = s.previewIndex();
            }
        }

        return max;
    }

    public int getCorrectInputLength() {
        return correctInputLength;
    }
    
//    public int getCorrectInputLength() {
//
//        int max = 0;
//
//        for (State s : states) {
//            if (s.typed().length() > max) {
//                max = s.typed().length();
//            }
//        }
//
//        return max;
//    }
    
    private State getBestState() {
        return states.stream().max(Comparator
                    .comparingInt(State::previewIndex)
                    .thenComparingInt(s -> s.selectedVariants().size())
                    .thenComparingInt(State::charIndex)).orElse(null);
    }
    
    public String getCurrentPreview() {
    	
        if (states.isEmpty()) {
            return "";
        }

        State best = getBestState();

        StringBuilder sb = new StringBuilder();

        // 確定済みモーラ
        for (String variant : best.selectedVariants()) {
            sb.append(variant);
        }
        
        // 確定済みモーラ(リファクタリング前)
        //sb.append(best.typed());

        // 現在入力中モーラ
        if (best.moraIndex() < allMoraVariants.size()) {
        	
        	//追加
            String current = best.currentVariant();
            
            if (best.charIndex() < current.length()) {
                sb.append(current.substring(0, best.charIndex()));
            }            
//            // 現在モーラの残り
//            sb.append(current.substring(best.charIndex()));
        }

//        // 修正: 残りのモーラを表示
//        for (int i = best.moraIndex() + 1; i < allMoraVariants.size(); i++) {
//
//            List<String> variants = allMoraVariants.get(i);
//
//            if (!variants.isEmpty()) {
//                sb.append(variants.get(0));
//            }
//        }

//		残りのモーラ(リファクタリング前)
//      for (int i = best.moraIndex() + 1; i < allMoraVariants.size(); i++) {
//      	sb.append(allMoraVariants.get(i).get(0));
//      }
        
        return sb.toString();
    }
    
    public String getCorrectPreview() {

        if (states.isEmpty()) {
            return "";
        }

        return preview.substring(0, getMaxPreviewIndex());
    }
    
    public String getRemainingPreview() {

        if (states.isEmpty()) {
            return preview;
        }

        return preview.substring(getMaxPreviewIndex());
    }
    
    public void reset() {
        initializeStates();
        
        inputHistory.clear();
        correctInputLength = 0;
    }
    
    public List<InputRecord> getInputHistory() {
        return inputHistory;
    }
    
    public List<CharacterStatus> getCharacterHistory() {
        return characterHistory;
    }
}
