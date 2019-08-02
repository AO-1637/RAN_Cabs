package com.android.ran.network;

import com.android.ran.model.Cab;
import com.android.ran.model.User;
import com.android.ran.payment.Checksum;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndPointInterface {

    @POST("/signIn")
    @FormUrlEncoded
    Call<User> authSignIn(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("/signUp")
    @FormUrlEncoded
    Call<User> createUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("password") String password
    );

    @GET("/api/users/{uID}")
    Call<User> userDetail(
            @Path("uID") String uID,
            @Query("x_key") String key,
            @Query("token") String token
    );

    @PUT("/api/users/{uID}")
    @FormUrlEncoded
    Call<User> userUpdate(
            @Path("uID") String cID,
            @Query("x_key") String key,
            @Query("token") String token,
            @Field("username") String username,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("alternateContact") String alternateContact
    );

    @GET("/api/cabs")
    Call<List<Cab>> availableCabList(
            @Query("x_key") String key,
            @Query("token") String token,
            @Query("collegeName") String collegeName,
            @Query("pickup") String pickup,
            @Query("drop") String drop,
            @Query("seats") String seats,
            @Query("startTime") String startTime
    );

    @PUT("/cabs/{cID}")
    @FormUrlEncoded
    Call<Cab> cabUpdate(
            @Path("cID") String cID,
            @Field("isBooked") boolean isBooked,
            @Field("pickup") String pickup,
            @Field("drop") String drop,
            @Field("startTime") String startTime
    );

    @PUT("/api/users/{uID}/bookCab")
    @FormUrlEncoded
    Call<User> userBookCab(
            @Path("uID") String uID,
            @Query("x_key") String key,
            @Query("token") String token,
            @Field("cabsBooked") String cID
    );

    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<Checksum> getChecksum(
            @Field("MID") String mId,
            @Field("ORDER_ID") String orderId,
            @Field("CUST_ID") String custId,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );
}