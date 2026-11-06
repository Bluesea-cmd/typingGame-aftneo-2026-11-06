package typingapp.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record TypingWord(
        String word,
        String hiragana,
        List<String> romajiMora
	) {
    public TypingWord {
        Objects.requireNonNull(word, "word must not be null");
        if (word.isBlank()) { throw new IllegalArgumentException("word must not be blank"); }

        Objects.requireNonNull(hiragana, "hiragana must not be null");
        if (hiragana.isBlank()) { throw new IllegalArgumentException("hiragana must not be blank"); }

        Objects.requireNonNull(romajiMora, "romajiMora must not be null");
        if (romajiMora.isEmpty()) { throw new IllegalArgumentException("romajiMora must not be empty"); }

        // イミュータブルなコピーを作成
        romajiMora = List.copyOf(romajiMora);
    }
    
    /** 入力文字列が候補に一致するか確認 */
    public int checkAnswer(String input) {
        if (input == null) return -1;
        return romajiMora.indexOf(input);
    }
    
    /**
     * マッチしたか確認するOptional版
     * @param input 入力文字列
     * @return マッチしたモラをOptionalで返す
     */
    public Optional<String> checkAnswerOptional(String input) {
        if (input == null || input.isBlank()) { return Optional.empty(); }
        String trimmed = input.trim();
        if (romajiMora.contains(trimmed)) { return Optional.of(trimmed); }
        return Optional.empty();
    }
    
    public int length() {
        return romajiMora.size();
    }
    
    public String getMoraJoined() {
        return String.join("-", romajiMora);
    }
    
    /**
     * 指定インデックスのモラを取得
     * @param index インデックス
     * @return モラ文字列
     * @throws IndexOutOfBoundsException インデックスが範囲外の場合
     */
    public String getMora(int index) {
        return romajiMora.get(index);
    }
}