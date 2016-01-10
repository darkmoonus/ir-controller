package vn.fpt.ircontroller.customizes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import vn.fpt.ircontroller.R;

@SuppressLint("Override")
public class MyGifView extends View {
    protected static final int DEFAULT_MOVIEW_DURATION = 1000;
    protected int mMovieResourceId;
    protected Movie mMovie;
    protected long mMovieStart;
    protected int mCurrentAnimationTime = 0;
    protected float mLeft;
    protected float mTop;
    protected float mScale;
    protected int mMeasuredMovieWidth;
    protected int mMeasuredMovieHeight;
    protected volatile boolean mPaused = false;
    protected boolean mVisible = true;

    public MyGifView(Context context) {
        this(context, null);
    }

    public MyGifView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public MyGifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttributes(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    protected void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView, defStyle, R.style.Widget_GifView);
        mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1);
        mPaused = array.getBoolean(R.styleable.GifView_paused, false);
        array.recycle();
        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }

    public void setMovieResource(int movieResId) {
        this.mMovieResourceId = movieResId;
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        requestLayout();
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
        requestLayout();
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovieTime(int time) {
        mCurrentAnimationTime = time;
        invalidate();
    }

    public void setPaused(boolean paused) {
        this.mPaused = paused;
        if (!paused) {
            mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime;
        }
        invalidate();
    }

    public boolean isPaused() {
        return this.mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            float scaleH = 1f;
            int measureModeWidth = MeasureSpec.getMode(widthMeasureSpec);
            if (measureModeWidth != MeasureSpec.UNSPECIFIED) {
                int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
                if (movieWidth > maximumWidth) {
                    scaleH = (float) movieWidth / (float) maximumWidth;
                }
            }
            float scaleW = 1f;
            int measureModeHeight = MeasureSpec.getMode(heightMeasureSpec);
            if (measureModeHeight != MeasureSpec.UNSPECIFIED) {
                int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
                if (movieHeight > maximumHeight) {
                    scaleW = (float) movieHeight / (float) maximumHeight;
                }
            }
            mScale = 1f / Math.max(scaleH, scaleW);
            mMeasuredMovieWidth = (int) (movieWidth * mScale);
            mMeasuredMovieHeight = (int) (movieHeight * mScale);
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!mPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    @SuppressLint("NewApi")
    protected void invalidateView() {
        if (mVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }

    protected void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    protected void drawMovieFrame(Canvas canvas) {
        mMovie.setTime(mCurrentAnimationTime);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
        canvas.restore();
    }

    @SuppressLint("NewApi")
    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mCurrentAnimationTime;
        result = prime * result + Float.floatToIntBits(mLeft);
        result = prime * result + mMeasuredMovieHeight;
        result = prime * result + mMeasuredMovieWidth;
        result = prime * result + ((mMovie == null) ? 0 : mMovie.hashCode());
        result = prime * result + mMovieResourceId;
        result = prime * result + (int) (mMovieStart ^ (mMovieStart >>> 32));
        result = prime * result + (mPaused ? 1231 : 1237);
        result = prime * result + Float.floatToIntBits(mScale);
        result = prime * result + Float.floatToIntBits(mTop);
        result = prime * result + (mVisible ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MyGifView other = (MyGifView) obj;
        if (mCurrentAnimationTime != other.mCurrentAnimationTime) {
            return false;
        }
        if (Float.floatToIntBits(mLeft) != Float.floatToIntBits(other.mLeft)) {
            return false;
        }
        if (mMeasuredMovieHeight != other.mMeasuredMovieHeight) {
            return false;
        }
        if (mMeasuredMovieWidth != other.mMeasuredMovieWidth) {
            return false;
        }
        if (mMovie == null) {
            if (other.mMovie != null) {
                return false;
            }
        } else if (!mMovie.equals(other.mMovie)) {
            return false;
        }
        if (mMovieResourceId != other.mMovieResourceId) {
            return false;
        }
        if (mMovieStart != other.mMovieStart) {
            return false;
        }
        if (mPaused != other.mPaused) {
            return false;
        }
        if (Float.floatToIntBits(mScale) != Float.floatToIntBits(other.mScale)) {
            return false;
        }
        if (Float.floatToIntBits(mTop) != Float.floatToIntBits(other.mTop)) {
            return false;
        }
        if (mVisible != other.mVisible) {
            return false;
        }
        return true;
    }
}
