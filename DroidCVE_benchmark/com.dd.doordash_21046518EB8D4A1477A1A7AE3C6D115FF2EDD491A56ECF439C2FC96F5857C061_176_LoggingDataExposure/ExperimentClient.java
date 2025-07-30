package com.dd.doordash.network.clients;

import android.util.Log;
import com.dd.doordash.base.models.Experiment;
import com.dd.doordash.network.ConsumerRetrofitFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/* loaded from: classes.dex */
public class ExperimentClient extends BaseApiClient {
    public static final String EXPERIMENT_PATH_TARGET_CONSUMER = "/v2/experiments/";
    public static final int EXPERIMENT_TARGET_CONSUMER = 0;
    public static final int EXPERIMENT_TARGET_DRIVER = 1;
    public static final int EXPERIMENT_TARGET_INVALID = -1;
    public static final String EXPERIMENT_TARGET_KEY = "target";
    public static final String EXPERIMENT_UPDATED_AT_KEY = "updated_at";
    private static final String TAG = ExperimentClient.class.getSimpleName();
    public static final String TARGET_CONSUMER = "consumer";
    private final ExperimentService experimentService;

    /* loaded from: classes.dex */
    public interface ExperimentService {
        @GET(ExperimentClient.EXPERIMENT_PATH_TARGET_CONSUMER)
        Call<List<Experiment>> getExperimentsForConsumer(@QueryMap Map<String, String> map);
    }

    @Inject
    public ExperimentClient(ConsumerRetrofitFactory consumerRetrofitFactory) {
        super(consumerRetrofitFactory);
        this.experimentService = (ExperimentService) consumerRetrofitFactory.createRetrofit(true).create(ExperimentService.class);
    }

    public Response<List<Experiment>> getExperiments(int experimentTargetType, String updatedAt) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(EXPERIMENT_TARGET_KEY, TARGET_CONSUMER);
        queryMap.put(EXPERIMENT_UPDATED_AT_KEY, updatedAt);
        switch (experimentTargetType) {
            case 0:
                Call<List<Experiment>> experimentsCall = this.experimentService.getExperimentsForConsumer(queryMap);
                try {
                    return experimentsCall.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Returning null experiments.");
                    return null;
                }
            default:
                Log.e(TAG, "Received an invalid target type: " + experimentTargetType);
                return null;
        }
    }
}
