package com.youplayer.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.youplayer.player.frame.YouPlayerContainerView;

public class YouPlayerAbsoluteLayout extends ViewGroup {
	
	private int mPaddingLeft;  
    private int mPaddingRight;  
    private int mPaddingTop;  
    private int mPaddingBottom;
    
    public YouPlayerAbsoluteLayout(Context context) {
        this(context, null);
    }

    public YouPlayerAbsoluteLayout(Context context, AttributeSet attrs) {
    	this(context, attrs, 0);
    }

    public YouPlayerAbsoluteLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mPaddingLeft = attrs.getAttributeIntValue(android.R.attr.paddingLeft, 0);  
        mPaddingRight = attrs.getAttributeIntValue(android.R.attr.paddingRight, 0);  
        mPaddingTop = attrs.getAttributeIntValue(android.R.attr.paddingTop, 0);  
        mPaddingBottom = attrs.getAttributeIntValue(android.R.attr.paddingBottom, 0);
    }

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
//        L.v(TAG, "onMeasure", "  onLayoutonMeasure  --> onMeasure");
        int maxHeight = 0;
        int maxWidth = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childRight;
                int childBottom;
                YouPlayerAbsoluteLayout.LayoutParams lp = (YouPlayerAbsoluteLayout.LayoutParams) child.getLayoutParams();
                if(lp.toLeftOf > 0 ) {
                	YouPlayerAbsoluteLayout.LayoutParams targetLp = (LayoutParams) findViewById(lp.toLeftOf).getLayoutParams();
                	lp.x = targetLp.x - lp.fadingEdgeLength;
                	measureChild(child, lp.fadingEdgeLength, heightMeasureSpec);
                } else if(lp.toRightOf > 0 ) {
                	YouPlayerAbsoluteLayout.LayoutParams targetLp = (LayoutParams) findViewById(lp.toRightOf).getLayoutParams();
                	lp.x = targetLp.x + lp.fadingEdgeLength;
                	measureChild(child, lp.fadingEdgeLength, heightMeasureSpec);
                } else {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
                childRight = lp.x + child.getMeasuredWidth();
                childBottom = lp.y + child.getMeasuredHeight();
                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        // Account for padding too
        maxWidth += mPaddingLeft + mPaddingRight;
        maxHeight += mPaddingTop + mPaddingBottom;

        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        
        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }

	@Override
	public void computeScroll() {
		if(YouPlayerContainerView.instance != null) {
			YouPlayerContainerView.instance.onComputeScroll();
		}
		super.computeScroll();
	}

	@Override
    protected void onLayout(boolean changed, int l, int t,
            int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

            	YouPlayerAbsoluteLayout.LayoutParams lp = (YouPlayerAbsoluteLayout.LayoutParams) child.getLayoutParams();
                int childLeft = 0;
                int childTop = mPaddingTop + lp.y;
                if(lp.toLeftOf > 0) {
                	View view = findViewById(lp.toLeftOf);
                	YouPlayerAbsoluteLayout.LayoutParams targetLp = (LayoutParams) view.getLayoutParams();
                	childLeft = targetLp.x - child.getMeasuredWidth();
                } else if(lp.toRightOf > 0){
                	View view = findViewById(lp.toRightOf);
                	YouPlayerAbsoluteLayout.LayoutParams targetLp = (LayoutParams) view.getLayoutParams();
                	childLeft = targetLp.x + view.getMeasuredWidth();
                } else {
                	childLeft = mPaddingLeft + lp.x;
                }
                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            }
        }
    }

	@Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new YouPlayerAbsoluteLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams. 
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof YouPlayerAbsoluteLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * The horizontal, or X, location of the child within the view group.
         */
        public int x;
        /**
         * The vertical, or Y, location of the child within the view group.
         */
        public int y;
        
    	private int fadingEdgeLength = 0;
    	
    	private int toLeftOf = 0;
    	private int toRightOf = 0;

        
        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }

       
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.AbsoluteLayout_Layout);
            x = a.getDimensionPixelOffset(
                    R.styleable.AbsoluteLayout_Layout_layout_x, 0);
            y = a.getDimensionPixelOffset(
                    R.styleable.AbsoluteLayout_Layout_layout_y, 0);
            fadingEdgeLength = a.getDimensionPixelOffset(R.styleable.AbsoluteLayout_Layout_fadingEdgeLength, 0);
            toLeftOf = a.getResourceId(R.styleable.AbsoluteLayout_Layout_layout_toLeftOf, 0);
            toRightOf = a.getResourceId(R.styleable.AbsoluteLayout_Layout_layout_toRightOf, 0);
            a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
    
    public interface ScrollListener {
    	public void onComputeScroll();
    }

}


