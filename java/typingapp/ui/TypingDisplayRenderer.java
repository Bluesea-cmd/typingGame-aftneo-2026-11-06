package typingapp.ui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import typingapp.model.PartialResult;

public class TypingDisplayRenderer {

    private final AttributeSet green;
    private final AttributeSet red;
    private final AttributeSet gray;
    
    public TypingDisplayRenderer() {

        SimpleAttributeSet g = new SimpleAttributeSet();
        StyleConstants.setForeground(g, Color.GREEN);
        green = g;

        SimpleAttributeSet r = new SimpleAttributeSet();
        StyleConstants.setForeground(r, Color.RED);
        red = r;

        SimpleAttributeSet gr = new SimpleAttributeSet();
        StyleConstants.setForeground(gr, Color.GRAY);
        gray = gr;
    }

    public void render(JTextPane typingLabel, PartialResult result) {

        StyledDocument doc = typingLabel.getStyledDocument();

        try {
        	// 追加
            doc.remove(0, doc.getLength());

//            String display = result.displayText();
            String correctPart = result.correctPart();
            String remainingPart = result.remainingPart();
            
            String display = correctPart + remainingPart;

////            if (result.input().length() <= result.correctPart().length()) {
////                display += result.remainingPart();
////            }

            boolean[] wrongFlags = result.wrongFlags();


            int correctLength = correctPart.length();
            int displayLength = display.length();

            for (int i = 0; i < displayLength; i++) {
                AttributeSet attr;
                
             // 1. まずミス入力位置（赤色）かどうかを最優先でチェック
                if (wrongFlags != null && i < wrongFlags.length && wrongFlags[i]) {
                    attr = red;
                } 
                // 2. 次に未入力位置（灰色）かをチェック
                else if (i >= correctLength) {
                    attr = gray;
                } 
                // 3. それ以外は入力成功位置（緑色）
                else {
                    attr = green;
                }
                
                doc.insertString(
                        doc.getLength(),
                        String.valueOf(display.charAt(i)),
                        attr);
            }
            
//            if (!result.remainingPart().isEmpty()) {
//
//                doc.insertString(
//                    doc.getLength(),
//                    result.remainingPart(),
//                    gray
//                );
//            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}