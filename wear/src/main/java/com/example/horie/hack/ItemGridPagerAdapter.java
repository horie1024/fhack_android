package com.example.horie.hack;

import android.content.Context;
import android.graphics.Rect;
import android.support.wearable.view.CardFrame;
import android.support.wearable.view.CardScrollView;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.ImageReference;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horie.hack.data.Item;

import java.util.ArrayList;

/**
 * Created by nissiy on 2014/09/07.
 */
public class ItemGridPagerAdapter extends GridPagerAdapter {
    private static final int GRID_COLUMN_COUNT = 4;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mItems;
    private Rect mInsets;

    public ItemGridPagerAdapter(
            Context context, ArrayList<Item> items, Rect insets) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = items;
        mInsets = insets;
    }

    @Override
    public int getRowCount() {
        if (mItems != null && mItems.size() > 0) {
            return mItems.size();
        }
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return GRID_COLUMN_COUNT;
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, final int column) {
        if (mItems.size() > 0) {
            final Item item = mItems.get(row);
            if (column == 0) {
                final View view = mLayoutInflater.inflate(
                        R.layout.gridpager_fullscreen_image, container, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                TextView textView = (TextView) view.findViewById(R.id.textView);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) textView.getLayoutParams();
                params.bottomMargin = mInsets.bottom;
                params.leftMargin = mInsets.left;
                params.rightMargin = mInsets.right;
                textView.setLayoutParams(params);

                imageView.setImageBitmap(item.bitmapImage);
                textView.setText(item.Title);
                container.addView(view);
                return view;
            } else if (column == 1) {
                final View view = mLayoutInflater.inflate(
                        R.layout.gridpager_fullscreen_image, container, false);
                // TODO: 将来的には地図のキャプチャにしたい
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                TextView textView = (TextView) view.findViewById(R.id.textView);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) textView.getLayoutParams();
                params.bottomMargin = mInsets.bottom;
                params.leftMargin = mInsets.left;
                params.rightMargin = mInsets.right;
                textView.setLayoutParams(params);

                imageView.setImageBitmap(item.bitmapImage);
                textView.setText(item.BrandName);
                // TODO: PlaceListの渡し方で失敗しているあとで解消
                //textView.setText(item.BrandName + "(" + item.PlaceList.get(0) + ")");
                container.addView(view);
                return view;
            } else if (column == 2) {
                CardScrollView cardScrollView = (CardScrollView) mLayoutInflater.inflate(
                        R.layout.gridpager_card, container, false);
                TextView textView = (TextView) cardScrollView.findViewById(R.id.textView);
                textView.setText(item.Title + " (" + item.Price + "円)\n\n" + item.DescLong);
                cardScrollView.setCardGravity(Gravity.BOTTOM);
                cardScrollView.setExpansionEnabled(true);
                cardScrollView.setExpansionDirection(CardFrame.EXPAND_DOWN);
                cardScrollView.setExpansionFactor(10);
                container.addView(cardScrollView);
                return cardScrollView;
            } else if (column == 3) {
                final View view = mLayoutInflater.inflate(
                        R.layout.gridpager_fullscreen_image, container, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                TextView textView = (TextView) view.findViewById(R.id.textView);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) textView.getLayoutParams();
                params.bottomMargin = mInsets.bottom;
                params.leftMargin = mInsets.left;
                params.rightMargin = mInsets.right;
                textView.setLayoutParams(params);

                imageView.setImageBitmap(item.bitmapImage);
                textView.setVisibility(View.GONE);
                container.addView(view);
                return view;
            }
        }

        return new View(mContext);
    }

    @Override
    public ImageReference getBackground(int row, int column) {
        if (column == 0) {
            return null;
        }
        if (mItems.size() > 0) {
            return ImageReference.forBitmap(mItems.get(row).bitmapImage);
        }
        return super.getBackground(row, column);
    }

    @Override
    protected void destroyItem(ViewGroup viewGroup, int row, int column, Object object) {
        viewGroup.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
