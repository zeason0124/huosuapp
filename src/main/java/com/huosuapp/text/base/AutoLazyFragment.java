package com.huosuapp.text.base;

import android.view.View;

import com.liang530.fragment.LazyFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hongliang on 16-6-7.
 */
public class AutoLazyFragment extends LazyFragment {
    Unbinder unbinder;
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this, getContentView());
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }
}