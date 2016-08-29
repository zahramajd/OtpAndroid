/*
 * ========================================*
 * Created By :
 *                Hamideh Hosseini (hamideh.hosseini.t@gmail.com)
 *                Zahra Majdabadi  (zahra.majabadi95@gmail.com)
 * =======================================
*/
package ir.taban.otp.ui;

import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoResizeTextView extends TextView {

    // Minimum text size for this text view
    public static final float MIN_TEXT_SIZE = 26;

    // Maximum text size for this text view
    public static final float MAX_TEXT_SIZE = 128;

    private static final int BISECTION_LOOP_WATCH_DOG = 30;

    // Interface for resize notifications
    public interface OnTextResizeListener {
        public void onTextResize(TextView textView, float oldSize, float newSize);
    }

    // Our ellipse string
    private static final String mEllipsis = "...";


    private OnTextResizeListener mTextResizeListener;


    private boolean mNeedsResize = false;

    private float mTextSize;

    private float mMaxTextSize = MAX_TEXT_SIZE;

    private float mMinTextSize = MIN_TEXT_SIZE;

    private float mSpacingMult = 1.0f;

    private float mSpacingAdd = 0.0f;

    private boolean mAddEllipsis = true;

    public AutoResizeTextView(Context context) {
        this(context, null);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSize = getTextSize();
    }


    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        mNeedsResize = true;
        resetTextSize();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mNeedsResize = true;
        }
    }


    public void setOnResizeListener(OnTextResizeListener listener) {
        mTextResizeListener = listener;
    }


    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }


    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }


    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }


    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
        requestLayout();
        invalidate();
    }


    public float getMaxTextSize() {
        return mMaxTextSize;
    }


    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }


    public float getMinTextSize() {
        return mMinTextSize;
    }


    public void setAddEllipsis(boolean addEllipsis) {
        mAddEllipsis = addEllipsis;
    }


    public boolean getAddEllipsis() {
        return mAddEllipsis;
    }


    public void resetTextSize() {
        if(mTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            //mMaxTextSize = mTextSize;
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(changed || mNeedsResize) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
            resizeText(widthLimit, heightLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }


    /**
     * Resize the text size with default width and height
     */
    public void resizeText() {
        int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
        resizeText(widthLimit, heightLimit);
    }

    /**
     * Resize the text size with specified width and height
     * @param width
     * @param height
     */
    public void resizeText(int width, int height) {
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text
        if(text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();

        // Bisection method: fast & precise
        float lower = mMinTextSize;
        float upper = mMaxTextSize;
        int loop_counter=1;
        float targetTextSize = (lower+upper)/2;
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        while(loop_counter < BISECTION_LOOP_WATCH_DOG && upper - lower > 1) {
            targetTextSize = (lower+upper)/2;
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            if(textHeight > height)
                upper = targetTextSize;
            else
                lower = targetTextSize;
            loop_counter++;
        }

        targetTextSize = lower;
        textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        if(mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {

            TextPaint paintCopy = new TextPaint(textPaint);
            paintCopy.setTextSize(targetTextSize);
            StaticLayout layout = new StaticLayout(text, paintCopy, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);

            if(layout.getLineCount() > 0) {

                int lastLine = layout.getLineForVertical(height) - 1;

                if(lastLine < 0) {
                    setText("");
                }
                // Otherwise, trim to the previous line and add an ellipsis
                else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = paintCopy.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the ellipsis
                    while(width < lineWidth + ellipseWidth) {
                        lineWidth = paintCopy.measureText(text.subSequence(start, --end + 1).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if(mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }
        // Reset force resize flag
        mNeedsResize = false;
    }

    // Set the text size of the text paint object and use a static layout to render text off screen before measuring
    private int getTextHeight(CharSequence source, TextPaint originalPaint, int width, float textSize) {
        TextPaint paint = new TextPaint(originalPaint);
        // Update the text paint object
        paint.setTextSize(textSize);
        // Measure using a static layout
        StaticLayout layout = new StaticLayout(source, paint, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getHeight();
    }

}