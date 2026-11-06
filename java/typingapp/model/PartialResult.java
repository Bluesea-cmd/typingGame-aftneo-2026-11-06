package typingapp.model;

import java.util.Objects;

public record PartialResult(
		String displayText,		// 表示する正しい文字
        String correctPart,		// 正解回答
        Boolean wrongText,		// 間違えているかどうか
        String remainingPart,	// 残りの判定できてない部分
        int wrongCharCount,		// 間違えた文字数
        boolean[] wrongFlags	// 文字色のフラグ
) {
    public PartialResult {
        Objects.requireNonNull(displayText, "displayText must not be null");
        Objects.requireNonNull(correctPart, "correctPart must not be null");
        Objects.requireNonNull(remainingPart, "remainingPart must not be null");
    }
}