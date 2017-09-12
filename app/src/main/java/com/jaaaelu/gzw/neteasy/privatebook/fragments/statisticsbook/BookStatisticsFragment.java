package com.jaaaelu.gzw.neteasy.privatebook.fragments.statisticsbook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jaaaelu.gzw.neteasy.common.app.BaseFragment;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.FindBookFragment.MY_PERMISSIONS_REQUEST_READ_CAMERA;


public class BookStatisticsFragment extends BaseFragment {

    @BindView(R.id.tv_private_book_total_count)
    TextView mPrivateBookTotalCount;
    @BindView(R.id.tv_private_book_total_price)
    TextView mPrivateBookTotalPrice;
    @BindView(R.id.tv_love_auth)
    TextView mLoveAuth;
    @BindView(R.id.tv_auth_book_count)
    TextView mAuthBookCount;
    @BindView(R.id.tv_love_publisher)
    TextView mLovePublisher;
    @BindView(R.id.tv_publisher_book_count)
    TextView mPublisherBookCount;
    @BindView(R.id.cl_empty_view)
    ConstraintLayout mEmptyView;
    @BindView(R.id.pie_chart)
    PieChart mChart;

    private List<Book> mPrivateBookList;
    private HashMap<String, Integer> mCountMap;
    private Typeface mTfLight;

    public BookStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_book_statistics;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        initPieChart();
    }

    private void initPieChart() {
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        mChart.setUsePercentValues(true);

        mChart.getDescription().setEnabled(false);

        mChart.setExtraOffsets(5, 10, 5, 5);  //设置间距

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTfLight);  //设置饼状图中间文字字体

        mChart.setCenterText("");

        mChart.setDrawHoleEnabled(true);

        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);

        mChart.setTransparentCircleRadius(61f);

        mChart.setTouchEnabled(false);  //设置是否响应点击触摸

        mChart.setDrawCenterText(true);  //设置是否绘制中心区域文字

        mChart.setDrawEntryLabels(false);  //设置是否绘制标签

        mChart.setRotationAngle(0); //设置旋转角度

        mChart.setRotationEnabled(true); //设置是否旋转

        mChart.setHighlightPerTapEnabled(false);  //设置是否高亮显示触摸的区域

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);  //设置动画效果

        Legend legend = mChart.getLegend(); //获取图例

        legend.setEnabled(true);    //是否开启设置图例

        legend.setWordWrapEnabled(true);    //如果设置为true，那么当图例过多或者过长一行显示不下的时候就会自适应换行

        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }

    @Override
    protected void initData() {
        super.initData();
        mCountMap = new HashMap<>();
        queryBook();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BookManager.DataChange) {
            queryBook();
            BookManager.DataChange = false;
        }
    }

    private void queryBook() {
        BookManager.queryAllBook(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                mPrivateBookList = tResult;
                setStatisticsInfo();
            }
        });
    }

    private void setStatisticsInfo() {
        if (mPrivateBookList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mPrivateBookTotalCount.setText("0");
        } else {
            mEmptyView.setVisibility(View.GONE);
            mPrivateBookTotalCount.setText("" + mPrivateBookList.size());
            mPrivateBookTotalPrice.setText(calculatePrivateBookTotalPrice());
            setFavoriteInfo();
            calculateTag();
        }
    }

    private void calculateTag() {
        mCountMap.clear();
        for (Book book : mPrivateBookList) {
            if (book.getTagsStr().split(",").length <= 1) {
                Integer count = mCountMap.get("无分类");
                mCountMap.put("无分类", ((count == null ? 0 : count) + 1));
                continue;
            }
            String tag = book.getTagsStr().split(",")[1].split("=")[1].replace('\'', ' ').trim();
            boolean containsTag = mCountMap.containsKey(tag);
            Log.e("TAG", tag);
            if (containsTag) {
                int count = mCountMap.get(tag);
                mCountMap.put(tag, (count + 1));
            } else {
                mCountMap.put(tag, 1);
            }
        }

        setPieChartData();
    }

    private void setPieChartData() {
        List<PieEntry> entries = new ArrayList<>();
        for (String key : mCountMap.keySet()) {
            entries.add(new PieEntry(mCountMap.get(key), key));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void setFavoriteInfo() {
        String auth = calculateMyFavoriteAuth();
        String publisher = calculateMyFavoritePublisher();
        mLoveAuth.setText(getAlreadyDealText(auth.split("-")[0] + " / 最爱作者"));
        mAuthBookCount.setText(auth.split("-")[1]);
        mLovePublisher.setText(getAlreadyDealText(publisher.split("-")[0] + " / 最爱出版社"));
        mPublisherBookCount.setText(publisher.split("-")[1]);
    }

    private SpannableString getAlreadyDealText(String content) {
        SpannableString newContent = new SpannableString(content);
        newContent.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textThirdColor)), content.indexOf("/"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newContent.setSpan(new AbsoluteSizeSpan(sp2px(getActivity(), 13)), content.indexOf("/"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return newContent;
    }

    private String calculatePrivateBookTotalPrice() {
        double sum = 0.0;
        for (Book book : mPrivateBookList) {
            sum += BookManager.handleMoneyUtil(book.getPrice());
        }
        return "¥ " + sum;
    }

    private String calculateMyFavoriteAuth() {
        mCountMap.clear();
        for (Book book : mPrivateBookList) {
            String authStr = book.getAuthorStr();
            String[] allAuth = authStr.split(",");
            for (String auth : allAuth) {
                String newAuth = auth;
                if (auth.contains("]")) {
                    newAuth = auth.substring(auth.indexOf("]") + 1, auth.length()).trim();
                }
                if (newAuth.contains("・")) {
                    newAuth = newAuth.replace("・", "").trim();
                }
                if (newAuth.contains("·")) {
                    newAuth = newAuth.replace("·", "").trim();
                }
                if (newAuth.contains("(日)")) {
                    newAuth = newAuth.replace("(日)", "").trim();
                }
                if (newAuth.contains("（日）")) {
                    newAuth = newAuth.replace("（日）", "").trim();
                }
                boolean containsBook = mCountMap.containsKey(newAuth);
                Log.e("Auth", newAuth);
                if (containsBook) {
                    int count = mCountMap.get(newAuth);
                    mCountMap.put(newAuth, (count + 1));
                } else {
                    mCountMap.put(newAuth, 1);
                }
            }
        }

        return calculateMostInMap();
    }

    private String calculateMostInMap() {
        String mostKey = "暂无";
        int mostValue = 0;
        for (String key : mCountMap.keySet()) {
            int count = mCountMap.get(key);
            if (count > mostValue) {
                mostKey = key;
                mostValue = count;
            }
        }
        return mostKey + "-" + mostValue + " 本";
    }

    private String calculateMyFavoritePublisher() {
        mCountMap.clear();
        for (Book book : mPrivateBookList) {
            String publisher = book.getPublisher();
            boolean containsBook = mCountMap.containsKey(publisher);
            if (containsBook) {
                int count = mCountMap.get(publisher);
                mCountMap.put(publisher, (count + 1));
            } else {
                mCountMap.put(publisher, 1);
            }
        }
        return calculateMostInMap();
    }

    @OnClick(R.id.cl_empty_view)
    public void onViewClicked() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
            CaptureActivity.show(getActivity());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_CAMERA);
        }
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CaptureActivity.show(getActivity());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "您拒绝了二维码扫描的必要权限，无法使用扫码查书的功能，您可以尝试使用关键字查书功能...", Toast.LENGTH_LONG).show();
                }

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
