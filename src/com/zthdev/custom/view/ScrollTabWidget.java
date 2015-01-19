package com.zthdev.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TabWidget;

/**
 * 
 * 类名称：ScrollTabWidget <br>  
 * 类描述：可滑动切换的TabWidget头 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-10-20 下午2:42:22 <br>  
 * @version V1.0
 */
public class ScrollTabWidget extends TabWidget
{
    private OnTabSelectionChanged mSelectionChangedListener;
    
    private int mSelectedTab = -1;
    
    public ScrollTabWidget(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public ScrollTabWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ScrollTabWidget(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    void setTabSelectionListener(OnTabSelectionChanged listener) 
    {
        mSelectionChangedListener = listener;
    }
    
    @Override
    public void setCurrentTab(int index)
    {
        super.setCurrentTab(index);
        
        mSelectedTab = index;
    }

    public void onFocusChange(View v, boolean hasFocus) 
    {
        if (v == this && hasFocus && getTabCount() > 0) 
        {
            getChildTabViewAt(mSelectedTab).requestFocus();
            return;
        }

        if (hasFocus) 
        {
            int i = 0;
            int numTabs = getTabCount();
            while (i < numTabs) {
                if (getChildTabViewAt(i) == v) 
                {
                    setCurrentTab(i);
                    mSelectionChangedListener.onTabSelectionChanged(i, false);
                    if (isShown()) 
                    {
                        // a tab is focused so send an event to announce the tab widget state
                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                    }
                    break;
                }
                i++;
            }
        }
    }
    
    public void addView(View child) 
    {
        super.addView(child);

        // TODO: detect this via geometry with a tabwidget listener rather
        // than potentially interfere with the view's listener
        child.setOnClickListener(new TabClickListener(getTabCount() - 1));
        child.setOnFocusChangeListener(this);
    }
    
 // registered with each tab indicator so we can notify tab host
    private class TabClickListener implements OnClickListener 
    {
        private final int mTabIndex;

        private TabClickListener(int tabIndex) 
        {
            mTabIndex = tabIndex;
        }

        public void onClick(View v) 
        {
            mSelectionChangedListener.onTabSelectionChanged(mTabIndex, true);
        }
    }
    
    static interface OnTabSelectionChanged 
    {
        /**
         * Informs the TabHost which tab was selected. It also indicates
         * if the tab was clicked/pressed or just focused into.
         *
         * @param tabIndex index of the tab that was selected
         * @param clicked whether the selection changed due to a touch/click
         * or due to focus entering the tab through navigation. Pass true
         * if it was due to a press/click and false otherwise.
         */
        void onTabSelectionChanged(int tabIndex, boolean clicked);
    }
}
