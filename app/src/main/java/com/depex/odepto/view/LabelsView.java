package com.depex.odepto.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.depex.odepto.Label;
import com.depex.odepto.ViewRender;
import com.depex.odepto.util.ColorBlindPattern;
import com.depex.odepto.util.LabelUtils;
import com.depex.odepto.util.MiscUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LabelsView  extends View{
    private static final int DEFAULT_CORNER_RADIUS_DP = 2;
    private static final int DEFAULT_LABEL_HEIGHT_DP = 16;
    private static final int DEFAULT_LABEL_WIDTH_DP = 32;
    private static final int DEFAULT_SPACING_DP = 3;
    private Map<ColorBlindPattern, BitmapShader> colorBlindShaders;
    private List<String> colors;
    private final float cornerRadius;

    private final float labelHeight;
    private final float labelWidth;
    private Paint paint;
    private RectF rect;
    private Paint shaderPaint;
    private final float spacing;
    private boolean colorBlind;


    public LabelsView(Context context) {
        this(context, null);
    }

    public LabelsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LabelsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = context.getResources().getDisplayMetrics().density;
        this.labelHeight = 16.0f * density;
        this.labelWidth = 32.0f * density;
        this.cornerRadius = 2.0f * density;
        this.spacing = 3.0f * density;
        colorBlind=true;
    }

    public void setLabels(List<Label> labels) {
        List<String> colors = new ArrayList();
        for (Label label : labels) {
            String colorName = label.getColor();
            if (!MiscUtils.isNullOrEmpty(colorName)) {
                colors.add(colorName);
            }
        }
        setColors(colors);
    }

    void setColors(List<String> colors) {
        if (!MiscUtils.equals(this.colors, colors)) {
            if (this.colors != null || colors.size() != 0) {
                if (this.colors == null) {
                    this.colors = new ArrayList();
                }
                int initialSize = this.colors.size();
                this.colors.clear();
                if (colors != null) {
                    this.colors.addAll(colors);
                }
                if (this.colors.size() != initialSize) {
                    requestLayout();
                } else {
                    invalidate();
                }
            }
        }
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int numColors = this.colors != null ? this.colors.size() : 0;
        if (numColors == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            float targetWidth;
            float desiredWidth = (((float) (getPaddingLeft() + getPaddingRight())) + (((float) numColors) * this.labelWidth)) + (((float) (numColors - 1)) * this.spacing);
            if (widthMode == Integer.MIN_VALUE) {
                targetWidth = Math.min(desiredWidth, (float) widthSize);
            } else {
                targetWidth = desiredWidth;
            }
            width = (int) Math.ceil((double) targetWidth);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float targetHeight;
            int numLines = (int) Math.ceil((double) (((float) numColors) / ((float) getCirclesPerLine(width))));
            float desiredHeight = (((float) (getPaddingTop() + getPaddingBottom())) + (((float) numLines) * this.labelHeight)) + (((float) (numLines - 1)) * this.spacing);
            if (heightMode == Integer.MIN_VALUE) {
                targetHeight = Math.min(desiredHeight, (float) heightSize);
            } else {
                targetHeight = desiredHeight;
            }
            height = (int) Math.ceil((double) targetHeight);
        }
        setMeasuredDimension(width, height);
    }



    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.colors != null) {
            if (this.paint == null) {
                this.paint = new Paint();
                this.paint.setAntiAlias(true);
                this.rect = new RectF();
            }
            int circlesPerLine = getCirclesPerLine(getWidth());
            int numLines = (int) Math.ceil((double) (((float) this.colors.size()) / ((float) circlesPerLine)));
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            for (int a = 0; a < numLines; a++) {
                for (int b = 0; b < circlesPerLine; b++) {
                    int index = (a * circlesPerLine) + b;
                    if (index == this.colors.size()) {
                        break;
                    }
                    this.rect.left = ((float) paddingLeft) + (((float) b) * (this.labelWidth + this.spacing));
                    this.rect.top = ((float) paddingTop) + (((float) a) * (this.labelHeight + this.spacing));
                    this.rect.right = this.rect.left + this.labelWidth;
                    this.rect.bottom = this.rect.top + this.labelHeight;
                    String colorName =  this.colors.get(index);
                    this.paint.setColor(Color.parseColor(colorName));
                    canvas.drawRoundRect(this.rect, this.cornerRadius, this.cornerRadius, this.paint);
                   /* if (this.colorBlind) {
                        ColorBlindPattern pattern = LabelUtils.getColorBlindPattern(colorName);
                        if (pattern != ColorBlindPattern.NONE) {
                            if (this.shaderPaint == null) {
                                this.shaderPaint = new Paint();
                                this.shaderPaint.setAntiAlias(true);
                            }
                            if (this.colorBlindShaders == null) {
                                this.colorBlindShaders = new HashMap();
                            }
                            if (!this.colorBlindShaders.containsKey(pattern)) {
                                this.colorBlindShaders.put(pattern, new BitmapShader(BitmapFactory.decodeResource(getResources(), pattern.resId()), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
                            }
                            this.shaderPaint.setShader((Shader) this.colorBlindShaders.get(pattern));
                            canvas.drawRoundRect(this.rect, this.cornerRadius, this.cornerRadius, this.shaderPaint);
                        }
                    }*/
                }
            }
        }
    }

    private int getCirclesPerLine(int width) {
        int numCircles = (int) Math.floor((double) (((float) width) / (this.labelWidth + (this.spacing / 2.0f))));
        return numCircles == 0 ? 1 : numCircles;
    }
}