package typingapp.model;

public enum InputResult {
    CORRECT,   // 正しく1文字進んだ
    WRONG,     // 間違えた（状態はそのまま）
    COMPLETE   // 最後まで入力完了
}