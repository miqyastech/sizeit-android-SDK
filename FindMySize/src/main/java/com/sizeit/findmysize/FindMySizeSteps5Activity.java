package com.sizeit.findmysize;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.sizeit.findmysize.databinding.ActivityFindMySizeStep5Binding;
import com.sizeit.findmysize.model.DataAPI;
import com.sizeit.findmysize.model.ResponseSize;
import com.sizeit.findmysize.network.APIClient;
import com.sizeit.findmysize.network.MainApi;
import com.sizeit.utils.Constants;
import com.sizeit.utils.Preferences;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindMySizeSteps5Activity extends BaseActivity {

    private ActivityFindMySizeStep5Binding binding;
    private int selectionPosition = 2;
    private MainApi apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_my_size_step_5);
        binding.setActivity(this);
        setUpInitialValue();
        setUpHeaderView();
    }

    private void setUpInitialValue() {
        binding.header.ivBack.setOnClickListener(v -> onBackPressed());
        binding.header.ivClose.setOnClickListener(v -> finishWithResultAndAnimation(null));

        selectionPosition = Preferences.getPreferences(this).getInt(Constants.hip, 2);
    }

    private void setUpHeaderView() {
        apiInterface = APIClient.getClient(this).create(MainApi.class);

        binding.tvNarrower.setTextColor(ContextCompat.getColor(this,
                selectionPosition == 1 ? R.color.black : R.color.colorGrayDark));
        binding.tvNarrower.setPaintFlags(selectionPosition == 1 ?
                binding.tvNarrower.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG : 0);

        binding.tvAverage.setTextColor(ContextCompat.getColor(this,
                selectionPosition == 2 ? R.color.black : R.color.colorGrayDark));
        binding.tvAverage.setPaintFlags(selectionPosition == 2 ?
                binding.tvAverage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG : 0);

        binding.tvCurvier.setTextColor(ContextCompat.getColor(this,
                selectionPosition == 3 ? R.color.black : R.color.colorGrayDark));
        binding.tvCurvier.setPaintFlags(selectionPosition == 3 ?
                binding.tvCurvier.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG : 0);

        binding.iv.setImageResource(selectionPosition == 1 ?
                R.drawable.hips_narrower : selectionPosition == 2 ?
                R.drawable.hips_average : R.drawable.hips_curvier);
    }

    public void onViewClicked(View view) {
        if (view == binding.tvNarrower) {
            selectionPosition = 1;
            setUpHeaderView();
        } else if (view == binding.tvAverage) {
            selectionPosition = 2;
            setUpHeaderView();
        } else if (view == binding.tvCurvier) {
            selectionPosition = 3;
            setUpHeaderView();
        } else if (view == binding.btnContinue) {
            Preferences.getPreferences(this).putInt(Constants.hip, selectionPosition);
            getProductSizes();
        }
    }

    private void getProductSizes() {
        DataAPI dataAPI = new DataAPI();
        dataAPI.setApiKey(FindMySizeActivity.api_key);
        dataAPI.setUserId(FindMySizeActivity.user_id);
        dataAPI.setHeight(Preferences.getPreferences(this).getInt(Constants.height));
        dataAPI.setWeight(Preferences.getPreferences(this).getInt(Constants.weight));
        int agePos = Preferences.getPreferences(this).getInt(Constants.age);
        dataAPI.setAge(agePos == 1 ? 15 : agePos == 2 ? 25 : agePos == 3 ? 35 : agePos == 4 ? 45 : agePos == 5 ? 60 : 0);
        dataAPI.setBelly(10);
        dataAPI.setHip(10);
        dataAPI.setBrand("");
        dataAPI.setBrandSize(10);
        showProgress();
        Call<ResponseSize> call3 = apiInterface.getAllSizes(dataAPI);
        call3.enqueue(new Callback<ResponseSize>() {
            @Override
            public void onResponse(@NotNull Call<ResponseSize> call, @NotNull Response<ResponseSize> response) {
                hideProgress();
                ResponseSize result = response.body();
                Log.e("onResponse", "onResponse: " + result);
                if (result.isSuccess()) {
                    if (result.getData() != null && result.getData().getSizes() != null &&
                            result.getData().getSizes().size() > 0) {
                        Preferences.getPreferences(FindMySizeSteps5Activity.this).setSizesList(result.getData().getSizes());

                        boolean isFound = false;
                        if (!FindMySizeActivity.miqyas_fit.equals("")) {
                            for (int i = 0; i < result.getData().getSizes().size(); i++) {
                                if (result.getData().getSizes().get(i).getName()
                                        .equalsIgnoreCase(FindMySizeActivity.miqyas_fit)) {
                                    isFound = true;
                                    Intent intent = new Intent();
                                    intent.putExtra(FindMySizeActivity.CURRENT_SIZE, result.getData().getSizes().get(i).getSize());
                                    finishWithResultAndAnimation(intent);
                                    break;
                                }
                            }
                        }
                        if (!isFound) {
                            finishWithResultAndAnimation(null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseSize> call, @NotNull Throwable t) {
                hideProgress();
                Log.e("onResponse", "onResponse: " + t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }
}