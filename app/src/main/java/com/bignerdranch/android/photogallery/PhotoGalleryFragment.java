package com.bignerdranch.android.photogallery;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2017/2/22.
 */

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;

    private List<GalleryItem> mItems = new ArrayList<>();

    //添加一个ThumbnailDownloader类型的成员变量
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    //创建PhotoGalleryFragment对象 供activity使用
    public static PhotoGalleryFragment newIntance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHanlder = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHanlder);


        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>(){

            @Override
            public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {

                Drawable drawable = new BitmapDrawable(getResources(), bitmap);

                photoHolder.bindDrawable(drawable);
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        setupAdapter();

        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {

        //isAdded()如果该片段正在增加它的活动返回true。 判断fragment已经与目标activity相关联
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mItemImageView;


        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);

        }

        public void bindDrawable(Drawable drawable) {

            mItemImageView.setImageDrawable(drawable);
        }

    }


    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item,parent,false);



            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {

            GalleryItem galleryItem = mGalleryItems.get(position);

            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);

            holder.bindDrawable(placeholder);

            //
            mThumbnailDownloader.queryThumbnail(holder, galleryItem.getUrl());

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }


        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
            //super.onPostExecute(items);
        }
    }
}
