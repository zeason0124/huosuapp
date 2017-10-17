package com.huosuapp.text.base;

import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.liang530.fragment.BaseFragment;

/**
 * Created by hongliang on 16-6-7.
 */
public class AutoBaseFragment extends BaseFragment {
    Unbinder unbinder;
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        Unbinder unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }
}