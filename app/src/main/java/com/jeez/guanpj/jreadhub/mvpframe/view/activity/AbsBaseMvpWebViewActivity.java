package com.jeez.guanpj.jreadhub.mvpframe.view.activity;

import android.os.Bundle;

import com.jeez.guanpj.jreadhub.mvpframe.model.IBaseModel;
import com.jeez.guanpj.jreadhub.mvpframe.presenter.AbsBasePresenter;
import com.jeez.guanpj.jreadhub.mvpframe.view.IBaseView;
import com.jeez.guanpj.jreadhub.util.TUtil;

/**
 * Created by Jie on 2016-11-2.
 */

public abstract class AbsBaseMvpWebViewActivity<P extends AbsBasePresenter, M extends IBaseModel> extends AbsBaseWebViewActivity implements IBaseView {
    public P mPresenter;
    public M mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        mPresenter.onAttatch(mModel, this);
    }

    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onInternetError() {

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
        super.onDestroy();
    }
}