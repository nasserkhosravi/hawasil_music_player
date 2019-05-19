package com.nasserkhosravi.hawasilmusicplayer.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.FloatRange;
import com.nasserkhosravi.hawasilmusicplayer.R;

/**
 * This is a fork with some changes
 * 1- I added disabling and enabling feature
 * 2- Original codes did not work in recycle view so I removed mBound. see problem in https://github.com/Devlight/ShadowLayout/issues/19
 */
public class ShadowLayout extends FrameLayout {

    // Default shadow values
    private final static float DEFAULT_SHADOW_RADIUS = 30.0F;
    private final static float DEFAULT_SHADOW_DISTANCE = 15.0F;
    private final static float DEFAULT_SHADOW_ANGLE = 45.0F;
    private final static int DEFAULT_SHADOW_COLOR = Color.DKGRAY;
    private final static boolean DEFAULT_IS_ENABLE_ANGLE = false;

    // Shadow bounds values
    private final static int MAX_ALPHA = 255;
    private final static float MAX_ANGLE = 360.0F;
    private final static float MIN_RADIUS = 0.1F;
    private final static float MIN_ANGLE = 0.0F;
    private final Canvas mCanvas = new Canvas();
    // Shadow paint
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setFilterBitmap(true);
        }
    };
    // Shadow bitmap and canvas
    private Bitmap mBitmap;
    // Check whether need to redraw shadow
    private boolean mInvalidateShadow = true;

    // Detect if shadow is visible
    private boolean mIsShadowed;
    private boolean isEnableAngle;

    // Shadow variables
    private int mShadowColor;
    private int mShadowAlpha;
    private float mShadowRadius;
    private float mShadowDistance;
    private float mShadowAngle = -1;
    private float mShadowDx;
    private float mShadowDy;

    public ShadowLayout(final Context context) {
        this(context, null);
    }

    public ShadowLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, mPaint);

        // Retrieve attributes from xml
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        try {
            setIsShadowed(typedArray.getBoolean(R.styleable.ShadowLayout_sl_shadowed, true));
            setShadowRadius(typedArray.getDimension(R.styleable.ShadowLayout_sl_shadow_radius, DEFAULT_SHADOW_RADIUS));
            setShadowDistance(typedArray.getDimension(R.styleable.ShadowLayout_sl_shadow_distance, DEFAULT_SHADOW_DISTANCE));
            setShadowAngle(typedArray.getInteger(R.styleable.ShadowLayout_sl_shadow_angle, (int) DEFAULT_SHADOW_ANGLE));
            setShadowColor(typedArray.getColor(R.styleable.ShadowLayout_sl_shadow_color, DEFAULT_SHADOW_COLOR));

            isEnableAngle = typedArray.getBoolean(R.styleable.ShadowLayout_sl_is_enable_angle, DEFAULT_IS_ENABLE_ANGLE);
        } finally {
            typedArray.recycle();
        }
    }

    public boolean isShadowed() {
        return mIsShadowed;
    }

    public void setIsShadowed(final boolean isShadowed) {
        mIsShadowed = isShadowed;
        postInvalidate();
    }

    public float getShadowDistance() {
        return mShadowDistance;
    }

    public void setShadowDistance(final float shadowDistance) {
        mShadowDistance = shadowDistance;
        resetShadow();
    }

    public float getShadowAngle() {
        return mShadowAngle;
    }

    public void setShadowAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float shadowAngle) {
        if (isEnableAngle) {
            mShadowAngle = Math.max(MIN_ANGLE, Math.min(shadowAngle, MAX_ANGLE));
            resetShadow();
        }
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public void setShadowRadius(final float shadowRadius) {
        mShadowRadius = Math.max(MIN_RADIUS, shadowRadius);

        if (isInEditMode()) return;
        // Set blur filter to paint
        mPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius, BlurMaskFilter.Blur.NORMAL));
        resetShadow();
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowColor(final int shadowColor) {
        mShadowColor = shadowColor;
        mShadowAlpha = Color.alpha(shadowColor);

        resetShadow();
    }

    public float getShadowDx() {
        return mShadowDx;
    }

    public float getShadowDy() {
        return mShadowDy;
    }

    // Reset shadow layer
    private void resetShadow() {
        // Detect shadow axis offset
        if (isEnableAngle) {
            mShadowDx = (float) ((mShadowDistance) * Math.cos(mShadowAngle / 180.0F * Math.PI));
            mShadowDy = (float) ((mShadowDistance) * Math.sin(mShadowAngle / 180.0F * Math.PI));
        }

        // Set padding for shadow bitmap
        final int padding = (int) (mShadowDistance + mShadowRadius);
        setPadding(padding, padding, padding, padding);
        requestLayout();
    }

    private int adjustShadowAlpha(final boolean adjust) {
        return Color.argb(
                adjust ? MAX_ALPHA : mShadowAlpha,
                Color.red(mShadowColor),
                Color.green(mShadowColor),
                Color.blue(mShadowColor)
        );
    }

    @Override
    public void requestLayout() {
        // Redraw shadow
        mInvalidateShadow = true;
        super.requestLayout();
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        // If is not shadowed, skip
        if (mIsShadowed) {
            // If need to redraw shadow
            if (mInvalidateShadow) {
                // If bounds is zero
                if (getWidth() != 0 && getHeight() != 0) {
                    // Reset bitmap to bounds
                    mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    // Canvas reset
                    mCanvas.setBitmap(mBitmap);

                    // We just redraw
                    mInvalidateShadow = false;
                    // Main feature of this lib. We create the local copy of all content, so now
                    // we can draw bitmap as a bottom layer of natural canvas.
                    // We draw shadow like blur effect on bitmap, cause of setShadowLayer() method of
                    // paint does`t draw shadow, it draw another copy of bitmap
                    super.dispatchDraw(mCanvas);

                    // Get the alpha bounds of bitmap
                    final Bitmap extractedAlpha = mBitmap.extractAlpha();
                    // Clear past content content to draw shadow
                    mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

                    // Draw extracted alpha bounds of our local canvas
                    mPaint.setColor(adjustShadowAlpha(false));
                    mCanvas.drawBitmap(extractedAlpha, mShadowDx, mShadowDy, mPaint);

                    // Recycle and clearReference extracted alpha
                    extractedAlpha.recycle();
                }
//                else {
//                     //Create placeholder bitmap when size is zero and wait until new size coming up
//                    mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
//                }
            }

            // Reset alpha to draw child with full alpha
            mPaint.setColor(adjustShadowAlpha(true));
            // Draw shadow bitmap
            if (mBitmap != null && !mBitmap.isRecycled())
                canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
        }

        // Draw child`s
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCanvas.setBitmap(null);
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}
