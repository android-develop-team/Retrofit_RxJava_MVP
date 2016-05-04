package com.example.y.mvp.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.y.mvp.R;
import com.example.y.mvp.adapter.BaseRecyclerViewAdapter;
import com.example.y.mvp.adapter.ImageListAdapter;
import com.example.y.mvp.constant.Constant;
import com.example.y.mvp.mvp.Bean.ImageListInfo;
import com.example.y.mvp.mvp.presenter.ImageListPresenter;
import com.example.y.mvp.mvp.presenter.ImageListPresenterImpl;
import com.example.y.mvp.mvp.view.ImageListView;
import com.example.y.mvp.utils.LogUtils;
import com.example.y.mvp.utils.UIUtils;
import com.example.y.mvp.widget.MyRecyclerView;

import java.util.LinkedList;
import java.util.List;

/**
 * by y on 2016/4/28.
 */
public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerView.LoadingData, ImageListView {

    private boolean isPrepared;
    private boolean isLoad;
    private View inflate;
    private SwipeRefreshLayout srfLayout;
    private MyRecyclerView recyclerView;
    private LinkedList<ImageListInfo> list;
    private ImageListAdapter adapter;
    private ImageListPresenter imageListPresenter;


    private static int page = 1;

    public static MainFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        MainFragment fragment = new MainFragment();
        bundle.putInt(FRAGMENT_INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View initView() {
        if (inflate == null) {
            inflate = View.inflate(mActivity, R.layout.fragment_main, null);
            srfLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.srf_layout);
            recyclerView = (MyRecyclerView) inflate.findViewById(R.id.recyclerView);
            isPrepared = true;
        }
        return inflate;
    }

    @Override
    protected void initData() {


        if (!isPrepared || !isVisible || isLoad) {
            return;
        }

        imageListPresenter = new ImageListPresenterImpl(this);

        list = new LinkedList<>();


        srfLayout.setOnRefreshListener(this);


        adapter = new ImageListAdapter(mActivity, list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLoadingData(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Constant.RECYCLERVIEW_GRIDVIEW, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<ImageListInfo>() {
            @Override
            public void onItemClick(View view, int position, ImageListInfo info) {
                imageListPresenter.onClick(info);
            }

            @Override
            public void onItemLongClick(View view, int position, ImageListInfo info) {


            }
        });

        srfLayout.post(new Runnable() {
            @Override
            public void run() {
                srfLayout.setRefreshing(true);
                onRefresh();
            }
        });
        isLoad = true;
    }


    @Override
    public void setImageListInfo(List<ImageListInfo> imageListInfo) {
        list.addAll(imageListInfo);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        adapter.isShowFooter(false);
        page = 1;
        adapter.removeAll();
        imageListPresenter.requestNetWork(index + 1, page);
    }

    @Override
    public void onLoadMore() {
        if (!srfLayout.isRefreshing()) {
            adapter.isShowFooter(true);
            ++page;
            imageListPresenter.requestNetWork(index + 1, page);
            LogUtils.i("MainFragment", "recyclerview到底了");
        }
    }


    @Override
    public void netWorkError() {
        Toast(UIUtils.getString(R.string.network_error));
    }

    @Override
    public void hideProgress() {
        srfLayout.setRefreshing(false);
    }

    @Override
    public void showFoot() {
        adapter.isShowFooter(true);
    }

    public void hideFoot() {
        adapter.isShowFooter(false);
    }
}