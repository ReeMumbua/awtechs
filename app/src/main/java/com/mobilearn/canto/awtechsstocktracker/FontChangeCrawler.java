package com.mobilearn.canto.awtechsstocktracker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by canto on 1/11/2018.
 */

public class FontChangeCrawler
{
    private Typeface typeface;

    public FontChangeCrawler(Typeface typeface)
    {
        this.typeface = typeface;
    }

    public FontChangeCrawler(Context con)
    {
        typeface = Typeface.createFromAsset(con.getAssets(), "fonts/robotcon.ttf");
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                // base case
                ((TextView) child).setTypeface(typeface);
            }
            else if(child instanceof EditText)
            {
                // base case
                ((EditText) child).setTypeface(typeface);
            }
        }
    }
}